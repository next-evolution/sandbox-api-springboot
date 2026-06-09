package jp.co.next_evolution.sandbox.application.usecase.fx.summertime;

import jp.co.next_evolution.sandbox.application.dto.fx.SummerTimeDto;
import jp.co.next_evolution.sandbox.domain.exception.NotFoundException;
import jp.co.next_evolution.sandbox.domain.repository.fx.SummerTimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetSummerTimeUseCase {

  private final SummerTimeRepository summerTimeRepository;

  public SummerTimeDto get(short targetYear) {

    return summerTimeRepository.get(targetYear)
                               .map(SummerTimeDto::fromDomain)
                               .orElseThrow(
                                   () -> new NotFoundException(String.valueOf(targetYear)));
  }

}
