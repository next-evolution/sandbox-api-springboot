package jp.co.next_evolution.sandbox.infrastructure.db.mapper.fx;

import java.time.LocalDateTime;
import java.util.List;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxZigZag;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxZigZagBarData;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxZigZagSearchRow;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxZigZagStatus;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxZigZagWave;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ZigZagMapper {

  // --- 検索 ---

  int searchCount(
      @Param("barType") String barType,
      @Param("symbol") String symbol,
      @Param("depth") int depth,
      @Param("barDateTimeMin") LocalDateTime barDateTimeMin,
      @Param("barDateTimeMax") LocalDateTime barDateTimeMax,
      @Param("wave") int wave,
      @Param("previousWave") int previousWave,
      @Param("nextWave") int nextWave,
      @Param("next2Wave") int next2Wave,
      @Param("wave4h") int wave4h
  );

  List<FxZigZagSearchRow> search(
      @Param("barType") String barType,
      @Param("symbol") String symbol,
      @Param("depth") int depth,
      @Param("barDateTimeMin") LocalDateTime barDateTimeMin,
      @Param("barDateTimeMax") LocalDateTime barDateTimeMax,
      @Param("wave") int wave,
      @Param("previousWave") int previousWave,
      @Param("nextWave") int nextWave,
      @Param("next2Wave") int next2Wave,
      @Param("wave4h") int wave4h,
      @Param("page") int page,
      @Param("size") int size
  );

  // --- バーデータ ---

  List<FxZigZagBarData> getBarDataList(
      @Param("barType") String barType,
      @Param("symbol") String symbol,
      @Param("depth") int depth,
      @Param("waveStart") LocalDateTime waveStart
  );

  // --- ステータス ---

  List<FxZigZagStatus> getStatusList(
      @Param("barType") String barType,
      @Param("symbolType") String symbolType,
      @Param("depth") int depth
  );

  FxZigZagStatus getStatus(
      @Param("barType") String barType,
      @Param("symbol") String symbol,
      @Param("depth") int depth
  );

  // --- 生成 ---

  int targetBarCount(
      @Param("barType") String barType,
      @Param("symbol") String symbol,
      @Param("barDateTime") LocalDateTime barDateTime
  );

  List<FxZigZag> previousList(
      @Param("barType") String barType,
      @Param("symbol") String symbol,
      @Param("depth") int depth,
      @Param("barDateTime") LocalDateTime barDateTime,
      @Param("limit") int limit
  );

  List<FxZigZag> targetList(
      @Param("barType") String barType,
      @Param("symbol") String symbol,
      @Param("depth") int depth,
      @Param("barDateTime") LocalDateTime barDateTime,
      @Param("limit") int limit
  );

  int insert(
      @Param("barType") String barType,
      @Param("e") FxZigZag entity
  );

  int update(
      @Param("barType") String barType,
      @Param("e") FxZigZag entity
  );

  // --- Wave ---

  void deleteWave(
      @Param("barType") String barType,
      @Param("symbol") String symbol,
      @Param("depth") int depth,
      @Param("barDateTime") LocalDateTime barDateTime
  );

  FxZigZagWave getLastWave(
      @Param("barType") String barType,
      @Param("symbol") String symbol,
      @Param("depth") int depth
  );

  void insertWaveBulk(
      @Param("barType") String barType,
      @Param("symbol") String symbol,
      @Param("depth") int depth,
      @Param("list") List<FxZigZagWave> list
  );

}
