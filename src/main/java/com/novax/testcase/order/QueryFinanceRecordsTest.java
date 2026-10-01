package com.novax.testcase.order;

import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.SwapFrontFinanceRecordEntity;
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
 * 查询财务记录
 * GET/swap/finance/record
 */
public class QueryFinanceRecordsTest extends PublicParams {
    Logger logger = Logger.getLogger(QueryFinanceRecordsTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/order/queryFinanceRecords.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "查询财务记录")
    public void queryFinanceRecords_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        // Long userId = 28649983L;
        Long userId = tradersOfCross.get(2);
        reqHeaders.put("userId", String.valueOf(userId));
        HashMap<String, String> bodys = StringToMap.stringToMap(params);

        url = baseUrl + UrlProperty.getFinanceRecord();
        JSONObject resp = httpClientUtil.sendGet(url, bodys, reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + bodys);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));
        int total = resp.getJSONObject("data").getIntValue("total");

        // 数据库断言 operation>2再加其它查询条件
        List<SwapFrontFinanceRecordEntity> swapFrontFinanceRecordEntityList = TraderTransactionStatistics.queryFinanceRecord(userId, 7);
        int size = swapFrontFinanceRecordEntityList.size();
        Assert.assertEquals(total, size, "用户" + userId + "近7日的财务记录统计不一致，测试：" + total + "，预期：" + size);

        logger.info("***************************PASS***************************");

    }
}
