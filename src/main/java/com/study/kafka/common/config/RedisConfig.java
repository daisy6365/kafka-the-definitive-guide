package com.study.kafka.common.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * [멱등성 체크] : Redis SETNX
 * - Consumer 소비량이 많을 때 사용
 * - 빠르고 안정적임
 * @@ DB만으로 충분히 체크가능 하긴 함!!
 */
@Configuration
@EnableCaching // 나중에 캐시기능
public class RedisConfig {
    /**
     * Redis 서버 연결
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        //  Spring boot 기본 Redis 클라이언트
        return new LettuceConnectionFactory();
    }

    // 문자열 기반
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        return new StringRedisTemplate(redisConnectionFactory);
    }

    // Object 기반 -> 필요시에만 사용
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }
}