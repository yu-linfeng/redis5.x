package com.coderbff.jedis.test;

import com.coderbuff.jedis.util.RedisUtil;
import com.codrbuff.jedis.client.RedisClient;
import org.junit.Test;
import redis.clients.jedis.Pipeline;

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
}
