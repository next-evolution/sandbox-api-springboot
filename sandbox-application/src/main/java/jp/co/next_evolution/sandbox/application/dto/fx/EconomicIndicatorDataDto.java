package jp.co.next_evolution.sandbox.application.dto.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import jp.co.next_evolution.sandbox.domain.model.fx.EconomicIndicatorData;

public record EconomicIndicatorDataDto(
    @Schema(requiredMode = REQUIRED, description = "経済指標コード", example = "CPI_YOY")
    String code,
    @Schema(requiredMode = REQUIRED, description = "国コード", example = "JP")
    String countryCode,
    @Schema(requiredMode = NOT_REQUIRED, description = "指標名")
    String name,
    @Schema(requiredMode = NOT_REQUIRED, description = "重要度")
    String importance,
    @Schema(requiredMode = NOT_REQUIRED, description = "説明")
    String description,
    @Schema(requiredMode = REQUIRED, description = "発表日時", example = "2026-01-23T12:34:56+09:00")
    LocalDateTime publication,
    @Schema(requiredMode = NOT_REQUIRED, description = "発表日")
    String publicationDate,
    @Schema(requiredMode = NOT_REQUIRED, description = "発表時刻")
    String publicationTime,
    @Schema(requiredMode = NOT_REQUIRED, description = "曜日")
    Short dayOfWeek,
    @Schema(requiredMode = NOT_REQUIRED, description = "サブタイトル")
    String subTitle,
    @Schema(requiredMode = REQUIRED, description = "実績値")
    String resultValue,
    @Schema(requiredMode = NOT_REQUIRED, description = "予想値")
    String forecastValue,
    @Schema(requiredMode = NOT_REQUIRED, description = "前回値")
    String previousValue,
    @Schema(requiredMode = NOT_REQUIRED, description = "単位")
    String unitOfValue,
    @Schema(requiredMode = NOT_REQUIRED, description = "メモ")
    String memo,
    @Schema(requiredMode = NOT_REQUIRED, description = "国名")
    String countryName,
    @Schema(requiredMode = NOT_REQUIRED, description = "国名略称")
    String countryNameShort
) {

  public static EconomicIndicatorDataDto fromDomain(EconomicIndicatorData model) {
    return new EconomicIndicatorDataDto(
        model.getCode(),
        model.getCountryCode(),
        model.getName(),
        model.getImportance(),
        model.getDescription(),
        model.getPublication(),
        model.getPublicationDate(),
        model.getPublicationTime(),
        model.getDayOfWeek(),
        model.getSubTitle(),
        model.getResultValue(),
        model.getForecastValue(),
        model.getPreviousValue(),
        model.getUnitOfValue(),
        model.getMemo(),
        model.getCountryName(),
        model.getCountryNameShort()
    );
  }

}
