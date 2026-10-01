package com.novax.testcase.order;

import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.SwapFrontOrderPlanEntity;
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


/**
 * 交易下单-开仓-分仓 （--计划单）
 * POST/swap/plan/order
 */
public class OpenPlanOrderOfSplitTest extends PublicParams {
    Logger logger = Logger.getLogger(OpenPlanOrderOfSplitTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "open")
    public Object[][] getProvider1() {
        return ExcelUtil.readObjDatas("/testcase/order/openPlanOrderOfSplit.xls", 0);
    }

    @Test(description = "创建交易员")
    public void a_traderReady() {
        GenerateUserTest.createTraders(1);
    }

    @Test(dataProvider = "open", groups = {"smokeTest"}, description = "分仓-计划单-开仓")
    public void planOrder_open_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        Long userId = tradersOfSplit.get(7);
        // Long userId = 28649983L;
        reqHeaders.put("userId", String.valueOf(userId));
        JSONObject bodys = JSONObject.parseObject(params);
        String symbol = (String) bodys.get("symbol");
        int triggerType = bodys.getIntValue("triggerType");
        int orderPriceType = bodys.getIntValue("orderPriceType");

        BigDecimal newPrice = NewestPriceUtil.newPriceOfSwap(symbol);
        BigDecimal triggerPrice = newPrice;
        if (triggerType == 1) {
            triggerPrice = newPrice.add(BigDecimal.valueOf(1000));
            bodys.put("triggerPrice", triggerPrice);
        } else if (triggerType == 2) {
            triggerPrice = newPrice.subtract(BigDecimal.valueOf(1000));
            bodys.put("triggerPrice", triggerPrice);
        } else {
            logger.info("入参triggerType不合法");
        }

        if (orderPriceType == 1) {
            BigDecimal orderPrice = triggerPrice.add(new BigDecimal(100));
            bodys.put("orderPrice", orderPrice);
        }

        // 存量计划单数
        int orderCountBefore = TraderTransactionStatistics.queryPlanOrders(userId).size();

        url = baseUrl + UrlProperty.getPlanOrder();
        JSONObject resp = httpClientUtil.sendPostByJson(url, bodys, reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + bodys);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        // 响应断言
        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));
        Thread.sleep(500);

        // 数据库断言
        List<SwapFrontOrderPlanEntity> swapFrontOrderPlanEntityList = TraderTransactionStatistics.queryPlanOrders(userId);
        int orderCountNow = swapFrontOrderPlanEntityList.size();
        Assert.assertEquals(orderCountNow - orderCountBefore, 1, "用户" + userId + "计划单委托异常");
        SwapFrontOrderPlanEntity swapFrontOrderPlanEntity = swapFrontOrderPlanEntityList.get(0);
        Assert.assertEquals(swapFrontOrderPlanEntity.getStatus(), 1,
                "计划单" + swapFrontOrderPlanEntity.getId() + "状态异常，测试：" + swapFrontOrderPlanEntity.getStatus() + "，预期：1等待委托");

        logger.info("***************************PASS***************************");
    }
}
