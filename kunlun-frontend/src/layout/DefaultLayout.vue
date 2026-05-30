<template>
  <div class="layout">
    <SideMenu />
    <div class="layout-right">
      <TopHeader />
      <div class="layout-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import TopHeader from './TopHeader.vue'
import SideMenu from './SideMenu.vue'
</script>

<style scoped lang="scss">
.layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}
.layout-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.layout-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
}
</style>
