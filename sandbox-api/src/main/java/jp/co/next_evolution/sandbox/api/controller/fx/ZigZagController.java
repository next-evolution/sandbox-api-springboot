package jp.co.next_evolution.sandbox.api.controller.fx;

import java.util.List;
import java.util.stream.Collectors;
import jp.co.next_evolution.sandbox.api.dto.request.fx.ZigZagBarDataRequest;
import jp.co.next_evolution.sandbox.api.dto.request.fx.ZigZagGenerateRequest;
import jp.co.next_evolution.sandbox.api.dto.request.fx.ZigZagSearchRequest;
import jp.co.next_evolution.sandbox.api.dto.request.fx.ZigZagStatusRequest;
import jp.co.next_evolution.sandbox.api.dto.response.fx.ZigZagBarDataResponse;
import jp.co.next_evolution.sandbox.api.dto.response.fx.ZigZagBarDataResponse.ZigZagBarData;
import jp.co.next_evolution.sandbox.api.dto.response.fx.ZigZagGenerateResponse;
import jp.co.next_evolution.sandbox.api.dto.response.fx.ZigZagResult;
import jp.co.next_evolution.sandbox.api.dto.response.fx.ZigZagSearchResponse;
import jp.co.next_evolution.sandbox.api.dto.response.fx.ZigZagStatusItem;
import jp.co.next_evolution.sandbox.api.dto.response.fx.ZigZagStatusResponse;
import jp.co.next_evolution.sandbox.api.type.ReturnCode;
import jp.co.next_evolution.sandbox.application.command.fx.ZigZagBarDataCommand;
import jp.co.next_evolution.sandbox.application.command.fx.ZigZagGenerateCommand;
import jp.co.next_evolution.sandbox.application.command.fx.ZigZagSearchCommand;
import jp.co.next_evolution.sandbox.application.command.fx.ZigZagStatusCommand;
import jp.co.next_evolution.sandbox.application.dto.fx.ZigZagSearchItem;
import jp.co.next_evolution.sandbox.application.usecase.fx.zigzag.GenerateZigZagUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.zigzag.GetZigZagBarDataUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.zigzag.GetZigZagStatusUseCase;
import jp.co.next_evolution.sandbox.application.usecase.fx.zigzag.SearchZigZagUseCase;
import jp.co.next_evolution.sandbox.domain.model.fx.BarType;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZagBarDataRow;
import jp.co.next_evolution.sandbox.domain.model.fx.zigzag.ZigZagStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/fx/zigzag")
@RequiredArgsConstructor
public class ZigZagController {

  private final SearchZigZagUseCase searchZigZagUseCase;

  private final GetZigZagStatusUseCase getZigZagStatusUseCase;

  private final GenerateZigZagUseCase generateZigZagUseCase;

  private final GetZigZagBarDataUseCase getZigZagBarDataUseCase;

  @PostMapping
  public ResponseEntity<ZigZagSearchResponse> search(
      @RequestBody @Validated ZigZagSearchRequest req) {

    SearchZigZagUseCase.SearchResult result = searchZigZagUseCase.execute(
        new ZigZagSearchCommand(
            BarType.of(req.getBarType()),
            req.getSymbol(),
            req.getDepth(),
            req.getBarDateTimeMin(),
            req.getBarDateTimeMax(),
            req.getWave(),
            req.getPreviousWave(),
            req.getNextWave(),
            req.getNext2Wave(),
            req.getDirection4h200(),
            req.getDirection4h75(),
            req.getDirection4h20(),
            req.getDirection1h200(),
            req.getDirection15m200(),
            req.getWave4h(),
            req.getDirectionTarget4h200(),
            req.getPage(),
            req.getSize()
        )
    );

    List<ZigZagResult> list = result.list().stream()
        .map(this::toResult)
        .collect(Collectors.toList());

    return ResponseEntity.ok(ZigZagSearchResponse.builder()
        .returnCode(ReturnCode.Ok)
        .totalCount(result.totalCount())
        .searchCount(result.totalCount())
        .totalPage(result.totalPage())
        .list(list)
        .build());

  }

