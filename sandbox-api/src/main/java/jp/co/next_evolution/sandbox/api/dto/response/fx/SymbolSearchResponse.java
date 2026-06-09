package jp.co.next_evolution.sandbox.api.dto.response.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import jp.co.next_evolution.sandbox.api.dto.response.ApiSearchResponse;
import jp.co.next_evolution.sandbox.application.dto.fx.SymbolDto;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class SymbolSearchResponse extends ApiSearchResponse {

  @Schema(requiredMode = REQUIRED, description = "SymbolList")
  private List<SymbolDto> list;

}
