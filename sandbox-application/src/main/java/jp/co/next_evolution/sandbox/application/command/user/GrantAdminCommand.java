package jp.co.next_evolution.sandbox.application.command.user;

public record GrantAdminCommand(String userId, boolean admin, String updatedBy) {

}
