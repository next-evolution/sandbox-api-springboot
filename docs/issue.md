# Login Issue

**ステータス: 解決済み（案B: Silent Login 実装済み）**

---

## 事象

API 呼び出し元は有効な ID Token を保持しているにもかかわらず、API 側の Redis セッションに該当する `AuthUser` が存在しないケースがある。

**発生条件の例**
- Redis の TTL 切れ・再起動
- Auth0 経由で認証済みだがログイン API を経由していない

---

## 現状コードの問題点

`JwtAuthFilter` の以下の実装が原因で、セッションなし時に意図しない動作が起きている。

```java
// sandbox-security/.../filter/JwtAuthFilter.java L42-43
AuthUser authUser = sessionRepository.findBySub(jwtAuthUser.sub())
    .orElse(jwtAuthUser); // ← Redis セッションがなくても admin=false で通過してしまう
```

さらに L52 の `sessionRepository.update(authUser)` により、**admin ユーザーの Redis に admin=false のセッションが上書き保存される**という副作用がある。
コメント「セッションなし → SecurityContext に何もセットしない → 401」と実装が乖離している。

---

## 設計案

### 案A: 再ログイン強制（401）

セッションがない場合は `SecurityContext` に何もセットせず、`AuthInterceptor` に 401 を返させる。

```java
Optional<AuthUser> sessionUser = sessionRepository.findBySub(jwtAuthUser.sub());
if (sessionUser.isEmpty()) {
    chain.doFilter(request, response);
    return;
}
AuthUser authUser = sessionUser.get();
```

**メリット**
- シンプルで安全

**デメリット**
- Redis TTL 切れのたびにユーザーが再ログインを求められる
- フロントが再ログイン → セッション再構築のフローを持つ必要がある

---

### 案B: Silent Login（sandbox_user テーブルから AuthUser を復元）【推奨】

セッションがない場合に DB からユーザー情報を取得して `AuthUser` を再構築し、Redis に書き直す。

**フロー**

```
JwtAuthFilter
  └─ sessionRepository.findBySub() → empty
       └─ userRepository.findBySub(sub)   ← sandbox-domain にリポジトリ interface を追加
            ├─ 見つかった → AuthUser 生成 → Redis に保存 → 通過
            └─ 見つからない → SecurityContext セットしない → 401
```

**実装イメージ（JwtAuthFilter のみ変更）**

```java
AuthUser authUser = sessionRepository.findBySub(jwtAuthUser.sub())
    .orElseGet(() ->
        userRepository.findBySub(jwtAuthUser.sub()) // sandbox_user テーブルから復元
            .map(u -> sessionRepository.save(u))    // Redis に書いて以降はキャッシュヒット
            .orElse(null)                           // DB にも存在しない → null → 401
    );

if (authUser == null) {
    chain.doFilter(request, response);
    return;
}
```

**メリット**
- ユーザーが再ログイン不要
- Redis 再起動時も透過的に復元できる
- admin フラグの誤保存（現状バグ）も合わせて解消できる

**デメリット**
- セッションミスのたびに DB クエリが発生する
- `sandbox-domain` に `UserRepository` interface の追加が必要（ただし `SessionRepository` と同じ層なのでアーキテクチャ上の問題はない）

---

## 推奨: 案B（Silent Login）

JWT の RS256 検証が通っている時点でユーザーは認証済み。セッション切れはキャッシュの欠如であり、再ログインを強制する根拠にならない。

### 判断が分かれるポイント

JWT は有効だが `sandbox_user` に存在しないケース（Auth0 登録済み・アプリ未登録）をどう扱うか。
通常は `orElse(null)` → 401 にして「プロフィール未登録」を明示するのが安全。

---

## 変更対象ファイル（案B）

| ファイル | 内容 |
|---|---|
| `sandbox-domain/.../repository/user/UserRepository.java` | `findBySub(String sub)` を追加 |
| `sandbox-infrastructure/.../repository/user/UserRepositoryImpl.java` | 上記の実装 |
| `sandbox-security/.../filter/JwtAuthFilter.java` | `userRepository` を DI し、セッションなし時の復元処理を追加 |

---

