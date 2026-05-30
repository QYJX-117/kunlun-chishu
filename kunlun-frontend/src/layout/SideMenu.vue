<template>
  <div class="sidebar" :class="{ collapsed }">
    <el-menu
      :default-active="activeMenu"
      :collapse="collapsed"
      router
      background-color="transparent"
      text-color="#9FB3C8"
      active-text-color="#F5C27A"
    >
      <el-sub-menu index="dashboard">
        <template #title>
          <el-icon><DataAnalysis /></el-icon>
          <span>智能看板</span>
        </template>
        <el-menu-item index="/dashboard/map">库存健康地图</el-menu-item>
        <el-menu-item index="/dashboard/quality">数据质量监控</el-menu-item>
        <el-menu-item index="/dashboard/sales-trend">销量趋势分析</el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="replenishment">
        <template #title>
          <el-icon><Box /></el-icon>
          <span>补货引擎</span>
        </template>
        <el-menu-item index="/replenishment/mrp">运行MRP</el-menu-item>
        <el-menu-item index="/replenishment/suggestion">补货建议列表</el-menu-item>
        <el-menu-item index="/replenishment/prediction-log">预测日志</el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="order">
        <template #title>
          <el-icon><Document /></el-icon>
          <span>订单中心</span>
        </template>
        <el-menu-item index="/order/pending">待处理工单</el-menu-item>
        <el-menu-item index="/order/history">历史订单</el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="dispatch">
        <template #title>
          <el-icon><Van /></el-icon>
          <span>车辆调度</span>
        </template>
        <el-menu-item index="/dispatch/vehicle">车辆调度</el-menu-item>
        <el-menu-item index="/dispatch/equipment">设备台账</el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="basic">
        <template #title>
          <el-icon><Setting /></el-icon>
          <span>基础数据</span>
        </template>
        <el-menu-item index="/basic/station">站点管理</el-menu-item>
        <el-menu-item index="/basic/material">油品管理</el-menu-item>
        <el-menu-item index="/basic/user">用户管理</el-menu-item>
      </el-sub-menu>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/store/app'
import { DataAnalysis, Box, Document, Setting, Van } from '@element-plus/icons-vue'

const route = useRoute()
const appStore = useAppStore()
const collapsed = computed(() => appStore.sidebarCollapsed)
const activeMenu = computed(() => route.path)
</script>

<style scoped lang="scss">
.sidebar {
  width: 220px;
  flex-shrink: 0;
  background: linear-gradient(180deg, #061529 0%, #081D38 100%);
  border-right: 1px solid rgba(255, 255, 255, 0.06);
  padding-top: 8px;
  transition: width 0.25s;
  overflow-y: auto;
  &.collapsed { width: 64px; }
}
.el-menu {
  border-right: none;
  .el-menu-item, :deep(.el-sub-menu__title) {
    font-size: 14px;
  }
  .el-menu-item.is-active {
    background: linear-gradient(90deg, rgba(245,194,122,0.18), rgba(255,179,87,0.06));
    border-left: 3px solid #F5C27A;
  }
  .el-menu-item:hover {
    background: rgba(255, 255, 255, 0.04);
  }
}
</style>
