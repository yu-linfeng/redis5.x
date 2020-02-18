package com.coderbuff.springdataredis.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * redis配置
 * @author okevin
 * @date 2020/2/18 14:20
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * 使用redis作为spring的缓存管理工具
     * 注意：springboot2.x与springboot1.x此处的区别较大
     * 在springboot1.x中，要使用redis的缓存管理工具为以下代码：
     *
     * public CacheManager cacheManager(RedisTemplate redisTemplate) {
     *     RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate);
     *     return redisCacheManager;
     * }
     *
     * @param redisConnectionFactory redis连接工厂
     * @return redis缓存管理
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheManager redisCacheManager = RedisCacheManager.create(redisConnectionFactory);
        return redisCacheManager;
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);     //配置连接工厂

        /*使用Jackson序列化和反序列化key、value值，默认使用JDK的序列化方式
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);  //指定要序列化的域，ALL表示所有字段、以及set/get方法，ANY是都有包括修饰符private和public
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);   //指定序列化输入的类型，NON_FINAL表示必须是非final修饰的类型
        jacksonSeial.setObjectMapper(om);

        //以下数据类型通过jackson序列化
        redisTemplate.setValueSerializer(jacksonSeial);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jacksonSeial);
        */
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
