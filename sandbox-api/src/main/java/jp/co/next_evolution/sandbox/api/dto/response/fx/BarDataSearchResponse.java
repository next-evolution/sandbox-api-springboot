package jp.co.next_evolution.sandbox.api.dto.response.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import jp.co.next_evolution.sandbox.api.dto.response.ApiSearchResponse;
import jp.co.next_evolution.sandbox.domain.model.fx.BarData;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class BarDataSearchResponse extends ApiSearchResponse {

  @Schema(description = "バーデータリスト", requiredMode = REQUIRED)
  private List<BarData> list;

}
