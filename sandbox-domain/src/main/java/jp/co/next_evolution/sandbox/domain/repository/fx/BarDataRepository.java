package jp.co.next_evolution.sandbox.domain.repository.fx;

import java.util.List;
import jp.co.next_evolution.sandbox.domain.model.fx.BarCsvImportCheckDto;
import jp.co.next_evolution.sandbox.domain.model.fx.BarData;
import jp.co.next_evolution.sandbox.domain.model.fx.BarDataStatusDto;
import jp.co.next_evolution.sandbox.domain.model.fx.BarLoadData;
import jp.co.next_evolution.sandbox.domain.model.fx.BarLoadRsi;
import jp.co.next_evolution.sandbox.domain.model.fx.BarLoadSma;
import jp.co.next_evolution.sandbox.domain.model.fx.BarType;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;

public interface BarDataRepository {

  List<BarDataStatusDto> statusList(SymbolType symbolType, BarType barType);

  int searchCount(String symbol, BarType barType, String barDateFrom, String barDateTo);

  List<BarData> search(
      String symbol,
      BarType barType,
      String barDateFrom,
      String barDateTo,
      boolean sortAsc,
      int page,
      int size
  );

  // ロードテーブル初期化
  void deleteLoad(String symbol);

  void deleteLoadSma(String symbol);

  void deleteLoadRsi(String symbol);

  // ロードテーブルへのバルクロード
  void bulkLoad(List<BarLoadData> list);

  void bulkLoadSma(List<BarLoadSma> list);

  void bulkLoadRsi(List<BarLoadRsi> list);

  // ロードテーブルの最新1件削除
  void deleteLatestLoad(String symbol);

  // ロードテーブルの最新足日時取得
  String getLatestLoadBarDateTime(String symbol);

  // 既存データとの整合性チェック
  BarCsvImportCheckDto importCheck(BarType barType, String symbol);

  // ロードテーブルから本テーブルへインサート
  int insertFromLoad(String symbol, BarType barType);

  int insertFromLoadSma(String symbol, BarType barType);

  int insertFromLoadRsi(String symbol, BarType barType);

  // 差分データ取得
  List<BarLoadData> getDiffBarData(String symbol, BarType barType);

  List<BarLoadSma> getDiffBarSma(String symbol, BarType barType);

  List<BarLoadRsi> getDiffBarRsi(String symbol, BarType barType);

  // 差分データ更新
  int updateBarData(String symbol, BarType barType);

  int updateBarSma(String symbol, BarType barType);

  int updateBarRsi(String symbol, BarType barType);

}
