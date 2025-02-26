export default {
    application: {
        name: 'Crystal Dispatcher Manager'
    },
    asideNavigation: {
        home: '首页',
        nodes: '节点'
    },
    page: {
        nodeManager: {
            title: '节点管理',
            table: {
                headers: {
                    nodeId: '节点 ID',
                    nodeName: '节点名称',
                    nodeAddress: '节点地址',
                    nodeRegisteredTime: '注册时间',
                    lastAliveTimestamp: '最后活跃',
                    nodeAliveness: '状态',
                    nodeNettyConnection: 'Netty',
                    modelName: '展示名称',
                    qualifiedModelName: '模型名称',
                    modelQuantization: "量化方式",
                    modelSize: '模型大小',
                    modelDigest: '模型签名',
                    modelModifiedTime: '修改时间'
                },
                values: {
                    secondsAgo: '{p0} 秒前',
                    ollamaModels: 'Ollama 模型',
                    disconnected: '未连接',
                    online: '在线',
                    offline: '离线'
                }
            }
        }
    }
}
