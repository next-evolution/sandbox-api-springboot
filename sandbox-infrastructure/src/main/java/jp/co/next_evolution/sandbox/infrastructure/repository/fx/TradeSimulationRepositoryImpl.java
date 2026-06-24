package jp.co.next_evolution.sandbox.infrastructure.repository.fx;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import jp.co.next_evolution.sandbox.domain.model.fx.trade.PriceInfo;
import jp.co.next_evolution.sandbox.domain.repository.fx.TradeSimulationRepository;
import jp.co.next_evolution.sandbox.infrastructure.db.mapper.fx.TradeSimulationMapper;
import jp.co.next_evolution.sandbox.infrastructure.external.GaitameRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TradeSimulationRepositoryImpl implements TradeSimulationRepository {

  private static final String USDJPY = "USDJPY";
  private static final DateTimeFormatter HM_FORMATTER =
      DateTimeFormatter.ofPattern("yyyyMMddHHmm");

  private final TradeSimulationMapper tradeSimulationMapper;
  private final GaitameRateService gaitameRateService;
  private final RedisTemplate<String, String> redisTemplate;

  @Override
  public PriceInfo getPrice(String symbol, LocalDateTime contractAt) {

    LocalDateTime barDateTime = interval15m(contractAt);
    String contractHm = barDateTime.format(HM_FORMATTER);

    BigDecimal priceUsdJpy = getPriceWithCache(USDJPY, contractHm);
    BigDecimal price = USDJPY.equals(symbol)
                       ? priceUsdJpy
                       : getPriceWithCache(symbol, contractHm);

    return PriceInfo.builder()
                    .symbol(symbol)
                    .barDateTime(barDateTime)
                    .price(price)
                    .priceUsdJpy(priceUsdJpy)
                    .build();

  }

  private BigDecimal getPriceWithCache(String symbol, String contractHm) {

    String redisKey = String.format("price:%s_%s", symbol, contractHm);

    String cached = redisTemplate.opsForValue().get(redisKey);
    if (cached != null) {
      return new BigDecimal(cached);
    }

    BigDecimal price = tradeSimulationMapper.getOpenPrice(symbol, contractHm);

    if (price == null || BigDecimal.ZERO.compareTo(price) == 0) {
      gaitameRateService.refreshRate(contractHm);
      String refreshed = redisTemplate.opsForValue().get(redisKey);
      price = refreshed != null ? new BigDecimal(refreshed) : BigDecimal.ZERO;
    }

    if (price.compareTo(BigDecimal.ZERO) > 0) {
      redisTemplate.opsForValue().set(redisKey, price.toPlainString(), Duration.ofMinutes(60));
    }

    return price;

  }

  private LocalDateTime interval15m(LocalDateTime dt) {
    int minute = dt.getMinute();
    int aligned = minute >= 45 ? 45 : minute >= 30 ? 30 : minute >= 15 ? 15 : 0;
    return dt.toLocalDate().atStartOfDay().plusHours(dt.getHour()).plusMinutes(aligned);
  }

}
