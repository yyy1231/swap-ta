package com.novax.mapper;

import com.novax.entity.mysql.SwapPositionFactorEntity;

import java.util.List;

public interface SwapPositionFactorEntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SwapPositionFactorEntity record);

    SwapPositionFactorEntity selectByPrimaryKey(Long id);

    List<SwapPositionFactorEntity> selectAll();

    int updateByPrimaryKey(SwapPositionFactorEntity record);
}