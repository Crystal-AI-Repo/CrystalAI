package com.lovelycatv.ai.crystal.dispatcher.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lovelycat
 * @version 1.0
 * @since 2025-02-15 18:47
 */
@Configuration
@ConfigurationProperties(prefix = "crystal")
public class DispatcherConfiguration {
    private RegisteredNodeConfiguration registeredNodeConfiguration;

    public RegisteredNodeConfiguration getRegisteredNodeConfiguration() {
        return registeredNodeConfiguration;
    }

    public void setRegisteredNodeConfiguration(RegisteredNodeConfiguration registeredNodeConfiguration) {
        this.registeredNodeConfiguration = registeredNodeConfiguration;
    }
}
