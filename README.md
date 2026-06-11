# sandbox-api-springboot

![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.5-6DB33F?logo=springboot&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-multi--module-02303A?logo=gradle&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.4-4479A1?logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-8.0-DC382D?logo=redis&logoColor=white)
![AWS Cognito](https://img.shields.io/badge/AWS_Cognito-FF9900?logo=amazonaws&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)

FX トレード支援を目的としたバックエンド REST API。  
Spring Boot 3 / Java 21 で構築し、**DDD（ドメイン駆動設計）** に基づく 5 モジュール Gradle 構成を採用。  
認証は AWS Cognito（RS256 JWT）、セッション管理は Redis で実装。

---

## アーキテクチャ

### モジュール構成

| モジュール | 役割 |
|---|---|
| `sandbox-domain` | ドメインモデル（値オブジェクト・集約）、リポジトリインターフェース、ドメイン例外。フレームワーク非依存。 |
| `sandbox-application` | ユースケース、コマンド、DTO。`sandbox-domain` のみに依存。 |
| `sandbox-infrastructure` | リポジトリ実装（MyBatis）、Redis 設定。 |
| `sandbox-api` | REST コントローラー、リクエスト/レスポンス DTO。実行可能 JAR を生成。 |
| `sandbox-security` | JWT フィルター（RS256）、認証インターセプター、Spring Security 設定。 |

### 依存関係

```
sandbox-api  ──→  sandbox-application  ──→  sandbox-domain
     │                                            ↑
     └──→  sandbox-security         sandbox-infrastructure
                                    (runtimeOnly) ──────┘
```

詳細は [docs/architecture.md](./docs/architecture.md) を参照。

---

## 主な機能

| ドメイン | 主なエンドポイント |
|---|---|
| **認証 / Auth** | ログイン・ログアウト（AWS Cognito JWT + Redis セッション） |
| **ユーザー / User** | プロフィール取得・ユーザー登録・情報更新 |
| **管理者 / Admin** | ユーザー検索・承認・ブロック・管理者権限付与、Redis キャッシュ管理 |
| **FX マスター** | 通貨シンボル・国・通貨ペア・経済指標（公開 API） |
| **FX バーデータ** | OHLC バーデータ検索・CSV 一括インポート |
| **ZigZag 分析** | ZigZag 生成・検索・ステータス取得・バーデータ取得 |
| **トレードシミュレーション** | リスク額・ロット比率・エントリーに基づくシミュレーション |

エンドポイント詳細は [docs/api.md](./docs/api.md) を参照。

---

## Getting Started

### 1. ローカルインフラ起動

```bash
# MySQL（43306）+ Redis（46379）を Docker で起動
docker compose up -d
```

プロジェクトルートに `.env` ファイルを作成：

```dotenv
MYSQL_ROOT_PASSWORD=root_password
DB_SCHEMA=sandbox_local
DB_PORT=43306
REDIS_PORT=46379
```

### 2. アプリケーション環境変数

`bootRun` 実行前にシェルまたは IDE に設定：

| 変数名 | 説明 | 例 |
|---|---|---|
| `DB_HOST` | MySQL ホスト | `localhost` |
| `DB_PORT` | MySQL ポート | `43306` |
| `DB_SCHEMA` | データベース名 | `sandbox_local` |
| `DB_USER` | DB ユーザー | `sandbox_app` |
| `DB_PASSWORD` | DB パスワード | `s4ndb0x_app` |
| `REDIS_HOST` | Redis ホスト | `localhost` |
| `REDIS_PORT` | Redis ポート | `46379` |
| `JWT_ISSUER1` | Cognito URL | `https://cognito-idp.ap-northeast-1.amazonaws.com/...` |
| `JWT_AUDIENCE1/2/3` | Cognito App Client ID | — |
| `JWT_ORIGIN1/2` | 許可オリジン | `http://localhost` |
| `BUCKET_NAME` | S3 バケット名 | `next-evolution` |
| `APP_NAME` | アプリ名（S3 パス用） | `sandbox` |

### 3. ビルド & 起動

```bash
# 全モジュールをビルド
./gradlew build

# API サーバー起動
./gradlew :sandbox-api:bootRun

# テスト省略ビルド
./gradlew build -x test

# Checkstyle（ビルド時に自動実行）
./gradlew checkstyleMain
```

---

## API Documentation

- OpenAPI Spec: [api-docs.yaml](./docs/api-docs.yaml)
- API Docs: https://next-evolution.github.io/sandbox-api-springboot/
