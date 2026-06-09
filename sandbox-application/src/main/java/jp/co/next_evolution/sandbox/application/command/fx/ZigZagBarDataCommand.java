package jp.co.next_evolution.sandbox.application.command.fx;

import java.time.LocalDateTime;
import jp.co.next_evolution.sandbox.domain.model.fx.BarType;

public record ZigZagBarDataCommand(
    BarType barType,
    String symbol,
    short depth,
    LocalDateTime waveStart,
    int wave
) {

}
