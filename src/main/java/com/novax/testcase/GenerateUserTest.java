package com.novax.testcase;

import com.novax.entity.mysql.UserEntity;
import com.novax.mapper.UserEntityMapper;
import com.novax.util.MyBatisUtil;
import com.novax.util.RandomChar;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.Random;

public class GenerateUserTest extends PublicParams {
    static Logger logger = Logger.getLogger(GenerateUserTest.class);

    @Parameters({"trader-num"})
    @Test(groups = {"traderReady"}, description = "创建交易员-保证金模式-全仓")
    public void traderReady(int n) {
        for (int i = 0; i < n; i++) {
            Long trader = GenerateUserTest.createTraders();
            traders.add(trader);
        }
        logger.info("traders:" + traders);
    }

    @Parameters({"traderOfCross-num"})
    @Test(groups = {"traderReady"}, description = "创建交易员-保证金模式-全仓")
    public void traderOfCrossReady(int n) {
        for (int i = 0; i < n; i++) {
            Long trader = GenerateUserTest.createTraders();
            tradersOfCross.add(trader);
        }
        logger.info("tradersOfCross:" + tradersOfCross);
    }

    @Parameters({"traderOfIsolated-num"})
    @Test(groups = {"traderReady"}, description = "创建交易员-保证金模式-逐仓")
    public void traderOfIsolatedReady(int n) {
        for (int i = 0; i < n; i++) {
            Long trader = GenerateUserTest.createTraders();
            OpenSwapAccountAndAnswer.updateMode(trader, 2);
            tradersOfIsolated.add(trader);
        }
        logger.info("tradersOfIsolated:" + tradersOfIsolated);
    }

    @Parameters({"traderOfSplit-num"})
    @Test(groups = {"traderReady"}, description = "创建交易员-保证金模式-分仓")
    public void traderOfSplitReady(int n) {
        for (int i = 0; i < n; i++) {
            Long trader = GenerateUserTest.createTraders();
            OpenSwapAccountAndAnswer.updateMode(trader, 3);
            tradersOfSplit.add(trader);
            // 创建分仓账户 --暂只创建btc_usdt,后续订单也要使用该币对，注意！！！
            AccountManagement.createSwapAccount(trader, "usdt", "btc_usdt");
        }
        logger.info("tradersOfSplit:" + tradersOfSplit);
    }

    public static Long createTraders() {
        Random random = new Random();
        String email = "elephant" + random.nextInt(100000) + RandomChar.getRandomChar(3) + "@163.com";
        createUser(email);
        Long userId = UserInfo.queryByEmail(email).getId();
        traderReady(userId);
        logger.info("***********************create trader success***********************");
        return userId;
    }

    public static void createUser(String email) {
        logger.info("测试sql执行时间，start:" + new Date());
        MyBatisUtil.executeVoid(UserEntityMapper.class, mapper -> {
            UserEntity userEntity = UserEntity.builder()
                    .tenantId(1L)
                    .areaCode("")
                    .phone(null)
                    .email(email)
                    .password("f2cf8b8a01fe9cb74208702dc87b1235")
                    .salt("60cde9c442f12e5ad6312dfc38bb394e")
                    .nickname("")
                    .headUrl("/static/images/userhead/u004.jpg")
                    .trueName(null)
                    .nationality("")
                    .idCardType(1)
                    .idCard("")
                    .authLevel(0)
                    .isAuth(0)
                    .assistantCurrency("usd")
                    .language("en")
                    .feeFlag(1)
                    .inviteCode(null)
                    .inviterId(20222039L)
                    .status(1)
                    .level(0)
                    .levelUpdateTime(new Date())
                    .spotDiscount(0)
                    .swapDiscount(1)
                    .swapCurrencyDiscount(0)
                    .googleKey(null)
                    .lastModified(null)
                    .isMerchant(0)
                    .isSwapOpened(1)
                    .isAnswerPassed(1)
                    .swapStatus(1)
                    .isLimitSpot(0)
                    .isLimitWithdrawal(0)
                    .limitMarket(null)
                    .isDeleted(0)
                    .isStatistics(1)
                    .isWhitelist(0)
                    .type(0)
                    .registerClient(1)
                    .registerIp("0.0.0.0")
                    .region(null)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            int insert = mapper.insert(userEntity);
            logger.info("测试sql执行时间，END：" + new Date());
            Assert.assertEquals(insert, 1, "user create fail");
        });
    }


    public static void traderReady(Long userId) {
        logger.info("***************TRADER READY BEGIN***************");
        // 1、开通合约、答题
        boolean isSwapOpened = OpenSwapAccountAndAnswer.isSwapOpened(userId);
        if (!isSwapOpened) {
            OpenSwapAccountAndAnswer.openSwap(userId);
        }
        // 2、配置仓位类型
        boolean modeConfigured = OpenSwapAccountAndAnswer.isModeConfigured(userId);
        if (!modeConfigured) {
            OpenSwapAccountAndAnswer.configureMode(userId);
        }
        // 3、有合约账户-USDT否，无则创建，初始化资产10000
        boolean haveSwapAccount = AccountManagement.isHaveSwapAccount(userId, "usdt");
        if (!haveSwapAccount) {
            AccountManagement.createSwapAccount(userId, "usdt");
        }
        // 4、用户合约杠杆倍数 默认是150 修改为10
        SwapConfigInfo.initialUserLeverage(userId, "btc_usdt");
        SwapConfigInfo.initialUserLeverage(userId, "eth_usdt");
        logger.info("***************TRADER READY END***************");
    }

    public static void createTraders(int n) {
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            String email = "elephant" + random.nextInt(100000) + RandomChar.getRandomChar(3) + "@163.com";
            createUser(email);
            Long userId = UserInfo.queryByEmail(email).getId();
            traderReady(userId);
            traders.add(userId);
        }
        logger.info("traders info:" + traders);
        logger.info("***********************create trader success***********************");
    }

    public static void main(String[] args) {
        // createTraders(1);
        createUser("multiCurrencySwap4@163.com");
    }
}
