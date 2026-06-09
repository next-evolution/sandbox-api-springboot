package jp.co.next_evolution.sandbox.infrastructure.repository.fx;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jp.co.next_evolution.sandbox.domain.model.KeyValue;
import jp.co.next_evolution.sandbox.domain.model.fx.Symbol;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;
import jp.co.next_evolution.sandbox.domain.repository.MasterCacheRepository;
import jp.co.next_evolution.sandbox.domain.repository.fx.SymbolRepository;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.FxSymbol;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.KeyValueRecord;
import jp.co.next_evolution.sandbox.infrastructure.db.mapper.fx.SymbolMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
@RequiredArgsConstructor
public class SymbolRepositoryImpl implements SymbolRepository {

  private final SymbolMapper symbolMapper;

  private final MasterCacheRepository masterCacheRepository;

  @Override public List<KeyValue> getList(SymbolType symbolType) {

    final String redisKey = masterCacheRepository.getMasterKey(Symbol.class.getSimpleName(),
                                                               symbolType.getCode());

    List<KeyValue> keyValueList = masterCacheRepository.getList(redisKey);

    if (CollectionUtils.isEmpty(keyValueList)) {
      keyValueList = symbolMapper.getList(symbolType.getCode())
                                 .stream()
                                 .map(this::toDomainKeyValue)
                                 .collect(Collectors.toList());
      masterCacheRepository.put(redisKey, keyValueList);
    }

    return keyValueList;

  }

  @Override public int count(SymbolType symbolType) {

    return symbolMapper.searchCount(symbolType.getCode());

  }

  @Override public List<Symbol> search(SymbolType symbolType, int page, int size) {

    return symbolMapper.search(symbolType.getCode(), page, size)
                       .stream()
                       .map(this::toDomain)
                       .collect(Collectors.toList());

  }

  @Override public Optional<Symbol> get(String symbol) {

    return Optional.ofNullable(symbolMapper.get(symbol)).map(this::toDomain);

  }

  @Override public boolean exists(String symbol) {

    return symbolMapper.exists(symbol);

  }

  @Override public int add(Symbol symbol) {

    return symbolMapper.insert(toRecord(symbol));

  }

  @Override public int update(Symbol symbol) {

    return symbolMapper.update(toRecord(symbol));

  }

  @Override public int update(Symbol symbol, String baseSymbol) {

    return symbolMapper.updateSymbol(toRecord(symbol), baseSymbol);

  }

  @Override public void refreshCache(String symbolType) {

    masterCacheRepository.put(
        masterCacheRepository.getMasterKey(Symbol.class.getSimpleName(),
                                           symbolType),
        symbolMapper.getList(symbolType)
                    .stream()
                    .map(this::toDomainKeyValue)
                    .collect(Collectors.toList())
    );

  }

  private Symbol toDomain(FxSymbol record) {
    return Symbol.builder()
                 .symbol(record.getSymbol())
                 .symbolType(SymbolType.of(record.getSymbolType()))
                 .name(record.getName())
                 .validScale(record.getValidScale())
                 .targetVolatility(record.getTargetVolatility())
                 .sortOrder(record.getSortOrder())
                 .deleted(record.isDeleted())
                 .createdAt(record.getCreatedAt())
                 .createdBy(record.getCreatedBy())
                 .updatedAt(record.getUpdatedAt())
                 .updatedBy(record.getUpdatedBy())
                 .build();
  }

  private FxSymbol toRecord(Symbol model) {
    return FxSymbol.builder()
                   .symbol(model.getSymbol())
                   .symbolType(model.getSymbolType().getCode())
                   .name(model.getName())
                   .validScale(model.getValidScale())
                   .targetVolatility(model.getTargetVolatility())
                   .sortOrder(model.getSortOrder())
                   .deleted(model.isDeleted())
                   .createdAt(model.getCreatedAt())
                   .createdBy(model.getCreatedBy())
                   .updatedAt(model.getUpdatedAt())
                   .updatedBy(model.getUpdatedBy())
                   .build();
  }

  private KeyValue toDomainKeyValue(KeyValueRecord record) {
    return new KeyValue(record.key(), record.value());
  }

}
