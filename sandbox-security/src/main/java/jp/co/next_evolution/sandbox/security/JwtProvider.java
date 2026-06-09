package jp.co.next_evolution.sandbox.security;

import java.util.List;
import jp.co.next_evolution.sandbox.domain.exception.AuthenticationException;
import jp.co.next_evolution.sandbox.domain.model.auth.AuthUser;
import lombok.Getter;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

/**
 * JWKS エンドポイントから公開鍵を取得して RS256 検証.
 * JWT_SECRET 不要。audience / issuer も自動検証.
 */
@Component
public class JwtProvider {

  @Getter
  private final JwtDecoder jwtDecoder;

  public JwtProvider(JwtConfig jwtConfig) {

    // 1. JWKS URI から公開鍵を自動取得するデコーダを構築
    NimbusJwtDecoder decoder =
        JwtDecoders.fromIssuerLocation(jwtConfig.getAllowedIssList().getFirst());

    // 2. audience バリデーター（カスタム実装）
    OAuth2TokenValidator<Jwt> audienceValidator = jwt -> {
      List<String> audiences = jwt.getAudience();
      for (String audience : audiences) {
        if (jwtConfig.getAllowedAudienceList().contains(audience)) {
          return OAuth2TokenValidatorResult.success();
        }
      }
      return OAuth2TokenValidatorResult.failure(
          new OAuth2Error("invalid_token",
                          "JWT audience invalid. expected: " + audiences, null)
      );
    };

    OAuth2TokenValidator<Jwt> validators =
        new DelegatingOAuth2TokenValidator<>(
            new JwtIssuerValidator(jwtConfig.getAllowedIssList().getFirst()),
            new JwtTimestampValidator(),
            audienceValidator
        );

    decoder.setJwtValidator(validators);

    this.jwtDecoder = decoder;
  }

  /**
   * token 文字列 → AuthUser ValueObject 署名 / 期限 / issuer / audience をすべて検証する.
   */
  public AuthUser parse(String token) {
    Jwt jwt;
    try {
      jwt = jwtDecoder.decode(token);
    } catch (JwtValidationException e) {
      // audience / issuer / exp などバリデーション失敗
      throw new AuthenticationException("JWT validation failed: "
                                        + e.getErrors().iterator().next().getDescription());
    } catch (JwtException e) {
      // 署名不正・形式不正
      throw new AuthenticationException("JWT invalid: " + e.getMessage());
    }

    String sub = jwt.getSubject();
    String email = jwt.getClaimAsString("email");
    Boolean emailVerified = jwt.getClaimAsBoolean("email_verified");

    if (sub == null || email == null || emailVerified == null) {
      throw new AuthenticationException("Required JWT claims missing");
    }

    return new AuthUser(sub, email, emailVerified, false);
  }

}
