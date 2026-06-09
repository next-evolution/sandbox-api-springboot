package jp.co.next_evolution.sandbox.application.usecase.fx.economicindicator;

import java.time.LocalDateTime;
import jp.co.next_evolution.sandbox.application.dto.fx.EconomicIndicatorDto;
import jp.co.next_evolution.sandbox.domain.exception.DuplicateException;
import jp.co.next_evolution.sandbox.domain.exception.InsertException;
import jp.co.next_evolution.sandbox.domain.model.fx.EconomicIndicator;
import jp.co.next_evolution.sandbox.domain.repository.fx.EconomicIndicatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddEconomicIndicatorUseCase {

  private final EconomicIndicatorRepository economicIndicatorRepository;

  @Transactional
  public void execute(EconomicIndicatorDto dto) {

    if (economicIndicatorRepository.exists(dto.countryCode(), dto.name())) {
      throw new DuplicateException(String.format("(%s) %s", dto.countryCode(), dto.name()));
    }

    LocalDateTime now = LocalDateTime.now();
    EconomicIndicator indicator = EconomicIndicator.builder()
        .countryCode(dto.countryCode())
        .name(dto.name())
        .importance(dto.importance())
        .description(dto.description())
        .unitOfValue(dto.unitOfValue())
        .deleted(false)
        .createdAt(now)
        .createdBy(getClass().getSimpleName())
        .updatedAt(now)
        .updatedBy(getClass().getSimpleName())
        .build();

    if (economicIndicatorRepository.add(indicator) != 1) {
      throw new InsertException(String.format("(%s) %s", dto.countryCode(), dto.name()));
    }

    economicIndicatorRepository.refreshCache(dto.countryCode());

  }

}
