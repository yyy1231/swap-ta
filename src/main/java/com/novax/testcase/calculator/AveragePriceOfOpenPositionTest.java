package com.novax.testcase.calculator;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.novax.pojo.CalculatorPosition;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 开仓均价
 * POST/calc/public/average-price
 */
public class AveragePriceOfOpenPositionTest extends PublicParams {
    Logger logger = Logger.getLogger(AveragePriceOfOpenPositionTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/calculator/averagePriceOfOpenPosition.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "开仓均价")
    public void averagePriceOfOpenPosition_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        JSONObject bodys = JSONObject.parseObject(params);
        JSONArray priceAndCountsList = bodys.getJSONArray("priceAndCounts");
        List<CalculatorPosition> positions = new ArrayList<>();
        for (int i = 0; i < priceAndCountsList.size(); i++) {
            CalculatorPosition calculatorPosition = new CalculatorPosition();
            long count = priceAndCountsList.getJSONObject(i).getIntValue("count");
            BigDecimal price = priceAndCountsList.getJSONObject(i).getBigDecimal("price");
            calculatorPosition.setOpenVolume(count);
            calculatorPosition.setOpenPrice(price);
            positions.add(i, calculatorPosition);
        }

        url = baseUrl + UrlProperty.getAveragePrice();
        JSONObject resp = httpClientUtil.sendPostByJson(url, bodys, reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + bodys);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));
        BigDecimal averagePrice = resp.getJSONObject("data").getBigDecimal("averagePrice");

        // 预期
        BigDecimal averagePriceOfOpenPosition = SwapCalculator.averagePriceOfOpenPosition(positions);

        // 断言
        Assert.assertTrue(averagePrice.subtract(averagePriceOfOpenPosition).abs().compareTo(BigDecimal.valueOf(0.1)) < 0,
                "合约计算器，开仓均价计算有误，测试：" + averagePrice + "，预期：" + averagePriceOfOpenPosition);

        logger.info("***************************PASS***************************");
    }
}
