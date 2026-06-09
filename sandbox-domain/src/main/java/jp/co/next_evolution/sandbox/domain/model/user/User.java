package jp.co.next_evolution.sandbox.domain.model.user;

import java.time.LocalDateTime;
import jp.co.next_evolution.sandbox.domain.exception.AuthenticationException;
import jp.co.next_evolution.sandbox.domain.exception.DuplicateException;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class User {

  private Long id;
  private UserId userId;
  private String emailAddress;
  private NickName nickName;
  private boolean approved;
  private LocalDateTime approvedAt;
  private boolean admin;
  private boolean blocked;
  private final boolean deleted;
  private final LocalDateTime createdAt;
  private final String createdBy;
  private final LocalDateTime updatedAt;
  private final String updatedBy;

  /**
   * blockedチェック.
   */
  public void checkBlocked() {
    if (this.blocked) {
      throw new AuthenticationException("blocked.");
    }
  }

  public void checkAlreadyApproved() {
    if (this.approved) {
      throw new DuplicateException("承認済みです");
    }
  }

  public void checkBlockDuplicate(boolean newBlocked) {
    if (this.blocked == newBlocked) {
      throw new DuplicateException(newBlocked ? "Block済みです" : "Block解除済みです");
    }
  }

  public void checkAdminDuplicate(boolean newAdmin) {
    if (this.admin == newAdmin) {
      throw new DuplicateException(newAdmin ? "admin権限設定済みです" : "admin権限設定剥奪済みです");
    }
  }

}