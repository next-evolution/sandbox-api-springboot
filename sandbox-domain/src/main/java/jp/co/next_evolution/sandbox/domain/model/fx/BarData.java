package jp.co.next_evolution.sandbox.domain.model.fx;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarData {

  private String symbol;

  private LocalDateTime barDateTime;

  private BigDecimal openPrice;

  private BigDecimal highPrice;

  private BigDecimal lowPrice;

  private BigDecimal closePrice;

  private int volume;

  private BigDecimal highProfit;

  private BigDecimal lowProfit;

  private BigDecimal closeProfit;

  private BigDecimal rangeProfit;

  private BigDecimal rsiValue;

  private BigDecimal rsiMa;

}
