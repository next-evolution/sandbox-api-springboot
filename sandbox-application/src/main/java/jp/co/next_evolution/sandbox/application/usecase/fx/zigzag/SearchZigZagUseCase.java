package jp.co.next_evolution.sandbox.application.usecase.fx.zigzag;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import jp.co.next_evolution.sandbox.application.command.fx.ZigZagSearchCommand;
import jp.co.next_evolution.sandbox.application.dto.fx.ZigZagSearchItem;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZagSearchRow;
import jp.co.next_evolution.sandbox.domain.repository.fx.ZigZagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchZigZagUseCase {

  private static final BigDecimal F7 = BigDecimal.valueOf(0.786);
  private static final BigDecimal F6 = BigDecimal.valueOf(0.618);
  private static final BigDecimal F5 = BigDecimal.valueOf(0.5);
  private static final BigDecimal F3 = BigDecimal.valueOf(0.382);
  private static final BigDecimal F2 = BigDecimal.valueOf(0.236);

  private final ZigZagRepository zigZagRepository;

  public SearchResult execute(ZigZagSearchCommand cmd) {

    int count = zigZagRepository.searchCount(
        cmd.barType(), cmd.symbol(), cmd.depth(),
        cmd.barDateTimeMin(), cmd.barDateTimeMax(),
        cmd.wave(), cmd.previousWave(), cmd.nextWave(), cmd.next2Wave(), cmd.wave4h());

    List<ZigZagSearchRow> rows = count == 0
        ? Collections.emptyList()
        : zigZagRepository.search(
            cmd.barType(), cmd.symbol(), cmd.depth(),
            cmd.barDateTimeMin(), cmd.barDateTimeMax(),
            cmd.wave(), cmd.previousWave(), cmd.nextWave(), cmd.next2Wave(), cmd.wave4h(),
            cmd.page(), cmd.size());

    List<ZigZagSearchItem> list = toItemList(rows, cmd);

    if (rows.size() != list.size()) {
      count = count - (rows.size() - list.size());
    }

    return new SearchResult(count, list, cmd.page(), cmd.size());

  }

  public record SearchResult(
      int totalCount,
      List<ZigZagSearchItem> list,
      int page,
      int size
  ) {

    public int totalPage() {
      return totalCount == 0 ? 0 : (totalCount + (size - 1)) / size;
    }

  }

  private List<ZigZagSearchItem> toItemList(List<ZigZagSearchRow> rows, ZigZagSearchCommand cmd) {

    List<ZigZagSearchItem> list = new ArrayList<>();

    for (ZigZagSearchRow row : rows) {

      ZigZagSearchItem item = toItem(row);
      int scale = row.getSymbol().endsWith("JPY") ? 3 : 5;

      ZigZagSearchItem.InfoSmaFibonacci current = item.getCurrent();
      if (current.getResistance().compareTo(BigDecimal.ZERO) > 0
          && current.getSupport().compareTo(BigDecimal.ZERO) > 0) {

        ZigZagSearchItem.Fibonacci fib = calcFibonacci(current, scale);
        current.setFibonacci(fib);
        calcSma(current.getSma4h200s(), fib, current.getWave());
        calcSma(current.getSma4h75s(), fib, current.getWave());
        calcSma(current.getSma4h20s(), fib, current.getWave());
        calcSma(current.getSma1h200s(), fib, current.getWave());
        calcSma(current.getSma15m200s(), fib, current.getWave());

        item.setNextRsRate(BigDecimal.ZERO);
        if (current.getWave() > 0
            ? item.getNext2().getWave() > current.getWave()
            : item.getNext2().getWave() < current.getWave()) {

          BigDecimal targetPrice = current.getWave() > 0
              ? item.getNext2().getSupport()
              : item.getNext2().getResistance();
          item.setNextRsRate(fibonacciValue(fib, targetPrice));
        }

        item.setNext2MaxRate(BigDecimal.ZERO);
        if (current.getWave() > 0 && item.getNext2().getWave() > current.getWave()) {
          item.setNext2MaxRate(
              item.getNext2().getResistance().subtract(current.getSupport())
                  .divide(fib.getPriceRange(), 3, RoundingMode.HALF_UP));
        }
        if (current.getWave() < 0 && item.getNext2().getWave() < current.getWave()) {
          item.setNext2MaxRate(
              current.getResistance().subtract(item.getNext2().getSupport())
                  .divide(fib.getPriceRange(), 3, RoundingMode.HALF_UP));
        }
      }

      if (isExclude(cmd.direction4h200(), current.getSma4h200s().getDirection())) {
        continue;
      }
      if (isExclude(cmd.direction4h75(), current.getSma4h75s().getDirection())) {
        continue;
      }
      if (isExclude(cmd.direction4h20(), current.getSma4h20s().getDirection())) {
        continue;
      }
      if (isExclude(cmd.direction1h200(), current.getSma1h200s().getDirection())) {
        continue;
      }
      if (isExclude(cmd.direction15m200(), current.getSma15m200s().getDirection())) {
        continue;
      }

      ZigZagSearchItem.InfoSmaFibonacci target4h = item.getTarget4h();
      if (target4h.getResistance().compareTo(BigDecimal.ZERO) > 0
          && target4h.getSupport().compareTo(BigDecimal.ZERO) > 0) {

        ZigZagSearchItem.Fibonacci fibTarget = calcFibonacci(target4h, scale);
        target4h.setFibonacci(fibTarget);
        calcSma(target4h.getSma4h200s(), fibTarget, target4h.getWave());
        calcSma(target4h.getSma4h75s(), fibTarget, target4h.getWave());
        calcSma(target4h.getSma4h20s(), fibTarget, target4h.getWave());
        calcSma(target4h.getSma1h200s(), fibTarget, target4h.getWave());
        calcSma(target4h.getSma15m200s(), fibTarget, target4h.getWave());

        if (isExclude(cmd.directionTarget4h200(), target4h.getSma4h200s().getDirection())) {
          continue;
        }
      }

      list.add(item);
    }

    return list;
  }

  private ZigZagSearchItem toItem(ZigZagSearchRow row) {
    ZigZagSearchItem item = new ZigZagSearchItem();
    item.setSymbol(row.getSymbol());
    item.setDepth(row.getDepth());
    item.setTarget4h(toInfoSmaFibonacci(row.getTarget4h()));
    item.setCurrent(toInfoSmaFibonacci(row.getCurrent()));
    item.setPrevious(toInfo(row.getPrevious()));
    item.setNext(toInfo(row.getNext()));
    item.setNext2(toInfo(row.getNext2()));
    item.setNextRsRate(BigDecimal.ZERO);
    item.setNext2MaxRate(BigDecimal.ZERO);
    item.setWaveDxy4h(row.getWaveDxy4h());
    item.setWaveDxy1h(row.getWaveDxy1h());
    item.setFractalWaveList(row.getFractalWaveList() == null
        ? Collections.emptyList()
        : row.getFractalWaveList().stream()
            .map(this::toFractalWave)
            .collect(Collectors.toList()));
    return item;
  }

  private ZigZagSearchItem.InfoSmaFibonacci toInfoSmaFibonacci(ZigZagSearchRow.WaveWithSma src) {
    ZigZagSearchItem.InfoSmaFibonacci t = new ZigZagSearchItem.InfoSmaFibonacci();
    if (src == null) {
      t.setResistance(BigDecimal.ZERO);
      t.setSupport(BigDecimal.ZERO);
      t.setSma4h200s(emptySma());
      t.setSma4h75s(emptySma());
      t.setSma4h20s(emptySma());
      t.setSma1h200s(emptySma());
      t.setSma15m200s(emptySma());
      return t;
    }
    t.setWaveStart(src.getWaveStart());
    t.setWaveEnd(src.getWaveEnd());
    t.setWave(src.getWave());
    t.setResistance(src.getResistance() != null ? src.getResistance() : BigDecimal.ZERO);
    t.setSupport(src.getSupport() != null ? src.getSupport() : BigDecimal.ZERO);
    t.setSma4h200s(toSma(src.getSma4h200s()));
    t.setSma4h75s(toSma(src.getSma4h75s()));
    t.setSma4h20s(toSma(src.getSma4h20s()));
    t.setSma1h200s(toSma(src.getSma1h200s()));
    t.setSma15m200s(toSma(src.getSma15m200s()));
    return t;
  }

  private ZigZagSearchItem.Info toInfo(ZigZagSearchRow.WaveInfo src) {
    ZigZagSearchItem.Info t = new ZigZagSearchItem.Info();
    if (src == null) {
      t.setResistance(BigDecimal.ZERO);
      t.setSupport(BigDecimal.ZERO);
      return t;
    }
    t.setWaveStart(src.getWaveStart());
    t.setWaveEnd(src.getWaveEnd());
    t.setWave(src.getWave());
    t.setResistance(src.getResistance() != null ? src.getResistance() : BigDecimal.ZERO);
    t.setSupport(src.getSupport() != null ? src.getSupport() : BigDecimal.ZERO);
    return t;
  }

  private ZigZagSearchItem.Sma toSma(ZigZagSearchRow.SmaPrice src) {
    ZigZagSearchItem.Sma sma = new ZigZagSearchItem.Sma();
    sma.setPriceS(src != null && src.getPriceS() != null ? src.getPriceS() : BigDecimal.ZERO);
    sma.setPriceE(src != null && src.getPriceE() != null ? src.getPriceE() : BigDecimal.ZERO);
    sma.setDeviation(BigDecimal.ZERO);
    sma.setFibonacci(BigDecimal.ZERO);
    sma.setDirection(0);
    sma.setPosition(0);
    return sma;
  }

  private ZigZagSearchItem.Sma emptySma() {
    ZigZagSearchItem.Sma sma = new ZigZagSearchItem.Sma();
    sma.setPriceS(BigDecimal.ZERO);
    sma.setPriceE(BigDecimal.ZERO);
    sma.setDeviation(BigDecimal.ZERO);
    sma.setFibonacci(BigDecimal.ZERO);
    sma.setDirection(0);
    sma.setPosition(0);
    return sma;
  }

  private ZigZagSearchItem.FractalWave toFractalWave(ZigZagSearchRow.FractalWaveInfo src) {
    ZigZagSearchItem.FractalWave fw = new ZigZagSearchItem.FractalWave();
    fw.setWaveStart(src.getWaveStart());
    fw.setWave(src.getWave());
    return fw;
  }

  private ZigZagSearchItem.Fibonacci calcFibonacci(
      ZigZagSearchItem.InfoSmaFibonacci info, int scale) {

    boolean uptrend = (info.getWave() > 0) == (info.getWave() % 2 != 0);
    BigDecimal f1 = uptrend ? info.getResistance() : info.getSupport();
    BigDecimal f0 = uptrend ? info.getSupport() : info.getResistance();
    BigDecimal range = info.getResistance().subtract(info.getSupport());

    ZigZagSearchItem.Fibonacci fib = new ZigZagSearchItem.Fibonacci();
    fib.setF1(f1.setScale(scale, RoundingMode.HALF_UP));
    fib.setF7(fibPrice(f1, range, F7, uptrend, scale));
    fib.setF6(fibPrice(f1, range, F6, uptrend, scale));
    fib.setF5(fibPrice(f1, range, F5, uptrend, scale));
    fib.setF3(fibPrice(f1, range, F3, uptrend, scale));
    fib.setF2(fibPrice(f1, range, F2, uptrend, scale));
    fib.setF0(f0.setScale(scale, RoundingMode.HALF_UP));
    fib.setPriceRange(range);
    return fib;
  }

  private BigDecimal fibPrice(
      BigDecimal f1, BigDecimal range, BigDecimal ratio, boolean uptrend, int scale) {
    BigDecimal fv = BigDecimal.ONE.subtract(ratio).multiply(range);
    return uptrend
        ? f1.subtract(fv).setScale(scale, RoundingMode.HALF_UP)
        : f1.add(fv).setScale(scale, RoundingMode.HALF_UP);
  }

  private BigDecimal fibonacciValue(ZigZagSearchItem.Fibonacci fib, BigDecimal targetPrice) {
    BigDecimal rate = fib.getF1().compareTo(fib.getF0()) > 0
        ? targetPrice.subtract(fib.getF0())
        : fib.getF0().subtract(targetPrice);
    return rate.divide(fib.getPriceRange(), 3, RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(100));
  }

  private void calcSma(ZigZagSearchItem.Sma sma, ZigZagSearchItem.Fibonacci fib, int wave) {
    if (sma.getPriceS().compareTo(BigDecimal.ZERO) > 0
        && sma.getPriceE().compareTo(BigDecimal.ZERO) > 0) {

      sma.setDeviation(sma.getPriceE().subtract(sma.getPriceS())
          .divide(fib.getPriceRange(), 3, RoundingMode.HALF_UP)
          .multiply(BigDecimal.valueOf(100)));

      BigDecimal fibRange = fib.getF1().subtract(sma.getPriceE())
          .multiply(wave > 0 ? BigDecimal.ONE : BigDecimal.valueOf(-1));
      sma.setFibonacci(
          BigDecimal.ONE.subtract(fibRange.divide(fib.getPriceRange(), 3, RoundingMode.HALF_UP))
              .multiply(BigDecimal.valueOf(100))
              .setScale(2, RoundingMode.HALF_UP));

      int deviation = sma.getDeviation().intValue();
      int direction = 0;
      if (deviation > 5) {
        direction = 1;
      }
      if (deviation > 15) {
        direction = 2;
      }
      if (deviation < -5) {
        direction = -1;
      }
      if (deviation < -15) {
        direction = -2;
      }
      sma.setDirection(direction);
      sma.setPosition(sma.getFibonacci().intValue() > 100 ? 1
          : sma.getFibonacci().intValue() < 0 ? -1 : 0);
    }
  }

  private boolean isExclude(int searchDir, int targetDir) {
    if (searchDir == 999) {
      return false;
    }
    if (searchDir == 0) {
      return targetDir != 0;
    }
    if (searchDir > 0) {
      return targetDir < searchDir;
    }
    return targetDir > searchDir;
  }

}
