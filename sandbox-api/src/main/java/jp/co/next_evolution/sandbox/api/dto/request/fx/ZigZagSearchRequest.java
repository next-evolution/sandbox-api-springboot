package jp.co.next_evolution.sandbox.api.dto.request.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import jp.co.next_evolution.sandbox.api.dto.request.ApiSearchRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ZigZagSearchRequest extends ApiSearchRequest {

  @Schema(requiredMode = REQUIRED)
  @NotNull
  private String barType;

  @Schema(requiredMode = REQUIRED)
  @NotBlank
  private String symbol;

  @Schema(requiredMode = REQUIRED)
  @Positive
  private short depth;

  @Schema(requiredMode = REQUIRED, example = "2026-01-02T11:22:33+09:00")
  @NotNull
  private LocalDateTime barDateTimeMin;

  @Schema(requiredMode = REQUIRED, example = "2026-01-02T11:22:33+09:00")
  @NotNull
  private LocalDateTime barDateTimeMax;

  @Schema(requiredMode = NOT_REQUIRED)
  private int wave;

  @Schema(requiredMode = NOT_REQUIRED)
  private int previousWave;

  @Schema(requiredMode = NOT_REQUIRED)
  private int nextWave;

  @Schema(requiredMode = NOT_REQUIRED)
  private int next2Wave;

  @Schema(requiredMode = NOT_REQUIRED)
  private int direction4h200;

  @Schema(requiredMode = NOT_REQUIRED)
  private int direction4h75;

  @Schema(requiredMode = NOT_REQUIRED)
  private int direction4h20;

  @Schema(requiredMode = NOT_REQUIRED)
  private int direction1h200;

  @Schema(requiredMode = NOT_REQUIRED)
  private int direction15m200;

  @Schema(requiredMode = NOT_REQUIRED)
  private int wave4h;

  @Schema(requiredMode = NOT_REQUIRED)
  private int directionTarget4h200;

}
