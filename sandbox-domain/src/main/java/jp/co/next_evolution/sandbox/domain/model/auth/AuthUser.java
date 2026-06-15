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
    boolean emailVerified,
    boolean admin
) implements UserDetails {

  public boolean isAdmin() {
    return admin;
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
    return emailVerified;
  }

  @JsonIgnore
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

}
