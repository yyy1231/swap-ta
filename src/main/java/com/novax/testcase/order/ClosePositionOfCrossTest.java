package com.novax.testcase.order;

import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.SwapFrontOrderEntity;
import com.novax.entity.mysql.SwapFrontPositionEntity;
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


/**
 * 交易下单-平仓-全仓
 * POST/swap/order
 */
public class ClosePositionOfCrossTest extends PublicParams {
    Logger logger = Logger.getLogger(ClosePositionOfCrossTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas_success")
    public Object[][] getProvider1() {
        return ExcelUtil.readObjDatas("/testcase/order/closePositionOfCross.xls", 0);
    }

    @Test(dataProvider = "datas_success", groups = {"smokeTest"}, description = "交易下单-平仓-全仓")
    public void closePositions_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        // Long userId = 28649983L;
        Long userId = tradersOfCross.get(2);
        reqHeaders.put("userId", String.valueOf(userId));

        JSONObject bodys = JSONObject.parseObject(params);
        String symbol = bodys.getString("symbol");
        int tradeType = bodys.getIntValue("tradeType");
        int orderPriceType = bodys.getIntValue("orderPriceType");
        byte direction = (byte) (tradeType == 3 ? 1 : 2);

        // 存量订单数
        int orderCountBefore = TraderTransactionStatistics.queryOrders(userId).size();

        // 存量仓位
        SwapFrontPositionEntity swapFrontPositionEntity = TraderTransactionStatistics.queryPositions(userId, symbol, direction);
        Assert.assertNotNull(swapFrontPositionEntity, "用户" + userId + "当前无持仓，无仓可平，或开仓单异常（无成交）");
        Long positionId = swapFrontPositionEntity.getId();
        Long volume = swapFrontPositionEntity.getVolume();
        Long frozen = swapFrontPositionEntity.getFrozen();
        BigDecimal costOpen = swapFrontPositionEntity.getCostOpen();
        float availableVolume = volume - frozen;
        Assert.assertTrue(availableVolume > 0, "用户" + userId + "仓位" + positionId + "全部冻结，不可平");

        int closeAmount = (int) Math.ceil(availableVolume / 2);
        bodys.put("volume", closeAmount);

        BigDecimal newPrice = NewestPriceUtil.newPriceOfSwap(symbol);
        BigDecimal price = null;
        if (orderPriceType == 1 && tradeType == 3) {
            // price = costOpen.add(BigDecimal.valueOf(1000)).setScale(1, RoundingMode.HALF_DOWN);
            price = newPrice.add(BigDecimal.valueOf(1000));
        }
        if (orderPriceType == 1 && tradeType == 4) {
            // price = costOpen.subtract(BigDecimal.valueOf(1000)).setScale(1, RoundingMode.HALF_DOWN);
            price = newPrice.subtract(BigDecimal.valueOf(1000));
        }
        bodys.put("price", price);

        url = baseUrl + UrlProperty.getOrder();
        JSONObject resp = httpClientUtil.sendPostByJson(url, bodys, reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + bodys);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));
        Thread.sleep(1000);

        // 断言
        // 1、当前订单数量swap_front_order
        List<SwapFrontOrderEntity> swapFrontOrderEntityList = TraderTransactionStatistics.queryOrders(userId);
        int orderCountNow = swapFrontOrderEntityList.size();
        Assert.assertEquals(orderCountNow - orderCountBefore, 1, "用户" + userId + "交易下单异常");
        SwapFrontOrderEntity swapFrontOrderEntity = swapFrontOrderEntityList.get(0);
        Long orderId = swapFrontOrderEntity.getId();

        // 2、当前仓位
        SwapFrontPositionEntity positions = TraderTransactionStatistics.queryPositions(userId, symbol, direction);
        Long volumeNow = positions.getVolume();
        if (orderPriceType == 3) {
            Assert.assertEquals(swapFrontOrderEntity.getStatus(), 2, "用户" + userId + "市价单" + orderId + "状态异常，或未完全成交");
            Assert.assertEquals(volume - volumeNow, closeAmount, "用户" + userId + "市价单" + orderId + "交易异常");
        }
        if (orderPriceType == 1) {
            Assert.assertEquals(swapFrontOrderEntity.getStatus(), 1, "用户" + userId + "限价单" + orderId + "状态异常，或市场波动较大已达到卖价：" + price);
            Assert.assertEquals(volume - volumeNow, 0, "用户" + userId + "限价单" + orderId + "交易异常");
        }

        logger.info("*****************************PASS*****************************");
    }

}
