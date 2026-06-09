package jp.co.next_evolution.sandbox.domain.repository.fx;

import java.util.List;
import java.util.Optional;
import jp.co.next_evolution.sandbox.domain.model.KeyValue;
import jp.co.next_evolution.sandbox.domain.model.fx.EconomicIndicator;

public interface EconomicIndicatorRepository {

  int count(String countryCode, String importance, String name);

  List<EconomicIndicator> search(int page, int size, String countryCode, String importance,
      String name);

  Optional<EconomicIndicator> get(Long id);

  boolean exists(String countryCode, String name);

  int add(EconomicIndicator indicator);

  int update(EconomicIndicator indicator, String countryCode);

  List<KeyValue> getList(String countryCode);

  List<EconomicIndicator> getEconomicIndicatorList(String countryCode);

  void refreshCache(String countryCode);

}
