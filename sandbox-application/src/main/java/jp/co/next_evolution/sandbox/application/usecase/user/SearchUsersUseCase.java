package jp.co.next_evolution.sandbox.application.usecase.user;

import java.util.Collections;
import java.util.List;
import jp.co.next_evolution.sandbox.application.command.user.SearchUsersCommand;
import jp.co.next_evolution.sandbox.application.dto.user.UserDto;
import jp.co.next_evolution.sandbox.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchUsersUseCase {

  private final UserRepository userRepository;

  public SearchResult execute(SearchUsersCommand cmd) {

    int count = userRepository.searchCount(cmd.emailAddress(), cmd.approved());

    List<UserDto> list = count == 0
                         ? Collections.emptyList()
                         : userRepository.search(
                             cmd.emailAddress(), cmd.approved(), cmd.page(), cmd.size())
                                         .stream()
                                         .map(UserDto::from)
                                         .toList();

    return new SearchResult(count, list, cmd.page(), cmd.size());

  }

  public record SearchResult(int totalCount, List<UserDto> userList, int page, int size) {

    public int totalPage() {
      return totalCount == 0
             ? 0
             : (totalCount + (size - 1)) / size;
    }

  }

}
