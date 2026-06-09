package jp.co.next_evolution.sandbox.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "jp.co.next_evolution.sandbox")
@ComponentScan(basePackages = {"jp.co.next_evolution.sandbox"})
public class SandboxApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(SandboxApiApplication.class, args);
  }

}
