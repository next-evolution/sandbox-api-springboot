package jp.co.next_evolution.sandbox.domain.repository.fx;

import java.util.List;
import java.util.Optional;
import jp.co.next_evolution.sandbox.domain.model.KeyValue;
import jp.co.next_evolution.sandbox.domain.model.fx.Country;

public interface CountryRepository {

  List<KeyValue> getList();

  int count();

  List<Country> search(int page, int size);

  List<Country> countryList();

  Optional<Country> get(String code);

  boolean exists(String code);

  int add(Country country);

  int update(Country country);

  int update(Country country, String code);

  void refreshCache();

}
