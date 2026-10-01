package com.novax.testcase;

import com.novax.entity.mysql.SwapMarginEntity;
import com.novax.pojo.CalculatorPosition;
import com.novax.pojo.CalculatorResults;
import com.novax.pojo.PositionCalculation;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 合约计算器
 * 计算预估强平价
 * 单向持仓 全仓&逐仓&分仓 计算结果一样
 * 开多：开仓均价 -【钱包余额 - 维持保证金】 /  【开仓数量(张数) * 每张面值(10/100U)/开仓均价】
 * 开空：开仓均价 +【钱包余额 - 维持保证金】 /  【开仓数量(张数) * 每张面值(10/100U)/开仓均价】
 * 双向持仓 全仓&分仓 计算结果一样  逐仓无双向持仓
 * 1、同币种双向净多头
 * 预估强平价 = 多头开仓均价 -【钱包余额 + 净多头钆差损益 - 多头仓位的维持保证金 - 空头仓位的维持保证金】 /  【仓位净多头数量(张数) * 每张面值(10/100U)】 * 多头开仓均价
 * 净多头钆差损益  = （空头开仓均价 - 多头开仓均价）/ 多头开仓均价 * 空头张数 * 每张面值
 * 2、同币种双向净空头
 * 预估强平价 = 空头开仓均价  +【钱包余额  + 净空头钆差损益- 空头仓位的维持保证金 - 多头仓位的维持保证金】 /  【仓位净空头数量(张数) * 每张面值(10/100U)】 * 空头开仓均价
 * 净空头钆差损益  = （空头开仓均价 - 多头开仓均价）/ 多头开仓均价 * 多头张数 * 每张面值
 * 3、同币种双向持仓，且净头寸为0，即多头张数 = 空头张数
 * 预估强平价：这时候不做计算，即仓位不会爆仓。在前端页面显示，多空两个仓位的爆仓价均显示为“--”
 */
public class SwapCalculator {
    static Logger logger = Logger.getLogger(SwapCalculator.class);

    // 强平价格  --单向持仓
    public static BigDecimal liquidationPriceOfOneWayPosition(String symbol, long volume, BigDecimal openPrice, int direction, BigDecimal walletBalance) {
        BigDecimal liquidationPrice;
        BigDecimal priceFluctuationSpace;
        // 面值
        BigDecimal contractSize = SwapConfigInfo.queryContractSize(symbol);
        // 维持保证金
        BigDecimal maintainMargin = TraderTransactionStatistics.maintainMarginOfPosition(symbol, volume);
        priceFluctuationSpace = (walletBalance.subtract(maintainMargin)).multiply(openPrice)
                .divide(BigDecimal.valueOf(volume).multiply(contractSize), 10, RoundingMode.FLOOR);
        if (direction == 1) {
            liquidationPrice = openPrice.subtract(priceFluctuationSpace);
        } else {
            liquidationPrice = openPrice.add(priceFluctuationSpace);
        }
        return liquidationPrice;
    }

