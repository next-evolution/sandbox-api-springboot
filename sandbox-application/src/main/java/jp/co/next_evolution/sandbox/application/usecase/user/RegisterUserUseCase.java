package jp.co.next_evolution.sandbox.application.usecase.user;

import java.time.LocalDateTime;
import jp.co.next_evolution.sandbox.application.command.user.RegisterUserCommand;
import jp.co.next_evolution.sandbox.application.dto.user.UserDto;
import jp.co.next_evolution.sandbox.domain.exception.DuplicateException;
import jp.co.next_evolution.sandbox.domain.model.user.NickName;
import jp.co.next_evolution.sandbox.domain.model.user.User;
import jp.co.next_evolution.sandbox.domain.model.user.UserId;
import jp.co.next_evolution.sandbox.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {

  private final UserRepository userRepository;

  @Transactional
  public UserDto execute(RegisterUserCommand cmd) {

    if (userRepository.existsByUserId(cmd.userId())) {
      throw new DuplicateException("登録済みのユーザです");
    }

    LocalDateTime now = LocalDateTime.now();

    User user = User.builder()
                    .userId(new UserId(cmd.userId()))
                    .emailAddress(cmd.email())
                    .nickName(new NickName(cmd.nickName()))
                    .approved(false)
                    .approvedAt(null)
                    .admin(false)
                    .blocked(false)
                    .deleted(false)
                    .createdAt(now)
                    .createdBy(cmd.userId())
                    .updatedAt(now)
                    .updatedBy(cmd.userId())
                    .build();

    userRepository.insertUser(user);

    return UserDto.from(user);

  }

}
