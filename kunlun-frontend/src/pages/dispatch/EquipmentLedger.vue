<template>
  <div class="equipment-ledger">
    <div class="stat-row">
      <StatCard label="设备总数" :value="0" :icon="Monitor" />
      <StatCard label="运行中" :value="0" :icon="CircleCheck" />
      <StatCard label="维护中" :value="0" :icon="SetUp" />
      <StatCard label="待检修" :value="0" :icon="Warning" />
    </div>
    <div class="glass-card placeholder-card">
      <el-empty description="设备台账功能预留，暂未开发" :image-size="120">
        <template #image>
          <el-icon :size="100" color="#60758A"><Monitor /></el-icon>
        </template>
      </el-empty>
      <p style="text-align:center;color:#9FB3C8;margin-top:8px">
        计划支持：设备台账登记、维保记录、检修提醒、备件管理
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Monitor, CircleCheck, SetUp, Warning } from '@element-plus/icons-vue'
import StatCard from '@/components/StatCard.vue'
</script>

<style scoped lang="scss">
.stat-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 16px; }
.placeholder-card { padding: 60px 20px; display: flex; flex-direction: column; align-items: center; }
</style>
