package jp.co.next_evolution.sandbox.application.usecase.fx.summertime;

import jp.co.next_evolution.sandbox.application.dto.fx.SummerTimeDto;
import jp.co.next_evolution.sandbox.domain.exception.DuplicateException;
import jp.co.next_evolution.sandbox.domain.exception.UpdateException;
import jp.co.next_evolution.sandbox.domain.repository.fx.SummerTimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateSummerTimeUseCase {

  private final SummerTimeRepository summerTimeRepository;

  @Transactional
  public void execute(short targetYear, SummerTimeDto summerTimeDto) {

    if (targetYear == summerTimeDto.targetYear()) {
      if (!summerTimeRepository.exists(targetYear)) {
        throw new UpdateException(String.valueOf(targetYear));
      }
      if (summerTimeRepository.update(summerTimeDto.toDomain()) != 1) {
        throw new UpdateException(String.valueOf(targetYear));
      }
    } else {
      if (summerTimeRepository.exists(targetYear)) {
        throw new DuplicateException(String.valueOf(targetYear));
      }
      if (summerTimeRepository.update(summerTimeDto.toDomain(), targetYear) != 1) {
        throw new UpdateException(String.valueOf(targetYear));
      }
    }


  }

}
