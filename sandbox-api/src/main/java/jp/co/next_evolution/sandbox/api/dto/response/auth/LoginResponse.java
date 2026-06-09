package jp.co.next_evolution.sandbox.api.dto.response.auth;

import jp.co.next_evolution.sandbox.api.dto.response.ApiResponse;
import jp.co.next_evolution.sandbox.application.dto.user.UserDto;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class LoginResponse extends ApiResponse {

  private UserDto user;

}
