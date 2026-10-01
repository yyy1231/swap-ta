package com.novax.testcase.assets;

import com.alibaba.fastjson.JSONObject;
import com.novax.testcase.PublicParams;
import com.novax.testcase.SwapConfigInfo;
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

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * 查询用户该市场可交易资产
 * GET/swap/balance
 */
public class QueryUserTradableAssetsTest extends PublicParams {
    Logger logger = Logger.getLogger(QueryUserTradableAssetsTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/assets/queryUserTradableAssets.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "查询用户该市场可交易资产")
    public void queryUserTradableAssets_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        Long userId = 28649983L;
        // 关联相应保证金模式的user
        if (tcDescp.equals("查询用户该市场可交易资产-全仓")) {
            userId = tradersOfCross.get(0);
        }
        if (tcDescp.equals("查询用户该市场可交易资产-逐仓")) {
            userId = tradersOfIsolated.get(0);
        }
        if (tcDescp.equals("查询用户该市场可交易资产-分仓")) {
            userId = tradersOfSplit.get(0);
        }
        reqHeaders.put("userId", String.valueOf(userId));
        Integer mode = SwapConfigInfo.queryMode(userId).getMode();
        HashMap<String, String> bodys = StringToMap.stringToMap(params);
        String symbol = bodys.get("symbol");

        url = baseUrl + UrlProperty.getTradableAssets();
        JSONObject resp = httpClientUtil.sendGet(url, bodys, reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + params);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        JSONObject data = resp.getJSONObject("data");
        // 可用余额
        BigDecimal available = data.getBigDecimal("available");
        // 保证金模式
        int mode1 = data.getIntValue("mode");
        // symbol
        String symbol1 = data.getString("symbol");

        // 预期结果
        BigDecimal tradableAmount = TraderTransactionStatistics.tradableAmount(userId, symbol);

        Assert.assertTrue(available.subtract(tradableAmount).abs().compareTo(BigDecimal.valueOf(1)) < 0,
                "用户" + userId + "可用余额统计有误，测试：" + available + "，预期：" + tradableAmount);
        Assert.assertEquals(mode, mode1, "用户" + userId + "仓位保证金模式统计不一致");
        Assert.assertEquals(symbol, symbol1, "用户" + userId + "交易币对统计不一致");

        logger.info("***************************PASS***************************");
    }

}