  @PostMapping("/status")
  public ResponseEntity<ZigZagStatusResponse> status(
      @RequestBody @Validated ZigZagStatusRequest req) {

    List<ZigZagStatus> statusList = getZigZagStatusUseCase.execute(
        new ZigZagStatusCommand(req.getSymbolType(), BarType.of(req.getBarType()), req.getDepth())
    );

    List<ZigZagStatusItem> itemList = statusList.stream()
        .map(this::toStatusItem)
        .collect(Collectors.toList());

    return ResponseEntity.ok(ZigZagStatusResponse.builder()
        .returnCode(ReturnCode.Ok)
        .totalCount(itemList.size())
        .searchCount(itemList.size())
        .totalPage(1)
        .list(itemList)
        .build());

  }

  @PostMapping("/generate")
  public ResponseEntity<ZigZagGenerateResponse> generate(
      @RequestBody @Validated ZigZagGenerateRequest req) {

    GenerateZigZagUseCase.GenerateResult result = generateZigZagUseCase.execute(
        new ZigZagGenerateCommand(
            req.getSymbol(),
            BarType.of(req.getBarType()),
            req.getDepth(),
            req.getBarDateTime(),
            req.getLoadSize()
        )
    );

    ReturnCode returnCode = result.warn() ? ReturnCode.Warn : ReturnCode.Ok;
    String message = result.warn() ? result.status().getMessage() : null;

    return ResponseEntity.ok(ZigZagGenerateResponse.builder()
        .returnCode(returnCode)
        .message(message)
        .status(toStatusItem(result.status()))
        .build());

  }

  @PostMapping("/bar-data")
  public ResponseEntity<ZigZagBarDataResponse> barData(
      @RequestBody @Validated ZigZagBarDataRequest req) {

    GetZigZagBarDataUseCase.BarDataResult result = getZigZagBarDataUseCase.execute(
        new ZigZagBarDataCommand(
            req.getBarType(),
            req.getSymbol(),
            req.getDepth(),
            req.getWaveStart(),
            req.getWave()
        )
    );

    List<ZigZagBarData> barDataList = result.list().stream()
        .map(this::toBarData)
        .collect(Collectors.toList());

    return ResponseEntity.ok(ZigZagBarDataResponse.builder()
        .returnCode(ReturnCode.Ok)
        .barType(result.barType())
        .symbol(result.symbol())
        .depth(result.depth())
        .wave(result.wave())
        .zigZagBarDataList(barDataList)
        .build());

  }

  private ZigZagResult toResult(ZigZagSearchItem item) {
    ZigZagResult r = new ZigZagResult();
    r.setSymbol(item.getSymbol());
    r.setDepth(item.getDepth());
    r.setTarget4h(toInfoSmaFibonacci(item.getTarget4h()));
    r.setCurrent(toInfoSmaFibonacci(item.getCurrent()));
    r.setPrevious(toInfo(item.getPrevious()));
    r.setNext(toInfo(item.getNext()));
    r.setNext2(toInfo(item.getNext2()));
    r.setNextRsRate(item.getNextRsRate());
    r.setNext2MaxRate(item.getNext2MaxRate());
    r.setWaveDxy4h(item.getWaveDxy4h());
    r.setWaveDxy1h(item.getWaveDxy1h());
    r.setFractalWaveList(item.getFractalWaveList().stream()
        .map(this::toFractalWave)
        .collect(Collectors.toList()));
    return r;
  }

