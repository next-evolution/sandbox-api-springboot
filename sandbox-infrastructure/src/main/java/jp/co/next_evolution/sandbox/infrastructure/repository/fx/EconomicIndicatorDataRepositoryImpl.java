package jp.co.next_evolution.sandbox.infrastructure.repository.fx;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jp.co.next_evolution.sandbox.domain.model.fx.EconomicIndicatorData;
import jp.co.next_evolution.sandbox.domain.repository.fx.EconomicIndicatorDataRepository;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxEconomicIndicatorData;
import jp.co.next_evolution.sandbox.infrastructure.db.mapper.fx.EconomicIndicatorDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EconomicIndicatorDataRepositoryImpl implements EconomicIndicatorDataRepository {

  private final EconomicIndicatorDataMapper economicIndicatorDataMapper;

  @Override
  public int count(String code, String countryCode, String importance,
      LocalDate publicationBaseDate) {
    return economicIndicatorDataMapper.count(code, countryCode, importance, publicationBaseDate);
  }

  @Override
  public List<EconomicIndicatorData> search(String code, String countryCode, String importance,
      LocalDate publicationBaseDate, int page, int size, boolean sortAsc) {
    return economicIndicatorDataMapper.search(code, countryCode, importance, publicationBaseDate,
        page, size, sortAsc)
        .stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<EconomicIndicatorData> get(String code, String countryCode,
      LocalDateTime publication) {
    return Optional.ofNullable(economicIndicatorDataMapper.get(code, countryCode, publication))
        .map(this::toDomain);
  }

  @Override
  public boolean exists(String code, String countryCode, LocalDateTime publication) {
    return economicIndicatorDataMapper.exists(code, countryCode, publication);
  }

  @Override
  public int add(EconomicIndicatorData data) {
    return economicIndicatorDataMapper.insert(toRecord(data));
  }

  @Override
  public int update(EconomicIndicatorData data, LocalDateTime publication) {
    return economicIndicatorDataMapper.update(toRecord(data), publication);
  }

  @Override
  public int updateCode(EconomicIndicatorData data, String code, String countryCode,
      LocalDateTime publication) {
    return economicIndicatorDataMapper.updateCode(toRecord(data), code, countryCode, publication);
  }

  @Override
  public int deleteLoad() {
    return economicIndicatorDataMapper.deleteLoad();
  }

  @Override
  public int insertLoad(EconomicIndicatorData data) {
    return economicIndicatorDataMapper.insertLoad(toRecord(data));
  }

  @Override
  public List<EconomicIndicatorData> loadDiff() {
    return economicIndicatorDataMapper.loadDiff()
        .stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public int insertFromLoad() {
    return economicIndicatorDataMapper.insertFromLoad();
  }

  private EconomicIndicatorData toDomain(FxEconomicIndicatorData record) {
    return EconomicIndicatorData.builder()
        .code(record.getCode())
        .countryCode(record.getCountryCode())
        .name(record.getName())
        .importance(record.getImportance())
        .description(record.getDescription())
        .publication(record.getPublication())
        .publicationDate(record.getPublicationDate())
        .publicationTime(record.getPublicationTime())
        .dayOfWeek(record.getDayOfWeek())
        .subTitle(record.getSubTitle())
        .resultValue(record.getResultValue())
        .forecastValue(record.getForecastValue())
        .previousValue(record.getPreviousValue())
        .unitOfValue(record.getUnitOfValue())
        .memo(record.getMemo())
        .countryName(record.getCountryName())
        .countryNameShort(record.getCountryNameShort())
        .build();
  }

  private FxEconomicIndicatorData toRecord(EconomicIndicatorData model) {
    return FxEconomicIndicatorData.builder()
        .code(model.getCode())
        .countryCode(model.getCountryCode())
        .publication(model.getPublication())
        .subTitle(model.getSubTitle())
        .resultValue(model.getResultValue())
        .forecastValue(model.getForecastValue())
        .previousValue(model.getPreviousValue())
        .memo(model.getMemo())
        .build();
  }

}
