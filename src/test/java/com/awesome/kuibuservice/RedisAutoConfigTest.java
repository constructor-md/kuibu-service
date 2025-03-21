package com.awesome.kuibuservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisAutoConfigTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private LettuceConnectionFactory connectionFactory;

    @Test
    void printConfig() {
        System.out.println("Host: " + connectionFactory.getHostName());
        System.out.println("Port: " + connectionFactory.getPort());
        System.out.println("Database: " + connectionFactory.getDatabase());
    }

    @Test
    void testConnection() {
        redisTemplate.opsForValue().set("test_key", "hello");
        Assertions.assertEquals("hello", redisTemplate.opsForValue().get("test_key"));
    }
}