package com.lovelycatv.ai.crystal.dispatcher.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lovelycat
 * @version 1.0
 * @since 2025-02-15 18:52
 */
@Configuration
@ConfigurationProperties(prefix = "crystal.node")
public class RegisteredNodeConfiguration {
    private String checkAliveCron = "0/5 * * * * ?";

    public String getCheckAliveCron() {
        return checkAliveCron;
    }

    public void setCheckAliveCron(String checkAliveCron) {
        this.checkAliveCron = checkAliveCron;
    }
}