---

# 要検討
- アノテーションのないメソッドが @NullMarked アノテーションが付けられたメソッドをオーバーライドしています

# isAdmin / isApproved は authorities を使うべきか

## 現状の問題点

`getAuthorities()` が常に `List.of()` を返しているため、`UsernamePasswordAuthenticationToken` に渡しているが Spring Security の認可機能を何も使えていない状態。

---

## authorities を使う場合 vs 使わない場合

### authorities に乗せる（`ROLE_ADMIN`, `ROLE_APPROVED` など）

**メリット**
- `@PreAuthorize("hasRole('ADMIN')")` など Spring Security のネイティブ機能が使える
- SecurityConfig の `requestMatchers` でパス単位の制御もできる

**デメリット**
- `AuthUser` は `sandbox-domain` に置かれており、`ROLE_ADMIN` のような Spring Security 固有の文字列が入るとドメイン層がセキュリティフレームワークに依存する
- 現プロジェクトは `AuthInterceptor` + コントローラの明示チェック（`authUser.isAdmin()`）が主流なので、`@PreAuthorize` を使わないなら導入コストだけかかる

### boolean フィールドのまま使う（現状）

**メリット**
- シンプルで読みやすい。`authUser.isAdmin()` は意図が明確
- `AuthUser` がドメインモデルとして純粋に保てる

**デメリット**
- `getAuthorities()` が空なので Spring Security の認可機能（`@PreAuthorize` 等）は使えない

---

## このプロジェクトでの推奨

DDD マルチモジュール構成・`AuthInterceptor` + コントローラチェックの設計を踏まえ、**boolean フィールドのまま** が整合性が高い。

`isApproved` を追加するなら `admin` と同じパターンで追加する：

```java
public record AuthUser(
    String sub,
    String email,
    Boolean emailVerified,
    Boolean admin,
    Boolean approved
) implements UserDetails {

  public boolean isAdmin() {
    return Boolean.TRUE.equals(admin);
  }

  public boolean isApproved() {
    return Boolean.TRUE.equals(approved);
  }
}
```

`getAuthorities()` を活かすなら、`@EnableMethodSecurity` を有効化して `@PreAuthorize` を使う設計に全体的に移行するタイミングでまとめて対応する。

---

# AuthInterceptor を削除して Spring Security に移行する

想定ロール: `ROLE_MEMBER`（isApproved 相当）、`ROLE_ADMIN`（isAdmin 相当）

## 現状の問題点（移行前に気づいた点）

`/v1/fx/master-list/**` は `SecurityConfig` の `permitAll()` に含まれていないため、
`@PublicApi` を付けていても **Spring Security が先に 401 を返している可能性がある**（`AuthInterceptor` まで到達しない）。
つまり `@PublicApi` は現状で機能していない可能性が高い。

---

## 移行方針

### 削除するもの
- `AuthInterceptor` の認証チェック部分（`preHandle` の 401 返却ロジック）
- `@PublicApi` アノテーション

### 残す / 移動するもの
- `AuthInterceptor.afterCompletion` のレスポンスタイムログ → 別の `HandlerInterceptor` に切り出すか `OncePerRequestFilter` に移動

### 変更するもの

**SecurityConfig** — `permitAll` に公開エンドポイントを明示し、認可を Spring Security に委譲

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**",
                     "/v1/debug", "/v1/guest/**", "/v1/logout-api/*",
                     "/v1/fx/master-list/**")   // @PublicApi の代替
    .permitAll()
    .requestMatchers("/**").hasRole("MEMBER"))   // MEMBER 以上のみ通過
```

**AuthUser.getAuthorities()**

```java
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
  List<SimpleGrantedAuthority> list = new ArrayList<>();
  if (Boolean.TRUE.equals(approved)) {
    list.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
  }
  if (Boolean.TRUE.equals(admin)) {
    list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }
  return list;
}
```

**Admin チェック** — `@EnableMethodSecurity` を有効化すれば使える

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping
public ResponseEntity<ApiResponse> someAdminApi() { ... }
```

---

## 決定事項

