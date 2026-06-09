package jp.co.next_evolution.sandbox.application.usecase.fx.trade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jp.co.next_evolution.sandbox.application.command.fx.TradeSimulationCommand;
import jp.co.next_evolution.sandbox.domain.model.fx.trade.PriceInfo;
import jp.co.next_evolution.sandbox.domain.model.fx.trade.TradeEntry;
import jp.co.next_evolution.sandbox.domain.model.fx.trade.TradePosition;
import jp.co.next_evolution.sandbox.domain.repository.fx.TradeSimulationRepository;
import jp.co.next_evolution.sandbox.domain.service.fx.FxTradeCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeSimulationUseCase {

  private final TradeSimulationRepository tradeSimulationRepository;
  private final FxTradeCalculator fxTradeCalculator;

  public SimulationResult execute(TradeSimulationCommand cmd) {

    TradeEntry entry = cmd.entry();
    List<TradePosition> positionList = cmd.positionList();

    PriceInfo priceInfo = tradeSimulationRepository.getPrice(
        entry.getSymbol(), entry.getContractAt());

    List<TradePosition> filteredPositions = initialize(entry, positionList, priceInfo);

    fxTradeCalculator.calculate(entry, filteredPositions, cmd.riskAmount(), cmd.firstLotRatio());

    return new SimulationResult(entry, filteredPositions);

  }

  public record SimulationResult(TradeEntry entry, List<TradePosition> positionList) {}

  private List<TradePosition> initialize(
      TradeEntry entry,
      List<TradePosition> positionList,
      PriceInfo priceInfo) {

    entry.applyPrice(priceInfo);

    List<TradePosition> filteredPositions = new ArrayList<>();
    if (BigDecimal.ZERO.compareTo(positionList.getFirst().getSettlementPrice()) == 0) {
      positionList.getFirst().setSettlementPrice(
          entry.computeDefaultSettlementPrice(BigDecimal.valueOf(0.6)));
      if (positionList.size() > 1) {
        positionList.get(1).setSettlementPrice(
            entry.computeDefaultSettlementPrice(BigDecimal.valueOf(0.9)));
        if (positionList.size() == 3) {
          positionList.get(2).setSettlementPrice(
              entry.computeDefaultSettlementPrice(BigDecimal.valueOf(1.2)));
        }
      }
      filteredPositions = new ArrayList<>(positionList);
    } else {
      short positionNumber = 1;
      for (TradePosition position : positionList) {
        if (BigDecimal.ZERO.compareTo(position.getSettlementPrice()) < 0) {
          position.setPositionNumber(positionNumber);
          filteredPositions.add(position);
          positionNumber++;
        }
      }
    }

    entry.applyDefaultLossPrice();

    return filteredPositions;

  }

}
