<script setup lang="ts">

import {ref} from "vue";
import type {RegisteredNode} from "@/data/RegisteredNode.ts";
import {apiPrefixDispatcher, internalGet} from "@/net/axios-request.ts";
import {useI18n} from "vue-i18n";
import {Refresh} from "@element-plus/icons-vue";
import {formatTimestamp} from "@/utils/datetime-utils.ts";

const { t } = useI18n()

const registeredNodes = ref<RegisteredNode[]>([])

function refreshData() {
  internalGet<RegisteredNode[]>(`${apiPrefixDispatcher}/web-manager/listNodes`, {}, {}).then((res) => {
    registeredNodes.value = res.data
  })
}

const tableRowClassName = ({ row, rowIndex }: { row: RegisteredNode, rowIndex: number }) => {
  if (!row.isAlive) {
    return 'warning-row'
  } else {
    return ''
  }
}

// setInterval(() => {
//   refreshData()
// }, 1000)

refreshData()


</script>

<template>
  <main class="page-container">
    <p class="page-title">{{ t('page.nodeManager.title') }}</p>
    <div class="page-header-toolbar">
      <el-button size="default" type="primary" plain @click="refreshData()"><el-icon><Refresh /></el-icon>Refresh</el-button>
    </div>
    <el-table
        :data="registeredNodes"
        style="width: 100%"
        :row-class-name="tableRowClassName"
    >
      <el-table-column prop="nodeId" :label="t('page.nodeManager.table.headers.nodeId')" width="180" />
      <el-table-column prop="nodeName" :label="t('page.nodeManager.table.headers.nodeName')" />
      <el-table-column :label="t('page.nodeManager.table.headers.nodeAddress')">
        <template #default="scope">
          {{ scope.row.host }}:{{ scope.row.port }}
        </template>
      </el-table-column>
      <el-table-column :label="t('page.nodeManager.table.headers.nodeRegisteredTime')">
        <template #default="scope">
          {{ formatTimestamp(scope.row.registeredTimestamp) }}
        </template>
      </el-table-column>
      <el-table-column :label="t('page.nodeManager.table.headers.lastAliveTimestamp')">
        <template #default="scope">
          {{ t('page.nodeManager.table.values.secondsAgo', { p0: ((new Date().getTime() - scope.row.lastAliveTimestamp) / 1000).toFixed(0) }) }}
        </template>
      </el-table-column>
    </el-table>
  </main>
</template>

<style scoped>

</style>

<style>
.el-table .warning-row {
  --el-table-tr-bg-color: var(--el-color-warning-light-9);
}
.el-table .success-row {
  --el-table-tr-bg-color: var(--el-color-success-light-9);
}
</style>