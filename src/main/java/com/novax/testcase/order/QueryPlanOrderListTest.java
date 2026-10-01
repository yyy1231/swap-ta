package com.novax.testcase.order;

import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.SwapFrontOrderPlanEntity;
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

import java.util.HashMap;
import java.util.List;

/**
 * 查询计划单
 * GET/swap/plan/order
 */
public class QueryPlanOrderListTest extends PublicParams {
    Logger logger = Logger.getLogger(QueryPlanOrderListTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/order/queryPlanOrderList.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "查询计划单")
    public void queryPlanOrderList_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        // Long userId = 28649983L;
        Long userId = tradersOfCross.get(12);
        reqHeaders.put("userId", String.valueOf(userId));
        HashMap<String, String> bodys = StringToMap.stringToMap(params);
        int status = Integer.parseInt(bodys.get("status"));

        url = baseUrl + UrlProperty.getQueryPlanOrder();
        JSONObject resp = httpClientUtil.sendGet(url, bodys, reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + bodys);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        int total = resp.getJSONObject("data").getIntValue("total");

        // 数据库断言
        if (status == 1) {
            List<SwapFrontOrderPlanEntity> swapFrontOrderPlanEntities = TraderTransactionStatistics.queryPeriodPlanOrders(userId);
            int size = swapFrontOrderPlanEntities.size();
            Assert.assertEquals(total, size, "用户" + userId + "等待委托状态的计划单数统计不一致，测试：" + total + "，预期：" +size);
        }
        if (status == 0) {
            List<SwapFrontOrderPlanEntity> swapFrontOrderPlanEntities = TraderTransactionStatistics.queryPeriodHistoryPlanOrders(userId);
            int size = swapFrontOrderPlanEntities.size();
            Assert.assertEquals(total, size, "用户" + userId + "历史委托计划单数统计不一致，测试：" + total + "，预期：" +size);
        }

        logger.info("***************************PASS***************************");

    }
}
