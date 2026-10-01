package com.novax.testcase.assets;

import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.SwapFrontPositionEntity;
import com.novax.testcase.PublicParams;
import com.novax.testcase.TraderTransactionStatistics;
import com.novax.testcase.UserAssetsStatistics;
import com.novax.util.ExcelUtil;
import com.novax.util.HttpClientUtil;
import com.novax.util.NewestPriceUtil;
import com.novax.util.UrlProperty;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;

/**
 * 用户合约资产查询
 * GET/swap/assets
 */
public class QueryUserSwapAssetsTest extends PublicParams {
    Logger logger = Logger.getLogger(QueryUserSwapAssetsTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/assets/queryUserSwapAssets.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "用户合约资产查询")
    public void queryUserSwapAssets_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        // Long userId = 28649983L;
        Long userId = tradersOfCross.get(0);
        reqHeaders.put("userId", String.valueOf(userId));

        url = baseUrl + UrlProperty.getAssets();
        JSONObject resp = httpClientUtil.sendGet(url, new HashMap<>(), reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + params);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        JSONObject data = resp.getJSONObject("data");
        // 未实现盈亏
        BigDecimal profitLoss = data.getBigDecimal("profitLoss");
        // 钱包余额 todo bug:接口返回错误，非U资产未折U，所有币种的available直接求和了
        BigDecimal walletBalance = data.getBigDecimal("walletBalance");
        // 用户合约总资产折合usd
        BigDecimal valuationCurrency = data.getBigDecimal("valuationCurrency");
        // 用户合约总资产折合btc
        BigDecimal valuationCurrencyBtc = data.getBigDecimal("valuationCurrencyBtc");


        // 预期
        BigDecimal unrealizedProfitOrLossSum = BigDecimal.ZERO;
        // 查持仓
        List<SwapFrontPositionEntity> swapFrontPositionEntities = TraderTransactionStatistics.queryPositions2(userId, null, null);
        for (SwapFrontPositionEntity positon : swapFrontPositionEntities) {
            unrealizedProfitOrLossSum = unrealizedProfitOrLossSum.add(TraderTransactionStatistics.unrealizedProfitOrLoss(positon));
        }
        // 用户合约总资产折合usd
        BigDecimal swapAssetsToU = UserAssetsStatistics.swapAssetsToU(userId);
        BigDecimal newPrice = NewestPriceUtil.newPriceOfSymbol("btc_usdt");
        // 用户合约总资产折合btc
        BigDecimal swapAssetsToBTC = swapAssetsToU.divide(newPrice, 5, RoundingMode.HALF_DOWN);

        Assert.assertTrue(profitLoss.subtract(unrealizedProfitOrLossSum).abs().compareTo(BigDecimal.valueOf(5)) < 0,
                "用户" + userId + "未实现盈亏统计有误，测试：" + profitLoss + "，预期：" +unrealizedProfitOrLossSum);
        Assert.assertTrue(valuationCurrency.subtract(swapAssetsToU).abs().compareTo(BigDecimal.valueOf(1)) < 0,
                "用户" + userId + "用户合约总资产折合U统计有误，测试：" + valuationCurrency + "，预期：" +swapAssetsToU);
        Assert.assertTrue(valuationCurrencyBtc.subtract(swapAssetsToBTC).abs().compareTo(BigDecimal.valueOf(0.001)) < 0,
                "用户" + userId + "用户合约总资产折合BTC统计有误，测试：" + valuationCurrencyBtc + "，预期：" +swapAssetsToBTC);
        logger.info("***************************PASS***************************");
    }

}
