package jp.co.next_evolution.sandbox.application.usecase.fx.country;

import jp.co.next_evolution.sandbox.application.dto.fx.CountryDto;
import jp.co.next_evolution.sandbox.domain.exception.DuplicateException;
import jp.co.next_evolution.sandbox.domain.exception.InsertException;
import jp.co.next_evolution.sandbox.domain.repository.fx.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddCountryUseCase {

  private final CountryRepository countryRepository;

  @Transactional
  public void execute(CountryDto countryDto) {

    if (countryRepository.exists(countryDto.code())) {
      throw new DuplicateException(countryDto.code());
    }

    if (countryRepository.add(countryDto.toDomain(this.getClass().getSimpleName())) != 1) {
      throw new InsertException(countryDto.code());
    }

    countryRepository.refreshCache();

  }

}
