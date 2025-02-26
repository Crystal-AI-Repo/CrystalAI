<script setup lang="ts">

import {ref} from "vue";
import type {RegisteredNode} from "@/data/RegisteredNode.ts";
import {apiPrefixDispatcher, internalGet} from "@/net/axios-request.ts";
import {useI18n} from "vue-i18n";
import {Lock, Refresh, Unlock} from "@element-plus/icons-vue";
import {formatTimestamp, iso8061Date} from "@/utils/datetime-utils.ts";
import {formatByteSize} from "../utils/number-utils.ts";
import type {OllamaModel} from "@/data/OllamaModel.ts";

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
      <el-table-column type="expand">
        <template #default="props">
          <div class="node-table-row-sub-container">
            <p class="font-size-1-05rem">{{ t('page.nodeManager.table.values.ollamaModels') }}</p>
            <el-table :data="props.row.ollamaModels" style="width: 100%">
              <!-- Model Display Name -->
              <el-table-column :label="t('page.nodeManager.table.headers.modelName')" fixed>
                <template #default="scope">
                  {{ scope.row.name }}
                </template>
              </el-table-column>
              <!-- Model Name -->
              <el-table-column :label="t('page.nodeManager.table.headers.qualifiedModelName')" prop="model" />
              <!-- Model Quantization -->
              <el-table-column :label="t('page.nodeManager.table.headers.modelQuantization')" width="100">
                <template #default="scope">
                  <el-tag>{{ scope.row.details.quantization_level }}</el-tag>
                </template>
              </el-table-column>
              <!-- Model Size -->
              <el-table-column
                  :label="t('page.nodeManager.table.headers.modelSize')"
                  width="100"
                  sortable
                  :sort-method="(a: OllamaModel, b: OllamaModel) => a.size - b.size"
              >
                <template #default="scope">
                  {{ formatByteSize(scope.row.size) }}
                </template>
              </el-table-column>
              <!-- Model Digest -->
              <el-table-column :label="t('page.nodeManager.table.headers.modelDigest')">
                <template #default="scope">
                  <el-text truncated>{{ scope.row.digest }}</el-text>
                </template>
              </el-table-column>
              <!-- Model Modified Time -->
              <el-table-column
                  :label="t('page.nodeManager.table.headers.modelModifiedTime')"
                  sortable
                  :sort-method="(a: OllamaModel, b: OllamaModel) => b.modified_at - a.modified_at"
              >
                <template #default="scope">
                  {{ formatTimestamp(iso8061Date(scope.row.modified_at).getTime()) }}
                </template>
              </el-table-column>
            </el-table>
          </div>
        </template>
      </el-table-column>

      <!-- Node Id -->
      <el-table-column prop="nodeId" :label="t('page.nodeManager.table.headers.nodeId')" width="180" />
      <!-- Node Name -->
      <el-table-column prop="nodeName" :label="t('page.nodeManager.table.headers.nodeName')" />
      <!-- Node Address -->
      <el-table-column :label="t('page.nodeManager.table.headers.nodeAddress')">
        <template #default="scope">
          <div class="flex-horizontal flex-center-vertically">
            <el-icon style="display: inline">
              <Lock v-if="scope.row.ssl" />
              <Unlock v-else />
            </el-icon>
            &nbsp;{{ scope.row.host }}:{{ scope.row.port }}
          </div>
        </template>
      </el-table-column>
      <!-- Node Registered Time -->
      <el-table-column
          :label="t('page.nodeManager.table.headers.nodeRegisteredTime')"
          sortable
          :sort-method="(a: RegisteredNode, b: RegisteredNode) => a.registeredTimestamp - b.registeredTimestamp"
      >
        <template #default="scope">
          {{ formatTimestamp(scope.row.registeredTimestamp) }}
        </template>
      </el-table-column>
      <!-- Node Aliveness -->
      <el-table-column :label="t('page.nodeManager.table.headers.nodeAliveness')">
        <template #default="scope">
          <el-tag v-if="scope.row.isAlive" type="success" size="default">{{ t('page.nodeManager.table.values.online') }}</el-tag>
          <el-tag v-else type="danger" size="default">{{ t('page.nodeManager.table.values.offline') }}</el-tag>
        </template>
      </el-table-column>
      <!-- Node Netty Connection -->
      <el-table-column :label="t('page.nodeManager.table.headers.nodeNettyConnection')">
        <template #default="scope">
          <el-tag v-if="scope.row.isNettyClientConnected" type="success" size="default">{{ scope.row.nettyClientPort }}</el-tag>
          <el-tag v-else type="danger" size="default">{{ t('page.nodeManager.table.values.disconnected') }}</el-tag>
        </template>
      </el-table-column>
      <!-- Node Last Alive Time -->
      <el-table-column :label="t('page.nodeManager.table.headers.lastAliveTimestamp')">
        <template #default="scope">
          {{ t('page.nodeManager.table.values.secondsAgo', { p0: ((new Date().getTime() - scope.row.lastAliveTimestamp) / 1000).toFixed(0) }) }}
        </template>
      </el-table-column>
    </el-table>
  </main>
</template>

<style scoped>
.node-table-row-sub-container {
  padding: 1rem;
}
</style>

<style>
.el-table .warning-row {
  --el-table-tr-bg-color: var(--el-color-warning-light-9);
}
.el-table .success-row {
  --el-table-tr-bg-color: var(--el-color-success-light-9);
}
</style>