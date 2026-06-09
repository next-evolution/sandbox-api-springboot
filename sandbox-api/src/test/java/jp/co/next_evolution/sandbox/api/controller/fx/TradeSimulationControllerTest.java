package jp.co.next_evolution.sandbox.api.controller.fx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jp.co.next_evolution.sandbox.api.dto.request.fx.TradeSimulationRequest;
import jp.co.next_evolution.sandbox.api.dto.request.fx.TradeSimulationRequest.PositionParam;
import jp.co.next_evolution.sandbox.api.dto.response.fx.TradeSimulationResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.usecase.fx.trade.TradeSimulationUseCase;
import jp.co.next_evolution.sandbox.domain.model.fx.trade.TradePosition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TradeSimulationControllerTest {

  @Mock
  private TradeSimulationUseCase tradeSimulationUseCase;

  @InjectMocks
  private TradeSimulationController controller;

  @Test
  void simulationReturnsOk() {
    TradeSimulationRequest req = new TradeSimulationRequest();
    ReflectionTestUtils.setField(req, "entry", new TradeSimulationRequest.EntryParam());
    ReflectionTestUtils.setField(req, "positionList", new ArrayList<>());

    TradeSimulationUseCase.SimulationResult result =
        new TradeSimulationUseCase.SimulationResult(null, new ArrayList<>());
    given(tradeSimulationUseCase.execute(any())).willReturn(result);

    ResponseEntity<TradeSimulationResponse> response = controller.simulation(req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Ok);
  }

  @Test
  void simulationMapsPositionListToDomain() {
    PositionParam position = buildPositionParam(BigDecimal.valueOf(1.5), BigDecimal.valueOf(0.1));
    TradeSimulationRequest req = new TradeSimulationRequest();
    ReflectionTestUtils.setField(req, "entry", new TradeSimulationRequest.EntryParam());
    ReflectionTestUtils.setField(req, "positionList", List.of(position));

    TradeSimulationUseCase.SimulationResult result =
        new TradeSimulationUseCase.SimulationResult(null, new ArrayList<>());
    given(tradeSimulationUseCase.execute(any())).willReturn(result);

    ResponseEntity<TradeSimulationResponse> response = controller.simulation(req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Ok);
  }

  @Test
  void positionParamToDomainWithNonNullOptionals() {
    BigDecimal lot = BigDecimal.valueOf(0.1);
    BigDecimal settlementRatio = BigDecimal.valueOf(1.5);
    PositionParam param = buildPositionParam(lot, settlementRatio);

    TradePosition domain = param.toDomain();

    assertThat(domain.getLot()).isEqualByComparingTo(lot);
    assertThat(domain.getSettlementRatio()).isEqualByComparingTo(settlementRatio);
    assertThat(domain.getPositionNumber()).isEqualTo((short) 1);
    assertThat(domain.getSettlementPrice()).isEqualByComparingTo(BigDecimal.valueOf(149.500));
  }

  @Test
  void positionParamToDomainWithNullOptionals() {
    PositionParam param = buildPositionParam(null, null);

    TradePosition domain = param.toDomain();

    assertThat(domain.getLot()).isEqualByComparingTo(BigDecimal.ZERO);
    assertThat(domain.getSettlementRatio()).isEqualByComparingTo(BigDecimal.ZERO);
  }

  private PositionParam buildPositionParam(BigDecimal lot, BigDecimal settlementRatio) {
    PositionParam param = new PositionParam();
    ReflectionTestUtils.setField(param, "positionNumber", (short) 1);
    ReflectionTestUtils.setField(param, "settlementPrice", BigDecimal.valueOf(149.500));
    ReflectionTestUtils.setField(param, "lot", lot);
    ReflectionTestUtils.setField(param, "settlementRatio", settlementRatio);
    return param;
  }

}
