package jp.co.next_evolution.sandbox.infrastructure.db.entity;

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
public class FxEconomicIndicator extends MasterColumns {

  private Long id;
  private String countryCode;
  private String name;
  private String importance;
  private String description;
  private String unitOfValue;
  private String countryName;
  private String countryNameShort;

}
