package jp.co.next_evolution.sandbox.api.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReturnCode {
  Ok(0),
  Warn(1),
  Error(2),
  Fatal(Integer.MAX_VALUE);

  private final int value;

  @JsonCreator
  public static ReturnCode get(int value) {
    return Arrays.stream(ReturnCode.values())
                 .filter(v -> v.value == value)
                 .findFirst()
                 .orElse(null);
  }

  @JsonValue
  public int getValue() {
    return value;
  }

}
