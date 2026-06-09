package jp.co.next_evolution.sandbox.api.controller.fx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jp.co.next_evolution.sandbox.api.dto.request.fx.ZigZagBarDataRequest;
import jp.co.next_evolution.sandbox.api.dto.request.fx.ZigZagGenerateRequest;
import jp.co.next_evolution.sandbox.api.dto.request.fx.ZigZagSearchRequest;
import jp.co.next_evolution.sandbox.api.dto.request.fx.ZigZagStatusRequest;
import jp.co.next_evolution.sandbox.api.dto.response.fx.ZigZagBarDataResponse;
import jp.co.next_evolution.sandbox.api.dto.response.fx.ZigZagGenerateResponse;
import jp.co.next_evolution.sandbox.api.dto.response.fx.ZigZagResult;
import jp.co.next_evolution.sandbox.api.dto.response.fx.ZigZagSearchResponse;
import jp.co.next_evolution.sandbox.api.dto.response.fx.ZigZagStatusResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.dto.fx.ZigZagSearchItem;
import jp.co.next_evolution.sandbox.application.usecase.fx.zigzag.GenerateZigZagUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.zigzag.GetZigZagBarDataUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.zigzag.GetZigZagStatusUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.zigzag.SearchZigZagUseCase;
import jp.co.next_evolution.sandbox.domain.model.fx.BarType;
import jp.co.next_evolution.sandbox.domain.model.fx.SymbolType;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZagBarDataRow;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZagStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ZigZagControllerTest {

  @Mock
  private SearchZigZagUseCase searchZigZagUseCase;

  @Mock
  private GetZigZagStatusUseCase getZigZagStatusUseCase;

  @Mock
  private GenerateZigZagUseCase generateZigZagUseCase;

  @Mock
  private GetZigZagBarDataUseCase getZigZagBarDataUseCase;

  @InjectMocks
  private ZigZagController controller;

  @Test
  void searchReturnsOk() {
    SearchZigZagUseCase.SearchResult result =
        new SearchZigZagUseCase.SearchResult(0, List.of(), 1, 20);
    given(searchZigZagUseCase.execute(any())).willReturn(result);

    ResponseEntity<ZigZagSearchResponse> response = controller.search(buildSearchRequest());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Ok);
  }

  @Test
  void searchMapsItemFieldsToResult() {
    ZigZagSearchItem item = buildSearchItem();
    SearchZigZagUseCase.SearchResult result =
        new SearchZigZagUseCase.SearchResult(1, List.of(item), 1, 20);
    given(searchZigZagUseCase.execute(any())).willReturn(result);

    ResponseEntity<ZigZagSearchResponse> response = controller.search(buildSearchRequest());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getList()).hasSize(1);
    ZigZagResult mapped = response.getBody().getList().get(0);

    // toResult
    assertThat(mapped.getSymbol()).isEqualTo("USDJPY");
    assertThat(mapped.getNextRsRate()).isEqualByComparingTo(BigDecimal.valueOf(0.382));

    // toInfoSmaFibonacci
    assertThat(mapped.getCurrent().getWave()).isEqualTo(101);
    assertThat(mapped.getCurrent().getResistance())
        .isEqualByComparingTo(BigDecimal.valueOf(150.000));

    // toSma
    assertThat(mapped.getCurrent().getSma4h200s().getPriceS())
        .isEqualByComparingTo(BigDecimal.valueOf(149.0));
    assertThat(mapped.getCurrent().getSma4h200s().getPriceE())
        .isEqualByComparingTo(BigDecimal.valueOf(149.5));

    // toFibonacci (non-null)
    assertThat(mapped.getCurrent().getFibonacci().getF1())
        .isEqualByComparingTo(BigDecimal.valueOf(150.000));
    assertThat(mapped.getCurrent().getFibonacci().getF0())
        .isEqualByComparingTo(BigDecimal.valueOf(148.000));

    // toInfo
    assertThat(mapped.getPrevious().getWave()).isEqualTo(98);
    assertThat(mapped.getPrevious().getResistance())
        .isEqualByComparingTo(BigDecimal.valueOf(150.000));

    // toFractalWave
    assertThat(mapped.getFractalWaveList()).hasSize(1);
    assertThat(mapped.getFractalWaveList().get(0).getWave()).isEqualTo(100);
    assertThat(mapped.getFractalWaveList().get(0).getWaveStart())
        .isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0));
  }

  @Test
  void searchMapsNullFibonacciToNull() {
    ZigZagSearchItem item = buildSearchItem();
    item.getCurrent().setFibonacci(null);
    SearchZigZagUseCase.SearchResult result =
        new SearchZigZagUseCase.SearchResult(1, List.of(item), 1, 20);
    given(searchZigZagUseCase.execute(any())).willReturn(result);

    ResponseEntity<ZigZagSearchResponse> response = controller.search(buildSearchRequest());

    assertThat(response.getBody().getList().get(0).getCurrent().getFibonacci()).isNull();
  }

  @Test
  void statusReturnsOk() {
    given(getZigZagStatusUseCase.execute(any())).willReturn(List.of());

    ZigZagStatusRequest req = new ZigZagStatusRequest();
    ReflectionTestUtils.setField(req, "symbolType", SymbolType.Trade);
    ReflectionTestUtils.setField(req, "barType", "4H");
    ReflectionTestUtils.setField(req, "depth", (short) 5);

    ResponseEntity<ZigZagStatusResponse> response = controller.status(req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Ok);
  }

  @Test
  void generateReturnsOk() {
    ZigZagStatus status = new ZigZagStatus();
    GenerateZigZagUseCase.GenerateResult result =
        new GenerateZigZagUseCase.GenerateResult(status, false);
    given(generateZigZagUseCase.execute(any())).willReturn(result);

    ResponseEntity<ZigZagGenerateResponse> response = controller.generate(buildGenerateRequest());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Ok);
  }

  @Test
  void generateReturnsWarnWhenWarn() {
    ZigZagStatus status = new ZigZagStatus();
    status.setMessage("警告メッセージ");
    GenerateZigZagUseCase.GenerateResult result =
        new GenerateZigZagUseCase.GenerateResult(status, true);
    given(generateZigZagUseCase.execute(any())).willReturn(result);

    ResponseEntity<ZigZagGenerateResponse> response = controller.generate(buildGenerateRequest());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Warn);
    assertThat(response.getBody().getMessage()).isEqualTo("警告メッセージ");
  }

  @Test
  void barDataReturnsOk() {
    GetZigZagBarDataUseCase.BarDataResult result =
        new GetZigZagBarDataUseCase.BarDataResult(BarType.H4, "USDJPY", (short) 5, 0, List.of());
    given(getZigZagBarDataUseCase.execute(any())).willReturn(result);

    ResponseEntity<ZigZagBarDataResponse> response = controller.barData(buildBarDataRequest());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReturnCode()).isEqualTo(ReturnCode.Ok);
  }

  @Test
  void barDataMapsRowFieldsToBarData() {
    ZigZagBarDataRow row = buildBarDataRow();
    GetZigZagBarDataUseCase.BarDataResult result =
        new GetZigZagBarDataUseCase.BarDataResult(
            BarType.H4, "USDJPY", (short) 5, 100, List.of(row));
    given(getZigZagBarDataUseCase.execute(any())).willReturn(result);

    ResponseEntity<ZigZagBarDataResponse> response = controller.barData(buildBarDataRequest());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    List<ZigZagBarDataResponse.ZigZagBarData> barDataList =
        response.getBody().getZigZagBarDataList();
    assertThat(barDataList).hasSize(1);

    // toBarData
    ZigZagBarDataResponse.ZigZagBarData mapped = barDataList.get(0);
    assertThat(mapped.getBarDateTime()).isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0));
    assertThat(mapped.getOpenPrice()).isEqualByComparingTo(BigDecimal.valueOf(149.000));
    assertThat(mapped.getHighPrice()).isEqualByComparingTo(BigDecimal.valueOf(150.000));
    assertThat(mapped.getLowPrice()).isEqualByComparingTo(BigDecimal.valueOf(148.000));
    assertThat(mapped.getClosePrice()).isEqualByComparingTo(BigDecimal.valueOf(149.500));
    assertThat(mapped.getSma200()).isEqualByComparingTo(BigDecimal.valueOf(148.500));
    assertThat(mapped.getSma75()).isEqualByComparingTo(BigDecimal.valueOf(149.000));
    assertThat(mapped.getSma20()).isEqualByComparingTo(BigDecimal.valueOf(149.200));
  }

  // --- リクエストビルダー ---

  private ZigZagSearchRequest buildSearchRequest() {
    ZigZagSearchRequest req = new ZigZagSearchRequest();
    ReflectionTestUtils.setField(req, "barType", "4H");
    ReflectionTestUtils.setField(req, "symbol", "USDJPY");
    ReflectionTestUtils.setField(req, "depth", (short) 5);
    return req;
  }

  private ZigZagGenerateRequest buildGenerateRequest() {
    ZigZagGenerateRequest req = new ZigZagGenerateRequest();
    ReflectionTestUtils.setField(req, "symbol", "USDJPY");
    ReflectionTestUtils.setField(req, "barType", "4H");
    ReflectionTestUtils.setField(req, "depth", (short) 5);
    ReflectionTestUtils.setField(req, "loadSize", 100);
    return req;
  }

  private ZigZagBarDataRequest buildBarDataRequest() {
    ZigZagBarDataRequest req = new ZigZagBarDataRequest();
    ReflectionTestUtils.setField(req, "barType", BarType.H4);
    ReflectionTestUtils.setField(req, "symbol", "USDJPY");
    ReflectionTestUtils.setField(req, "depth", (short) 5);
    return req;
  }

  // --- テストデータビルダー ---

  private ZigZagSearchItem buildSearchItem() {
    ZigZagSearchItem item = new ZigZagSearchItem();
    item.setSymbol("USDJPY");
    item.setDepth(5);
    item.setTarget4h(buildInfoSmaFibonacci(100));
    item.setCurrent(buildInfoSmaFibonacci(101));
    item.setPrevious(buildInfo(98));
    item.setNext(buildInfo(102));
    item.setNext2(buildInfo(104));
    item.setNextRsRate(BigDecimal.valueOf(0.382));
    item.setNext2MaxRate(BigDecimal.valueOf(0.618));
    item.setWaveDxy4h(BigDecimal.ZERO);
    item.setWaveDxy1h(BigDecimal.ZERO);
    ZigZagSearchItem.FractalWave fw = new ZigZagSearchItem.FractalWave();
    fw.setWaveStart(LocalDateTime.of(2024, 1, 1, 0, 0));
    fw.setWave(100);
    item.setFractalWaveList(List.of(fw));
    return item;
  }

  private ZigZagSearchItem.InfoSmaFibonacci buildInfoSmaFibonacci(int wave) {
    ZigZagSearchItem.InfoSmaFibonacci info = new ZigZagSearchItem.InfoSmaFibonacci();
    info.setWave(wave);
    info.setResistance(BigDecimal.valueOf(150.000));
    info.setSupport(BigDecimal.valueOf(148.000));
    info.setFibonacci(buildFibonacci());
    info.setSma4h200s(buildSma(149.0));
    info.setSma4h75s(buildSma(149.2));
    info.setSma4h20s(buildSma(149.4));
    info.setSma1h200s(buildSma(149.1));
    info.setSma15m200s(buildSma(149.3));
    return info;
  }

  private ZigZagSearchItem.Fibonacci buildFibonacci() {
    ZigZagSearchItem.Fibonacci fib = new ZigZagSearchItem.Fibonacci();
    fib.setF1(BigDecimal.valueOf(150.000));
    fib.setF7(BigDecimal.valueOf(149.570));
    fib.setF6(BigDecimal.valueOf(149.236));
    fib.setF5(BigDecimal.valueOf(149.000));
    fib.setF3(BigDecimal.valueOf(148.764));
    fib.setF2(BigDecimal.valueOf(148.528));
    fib.setF0(BigDecimal.valueOf(148.000));
    fib.setPriceRange(BigDecimal.valueOf(2.000));
    return fib;
  }

  private ZigZagSearchItem.Sma buildSma(double priceS) {
    ZigZagSearchItem.Sma sma = new ZigZagSearchItem.Sma();
    sma.setPriceS(BigDecimal.valueOf(priceS));
    sma.setPriceE(BigDecimal.valueOf(priceS + 0.5));
    return sma;
  }

  private ZigZagSearchItem.Info buildInfo(int wave) {
    ZigZagSearchItem.Info info = new ZigZagSearchItem.Info();
    info.setWave(wave);
    info.setResistance(BigDecimal.valueOf(150.000));
    info.setSupport(BigDecimal.valueOf(148.000));
    return info;
  }

  private ZigZagBarDataRow buildBarDataRow() {
    ZigZagBarDataRow row = new ZigZagBarDataRow();
    row.setBarDateTime(LocalDateTime.of(2024, 1, 1, 0, 0));
    row.setOpenPrice(BigDecimal.valueOf(149.000));
    row.setHighPrice(BigDecimal.valueOf(150.000));
    row.setLowPrice(BigDecimal.valueOf(148.000));
    row.setClosePrice(BigDecimal.valueOf(149.500));
    row.setSma200(BigDecimal.valueOf(148.500));
    row.setSma75(BigDecimal.valueOf(149.000));
    row.setSma20(BigDecimal.valueOf(149.200));
    return row;
  }

}
