package jp.co.next_evolution.sandbox.application.command.fx;

import jp.co.next_evolution.sandbox.domain.model.fx.BarType;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;

public record ZigZagStatusCommand(
    SymbolType symbolType,
    BarType barType,
    int depth
) {

}
