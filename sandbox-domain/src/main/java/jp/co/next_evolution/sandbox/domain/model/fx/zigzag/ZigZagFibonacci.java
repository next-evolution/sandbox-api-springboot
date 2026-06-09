package jp.co.next_evolution.sandbox.domain.model.fx.zigzag;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ZigZagFibonacci {

  public static final BigDecimal F7 = BigDecimal.valueOf(0.786);
  public static final BigDecimal F6 = BigDecimal.valueOf(0.618);
  public static final BigDecimal F5 = BigDecimal.valueOf(0.5);
  public static final BigDecimal F3 = BigDecimal.valueOf(0.382);
  public static final BigDecimal F2 = BigDecimal.valueOf(0.236);

  private BigDecimal f1;
  private BigDecimal f7;
  private BigDecimal f6;
  private BigDecimal f5;
  private BigDecimal f3;
  private BigDecimal f2;
  private BigDecimal f0;
  private BigDecimal priceRange;

}
