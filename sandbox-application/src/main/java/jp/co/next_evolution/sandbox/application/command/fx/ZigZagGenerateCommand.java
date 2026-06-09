package jp.co.next_evolution.sandbox.application.command.fx;

import java.time.LocalDateTime;
import jp.co.next_evolution.sandbox.domain.model.fx.BarType;

public record ZigZagGenerateCommand(
    String symbol,
    BarType barType,
    int depth,
    LocalDateTime barDateTime,
    int loadSize
) {

}
