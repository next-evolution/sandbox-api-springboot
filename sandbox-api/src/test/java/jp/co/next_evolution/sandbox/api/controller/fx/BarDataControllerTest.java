package jp.co.next_evolution.sandbox.api.controller.fx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.io.InputStream;
import java.util.List;
import jp.co.next_evolution.sandbox.api.dto.request.fx.BarDataSearchRequest;
import jp.co.next_evolution.sandbox.api.dto.response.fx.BarDataSearchResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.dto.fx.BarDataImportResult;
import jp.co.next_evolution.sandbox.application.usecase.fx.bardata.ImportCsvBarDataUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.bardata.SearchBarDataUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.bardata.StatusBarDataUseCase;
import jp.co.next_evolution.sandbox.domain.model.auth.AuthUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class BarDataControllerTest {

  @Mock
  private SearchBarDataUseCase searchBarDataUseCase;

  @Mock
  private ImportCsvBarDataUseCase importCsvBarDataUseCase;

  @Mock
  private StatusBarDataUseCase statusBarDataUseCase;

  @Mock
  private MultipartFile mockFile;

  @InjectMocks
  private BarDataController controller;

  @Test
  void searchReturnsOk() {
    SearchBarDataUseCase.SearchResult result =
        new SearchBarDataUseCase.SearchResult(0, List.of(), 1, 20);
    given(searchBarDataUseCase.execute(any())).willReturn(result);

    BarDataSearchRequest req = new BarDataSearchRequest();
    ReflectionTestUtils.setField(req, "barType", "4H");
    ReflectionTestUtils.setField(req, "symbol", "USDJPY");

    ResponseEntity<BarDataSearchResponse> response = controller.search(req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Ok);
  }

  @Test
  void importCsvReturnsOk() throws Exception {
    AuthUser authUser = new AuthUser("sub-123", "test@example.com", true, false);
    BarDataImportResult importResult = new BarDataImportResult();
    given(mockFile.getInputStream()).willReturn(InputStream.nullInputStream());
    given(mockFile.getOriginalFilename()).willReturn("test.csv");
    given(mockFile.getSize()).willReturn(100L);
    given(importCsvBarDataUseCase.execute(any())).willReturn(importResult);

    ResponseEntity<BarDataImportResult> response =
        controller.importCsv("USDJPY", "4H", false, mockFile, authUser);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void statusReturnsOk() {
    given(statusBarDataUseCase.execute(any())).willReturn(List.of());

    ResponseEntity<List<BarDataImportResult>> response = controller.status("Trade", "4H");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

}
