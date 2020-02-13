package com.codrbuff.jedis.client;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis客户端连接
 * @author okevin
 * @date 2020/2/12 23:17
 */
public class RedisClient {

    /**
     * redis服务器地址
     */
    private static final String HOST = "localhost";

    /**
     * jedis连接池
     */
    private static JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), HOST);

    private RedisClient() {

    }

    /**
     * 从jedispool中获取一个jedis连接
     * @return jedis连接
     */
    public static Jedis getJedis() {
        return jedisPool.getResource();
    }
}
