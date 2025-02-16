package com.lovelycatv.ai.crystal.dispatcher.netty

import com.lovelycatv.ai.crystal.dispatcher.config.DispatcherServerConfiguration
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

/**
 * @author lovelycat
 * @since 2025-02-16 17:44
 * @version 1.0
 */
@Component
class NettyServerStarter(
    private val dispatcherServerConfiguration: DispatcherServerConfiguration,
    private val dispatcherNettyServer: DispatcherNettyServer
) : CommandLineRunner {
    /**
     * Callback used to run the bean.
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    override fun run(vararg args: String?) {
        dispatcherNettyServer.startServer(dispatcherServerConfiguration.communicationPort)
    }

}