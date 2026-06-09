package jp.co.next_evolution.sandbox.application.usecase.fx.zigzag;

import java.util.List;
import jp.co.next_evolution.sandbox.application.command.fx.ZigZagBarDataCommand;
import jp.co.next_evolution.sandbox.domain.model.fx.BarType;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZagBarDataRow;
import jp.co.next_evolution.sandbox.domain.repository.fx.ZigZagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetZigZagBarDataUseCase {

  private final ZigZagRepository zigZagRepository;

  public BarDataResult execute(ZigZagBarDataCommand cmd) {
    List<ZigZagBarDataRow> list = zigZagRepository.getBarDataList(
        cmd.barType(), cmd.symbol(), cmd.depth(), cmd.waveStart());
    return new BarDataResult(cmd.barType(), cmd.symbol(), cmd.depth(), cmd.wave(), list);
  }

  public record BarDataResult(
      BarType barType,
      String symbol,
      short depth,
      int wave,
      List<ZigZagBarDataRow> list
  ) {

  }

}
