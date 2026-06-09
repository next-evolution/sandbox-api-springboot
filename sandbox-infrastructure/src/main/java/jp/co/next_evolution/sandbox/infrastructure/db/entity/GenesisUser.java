package jp.co.next_evolution.sandbox.infrastructure.db.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GenesisUser extends MasterColumns {

  private Long id;
  private String userId;
  private String emailAddress;
  private String nickName;
  private boolean approved;
  private LocalDateTime approvedAt;
  private boolean admin;
  private boolean blocked;

}
