package jp.co.next_evolution.sandbox.api.dto.request.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import jp.co.next_evolution.sandbox.domain.model.fx.BarType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ZigZagBarDataRequest {

  @Schema(requiredMode = REQUIRED, implementation = BarType.class)
  @NotNull
  private BarType barType;

  @Schema(requiredMode = REQUIRED)
  @NotBlank
  private String symbol;

  @Schema(requiredMode = REQUIRED)
  @Positive
  private short depth;

  @Schema(requiredMode = REQUIRED, example = "2026-01-23T12:34:56+09:00")
  @NotNull
  private LocalDateTime waveStart;

  @Schema(requiredMode = REQUIRED)
  private int wave;

}
