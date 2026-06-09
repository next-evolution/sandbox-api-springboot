package jp.co.next_evolution.sandbox.infrastructure.redis.repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import jp.co.next_evolution.sandbox.domain.model.KeyValue;
import jp.co.next_evolution.sandbox.domain.repository.MasterCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
@RequiredArgsConstructor
public class RedisMasterCacheRepositoryImpl implements MasterCacheRepository {

  private final RedisTemplate<String, List<KeyValue>> redisTemplateKeyValueList;

  @Override public List<KeyValue> getList(String cacheKey) {
    return redisTemplateKeyValueList.hasKey(cacheKey)
           ? redisTemplateKeyValueList.opsForValue().get(cacheKey)
           : Collections.emptyList();
  }

  @Override public void put(String cacheKey, List<KeyValue> values) {
    redisTemplateKeyValueList.opsForValue().set(cacheKey, values);
  }

  public String getMasterKey(String simpleName) {
    return String.format("master:%s", simpleName);
  }

  public String getMasterKey(String simpleName, String suffix) {
    return String.format("master:%s_%s", simpleName, suffix);
  }

  @Override public void deleteByPattern(String pattern) {
    Optional.ofNullable(redisTemplateKeyValueList.keys(pattern))
            .orElse(Collections.emptySet())
            .forEach(redisTemplateKeyValueList::delete);
  }

  @Override public String getStatus() {

    Set<String> keys = Optional.ofNullable(redisTemplateKeyValueList.keys("master*"))
                               .orElse(Collections.emptySet());

    StringBuilder message = new StringBuilder();

    for (String key : keys.stream().sorted().toList()) {
      List<KeyValue> list = getList(key);

      message.append(key)
             .append("=")
             .append(CollectionUtils.isEmpty(list)
                     ? 0
                     : list.size())
             .append(System.lineSeparator());
    }

    return message.toString();

  }

}
