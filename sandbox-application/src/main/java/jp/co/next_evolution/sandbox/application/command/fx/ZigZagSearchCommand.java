package jp.co.next_evolution.sandbox.application.command.fx;

import java.time.LocalDateTime;
import jp.co.next_evolution.sandbox.domain.model.fx.BarType;

public record ZigZagSearchCommand(
    BarType barType,
    String symbol,
    int depth,
    LocalDateTime barDateTimeMin,
    LocalDateTime barDateTimeMax,
    int wave,
    int previousWave,
    int nextWave,
    int next2Wave,
    int direction4h200,
    int direction4h75,
    int direction4h20,
    int direction1h200,
    int direction15m200,
    int wave4h,
    int directionTarget4h200,
    int page,
    int size
) {

}
