package jp.co.next_evolution.sandbox.domain.model.fx.trade;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TradeType {
  L("L"),
  S("S");

  private final String code;

  @JsonCreator
  public static TradeType of(String code) {
    return Arrays.stream(values())
                 .filter(v -> v.code.equals(code))
                 .findFirst()
                 .orElse(null);
  }

  @JsonValue
  public String getCode() {
    return code;
  }
}