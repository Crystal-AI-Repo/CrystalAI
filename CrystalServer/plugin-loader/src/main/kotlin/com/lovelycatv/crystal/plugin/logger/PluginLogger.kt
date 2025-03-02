package com.lovelycatv.crystal.plugin.logger

import org.slf4j.LoggerFactory

/**
 * @author lovelycat
 * @since 2025-03-02 22:54
 * @version 1.0
 */
class PluginLogger(pluginName: String) {
    private val logger = LoggerFactory.getLogger(pluginName)

    fun info(message: String) {
        logger.info(message)
    }

    fun info(message: String, t: Throwable) {
        logger.info(message, t)
    }

    fun warn(message: String) {
        logger.warn(message)
    }

    fun warn(message: String, t: Throwable) {
        logger.warn(message, t)
    }

    fun error(message: String) {
        logger.error(message)
    }

    fun error(message: String, t: Throwable) {
        logger.error(message, t)
    }
}