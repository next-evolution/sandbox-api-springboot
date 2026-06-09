package jp.co.next_evolution.sandbox.domain.model.fx.trade;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PriceInfo {

  private final String symbol;
  private final LocalDateTime barDateTime;
  private final BigDecimal price;
  private final BigDecimal priceUsdJpy;

}