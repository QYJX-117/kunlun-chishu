<template>
  <el-tag :type="tagType" size="small" effect="dark">
    {{ label }}
  </el-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{ status: string }>()

const tagType = computed(() => {
  const map: Record<string, string> = {
    SAFE: 'success', WARNING: 'warning', DANGER: 'danger',
    PENDING: 'warning', CONFIRMED: '', COMPLETED: 'success', CANCELLED: 'info',
    IN_TRANSIT: ''
  }
  return map[props.status] || 'info'
})

const label = computed(() => {
  const map: Record<string, string> = {
    SAFE: '健康', WARNING: '预警', DANGER: '告警',
    PENDING: '待确认', CONFIRMED: '已确认', COMPLETED: '已完成', CANCELLED: '已取消',
    IN_TRANSIT: '运输中'
  }
  return map[props.status] || props.status
})
</script>
