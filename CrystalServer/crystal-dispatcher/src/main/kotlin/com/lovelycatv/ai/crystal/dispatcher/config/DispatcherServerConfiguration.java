package com.lovelycatv.ai.crystal.dispatcher.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lovelycat
 * @version 1.0
 * @since 2025-02-16 17:42
 */
@Configuration
@ConfigurationProperties(prefix = "crystal.server")
public class DispatcherServerConfiguration {
    private int communicationPort = 6210;

    public int getCommunicationPort() {
        return communicationPort;
    }

    public void setCommunicationPort(int communicationPort) {
        this.communicationPort = communicationPort;
    }
}
