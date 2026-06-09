package jp.co.next_evolution.sandbox.application.usecase.fx;

import jp.co.next_evolution.sandbox.domain.repository.MasterCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MasterStatusUseCase {

  private final MasterCacheRepository masterCacheRepository;

  /**
   * Redisに登録しているマスターキャッシュの最新ステータスを返す.
   *
   * @return Redisキャッシュのステータス文字列
   */
  public String execute() {
    return masterCacheRepository.getStatus();
  }

}