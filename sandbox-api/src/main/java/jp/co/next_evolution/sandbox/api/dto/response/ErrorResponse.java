package jp.co.next_evolution.sandbox.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(
    @Schema(requiredMode = REQUIRED, description = "HttpStatus", example = "500,401,404,,,")
    int status,
    @Schema(requiredMode = REQUIRED, description = "HttpStatus", example = "UNAUTHORIZED|BAD_REQUEST,INTERNAL_SERVER_ERROR,,,")
    String statusText,
    @Schema(requiredMode = REQUIRED, description = "message")
    String message) {

}
