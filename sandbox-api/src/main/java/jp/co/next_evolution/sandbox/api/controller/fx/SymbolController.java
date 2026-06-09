package jp.co.next_evolution.sandbox.api.controller.fx;

import java.util.List;
import jp.co.next_evolution.sandbox.api.dto.request.fx.SymbolRequest;
import jp.co.next_evolution.sandbox.api.dto.request.fx.SymbolSearchRequest;
import jp.co.next_evolution.sandbox.api.dto.response.fx.SymbolSearchResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.dto.fx.SymbolDto;
import jp.co.next_evolution.sandbox.application.usecase.fx.symbol.AddSymbolUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.symbol.GetSymbolUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.symbol.SearchSymbolUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.symbol.UpdateSymbolUseCase;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/fx/symbol")
@RequiredArgsConstructor
public class SymbolController {

  private final SearchSymbolUseCase searchSymbolUseCase;

  private final AddSymbolUseCase addSymbolUseCase;

  private final GetSymbolUseCase getSymbolUseCase;

  private final UpdateSymbolUseCase updateSymbolUseCase;

  @GetMapping("/currency-pair-list")
  public ResponseEntity<List<SymbolDto>> currencyPairList() {

    return ResponseEntity.ok(searchSymbolUseCase.execute(SymbolType.Trade, 1, 500).symbolList());

  }

  @GetMapping("/currency-index-list")
  public ResponseEntity<List<SymbolDto>> currencyIndexList() {

    return ResponseEntity.ok(searchSymbolUseCase.execute(SymbolType.Analyze, 1, 500).symbolList());

  }

  @PostMapping("/search")
  public ResponseEntity<SymbolSearchResponse> search(
      @RequestBody @Validated SymbolSearchRequest req) {

    SearchSymbolUseCase.SearchResult result =
        searchSymbolUseCase.execute(req.toSymbolType(), req.getPage(), req.getSize());

    return ResponseEntity.ok(SymbolSearchResponse.builder()
                                                 .returnCode(ReturnCode.Ok)
                                                 .totalCount(result.totalCount())
                                                 .searchCount(result.totalCount())
                                                 .totalPage(result.totalPage())
                                                 .list(result.symbolList())
                                                 .build());

  }

  @PostMapping
  public ResponseEntity<Void> add(@RequestBody @Validated SymbolRequest req) {

    addSymbolUseCase.execute(req.getSymbol());
    return ResponseEntity.ok().build();

  }

  @GetMapping("/{symbol}")
  public ResponseEntity<SymbolDto> get(@PathVariable String symbol) {

    return ResponseEntity.ok(getSymbolUseCase.get(symbol));

  }

  @PutMapping("/{symbol}")
  public ResponseEntity<Void> update(@PathVariable String symbol,
                                     @RequestBody @Validated SymbolRequest req) {

    updateSymbolUseCase.execute(symbol, req.getSymbol());
    return ResponseEntity.ok().build();

  }

}
