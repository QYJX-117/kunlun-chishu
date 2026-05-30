<template>
  <div class="dashboard-map">
    <!-- 顶部统计卡片 -->
    <div class="stat-row">
      <StatCard label="运行站点×油品组合" :value="totalCount" :icon="Shop" icon-bg="rgba(59,130,246,0.18)" />
      <StatCard label="库存健康" :value="safeCount" :icon="CircleCheck" icon-bg="rgba(39,196,107,0.18)" />
      <StatCard label="库存预警" :value="warningCount" :icon="Warning" icon-bg="rgba(255,170,51,0.18)" />
      <StatCard label="库存告警" :value="dangerCount" :icon="CircleClose" icon-bg="rgba(255,77,79,0.18)" />
    </div>

    <!-- 中间：地图 + 右侧面板 -->
    <div class="map-row">
      <div class="map-area glass-card">
        <MapContainer
          :markers="mapMarkers"
          @marker-click="onMarkerClick"
        />
      </div>
      <div class="right-panel">
        <div class="glass-card panel-chart">
          <h3 class="panel-title">库存对比（各站点当前库存 vs 安全库存）</h3>
          <v-chart :option="barOption" autoresize style="height:220px" />
        </div>
        <div class="glass-card panel-orders">
          <h3 class="panel-title">待执行订单</h3>
          <div v-if="pendingOrders.length" class="order-list">
            <div v-for="o in pendingOrders.slice(0, 5)" :key="o.id" class="order-item">
              <span>{{ o.stationName || '站点' + o.stationId }}</span>
              <span class="gold-text">{{ o.suggestedQuantity }}L</span>
              <StatusTag :status="o.status" />
            </div>
          </div>
          <el-empty v-else description="暂无待执行订单" :image-size="60" />
        </div>
      </div>
    </div>

    <!-- 底部表格 — 无交替色 -->
    <div class="glass-card" style="margin-top:16px">
      <el-table :data="tableData" style="width:100%">
        <el-table-column prop="name" label="站点名称" min-width="160" />
        <el-table-column prop="area" label="区域" width="80" />
        <el-table-column prop="materialName" label="油品" width="100" />
        <el-table-column prop="currentStock" label="当前库存(L)" width="130">
          <template #default="{ row }">{{ formatNum(row.currentStock) }}</template>
        </el-table-column>
        <el-table-column prop="safetyStock" label="安全库存(L)" width="130">
          <template #default="{ row }">{{ formatNum(row.safetyStock) }}</template>
        </el-table-column>
        <el-table-column label="健康状态" width="110">
          <template #default="{ row }"><StatusTag :status="row.stockStatus" /></template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Shop, CircleCheck, Warning, CircleClose } from '@element-plus/icons-vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { BarChart, LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import StatCard from '@/components/StatCard.vue'
import StatusTag from '@/components/StatusTag.vue'
import MapContainer from '@/components/MapContainer.vue'
import { getStationStatus, getRecentSales, getInventoryCompare } from '@/api/bi'
import { getOrders } from '@/api/order'

use([BarChart, LineChart, GridComponent, TooltipComponent, CanvasRenderer])

const stationStatus = ref<any[]>([])
const pendingOrders = ref<any[]>([])
const inventoryCompare = ref<any[]>([])

// 总记录数 = 站点×油品组合（20个站点 × 5种油品 = 理论上限100条）
const totalCount = computed(() => stationStatus.value.length)
// 按站点去重统计
const uniqueStations = computed(() => new Set(stationStatus.value.map(s => s.stationId)).size)
const safeCount = computed(() => stationStatus.value.filter(s => s.stockStatus === 'SAFE').length)
const warningCount = computed(() => stationStatus.value.filter(s => s.stockStatus === 'WARNING').length)
const dangerCount = computed(() => stationStatus.value.filter(s => s.stockStatus === 'DANGER').length)

const mapMarkers = computed(() =>
  stationStatus.value.map(s => ({ id: s.stationId, lng: s.lng, lat: s.lat, name: s.name, status: s.stockStatus }))
)

const tableData = computed(() => stationStatus.value)

// 库存对比柱状图：使用 inventory-compare API 返回的站点级聚合数据
const barOption = computed(() => {
  const data = inventoryCompare.value.length > 0
    ? inventoryCompare.value
    : stationStatus.value
  // 按站点名聚合，取前12个站点
  const stationMap = new Map<string, { currentStock: number; safetyStock: number }>()
  data.forEach((s: any) => {
    const name = (s.stationName || s.name || '').substring(0, 6)
    if (!stationMap.has(name)) {
      stationMap.set(name, { currentStock: 0, safetyStock: 0 })
    }
    const entry = stationMap.get(name)!
    entry.currentStock += Number(s.currentStock || 0)
    entry.safetyStock += Number(s.safetyStock || 0)
  })
  const entries = Array.from(stationMap.entries()).slice(0, 12)

  return {
    tooltip: { trigger: 'axis' },
    grid: { left: 20, right: 20, top: 10, bottom: 20 },
    xAxis: {
      type: 'category',
      data: entries.map(e => e[0]),
      axisLabel: { color: '#9FB3C8', fontSize: 10 }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#9FB3C8', fontSize: 10 },
      splitLine: { lineStyle: { color: 'rgba(255,255,255,0.08)' } }
    },
    series: [
      {
        name: '当前库存', type: 'bar',
        data: entries.map(e => e[1].currentStock),
        itemStyle: { color: '#60A5FA', borderRadius: [4, 4, 0, 0] }
      },
      {
        name: '安全库存', type: 'bar',
        data: entries.map(e => e[1].safetyStock),
        itemStyle: { color: '#F5C27A', borderRadius: [4, 4, 0, 0] }
      }
    ]
  }
})

function onMarkerClick(id: number) {
  getRecentSales(id, 7)
}

function formatNum(v: number) {
  return v?.toLocaleString?.() || v
}

onMounted(async () => {
  try {
    const [sRes, oRes, invRes] = await Promise.all([
      getStationStatus(),
      getOrders({ status: 'PENDING', page: 1, size: 20 }),
      getInventoryCompare().catch(() => ({ data: [] }))
    ])
    stationStatus.value = sRes.data || []
    pendingOrders.value = oRes.data?.records || []
    inventoryCompare.value = invRes.data || []
  } catch (e) { /* handled by interceptor */ }
})
</script>

<style scoped lang="scss">
.stat-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}
.map-row {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 16px;
  height: 440px;
}
.map-area {
  overflow: hidden;
  background: rgba(15, 40, 70, 0.6);
}
.right-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.panel-chart, .panel-orders {
  flex: 1;
  padding: 16px;
  min-height: 0;
  overflow: auto;
  background: rgba(18, 45, 75, 0.7);
}
.panel-title {
  font-size: 14px;
  font-weight: 600;
  color: #D7E3F4;
  margin-bottom: 12px;
}
.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid rgba(255,255,255,0.06);
  font-size: 13px;
  color: #D7E3F4;
}
</style>
