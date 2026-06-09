package jp.co.next_evolution.sandbox.application.usecase.fx.country;

import jp.co.next_evolution.sandbox.application.dto.fx.CountryDto;
import jp.co.next_evolution.sandbox.domain.exception.DuplicateException;
import jp.co.next_evolution.sandbox.domain.exception.UpdateException;
import jp.co.next_evolution.sandbox.domain.repository.fx.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateCountryUseCase {

  private final CountryRepository countryRepository;

  @Transactional
  public void execute(String baseCode, CountryDto countryDto) {
    if (baseCode.equals(countryDto.code())) {
      if (!countryRepository.exists(baseCode)) {
        throw new UpdateException(baseCode);
      }
      if (countryRepository.update(countryDto.toDomain(this.getClass().getSimpleName())) != 1) {
        throw new UpdateException(baseCode);
      }
    } else {
      if (countryRepository.exists(countryDto.code())) {
        throw new DuplicateException(countryDto.code());
      }
      if (countryRepository.update(countryDto.toDomain(this.getClass().getSimpleName()), baseCode)
          != 1) {
        throw new UpdateException(baseCode);
      }
    }

    countryRepository.refreshCache();
  }

}
