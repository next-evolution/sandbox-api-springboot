package jp.co.next_evolution.sandbox.api.dto.response.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jp.co.next_evolution.sandbox.api.dto.response.ApiResponse;
import jp.co.next_evolution.sandbox.domain.model.fx.BarType;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ZigZagBarDataResponse extends ApiResponse {

  @Schema(requiredMode = REQUIRED, implementation = BarType.class)
  private BarType barType;

  @Schema(requiredMode = REQUIRED)
  private String symbol;

  @Schema(requiredMode = REQUIRED)
  private short depth;

  @Schema(requiredMode = REQUIRED)
  private int wave;

  private List<ZigZagBarData> zigZagBarDataList;

  @Data
  public static class ZigZagBarData {
    @Schema(requiredMode = REQUIRED, example = "2026-01-02T11:22:33+09:00")
    private LocalDateTime barDateTime;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal openPrice;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal highPrice;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal lowPrice;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal closePrice;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal sma200;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal sma75;
    @Schema(requiredMode = REQUIRED)
    private BigDecimal sma20;
  }

}
