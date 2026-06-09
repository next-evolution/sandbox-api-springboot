package jp.co.next_evolution.sandbox.domain.model.fx.zigzag;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

// ZigZag検索クエリの生DBマッピング結果。Fibonacci・SMA計算値はアプリ層で付与する。
@Data
@NoArgsConstructor
public class ZigZagSearchRow {

  private String symbol;
  private int depth;

  private WaveWithSma target4h;
  private WaveWithSma current;
  private WaveInfo previous;
  private WaveInfo next;
  private WaveInfo next2;

  private BigDecimal waveDxy4h;
  private BigDecimal waveDxy1h;

  private List<FractalWaveInfo> fractalWaveList;

  @Data
  @NoArgsConstructor
  public static class WaveInfo {
    private LocalDateTime waveStart;
    private LocalDateTime waveEnd;
    private int wave;
    private BigDecimal resistance;
    private BigDecimal support;
  }

  @Data
  @NoArgsConstructor
  public static class WaveWithSma {
    private LocalDateTime waveStart;
    private LocalDateTime waveEnd;
    private int wave;
    private BigDecimal resistance;
    private BigDecimal support;
    private SmaPrice sma4h200s;
    private SmaPrice sma4h75s;
    private SmaPrice sma4h20s;
    private SmaPrice sma1h200s;
    private SmaPrice sma15m200s;
  }

  @Data
  @NoArgsConstructor
  public static class SmaPrice {
    private BigDecimal priceS;
    private BigDecimal priceE;
  }

  @Data
  @NoArgsConstructor
  public static class FractalWaveInfo {
    private LocalDateTime waveStart;
    private int wave;
  }

}
