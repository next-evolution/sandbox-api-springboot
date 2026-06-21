package jp.co.next_evolution.sandbox.domain.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthUser(
    String sub,
    String email,
    Boolean emailVerified,
    Boolean admin,
    Boolean approved
) implements UserDetails {

  public boolean isAdmin() {
    return Boolean.TRUE.equals(admin);
  }

  public boolean isApproved() {
    return Boolean.TRUE.equals(approved);
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
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    if (isApproved()) {
      authorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
    }
    if (isAdmin()) {
      authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
    return authorities;
  }

}
