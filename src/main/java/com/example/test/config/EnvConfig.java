package com.example.test.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class EnvConfig {

    private final Environment environment;

    public String getProperty(String key) {
        return environment.getProperty(key);
    }
}
