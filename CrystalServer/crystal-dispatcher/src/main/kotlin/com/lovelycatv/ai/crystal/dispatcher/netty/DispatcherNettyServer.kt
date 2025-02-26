package com.lovelycatv.ai.crystal.dispatcher.netty

import com.lovelycatv.ai.crystal.common.netty.codec.FrameDecoder
import com.lovelycatv.ai.crystal.common.netty.codec.impl.NettyMessageChainDecoder
import com.lovelycatv.ai.crystal.common.netty.codec.impl.NettyMessageChainEncoder
import com.lovelycatv.ai.crystal.common.netty.handler.NettyEmptyReceivedMessageHandler
import com.lovelycatv.ai.crystal.dispatcher.OllamaTaskManager
import com.lovelycatv.ai.crystal.dispatcher.config.RegisteredNodeConfiguration
import com.lovelycatv.ai.crystal.dispatcher.manager.AbstractNodeManager
import com.lovelycatv.ai.crystal.dispatcher.netty.handler.NettyClientConnectionHandler
import com.lovelycatv.ai.crystal.dispatcher.netty.handler.NettyClientOllamaChatResponseHandler
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.stereotype.Component

/**
 * @author lovelycat
 * @since 2025-02-16 17:45
 * @version 1.0
 */
@Component
class DispatcherNettyServer(
    context: ConfigurableApplicationContext,
    private val dispatcherNodeConfiguration: RegisteredNodeConfiguration,
    private val nodeManager: AbstractNodeManager,
    private val ollamaTaskManager: OllamaTaskManager
) : AbstractDispatcherNettyServer(context) {
    override fun buildServerBootstrap(): ServerBootstrap {
        return ServerBootstrap()
            .group(NioEventLoopGroup(), NioEventLoopGroup())
            .channel(NioServerSocketChannel::class.java)
            .childHandler(object : ChannelInitializer<NioSocketChannel>() {
                override fun initChannel(channel: NioSocketChannel) {
                    channel.pipeline().addLast(FrameDecoder())
                    channel.pipeline().addLast(NettyMessageChainEncoder())
                    channel.pipeline().addLast(NettyMessageChainDecoder())
                    channel.pipeline().addLast(NettyEmptyReceivedMessageHandler())
                    channel.pipeline().addLast(
                        NettyClientConnectionHandler(
                            nodeManager = nodeManager,
                            getSecretKey = { nodeName ->
                                dispatcherNodeConfiguration.secretKeys.find { it.nodeName == nodeName }?.secretKey
                                    ?: dispatcherNodeConfiguration.defaultSecretKey
                            }
                        )
                    )
                    channel.pipeline().addLast(
                        NettyClientOllamaChatResponseHandler(
                            onResponseReceived = { messageChain, it ->
                                ollamaTaskManager.onMessageReceived(messageChain, it)
                            }
                        )
                    )
                }
            })
    }
}