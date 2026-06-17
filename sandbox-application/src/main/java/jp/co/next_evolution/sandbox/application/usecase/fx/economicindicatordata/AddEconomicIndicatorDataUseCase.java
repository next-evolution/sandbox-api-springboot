package jp.co.next_evolution.sandbox.application.usecase.fx.economicindicatordata;

import jp.co.next_evolution.sandbox.application.dto.fx.EconomicIndicatorDataDto;
import jp.co.next_evolution.sandbox.domain.exception.DuplicateException;
import jp.co.next_evolution.sandbox.domain.exception.InsertException;
import jp.co.next_evolution.sandbox.domain.model.fx.EconomicIndicatorData;
import jp.co.next_evolution.sandbox.domain.repository.fx.EconomicIndicatorDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddEconomicIndicatorDataUseCase {

  private final EconomicIndicatorDataRepository economicIndicatorDataRepository;

  @Transactional
  public void execute(EconomicIndicatorDataDto dto) {

    if (economicIndicatorDataRepository.exists(dto.code(), dto.countryCode(), dto.publication())) {
      throw new DuplicateException(
          String.format("(%s) %s / %s", dto.countryCode(), dto.code(), dto.publication()));
    }

    EconomicIndicatorData data = EconomicIndicatorData.builder()
        .code(dto.code())
        .countryCode(dto.countryCode())
        .publication(dto.publication())
        .subTitle(dto.subTitle())
        .resultValue(dto.resultValue())
        .forecastValue(dto.forecastValue())
        .previousValue(dto.previousValue())
        .memo(dto.memo())
        .build();

    if (economicIndicatorDataRepository.add(data) != 1) {
      throw new InsertException(
          String.format("(%s) %s / %s", dto.countryCode(), dto.code(), dto.publication()));
    }

  }

}
