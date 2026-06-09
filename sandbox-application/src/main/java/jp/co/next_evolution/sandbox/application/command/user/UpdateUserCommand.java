package jp.co.next_evolution.sandbox.application.command.user;

public record UpdateUserCommand(String userId, String nickName, String updatedBy) {

}
