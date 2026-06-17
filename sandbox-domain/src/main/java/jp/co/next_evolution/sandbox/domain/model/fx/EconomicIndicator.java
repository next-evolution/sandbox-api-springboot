package jp.co.next_evolution.sandbox.domain.model.fx;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EconomicIndicator {

  private final String code;
  private final String countryCode;
  private final String name;
  private final String importance;
  private final String description;
  private final String unitOfValue;
  private final String countryName;
  private final String countryNameShort;
  private final boolean deleted;
  private final LocalDateTime createdAt;
  private final String createdBy;
  private final LocalDateTime updatedAt;
  private final String updatedBy;

}
