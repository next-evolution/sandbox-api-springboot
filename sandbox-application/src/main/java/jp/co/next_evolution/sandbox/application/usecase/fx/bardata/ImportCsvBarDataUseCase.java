package jp.co.next_evolution.sandbox.application.usecase.fx.bardata;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jp.co.next_evolution.sandbox.application.command.fx.ImportCsvBarDataCommand;
import jp.co.next_evolution.sandbox.application.config.SandboxAppProperties;
import jp.co.next_evolution.sandbox.application.config.SandboxStorageProperties;
import jp.co.next_evolution.sandbox.application.dto.fx.BarCsvRow;
import jp.co.next_evolution.sandbox.application.dto.fx.BarDataImportResult;
import jp.co.next_evolution.sandbox.domain.exception.SandboxApiException;
import jp.co.next_evolution.sandbox.domain.model.fx.BarCsvImportCheckDto;
import jp.co.next_evolution.sandbox.domain.model.fx.BarLoadData;
import jp.co.next_evolution.sandbox.domain.model.fx.BarLoadRsi;
import jp.co.next_evolution.sandbox.domain.model.fx.BarLoadSma;
import jp.co.next_evolution.sandbox.domain.model.fx.BarType;
import jp.co.next_evolution.sandbox.domain.repository.fx.BarDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.MappingIterator;
import tools.jackson.dataformat.csv.CsvMapper;
import tools.jackson.dataformat.csv.CsvSchema;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportCsvBarDataUseCase {

  private static final int RSI_RANGE = 14;

  private static final CsvMapper CSV_MAPPER;

  static {
    CSV_MAPPER = new CsvMapper();
  }

  private final SandboxAppProperties sandboxAppProperties;

  private final SandboxStorageProperties sandboxStorageProperties;

  private final BarDataRepository barDataRepository;

  @Transactional
  public BarDataImportResult execute(ImportCsvBarDataCommand cmd) {

    return doImport(
        cmd.symbol(), cmd.barType(), cmd.skipLatest(),
        cmd.fileInputStream(), cmd.originalFileName(), cmd.fileSize(), cmd.userSub());

  }

  private BarDataImportResult doImport(
      String symbol,
      BarType barType,
      boolean skipLatest,
      InputStream fileInputStream,
      String originalFileName,
      long fileSize,
      String userSub
  ) {

    // 1. ファイル名チェック
    String expectedPattern = symbol + "_" + barType.getKeyword();
    if (!originalFileName.contains(expectedPattern)) {
      return BarDataImportResult.builder()
          .fileName(originalFileName)
          .fileSize(fileSize)
          .resultStatus("ERROR")
          .readCount(0)
          .message("file not exists.")
          .build();
    }

    // 2. ファイルをアップロードディレクトリに保存
    Path savedFile = saveFile(fileInputStream, originalFileName, userSub);

    // 3. ロードテーブル初期化
    barDataRepository.deleteLoad(symbol);
    barDataRepository.deleteLoadSma(symbol);
    barDataRepository.deleteLoadRsi(symbol);

    // 4. CSVを読み込みロードテーブルへバルクインサート
    int readCount = loadCsv(savedFile, symbol, barType);

    // skipLatest=true の場合、最新1件を削除
    if (skipLatest && readCount > 0) {
      barDataRepository.deleteLatestLoad(symbol);
      readCount--;
    }

    // ロードテーブルの最新足日時を取得
    String latestBarDateTime = barDataRepository.getLatestLoadBarDateTime(symbol);

    // 5. 既存データとの整合性チェック
    BarCsvImportCheckDto checkDto = barDataRepository.importCheck(barType, symbol);

    if (isImportCheckError(checkDto, symbol)) {
      return BarDataImportResult.builder()
          .symbol(symbol)
          .barDateTime(latestBarDateTime)
          .fileName(originalFileName)
          .fileSize(fileSize)
          .resultStatus("ERROR")
          .readCount(readCount)
          .existsCount(0)
          .insertCount(0)
          .differenceCount(0)
          .message("import check error.")
          .build();
    }

    if (checkDto.getDiffCount() > 0) {
      log.warn("差分データ検出: symbol={}, diffCount={}", symbol, checkDto.getDiffCount());
    }

    // 6. ロードテーブルから本テーブルへインサート
    int insertCount = barDataRepository.insertFromLoad(symbol, barType);
    int insertSmaCount = barDataRepository.insertFromLoadSma(symbol, barType);
    int insertRsiCount = barDataRepository.insertFromLoadRsi(symbol, barType);
    log.info("インサート完了: symbol={}, bar={}, sma={}, rsi={}",
        symbol, insertCount, insertSmaCount, insertRsiCount);

    // 7. 差分更新
    int differenceCount = processDiffUpdate(symbol, barType);

    // resultStatus 決定
    String resultStatus = insertCount > 0 ? "OK" : "SKIP";

    return BarDataImportResult.builder()
        .symbol(symbol)
        .barDateTime(latestBarDateTime)
        .fileName(originalFileName)
        .fileSize(fileSize)
        .resultStatus(resultStatus)
        .readCount(readCount)
        .existsCount(checkDto.getExistsCount())
        .insertCount(insertCount)
        .differenceCount(differenceCount)
        .build();

  }

  private Path saveFile(InputStream fileInputStream, String originalFileName, String userSub) {

    try {
      Path uploadDir = Paths.get(
          sandboxStorageProperties.getBucket(), sandboxStorageProperties.getFx(),
          "BarDataService", userSub);
      Files.createDirectories(uploadDir);
      Path savedFile = uploadDir.resolve(originalFileName);
      Files.copy(fileInputStream, savedFile, StandardCopyOption.REPLACE_EXISTING);
      return savedFile;
    } catch (IOException e) {
      throw new SandboxApiException("ファイル保存に失敗しました: " + originalFileName, e);
    }

  }

  private int loadCsv(Path csvFile, String symbol, BarType barType) {

    // CsvSchema schema = CsvSchema.emptySchema().withHeader();
    CsvSchema schema = CSV_MAPPER.schemaFor(BarCsvRow.class).withHeader();

    List<BarLoadData> barBuffer = new ArrayList<>();
    List<BarLoadSma> smaBuffer = new ArrayList<>();
    List<BarLoadRsi> rsiBuffer = new ArrayList<>();
    int count = 0;

    try (MappingIterator<BarCsvRow> iterator =
        CSV_MAPPER.readerFor(BarCsvRow.class).with(schema).readValues(csvFile.toFile())) {

      while (iterator.hasNext()) {
        BarCsvRow row = iterator.next();
        LocalDateTime barDateTime = barType.parseBarDateTime(row.getBarDateTime());

        barBuffer.add(toBarLoadData(symbol, barDateTime, row));
        smaBuffer.add(toBarLoadSma(symbol, barDateTime, 200, row.getSma200(), row));
        smaBuffer.add(toBarLoadSma(symbol, barDateTime, 75, row.getSma75(), row));
        smaBuffer.add(toBarLoadSma(symbol, barDateTime, 20, row.getSma20(), row));
        rsiBuffer.add(toBarLoadRsi(symbol, barDateTime, row));

        count++;

        if (barBuffer.size() >= sandboxAppProperties.getCsvBulkLoadSize()) {
          barDataRepository.bulkLoad(barBuffer);
          barDataRepository.bulkLoadSma(smaBuffer);
          barDataRepository.bulkLoadRsi(rsiBuffer);
          barBuffer.clear();
          smaBuffer.clear();
          rsiBuffer.clear();
        }
      }

      if (!barBuffer.isEmpty()) {
        barDataRepository.bulkLoad(barBuffer);
        barDataRepository.bulkLoadSma(smaBuffer);
        barDataRepository.bulkLoadRsi(rsiBuffer);
      }

    } catch (JacksonException e) {
      throw new SandboxApiException("CSV読み込みに失敗しました", e);
    }

    return count;

  }

  private int processDiffUpdate(String symbol, BarType barType) {

    List<BarLoadData> diffData = barDataRepository.getDiffBarData(symbol, barType);
    if (!diffData.isEmpty()) {
      diffData.forEach(d -> log.warn(
          "BarData差分: symbol={}, barDateTime={}, open={}, close={}",
          d.getSymbol(), d.getBarDateTime(), d.getOpenPrice(), d.getClosePrice()
      ));
      barDataRepository.updateBarData(symbol, barType);
    }

    List<BarLoadSma> diffSma = barDataRepository.getDiffBarSma(symbol, barType);
    if (!diffSma.isEmpty()) {
      log.warn("BarSma差分: symbol={}, diffCount={}", symbol, diffSma.size());
      barDataRepository.updateBarSma(symbol, barType);
    }

    List<BarLoadRsi> diffRsi = barDataRepository.getDiffBarRsi(symbol, barType);
    if (!diffRsi.isEmpty()) {
      log.warn("BarRsi差分: symbol={}, diffCount={}", symbol, diffRsi.size());
      barDataRepository.updateBarRsi(symbol, barType);
    }

    return diffData.size();

  }

  private boolean isImportCheckError(BarCsvImportCheckDto checkDto, String symbol) {

    if (checkDto.getExistsCount() == 0 && !sandboxAppProperties.isImportCheckSkip()) {
      log.error("インポートチェックエラー: 既存レコードが0件です。symbol={}", symbol);
      return true;
    }

    return false;

  }

  private BarLoadData toBarLoadData(String symbol, LocalDateTime barDateTime, BarCsvRow row) {

    return BarLoadData.builder()
        .symbol(symbol)
        .barDateTime(barDateTime)
        .openPrice(row.getOpenPrice())
        .highPrice(row.getHighPrice())
        .lowPrice(row.getLowPrice())
        .closePrice(row.getClosePrice())
        .volume(row.getVolume() != null ? row.getVolume() : 0)
        .build();

  }

  private BarLoadSma toBarLoadSma(
      String symbol, LocalDateTime barDateTime, int smaRange, BigDecimal smaPrice, BarCsvRow row) {

    return BarLoadSma.of(symbol, barDateTime, smaRange, smaPrice,
        row.getHighPrice(), row.getLowPrice());

  }

  private BarLoadRsi toBarLoadRsi(String symbol, LocalDateTime barDateTime, BarCsvRow row) {

    return BarLoadRsi.builder()
        .symbol(symbol)
        .barDateTime(barDateTime)
        .rsiRange(RSI_RANGE)
        .rsiValue(row.getRsi())
        .rsiMa(row.getRsiMa())
        .build();

  }

}
