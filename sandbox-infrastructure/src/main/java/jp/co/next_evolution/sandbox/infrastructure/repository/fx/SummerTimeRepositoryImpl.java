package jp.co.next_evolution.sandbox.infrastructure.repository.fx;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jp.co.next_evolution.sandbox.domain.model.fx.SummerTime;
import jp.co.next_evolution.sandbox.domain.repository.fx.SummerTimeRepository;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxSummerTime;
import jp.co.next_evolution.sandbox.infrastructure.db.mapper.fx.SummerTimeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SummerTimeRepositoryImpl implements SummerTimeRepository {

  private final SummerTimeMapper summerTimeMapper;

  @Override
  public int count() {
    return summerTimeMapper.count();
  }

  @Override
  public List<SummerTime> search(int page, int size) {
    return summerTimeMapper.search(page, size)
                           .stream()
                           .map(this::toDomain)
                           .collect(Collectors.toList());
  }

  @Override
  public Optional<SummerTime> get(short targetYear) {
    return Optional.ofNullable(summerTimeMapper.get(targetYear)).map(this::toDomain);
  }

  @Override
  public boolean exists(short targetYear) {
    return summerTimeMapper.exists(targetYear);
  }

  @Override
  public int add(SummerTime summerTime) {
    return summerTimeMapper.insert(toEntity(summerTime));
  }

  @Override
  public int update(SummerTime summerTime) {
    return summerTimeMapper.update(toEntity(summerTime));
  }

  @Override public int update(SummerTime summerTime, int targetYear) {
    return summerTimeMapper.updateYear(toEntity(summerTime), targetYear);
  }

  private SummerTime toDomain(FxSummerTime record) {
    return SummerTime.builder()
                     .targetYear(record.getTargetYear())
                     .applyStart(record.getApplyStart())
                     .applyEnd(record.getApplyEnd())
                     .build();
  }

  private FxSummerTime toEntity(SummerTime model) {
    return FxSummerTime.builder()
                       .targetYear(model.getTargetYear())
                       .applyStart(model.getApplyStart())
                       .applyEnd(model.getApplyEnd())
                       .build();
  }

}
