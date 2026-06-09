package jp.co.next_evolution.sandbox.api.dto.request.admin;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jp.co.next_evolution.sandbox.api.dto.request.ApiSearchRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSearchRequest extends ApiSearchRequest {

  @Schema(description = "メールアドレス（部分一致）", requiredMode = NOT_REQUIRED, example = "account@domain.com")
  private String emailAddress;

  @Schema(description = "承認フラグ", requiredMode = NOT_REQUIRED)
  private Boolean approved;

}
