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
public class FxBarLoadRsi {

  private String symbol;

  private LocalDateTime barDateTime;

  private int rsiRange;

  private BigDecimal rsiValue;

  private BigDecimal rsiMa;

}
