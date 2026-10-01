package com.novax.testcase.assets;

import com.alibaba.fastjson.JSONObject;
import com.novax.testcase.PublicParams;
import com.novax.testcase.SwapConfigInfo;
import com.novax.testcase.TraderTransactionStatistics;
import com.novax.testcase.UserAssetsStatistics;
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
 * 查询交易页用户可划转资产
 * GET/swap/transfer/balance
 */
public class QueryUserTransferableAssetsTest extends PublicParams {
    Logger logger = Logger.getLogger(QueryUserTransferableAssetsTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/assets/queryUserTransferableAssets.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "查询交易页用户可划转资产")
    public void queryUserTransferableAssets_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        Long userId = 28649983L;
        // 关联相应保证金模式的user
        if (tcDescp.equals("查询交易页用户可划转资产-全仓")) {
            userId = tradersOfCross.get(0);
        }
        if (tcDescp.equals("查询交易页用户可划转资产-逐仓")) {
            userId = tradersOfIsolated.get(0);
        }
        if (tcDescp.equals("查询交易页用户可划转资产-分仓")) {
            userId = tradersOfSplit.get(0);
        }

        reqHeaders.put("userId", String.valueOf(userId));
        Integer mode = SwapConfigInfo.queryMode(userId).getMode();
        HashMap<String, String> bodys = StringToMap.stringToMap(params);
        String symbol = bodys.get("symbol");

        url = baseUrl + UrlProperty.getTransferBalance();
        JSONObject resp = httpClientUtil.sendGet(url, bodys, reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + params);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        // 测试结果
        JSONObject data = resp.getJSONObject("data");
        // 维持保证金
        BigDecimal margin = data.getBigDecimal("margin");
        // 保证金余额
        BigDecimal marginBalance = data.getBigDecimal("marginBalance");
        // 保证金率
        BigDecimal marginRate = data.getBigDecimal("marginRate");
        // 未实现盈亏
        BigDecimal profitLoss = data.getBigDecimal("profitLoss");
        // 可划转余额
        BigDecimal transferBalance = data.getBigDecimal("transferBalance");
        // 钱包余额
        BigDecimal walletBalance = data.getBigDecimal("walletBalance");

        // 预期结果
        // 维持保证金
        BigDecimal maintainMarginOfAccount = TraderTransactionStatistics.maintainMarginOfAccount(userId, symbol);
        // 保证金余额
        BigDecimal marginBalance1 = TraderTransactionStatistics.marginBalance(userId, symbol);
        // 保证金率
        BigDecimal marginRateOfAccount = TraderTransactionStatistics.marginRateOfAccount(userId, symbol).multiply(BigDecimal.valueOf(100));
        // 未实现盈亏
        BigDecimal unrealizedProfitOrLoss = TraderTransactionStatistics.unrealizedProfitOrLoss(userId, symbol);
        // 可划转余额
        BigDecimal transferableAmount = TraderTransactionStatistics.transferableAmount(userId, symbol);
        // 钱包余额
        BigDecimal available = UserAssetsStatistics.swapAccountAvailable(userId, "usdt", symbol);

        Assert.assertTrue(margin.subtract(maintainMarginOfAccount).abs().compareTo(BigDecimal.ZERO) == 0,
                "用户" + userId + "维持保证金计算有误，测试：" + margin + "，预期：" +maintainMarginOfAccount);
        if (mode == 1 || mode == 3) {
            Assert.assertTrue(marginBalance.subtract(marginBalance1).abs().compareTo(BigDecimal.valueOf(5)) < 0,
                    "用户" + userId + "保证金余额计算有误，测试：" + marginBalance + "，预期：" +marginBalance1);
            Assert.assertTrue(marginRate.subtract(marginRateOfAccount).abs().compareTo(BigDecimal.valueOf(5)) < 0,
                    "用户" + userId + "保证金率计算有误，测试：" + marginRate + "，预期：" +marginRateOfAccount);
            Assert.assertTrue(transferBalance.subtract(transferableAmount).abs().compareTo(BigDecimal.valueOf(5)) < 0,
                    "用户" + userId + "可划转余额计算有误，测试：" + transferBalance + "，预期：" +transferableAmount);
        } else { // mode==2逐仓：不计算保证金率;保证金余额、可划转余额和浮动盈亏无关，测试和预期不存在误差
            Assert.assertTrue(marginBalance.subtract(marginBalance1).abs().compareTo(BigDecimal.ZERO) == 0,
                    "用户" + userId + "保证金余额计算有误，测试：" + marginBalance + "，预期：" +marginBalance1);
            Assert.assertTrue(transferBalance.subtract(transferableAmount).abs().compareTo(BigDecimal.ZERO) == 0,
                    "用户" + userId + "可划转余额计算有误，测试：" + transferBalance + "，预期：" +transferableAmount);
        }
        Assert.assertTrue(profitLoss.subtract(unrealizedProfitOrLoss).abs().compareTo(BigDecimal.valueOf(5)) < 0,
                "用户" + userId + "未实现盈亏计算有误，测试：" + profitLoss + "，预期：" +unrealizedProfitOrLoss);
        Assert.assertTrue(walletBalance.subtract(available).abs().compareTo(BigDecimal.ZERO) == 0,
                "用户" + userId + "钱包余额计算有误，测试：" + walletBalance + "，预期：" +available);

        logger.info("***************************PASS***************************");
    }

}
