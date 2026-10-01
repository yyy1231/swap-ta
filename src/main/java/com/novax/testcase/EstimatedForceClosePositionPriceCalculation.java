package com.novax.testcase;

import com.novax.entity.mysql.SwapFrontPositionEntity;
import com.novax.util.NewestPriceUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


/**
 * 计算预估强平价
 * 全仓
 * 单向持仓
 * 开多：预估强平价=最新价格-[保证金余额-所有仓位的维持保证金之和]/[该多头仓位数量(张数)*每张价值/开仓均价]
 * 开空：预估强平价=最新价格+[保证金余额-所有仓位的维持保证金之和]/[该空头仓位数量(张数)*每张面值/开仓均价]
 * 双向持仓  同币对多空仓位强平价格一样
 * 1、同币种双向净多头
 * 预估强平价=最新价格-[保证金余额-所有仓位的维持保证金之和]/[仓位净多头数量(张数)*每张价值/开仓均价]
 * 2、同币种双向净空头
 * 预估强平价=最新价格+[保证金余额-所有仓位的维持保证金之和]/[仓位净空头数量(张数)*每张价值/开仓均价]
 * 3、同币种双向持仓，且净头寸为0，即多头张数 = 空头张数
 * 预估强平价：这时候不做计算，即仓位不会爆仓。在前端页面显示，多空两个仓位的爆仓价均显示为“--”
 * 4、双向持仓加其他币种持仓
 * 逐仓
 * 1、开多
 * 预估强平价 = 最新价格 -[仓位保证金 + 该逐仓仓位未实现盈亏 - 该逐仓仓位的维持保证金] / [仓位数量(张数) * 每张面值/ 开仓均价]
 * 2、开空
 * 预估强平价 = 最新价格 +[仓位保证金 + 该逐仓仓位未实现盈亏 - 该逐仓仓位的维持保证金] / [仓位数量(张数) * 每张面值/ 开仓均价]
 * 分仓：
 * 单向持仓
 * 开多：预估强平价=最新价格-[该交易对保证金余额-该交易对所有仓位的维持保证金之和]/[该多头仓位数量(张数)*每张价值/开仓均价]
 * 开空：预估强平价=最新价格+[该交易对保证金余额-该交易对所有仓位的维持保证金之和]/[该空头仓位数量(张数)*每张面值/开仓均价]
 * 双向持仓  同币对多空仓位强平价格一样
 * 1、同币种双向净多头
 * 预估强平价=最新价格-[该交易对保证金余额-该交易对所有仓位的维持保证金之和]/[该交易对仓位净多头数量(张数)*每张价值/开仓均价]
 * 2、同币种双向净空头
 * 预估强平价=最新价格+[该交易对保证金余额-该交易对所有仓位的维持保证金之和]/[该交易对仓位净空头数量(张数)*每张价值/开仓均价]
 * 3、同币种双向持仓，且净头寸为0，即多头张数 = 空头张数
 * 预估强平价：这时候不做计算，即仓位不会爆仓。在前端页面显示，多空两个仓位的爆仓价均显示为“--”
 * 4、双向持仓加其他币种持仓
 */
public class EstimatedForceClosePositionPriceCalculation {
    static Logger logger = Logger.getLogger(EstimatedForceClosePositionPriceCalculation.class);

