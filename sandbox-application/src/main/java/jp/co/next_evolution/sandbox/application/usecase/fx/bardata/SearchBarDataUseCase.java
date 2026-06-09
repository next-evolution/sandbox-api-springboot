package jp.co.next_evolution.sandbox.application.usecase.fx.bardata;

import java.util.Collections;
import java.util.List;
import jp.co.next_evolution.sandbox.application.command.fx.SearchBarDataCommand;
import jp.co.next_evolution.sandbox.domain.model.fx.BarData;
import jp.co.next_evolution.sandbox.domain.repository.fx.BarDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchBarDataUseCase {

  private final BarDataRepository barDataRepository;

  public SearchResult execute(SearchBarDataCommand cmd) {

    int count = barDataRepository.searchCount(
        cmd.symbol(), cmd.barType(), cmd.barDateFrom(), cmd.barDateTo());

    List<BarData> list = count == 0
                         ? Collections.emptyList()
                         : barDataRepository.search(
                             cmd.symbol(), cmd.barType(), cmd.barDateFrom(), cmd.barDateTo(),
                             cmd.sortAsc(), cmd.page(), cmd.size());

    return new SearchResult(count, list, cmd.page(), cmd.size());

  }

  public record SearchResult(int totalCount, List<BarData> barDataList, int page, int size) {

    public int totalPage() {
      return totalCount == 0
             ? 0
             : (totalCount + (size - 1)) / size;
    }

  }

}
