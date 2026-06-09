package jp.co.next_evolution.sandbox.api.dto.response.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import jp.co.next_evolution.sandbox.api.dto.response.ApiSearchResponse;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ZigZagStatusResponse extends ApiSearchResponse {

  @Schema(requiredMode = REQUIRED)
  private List<ZigZagStatusItem> list;

}
