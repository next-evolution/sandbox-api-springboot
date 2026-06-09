package jp.co.next_evolution.sandbox.application.usecase.fx.summertime;

import jp.co.next_evolution.sandbox.application.dto.fx.SummerTimeDto;
import jp.co.next_evolution.sandbox.domain.exception.DuplicateException;
import jp.co.next_evolution.sandbox.domain.exception.InsertException;
import jp.co.next_evolution.sandbox.domain.repository.fx.SummerTimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddSummerTimeUseCase {

  private final SummerTimeRepository summerTimeRepository;

  @Transactional
  public void execute(SummerTimeDto summerTimeDto) {

    if (summerTimeRepository.exists(summerTimeDto.targetYear())) {
      throw new DuplicateException(String.valueOf(summerTimeDto.targetYear()));
    }

    if (summerTimeRepository.add(summerTimeDto.toDomain()) != 1) {
      throw new InsertException(String.valueOf(summerTimeDto.targetYear()));
    }

  }

}
