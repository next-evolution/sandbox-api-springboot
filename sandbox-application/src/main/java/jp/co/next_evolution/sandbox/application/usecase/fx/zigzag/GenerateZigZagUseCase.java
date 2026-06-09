package jp.co.next_evolution.sandbox.application.usecase.fx.zigzag;

import java.util.ArrayList;
import java.util.List;
import jp.co.next_evolution.sandbox.application.command.fx.ZigZagGenerateCommand;
import jp.co.next_evolution.sandbox.domain.exception.InsertException;
import jp.co.next_evolution.sandbox.domain.exception.UpdateException;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZag;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZagCalculation;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZagStatus;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZagWave;
import jp.co.next_evolution.sandbox.domain.repository.fx.ZigZagRepository;
import jp.co.next_evolution.sandbox.domain.service.fx.ZigZagDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateZigZagUseCase {

  private final ZigZagRepository zigZagRepository;

  private final ZigZagDomainService zigZagDomainService;

  @Transactional
  public GenerateResult execute(ZigZagGenerateCommand cmd) {
    log.debug("{}", cmd);

    ZigZagStatus status = zigZagRepository.getStatus(cmd.barType(), cmd.symbol(), cmd.depth());

    int maxLimit = zigZagRepository.targetBarCount(cmd.barType(), cmd.symbol(), cmd.barDateTime());
    if (maxLimit > cmd.loadSize()) {
      maxLimit = cmd.loadSize();
    }

    List<ZigZag> previousList = zigZagRepository.previousList(
        cmd.barType(), cmd.symbol(), cmd.depth(), cmd.barDateTime(), cmd.depth() - 1);

    if (previousList.isEmpty() || zigZagDomainService.previousNotExists(previousList)) {
      String msg = previousList.isEmpty() ? "previousList empty." : "previous not exists.";
      status.setMessage(msg);
      return new GenerateResult(status, true);
    }

    List<ZigZag> targetList = zigZagRepository.targetList(
        cmd.barType(), cmd.symbol(), cmd.depth(), cmd.barDateTime(), maxLimit);

    if (targetList.isEmpty()) {
      status.setMessage("targetList empty.");
      return new GenerateResult(status, true);
    }

    status.setZigzagCount(targetList.size());
    log.debug("[{}] previous={} target={}", cmd.symbol(), previousList.size(), targetList.size());

    var zigzag = new ZigZagCalculation(previousList.getLast());
    zigzag.setId(0);

    for (ZigZag target : targetList) {
      ZigZag previous = zigZagDomainService.calculatePrevious(previousList);
      ZigZagCalculation snapshot = zigzag.snapshot();
      zigzag.setId(zigzag.getId() + 1);

      zigzag.calculate(snapshot, target, previous);

      ZigZag entity = zigzag.toEntity();
      entity.setSymbol(cmd.symbol());
      entity.setDepth(cmd.depth());

      if (target.isExistsZigzag()) {
        if (zigZagRepository.update(cmd.barType(), entity) != 1) {
          throw new UpdateException(cmd.symbol() + ":" + zigzag.getBarDateTime());
        }
      } else {
        if (zigZagRepository.insert(cmd.barType(), entity) != 1) {
          throw new InsertException(cmd.symbol() + ":" + zigzag.getBarDateTime());
        }
      }

      if (previousList.size() == cmd.depth() - 1) {
        previousList.removeFirst();
      }
      previousList.add(target);
    }

    log.debug("[{}] insert|update={}", cmd.symbol(), targetList.size());

    processWaveList(cmd, zigzag);

    ZigZagStatus resultStatus = zigZagRepository.getStatus(
        cmd.barType(), cmd.symbol(), cmd.depth());
    resultStatus.setMessage(String.format("target=%,d wave=%,d",
                                          targetList.size(),
                                          zigzag.getWaveList() == null ? 0 : zigzag.getWaveList()
                                                                                   .size()));

    return new GenerateResult(resultStatus, false);

  }

  private void processWaveList(ZigZagGenerateCommand cmd, ZigZagCalculation zigzag) {

    List<ZigZagWave> waveList = zigzag.getWaveList();
    if (waveList == null || waveList.isEmpty()) {
      return;
    }

    zigZagRepository.deleteWave(cmd.barType(), cmd.symbol(), cmd.depth(), cmd.barDateTime());
    log.debug("deleteWave >= {}", cmd.barDateTime());

    ZigZagWave lastWave = zigZagRepository.getLastWave(cmd.barType(), cmd.symbol(), cmd.depth());
    log.debug("lastWave = {}", lastWave);

    if (lastWave != null) {
      waveList.getFirst().setPreviousWaveStart(lastWave.getWaveStart());
      waveList.getFirst().setPreviousWave(lastWave.getWave());
    }

    // 前後のwaveが連続していないものを除外する
    List<ZigZagWave> enableList = new ArrayList<>();
    ZigZagWave prev = null;
    for (ZigZagWave wave : waveList) {
      if (prev != null && wave.getWaveStart().isEqual(prev.getWaveEnd())) {
        enableList.add(wave);
      }
      prev = wave;
    }

    if (!enableList.isEmpty()) {
      try {
        zigZagRepository.insertWaveBulk(
            cmd.barType(), cmd.symbol(), cmd.depth(), enableList);
      } catch (Exception e) {
        log.error("{}:{}", cmd.symbol(), zigzag.getBarDateTime(), e);
        throw new InsertException(cmd.symbol() + ":" + zigzag.getBarDateTime());
      }
    }

  }

  public record GenerateResult(ZigZagStatus status, boolean warn) {

  }

}
