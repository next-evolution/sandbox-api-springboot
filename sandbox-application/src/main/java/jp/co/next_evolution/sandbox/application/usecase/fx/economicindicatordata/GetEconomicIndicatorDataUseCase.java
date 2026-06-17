package jp.co.next_evolution.sandbox.application.usecase.fx.economicindicatordata;

import java.time.LocalDateTime;
import jp.co.next_evolution.sandbox.application.dto.fx.EconomicIndicatorDataDto;
import jp.co.next_evolution.sandbox.domain.exception.NotFoundException;
import jp.co.next_evolution.sandbox.domain.repository.fx.EconomicIndicatorDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetEconomicIndicatorDataUseCase {

  private final EconomicIndicatorDataRepository economicIndicatorDataRepository;

  public EconomicIndicatorDataDto execute(String code, String countryCode,
      LocalDateTime publication) {

    return economicIndicatorDataRepository.get(code, countryCode, publication)
        .map(EconomicIndicatorDataDto::fromDomain)
        .orElseThrow(() -> new NotFoundException(
            String.format("(%s) %s / %s", countryCode, code, publication)));

  }

}
