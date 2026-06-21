package jp.co.next_evolution.sandbox.api.controller.admin;

import jp.co.next_evolution.sandbox.api.dto.request.admin.UserAdminRequest;
import jp.co.next_evolution.sandbox.api.dto.request.admin.UserBlockRequest;
import jp.co.next_evolution.sandbox.api.dto.request.admin.UserSearchRequest;
import jp.co.next_evolution.sandbox.api.dto.response.admin.UserResponse;
import jp.co.next_evolution.sandbox.api.dto.response.admin.UserSearchResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.command.user.ApproveUserCommand;
import jp.co.next_evolution.sandbox.application.command.user.BlockUserCommand;
import jp.co.next_evolution.sandbox.application.command.user.GrantAdminCommand;
import jp.co.next_evolution.sandbox.application.command.user.SearchUsersCommand;
import jp.co.next_evolution.sandbox.application.dto.user.UserDto;
import jp.co.next_evolution.sandbox.application.usecase.user.ApproveUserUseCase;
import jp.co.next_evolution.sandbox.application.usecase.user.BlockUserUseCase;
import jp.co.next_evolution.sandbox.application.usecase.user.GrantAdminUseCase;
import jp.co.next_evolution.sandbox.application.usecase.user.SearchUsersUseCase;
import jp.co.next_evolution.sandbox.domain.model.auth.AuthUser;
import jp.co.next_evolution.sandbox.domain.model.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/users")
@RequiredArgsConstructor
public class UsersController {

  private final SearchUsersUseCase searchUsersUseCase;

  private final ApproveUserUseCase approveUserUseCase;

  private final BlockUserUseCase blockUserUseCase;

  private final GrantAdminUseCase grantAdminUseCase;

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<UserSearchResponse> search(
      @RequestBody @Validated UserSearchRequest req) {

    SearchUsersUseCase.SearchResult result = searchUsersUseCase.execute(
        new SearchUsersCommand(req.getEmailAddress(), req.getApproved(), req.getPage(),
                               req.getSize())
    );

    return ResponseEntity.ok(UserSearchResponse.builder()
                                               .returnCode(ReturnCode.Ok)
                                               .totalCount(result.totalCount())
                                               .searchCount(result.totalCount())
                                               .totalPage(result.totalPage())
                                               .list(result.userList())
                                               .build());

  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/approved/{userId}")
  public ResponseEntity<UserResponse> approved(
      @PathVariable(name = "userId") String userIdBase64,
      @AuthenticationPrincipal AuthUser authUser) {

    String userId = UserId.decodeUserIdValue(userIdBase64);
    UserDto result = approveUserUseCase.execute(new ApproveUserCommand(userId, authUser.sub()));

    return ResponseEntity.ok(
        UserResponse.builder().returnCode(ReturnCode.Ok).user(result).build());

  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/block/{userId}")
  public ResponseEntity<UserResponse> block(
      @PathVariable(name = "userId") String userIdBase64,
      @RequestBody @Validated UserBlockRequest req,
      @AuthenticationPrincipal AuthUser authUser) {

    String userId = UserId.decodeUserIdValue(userIdBase64);
    UserDto result = blockUserUseCase.execute(
        new BlockUserCommand(userId, req.getBlocked(), authUser.sub()));

    return ResponseEntity.ok(
        UserResponse.builder().returnCode(ReturnCode.Ok).user(result).build());

  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/admin/{userId}")
  public ResponseEntity<UserResponse> grantAdmin(
      @PathVariable(name = "userId") String userIdBase64,
      @RequestBody @Validated UserAdminRequest req,
      @AuthenticationPrincipal AuthUser authUser) {

    String userId = UserId.decodeUserIdValue(userIdBase64);
    UserDto result = grantAdminUseCase.execute(
        new GrantAdminCommand(userId, req.getAdmin(), authUser.sub()));

    return ResponseEntity.ok(
        UserResponse.builder().returnCode(ReturnCode.Ok).user(result).build());

  }

}
