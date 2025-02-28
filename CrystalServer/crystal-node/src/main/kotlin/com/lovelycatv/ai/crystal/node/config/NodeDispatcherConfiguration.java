package com.lovelycatv.ai.crystal.node.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lovelycat
 * @version 1.0
 * @since 2025-02-15 17:51
 */
@Configuration
@ConfigurationProperties(prefix = "crystal.dispatcher")
public class NodeDispatcherConfiguration {
    public String host = "0.0.0.0";
    public int port = 5210;
    public boolean ssl = false;

    public String getBaseUrl() {
        return (this.ssl ? "https" : "http") + "://" + host + ((port != 80 && port != 443) ? (":" + port) : "");
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }
}
