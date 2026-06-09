package jp.co.next_evolution.sandbox.api.dto.request.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jp.co.next_evolution.sandbox.api.dto.request.ApiSearchRequest;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SymbolSearchRequest extends ApiSearchRequest {

  @Schema(requiredMode = REQUIRED)
  @NotNull
  private String symbolType;

  // 変換メソッドをDTOに持たせる
  public SymbolType toSymbolType() {
    return SymbolType.of(symbolType);  // 不正値はここで IllegalArgumentException
  }

}
