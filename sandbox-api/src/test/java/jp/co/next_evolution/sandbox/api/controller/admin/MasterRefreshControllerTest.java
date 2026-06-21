package jp.co.next_evolution.sandbox.api.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import jp.co.next_evolution.sandbox.api.dto.response.ApiResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.usecase.fx.MasterRefreshUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.MasterStatusUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class MasterRefreshControllerTest {

  @Mock
  private MasterRefreshUseCase masterRefreshUseCase;

  @Mock
  private MasterStatusUseCase masterStatusUseCase;

  @InjectMocks
  private MasterRefreshController controller;

  @Test
  void statusReturnsOk() {
    given(masterStatusUseCase.execute()).willReturn("status: ok");

    ResponseEntity<ApiResponse> response = controller.status();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Ok);
    assertThat(response.getBody().getMessage()).isEqualTo("status: ok");
  }

  @Test
  void refreshReturnsOk() {
    given(masterRefreshUseCase.execute()).willReturn("refresh done");

    ResponseEntity<ApiResponse> response = controller.refresh();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Ok);
    assertThat(response.getBody().getMessage()).isEqualTo("refresh done");
  }

}
