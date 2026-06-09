package jp.co.next_evolution.sandbox.api.dto.response.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jp.co.next_evolution.sandbox.api.dto.response.ApiResponse;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ZigZagGenerateResponse extends ApiResponse {

  @Schema(requiredMode = REQUIRED)
  private ZigZagStatusItem status;

}
