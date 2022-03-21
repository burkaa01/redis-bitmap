package com.github.burkaa01.trigger.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {

    private static final String PROTOCOL = "http://";
    private static final String DELIMITER = ":";

    @Value("${distinct.host}")
    private String distinctHost;
    @Value("${distinct.port}")
    private Integer distinctPort;

    @Value("${done.host}")
    private String doneHost;
    @Value("${done.port}")
    private Integer donePort;

    @Bean(name = "distinctApiHost")
    public String distinctApiHost() {
        return PROTOCOL + distinctHost + DELIMITER + distinctPort;
    }

    @Bean(name = "doneApiHost")
    public String doneApiHost() {
        return PROTOCOL + doneHost + DELIMITER + donePort;
    }
}
