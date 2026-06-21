package jp.co.next_evolution.sandbox.application.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "sandbox.app")
public class SandboxAppProperties {

  private List<String> indicatorExcludeList = new ArrayList<>();

  private List<String> indicatorStripList = new ArrayList<>();

  private int csvBulkLoadSize;

  private boolean importCheckSkip;

}
