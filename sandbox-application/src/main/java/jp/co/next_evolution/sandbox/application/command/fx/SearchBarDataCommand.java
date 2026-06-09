package jp.co.next_evolution.sandbox.application.command.fx;

import jp.co.next_evolution.sandbox.domain.model.fx.BarType;

public record SearchBarDataCommand(
    String symbol,
    BarType barType,
    String barDateFrom,
    String barDateTo,
    boolean sortAsc,
    int page,
    int size
) {

}
