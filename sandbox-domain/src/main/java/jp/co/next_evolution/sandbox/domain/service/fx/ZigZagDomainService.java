package jp.co.next_evolution.sandbox.domain.service.fx;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZag;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZagFibonacci;
import org.springframework.stereotype.Component;

@Component
public class ZigZagDomainService {

  // wave・resistance・supportからFibonacci水準を計算する。uptrend = (wave > 0) == (wave % 2 != 0)
  public ZigZagFibonacci calculateFibonacci(
      int wave, BigDecimal resistance, BigDecimal support, int scale) {

    boolean uptrend = (wave > 0) == (wave % 2 != 0);
    BigDecimal f1 = uptrend ? resistance : support;
    BigDecimal f0 = uptrend ? support : resistance;
    BigDecimal range = resistance.subtract(support);

    return ZigZagFibonacci.builder()
        .f1(f1.setScale(scale, RoundingMode.HALF_UP))
        .f7(fibonacciPrice(f1, range, ZigZagFibonacci.F7, uptrend, scale))
        .f6(fibonacciPrice(f1, range, ZigZagFibonacci.F6, uptrend, scale))
        .f5(fibonacciPrice(f1, range, ZigZagFibonacci.F5, uptrend, scale))
        .f3(fibonacciPrice(f1, range, ZigZagFibonacci.F3, uptrend, scale))
        .f2(fibonacciPrice(f1, range, ZigZagFibonacci.F2, uptrend, scale))
        .f0(f0.setScale(scale, RoundingMode.HALF_UP))
        .priceRange(range)
        .build();
  }

  // Fibonacci水準に対するtargetPriceの戻り率（%）を返す
  public BigDecimal getFibonacciRate(ZigZagFibonacci fibonacci, BigDecimal targetPrice) {
    BigDecimal numerator = fibonacci.getF1().compareTo(fibonacci.getF0()) > 0
        ? targetPrice.subtract(fibonacci.getF0())
        : fibonacci.getF0().subtract(targetPrice);
    return numerator.divide(fibonacci.getPriceRange(), 3, RoundingMode.HALF_UP)
                 .multiply(BigDecimal.valueOf(100));
  }

  // previousListの高値・安値・日時を集約して直前ZigZagを返す
  public ZigZag calculatePrevious(List<ZigZag> previousList) {
    ZigZag result = previousList.getFirst();
    for (ZigZag target : previousList) {
      result.setBarDateTime(target.getBarDateTime());
      if (target.getBarHighPrice().compareTo(result.getBarHighPrice()) > 0) {
        result.setBarHighPrice(target.getBarHighPrice());
      }
      if (target.getBarLowPrice().compareTo(result.getBarLowPrice()) < 0) {
        result.setBarLowPrice(target.getBarLowPrice());
      }
    }
    return result;
  }

  // previousListにexistsZigzag=trueのレコードが1件も存在しない場合にtrueを返す
  public boolean previousNotExists(List<ZigZag> previousList) {
    return previousList.stream().noneMatch(ZigZag::isExistsZigzag);
  }

  private BigDecimal fibonacciPrice(
      BigDecimal priceF1, BigDecimal range, BigDecimal ratio, boolean uptrend, int scale) {
    BigDecimal offset = BigDecimal.ONE.subtract(ratio).multiply(range);
    return uptrend
        ? priceF1.subtract(offset).setScale(scale, RoundingMode.HALF_UP)
        : priceF1.add(offset).setScale(scale, RoundingMode.HALF_UP);
  }

}
