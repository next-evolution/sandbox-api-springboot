package jp.co.next_evolution.sandbox.application.usecase.fx.symbol;

import jp.co.next_evolution.sandbox.application.dto.fx.SymbolDto;
import jp.co.next_evolution.sandbox.domain.exception.NotFoundException;
import jp.co.next_evolution.sandbox.domain.repository.fx.SymbolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetSymbolUseCase {

  private final SymbolRepository symbolRepository;

  public SymbolDto get(String symbol) {

    return symbolRepository.get(symbol)
                           .map(SymbolDto::fromDomain)
                           .orElseThrow(() -> new NotFoundException(symbol));

  }

}
