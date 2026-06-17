package jp.co.next_evolution.sandbox.domain.repository.fx;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import jp.co.next_evolution.sandbox.domain.model.fx.EconomicIndicatorData;

public interface EconomicIndicatorDataRepository {

  int count(String code, String countryCode, String importance, LocalDate publicationBaseDate);

  List<EconomicIndicatorData> search(String code, String countryCode, String importance,
      LocalDate publicationBaseDate, int page, int size, boolean sortAsc);

  Optional<EconomicIndicatorData> get(String code, String countryCode, LocalDateTime publication);

  boolean exists(String code, String countryCode, LocalDateTime publication);

  int add(EconomicIndicatorData data);

  int update(EconomicIndicatorData data, LocalDateTime publication);

  int updateCode(EconomicIndicatorData data, String code, String countryCode,
      LocalDateTime publication);

  int deleteLoad();

  int insertLoad(EconomicIndicatorData data);

  List<EconomicIndicatorData> loadDiff();

  int insertFromLoad();

}
