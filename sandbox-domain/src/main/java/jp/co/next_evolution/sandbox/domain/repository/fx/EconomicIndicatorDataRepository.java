package jp.co.next_evolution.sandbox.domain.repository.fx;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import jp.co.next_evolution.sandbox.domain.model.fx.EconomicIndicatorData;

public interface EconomicIndicatorDataRepository {

  int count(long id, String importance, String countryCode, LocalDate publicationBaseDate);

  List<EconomicIndicatorData> search(long id, String importance, String countryCode,
      LocalDate publicationBaseDate, int page, int size, boolean sortAsc);

  Optional<EconomicIndicatorData> get(Long id, LocalDateTime publication);

  boolean exists(Long id, LocalDateTime publication);

  int add(EconomicIndicatorData data);

  int update(EconomicIndicatorData data, LocalDateTime publication);

  int updateId(EconomicIndicatorData data, Long id, LocalDateTime publication);

  int deleteLoad();

  int insertLoad(EconomicIndicatorData data);

  List<EconomicIndicatorData> loadDiff();

  int insertFromLoad();

}
