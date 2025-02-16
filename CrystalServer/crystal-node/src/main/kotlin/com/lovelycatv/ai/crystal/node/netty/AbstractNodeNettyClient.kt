package com.lovelycatv.ai.crystal.node.netty

import com.lovelycatv.ai.crystal.common.data.message.ClientConnectedMessage
import com.lovelycatv.ai.crystal.common.data.message.MessageChainBuilder
import com.lovelycatv.ai.crystal.common.util.logger
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import java.net.InetSocketAddress

/**
 * @author lovelycat
 * @since 2025-02-16 22:05
 * @version 1.0
 */
abstract class AbstractNodeNettyClient(
    private val applicationName: String
) {
    private val log = this.logger()

    private var channel: Channel? = null

    val isConnected: Boolean get() = this.channel != null && this.channel?.isActive == true

    private var tRequireManualDisconnect: Boolean = false

    /**
     * Customize the client bootstrap
     *
     * @return [Bootstrap]
     */
    abstract fun buildBootstrap(): Bootstrap

    fun startClient(host: String, port: Int): Pair<Boolean, String?> {
        if (isConnected) {
            return false to "Already connected to dispatcher."
        }

        val channel = try {
            val serverAddress = InetSocketAddress(
                host.replace("http://", "").replace("https://", ""),
                port
            )

            val bootstrap = this.buildBootstrap()

            bootstrap.connect(serverAddress).sync().channel()
        } catch (e: Exception) {
            log.error("Failed to connect to dispatcher. Reason: ${e.message}", e)
            return false to "Failed to connect to dispatcher."
        }

        // Reset flag
        this.tRequireManualDisconnect = false

        log.info("Connected to dispatcher.")

        // Send connected message to dispatcher
        channel.writeAndFlush(MessageChainBuilder {
            this.addMessage(ClientConnectedMessage(nodeName = applicationName))
        }).addListener {
            if (it.isSuccess) {
                log.info("Connected Message has been send, waiting for auth request message from dispatcher.")
            } else {
                log.warn("Connected Message send failed, reason: ${it.cause().message}", it.cause())
                log.error("Client is shutting down due to Connected Message send failed.")
                this.stopClient()
            }
        }

        channel.closeFuture().addListener {
            if (!tRequireManualDisconnect) {
                log.warn("Disconnected from dispatcher for some reasons. Waiting for reconnect...")
            } else {
                log.warn("Disconnected from dispatcher.")
            }

            this.channel = null
        }

        this.channel = channel

        return true to "Connected to dispatcher."
    }

    fun stopClient(): Pair<Boolean, String?> {
        if (!isConnected) {
            return false to "Not connected to dispatcher."
        }
        this.tRequireManualDisconnect = true
        channel!!.close()
        return true to "Disconnected from dispatcher."
    }
}