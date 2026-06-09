package jp.co.next_evolution.sandbox.application.command.fx;

import java.io.InputStream;
import jp.co.next_evolution.sandbox.domain.model.fx.BarType;

public record ImportCsvBarDataCommand(
    String symbol,
    BarType barType,
    boolean skipLatest,
    InputStream fileInputStream,
    String originalFileName,
    long fileSize,
    String userSub
) {

}
