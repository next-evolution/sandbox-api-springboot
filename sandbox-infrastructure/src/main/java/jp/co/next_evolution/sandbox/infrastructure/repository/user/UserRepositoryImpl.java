package jp.co.next_evolution.sandbox.infrastructure.repository.user;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jp.co.next_evolution.sandbox.domain.exception.InsertException;
import jp.co.next_evolution.sandbox.domain.exception.UpdateException;
import jp.co.next_evolution.sandbox.domain.model.user.NickName;
import jp.co.next_evolution.sandbox.domain.model.user.User;
import jp.co.next_evolution.sandbox.domain.model.user.UserId;
import jp.co.next_evolution.sandbox.domain.repository.user.UserRepository;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.GenesisUser;
import jp.co.next_evolution.sandbox.infrastructure.db.mapper.user.GenesisUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final GenesisUserMapper genesisUserMapper;

  @Override
  public Optional<User> login(String userId, String email) {
    return Optional.ofNullable(genesisUserMapper.login(userId, email))
                   .map(this::toDomain);
  }

  @Override
  public int searchCount(String emailAddress, Boolean approved) {
    return genesisUserMapper.searchCount(emailAddress, approved);
  }

  @Override
  public List<User> search(String emailAddress, Boolean approved, int page, int size) {
    return genesisUserMapper.search(emailAddress, approved, page, size)
                            .stream()
                            .map(this::toDomain)
                            .collect(Collectors.toList());
  }

  @Override
  public Optional<User> findByUserId(String userId) {
    return Optional.ofNullable(genesisUserMapper.findByUserId(userId))
                   .map(this::toDomain);
  }

  @Override
  public boolean existsByUserId(String userId) {
    return genesisUserMapper.existsByUserId(userId);
  }

  @Override
  public void insertUser(User user) {
    if (genesisUserMapper.insertUser(toEntity(user)) != 1) {
      throw new InsertException("ユーザ新規登録");
    }
  }

  @Override
  public void updateNickName(User user) {
    if (genesisUserMapper.updateNickName(toEntity(user)) != 1) {
      throw new UpdateException("ユーザ情報更新");
    }
  }

  @Override
  public void updateApproved(User user) {
    if (genesisUserMapper.updateApproved(toEntity(user)) != 1) {
      throw new UpdateException("ユーザ承認");
    }
  }

  @Override
  public void updateBlocked(User user) {
    if (genesisUserMapper.updateBlocked(toEntity(user)) != 1) {
      throw new UpdateException("Block設定");
    }
  }

  @Override
  public void updateAdmin(User user) {
    if (genesisUserMapper.updateAdmin(toEntity(user)) != 1) {
      throw new UpdateException("管理者権限設定");
    }
  }

  private User toDomain(GenesisUser record) {
    return User.builder()
               .id(record.getId())
               .userId(new UserId(record.getUserId()))
               .emailAddress(record.getEmailAddress())
               .nickName(new NickName(record.getNickName()))
               .approved(record.isApproved())
               .approvedAt(record.getApprovedAt())
               .admin(record.isAdmin())
               .blocked(record.isBlocked())
               .deleted(record.isDeleted())
               .createdAt(record.getCreatedAt())
               .createdBy(record.getCreatedBy())
               .updatedAt(record.getUpdatedAt())
               .updatedBy(record.getUpdatedBy())
               .build();
  }

  private GenesisUser toEntity(User user) {
    return GenesisUser.builder()
                      .id(user.getId())
                      .userId(user.getUserId().value())
                      .emailAddress(user.getEmailAddress())
                      .nickName(user.getNickName().value())
                      .approved(user.isApproved())
                      .approvedAt(user.getApprovedAt())
                      .admin(user.isAdmin())
                      .blocked(user.isBlocked())
                      .deleted(user.isDeleted())
                      .createdAt(user.getCreatedAt())
                      .createdBy(user.getCreatedBy())
                      .updatedAt(user.getUpdatedAt())
                      .updatedBy(user.getUpdatedBy())
                      .build();
  }

}
