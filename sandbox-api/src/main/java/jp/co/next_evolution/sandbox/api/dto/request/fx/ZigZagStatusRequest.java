package jp.co.next_evolution.sandbox.api.dto.request.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ZigZagStatusRequest {

  @Schema(requiredMode = REQUIRED, implementation = SymbolType.class)
  @NotNull
  private SymbolType symbolType;

  @Schema(requiredMode = REQUIRED)
  @NotNull
  private String barType;

  @Schema(requiredMode = REQUIRED)
  @Positive
  private short depth;

}
