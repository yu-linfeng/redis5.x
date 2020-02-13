package com.coderbuff.jedis.util;

import com.codrbuff.jedis.client.RedisClient;
import redis.clients.jedis.Jedis;

/**
 * redis工具类
 * @author okevin
 * @date 2020/2/12 23:13
 */
public class RedisUtil {

    //###########字符串（string）数据类型相关操作############

    /**
     * 字符串写入
     * @param key key
     * @param value 值
     * @return 写入的值
     */
    public static String set(String key, String value) {
        try (Jedis jedis = RedisClient.getJedis()){
            jedis.set(key, value);
            return value;
        }
    }

    public static String get(String key) {
        try (Jedis jedis = RedisClient.getJedis()) {
            return jedis.get(key);
        }
    }

}
