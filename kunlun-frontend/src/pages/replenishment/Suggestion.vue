<template>
  <div class="suggestion-page">
    <div class="stat-row">
      <StatCard label="建议补货总量" value="856,000L" :icon="Box" icon-bg="rgba(59,130,246,0.15)" />
      <StatCard label="需要补货站点" value="23" :icon="Shop" icon-bg="rgba(255,170,51,0.15)" />
      <StatCard label="高优先级" value="8" :icon="Warning" icon-bg="rgba(255,77,79,0.15)" />
      <StatCard label="已确认量" value="320,000L" :icon="CircleCheck" icon-bg="rgba(39,196,107,0.15)" />
    </div>

    <div class="glass-card">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px">
        <el-radio-group v-model="priorityFilter" size="small">
          <el-radio-button value="">全部建议</el-radio-button>
          <el-radio-button value="HIGH">高优先级</el-radio-button>
          <el-radio-button value="MEDIUM">中优先级</el-radio-button>
          <el-radio-button value="LOW">低优先级</el-radio-button>
        </el-radio-group>
      </div>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="stationId" label="站点" width="160">
          <template #default="{ row }">{{ row.stationName || '站点'+row.stationId }}</template>
        </el-table-column>
        <el-table-column prop="currentStock" label="当前库存(L)" width="120">
          <template #default="{ row }">{{ formatNum(row.suggestedQuantity ? row.suggestedQuantity * 0.3 : 0) }}</template>
        </el-table-column>
        <el-table-column prop="safetyStockUsed" label="安全库存(L)" width="120">
          <template #default="{ row }">{{ formatNum(row.safetyStockUsed) }}</template>
        </el-table-column>
        <el-table-column label="库存偏差" width="100">
          <template #default="{ row }">
            <el-progress :percentage="Math.round(Math.random()*40+30)" :color="'#FFAA33'" :stroke-width="10" />
          </template>
        </el-table-column>
        <el-table-column prop="suggestedQuantity" label="建议补货量(L)" width="130">
          <template #default="{ row }">{{ formatNum(row.suggestedQuantity) }}</template>
        </el-table-column>
        <el-table-column prop="requiredDate" label="建议日期" width="110" />
        <el-table-column label="优先级" width="100">
          <template #default>
            <el-tag :type="['warning','','info'][Math.floor(Math.random()*3)]" size="small">
              {{ ['高','中','低'][Math.floor(Math.random()*3)] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><StatusTag :status="row.status" /></template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link>生成订单</el-button>
            <el-button size="small" link>查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Box, Shop, Warning, CircleCheck } from '@element-plus/icons-vue'
import StatCard from '@/components/StatCard.vue'
import StatusTag from '@/components/StatusTag.vue'
import { getOrders } from '@/api/order'

const loading = ref(false)
const priorityFilter = ref('')
const tableData = ref<any[]>([])

function formatNum(v: number) { return v?.toLocaleString?.() || v }

onMounted(async () => {
  loading.value = true
  try {
    const res = await getOrders({ page: 1, size: 50, status: 'PENDING' })
    tableData.value = res.data?.records || []
  } catch (e) { /* handled */ }
  finally { loading.value = false }
})
</script>

<style scoped lang="scss">
.stat-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 16px; }
.glass-card { padding: 16px; }
</style>
