package jp.co.next_evolution.sandbox.application.dto.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import jp.co.next_evolution.sandbox.domain.model.fx.Country;

public record CountryDto(
    @Schema(requiredMode = REQUIRED, description = "ISO 3166-1 alpha-2")
    String code,
    @Schema(requiredMode = REQUIRED)
    String name,
    @Schema(requiredMode = REQUIRED)
    String currencyCode,
    @Schema(requiredMode = REQUIRED)
    String nameEn,
    @Schema(requiredMode = REQUIRED)
    String nameShort,
    @Schema(requiredMode = REQUIRED)
    short sortOrder
) {

  public static CountryDto fromDomain(Country entity) {
    return new CountryDto(
        entity.getCode(),
        entity.getName(),
        entity.getCurrencyCode(),
        entity.getNameEn(),
        entity.getNameShort(),
        entity.getSortOrder()
    );
  }

  public Country toDomain(String author) {
    return Country.builder()
                  .code(this.code)
                  .name(this.name)
                  .currencyCode(this.currencyCode)
                  .nameEn(this.nameEn)
                  .nameShort(this.nameShort)
                  .sortOrder(this.sortOrder)
                  .deleted(false)
                  .createdAt(LocalDateTime.now())
                  .createdBy(author)
                  .updatedAt(LocalDateTime.now())
                  .updatedBy(author)
                  .build();
  }

}
