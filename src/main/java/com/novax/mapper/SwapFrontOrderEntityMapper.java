package com.novax.mapper;

import com.novax.entity.mysql.SwapFrontOrderEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SwapFrontOrderEntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SwapFrontOrderEntity record);

    SwapFrontOrderEntity selectByPrimaryKey(Long id);

    List<SwapFrontOrderEntity> selectAll();

    int updateByPrimaryKey(SwapFrontOrderEntity record);

    List<SwapFrontOrderEntity> selectByUserId(Long userId);

    List<SwapFrontOrderEntity> selectByStatus(@Param("userId") Long userId, @Param("createTime") Date zeroTime);


    List<SwapFrontOrderEntity> selectBySymbol(@Param("userId") Long userId, @Param("symbol") String symbol);

    List<SwapFrontOrderEntity> selectHistoryOrders(@Param("userId") Long userId, @Param("createTime") Date zeroTime);

    List<SwapFrontOrderEntity> selectOrdersWithStopProfitLoss(Long userId);

    List<SwapFrontOrderEntity> selectOrders(@Param("userId") Long userId, @Param("symbol") String symbol, @Param("tradeType") byte tradeType);
}