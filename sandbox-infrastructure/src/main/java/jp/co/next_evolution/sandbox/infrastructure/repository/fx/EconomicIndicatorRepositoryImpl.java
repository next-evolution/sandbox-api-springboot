package jp.co.next_evolution.sandbox.infrastructure.repository.fx;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jp.co.next_evolution.sandbox.domain.model.KeyValue;
import jp.co.next_evolution.sandbox.domain.model.fx.EconomicIndicator;
import jp.co.next_evolution.sandbox.domain.repository.MasterCacheRepository;
import jp.co.next_evolution.sandbox.domain.repository.fx.EconomicIndicatorRepository;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxEconomicIndicator;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.KeyValueRecord;
import jp.co.next_evolution.sandbox.infrastructure.db.mapper.fx.EconomicIndicatorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
@RequiredArgsConstructor
public class EconomicIndicatorRepositoryImpl implements EconomicIndicatorRepository {

  private final EconomicIndicatorMapper economicIndicatorMapper;

  private final MasterCacheRepository masterCacheRepository;

  @Override
  public int count(String countryCode, String importance, String name) {
    return economicIndicatorMapper.count(countryCode, importance, name);
  }

  @Override
  public List<EconomicIndicator> search(int page, int size, String countryCode, String importance,
      String name) {
    return economicIndicatorMapper.search(page, size, countryCode, importance, name)
        .stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<EconomicIndicator> get(Long id) {
    return Optional.ofNullable(economicIndicatorMapper.get(id)).map(this::toDomain);
  }

  @Override
  public boolean exists(String countryCode, String name) {
    return economicIndicatorMapper.exists(countryCode, name);
  }

  @Override
  public int add(EconomicIndicator indicator) {
    return economicIndicatorMapper.insert(toRecord(indicator));
  }

  @Override
  public int update(EconomicIndicator indicator, String countryCode) {
    return economicIndicatorMapper.update(toRecord(indicator), countryCode);
  }

  @Override
  public List<KeyValue> getList(String countryCode) {
    String redisKey = masterCacheRepository.getMasterKey(
        EconomicIndicator.class.getSimpleName(), countryCode);
    List<KeyValue> keyValueList = masterCacheRepository.getList(redisKey);
    if (CollectionUtils.isEmpty(keyValueList)) {
      keyValueList = economicIndicatorMapper.getList(countryCode)
          .stream()
          .map(this::toDomainKeyValue)
          .collect(Collectors.toList());
      masterCacheRepository.put(redisKey, keyValueList);
    }
    return keyValueList;
  }

  @Override
  public List<EconomicIndicator> getEconomicIndicatorList(String countryCode) {
    return economicIndicatorMapper.getEconomicIndicatorList(countryCode)
        .stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void refreshCache(String countryCode) {
    String cacheKey = masterCacheRepository.getMasterKey(
        EconomicIndicator.class.getSimpleName(), countryCode);
    masterCacheRepository.put(cacheKey, economicIndicatorMapper.getList(countryCode)
        .stream()
        .map(this::toDomainKeyValue)
        .collect(Collectors.toList()));
  }

  private EconomicIndicator toDomain(FxEconomicIndicator record) {
    return EconomicIndicator.builder()
        .id(record.getId())
        .countryCode(record.getCountryCode())
        .name(record.getName())
        .importance(record.getImportance())
        .description(record.getDescription())
        .unitOfValue(record.getUnitOfValue())
        .countryName(record.getCountryName())
        .countryNameShort(record.getCountryNameShort())
        .deleted(record.isDeleted())
        .createdAt(record.getCreatedAt())
        .createdBy(record.getCreatedBy())
        .updatedAt(record.getUpdatedAt())
        .updatedBy(record.getUpdatedBy())
        .build();
  }

  private FxEconomicIndicator toRecord(EconomicIndicator model) {
    return FxEconomicIndicator.builder()
        .id(model.getId())
        .countryCode(model.getCountryCode())
        .name(model.getName())
        .importance(model.getImportance())
        .description(model.getDescription())
        .unitOfValue(model.getUnitOfValue())
        .deleted(model.isDeleted())
        .createdAt(model.getCreatedAt())
        .createdBy(model.getCreatedBy())
        .updatedAt(model.getUpdatedAt())
        .updatedBy(model.getUpdatedBy())
        .build();
  }

  private KeyValue toDomainKeyValue(KeyValueRecord record) {
    return new KeyValue(record.key(), record.value());
  }

}
