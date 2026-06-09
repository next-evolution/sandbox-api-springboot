package jp.co.next_evolution.sandbox.api.controller.fx;

import java.util.List;
import jp.co.next_evolution.sandbox.application.usecase.fx.GetMasterUseCase;
import jp.co.next_evolution.sandbox.domain.model.KeyValue;
import jp.co.next_evolution.sandbox.security.annotation.PublicApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/fx/master-list")
@RequiredArgsConstructor
public class MasterListController {

  private final GetMasterUseCase getMasterUseCase;

  @PublicApi
  @GetMapping("/symbol/{symbolType}")
  public ResponseEntity<List<KeyValue>> symbol(@PathVariable String symbolType) {

    return ResponseEntity.ok(getMasterUseCase.symbol(symbolType));

  }

  @PublicApi
  @GetMapping("/country")
  public ResponseEntity<List<KeyValue>> country() {

    return ResponseEntity.ok(getMasterUseCase.country());

  }

  @PublicApi
  @GetMapping("/currency-pair")
  public ResponseEntity<List<KeyValue>> currencyPair() {

    return ResponseEntity.ok(getMasterUseCase.currencyPair());

  }

  @PublicApi
  @GetMapping("/currency-index")
  public ResponseEntity<List<KeyValue>> currencyIndex() {

    return ResponseEntity.ok(getMasterUseCase.currencyIndex());

  }

  @PublicApi
  @GetMapping("/economic-indicator/{countryCode}")
  public ResponseEntity<List<KeyValue>> economicIndicator(
      @PathVariable String countryCode) {

    return ResponseEntity.ok(getMasterUseCase.economicIndicator(countryCode));

  }

}
