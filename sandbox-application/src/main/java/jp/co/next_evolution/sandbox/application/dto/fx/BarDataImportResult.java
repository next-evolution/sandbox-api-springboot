package jp.co.next_evolution.sandbox.application.dto.fx;

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
public class BarDataImportResult extends FileImportResult {

  private String symbol;

  private String barDateTime;

  private int existsCount;

  private int insertCount;

  private int differenceCount;

}
