package jp.co.next_evolution.sandbox.infrastructure.db.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FxZigZagWave {

  private LocalDateTime waveStart;
  private LocalDateTime waveEnd;
  private int wave;
  private BigDecimal resistance;
  private BigDecimal support;
  private LocalDateTime previousWaveStart;
  private int previousWave;
  private String waveMemo;

}
