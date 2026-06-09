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
public class BarLoadSma {

  private String symbol;

  private LocalDateTime barDateTime;

  private int smaRange;

  private BigDecimal smaPrice;

  private boolean smaCross;

  public static BarLoadSma of(
      String symbol, LocalDateTime barDateTime,
      int smaRange, BigDecimal smaPrice,
      BigDecimal highPrice, BigDecimal lowPrice) {

    boolean smaCross = smaPrice != null
        && highPrice != null
        && lowPrice != null
        && highPrice.compareTo(smaPrice) >= 0
        && lowPrice.compareTo(smaPrice) <= 0;

    return BarLoadSma.builder()
        .symbol(symbol)
        .barDateTime(barDateTime)
        .smaRange(smaRange)
        .smaPrice(smaPrice)
        .smaCross(smaCross)
        .build();
  }

}
