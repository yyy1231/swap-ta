package com.novax.testcase;

import com.novax.entity.mysql.SwapFrontOrderEntity;
import com.novax.entity.mysql.SwapFrontOrderRecordEntity;
import com.novax.mapper.SwapFrontOrderEntityMapper;
import com.novax.mapper.SwapFrontOrderRecordEntityMapper;
import com.novax.util.MyBatisUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 计算单笔订单开仓均价
 * 开仓价格=1/(1/价格1*张数1 + 1/价格2*张数2 +…) *总张数
 * 即=总张数/(张数1/价格1 + 张数2/价格2 +…)
 */

public class AveragePriceOfOpenPositionCalculation {
    static Logger logger = Logger.getLogger(AveragePriceOfOpenPositionCalculation.class);

    // 单个订单
    public static BigDecimal averagePriceOfOrder(Long orderId) {
        SwapFrontOrderEntity orderEntity = MyBatisUtil.execute(SwapFrontOrderEntityMapper.class, m -> m.selectByPrimaryKey(orderId));
        Long totalTradedVolume = orderEntity.getTradeVolume();
        // 查询订单对应的成交记录
        List<SwapFrontOrderRecordEntity> transactionRecordEntityList = MyBatisUtil.execute(SwapFrontOrderRecordEntityMapper.class, m -> m.selectByOrderId(orderId));
        BigDecimal median = BigDecimal.ZERO;
        for (SwapFrontOrderRecordEntity transactionRecord : transactionRecordEntityList) {
            Long tradeVolume = transactionRecord.getTradeVolume();
            BigDecimal tradePrice = transactionRecord.getTradePrice();
            BigDecimal medianValue = BigDecimal.valueOf(tradeVolume).divide(tradePrice, 15, RoundingMode.FLOOR);
            median = median.add(medianValue);
        }
        BigDecimal averagePrice = BigDecimal.valueOf(totalTradedVolume).divide(median, 12, RoundingMode.FLOOR);
        logger.info("订单：" + orderId + "开仓均价：" + averagePrice);
        return averagePrice;
    }

    // 存量仓位+合并订单
    public static BigDecimal averagePriceOfPosition(int positonVolume, BigDecimal positionPrice, int orderVolume, BigDecimal orderPrice) {
        int totalVolume = positonVolume + orderVolume;
        BigDecimal medianValue1 = BigDecimal.valueOf(positonVolume).divide(positionPrice, 15, RoundingMode.FLOOR);
        BigDecimal medianValue2 = BigDecimal.valueOf(orderVolume).divide(orderPrice, 15, RoundingMode.FLOOR);
        BigDecimal medianValue = medianValue1.add(medianValue2);
        return BigDecimal.valueOf(totalVolume).divide(medianValue, 12, RoundingMode.FLOOR);
    }

    public static void main(String[] args) throws IOException {
        averagePriceOfOrder(523862518371061760L);
    }
}
