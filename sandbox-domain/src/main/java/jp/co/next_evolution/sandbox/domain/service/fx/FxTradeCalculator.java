package jp.co.next_evolution.sandbox.domain.service.fx;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import jp.co.next_evolution.sandbox.domain.model.fx.trade.TradeEntry;
import jp.co.next_evolution.sandbox.domain.model.fx.trade.TradePosition;
import jp.co.next_evolution.sandbox.domain.model.fx.trade.TradeType;
import org.springframework.stereotype.Component;

@Component
public class FxTradeCalculator {

  private static final BigDecimal UNIT_VALUE = new BigDecimal("100000");
  private static final BigDecimal HUNDRED = new BigDecimal("100");
  private static final BigDecimal THOUSAND = new BigDecimal("1000");
  private static final BigDecimal UNIT_VALUE_HUNDRED = UNIT_VALUE.multiply(HUNDRED);

  private static final BigDecimal DEFAULT_RISK_AMOUNT = new BigDecimal("10000");
  private static final BigDecimal DEFAULT_FIRST_LOT_RATIO = new BigDecimal("0.3");

  // 有効数字2桁（旧コードに合わせて MC3 も precision=2 とする）
  private static final MathContext MC2 = new MathContext(2, RoundingMode.HALF_UP);
  private static final MathContext MC3 = new MathContext(2, RoundingMode.HALF_UP);

  public void calculate(
      TradeEntry entry,
      List<TradePosition> positionList,
      BigDecimal riskAmount,
      BigDecimal firstLotRatio) {

    BigDecimal effectiveRisk = BigDecimal.ZERO.compareTo(riskAmount) == 0
                               ? DEFAULT_RISK_AMOUNT
                               : riskAmount;

    BigDecimal effectiveRatio = BigDecimal.ZERO.compareTo(firstLotRatio) == 0
                                ? DEFAULT_FIRST_LOT_RATIO
                                : firstLotRatio.divide(HUNDRED, 2, RoundingMode.HALF_UP);

    calculateLot(entry, positionList, effectiveRatio, effectiveRisk);

    if (entry.getLot().compareTo(BigDecimal.ZERO) > 0) {
      calculateAmount(entry, positionList);
      calculateEntrySettlementRatio(entry);
    }

  }

  public BigDecimal priceRange(BigDecimal base, BigDecimal value) {
    return base.compareTo(value) > 0 ? base.subtract(value) : value.subtract(base);
  }

  public int pips(BigDecimal base, BigDecimal value, boolean isDollarCurrency) {
    return isDollarCurrency
           ? priceRange(base, value).multiply(THOUSAND).multiply(HUNDRED).intValue()
           : priceRange(base, value).multiply(THOUSAND).intValue();
  }

  public int profitAmount(TradeEntry entry, TradePosition position) {
    boolean isProfit = TradeType.L.equals(entry.getTradeType())
                       ? position.getSettlementPrice()
                                 .compareTo(entry.getContractPrice()) > 0
                       : position.getSettlementPrice()
                                 .compareTo(entry.getContractPrice()) < 0;

    BigDecimal pip = priceRange(entry.getContractPrice(), position.getSettlementPrice());
    BigDecimal amountJpy = pip.multiply(position.getLot())
                              .multiply(entry.isDollarCurrency()
                                        ? UNIT_VALUE_HUNDRED
                                        : UNIT_VALUE);

    int amount = entry.isDollarCurrency()
                 ? amountJpy.multiply(
                     entry.getPriceJpy().divide(HUNDRED, 2, RoundingMode.HALF_UP)).intValue()
                 : amountJpy.intValue();

    return isProfit ? amount : amount * -1;
  }

  public int lossAmount(TradeEntry entry, BigDecimal lot) {
    BigDecimal pip = priceRange(entry.getContractPrice(), entry.getLossPrice());
    BigDecimal amountJpy = pip.multiply(lot)
                              .multiply(entry.isDollarCurrency()
                                        ? UNIT_VALUE_HUNDRED
                                        : UNIT_VALUE);
    return entry.isDollarCurrency()
           ? amountJpy.multiply(
               entry.getPriceJpy().divide(HUNDRED, 2, RoundingMode.HALF_UP)).intValue()
           : amountJpy.intValue();
  }

  public BigDecimal totalLot(TradeEntry entry, BigDecimal riskAmount) {
    BigDecimal lossValue = priceRange(entry.getContractPrice(), entry.getLossPrice());
    int scale = entry.isDollarCurrency() ? 3 : 2;
    BigDecimal lotTemp = riskAmount.divide(lossValue, scale, RoundingMode.HALF_UP)
                                   .divide(UNIT_VALUE, scale, RoundingMode.HALF_UP);
    return entry.isDollarCurrency()
           ? lotTemp.divide(entry.getPriceJpy(), scale - 1, RoundingMode.HALF_UP)
           : lotTemp;
  }

  private void calculateLot(
      TradeEntry entry,
      List<TradePosition> positionList,
      BigDecimal firstLotRatio,
      BigDecimal riskAmount) {

    entry.setLot(totalLot(entry, riskAmount));

    if (positionList.size() == 1) {
      positionList.getFirst().setLot(entry.getLot());
    } else if (positionList.size() > 1) {
      positionList.getFirst().setLot(
          entry.getLot()
               .multiply(firstLotRatio, entry.isDollarCurrency() ? MC3 : MC2));

      if (positionList.size() == 2) {
        positionList.get(1).setLot(
            entry.getLot().subtract(positionList.getFirst().getLot()));
      } else if (positionList.size() == 3) {
        positionList.get(1).setLot(
            entry.getLot()
                 .subtract(positionList.getFirst().getLot())
                 .divide(BigDecimal.TWO, 2, RoundingMode.HALF_UP));

        positionList.get(2).setLot(
            entry.getLot()
                 .subtract(positionList.getFirst().getLot())
                 .subtract(positionList.get(1).getLot()));
      }
    }

  }

  private void calculateAmount(TradeEntry entry, List<TradePosition> positionList) {

    int profitAmountTotal = 0;

    for (TradePosition position : positionList) {
      position.setProfitAmount(profitAmount(entry, position));
      position.setLossAmount(lossAmount(entry, position.getLot()));
      profitAmountTotal += position.getProfitAmount();

      position.setSettlementRatio(
          BigDecimal.valueOf((double) position.getProfitAmount() / position.getLossAmount())
                    .setScale(2, RoundingMode.HALF_UP));

      position.setSettlementPips(
          pips(position.getSettlementPrice(), entry.getContractPrice(),
               entry.isDollarCurrency()));
    }

    entry.setSettlementAmount(profitAmountTotal);
    entry.setLossPips(
        pips(entry.getContractPrice(), entry.getLossPrice(), entry.isDollarCurrency()));

  }

  private void calculateEntrySettlementRatio(TradeEntry entry) {
    entry.setSettlementRatio(
        BigDecimal.valueOf((double) entry.getSettlementAmount()
                           / lossAmount(entry, entry.getLot()))
                  .setScale(2, RoundingMode.HALF_UP));
  }

}
