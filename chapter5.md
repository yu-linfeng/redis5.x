# Java客户端（上）

> 有关本章的源码：[https://github.com/yu-linfeng/redis5.x_tutorial/tree/master/code/jedis](https://github.com/yu-linfeng/redis5.x_tutorial/tree/master/code/jedis)

前面的章节介绍了redis的安装、还有命令配置等内容，我们在实际使用时大部分情况都是利用现成的Java客户端对redis进行操作。当然命令并不是没用，它极有可能在你排查问题时排上用场，因为你有可能会直接连入redis服务端通过命令行来排查是否是redis缓存的问题。

redis的Java客户端最常用的是**jedis**开源库，本章我们也将围绕jedis的对redis进行一些简单的操作，jedis的GitHub地址：[https://github.com/xetorthio/jedis](https://github.com/xetorthio/jedis)。

```java
package com.coderbuff.jedis.simple;

import redis.clients.jedis.Jedis;

/**
 * @author okevin
 * @date 2020/2/12 23:08
 */
public class Demo {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost");
        jedis.set("redis-client", "jedis");
        System.out.println(jedis.get("redis-client"));
        jedis.close();
    }
}

```

这是一个简单的jedis连接示例，使用MySQL的经验告诉我们：类似有socket连接的，我们最好是通过“池化”技术，一是更好的管理我们的连接；二是能更好的利用连接资源。所以当我们在使用jedis时，最好是使用jedis提供的池化技术。

```java
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
```

```java
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
```

当然我不准备在这里把所有的命令都展示出来，关于SDK的使用大可查看官方文档。

redis中有一个重要的功能——**pipeline（管道）**，我们在操作大量数据时，redis的吞吐量性能可能较低，此时我们可以通过pipeline进行批量操作。这个功能在redis的命令中并没有，但redis是支持的。所以本章将重点介绍pipeline的使用，这在实际应用中非常常用。



## pipeline（管道）

pipeline提供了命令的批量提交，当我们有批量查询或者写入操作时，单个命令的“往返时间”是1ms，那么10个命令就会消耗10ms，如果我们使用pipeline批量操作后可以一次性提交10个命令，redis的响应时间将会大大减小。吞吐量也自然提高。

实际上，之所以采用pipeline批量提交主要是为了控制**网络开销**，10个命令就会有10次网络开销，网络开销对于处于异地机房的影响尤为明显。所以在进行批量操作时，尽量使用pipeline管道操作。下面的例子是1万次字符串类型的写入，反映了非pipeline和pipeline的性能对比。

```java
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
        for (int i = 0; i < 10000; i++) {
            pipeline.set("key_" + i, String.valueOf(i));
        }
        pipeline.sync();
        long pipelineEnd = System.currentTimeMillis();
        System.out.println("pipeline操作10000次字符串数据类型set写入，耗时：" + (pipelineEnd - pipelineStart) + "毫秒");
    }
}
```

控制台输出结果：

```
非pipeline操作10000次字符串数据类型set写入，耗时：1843毫秒
pipeline操作10000次字符串数据类型set写入，耗时：205毫秒
```

可以看到，通过pipeline管道批量操作，吞吐量性能大大提到。

**重要**

使用pipeline有几个值得注意的地方：

1. 它相比较于redis原生的字符串数据类型的批量操作命令，pipeline是**非原子性**的，```mset```是**原子性**的。```mset```批量操作要么都成功要么都失败，而pipeline则不能保证。
2. ```mset```只是```set```**一个命令**的批量操作，而pipeline则可以批量发送**多个命令**，这里就存在事务的问题。

针对问题一，我们在进行批量操作时应尽可能的把批量操作拆分成小粒度的pipeline。

针对问题二，要保证多个命令的事务，就需要使用redis提供的事务相关的命令，但redis中的事务是“假事务”，因为它仍然不能保证原子性。在下一章中，会详细介绍redis原生的简单事务（不保证原子性），以及如何在redis中保证事务的原子性。



