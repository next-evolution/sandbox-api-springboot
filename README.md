# sandbox-api-springboot

## 環境変数

### docker-compose.yml 用（`.env` ファイルに設定）

プロジェクトルートに `.env` ファイルを作成し、以下の変数を設定してください。

| 変数名 | 説明 | 例 |
|---|---|---|
| `MYSQL_ROOT_PASSWORD` | MySQL root パスワード | `root_password` |
| `DB_SCHEMA` | MySQL データベース名 | `sandbox_local` |
| `DB_PORT` | ホスト側 MySQL ポート | `43306` |
| `REDIS_PORT` | ホスト側 Redis ポート | `46379` |

```dotenv
MYSQL_ROOT_PASSWORD=root_password
DB_SCHEMA=sandbox_local
DB_PORT=43306
REDIS_PORT=46379
```

### application.yml 用（`bootRun` 実行前に設定）

| 変数名 | 説明 | デフォルト値 |
|---|---|---|
| `DB_HOST` | MySQL ホスト | `localhost` |
| `DB_PORT` | MySQL ポート | `3306` |
| `DB_SCHEMA` | データベース名 | `dev` |
| `DB_USER` | データベースユーザー | `user` |
| `DB_PASSWORD` | データベースパスワード | `password` |
| `REDIS_HOST` | Redis ホスト | `localhost` |
| `REDIS_PORT` | Redis ポート | `6379` |
| `JWT_ORIGIN1` | 許可オリジン1 | — |
| `JWT_ORIGIN2` | 許可オリジン2 | — |
| `JWT_ISSUER1` | JWT 発行者（Cognito URL） | — |
| `JWT_AUDIENCE1` | Cognito App Client ID 1 | — |
| `JWT_AUDIENCE2` | Cognito App Client ID 2 | — |
| `JWT_AUDIENCE3` | Cognito App Client ID 3 | — |
| `BUCKET_NAME` | S3 バケット名 | `next-evolution` |
| `APP_NAME` | アプリ名（S3 パス用） | `sandbox` |

## Build & Run

```bash
# 全モジュールをビルド
./gradlew build

# API サーバー起動（上記の環境変数を事前に設定すること）
./gradlew :sandbox-api:bootRun

# テストを除いてビルド
./gradlew build -x test

# Checkstyle（ビルド時に自動実行）
./gradlew checkstyleMain
```

### ローカルインフラ起動

```bash
# MySQL + Redis を Docker で起動
docker compose up -d
```

MySQL はポート `43306`、Redis はポート `46379` で公開されます。

# API Documentation

- OpenAPI Spec: [api-docs.yaml](./docs/api-docs.yaml)
- API Docs: https://next-evolution.github.io/sandbox-api-springboot/
