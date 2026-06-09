package jp.co.next_evolution.sandbox.domain.model.fx.zigzag;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ZigZagBarDataRow {

  private LocalDateTime barDateTime;
  private BigDecimal openPrice;
  private BigDecimal highPrice;
  private BigDecimal lowPrice;
  private BigDecimal closePrice;
  private BigDecimal sma200;
  private BigDecimal sma75;
  private BigDecimal sma20;

}
