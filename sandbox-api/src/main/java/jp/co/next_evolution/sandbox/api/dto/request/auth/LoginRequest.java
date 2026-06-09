package jp.co.next_evolution.sandbox.api.dto.request.auth;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

  @Schema(requiredMode = REQUIRED, description = "email(BASE64 encode)", example = "YWNjb3VudEBkb21haW4uY29t")
  private String email;

}
