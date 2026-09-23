package jp.co.next_evolution.sandbox.security.config;

import java.util.Arrays;
import jp.co.next_evolution.sandbox.security.JwtConfig;
import jp.co.next_evolution.sandbox.security.filter.CsrfCookieFilter;
import jp.co.next_evolution.sandbox.security.filter.JwtAuthFilter;
import jp.co.next_evolution.sandbox.security.filter.ResponseTimeFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(JwtConfig.class)
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;
  private final ResponseTimeFilter responseTimeFilter;
  private final CsrfCookieFilter csrfCookieFilter;
  private final JwtConfig jwtConfig;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // CookieCsrfTokenRepositoryはXOR化しない生トークンをCookieに書き出すため、
    // リクエスト側の属性解決もデフォルトのXorCsrfTokenRequestAttributeHandlerではなく
    // こちらに合わせる（Spring Security公式のSPA連携手順）
    CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
    requestHandler.setCsrfRequestAttributeName(null);

    // デフォルトはCookieのPathをservlet context-path（/api）に合わせるため、
    // 「/」配下のSPA画面からdocument.cookie経由で読めない。JwtCookieProviderと
    // 同じくPath=/・Secure・SameSite=Noneを明示し、別オリジンでも読み取り・送信できるようにする
    CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
    csrfTokenRepository.setCookiePath("/");
    csrfTokenRepository.setCookieCustomizer(builder -> builder.secure(true).sameSite("None"));

    http.cors(Customizer.withDefaults())
        .csrf(csrf -> csrf
            .csrfTokenRepository(csrfTokenRepository)
            .csrfTokenRequestHandler(requestHandler)
            // Bearer方式（Flutter）はCSRF検証をスキップする
            .ignoringRequestMatchers(jwtAuthFilter::isBearerRequest))
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/v3/api-docs/**", "/v3/api-docs.yaml", "/swagger-ui/**",
                             "/v1/fx/master-list/**",
                             "/v1/auth/login/web", "/v1/auth/login/app")
            .permitAll()
            // 未承認（ROLE_MEMBER無し）でもJWTが有効なら到達させる
            // POST: 登録処理自体を実行させる / GET: profile()内でWarn（利用承認待ち）を返させる
            .requestMatchers(HttpMethod.POST, "/v1/user")
            .permitAll()
            .requestMatchers(HttpMethod.GET, "/v1/user")
            .permitAll()
            .requestMatchers("/**").hasRole("MEMBER"))
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(responseTimeFilter, JwtAuthFilter.class)
        .addFilterAfter(csrfCookieFilter, JwtAuthFilter.class);

    return http.build();
  }

  /**
   * React（別オリジン配信）からのCookie送受信を許可するためのCORS設定.
   * {@code jwt.allowedOriginList}（環境変数 CORS_ORIGIN1/2）で許可オリジンを明示する
   * （Cookie利用時、Originのワイルドカード指定はブラウザ仕様上使えない）.
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(jwtConfig.getAllowedOrigins()));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-XSRF-TOKEN"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

}
