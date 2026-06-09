package jp.co.next_evolution.sandbox.infrastructure.external;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import jp.co.next_evolution.sandbox.domain.model.KeyValue;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;
import jp.co.next_evolution.sandbox.domain.repository.fx.SymbolRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class GaitameRateService {

  private final SymbolRepository symbolRepository;

  private final RestClient fxRateGaitameClient;

  private final RedisTemplate<String, String> redisTemplate;

  public void refreshRate(String contractHm) {

    Set<String> symbolCodes = symbolRepository.getList(SymbolType.Trade)
        .stream()
        .map(KeyValue::key)
        .collect(Collectors.toSet());

    if (CollectionUtils.isEmpty(symbolCodes)) {
      log.warn("シンボルリストが空のため、Gaitameレート取得をスキップします。target={}", contractHm);
      return;
    }

    try {

      GaitameRateDto response = fxRateGaitameClient.get()
                                                   .uri("/v3/info/prices/rate")
                                                   .retrieve()
                                                   .body(GaitameRateDto.class);

      if (response == null || CollectionUtils.isEmpty(response.getData())) {
        log.info("Gaitameレート取得: レスポンスが空。target={}", contractHm);
        return;
      }

      log.info("Gaitameレート取得: target={} size={}", contractHm, response.getData().size());

      for (GaitameRateDto.Rate rate : response.getData()) {
        if (symbolCodes.contains(rate.getPair())) {
          redisTemplate.opsForValue()
                       .set(String.format("price:%s_%s", rate.getPair(), contractHm),
                            rate.getOpen().toPlainString(),
                            60, TimeUnit.MINUTES);
        }
      }

    } catch (Exception e) {
      throw new RuntimeException("Gaitameレート取得エラー。target=" + contractHm, e);
    }

  }

  @Data
  public static class GaitameRateDto {

    private int status;
    private List<Rate> data;

    @Data
    public static class Rate {

      private String pair;
      private BigDecimal open;

    }

  }

}
