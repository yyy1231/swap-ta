package com.novax.util;

import lombok.val;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

public class MarkPriceUtil {
    static Logger logger = Logger.getLogger(MarkPriceUtil.class);

    public static BigDecimal markPriceOfSymbol(String symbol) {
        var markPrice = BigDecimal.ZERO;
        val redisValue = RedisUtil.getJedis().hget("swap:mark:price", symbol);
        if (null != redisValue) {
            markPrice = new BigDecimal(redisValue);
        }
        logger.info("币对" + symbol + "的标记价：" + markPrice);
        return markPrice;
    }

    public static void main(String[] args) {
        System.out.println(markPriceOfSymbol("btc_usdt"));
    }
}
