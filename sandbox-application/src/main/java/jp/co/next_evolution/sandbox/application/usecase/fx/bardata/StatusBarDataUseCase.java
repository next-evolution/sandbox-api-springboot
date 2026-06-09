package jp.co.next_evolution.sandbox.application.usecase.fx.bardata;

import java.util.List;
import java.util.stream.Collectors;
import jp.co.next_evolution.sandbox.application.command.fx.StatusBarDataCommand;
import jp.co.next_evolution.sandbox.application.dto.fx.BarDataImportResult;
import jp.co.next_evolution.sandbox.domain.model.fx.BarDataStatusDto;
import jp.co.next_evolution.sandbox.domain.repository.fx.BarDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatusBarDataUseCase {

  private final BarDataRepository barDataRepository;

  public List<BarDataImportResult> execute(StatusBarDataCommand cmd) {

    return barDataRepository.statusList(cmd.symbolType(), cmd.barType())
        .stream()
        .map(dto -> BarDataImportResult.builder()
            .symbol(dto.getSymbol())
            .existsCount(dto.getCount())
            .message(buildMessage(dto))
            .build())
        .collect(Collectors.toList());

  }

  private String buildMessage(BarDataStatusDto dto) {
    if (dto.getBarDateTimeMinS() == null && dto.getBarDateTimeMaxS() == null) {
      return null;
    }
    return dto.getBarDateTimeMinS() + "~" + dto.getBarDateTimeMaxS();
  }

}
