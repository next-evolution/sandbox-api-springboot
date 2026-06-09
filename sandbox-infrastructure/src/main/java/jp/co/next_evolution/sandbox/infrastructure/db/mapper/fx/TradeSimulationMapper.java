package jp.co.next_evolution.sandbox.infrastructure.db.mapper.fx;

import java.math.BigDecimal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TradeSimulationMapper {

  BigDecimal getOpenPrice(
      @Param("symbol") String symbol,
      @Param("contractHm") String contractHm
  );

}
