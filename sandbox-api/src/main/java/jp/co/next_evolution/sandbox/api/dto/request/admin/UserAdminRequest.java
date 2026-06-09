package jp.co.next_evolution.sandbox.api.dto.request.admin;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserAdminRequest {

  @Schema(description = "管理者権限フラグ", requiredMode = REQUIRED)
  @NotNull
  private Boolean admin;

}
