package jp.co.next_evolution.sandbox.api.controller.fx;

import java.util.List;
import jp.co.next_evolution.sandbox.api.dto.request.fx.BarDataSearchRequest;
import jp.co.next_evolution.sandbox.api.dto.response.fx.BarDataSearchResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.command.fx.ImportCsvBarDataCommand;
import jp.co.next_evolution.sandbox.application.command.fx.SearchBarDataCommand;
import jp.co.next_evolution.sandbox.application.command.fx.StatusBarDataCommand;
import jp.co.next_evolution.sandbox.application.dto.fx.BarDataImportResult;
import jp.co.next_evolution.sandbox.application.usecase.fx.bardata.ImportCsvBarDataUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.bardata.SearchBarDataUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.bardata.StatusBarDataUseCase;
import jp.co.next_evolution.sandbox.domain.model.auth.AuthUser;
import jp.co.next_evolution.sandbox.domain.model.fx.BarType;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/fx/bar-data")
@RequiredArgsConstructor
public class BarDataController {

  private final SearchBarDataUseCase searchBarDataUseCase;

  private final ImportCsvBarDataUseCase importCsvBarDataUseCase;

  private final StatusBarDataUseCase statusBarDataUseCase;

  @PostMapping
  public ResponseEntity<BarDataSearchResponse> search(
      @RequestBody @Validated BarDataSearchRequest req) {

    SearchBarDataUseCase.SearchResult result = searchBarDataUseCase.execute(
        new SearchBarDataCommand(
            req.getSymbol(),
            req.toBarType(),
            req.getBarDateFrom(),
            req.getBarDateTo(),
            req.isSortAsc(),
            req.getPage(),
            req.getSize()
        )
    );

    return ResponseEntity.ok(BarDataSearchResponse.builder()
                                                  .returnCode(ReturnCode.Ok)
                                                  .totalCount(result.totalCount())
                                                  .searchCount(result.totalCount())
                                                  .totalPage(result.totalPage())
                                                  .list(result.barDataList())
                                                  .build());

  }

  @PostMapping("/import-csv/{symbol}/{barType}/{skipLatest}")
  public ResponseEntity<BarDataImportResult> importCsv(
      @PathVariable(name = "symbol") String symbol,
      @PathVariable(name = "barType") String barType,
      @PathVariable(name = "skipLatest") boolean skipLatest,
      @RequestPart(name = "uploadFile") MultipartFile uploadFile,
      @AuthenticationPrincipal AuthUser authUser
  ) throws Exception {

    BarDataImportResult result = importCsvBarDataUseCase.execute(
        new ImportCsvBarDataCommand(
            symbol,
            BarType.of(barType),
            skipLatest,
            uploadFile.getInputStream(),
            uploadFile.getOriginalFilename(),
            uploadFile.getSize(),
            authUser.sub()
        )
    );

    return ResponseEntity.ok(result);

  }

  @GetMapping("/{symbolType}/{barType}")
  public ResponseEntity<List<BarDataImportResult>> status(
      @PathVariable(name = "symbolType") String symbolType,
      @PathVariable(name = "barType") String barType) {

    List<BarDataImportResult> result = statusBarDataUseCase.execute(
        new StatusBarDataCommand(
            SymbolType.of(symbolType),
            BarType.of(barType)
        )
    );

    return ResponseEntity.ok(result);

  }

}
