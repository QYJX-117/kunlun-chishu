<template>
  <div class="quality-page">
    <div class="stat-row">
      <StatCard label="数据完整性" :value="`${health?.completeness ?? '--'}%`" :trend="1.5" :icon="DataBoard" />
      <StatCard label="数据准确率" :value="`${health?.accuracyRate ?? '--'}%`" :trend="2.1" :icon="Aim" />
      <StatCard label="异常记录数" :value="health?.anomalyCount ?? 0" :trend="-0.3" :icon="WarningFilled" />
      <StatCard label="数据截止日期" :value="health?.latestDate || '-'" :trend="0.8" :icon="Clock" />
    </div>

    <div class="chart-row">
      <div class="glass-card chart-left">
        <h3>质量趋势</h3>
        <v-chart :option="trendOption" autoresize style="height:260px" />
      </div>
      <div class="glass-card chart-mid">
        <h3>数据分布</h3>
        <v-chart :option="pieOption" autoresize style="height:260px" />
      </div>
      <div class="glass-card chart-right">
        <h3>告警列表</h3>
        <div v-if="health?.anomalyCount" class="alert-list">
          <div class="alert-item">
            <el-icon color="#FF4D4F"><WarningFilled /></el-icon>
            <span>异常数据 {{ health.anomalyCount }} 条</span>
            <el-tag type="danger" size="small">需处理</el-tag>
          </div>
        </div>
        <el-empty v-else description="暂无告警" :image-size="60" />
      </div>
    </div>

    <div class="glass-card" style="margin-top:16px">
      <h3 style="margin-bottom:12px;color:#D7E3F4">规则监控</h3>
      <el-table :data="ruleData" style="width:100%">
        <el-table-column prop="rule" label="监控规则" min-width="200" />
        <el-table-column prop="passRate" label="通过率" width="200">
          <template #default="{ row }">
            <el-progress :percentage="row.passRate" :color="row.passRate > 90 ? '#27C46B' : '#FFAA33'" :stroke-width="12" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="row.passRate > 90 ? 'success' : 'warning'" size="small">{{ row.passRate > 90 ? '正常' : '注意' }}</el-tag></template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Aim, DataBoard, Clock, WarningFilled } from '@element-plus/icons-vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { LineChart, PieChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import StatCard from '@/components/StatCard.vue'
import { getDataHealth } from '@/api/bi'

use([LineChart, PieChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer])

const health = ref<any>({})

const trendOption = {
  tooltip: { trigger: 'axis' },
  legend: { textStyle: { color: '#9FB3C8' }, top: 0 },
  grid: { left: 20, right: 20, top: 40, bottom: 20 },
  xAxis: { type: 'category', data: ['5/22', '5/23', '5/24', '5/25', '5/26', '5/27', '5/28'], axisLabel: { color: '#9FB3C8' }, axisLine: { lineStyle: { color: 'rgba(255,255,255,0.08)' } } },
  yAxis: { type: 'value', min: 90, max: 100, axisLabel: { color: '#9FB3C8' }, splitLine: { lineStyle: { color: 'rgba(255,255,255,0.06)' } } },
  series: [
    { name: '准确性', type: 'line', data: [97, 98, 97.5, 98.2, 98.5, 98.3, 98.6], smooth: true, lineStyle: { color: '#3B82F6', width: 2 }, itemStyle: { color: '#3B82F6' } },
    { name: '完整性', type: 'line', data: [96, 96.5, 97, 97.2, 97.3, 97.1, 97.3], smooth: true, lineStyle: { color: '#27C46B', width: 2 }, itemStyle: { color: '#27C46B' } }
  ]
}

const pieOption = {
  tooltip: { trigger: 'item' },
  legend: { bottom: 0, textStyle: { color: '#9FB3C8' } },
  series: [{
    type: 'pie', radius: ['50%', '75%'], center: ['50%', '45%'],
    label: { show: false },
    data: [
      { value: 85, name: '优质', itemStyle: { color: '#27C46B' } },
      { value: 12, name: '可用', itemStyle: { color: '#FFAA33' } },
      { value: 3, name: '低质', itemStyle: { color: '#FF4D4F' } }
    ]
  }]
}

const ruleData = [
  { rule: '单日销量波动 ≤ 3倍标准差', passRate: 97.5 },
  { rule: '库存数据更新延迟 ≤ 1小时', passRate: 94.2 },
  { rule: '站点经纬度坐标有效性', passRate: 100 },
  { rule: '订单状态流转合规性', passRate: 99.1 }
]

onMounted(async () => {
  try {
    const res = await getDataHealth()
    health.value = res.data || {}
  } catch (e) { /* handled */ }
})
</script>

<style scoped lang="scss">
.stat-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 16px; }
.chart-row { display: grid; grid-template-columns: 1fr 1fr 280px; gap: 16px; height: 340px; }
.chart-left, .chart-mid, .chart-right { padding: 16px; overflow: auto; }
.chart-left h3, .chart-mid h3, .chart-right h3 { font-size: 15px; color: #D7E3F4; margin-bottom: 12px; }
.alert-item { display: flex; align-items: center; gap: 8px; padding: 10px 0; border-bottom: 1px solid rgba(255,255,255,0.04); font-size: 13px; color: #D7E3F4; }
</style>
