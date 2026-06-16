package jp.co.next_evolution.sandbox.infrastructure.db.mapper.fx;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxEconomicIndicatorData;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EconomicIndicatorDataMapper {

  int count(String code, String countryCode, String importance, LocalDate publicationBaseDate);

  List<FxEconomicIndicatorData> search(String code, String countryCode, String importance,
      LocalDate publicationBaseDate, int page, int size, boolean sortAsc);

  boolean exists(String code, String countryCode, LocalDateTime publication);

  FxEconomicIndicatorData get(String code, String countryCode, LocalDateTime publication);

  int insert(FxEconomicIndicatorData data);

  int update(FxEconomicIndicatorData data, LocalDateTime publication);

  int updateCode(FxEconomicIndicatorData data, String code, String countryCode,
      LocalDateTime publication);

  int deleteLoad();

  int insertLoad(FxEconomicIndicatorData data);

  List<FxEconomicIndicatorData> loadDiff();

  int insertFromLoad();

}
