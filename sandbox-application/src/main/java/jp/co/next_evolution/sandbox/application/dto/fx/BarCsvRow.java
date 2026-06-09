package jp.co.next_evolution.sandbox.application.dto.fx;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({"barDateTime",
                    "openPrice",
                    "highPrice",
                    "lowPrice",
                    "closePrice",
                    "volume",
                    "sma200",
                    "sma75",
                    "sma20",
                    "rsi",
                    "rsiMa"
})
@Data
@NoArgsConstructor
public class BarCsvRow {

  private String barDateTime;

  private BigDecimal openPrice;

  private BigDecimal highPrice;

  private BigDecimal lowPrice;

  private BigDecimal closePrice;

  private Integer volume;

  private BigDecimal sma200;

  private BigDecimal sma75;

  private BigDecimal sma20;

  private BigDecimal rsi;

  private BigDecimal rsiMa;

  private String regularBullish;

  private String regularBullishLabel;

  private String regularBearish;

  private String regularBearishLabel;

}
