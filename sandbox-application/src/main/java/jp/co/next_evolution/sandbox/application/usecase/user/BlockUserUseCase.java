package jp.co.next_evolution.sandbox.application.usecase.user;

import jp.co.next_evolution.sandbox.application.command.user.BlockUserCommand;
import jp.co.next_evolution.sandbox.application.dto.user.UserDto;
import jp.co.next_evolution.sandbox.domain.exception.NotFoundException;
import jp.co.next_evolution.sandbox.domain.model.user.User;
import jp.co.next_evolution.sandbox.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlockUserUseCase {

  private final UserRepository userRepository;

  @Transactional
  public UserDto execute(BlockUserCommand cmd) {

    User user = userRepository.findByUserId(cmd.userId())
                              .orElseThrow(() -> new NotFoundException("ユーザが存在しません"));

    user.checkBlockDuplicate(cmd.blocked());

    User updated = User.builder()
                       .id(user.getId())
                       .userId(user.getUserId())
                       .emailAddress(user.getEmailAddress())
                       .nickName(user.getNickName())
                       .approved(user.isApproved())
                       .approvedAt(user.getApprovedAt())
                       .admin(user.isAdmin())
                       .blocked(cmd.blocked())
                       .deleted(user.isDeleted())
                       .createdAt(user.getCreatedAt())
                       .createdBy(user.getCreatedBy())
                       .updatedAt(java.time.LocalDateTime.now())
                       .updatedBy(cmd.updatedBy())
                       .build();

    userRepository.updateBlocked(updated);

    return UserDto.from(updated);

  }

}
