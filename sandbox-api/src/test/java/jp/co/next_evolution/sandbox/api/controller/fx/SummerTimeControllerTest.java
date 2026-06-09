package jp.co.next_evolution.sandbox.api.controller.fx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.List;
import jp.co.next_evolution.sandbox.api.dto.request.ApiSearchRequest;
import jp.co.next_evolution.sandbox.api.dto.request.fx.SummerTimeRequest;
import jp.co.next_evolution.sandbox.api.dto.response.fx.SummerTimeSearchResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.dto.fx.SummerTimeDto;
import jp.co.next_evolution.sandbox.application.usecase.fx.summertime.AddSummerTimeUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.summertime.GetSummerTimeUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.summertime.SearchSummerTimeUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.summertime.UpdateSummerTimeUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class SummerTimeControllerTest {

  @Mock
  private SearchSummerTimeUseCase searchSummerTimeUseCase;

  @Mock
  private AddSummerTimeUseCase addSummerTimeUseCase;

  @Mock
  private GetSummerTimeUseCase getSummerTimeUseCase;

  @Mock
  private UpdateSummerTimeUseCase updateSummerTimeUseCase;

  @InjectMocks
  private SummerTimeController controller;

  @Test
  void searchReturnsOk() {
    SearchSummerTimeUseCase.SearchResult result =
        new SearchSummerTimeUseCase.SearchResult(0, List.of(), 1, 20);
    given(searchSummerTimeUseCase.execute(anyInt(), anyInt())).willReturn(result);

    ApiSearchRequest req = new ApiSearchRequest();

    ResponseEntity<SummerTimeSearchResponse> response = controller.search(req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Ok);
  }

  @Test
  void addReturnsOk() {
    SummerTimeRequest req = new SummerTimeRequest();

    ResponseEntity<Void> response = controller.add(req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void getReturnsSummerTimeDto() {
    SummerTimeDto dto = new SummerTimeDto(
        (short) 2024,
        LocalDate.of(2024, 3, 10),
        LocalDate.of(2024, 11, 3));
    given(getSummerTimeUseCase.get((short) 2024)).willReturn(dto);

    ResponseEntity<SummerTimeDto> response = controller.get((short) 2024);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(dto);
  }

  @Test
  void updateReturnsOk() {
    SummerTimeRequest req = new SummerTimeRequest();

    ResponseEntity<Void> response = controller.update((short) 2024, req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

}
