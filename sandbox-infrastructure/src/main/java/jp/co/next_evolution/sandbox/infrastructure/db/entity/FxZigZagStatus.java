package jp.co.next_evolution.sandbox.infrastructure.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FxZigZagStatus {

  private String symbol;
  private String barDateTimeMin;
  private String barDateTimeMax;
  private int barCount;
  private String barDateTimeMinZigZag;
  private String barDateTimeMaxZigZag;
  private int zigzagCount;
  private int breakResistanceCount;
  private int breakSupportCount;

}
