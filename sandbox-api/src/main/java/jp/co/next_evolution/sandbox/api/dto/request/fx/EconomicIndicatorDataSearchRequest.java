package jp.co.next_evolution.sandbox.api.dto.request.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import jp.co.next_evolution.sandbox.api.dto.request.ApiSearchRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EconomicIndicatorDataSearchRequest extends ApiSearchRequest {

  @Schema(requiredMode = NOT_REQUIRED, description = "国コード")
  private String countryCode;

  @Schema(requiredMode = NOT_REQUIRED, description = "重要度")
  private String importance;

  @Schema(requiredMode = NOT_REQUIRED, description = "経済指標コード")
  private String code;

  @Schema(requiredMode = NOT_REQUIRED, description = "昇順ソート")
  private boolean sortAsc;

  @Schema(requiredMode = NOT_REQUIRED, description = "発表日(基準日)", example = "yyyy-MM-dd")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate publicationBaseDate;

}
