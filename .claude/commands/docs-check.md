# docs-check

`docs/api.md` と `docs/architecture.md` が実装と乖離していないかチェックする。

## 手順

以下を順番に実施し、差異をまとめてレポートする。

### 1. コントローラー vs `docs/api.md`

- `sandbox-api/src/main/java/**/controller/` 配下の全コントローラーを読む
- 各エンドポイント（HTTPメソッド・パス・`@PublicApi`の有無）を抽出し、`docs/api.md` の記載と照合する
- レスポンス型（`ApiResponse` でラップされているか、直接返却か）を確認し、`docs/api.md` の共通仕様と照合する

### 2. `ReturnCode` vs `docs/api.md`

- `sandbox-api/src/main/java/**/type/ReturnCode.java` を読む
- 列挙値と `@JsonValue` の型（文字列か整数か）を確認し、`docs/api.md` の記載と照合する

### 3. 認証フロー vs `docs/architecture.md`

- `sandbox-security/src/main/java/**/filter/JwtAuthFilter.java` を読む
- `sandbox-security/src/main/java/**/interceptor/AuthInterceptor.java` を読む
- 実際のフローと `docs/architecture.md` の「認証フロー」セクションを照合する

### 4. `AuthUser` vs `docs/architecture.md`

- `sandbox-domain/src/main/java/**/model/auth/AuthUser.java` を読む
- フィールド・メソッド・`@JsonIgnore` の付与状況と `docs/architecture.md` の「AuthUser」セクションを照合する

### 5. モジュール依存 vs `docs/architecture.md`

- `settings.gradle` と各モジュールの `build.gradle` を読む
- 依存関係図と `docs/architecture.md` の「モジュール構成」を照合する

### 6. `@Schema` ルール vs `CLAUDE.md`

CLAUDE.md の「@Schema の type 指定」に準拠しているか、全 DTO・Request・Response クラスを確認する。

- `sandbox-api/src/main/java/**/dto/**/*.java` と `sandbox-application/src/main/java/**/dto/**/*.java` 配下の全ファイルを読む
- **禁止事項**: `@Schema(type = "...")` が標準 Java 型（`String`、`boolean`、`int`/`short`/`long`、`BigDecimal`、`LocalDateTime`）に付いていないか
- **enum の `implementation`**: `@JsonValue` を持つ enum フィールドに `type = "..."` ではなく `@Schema(implementation = Xxx.class)` が付いているか
- **example（日時）**:
  - `LocalDateTime` フィールドの example が `"2026-01-23T12:34:56+09:00"` 形式の実際の日時値か
  - `LocalDate` フィールドの example が `"2026-01-23"` 形式の実際の日付値か
  - `yyyyMMdd` パターン（`@Pattern(regexp = ".*[0-9]{8}.*")`）の `String` 日付フィールドの example が `"20260123"` のような実際の日付値か（`"yyyyMMdd"` 等のフォーマット文字列になっていないか）

## レポート形式

差異がある場合:

```
## 差異あり

### [ファイル名]
- **項目**: （ドキュメントの記載）
- **実装**: （実際の実装）
- **修正案**: （推奨される修正内容）
```

差異がない場合:

```
## 差異なし
docs/*.md と実装の間に乖離は見つかりませんでした。
```
