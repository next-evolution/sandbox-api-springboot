# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 言語設定
- 常に日本語で会話する
- コメントも日本語で記述する
- エラーメッセージの説明も日本語で行う

## Build & Run

```bash
# Build all modules
./gradlew build

# Run the API server (requires env vars — see below)
./gradlew :sandbox-api:bootRun

# Build without tests
./gradlew build -x test

# Checkstyle (runs automatically on build)
./gradlew checkstyleMain
```

### Required Environment Variables

| Variable | Example |
|---|---|
| DB_HOST | localhost |
| DB_PORT | 43306 |
| DB_SCHEMA | sandbox_local |
| DB_USER | sandbox_app |
| DB_PASSWORD | s4ndb0x_app |
| REDIS_HOST | localhost |
| REDIS_PORT | 46379 |
| JWT_ISSUER1 | https://cognito-idp.ap-northeast-1.amazonaws.com/... |
| JWT_AUDIENCE1/2/3 | Cognito App Client IDs |
| JWT_ORIGIN1/2 | http://localhost, http://localhost:3000 |

### Local Infrastructure

```bash
# Start MySQL + Redis via Docker
docker compose up -d
```

MySQL is exposed on port 43306, Redis on 46379.

## Architecture

This is a **Spring Boot 3 / Java 21 multi-module Gradle project** following DDD (Domain-Driven Design) with strict layer separation.

### Module Dependency Flow

```
sandbox-api  →  sandbox-application  →  sandbox-domain
     ↓                                        ↑
sandbox-security               sandbox-infrastructure
     ↓                                        ↑
                  (runtimeOnly) ──────────────┘
```

- **sandbox-domain** — Domain models (value objects, aggregates), repository interfaces, and domain exceptions. No framework dependencies beyond Spring Security Core.
- **sandbox-application** — Use cases (`*UseCase.java`), commands, and DTOs. Depends only on `sandbox-domain`. Each use case is a single `@Service` class with an `execute()` method.
- **sandbox-infrastructure** — Repository implementations (`*RepositoryImpl`), MyBatis mappers, Redis config. Uses `Collectors.toList()` (not `Stream.toList()`) to produce mutable `ArrayList` for Redis serialization compatibility.
- **sandbox-api** — REST controllers, request/response DTOs, `GlobalExceptionHandler`. Produces the executable JAR.
- **sandbox-security** — JWT filter (`JwtAuthFilter`), auth interceptor (`AuthInterceptor`), Spring Security config.

### Authentication Flow

1. All requests pass through `JwtAuthFilter` (OncePerRequestFilter) — validates RS256 JWT (AWS Cognito), then looks up `AuthUser` from Redis (which holds the `admin` flag), sets `SecurityContextHolder`, updates Redis session TTL.
2. `AuthInterceptor` (HandlerInterceptor) checks `SecurityContextHolder` for `AuthUser`. Returns 401 if absent.
3. Mark controller methods with `@PublicApi` to skip auth in the interceptor.
4. `POST /api/v1/auth/login` — verifies JWT email matches BASE64-decoded request body email, fetches `User` from DB (including `admin` flag), then saves `AuthUser` with `admin` flag to Redis.

### AuthUser

`AuthUser` (`sandbox-domain/.../model/auth/AuthUser.java`) は JWT クレームと DB の admin フラグを保持する Record。

- フィールド: `sub`, `email`, `emailVerified`, `admin`
- `isAdmin()` メソッドで管理者判定
- `UserDetails` を implements しているが、`getUsername()` / `getPassword()` / `isEnabled()` / `getAuthorities()` には `@JsonIgnore` を付与し、Redis の JSON にはレコードコンポーネントのみ保存される
- `JwtProvider.parse()` では JWT から admin 情報を得られないため `admin=false` で生成し、`JwtAuthFilter` で Redis から admin 付き `AuthUser` を上書き取得する

### パスパラメータの userId

パスに `{userId}` を含むエンドポイントでは、userId は Base64 エンコード済みで渡される。`UserId.decodeUserIdValue()` でデコードすること。

```java
String userId = UserId.decodeUserIdValue(userIdBase64);
```

また、userId・email はリクエストボディには含めず、JWT の `AuthUser` から取得する（`authUser.sub()`, `authUser.email()`）。

### 管理者専用 API の実装パターン

```java
@GetMapping
public ResponseEntity<ApiResponse> someAdminApi(@AuthenticationPrincipal AuthUser authUser) {
    if (!authUser.isAdmin()) {
        throw new ForbiddenException("管理者用APIです");
    }
    // useCase 実行
}
```

- `ForbiddenException` (`sandbox-domain/.../exception/ForbiddenException.java`) → HTTP 403 FORBIDDEN

### Redis Usage

- Sessions are stored and managed via `SessionRepository` (domain interface) / `RedisSessionRepositoryImpl`.
- Master data (FX symbols, countries) is cached in Redis via `MasterCacheRepository` with a cache-aside pattern: read from Redis → on miss, read from DB and write back to Redis. Cache keys are built from `Symbol.class.getSimpleName() + symbolType`.

### API Conventions

- Base path: `/api` (context-path), versioned as `/v1/...`
- All responses wrap data in `ApiResponse` / `ApiSearchResponse` with a `ReturnCode` (Ok, Warn, etc.).
- Domain exceptions are mapped to HTTP status codes in `GlobalExceptionHandler`:
  - `AuthenticationException` → 401 UNAUTHORIZED
  - `ForbiddenException` → 403 FORBIDDEN
  - `NotFoundException` → 404 NOT FOUND
  - `DuplicateException`, `InsertException`, `UpdateException` → 400 BAD REQUEST
- Checkstyle (`checkstyle.xml`) is enforced on every build.

### Package Root

`jp.co.next_evolution.sandbox`

### Stream / Collection Convention

- In `sandbox-application`: use `Stream.toList()` (immutable, fine for non-Redis use).
- In `sandbox-infrastructure`: use `.collect(Collectors.toList())` (returns mutable `ArrayList`, required for Redis serialization).

### DateTime Format Convention

- API の `LocalDateTime` フィールドは RFC 3339 形式 `yyyy-MM-dd'T'HH:mm:ssXXX`（例: `2026-01-02T11:22:33+09:00`）で統一する。
- シリアライズ/デシリアライズは `JacksonConfig`（`sandbox-api/config/JacksonConfig.java`）でグローバル設定済み。JST固定（`ZoneOffset.ofHours(9)`）で `LocalDateTime ↔ OffsetDateTime` 変換を行う。
- Request / Response DTOに `@JsonFormat` / `@JsonSerialize` / `@JsonDeserialize` を個別付与しない（グローバル設定が適用される）。
- `LocalDate` 型は対象外。既存の `@JsonFormat(pattern = "yyyy-MM-dd")` をそのまま使用する。
