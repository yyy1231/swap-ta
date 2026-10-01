package com.novax.testcase.assets;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.novax.pojo.TransferableAsset;
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
 * 可划转资产列表查询
 * GET/swap/asset/all
 */
public class QueryTransferableAssetListTest extends PublicParams {
    Logger logger = Logger.getLogger(QueryTransferableAssetListTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/assets/queryTransferableAssetList.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "可划转资产列表查询")
    public void queryTransferableAssetList_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        url = baseUrl + UrlProperty.getAssetList();
        JSONObject resp = httpClientUtil.sendGet(url, new HashMap<>(), reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + params);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));

        JSONArray data = resp.getJSONArray("data");
        Assert.assertTrue(data.size() != 0, "平台可划转资产列表查询异常");

        // 预期
        List<TransferableAsset> transferableAssetList = SwapConfigInfo.queryAssetList();

        // 断言
        Assert.assertEquals(data.size(), transferableAssetList.size(), "平台可划转资产列表统计不一致，测试：" + data.size() + "，预期：" + transferableAssetList.size());

        logger.info("***************************PASS***************************");
    }

}
