package jp.co.next_evolution.sandbox.domain.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthUser(
    String sub,
    String email,
    Boolean emailVerified,
    Boolean admin
) implements UserDetails {

  @JsonIgnore
  public boolean isAdmin() {
    return Boolean.TRUE.equals(admin);
  }

  @JsonIgnore
  @Override
  public String getUsername() {
    return sub;
  }

  @JsonIgnore
  @Override
  public String getPassword() {
    return "";
  }

  @JsonIgnore
  @Override
  public boolean isEnabled() {
    return Boolean.TRUE.equals(emailVerified);
  }

  @JsonIgnore
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

}
