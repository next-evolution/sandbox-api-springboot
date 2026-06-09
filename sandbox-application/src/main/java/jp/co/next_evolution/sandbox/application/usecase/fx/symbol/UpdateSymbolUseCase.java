package jp.co.next_evolution.sandbox.application.usecase.fx.symbol;

import jp.co.next_evolution.sandbox.application.dto.fx.SymbolDto;
import jp.co.next_evolution.sandbox.domain.exception.DuplicateException;
import jp.co.next_evolution.sandbox.domain.exception.UpdateException;
import jp.co.next_evolution.sandbox.domain.repository.fx.SymbolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateSymbolUseCase {

  private final SymbolRepository symbolRepository;

  @Transactional
  public void execute(String baseSymbol, SymbolDto symbolDto) {

    if (baseSymbol.equals(symbolDto.symbol())) {
      if (!symbolRepository.exists(baseSymbol)) {
        throw new UpdateException(baseSymbol);
      }
      if (symbolRepository.update(symbolDto.toDomain(this.getClass().getSimpleName())) != 1) {
        throw new UpdateException(baseSymbol);
      }
    } else {
      if (symbolRepository.exists(symbolDto.symbol())) {
        throw new DuplicateException(symbolDto.symbol());
      }
      if (symbolRepository.update(symbolDto.toDomain(this.getClass().getSimpleName()), baseSymbol)
          != 1) {
        throw new UpdateException(baseSymbol);
      }
    }

    symbolRepository.refreshCache(symbolDto.symbolType());

  }

}
