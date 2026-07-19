package jp.co.next_evolution.sandbox.security.config;

import jp.co.next_evolution.sandbox.security.JwtConfig;
import jp.co.next_evolution.sandbox.security.filter.JwtAuthFilter;
import jp.co.next_evolution.sandbox.security.filter.ResponseTimeFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(JwtConfig.class)
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;
  private final ResponseTimeFilter responseTimeFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/v3/api-docs/**", "/v3/api-docs.yaml", "/swagger-ui/**",
                             "/v1/fx/master-list/**", "/v1/auth/login")
            .permitAll()
            // 未承認（ROLE_MEMBER無し）でもJWTが有効なら到達させる
            // POST: 登録処理自体を実行させる / GET: profile()内でWarn（利用承認待ち）を返させる
            .requestMatchers(HttpMethod.POST, "/v1/user")
            .permitAll()
            .requestMatchers(HttpMethod.GET, "/v1/user")
            .permitAll()
            .requestMatchers("/**").hasRole("MEMBER"))
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(responseTimeFilter, JwtAuthFilter.class);

    return http.build();
  }

}