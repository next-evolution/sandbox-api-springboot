package jp.co.next_evolution.sandbox.domain.model.fx;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Country {

  private final String code;
  private final String name;
  private final String currencyCode;
  private final String nameEn;
  private final String nameShort;
  private final short sortOrder;
  private final boolean deleted;
  private final LocalDateTime createdAt;
  private final String createdBy;
  private final LocalDateTime updatedAt;
  private final String updatedBy;

}
