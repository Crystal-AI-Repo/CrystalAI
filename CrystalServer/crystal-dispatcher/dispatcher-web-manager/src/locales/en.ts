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
                    lastAliveTimestamp: 'Last Alive'
                },
                values: {
                    secondsAgo: '{p0}s ago'
                }
            }
        }
    }
}