# Tips

## VS Code 設定（`.vscode/settings.json`）

このプロジェクトには VS Code 向けの推奨設定が含まれています。

```json
{
  "java.compile.nullAnalysis.mode": "disabled",
  "java.project.outputPath": "build",
  "java.import.gradle.enabled": true,
  "java.import.gradle.wrapper.enabled": true,
  "java.configuration.updateBuildConfiguration": "automatic",
  "java.debug.settings.hotCodeReplace": "auto",
  "java.inlayHints.parameterNames.enabled": "literals",
  "editor.formatOnSave": true,
  "editor.codeActionsOnSave": {
    "source.organizeImports": "explicit"
  },
  "files.exclude": {
    "**/build": true,
    "**/.gradle": true,
    "**/bin": true
  },
  "search.exclude": {
    "**/build": true,
    "**/.gradle": true,
    "**/bin": true
  },
  "[java]": {
    "editor.tabSize": 4
  }
}
```

### 各設定の説明

| キー | 値 | 説明 |
|---|---|---|
| `java.compile.nullAnalysis.mode` | `"disabled"` | null 解析を無効化。誤検知による警告ノイズを抑制する |
| `java.project.outputPath` | `"build"` | コンパイル出力先を Gradle のビルドディレクトリ（`build/`）に統一する |
| `java.import.gradle.enabled` | `true` | Gradle プロジェクトのインポートを有効化する |
| `java.import.gradle.wrapper.enabled` | `true` | Gradle Wrapper（`gradlew`）経由でのビルドを使用する |
| `java.configuration.updateBuildConfiguration` | `"automatic"` | `build.gradle` 変更時にプロジェクトを自動再インポートする |
| `java.debug.settings.hotCodeReplace` | `"auto"` | デバッグ中にコードを変更・保存すると再起動なしでクラスを差し替える |
| `java.inlayHints.parameterNames.enabled` | `"literals"` | メソッド呼び出し時にリテラル引数の引数名をインライン表示する |
| `editor.formatOnSave` | `true` | ファイル保存時に自動フォーマットを実行する |
| `editor.codeActionsOnSave` | `organizeImports` | 保存時に未使用 import の削除と順序整理を行う |
| `files.exclude` | `build`, `.gradle`, `bin` | エクスプローラーからビルド成果物を非表示にする |
| `search.exclude` | `build`, `.gradle`, `bin` | 全文検索からビルド成果物を除外する |
| `[java] editor.tabSize` | `4` | Java ファイルのタブサイズを 4 に統一する |

### `bin` フォルダについて

VS Code の Java 言語サーバーは、Gradle マルチモジュール構成において各モジュール配下に `bin/` フォルダを生成する。
`files.exclude` はあくまでエクスプローラーと検索から**非表示にする**設定であり、フォルダの生成自体は止まらない。

| 対策 | 設定箇所 | 効果 |
|---|---|---|
| git 追跡から除外 | `.gitignore`（`**/bin/` 記載済み） | リポジトリには含まれない |
| VS Code から非表示 | `files.exclude` / `search.exclude` | エクスプローラー・検索に表示されない |

`java.project.outputPath` をモジュールごとに指定する方法もあるが、言語サーバーが独自に `bin` を使うため完全な抑制は難しい。上記 2 つの対策の組み合わせが現実的な運用となる。

---

## 推奨 VS Code 拡張機能（Spring Boot 関連）

| 拡張機能 ID | 名前 | 説明 |
|---|---|---|
| `redhat.java` | Language Support for Java (Red Hat) | Java の構文解析・補完・リファクタリング基盤 |
| `vscjava.vscode-java-debug` | Debugger for Java | Java デバッガー（ブレークポイント・ホットコードリプレース） |
| `vscjava.vscode-gradle` | Gradle for Java | Gradle タスクの GUI 実行・依存関係の確認 |
| `vmware.vscode-spring-boot` | Spring Boot Tools | `application.properties` / `application.yml` の補完・Spring Bean の解析 |
| `vmware.vscode-boot-dev-pack` | Spring Boot Extension Pack | 上記 Spring Boot 関連拡張をまとめたパック |
| `vscjava.vscode-spring-boot-dashboard` | Spring Boot Dashboard | アプリの起動・停止・エンドポイント一覧をサイドバーで管理 |
| `vscjava.vscode-spring-initializr` | Spring Initializr Java Support | Spring Initializr を使ったプロジェクト生成 |
| `vscjava.vscode-maven` | Maven for Java | Maven プロジェクトのサポート（Gradle プロジェクトでも補助的に利用可） |

---

## トラブルシューティング

### F12（定義元へ移動）が動かない

以前は動いていたのに突然動かなくなった場合の確認手順。

#### 1. 拡張機能が有効になっているか確認

`Cmd+Shift+X` → `Language Support for Java` で検索し、**無効化されていないか**確認する。
インストール済みでも無効状態だと定義ジャンプは機能しない。

#### 2. Java 言語サーバーのキャッシュをリセット

```
Cmd+Shift+P → "Java: Clean Java Language Server Workspace" → 再起動
```

言語サーバーのキャッシュが破損している場合に有効。再起動後にインデックスが完了するまで待つ。

#### 3. キーバインドの競合確認

`Cmd+K Cmd+S` → `revealDefinition` で検索し、F12 が `editor.action.revealDefinition` に割り当てられているか確認する。
別の拡張機能が F12 を上書きしている場合は競合が表示される。
