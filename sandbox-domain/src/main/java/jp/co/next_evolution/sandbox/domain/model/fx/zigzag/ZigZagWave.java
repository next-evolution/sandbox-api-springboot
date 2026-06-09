package jp.co.next_evolution.sandbox.domain.model.fx.zigzag;

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
public class ZigZagWave {

  // insertWaveBulk 呼び出し前にアプリ層が設定する
  private String symbol;
  private int depth;

  private LocalDateTime waveStart;
  private LocalDateTime waveEnd;
  private int wave;
  private BigDecimal resistance;
  private BigDecimal support;
  private LocalDateTime previousWaveStart;
  private int previousWave;
  private String waveMemo;

}
