package jp.co.next_evolution.sandbox.application.command.fx;

import java.math.BigDecimal;
import java.util.List;
import jp.co.next_evolution.sandbox.domain.model.fx.trade.TradeEntry;
import jp.co.next_evolution.sandbox.domain.model.fx.trade.TradePosition;

public record TradeSimulationCommand(
    BigDecimal riskAmount,
    BigDecimal firstLotRatio,
    TradeEntry entry,
    List<TradePosition> positionList
) {}
