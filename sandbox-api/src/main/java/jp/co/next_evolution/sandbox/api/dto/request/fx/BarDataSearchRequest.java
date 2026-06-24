package jp.co.next_evolution.sandbox.api.dto.request.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jp.co.next_evolution.sandbox.api.dto.request.ApiSearchRequest;
import jp.co.next_evolution.sandbox.domain.model.fx.BarType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BarDataSearchRequest extends ApiSearchRequest {

  @Schema(description = "barType", requiredMode = REQUIRED, implementation = BarType.class)
  private String barType;

  @Schema(description = "symbol", requiredMode = REQUIRED)
  @NotBlank
  @Pattern(regexp = "[A-Z0-9]{3,6}|[A-Z]{3}", message = "input format example USDJPY, DXY ")
  private String symbol;

  @Schema(description = "barDateFrom", example = "20260123", requiredMode = NOT_REQUIRED)
  @Pattern(regexp = "(^$|[0-9]{8})", message = "input format 20260123")
  private String barDateFrom;

  @Schema(description = "barDateTo", example = "20260123", requiredMode = NOT_REQUIRED)
  @Pattern(regexp = "(^$|[0-9]{8})", message = "input format 20260123")
  private String barDateTo;

  @Schema(description = "sort asc", requiredMode = NOT_REQUIRED)
  private boolean sortAsc;

  public BarType toBarType() {
    return BarType.of(barType);
  }

}
