package jp.co.next_evolution.sandbox.api.dto.request.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jp.co.next_evolution.sandbox.api.dto.request.ApiSearchRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EconomicIndicatorSearchRequest extends ApiSearchRequest {

  @Schema(requiredMode = NOT_REQUIRED, description = "国コード", example = "JP")
  private String countryCode;

  @Schema(requiredMode = NOT_REQUIRED, description = "重要度",
      allowableValues = {"", "H", "M", "X", "Z"})
  private String importance;

  @Schema(requiredMode = NOT_REQUIRED, description = "指標名")
  private String name;

}
