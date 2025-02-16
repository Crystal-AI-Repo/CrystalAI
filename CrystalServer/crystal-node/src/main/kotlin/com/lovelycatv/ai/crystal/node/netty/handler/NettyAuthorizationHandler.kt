package com.lovelycatv.ai.crystal.node.netty.handler

import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.MessageChainBuilder
import com.lovelycatv.ai.crystal.common.data.message.auth.AuthorizeRequestMessage
import com.lovelycatv.ai.crystal.common.data.message.auth.AuthorizeResponseMessage
import com.lovelycatv.ai.crystal.common.util.logger
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * @author lovelycat
 * @since 2025-02-17 00:19
 * @version 1.0
 */
class NettyAuthorizationHandler(
    private val currentNodeId: String,
    private val applicationName: String,
    private val correctSecretKey: String
) : SimpleChannelInboundHandler<MessageChain>() {
    private val log = this.logger()

    /**
     * Is called for each message of type [I].
     *
     * @param ctx           the [ChannelHandlerContext] which this [SimpleChannelInboundHandler]
     * belongs to
     * @param msg           the message to handle
     * @throws Exception    is thrown if an error occurred
     */
    override fun channelRead0(ctx: ChannelHandlerContext, msg: MessageChain) {
        if (msg.isEmpty()) {
            ctx.fireChannelRead(msg)
        } else if (msg.messages[0] is AuthorizeRequestMessage) {
            val authorizeRequestMessage = msg.messages[0] as AuthorizeRequestMessage
            val nodeName = authorizeRequestMessage.nodeName
            val secretKey = authorizeRequestMessage.secretKey

            log.info("Authorize Request received.")

            if (nodeName == applicationName) {
                if (secretKey == correctSecretKey) {
                    log.info("Authentication for dispatcher is successful.")

                    ctx.writeAndFlush(MessageChainBuilder {
                        this.addMessage(AuthorizeResponseMessage.success(currentNodeId, applicationName, "123", "Welcome Back!"))
                    })
                } else {
                    log.warn("Authorize Request is failed due to invalid secret key: [{}]", secretKey)

                    ctx.writeAndFlush(MessageChainBuilder {
                        this.addMessage(AuthorizeResponseMessage.failed(currentNodeId, applicationName, "Invalid secret key"))
                    })
                }
            } else {
                log.warn("Authorize Request is failed due to wrong node name, given: [${nodeName}], current: [${applicationName}]")

                ctx.writeAndFlush(MessageChainBuilder {
                    this.addMessage(AuthorizeResponseMessage.failed(currentNodeId, applicationName, "Wrong node name"))
                })
            }
        } else {
            ctx.fireChannelRead(msg)
        }
    }
}