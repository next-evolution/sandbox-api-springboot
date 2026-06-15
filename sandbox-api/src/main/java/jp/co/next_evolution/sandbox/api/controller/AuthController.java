package jp.co.next_evolution.sandbox.api.controller;

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
import lombok.RequiredArgsConstructor;
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

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
      @AuthenticationPrincipal AuthUser authUser,   // JwtAuthFilterがセット済み
      @RequestBody LoginRequest request
  ) {
    var cmd = new LoginCommand(authUser, request.getEmail());
    UserDto userDto = loginUseCase.execute(cmd);

    return ResponseEntity.ok(LoginResponse.builder()
                                          .returnCode(userDto != null
                                                      ? ReturnCode.Ok
                                                      : ReturnCode.Warn)
                                          .user(userDto)
                                          .build());
  }

  @PostMapping("/logout-api")
  public ResponseEntity<ApiResponse> logout(@RequestBody LogoutRequest requestBody,
                                            @AuthenticationPrincipal AuthUser authUser,
                                            HttpSession httpSession) {

    logoutUseCase.execute(new LogoutCommand(authUser, requestBody.getUserId()));
    httpSession.invalidate();

    return ResponseEntity.ok(ApiResponse.builder().returnCode(ReturnCode.Ok).build());

  }

}
