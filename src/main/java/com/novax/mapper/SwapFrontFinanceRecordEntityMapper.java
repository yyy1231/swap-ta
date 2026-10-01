package com.novax.mapper;

import com.novax.entity.mysql.SwapFrontFinanceRecordEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SwapFrontFinanceRecordEntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SwapFrontFinanceRecordEntity record);

    SwapFrontFinanceRecordEntity selectByPrimaryKey(Long id);

    List<SwapFrontFinanceRecordEntity> selectAll();

    int updateByPrimaryKey(SwapFrontFinanceRecordEntity record);

    List<SwapFrontFinanceRecordEntity> selectByOrderId(@Param("userId") Long userId, @Param("orderId") Long orderId);

    List<SwapFrontFinanceRecordEntity> selectPeriodRecords(@Param("userId") Long userId, @Param("createTime") Date zeroTime);

    SwapFrontFinanceRecordEntity selectLiquidationRecord(@Param("userId") Long userId, @Param("orderId") Long orderId);
}