package jp.co.next_evolution.sandbox.domain.model.fx;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import jp.co.next_evolution.sandbox.domain.exception.DomainValidationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BarType {
  M15("15M", "15m", "15"),
  H1("1H", "1h", "60"),
  H4("4H", "4h", "240"),
  D1("1D", "1d", "1D");

  private final String code;

  private final String suffix;

  private final String keyword;

  public String getTableName() {
    return "fx_bar_" + suffix;
  }

  public LocalDateTime parseBarDateTime(String dateTimeStr) {
    String src = this == D1 ? dateTimeStr + "T00:00:00+09:00" : dateTimeStr;
    return OffsetDateTime.parse(src).toLocalDateTime();
  }

  public static BarType of(String code) {
    return Arrays.stream(values())
                 .filter(v -> v.getCode().equals(code))
                 .findFirst()
                 .orElseThrow(() -> new DomainValidationException("Unknown BarType: " + code));
  }
}
