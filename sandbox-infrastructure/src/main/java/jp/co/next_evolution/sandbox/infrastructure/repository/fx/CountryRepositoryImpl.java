package jp.co.next_evolution.sandbox.infrastructure.repository.fx;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jp.co.next_evolution.sandbox.domain.model.KeyValue;
import jp.co.next_evolution.sandbox.domain.model.fx.Country;
import jp.co.next_evolution.sandbox.domain.repository.MasterCacheRepository;
import jp.co.next_evolution.sandbox.domain.repository.fx.CountryRepository;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxCountry;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.KeyValueRecord;
import jp.co.next_evolution.sandbox.infrastructure.db.mapper.fx.CountryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
@RequiredArgsConstructor
public class CountryRepositoryImpl implements CountryRepository {

  private final CountryMapper countryMapper;

  private final MasterCacheRepository masterCacheRepository;

  @Override
  public List<KeyValue> getList() {
    final String redisKey = masterCacheRepository.getMasterKey(Country.class.getSimpleName());

    List<KeyValue> keyValueList = masterCacheRepository.getList(redisKey);

    if (CollectionUtils.isEmpty(keyValueList)) {
      keyValueList = countryMapper.getList()
                                  .stream()
                                  .map(this::toDomainKeyValue)
                                  .collect(Collectors.toList());
    }
    return keyValueList;
  }

  @Override
  public int count() {
    return countryMapper.count();
  }

  @Override
  public List<Country> search(int page, int size) {
    return countryMapper.search(page, size)
                        .stream()
                        .map(this::toDomain)
                        .collect(Collectors.toList());
  }

  @Override
  public List<Country> countryList() {
    return countryMapper.countryList()
                        .stream()
                        .map(this::toDomain)
                        .collect(Collectors.toList());
  }

  @Override
  public Optional<Country> get(String code) {
    return Optional.ofNullable(countryMapper.get(code)).map(this::toDomain);
  }

  @Override
  public boolean exists(String code) {
    return countryMapper.exists(code);
  }

  @Override
  public int add(Country country) {
    return countryMapper.insert(toRecord(country));
  }

  @Override
  public int update(Country country) {
    return countryMapper.update(toRecord(country));
  }

  @Override
  public int update(Country country, String code) {
    return countryMapper.updateCode(toRecord(country), code);
  }

  @Override
  public void refreshCache() {
    masterCacheRepository.put(masterCacheRepository.getMasterKey(Country.class.getSimpleName()),
                              countryMapper.getList()
                                           .stream()
                                           .map(this::toDomainKeyValue)
                                           .collect(Collectors.toList()));
  }

  private Country toDomain(FxCountry record) {
    return Country.builder()
                  .code(record.getCode())
                  .name(record.getName())
                  .currencyCode(record.getCurrencyCode())
                  .nameEn(record.getNameEn())
                  .nameShort(record.getNameShort())
                  .sortOrder(record.getSortOrder())
                  .deleted(record.isDeleted())
                  .createdAt(record.getCreatedAt())
                  .createdBy(record.getCreatedBy())
                  .updatedAt(record.getUpdatedAt())
                  .updatedBy(record.getUpdatedBy())
                  .build();
  }

  private FxCountry toRecord(Country model) {
    return FxCountry.builder()
                    .code(model.getCode())
                    .name(model.getName())
                    .currencyCode(model.getCurrencyCode())
                    .nameEn(model.getNameEn())
                    .nameShort(model.getNameShort())
                    .sortOrder(model.getSortOrder())
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
