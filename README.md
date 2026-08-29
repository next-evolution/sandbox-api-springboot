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

### 0. 前提条件（Claude Code利用時）

`$SANDBOX_HOME` 直下に `claude-code`（横断仕様ドキュメントフォルダ、Google Driveへのシンボリックリンク）が必要です。`CLAUDE.md` の `@../claude-code/...` importの解決に使用されます。

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
sandbox-api ──→ sandbox-application ──→ sandbox-domain ←──┐
     │                                                     │
     ├──→ sandbox-security ─────────────────────────────→──┤
     │                                                     │
     └──(runtimeOnly)──→ sandbox-infrastructure ─────────→─┘
```

詳細は [docs/architecture.md](./docs/architecture.md) を参照。

### ディレクトリ構成（非モジュール）

| ディレクトリ | 用途 |
|---|---|
| `local/` | Docker コンテナ共通のボリュームマウント（`storage/`, `tmp/`, `work/`） |
| `docs/` | アーキテクチャ・開発 Tips |

> Docker Compose 環境・Bruno テストコレクション・データファイルは [`sandbox-tools`](../sandbox-tools) リポジトリで管理。

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

エンドポイント詳細は [api-docs.yaml](../claude-code/architecture/api-docs.yaml)（OpenAPI spec）を参照。起動中は Swagger UI からも参照可能。

---

## Getting Started

### 1. ローカルインフラ起動

MySQL・Redis は `sandbox-tools` リポジトリで管理。

```bash
# sandbox-tools/docker/ で実行
cd ../sandbox-tools/docker
cp .env.compose.example .env.compose  # 初回のみ・値を実際の環境に合わせて編集
docker compose --env-file .env.compose up -d
```

詳細は [`sandbox-tools/docs/docker.md`](../sandbox-tools/docs/docker.md) 参照。

> `initdb.d/` スクリプトが初回起動時に DB 作成・アプリユーザー作成・管理者ユーザー INSERT を自動実行します。  
> 再初期化する場合は `docker compose down -v` でボリュームを削除してから再起動してください。

### 2. アプリケーション環境変数

```bash
# テンプレートをコピーして実際の値を設定（初回のみ）
cp .env.bootRun.example .env.bootRun
```

* `build.gradle` が `.env.bootRun` を自動読み込みするため、`source` や `export` は不要です。
* 環境変数 は [env-value.md](../claude-code/architecture/env-value.md) 参照。

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

- OpenAPI Spec: [api-docs.yaml](../claude-code/architecture/api-docs.yaml)
- 開発 Tips（VS Code 設定など）: [docs/tips.md](./docs/tips.md)
