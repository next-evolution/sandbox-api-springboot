package jp.co.next_evolution.sandbox.infrastructure.db.mapper.fx;

import java.util.List;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxSymbol;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.KeyValueRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SymbolMapper {

  List<KeyValueRecord> getList(String symbolType);

  int searchCount(String symbolType);

  List<FxSymbol> search(String symbolType, int page, int size);

  FxSymbol get(String symbol);

  boolean exists(String symbol);

  int insert(FxSymbol symbol);

  int update(FxSymbol symbol);

  int updateSymbol(FxSymbol symbol, String baseSymbol);

}
