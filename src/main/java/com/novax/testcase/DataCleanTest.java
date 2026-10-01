package com.novax.testcase;

import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.SwapFrontOrderEntity;
import com.novax.entity.mysql.SwapFrontOrderPlanEntity;
import com.novax.entity.mysql.SwapFrontPositionEntity;
import com.novax.entity.mysql.UserEntity;
import com.novax.mapper.UserEntityMapper;
import com.novax.util.*;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 测试数据清理
 * step1、筛选测试用户
 * step2、撤销交易中订单 即限价单
 * step3、撤销计划单
 * step4、平掉所有仓位
 */
public class DataCleanTest {
    static Logger logger = Logger.getLogger(DataCleanTest.class);
    public static String baseUrl = "http://" + PropertyUtil.getServerUrl() + ":" + PropertyUtil.getServerPort();
    public static String headers = "{language=en,Content-type=application/json}";
    public static HashMap<String, String> reqHeaders = StringToMap.stringToMap(headers);

    @Test(groups = {"smokeTest"}, description = "撤销交易中订单")
    public static void cancelAllOrders_Test() throws Exception {
        logger.info("***************************cancelAllOrders begin***************************");
        // 是否周一 清理上周一到周五的数据
        if (DateUtil.isNotMonday()) {
            return;
        }
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        reqHeaders.put("Content-type", "application/x-www-form-urlencoded");
        HashMap<String, String> bodys = new HashMap<>();

        List<UserEntity> users = queryTestUsers("elephant", DateUtil.getZeroTime(8), DateUtil.getZeroTime(3));
        for (UserEntity user : users) {
            Long userId = user.getId();
            reqHeaders.put("userId", String.valueOf(userId));
            List<SwapFrontOrderEntity> swapFrontOrderEntityList = TraderTransactionStatistics.queryInTradingOrders(userId);
            // 有交易中订单否
            if (swapFrontOrderEntityList.size() == 0) {
                logger.info("用户" + userId + "无交易中状态的订单，continue处理下一个");
                continue;
            }

            String url = baseUrl + UrlProperty.getRepeaAlllOrders();
            JSONObject resp = httpClientUtil.sendPostByForm(url, bodys, reqHeaders);

            logger.info("requestUrl:" + url);
            logger.info("headers:" + reqHeaders);
            logger.info("入参：" + bodys);
            logger.info("响应数据：" + resp);

            Assert.assertEquals(resp.get("code"), 200, resp.getString("msg"));
            Assert.assertEquals(resp.get("msg"), "Order Cancelled");
            Thread.sleep(200);

            // 断言 订单状态更新为3已撤销
            for (SwapFrontOrderEntity entity : swapFrontOrderEntityList) {
                Long orderId = entity.getId();
                SwapFrontOrderEntity swapFrontOrderEntity = TraderTransactionStatistics.queryOrder(orderId);
                Assert.assertEquals(swapFrontOrderEntity.getStatus(), 3, "订单" + orderId + "撤销异常");
            }
        }

        logger.info("***************************cancelAllOrders end***************************");
    }

    @Test(groups = {"smokeTest"}, description = "撤销计划委托单")
    public static void cancelAllPlanOrders_Test() throws Exception {
        logger.info("***************************cancelAllPlanOrders begin***************************");
        // 清理上周一到周五的数据
        if (DateUtil.isNotMonday()) {
            return;
        }
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        reqHeaders.put("Content-type", "application/x-www-form-urlencoded");
        HashMap<String, String> bodys = new HashMap<>();

        List<UserEntity> users = queryTestUsers("elephant", DateUtil.getZeroTime(8), DateUtil.getZeroTime(3));
        for (UserEntity user : users) {
            Long userId = user.getId();
            reqHeaders.put("userId", String.valueOf(userId));

            List<SwapFrontOrderPlanEntity> swapFrontOrderPlanEntityList = TraderTransactionStatistics.queryPlanOrders(userId);
            // 有计划委托单否
            if (swapFrontOrderPlanEntityList.size() == 0) {
                logger.info("用户" + userId + "无计划委托单，continue处理下一个");
                continue;
            }

            String url = baseUrl + UrlProperty.getRepealAllPlanOrder();
            JSONObject resp = httpClientUtil.sendPostByForm(url, bodys, reqHeaders);

            logger.info("requestUrl:" + url);
            logger.info("headers:" + reqHeaders);
            logger.info("入参：" + bodys);
            logger.info("响应数据：" + resp);

            Assert.assertEquals(resp.get("code"), 200, resp.getString("msg"));
            Assert.assertEquals(resp.get("msg"), "Order Cancelled");
            Thread.sleep(200);

            // 断言 计划单状态更新为已撤销
            for (SwapFrontOrderPlanEntity entity : swapFrontOrderPlanEntityList) {
                Long orderId = entity.getId();
                SwapFrontOrderPlanEntity swapFrontOrderPlanEntity = TraderTransactionStatistics.queryPlanOrder(orderId);
                Assert.assertEquals(swapFrontOrderPlanEntity.getStatus(), 4, "计划单" + orderId + "撤销异常");
            }
        }

        logger.info("***************************cancelAllPlanOrders end***************************");
    }

