package jp.co.next_evolution.sandbox.api.dto.request.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ZigZagGenerateRequest {

  @Schema(requiredMode = REQUIRED)
  @NotBlank
  private String symbol;

  @Schema(requiredMode = REQUIRED)
  @NotNull
  private String barType;

  @Schema(requiredMode = REQUIRED)
  @Positive
  private short depth;

  @Schema(requiredMode = REQUIRED, example = "2026-01-02T11:22:33+09:00")
  @NotNull
  private LocalDateTime barDateTime;

  @Schema(requiredMode = REQUIRED, description = "処理件数上限")
  @Positive
  private int loadSize;

}
