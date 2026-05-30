<template>
  <div class="vehicle-dispatch">
    <div class="stat-row">
      <StatCard label="调度中车辆" :value="0" :icon="Van" />
      <StatCard label="待装车订单" :value="0" :icon="List" />
      <StatCard label="在途车辆" :value="0" :icon="Promotion" />
      <StatCard label="已完成调度" :value="0" :icon="CircleCheck" />
    </div>
    <div class="glass-card placeholder-card">
      <el-empty description="车辆调度功能开发中，敬请期待" :image-size="120">
        <template #image>
          <el-icon :size="100" color="#60758A"><Van /></el-icon>
        </template>
      </el-empty>
      <p style="text-align:center;color:#9FB3C8;margin-top:8px">
        计划支持：路线规划、运力分配、在途监控、装卸调度
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Van, List, Promotion, CircleCheck } from '@element-plus/icons-vue'
import StatCard from '@/components/StatCard.vue'
</script>

<style scoped lang="scss">
.stat-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 16px; }
.placeholder-card { padding: 60px 20px; display: flex; flex-direction: column; align-items: center; }
</style>
