package jp.co.next_evolution.sandbox.api.controller.fx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

import java.util.List;
import jp.co.next_evolution.sandbox.api.dto.request.fx.SymbolRequest;
import jp.co.next_evolution.sandbox.api.dto.request.fx.SymbolSearchRequest;
import jp.co.next_evolution.sandbox.api.dto.response.fx.SymbolSearchResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.dto.fx.SymbolDto;
import jp.co.next_evolution.sandbox.application.usecase.fx.symbol.AddSymbolUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.symbol.GetSymbolUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.symbol.SearchSymbolUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.symbol.UpdateSymbolUseCase;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SymbolControllerTest {

  @Mock
  private SearchSymbolUseCase searchSymbolUseCase;

  @Mock
  private AddSymbolUseCase addSymbolUseCase;

  @Mock
  private GetSymbolUseCase getSymbolUseCase;

  @Mock
  private UpdateSymbolUseCase updateSymbolUseCase;

  @InjectMocks
  private SymbolController controller;

  @Test
  void currencyPairListReturnsSymbolDtoList() {
    SearchSymbolUseCase.SearchResult result =
        new SearchSymbolUseCase.SearchResult(0, List.of(), 1, 500);
    given(searchSymbolUseCase.execute(SymbolType.Trade, 1, 500)).willReturn(result);

    ResponseEntity<List<SymbolDto>> response = controller.currencyPairList();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void currencyIndexListReturnsSymbolDtoList() {
    SearchSymbolUseCase.SearchResult result =
        new SearchSymbolUseCase.SearchResult(0, List.of(), 1, 500);
    given(searchSymbolUseCase.execute(SymbolType.Analyze, 1, 500)).willReturn(result);

    ResponseEntity<List<SymbolDto>> response = controller.currencyIndexList();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void searchReturnsOk() {
    SymbolSearchRequest req = new SymbolSearchRequest();
    ReflectionTestUtils.setField(req, "symbolType", "Trade");
    SearchSymbolUseCase.SearchResult result =
        new SearchSymbolUseCase.SearchResult(0, List.of(), 0, 0);
    given(searchSymbolUseCase.execute(any(), anyInt(), anyInt())).willReturn(result);

    ResponseEntity<SymbolSearchResponse> response = controller.search(req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Ok);
  }

  @Test
  void addReturnsOk() {
    SymbolRequest req = new SymbolRequest();

    ResponseEntity<Void> response = controller.add(req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void getReturnsSymbolDto() {
    ResponseEntity<SymbolDto> response = controller.get("USDJPY");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void updateReturnsOk() {
    SymbolRequest req = new SymbolRequest();

    ResponseEntity<Void> response = controller.update("USDJPY", req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

}
