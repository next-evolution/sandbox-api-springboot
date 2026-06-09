package jp.co.next_evolution.sandbox.application.usecase.fx.country;

import java.util.List;
import jp.co.next_evolution.sandbox.application.dto.fx.CountryDto;
import jp.co.next_evolution.sandbox.domain.repository.fx.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchCountryUseCase {

  private final CountryRepository countryRepository;

  public SearchResult execute(int page, int size) {

    int count = countryRepository.count();
    List<CountryDto> list = countryRepository.search(page, size)
                                             .stream()
                                             .map(CountryDto::fromDomain)
                                             .toList();

    return new SearchResult(count, list, page, size);

  }

  public record SearchResult(int totalCount, List<CountryDto> countryList, int page, int size) {

    public int totalPage() {
      return totalCount == 0
             ? 0
             : (totalCount + (size - 1)) / size;
    }

  }

}
