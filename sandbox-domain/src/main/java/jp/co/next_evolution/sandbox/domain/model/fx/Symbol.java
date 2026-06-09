package jp.co.next_evolution.sandbox.domain.model.fx;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Symbol {

  private final String symbol;
  private final SymbolType symbolType;
  private final String name;
  private final short validScale;
  private final BigDecimal targetVolatility;
  private final int sortOrder;
  private final boolean deleted;
  private final LocalDateTime createdAt;
  private final String createdBy;
  private final LocalDateTime updatedAt;
  private final String updatedBy;

}

