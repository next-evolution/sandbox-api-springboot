package jp.co.next_evolution.sandbox.infrastructure.db.mapper.user;

import java.util.List;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.SandboxUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SandboxUserMapper {

  SandboxUser login(String userId, String email);

  int searchCount(String emailAddress, Boolean approved);

  List<SandboxUser> search(String emailAddress, Boolean approved, int page, int size);

  SandboxUser findByUserId(String userId);

  boolean existsByUserId(String userId);

  int insertUser(SandboxUser user);

  int updateNickName(SandboxUser user);

  int updateApproved(SandboxUser user);

  int updateBlocked(SandboxUser user);

  int updateAdmin(SandboxUser user);

}
