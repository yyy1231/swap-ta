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
 * 平仓价格-收益
 * GET/calc/public/close-price
 */
public class ClosePositionPriceForIncomeTest extends PublicParams {
    Logger logger = Logger.getLogger(ClosePositionPriceForIncomeTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/calculator/closePositionPriceForIncome.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "平仓价格-收益")
    public void closePositionPriceForIncome_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        HashMap<String, String> bodys = StringToMap.stringToMap(params);
        String symbol = bodys.get("symbol");
        int direction = Integer.parseInt(bodys.get("type"));
        BigDecimal openPrice = new BigDecimal(bodys.get("openPrice"));
        int volume = Integer.parseInt(bodys.get("numberPositClose"));
        BigDecimal income = new BigDecimal(bodys.get("inCom"));

        url = baseUrl + UrlProperty.getClosePrice();
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
        BigDecimal closePositionPriceForIncome = SwapCalculator.closePositionPriceForIncome(symbol, volume, openPrice, direction, income);

        // 断言
        Assert.assertTrue(price.subtract(closePositionPriceForIncome).abs().compareTo(BigDecimal.valueOf(0.1)) < 0,
                "合约计算器，获得预期收益所需的平仓价，计算有误，测试：" + price + "，预期：" + closePositionPriceForIncome);

        logger.info("***************************PASS***************************");
    }
}
