package jp.co.next_evolution.sandbox.api.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import jp.co.next_evolution.sandbox.api.dto.response.ApiResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.usecase.fx.MasterRefreshUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.MasterStatusUseCase;
import jp.co.next_evolution.sandbox.domain.exception.ForbiddenException;
import jp.co.next_evolution.sandbox.domain.model.auth.AuthUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class MasterRefreshControllerTest {

  private static final AuthUser ADMIN_USER = new AuthUser("sub-123", "admin@example.com", true, true);
  private static final AuthUser NON_ADMIN_USER = new AuthUser("sub-456", "user@example.com", true, false);

  @Mock
  private MasterRefreshUseCase masterRefreshUseCase;

  @Mock
  private MasterStatusUseCase masterStatusUseCase;

  @InjectMocks
  private MasterRefreshController controller;

  @Test
  void statusReturnsOkWhenAdmin() {
    given(masterStatusUseCase.execute()).willReturn("status: ok");

    ResponseEntity<ApiResponse> response = controller.status(ADMIN_USER);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Ok);
    assertThat(response.getBody().getMessage()).isEqualTo("status: ok");
  }

  @Test
  void statusThrowsForbiddenWhenNotAdmin() {
    assertThatThrownBy(() -> controller.status(NON_ADMIN_USER))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("管理者用APIです");
  }

  @Test
  void refreshReturnsOkWhenAdmin() {
    given(masterRefreshUseCase.execute()).willReturn("refresh done");

    ResponseEntity<ApiResponse> response = controller.refresh(ADMIN_USER);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Ok);
    assertThat(response.getBody().getMessage()).isEqualTo("refresh done");
  }

  @Test
  void refreshThrowsForbiddenWhenNotAdmin() {
    assertThatThrownBy(() -> controller.refresh(NON_ADMIN_USER))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("管理者用APIです");
  }

}
