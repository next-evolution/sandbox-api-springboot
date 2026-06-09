package jp.co.next_evolution.sandbox.domain.model.fx.trade;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeEntry {

  private Long id;
  private String tradeVersion;
  private EntryType entryType;
  private String symbol;
  private TradeType tradeType;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime contractAt;
  private String fibonacciType;
  private String fibonacciBar;
  private BigDecimal contractPrice;
  private BigDecimal lossPrice;
  private int positionRatio;
  private BigDecimal priceJpy;
  private BigDecimal lot;
  private int settlementAmount;
  private int lossPips;
  private BigDecimal settlementRatio;
  private String comment;
  private String imagePath;

  public boolean isDollarCurrency() {
    return StringUtils.hasText(symbol) && !symbol.endsWith("JPY");
  }

  public void applyPrice(PriceInfo priceInfo) {
    if (id == null || id == 0L) {
      id = -1L;
    }
    contractAt = priceInfo.getBarDateTime();
    if (BigDecimal.ZERO.compareTo(contractPrice) == 0) {
      contractPrice = priceInfo.getPrice();
    }
    priceJpy = BigDecimal.ZERO.compareTo(priceJpy) == 0
               ? priceInfo.getPriceUsdJpy()
               : priceJpy;
  }

  public BigDecimal computeDefaultSettlementPrice(BigDecimal plusPips) {
    BigDecimal offset = isDollarCurrency()
                        ? plusPips.divide(new BigDecimal("100"), 5, RoundingMode.HALF_UP)
                        : plusPips;
    return TradeType.L.equals(tradeType)
           ? contractPrice.add(offset)
           : contractPrice.subtract(offset);
  }

  public void applyDefaultLossPrice() {
    if (BigDecimal.ZERO.compareTo(lossPrice) != 0) {
      return;
    }
    BigDecimal range = isDollarCurrency()
                       ? BigDecimal.valueOf(0.003)
                       : BigDecimal.valueOf(0.3);
    lossPrice = TradeType.L.equals(tradeType)
                ? contractPrice.subtract(range)
                : contractPrice.add(range);
  }

}