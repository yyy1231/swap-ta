package com.novax.testcase.other;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.SwapFrontFinanceRecordEntity;
import com.novax.entity.mysql.SwapFrontOrderEntity;
import com.novax.entity.mysql.SwapFrontOrderRecordEntity;
import com.novax.entity.mysql.SwapFrontPositionEntity;
import com.novax.testcase.*;
import com.novax.util.ExcelUtil;
import com.novax.util.HttpClientUtil;
import com.novax.util.NewestPriceUtil;
import com.novax.util.UrlProperty;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 测试场景：爆仓
 * step1:小币种下单
 * step2:redis写入爆仓价
 * step3:断言订单状态
 */
public class LiquidationTest extends PublicParams {
    Logger logger = Logger.getLogger(LiquidationTest.class);
    HttpClientUtil httpClientUtil;
    String url;
    String symbol;


    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "one-way-long")
    public Object[][] getProvider1() {
        return ExcelUtil.readObjDatas("/testcase/other/openPositionOfCross.xls", 0);
    }

    @Test(groups = {"smokeTest"}, description = "爆仓场景-step1-初始化配置")
    public void ready() {
        Long userId = traders.get(2);
        // Long userId = 28649983L;
        HSSFCell cell = ExcelUtil.getCell("/testcase/other/openPositionOfCross.xls", 0, 1, 5);
        JSONObject params = JSON.parseObject(cell.getStringCellValue());
        symbol = params.getString("symbol");
        SwapConfigInfo.initialUserLeverage(userId, symbol);
        AccountManagement.updateSwapAccBalance(userId, "usdt", BigDecimal.valueOf(20));
    }

    @Test(dataProvider = "one-way-long", groups = {"smokeTest"}, dependsOnMethods = {"ready"}, description = "爆仓场景-step2-下单")
    public void openOrder_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        // Long userId = 28670509L;
        Long userId = traders.get(2);
        // Long userId = 28649983L;
        reqHeaders.put("userId", String.valueOf(userId));
        JSONObject bodys = JSONObject.parseObject(params);
        int tradeVolume = (int) bodys.get("volume");
        String symbol = (String) bodys.get("symbol");
        int tradeType = (int) bodys.get("tradeType");

        // 存量订单数
        int orderCountBefore = TraderTransactionStatistics.queryOrders(userId).size();
        // 存量仓位
        SwapFrontPositionEntity swapFrontPositionEntity = TraderTransactionStatistics.queryPositions(userId, symbol, (byte) tradeType);
        Long positionVolumeBefore = Optional.ofNullable(swapFrontPositionEntity).map(SwapFrontPositionEntity::getVolume).orElse(0L);

        url = baseUrl + UrlProperty.getOrder();
        JSONObject resp = httpClientUtil.sendPostByJson(url, JSONObject.parseObject(params), reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + bodys);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        // 响应断言
        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg") + "：爆仓场景使用的小币种市场" + symbol + "，或行情采集被关闭了，没有k线和盘口导致下单失败，手动检查下");
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));
        Thread.sleep(1000);

        // 数据库断言
        dataAssert(userId, orderCountBefore, tradeVolume, symbol, (byte) tradeType, positionVolumeBefore);

        logger.info("***************************PASS***************************");
    }

    @Test(groups = {"smokeTest"}, dependsOnMethods = {"openOrder_Success_Test"}, description = "爆仓")
    public void liquidation_Success_Test() throws InterruptedException {
        Long userId = traders.get(2);
        // Long userId = 28649983L;

        // 目标价
        BigDecimal targetPrice = BigDecimal.ZERO;

        // 查仓位
        List<SwapFrontPositionEntity> positionEntities = TraderTransactionStatistics.queryPositions2(userId, null, null);
        SwapFrontPositionEntity position = positionEntities.get(0);
        Long positionId = position.getId();
        Integer direction = position.getDirection();
        BigDecimal forceClosePositonPrice = EstimatedForceClosePositionPriceCalculation.estimatedForceClosePositonPrice(position);

        if (direction == 1) {
            targetPrice = forceClosePositonPrice.subtract(BigDecimal.valueOf(0.01));
        }
        if (direction == 2) {
            targetPrice = forceClosePositonPrice.add(BigDecimal.valueOf(0.01));
        }

        // redis 写入触发目标价
        Long result = NewestPriceUtil.writeSwapPrice("swap:price:", symbol, targetPrice.doubleValue());
        Assert.assertEquals(result, 1, "redis爆仓价写入异常");
        Thread.sleep(10000);

        // 断言
        // 1、爆仓后，生成爆仓单
        List<SwapFrontOrderEntity> swapFrontOrderEntityList = TraderTransactionStatistics.queryOrders(userId);
        SwapFrontOrderEntity swapFrontOrderEntity = swapFrontOrderEntityList.get(0);
        Integer status = swapFrontOrderEntity.getStatus();
        Long orderId = swapFrontOrderEntity.getId();
        Assert.assertEquals(status, 6, "仓位" + positionId + "未爆仓");
        // 2、财务表，爆仓清算记录
        SwapFrontFinanceRecordEntity swapFrontFinanceRecordEntity = TraderTransactionStatistics.queryLiquidationRecord(userId, orderId);
        Assert.assertNotNull(swapFrontFinanceRecordEntity, "爆仓单：" + orderId + "仓位：" + positionId + "无爆仓清算记录");

        logger.info("***************************PASS***************************");
    }

    public void dataAssert(Long userId, int orderCountBefore, int tradeVolume, String symbol, byte tradeType, Long positionVolumeBefore) {
        // 数据库断言
        // 1、当前订单数量swap_front_order
        List<SwapFrontOrderEntity> swapFrontOrderEntityList = TraderTransactionStatistics.queryOrders(userId);
        int orderCountNow = swapFrontOrderEntityList.size();
        Assert.assertEquals(orderCountNow - orderCountBefore, 1, "用户" + userId + "交易下单异常");

        // 2、订单成交记录swap_front_order_record
        Long orderId = swapFrontOrderEntityList.get(0).getId();
        List<SwapFrontOrderRecordEntity> swapFrontOrderRecordEntityList = TraderTransactionStatistics.queryOrderRecords(orderId);
        long volume = 0L;
        for (SwapFrontOrderRecordEntity entity : swapFrontOrderRecordEntityList) {
            Long num = entity.getTradeVolume();
            volume += num;
        }
        Assert.assertEquals(volume, tradeVolume, "开仓单" + orderId + "成交异常或未完全成交，测试：" + volume + "，预期：" + tradeVolume);

        // 3、swap_front_position持仓
        SwapFrontPositionEntity positionEntity = TraderTransactionStatistics.queryPositions(userId, symbol, (byte) tradeType);
        Long positionVolumeNow = positionEntity.getVolume();
        Assert.assertEquals(positionVolumeNow - positionVolumeBefore, tradeVolume, "用户" + userId + "持仓数量异常，测试：" + (positionVolumeNow - positionVolumeBefore) + "，预期：" + tradeVolume);

        // 订单手续费
        BigDecimal orderFees = TraderTransactionStatistics.feeCalculate(orderId);

        // 4、swap_front_finance_record财务记录
        List<SwapFrontFinanceRecordEntity> swapFrontFinanceRecordEntityList = TraderTransactionStatistics.queryFinanceRecord(userId, orderId);
        Assert.assertTrue(swapFrontFinanceRecordEntityList.size() != 0, "订单" + orderId + "财务记录异常");
        BigDecimal fees = BigDecimal.ZERO;
        for (SwapFrontFinanceRecordEntity financeRecord : swapFrontFinanceRecordEntityList) {
            if (financeRecord.getOperation() == 5 || financeRecord.getOperation() == 6) {
                fees = fees.add(financeRecord.getAmount());
            }
        }
        Assert.assertTrue(orderFees.add(fees).abs().compareTo(BigDecimal.valueOf(0.002)) < 0, "订单" + orderId + "手续费计算异常，测试：" + fees + "，预期：" + orderFees);

    }
}
