package com.novax.testcase.calculator;

import com.alibaba.fastjson.JSONObject;
import com.novax.testcase.PublicParams;
import com.novax.testcase.SwapCalculator;
import com.novax.util.ExcelUtil;
import com.novax.util.HttpClientUtil;
import com.novax.util.UrlProperty;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;

/**
 * 强平价格-双向持仓
 * POST/calc/public/double-strong-price
 */
public class LiquidationPriceOfTwoWayPositionTest extends PublicParams {
    Logger logger = Logger.getLogger(LiquidationPriceOfTwoWayPositionTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/calculator/liquidationPriceOfTwoWayPosition.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "强平价格-双向持仓")
    public void liquidationPriceOfTwoWayPosition_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        JSONObject bodys = JSONObject.parseObject(params);
        String symbol = bodys.getString("symbol");
        int longVolume = bodys.getIntValue("count");
        BigDecimal longOpenPrice = bodys.getBigDecimal("openPrice");
        int shortVolume = bodys.getIntValue("blankCount");
        BigDecimal shortOpenPrice = bodys.getBigDecimal("blankOpenPrice");
        BigDecimal walletBalance = bodys.getBigDecimal("walletBalance");

        url = baseUrl + UrlProperty.getDoubleStrongPrice();
        JSONObject resp = httpClientUtil.sendPostByJson(url, bodys, reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + bodys);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        // 测试
        BigDecimal price = resp.getJSONObject("data").getBigDecimal("price");
        // 预期
        BigDecimal forceClosePrice = SwapCalculator.liquidationPriceOfTwoWayPosition(symbol,longVolume,longOpenPrice,shortVolume,shortOpenPrice,walletBalance);
        Assert.assertTrue(price.subtract(forceClosePrice).abs().compareTo(BigDecimal.valueOf(0.001)) < 0,
                "合约计算器，强平价格-双向持仓，强平价格计算不一致，测试：" + price + "，预期：" + forceClosePrice);

        logger.info("***************************PASS***************************");
    }
}
