package jp.co.next_evolution.sandbox.application.dto.fx;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class ZigZagSearchItem {

  private String symbol;
  private int depth;
  private InfoSmaFibonacci target4h;
  private InfoSmaFibonacci current;
  private Info previous;
  private Info next;
  private Info next2;
  private BigDecimal nextRsRate;
  private BigDecimal next2MaxRate;
  private BigDecimal waveDxy4h;
  private BigDecimal waveDxy1h;
  private List<FractalWave> fractalWaveList;

  @Data
  public static class Info {
    private LocalDateTime waveStart;
    private LocalDateTime waveEnd;
    private int wave;
    private BigDecimal resistance;
    private BigDecimal support;
  }

  @Data
  public static class InfoSmaFibonacci {
    private LocalDateTime waveStart;
    private LocalDateTime waveEnd;
    private int wave;
    private BigDecimal resistance;
    private BigDecimal support;
    private Fibonacci fibonacci;
    private Sma sma4h200s;
    private Sma sma4h75s;
    private Sma sma4h20s;
    private Sma sma1h200s;
    private Sma sma15m200s;
  }

  @Data
  public static class Fibonacci {
    private BigDecimal f1;
    private BigDecimal f7;
    private BigDecimal f6;
    private BigDecimal f5;
    private BigDecimal f3;
    private BigDecimal f2;
    private BigDecimal f0;
    private BigDecimal priceRange;
  }

  @Data
  public static class Sma {
    private BigDecimal priceS;
    private BigDecimal priceE;
    private BigDecimal deviation;
    private BigDecimal fibonacci;
    private int direction;
    private int position;
  }

  @Data
  public static class FractalWave {
    private LocalDateTime waveStart;
    private int wave;
  }

}
