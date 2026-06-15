# Spring Boot RestAPI

Spring Boot 3 / Java 21 で構築された RestAPI。

---

## このファイルの管理方針

**CLAUDE.md は「Claudeの行動を変える指示書」** であり、ドキュメントではない。
毎回コンテキストに全文読み込まれるため、肥大化させない。

| 種類 | 置き場所 |
|---|---|
| コーディング規約・禁止事項 | **CLAUDE.md** |
| アーキテクチャの制約（依存方向など） | **CLAUDE.md** |
| ビルド・実行コマンド | **CLAUDE.md** |
| 重要な落とし穴（Stream、DateTime など） | **CLAUDE.md** |
| エンドポイント一覧 | `docs/api.md` |
| アーキテクチャ詳細・認証フロー・ライブラリ | `docs/architecture.md` |
| タスク指示（step 系） | **プロンプトで渡す** |

---

## ドキュメント参照先

| 内容 | ファイル |
|---|---|
| APIエンドポイント一覧・レスポンス仕様 | [docs/api.md](docs/api.md) |
| アーキテクチャ詳細・認証フロー・モジュール構成 | [docs/architecture.md](docs/architecture.md) |
| VS Code 推奨設定・開発 Tips | [docs/tips.md](docs/tips.md) |

### 利用可能なカスタムコマンド

| コマンド | 用途 |
|---|---|
| `/docs-check` | ドキュメント（`docs/*.md`）と実装の乖離チェック。コミット前などに手動実行する |

---

## 言語設定

- 常に日本語で会話する
- コメントも日本語で記述する
- エラーメッセージの説明も日本語で行う

---

## Build & Run

```bash
# ビルド
./gradlew build

# 実行（.env.bootRun から環境変数を自動読み込み）
cp .env.bootRun.example .env.bootRun   # 初回のみ・値を実際の環境に合わせて編集
./gradlew sandbox-api:bootRun

# テスト省略ビルド
./gradlew build -x test

# Checkstyle（ビルド時に自動実行）
./gradlew checkstyleMain

# docker compose .env.compose を編集して実際の値を設定
docker compose --env-file .env.compose up -d
```

### 環境変数ファイル

| ファイル | 用途 |
|---|---|
| `.env.bootRun.example` | bootRun テンプレート（git 管理対象） |
| `.env.bootRun` | bootRun 実際の値（git 除外済み） |
| `.env.compose.example` | docker compose テンプレート（git 管理対象） |
| `.env.compose` | docker compose 実際の値（git 除外済み） |

`build.gradle` の `bootRun` タスクが `.env.bootRun` を自動読み込みするため、別途 `export` や `source` は不要。

### ローカルインフラ起動

```bash
# MySQL（43306）+ Redis（46379）を Docker で起動
cp .env.compose.example .env.compose  # 初回のみ・値を実際の環境に合わせて編集
docker compose --env-file .env.compose up -d
```

---

## アーキテクチャ

DDD（ドメイン駆動設計）に基づくマルチモジュール構成。詳細は [docs/architecture.md](docs/architecture.md) 参照。

### モジュール依存関係

```
sandbox-api ──→ sandbox-application ──→ sandbox-domain ←──┐
     │                                                     │
     ├──→ sandbox-security ─────────────────────────────→──┤
     │                                                     │
     └──(runtimeOnly)──→ sandbox-infrastructure ─────────→─┘
```

### パッケージルート

`jp.co.next_evolution.sandbox`

---

## 実装規約

### エラー型

| 例外クラス | HTTP ステータス |
|---|---|
| AuthenticationException | 401 UNAUTHORIZED |
| ForbiddenException | 403 FORBIDDEN |
| NotFoundException | 404 NOT FOUND |
| DuplicateException / InsertException / UpdateException | 400 BAD REQUEST |

### Stream / Collection

- `sandbox-application`: `Stream.toList()`（immutable、Redis 非対象）
- `sandbox-infrastructure`: `.collect(Collectors.toList())`（Redis シリアライズのため可変 ArrayList が必要）

### 日時フォーマット

- `LocalDateTime` は RFC 3339 形式 `yyyy-MM-dd'T'HH:mm:ssXXX`（例: `2026-01-02T11:22:33+09:00`）
- `JacksonConfig` でグローバル設定済み。DTO に `@JsonFormat` / `@JsonSerialize` / `@JsonDeserialize` を個別付与しない
- `LocalDate` は対象外（既存の `@JsonFormat(pattern = "yyyy-MM-dd")` をそのまま使用）

### userId のデコード

パスパラメータ `{userId}` は Base64 エンコード済み。`UserId.decodeUserIdValue()` でデコードする。
userId・email はリクエストボディに含めず、`AuthUser`（`authUser.sub()`, `authUser.email()`）から取得する。

```java
String userId = UserId.decodeUserIdValue(userIdBase64);
```

### 管理者専用 API

```java
@GetMapping
public ResponseEntity<ApiResponse> someAdminApi(@AuthenticationPrincipal AuthUser authUser) {
    if (!authUser.isAdmin()) {
        throw new ForbiddenException("管理者用APIです");
    }
    // useCase 実行
}
```

### 環境変数

新しい環境変数を追加・削除・リネームしたら、`.env.bootRun.example` と `.env.compose.example` の該当箇所も同時に更新する。

### Checkstyle

Java ファイルを編集したら必ず実行し、エラーがゼロになってから完了とする。

```bash
./gradlew checkstyleMain
```

主なルール（Google Java Style 準拠 / `checkstyle.xml` 参照）:

| ルール | 内容 |
|---|---|
| インデント | 2スペース（タブ禁止） |
| 1行最大 | 100文字（import・URL 行は除外） |
| import | ワイルドカード（`*`）禁止。`STATIC` → `THIRD_PARTY_PACKAGE` の順でアルファベット昇順 |
| 未使用 import | Checkstyle 非対象だが必ず削除する |

### 認証フロー（概要）

詳細は [docs/architecture.md](docs/architecture.md) 参照。

1. `JwtAuthFilter` — RS256 JWT 検証 → Redis から `AuthUser`（admin フラグ含む）を取得 → `SecurityContextHolder` にセット
2. `AuthInterceptor` — `AuthUser` がなければ 401
3. `@PublicApi` — インターセプターの認証チェックをスキップ
