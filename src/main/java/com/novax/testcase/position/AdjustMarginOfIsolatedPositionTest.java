package com.novax.testcase.position;

import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.SwapFrontPositionEntity;
import com.novax.mapper.SwapFrontPositionEntityMapper;
import com.novax.testcase.PublicParams;
import com.novax.testcase.TraderTransactionStatistics;
import com.novax.util.*;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * 追加、减少保证金  --逐仓
 * POST/swap/position/margin
 */
public class AdjustMarginOfIsolatedPositionTest extends PublicParams {
    Logger logger = Logger.getLogger(AdjustMarginOfIsolatedPositionTest.class);
    HttpClientUtil httpClientUtil;
    String url;

    @BeforeClass(groups = {"smokeTest"})
    public void setUp() {
        httpClientUtil = new HttpClientUtil();
    }

    @DataProvider(name = "datas")
    public Object[][] getProvider() {
        return ExcelUtil.readObjDatas("/testcase/position/adjustMarginOfIsolatedPosition.xls", 0);
    }

    @Test(dataProvider = "datas", groups = {"smokeTest"}, description = "调整逐仓保证金")
    public void adjustMargin_Success_Test(String tcNum, String tcName, String tcDescp, String reqAdr, String headers, String params, String expect) throws Exception {
        reqHeaders.put("Content-type", "application/x-www-form-urlencoded");
        // Long userId = 28649983L;
        Long userId = tradersOfIsolated.get(0);

        // 查询持仓
        List<SwapFrontPositionEntity> swapFrontPositionEntities = TraderTransactionStatistics.queryPositions2(userId, null, null);
        Assert.assertTrue(swapFrontPositionEntities.size() != 0, "用户" + userId + "无持仓");
        SwapFrontPositionEntity position = swapFrontPositionEntities.get(0);
        Long positionId = position.getId();
        BigDecimal margin = position.getMargin();
        BigDecimal addMargin = position.getAddMargin();

        reqHeaders.put("userId", String.valueOf(userId));
        HashMap<String, String> bodys = StringToMap.stringToMap(params);
        bodys.put("id", String.valueOf(positionId));
        BigDecimal amount = new BigDecimal(bodys.get("amount"));

        url = baseUrl + UrlProperty.getMargin();
        JSONObject resp = httpClientUtil.sendPostByForm(url, bodys, reqHeaders);

        logger.info(tcNum + "-requestUrl:" + url);
        logger.info(tcNum + "-headers:" + reqHeaders);
        logger.info(tcNum + "-入参:" + bodys);
        logger.info(tcNum + "-响应数据:" + resp);
        logger.info(tcNum + "-预期结果:" + expect);

        Assert.assertEquals(resp.get("code"), JSONObject.parseObject(expect).get("code"), resp.getString("msg"));
        Assert.assertEquals(resp.get("msg"), JSONObject.parseObject(expect).get("msg"));
        Thread.sleep(500);

        //断言
        SwapFrontPositionEntity positionNow = MyBatisUtil.execute(SwapFrontPositionEntityMapper.class, m -> m.selectByPrimaryKey(positionId));
        BigDecimal marginNow = positionNow.getMargin();
        BigDecimal addMarginNow = positionNow.getAddMargin();
        Assert.assertTrue(marginNow.subtract(margin).abs().subtract(amount).compareTo(BigDecimal.ZERO) == 0, "仓位" + positionId + "调整保证金异常");
        Assert.assertTrue(addMarginNow.subtract(addMargin).abs().subtract(amount).compareTo(BigDecimal.ZERO) == 0, "仓位" + positionId + "调整保证金异常");

        logger.info("***************************PASS***************************");
    }

}
