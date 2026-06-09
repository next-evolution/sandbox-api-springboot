package jp.co.next_evolution.sandbox.api.controller.fx;

import java.util.List;
import java.util.stream.Collectors;
import jp.co.next_evolution.sandbox.api.dto.request.fx.TradeSimulationRequest;
import jp.co.next_evolution.sandbox.api.dto.response.fx.TradeSimulationResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.command.fx.TradeSimulationCommand;
import jp.co.next_evolution.sandbox.application.usecase.fx.trade.TradeSimulationUseCase;
import jp.co.next_evolution.sandbox.domain.model.fx.trade.TradeEntry;
import jp.co.next_evolution.sandbox.domain.model.fx.trade.TradePosition;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/fx/trade/simulation")
@RequiredArgsConstructor
public class TradeSimulationController {

  private final TradeSimulationUseCase tradeSimulationUseCase;

  @PostMapping
  public ResponseEntity<TradeSimulationResponse> simulation(
      @RequestBody @Validated TradeSimulationRequest req) {

    TradeEntry entry = req.getEntry().toDomain();
    List<TradePosition> positionList = req.getPositionList().stream()
        .map(TradeSimulationRequest.PositionParam::toDomain)
        .collect(Collectors.toList());

    TradeSimulationUseCase.SimulationResult result = tradeSimulationUseCase.execute(
        new TradeSimulationCommand(
            req.getRiskAmount(),
            req.getFirstLotRatio(),
            entry,
            positionList
        )
    );

    return ResponseEntity.ok(TradeSimulationResponse.builder()
                                                    .returnCode(ReturnCode.Ok)
                                                    .entry(result.entry())
                                                    .positionList(result.positionList())
                                                    .build());

  }

}
