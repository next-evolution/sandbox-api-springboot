package jp.co.next_evolution.sandbox.infrastructure.db.mapper.fx;

import java.util.List;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxBarCsvImportCheckDto;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxBarLoad;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxBarLoadRsi;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxBarLoadSma;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BarDataImportMapper {

  // ロードテーブル初期化
  void deleteLoad(@Param("symbol") String symbol);

  void deleteLoadSma(@Param("symbol") String symbol);

  void deleteLoadRsi(@Param("symbol") String symbol);

  // ロードテーブルへのバルクインサート
  void bulkLoad(@Param("list") List<FxBarLoad> list);

  void bulkLoadSma(@Param("list") List<FxBarLoadSma> list);

  void bulkLoadRsi(@Param("list") List<FxBarLoadRsi> list);

  // 最新1件削除（各ロードテーブル）
  void deleteLatestLoad(@Param("symbol") String symbol);

  void deleteLatestLoadSma(@Param("symbol") String symbol);

  void deleteLatestLoadRsi(@Param("symbol") String symbol);

  // 最新足日時取得
  String getLatestLoadBarDateTime(@Param("symbol") String symbol);

  // 既存データとの整合性チェック
  FxBarCsvImportCheckDto importCheck(
      @Param("tableBarData") String tableBarData,
      @Param("symbol") String symbol
  );

  // ロードテーブルから本テーブルへインサート
  int insertFromLoad(@Param("symbol") String symbol, @Param("barType") String barType);

  int insertFromLoadSma(@Param("symbol") String symbol, @Param("barType") String barType);

  int insertFromLoadRsi(@Param("symbol") String symbol, @Param("barType") String barType);

  // 差分データ取得
  List<FxBarLoad> getDiffBarData(@Param("symbol") String symbol, @Param("barType") String barType);

  List<FxBarLoadSma> getDiffBarSma(
      @Param("symbol") String symbol, @Param("barType") String barType);

  List<FxBarLoadRsi> getDiffBarRsi(
      @Param("symbol") String symbol, @Param("barType") String barType);

  // 差分データ更新
  int updateBarData(@Param("symbol") String symbol, @Param("barType") String barType);

  int updateBarSma(@Param("symbol") String symbol, @Param("barType") String barType);

  int updateBarRsi(@Param("symbol") String symbol, @Param("barType") String barType);

}
