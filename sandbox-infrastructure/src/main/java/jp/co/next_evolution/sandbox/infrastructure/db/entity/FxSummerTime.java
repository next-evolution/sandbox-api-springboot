package jp.co.next_evolution.sandbox.infrastructure.db.entity;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FxSummerTime {

  private short targetYear;
  private LocalDate applyStart;
  private LocalDate applyEnd;

}
