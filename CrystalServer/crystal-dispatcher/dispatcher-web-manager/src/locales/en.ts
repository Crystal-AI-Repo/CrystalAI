export default {
    application: {
        name: 'Crystal Dispatcher Manager'
    },
    asideNavigation: {
        home: 'Home',
        nodes: 'Nodes'
    },
    page: {
        nodeManager: {
            title: 'Node Manager',
            table: {
                headers: {
                    nodeId: 'Node ID',
                    nodeName: 'Node Name',
                    nodeAddress: 'Node Address',
                    nodeRegisteredTime: 'Registered Time',
                    lastAliveTimestamp: 'Last Alive',
                    nodeAliveness: 'Aliveness',
                    nodeNettyConnection: 'Netty',
                    modelName: 'Display Name',
                    qualifiedModelName: 'Model Name',
                    modelQuantization: "Quantization",
                    modelSize: 'Size',
                    modelDigest: 'Digest',
                    modelModifiedTime: 'Modified Time'
                },
                values: {
                    secondsAgo: '{p0}s ago',
                    ollamaModels: 'Ollama Models',
                    disconnected: 'Disconnected',
                    online: 'Online',
                    offline: 'Offline'
                }
            }
        }
    }
}