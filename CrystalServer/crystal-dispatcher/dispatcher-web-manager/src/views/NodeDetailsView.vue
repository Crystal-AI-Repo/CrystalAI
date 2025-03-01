<script setup lang="ts">

import {Ref, ref} from "vue";
import type {RegisteredNode} from "@/data/RegisteredNode.ts";
import {apiPrefixDispatcher, internalGet} from "@/net/axios-request.ts";
import {ElMessage} from "element-plus";
import {useI18n} from "vue-i18n";

const { t } = useI18n()

const props = defineProps({
  nodeId: String
})

const nodeInfo: Ref<RegisteredNode | null> = ref<RegisteredNode | null>(null)
const loading = ref(true)

function getNodeInfo() {
  internalGet<RegisteredNode | null>(
      `${apiPrefixDispatcher}/web-manager/getNodeById`,
      {},
      { 'nodeId': props.nodeId }
  ).then(res => {
    nodeInfo.value = res.data
    loading.value = false
  }).catch((err) => {
    ElMessage.error(t('page.nodeDetails.message.couldNotFetchNodeDetails') + ", code: " + err.code + ", message: " + err.message)
  })
}

getNodeInfo()

</script>

<template>
  <main class="node-details-container">
    <el-skeleton style="width: 100%" :loading="loading" animated>
      <template #template>
        <div class="flex-vertical" style="width: 100%">
          <el-skeleton-item variant="h1" style="width: 30%" />
          <p />
          <el-skeleton-item variant="h2" style="width: 50%" />
        </div>
      </template>
      <template #default>
        <p class="node-name">{{ nodeInfo?.nodeName }}</p>
        <p class="node-id">{{ nodeInfo?.nodeId }}</p>
      </template>
    </el-skeleton>
  </main>
</template>

<style scoped>
.node-details-container {
  width: 100%;
  height: 100%;
  padding: 1rem;
  box-sizing: border-box;
}

.node-name {
  font-size: 1.5rem;
}

.node-id {
  font-size: 1rem;
  color: #aaa;
}
</style>