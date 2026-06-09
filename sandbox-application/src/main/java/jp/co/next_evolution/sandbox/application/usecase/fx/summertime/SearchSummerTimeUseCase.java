package jp.co.next_evolution.sandbox.application.usecase.fx.summertime;

import java.util.List;
import jp.co.next_evolution.sandbox.application.dto.fx.SummerTimeDto;
import jp.co.next_evolution.sandbox.domain.repository.fx.SummerTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchSummerTimeUseCase {

  private final SummerTimeRepository summerTimeRepository;

  public SearchResult execute(int page, int size) {

    int count = summerTimeRepository.count();
    List<SummerTimeDto> list = summerTimeRepository.search(page, size)
                                                   .stream()
                                                   .map(SummerTimeDto::fromDomain)
                                                   .toList();

    return new SearchResult(count, list, page, size);

  }

  public record SearchResult(int totalCount,
                             List<SummerTimeDto> summerTimeList,
                             int page,
                             int size) {

    public int totalPage() {
      return totalCount == 0 ? 0 : (totalCount + (size - 1)) / size;
    }

  }

}
