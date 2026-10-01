package com.novax.mapper;

import com.novax.entity.mysql.SwapFrontOrderPlanEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SwapFrontOrderPlanEntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SwapFrontOrderPlanEntity record);

    SwapFrontOrderPlanEntity selectByPrimaryKey(Long id);

    List<SwapFrontOrderPlanEntity> selectAll();

    int updateByPrimaryKey(SwapFrontOrderPlanEntity record);

    List<SwapFrontOrderPlanEntity> selectByUserId(Long userId);

    List<SwapFrontOrderPlanEntity> selectByOrderId(@Param("userId") Long userId, @Param("orderId") Long orderId);

    List<SwapFrontOrderPlanEntity> selectBySymbol(@Param("userId") Long userId, @Param("symbol") String symbol);

    List<SwapFrontOrderPlanEntity> selectStopProfitLossOrders(Long userId);

    List<SwapFrontOrderPlanEntity> selectHistoryPlanOrders(Long userId);

    List<SwapFrontOrderPlanEntity> selectPeriodPlanOrders(@Param("userId") Long userId, @Param("createTime") Date zeroTime);

    List<SwapFrontOrderPlanEntity> selectPeriodHistoryPlanOrders(@Param("userId") Long userId, @Param("createTime") Date zeroTime);
}