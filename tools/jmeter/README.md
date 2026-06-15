# JMeter テスト方針

## ファイル構成

```
./
  README.md                    ← 本ファイル
  jmeter-exec.sh               ← 実行シェルスクリプト（全 JMX 共通）
  setup/                       ← DB 再構築後に1回だけ実行するもの
    sandbox_100_initialize.jmx ← 初期化（ユーザー登録・承認フロー）✅
    setup.sh                   ← setup 全体を直列実行するラッパー
  scenarios/                   ← 機能追加時に繰り返し実行するもの
    sandbox.jmx                ← ログイン単体確認用
    sandbox_200_register.jmx   ← 登録系 ✅
    sandbox_300_update.jmx     ← 更新系 🚧 未作成
    sandbox_400_search.jmx     ← 参照系（負荷テスト対象）🚧 未作成
    sandbox_600_admin.jmx      ← 管理系 🚧 未作成
  data/
    user.csv                   ← ログインユーザー（git 除外・要作成）
    admin_user.csv             ← 管理者ユーザー（git 除外・要作成）
    settings.csv               ← 設定情報（git 除外・要作成）
    user.csv.example           ← user.csv のテンプレート
    admin_user.csv.example     ← admin_user.csv のテンプレート
    settings.csv.example       ← settings.csv のテンプレート
    country.csv                ← 国データ
    symbol.csv
    summer_time.csv
    economic_indicator.csv
  response/
    {suffix}/                  ← レスポンスファイル出力先
  result/
    {suffix}/                  ← JMeter 集計結果出力先（result.csv, jmeter.log）
```

---

## data/ の準備

`user.csv` / `admin_user.csv` / `settings.csv` はセキュリティ情報（メールアドレス・パスワード等）を含むため git 管理対象外。
対応する `*.csv.example` をコピーしてリネームし、実際の値に書き換えて使用する。

```bash
cp data/user.csv.example       data/user.csv
cp data/admin_user.csv.example data/admin_user.csv
cp data/settings.csv.example   data/settings.csv
# 各 CSV を編集して実際のメールアドレス・パスワード等を設定する
```

---

## シナリオ概要

### setup/（DB 再構築後に1回だけ実行）

| JMX | ログインユーザー | Loop | 用途 | 状態 |
|---|---|---|---|---|
| `setup/sandbox_100_initialize.jmx` | user.csv 全件 | USER_COUNT | 初回ユーザー登録・管理者承認フロー（直列） | ✅ |

### scenarios/（機能追加時に繰り返し実行）

| JMX | ログインユーザー | Loop | 用途 | 状態 |
|---|---|---|---|---|
| `scenarios/sandbox.jmx` | user.csv 先頭1件 | 1 | ログイン単体確認 | ✅ |
| `scenarios/sandbox_200_register.jmx` | user.csv 先頭1件 | 1 | CSV データをそのまま登録 | ✅ |
| `scenarios/sandbox_300_update.jmx` | user.csv 先頭1件 | 1 | CSV データのキー以外の値を更新 | 🚧 未作成 |
| `scenarios/sandbox_400_search.jmx` | user.csv 全件 | USER_COUNT | 参照系（軽めの負荷テスト） | 🚧 未作成 |
| `scenarios/sandbox_600_admin.jmx` | admin_user.csv 先頭1件 | 1 | 管理者専用 API（user.csv 不使用） | 🚧 未作成 |

---

## setup/sandbox_100_initialize.jmx（初期化フロー）

初回ユーザー登録と管理者による承認を行う一連のフロー。
`sandbox_200_register.jmx` より先に実行する必要がある。

**全 ThreadGroup を直列実行（`TestPlan.serialize_threadgroups=true`）する前提。**  
**`user.csv` は TestPlan 直下に1つ定義し、全 ThreadGroup で共有する（`shareMode.all`, `recycle=true`）。**  
**LoopCount は `jmeter-exec.sh` が `wc -l data/user.csv` から自動計算して `-JUSER_COUNT` で渡す。**

### フロー

