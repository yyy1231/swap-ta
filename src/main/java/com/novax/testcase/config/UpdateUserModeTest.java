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
 * 设置用户合约保证金模式
 * POST/front/swap/user/mode
 */
public class UpdateUserModeTest extends PublicParams {
    Logger logger = Logger.getLogger(UpdateUserModeTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/config/updateUserMode.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "设置用户合约保证金模式")
    public void updateMode_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        // long userId = 28670664L;
        Long userId = traders.get(0);
        reqHeaders.put("userId", String.valueOf(userId));
        reqHeaders.put("Content-type", "application/x-www-form-urlencoded");
        HashMap<String, String> reqParams = StringToMap.stringToMap(params);
        // reqParams.put("mode", "1");
        int mode = Integer.parseInt(reqParams.get("mode"));

        url = baseUrl + UrlProperty.getMode();
        JSONObject resp = httpClientUtil.sendPostByForm(url, reqParams, reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + reqParams);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        // 响应断言
        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));
        Thread.sleep(500);

        // 数据库断言
        SwapFrontUserConfigEntity configEntity = SwapConfigInfo.queryMode(userId);
        Integer modeType = configEntity.getMode();
        Assert.assertEquals(modeType, mode,
                "用户" + userId + "保证金模式设置有误，测试：" + modeType + "，预期：" + mode);

        logger.info("***************************PASS***************************");
    }

}
