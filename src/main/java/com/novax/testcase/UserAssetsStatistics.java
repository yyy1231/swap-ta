package com.novax.testcase;

import com.alibaba.fastjson.JSONObject;
import com.novax.entity.mysql.SpotUserAssetsEntity;
import com.novax.entity.mysql.SwapFrontUserAssetsEntity;
import com.novax.entity.mysql.SwapFrontUserConfigEntity;
import com.novax.mapper.SpotUserAssetsEntityMapper;
import com.novax.mapper.SwapFrontUserAssetsEntityMapper;
import com.novax.util.HttpClientUtil;
import com.novax.util.MyBatisUtil;
import com.novax.util.NewestPriceUtil;
import com.novax.util.PropertyUtil;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class UserAssetsStatistics {
    static Logger logger = Logger.getLogger(UserAssetsStatistics.class);

    // 用户资产总余额(USDT)，调基础平台接口
    public static BigDecimal totalAssetsBalance(Long userId) throws Exception {
        String walletUrl = PropertyUtil.getWalletUrl();
        String url = "http://" + walletUrl + "/user-asset/total";
        HashMap<String, String> headers = new HashMap<>();
        headers.put("language", "en");
        headers.put("userId", String.valueOf(userId));
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        JSONObject resp = httpClientUtil.sendGet(url, null, headers);
        Assert.assertEquals(resp.getString("msg"), "Operation Successful");
        BigDecimal totalUsd = resp.getJSONObject("data").getBigDecimal("totalUsd");
        logger.info("用户：" + userId + "，资产总余额(USDT)：" + totalUsd);
        return totalUsd;
    }

    // 用户现货账户资产折合(USDT)，调基础平台接口
    public static BigDecimal spotAssetsToUSDT(Long userId) throws Exception {
        String spotUrl = PropertyUtil.getSpotUrl();
        String url = "http://" + spotUrl + "/spot/assets";
        HashMap<String, String> headers = new HashMap<>();
        headers.put("language", "en");
        headers.put("userId", String.valueOf(userId));
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        JSONObject resp = httpClientUtil.sendGet(url, null, headers);
        Assert.assertEquals(resp.getString("msg"), "success");
        BigDecimal valuationCurrency = resp.getJSONObject("data").getBigDecimal("valuationCurrency");
        logger.info("用户：" + userId + "，现货账户资产折合(USDT)：" + valuationCurrency);
        return valuationCurrency;
    }

    // 现货账户可使用余额 -单币种
    public static BigDecimal spotAccountBalanceOfCurrency(Long userId, String currency) {
        SpotUserAssetsEntity spotUserAssetsEntity = MyBatisUtil.execute(SpotUserAssetsEntityMapper.class, m -> m.selectByUserId(userId, currency));
        BigDecimal available = BigDecimal.ZERO;
        if (spotUserAssetsEntity != null) {
            available = spotUserAssetsEntity.getAvailable().subtract(spotUserAssetsEntity.getFrozen());
        }
        logger.info("用户：" + userId + "，现货账户" + currency + "可使用余额：" + available);
        return available;
    }

    // 查用户全部合约账户
    public static List<SwapFrontUserAssetsEntity> swapAccountAll(Long userId) {
        return MyBatisUtil.execute(SwapFrontUserAssetsEntityMapper.class, m -> m.selectAll(userId));
    }

    // 合约账户
    public static SwapFrontUserAssetsEntity swapAccount(Long userId, String currency, String tradeSymbol) {
        SwapFrontUserConfigEntity configEntity = SwapConfigInfo.queryMode(userId);
        Integer mode = configEntity.getMode();
        if (mode == 1 || mode == 2) {
            return MyBatisUtil.execute(SwapFrontUserAssetsEntityMapper.class, m -> m.selectByMode(userId, currency, null));
        } else {
            return MyBatisUtil.execute(SwapFrontUserAssetsEntityMapper.class, m -> m.selectByMode(userId, currency, tradeSymbol));
        }
    }

    // 合约账户所有资产折U
    public static BigDecimal swapAssetsToU(Long userId) {
        BigDecimal accountBalanceSum = BigDecimal.ZERO;
        // 查用户全部合约账户
        List<SwapFrontUserAssetsEntity> swapFrontUserAssetsEntityList = swapAccountAll(userId);
        for (SwapFrontUserAssetsEntity userAssetsEntity : swapFrontUserAssetsEntityList) {
            String currency = userAssetsEntity.getCurrency();
            String symbol = userAssetsEntity.getSymbol();
            BigDecimal available = userAssetsEntity.getAvailable();
            if (currency.equals("usdt")) {
                accountBalanceSum = accountBalanceSum.add(available);
            } else {
                BigDecimal newestPrice = NewestPriceUtil.newPriceOfSymbol(symbol);
                logger.info(symbol + "的最新价：" + newestPrice);
                accountBalanceSum = accountBalanceSum.add(available.multiply(newestPrice));
            }
        }
        logger.info("用户" + userId + "全部合约账户折U：" + accountBalanceSum);
        return accountBalanceSum;
    }

    /**
     * 合约账户钱包余额
     * 全仓&逐仓 currency=usdt symbol=usdt
     * 分仓 currency=usdt symbol=交易对
     */
    public static BigDecimal swapAccountAvailable(Long userId, String currency, String symbol) {
        SwapFrontUserAssetsEntity swapFrontUserAssetsEntity = swapAccount(userId, currency, symbol);
        Assert.assertNotNull(swapFrontUserAssetsEntity, "用户" + userId + "无合约账户");
        BigDecimal available = swapFrontUserAssetsEntity.getAvailable();
        logger.info("用户" + userId + "，合约账户(全仓/逐仓)钱包余额：" + available);
        return available;
    }

    // 合约账户可使用余额 -单币种 available-frozen
    public static BigDecimal swapAccountBalanceOfCurrency(Long userId, String currency) {
        SwapFrontUserAssetsEntity swapUserAssetsEntity = MyBatisUtil.execute(SwapFrontUserAssetsEntityMapper.class, m -> m.selectByUserId(userId, currency));
        BigDecimal available = BigDecimal.ZERO;
        if (swapUserAssetsEntity != null) {
            available = swapUserAssetsEntity.getAvailable().subtract(swapUserAssetsEntity.getFrozen());
        }
        logger.info("用户：" + userId + "，合约账户" + currency + "可使用余额：" + available);
        return available;
    }

    public static void main(String[] args) {
        System.out.println(swapAssetsToU(28649983L));
    }
}
