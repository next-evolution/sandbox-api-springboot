package jp.co.next_evolution.sandbox.infrastructure.db.mapper.user;

import java.util.List;
import jp.co.next_evolution.sandbox.infrastructure.db.entity.GenesisUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GenesisUserMapper {

  GenesisUser login(String userId, String email);

  int searchCount(String emailAddress, Boolean approved);

  List<GenesisUser> search(String emailAddress, Boolean approved, int page, int size);

  GenesisUser findByUserId(String userId);

  boolean existsByUserId(String userId);

  int insertUser(GenesisUser user);

  int updateNickName(GenesisUser user);

  int updateApproved(GenesisUser user);

  int updateBlocked(GenesisUser user);

  int updateAdmin(GenesisUser user);

}
