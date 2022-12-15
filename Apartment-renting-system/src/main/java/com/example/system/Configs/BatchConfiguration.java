package com.example.system.Configs;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource(value = { "classpath:application.properties" })
public class BatchConfiguration implements EnvironmentAware {

    private Environment environment;


    public String getProperty(String key) {
        return environment.getProperty(key);
    }

    @Override
    public void setEnvironment(Environment env) {
        this.environment = env;
    }
}