package com.novax.testcase.order;

import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.*;
import com.novax.testcase.AccountManagement;
import com.novax.testcase.GenerateUserTest;
import com.novax.testcase.PublicParams;
import com.novax.testcase.TraderTransactionStatistics;
import com.novax.util.ExcelUtil;
import com.novax.util.HttpClientUtil;
import com.novax.util.NewestPriceUtil;
import com.novax.util.UrlProperty;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


/**
 * 交易下单-开仓-全仓
 * POST/swap/order
 */
public class OpenPositionOfCrossTest extends PublicParams {
    Logger logger = Logger.getLogger(OpenPositionOfCrossTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "one-way-long")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/order/openPositionOfCross.xls", 0);
    }

    @DataProvider(name = "one-way-short")
    public Object[][] getProvider1() {
        return ExcelUtil.readObjDatas("/testcase/order/openPositionOfCross.xls", 1);
    }

    @DataProvider(name = "two-way-long")
    public Object[][] getProvider2() {
        return ExcelUtil.readObjDatas("/testcase/order/openPositionOfCross.xls", 2);
    }

    @DataProvider(name = "two-way-equal")
    public Object[][] getProvider3() {
        return ExcelUtil.readObjDatas("/testcase/order/openPositionOfCross.xls", 3);
    }

    @DataProvider(name = "two-way-short")
    public Object[][] getProvider4() {
        return ExcelUtil.readObjDatas("/testcase/order/openPositionOfCross.xls", 4);
    }

    @DataProvider(name = "multi-one-way-long")
    public Object[][] getProvider5() {
        return ExcelUtil.readObjDatas("/testcase/order/openPositionOfCross.xls", 5);
    }

    @DataProvider(name = "multi-one-way-short")
    public Object[][] getProvider6() {
        return ExcelUtil.readObjDatas("/testcase/order/openPositionOfCross.xls", 6);
    }

    @DataProvider(name = "multi-two-way-long")
    public Object[][] getProvider7() {
        return ExcelUtil.readObjDatas("/testcase/order/openPositionOfCross.xls", 7);
    }

    @DataProvider(name = "multi-two-way-equal")
    public Object[][] getProvider8() {
        return ExcelUtil.readObjDatas("/testcase/order/openPositionOfCross.xls", 8);
    }

    @DataProvider(name = "multi-two-way-short")
    public Object[][] getProvider9() {
        return ExcelUtil.readObjDatas("/testcase/order/openPositionOfCross.xls", 9);
    }

    @DataProvider(name = "limitOrder")
    public Object[][] getProvider10() {
        return ExcelUtil.readObjDatas("/testcase/order/openPositionOfCross.xls", 10);
    }

    @DataProvider(name = "takeProfitStopLoss")
    public Object[][] getProvider11() {
        return ExcelUtil.readObjDatas("/testcase/order/openPositionOfCross.xls", 11);
    }

    @DataProvider(name = "feeWithEt")
    public Object[][] getProvider12() {
        return ExcelUtil.readObjDatas("/testcase/order/openPositionOfCross.xls", 12);
    }

    // @Test(groups = {"smokeTest"}, description = "创建交易员", enabled = false)
    public void a_traderReady() {
        for (int i = 0; i < 12; i++) {
            Long trader = GenerateUserTest.createTraders();
            tradersOfCross.add(trader);
        }
        logger.info("tradersOfCross:" + tradersOfCross);
    }

    @Test(dataProvider = "one-way-long", groups = {"smokeTest"}, description = "交易下单-全仓-单向多")
    public void openOrder_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        // Long userId = 28670509L;
        Long userId = tradersOfCross.get(0);
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
        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        // 数据库断言
        dataAssert(userId, orderCountBefore, tradeVolume, symbol, (byte) tradeType, positionVolumeBefore);

        logger.info("***************************PASS***************************");
    }

    @Test(dataProvider = "one-way-short", groups = {"smokeTest"}, description = "交易下单-全仓-单向空")
    public void openOrder_Success_Test1(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        Long userId = tradersOfCross.get(1);
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
        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        // 数据库断言
        dataAssert(userId, orderCountBefore, tradeVolume, symbol, (byte) tradeType, positionVolumeBefore);

        logger.info("***************************PASS***************************");
    }

    @Test(dataProvider = "two-way-long", groups = {"smokeTest"}, description = "交易下单-全仓-双向净多")
    public void openOrder_Success_Test2(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        Long userId = tradersOfCross.get(2);
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
        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        // 数据库断言
        dataAssert(userId, orderCountBefore, tradeVolume, symbol, (byte) tradeType, positionVolumeBefore);

        logger.info("***************************PASS***************************");
    }

    @Test(dataProvider = "two-way-equal", groups = {"smokeTest"}, description = "交易下单-全仓-双向相等")
    public void openOrder_Success_Test3(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        Long userId = tradersOfCross.get(3);
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
        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        // 数据库断言
        dataAssert(userId, orderCountBefore, tradeVolume, symbol, (byte) tradeType, positionVolumeBefore);

        logger.info("***************************PASS***************************");
    }

    @Test(dataProvider = "two-way-short", groups = {"smokeTest"}, description = "交易下单-全仓-双向净空")
    public void openOrder_Success_Test4(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        Long userId = tradersOfCross.get(4);
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
        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        // 数据库断言
        dataAssert(userId, orderCountBefore, tradeVolume, symbol, (byte) tradeType, positionVolumeBefore);

        logger.info("***************************PASS***************************");
    }

    @Test(dataProvider = "multi-one-way-long", groups = {"smokeTest"}, description = "交易下单-全仓-多币对-单向多")
    public void openOrder_Success_Test5(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        Long userId = tradersOfCross.get(5);
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
        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        // 数据库断言
        dataAssert(userId, orderCountBefore, tradeVolume, symbol, (byte) tradeType, positionVolumeBefore);

        logger.info("***************************PASS***************************");
    }

    @Test(dataProvider = "multi-one-way-short", groups = {"smokeTest"}, description = "交易下单-全仓-多币对-单向空")
    public void openOrder_Success_Test6(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        Long userId = tradersOfCross.get(6);
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
        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        // 数据库断言
        dataAssert(userId, orderCountBefore, tradeVolume, symbol, (byte) tradeType, positionVolumeBefore);

        logger.info("***************************PASS***************************");
    }

    @Test(dataProvider = "multi-two-way-long", groups = {"smokeTest"}, description = "交易下单-全仓-多币对-双向净多")
    public void openOrder_Success_Test7(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        Long userId = tradersOfCross.get(7);
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
        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        // 数据库断言
        dataAssert(userId, orderCountBefore, tradeVolume, symbol, (byte) tradeType, positionVolumeBefore);

        logger.info("***************************PASS***************************");
    }

    @Test(dataProvider = "multi-two-way-equal", groups = {"smokeTest"}, description = "交易下单-全仓-多币对-双向相等")
    public void openOrder_Success_Test8(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        Long userId = tradersOfCross.get(8);
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
        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        // 数据库断言
        dataAssert(userId, orderCountBefore, tradeVolume, symbol, (byte) tradeType, positionVolumeBefore);

        logger.info("***************************PASS***************************");
    }

    @Test(dataProvider = "multi-two-way-short", groups = {"smokeTest"}, description = "交易下单-全仓-多币对-双向净空")
    public void openOrder_Success_Test9(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        Long userId = tradersOfCross.get(9);
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
        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        // 数据库断言
        dataAssert(userId, orderCountBefore, tradeVolume, symbol, (byte) tradeType, positionVolumeBefore);

        logger.info("***************************PASS***************************");
    }

    @Test(dataProvider = "limitOrder", groups = {"smokeTest"}, description = "交易下单-限价单")
    public void openLimitOrder_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        Long userId = tradersOfCross.get(10);
        reqHeaders.put("userId", String.valueOf(userId));
        JSONObject bodys = JSONObject.parseObject(params);
        String symbol = (String) bodys.get("symbol");
        int tradeType = (int) bodys.get("tradeType");

        BigDecimal newPrice = NewestPriceUtil.newPriceOfSwap(symbol);
        BigDecimal price;
        if (tradeType == 1) {
            price = newPrice.subtract(BigDecimal.valueOf(1000));
        } else {
            price = newPrice.add(BigDecimal.valueOf(1000));
        }
        bodys.put("price", price);

        // 存量订单数
        int orderCountBefore = TraderTransactionStatistics.queryOrders(userId).size();

        url = baseUrl + UrlProperty.getOrder();
        JSONObject resp = httpClientUtil.sendPostByJson(url, bodys, reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + bodys);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        // 响应断言
        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        // 数据库断言
        List<SwapFrontOrderEntity> swapFrontOrderEntityList = TraderTransactionStatistics.queryOrders(userId);
        int orderCountNow = swapFrontOrderEntityList.size();
        Assert.assertEquals(orderCountNow - orderCountBefore, 1, "用户" + userId + "交易下单异常");
        SwapFrontOrderEntity swapFrontOrderEntity = swapFrontOrderEntityList.get(0);
        Assert.assertEquals(swapFrontOrderEntity.getStatus(), 1,
                "限价单" + swapFrontOrderEntity.getId() + "状态异常，测试：" + swapFrontOrderEntity.getStatus() + "，预期：1");

        logger.info("***************************PASS***************************");
    }

    @Test(dataProvider = "takeProfitStopLoss", groups = {"smokeTest"}, description = "交易下单-设置止盈止损")
    public void openOrder_TakeProfitStopLoss_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        Long userId = tradersOfCross.get(11);
        // Long userId = 28649983L;
        reqHeaders.put("userId", String.valueOf(userId));
        JSONObject bodys = JSONObject.parseObject(params);
        String symbol = bodys.getString("symbol");
        int tradeType = bodys.getIntValue("tradeType");
        int orderPriceType = bodys.getIntValue("orderPriceType");

        BigDecimal newPrice = NewestPriceUtil.newPriceOfSwap(symbol);
        BigDecimal triggerPriceProfit;
        BigDecimal triggerPriceLoss;
        if (tradeType == 1 && orderPriceType == 3) {
            triggerPriceProfit = newPrice.add(BigDecimal.valueOf(5000));
            triggerPriceLoss = newPrice.subtract(BigDecimal.valueOf(5000));
            bodys.put("triggerPriceProfit", triggerPriceProfit);
            bodys.put("triggerPriceLoss", triggerPriceLoss);
        } else if (tradeType == 2 && orderPriceType == 1) {
            BigDecimal price = newPrice.add(BigDecimal.valueOf(2000));
            triggerPriceProfit = newPrice.subtract(BigDecimal.valueOf(5000));
            triggerPriceLoss = newPrice.add(BigDecimal.valueOf(5000));
            bodys.put("price", price);
            bodys.put("triggerPriceProfit", triggerPriceProfit);
            bodys.put("triggerPriceLoss", triggerPriceLoss);
        } else {
            logger.info("交易下单-设置止盈止损，其它场景暂不考虑");
        }

        // 存量订单数
        int orderCountBefore = TraderTransactionStatistics.queryOrders(userId).size();

        url = baseUrl + UrlProperty.getOrder();
        JSONObject resp = httpClientUtil.sendPostByJson(url, bodys, reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + bodys);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        // 响应断言
        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));
        Thread.sleep(1000);

        // 数据库断言
        // 1、swap_front_order记录+1
        List<SwapFrontOrderEntity> swapFrontOrderEntityList = TraderTransactionStatistics.queryOrders(userId);
        int orderCountNow = swapFrontOrderEntityList.size();
        Assert.assertEquals(orderCountNow - orderCountBefore, 1, "用户" + userId + "交易下单异常");
        SwapFrontOrderEntity swapFrontOrderEntity = swapFrontOrderEntityList.get(0);
        Long orderId = swapFrontOrderEntity.getId();
        // 2、订单状态
        if (orderPriceType == 3) {
            Assert.assertEquals(swapFrontOrderEntity.getStatus(), 2,
                    "市价单" + orderId + "状态异常，测试：" + swapFrontOrderEntity.getStatus() + "，预期：2已成交");
        } else {
            Assert.assertEquals(swapFrontOrderEntity.getStatus(), 1,
                    "限价单" + orderId + "状态异常，测试：" + swapFrontOrderEntity.getStatus() + "，预期：1交易中");
        }
        // 3、止盈止损
        SwapStopProfitLossEntity swapStopProfitLossEntity = TraderTransactionStatistics.queryStopProfitLossDetail(orderId);
        Assert.assertNotNull(swapStopProfitLossEntity, orderId + "订单异常，未查询到止盈止损数据");
        if (orderPriceType == 3) {
            Assert.assertEquals(swapStopProfitLossEntity.getState(), 1,
                    "市价单" + orderId + "止盈止损数据状态异常，测试：" + swapStopProfitLossEntity.getState() + "，预期：1已生效");
        } else {
            Assert.assertEquals(swapStopProfitLossEntity.getState(), 0,
                    "限价单" + orderId + "止盈止损数据状态异常，测试：" + swapStopProfitLossEntity.getState() + "，预期：0未生效");
        }
        // 4、计划单
        List<SwapFrontOrderPlanEntity> swapFrontOrderPlanEntities = TraderTransactionStatistics.queryPlanOrdersByOrderId(userId, orderId);
        if (orderPriceType == 3) {
            Assert.assertEquals(swapFrontOrderPlanEntities.size(), 2, "市价单" + orderId + "止盈止损计划单数据异常");
        } else {
            Assert.assertEquals(swapFrontOrderPlanEntities.size(), 0, "计划单" + orderId + "止盈止损计划单数据异常");
        }
        logger.info("***************************PASS***************************");
    }

    @Test(dataProvider = "feeWithEt", groups = {"smokeTest"}, description = "交易下单-et抵扣手续费")
    public void openOrder_FeeWithEt_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        // Long userId = 28670509L;
        Long userId = tradersOfCross.get(12);
        reqHeaders.put("userId", String.valueOf(userId));
        JSONObject bodys = JSONObject.parseObject(params);
        int tradeVolume = (int) bodys.get("volume");
        String symbol = (String) bodys.get("symbol");
        int tradeType = (int) bodys.get("tradeType");

        // 账户充值et
        AccountManagement.createSwapAccount(userId, "et", "et");

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
        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        // 数据库断言
        dataAssert(userId, orderCountBefore, tradeVolume, symbol, (byte) tradeType, positionVolumeBefore);

        logger.info("***************************PASS***************************");
    }


    public void dataAssert(Long userId, int orderCountBefore, int tradeVolume, String symbol, byte tradeType, Long positionVolumeBefore) throws InterruptedException {
        Thread.sleep(1000);
        // 数据库断言
        // 1、当前订单数量swap_front_order
        List<SwapFrontOrderEntity> swapFrontOrderEntityList = TraderTransactionStatistics.queryOrders(userId);
        int orderCountNow = swapFrontOrderEntityList.size();
        Assert.assertEquals(orderCountNow - orderCountBefore, 1, "用户" + userId + "交易下单异常");

        // 2、订单成交记录swap_front_order_record
        List<SwapFrontOrderEntity> swapFrontOrderEntities = TraderTransactionStatistics.queryOrder(userId, symbol, tradeType);
        Assert.assertTrue(swapFrontOrderEntities.size() > 0, "用户" + userId + "交易下单异常");
        Long orderId = swapFrontOrderEntities.get(0).getId();
        long volume = 0L;
        for (int i = 0; i < 12; i++) {
            volume = 0L;// 每次进入新一轮轮询时，将其重置为 0，防止重复累加
            List<SwapFrontOrderRecordEntity> swapFrontOrderRecordEntityList = TraderTransactionStatistics.queryOrderRecords(orderId);
            for (SwapFrontOrderRecordEntity entity : swapFrontOrderRecordEntityList) {
                Long num = entity.getTradeVolume();
                volume += num;
            }
            if (volume == tradeVolume) break;
            Thread.sleep(2000);
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
