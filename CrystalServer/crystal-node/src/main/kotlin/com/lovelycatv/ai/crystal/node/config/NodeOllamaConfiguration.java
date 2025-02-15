package com.lovelycatv.ai.crystal.node.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lovelycat
 * @version 1.0
 * @since 2025-02-15 17:59
 */
@Configuration
@ConfigurationProperties(prefix = "crystal.ollama")
public class NodeOllamaConfiguration {
    private String host = "localhost";
    private int port = 11434;
    private boolean ssl = false;
    private String defaultModel = "gemma2:9b";
    private double defaultTemperature = 0.5;

    public String getBaseUrl() {
        return (this.ssl ? "https" : "http") + "://" + host + ":" + port;
    }

    public String getHost() {
        return host;
    }

    public double getDefaultTemperature() {
        return defaultTemperature;
    }

    public int getPort() {
        return port;
    }

    public String getDefaultModel() {
        return defaultModel;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public void setDefaultModel(String defaultModel) {
        this.defaultModel = defaultModel;
    }

    public void setDefaultTemperature(double defaultTemperature) {
        this.defaultTemperature = defaultTemperature;
    }
}
