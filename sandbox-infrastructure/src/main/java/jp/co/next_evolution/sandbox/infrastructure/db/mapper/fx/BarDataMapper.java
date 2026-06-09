package jp.co.next_evolution.sandbox.infrastructure.db.mapper.fx;

import java.util.List;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxBarData;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxBarDataStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BarDataMapper {

  List<FxBarDataStatus> statusList(
      @Param("symbolType") String symbolType,
      @Param("barType") String barType
  );

  int searchCount(
      @Param("symbol") String symbol,
      @Param("barType") String barType,
      @Param("barDateFrom") String barDateFrom,
      @Param("barDateTo") String barDateTo
  );

  List<FxBarData> search(
      @Param("symbol") String symbol,
      @Param("barType") String barType,
      @Param("barDateFrom") String barDateFrom,
      @Param("barDateTo") String barDateTo,
      @Param("sortAsc") boolean sortAsc,
      @Param("page") int page,
      @Param("size") int size
  );

}
