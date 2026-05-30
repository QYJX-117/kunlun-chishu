<template>
  <div class="stat-card glass-card">
    <div class="stat-icon" :style="{ background: iconBg }">
      <el-icon :size="20"><component :is="icon" /></el-icon>
    </div>
    <div class="stat-body">
      <div class="stat-label">{{ label }}</div>
      <div class="stat-value">{{ value }}</div>
      <div v-if="trend !== undefined" class="stat-trend" :class="trend >= 0 ? 'up' : 'down'">
        <el-icon :size="12"><component :is="trend >= 0 ? 'Top' : 'Bottom'" /></el-icon>
        {{ Math.abs(trend) }}%
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  label: string
  value: string | number
  trend?: number
  icon: any
  iconBg?: string
}>()
</script>

<style scoped lang="scss">
.stat-card {
  padding: 18px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  min-height: 100px;
}
.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(59, 130, 246, 0.15);
  flex-shrink: 0;
}
.stat-body { flex: 1; }
.stat-label { font-size: 13px; color: #9FB3C8; margin-bottom: 4px; }
.stat-value { font-family: 'DIN', monospace; font-size: 26px; font-weight: 700; color: #F5F7FA; }
.stat-trend { font-size: 12px; margin-top: 4px; display: flex; align-items: center; gap: 2px; }
.stat-trend.up { color: #27C46B; }
.stat-trend.down { color: #FF4D4F; }
</style>
