package jp.co.next_evolution.sandbox.domain.model.fx;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EconomicIndicatorData {

  private final String code;
  private final String countryCode;
  private final String name;
  private final String importance;
  private final String description;
  private final LocalDateTime publication;
  private final String publicationDate;
  private final String publicationTime;
  private final short dayOfWeek;
  private final String subTitle;
  private final String resultValue;
  private final String forecastValue;
  private final String previousValue;
  private final String unitOfValue;
  private final String memo;
  private final String countryName;
  private final String countryNameShort;

}