```
[TestPlan 直下]
  CSV: user.csv（email, password, nickName）shareMode.all, recycle=true

[SetUp Thread Group] 一般ユーザーログイン + ユーザー登録  ← num_threads=1, loops=${__P(USER_COUNT,4)}
  Step1: Cognito ログイン（一般ユーザー）      ← IdToken 取得
  Step2: Sandbox API 初回ログイン              ← returnCode:1（DB 未登録 Warn）を期待
  Step3: ユーザー登録 POST /api/v1/user        ← nickName（CSV から取得）を送信、approved=false で DB insert

[ThreadGroup] 管理者ログイン + ユーザー承認   ← num_threads=1, loops=${__P(USER_COUNT,4)}
  CSV: admin_user.csv（adminEmail, adminPassword）recycle=true（1件を繰り返し使用）
  Step4: Cognito ログイン（管理者）            ← adminIdToken 取得
  BeanShell: adminEmail を Base64 エンコード   ← adminEmailEncoded をセット / isFirstAdminLoop（"true"/"false"）をセット
  Step5: Sandbox API ログイン（管理者）        ← returnCode:0 を期待
  Step6: ユーザー検索 POST /api/v1/admin/users ← email で絞り込み、userId を RegexExtractor で抽出
  BeanShell: userId を Base64 エンコード       ← userIdEncoded をセット
  Step7: ユーザー承認 PUT /api/v1/admin/users/approved/{userIdEncoded} ← returnCode:0 を期待
  [IfController] isFirstAdminLoop == "true"（1件目のユーザーのみ）
    Step8: 管理者権限付与 PUT /api/v1/admin/users/admin/{userIdEncoded} ← body: {"admin":true}、returnCode:0 を期待

[ThreadGroup] 一般ユーザー再ログイン確認      ← num_threads=1, loops=${__P(USER_COUNT,4)}
  Step9: Cognito 再ログイン（一般ユーザー）    ← idToken を再取得
  Step10: Sandbox API 再ログイン              ← returnCode:0（承認済み Ok）を期待
```

### IfController の実装パターン

`useExpression=true` の IfController は変数置換後の値が文字列 `"true"` と一致するかを確認する。  
`${var} == 1` のような式は置換後も `"1 == 1"` という文字列になるため **常に false と判定される**。

**正しいパターン：BeanShell で `"true"` / `"false"` をセットして参照する**

```java
// BeanShell でループ回数を追跡
String countStr = vars.get("__adminIter");
int count = (countStr == null || countStr.isEmpty()) ? 0 : Integer.parseInt(countStr);
count++;
vars.put("__adminIter", String.valueOf(count));
vars.put("isFirstAdminLoop", count == 1 ? "true" : "false");
```

```
IfController 条件: ${isFirstAdminLoop}  （useExpression=true）
```

### Response Assertion

| 期待値 | 検証文字列 |
|---|---|
| Ok | `"returnCode":0` |
| Warn | `"returnCode":1` |

### CSV ファイル

| ファイル | カラム | 用途 |
|---|---|---|
| `data/user.csv` | `email, password, nickName` | 全 ThreadGroup 共有。行数が USER_COUNT として自動設定される |
| `data/admin_user.csv` | `adminEmail, adminPassword` | 管理者承認 ThreadGroup のみ。recycle=true で繰り返し使用 |

---

## 各 JMX の共通構造

全 JMX に Cognito ログイン → Sandbox API ログインを内包する。
JMX 間でのデータ引き継ぎは行わない（各 JMX が独立して実行可能）。

```
[CSV Data Set Config]          ← 対象 CSV を読み込む
[Step1] Cognito ログイン        ← IdToken 取得
[Step2] Sandbox API ログイン    ← POST /api/v1/auth/login
[Step3〜] 各 API リクエスト
  └─ ResponseAssertion         ← "returnCode":0 等を検証
  └─ Save Response to file     ← レスポンスボディを個別ファイルに保存
[ResultCollector] ツリー表示    ← GUI 確認用
[ResultCollector] CSV 出力     ← CLI 実行時の集計結果
```

---

## CSV 設定方針

- `shareMode.all`：全スレッドで CSV ポインタを共有し、順番に読み込む
- `recycle=true`：末尾に達したら先頭に戻る
- `ignoreFirstLine=true`：ヘッダー行をスキップ

### Thread 数と CSV の対応

| threads | 割り当てユーザー |
|---|---|
| 1 | CSV の 1 行目のみ |
| 3 | 1行目→2行目→3行目 を順番に割り当て |
| 4以上 | 末尾に達したら先頭に戻って繰り返し |

