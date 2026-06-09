package jp.co.next_evolution.sandbox.infrastructure.repository.fx;

import java.util.List;
import java.util.stream.Collectors;
import jp.co.next_evolution.sandbox.domain.model.fx.BarCsvImportCheckDto;
import jp.co.next_evolution.sandbox.domain.model.fx.BarData;
import jp.co.next_evolution.sandbox.domain.model.fx.BarDataStatusDto;
import jp.co.next_evolution.sandbox.domain.model.fx.BarLoadData;
import jp.co.next_evolution.sandbox.domain.model.fx.BarLoadRsi;
import jp.co.next_evolution.sandbox.domain.model.fx.BarLoadSma;
import jp.co.next_evolution.sandbox.domain.model.fx.BarType;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;
import jp.co.next_evolution.sandbox.domain.repository.fx.BarDataRepository;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxBarCsvImportCheckDto;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxBarData;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxBarDataStatus;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxBarLoad;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxBarLoadRsi;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxBarLoadSma;
import jp.co.next_evolution.sandbox.infrastructure.db.mapper.fx.BarDataImportMapper;
import jp.co.next_evolution.sandbox.infrastructure.db.mapper.fx.BarDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BarDataRepositoryImpl implements BarDataRepository {

  private final BarDataMapper barDataMapper;

  private final BarDataImportMapper barDataImportMapper;

  @Override
  public List<BarDataStatusDto> statusList(SymbolType symbolType, BarType barType) {

    return barDataMapper.statusList(symbolType.getCode(), barType.getSuffix())
        .stream()
        .map(entity -> BarDataStatusDto.builder()
            .symbol(entity.getSymbol())
            .barDateTimeMinS(entity.getBarDateTimeMinS())
            .barDateTimeMaxS(entity.getBarDateTimeMaxS())
            .count(entity.getCount())
            .build())
        .collect(Collectors.toList());

  }

  @Override
  public int searchCount(String symbol, BarType barType, String barDateFrom, String barDateTo) {

    return barDataMapper.searchCount(symbol, barType.getSuffix(), barDateFrom, barDateTo);

  }

  @Override
  public List<BarData> search(
      String symbol,
      BarType barType,
      String barDateFrom,
      String barDateTo,
      boolean sortAsc,
      int page,
      int size
  ) {

    return barDataMapper.search(symbol, barType.getSuffix(), barDateFrom, barDateTo,
                                sortAsc, page, size)
                        .stream()
                        .map(this::toDomain)
                        .collect(Collectors.toList());

  }

  @Override
  public void deleteLoad(String symbol) {
    barDataImportMapper.deleteLoad(symbol);
  }

  @Override
  public void deleteLoadSma(String symbol) {
    barDataImportMapper.deleteLoadSma(symbol);
  }

  @Override
  public void deleteLoadRsi(String symbol) {
    barDataImportMapper.deleteLoadRsi(symbol);
  }

  @Override
  public void bulkLoad(List<BarLoadData> list) {

    List<FxBarLoad> entities = list.stream()
        .map(this::toFxBarLoad)
        .collect(Collectors.toList());
    barDataImportMapper.bulkLoad(entities);

  }

  @Override
  public void bulkLoadSma(List<BarLoadSma> list) {

    List<FxBarLoadSma> entities = list.stream()
        .map(this::toFxBarLoadSma)
        .collect(Collectors.toList());
    barDataImportMapper.bulkLoadSma(entities);

  }

  @Override
  public void bulkLoadRsi(List<BarLoadRsi> list) {

    List<FxBarLoadRsi> entities = list.stream()
        .map(this::toFxBarLoadRsi)
        .collect(Collectors.toList());
    barDataImportMapper.bulkLoadRsi(entities);

  }

  @Override
  public void deleteLatestLoad(String symbol) {

    barDataImportMapper.deleteLatestLoad(symbol);
    barDataImportMapper.deleteLatestLoadSma(symbol);
    barDataImportMapper.deleteLatestLoadRsi(symbol);

  }

  @Override
  public String getLatestLoadBarDateTime(String symbol) {
    return barDataImportMapper.getLatestLoadBarDateTime(symbol);
  }

  @Override
  public BarCsvImportCheckDto importCheck(BarType barType, String symbol) {

    FxBarCsvImportCheckDto entity = barDataImportMapper.importCheck(barType.getTableName(), symbol);
    return BarCsvImportCheckDto.builder()
        .symbol(symbol)
        .existsCount(entity.getExistsCount())
        .diffCount(entity.getDiffCount())
        .build();

  }

  @Override
  public int insertFromLoad(String symbol, BarType barType) {
    return barDataImportMapper.insertFromLoad(symbol, barType.getSuffix());
  }

  @Override
  public int insertFromLoadSma(String symbol, BarType barType) {
    return barDataImportMapper.insertFromLoadSma(symbol, barType.getSuffix());
  }

  @Override
  public int insertFromLoadRsi(String symbol, BarType barType) {
    return barDataImportMapper.insertFromLoadRsi(symbol, barType.getSuffix());
  }

  @Override
  public List<BarLoadData> getDiffBarData(String symbol, BarType barType) {

    return barDataImportMapper.getDiffBarData(symbol, barType.getSuffix())
        .stream()
        .map(this::toDomainLoad)
        .collect(Collectors.toList());

  }

  @Override
  public List<BarLoadSma> getDiffBarSma(String symbol, BarType barType) {

    return barDataImportMapper.getDiffBarSma(symbol, barType.getSuffix())
        .stream()
        .map(this::toDomainLoadSma)
        .collect(Collectors.toList());

  }

  @Override
  public List<BarLoadRsi> getDiffBarRsi(String symbol, BarType barType) {

    return barDataImportMapper.getDiffBarRsi(symbol, barType.getSuffix())
        .stream()
        .map(this::toDomainLoadRsi)
        .collect(Collectors.toList());

  }

  @Override
  public int updateBarData(String symbol, BarType barType) {
    return barDataImportMapper.updateBarData(symbol, barType.getSuffix());
  }

  @Override
  public int updateBarSma(String symbol, BarType barType) {
    return barDataImportMapper.updateBarSma(symbol, barType.getSuffix());
  }

  @Override
  public int updateBarRsi(String symbol, BarType barType) {
    return barDataImportMapper.updateBarRsi(symbol, barType.getSuffix());
  }

  private BarData toDomain(FxBarData record) {

    return BarData.builder()
        .symbol(record.getSymbol())
        .barDateTime(record.getBarDateTime())
        .openPrice(record.getOpenPrice())
        .highPrice(record.getHighPrice())
        .lowPrice(record.getLowPrice())
        .closePrice(record.getClosePrice())
        .volume(record.getVolume())
        .highProfit(record.getHighProfit())
        .lowProfit(record.getLowProfit())
        .closeProfit(record.getCloseProfit())
        .rangeProfit(record.getRangeProfit())
        .rsiValue(record.getRsiValue())
        .rsiMa(record.getRsiMa())
        .build();

  }

  private FxBarLoad toFxBarLoad(BarLoadData domain) {

    return FxBarLoad.builder()
        .symbol(domain.getSymbol())
        .barDateTime(domain.getBarDateTime())
        .openPrice(domain.getOpenPrice())
        .highPrice(domain.getHighPrice())
        .lowPrice(domain.getLowPrice())
        .closePrice(domain.getClosePrice())
        .volume(domain.getVolume())
        .build();

  }

  private FxBarLoadSma toFxBarLoadSma(BarLoadSma domain) {

    return FxBarLoadSma.builder()
        .symbol(domain.getSymbol())
        .barDateTime(domain.getBarDateTime())
        .smaRange(domain.getSmaRange())
        .smaPrice(domain.getSmaPrice())
        .smaCross(domain.isSmaCross())
        .build();

  }

  private FxBarLoadRsi toFxBarLoadRsi(BarLoadRsi domain) {

    return FxBarLoadRsi.builder()
        .symbol(domain.getSymbol())
        .barDateTime(domain.getBarDateTime())
        .rsiRange(domain.getRsiRange())
        .rsiValue(domain.getRsiValue())
        .rsiMa(domain.getRsiMa())
        .build();

  }

  private BarLoadData toDomainLoad(FxBarLoad entity) {

    return BarLoadData.builder()
        .symbol(entity.getSymbol())
        .barDateTime(entity.getBarDateTime())
        .openPrice(entity.getOpenPrice())
        .highPrice(entity.getHighPrice())
        .lowPrice(entity.getLowPrice())
        .closePrice(entity.getClosePrice())
        .volume(entity.getVolume())
        .build();

  }

  private BarLoadSma toDomainLoadSma(FxBarLoadSma entity) {

    return BarLoadSma.builder()
        .symbol(entity.getSymbol())
        .barDateTime(entity.getBarDateTime())
        .smaRange(entity.getSmaRange())
        .smaPrice(entity.getSmaPrice())
        .smaCross(entity.isSmaCross())
        .build();

  }

  private BarLoadRsi toDomainLoadRsi(FxBarLoadRsi entity) {

    return BarLoadRsi.builder()
        .symbol(entity.getSymbol())
        .barDateTime(entity.getBarDateTime())
        .rsiRange(entity.getRsiRange())
        .rsiValue(entity.getRsiValue())
        .rsiMa(entity.getRsiMa())
        .build();

  }

}
