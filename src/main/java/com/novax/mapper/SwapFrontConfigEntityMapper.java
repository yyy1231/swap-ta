package com.novax.mapper;

import com.novax.entity.mysql.SwapFrontConfigEntity;

import java.util.List;

public interface SwapFrontConfigEntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SwapFrontConfigEntity record);

    SwapFrontConfigEntity selectByPrimaryKey(Long id);

    List<SwapFrontConfigEntity> selectAll();

    int updateByPrimaryKey(SwapFrontConfigEntity record);

    SwapFrontConfigEntity selectBySymbol(String symbol);

    List<SwapFrontConfigEntity> selectMaxLeverageSymbolList();

    List<SwapFrontConfigEntity> selectSwapConfig();
}