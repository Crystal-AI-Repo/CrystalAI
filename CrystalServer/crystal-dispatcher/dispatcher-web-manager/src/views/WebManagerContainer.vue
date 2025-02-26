<script setup lang="ts">

import {onMounted, ref, watch} from "vue";
import {Cpu, House} from "@element-plus/icons-vue";
import {useI18n} from "vue-i18n";
import router from "@/router";

const isAsideMenuCollapsed = ref(false)

const { t } = useI18n()

/* Dynamic observer for activated menu item */
const activeMenuItem = ref("/home")

onMounted(() => {
  autoUpdateActiveMenuItem()
})

watch(router.currentRoute, () => {
  console.log(router.currentRoute.value)
  autoUpdateActiveMenuItem()
})

function autoUpdateActiveMenuItem() {
  console.log(router.currentRoute.value)
  activeMenuItem.value = router.currentRoute.value.path
}

</script>

<template>
  <main class="web-manager-container flex-vertical">
    <div class="web-manager-header">
      <el-menu
          mode="horizontal"
          :ellipsis="false"
      >
        <el-menu-item index="/home">{{ t('application.name') }}</el-menu-item>
        <span class="flex-grow-1" />
      </el-menu>
    </div>

    <div class="flex-horizontal flex-grow-1">
      <div class="web-manager-aside">
        <el-menu
            class="height-100 menu"
            :default-active="activeMenuItem"
            :collapse="isAsideMenuCollapsed"
            default-active="/home"
            router
        >
          <el-menu-item index="/home">
            <template #title>
              <el-icon><House /></el-icon>
              <span>{{ t('asideNavigation.home') }}</span>
            </template>
          </el-menu-item>

          <el-menu-item index="/nodes">
            <template #title>
              <el-icon><Cpu /></el-icon>
              <span>{{ t('asideNavigation.nodes') }}</span>
            </template>
          </el-menu-item>
        </el-menu>
      </div>

      <div class="web-manager-main">
        <RouterView />
      </div>
    </div>

  </main>
</template>

<style scoped>
.web-manager-container {
  width: inherit;
  height: inherit;
}

.web-manager-header {
  width: 100%;
}

.web-manager-aside {
  width: auto;
}

.web-manager-aside .menu {
  min-width: 160px;
}

.web-manager-main {
  flex-grow: 1;
}
</style>