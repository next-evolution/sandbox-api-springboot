package jp.co.next_evolution.sandbox.api.dto.response.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jp.co.next_evolution.sandbox.domain.model.fx.BarType;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;
import lombok.Data;

@Data
public class ZigZagStatusItem {

  @Schema(requiredMode = REQUIRED)
  private SymbolType symbolType;

  @Schema(requiredMode = REQUIRED)
  private BarType barType;

  @Schema(requiredMode = REQUIRED)
  private String symbol;

  @Schema(requiredMode = REQUIRED)
  private short depth;

  @Schema(requiredMode = REQUIRED)
  private String barDateTimeMin;

  @Schema(requiredMode = REQUIRED)
  private String barDateTimeMax;

  @Schema(requiredMode = REQUIRED)
  private int barCount;

  @Schema(requiredMode = REQUIRED)
  private String barDateTimeMinZigZag;

  @Schema(requiredMode = REQUIRED)
  private String barDateTimeMaxZigZag;

  @Schema(requiredMode = REQUIRED)
  private int zigzagCount;

  @Schema(requiredMode = REQUIRED)
  private int breakResistanceCount;

  @Schema(requiredMode = REQUIRED)
  private int breakSupportCount;

  private String message;

}