    // 强平价格  --双向持仓
    public static BigDecimal liquidationPriceOfTwoWayPosition(String symbol, long longVolume, BigDecimal longOpenPrice, long shortVolume, BigDecimal shortOpenPrice, BigDecimal walletBalance) {
        // 强平价格
        BigDecimal liquidationPrice;
        // 净多头钆差损益/净空头钆差损益
        BigDecimal netProfitAndLoss;
        // 多头仓位的维持保证金
        BigDecimal longPositonsMaintainMargin = TraderTransactionStatistics.maintainMarginOfPosition(symbol, longVolume);
        // 空头仓位的维持保证金
        BigDecimal shortPositonsMaintainMargin = TraderTransactionStatistics.maintainMarginOfPosition(symbol, shortVolume);
        BigDecimal contractSize = SwapConfigInfo.queryContractSize(symbol);
        long netVolume = longVolume - shortVolume;
        if (netVolume > 0) {
            // 净多头钆差损益  = （空头开仓均价 - 多头开仓均价）/ 多头开仓均价 * 空头张数 * 每张面值
            netProfitAndLoss = shortOpenPrice.subtract(longOpenPrice).multiply(BigDecimal.valueOf(shortVolume))
                    .multiply(contractSize).divide(longOpenPrice, 5, RoundingMode.HALF_DOWN);
            // 预估强平价 = 多头开仓均价 -【钱包余额 + 净多头钆差损益 - 多头仓位的维持保证金 - 空头仓位的维持保证金】 /  【仓位净多头数量(张数) * 每张面值(10/100U)】 * 多头开仓均价
            liquidationPrice = longOpenPrice.subtract(
                    walletBalance.add(netProfitAndLoss).subtract(longPositonsMaintainMargin).subtract(shortPositonsMaintainMargin)
                            .multiply(longOpenPrice).divide(BigDecimal.valueOf(netVolume).multiply(contractSize), 5, RoundingMode.HALF_DOWN));
        } else if (netVolume < 0) {
            // 净空头钆差损益  = （空头开仓均价 - 多头开仓均价）/ 多头开仓均价 * 多头张数 * 每张面值
            netProfitAndLoss = shortOpenPrice.subtract(longOpenPrice).multiply(BigDecimal.valueOf(longVolume))
                    .multiply(contractSize).divide(longOpenPrice, 5, RoundingMode.HALF_DOWN);
            // 预估强平价 = 空头开仓均价  +【钱包余额  + 净空头钆差损益- 空头仓位的维持保证金 - 多头仓位的维持保证金】 /  【仓位净空头数量(张数) * 每张面值(10/100U)】 * 空头开仓均价
            liquidationPrice = shortOpenPrice.add(
                    walletBalance.add(netProfitAndLoss).subtract(longPositonsMaintainMargin).subtract(shortPositonsMaintainMargin)
                            .multiply(shortOpenPrice).divide(BigDecimal.valueOf(Math.abs(netVolume)).multiply(contractSize), 5, RoundingMode.HALF_DOWN));
        } else {
            liquidationPrice = BigDecimal.ZERO;
            logger.info("同币种双向持仓，且净头寸为0，即多头张数 = 空头张数。此场景，预估强平价不做计算，即仓位不会爆仓");
        }
        return liquidationPrice;
    }

    // 开仓均价 总张数/(张数1/价格1 + 张数2/价格2 +…) 开多&开空计算结果一样
    public static BigDecimal averagePriceOfOpenPosition(List<CalculatorPosition> positions) {
        BigDecimal averagePrice;
        Long totalVolumn = 0L;
        BigDecimal medianValue = BigDecimal.ZERO;
        for (CalculatorPosition position : positions) {
            Long openVolumn = position.getOpenVolume();
            BigDecimal openPrice = position.getOpenPrice();
            totalVolumn += openVolumn;
            medianValue = medianValue.add(BigDecimal.valueOf(openVolumn).divide(openPrice, 15, RoundingMode.HALF_DOWN));
        }
        averagePrice = BigDecimal.valueOf(totalVolumn).divide(medianValue, 5, RoundingMode.HALF_DOWN);
        return averagePrice;
    }

    // 收益
    public static CalculatorResults income(String symbol, long volume, BigDecimal openPrice, BigDecimal closePrice, int direction, int leverage) {
        CalculatorResults results = new CalculatorResults();
        // 面值
        BigDecimal contractSize = SwapConfigInfo.queryContractSize(symbol);
        // 数量
        BigDecimal quantity = BigDecimal.valueOf(volume).multiply(contractSize).divide(openPrice, 10, RoundingMode.HALF_DOWN);
        BigDecimal profit;
        if (direction == 1) {
            profit = closePrice.subtract(openPrice).multiply(quantity);
        } else {
            profit = openPrice.subtract(closePrice).multiply(quantity);
        }
        BigDecimal margin = BigDecimal.valueOf(volume).multiply(contractSize).divide(BigDecimal.valueOf(leverage), 5, RoundingMode.HALF_DOWN);
        BigDecimal returnRate = profit.divide(margin, 5, RoundingMode.HALF_DOWN);
        results.setIncome(profit);
        results.setMargin(margin);
        results.setReturnRate(returnRate);
        return results;
    }

