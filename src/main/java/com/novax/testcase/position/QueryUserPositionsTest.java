package com.novax.testcase.position;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.SwapFrontPositionEntity;
import com.novax.testcase.EstimatedForceClosePositionPriceCalculation;
import com.novax.testcase.PublicParams;
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
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 用户持仓信息
 * GET/swap/position/all
 */
public class QueryUserPositionsTest extends PublicParams {
    Logger logger = Logger.getLogger(QueryUserPositionsTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/position/queryUserPositions.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "用户持仓信息")
    public void queryUserPositions_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        reqHeaders.put("Content-type", "application/x-www-form-urlencoded");
        Long userId = 28649983L;
        if (tcDescp.equals("用户持仓信息-全仓-all") || tcDescp.equals("用户持仓信息-全仓-symbol")) {
            userId = tradersOfCross.get(9);
        }
        if (tcDescp.equals("用户持仓信息-逐仓-all") || tcDescp.equals("用户持仓信息-逐仓-symbol")) {
            userId = tradersOfIsolated.get(0);
        }
        if (tcDescp.equals("用户持仓信息-分仓-all") || tcDescp.equals("用户持仓信息-分仓-symbol")) {
            userId = tradersOfSplit.get(4);
        }
        reqHeaders.put("userId", String.valueOf(userId));
        HashMap<String, String> bodys = StringToMap.stringToMap(params);
        String symbol = bodys.get("symbol");

        url = baseUrl + UrlProperty.getPositionAll();
        JSONObject resp = httpClientUtil.sendGet(url, StringToMap.stringToMap(params), reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + params);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        JSONArray data = resp.getJSONArray("data");
        int count = data.size();
        JSONObject positionJson = data.getJSONObject(0);
        Long positionId = positionJson.getLongValue("id");
        int mode = positionJson.getIntValue("mode");

        // 未实现盈亏
        BigDecimal profit = positionJson.getBigDecimal("profit");
        // 仓位保证金
        BigDecimal margin = positionJson.getBigDecimal("margin");
        // 保证金比率
        BigDecimal marginRate = positionJson.getBigDecimal("marginRate");
        // 回报率=未实现盈亏/margin
        BigDecimal profitRate = positionJson.getBigDecimal("profitRate");
        // 强平价格
        BigDecimal liquidationPrice = positionJson.getBigDecimal("liquidationPrice");

        // 断言
        List<SwapFrontPositionEntity> swapFrontPositionEntities = TraderTransactionStatistics.queryPositions2(userId, symbol, null);
        int size = swapFrontPositionEntities.size();
        Assert.assertEquals(count, size, "用户" + userId + "持仓信息查询有误");
        SwapFrontPositionEntity position = null;
        for (SwapFrontPositionEntity positionEntity : swapFrontPositionEntities) {
            if (Objects.equals(positionEntity.getId(), positionId)) {
                position = positionEntity;
            }
        }

        // 预期结果
        // 未实现盈亏
        BigDecimal unrealizedProfitOrLoss = TraderTransactionStatistics.unrealizedProfitOrLoss(position);
        // 仓位保证金
        BigDecimal marginOfPosition = TraderTransactionStatistics.initialMarginOfPosition(mode, position);
        // 保证金比率
        BigDecimal marginRateOfPosition = TraderTransactionStatistics.marginRateOfPosition(userId, position)
                .multiply(BigDecimal.valueOf(100));
        // 回报率
        BigDecimal returnRate = unrealizedProfitOrLoss.divide(marginOfPosition, 5, RoundingMode.HALF_DOWN)
                .multiply(BigDecimal.valueOf(100));
        // 强平价格
        BigDecimal forceClosePositonPrice = EstimatedForceClosePositionPriceCalculation.estimatedForceClosePositonPrice(position);


        Assert.assertTrue(profit.subtract(unrealizedProfitOrLoss).abs().compareTo(BigDecimal.valueOf(5)) <= 0,
                "用户" + userId + "持仓" + position.getId() + "未实现盈亏计算有误，测试：" + profit + "，预期：" +unrealizedProfitOrLoss);
        Assert.assertTrue(margin.subtract(marginOfPosition).abs().compareTo(BigDecimal.valueOf(0.1)) <= 0,
                "用户" + userId + "持仓" + position.getId() + "仓位保证金计算有误，测试：" + margin + "，预期：" +marginOfPosition);
        Assert.assertTrue(marginRate.subtract(marginRateOfPosition).abs().compareTo(BigDecimal.valueOf(10)) <= 0,
                "用户" + userId + "持仓" + position.getId() + "保证金比率计算有误，测试：" + marginRate + "，预期：" +marginRateOfPosition);
        Assert.assertTrue(profitRate.subtract(returnRate).abs().compareTo(BigDecimal.valueOf(10)) <= 0,
                "用户" + userId + "持仓" + position.getId() + "回报率计算有误，测试：" + profitRate + "，预期：" +returnRate);
        Assert.assertTrue(liquidationPrice.subtract(forceClosePositonPrice).abs().compareTo(BigDecimal.valueOf(100)) <= 0,
                "用户" + userId + "持仓" + position.getId() + "强平价格计算有误，测试：" + liquidationPrice + "，预期：" +forceClosePositonPrice);

        logger.info("***************************PASS***************************");
    }

}
