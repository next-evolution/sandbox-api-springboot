package jp.co.next_evolution.sandbox.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class ResponseTimeFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain
  ) throws ServletException, IOException {
    long start = System.currentTimeMillis();
    try {
      chain.doFilter(request, response);
    } finally {
      log.info("result:{}|{}|{}|{}:{}",
               response.getStatus(),
               System.currentTimeMillis() - start,
               request.getRemoteAddr(),
               request.getMethod(),
               request.getServletPath());
    }
  }

}
