package jp.co.next_evolution.sandbox.infrastructure.db.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FxEconomicIndicatorData {

  private String code;
  private String countryCode;
  private String name;
  private String importance;
  private String description;
  private LocalDateTime publication;
  private String publicationDate;
  private String publicationTime;
  private short dayOfWeek;
  private String subTitle;
  private String resultValue;
  private String forecastValue;
  private String previousValue;
  private String unitOfValue;
  private String memo;
  private String countryName;
  private String countryNameShort;

}
