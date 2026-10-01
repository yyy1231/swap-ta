package com.novax.util;


import redis.clients.jedis.JedisCluster;

public class ClearCacheUtil {

    public static void clearCache(String key) {
        RedisUtil.getJedis().del(key);
    }

    public static void deleteHashValue(String key, String field) {
        JedisCluster jedis = RedisUtil.getJedis();
        jedis.hdel(key, field);
    }

    public static void main(String[] args) {
        // deleteHashValue("swap:position:buy:btc_usdt","28649983");
        deleteHashValue("swap:position:sell:btc_usdt", "28649983");
    }

}
