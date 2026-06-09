package jp.co.next_evolution.sandbox.domain.model.fx;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarDataStatusDto {

  private String symbol;

  private String barDateTimeMinS;

  private String barDateTimeMaxS;

  private int count;

}