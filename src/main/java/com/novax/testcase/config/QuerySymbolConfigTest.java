package com.novax.testcase.config;

import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.SwapFrontConfigEntity;
import com.novax.testcase.PublicParams;
import com.novax.testcase.SwapConfigInfo;
import com.novax.util.*;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * 查询合约市场指定币对配置
 * GET/front/swapConfig/{symbol}
 */
public class QuerySymbolConfigTest extends PublicParams {
    Logger logger = Logger.getLogger(QuerySymbolConfigTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/config/querySymbolConfig.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "查询合约市场指定币对配置")
    public void querySymbolConfig_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        reqHeaders.put("Content-type", "application/x-www-form-urlencoded");
        HashMap<String, String> reqParams = StringToMap.stringToMap(params);
        String symbol = reqParams.get("symbol");

        url = baseUrl + UrlProperty.getSymbolConfig();
        String requestUrl = VariableReplace.replace(url, symbol);
        JSONObject resp = httpClientUtil.sendGet(requestUrl, new HashMap<>(), new HashMap<>());

        logger.info(tcNum + "-requestUrl:" + requestUrl);
        logger.info(tcNum + "-headers:" + "");
        logger.info(tcNum + "-入参:" + "");
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        // 响应断言
        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        // 响应
        JSONObject data = resp.getJSONObject("data");
        // 面值
        BigDecimal contractSize = data.getBigDecimal("contractSize");
        // 单笔最大挂单张数
        long maxVolume = data.getLongValue("maxVolume");
        // 单笔最小挂单张数
        long minVolume = data.getLongValue("minVolume");
        // 合约价格最小变动精度
        BigDecimal priceTick = data.getBigDecimal("priceTick");
        // 状态：1=使用，0=禁用
        byte status = data.getByteValue("status");

        // 预期
        SwapFrontConfigEntity config = SwapConfigInfo.querySymbolConfig(symbol);
        BigDecimal configContractSize = config.getContractSize();
        Long configMaxVolume = config.getMaxVolume();
        Long configMinVolume = config.getMinVolume();
        BigDecimal configPriceTick = config.getPriceTick();
        Byte configStatus = config.getStatus();

        // 断言
        Assert.assertEquals(contractSize.subtract(configContractSize).compareTo(BigDecimal.ZERO), 0,
                "标的指数" + symbol + "[面值]与库中swap_front_config配置不一致");
        Assert.assertEquals(maxVolume - configMaxVolume, 0,
                "标的指数" + symbol + "[单笔最大挂单张数]与库中swap_front_config配置不一致");
        Assert.assertEquals(minVolume - configMinVolume, 0,
                "标的指数" + symbol + "[单笔最小挂单张数]与库中swap_front_config配置不一致");
        Assert.assertEquals(priceTick.subtract(configPriceTick).compareTo(BigDecimal.ZERO), 0,
                "标的指数" + symbol + "[合约价格最小变动精度]与库中swap_front_config配置不一致");
        Assert.assertEquals(status, configStatus,
                "标的指数" + symbol + "[状态]与库中swap_front_config配置不一致");

        logger.info("***************************PASS***************************");
    }

}
