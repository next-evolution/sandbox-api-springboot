package jp.co.next_evolution.sandbox.application.usecase.user;

import java.time.LocalDateTime;
import jp.co.next_evolution.sandbox.application.command.user.UpdateUserCommand;
import jp.co.next_evolution.sandbox.application.dto.user.UserDto;
import jp.co.next_evolution.sandbox.domain.exception.NotFoundException;
import jp.co.next_evolution.sandbox.domain.model.user.NickName;
import jp.co.next_evolution.sandbox.domain.model.user.User;
import jp.co.next_evolution.sandbox.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCase {

  private final UserRepository userRepository;

  @Transactional
  public UserDto execute(UpdateUserCommand cmd) {

    User user = userRepository.findByUserId(cmd.userId())
                              .orElseThrow(() -> new NotFoundException("ユーザが存在しません"));

    User updated = User.builder()
                       .id(user.getId())
                       .userId(user.getUserId())
                       .emailAddress(user.getEmailAddress())
                       .nickName(new NickName(cmd.nickName()))
                       .approved(user.isApproved())
                       .approvedAt(user.getApprovedAt())
                       .admin(user.isAdmin())
                       .blocked(user.isBlocked())
                       .deleted(user.isDeleted())
                       .createdAt(user.getCreatedAt())
                       .createdBy(user.getCreatedBy())
                       .updatedAt(LocalDateTime.now())
                       .updatedBy(cmd.updatedBy())
                       .build();

    userRepository.updateNickName(updated);

    return UserDto.from(updated);

  }

}
