package jp.co.next_evolution.sandbox.api.controller.fx;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import jp.co.next_evolution.sandbox.api.dto.request.fx.EconomicIndicatorDataRequest;
import jp.co.next_evolution.sandbox.api.dto.request.fx.EconomicIndicatorDataSearchRequest;
import jp.co.next_evolution.sandbox.api.dto.response.fx.EconomicIndicatorDataSearchResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.dto.fx.EconomicIndicatorDataDto;
import jp.co.next_evolution.sandbox.application.dto.fx.FileImportResult;
import jp.co.next_evolution.sandbox.application.usecase.fx.economicindicatordata.AddEconomicIndicatorDataUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.economicindicatordata.GetEconomicIndicatorDataUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.economicindicatordata.ImportTextEconomicIndicatorDataUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.economicindicatordata.SearchEconomicIndicatorDataUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.economicindicatordata.UpdateEconomicIndicatorDataUseCase;
import jp.co.next_evolution.sandbox.domain.exception.GenesisApiException;
import jp.co.next_evolution.sandbox.domain.model.auth.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/fx/economic-indicator-data")
@RequiredArgsConstructor
public class EconomicIndicatorDataController {

  private final SearchEconomicIndicatorDataUseCase searchEconomicIndicatorDataUseCase;

  private final GetEconomicIndicatorDataUseCase getEconomicIndicatorDataUseCase;

  private final AddEconomicIndicatorDataUseCase addEconomicIndicatorDataUseCase;

  private final UpdateEconomicIndicatorDataUseCase updateEconomicIndicatorDataUseCase;

  private final ImportTextEconomicIndicatorDataUseCase importTextEconomicIndicatorDataUseCase;

  @PostMapping("/search")
  public ResponseEntity<EconomicIndicatorDataSearchResponse> search(
      @RequestBody @Validated EconomicIndicatorDataSearchRequest req) {

    SearchEconomicIndicatorDataUseCase.SearchResult result =
        searchEconomicIndicatorDataUseCase.execute(
            req.getId(), req.getImportance(), req.getCountryCode(),
            req.getPublicationBaseDate(), req.getPage(), req.getSize(), req.isSortAsc());

    return ResponseEntity.ok(EconomicIndicatorDataSearchResponse.builder()
        .returnCode(ReturnCode.Ok)
        .totalCount(result.totalCount())
        .searchCount(result.totalCount())
        .totalPage(result.totalPage())
        .list(result.list())
        .build());

  }

  @GetMapping("/{economicIndicatorId}/{publication}")
  public ResponseEntity<EconomicIndicatorDataDto> get(
      @PathVariable Long economicIndicatorId,
      @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime publication) {

    return ResponseEntity.ok(
        getEconomicIndicatorDataUseCase.execute(economicIndicatorId, publication));

  }

  @PostMapping
  public ResponseEntity<Void> add(
      @RequestBody @Validated EconomicIndicatorDataRequest req) {

    addEconomicIndicatorDataUseCase.execute(req.getData());
    return ResponseEntity.ok().build();

  }

  @PutMapping("/{economicIndicatorId}/{publication}")
  public ResponseEntity<Void> update(
      @PathVariable Long economicIndicatorId,
      @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime publication,
      @RequestBody @Validated EconomicIndicatorDataRequest req) {

    updateEconomicIndicatorDataUseCase.execute(economicIndicatorId, publication, req.getData());
    return ResponseEntity.ok().build();

  }

  @PostMapping("/import-text")
  public ResponseEntity<List<FileImportResult>> importText(
      @RequestPart("uploadFileList") MultipartFile[] uploadFileList,
      @AuthenticationPrincipal AuthUser authUser) {

    List<ImportTextEconomicIndicatorDataUseCase.FileEntry> files =
        Arrays.stream(uploadFileList)
              .map(f -> {
                try {
                  return new ImportTextEconomicIndicatorDataUseCase.FileEntry(
                      f.getOriginalFilename(), f.getInputStream(), f.getSize());
                } catch (Exception e) {
                  throw new GenesisApiException("ファイル読み込みに失敗しました: " + f.getOriginalFilename(), e);
                }
              })
              .toList();

    List<FileImportResult> result = importTextEconomicIndicatorDataUseCase.execute(
        files, authUser.sub());

    return ResponseEntity.ok(result);

  }

}
