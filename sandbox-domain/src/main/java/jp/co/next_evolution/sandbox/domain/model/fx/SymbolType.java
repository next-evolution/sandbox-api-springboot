package jp.co.next_evolution.sandbox.domain.model.fx;

import java.util.Arrays;
import jp.co.next_evolution.sandbox.domain.exception.DomainValidationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SymbolType {
  Trade("Trade"),
  Analyze("Analyze");

  private final String code;

  public static SymbolType of(String code) {
    return Arrays.stream(values())
                 .filter(v -> v.getCode().equals(code))
                 .findFirst()
                 .orElseThrow(() -> new DomainValidationException("Unknown SymbolType: " + code));
  }
}
