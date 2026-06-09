package jp.co.next_evolution.sandbox.application.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "sandbox.storage.data")
public class SandboxStorageProperties {

  private String bucket;

  private String fx;

}