package jp.co.next_evolution.sandbox.api.config;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

// 起動完了時に、生成済みの OpenAPI 仕様（/v3/api-docs.yaml）を
// documents/architecture/api-docs.yaml へ自動コピーする。
// gin/laravel 等の他バックエンドが参照する横断の「API共通仕様」を、springboot起動のたびに最新化する目的。
// SANDBOX_HOME環境変数が未設定、または documents/architecture ディレクトリが存在しない環境
// （他の開発者・CI等）では何もせず起動を継続する。
@Slf4j
@Component
public class OpenApiExportListener implements ApplicationListener<ApplicationReadyEvent> {

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    String sandboxHome = System.getenv("SANDBOX_HOME");
    if (sandboxHome == null || sandboxHome.isBlank()) {
      log.debug("SANDBOX_HOME 未設定のため OpenAPI 仕様のエクスポートをスキップします。");
      return;
    }

    Path targetPath = Path.of(sandboxHome, "documents", "architecture", "api-docs.yaml");
    if (!Files.isDirectory(targetPath.getParent())) {
      log.debug("エクスポート先ディレクトリが存在しないためスキップします: {}", targetPath.getParent());
      return;
    }

    Environment env = event.getApplicationContext().getEnvironment();
    String port = env.getProperty("local.server.port", env.getProperty("server.port", "8080"));
    String contextPath = env.getProperty("server.servlet.context-path", "");
    String url = "http://localhost:" + port + contextPath + "/v3/api-docs.yaml";

    try {
      String yaml = RestClient.create().get().uri(url).retrieve().body(String.class);
      Files.writeString(targetPath, yaml);
      log.info("OpenAPI仕様をエクスポートしました: {}", targetPath);
    } catch (Exception e) {
      log.warn("OpenAPI仕様のエクスポートに失敗しました（起動は継続します）: {}", e.getMessage());
    }
  }
}
