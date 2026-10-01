package com.novax.testcase.other;

import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.SwapFrontOrderEntity;
import com.novax.entity.mysql.SwapFrontOrderPlanEntity;
import com.novax.testcase.PublicParams;
import com.novax.testcase.SwapConfigInfo;
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
 * 测试场景：计划单触发
 * step1:小币种下计划单
 * step2:redis写入触发价
 * step3:断言计划单状态
 */
public class PlanOrderTriggerTest extends PublicParams {
    Logger logger = Logger.getLogger(PlanOrderTriggerTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "open")
    public Object[][] getProvider1() {
        return ExcelUtil.readObjDatas("/testcase/other/openPlanOrderOfCross.xls", 0);
    }

    @Test(groups = {"smokeTest"}, description = "计划单触发场景-step1-初始化配置")
    public void ready() {
        Long userId = traders.get(1);
        SwapConfigInfo.initialUserLeverage(userId, "storj_usdt");
    }

    @Test(dataProvider = "open", groups = {"smokeTest"}, dependsOnMethods = {"ready"}, description = "计划单")
    public void planOrder_open_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        Long userId = traders.get(1);
        // Long userId = 28649983L;

        reqHeaders.put("userId", String.valueOf(userId));
        JSONObject bodys = JSONObject.parseObject(params);
        String symbol = (String) bodys.get("symbol");
        int triggerType = bodys.getIntValue("triggerType");
        int orderPriceType = bodys.getIntValue("orderPriceType");

        BigDecimal newPrice = NewestPriceUtil.newPriceOfSwap(symbol);
        BigDecimal triggerPrice = newPrice;
        if (triggerType == 1) {
            triggerPrice = newPrice.add(BigDecimal.valueOf(0.01));
            bodys.put("triggerPrice", triggerPrice);
        } else if (triggerType == 2) {
            triggerPrice = newPrice.subtract(BigDecimal.valueOf(0.01));
            bodys.put("triggerPrice", triggerPrice);
        } else {
            logger.info("入参triggerType不合法");
        }

        if (orderPriceType == 1) {
            BigDecimal orderPrice = triggerPrice.add(new BigDecimal("0.001"));
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
        Thread.sleep(1000);

        // 数据库断言
        List<SwapFrontOrderPlanEntity> swapFrontOrderPlanEntityList = TraderTransactionStatistics.queryPlanOrders(userId);
        int orderCountNow = swapFrontOrderPlanEntityList.size();
        Assert.assertEquals(orderCountNow - orderCountBefore, 1, "用户" + userId + "计划单委托异常");
        SwapFrontOrderPlanEntity swapFrontOrderPlanEntity = swapFrontOrderPlanEntityList.get(0);
        Assert.assertEquals(swapFrontOrderPlanEntity.getStatus(), 1,
                "计划单" + swapFrontOrderPlanEntity.getId() + "状态异常，测试：" + swapFrontOrderPlanEntity.getStatus() + "，预期：1等待委托");

        logger.info("***************************PASS***************************");
    }

    @Test(groups = {"smokeTest"}, dependsOnMethods = {"planOrder_open_Success_Test"}, description = "计划单触发")
    public void planOrder_trigger_Success_Test() throws InterruptedException {
        Long userId = traders.get(1);
        // Long userId = 28649983L;

        // 目标价
        BigDecimal targetPrice = BigDecimal.ZERO;

        // 查计划单
        List<SwapFrontOrderPlanEntity> swapFrontOrderPlanEntityList = TraderTransactionStatistics.queryPlanOrders(userId);
        Assert.assertTrue(swapFrontOrderPlanEntityList.size() != 0, "用户" + userId + "，无等待委托状态的计划单，无计划单可撤");
        SwapFrontOrderPlanEntity planEntity = swapFrontOrderPlanEntityList.get(0);
        Long planEntityId = planEntity.getId();
        BigDecimal triggerPrice = planEntity.getTriggerPrice();
        Integer triggerType = planEntity.getTriggerType();

        if (triggerType == 1) {
            targetPrice = triggerPrice.add(BigDecimal.valueOf(0.005));
        }
        if (triggerType == 2) {
            targetPrice = triggerPrice.subtract(BigDecimal.valueOf(0.005));
        }

        // redis 写入触发目标价
        Long result = NewestPriceUtil.writeSwapPrice("swap:price:", "storj_usdt", targetPrice.doubleValue());
        Assert.assertEquals(result, 1, "redis触发价写入异常");
        Thread.sleep(10000);

        // 断言
        // 1、触发后，计划单状态从1等待委托更新到2已委托
        SwapFrontOrderPlanEntity swapFrontOrderPlanEntity = TraderTransactionStatistics.queryPlanOrder(planEntityId);
        Integer status = swapFrontOrderPlanEntity.getStatus();
        Assert.assertEquals(status, 2, "计划单" + planEntityId + "未触发");
        // 触发后，关联的委托单
        Long orderId = swapFrontOrderPlanEntity.getOrderId();

        // 2、查委托单
        SwapFrontOrderEntity swapFrontOrderEntity = TraderTransactionStatistics.queryOrder(orderId);
        Assert.assertNotNull(swapFrontOrderEntity, "计划单" + planEntityId + "触发后关联的委托单不存在");

        logger.info("***************************PASS***************************");
    }


}
