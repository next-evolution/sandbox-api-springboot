package jp.co.next_evolution.sandbox.infrastructure.db.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FxZigZagSearchRow {

  private String symbol;
  private int depth;

  // current wave
  private LocalDateTime curWaveStart;
  private LocalDateTime curWaveEnd;
  private int curWave;
  private BigDecimal curResistance;
  private BigDecimal curSupport;

  // previous wave
  private LocalDateTime prvWaveStart;
  private LocalDateTime prvWaveEnd;
  private int prvWave;
  private BigDecimal prvResistance;
  private BigDecimal prvSupport;

  // next wave
  private LocalDateTime nxtWaveStart;
  private LocalDateTime nxtWaveEnd;
  private int nxtWave;
  private BigDecimal nxtResistance;
  private BigDecimal nxtSupport;

  // next2 wave
  private LocalDateTime nx2WaveStart;
  private LocalDateTime nx2WaveEnd;
  private int nx2Wave;
  private BigDecimal nx2Resistance;
  private BigDecimal nx2Support;

  // current SMA
  private BigDecimal curSma4h200sS;
  private BigDecimal curSma4h200sE;
  private BigDecimal curSma4h75sS;
  private BigDecimal curSma4h75sE;
  private BigDecimal curSma4h20sS;
  private BigDecimal curSma4h20sE;
  private BigDecimal curSma1h200sS;
  private BigDecimal curSma1h200sE;
  private BigDecimal curSma15m200sS;
  private BigDecimal curSma15m200sE;

  // target4h wave
  private LocalDateTime t4hWaveStart;
  private LocalDateTime t4hWaveEnd;
  private int t4hWave;
  private BigDecimal t4hResistance;
  private BigDecimal t4hSupport;

  // target4h SMA
  private BigDecimal t4hSma4h200sS;
  private BigDecimal t4hSma4h200sE;
  private BigDecimal t4hSma4h75sS;
  private BigDecimal t4hSma4h75sE;
  private BigDecimal t4hSma4h20sS;
  private BigDecimal t4hSma4h20sE;
  private BigDecimal t4hSma1h200sS;
  private BigDecimal t4hSma1h200sE;
  private BigDecimal t4hSma15m200sS;
  private BigDecimal t4hSma15m200sE;

  private BigDecimal waveDxy4h;
  private BigDecimal waveDxy1h;

}
