package jp.co.next_evolution.sandbox.api.dto.response.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class ZigZagResult {

  @Schema(requiredMode = REQUIRED)
  private String symbol;

  @Schema(requiredMode = REQUIRED)
  private int depth;

  @Schema(requiredMode = REQUIRED)
  private InfoSmaFibonacci target4h;

  @Schema(requiredMode = REQUIRED)
  private InfoSmaFibonacci current;

  @Schema(requiredMode = REQUIRED)
  private Info previous;

  @Schema(requiredMode = REQUIRED)
  private Info next;

  @Schema(requiredMode = REQUIRED)
  private Info next2;

  @Schema(requiredMode = REQUIRED)
  private BigDecimal nextRsRate;

  @Schema(requiredMode = REQUIRED)
  private BigDecimal next2MaxRate;

  @Schema(requiredMode = REQUIRED)
  private BigDecimal waveDxy4h;

  @Schema(requiredMode = REQUIRED)
  private BigDecimal waveDxy1h;

  @Schema(requiredMode = REQUIRED)
  private List<FractalWave> fractalWaveList;

  @Data
  public static class Info {
    @Schema(requiredMode = REQUIRED, example = "2026-01-23T12:34:56+09:00")
    private LocalDateTime waveStart;
    @Schema(requiredMode = REQUIRED, example = "2026-01-23T12:34:56+09:00")
    private LocalDateTime waveEnd;
    @Schema(requiredMode = REQUIRED)
    private int wave;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal resistance;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal support;
  }

  @Data
  public static class InfoSmaFibonacci {
    @Schema(requiredMode = REQUIRED, example = "2026-01-23T12:34:56+09:00")
    private LocalDateTime waveStart;
    @Schema(requiredMode = REQUIRED, example = "2026-01-23T12:34:56+09:00")
    private LocalDateTime waveEnd;
    @Schema(requiredMode = REQUIRED)
    private int wave;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal resistance;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal support;
    @Schema(requiredMode = REQUIRED)
    private Fibonacci fibonacci;
    @Schema(requiredMode = REQUIRED)
    private Sma sma4h200s;
    @Schema(requiredMode = REQUIRED)
    private Sma sma4h75s;
    @Schema(requiredMode = REQUIRED)
    private Sma sma4h20s;
    @Schema(requiredMode = REQUIRED)
    private Sma sma1h200s;
    @Schema(requiredMode = REQUIRED)
    private Sma sma15m200s;
  }

  @Data
  public static class Sma {
    @Schema(requiredMode = REQUIRED)
    private BigDecimal priceS;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal priceE;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal deviation;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal fibonacci;
    @Schema(requiredMode = REQUIRED)
    private int direction;
    @Schema(requiredMode = REQUIRED)
    private int position;
  }

  @Data
  public static class Fibonacci {
    @Schema(requiredMode = REQUIRED)
    private BigDecimal f1;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal f7;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal f6;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal f5;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal f3;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal f2;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal f0;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal priceRange;
  }

  @Data
  public static class FractalWave {
    @Schema(requiredMode = REQUIRED, example = "2026-01-23T12:34:56+09:00")
    private LocalDateTime waveStart;
    @Schema(requiredMode = REQUIRED)
    private int wave;
  }

}
