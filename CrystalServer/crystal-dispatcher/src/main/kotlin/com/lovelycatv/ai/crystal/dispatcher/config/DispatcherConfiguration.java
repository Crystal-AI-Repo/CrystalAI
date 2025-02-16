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
    private RegisteredNodeConfiguration node;
    private DispatcherServerConfiguration server;

    public RegisteredNodeConfiguration getNode() {
        return node;
    }

    public DispatcherServerConfiguration getServer() {
        return server;
    }

    public void setNode(RegisteredNodeConfiguration node) {
        this.node = node;
    }

    public void setServer(DispatcherServerConfiguration server) {
        this.server = server;
    }
}
