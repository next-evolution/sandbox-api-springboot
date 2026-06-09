package jp.co.next_evolution.sandbox.application.usecase.user;

import java.util.Optional;
import jp.co.next_evolution.sandbox.application.command.user.GetProfileCommand;
import jp.co.next_evolution.sandbox.application.dto.user.UserDto;
import jp.co.next_evolution.sandbox.domain.exception.NotFoundException;
import jp.co.next_evolution.sandbox.domain.model.user.User;
import jp.co.next_evolution.sandbox.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetProfileUseCase {

  private final UserRepository userRepository;

  /**
   * ユーザープロフィールを取得する.
   * 未承認の場合は Optional.empty() を返す.
   */
  public Optional<UserDto> execute(GetProfileCommand cmd) {

    User user = userRepository.findByUserId(cmd.userId())
                              .orElseThrow(() -> new NotFoundException("ユーザが存在しません"));

    if (!user.isApproved()) {
      return Optional.empty();
    }

    return Optional.of(UserDto.from(user));

  }

}
