# API エンドポイント一覧

ベースパス: `/api`（context-path）+ `/v1/...`  
例: `POST /api/v1/auth/login`

---

## 共通仕様

### レスポンス形式

多くのエンドポイントで `ApiResponse` / `ApiSearchResponse` でラップされる（例外は下表参照）。

```json
// ApiResponse（単件・操作系）
{ "returnCode": 0, "message": null, "user": { ... } }

// ApiSearchResponse（一覧検索系）
{ "returnCode": 0, "totalCount": 100, "searchCount": 100, "totalPage": 5, "list": [ ... ] }
```

`ReturnCode` は整数値: `Ok`=0（正常）/ `Warn`=1（警告）/ `Error`=2（エラー）/ `Fatal`=2147483647

**`ApiResponse` でラップされないエンドポイント:**

| エンドポイント | 戻り型 |
|---|---|
| `GET /v1/fx/master-list/*` | `List<KeyValue>` |
| `GET /v1/fx/symbol/currency-pair-list` | `List<SymbolDto>` |
| `GET /v1/fx/symbol/currency-index-list` | `List<SymbolDto>` |
| `GET /v1/fx/symbol/{symbol}` | `SymbolDto`（直接） |
| `POST /v1/fx/symbol` | ボディなし（200 OK） |
| `PUT /v1/fx/symbol/{symbol}` | ボディなし（200 OK） |
| `GET /v1/fx/country/{code}` | `CountryDto`（直接） |
| `POST /v1/fx/country` | ボディなし（200 OK） |
| `PUT /v1/fx/country/{code}` | ボディなし（200 OK） |
| `GET /v1/fx/summer-time/{targetYear}` | `SummerTimeDto`（直接） |
| `POST /v1/fx/summer-time` | ボディなし（200 OK） |
| `PUT /v1/fx/summer-time/{targetYear}` | ボディなし（200 OK） |
| `GET /v1/fx/economic-indicator/{countryCode}/{code}` | `EconomicIndicatorDto`（直接） |
| `POST /v1/fx/economic-indicator` | ボディなし（200 OK） |
| `PUT /v1/fx/economic-indicator/{countryCode}/{code}` | ボディなし（200 OK） |
| `GET /v1/fx/bar-data/{symbolType}/{barType}` | `List<BarDataImportResult>` |
| `POST /v1/fx/bar-data/import-csv/{symbol}/{barType}/{skipLatest}` | `BarDataImportResult` |
| `GET /v1/fx/economic-indicator-data/{countryCode}/{code}/{publication}` | `EconomicIndicatorDataDto`（直接） |
| `POST /v1/fx/economic-indicator-data` | ボディなし（200 OK） |
| `PUT /v1/fx/economic-indicator-data/{countryCode}/{code}/{publication}` | ボディなし（200 OK） |
| `POST /v1/fx/economic-indicator-data/import-text` | `List<FileImportResult>` |

### 認証

- 全エンドポイントで JWT（Authorization ヘッダー）が必要
- `/v1/fx/master-list/**` のみ認証不要（`SecurityConfig` の `permitAll` で制御。下表で「公開」と表記）
- 管理者専用エンドポイントは `@PreAuthorize("hasRole('ADMIN')")` で制御（非管理者は 403）

---

## Auth

| メソッド | パス | 説明 |
|---|---|---|
| POST | `/v1/auth/login` | ログイン。JWT の email と Base64 デコードしたリクエストの email を照合し、AuthUser を Redis に保存 |
| POST | `/v1/auth/logout-api` | ログアウト。Redis セッションを削除 |

---

## User

| メソッド | パス | 説明 |
|---|---|---|
| GET | `/v1/user` | プロフィール取得。未承認の場合は `ReturnCode: Warn` |
| POST | `/v1/user` | ユーザー登録。`sub`・`email` は JWT から取得 |
| PUT | `/v1/user/{userId}` | ユーザー情報更新。`{userId}` は Base64 エンコード済み。他ユーザーは 403 |

---

## Admin（管理者専用 — 非管理者は 403）

| メソッド | パス | 説明 |
|---|---|---|
| POST | `/v1/admin/users` | ユーザー検索（`emailAddress`・`approved`・ページング） |
| PUT | `/v1/admin/users/approved/{userId}` | ユーザー承認 |
| PUT | `/v1/admin/users/block/{userId}` | ユーザーブロック / 解除 |
| PUT | `/v1/admin/users/admin/{userId}` | 管理者権限の付与 / 剥奪 |
| GET | `/v1/admin/master-refresh` | Redis マスターキャッシュのステータス取得 |
| PUT | `/v1/admin/master-refresh` | Redis マスターキャッシュをリフレッシュ |

---

## FX - Master List（公開 — 認証不要）

