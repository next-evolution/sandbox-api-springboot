package jp.co.next_evolution.sandbox.domain.model.fx.zigzag;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ZigZagCalculation {

  private static final int BACKSTEP_3 = 3;
  private static final LocalDateTime MIN_DATETIME = LocalDateTime.of(1999, 1, 1, 0, 0, 0);

  private String symbol;
  private int depth;
  private LocalDateTime barDateTime;

  private Price resistance;
  private Price resistanceFractal;
  private Price support;
  private Price supportFractal;

  private Price priceHigh;
  private Price priceLow;
  private Price backStepHigh;
  private Price backStepLow;

  private BigDecimal fractalHigh;
  private BigDecimal fractalLow;

  private int wave;
  private boolean waveStart;
  private boolean breakResistance;
  private boolean breakSupport;

  private int waveFractal;
  private boolean breakResistanceFractal;
  private boolean breakSupportFractal;

  private boolean upTrend;
  private int backStepUp;
  private int backStepDown;

  private String message;
  private int id;

  private List<ZigZagWave> waveList;
  private List<ZigZagWave> waveFractalList;
  private List<String> conditionList;

  public ZigZagCalculation(ZigZag zigzag) {
    symbol = zigzag.getSymbol();
    depth = zigzag.getDepth();
    barDateTime = zigzag.getBarDateTime();

    resistance = new Price(zigzag.getResistance(), zigzag.getResistanceBarDateTime());
    resistanceFractal = new Price(
        zigzag.getResistanceFractal(), zigzag.getResistanceFractalBarDateTime());
    support = new Price(zigzag.getSupport(), zigzag.getSupportBarDateTime());
    supportFractal = new Price(zigzag.getSupportFractal(), zigzag.getSupportFractalBarDateTime());
    priceHigh = new Price(zigzag.getPriceHigh(), zigzag.getPriceHighBarDateTime());
    priceLow = new Price(zigzag.getPriceLow(), zigzag.getPriceLowBarDateTime());
    backStepHigh = new Price(zigzag.getBackStepHigh(), zigzag.getBackStepHighBarDateTime());
    backStepLow = new Price(zigzag.getBackStepLow(), zigzag.getBackStepLowBarDateTime());

    fractalHigh = zigzag.getFractalHigh();
    fractalLow = zigzag.getFractalLow();

    waveStart = false;
    wave = zigzag.getWave();
    breakResistance = zigzag.isBreakResistance();
    breakSupport = zigzag.isBreakSupport();

    waveFractal = zigzag.getWave();
    breakResistanceFractal = zigzag.isBreakResistance();
    breakSupportFractal = zigzag.isBreakSupport();

    upTrend = zigzag.isUpTrend();
    backStepUp = zigzag.getBackStepUp();
    backStepDown = zigzag.getBackStepDown();

    waveList = new ArrayList<>();
    waveFractalList = new ArrayList<>();
  }

  public ZigZagCalculation snapshot() {
    return ZigZagCalculation.builder()
        .symbol(symbol)
        .depth(depth)
        .barDateTime(barDateTime)
        .resistance(new Price(resistance))
        .resistanceFractal(new Price(resistanceFractal))
        .support(new Price(support))
        .supportFractal(new Price(supportFractal))
        .priceHigh(new Price(priceHigh))
        .priceLow(new Price(priceLow))
        .fractalHigh(fractalHigh)
        .fractalLow(fractalLow)
        .backStepHigh(new Price(backStepHigh))
        .backStepLow(new Price(backStepLow))
        .waveStart(false)
        .wave(wave)
        .breakResistance(breakResistance)
        .breakSupport(breakSupport)
        .waveFractal(waveFractal)
        .breakResistanceFractal(breakResistanceFractal)
        .breakSupportFractal(breakSupportFractal)
        .upTrend(upTrend)
        .backStepUp(backStepUp)
        .backStepDown(backStepDown)
        .build();
  }

  public ZigZag toEntity() {
    return ZigZag.builder()
        .symbol(symbol)
        .depth(depth)
        .barDateTime(barDateTime)
        .resistance(resistance.getPrice())
        .resistanceFractal(resistanceFractal.getPrice())
        .support(support.getPrice())
        .supportFractal(supportFractal.getPrice())
        .priceHigh(priceHigh.getPrice())
        .priceLow(priceLow.getPrice())
        .backStepHigh(backStepHigh.getPrice())
        .backStepLow(backStepLow.getPrice())
        .fractalHigh(fractalHigh)
        .fractalLow(fractalLow)
        .resistanceBarDateTime(resistance.getBarDateTime())
        .resistanceFractalBarDateTime(resistanceFractal.getBarDateTime())
        .supportBarDateTime(support.getBarDateTime())
        .supportFractalBarDateTime(supportFractal.getBarDateTime())
        .priceHighBarDateTime(priceHigh.getBarDateTime())
        .priceLowBarDateTime(priceLow.getBarDateTime())
        .backStepHighBarDateTime(backStepHigh.getBarDateTime())
        .backStepLowBarDateTime(backStepLow.getBarDateTime())
        .wave(wave)
        .breakResistance(breakResistance)
        .breakSupport(breakSupport)
        .waveFractal(waveFractal)
        .breakResistanceFractal(breakResistanceFractal)
        .breakSupportFractal(breakSupportFractal)
        .upTrend(upTrend)
        .backStepUp(backStepUp)
        .backStepDown(backStepDown)
        .build();
  }

  public void calculate(ZigZagCalculation r, ZigZag target, ZigZag previous) {
    barDateTime = target.getBarDateTime();
    message = "";
    conditionList = new ArrayList<>();

    if (target.getBarLowPrice().compareTo(r.getSupport().getPrice()) < 0
        && target.getBarHighPrice().compareTo(r.getResistance().getPrice()) > 0) {
      if (r.isUpTrend()) {
        breakResistanceSupport(r, target);
      } else {
        breakSupportResistance(r, target);
      }
    } else {
      calculateBackStepUp(r, target, previous);
      calculateBackStepDown(r, target, previous);
      calculateLatest(r, target);
      calculateFractal(r, target);
      calculateBreakResistance(r, target);
      calculateBreakSupport(r, target);
    }
  }

  private void breakResistanceSupport(ZigZagCalculation r, ZigZag target) {
    upTrend = false;
    breakResistance = false;
    breakSupport = true;

    resistance = new Price(target.getBarHighPrice(), target.getBarDateTime());
    resistanceFractal = new Price(target.getBarHighPrice(), target.getBarDateTime());
    priceHigh = new Price(target.getBarHighPrice(), target.getBarDateTime());
    support = new Price(r.getSupportFractal());

    if (r.getWave() > 0) {
      addWave(r.getResistance().getBarDateTime(), resistanceFractal.getBarDateTime(),
          r.getResistance().getPrice(), r.getSupport().getPrice(),
          r.getWave(), "BRS_RV1");
      wave = -1;
    } else {
      if (r.getWave() % 2 == 0) {
        addWave(r.getSupport().getBarDateTime(), resistanceFractal.getBarDateTime(),
            r.getFractalHigh(), r.getSupport().getPrice(),
            r.getWave(), "BRS_DW2");
      } else {
        addWave(r.getResistance().getBarDateTime(), r.getSupport().getBarDateTime(),
            r.getResistance().getPrice(), r.getSupport().getPrice(),
            r.getWave(), "BRS_DW1");
      }
      wave = wave - 1;
    }

    support = new Price(target.getBarLowPrice(), target.getBarDateTime());
    supportFractal = new Price(target.getBarLowPrice(), target.getBarDateTime());
    priceLow = new Price(target.getBarLowPrice(), target.getBarDateTime());
    fractalHigh = target.getBarLowPrice();
    conditionList.add("breakResistanceSupport");
  }

  private void breakSupportResistance(ZigZagCalculation r, ZigZag target) {
    upTrend = true;
    breakResistance = true;
    breakSupport = false;

    support = new Price(target.getBarLowPrice(), target.getBarDateTime());
    supportFractal = new Price(target.getBarLowPrice(), target.getBarDateTime());
    priceLow = new Price(target.getBarLowPrice(), target.getBarDateTime());
    resistance = new Price(r.getResistanceFractal());

    if (r.getWave() < 0) {
      addWave(r.getResistance().getBarDateTime(), supportFractal.getBarDateTime(),
          r.getResistance().getPrice(), support.getPrice(),
          r.getWave(), "BSR_RV1");
      wave = 1;
    } else {
      if (r.getWave() % 2 == 0) {
        addWave(r.getResistance().getBarDateTime(), supportFractal.getBarDateTime(),
            r.getResistance().getPrice(), r.getFractalLow(),
            r.getWave(), "BSR_UP2");
      } else {
        addWave(r.getSupport().getBarDateTime(), r.getResistance().getBarDateTime(),
            r.getResistance().getPrice(), r.getSupport().getPrice(),
            r.getWave(), "BSR_UP1");
      }
      wave = wave + 1;
    }

    resistance = new Price(target.getBarHighPrice(), target.getBarDateTime());
    resistanceFractal = new Price(target.getBarHighPrice(), target.getBarDateTime());
    priceHigh = new Price(target.getBarHighPrice(), target.getBarDateTime());
    fractalLow = target.getBarHighPrice();
    conditionList.add("breakSupportResistance");
  }

  private void calculateBackStepUp(ZigZagCalculation r, ZigZag target, ZigZag previous) {
    updateBackStepHigh(r, target);

    if (r.getBackStepUp() == BACKSTEP_3) {
      commitBackStepUp(r, target.getBarDateTime());
      conditionList.add("backStepUp3");
    }

    if (backStepUp < BACKSTEP_3 && backStepUp > 0 && r.getBackStepUp() > 0) {
      backStepUp++;
      if (target.getBarHighPrice().compareTo(r.getBackStepHigh().getPrice()) > 0
          || (r.isBreakSupport() && backStepUp == 2)) {
        if (target.getBarLowPrice().compareTo(r.getSupport().getPrice()) > 0) {
          commitBackStepUp(r, target.getBarDateTime());
          conditionList.add("backStepUp2");
        }
      }
      if (target.getBarLowPrice().compareTo(previous.getBarLowPrice()) < 0) {
        backStepUp = 0;
        conditionList.add("backStepUpCancel");
      }
    }

    if (target.getBarHighPrice().compareTo(previous.getBarHighPrice()) >= 0
        && r.getBackStepUp() == 0
        && !r.isUpTrend()
        && target.getBarLowPrice().compareTo(r.getSupportFractal().getPrice()) > 0) {
      backStepUp = 1;
      backStepHigh = new Price(target.getBarHighPrice(), target.getBarDateTime());
      conditionList.add("backStepUp1");
    }
  }

  private void calculateBackStepDown(ZigZagCalculation r, ZigZag target, ZigZag previous) {
    updateBackStepLow(r, target);

    if (r.getBackStepDown() == BACKSTEP_3) {
      commitBackStepDown(r, target.getBarDateTime());
      conditionList.add("backStepDown3");
    }

    if (backStepDown < BACKSTEP_3 && backStepDown > 0 && r.getBackStepDown() > 0) {
      backStepDown++;
      if (target.getBarLowPrice().compareTo(r.getBackStepLow().getPrice()) < 0
          || (r.isBreakResistance() && backStepDown == 2)) {
        if (target.getBarHighPrice().compareTo(r.getResistance().getPrice()) < 0) {
          commitBackStepDown(r, target.getBarDateTime());
          conditionList.add("backStepDown2");
        }
      }
      if (target.getBarHighPrice().compareTo(previous.getBarHighPrice()) > 0) {
        backStepDown = 0;
        conditionList.add("backStepDownCancel");
      }
    }

    if (target.getBarLowPrice().compareTo(previous.getBarLowPrice()) <= 0
        && r.getBackStepDown() == 0
        && r.isUpTrend()
        && target.getBarHighPrice().compareTo(r.getResistance().getPrice()) < 0) {
      backStepDown = 1;
      backStepLow = new Price(target.getBarLowPrice(), target.getBarDateTime());
      conditionList.add("backStepDown1");
    }
  }

  private void calculateLatest(ZigZagCalculation r, ZigZag target) {
    if (target.getBarHighPrice().compareTo(r.getPriceHigh().getPrice()) > 0) {
      priceHigh = new Price(target.getBarHighPrice(), target.getBarDateTime());
    }
    if (target.getBarLowPrice().compareTo(r.getPriceLow().getPrice()) < 0) {
      priceLow = new Price(target.getBarLowPrice(), target.getBarDateTime());
    }
    if (target.getBarHighPrice().compareTo(r.getFractalHigh()) > 0) {
      fractalHigh = target.getBarHighPrice();
    }
    if (target.getBarLowPrice().compareTo(r.getFractalLow()) < 0) {
      fractalLow = target.getBarLowPrice();
    }
  }

  private void calculateFractal(ZigZagCalculation r, ZigZag target) {
    if (target.getBarHighPrice().compareTo(r.getResistanceFractal().getPrice()) > 0) {
      resistanceFractal = new Price(target.getBarHighPrice(), target.getBarDateTime());
      priceLow = new Price(resistanceFractal);
      upTrend = true;
      backStepUp = 0;
      conditionList.add("fractalUp");
    }
    if (target.getBarLowPrice().compareTo(r.getSupportFractal().getPrice()) < 0) {
      supportFractal = new Price(target.getBarLowPrice(), target.getBarDateTime());
      priceHigh = new Price(supportFractal);
      upTrend = false;
      backStepDown = 0;
      conditionList.add("fractalDown");
    }
  }

  private void calculateBreakResistance(ZigZagCalculation r, ZigZag target) {
    if (target.getBarHighPrice().compareTo(r.getResistance().getPrice()) > 0) {
      upTrend = true;
      if (target.getBarLowPrice().compareTo(support.getPrice()) >= 0) {
        breakSupport = false;
      }
      resistance = new Price(target.getBarHighPrice(), target.getBarDateTime());
      resistanceFractal = new Price(target.getBarHighPrice(), target.getBarDateTime());
      priceHigh = new Price(target.getBarHighPrice(), target.getBarDateTime());

      if (!r.isBreakResistance()) {
        if (r.isBreakSupport() && !breakSupport) {
          addWave(r.getResistance().getBarDateTime(), support.getBarDateTime(),
              r.getResistance().getPrice(), support.getPrice(),
              r.getWave(), "BR1_RV1x");
        }
        breakResistance = true;
        support = new Price(supportFractal);
        if (r.getWave() < 0) {
          addWave(r.getSupport().getBarDateTime(), supportFractal.getBarDateTime(),
              r.getResistance().getPrice(), r.getSupport().getPrice(),
              r.getWave(), "BR1_RV1");
          wave = 1;
        } else {
          if (r.getWave() % 2 == 0) {
            addWave(r.getResistance().getBarDateTime(), supportFractal.getBarDateTime(),
                r.getResistance().getPrice(), r.getFractalLow(),
                r.getWave(), "BR1_UP2");
          } else {
            addWave(r.getSupport().getBarDateTime(), resistance.getBarDateTime(),
                resistance.getPrice(), r.getSupport().getPrice(),
                r.getWave(), "BR1_UP1");
          }
          wave = wave + 1;
        }
        conditionList.add("breakResistance1");
      }

      if (r.isBreakResistance() && r.isUpTrend()
          && r.getBackStepDown() == 2 && backStepDown > 0) {
        supportFractal = new Price(backStepLow);
        support = new Price(supportFractal);
        if (r.getWave() < 0) {
          addWave(r.getSupport().getBarDateTime(), supportFractal.getBarDateTime(),
              r.getResistance().getPrice(), r.getSupport().getPrice(),
              r.getWave(), "BR2_RV1");
          wave = 1;
        } else {
          if (r.getWave() % 2 == 0) {
            addWave(r.getResistance().getBarDateTime(), supportFractal.getBarDateTime(),
                r.getResistance().getPrice(), r.getFractalLow(),
                r.getWave(), "BR2_UP2");
          } else {
            addWave(r.getSupport().getBarDateTime(), resistance.getBarDateTime(),
                resistance.getPrice(), r.getSupport().getPrice(),
                r.getWave(), "BR2_UP1");
          }
          wave = wave + 1;
        }
        conditionList.add("breakResistance2");
      }
      fractalLow = target.getBarHighPrice();
    }
  }

  private void calculateBreakSupport(ZigZagCalculation r, ZigZag target) {
    if (target.getBarLowPrice().compareTo(r.getSupport().getPrice()) < 0) {
      upTrend = false;
      if (target.getBarHighPrice().compareTo(resistance.getPrice()) <= 0) {
        breakResistance = false;
      }
      support = new Price(target.getBarLowPrice(), target.getBarDateTime());
      supportFractal = new Price(target.getBarLowPrice(), target.getBarDateTime());
      priceLow = new Price(target.getBarLowPrice(), target.getBarDateTime());

      if (!r.isBreakSupport()) {
        if (r.isBreakResistance() && !breakResistance) {
          addWave(r.getSupport().getBarDateTime(), resistance.getBarDateTime(),
              resistance.getPrice(), r.getSupport().getPrice(),
              r.getWave(), "BS1_RV1x");
        }
        breakSupport = true;
        resistance = new Price(resistanceFractal);
        if (r.getWave() > 0) {
          addWave(r.getResistance().getBarDateTime(),
              r.getResistanceFractal().getBarDateTime(),
              r.getResistance().getPrice(),
              r.getSupport().getPrice(),
              r.getWave(), "BS1_RV1");
          wave = -1;
        } else {
          if (r.getWave() % 2 == 0) {
            addWave(r.getSupport().getBarDateTime(),
                r.getResistanceFractal().getBarDateTime(),
                r.getFractalHigh(),
                r.getSupport().getPrice(),
                r.getWave(), "BS1_DW2");
          } else {
            addWave(r.getSupport().getBarDateTime(), resistanceFractal.getBarDateTime(),
                r.getResistance().getPrice(), r.getSupport().getPrice(),
                r.getWave(), "BS1_DW1");
          }
          wave = wave - 1;
        }
        conditionList.add("breakSupport1");
      }

      if (r.isBreakSupport() && !r.isUpTrend()
          && r.getBackStepUp() == 2 && backStepUp > 0) {
        resistanceFractal = new Price(backStepHigh);
        resistance = new Price(resistanceFractal);
        if (r.getWave() > 0) {
          addWave(r.getResistance().getBarDateTime(), resistanceFractal.getBarDateTime(),
              r.getResistance().getPrice(), r.getSupport().getPrice(),
              r.getWave(), "BS2_RV1");
          wave = -1;
        } else {
          if (r.getWave() % 2 == 0) {
            addWave(r.getSupport().getBarDateTime(), resistanceFractal.getBarDateTime(),
                r.getFractalHigh(), r.getSupport().getPrice(),
                r.getWave(), "BS2_DW2");
          } else {
            addWave(r.getResistance().getBarDateTime(), r.getSupport().getBarDateTime(),
                r.getResistance().getPrice(), r.getSupport().getPrice(),
                r.getWave(), "BS2_DW1");
          }
          wave = wave - 1;
        }
        conditionList.add("breakSupport2");
      }
      fractalHigh = target.getBarLowPrice();
    }
  }

  private void updateBackStepHigh(ZigZagCalculation r, ZigZag target) {
    if (r.getBackStepUp() > 0
        && target.getBarHighPrice().compareTo(r.getBackStepHigh().getPrice()) > 0) {
      backStepHigh = new Price(target.getBarHighPrice(), target.getBarDateTime());
    }
  }

  private void updateBackStepLow(ZigZagCalculation r, ZigZag target) {
    if (r.getBackStepDown() > 0
        && target.getBarLowPrice().compareTo(r.getBackStepLow().getPrice()) < 0) {
      backStepLow = new Price(target.getBarLowPrice(), target.getBarDateTime());
    }
  }

  private void commitBackStepUp(ZigZagCalculation r, LocalDateTime targetBarDateTime) {
    backStepUp = 0;
    upTrend = true;

    if (r.isBreakSupport()) {
      breakSupport = false;
      if (r.getWave() % 2 == 0) {
        addWave(r.getResistance().getBarDateTime(), r.getSupport().getBarDateTime(),
            r.getResistance().getPrice(), r.getSupport().getPrice(),
            r.getWave(), "BSU_DW2");
      } else {
        addWave(r.getResistance().getBarDateTime(), r.getSupport().getBarDateTime(),
            r.getResistance().getPrice(), r.getSupport().getPrice(),
            r.getWave(), "BSU_DW1");
      }
      wave--;
    }

    if (r.isBreakResistance()) {
      breakResistance = false;
      if (r.getWave() % 2 == 0) {
        addWave(r.getResistance().getBarDateTime(), supportFractal.getBarDateTime(),
            r.getResistance().getPrice(), r.getFractalLow(),
            r.getWave(), "BSU_UP2");
      } else {
        addWave(r.getSupport().getBarDateTime(), resistance.getBarDateTime(),
            resistance.getPrice(), r.getSupport().getPrice(),
            r.getWave(), "BSU_UP1");
      }
      wave++;
    }

    resistanceFractal = new Price(backStepHigh);
    supportFractal = new Price(r.getPriceLow());
    priceHigh = new Price(backStepHigh);
    priceLow = new Price(backStepHigh);

    if (r.isBreakResistance() && r.isUpTrend()) {
      breakResistance = false;
    }
  }

  private void commitBackStepDown(ZigZagCalculation r, LocalDateTime targetBarDateTime) {
    backStepDown = 0;
    upTrend = false;

    if (r.isBreakResistance()) {
      breakResistance = false;
      if (r.getWave() % 2 == 0) {
        addWave(r.getResistance().getBarDateTime(), supportFractal.getBarDateTime(),
            r.getResistance().getPrice(), r.getFractalLow(),
            r.getWave(), "BSD_UP2");
      } else {
        addWave(r.getSupport().getBarDateTime(), resistance.getBarDateTime(),
            resistance.getPrice(), r.getSupport().getPrice(),
            r.getWave(), "BSD_UP1");
      }
      wave++;
    }

    if (r.isBreakSupport()) {
      breakSupport = false;
      if (r.getWave() % 2 == 0) {
        addWave(r.getSupport().getBarDateTime(), resistanceFractal.getBarDateTime(),
            r.getFractalHigh(), r.getSupport().getPrice(),
            r.getWave(), "BSD_DW2");
      } else {
        addWave(r.getResistance().getBarDateTime(), r.getSupport().getBarDateTime(),
            r.getResistance().getPrice(), r.getSupport().getPrice(),
            r.getWave(), "BSD_DW1");
      }
      wave--;
    }

    resistanceFractal = new Price(r.getPriceHigh());
    supportFractal = new Price(backStepLow);
    priceHigh = new Price(backStepLow);
    priceLow = new Price(backStepLow);

    if (r.isBreakSupport() && !r.isUpTrend()) {
      breakSupport = false;
    }
  }

  private void addWave(LocalDateTime from, LocalDateTime to,
      BigDecimal waveResistance, BigDecimal waveSupport, int waveNo, String memo) {

    if (from.isEqual(to)) {
      return;
    }

    LocalDateTime previousWaveStart = MIN_DATETIME;
    int previousWaveNo = 0;

    if (waveList != null && !waveList.isEmpty()) {
      ZigZagWave last = waveList.getLast();
      if (last.getWaveStart().isEqual(from) && last.getWaveEnd().isEqual(to)) {
        return;
      }
      previousWaveStart = last.getWaveStart();
      previousWaveNo = last.getWave();
    }

    if (waveList == null) {
      waveList = new ArrayList<>();
    }
    waveList.add(ZigZagWave.builder()
        .waveStart(from)
        .waveEnd(to)
        .resistance(waveResistance)
        .support(waveSupport)
        .wave(waveNo)
        .previousWaveStart(previousWaveStart)
        .previousWave(previousWaveNo)
        .waveMemo(memo)
        .build());
  }

  @Data
  public static class Price {

    private LocalDateTime barDateTime;
    private BigDecimal price;

    public Price(Price price) {
      this.price = price.getPrice();
      this.barDateTime = price.getBarDateTime();
    }

    public Price(BigDecimal price, LocalDateTime barDateTime) {
      this.price = price;
      this.barDateTime = barDateTime;
    }

  }

}
