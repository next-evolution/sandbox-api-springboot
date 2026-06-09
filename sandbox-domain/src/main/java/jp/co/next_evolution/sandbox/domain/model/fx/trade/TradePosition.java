package jp.co.next_evolution.sandbox.domain.model.fx.trade;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradePosition {

  private Long id;
  private short positionNumber;
  private BigDecimal settlementPrice;
  private int settlementPips;
  private BigDecimal settlementRatio;
  private BigDecimal lot;
  private int profitAmount;
  private int lossAmount;

}