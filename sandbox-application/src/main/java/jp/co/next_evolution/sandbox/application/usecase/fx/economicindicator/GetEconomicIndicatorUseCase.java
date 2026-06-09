package jp.co.next_evolution.sandbox.application.usecase.fx.economicindicator;

import jp.co.next_evolution.sandbox.application.dto.fx.EconomicIndicatorDto;
import jp.co.next_evolution.sandbox.domain.exception.NotFoundException;
import jp.co.next_evolution.sandbox.domain.repository.fx.EconomicIndicatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetEconomicIndicatorUseCase {

  private final EconomicIndicatorRepository economicIndicatorRepository;

  public EconomicIndicatorDto execute(String countryCode, Long id) {

    return economicIndicatorRepository.get(id)
        .filter(e -> e.getCountryCode().equals(countryCode))
        .map(EconomicIndicatorDto::fromDomain)
        .orElseThrow(() -> new NotFoundException(String.format("(%s) %d", countryCode, id)));

  }

}
