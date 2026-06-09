package jp.co.next_evolution.sandbox.api.controller.fx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;
import jp.co.next_evolution.sandbox.application.usecase.fx.GetMasterUseCase;
import jp.co.next_evolution.sandbox.domain.model.KeyValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class MasterListControllerTest {

  @Mock
  private GetMasterUseCase getMasterUseCase;

  @InjectMocks
  private MasterListController controller;

  @Test
  void symbolReturnsKeyValueList() {
    List<KeyValue> expected = List.of(new KeyValue("USDJPY", "ドル円"));
    given(getMasterUseCase.symbol("Trade")).willReturn(expected);

    ResponseEntity<List<KeyValue>> response = controller.symbol("Trade");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(expected);
  }

  @Test
  void countryReturnsKeyValueList() {
    List<KeyValue> expected = List.of(new KeyValue("JP", "日本"));
    given(getMasterUseCase.country()).willReturn(expected);

    ResponseEntity<List<KeyValue>> response = controller.country();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(expected);
  }

  @Test
  void currencyPairReturnsKeyValueList() {
    List<KeyValue> expected = List.of(new KeyValue("USDJPY", "ドル円"));
    given(getMasterUseCase.currencyPair()).willReturn(expected);

    ResponseEntity<List<KeyValue>> response = controller.currencyPair();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(expected);
  }

  @Test
  void currencyIndexReturnsKeyValueList() {
    List<KeyValue> expected = List.of(new KeyValue("DXY", "ドルインデックス"));
    given(getMasterUseCase.currencyIndex()).willReturn(expected);

    ResponseEntity<List<KeyValue>> response = controller.currencyIndex();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(expected);
  }

  @Test
  void economicIndicatorReturnsKeyValueList() {
    List<KeyValue> expected = List.of(new KeyValue("1", "GDP"));
    given(getMasterUseCase.economicIndicator("JP")).willReturn(expected);

    ResponseEntity<List<KeyValue>> response = controller.economicIndicator("JP");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(expected);
  }

}
