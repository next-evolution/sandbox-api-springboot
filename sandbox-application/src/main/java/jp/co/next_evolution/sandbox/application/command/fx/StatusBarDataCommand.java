package jp.co.next_evolution.sandbox.application.command.fx;

import jp.co.next_evolution.sandbox.domain.model.fx.BarType;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;

public record StatusBarDataCommand(
    SymbolType symbolType,
    BarType barType
) {

}
