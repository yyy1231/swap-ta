package com.novax.testcase.calculator;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.novax.pojo.PositionCalculation;
import com.novax.testcase.PublicParams;
import com.novax.testcase.SwapCalculator;
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
 * 头寸计算
 * GET/calc/public/posit-calculi
 */
public class PositionCalculateTest extends PublicParams {
    Logger logger = Logger.getLogger(PositionCalculateTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/calculator/positionCalculate.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "头寸计算")
    public void positionCalculate_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        HashMap<String, String> bodys = StringToMap.stringToMap(params);
        String symbol = bodys.get("symbol");

        url = baseUrl + UrlProperty.getPositCalc();
        JSONObject resp = httpClientUtil.sendGet(url, bodys, reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + bodys);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        JSONArray data = resp.getJSONArray("data");
        int size = data.size();

        // 预期
        List<PositionCalculation> positionCalculationList = SwapCalculator.positionCalculate(symbol);
        int size1 = positionCalculationList.size();

        // 断言
        Assert.assertEquals(size, size1, "交易对头寸计算有误，测试：" + size + "，预期：" + size1);
        for (int i = 0; i < size; i++) {
            // 测试
            JSONObject position = data.getJSONObject(i);
            int maxLeverage = position.getIntValue("maxLeverage");
            int minLeverage = position.getIntValue("minLeverage");
            int maxVolume = position.getIntValue("maxVolume");
            // 预期
            PositionCalculation positionCalculation = positionCalculationList.get(i);
            Integer maxLeverage1 = positionCalculation.getMaxLeverage();
            Integer minLeverage1 = positionCalculation.getMinLeverage();
            Long maxVolume1 = positionCalculation.getMaxVolume();

            Assert.assertEquals(maxLeverage, maxLeverage1, "交易对" + symbol + "头寸计算有误maxLeverage");
            Assert.assertEquals(minLeverage, minLeverage1, "交易对" + symbol + "头寸计算有误minLeverage");
            Assert.assertEquals(maxVolume, maxVolume1, "交易对" + symbol + "头寸计算有误maxVolume");
        }

        logger.info("***************************PASS***************************");
    }
}
