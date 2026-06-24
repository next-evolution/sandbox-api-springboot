package jp.co.next_evolution.sandbox.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ApiResponse {

  @Schema(requiredMode = REQUIRED, description = "処理結果", implementation = Integer.class)
  private ReturnCode returnCode;

  @Schema(requiredMode = NOT_REQUIRED, description = "message")
  private String message;

}
