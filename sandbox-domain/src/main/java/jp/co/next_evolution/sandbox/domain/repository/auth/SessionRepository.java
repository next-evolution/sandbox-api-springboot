package jp.co.next_evolution.sandbox.domain.repository.auth;

import java.util.Optional;
import jp.co.next_evolution.sandbox.domain.model.auth.AuthUser;

/**
 * Redis セッション管理の Domain interface. Infrastructure の Redis 実装詳細を Domain 層に漏らさない.
 */
public interface SessionRepository {

  /**
   * セッション保存（Upsert）.
   * ログイン時に呼ぶ。既存エントリは上書き更新.
   */
  void save(AuthUser authUser);

  /**
   * セッション取得.
   * sub (JWT の subject = user_id) をキーに検索.
   */
  Optional<AuthUser> findBySub(String sub);

  /**
   * セッション削除.
   * ログアウト時に呼ぶ.
   */
  void deleteBySub(String sub);

  /**
   * セッション更新.
   */
  void update(AuthUser authUser);

}
