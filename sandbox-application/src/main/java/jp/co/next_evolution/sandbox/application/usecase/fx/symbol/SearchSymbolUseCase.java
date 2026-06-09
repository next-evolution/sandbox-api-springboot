package jp.co.next_evolution.sandbox.application.usecase.fx.symbol;

import java.util.Collections;
import java.util.List;
import jp.co.next_evolution.sandbox.application.dto.fx.SymbolDto;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;
import jp.co.next_evolution.sandbox.domain.repository.fx.SymbolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchSymbolUseCase {

  private final SymbolRepository symbolRepository;

  public SearchResult execute(SymbolType symbolType, int page, int size) {

    int count = symbolRepository.count(symbolType);
    List<SymbolDto> list = count == 0
                           ? Collections.emptyList()
                           : symbolRepository.search(symbolType, page, size)
                                             .stream()
                                             .map(SymbolDto::fromDomain)
                                             .toList();
    return new SearchResult(count, list, page, size);

  }

  public record SearchResult(int totalCount, List<SymbolDto> symbolList, int page, int size) {

    public int totalPage() {
      return totalCount == 0
             ? 0
             : (totalCount + (size - 1)) / size;
    }

  }

}
