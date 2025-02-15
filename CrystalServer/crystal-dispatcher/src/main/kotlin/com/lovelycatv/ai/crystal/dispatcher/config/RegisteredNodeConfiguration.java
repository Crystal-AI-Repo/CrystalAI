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

    private String defaultSecretKey = "Q3J5c3RhbEFJTm9kZQ==";

    private NodeSecretKey[] secretKeys;

    public String getCheckAliveCron() {
        return checkAliveCron;
    }

    public String getDefaultSecretKey() {
        return defaultSecretKey;
    }

    public NodeSecretKey[] getSecretKeys() {
        return secretKeys;
    }

    public void setCheckAliveCron(String checkAliveCron) {
        this.checkAliveCron = checkAliveCron;
    }

    public void setDefaultSecretKey(String defaultSecretKey) {
        this.defaultSecretKey = defaultSecretKey;
    }

    public void setSecretKeys(NodeSecretKey[] secretKeys) {
        this.secretKeys = secretKeys;
    }

    public static class NodeSecretKey {
        private String nodeName;
        private String secretKey;

        public String getSecretKey() {
            return secretKey;
        }

        public String getNodeName() {
            return nodeName;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }
    }
}