    // 同币种双向持仓，多空张数相等，开仓价不同的场景下，随行情波动，多空仓位盈亏不能相抵，so,目前提供的预估平仓价公式存在bug,待产品确认修正方案（暂不处理）
    public static BigDecimal estimatedForceClosePositonPrice(SwapFrontPositionEntity positionEntity) {
        // 基础信息
        String symbol = positionEntity.getSymbol();
        int mode = positionEntity.getMode();
        int direction = positionEntity.getDirection();
        long volume = positionEntity.getVolume();
        BigDecimal contractSize = positionEntity.getContractSize();
        BigDecimal averageOpeningPrice = positionEntity.getCostOpen();
        Long userId = positionEntity.getUserId();

        // 最新价
        BigDecimal newestPrice = NewestPriceUtil.newPriceOfSwap(symbol);
        logger.info("币对" + symbol + "合约最新价：" + newestPrice);

        // 查询用户持有指定币对的仓位
        List<SwapFrontPositionEntity> positionEntityList = TraderTransactionStatistics.queryPositions2(userId, symbol, null);
        BigDecimal estimatedForceClosePositonPrice = BigDecimal.ZERO;
        // 全仓
        if (mode != 2) {
            // 未实现盈亏
            BigDecimal unrealizedProfitOrLoss;
            // 账户余额
            BigDecimal accountBalance;
            // 保证金余额
            BigDecimal marginBalance;
            // 维持保证金之和
            BigDecimal totalMaintainMargin;
            if (mode == 1) { // 全仓
                unrealizedProfitOrLoss = TraderTransactionStatistics.unrealizedProfitOrLoss(userId);
                accountBalance = UserAssetsStatistics.swapAccountAvailable(userId, "usdt", null);
                marginBalance = accountBalance.add(unrealizedProfitOrLoss);
                totalMaintainMargin = TraderTransactionStatistics.maintainMarginOfAccount(userId, null);
            } else { // 分仓
                unrealizedProfitOrLoss = TraderTransactionStatistics.unrealizedProfitOrLoss(userId, symbol);
                accountBalance = UserAssetsStatistics.swapAccountAvailable(userId, "usdt", symbol);
                marginBalance = accountBalance.add(unrealizedProfitOrLoss);
                totalMaintainMargin = TraderTransactionStatistics.maintainMarginOfAccount(userId, symbol);
            }

            logger.info("用户" + userId + "，所有仓位的未实现盈亏：" + unrealizedProfitOrLoss);
            logger.info("用户" + userId + "，合约账户可用余额：" + accountBalance);
            logger.info("用户" + userId + "，保证金余额：" + marginBalance);
            logger.info("用户" + userId + "，所有仓位的维持保证金之和：" + totalMaintainMargin);

            if (positionEntityList.size() == 1) { // 单向持仓（同一币对，多或空） 即该币对仅有当前计算的仓位
                // 剩余可用保证金可支撑持有数量币的价格浮动空间
                BigDecimal priceFluctuationSpace = (marginBalance.subtract(totalMaintainMargin)).divide(BigDecimal.valueOf(volume).multiply(contractSize)
                        .divide(averageOpeningPrice, 5, RoundingMode.HALF_UP), 5, RoundingMode.HALF_UP);
                if (direction == 1) { // 开多
                    estimatedForceClosePositonPrice = newestPrice.subtract(priceFluctuationSpace);
                } else { // 开空
                    estimatedForceClosePositonPrice = newestPrice.add(priceFluctuationSpace);
                }
                logger.info("用户" + userId + "，交易对：" + symbol + "，全仓，单向持仓" + "，方向" + direction + "，预估强评价：" + estimatedForceClosePositonPrice);
            } else if (positionEntityList.size() == 2) { // 双向持仓（同一币对，多和空）
                long longVolume = 0;
                long shortVolume = 0;
                BigDecimal averageOpeningPriceOfLong = null;
                BigDecimal averageOpeningPriceOfShort = null;
                for (SwapFrontPositionEntity position : positionEntityList) {
                    if (position.getDirection() == 1) {
                        longVolume = position.getVolume();
                        averageOpeningPriceOfLong = position.getCostOpen();
                    } else if (position.getDirection() == 2) {
                        shortVolume = position.getVolume();
                        averageOpeningPriceOfShort = position.getCostOpen();
                    } else {
                        logger.info("direction参数不合法");
                    }
                }
                long netVolume = longVolume - shortVolume;
                if (netVolume > 0) { // 1、同币种双向净多头
                    estimatedForceClosePositonPrice = newestPrice.subtract((marginBalance.subtract(totalMaintainMargin)).divide(BigDecimal.valueOf(netVolume).multiply(contractSize)
                            .divide(averageOpeningPriceOfLong, 5, RoundingMode.HALF_UP), 5, RoundingMode.HALF_UP));
                    logger.info("用户" + userId + "，交易对：" + symbol + "，全仓，双向净多，预估强评价：" + estimatedForceClosePositonPrice);

                } else if (netVolume < 0) { // 2、同币种双向净空头
                    estimatedForceClosePositonPrice = newestPrice.add((marginBalance.subtract(totalMaintainMargin)).divide(BigDecimal.valueOf(Math.abs(netVolume)).multiply(contractSize)
                            .divide(averageOpeningPriceOfShort, 5, RoundingMode.HALF_UP), 5, RoundingMode.HALF_UP));
                    logger.info("用户" + userId + "，交易对：" + symbol + "，全仓，双向净空，预估强评价：" + estimatedForceClosePositonPrice);

                } else { // 3、同币种双向持仓，且净头寸为0，即多头张数 = 空头张数
                    // estimatedForceClosePositonPrice = BigDecimal.ZERO;
                    logger.info("全仓，同币种双向持仓，且净头寸为0，即多头张数 = 空头张数。此场景，预估强平价不做计算，即仓位不会爆仓");
                }
            } else {
                logger.info("没有该币对的持仓：" + symbol);
            }
        } else { // 逐仓
            // 仓位保证金--用于逐仓模式
            // BigDecimal marginOfPositon = positionEntity.getMargin().add(positionEntity.getAddMargin());
            BigDecimal marginOfPositon = positionEntity.getMargin();
            // 仓位未实现盈亏
            BigDecimal unrealizedProfitOrLossOfPositon = TraderTransactionStatistics
                    .unrealizedProfitOrLoss(direction, newestPrice, averageOpeningPrice, volume, contractSize);
            logger.info("用户" + userId + "，交易对：" + symbol + "，逐仓" + "，方向" + direction + "，未实现盈亏：" + unrealizedProfitOrLossOfPositon);
            // 仓位维持保证金
            BigDecimal maintainMarginOfPositon = TraderTransactionStatistics.maintainMarginOfPosition(positionEntity);
            logger.info("用户" + userId + "，交易对：" + symbol + "，逐仓" + "，方向" + direction + "，仓位维持保证金：" + maintainMarginOfPositon);
            // 剩余可用保证金可支撑持有数量币的价格浮动空间
            BigDecimal priceFluctuationSpace = (marginOfPositon.add(unrealizedProfitOrLossOfPositon).subtract(maintainMarginOfPositon))
                    .divide(BigDecimal.valueOf(volume).multiply(contractSize)
                            .divide(averageOpeningPrice, 5, RoundingMode.HALF_UP), 5, RoundingMode.HALF_UP);
            if (positionEntity.getDirection() == 1) {// 开多
                // 预估强平价=最新价格-[仓位保证金+该逐仓仓位未实现盈亏-该逐仓仓位的维持保证金]/[仓位数量(张数)*每张面值/开仓均价]
                estimatedForceClosePositonPrice = newestPrice.subtract(priceFluctuationSpace);
                logger.info("用户" + userId + "，交易对：" + symbol + "，逐仓，开多，预估强评价：" + estimatedForceClosePositonPrice);
            } else {// 开空
                // 预估强平价=最新价格+[仓位保证金+该逐仓仓位未实现盈亏-该逐仓仓位的维持保证金]/[仓位数量(张数)*每张面值/开仓均价]
                estimatedForceClosePositonPrice = newestPrice.add(priceFluctuationSpace);
                logger.info("用户" + userId + "，交易对：" + symbol + "，逐仓，开空，预估强评价：" + estimatedForceClosePositonPrice);
            }

        }
        if (estimatedForceClosePositonPrice.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return estimatedForceClosePositonPrice;
    }

    public static void main(String[] args) throws IOException {
        List<SwapFrontPositionEntity> entitys = TraderTransactionStatistics.queryPositions2(28649983L, "eth_usdt", null);
        System.out.println(estimatedForceClosePositonPrice(entitys.get(0)));
    }
}


