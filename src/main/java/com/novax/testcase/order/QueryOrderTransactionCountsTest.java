package com.novax.testcase.order;

import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.SwapFrontOrderRecordEntity;
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
 * 查询用户合约的交易量
 * GET/swap/count
 */
public class QueryOrderTransactionCountsTest extends PublicParams {
    Logger logger = Logger.getLogger(QueryOrderTransactionCountsTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/order/queryOrderTransactionCounts.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "查询用户合约的交易量")
    public void queryTransactionCounts_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        // Long userId = 28649983L;
        Long userId = tradersOfCross.get(0);
        HashMap<String, String> bodys = StringToMap.stringToMap(params);
        bodys.put("uid", String.valueOf(userId));
        int type = Integer.parseInt(bodys.get("type"));

        url = baseUrl + UrlProperty.getSwapCount();
        JSONObject resp = httpClientUtil.sendGet(url, bodys, reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + bodys);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        int data = resp.getIntValue("data");

        // 数据库断言
        if (type == 1) {
            List<SwapFrontOrderRecordEntity> swapFrontOrderRecordEntities = TraderTransactionStatistics.queryTodayTransactionRecords(userId);
            int size = swapFrontOrderRecordEntities.size();
            Assert.assertEquals(data, size, "用户" + userId + "今日成交记录统计不一致，测试：" + data + "，预期：" +size);
        }
        if (type == 0) {
            List<SwapFrontOrderRecordEntity> swapFrontOrderRecordEntities = TraderTransactionStatistics.queryTransactions(userId);
            int size = swapFrontOrderRecordEntities.size();
            Assert.assertEquals(data, size, "用户" + userId + "成交记录统计不一致，测试：" + data + "，预期：" +size);
        }

        logger.info("***************************PASS***************************");
    }
}
