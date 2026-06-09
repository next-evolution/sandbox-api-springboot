package jp.co.next_evolution.sandbox.application.usecase.user;

import java.util.Optional;
import jp.co.next_evolution.sandbox.application.command.user.LoginCommand;
import jp.co.next_evolution.sandbox.application.dto.user.UserDto;
import jp.co.next_evolution.sandbox.domain.exception.AuthenticationException;
import jp.co.next_evolution.sandbox.domain.model.auth.AuthUser;
import jp.co.next_evolution.sandbox.domain.model.user.Email;
import jp.co.next_evolution.sandbox.domain.model.user.User;
import jp.co.next_evolution.sandbox.domain.repository.auth.SessionRepository;
import jp.co.next_evolution.sandbox.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

  private final UserRepository userRepository;
  private final SessionRepository sessionRepository;

  @Transactional
  public UserDto execute(LoginCommand cmd) {

    AuthUser authUser = cmd.authUser();
    if (authUser == null) {
      throw new AuthenticationException("login failed.");
    }

    // 1. BASE64 decode
    String decodedEmail = Email.decodeEmail(cmd.encodedEmail());

    // 2. JWT内emailとリクエストbodyのemailを照合
    if (!authUser.email().equals(decodedEmail)) {
      throw new AuthenticationException("login failed.");
    }

    // 3. ユーザーテーブルから取得（admin フラグ含む）
    Optional<User> user = userRepository.login(authUser.sub(), decodedEmail);

    // 4. Domainルール
    user.ifPresent(User::checkBlocked);

    // 5. admin フラグ付き AuthUser を Redis に保存
    AuthUser authUserWithAdmin = new AuthUser(
        authUser.sub(), authUser.email(), authUser.emailVerified(),
        user.map(User::isAdmin).orElse(false)
    );
    sessionRepository.save(authUserWithAdmin);

    return user.map(UserDto::from).orElse(null);

  }

}
