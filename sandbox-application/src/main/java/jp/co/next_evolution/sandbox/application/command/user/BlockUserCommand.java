package jp.co.next_evolution.sandbox.application.command.user;

public record BlockUserCommand(String userId, boolean blocked, String updatedBy) {

}
