package jp.co.next_evolution.sandbox.api.dto.response.admin;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jp.co.next_evolution.sandbox.api.dto.response.ApiResponse;
import jp.co.next_evolution.sandbox.application.dto.user.UserDto;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class UserResponse extends ApiResponse {

  @Schema(description = "ユーザー情報")
  private UserDto user;

}
