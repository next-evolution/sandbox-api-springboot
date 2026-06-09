package jp.co.next_evolution.sandbox.api.controller.fx;

import jp.co.next_evolution.sandbox.api.dto.request.fx.EconomicIndicatorRequest;
import jp.co.next_evolution.sandbox.api.dto.request.fx.EconomicIndicatorSearchRequest;
import jp.co.next_evolution.sandbox.api.dto.response.fx.EconomicIndicatorSearchResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.dto.fx.EconomicIndicatorDto;
import jp.co.next_evolution.sandbox.application.usecase.fx.economicindicator.AddEconomicIndicatorUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.economicindicator.GetEconomicIndicatorUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.economicindicator.SearchEconomicIndicatorUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.economicindicator.UpdateEconomicIndicatorUseCase;
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
@RequestMapping("/v1/fx/economic-indicator")
@RequiredArgsConstructor
public class EconomicIndicatorController {

  private final SearchEconomicIndicatorUseCase searchEconomicIndicatorUseCase;

  private final GetEconomicIndicatorUseCase getEconomicIndicatorUseCase;

  private final AddEconomicIndicatorUseCase addEconomicIndicatorUseCase;

  private final UpdateEconomicIndicatorUseCase updateEconomicIndicatorUseCase;

  @PostMapping("/search")
  public ResponseEntity<EconomicIndicatorSearchResponse> search(
      @RequestBody @Validated EconomicIndicatorSearchRequest req) {

    SearchEconomicIndicatorUseCase.SearchResult result =
        searchEconomicIndicatorUseCase.execute(
            req.getPage(), req.getSize(),
            req.getCountryCode(), req.getImportance(), req.getName());

    return ResponseEntity.ok(EconomicIndicatorSearchResponse.builder()
        .returnCode(ReturnCode.Ok)
        .totalCount(result.totalCount())
        .searchCount(result.totalCount())
        .totalPage(result.totalPage())
        .list(result.list())
        .build());

  }

  @GetMapping("/{countryCode}/{id}")
  public ResponseEntity<EconomicIndicatorDto> get(
      @PathVariable String countryCode,
      @PathVariable Long id) {

    return ResponseEntity.ok(getEconomicIndicatorUseCase.execute(countryCode, id));

  }

  @PostMapping
  public ResponseEntity<Void> add(
      @RequestBody @Validated EconomicIndicatorRequest req) {

    addEconomicIndicatorUseCase.execute(req.getIndicator());
    return ResponseEntity.ok().build();

  }

  @PutMapping("/{countryCode}/{id}")
  public ResponseEntity<Void> update(
      @PathVariable String countryCode,
      @PathVariable Long id,
      @RequestBody @Validated EconomicIndicatorRequest req) {

    updateEconomicIndicatorUseCase.execute(countryCode, id, req.getIndicator());
    return ResponseEntity.ok().build();

  }

}
