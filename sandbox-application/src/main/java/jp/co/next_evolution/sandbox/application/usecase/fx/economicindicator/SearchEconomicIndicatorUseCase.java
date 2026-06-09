package jp.co.next_evolution.sandbox.application.usecase.fx.economicindicator;

import java.util.List;
import jp.co.next_evolution.sandbox.application.dto.fx.EconomicIndicatorDto;
import jp.co.next_evolution.sandbox.domain.repository.fx.EconomicIndicatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchEconomicIndicatorUseCase {

  private final EconomicIndicatorRepository economicIndicatorRepository;

  public SearchResult execute(int page, int size, String countryCode, String importance,
      String name) {

    int count = economicIndicatorRepository.count(countryCode, importance, name);
    List<EconomicIndicatorDto> list = count > 0
        ? economicIndicatorRepository.search(page, size, countryCode, importance, name)
                                     .stream()
                                     .map(EconomicIndicatorDto::fromDomain)
                                     .toList()
        : List.of();

    return new SearchResult(count, list, page, size);

  }

  public record SearchResult(int totalCount, List<EconomicIndicatorDto> list, int page, int size) {

    public int totalPage() {
      return totalCount == 0 ? 0 : (totalCount + (size - 1)) / size;
    }

  }

}
