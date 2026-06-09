package jp.co.next_evolution.sandbox.application.usecase.fx;

import jp.co.next_evolution.sandbox.domain.model.KeyValue;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;
import jp.co.next_evolution.sandbox.domain.repository.MasterCacheRepository;
import jp.co.next_evolution.sandbox.domain.repository.fx.CountryRepository;
import jp.co.next_evolution.sandbox.domain.repository.fx.EconomicIndicatorRepository;
import jp.co.next_evolution.sandbox.domain.repository.fx.SymbolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MasterRefreshUseCase {

  private final CountryRepository countryRepository;

  private final SymbolRepository symbolRepository;

  private final EconomicIndicatorRepository economicIndicatorRepository;

  private final MasterCacheRepository masterCacheRepository;

  public String execute() {

    // 国マスターのキャッシュを更新
    countryRepository.refreshCache();

    // シンボルマスターのキャッシュを全種類更新
    for (SymbolType symbolType : SymbolType.values()) {
      symbolRepository.refreshCache(symbolType.getCode());
    }

    // 経済指標マスターのキャッシュを全国コードで更新
    for (KeyValue country : countryRepository.getList()) {
      economicIndicatorRepository.refreshCache(country.key());
    }

    // priceキャッシュを全削除
    masterCacheRepository.deleteByPattern("price*");

    return masterCacheRepository.getStatus();

  }

}