    @Test(groups = {"smokeTest"}, description = "平仓")
    public static void closePositions_Test() throws Exception {
        logger.info("*****************************CLOSE POSITIONS BEGIN*****************************");
        // 是否周一 清理上周一到周五的数据
        if (DateUtil.isNotMonday()) {
            return;
        }
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        List<UserEntity> users = queryTestUsers("elephant", DateUtil.getZeroTime(8), DateUtil.getZeroTime(3));
        String params2 = "{\"orderPriceType\":\"3\",\"price\":\"\",\"symbol\":\"btc_usdt\",\"tradeType\":\"3\",\"volume\":2}";
        JSONObject bodys = JSONObject.parseObject(params2);

        // 清仓失败的用户
        List<Long> failusers = new ArrayList<>();
        for (UserEntity user : users) {
            Long userId = user.getId();
            reqHeaders.put("userId", String.valueOf(userId));

            // 用户持仓
            List<SwapFrontPositionEntity> positionEntities = TraderTransactionStatistics.queryPositions2(userId, null, null);
            // 有持仓否
            if (positionEntities.size() == 0) {
                logger.info("用户" + userId + "无持仓，continue处理下一个");
                continue;
            }

            for (SwapFrontPositionEntity swapFrontPositionEntity : positionEntities) {
                Integer direction = swapFrontPositionEntity.getDirection();
                Long positionId = swapFrontPositionEntity.getId();
                Long volume = swapFrontPositionEntity.getVolume();
                Long frozen = swapFrontPositionEntity.getFrozen();
                String symbol = swapFrontPositionEntity.getSymbol();

                long availableVolume = volume - frozen;
                if (availableVolume == 0) {
                    logger.info("仓位" + positionId + "所有持仓已冻结");
                    continue;
                }

                int tradeType = direction == 1 ? 3 : 4;
                bodys.put("symbol", symbol);
                bodys.put("volume", availableVolume);
                bodys.put("tradeType", tradeType);

                String url = baseUrl + UrlProperty.getOrder();
                JSONObject resp = httpClientUtil.sendPostByJson(url, bodys, reqHeaders);

                logger.info("requestUrl:" + url);
                logger.info("headers:" + reqHeaders);
                logger.info("入参：" + bodys);
                logger.info("响应数据：" + resp);

                // 不用断言 会中断程序
                // Assert.assertEquals(resp.get("code"), 200, resp.getString("msg"));
                // Assert.assertEquals(resp.get("msg"), "Order Successful");
                Thread.sleep(500);
            }
            // 平仓异常的用户 保存到failusers 手动处理
            List<SwapFrontPositionEntity> positions = TraderTransactionStatistics.queryPositions2(userId, null, null);
            // Assert.assertEquals(positions.size(), 0, "用户" + userId + "仓位未全平");
            if (positions.size() != 0) {
                failusers.add(userId);
            }
        }
        logger.info("平仓异常的用户：" + failusers);
        logger.info("*****************************CLOSE POSITIONS END*****************************");
    }

    // @Test(groups = {"smokeTest"}, description = "清redis仓位信息", enabled = false)
    public static void cleanRedisPositions() throws ParseException, InterruptedException {
        logger.info("*****************************Clean Redis POSITIONS BEGIN*****************************");
        // 是否周一 清理上周一到周五的数据
        if (!DateUtil.isNotMonday()) {
            return;
        }
        List<UserEntity> users = queryTestUsers("elephant", DateUtil.getZeroTime(31), DateUtil.getZeroTime(4));
        for (UserEntity user : users) {
            ClearCacheUtil.deleteHashValue("swap:position:buy:btc_usdt", String.valueOf(user.getId()));
            ClearCacheUtil.deleteHashValue("swap:position:sell:btc_usdt", String.valueOf(user.getId()));
            ClearCacheUtil.deleteHashValue("swap:position:buy:eth_usdt", String.valueOf(user.getId()));
            ClearCacheUtil.deleteHashValue("swap:position:sell:eth_usdt", String.valueOf(user.getId()));
            ClearCacheUtil.deleteHashValue("swap:position:buy:storj_usdt", String.valueOf(user.getId()));
            ClearCacheUtil.deleteHashValue("swap:position:sell:storj_usdt", String.valueOf(user.getId()));
            Thread.sleep(200);
        }

        logger.info("*****************************Clean Redis POSITIONS BEGIN*****************************");
    }

    // 筛选测试用户 创建时间从from到to时间段的测试用户
    public static List<UserEntity> queryTestUsers(String str, Date from, Date to) {
        String email = str + "%" + "163.com";
        List<UserEntity> users = MyBatisUtil.execute(UserEntityMapper.class, m -> m.selectTestUsers(email, from, to));
        logger.info("测试用户：" + users);
        return users;
    }

    public static void main(String[] args) throws ParseException, InterruptedException {
        cleanRedisPositions();
        // System.out.println(queryTestUsers("elephant", DateUtil.getZeroTime(8), DateUtil.getZeroTime(3)).size());
    }
}
