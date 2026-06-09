package jp.co.next_evolution.sandbox.application.dto.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import jp.co.next_evolution.sandbox.domain.model.fx.SummerTime;

public record SummerTimeDto(
    @Schema(requiredMode = REQUIRED, description = "対象年") short targetYear,
    @Schema(requiredMode = REQUIRED, description = "適用開始日") LocalDate applyStart,
    @Schema(requiredMode = REQUIRED, description = "適用終了日") LocalDate applyEnd
) {

  public static SummerTimeDto fromDomain(SummerTime entity) {
    return new SummerTimeDto(entity.getTargetYear(), entity.getApplyStart(), entity.getApplyEnd());
  }

  public SummerTime toDomain() {
    return SummerTime.builder()
                     .targetYear(this.targetYear)
                     .applyStart(this.applyStart)
                     .applyEnd(this.applyEnd).build();
  }

}
