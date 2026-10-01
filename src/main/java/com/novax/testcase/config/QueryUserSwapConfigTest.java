package com.novax.testcase.config;

import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.SwapFrontUserConfigEntity;
import com.novax.testcase.PublicParams;
import com.novax.testcase.SwapConfigInfo;
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

/**
 * 查询用户合约配置
 * GET/v3/user/symbol/config
 */
public class QueryUserSwapConfigTest extends PublicParams {
    Logger logger = Logger.getLogger(QueryUserSwapConfigTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/config/queryUserSwapConfig.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "查询用户合约配置")
    public void queryUserSwapConfig_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        // long userId = 28670664L;
        Long userId = traders.get(0);
        reqHeaders.put("userId", String.valueOf(userId));
        reqHeaders.put("Content-type", "application/x-www-form-urlencoded");
        HashMap<String, String> reqParams = StringToMap.stringToMap(params);

        url = baseUrl + UrlProperty.getUserConfig();
        JSONObject resp = httpClientUtil.sendGet(url, reqParams, reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + reqParams);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        // 响应断言
        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        // 数据库断言
        // 测试结果
        int mode = resp.getJSONObject("data").getIntValue("mode");
        // 预期结果
        SwapFrontUserConfigEntity configEntity = SwapConfigInfo.queryMode(userId);
        Integer modeType = configEntity.getMode();
        Assert.assertEquals(mode, modeType, "用户" + userId + "保证金模式查询结果有误，测试：" + mode + "，预期：" + modeType);

        logger.info("***************************PASS***************************");
    }

}
