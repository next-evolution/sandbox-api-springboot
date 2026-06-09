package jp.co.next_evolution.sandbox.application.usecase.fx.country;

import jp.co.next_evolution.sandbox.application.dto.fx.CountryDto;
import jp.co.next_evolution.sandbox.domain.exception.NotFoundException;
import jp.co.next_evolution.sandbox.domain.repository.fx.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetCountryUseCase {

  private final CountryRepository countryRepository;

  public CountryDto get(String code) {

    return countryRepository.get(code)
                            .map(CountryDto::fromDomain)
                            .orElseThrow(() -> new NotFoundException(code));
  }
  
}
