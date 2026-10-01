package com.novax.testcase.calculator;

import com.alibaba.fastjson.JSONObject;
import com.novax.pojo.CalculatorResults;
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
 * 收益
 * GET/calc/public/incom
 */
public class IncomeTest extends PublicParams {
    Logger logger = Logger.getLogger(IncomeTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/calculator/income.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "收益")
    public void income_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        HashMap<String, String> bodys = StringToMap.stringToMap(params);
        String symbol = bodys.get("symbol");
        int direction = Integer.parseInt(bodys.get("type"));
        BigDecimal openPrice = new BigDecimal(bodys.get("openPrice"));
        BigDecimal closePrice = new BigDecimal(bodys.get("closePrice"));
        int volume = Integer.parseInt(bodys.get("numberPositClose"));
        int leverage = Integer.parseInt(bodys.get("leverage"));

        url = baseUrl + UrlProperty.getProfit();
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
        BigDecimal inCome = data.getBigDecimal("inCome");
        // 保证金
        BigDecimal margin = data.getBigDecimal("secureDeposit");
        // 回报率
        BigDecimal returnRate = data.getBigDecimal("returnRate");

        // 预期
        CalculatorResults results = SwapCalculator.income(symbol, volume, openPrice, closePrice, direction, leverage);
        BigDecimal income = results.getIncome();
        BigDecimal margin1 = results.getMargin();
        BigDecimal returnRate1 = results.getReturnRate();

        // 断言
        Assert.assertTrue(inCome.subtract(income).abs().compareTo(BigDecimal.valueOf(0.1)) < 0,
                "合约计算器，平仓收益计算有误，测试：" + inCome + "，预期：" +income);
        Assert.assertTrue(margin.subtract(margin1).abs().compareTo(BigDecimal.ZERO) == 0,
                "合约计算器，持仓保证金计算有误，测试：" + margin + "，预期：" +margin1);
        Assert.assertTrue(returnRate.subtract(returnRate1).abs().compareTo(BigDecimal.valueOf(0.01)) < 0,
                "合约计算器，回报率计算有误，测试：" + returnRate + "，预期：" +returnRate1);

        logger.info("***************************PASS***************************");
    }
}
