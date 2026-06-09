package jp.co.next_evolution.sandbox.application.dto.user;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import jp.co.next_evolution.sandbox.domain.model.user.User;

public record UserDto(
    @Schema(requiredMode = REQUIRED, description = "ID(Number)", example = "999")
    Long id,
    @Schema(requiredMode = REQUIRED, description = "ユーザID(cognito sub)", example = "1aaaaaa1-2bb2-3cc3-4dd4-5eeeeeeeeee5")
    String userId,
    @Schema(requiredMode = REQUIRED, description = "email", example = "account@domain.com")
    String emailAddress,
    @Schema(requiredMode = REQUIRED, description = "nickname", example = "Mr. Consideration")
    String nickName,
    @Schema(requiredMode = REQUIRED, description = "承認フラグ")
    boolean approved,
    @Schema(requiredMode = NOT_REQUIRED, description = "承認日時", example = "2013-04-08 10:20:30")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime approvedAt,
    @Schema(requiredMode = REQUIRED, description = "管理者フラグ")
    boolean admin,
    @Schema(type = "boolean", requiredMode = REQUIRED, description = "auth0 blocked")
    boolean blocked,
    @Schema(requiredMode = REQUIRED, description = "新規登録日時", example = "2013-04-08 10:20:30")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt,
    @Schema(requiredMode = NOT_REQUIRED, description = "更新日時", example = "2013-04-08 10:20:30")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt
) {

  /**
   * deleted, createdBy, updatedByはresponseから除外する.
   */
  public static UserDto from(User entity) {
    return new UserDto(
        entity.getId(),
        entity.getUserId().value(),
        entity.getEmailAddress(),
        entity.getNickName().value(),
        entity.isApproved(),
        entity.getApprovedAt(),
        entity.isAdmin(),
        entity.isBlocked(),
        entity.getCreatedAt(),
        entity.getUpdatedAt()
    );
  }

}
