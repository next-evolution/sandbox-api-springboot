package jp.co.next_evolution.sandbox.domain.model.fx.zigzag;

import jp.co.next_evolution.sandbox.domain.model.fx.BarType;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZigZagStatus {

  private SymbolType symbolType;
  private BarType barType;
  private String symbol;
  private short depth;

  private String barDateTimeMin;
  private String barDateTimeMax;
  private int barCount;

  private String barDateTimeMinZigZag;
  private String barDateTimeMaxZigZag;
  private int zigzagCount;
  private int breakResistanceCount;
  private int breakSupportCount;

  private String message;

}
