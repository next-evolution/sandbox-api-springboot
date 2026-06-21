package jp.co.next_evolution.sandbox.api.controller.admin;

import jp.co.next_evolution.sandbox.api.dto.response.ApiResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.usecase.fx.MasterRefreshUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.MasterStatusUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<ApiResponse> status() {

    return ResponseEntity.ok(ApiResponse.builder()
                                        .returnCode(ReturnCode.Ok)
                                        .message(masterStatusUseCase.execute())
                                        .build());

  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping
  public ResponseEntity<ApiResponse> refresh() {

    return ResponseEntity.ok(ApiResponse.builder()
                                        .returnCode(ReturnCode.Ok)
                                        .message(masterRefreshUseCase.execute())
                                        .build());

  }

}