package jp.co.next_evolution.sandbox.security.config;

import jp.co.next_evolution.sandbox.security.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // interceptorで認証制御するためpermitAll()
        // .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .authorizeHttpRequests(auth -> auth.requestMatchers("/v3/api-docs/**",
                                                            "/v3/api-docs.yaml",
                                                            "/swagger-ui/**",
                                                            "/v1/debug",
                                                            "/v1/guest/**",
                                                            "/v1/logout-api/*")
                                           .permitAll()
                                           .requestMatchers("/**")
                                           .authenticated())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

}