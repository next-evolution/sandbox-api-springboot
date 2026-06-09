package jp.co.next_evolution.sandbox.infrastructure.db.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FxBarCsvImportCheckDto {

  private int existsCount;

  private int diffCount;

}
