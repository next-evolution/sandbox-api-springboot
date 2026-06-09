package jp.co.next_evolution.sandbox.application.usecase.fx.symbol;

import jp.co.next_evolution.sandbox.application.dto.fx.SymbolDto;
import jp.co.next_evolution.sandbox.domain.exception.DuplicateException;
import jp.co.next_evolution.sandbox.domain.exception.InsertException;
import jp.co.next_evolution.sandbox.domain.repository.fx.SymbolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddSymbolUseCase {

  private final SymbolRepository symbolRepository;

  @Transactional
  public void execute(SymbolDto symbolDto) {

    if (symbolRepository.exists(symbolDto.symbol())) {
      throw new DuplicateException(symbolDto.symbol());
    }

    var symbol = symbolDto.toDomain(this.getClass().getSimpleName());
    if (symbolRepository.add(symbol) != 1) {
      throw new InsertException(symbol.getSymbol());
    }

    symbolRepository.refreshCache(symbolDto.symbolType());

  }

}
