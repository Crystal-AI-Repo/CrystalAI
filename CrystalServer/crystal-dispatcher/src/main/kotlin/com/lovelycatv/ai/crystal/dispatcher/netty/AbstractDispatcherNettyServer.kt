package com.lovelycatv.ai.crystal.dispatcher.netty

import com.lovelycatv.ai.crystal.common.util.logger
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext

/**
 * @author lovelycat
 * @since 2025-02-16 18:19
 * @version 1.0
 */
abstract class AbstractDispatcherNettyServer(
    private val context: ConfigurableApplicationContext
) {
    protected val log = this.logger()

    private var lastStartedPort: Int = -1

    private var _channelFuture: ChannelFuture? = null
    private val channelFuture: ChannelFuture? get() = this._channelFuture

    val isStarted get() = this.channelFuture != null && this.channelFuture?.channel()?.isActive == true

    private var tRequireManualStop = false

    fun startServer(port: Int = this.lastStartedPort): Pair<Boolean, String?> {
        if (isStarted) {
            return false to "Netty Server is running."
        }
        val serverBootstrap = this.buildServerBootstrap()

        val channelFuture = try {
            serverBootstrap.bind(port)
        } catch (e: Exception) {
            log.error("Failed to start Netty Server, reason: ${e.message}", e)
            log.error("Due to this exception, dispatcher server is shutting down.")
            SpringApplication.exit(context)
            return false to e.message
        }

        // Start Listener
        channelFuture.addListener {
            log.info("Netty Server started on port $port")

            // Reset last started port
            this.lastStartedPort = port

            // Reset manual stop flag
            this.tRequireManualStop = false

            // onStarted() callback
            this.onStarted()
        }

        this._channelFuture = channelFuture

        // Closure Listener
        val channelCloseFuture = this.channelFuture!!.channel().closeFuture()
        channelCloseFuture.addListener {
            if (!tRequireManualStop) {
                log.warn("Netty Server stopped by some reasons, trying to restart...")
                if (this.lastStartedPort > 0) {
                    this.startServer(this.lastStartedPort)
                } else {
                    log.error("Netty Server cannot be automatically restarted caused by invalid lastStartedPort (${lastStartedPort}).")
                    log.error("Due to this exception, dispatcher server is shutting down.")
                    SpringApplication.exit(context)
                }
            } else {
                // Manually stopped
                log.warn("Netty Server stopped by manually")
                // Clear ChannelFuture
                this._channelFuture = null
            }

            // onStopped() callback
            this.onStopped(this.tRequireManualStop)
        }

        return true to "Netty Server started on port $port"
    }

    fun stopServer(): Pair<Boolean, String?> {
        if (!isStarted) {
            return false to "Netty Server is not running."
        }
        this.tRequireManualStop = true
        this.channelFuture!!.channel().close()
        return true to "Netty Server stopped."
    }

    /**
     * Customize the ServerBootstrap
     *
     * @return [ServerBootstrap]
     */
    protected abstract fun buildServerBootstrap(): ServerBootstrap

    /**
     * Callback when server started
     *
     */
    open fun onStarted() {}

    /**
     * Callback when server stopped
     *
     * @param manually Stopped by manually
     */
    open fun onStopped(manually: Boolean) {}
}