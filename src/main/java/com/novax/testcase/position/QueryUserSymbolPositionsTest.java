package com.novax.testcase.position;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.SwapFrontPositionEntity;
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

import java.util.HashMap;
import java.util.List;

/**
 * 用户持仓信息 symbol
 * GET/swap/position
 */
public class QueryUserSymbolPositionsTest extends PublicParams {
    Logger logger = Logger.getLogger(QueryUserSymbolPositionsTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/position/queryUserSymbolPositions.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "查询用户持仓信息")
    public void queryUserPositions_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        reqHeaders.put("Content-type", "application/x-www-form-urlencoded");
        // Long userId = 28649983L;
        Long userId = tradersOfCross.get(0);
        reqHeaders.put("userId", String.valueOf(userId));
        HashMap<String, String> bodys = StringToMap.stringToMap(params);
        String symbol = bodys.get("symbol");

        url = baseUrl + UrlProperty.getPosition();
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

        // 断言
        List<SwapFrontPositionEntity> swapFrontPositionEntities = TraderTransactionStatistics.queryPositions2(userId, symbol, null);
        int size = swapFrontPositionEntities.size();
        Assert.assertEquals(count, size, "用户" + userId + "持仓信息查询有误");

        logger.info("***************************PASS***************************");
    }

}
