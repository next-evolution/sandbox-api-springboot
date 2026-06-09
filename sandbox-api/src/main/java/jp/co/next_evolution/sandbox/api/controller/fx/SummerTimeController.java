package jp.co.next_evolution.sandbox.api.controller.fx;

import jp.co.next_evolution.sandbox.api.dto.request.ApiSearchRequest;
import jp.co.next_evolution.sandbox.api.dto.request.fx.SummerTimeRequest;
import jp.co.next_evolution.sandbox.api.dto.response.fx.SummerTimeSearchResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.dto.fx.SummerTimeDto;
import jp.co.next_evolution.sandbox.application.usecase.fx.summertime.AddSummerTimeUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.summertime.GetSummerTimeUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.summertime.SearchSummerTimeUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.summertime.UpdateSummerTimeUseCase;
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
@RequestMapping("/v1/fx/summer-time")
@RequiredArgsConstructor
public class SummerTimeController {

  private final SearchSummerTimeUseCase searchSummerTimeUseCase;

  private final AddSummerTimeUseCase addSummerTimeUseCase;

  private final GetSummerTimeUseCase getSummerTimeUseCase;

  private final UpdateSummerTimeUseCase updateSummerTimeUseCase;

  @PostMapping("/search")
  public ResponseEntity<SummerTimeSearchResponse> search(
      @RequestBody @Validated ApiSearchRequest req) {

    SearchSummerTimeUseCase.SearchResult result =
        searchSummerTimeUseCase.execute(req.getPage(), req.getSize());

    return ResponseEntity.ok(SummerTimeSearchResponse.builder().returnCode(ReturnCode.Ok)
        .totalCount(result.totalCount()).searchCount(result.totalCount())
        .totalPage(result.totalPage()).list(result.summerTimeList()).build());

  }

  @PostMapping
  public ResponseEntity<Void> add(@RequestBody @Validated SummerTimeRequest req) {

    addSummerTimeUseCase.execute(req.getSummerTime());
    return ResponseEntity.ok().build();

  }

  @GetMapping("/{targetYear}")
  public ResponseEntity<SummerTimeDto> get(@PathVariable short targetYear) {

    return ResponseEntity.ok(getSummerTimeUseCase.get(targetYear));

  }

  @PutMapping("/{targetYear}")
  public ResponseEntity<Void> update(@PathVariable short targetYear,
      @RequestBody @Validated SummerTimeRequest req) {

    updateSummerTimeUseCase.execute(targetYear, req.getSummerTime());
    return ResponseEntity.ok().build();

  }

}
