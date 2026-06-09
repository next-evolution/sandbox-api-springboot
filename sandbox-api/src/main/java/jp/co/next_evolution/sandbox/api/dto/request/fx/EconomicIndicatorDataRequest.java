package jp.co.next_evolution.sandbox.api.dto.request.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jp.co.next_evolution.sandbox.application.dto.fx.EconomicIndicatorDataDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EconomicIndicatorDataRequest {

  @Schema(requiredMode = REQUIRED)
  @Valid
  private EconomicIndicatorDataDto data;

}