| 論点 | 決定 |
|---|---|
| Admin チェックの方法 | `@PreAuthorize("hasRole('ADMIN')")` に統一（`SecurityConfig` に `@EnableMethodSecurity` を追加） |
| ロギングの移動先 | `OncePerRequestFilter` を新規作成（`JwtAuthFilter` の後段に配置） |
| `approved=false` の扱い | `getAuthorities()` で `ROLE_MEMBER` を返さず、Spring Security に 403 を返させる |

## approved=false の 403 を JwtAuthFilter で返さない理由

- `JwtAuthFilter` が `response.sendError(403)` を直接書くと `AccessDeniedHandler` を迂回する
- `JwtAuthFilter` が DB を参照するのはセッションなし（silent login）時だけのため、Redis に `approved=true` が残っている間は即時反映されない
- `getAuthorities()` に委ねることで「AuthUser を SecurityContext に置く」責務に集中できる

## approved 変更の即時反映

`approved=false` に変更した際、ユーザー管理側で `sessionRepository.deleteBySub(sub)` を呼ぶ。
次のリクエストで silent login → `approved=false` → `ROLE_MEMBER` なし → Spring Security が 403 を返す。

---

# Cognito カスタムクレーム（`admin` / `approved`）案

**結論: 現設計を維持（Cognito + Lambda 案は採用しない）**

## 検討内容

AWS Cognito の Pre Token Generation Lambda で `admin` / `approved` を JWT クレームに埋め込む案。

| 観点 | Cognito + Lambda | 現状（Redis） |
| --- | --- | --- |
| JWT の自己完結性 | ◎ stateless・Redis ルックアップ不要 | △ Redis 依存 |
| 権限変更の即時反映 | ✗ Access Token TTL まで遅延（デフォルト 1 時間） | ◎ `sessionRepository.deleteBySub(sub)` で即時 |
| `approved` の意味的な置き場所 | △ Cognito はアイデンティティ層（業務ロジックとの結合が強くなる） | ◎ `sandbox_user` テーブルがアプリ側の正 |
| Lambda 障害の影響 | ログイン不可になるリスク | なし |

## 採用しない理由

- `approved` はアプリレベルの承認状態（業務ロジック）であり、Cognito（アイデンティティ層）に持たせるのは責務の越境になる
- `approved=false` / `admin=false` への変更が Access Token の TTL が切れるまで反映されない（即時反映不可）
- 現設計の責務分離が明確：Cognito（JWT）= 認証（誰か）、`sandbox_user` + Redis = 認可（何ができるか）

---

# `UsernamePasswordAuthenticationToken` の Redis 保存 / `AuthUser` の要否

**結論: 現設計を維持（変更しない）**

## 検討内容

### `UsernamePasswordAuthenticationToken` を Redis に保存すべきか？

**採用しない。** 理由：

| 観点 | `AuthUser` を Redis に保存（現状） | `UsernamePasswordAuthenticationToken` を Redis に保存 |
| --- | --- | --- |
| シリアライズ | record → JSON が素直 | `Collection<GrantedAuthority>` 等で複雑 |
| 依存関係 | `sandbox-domain` → Spring Security Core のみ | `SessionRepository`（ドメイン層）が Spring Security クラスに依存しドメイン汚染になる |
| 責務分離 | 「何を保存するか」と「どう使うか」が分離できている | Spring Security の詳細がドメイン層に漏れる |

`JwtAuthFilter` L69〜72 での `UsernamePasswordAuthenticationToken` へのラップはセキュリティ層の責務として適切。

### `AuthUser` は不要になるか？

**不要にならない。** 次の 2 点がある限り必要：

1. **admin フラグの補完**: JWT クレームに `admin` が含まれないため、Redis から `AuthUser` を取得して補完する設計になっている（`JwtProvider.parse()` は `admin=false` で生成）
2. **コントローラーの `@AuthenticationPrincipal`**: `authUser.sub()` / `authUser.email()` / `authUser.isAdmin()` をコントローラーが直接参照しており、汎用の Spring Security `User` クラスでは代替できない

JWT に `admin` を埋め込む方式に変更すれば Redis ルックアップは不要になるが、JWT 再発行の仕組みが必要になり別問題が生じる。