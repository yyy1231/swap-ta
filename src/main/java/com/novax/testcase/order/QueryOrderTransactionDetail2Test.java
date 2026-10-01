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

import java.util.HashMap;
import java.util.List;

/**
 * 合约单个委托成交记录
 * GET/swap/order/order-detail
 */
public class QueryOrderTransactionDetail2Test extends PublicParams {
    Logger logger = Logger.getLogger(QueryOrderTransactionDetail2Test.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/order/queryOrderTransactionDetail.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "合约单个委托成交记录")
    public void queryTransactionDetail_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        // Long userId = 28649983L;
        Long userId = tradersOfCross.get(0);
        reqHeaders.put("userId", String.valueOf(userId));

        List<SwapFrontOrderEntity> swapFrontOrderEntityList = TraderTransactionStatistics.queryOrders(userId);
        Assert.assertTrue(swapFrontOrderEntityList.size() != 0, "用户" + userId + "无委托单");
        Long orderId = swapFrontOrderEntityList.get(0).getId();
        HashMap<String, String> bodys = StringToMap.stringToMap(params);
        bodys.put("orderId", String.valueOf(orderId));

        url = baseUrl + UrlProperty.getOrderDetail2();
        JSONObject resp = httpClientUtil.sendGet(url, bodys, reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + bodys);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        JSONObject data = resp.getJSONObject("data");
        int volume = data.getIntValue("volume");
        int status = data.getIntValue("status");
        int isStopOrder = data.getIntValue("isStopOrder");

        // 数据库断言
        SwapFrontOrderEntity swapFrontOrderEntity = TraderTransactionStatistics.queryOrder(orderId);
        Long volumeExpect = swapFrontOrderEntity.getVolume();
        Integer statusExpect = swapFrontOrderEntity.getStatus();
        Integer isStopOrderExpect = swapFrontOrderEntity.getIsStopOrder();

        Assert.assertEquals(volume, volumeExpect, "订单" + orderId + "委托数量查询不一致，测试：" + volume + "，预期：" +volumeExpect);
        Assert.assertEquals(status, statusExpect, "订单" + orderId + "状态查询不一致，测试：" + status + "，预期：" +statusExpect);
        // Assert.assertEquals(isStopOrder, isStopOrderExpect, "订单" + orderId + "是否设置止盈止损查询不一致，测试：" + isStopOrder + "，预期：" +isStopOrderExpect);

        logger.info("***************************PASS***************************");
    }
}
