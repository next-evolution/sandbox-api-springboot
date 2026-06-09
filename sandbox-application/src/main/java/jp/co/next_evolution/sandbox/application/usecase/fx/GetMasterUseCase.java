package jp.co.next_evolution.sandbox.application.usecase.fx;

import java.util.List;
import jp.co.next_evolution.sandbox.domain.model.KeyValue;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;
import jp.co.next_evolution.sandbox.domain.repository.fx.CountryRepository;
import jp.co.next_evolution.sandbox.domain.repository.fx.EconomicIndicatorRepository;
import jp.co.next_evolution.sandbox.domain.repository.fx.SymbolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetMasterUseCase {

  private final SymbolRepository symbolRepository;

  private final CountryRepository countryRepository;

  private final EconomicIndicatorRepository economicIndicatorRepository;

  public List<KeyValue> symbol(String symbolType) {
    return symbolRepository.getList(SymbolType.of(symbolType));
  }

  public List<KeyValue> country() {
    return countryRepository.getList();
  }

  public List<KeyValue> currencyPair() {
    return symbolRepository.getList(SymbolType.Trade);
  }

  public List<KeyValue> currencyIndex() {
    return symbolRepository.getList(SymbolType.Analyze);
  }

  public List<KeyValue> economicIndicator(String countryCode) {
    return economicIndicatorRepository.getList(countryCode);
  }

}
