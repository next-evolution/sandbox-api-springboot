package jp.co.next_evolution.sandbox.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jp.co.next_evolution.sandbox.api.dto.request.auth.LoginRequest;
import jp.co.next_evolution.sandbox.api.dto.request.auth.LogoutRequest;
import jp.co.next_evolution.sandbox.api.dto.response.ApiResponse;
import jp.co.next_evolution.sandbox.api.dto.response.auth.LoginResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.command.user.LoginCommand;
import jp.co.next_evolution.sandbox.application.command.user.LogoutCommand;
import jp.co.next_evolution.sandbox.application.dto.user.UserDto;
import jp.co.next_evolution.sandbox.application.usecase.user.LoginUseCase;
import jp.co.next_evolution.sandbox.application.usecase.user.LogoutUseCase;
import jp.co.next_evolution.sandbox.domain.model.auth.AuthUser;
import jp.co.next_evolution.sandbox.security.BearerTokenResolver;
import jp.co.next_evolution.sandbox.security.JwtCookieProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final LoginUseCase loginUseCase;

  private final LogoutUseCase logoutUseCase;

  private final JwtCookieProvider jwtCookieProvider;

  /**
   * React（Web）向けログイン. リクエストは{@code Authorization: Bearer}で受け取り
   * （ログイン成立前はCookieが無いため）、成功時はJWTをHttpOnly CookieとしてSet-Cookieする.
   */
  @PostMapping("/login/web")
  public ResponseEntity<LoginResponse> loginWeb(
      @AuthenticationPrincipal AuthUser authUser,   // JwtAuthFilterがセット済み
      @RequestBody LoginRequest request,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse
  ) {
    LoginResponse body = executeLogin(authUser, request);

    String jwt = BearerTokenResolver.resolve(httpRequest);
    if (jwt != null) {
      httpResponse.addHeader(HttpHeaders.SET_COOKIE,
                             jwtCookieProvider.buildLoginCookie(jwt).toString());
    }

    return ResponseEntity.ok(body);
  }

  /**
   * Flutter（App）向けログイン. 従来どおりBearer方式のみ・Cookie発行は行わない.
   */
  @PostMapping("/login/app")
  public ResponseEntity<LoginResponse> loginApp(
      @AuthenticationPrincipal AuthUser authUser,   // JwtAuthFilterがセット済み
      @RequestBody LoginRequest request
  ) {
    return ResponseEntity.ok(executeLogin(authUser, request));
  }

  private LoginResponse executeLogin(AuthUser authUser, LoginRequest request) {
    var cmd = new LoginCommand(authUser, request.getEmail());
    UserDto userDto = loginUseCase.execute(cmd);

    return LoginResponse.builder()
                        .returnCode(userDto != null ? ReturnCode.Ok : ReturnCode.Warn)
                        .user(userDto)
                        .build();
  }

  @PostMapping("/logout-api")
  public ResponseEntity<ApiResponse> logout(@RequestBody LogoutRequest requestBody,
                                            @AuthenticationPrincipal AuthUser authUser,
                                            HttpSession httpSession,
                                            HttpServletResponse httpResponse) {

    logoutUseCase.execute(new LogoutCommand(authUser, requestBody.getUserId()));
    httpSession.invalidate();
    // React（Web）向けCookieを失効させる。Flutter（App）はCookie未使用のため無害
    httpResponse.addHeader(HttpHeaders.SET_COOKIE,
                           jwtCookieProvider.buildLogoutCookie().toString());

    return ResponseEntity.ok(ApiResponse.builder().returnCode(ReturnCode.Ok).build());

  }

}
