package com.novax.mapper;

import com.novax.entity.mysql.SwapFrontFundsRateEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SwapFrontFundsRateEntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SwapFrontFundsRateEntity record);

    SwapFrontFundsRateEntity selectByPrimaryKey(Long id);

    List<SwapFrontFundsRateEntity> selectAll();

    int updateByPrimaryKey(SwapFrontFundsRateEntity record);

    List<SwapFrontFundsRateEntity> selectBySymbol(String symbol);

    List<SwapFrontFundsRateEntity> selectBySymbolAndTime(@Param("symbol") String symbol, @Param("createTime") Date nDaysAgo);
}