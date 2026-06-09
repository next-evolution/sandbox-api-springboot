package jp.co.next_evolution.sandbox.domain.repository;

import java.util.List;
import jp.co.next_evolution.sandbox.domain.model.KeyValue;

public interface MasterCacheRepository {

  List<KeyValue> getList(String cacheKey);

  void put(String cacheKey, List<KeyValue> values);

  String getMasterKey(String simpleName);

  String getMasterKey(String simpleName, String suffix);

  String getStatus();

  void deleteByPattern(String pattern);

}
