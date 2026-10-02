# コードレビュー指摘事項

2026-10-02 のコードレビュー（直近コミット `git diff HEAD~1` 対象）で挙がった指摘のうち、対応検討が必要なもの。
レビューは差分と周辺ファイルの読み取りのみで、コードの実行・テストはしていない。

> 旧 `/v1/auth/login` エンドポイントの削除（旧クライアントが 401/403 になる懸念）は、確認済みで問題なしのため本ファイルの対象外。

| # | 重要度 | 概要 | 対象 |
|---|---|---|---|
| 1 | 中 | SPA が XSRF-TOKEN クッキーを読めない可能性 | `SecurityConfig.java:50` |
| 2 | 中 | クッキー有効期限と JWT 期限のずれ | `JwtCookieProvider.java:26` |
| 4 | 軽微 | CSRF スキップ条件が広すぎる | `SecurityConfig.java:58` |
| 5 | 軽微 | Warn ログインでも JWT クッキーを発行 | `AuthController.loginWeb` |
| 6 | 軽微 | `settings.local.json` がコミットされている | `.claude/settings.local.json` |
| 7 | 軽微 | 不要な `Set-Cookie: XSRF-TOKEN` | `CsrfCookieFilter.java:27` |

---

## 1. SPA が XSRF-TOKEN クッキーを読めない可能性（中）

- **対象**: `sandbox-security/.../config/SecurityConfig.java:50`
- **内容**: `XSRF-TOKEN` クッキーを API ホストが `Domain` 属性なしで発行している。React が API と別ホストで配信される場合、SPA の `document.cookie` から読めない。
- **影響**: SPA が `X-XSRF-TOKEN` ヘッダーを送れず、クッキー認証の POST / PUT / DELETE がすべて CSRF 検証で 403 になる。同一ホストのプロキシ、または共通の親 `Domain` を前提にしないと動作しない。
- **対応案**:
  - 配信構成（同一ホストのプロキシか、別ホストか）を確定する。
  - 別ホストなら、共通の親ドメインを `Domain` に指定する。
  - CLAUDE.md の「CSRF Cookie の Path（落とし穴）」と合わせて、Path・Domain・Secure・SameSite の属性を JWT クッキー（`JwtCookieProvider`）と揃える。
- **関連**: [auth.md](../../documents/architecture/auth.md) の CORS / CSRF の記述（別オリジン配信・Cookie 属性）に関わる。認証・認可の全体見直し（React の HttpOnly Cookie 化）の検討時に合わせて確認する。

## 2. クッキー有効期限と JWT 期限のずれ（中）

- **対象**: `sandbox-security/.../JwtCookieProvider.java:26`
- **内容**: クッキーの有効期限は `sandbox.session-ttl`（3600 秒）だが、中身の Cognito JWT には独自の期限があり、更新処理もない。
- **影響**: JWT の有効期限がクッキーより短いと、クッキーが有効に見えるのに `JwtAuthFilter` が認証コンテキストを消し、ユーザーは再ログインまで 401 になる。
- **対応案**:
  - クッキーの有効期限を JWT の `exp` に合わせる。
  - またはリフレッシュトークンによる更新を設ける。
  - Cognito 側のトークン有効期限と `sandbox.session-ttl` の関係を整理する。
- **関連**: [auth.md](../../documents/architecture/auth.md) の「未検証・未確定事項」にある Cookie / CSRF Cookie の Max-Age・更新方針（未定）そのもの。方針は auth.md 側で決め、実装修正は本プロジェクトで行う。

## 4. CSRF スキップ条件が広すぎる（軽微）

- **対象**: `sandbox-security/.../config/SecurityConfig.java:58`
- **内容**: `Authorization` ヘッダーが `Bearer ` で始まる要求は、トークンの検証前に CSRF チェックをスキップする。
- **影響**: 不正な Bearer ヘッダーとクッキーを併せて送ると CSRF チェックを回避できる。ただしその後トークン検証に失敗するため、悪用経路は確認されていない。`JwtAuthFilter` が認証コンテキストを消すことに依存した設計になっている。
- **対応案**: CSRF スキップの条件を絞る（例: クッキー認証が成立していない場合のみ免除する）。

## 5. Warn ログインでも JWT クッキーを発行（軽微）

- **対象**: `sandbox-api/.../AuthController.java`（`loginWeb`）
- **内容**: `executeLogin` が `ReturnCode.Warn`（`userDto == null`）を返しても、JWT クッキーを発行する。
- **影響**: ユースケースが受理しなかったログインに対しても、有効期限付きの HttpOnly セッションクッキーが発行される。
- **対応案**: `ReturnCode.Warn` のときはクッキーを発行せず、適切なエラー応答を返す。

## 6. `settings.local.json` がコミットされている（軽微）

- **対象**: `.claude/settings.local.json`
- **内容**: `*.local.json` は開発者ごとの設定ファイルだが、コミットされている。`git checkout *`、`git pull *`、`gh api *`、`WebSearch` の許可が全クローンに配布される。
- **影響**: 個人設定の意味が失われ、意図しない許可が共有される。
- **対応案**:
  - `.gitignore` に `.claude/settings.local.json` を追加し、リポジトリから外す（`git rm --cached`）。
  - 共有したい許可は `.claude/settings.json` に移す。
- **注意**: sandbox 直下は git リポジトリではない。コミットされているのは `sandbox-api-springboot` 側のリポジトリか、実際に確認する。

## 7. 不要な `Set-Cookie: XSRF-TOKEN`（軽微）

- **対象**: `sandbox-security/.../filter/CsrfCookieFilter.java:27`
- **内容**: `getToken()` を全リクエストで呼ぶため、CSRF 対象外の Flutter（Bearer）リクエストのレスポンスにも毎回 `Set-Cookie: XSRF-TOKEN` が付く。
- **影響**: 機能上の問題はないが、無駄なクッキー発行とレスポンスサイズの増加になる。
- **対応案**: Bearer 認証のリクエストでは `getToken()` を呼ばないようにする。
