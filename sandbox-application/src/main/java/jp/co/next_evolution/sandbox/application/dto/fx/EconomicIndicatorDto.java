package jp.co.next_evolution.sandbox.application.dto.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jp.co.next_evolution.sandbox.domain.model.fx.EconomicIndicator;

public record EconomicIndicatorDto(
    @Schema(requiredMode = NOT_REQUIRED, description = "ID（更新時は必須）")
    Long id,
    @Schema(requiredMode = REQUIRED, description = "国コード", example = "JP")
    String countryCode,
    @Schema(requiredMode = REQUIRED, description = "指標名")
    String name,
    @Schema(requiredMode = REQUIRED, description = "重要度", allowableValues = {"H", "M", "X", "Z"})
    String importance,
    @Schema(requiredMode = NOT_REQUIRED, description = "説明")
    String description,
    @Schema(requiredMode = NOT_REQUIRED, description = "単位")
    String unitOfValue,
    @Schema(requiredMode = NOT_REQUIRED, description = "国名")
    String countryName,
    @Schema(requiredMode = NOT_REQUIRED, description = "国名略称")
    String countryNameShort
) {

  public static EconomicIndicatorDto fromDomain(EconomicIndicator model) {
    return new EconomicIndicatorDto(
        model.getId(),
        model.getCountryCode(),
        model.getName(),
        model.getImportance(),
        model.getDescription(),
        model.getUnitOfValue(),
        model.getCountryName(),
        model.getCountryNameShort()
    );
  }

}
