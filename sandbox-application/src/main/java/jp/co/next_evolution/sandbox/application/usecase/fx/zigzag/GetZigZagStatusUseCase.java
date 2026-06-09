package jp.co.next_evolution.sandbox.application.usecase.fx.zigzag;

import java.util.List;
import jp.co.next_evolution.sandbox.application.command.fx.ZigZagStatusCommand;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZagStatus;
import jp.co.next_evolution.sandbox.domain.repository.fx.ZigZagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetZigZagStatusUseCase {

  private final ZigZagRepository zigZagRepository;

  public List<ZigZagStatus> execute(ZigZagStatusCommand cmd) {
    return zigZagRepository.getStatusList(cmd.symbolType(), cmd.barType(), cmd.depth());
  }

}
