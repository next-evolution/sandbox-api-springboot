package jp.co.next_evolution.sandbox.api.controller.fx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

import java.util.List;
import jp.co.next_evolution.sandbox.api.dto.request.ApiSearchRequest;
import jp.co.next_evolution.sandbox.api.dto.request.fx.CountryRequest;
import jp.co.next_evolution.sandbox.api.dto.response.fx.CountrySearchResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.dto.fx.CountryDto;
import jp.co.next_evolution.sandbox.application.usecase.fx.country.AddCountryUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.country.GetCountryUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.country.SearchCountryUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.country.UpdateCountryUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CountryControllerTest {

  @Mock
  private SearchCountryUseCase searchCountryUseCase;

  @Mock
  private AddCountryUseCase addCountryUseCase;

  @Mock
  private GetCountryUseCase getCountryUseCase;

  @Mock
  private UpdateCountryUseCase updateCountryUseCase;

  @InjectMocks
  private CountryController controller;

  @Test
  void searchReturnsOk() {
    SearchCountryUseCase.SearchResult result =
        new SearchCountryUseCase.SearchResult(0, List.of(), 1, 20);
    given(searchCountryUseCase.execute(anyInt(), anyInt())).willReturn(result);

    ApiSearchRequest req = new ApiSearchRequest();

    ResponseEntity<CountrySearchResponse> response = controller.search(req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Ok);
  }

  @Test
  void addReturnsOk() {
    CountryRequest req = new CountryRequest();

    ResponseEntity<Void> response = controller.add(req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void getReturnsCountryDto() {
    CountryDto dto = new CountryDto("JP", "日本", "JPY", "Japan", "JP", (short) 1);
    given(getCountryUseCase.get("JP")).willReturn(dto);

    ResponseEntity<CountryDto> response = controller.get("JP");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(dto);
  }

  @Test
  void updateReturnsOk() {
    CountryRequest req = new CountryRequest();

    ResponseEntity<Void> response = controller.update("JP", req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

}
