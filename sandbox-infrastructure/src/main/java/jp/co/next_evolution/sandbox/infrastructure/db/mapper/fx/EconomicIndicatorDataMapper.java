package jp.co.next_evolution.sandbox.infrastructure.db.mapper.fx;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxEconomicIndicatorData;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EconomicIndicatorDataMapper {

  int count(long id, String importance, String countryCode, LocalDate publicationBaseDate);

  List<FxEconomicIndicatorData> search(long id, String importance, String countryCode,
      LocalDate publicationBaseDate, int page, int size, boolean sortAsc);

  boolean exists(Long id, LocalDateTime publication);

  FxEconomicIndicatorData get(Long id, LocalDateTime publication);

  int insert(FxEconomicIndicatorData data);

  int update(FxEconomicIndicatorData data, LocalDateTime publication);

  int updateId(FxEconomicIndicatorData data, Long id, LocalDateTime publication);

  int deleteLoad();

  int insertLoad(FxEconomicIndicatorData data);

  List<FxEconomicIndicatorData> loadDiff();

  int insertFromLoad();

}
