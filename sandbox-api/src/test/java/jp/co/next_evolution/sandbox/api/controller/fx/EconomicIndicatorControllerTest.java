package jp.co.next_evolution.sandbox.api.controller.fx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

import java.util.List;
import jp.co.next_evolution.sandbox.api.dto.request.fx.EconomicIndicatorRequest;
import jp.co.next_evolution.sandbox.api.dto.request.fx.EconomicIndicatorSearchRequest;
import jp.co.next_evolution.sandbox.api.dto.response.fx.EconomicIndicatorSearchResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.dto.fx.EconomicIndicatorDto;
import jp.co.next_evolution.sandbox.application.usecase.fx.economicindicator.AddEconomicIndicatorUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.economicindicator.GetEconomicIndicatorUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.economicindicator.SearchEconomicIndicatorUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.economicindicator.UpdateEconomicIndicatorUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class EconomicIndicatorControllerTest {

  @Mock
  private SearchEconomicIndicatorUseCase searchEconomicIndicatorUseCase;

  @Mock
  private GetEconomicIndicatorUseCase getEconomicIndicatorUseCase;

  @Mock
  private AddEconomicIndicatorUseCase addEconomicIndicatorUseCase;

  @Mock
  private UpdateEconomicIndicatorUseCase updateEconomicIndicatorUseCase;

  @InjectMocks
  private EconomicIndicatorController controller;

  @Test
  void searchReturnsOk() {
    SearchEconomicIndicatorUseCase.SearchResult result =
        new SearchEconomicIndicatorUseCase.SearchResult(0, List.of(), 1, 20);
    given(searchEconomicIndicatorUseCase.execute(
        anyInt(), anyInt(), any(), any(), any())).willReturn(result);

    EconomicIndicatorSearchRequest req = new EconomicIndicatorSearchRequest();

    ResponseEntity<EconomicIndicatorSearchResponse> response = controller.search(req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Ok);
  }

  @Test
  void getReturnsEconomicIndicatorDto() {
    EconomicIndicatorDto dto =
        new EconomicIndicatorDto("GDP_F_QOQ", "JP", "GDP", "H", null, null, null, null);
    given(getEconomicIndicatorUseCase.execute("JP", "GDP_F_QOQ")).willReturn(dto);

    ResponseEntity<EconomicIndicatorDto> response = controller.get("JP", "GDP_F_QOQ");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(dto);
  }

  @Test
  void addReturnsOk() {
    EconomicIndicatorRequest req = new EconomicIndicatorRequest();

    ResponseEntity<Void> response = controller.add(req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void updateReturnsOk() {
    EconomicIndicatorRequest req = new EconomicIndicatorRequest();

    ResponseEntity<Void> response = controller.update("JP", "GDP_F_QOQ", req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

}
