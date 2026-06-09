package jp.co.next_evolution.sandbox.security.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jp.co.next_evolution.sandbox.domain.model.auth.AuthUser;
import jp.co.next_evolution.sandbox.security.annotation.PublicApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

  private static final String PROCESS_START_TIME = "processTimeMillis";

  @Override
  public boolean preHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler
  ) throws Exception {

    // HandlerMethod 以外（静的リソース等）はスルー
    if (!(handler instanceof HandlerMethod method)) {
      return true;
    }

    request.setAttribute(PROCESS_START_TIME, System.currentTimeMillis());

    if (request.getServletPath().startsWith("/v3/api-docs")
        || request.getServletPath().startsWith("/swagger-ui/")) {
      return true;
    }

    // @PublicApi が付与されていれば認証スキップ
    if (AnnotationUtils.findAnnotation(method.getMethod(), PublicApi.class) != null) {
      return true;
    }

    // JwtAuthFilter がセットした AuthUser を取り出す
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof AuthUser authUser)) {
      log.error("{} -> {}", request.getServletPath(), "Unauthorized");
      response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
      return false;
    }

    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request,
                              HttpServletResponse response,
                              Object handler,
                              Exception ex) {
    if (!ObjectUtils.isEmpty(request.getAttribute(PROCESS_START_TIME))) {
      log.info("result:{}|{}|{}|{}:{}",
               response.getStatus(),
               System.currentTimeMillis() - (Long) request.getAttribute(PROCESS_START_TIME),
               request.getRemoteAddr(),
               request.getMethod(),
               request.getServletPath());
    }
  }

}
