package jp.co.next_evolution.sandbox.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class FxRateClientConfig {

  @Value("${genesis.external.gaitame-price-rate}")
  private String gaitamePriceRate;

  @Bean("fxRateGaitameClient")
  public RestClient fxRateGaitameClient() {
    return RestClient.builder()
                     .baseUrl(gaitamePriceRate)
                     .build();
  }

}
