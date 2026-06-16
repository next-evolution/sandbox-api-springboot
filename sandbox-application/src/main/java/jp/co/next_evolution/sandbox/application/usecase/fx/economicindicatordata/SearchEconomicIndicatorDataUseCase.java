package jp.co.next_evolution.sandbox.application.usecase.fx.economicindicatordata;

import java.time.LocalDate;
import java.util.List;
import jp.co.next_evolution.sandbox.application.dto.fx.EconomicIndicatorDataDto;
import jp.co.next_evolution.sandbox.domain.repository.fx.EconomicIndicatorDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchEconomicIndicatorDataUseCase {

  private final EconomicIndicatorDataRepository economicIndicatorDataRepository;

  public SearchResult execute(String code, String importance, String countryCode,
      LocalDate publicationBaseDate, int page, int size, boolean sortAsc) {

    int count = economicIndicatorDataRepository.count(code, countryCode, importance,
        publicationBaseDate);
    List<EconomicIndicatorDataDto> list = count > 0
        ? economicIndicatorDataRepository.search(code, countryCode, importance, publicationBaseDate,
            page, size, sortAsc)
                                         .stream()
                                         .map(EconomicIndicatorDataDto::fromDomain)
                                         .toList()
        : List.of();

    return new SearchResult(count, list, page, size);

  }

  public record SearchResult(int totalCount, List<EconomicIndicatorDataDto> list, int page,
      int size) {

    public int totalPage() {
      return totalCount == 0 ? 0 : (totalCount + (size - 1)) / size;
    }

  }

}
