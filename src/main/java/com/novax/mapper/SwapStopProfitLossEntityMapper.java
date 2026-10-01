package com.novax.mapper;

import com.novax.entity.mysql.SwapStopProfitLossEntity;

import java.util.List;

public interface SwapStopProfitLossEntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SwapStopProfitLossEntity record);

    SwapStopProfitLossEntity selectByPrimaryKey(Long id);

    List<SwapStopProfitLossEntity> selectAll();

    int updateByPrimaryKey(SwapStopProfitLossEntity record);

    SwapStopProfitLossEntity selectByOrderId(Long orderId);
}