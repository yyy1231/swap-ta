package com.novax.mapper;

import com.novax.entity.mysql.SwapFrontOrderRecordEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SwapFrontOrderRecordEntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SwapFrontOrderRecordEntity record);

    SwapFrontOrderRecordEntity selectByPrimaryKey(Long id);

    List<SwapFrontOrderRecordEntity> selectAll();

    int updateByPrimaryKey(SwapFrontOrderRecordEntity record);

    List<SwapFrontOrderRecordEntity> selectByOrderId(Long orderId);

    List<SwapFrontOrderRecordEntity> selectByUserId(Long userId);

    List<SwapFrontOrderRecordEntity> selectPeriodTransactions(@Param("userId") Long userId, @Param("createTime") Date zeroTime);

    List<SwapFrontOrderRecordEntity> selectTodayRecords(Long userId);
}