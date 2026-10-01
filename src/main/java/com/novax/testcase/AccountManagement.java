package com.novax.testcase;

import com.novax.entity.mysql.SpotUserAssetsEntity;
import com.novax.entity.mysql.SwapFrontUserAssetsEntity;
import com.novax.mapper.SpotUserAssetsEntityMapper;
import com.novax.mapper.SwapFrontUserAssetsEntityMapper;
import com.novax.util.MyBatisUtil;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 账户管理 创建账户 充值
 */
public class AccountManagement {
    static Logger logger = Logger.getLogger(AccountManagement.class);

    // 是否有现货账户-USDT
    public static boolean isHaveSpotAccount(Long userId, String currency) {
        SpotUserAssetsEntity entity = MyBatisUtil.execute(SpotUserAssetsEntityMapper.class, m -> m.selectByUserId(userId, currency));
        if (entity == null) {
            logger.info("用户" + userId + "现货账户无USDT资产");
            return false;
        }
        return true;
    }

    // 是否有合约账户-USDT
    public static boolean isHaveSwapAccount(Long userId, String currency) {
        SwapFrontUserAssetsEntity entity = MyBatisUtil.execute(SwapFrontUserAssetsEntityMapper.class, m -> m.selectByUserId(userId, currency));
        if (entity == null) {
            logger.info("用户" + userId + "合约账户无USDT资产");
            return false;
        }
        return true;
    }

    // 创建现货账户
    public static void createSpotAccount(Long userId, String currency) {
        MyBatisUtil.executeVoid(SpotUserAssetsEntityMapper.class, mapper -> {
            SpotUserAssetsEntity existing = mapper.selectByUserId(userId, currency);
            if (existing == null) {
                SpotUserAssetsEntity assetsEntity = SpotUserAssetsEntity.builder()
                        .tenantId(1L)
                        .userId(userId)
                        .currency(currency)
                        .available(BigDecimal.valueOf(10000))
                        .frozen(BigDecimal.ZERO)
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build();
                int insert = mapper.insert(assetsEntity);
                Assert.assertEquals(insert, 1, "用户：" + userId + "现货账户创建失败");
            }
        });
    }

    // 创建合约账户
    public static void createSwapAccount(Long userId, String currency) {
        MyBatisUtil.executeVoid(SwapFrontUserAssetsEntityMapper.class, mapper -> {
            SwapFrontUserAssetsEntity existing = mapper.selectByUserId(userId, currency);
            if (existing == null) {
                SwapFrontUserAssetsEntity assetsEntity = SwapFrontUserAssetsEntity.builder()
                        .tenantId(1L)
                        .userId(userId)
                        .symbol("usdt")
                        .currency(currency)
                        .available(BigDecimal.valueOf(10000))
                        .frozen(BigDecimal.ZERO)
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build();
                int insert = mapper.insert(assetsEntity);
                Assert.assertEquals(insert, 1, "用户：" + userId + "合约账户创建失败");
            }
        });
    }

    // 创建合约账户
    public static void createSwapAccount(Long userId, String currency, String symbol) {
        MyBatisUtil.executeVoid(SwapFrontUserAssetsEntityMapper.class, mapper -> {
            SwapFrontUserAssetsEntity assetsEntity = SwapFrontUserAssetsEntity.builder()
                    .tenantId(1L)
                    .userId(userId)
                    .symbol(symbol)
                    .currency(currency)
                    .available(BigDecimal.valueOf(1000))
                    .frozen(BigDecimal.ZERO)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            int insert = mapper.insert(assetsEntity);
            Assert.assertEquals(insert, 1, "用户：" + userId + "合约账户创建失败");
        });
    }

    // 现货账户充值
    public static void increaseSpotAccBalance(Long userId, String currency) {
        MyBatisUtil.executeVoid(SpotUserAssetsEntityMapper.class, mapper -> {
            SpotUserAssetsEntity spotUserAssetsEntity = mapper.selectByUserId(userId, currency);
            if (spotUserAssetsEntity == null) {
                createSpotAccount(userId, currency);
            } else {
                spotUserAssetsEntity.setAvailable(BigDecimal.valueOf(100000));
                spotUserAssetsEntity.setUpdateTime(new Date());
                int i = mapper.updateByPrimaryKey(spotUserAssetsEntity);
                Assert.assertEquals(i, 1, "用户：" + userId + "现货账户余额更新失败");
            }
        });
    }

    // 更新合约账户资产
    public static void updateSwapAccBalance(Long userId, String currency, BigDecimal amount) {
        MyBatisUtil.executeVoid(SwapFrontUserAssetsEntityMapper.class, mapper -> {
            SwapFrontUserAssetsEntity swapFrontUserAssets = mapper.selectByUserId(userId, currency);
            swapFrontUserAssets.setAvailable(amount);
            swapFrontUserAssets.setUpdateTime(new Date());
            int i = mapper.updateByPrimaryKey(swapFrontUserAssets);
            Assert.assertEquals(i, 1, "用户：" + userId + "合约账户余额更新失败");
        });
    }

    public static void main(String[] args) {
        // increaseSpotAccBalance(20251164L, "usdt");
        createSwapAccount(28670905L, "usdt", "btc_usdt");
    }
}
