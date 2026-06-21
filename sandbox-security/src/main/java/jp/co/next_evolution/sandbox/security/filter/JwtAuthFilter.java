package jp.co.next_evolution.sandbox.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import jp.co.next_evolution.sandbox.domain.model.auth.AuthUser;
import jp.co.next_evolution.sandbox.domain.repository.auth.SessionRepository;
import jp.co.next_evolution.sandbox.domain.repository.user.UserRepository;
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
              .orElse(null);
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
   * Authorization: Bearer token からトークン文字列を取り出す. ヘッダーなし / Bearer でない場合は null を返す.
   */
  private String resolveToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      return header.substring(7);
    }
    return null;
  }

}