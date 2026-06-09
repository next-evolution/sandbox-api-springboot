package jp.co.next_evolution.sandbox.infrastructure.db.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FxBarLoadSma {

  private String symbol;

  private LocalDateTime barDateTime;

  private int smaRange;

  private BigDecimal smaPrice;

  private boolean smaCross;

}
