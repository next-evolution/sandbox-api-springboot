package jp.co.next_evolution.sandbox.security;

import jakarta.servlet.http.HttpServletRequest;

/**
 * {@code Authorization: Bearer} ヘッダーからトークン文字列を取り出す共通ロジック.
 * {@code JwtAuthFilter}（トークン検証）と {@code AuthController}（ログインCookie発行）の
 * 両方から参照される.
 */
public final class BearerTokenResolver {

  private static final String BEARER_PREFIX = "Bearer ";

  private BearerTokenResolver() {
  }

  /**
   * Authorization ヘッダーからトークン文字列を取り出す. ヘッダーなし / Bearer でない場合は null.
   */
  public static String resolve(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith(BEARER_PREFIX)) {
      return header.substring(BEARER_PREFIX.length());
    }
    return null;
  }

}
