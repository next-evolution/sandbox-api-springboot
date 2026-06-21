package jp.co.next_evolution.sandbox.api.controller.fx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import jp.co.next_evolution.sandbox.api.dto.request.fx.EconomicIndicatorDataRequest;
import jp.co.next_evolution.sandbox.api.dto.request.fx.EconomicIndicatorDataSearchRequest;
import jp.co.next_evolution.sandbox.api.dto.response.fx.EconomicIndicatorDataSearchResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.dto.fx.EconomicIndicatorDataDto;
import jp.co.next_evolution.sandbox.application.dto.fx.FileImportResult;
import jp.co.next_evolution.sandbox.application.usecase.fx.economicindicatordata.AddEconomicIndicatorDataUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.economicindicatordata.GetEconomicIndicatorDataUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.economicindicatordata.ImportTextEconomicIndicatorDataUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.economicindicatordata.SearchEconomicIndicatorDataUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.economicindicatordata.UpdateEconomicIndicatorDataUseCase;
import jp.co.next_evolution.sandbox.domain.model.auth.AuthUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class EconomicIndicatorDataControllerTest {

  @Mock
  private SearchEconomicIndicatorDataUseCase searchEconomicIndicatorDataUseCase;

  @Mock
  private GetEconomicIndicatorDataUseCase getEconomicIndicatorDataUseCase;

  @Mock
  private AddEconomicIndicatorDataUseCase addEconomicIndicatorDataUseCase;

  @Mock
  private UpdateEconomicIndicatorDataUseCase updateEconomicIndicatorDataUseCase;

  @Mock
  private ImportTextEconomicIndicatorDataUseCase importTextEconomicIndicatorDataUseCase;

  @Mock
  private MultipartFile mockFile;

  @InjectMocks
  private EconomicIndicatorDataController controller;

  @Test
  void searchReturnsOk() {
    SearchEconomicIndicatorDataUseCase.SearchResult result =
        new SearchEconomicIndicatorDataUseCase.SearchResult(0, List.of(), 1, 20);
    given(searchEconomicIndicatorDataUseCase.execute(
        any(), any(), any(), any(), anyInt(), anyInt(), anyBoolean())).willReturn(result);

    EconomicIndicatorDataSearchRequest req = new EconomicIndicatorDataSearchRequest();

    ResponseEntity<EconomicIndicatorDataSearchResponse> response = controller.search(req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Ok);
  }

  @Test
  void getReturnsDto() {
    LocalDateTime publication = LocalDateTime.of(2024, 1, 15, 10, 0, 0);

    ResponseEntity<EconomicIndicatorDataDto> response =
        controller.get("JP", "GDP_F_QOQ", publication);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void addReturnsOk() {
    EconomicIndicatorDataRequest req = new EconomicIndicatorDataRequest();

    ResponseEntity<Void> response = controller.add(req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void updateReturnsOk() {
    LocalDateTime publication = LocalDateTime.of(2024, 1, 15, 10, 0, 0);
    EconomicIndicatorDataRequest req = new EconomicIndicatorDataRequest();

    ResponseEntity<Void> response = controller.update("JP", "GDP_F_QOQ", publication, req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void importTextReturnsOk() throws Exception {
    AuthUser authUser = new AuthUser("sub-123", "test@example.com", true, false, true);
    given(mockFile.getOriginalFilename()).willReturn("test.txt");
    given(mockFile.getInputStream()).willReturn(InputStream.nullInputStream());
    given(mockFile.getSize()).willReturn(100L);
    given(importTextEconomicIndicatorDataUseCase.execute(any(), any())).willReturn(List.of());

    ResponseEntity<List<FileImportResult>> response =
        controller.importText(new MultipartFile[]{mockFile}, authUser);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

}
