<template>
  <div class="history-page">
    <div class="stat-row">
      <StatCard label="本月完成" :value="142" :icon="CircleCheck" />
      <StatCard label="本月取消" :value="5" :icon="CircleClose" />
      <StatCard label="本月金额" :value="'¥3,286,000'" :icon="Money" />
      <StatCard label="准时率" :value="'94.2%'" :trend="1.8" :icon="Clock" />
    </div>

    <div class="glass-card">
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="订单号" width="80" />
        <el-table-column prop="stationId" label="站点" width="160" />
        <el-table-column prop="suggestedQuantity" label="补货量(L)" width="120">
          <template #default="{ row }">{{ formatNum(row.suggestedQuantity) }}</template>
        </el-table-column>
        <el-table-column prop="requiredDate" label="到货日期" width="110" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><StatusTag :status="row.status" /></template>
        </el-table-column>
        <el-table-column prop="predictionMethod" label="预测方法" width="160" />
        <el-table-column prop="createdAt" label="完成时间" width="160" />
      </el-table>
      <div style="margin-top:12px;text-align:right">
        <el-pagination v-model:current-page="page" :total="total" :page-size="20" layout="prev, pager, next" @change="loadData" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { CircleCheck, CircleClose, Money, Clock } from '@element-plus/icons-vue'
import StatCard from '@/components/StatCard.vue'
import StatusTag from '@/components/StatusTag.vue'
import { getOrders } from '@/api/order'

const loading = ref(false)
const page = ref(1)
const total = ref(0)
const tableData = ref<any[]>([])

function formatNum(v: number) { return v?.toLocaleString?.() || v }

async function loadData() {
  loading.value = true
  try {
    const res = await getOrders({ page: page.value, size: 20 })
    tableData.value = (res.data?.records || []).filter((o: any) => o.status === 'COMPLETED' || o.status === 'CANCELLED')
    total.value = res.data?.total || 0
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.stat-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 16px; }
.glass-card { padding: 16px; }
</style>
