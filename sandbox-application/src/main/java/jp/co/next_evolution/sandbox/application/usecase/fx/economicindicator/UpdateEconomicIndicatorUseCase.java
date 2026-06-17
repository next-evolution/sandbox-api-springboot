package jp.co.next_evolution.sandbox.application.usecase.fx.economicindicator;

import java.time.LocalDateTime;
import jp.co.next_evolution.sandbox.application.dto.fx.EconomicIndicatorDto;
import jp.co.next_evolution.sandbox.domain.exception.DuplicateException;
import jp.co.next_evolution.sandbox.domain.exception.NotFoundException;
import jp.co.next_evolution.sandbox.domain.exception.UpdateException;
import jp.co.next_evolution.sandbox.domain.model.fx.EconomicIndicator;
import jp.co.next_evolution.sandbox.domain.repository.fx.EconomicIndicatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateEconomicIndicatorUseCase {

  private final EconomicIndicatorRepository economicIndicatorRepository;

  @Transactional
  public void execute(String countryCode, String code, EconomicIndicatorDto dto) {

    economicIndicatorRepository.get(countryCode, code)
        .orElseThrow(() -> new NotFoundException(String.format("(%s) %s", countryCode, code)));

    String newCountryCode = dto.countryCode();
    if (!countryCode.equals(newCountryCode)
        && economicIndicatorRepository.exists(newCountryCode, dto.name())) {
      throw new DuplicateException(String.format("(%s) %s", newCountryCode, dto.name()));
    }

    EconomicIndicator toUpdate = EconomicIndicator.builder()
        .code(code)
        .countryCode(newCountryCode)
        .name(dto.name())
        .importance(dto.importance())
        .description(dto.description())
        .unitOfValue(dto.unitOfValue())
        .updatedAt(LocalDateTime.now())
        .updatedBy(getClass().getSimpleName())
        .build();

    if (economicIndicatorRepository.update(toUpdate, countryCode) != 1) {
      throw new UpdateException(String.format("(%s) %s", countryCode, code));
    }

    economicIndicatorRepository.refreshCache(countryCode);
    if (!countryCode.equals(newCountryCode)) {
      economicIndicatorRepository.refreshCache(newCountryCode);
    }

  }

}
