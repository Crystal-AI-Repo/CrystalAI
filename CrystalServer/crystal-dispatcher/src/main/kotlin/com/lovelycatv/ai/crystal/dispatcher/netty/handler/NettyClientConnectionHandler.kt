package com.lovelycatv.ai.crystal.dispatcher.netty.handler

import com.lovelycatv.ai.crystal.common.data.message.ClientConnectedMessage
import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.MessageChainBuilder
import com.lovelycatv.ai.crystal.common.data.message.auth.AuthorizeRequestMessage
import com.lovelycatv.ai.crystal.common.data.message.auth.AuthorizeResponseMessage
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.dispatcher.manager.AbstractNodeManager
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * @author lovelycat
 * @since 2025-02-16 22:44
 * @version 1.0
 */
class NettyClientConnectionHandler(
    private val nodeManager: AbstractNodeManager,
    private val getSecretKey: (nodeName: String) -> String,
) : SimpleChannelInboundHandler<MessageChain>() {
    private val log = this.logger()

    override fun channelActive(ctx: ChannelHandlerContext) {
        log.info("New client connected: {}, waiting for Connected Message sent by node.", ctx.channel().remoteAddress())
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        log.info("Client disconnected: " + ctx.channel().remoteAddress())
        // Find out the disconnect node
        val disconnectedNode = nodeManager.nettyClientPortMap.filterValues {
            it == ctx.channel().remoteAddress().toString().split(":")[1].toInt()
        }.toList().firstOrNull()
        disconnectedNode?.let {
            val node = nodeManager.getRegisteredNode(it.first)
            log.info("Node [{} / {}] disconnected from netty server.", node?.nodeName, node?.requestUrl)
            // Clear netty connection
            nodeManager.setNodeNettyChannel(it.first, null)
        }
    }

    /**
     * Is called for each message of type [I].
     *
     * @param ctx           the [ChannelHandlerContext] which this [SimpleChannelInboundHandler]
     * belongs to
     * @param msg           the message to handle
     * @throws Exception    is thrown if an error occurred
     */
    override fun channelRead0(ctx: ChannelHandlerContext, msg: MessageChain) {
        if (msg.messages[0] is ClientConnectedMessage) {
            // Node connected confirmed
            val clientConnectedMessage = msg.messages[0] as ClientConnectedMessage

            val nodeName = clientConnectedMessage.nodeName
            val secretKey = getSecretKey.invoke(nodeName)

            log.info("Connected Message send from [{}] received, sending auth request...", nodeName)

            // Send auth request to this node
            ctx.writeAndFlush(MessageChainBuilder {
                this.addMessage(AuthorizeRequestMessage(nodeName = nodeName, secretKey = secretKey))
            })

            ctx.fireChannelRead(msg.dropMessage(1))
        } else if (msg.messages[0] is AuthorizeResponseMessage) {
            // Node connected confirmed
            val authorizeResponseMessage = msg.messages[0] as AuthorizeResponseMessage

            val nodeId = authorizeResponseMessage.nodeId
            val nodeName = authorizeResponseMessage.nodeName
            val message = authorizeResponseMessage.message

            if (authorizeResponseMessage.success) {
                log.info("Authorization successful for node [{} / {}], message: {}", nodeName, nodeId, message)
            } else {
                log.warn("Node [{} / {}] has refused the auth request, message: {}", nodeName, nodeId, message)
            }

            nodeManager.setNodeNettyChannel(nodeId, ctx.channel())

            ctx.fireChannelRead(msg.dropMessage(1))
        } else {
            ctx.fireChannelRead(msg)
        }
    }
}