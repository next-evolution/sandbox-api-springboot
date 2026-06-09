package jp.co.next_evolution.sandbox.infrastructure.db.mapper.fx;

import java.util.List;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxEconomicIndicator;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.KeyValueRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EconomicIndicatorMapper {

  List<KeyValueRecord> getList(String countryCode);

  List<FxEconomicIndicator> getEconomicIndicatorList(String countryCode);

  int count(String countryCode, String importance, String name);

  List<FxEconomicIndicator> search(int page, int size, String countryCode, String importance,
      String name);

  FxEconomicIndicator get(Long id);

  boolean exists(String countryCode, String name);

  int insert(FxEconomicIndicator indicator);

  int update(FxEconomicIndicator indicator, String countryCode);

}
