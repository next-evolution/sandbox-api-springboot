package jp.co.next_evolution.sandbox.application.usecase.fx.economicindicatordata;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import jp.co.next_evolution.sandbox.application.dto.fx.EconomicIndicatorDataDto;
import jp.co.next_evolution.sandbox.domain.exception.DuplicateException;
import jp.co.next_evolution.sandbox.domain.exception.NotFoundException;
import jp.co.next_evolution.sandbox.domain.exception.UpdateException;
import jp.co.next_evolution.sandbox.domain.model.fx.EconomicIndicatorData;
import jp.co.next_evolution.sandbox.domain.repository.fx.EconomicIndicatorDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateEconomicIndicatorDataUseCase {

  private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

  private final EconomicIndicatorDataRepository economicIndicatorDataRepository;

  @Transactional
  public void execute(String code, String countryCode, LocalDateTime publication,
      EconomicIndicatorDataDto dto) {

    EconomicIndicatorData existing = economicIndicatorDataRepository.get(code, countryCode,
        publication)
        .orElseThrow(() -> new NotFoundException(publication.format(DTF)));

    boolean isCodeDiff = !code.equals(dto.code()) || !countryCode.equals(dto.countryCode());
    boolean isPublicationDiff = !publication.equals(dto.publication());

    String displayName = isPublicationDiff
        ? String.format("[%s] -> [%s]", publication.format(DTF), dto.publication().format(DTF))
        : String.format("[%s] (%s) %s", publication.format(DTF),
            existing.getCountryNameShort(), existing.getName());

    EconomicIndicatorData toUpdate = EconomicIndicatorData.builder()
        .code(dto.code())
        .countryCode(dto.countryCode())
        .publication(dto.publication())
        .subTitle(dto.subTitle())
        .resultValue(dto.resultValue())
        .forecastValue(dto.forecastValue())
        .previousValue(dto.previousValue())
        .memo(dto.memo())
        .build();

    if (isCodeDiff) {
      if (economicIndicatorDataRepository.exists(
          dto.code(), dto.countryCode(), dto.publication())) {
        throw new DuplicateException(displayName);
      }
      if (economicIndicatorDataRepository.updateCode(toUpdate, code, countryCode,
          publication) != 1) {
        throw new UpdateException(displayName);
      }
    } else {
      if (isPublicationDiff
          && economicIndicatorDataRepository.exists(dto.code(), dto.countryCode(),
              dto.publication())) {
        throw new DuplicateException(displayName);
      }
      if (economicIndicatorDataRepository.update(toUpdate, publication) != 1) {
        throw new UpdateException(displayName);
      }
    }

  }

}
