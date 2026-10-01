package com.novax.util;

import com.alibaba.fastjson.JSONObject;
import lombok.val;
import org.apache.log4j.Logger;
import redis.clients.jedis.JedisCluster;

import java.math.BigDecimal;

public class NewestPriceUtil {
    static Logger logger = Logger.getLogger(NewestPriceUtil.class);

    // 现货最新价
    public static BigDecimal newPriceOfSymbol(String symbol) {
        var newPrice = BigDecimal.ZERO;
        val redisValue = RedisUtil.getJedis().hget("spot:new:price", symbol);
        System.out.println("redisValue" + redisValue);
        val parsedObj = JSONObject.parseObject(redisValue);
        if (null != parsedObj && null != parsedObj.getBigDecimal("price")) {
            newPrice = parsedObj.getBigDecimal("price");
        }
        logger.info("币对" + symbol + "的现货最新价：" + newPrice);
        return newPrice;
    }

    // 合约最新价
    public static BigDecimal newPriceOfSwap(String symbol) {
        var newPrice = BigDecimal.ZERO;
        val redisValue = RedisUtil.getJedis().hget("swap:new:price", symbol);
        System.out.println("redisValue" + redisValue);
        val parsedObj = JSONObject.parseObject(redisValue);
        if (null != parsedObj && null != parsedObj.getBigDecimal("price")) {
            newPrice = parsedObj.getBigDecimal("price");
        }
        logger.info("币对" + symbol + "的合约最新价：" + newPrice);
        return newPrice;
    }

    // 写入价格
    public static Long writeSwapPrice(String key, String symbol, Double value) {
        JedisCluster jedis = RedisUtil.getJedis();
        // swap:price
        return jedis.zadd(key + symbol, System.currentTimeMillis(), String.valueOf(value));

    }


    public static void main(String[] args) {
        System.out.println(writeSwapPrice("swap:price:","storj_usdt",0.2800));
    }
}
