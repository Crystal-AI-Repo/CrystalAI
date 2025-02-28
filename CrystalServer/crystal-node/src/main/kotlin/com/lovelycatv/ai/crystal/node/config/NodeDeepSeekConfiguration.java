package com.lovelycatv.ai.crystal.node.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lovelycat
 * @version 1.0
 * @since 2025-02-15 17:59
 */
@Configuration
@ConfigurationProperties(prefix = "crystal.deepseek")
public class NodeDeepSeekConfiguration {
    private boolean enabled = false;
    private String host = "https://api.deepseek.com";
    private int port = 80;
    private boolean ssl = true;
    private String apiKey = "";
    private String defaultModel = "deepseek-chat";
    private double defaultTemperature = 0.1;

    private long maxExecutionTimeMillis = 16000;

    public String getBaseUrl() {
        return (this.ssl ? "https" : "http") + "://" + host + ((port != 80 && port != 443) ? (":" + port) : "");
    }

    public boolean isEnabled() {
        return enabled;
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

    public String getApiKey() {
        return apiKey;
    }

    public String getDefaultModel() {
        return defaultModel;
    }

    public boolean isSsl() {
        return ssl;
    }

    public long getMaxExecutionTimeMillis() {
        return maxExecutionTimeMillis;
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setDefaultModel(String defaultModel) {
        this.defaultModel = defaultModel;
    }

    public void setDefaultTemperature(double defaultTemperature) {
        this.defaultTemperature = defaultTemperature;
    }

    public void setMaxExecutionTimeMillis(long maxExecutionTimeMillis) {
        this.maxExecutionTimeMillis = maxExecutionTimeMillis;
    }
}
