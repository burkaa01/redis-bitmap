package com.github.burkaa01.distinct.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisShardInfo;

@Configuration
public class RedisConfig {

    @Value("${redis.host}")
    private String host;
    @Value("${redis.port}")
    private Integer port;
    @Value("${redis.password}")
    private String password;

    @Bean
    public JedisShardInfo redisConnectionInfo() {
        JedisShardInfo info = new JedisShardInfo(host, port, false);
        info.setPassword(password);
        return info;
    }
}