---

## CSV の使い方（country.csv の例）

| カラム | 登録系 | 更新系 |
|---|---|---|
| `code` | `${code}` | `${code}`（キー、変更なし） |
| `name` | `${name}` | `${name}` |
| `name_en` | `${name_en}` | `${name_en}_update` |
| `name_short` | `${name_short}` | `${name_short}` |
| `currency_code` | `${currency_code}` | `${currency_code}` |
| `sort_order` | `${sort_order}` | `${sort_order}` |

更新系は登録系と同じ CSV を使用し、`_update` を付加することで更新されたことを確認できる。

---

## レスポンス保存

`Save Response to file` PostProcessor を各ステップに付与し、レスポンスボディを個別ファイルに保存する。

- 保存先：`response/${suffix}/`（`<test_dir>` を起点とした相対パス）
- ファイル名：`${__time(yyyyMMdd_HHmmssSSS)}_${__threadNum}_` + ステップ名プレフィックス
- ファイル名にタイムスタンプとスレッド番号を含めることで重複を回避

```
response/20240614_001/
  20240614_123456789_1_Step2_login.json
  20240614_123456790_1_Step3_country_US.json
  ...
```

JMeter 集計結果（レイテンシ・ステータスコード等）は `ResultCollector` で別途 CSV に記録する。

- 保存先：`result/${suffix}/result.csv`
- 実行ログ：`result/${suffix}/jmeter.log`

---

## DB 再構築手順

`sandbox_100_initialize.jmx` を再実行する前に DB をクリーンな状態に戻す。

```bash
# プロジェクトルートで実行
# ボリューム込みで停止・削除
docker compose --env-file .env.compose down -v

# クリーンな状態で再起動（initdb.d のスクリプトが自動実行される）
docker compose --env-file .env.compose up -d
```

`initdb.d` のシェルスクリプト（DDL・DML）が再実行されるため、管理者ユーザーの INSERT まで自動で行われる。

---

## 実行フロー

**`jmeter-exec.sh` はスクリプト自身が存在するディレクトリ（`<test_dir>`）をカレントとして動作する。**  
JMX 内のパスはすべて `<test_dir>` 起点の相対パス（`data/`, `result/`, `response/`）で記述する。  
`<test_dir>` ごと別の場所に移動しても、フォルダ内の構造が同じであれば動作する。

### 初期構築（DB 再構築後に1回だけ実行）

```bash
# setup.sh はサブフォルダ内の全初期化 JMX を直列実行する
./setup/setup.sh 20240614_001
```

`setup.sh` の内部では `jmeter-exec.sh` を呼び出しているため、直接実行することも可能：

```bash
./jmeter-exec.sh setup/sandbox_100_initialize 20240614_001
```

### 機能テスト（機能追加時・繰り返し実行可能）

基本的に登録 → 更新 → 参照 → 管理の順で、各実行後に結果を確認してから次へ進む。

```bash
# 登録系（Thread=1 固定）
./jmeter-exec.sh scenarios/sandbox_200_register 20240614_001

# 更新系（Thread=1 固定）
./jmeter-exec.sh scenarios/sandbox_300_update 20240614_001

# 参照系（Thread 数を指定して軽めの負荷）
./jmeter-exec.sh scenarios/sandbox_400_search 20240614_001 5

# 管理系（Thread=1 固定）
./jmeter-exec.sh scenarios/sandbox_600_admin 20240614_001

# ログイン単体確認
./jmeter-exec.sh scenarios/sandbox 20240614_001
```

### jmeter-exec.sh 引数

```
Usage: ./jmeter-exec.sh <jmxPrefix> <resultSuffix> [threads] [loops] [rampUp]
  jmxPrefix    : JMX ファイル名のプレフィックス（例: sandbox_100_initialize）
  resultSuffix : 結果フォルダの suffix（例: 20240614_001）
  threads      : スレッド数（デフォルト: 1）
  loops        : ループ数（デフォルト: 1）
  rampUp       : Ramp-up 秒数（デフォルト: 1）
```

---

## 特定 API の再実行

JMeter GUI からファイルを開き、対象の ThreadGroup または Sampler を右クリック →「開始」で個別実行可能。
各 JMX は ThreadGroup を最小単位として構成する。
