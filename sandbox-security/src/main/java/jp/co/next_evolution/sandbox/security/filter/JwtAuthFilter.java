package jp.co.next_evolution.sandbox.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import jp.co.next_evolution.sandbox.domain.model.auth.AuthUser;
import jp.co.next_evolution.sandbox.domain.repository.auth.SessionRepository;
import jp.co.next_evolution.sandbox.domain.repository.user.UserRepository;
import jp.co.next_evolution.sandbox.security.BearerTokenResolver;
import jp.co.next_evolution.sandbox.security.JwtCookieProvider;
import jp.co.next_evolution.sandbox.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;
  private final SessionRepository sessionRepository;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain
  ) throws ServletException, IOException {

    String token = resolveToken(request);

    if (token != null) {
      try {
        // RS256検証 + audience/issuer/exp チェック → AuthUser 生成（admin=false）
        AuthUser jwtAuthUser = jwtProvider.parse(token);

        // Redis から admin フラグ付き AuthUser を取得
        Optional<AuthUser> sessionOpt = sessionRepository.findBySub(jwtAuthUser.sub());

        AuthUser authUser;
        if (sessionOpt.isPresent()) {
          authUser = sessionOpt.get();
          sessionRepository.update(authUser);
        } else {
          // セッションなし → sandbox_user から AuthUser を復元（silent login）
          authUser = userRepository.findByUserId(jwtAuthUser.sub())
              .map(user -> {
                AuthUser restored = new AuthUser(
                    jwtAuthUser.sub(), jwtAuthUser.email(), jwtAuthUser.emailVerified(),
                    user.isAdmin(), user.isApproved()
                );
                sessionRepository.save(restored);
                return restored;
              })
              // sandbox_user未登録（初回ログイン）でもJWT由来のauthUserにフォールバックする
              .orElse(jwtAuthUser);
        }

        if (authUser == null) {
          chain.doFilter(request, response);
          return;
        }

        var auth = new UsernamePasswordAuthenticationToken(
            authUser, null, authUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

      } catch (Exception e) {
        log.error("doFilterInternal[{}]{}", request.getServletPath(), e.getMessage());
        SecurityContextHolder.clearContext();
      }
    }

    chain.doFilter(request, response);
  }

  /**
   * トークン文字列を取り出す. Flutter（Bearer）を優先し、無ければReact向けCookieを見る.
   */
  private String resolveToken(HttpServletRequest request) {
    String bearerToken = BearerTokenResolver.resolve(request);
    if (bearerToken != null) {
      return bearerToken;
    }
    return resolveCookieToken(request);
  }

  private String resolveCookieToken(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return null;
    }
    for (Cookie cookie : cookies) {
      if (JwtCookieProvider.COOKIE_NAME.equals(cookie.getName())) {
        return cookie.getValue();
      }
    }
    return null;
  }

  /**
   * Bearerトークンによるリクエストか判定する. CSRF対象外判定（Flutter向け）にも使用する.
   */
  public boolean isBearerRequest(HttpServletRequest request) {
    return BearerTokenResolver.resolve(request) != null;
  }

}