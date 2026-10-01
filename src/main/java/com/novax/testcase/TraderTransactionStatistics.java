package com.novax.testcase;

import com.novax.entity.mysql.*;
import com.novax.mapper.*;
import com.novax.util.DateUtil;
import com.novax.util.MyBatisUtil;
import com.novax.util.NewestPriceUtil;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class TraderTransactionStatistics {
    static Logger logger = Logger.getLogger(TraderTransactionStatistics.class);

    // 查询订单
    public static SwapFrontOrderEntity queryOrder(Long orderId) {
        return MyBatisUtil.execute(SwapFrontOrderEntityMapper.class, m -> m.selectByPrimaryKey(orderId));
    }

    // 统计全部订单
    public static List<SwapFrontOrderEntity> queryOrders(Long userId) {
        return MyBatisUtil.execute(SwapFrontOrderEntityMapper.class, m -> m.selectByUserId(userId));
    }

    // 统计指定条件订单
    public static List<SwapFrontOrderEntity> queryOrder(Long userId, String symbol, byte tradeType) {
        return MyBatisUtil.execute(SwapFrontOrderEntityMapper.class, m -> m.selectOrders(userId, symbol, tradeType));
    }

    // 限价单统计 --status=1交易中状态的订单
    public static List<SwapFrontOrderEntity> queryInTradingOrders(Long userId) throws ParseException {
        Date zeroTime = DateUtil.getZeroTime2(7);
        return MyBatisUtil.execute(SwapFrontOrderEntityMapper.class, m -> m.selectByStatus(userId, zeroTime));
    }

    // 限价单统计 --symbol
    public static List<SwapFrontOrderEntity> queryInTradingOrders(Long userId, String symbol) {
        return MyBatisUtil.execute(SwapFrontOrderEntityMapper.class, m -> m.selectBySymbol(userId, symbol));
    }

    // 历史委托单统计 --近7天
    public static List<SwapFrontOrderEntity> queryHistoryOrders(Long userId) throws ParseException {
        Date zeroTime = DateUtil.getZeroTime2(7);
        return MyBatisUtil.execute(SwapFrontOrderEntityMapper.class, m -> m.selectHistoryOrders(userId, zeroTime));
    }

    // 等待委托状态的计划单 --status=1
    public static List<SwapFrontOrderPlanEntity> queryPlanOrders(Long userId) {
        return MyBatisUtil.execute(SwapFrontOrderPlanEntityMapper.class, m -> m.selectByUserId(userId));
    }

    // 等待委托状态的计划单 --status=1
    public static List<SwapFrontOrderPlanEntity> queryPeriodPlanOrders(Long userId) throws ParseException {
        Date zeroTime = DateUtil.getZeroTime2(7);
        return MyBatisUtil.execute(SwapFrontOrderPlanEntityMapper.class, m -> m.selectPeriodPlanOrders(userId, zeroTime));
    }

    // 历史委托的计划单
    public static List<SwapFrontOrderPlanEntity> queryHistoryPlanOrders(Long userId) {
        return MyBatisUtil.execute(SwapFrontOrderPlanEntityMapper.class, m -> m.selectHistoryPlanOrders(userId));
    }

    // 历史委托的计划单
    public static List<SwapFrontOrderPlanEntity> queryPeriodHistoryPlanOrders(Long userId) throws ParseException {
        Date zeroTime = DateUtil.getZeroTime2(7);
        return MyBatisUtil.execute(SwapFrontOrderPlanEntityMapper.class, m -> m.selectPeriodHistoryPlanOrders(userId, zeroTime));
    }

    // 统计计划单 --symbol
    public static List<SwapFrontOrderPlanEntity> queryPlanOrders(Long userId, String symbol) {
        return MyBatisUtil.execute(SwapFrontOrderPlanEntityMapper.class, m -> m.selectBySymbol(userId, symbol));
    }

    // 查询计划单
    public static SwapFrontOrderPlanEntity queryPlanOrder(Long id) {
        return MyBatisUtil.execute(SwapFrontOrderPlanEntityMapper.class, m -> m.selectByPrimaryKey(id));
    }

    // 订单关联的计划单
    public static List<SwapFrontOrderPlanEntity> queryPlanOrdersByOrderId(Long userId, Long orderId) {
        return MyBatisUtil.execute(SwapFrontOrderPlanEntityMapper.class, m -> m.selectByOrderId(userId, orderId));
    }

    // 订单止盈止损详情
    public static SwapStopProfitLossEntity queryStopProfitLossDetail(Long orderId) {
        return MyBatisUtil.execute(SwapStopProfitLossEntityMapper.class, m -> m.selectByOrderId(orderId));
    }

    // 查询设置止盈止损的委托单
    public static List<SwapFrontOrderEntity> queryOrdersWithStopProfitLoss(Long userId) {
        return MyBatisUtil.execute(SwapFrontOrderEntityMapper.class, m -> m.selectOrdersWithStopProfitLoss(userId));
    }

    // 查询用户等待委托状态的止盈止损计划单
    public static List<SwapFrontOrderPlanEntity> queryStopProfitLossOrders(Long userId) {
        return MyBatisUtil.execute(SwapFrontOrderPlanEntityMapper.class, m -> m.selectStopProfitLossOrders(userId));
    }

    // 成交记录统计
    public static List<SwapFrontOrderRecordEntity> queryTransactions(Long userId) {
        return MyBatisUtil.execute(SwapFrontOrderRecordEntityMapper.class, m -> m.selectByUserId(userId));
    }

    // 成交记录统计 近n天
    public static List<SwapFrontOrderRecordEntity> queryTransactions(Long userId, int n) throws ParseException {
        Date zeroTime = DateUtil.getZeroTime2(n);
        return MyBatisUtil.execute(SwapFrontOrderRecordEntityMapper.class, m -> m.selectPeriodTransactions(userId, zeroTime));
    }

    // 成交记录统计 近n天
    public static List<SwapFrontOrderRecordEntity> queryTransactions2(Long userId, int n) {
        Date zeroTime = DateUtil.addDay(new Date(), -n);
        return MyBatisUtil.execute(SwapFrontOrderRecordEntityMapper.class, m -> m.selectPeriodTransactions(userId, zeroTime));
    }

    // 订单成交记录统计
    public static List<SwapFrontOrderRecordEntity> queryOrderRecords(Long orderId) {
        return MyBatisUtil.execute(SwapFrontOrderRecordEntityMapper.class, m -> m.selectByOrderId(orderId));
    }

    // 订单今天的成交记录
    public static List<SwapFrontOrderRecordEntity> queryTodayTransactionRecords(Long userId) {
        return MyBatisUtil.execute(SwapFrontOrderRecordEntityMapper.class, m -> m.selectTodayRecords(userId));
    }

    // 指定symbol+direction持仓
    public static SwapFrontPositionEntity queryPositions(Long userId, String symbol, Byte direction) {
        return MyBatisUtil.execute(SwapFrontPositionEntityMapper.class, m -> m.selectBySymbolAndDirection(userId, symbol, direction));
    }

    public static List<SwapFrontPositionEntity> queryPositions2(Long userId, String tradeSymbol, Integer direction) {
        return MyBatisUtil.execute(SwapFrontPositionEntityMapper.class, m -> m.selectPositions(userId, tradeSymbol, direction));
    }

    // 财务记录
    public static List<SwapFrontFinanceRecordEntity> queryFinanceRecord(Long userId, Long orderId) {
        return MyBatisUtil.execute(SwapFrontFinanceRecordEntityMapper.class, m -> m.selectByOrderId(userId, orderId));
    }

    // 财务记录 --爆仓清算
    public static SwapFrontFinanceRecordEntity queryLiquidationRecord(Long userId, Long orderId) {
        return MyBatisUtil.execute(SwapFrontFinanceRecordEntityMapper.class, m -> m.selectLiquidationRecord(userId, orderId));
    }

    // 财务记录-近n天
    public static List<SwapFrontFinanceRecordEntity> queryFinanceRecord(Long userId, int n) {
        Date zeroTime = DateUtil.addDay(new Date(), -n);
        return MyBatisUtil.execute(SwapFrontFinanceRecordEntityMapper.class, m -> m.selectPeriodRecords(userId, zeroTime));
    }

    /**
     * 单个仓位初始保证金
     * 全仓&分仓=张数*面值/杠杆倍数
     * 逐仓=张数*面值/杠杆倍数 - 手续费  直接查库
     */
    public static BigDecimal initialMarginOfPosition(int mode, SwapFrontPositionEntity positionEntity) {
        if (mode == 1 || mode == 3) {
            // 持仓量
            Long volume = positionEntity.getVolume();
            // 面值
            BigDecimal contractSize = positionEntity.getContractSize();
            // 杠杆倍数
            int leverage = positionEntity.getLeverage();
            return BigDecimal.valueOf(volume).multiply(contractSize).divide(BigDecimal.valueOf(leverage), 2, RoundingMode.HALF_UP);
        } else {
            return positionEntity.getMargin();
        }
    }

    /**
     * 用户所有仓位的占用保证金
     * 全仓&逐仓：所有仓位初始保证金之和
     * 分仓：指定symbol 多空仓位初始保证金之和
     */
    public static BigDecimal totalInitialMargin(Long userId, String tradeSymbol) {
        BigDecimal totalInitialMargin = BigDecimal.ZERO;
        SwapFrontUserConfigEntity configEntity = SwapConfigInfo.queryMode(userId);
        Integer mode = configEntity.getMode();
        List<SwapFrontPositionEntity> swapFrontPositionEntities;
        if (mode == 1 || mode == 2) {
            swapFrontPositionEntities = queryPositions2(userId, null, null);
        } else {
            swapFrontPositionEntities = queryPositions2(userId, tradeSymbol, null);
        }
        for (SwapFrontPositionEntity entity : swapFrontPositionEntities) {
            BigDecimal initialMargin = initialMarginOfPosition(mode, entity);
            totalInitialMargin = totalInitialMargin.add(initialMargin);
        }
        logger.info("用户" + userId + "所有仓位占用保证金：" + totalInitialMargin);
        return totalInitialMargin;
    }

    // 委托冻结保证金
    public static BigDecimal queryFrozenMargin(Long userId, String currency, String tradeSymbol) {
        SwapFrontUserAssetsEntity swapFrontUserAssetsEntity = UserAssetsStatistics.swapAccount(userId, currency, tradeSymbol);
        return swapFrontUserAssetsEntity.getFrozen();
    }

    // 用户所有仓位未实现盈亏 全仓&逐仓
    public static BigDecimal unrealizedProfitOrLoss(Long userId) {
        BigDecimal unrealizedProfitOrLoss;
        // 多仓
        List<SwapFrontPositionEntity> longPositionEntity
                = queryPositions2(userId, null, 1);
        BigDecimal unrealizedProfitOrLossOfLongPosition = new BigDecimal("0.00000");
        for (SwapFrontPositionEntity position : longPositionEntity) {
            BigDecimal unrealizedProfitOrLossOfSymbol
                    = unrealizedProfitOrLoss(position);
            unrealizedProfitOrLossOfLongPosition
                    = unrealizedProfitOrLossOfLongPosition.add(unrealizedProfitOrLossOfSymbol);
        }
        logger.info("用户" + userId + "多仓仓位未实现盈亏共计：" + unrealizedProfitOrLossOfLongPosition);

        // 空仓
        List<SwapFrontPositionEntity> shortPositionEntity
                = queryPositions2(userId, null, 2);
        BigDecimal unrealizedProfitOrLossOfShortPosition = new BigDecimal("0.00000");
        for (SwapFrontPositionEntity position : shortPositionEntity) {
            BigDecimal unrealizedProfitOrLossOfSymbol
                    = unrealizedProfitOrLoss(position);
            unrealizedProfitOrLossOfShortPosition
                    = unrealizedProfitOrLossOfShortPosition.add(unrealizedProfitOrLossOfSymbol);
        }
        logger.info("用户" + userId + "空仓仓位未实现盈亏共计：" + unrealizedProfitOrLossOfShortPosition);

        // 该用户全部仓位未实现盈亏
        unrealizedProfitOrLoss = unrealizedProfitOrLossOfLongPosition.add(unrealizedProfitOrLossOfShortPosition);
        logger.info("用户" + userId + "全部仓位未实现盈亏共计：" + unrealizedProfitOrLoss);
        return unrealizedProfitOrLoss;
    }

    // 用户所有仓位未实现盈亏 全仓&逐仓&分仓 分仓需指定币对
    public static BigDecimal unrealizedProfitOrLoss(Long userId, String tradeSymbol) {
        BigDecimal unrealizedProfitOrLoss;
        // 多仓
        List<SwapFrontPositionEntity> longPositionEntity = null;
        // 空仓
        List<SwapFrontPositionEntity> shortPositionEntity = null;
        BigDecimal unrealizedProfitOrLossOfLongPosition = new BigDecimal("0.00000");
        BigDecimal unrealizedProfitOrLossOfShortPosition = new BigDecimal("0.00000");
        if (tradeSymbol == null || tradeSymbol.equals("") || tradeSymbol.equals("usdt")) {
            longPositionEntity = queryPositions2(userId, null, 1);
            shortPositionEntity = queryPositions2(userId, null, 2);
        } else {
            longPositionEntity = queryPositions2(userId, tradeSymbol, 1);
            shortPositionEntity = queryPositions2(userId, tradeSymbol, 2);
        }
        for (SwapFrontPositionEntity position : longPositionEntity) {
            BigDecimal unrealizedProfitOrLossOfSymbol
                    = unrealizedProfitOrLoss(position);
            unrealizedProfitOrLossOfLongPosition
                    = unrealizedProfitOrLossOfLongPosition.add(unrealizedProfitOrLossOfSymbol);
        }
        logger.info("用户" + userId + "交易对" + tradeSymbol + "多仓仓位未实现盈亏共计：" + unrealizedProfitOrLossOfLongPosition);

        for (SwapFrontPositionEntity position : shortPositionEntity) {
            BigDecimal unrealizedProfitOrLossOfSymbol
                    = unrealizedProfitOrLoss(position);
            unrealizedProfitOrLossOfShortPosition
                    = unrealizedProfitOrLossOfShortPosition.add(unrealizedProfitOrLossOfSymbol);
        }
        logger.info("用户" + userId + "交易对" + tradeSymbol + "空仓仓位未实现盈亏共计：" + unrealizedProfitOrLossOfShortPosition);

        // 该用户全部仓位未实现盈亏
        unrealizedProfitOrLoss = unrealizedProfitOrLossOfLongPosition.add(unrealizedProfitOrLossOfShortPosition);
        logger.info("用户" + userId + "交易对" + tradeSymbol + "全部仓位未实现盈亏共计：" + unrealizedProfitOrLoss);
        return unrealizedProfitOrLoss;
    }

    // 计算单个仓位未实现盈亏
    public static BigDecimal unrealizedProfitOrLoss(SwapFrontPositionEntity position) {
        Integer direction = position.getDirection();
        BigDecimal newPriceOfSwap = NewestPriceUtil.newPriceOfSwap(position.getSymbol());
        BigDecimal averageOpeningPrice = position.getCostOpen();
        Long volume = position.getVolume();
        BigDecimal contractSize = position.getContractSize();
        return unrealizedProfitOrLoss(direction, newPriceOfSwap, averageOpeningPrice, volume, contractSize);
    }

    /**
     * 计算单个仓位未实现盈亏
     * 多仓：未实现盈亏=（最新价-开仓均价）*（持仓张数*面值）/开仓均价
     * 空仓：未实现盈亏=（开仓均价-最新价）*（持仓张数*面值）/开仓均价
     */
    public static BigDecimal unrealizedProfitOrLoss(int direction, BigDecimal newestPrice, BigDecimal averageOpeningPrice, Long volume, BigDecimal contractSize) {
        BigDecimal unrealizedProfitOrLoss = BigDecimal.ZERO;
        if (direction == 1) {
            unrealizedProfitOrLoss = (newestPrice.subtract(averageOpeningPrice))
                    .multiply(BigDecimal.valueOf(volume)).multiply(contractSize)
                    .divide(averageOpeningPrice, 5, RoundingMode.HALF_UP);
        } else if (direction == 2) {
            unrealizedProfitOrLoss = (averageOpeningPrice.subtract(newestPrice))
                    .multiply(BigDecimal.valueOf(volume)).multiply(contractSize)
                    .divide(averageOpeningPrice, 5, RoundingMode.HALF_UP);
        } else {
            logger.info("开仓方向参数不合法：" + direction);
        }
        return unrealizedProfitOrLoss;
    }

    // 保证金余额 全仓&逐仓&分仓 账户维度
    public static BigDecimal marginBalance(Long userId, String tradeSymbol) {
        BigDecimal marginBalance = BigDecimal.ZERO;
        SwapFrontUserConfigEntity configEntity = SwapConfigInfo.queryMode(userId);
        Integer mode = configEntity.getMode();
        if (mode == 1) {// 全仓
            // 钱包余额
            BigDecimal available = UserAssetsStatistics.swapAccountAvailable(userId, "usdt", null);
            // 未实现盈亏
            BigDecimal unrealizedProfitOrLoss = TraderTransactionStatistics.unrealizedProfitOrLoss(userId);
            marginBalance = available.add(unrealizedProfitOrLoss);
        } else if (mode == 2) {
            // 钱包余额
            marginBalance = UserAssetsStatistics.swapAccountAvailable(userId, "usdt", null);
        } else if (mode == 3) {
            // 钱包余额
            BigDecimal available = UserAssetsStatistics.swapAccountAvailable(userId, "usdt", tradeSymbol);
            // 未实现盈亏
            BigDecimal unrealizedProfitOrLoss = TraderTransactionStatistics.unrealizedProfitOrLoss(userId, tradeSymbol);
            marginBalance = available.add(unrealizedProfitOrLoss);
        } else {
            logger.info("用户" + userId + "仓位保证金模式参数不合法");
        }
        logger.info("用户：" + userId + "保证金余额" + marginBalance);
        return marginBalance;
    }

    // 逐仓 单个仓位保证金余额=仓位保证金+未实现盈亏
    public static BigDecimal marginBalanceOfPosition(SwapFrontPositionEntity position) {
        // 1、仓位保证金
        BigDecimal margin = position.getMargin();
        // 2、未实现盈亏
        BigDecimal unrealizedProfitOrLoss = unrealizedProfitOrLoss(position);
        BigDecimal marginBalance = margin.add(unrealizedProfitOrLoss);
        logger.info("仓位：" + position.getId() + "保证金余额：" + marginBalance);
        return marginBalance;
    }

    /**
     * 单个仓位维持保证金=仓位张数 * 面值 * 维持保证金率 - 该层级维持保证金速算额
     */
    public static BigDecimal maintainMarginOfPosition(SwapFrontPositionEntity positionEntity) {
        // 持仓量
        long volume = positionEntity.getVolume();
        // 标的
        String symbol = positionEntity.getSymbol();
        // 面值
        BigDecimal contractSize = positionEntity.getContractSize();
        // 维持保证金率
        SwapMarginEntity tier = MaintainMarginConfiguration.queryTier(symbol, volume);
        BigDecimal maintainMarginRate = tier.getRatio();
        // 维持保证金速算额
        BigDecimal maintainMarginQuickDeductionAmount = tier.getMargin();
        // 维持保证金
        BigDecimal maintainMargin = BigDecimal.valueOf(volume)
                .multiply(contractSize)
                .multiply(maintainMarginRate)
                .subtract(maintainMarginQuickDeductionAmount);
        logger.info(positionEntity.getId() + "仓位维持保证金：" + maintainMargin);
        return maintainMargin;
    }

    /**
     * 单个仓位维持保证金=仓位张数 * 面值 * 维持保证金率 - 该层级维持保证金速算额
     */
    public static BigDecimal maintainMarginOfPosition(String symbol, Long volume) {
        // 面值
        BigDecimal contractSize = SwapConfigInfo.queryContractSize(symbol);
        // 维持保证金率
        SwapMarginEntity tier = MaintainMarginConfiguration.queryTier(symbol, volume);
        BigDecimal maintainMarginRate = tier.getRatio();
        // 维持保证金速算额
        BigDecimal maintainMarginQuickDeductionAmount = tier.getMargin();
        // 维持保证金
        return BigDecimal.valueOf(volume).multiply(contractSize).multiply(maintainMarginRate)
                .subtract(maintainMarginQuickDeductionAmount);
    }

    /**
     * 账户的维持保证金
     * 全仓&逐仓：所有仓位的维持保证金之和
     * 分仓，同交易对，多空仓位的维持保证金之和 即账户的维持保证金 分仓！！！
     */
    public static BigDecimal maintainMarginOfAccount(Long userId, String tradeSymbol) {
        BigDecimal maintainMarginSum = BigDecimal.ZERO;
        List<SwapFrontPositionEntity> swapFrontPositionEntities;
        if (tradeSymbol == null || tradeSymbol.equals("") || tradeSymbol.equals("usdt")) {
            swapFrontPositionEntities = queryPositions2(userId, null, null);
        } else {
            swapFrontPositionEntities = queryPositions2(userId, tradeSymbol, null);
        }
        for (SwapFrontPositionEntity position : swapFrontPositionEntities) {
            BigDecimal maintainMarginOfPosition = maintainMarginOfPosition(position);
            maintainMarginSum = maintainMarginSum.add(maintainMarginOfPosition);
        }
        logger.info("用户" + userId + "账户的维持保证金：" + maintainMarginSum);
        return maintainMarginSum;
    }

    /**
     * 账户的保证金率:
     * 全仓：保证金余额 / 维持保证金
     * 逐仓：逐仓，无该概念
     * 分仓账户的保证金率：该交易对账户保证金余额 /该交易对维持保证金
     */
    public static BigDecimal marginRateOfAccount(Long userId, String tradeSymbol) {
        BigDecimal marginRateOfAccount;
        BigDecimal marginBalance;
        BigDecimal maintainMargin;
        if (tradeSymbol == null || tradeSymbol.equals("") || tradeSymbol.equals("usdt")) {
            marginBalance = marginBalance(userId, null);
            maintainMargin = maintainMarginOfAccount(userId, null);
        } else {
            marginBalance = marginBalance(userId, tradeSymbol);
            maintainMargin = maintainMarginOfAccount(userId, tradeSymbol);
        }
        marginRateOfAccount = marginBalance.divide(maintainMargin, 5, RoundingMode.CEILING);
        logger.info("用户" + userId + "账户的保证金率：" + marginRateOfAccount);
        return marginRateOfAccount;
    }

    /**
     * 仓位上的保证金率
     * 全仓：（ 钱包余额 + 所有仓位未实现盈亏）/ 所有仓位维持保证金
     * 逐仓：(该仓位保证金+该仓位未实现盈亏)/该仓位维持保证金
     * 分仓：（对应交易对的分仓钱包余额 +同交易对所有仓位未实现盈亏）/ 同交易对所有仓位维持保证金
     */
    public static BigDecimal marginRateOfPosition(Long userId, SwapFrontPositionEntity position) {
        BigDecimal marginRate = BigDecimal.ZERO;
        SwapFrontUserConfigEntity configEntity = SwapConfigInfo.queryMode(userId);
        Integer mode = configEntity.getMode();
        if (mode == 1) { // 全仓
            // 1、保证金余额
            BigDecimal marginBalance = marginBalance(userId, "");
            // 2、所有仓位维持保证金
            BigDecimal maintainMarginSum = BigDecimal.ZERO;
            List<SwapFrontPositionEntity> swapFrontPositionEntities = queryPositions2(userId, null, null);
            for (SwapFrontPositionEntity entity : swapFrontPositionEntities) {
                BigDecimal maintainMargin = maintainMarginOfPosition(entity);
                maintainMarginSum = maintainMarginSum.add(maintainMargin);
            }
            marginRate = marginBalance.divide(maintainMarginSum, 5, RoundingMode.CEILING);
        } else if (mode == 2) { // 逐仓
            // 该仓位保证金+该仓位未实现盈亏
            BigDecimal marginBalanceOfPosition = marginBalanceOfPosition(position);
            // 该仓位维持保证金
            BigDecimal maintainMarginOfPosition = maintainMarginOfPosition(position);
            marginRate = marginBalanceOfPosition.divide(maintainMarginOfPosition, 5, RoundingMode.CEILING);
        } else if (mode == 3) { // 分仓
            String symbol = position.getSymbol();
            // 对应交易对的分仓钱包余额
            BigDecimal available = UserAssetsStatistics.swapAccountAvailable(userId, "usdt", symbol);
            // 同交易对所有仓位未实现盈亏
            BigDecimal unrealizedProfitOrLoss = unrealizedProfitOrLoss(userId, symbol);
            // 同交易对所有仓位维持保证金
            BigDecimal maintainMarginOfPosition = maintainMarginOfAccount(userId, symbol);
            marginRate = available.add(unrealizedProfitOrLoss).divide(maintainMarginOfPosition, 5, RoundingMode.CEILING);
        }
        logger.info("用户" + userId + "仓位" + position.getId() + "保证金率：" + marginRate);
        return marginRate;
    }

    /**
     * 可用划转余额 --账户维度
     * 全仓=保证金余额 - 仓位占用保证金 - 委托冻结的保证金 - 所有仓位维持保证金之和
     * 逐仓=保证金余额 - 仓位占用保证金 - 委托冻结的保证金
     * 分仓=保证金余额 - 仓位占用保证金 - 委托冻结的保证金 - 维持保证金  指定symbol
     */
    public static BigDecimal transferableAmount(Long userId, String tradeSymbol) {
        SwapFrontUserConfigEntity configEntity = SwapConfigInfo.queryMode(userId);
        Integer mode = configEntity.getMode();
        BigDecimal marginBalance;
        BigDecimal totalInitialMargin;
        BigDecimal frozenMargin;
        BigDecimal totalMaintainMargin;
        if (mode == 1) {
            marginBalance = marginBalance(userId, null);
            totalInitialMargin = totalInitialMargin(userId, null);
            frozenMargin = queryFrozenMargin(userId, "usdt", null);
            totalMaintainMargin = maintainMarginOfAccount(userId, null);
            return marginBalance.subtract(totalInitialMargin).subtract(frozenMargin).subtract(totalMaintainMargin);
        } else if (mode == 2) {
            marginBalance = marginBalance(userId, null);
            totalInitialMargin = totalInitialMargin(userId, null);
            frozenMargin = queryFrozenMargin(userId, "usdt", null);
            return marginBalance.subtract(totalInitialMargin).subtract(frozenMargin);
        } else if (mode == 3) {
            marginBalance = marginBalance(userId, tradeSymbol);
            totalInitialMargin = totalInitialMargin(userId, tradeSymbol);
            frozenMargin = queryFrozenMargin(userId, "usdt", tradeSymbol);
            totalMaintainMargin = maintainMarginOfAccount(userId, tradeSymbol);
            return marginBalance.subtract(totalInitialMargin).subtract(frozenMargin).subtract(totalMaintainMargin);
        } else {
            Assert.assertEquals(1, 2, "用户" + userId + "仓位保证金模式参数不合法");
            return BigDecimal.ZERO;
        }
    }

    /**
     * 逐仓 仓位可提取:
     * 最大可减少保证金=min(增加的保证金，保证金余额-仓位占用保证金)
     * =min(增加的保证金,增加保证金+仓位未实现盈亏)
     * 保证金余额 = 初始保证金+增加保证金+仓位未实现盈亏
     */
    public static BigDecimal transferableAmountOfIsolatedPosition(SwapFrontPositionEntity position) {
        // 增加的保证金
        BigDecimal increasedMargin = position.getAddMargin();
        // 仓位未实现盈亏
        BigDecimal unrealizedProfitOrLoss = unrealizedProfitOrLoss(position);
        // 可提取保证金
        BigDecimal withdrawableMargin = increasedMargin.min(increasedMargin.add(unrealizedProfitOrLoss));
        logger.info("逐仓：" + position.getId() + "仓位可提取保证金：" + withdrawableMargin);
        return withdrawableMargin;
    }

    /**
     * 可用资产 即可用于交易的资产
     * 全仓=钱包余额-累加已用保证金+累加未实现盈亏=保证金余额-已用保证金
     * 逐仓=钱包余额-已用保证金
     * 分仓=账户余额-已用保证金+未实现盈亏=保证金余额-已用保证金  指定symbol
     * 【已用保证金：委托冻结+累加仓位保证金】
     */
    public static BigDecimal tradableAmount(Long userId, String tradeSymbol) {
        SwapFrontUserConfigEntity configEntity = SwapConfigInfo.queryMode(userId);
        Integer mode = configEntity.getMode();
        BigDecimal marginBalance;
        BigDecimal totalInitialMargin;
        BigDecimal frozenMargin;
        if (mode == 1 || mode == 2) {
            marginBalance = marginBalance(userId, null);
            totalInitialMargin = totalInitialMargin(userId, null);
            frozenMargin = queryFrozenMargin(userId, "usdt", null);
            return marginBalance.subtract(totalInitialMargin).subtract(frozenMargin);
        } else if (mode == 3) {
            marginBalance = marginBalance(userId, tradeSymbol);
            totalInitialMargin = totalInitialMargin(userId, tradeSymbol);
            frozenMargin = queryFrozenMargin(userId, "usdt", tradeSymbol);
            return marginBalance.subtract(totalInitialMargin).subtract(frozenMargin);
        } else {
            Assert.assertEquals(1, 2, "用户" + userId + "仓位保证金模式参数不合法");
            return BigDecimal.ZERO;
        }
    }

    /**
     * 订单手续费计算
     * 单位是USDT：
     * 用户撮合方向是taker，=对应的vip等级合约taker手续费*撮合张数*面值
     * 用户撮合方向是maker，=对应的vip等级合约maker手续费*撮合张数*面值
     * 单位是ET
     * 用户撮合方向是taker，=对应的vip等级合约taker手续费*(1-对应的vip等级合约taker抵扣)/et现货最新价*撮合张数*面值
     * 用户撮合方向是maker，=对应的vip等级合约maker手续费*(1-对应的vip等级合约maker抵扣)/et现货最新价*撮合张数*面值
     * 备注：存在订单手续费扣et后 账户无et的场景 该场景下手续费回测有误
     */
    public static BigDecimal feeCalculate(Long orderId) {
        SwapFrontOrderEntity orderEntity = MyBatisUtil.execute(SwapFrontOrderEntityMapper.class, m -> m.selectByPrimaryKey(orderId));
        Long userId = orderEntity.getUserId();
        int status = orderEntity.getStatus();
        BigDecimal feeOfOrder = BigDecimal.ZERO;
        if (status != 6) {
            // 用户vip等级
            int userVipLevel = queryUserVipLevel(userId);
            // 查询等级手续费配置
            VipLevelConfig vipLevelConfig = queryVipLevelConfig(userVipLevel);
            // 查合约账户ET余额
            BigDecimal etBalance = UserAssetsStatistics.swapAccountBalanceOfCurrency(userId, "et");
            // 订单关联的成交记录
            List<SwapFrontOrderRecordEntity> transactionRecordEntityList = queryOrderRecords(orderId);
            for (SwapFrontOrderRecordEntity record : transactionRecordEntityList) {
                BigDecimal fee;
                // 成交量
                Long tradeVolume = record.getTradeVolume();
                String symbol = record.getSymbol();
                BigDecimal contractSize = SwapConfigInfo.queryContractSize(symbol);
                // 成交额
                BigDecimal tradeAmount = contractSize.multiply(BigDecimal.valueOf(tradeVolume));
                // 查询流动性方向1:taker;2:maker
                int role = record.getRole();
                if (etBalance.compareTo(BigDecimal.ZERO) == 0) {// 无et 即扣u 回测！！！
                    BigDecimal feeRate;
                    if (role == 1) {
                        feeRate = vipLevelConfig.getSwapFrontTaker();
                    } else {
                        feeRate = vipLevelConfig.getSwapFrontMaker();
                    }
                    fee = feeRate.multiply(tradeAmount);
                } else {
                    BigDecimal et_usdt = NewestPriceUtil.newPriceOfSymbol("et_usdt");
                    BigDecimal feeRate;
                    BigDecimal discount;
                    if (role == 1) {
                        feeRate = vipLevelConfig.getSwapFrontTaker();
                        discount = vipLevelConfig.getSwapFrontTakerDiscount();
                    } else {
                        feeRate = vipLevelConfig.getSwapFrontMaker();
                        discount = vipLevelConfig.getSwapFrontMakerDiscount();
                    }
                    fee = feeRate.multiply(BigDecimal.valueOf(1).subtract(discount)).multiply(tradeAmount)
                            .divide(et_usdt, 10, RoundingMode.CEILING);
                }
                feeOfOrder = feeOfOrder.add(fee);
            }
        }
        logger.info("订单：" + orderId + "手续费" + feeOfOrder);
        return feeOfOrder;
    }

    // 查用户等级 默认0
    public static int queryUserVipLevel(Long userId) {
        VipLevelUp userVipLevel = MyBatisUtil.execute(VipLevelUpMapper.class, m -> m.selectByUserId(userId));
        return Optional.ofNullable(userVipLevel).map(VipLevelUp::getLevel).orElse(0);
    }

    // 查手续费率
    public static VipLevelConfig queryVipLevelConfig(int level) {
        return MyBatisUtil.execute(VipLevelConfigMapper.class, m -> m.selectByLevel(level));
    }


    public static void main(String[] args) {
        // System.out.println(transferableAmount(28649983L, "eth_usdt"));
        // List<SwapFrontPositionEntity> swapFrontPositionEntities = queryPositions2(28649983L, null, null);
        // System.out.println(transferableAmountOfIsolatedPosition(swapFrontPositionEntities.get(0)));

        System.out.println(queryFinanceRecord(28649983L, 2001739500074172417L));
    }
}
