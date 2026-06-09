package jp.co.next_evolution.sandbox.domain.model.fx;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SummerTime {

  private final short targetYear;
  private final LocalDate applyStart;
  private final LocalDate applyEnd;

}
