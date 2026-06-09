package jp.co.next_evolution.sandbox.infrastructure.db.mapper.fx;

import java.util.List;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxSummerTime;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SummerTimeMapper {

  int count();

  List<FxSummerTime> search(int page, int size);

  FxSummerTime get(short targetYear);

  boolean exists(short targetYear);

  int insert(FxSummerTime summerTime);

  int update(FxSummerTime summerTime);

  int updateYear(FxSummerTime summerTime, int targetYear);

}
