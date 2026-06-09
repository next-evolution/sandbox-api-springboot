package jp.co.next_evolution.sandbox.api.dto.request.user;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRegistrationRequest {

  @Schema(description = "ニックネーム", requiredMode = REQUIRED, example = "Mr. Consideration")
  @NotBlank
  @Size(max = 50)
  private String nickName;

}
