package com.novax.testcase.order;

import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.SwapFrontOrderEntity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 撤销全部订单
 * POST/swap/order/repeal-all
 */

public class CancelAllOrdersTest extends PublicParams {
    Logger logger = Logger.getLogger(CancelAllOrdersTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/order/cancelAllOrders.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "撤销全部订单")
    public void cancelAllOrders_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        reqHeaders.put("Content-type", "application/x-www-form-urlencoded");
        // Long userId = 28649983L;
        Long userId = tradersOfCross.get(10);

        reqHeaders.put("userId", String.valueOf(userId));
        HashMap<String, String> bodys = StringToMap.stringToMap(params);
        String symbol = bodys.get("symbol");

        List<SwapFrontOrderEntity> swapFrontOrderEntityList = new ArrayList<>();
        if (tcDescp.equals("撤销全部订单-symbol")) {
            swapFrontOrderEntityList = TraderTransactionStatistics.queryInTradingOrders(userId, symbol);
        }
        if (tcDescp.equals("撤销全部订单-all")) {
            swapFrontOrderEntityList = TraderTransactionStatistics.queryInTradingOrders(userId);
        }
        // 断言交易中状态的订单
        Assert.assertTrue(swapFrontOrderEntityList.size() != 0, "用户" + userId + "，无交易中状态的订单，无单可撤");

        url = baseUrl + UrlProperty.getRepeaAlllOrders();
        JSONObject resp = httpClientUtil.sendPostByForm(url, bodys, reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + bodys);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));
        Thread.sleep(500);

        // 断言 订单状态更新为3已撤销
        for (SwapFrontOrderEntity entity : swapFrontOrderEntityList) {
            Long orderId = entity.getId();
            SwapFrontOrderEntity swapFrontOrderEntity = TraderTransactionStatistics.queryOrder(orderId);
            Assert.assertEquals(swapFrontOrderEntity.getStatus(), 3, "订单" + orderId + "撤销异常");
        }

        logger.info("***************************PASS***************************");
    }
}
