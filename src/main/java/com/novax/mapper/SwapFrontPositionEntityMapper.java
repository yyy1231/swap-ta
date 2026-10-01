package com.novax.mapper;

import com.novax.entity.mysql.SwapFrontPositionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SwapFrontPositionEntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SwapFrontPositionEntity record);

    SwapFrontPositionEntity selectByPrimaryKey(Long id);

    List<SwapFrontPositionEntity> selectAll();

    int updateByPrimaryKey(SwapFrontPositionEntity record);

    SwapFrontPositionEntity selectBySymbolAndDirection(@Param("userId") Long userId, @Param("symbol") String symbol, @Param("direction") Byte direction);

    List<SwapFrontPositionEntity> selectLongPosition(Long userId);

    List<SwapFrontPositionEntity> selectShortPosition(Long userId);

    List<SwapFrontPositionEntity> selectPositions(@Param("userId") Long userId, @Param("symbol") String symbol, @Param("direction") Integer direction);
}