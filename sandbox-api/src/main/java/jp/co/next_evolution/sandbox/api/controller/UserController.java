package jp.co.next_evolution.sandbox.api.controller;

import jp.co.next_evolution.sandbox.api.dto.request.user.UserRegistrationRequest;
import jp.co.next_evolution.sandbox.api.dto.request.user.UserUpdateRequest;
import jp.co.next_evolution.sandbox.api.dto.response.ApiResponse;
import jp.co.next_evolution.sandbox.api.dto.response.admin.UserResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.command.user.GetProfileCommand;
import jp.co.next_evolution.sandbox.application.command.user.RegisterUserCommand;
import jp.co.next_evolution.sandbox.application.command.user.UpdateUserCommand;
import jp.co.next_evolution.sandbox.application.dto.user.UserDto;
import jp.co.next_evolution.sandbox.application.usecase.user.GetProfileUseCase;
import jp.co.next_evolution.sandbox.application.usecase.user.RegisterUserUseCase;
import jp.co.next_evolution.sandbox.application.usecase.user.UpdateUserUseCase;
import jp.co.next_evolution.sandbox.domain.exception.ForbiddenException;
import jp.co.next_evolution.sandbox.domain.model.auth.AuthUser;
import jp.co.next_evolution.sandbox.domain.model.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

  private final GetProfileUseCase getProfileUseCase;

  private final RegisterUserUseCase registerUserUseCase;

  private final UpdateUserUseCase updateUserUseCase;

  @GetMapping
  public ResponseEntity<ApiResponse> profile(@AuthenticationPrincipal AuthUser authUser) {

    return getProfileUseCase.execute(new GetProfileCommand(authUser.sub()))
                            .map(user -> (ApiResponse) UserResponse.builder()
                                                                   .returnCode(ReturnCode.Ok)
                                                                   .user(user)
                                                                   .build())
                            .map(ResponseEntity::ok)
                            .orElse(ResponseEntity.ok(ApiResponse.builder()
                                                                  .returnCode(ReturnCode.Warn)
                                                                  .message("利用承認待ちです")
                                                                  .build()));

  }

  @PostMapping
  public ResponseEntity<UserResponse> registration(
      @RequestBody @Validated UserRegistrationRequest req,
      @AuthenticationPrincipal AuthUser authUser) {

    UserDto result = registerUserUseCase.execute(
        new RegisterUserCommand(authUser.sub(), authUser.email(), req.getNickName()));

    return ResponseEntity.ok(
        UserResponse.builder().returnCode(ReturnCode.Ok).user(result).build());

  }

  @PutMapping("/{userId}")
  public ResponseEntity<UserResponse> update(
      @PathVariable(name = "userId") String userIdBase64,
      @RequestBody @Validated UserUpdateRequest req,
      @AuthenticationPrincipal AuthUser authUser) {

    String userId = UserId.decodeUserIdValue(userIdBase64);
    if (!authUser.sub().equals(userId)) {
      throw new ForbiddenException("他のユーザの情報は更新できません");
    }

    UserDto result = updateUserUseCase.execute(
        new UpdateUserCommand(authUser.sub(), req.getNickName(), authUser.sub()));

    return ResponseEntity.ok(
        UserResponse.builder().returnCode(ReturnCode.Ok).user(result).build());

  }

}
