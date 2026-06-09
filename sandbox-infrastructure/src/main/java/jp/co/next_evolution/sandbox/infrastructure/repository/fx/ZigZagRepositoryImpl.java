package jp.co.next_evolution.sandbox.infrastructure.repository.fx;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import jp.co.next_evolution.sandbox.domain.model.fx.BarType;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZag;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZagBarDataRow;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZagSearchRow;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZagStatus;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZagWave;
import jp.co.next_evolution.sandbox.domain.repository.fx.ZigZagRepository;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxZigZag;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxZigZagBarData;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxZigZagSearchRow;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxZigZagStatus;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxZigZagWave;
import jp.co.next_evolution.sandbox.infrastructure.db.mapper.fx.ZigZagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ZigZagRepositoryImpl implements ZigZagRepository {

  private final ZigZagMapper zigZagMapper;

  @Override
  public int searchCount(BarType barType, String symbol, int depth,
      LocalDateTime barDateTimeMin, LocalDateTime barDateTimeMax,
      int wave, int previousWave, int nextWave, int next2Wave, int wave4h) {

    return zigZagMapper.searchCount(
        barType.getSuffix(), symbol, depth,
        barDateTimeMin, barDateTimeMax,
        wave, previousWave, nextWave, next2Wave, wave4h);
  }

  @Override
  public List<ZigZagSearchRow> search(BarType barType, String symbol, int depth,
      LocalDateTime barDateTimeMin, LocalDateTime barDateTimeMax,
      int wave, int previousWave, int nextWave, int next2Wave, int wave4h,
      int page, int size) {

    return zigZagMapper.search(
        barType.getSuffix(), symbol, depth,
        barDateTimeMin, barDateTimeMax,
        wave, previousWave, nextWave, next2Wave, wave4h,
        page, size)
        .stream()
        .map(this::toSearchDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<ZigZagStatus> getStatusList(SymbolType symbolType, BarType barType, int depth) {

    return zigZagMapper.getStatusList(barType.getSuffix(), symbolType.getCode(), depth)
        .stream()
        .map(e -> toStatusDomain(e, symbolType, barType))
        .collect(Collectors.toList());
  }

  @Override
  public ZigZagStatus getStatus(BarType barType, String symbol, int depth) {

    FxZigZagStatus e = zigZagMapper.getStatus(barType.getSuffix(), symbol, depth);
    return toStatusDomain(e, null, barType);
  }

  @Override
  public int targetBarCount(BarType barType, String symbol, LocalDateTime barDateTime) {
    return zigZagMapper.targetBarCount(barType.getSuffix(), symbol, barDateTime);
  }

  @Override
  public List<ZigZag> previousList(BarType barType, String symbol, int depth,
      LocalDateTime barDateTime, int limit) {

    return zigZagMapper.previousList(barType.getSuffix(), symbol, depth, barDateTime, limit)
        .stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<ZigZag> targetList(BarType barType, String symbol, int depth,
      LocalDateTime barDateTime, int limit) {

    return zigZagMapper.targetList(barType.getSuffix(), symbol, depth, barDateTime, limit)
        .stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public int insert(BarType barType, ZigZag zigzag) {
    return zigZagMapper.insert(barType.getSuffix(), toEntity(zigzag));
  }

  @Override
  public int update(BarType barType, ZigZag zigzag) {
    return zigZagMapper.update(barType.getSuffix(), toEntity(zigzag));
  }

  @Override
  public void deleteWave(BarType barType, String symbol, int depth, LocalDateTime barDateTime) {
    zigZagMapper.deleteWave(barType.getSuffix(), symbol, depth, barDateTime);
  }

  @Override
  public ZigZagWave getLastWave(BarType barType, String symbol, int depth) {
    FxZigZagWave e = zigZagMapper.getLastWave(barType.getSuffix(), symbol, depth);
    return e == null ? null : toWaveDomain(e);
  }

  @Override
  public void insertWaveBulk(BarType barType, String symbol, int depth,
      List<ZigZagWave> waveList) {

    List<FxZigZagWave> entities = waveList.stream()
        .map(this::toWaveEntity)
        .collect(Collectors.toList());
    zigZagMapper.insertWaveBulk(barType.getSuffix(), symbol, depth, entities);
  }

  @Override
  public List<ZigZagBarDataRow> getBarDataList(BarType barType, String symbol, int depth,
      LocalDateTime waveStart) {

    return zigZagMapper.getBarDataList(barType.getSuffix(), symbol, depth, waveStart)
        .stream()
        .map(this::toBarDataRow)
        .collect(Collectors.toList());
  }

  // ------------------------------------------------------------------ //
  // Domain ←→ Entity 変換                                               //
  // ------------------------------------------------------------------ //

  private ZigZag toDomain(FxZigZag e) {
    return ZigZag.builder()
        .symbol(e.getSymbol())
        .depth(e.getDepth())
        .barDateTime(e.getBarDateTime())
        .resistance(e.getResistance())
        .resistanceFractal(e.getResistanceFractal())
        .support(e.getSupport())
        .supportFractal(e.getSupportFractal())
        .priceHigh(e.getPriceHigh())
        .priceLow(e.getPriceLow())
        .backStepHigh(e.getBackStepHigh())
        .backStepLow(e.getBackStepLow())
        .fractalHigh(e.getFractalHigh())
        .fractalLow(e.getFractalLow())
        .resistanceBarDateTime(e.getResistanceBarDateTime())
        .resistanceFractalBarDateTime(e.getResistanceFractalBarDateTime())
        .supportBarDateTime(e.getSupportBarDateTime())
        .supportFractalBarDateTime(e.getSupportFractalBarDateTime())
        .priceHighBarDateTime(e.getPriceHighBarDateTime())
        .priceLowBarDateTime(e.getPriceLowBarDateTime())
        .backStepHighBarDateTime(e.getBackStepHighBarDateTime())
        .backStepLowBarDateTime(e.getBackStepLowBarDateTime())
        .wave(e.getWave())
        .upTrend(e.isUpTrend())
        .breakResistance(e.isBreakResistance())
        .breakSupport(e.isBreakSupport())
        .backStepUp(e.getBackStepUp())
        .backStepDown(e.getBackStepDown())
        .waveFractal(e.getWave())
        .breakResistanceFractal(e.isBreakResistance())
        .breakSupportFractal(e.isBreakSupport())
        .barHighPrice(e.getBarHighPrice())
        .barLowPrice(e.getBarLowPrice())
        .barClosePrice(e.getBarClosePrice())
        .existsZigzag(e.isExistsZigzag())
        .build();
  }

  private FxZigZag toEntity(ZigZag z) {
    return FxZigZag.builder()
        .symbol(z.getSymbol())
        .depth(z.getDepth())
        .barDateTime(z.getBarDateTime())
        .resistance(z.getResistance())
        .resistanceFractal(z.getResistanceFractal())
        .support(z.getSupport())
        .supportFractal(z.getSupportFractal())
        .priceHigh(z.getPriceHigh())
        .priceLow(z.getPriceLow())
        .backStepHigh(z.getBackStepHigh())
        .backStepLow(z.getBackStepLow())
        .fractalHigh(z.getFractalHigh())
        .fractalLow(z.getFractalLow())
        .resistanceBarDateTime(z.getResistanceBarDateTime())
        .resistanceFractalBarDateTime(z.getResistanceFractalBarDateTime())
        .supportBarDateTime(z.getSupportBarDateTime())
        .supportFractalBarDateTime(z.getSupportFractalBarDateTime())
        .priceHighBarDateTime(z.getPriceHighBarDateTime())
        .priceLowBarDateTime(z.getPriceLowBarDateTime())
        .backStepHighBarDateTime(z.getBackStepHighBarDateTime())
        .backStepLowBarDateTime(z.getBackStepLowBarDateTime())
        .wave(z.getWave())
        .upTrend(z.isUpTrend())
        .breakResistance(z.isBreakResistance())
        .breakSupport(z.isBreakSupport())
        .backStepUp(z.getBackStepUp())
        .backStepDown(z.getBackStepDown())
        .build();
  }

  private ZigZagWave toWaveDomain(FxZigZagWave e) {
    return ZigZagWave.builder()
        .waveStart(e.getWaveStart())
        .waveEnd(e.getWaveEnd())
        .wave(e.getWave())
        .resistance(e.getResistance())
        .support(e.getSupport())
        .previousWaveStart(e.getPreviousWaveStart())
        .previousWave(e.getPreviousWave())
        .waveMemo(e.getWaveMemo())
        .build();
  }

  private FxZigZagWave toWaveEntity(ZigZagWave w) {
    return FxZigZagWave.builder()
        .waveStart(w.getWaveStart())
        .waveEnd(w.getWaveEnd())
        .wave(w.getWave())
        .resistance(w.getResistance())
        .support(w.getSupport())
        .previousWaveStart(w.getPreviousWaveStart())
        .previousWave(w.getPreviousWave())
        .waveMemo(w.getWaveMemo())
        .build();
  }

  private ZigZagStatus toStatusDomain(FxZigZagStatus e, SymbolType symbolType, BarType barType) {
    return ZigZagStatus.builder()
        .symbolType(symbolType)
        .barType(barType)
        .symbol(e.getSymbol())
        .barDateTimeMin(e.getBarDateTimeMin())
        .barDateTimeMax(e.getBarDateTimeMax())
        .barCount(e.getBarCount())
        .barDateTimeMinZigZag(e.getBarDateTimeMinZigZag())
        .barDateTimeMaxZigZag(e.getBarDateTimeMaxZigZag())
        .zigzagCount(e.getZigzagCount())
        .breakResistanceCount(e.getBreakResistanceCount())
        .breakSupportCount(e.getBreakSupportCount())
        .build();
  }

  private ZigZagSearchRow toSearchDomain(FxZigZagSearchRow e) {
    ZigZagSearchRow row = new ZigZagSearchRow();
    row.setSymbol(e.getSymbol());
    row.setDepth(e.getDepth());

    ZigZagSearchRow.WaveWithSma cur = new ZigZagSearchRow.WaveWithSma();
    cur.setWaveStart(e.getCurWaveStart());
    cur.setWaveEnd(e.getCurWaveEnd());
    cur.setWave(e.getCurWave());
    cur.setResistance(e.getCurResistance());
    cur.setSupport(e.getCurSupport());
    cur.setSma4h200s(toSmaPrice(e.getCurSma4h200sS(), e.getCurSma4h200sE()));
    cur.setSma4h75s(toSmaPrice(e.getCurSma4h75sS(), e.getCurSma4h75sE()));
    cur.setSma4h20s(toSmaPrice(e.getCurSma4h20sS(), e.getCurSma4h20sE()));
    cur.setSma1h200s(toSmaPrice(e.getCurSma1h200sS(), e.getCurSma1h200sE()));
    cur.setSma15m200s(toSmaPrice(e.getCurSma15m200sS(), e.getCurSma15m200sE()));
    row.setCurrent(cur);

    ZigZagSearchRow.WaveWithSma t4h = new ZigZagSearchRow.WaveWithSma();
    t4h.setWaveStart(e.getT4hWaveStart());
    t4h.setWaveEnd(e.getT4hWaveEnd());
    t4h.setWave(e.getT4hWave());
    t4h.setResistance(e.getT4hResistance());
    t4h.setSupport(e.getT4hSupport());
    t4h.setSma4h200s(toSmaPrice(e.getT4hSma4h200sS(), e.getT4hSma4h200sE()));
    t4h.setSma4h75s(toSmaPrice(e.getT4hSma4h75sS(), e.getT4hSma4h75sE()));
    t4h.setSma4h20s(toSmaPrice(e.getT4hSma4h20sS(), e.getT4hSma4h20sE()));
    t4h.setSma1h200s(toSmaPrice(e.getT4hSma1h200sS(), e.getT4hSma1h200sE()));
    t4h.setSma15m200s(toSmaPrice(e.getT4hSma15m200sS(), e.getT4hSma15m200sE()));
    row.setTarget4h(t4h);

    ZigZagSearchRow.WaveInfo prv = new ZigZagSearchRow.WaveInfo();
    prv.setWaveStart(e.getPrvWaveStart());
    prv.setWaveEnd(e.getPrvWaveEnd());
    prv.setWave(e.getPrvWave());
    prv.setResistance(e.getPrvResistance());
    prv.setSupport(e.getPrvSupport());
    row.setPrevious(prv);

    ZigZagSearchRow.WaveInfo nxt = new ZigZagSearchRow.WaveInfo();
    nxt.setWaveStart(e.getNxtWaveStart());
    nxt.setWaveEnd(e.getNxtWaveEnd());
    nxt.setWave(e.getNxtWave());
    nxt.setResistance(e.getNxtResistance());
    nxt.setSupport(e.getNxtSupport());
    row.setNext(nxt);

    ZigZagSearchRow.WaveInfo nx2 = new ZigZagSearchRow.WaveInfo();
    nx2.setWaveStart(e.getNx2WaveStart());
    nx2.setWaveEnd(e.getNx2WaveEnd());
    nx2.setWave(e.getNx2Wave());
    nx2.setResistance(e.getNx2Resistance());
    nx2.setSupport(e.getNx2Support());
    row.setNext2(nx2);

    row.setWaveDxy4h(e.getWaveDxy4h());
    row.setWaveDxy1h(e.getWaveDxy1h());
    row.setFractalWaveList(Collections.emptyList());
    return row;
  }

  private ZigZagSearchRow.SmaPrice toSmaPrice(BigDecimal priceS, BigDecimal priceE) {
    ZigZagSearchRow.SmaPrice sma = new ZigZagSearchRow.SmaPrice();
    sma.setPriceS(priceS);
    sma.setPriceE(priceE);
    return sma;
  }

  private ZigZagBarDataRow toBarDataRow(FxZigZagBarData e) {
    ZigZagBarDataRow row = new ZigZagBarDataRow();
    row.setBarDateTime(e.getBarDateTime());
    row.setOpenPrice(e.getOpenPrice());
    row.setHighPrice(e.getHighPrice());
    row.setLowPrice(e.getLowPrice());
    row.setClosePrice(e.getClosePrice());
    row.setSma200(e.getSma200());
    row.setSma75(e.getSma75());
    row.setSma20(e.getSma20());
    return row;
  }

}
