package jp.co.next_evolution.sandbox.infrastructure.db.entity;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FxSymbol extends MasterColumns {

  private String symbol;
  private String symbolType;
  private String name;
  private short validScale;
  private BigDecimal targetVolatility;
  private int sortOrder;

}
