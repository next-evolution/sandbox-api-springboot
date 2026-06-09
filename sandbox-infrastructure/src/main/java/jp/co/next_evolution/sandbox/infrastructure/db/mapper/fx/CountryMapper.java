package jp.co.next_evolution.sandbox.infrastructure.db.mapper.fx;

import java.util.List;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxCountry;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.KeyValueRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CountryMapper {

  List<KeyValueRecord> getList();

  int count();

  List<FxCountry> search(int page, int size);

  List<FxCountry> countryList();

  FxCountry get(String code);

  boolean exists(String code);

  int insert(FxCountry country);

  int update(FxCountry country);

  int updateCode(FxCountry country, String code);
}
