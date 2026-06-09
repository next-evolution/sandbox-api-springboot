package jp.co.next_evolution.sandbox.domain.repository.fx;

import java.time.LocalDateTime;
import jp.co.next_evolution.sandbox.domain.model.fx.trade.PriceInfo;

public interface TradeSimulationRepository {

  PriceInfo getPrice(String symbol, LocalDateTime contractAt);

}