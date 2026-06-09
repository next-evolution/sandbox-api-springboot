package jp.co.next_evolution.sandbox.domain.repository.user;

import java.util.List;
import java.util.Optional;
import jp.co.next_evolution.sandbox.domain.model.user.User;

public interface UserRepository {

  Optional<User> login(String userId, String email);

  int searchCount(String emailAddress, Boolean approved);

  List<User> search(String emailAddress, Boolean approved, int page, int size);

  Optional<User> findByUserId(String userId);

  boolean existsByUserId(String userId);

  void insertUser(User user);

  void updateNickName(User user);

  void updateApproved(User user);

  void updateBlocked(User user);

  void updateAdmin(User user);

}
