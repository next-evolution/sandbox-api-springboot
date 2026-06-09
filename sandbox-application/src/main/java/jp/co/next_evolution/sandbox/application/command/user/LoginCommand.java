package jp.co.next_evolution.sandbox.application.command.user;

import jp.co.next_evolution.sandbox.domain.model.auth.AuthUser;

public record LoginCommand(AuthUser authUser, String encodedEmail) {

}
