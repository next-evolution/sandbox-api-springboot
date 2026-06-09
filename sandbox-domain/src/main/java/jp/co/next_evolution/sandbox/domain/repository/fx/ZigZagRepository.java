package jp.co.next_evolution.sandbox.domain.repository.fx;

import java.time.LocalDateTime;
import java.util.List;
import jp.co.next_evolution.sandbox.domain.model.fx.BarType;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZag;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZagBarDataRow;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZagSearchRow;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZagStatus;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZagWave;

public interface ZigZagRepository {

  // --- 検索 ---

  int searchCount(BarType barType, String symbol, int depth,
      LocalDateTime barDateTimeMin, LocalDateTime barDateTimeMax,
      int wave, int previousWave, int nextWave, int next2Wave, int wave4h);

  List<ZigZagSearchRow> search(BarType barType, String symbol, int depth,
      LocalDateTime barDateTimeMin, LocalDateTime barDateTimeMax,
      int wave, int previousWave, int nextWave, int next2Wave, int wave4h,
      int page, int size);

  // --- ステータス ---

  List<ZigZagStatus> getStatusList(SymbolType symbolType, BarType barType, int depth);

  ZigZagStatus getStatus(BarType barType, String symbol, int depth);

  // --- バーデータ ---

  List<ZigZagBarDataRow> getBarDataList(BarType barType, String symbol, int depth,
      LocalDateTime waveStart);

  // --- 生成 ---

  int targetBarCount(BarType barType, String symbol, LocalDateTime barDateTime);

  List<ZigZag> previousList(BarType barType, String symbol, int depth,
      LocalDateTime barDateTime, int limit);

  List<ZigZag> targetList(BarType barType, String symbol, int depth,
      LocalDateTime barDateTime, int limit);

  int insert(BarType barType, ZigZag zigzag);

  int update(BarType barType, ZigZag zigzag);

  // --- Wave ---

  void deleteWave(BarType barType, String symbol, int depth, LocalDateTime barDateTime);

  ZigZagWave getLastWave(BarType barType, String symbol, int depth);

  void insertWaveBulk(BarType barType, String symbol, int depth, List<ZigZagWave> waveList);

}
