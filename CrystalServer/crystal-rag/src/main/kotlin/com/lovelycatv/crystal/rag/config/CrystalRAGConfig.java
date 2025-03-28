package com.lovelycatv.crystal.rag.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lovelycat
 * @version 1.0
 * @since 2025-03-27 23:19
 */
@Configuration
@ConfigurationProperties(prefix = "crystal")
public class CrystalRAGConfig {
    private DispatcherConfig dispatcher;
    private NodeConfig node;
    private OllamaConfig ollama;

    public DispatcherConfig getDispatcher() {
        return dispatcher;
    }

    public NodeConfig getNode() {
        return node;
    }

    public OllamaConfig getOllama() {
        return ollama;
    }

    public void setDispatcher(DispatcherConfig dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void setNode(NodeConfig node) {
        this.node = node;
    }

    public void setOllama(OllamaConfig ollama) {
        this.ollama = ollama;
    }

    public static class DispatcherConfig {
        private String host;
        private Integer port;
        private String accessKey;

        public String getHost() {
            return host;
        }

        public Integer getPort() {
            return port;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }
    }

    public static class NodeConfig {
        private String host;
        private Integer port;
        private String secretKey;

        public String getHost() {
            return host;
        }

        public Integer getPort() {
            return port;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }
    }

    public static class OllamaConfig {
        private String host;
        private Integer port;


        public String getHost() {
            return host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public void setHost(String host) {
            this.host = host;
        }
    }
}
