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
                    lastAliveTimestamp: '最后活跃'
                },
                values: {
                    secondsAgo: '{p0} 秒前'
                }
            }
        }
    }
}
