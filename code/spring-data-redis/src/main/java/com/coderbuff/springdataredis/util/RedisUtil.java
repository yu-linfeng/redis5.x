package com.coderbuff.springdataredis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * redis操作工具类
 * @author okevin
 * @date 2020/2/18 14:34
 */
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 字符串类型写入操作
     * @param key key值
     * @param value value值
     */
    public void set(String key, String value) {
        this.redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 可设置过期时间字符串类型写入操作
     * @param key key值
     * @param value value值
     * @param expire 过期时间
     * @param timeUnit 过期时间单位
     */
    public void set(String key, String value, Long expire, TimeUnit timeUnit) {
        this.redisTemplate.opsForValue().set(key, value, expire, timeUnit);
    }

    /**
     * 字符串类型读取操作
     * @param key key值
     * @return value值
     */
    public String get(String key) {
        return (String) this.redisTemplate.opsForValue().get(key);
    }
}
