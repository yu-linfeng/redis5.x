package com.coderbff.jedis.test;

import com.coderbuff.jedis.util.RedisUtil;
import com.codrbuff.jedis.client.RedisClient;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author okevin
 * @date 2020/2/12 23:32
 */
public class JedisTests {

    @Test
    public void testPipeline() {
        long setStart = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            RedisUtil.set("key_" + i, String.valueOf(i));
        }
        long setEnd = System.currentTimeMillis();
        System.out.println("非pipeline操作10000次字符串数据类型set写入，耗时：" + (setEnd - setStart) + "毫秒");

        long pipelineStart = System.currentTimeMillis();
        Pipeline pipeline = RedisClient.getJedis().pipelined();
        pipeline.multi();
        for (int i = 0; i < 10000; i++) {
            pipeline.set("key_" + i, String.valueOf(i));
        }
        pipeline.exec();
        pipeline.sync();
        long pipelineEnd = System.currentTimeMillis();
        System.out.println("pipeline操作10000次字符串数据类型set写入，耗时：" + (pipelineEnd - pipelineStart) + "毫秒");
    }

    @Test
    public void testTransaction() {
        Jedis jedis = RedisClient.getJedis();
        jedis.watch("a", "c");
        Transaction transaction = jedis.multi();
        transaction.set("a", "b");
        transaction.set("b", "c");
        transaction.exec();
        jedis.close();
    }

    @Test
    public void testLua() {
        Jedis jedis = RedisClient.getJedis();
        List<String> keys = new ArrayList<>();
        keys.add("name");
        keys.add("age");
        List<String> values = new ArrayList<>();
        values.add("kevin");
        values.add("25");
        jedis.eval("redis.call('set', KEYS[1], ARGV[1]) redis.call('set', KEYS[2], ARGV[2])", keys, values);
        jedis.close();
    }
}
