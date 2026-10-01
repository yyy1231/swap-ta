package com.novax.testcase.order;

import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.SwapFrontOrderEntity;
import com.novax.entity.mysql.SwapStopProfitLossEntity;
import com.novax.testcase.PublicParams;
import com.novax.testcase.TraderTransactionStatistics;
import com.novax.util.ExcelUtil;
import com.novax.util.HttpClientUtil;
import com.novax.util.StringToMap;
import com.novax.util.UrlProperty;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * 获取订单止盈止损详情
 * GET/v3/stop/profit/loss
 */
public class QueryOrderTakeProfitStopLossDetailTest extends PublicParams {
    Logger logger = Logger.getLogger(QueryOrderTakeProfitStopLossDetailTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/order/queryOrderTakeProfitStopLossDetail.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "获取订单止盈止损详情")
    public void queryOrderTakeProfitStopLossDetail_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        // Long userId = 28649983L;
        Long userId = tradersOfCross.get(11);
        reqHeaders.put("userId", String.valueOf(userId));

        // 查询设置止盈止损的委托单
        List<SwapFrontOrderEntity> swapFrontOrderEntities = TraderTransactionStatistics.queryOrdersWithStopProfitLoss(userId);
        Assert.assertTrue(swapFrontOrderEntities.size() != 0, "用户" + userId + "无设置了止盈止损的委托单");
        Long orderId = swapFrontOrderEntities.get(0).getId();

        HashMap<String, String> bodys = StringToMap.stringToMap(params);
        bodys.put("orderId", String.valueOf(orderId));

        url = baseUrl + UrlProperty.getStopProfitLossDetail();
        JSONObject resp = httpClientUtil.sendGet(url, bodys, reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + bodys);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        JSONObject data = resp.getJSONObject("data");
        BigDecimal triggerPriceLoss = data.getBigDecimal("triggerPriceLoss");
        BigDecimal triggerPriceProfit = data.getBigDecimal("triggerPriceProfit");
        int tradeType = data.getIntValue("tradeType");
        int state = data.getIntValue("state");

        // 数据库断言
        SwapStopProfitLossEntity swapStopProfitLossEntity = TraderTransactionStatistics.queryStopProfitLossDetail(orderId);
        BigDecimal triggerPriceLossExpect = swapStopProfitLossEntity.getTriggerPriceLoss();
        BigDecimal triggerPriceProfitExpect = swapStopProfitLossEntity.getTriggerPriceProfit();
        Integer tradeTypeExpect = swapStopProfitLossEntity.getTradeType();
        Integer stateExpect = swapStopProfitLossEntity.getState();

        Assert.assertTrue(triggerPriceLoss.subtract(triggerPriceLossExpect).compareTo(BigDecimal.ZERO) == 0,
                "订单" + orderId + "止损触发价查询不一致，测试：" + triggerPriceLoss + "，预期：" +triggerPriceLossExpect);
        Assert.assertTrue(triggerPriceProfit.subtract(triggerPriceProfitExpect).compareTo(BigDecimal.ZERO) == 0,
                "订单" + orderId + "止盈触发价查询不一致，测试：" + triggerPriceProfit + "，预期：" +triggerPriceProfitExpect);
        Assert.assertEquals(tradeType, tradeTypeExpect,
                "订单" + orderId + "tradeType查询不一致，测试：" + tradeType + "，预期：" +tradeTypeExpect);
        Assert.assertEquals(state, stateExpect,
                "订单" + orderId + "关联的止盈止损详情状态查询不一致，测试：" + state + "，预期：" +stateExpect);

        logger.info("***************************PASS***************************");

    }
}
