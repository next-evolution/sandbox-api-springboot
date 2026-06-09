package jp.co.next_evolution.sandbox.api.dto.response.admin;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import jp.co.next_evolution.sandbox.api.dto.response.ApiSearchResponse;
import jp.co.next_evolution.sandbox.application.dto.user.UserDto;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class UserSearchResponse extends ApiSearchResponse {

  @Schema(description = "ユーザーリスト", requiredMode = REQUIRED)
  private List<UserDto> list;

}
