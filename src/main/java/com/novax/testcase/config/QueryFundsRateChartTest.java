package com.novax.testcase.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.SwapFrontFundsRateEntity;
import com.novax.testcase.PublicParams;
import com.novax.testcase.SwapConfigInfo;
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
 * 资金费率折线图
 * GET/swap/information/funds-rate
 */
public class QueryFundsRateChartTest extends PublicParams {
    Logger logger = Logger.getLogger(QueryFundsRateChartTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/config/queryFundsRateChart.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "资金费率折线图")
    public void queryFundsRateChart_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        reqHeaders.put("Content-type", "application/x-www-form-urlencoded");
        HashMap<String, String> reqParams = StringToMap.stringToMap(params);
        String symbol = reqParams.get("symbol");
        int days = Integer.parseInt(reqParams.get("days")) == 1 ? 7 : 90;

        url = baseUrl + UrlProperty.getFundsRateChart();
        JSONObject resp = httpClientUtil.sendGet(url, reqParams, reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + reqParams);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        // 响应断言
        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        // 响应
        JSONArray data = resp.getJSONArray("data");
        int periodNum = data.size();

        // 数据库断言
        List<SwapFrontFundsRateEntity> swapFrontFundsRateEntities = SwapConfigInfo.queryPeriodFundsRate(symbol, days);
        int num = swapFrontFundsRateEntities.size();
        Assert.assertTrue(periodNum - num <= 1, "交易对：" + symbol + "，周期：" + days + "，资金费率查询有误，测试：" + periodNum + "，预期：" + num);

        logger.info("***************************PASS***************************");
    }

}
