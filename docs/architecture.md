# アーキテクチャ詳細

Spring Boot 3 / Java 21 マルチモジュール Gradle プロジェクト。DDD（ドメイン駆動設計）に基づく厳格なレイヤー分離。

---

## モジュール構成

| モジュール | 役割 |
|---|---|
| `sandbox-domain` | ドメインモデル（値オブジェクト・集約）、リポジトリインターフェース、ドメイン例外。Spring Context / Spring Security Core のみに依存。 |
| `sandbox-application` | ユースケース（`*UseCase.java`）、コマンド、DTO。`sandbox-domain` のみに依存。各ユースケースは `@Service` クラスで `execute()` メソッドを持つ。 |
| `sandbox-infrastructure` | リポジトリ実装（`*RepositoryImpl`）、MyBatis マッパー、Redis 設定。 |
| `sandbox-api` | REST コントローラー、リクエスト/レスポンス DTO、`GlobalExceptionHandler`。実行可能 JAR を生成。 |
| `sandbox-security` | JWT フィルター（`JwtAuthFilter`）、認証インターセプター（`AuthInterceptor`）、Spring Security 設定。`sandbox-domain` に依存。 |

---

## 認証フロー

1. 全リクエストが `JwtAuthFilter`（OncePerRequestFilter）を通過
   - RS256 JWT を検証
   - Redis から `AuthUser`（admin・approved フラグ含む）を取得
     - **セッションあり**: Redis の `AuthUser` を使い TTL をリセット
     - **セッションなし**: `sandbox_user` テーブルから `AuthUser` を復元して Redis に保存（silent login）
     - **DB にも存在しない**: `SecurityContextHolder` に何もセットしない → 401
   - `SecurityContextHolder` にセット
2. Spring Security `SecurityFilterChain` が認可を制御
   - `/v1/fx/master-list/**` — `permitAll`（認証不要）
   - `/**` — `hasRole("MEMBER")`（`approved=true` のユーザーのみ通過）
   - 管理者専用エンドポイント — `@PreAuthorize("hasRole('ADMIN')")`（`admin=true` のユーザーのみ通過）
3. `POST /api/v1/auth/login` — JWT のメール情報と BASE64 デコードしたリクエストボディのメールを照合 → DB から `User`（admin・approved フラグ含む）を取得 → `AuthUser` を Redis に保存

---

## AuthUser

`AuthUser`（`sandbox-domain/.../model/auth/AuthUser.java`）は JWT クレームと DB の admin・approved フラグを保持する Record。

- フィールド: `sub`, `email`, `emailVerified`, `admin`, `approved`
- `isAdmin()` — 管理者判定、`isApproved()` — 承認済み判定
- `getAuthorities()` — `approved=true` なら `ROLE_MEMBER`、`admin=true` なら `ROLE_ADMIN` を返す
- `UserDetails` を implements しているが、`getUsername()` / `getPassword()` / `isEnabled()` / `getAuthorities()` には `@JsonIgnore` を付与し、Redis の JSON にはレコードコンポーネントのみ保存される
- `JwtProvider.parse()` では JWT から admin/approved 情報を得られないため `admin=false, approved=false` で生成し、`JwtAuthFilter` で Redis から正しいフラグ付き `AuthUser` を上書き取得する

---

## Redis 利用

- **セッション**: `SessionRepository`（ドメインインターフェース）/ `RedisSessionRepositoryImpl` で管理
- **マスターデータキャッシュ**: `MasterCacheRepository` でキャッシュアサイドパターンを実装
  - Redis を読む → ミスの場合 DB から取得して Redis に書き戻す
  - キャッシュキーは `Symbol.class.getSimpleName() + symbolType` で構築

---

## API 規約

- ベースパス: `/api`（context-path）、バージョニング: `/v1/...`
- 基本レスポンスは `ApiResponse` / `ApiSearchResponse` でラップし `ReturnCode` を含む
- マスター系・一部操作系エンドポイントは生 DTO / `List<T>` / 空ボディ（200 OK）を返す（詳細は `docs/api.md` 参照）
- Checkstyle（`checkstyle.xml`）はビルド時に自動実行
