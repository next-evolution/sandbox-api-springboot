package jp.co.next_evolution.sandbox.domain.repository.fx;

import java.util.List;
import java.util.Optional;
import jp.co.next_evolution.sandbox.domain.model.KeyValue;
import jp.co.next_evolution.sandbox.domain.model.fx.Symbol;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;

public interface SymbolRepository {

  List<KeyValue> getList(SymbolType symbolType);

  int count(SymbolType symbolType);

  List<Symbol> search(SymbolType symbolType, int page, int size);

  Optional<Symbol> get(String symbol);

  boolean exists(String symbol);

  int add(Symbol symbol);

  int update(Symbol symbol);

  int update(Symbol symbol, String baseSymbol);

  void refreshCache(String symbolType);

}