    /**
     * 获得指定收益相应的平仓价格
     * 多仓：平仓价=开仓价+收益*开仓价/(数量*面值)
     * 空仓：平仓价=开仓价-收益*开仓价/(数量*面值)
     */
    public static BigDecimal closePositionPriceForIncome(String symbol, long volume, BigDecimal openPrice, int direction, BigDecimal income) {
        BigDecimal closePrice;
        // 面值
        BigDecimal contractSize = SwapConfigInfo.queryContractSize(symbol);
        BigDecimal medianValue = income.multiply(openPrice).divide(BigDecimal.valueOf(volume).multiply(contractSize), 5, RoundingMode.HALF_DOWN);
        if (direction == 1) {
            closePrice = openPrice.add(medianValue);
        } else {
            closePrice = openPrice.subtract(medianValue);
        }
        return closePrice;
    }

    /**
     * 达到指定收益率相应的平仓价格
     * 多仓：平仓价=开仓价+收益率*开仓价/杠杆倍数
     * 空仓：平仓价=开仓价-收益率*开仓价/杠杆倍数
     */
    public static BigDecimal closePositionPriceForIncomeRate(BigDecimal openPrice, int direction, int leverage, BigDecimal incomeRate) {
        BigDecimal closePrice;
        BigDecimal medianValue = incomeRate.multiply(openPrice).divide(BigDecimal.valueOf(leverage), 10, RoundingMode.HALF_DOWN);
        if (direction == 1) {
            closePrice = openPrice.add(medianValue);
        } else {
            closePrice = openPrice.subtract(medianValue);
        }
        return closePrice;
    }

    /**
     * 头寸计算
     */
    public static List<PositionCalculation> positionCalculate(String symbol) {
        List<PositionCalculation> positionCalculationList = new ArrayList<>();
        List<SwapMarginEntity> swapMarginEntityList = SwapConfigInfo.querySwapMarginConfig(symbol);
        for (int i = 0; i < swapMarginEntityList.size(); i++) {
            Integer maxLeverage = swapMarginEntityList.get(i).getLeverage();
            Integer nextLevelLeverage;
            if ((i + 1) == swapMarginEntityList.size()) {
                nextLevelLeverage = 0;
            } else {
                nextLevelLeverage = swapMarginEntityList.get(i + 1).getLeverage();
            }
            Long position = swapMarginEntityList.get(i).getPosition();
            PositionCalculation positionCalculation = PositionCalculation.builder()
                    .maxLeverage(maxLeverage)
                    .maxVolume(position)
                    .minLeverage(nextLevelLeverage)
                    .build();
            positionCalculationList.add(positionCalculation);
        }
        return positionCalculationList;
    }

    public static void main(String[] args) {
        // System.out.println(liquidationPriceOfOneWayPosition("btc_usdt", 1000, BigDecimal.valueOf(105000), 1, BigDecimal.valueOf(5000)));
        // System.out.println(liquidationPriceOfTwoWayPosition("btc_usdt", 500, BigDecimal.valueOf(105000), 500, BigDecimal.valueOf(115000), BigDecimal.valueOf(10000)));
        // System.out.println(income("btc_usdt", 100, BigDecimal.valueOf(105000), BigDecimal.valueOf(110000), 1, 10));
        // CalculatorPosition position = new CalculatorPosition(BigDecimal.valueOf(110000), 10L);
        // CalculatorPosition position2 = new CalculatorPosition(BigDecimal.valueOf(105000), 50L);
        // CalculatorPosition position3 = new CalculatorPosition(BigDecimal.valueOf(121111), 30L);
        // List<CalculatorPosition> list = new ArrayList<CalculatorPosition>();
        // list.add(position);
        // list.add(position2);
        // list.add(position3);
        // System.out.println(averagePriceOfOpenPosition(list));
        // System.out.println(closePositionPriceForIncome("btc_usdt", 55, BigDecimal.valueOf(110000), 2, BigDecimal.valueOf(20)));
        // System.out.println(closePositionPriceForIncomeRate(BigDecimal.valueOf(110000), 2, 32, BigDecimal.valueOf(0.2)));
        System.out.println(positionCalculate("btc_usdt"));
    }
}
