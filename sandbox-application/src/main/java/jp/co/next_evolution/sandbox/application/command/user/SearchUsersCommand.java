package jp.co.next_evolution.sandbox.application.command.user;

public record SearchUsersCommand(String emailAddress, Boolean approved, int page, int size) {

}