  private ZigZagResult.InfoSmaFibonacci toInfoSmaFibonacci(ZigZagSearchItem.InfoSmaFibonacci src) {
    ZigZagResult.InfoSmaFibonacci t = new ZigZagResult.InfoSmaFibonacci();
    t.setWaveStart(src.getWaveStart());
    t.setWaveEnd(src.getWaveEnd());
    t.setWave(src.getWave());
    t.setResistance(src.getResistance());
    t.setSupport(src.getSupport());
    t.setFibonacci(toFibonacci(src.getFibonacci()));
    t.setSma4h200s(toSma(src.getSma4h200s()));
    t.setSma4h75s(toSma(src.getSma4h75s()));
    t.setSma4h20s(toSma(src.getSma4h20s()));
    t.setSma1h200s(toSma(src.getSma1h200s()));
    t.setSma15m200s(toSma(src.getSma15m200s()));
    return t;
  }

  private ZigZagResult.Info toInfo(ZigZagSearchItem.Info src) {
    ZigZagResult.Info t = new ZigZagResult.Info();
    t.setWaveStart(src.getWaveStart());
    t.setWaveEnd(src.getWaveEnd());
    t.setWave(src.getWave());
    t.setResistance(src.getResistance());
    t.setSupport(src.getSupport());
    return t;
  }

  private ZigZagResult.Fibonacci toFibonacci(ZigZagSearchItem.Fibonacci src) {
    if (src == null) {
      return null;
    }
    ZigZagResult.Fibonacci t = new ZigZagResult.Fibonacci();
    t.setF1(src.getF1());
    t.setF7(src.getF7());
    t.setF6(src.getF6());
    t.setF5(src.getF5());
    t.setF3(src.getF3());
    t.setF2(src.getF2());
    t.setF0(src.getF0());
    t.setPriceRange(src.getPriceRange());
    return t;
  }

  private ZigZagResult.Sma toSma(ZigZagSearchItem.Sma src) {
    ZigZagResult.Sma t = new ZigZagResult.Sma();
    t.setPriceS(src.getPriceS());
    t.setPriceE(src.getPriceE());
    t.setDeviation(src.getDeviation());
    t.setFibonacci(src.getFibonacci());
    t.setDirection(src.getDirection());
    t.setPosition(src.getPosition());
    return t;
  }

  private ZigZagResult.FractalWave toFractalWave(ZigZagSearchItem.FractalWave src) {
    ZigZagResult.FractalWave t = new ZigZagResult.FractalWave();
    t.setWaveStart(src.getWaveStart());
    t.setWave(src.getWave());
    return t;
  }

  private ZigZagBarData toBarData(ZigZagBarDataRow row) {
    ZigZagBarData d = new ZigZagBarData();
    d.setBarDateTime(row.getBarDateTime());
    d.setOpenPrice(row.getOpenPrice());
    d.setHighPrice(row.getHighPrice());
    d.setLowPrice(row.getLowPrice());
    d.setClosePrice(row.getClosePrice());
    d.setSma200(row.getSma200());
    d.setSma75(row.getSma75());
    d.setSma20(row.getSma20());
    return d;
  }

  private ZigZagStatusItem toStatusItem(ZigZagStatus s) {
    ZigZagStatusItem item = new ZigZagStatusItem();
    item.setSymbolType(s.getSymbolType());
    item.setBarType(s.getBarType());
    item.setSymbol(s.getSymbol());
    item.setDepth(s.getDepth());
    item.setBarDateTimeMin(s.getBarDateTimeMin());
    item.setBarDateTimeMax(s.getBarDateTimeMax());
    item.setBarCount(s.getBarCount());
    item.setBarDateTimeMinZigZag(s.getBarDateTimeMinZigZag());
    item.setBarDateTimeMaxZigZag(s.getBarDateTimeMaxZigZag());
    item.setZigzagCount(s.getZigzagCount());
    item.setBreakResistanceCount(s.getBreakResistanceCount());
    item.setBreakSupportCount(s.getBreakSupportCount());
    item.setMessage(s.getMessage());
    return item;
  }

}
