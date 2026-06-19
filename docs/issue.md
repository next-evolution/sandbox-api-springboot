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