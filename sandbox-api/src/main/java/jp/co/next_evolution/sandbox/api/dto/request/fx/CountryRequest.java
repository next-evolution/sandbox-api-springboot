package jp.co.next_evolution.sandbox.api.dto.request.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jp.co.next_evolution.sandbox.application.dto.fx.CountryDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CountryRequest {

  @Schema(requiredMode = REQUIRED, implementation = CountryDto.class)
  private CountryDto country;

}
