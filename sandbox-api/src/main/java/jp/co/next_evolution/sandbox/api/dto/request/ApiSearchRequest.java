package jp.co.next_evolution.sandbox.api.dto.request;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiSearchRequest {

  @Schema(requiredMode = REQUIRED, example = "1")
  @Min(1)
  private int page;

  @Schema(requiredMode = REQUIRED, example = "20")
  @Min(1)
  private int size;

}
