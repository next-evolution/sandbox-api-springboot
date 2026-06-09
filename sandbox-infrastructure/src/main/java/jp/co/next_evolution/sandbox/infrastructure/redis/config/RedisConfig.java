package jp.co.next_evolution.sandbox.infrastructure.redis.config;

import java.util.List;
import jp.co.next_evolution.sandbox.domain.model.KeyValue;
import jp.co.next_evolution.sandbox.domain.model.auth.AuthUser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Value("${spring.redis.host}")
  private String redisHost;

  @Value("${spring.redis.port}")
  private int redisPort;

  @Value("${spring.redis.password}")
  private String redisPassword;

  @Bean(name = {"jedisConnectionFactory"})
  public JedisConnectionFactory jedisConnectionFactory() {
    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setHostName(redisHost);
    redisStandaloneConfiguration.setPort(redisPort);
    redisStandaloneConfiguration.setPassword(redisPassword);
    return new JedisConnectionFactory(redisStandaloneConfiguration);
  }

  @Bean
  public RedisTemplate<String, AuthUser> redisTemplateAuthUser(
      @Qualifier("jedisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, AuthUser> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return redisTemplate;
  }

  @Bean
  public RedisTemplate<String, List<KeyValue>> redisTemplateKeyValueList(
      @Qualifier("jedisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, List<KeyValue>> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return redisTemplate;
  }

  /**
   * Key / Value ともに StringSerializer に統一.
   * Redis CLI で直接確認できるよう可読性を優先.
   */
  @Bean
  public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);

    StringRedisSerializer s = new StringRedisSerializer();
    template.setKeySerializer(s);
    template.setValueSerializer(s);
    template.setHashKeySerializer(s);
    template.setHashValueSerializer(s);

    template.afterPropertiesSet();
    return template;
  }

}

