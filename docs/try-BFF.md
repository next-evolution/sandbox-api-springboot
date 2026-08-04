# BFF化・マイクロサービス化 検討ログ

React / Flutter 向け認証の BFF（Backend For Frontend）化を検討した際の議論のまとめ。

---

## 1. マイクロサービス化の検討（見送り）

`docs/api.md` を集計すると、エンドポイント数は **47個**（12リソースグループ: Auth/User/Admin/FX配下）。

- Symbol・Country・Economic-Indicator・Bar-Data・ZigZag・Trade-Simulation は互いに参照し合うマスタデータ群で、単一 MySQL に閉じている
- `sandbox-infrastructure` は MySQL（MyBatis）+ Redis（セッション・マスターキャッシュ）のみ
- この規模・結合度ではサービス分割のコスト（分散トランザクション、サービス間RPC、デプロイ・監視の複雑化）がメリットを上回る

**結論**: 現時点ではマイクロサービス化は見送り、モジュラーモノリスを維持する。将来 Oracle/PostgreSQL/Memcached など異種データストアを扱う新ドメインが増えた場合や、特定リソースだけスケール要求が大きく異なる場合に再検討する。

---

## 2. BFF認証の方針

Bearer TokenのBFF化を検討していたが、新規サービスを立てず **`sandbox-security` モジュール内に組み込む**方針とした。

- 認証基盤: AWS Cognito（RS256 JWT、React/Flutter共通IdP）
- フロントエンド: React（Web） / Flutter（モバイル）の2種類
- JWT検証ロジック（JWKS取得・RS256検証）は共通化し、トークンの受け渡し方式のみクライアント種別で出し分ける

| クライアント | トークン受け渡し |
|---|---|
| React | HttpOnly Cookie |
| Flutter | Bearer Token |

---

## 3. 具体設計

### ログインAPI

- React用・Flutter用でエンドポイントを分ける
- React: レスポンスで `Set-Cookie`（HttpOnly）を発行
- Flutter: レスポンスボディに Bearer Token を含める

### ログイン以外のAPI

- リクエストヘッダーのみ差異あり（`Authorization: Bearer` or Cookie）。レスポンスは完全に共通
- `JwtAuthFilter`（`OncePerRequestFilter`）のトークン抽出処理を分岐させる
  - `Authorization: Bearer` ヘッダーがあればそれを使用（Flutter）
  - なければ HttpOnly Cookie から抽出（React）
  - 以降のJWT検証・Redis参照・`SecurityContextHolder` セットは共通ロジックのまま
- MVC の `HandlerInterceptor` は Spring Security のフィルタチェーンより後段で動くため、認証判定には使わない

### CSRF対策

- React（Cookie方式）はブラウザが自動送信するため CSRF対策が必要。Flutter（Bearer方式）はクライアントが明示的にヘッダー付与するためCSRFの脅威モデルに該当しない
- 方針: **React/FlutterともにCSRFトークンを発行してレスポンスに含める。Flutterのリクエストに含まれるCSRFトークンは検証時に無視する**
  - Bearer Token方式はそもそもCSRFの対象外のため、発行のみ・検証スキップでも安全性に問題なし
  - CSRF検証要否の判定は、`JwtAuthFilter` のトークン抽出分岐と同じシグナル（`Authorization` ヘッダーの有無）を使う
  - Spring Securityの `CsrfFilter` に `RequestMatcher`（`requireCsrfProtectionMatcher` 相当）を渡し、「`Authorization: Bearer` ヘッダーがあれば検証スキップ、なければ（Cookie認証）検証する」の判定ロジックを1箇所に集約する

---

## 4. 未決事項 / TODO

- ログインAPIレスポンスにReact/Flutter識別用フィールドを追加するか（エンドポイントが分かれているため必須ではなく、ログ・デバッグ用途であれば任意）
- 現行 `SecurityConfig` のCSRF設定（Bearer Token前提でおそらく無効化）の確認・見直し
- 具体的な実装（Cookie発行エンドポイント、CSRF `RequestMatcher` の実装箇所）
