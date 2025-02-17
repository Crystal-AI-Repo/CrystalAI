package com.lovelycatv.ai.crystal.node.netty

import com.lovelycatv.ai.crystal.common.netty.codec.FrameDecoder
import com.lovelycatv.ai.crystal.common.netty.codec.impl.NettyMessageChainDecoder
import com.lovelycatv.ai.crystal.common.netty.codec.impl.NettyMessageChainEncoder
import com.lovelycatv.ai.crystal.node.Global
import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
import com.lovelycatv.ai.crystal.node.exception.InvalidNodeIdException
import com.lovelycatv.ai.crystal.node.netty.handler.NettyAuthorizationHandler
import com.lovelycatv.ai.crystal.common.netty.handler.NettyEmptyReceivedMessageHandler
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * @author lovelycat
 * @since 2025-02-16 21:59
 * @version 1.0
 */
@Component
class NodeNettyClient(
    @Value("\${spring.application.name}")
    private val applicationName: String,
    private val nodeConfiguration: NodeConfiguration
) : AbstractNodeNettyClient(applicationName) {
    /**
     * Customize the client bootstrap
     *
     * @return [Bootstrap]
     */
    override fun buildBootstrap(): Bootstrap {
        val currentUUID = Global.Variables.currentNodeUUID
            ?: throw InvalidNodeIdException(null, "Node may not be registered to dispatcher when creating netty client.")

        return Bootstrap()
            .group(NioEventLoopGroup())
            .channel(NioSocketChannel::class.java)
            .handler(object : ChannelInitializer<NioSocketChannel>() {
                override fun initChannel(ch: NioSocketChannel) {
                    ch.pipeline().addLast(FrameDecoder())
                    ch.pipeline().addLast(NettyMessageChainEncoder())
                    ch.pipeline().addLast(NettyMessageChainDecoder())
                    ch.pipeline().addLast(NettyEmptyReceivedMessageHandler())
                    ch.pipeline().addLast(
                        NettyAuthorizationHandler(
                            currentNodeId = currentUUID,
                            applicationName = applicationName,
                            correctSecretKey = nodeConfiguration.secretKey
                        )
                    )
                }

            })
    }

}