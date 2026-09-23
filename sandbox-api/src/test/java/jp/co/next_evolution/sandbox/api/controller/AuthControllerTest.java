package jp.co.next_evolution.sandbox.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jp.co.next_evolution.sandbox.api.dto.request.auth.LoginRequest;
import jp.co.next_evolution.sandbox.api.dto.request.auth.LogoutRequest;
import jp.co.next_evolution.sandbox.api.dto.response.ApiResponse;
import jp.co.next_evolution.sandbox.api.dto.response.auth.LoginResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.dto.user.UserDto;
import jp.co.next_evolution.sandbox.application.usecase.user.LoginUseCase;
import jp.co.next_evolution.sandbox.application.usecase.user.LogoutUseCase;
import jp.co.next_evolution.sandbox.domain.model.auth.AuthUser;
import jp.co.next_evolution.sandbox.security.JwtCookieProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  @Mock
  private LoginUseCase loginUseCase;

  @Mock
  private LogoutUseCase logoutUseCase;

  @Mock
  private JwtCookieProvider jwtCookieProvider;

  @Mock
  private HttpSession httpSession;

  @Mock
  private HttpServletRequest httpServletRequest;

  @Mock
  private HttpServletResponse httpServletResponse;

  @InjectMocks
  private AuthController controller;

  @Test
  void loginAppReturnsOkWhenUserExists() {
    AuthUser authUser = new AuthUser("sub-123", "test@example.com", true, false, true);
    LoginRequest req = new LoginRequest();
    UserDto userDto = new UserDto(1L, "sub-123", "test@example.com",
        "TestUser", true, null, false, false, null, null);
    given(loginUseCase.execute(any())).willReturn(userDto);

    ResponseEntity<LoginResponse> response = controller.loginApp(authUser, req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Ok);
    assertThat(response.getBody().getUser()).isEqualTo(userDto);
  }

  @Test
  void loginAppReturnsWarnWhenUserIsNull() {
    AuthUser authUser = new AuthUser("sub-123", "test@example.com", true, false, true);
    LoginRequest req = new LoginRequest();
    given(loginUseCase.execute(any())).willReturn(null);

    ResponseEntity<LoginResponse> response = controller.loginApp(authUser, req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Warn);
    assertThat(response.getBody().getUser()).isNull();
  }

  @Test
  void loginWebSetsJwtCookieWhenBearerTokenPresent() {
    AuthUser authUser = new AuthUser("sub-123", "test@example.com", true, false, true);
    LoginRequest req = new LoginRequest();
    UserDto userDto = new UserDto(1L, "sub-123", "test@example.com",
        "TestUser", true, null, false, false, null, null);
    given(loginUseCase.execute(any())).willReturn(userDto);
    given(httpServletRequest.getHeader("Authorization")).willReturn("Bearer jwt-token-value");
    ResponseCookie cookie = ResponseCookie.from(JwtCookieProvider.COOKIE_NAME, "jwt-token-value")
                                          .build();
    given(jwtCookieProvider.buildLoginCookie("jwt-token-value")).willReturn(cookie);

    ResponseEntity<LoginResponse> response =
        controller.loginWeb(authUser, req, httpServletRequest, httpServletResponse);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Ok);
    verify(httpServletResponse).addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  @Test
  void logoutReturnsOkAndClearsJwtCookie() {
    LogoutRequest req = new LogoutRequest();
    AuthUser authUser = new AuthUser("sub-123", "test@example.com", true, false, true);
    ResponseCookie cookie =
        ResponseCookie.from(JwtCookieProvider.COOKIE_NAME, "").maxAge(0).build();
    given(jwtCookieProvider.buildLogoutCookie()).willReturn(cookie);

    ResponseEntity<ApiResponse> response =
        controller.logout(req, authUser, httpSession, httpServletResponse);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Ok);
    verify(httpServletResponse).addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

}
