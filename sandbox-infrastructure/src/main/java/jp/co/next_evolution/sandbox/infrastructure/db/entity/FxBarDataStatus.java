package jp.co.next_evolution.sandbox.infrastructure.db.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FxBarDataStatus {

  private String symbol;

  private String barDateTimeMinS;

  private String barDateTimeMaxS;

  private int count;

}
