package com.novax.testcase.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.SwapFrontConfigEntity;
import com.novax.testcase.PublicParams;
import com.novax.testcase.SwapConfigInfo;
import com.novax.util.ExcelUtil;
import com.novax.util.HttpClientUtil;
import com.novax.util.UrlProperty;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;

/**
 * 获取配置了最大杠杆倍数的币对
 * GET/public/v3/maxleverage/symbol
 */
public class QueryMaxLeverageSymbolsTest extends PublicParams {
    Logger logger = Logger.getLogger(QueryMaxLeverageSymbolsTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/config/queryMaxLeverageSymbols.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "获取配置了最大杠杆倍数的币对")
    public void queryMaxLeverageSymbols_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        reqHeaders.put("Content-type", "application/x-www-form-urlencoded");

        url = baseUrl + UrlProperty.getMaxleverage();
        JSONObject resp = httpClientUtil.sendGet(url, new HashMap<>(), new HashMap<>());

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + "");
        logger.info(tcNum + "-入参:" + "");
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        // 响应断言
        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        // 测试结果
        JSONArray data = resp.getJSONArray("data");
        int size = data.size();

        // 数据库断言
        List<SwapFrontConfigEntity> swapFrontConfigEntityList = SwapConfigInfo.queryMaxLeverageSymbolList();
        int num = swapFrontConfigEntityList.size();
        Assert.assertEquals(size, num, "获取配置了最大杠杆倍数的币对，测试：" + size + "，预期：" + num);

        logger.info("***************************PASS***************************");
    }

}
