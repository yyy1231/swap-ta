package com.novax.testcase.calculator;

import com.alibaba.fastjson.JSONObject;
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

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * 平仓价格-收益率
 * GET/calc/public/close-pri
 */
public class ClosePositionPriceForIncomeRateTest extends PublicParams {
    Logger logger = Logger.getLogger(ClosePositionPriceForIncomeRateTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/calculator/closePositionPriceForIncomeRate.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "平仓价格-收益")
    public void closePositionPriceForIncomeRate_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        HashMap<String, String> bodys = StringToMap.stringToMap(params);
        int direction = Integer.parseInt(bodys.get("type"));
        BigDecimal openPrice = new BigDecimal(bodys.get("openPrice"));
        int leverage = Integer.parseInt(bodys.get("leverage"));
        BigDecimal returnRate = new BigDecimal(bodys.get("returnRate"));

        url = baseUrl + UrlProperty.getClosePri();
        JSONObject resp = httpClientUtil.sendGet(url, bodys, reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + bodys);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        JSONObject data = resp.getJSONObject("data");
        // 收益
        BigDecimal price = data.getBigDecimal("price");

        // 预期
        BigDecimal closePositionPriceForIncome = SwapCalculator.closePositionPriceForIncomeRate(openPrice, direction, leverage, returnRate);

        // 断言
        Assert.assertTrue(price.subtract(closePositionPriceForIncome).abs().compareTo(BigDecimal.valueOf(0.1)) < 0,
                "合约计算器，获得预期收益率所需的平仓价，计算有误，测试：" + price + "，预期：" + closePositionPriceForIncome);

        logger.info("***************************PASS***************************");
    }
}
