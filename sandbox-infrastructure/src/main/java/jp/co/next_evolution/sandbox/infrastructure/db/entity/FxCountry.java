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
public class FxCountry extends MasterColumns {

  private String code;
  private String name;
  private String currencyCode;
  private String nameEn;
  private String nameShort;
  private short sortOrder;

}
