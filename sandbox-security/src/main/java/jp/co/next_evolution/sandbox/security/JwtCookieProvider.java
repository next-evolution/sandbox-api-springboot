package jp.co.next_evolution.sandbox.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/**
 * React（Web）向けに、CognitoのJWTをそのままHttpOnly CookieとしてSet-Cookieする.
 * トークン自体の検証は{@link JwtProvider}が行うため、ここではCookieの発行・失効のみを担う.
 * React/API は別オリジン配信のため、{@code SameSite=None; Secure}が必須.
 */
@Component
public class JwtCookieProvider {

  public static final String COOKIE_NAME = "sandbox_jwt";

  @Value("${sandbox.session-ttl}")
  private long sessionTtl;

  public ResponseCookie buildLoginCookie(String jwt) {
    return ResponseCookie.from(COOKIE_NAME, jwt)
        .httpOnly(true)
        .secure(true)
        .sameSite("None")
        .path("/")
        .maxAge(sessionTtl)
        .build();
  }

  public ResponseCookie buildLogoutCookie() {
    return ResponseCookie.from(COOKIE_NAME, "")
        .httpOnly(true)
        .secure(true)
        .sameSite("None")
        .path("/")
        .maxAge(0)
        .build();
  }

}
