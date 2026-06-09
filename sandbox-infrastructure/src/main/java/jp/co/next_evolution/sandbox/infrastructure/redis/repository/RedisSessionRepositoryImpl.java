package jp.co.next_evolution.sandbox.infrastructure.redis.repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import jp.co.next_evolution.sandbox.domain.model.auth.AuthUser;
import jp.co.next_evolution.sandbox.domain.repository.auth.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisSessionRepositoryImpl implements SessionRepository {

  @Value("${sandbox.session-ttl}")
  private int sessionTtl;

  private static final String KEY_PREFIX = "session:";

  private final RedisTemplate<String, AuthUser> redisTemplateAuthUser;

  @Override
  public void save(AuthUser authUser) {
    String key = toKey(authUser.sub());

    redisTemplateAuthUser.opsForValue().set(key, authUser);

    // TTL 設定（ログインのたびにリセット）
    redisTemplateAuthUser.expire(key, sessionTtl, TimeUnit.SECONDS);
  }

  @Override
  public Optional<AuthUser> findBySub(String sub) {
    String key = toKey(sub);

    AuthUser authUser = redisTemplateAuthUser.opsForValue().get(key);

    if (authUser == null) {
      return Optional.empty();
    }

    return Optional.of(authUser);
  }

  @Override
  public void deleteBySub(String sub) {
    redisTemplateAuthUser.delete(toKey(sub));
  }

  @Override public void update(AuthUser authUser) {
    String key = toKey(authUser.sub());
    if (redisTemplateAuthUser.hasKey(toKey(key))) {
      // TTL 設定（ログインのたびにリセット）
      redisTemplateAuthUser.expire(key, sessionTtl, TimeUnit.SECONDS);
    }
  }

  private String toKey(String sub) {
    return KEY_PREFIX + sub;
  }

}
