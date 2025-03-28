package com.lovelycatv.ai.crystal.node.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovelycatv.ai.crystal.common.client.FeignClientExtensionsKt;
import com.lovelycatv.ai.crystal.common.client.codec.JacksonFeignDecoder;
import com.lovelycatv.ai.crystal.common.client.codec.JacksonFeignEncoder;
import com.lovelycatv.ai.crystal.node.NodeRunningMode;
import com.lovelycatv.ai.crystal.node.client.NodeDispatcherClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lovelycat
 * @version 1.0
 * @since 2025-02-15 17:40
 */
@Configuration
@ConfigurationProperties(prefix = "crystal")
public class NodeConfiguration {
    public NodeRunningMode modeEnum;
    private String mode;
    private NodeDispatcherConfiguration dispatcher = new NodeDispatcherConfiguration();
    private NodeOllamaConfiguration ollama = new NodeOllamaConfiguration();
    private NodeDeepSeekConfiguration deepseek = new NodeDeepSeekConfiguration();

    private boolean ssl = false;

    private String secretKey = "Q3J5c3RhbEFJTm9kZQ==";

    @Bean("dispatcherClient")
    public NodeDispatcherClient dispatcherClient() {
        ObjectMapper objectMapper = new ObjectMapper();
        return FeignClientExtensionsKt.getFeignClient(
                NodeDispatcherClient.class,
                dispatcher.getBaseUrl(),
                objectMapper,
                objectMapper,
                new JacksonFeignEncoder(objectMapper),
                new JacksonFeignDecoder(objectMapper),
                builder -> builder
        );
    }

    public NodeRunningMode getModeEnum() {
        if (NodeRunningMode.STANDALONE.getMode().equalsIgnoreCase(this.mode)) {
            return NodeRunningMode.STANDALONE;
        } else if (NodeRunningMode.CLUSTER.getMode().equalsIgnoreCase(this.mode)) {
            return NodeRunningMode.CLUSTER;
        } else {
            throw new IllegalStateException("Unknown mode " + this.mode);
        }
    }

    public String getMode() {
        return mode;
    }

    public NodeDispatcherConfiguration getDispatcher() {
        return dispatcher;
    }

    public NodeOllamaConfiguration getOllama() {
        return ollama;
    }

    public NodeDeepSeekConfiguration getDeepseek() {
        return deepseek;
    }

    public void setMode(String mode) {
        this.mode = mode;
        this.modeEnum = this.getModeEnum();
    }

    public boolean isSsl() {
        return ssl;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setDispatcher(NodeDispatcherConfiguration dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void setOllama(NodeOllamaConfiguration ollama) {
        this.ollama = ollama;
    }

    public void setDeepseek(NodeDeepSeekConfiguration deepseek) {
        this.deepseek = deepseek;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
