package jp.co.next_evolution.sandbox.api.dto.request.fx;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jp.co.next_evolution.sandbox.domain.model.fx.trade.EntryType;
import jp.co.next_evolution.sandbox.domain.model.fx.trade.TradeEntry;
import jp.co.next_evolution.sandbox.domain.model.fx.trade.TradePosition;
import jp.co.next_evolution.sandbox.domain.model.fx.trade.TradeType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TradeSimulationRequest {

  @Schema(requiredMode = REQUIRED)
  @Digits(integer = 6, fraction = 0)
  private BigDecimal riskAmount;

  @Schema(requiredMode = REQUIRED)
  @Positive
  private BigDecimal firstLotRatio;

  @Schema(requiredMode = REQUIRED)
  @Valid
  private EntryParam entry;

  @Schema(requiredMode = REQUIRED)
  @Valid
  private List<PositionParam> positionList;

  @Getter
  @NoArgsConstructor
  public static class EntryParam {

    private Long id;

    @Schema(requiredMode = REQUIRED)
    @NotBlank
    private String tradeVersion;

    @Schema(requiredMode = REQUIRED)
    private EntryType entryType;

    @Schema(requiredMode = REQUIRED)
    @Pattern(regexp = "[A-Z][A-Z0-9]{2,6}", message = "例: USDJPY")
    private String symbol;

    @Schema(requiredMode = REQUIRED, implementation = TradeType.class)
    private TradeType tradeType;

    @Schema(requiredMode = REQUIRED, example = "2026-01-02T11:22:33+09:00")
    private LocalDateTime contractAt;

    @Schema(requiredMode = REQUIRED)
    @NotBlank
    private String fibonacciType;

    @Schema(requiredMode = REQUIRED)
    @NotBlank
    private String fibonacciBar;

    @Schema(requiredMode = REQUIRED)
    @Digits(integer = 3, fraction = 5)
    private BigDecimal contractPrice;

    @Schema(requiredMode = REQUIRED)
    @Digits(integer = 3, fraction = 5)
    private BigDecimal lossPrice;

    private int positionRatio;

    @Schema(requiredMode = REQUIRED)
    @Digits(integer = 3, fraction = 5)
    private BigDecimal priceJpy;

    private BigDecimal lot;
    private int settlementAmount;
    private int lossPips;
    private BigDecimal settlementRatio;
    private String comment;
    private String imagePath;

    public TradeEntry toDomain() {
      return TradeEntry.builder()
                       .id(id)
                       .tradeVersion(tradeVersion)
                       .entryType(entryType)
                       .symbol(symbol)
                       .tradeType(tradeType)
                       .contractAt(contractAt)
                       .fibonacciType(fibonacciType)
                       .fibonacciBar(fibonacciBar)
                       .contractPrice(contractPrice)
                       .lossPrice(lossPrice)
                       .positionRatio(positionRatio)
                       .priceJpy(priceJpy)
                       .lot(lot != null ? lot : BigDecimal.ZERO)
                       .settlementAmount(settlementAmount)
                       .lossPips(lossPips)
                       .settlementRatio(settlementRatio != null ? settlementRatio : BigDecimal.ZERO)
                       .comment(comment)
                       .imagePath(imagePath)
                       .build();
    }

  }

  @Getter
  @NoArgsConstructor
  public static class PositionParam {

    private Long id;

    @Schema(requiredMode = REQUIRED)
    @Positive
    private short positionNumber;

    @Schema(requiredMode = REQUIRED)
    @Digits(integer = 3, fraction = 5)
    private BigDecimal settlementPrice;

    private int settlementPips;
    private BigDecimal settlementRatio;
    private BigDecimal lot;
    private int profitAmount;
    private int lossAmount;

    public TradePosition toDomain() {
      return TradePosition.builder()
                          .id(id)
                          .positionNumber(positionNumber)
                          .settlementPrice(settlementPrice)
                          .settlementPips(settlementPips)
                          .settlementRatio(
                              settlementRatio != null ? settlementRatio : BigDecimal.ZERO)
                          .lot(lot != null ? lot : BigDecimal.ZERO)
                          .profitAmount(profitAmount)
                          .lossAmount(lossAmount)
                          .build();
    }

  }

}
