package jp.co.next_evolution.sandbox.domain.model.fx.trade;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EntryType {
  F3("F3"),
  FR("FR"),
  F7("F7"),
  UP("UP"),
  DW("DW");

  private final String code;

  @JsonCreator
  public static EntryType of(String code) {
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