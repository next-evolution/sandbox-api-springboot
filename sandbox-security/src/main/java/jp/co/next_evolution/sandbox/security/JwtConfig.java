package jp.co.next_evolution.sandbox.security;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@Getter
@Setter
@ConfigurationProperties("jwt")
public class JwtConfig {

  private List<String> allowedOriginList;

  private List<String> allowedIssList;

  private List<String> allowedAudienceList;

  public String[] getAllowedOrigins() {

    return CollectionUtils.isEmpty(this.allowedOriginList)
           ? new String[]{"http://localhost"}
           : allowedOriginList.toArray(new String[0]);
  }

}
