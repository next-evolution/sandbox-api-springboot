package jp.co.next_evolution.sandbox.api.controller.admin;

import jp.co.next_evolution.sandbox.api.dto.response.ApiResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.usecase.fx.MasterRefreshUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.MasterStatusUseCase;
import jp.co.next_evolution.sandbox.domain.exception.ForbiddenException;
import jp.co.next_evolution.sandbox.domain.model.auth.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/master-refresh")
@RequiredArgsConstructor
public class MasterRefreshController {

  private final MasterRefreshUseCase masterRefreshUseCase;

  private final MasterStatusUseCase masterStatusUseCase;

  /**
   * マスターキャッシュの現在のステータスを取得する.
   *
   * @return Redisキャッシュのステータスを含むレスポンス
   */
  @GetMapping
  public ResponseEntity<ApiResponse> status(@AuthenticationPrincipal AuthUser authUser) {

    if (!authUser.isAdmin()) {
      throw new ForbiddenException("管理者用APIです");
    }

    return ResponseEntity.ok(ApiResponse.builder()
                                        .returnCode(ReturnCode.Ok)
                                        .message(masterStatusUseCase.execute())
                                        .build());

  }

  /**
   * マスターキャッシュをリフレッシュする.
   *
   * @return 更新後のRedisキャッシュステータスを含むレスポンス
   */
  @PutMapping
  public ResponseEntity<ApiResponse> refresh(@AuthenticationPrincipal AuthUser authUser) {

    if (!authUser.isAdmin()) {
      throw new ForbiddenException("管理者用APIです");
    }

    return ResponseEntity.ok(ApiResponse.builder()
                                        .returnCode(ReturnCode.Ok)
                                        .message(masterRefreshUseCase.execute())
                                        .build());

  }

}