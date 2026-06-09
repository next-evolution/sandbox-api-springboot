package jp.co.next_evolution.sandbox.domain.model.fx.zigzag;

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
public class ZigZag {

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
  private boolean breakResistance;
  private boolean breakSupport;
  private int waveFractal;
  private boolean breakResistanceFractal;
  private boolean breakSupportFractal;
  private boolean upTrend;
  private int backStepUp;
  private int backStepDown;

  // DB非保存 — クエリ時にJOINで取得するバーデータ
  private BigDecimal barHighPrice;
  private BigDecimal barLowPrice;
  private BigDecimal barClosePrice;
  private boolean existsZigzag;

}
