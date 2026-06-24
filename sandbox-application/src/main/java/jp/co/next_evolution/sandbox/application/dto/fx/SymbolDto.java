package jp.co.next_evolution.sandbox.application.dto.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jp.co.next_evolution.sandbox.domain.model.fx.Symbol;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;

public record SymbolDto(
    @Schema(requiredMode = REQUIRED, description = "symbol", example = "USDJPY")
    String symbol,
    @Schema(requiredMode = REQUIRED, description = "symbol", example = "Trade|Analyze")
    String symbolType,
    @Schema(requiredMode = REQUIRED, description = "name", example = "ドル円")
    String name,
    @Schema(requiredMode = REQUIRED, description = "scale", example = "3")
    short validScale,
    @Schema(requiredMode = REQUIRED, description = "targetVolatility", example = "0.005")
    BigDecimal targetVolatility,
    @Schema(requiredMode = REQUIRED, description = "sortOrder", example = "100")
    int sortOrder
) {

  public static SymbolDto fromDomain(Symbol entity) {
    return new SymbolDto(
        entity.getSymbol(),
        entity.getSymbolType().getCode(),
        entity.getName(),
        entity.getValidScale(),
        entity.getTargetVolatility(),
        entity.getSortOrder()
    );
  }

  public Symbol toDomain(String author) {
    return Symbol.builder()
                 .symbol(this.symbol)
                 .symbolType(SymbolType.of(this.symbolType))
                 .name(this.name)
                 .validScale(this.validScale)
                 .targetVolatility(this.targetVolatility)
                 .sortOrder(this.sortOrder)
                 .deleted(false)
                 .createdAt(LocalDateTime.now())
                 .createdBy(author)
                 .updatedAt(LocalDateTime.now())
                 .updatedBy(author)
                 .build();
  }

}
