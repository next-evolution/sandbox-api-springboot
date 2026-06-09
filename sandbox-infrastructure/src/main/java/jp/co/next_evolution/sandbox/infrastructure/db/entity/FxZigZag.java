package jp.co.next_evolution.sandbox.infrastructure.db.entity;

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
public class FxZigZag {

  private String symbol;
  private int depth;
  private LocalDateTime barDateTime;

  private BigDecimal resistance;
  private BigDecimal resistanceFractal;
  private BigDecimal support;
  private BigDecimal supportFractal;
  private BigDecimal priceHigh;
  private BigDecimal priceLow;
  private BigDecimal backStepHigh;
  private BigDecimal backStepLow;

  private BigDecimal fractalHigh;
  private BigDecimal fractalLow;

  private LocalDateTime resistanceBarDateTime;
  private LocalDateTime resistanceFractalBarDateTime;
  private LocalDateTime supportBarDateTime;
  private LocalDateTime supportFractalBarDateTime;
  private LocalDateTime priceHighBarDateTime;
  private LocalDateTime priceLowBarDateTime;
  private LocalDateTime backStepHighBarDateTime;
  private LocalDateTime backStepLowBarDateTime;

  private int wave;
  private boolean upTrend;
  private boolean breakResistance;
  private boolean breakSupport;
  private int backStepUp;
  private int backStepDown;

  // previousList / targetList クエリでJOINにより取得するフィールド
  private BigDecimal barHighPrice;
  private BigDecimal barLowPrice;
  private BigDecimal barClosePrice;
  private boolean existsZigzag;

}
