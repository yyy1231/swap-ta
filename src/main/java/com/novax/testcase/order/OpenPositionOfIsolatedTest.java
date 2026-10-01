package com.novax.testcase.order;

import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.SwapFrontOrderEntity;
import com.novax.testcase.GenerateUserTest;
import com.novax.testcase.OpenSwapAccountAndAnswer;
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
 * 交易下单-开仓-逐仓
 * POST/swap/order
 */
public class OpenPositionOfIsolatedTest extends PublicParams {
    Logger logger = Logger.getLogger(OpenPositionOfIsolatedTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "open")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/order/openPositionOfIsolated.xls", 0);
    }

    // @Test(groups = {"smokeTest"}, description = "创建交易员",enabled = false)
    public void a_traderReady() {
        Long trader = GenerateUserTest.createTraders();
        OpenSwapAccountAndAnswer.updateMode(trader,2);
        tradersOfIsolated.add(trader);
        logger.info("tradersOfIsolated:" + tradersOfIsolated);
    }

    @Test(dataProvider = "open", groups = {"smokeTest"}, description = "交易下单-开仓-逐仓")
    public void openOrder_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        // Long userId = 28649983L;
        Long userId = tradersOfIsolated.get(0);
        reqHeaders.put("userId", String.valueOf(userId));
        JSONObject bodys = JSONObject.parseObject(params);
        int tradeVolume = bodys.getIntValue("volume");
        String symbol = bodys.getString("symbol");
        int tradeType = bodys.getIntValue("tradeType");
        int orderPriceType = bodys.getIntValue("orderPriceType");

        BigDecimal newPrice = NewestPriceUtil.newPriceOfSwap(symbol);
        BigDecimal price = null;
        BigDecimal triggerPriceLoss;
        BigDecimal triggerPriceProfit;
        if (tcDescp.equals("逐仓-限价-开多")) {
            price = newPrice.subtract(BigDecimal.valueOf(1000));
        }
        if (tcDescp.equals("逐仓-限价-开空")) {
            price = newPrice.add(BigDecimal.valueOf(1000));
        }
        if (tcDescp.equals("逐仓-市价-开多-设置止盈止损")) {
            triggerPriceProfit = newPrice.add(BigDecimal.valueOf(5000));
            triggerPriceLoss = newPrice.subtract(BigDecimal.valueOf(5000));
            bodys.put("triggerPriceProfit", triggerPriceProfit);
            bodys.put("triggerPriceLoss", triggerPriceLoss);
        }
        if (tcDescp.equals("逐仓-限价-开空-设置止盈止损")) {
            price = newPrice.add(BigDecimal.valueOf(1000));
            triggerPriceProfit = newPrice.subtract(BigDecimal.valueOf(5000));
            triggerPriceLoss = newPrice.add(BigDecimal.valueOf(5000));
            bodys.put("triggerPriceProfit", triggerPriceProfit);
            bodys.put("triggerPriceLoss", triggerPriceLoss);
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
        Thread.sleep(1000);

        // 数据库断言
        // 订单表
        List<SwapFrontOrderEntity> swapFrontOrderEntityList = TraderTransactionStatistics.queryOrders(userId);
        int orderCountNow = swapFrontOrderEntityList.size();
        Assert.assertEquals(orderCountNow - orderCountBefore, 1, "用户" + userId + "交易下单异常");


        logger.info("***************************PASS***************************");
    }
}
