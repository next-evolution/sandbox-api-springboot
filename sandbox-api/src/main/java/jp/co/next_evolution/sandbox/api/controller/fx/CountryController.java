package jp.co.next_evolution.sandbox.api.controller.fx;

import jp.co.next_evolution.sandbox.api.dto.request.ApiSearchRequest;
import jp.co.next_evolution.sandbox.api.dto.request.fx.CountryRequest;
import jp.co.next_evolution.sandbox.api.dto.response.fx.CountrySearchResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.dto.fx.CountryDto;
import jp.co.next_evolution.sandbox.application.usecase.fx.country.AddCountryUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.country.GetCountryUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.country.SearchCountryUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.country.UpdateCountryUseCase;
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
@RequestMapping("/v1/fx/country")
@RequiredArgsConstructor
public class CountryController {

  private final SearchCountryUseCase searchCountryUseCase;

  private final AddCountryUseCase addCountryUseCase;

  private final GetCountryUseCase getCountryUseCase;

  private final UpdateCountryUseCase updateCountryUseCase;

  @PostMapping("/search")
  public ResponseEntity<CountrySearchResponse> search(
      @RequestBody @Validated ApiSearchRequest req) {

    SearchCountryUseCase.SearchResult result = searchCountryUseCase.execute(req.getPage(),
                                                                            req.getSize());

    return ResponseEntity.ok(CountrySearchResponse.builder()
                                                  .returnCode(ReturnCode.Ok)
                                                  .totalCount(result.totalCount())
                                                  .searchCount(result.totalCount())
                                                  .totalPage(result.totalPage())
                                                  .list(result.countryList())
                                                  .build());

  }

  @PostMapping
  public ResponseEntity<Void> add(@RequestBody @Validated CountryRequest req) {

    addCountryUseCase.execute(req.getCountry());
    return ResponseEntity.ok().build();

  }

  @GetMapping("/{code}")
  public ResponseEntity<CountryDto> get(@PathVariable String code) {

    return ResponseEntity.ok(getCountryUseCase.get(code));

  }

  @PutMapping("/{code}")
  public ResponseEntity<Void> update(@PathVariable String code,
                                     @RequestBody @Validated CountryRequest req) {

    updateCountryUseCase.execute(code, req.getCountry());
    return ResponseEntity.ok().build();

  }

}
