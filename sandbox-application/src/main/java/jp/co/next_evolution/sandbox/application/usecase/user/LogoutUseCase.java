package jp.co.next_evolution.sandbox.application.usecase.user;

import jp.co.next_evolution.sandbox.application.command.user.LogoutCommand;
import jp.co.next_evolution.sandbox.domain.model.auth.AuthUser;
import jp.co.next_evolution.sandbox.domain.model.user.UserId;
import jp.co.next_evolution.sandbox.domain.repository.auth.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutUseCase {

  private final SessionRepository sessionRepository;

  public void execute(LogoutCommand cmd) {
    try {
      AuthUser authUser = cmd.authUser();
      if (authUser != null) {
        String userIdValue = UserId.decodeUserIdValue(cmd.encodedUserId());
        if (authUser.sub().equals(userIdValue)) {
          sessionRepository.deleteBySub(authUser.sub());
        } else {
          log.error("logout failed [REQ:UserId:{}|TOKEN:UserId:{}]", userIdValue, authUser.sub());
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

}