| メソッド | パス | 説明 |
|---|---|---|
| GET | `/v1/fx/master-list/symbol/{symbolType}` | シンボル一覧（`symbolType` でフィルタ） |
| GET | `/v1/fx/master-list/country` | 国一覧 |
| GET | `/v1/fx/master-list/currency-pair` | 通貨ペア一覧 |
| GET | `/v1/fx/master-list/currency-index` | 通貨インデックス一覧 |
| GET | `/v1/fx/master-list/economic-indicator/{countryCode}` | 経済指標一覧（国コードでフィルタ） |

---

## FX - Symbol

| メソッド | パス | 説明 |
|---|---|---|
| GET | `/v1/fx/symbol/currency-pair-list` | 通貨ペア一覧（`SymbolType.Trade`、最大500件） |
| GET | `/v1/fx/symbol/currency-index-list` | 通貨インデックス一覧（`SymbolType.Analyze`、最大500件） |
| POST | `/v1/fx/symbol/search` | シンボル検索（`symbolType`・ページング） |
| POST | `/v1/fx/symbol` | シンボル追加 |
| GET | `/v1/fx/symbol/{symbol}` | シンボル取得 |
| PUT | `/v1/fx/symbol/{symbol}` | シンボル更新 |

---

## FX - Country

| メソッド | パス | 説明 |
|---|---|---|
| POST | `/v1/fx/country/search` | 国検索（ページング） |
| POST | `/v1/fx/country` | 国追加 |
| GET | `/v1/fx/country/{code}` | 国取得 |
| PUT | `/v1/fx/country/{code}` | 国更新 |

---

## FX - Summer Time

| メソッド | パス | 説明 |
|---|---|---|
| POST | `/v1/fx/summer-time/search` | サマータイム検索（ページング） |
| POST | `/v1/fx/summer-time` | サマータイム追加 |
| GET | `/v1/fx/summer-time/{targetYear}` | サマータイム取得 |
| PUT | `/v1/fx/summer-time/{targetYear}` | サマータイム更新 |

---

## FX - Economic Indicator

| メソッド | パス | 説明 |
|---|---|---|
| POST | `/v1/fx/economic-indicator/search` | 経済指標検索（`countryCode`・`importance`・`name`・ページング） |
| POST | `/v1/fx/economic-indicator` | 経済指標追加 |
| GET | `/v1/fx/economic-indicator/{countryCode}/{code}` | 経済指標取得 |
| PUT | `/v1/fx/economic-indicator/{countryCode}/{code}` | 経済指標更新 |

---

## FX - Economic Indicator Data

| メソッド | パス | 説明 |
|---|---|---|
| POST | `/v1/fx/economic-indicator-data/search` | 経済指標データ検索（`code`・`importance`・`countryCode`・`publicationBaseDate`・ページング） |
| POST | `/v1/fx/economic-indicator-data` | 経済指標データ追加 |
| GET | `/v1/fx/economic-indicator-data/{countryCode}/{code}/{publication}` | 経済指標データ取得（`publication` は `yyyy-MM-dd HH:mm:ss`） |
| PUT | `/v1/fx/economic-indicator-data/{countryCode}/{code}/{publication}` | 経済指標データ更新 |
| POST | `/v1/fx/economic-indicator-data/import-text` | テキストファイル一括インポート（`multipart/form-data`、`uploadFileList`） |

---

## FX - Bar Data

| メソッド | パス | 説明 |
|---|---|---|
| POST | `/v1/fx/bar-data` | バーデータ検索（`symbol`・`barType`・日付範囲・ページング） |
| POST | `/v1/fx/bar-data/import-csv/{symbol}/{barType}/{skipLatest}` | CSV インポート（`multipart/form-data`、`uploadFile`） |
| GET | `/v1/fx/bar-data/{symbolType}/{barType}` | インポートステータス取得 |

---

## FX - ZigZag

| メソッド | パス | 説明 |
|---|---|---|
| POST | `/v1/fx/zigzag` | ZigZag 検索（`barType`・`symbol`・`depth`・各種フィルタ・ページング） |
| POST | `/v1/fx/zigzag/status` | ZigZag ステータス取得（`symbolType`・`barType`・`depth`） |
| POST | `/v1/fx/zigzag/generate` | ZigZag 生成（`symbol`・`barType`・`depth`・`barDateTime`・`loadSize`） |
| POST | `/v1/fx/zigzag/bar-data` | ZigZag バーデータ取得（`barType`・`symbol`・`depth`・`waveStart`・`wave`） |

---

## FX - Trade Simulation

| メソッド | パス | 説明 |
|---|---|---|
| POST | `/v1/fx/trade/simulation` | トレードシミュレーション（`riskAmount`・`firstLotRatio`・`entry`・`positionList`） |
