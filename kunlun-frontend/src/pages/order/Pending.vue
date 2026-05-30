<template>
  <div class="pending-page">
    <div class="stat-row">
      <StatCard label="待确认" :value="pendingCount" :icon="Clock" />
      <StatCard label="待审核" :value="12" :icon="Checked" />
      <StatCard label="待发车" :value="5" :icon="Van" />
      <StatCard label="今日计划" :value="28" :icon="List" />
      <StatCard label="逾期未处理" :value="3" :icon="WarningFilled" icon-bg="rgba(255,77,79,0.15)" />
      <StatCard label="预计金额" :value="'¥586,200'" :icon="Money" />
    </div>

    <div class="glass-card">
      <el-tabs v-model="activeTab" style="margin-bottom:8px">
        <el-tab-pane name="ALL" label="全部" />
        <el-tab-pane name="PENDING" label="待确认" />
        <el-tab-pane name="CONFIRMED" label="待审核" />
        <el-tab-pane name="IN_TRANSIT" label="待发车" />
      </el-tabs>
      <el-table :data="tableData" stripe v-loading="loading" @row-click="openDetail">
        <el-table-column prop="id" label="订单号" width="80" />
        <el-table-column prop="stationId" label="站点" width="160" />
        <el-table-column prop="materialId" label="油品" width="100" />
        <el-table-column prop="suggestedQuantity" label="补货量(L)" width="120">
          <template #default="{ row }">{{ formatNum(row.suggestedQuantity) }}</template>
        </el-table-column>
        <el-table-column prop="requiredDate" label="要求到货日" width="110" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><StatusTag :status="row.status" /></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="生成时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status==='PENDING'" size="small" type="primary" link @click.stop="confirm(row.id)">确认</el-button>
            <el-button v-if="row.status==='CONFIRMED'" size="small" type="success" link @click.stop="complete(row.id)">完成</el-button>
            <el-button v-if="row.status==='PENDING'" size="small" link @click.stop="cancel(row.id)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:12px;text-align:right">
        <el-pagination v-model:current-page="page" :total="total" :page-size="20" layout="prev, pager, next" @change="loadData" />
      </div>
    </div>

    <!-- 订单详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="订单详情" size="500px">
      <template v-if="detailRow">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="订单号">{{ detailRow.id }}</el-descriptions-item>
          <el-descriptions-item label="站点">{{ detailRow.stationId }}</el-descriptions-item>
          <el-descriptions-item label="补货量">{{ formatNum(detailRow.suggestedQuantity) }} L</el-descriptions-item>
          <el-descriptions-item label="状态"><StatusTag :status="detailRow.status" /></el-descriptions-item>
        </el-descriptions>

        <h4 style="margin:16px 0 8px;color:#D7E3F4">库存与安全库存对比</h4>
        <div style="display:flex;gap:20px;align-items:center">
          <el-progress :percentage="62" :stroke-width="20" :color="'#FFAA33'">
            <span style="color:#D7E3F4;font-size:12px">当前库存 62%</span>
          </el-progress>
          <span style="color:#9FB3C8;font-size:13px">安全库存线: 85%</span>
        </div>

        <h4 style="margin:16px 0 8px;color:#D7E3F4">预测需求（未来三天）</h4>
        <div v-for="i in 3" :key="i" style="display:flex;justify-content:space-between;padding:8px 0;border-bottom:1px solid rgba(255,255,255,0.04)">
          <span style="color:#9FB3C8">第{{ i }}天</span>
          <span style="color:#F5F7FA">{{ (Math.random()*30000+20000).toFixed(0) }} L</span>
        </div>

        <h4 style="margin:16px 0 8px;color:#D7E3F4">订单流程</h4>
        <el-timeline>
          <el-timeline-item :timestamp="detailRow.createdAt" placement="top" color="#27C46B">系统生成补货建议</el-timeline-item>
          <el-timeline-item v-if="detailRow.confirmedAt" :timestamp="detailRow.confirmedAt" placement="top" color="#3B82F6">调度员确认</el-timeline-item>
          <el-timeline-item v-if="detailRow.status==='COMPLETED'" timestamp="-" placement="top" color="#27C46B">已到货完成</el-timeline-item>
        </el-timeline>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Clock, Checked, Van, List, WarningFilled, Money } from '@element-plus/icons-vue'
import StatCard from '@/components/StatCard.vue'
import StatusTag from '@/components/StatusTag.vue'
import { getOrders, confirmOrder, completeOrder, cancelOrder } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const activeTab = ref('ALL')
const tableData = ref<any[]>([])
const page = ref(1)
const total = ref(0)
const pendingCount = ref(0)
const drawerVisible = ref(false)
const detailRow = ref<any>(null)

function formatNum(v: number) { return v?.toLocaleString?.() || v }

async function loadData() {
  loading.value = true
  try {
    const res = await getOrders({
      status: activeTab.value === 'ALL' ? undefined : activeTab.value,
      page: page.value,
      size: 20
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
    if (activeTab.value === 'ALL') pendingCount.value = res.data?.total || 0
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

function openDetail(row: any) { detailRow.value = row; drawerVisible.value = true }

async function confirm(id: number) {
  try {
    await ElMessageBox.confirm('确定要确认此订单吗？', '确认操作')
    await confirmOrder(id)
    ElMessage.success('订单已确认')
    loadData()
  } catch (e) { /* cancelled or error */ }
}

async function complete(id: number) {
  try {
    await ElMessageBox.confirm('确定标记为已完成？', '完成操作')
    await completeOrder(id)
    ElMessage.success('订单已完成')
    loadData()
  } catch (e) { /* cancelled or error */ }
}

async function cancel(id: number) {
  try {
    await ElMessageBox.confirm('确定要取消此订单吗？', '取消操作')
    await cancelOrder(id)
    ElMessage.success('订单已取消')
    loadData()
  } catch (e) { /* cancelled or error */ }
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.stat-row { display: grid; grid-template-columns: repeat(6, 1fr); gap: 16px; margin-bottom: 16px; }
.glass-card { padding: 16px; }
</style>
