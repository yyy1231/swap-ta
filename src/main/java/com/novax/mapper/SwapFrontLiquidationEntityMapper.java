package com.novax.mapper;

import com.novax.entity.mysql.SwapFrontLiquidationEntity;

import java.util.List;

public interface SwapFrontLiquidationEntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SwapFrontLiquidationEntity record);

    SwapFrontLiquidationEntity selectByPrimaryKey(Long id);

    List<SwapFrontLiquidationEntity> selectAll();

    int updateByPrimaryKey(SwapFrontLiquidationEntity record);
}