<template>
  <div class="prediction-log">
    <div class="stat-row">
      <StatCard label="总执行次数" :value="logCount" :icon="Timer" icon-bg="rgba(59,130,246,0.15)" />
      <StatCard label="平均准确率(MAPE)" :value="'91.3%'" :trend="2.1" :icon="Aim" icon-bg="rgba(39,196,107,0.15)" />
      <StatCard label="最佳算法" :value="'二次指数平滑'" :icon="Trophy" icon-bg="rgba(255,170,51,0.15)" />
      <StatCard label="预测覆盖率" :value="'96.8%'" :icon="PieChart" icon-bg="rgba(168,85,247,0.15)" />
    </div>

    <div class="chart-row">
      <div class="glass-card">
        <h3>算法准确率对比</h3>
        <v-chart :option="barOption" autoresize style="height:260px" />
      </div>
      <div class="glass-card">
        <h3>预测状态分布</h3>
        <v-chart :option="ringOption" autoresize style="height:260px" />
      </div>
    </div>

    <div class="glass-card" style="margin-top:16px">
      <h3 style="margin-bottom:12px;color:#D7E3F4">执行记录</h3>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="predictDate" label="预测日期" width="110" />
        <el-table-column prop="modelName" label="算法类型" width="150" />
        <el-table-column prop="mse" label="MSE" width="100">
          <template #default="{ row }">{{ formatNum(row.mse) }}</template>
        </el-table-column>
        <el-table-column prop="mape" label="MAPE(%)" width="100">
          <template #default="{ row }">{{ row.mape ? Number(row.mape).toFixed(1) : '-' }}</template>
        </el-table-column>
        <el-table-column prop="trackingSignal" label="跟踪信号" width="100">
          <template #default="{ row }">{{ row.trackingSignal ? Number(row.trackingSignal).toFixed(2) : '-' }}</template>
        </el-table-column>
        <el-table-column prop="forecastValue" label="预测值(L)" width="120">
          <template #default="{ row }">{{ formatNum(row.forecastValue) }}</template>
        </el-table-column>
        <el-table-column prop="actualValue" label="实际值(L)" width="120">
          <template #default="{ row }">{{ formatNum(row.actualValue) }}</template>
        </el-table-column>
      </el-table>
      <div style="margin-top:12px;text-align:right">
        <el-pagination background layout="prev, pager, next" :total="logCount" :page-size="20" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Timer, Aim, Trophy, PieChart } from '@element-plus/icons-vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { BarChart, PieChart as EPie } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import StatCard from '@/components/StatCard.vue'
import { getPredictionLogs } from '@/api/base'

use([BarChart, EPie, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer])

const loading = ref(false)
const tableData = ref<any[]>([])
const logCount = ref(500)

const barOption = {
  tooltip: { trigger: 'axis' },
  grid: { left: 20, right: 20, top: 40, bottom: 20 },
  xAxis: { type: 'category', data: ['一次平滑','二次平滑','移动平均','线性回归','ARIMA'], axisLabel: { color: '#9FB3C8' } },
  yAxis: { type: 'value', max: 100, axisLabel: { color: '#9FB3C8', formatter: '{value}%' }, splitLine: { lineStyle: { color: 'rgba(255,255,255,0.06)' } } },
  series: [{
    type: 'bar', data: [
      { value: 82, itemStyle: { color: '#3B82F6', borderRadius: [6,6,0,0] } },
      { value: 87, itemStyle: { color: '#27C46B', borderRadius: [6,6,0,0] } },
      { value: 79, itemStyle: { color: '#FFAA33', borderRadius: [6,6,0,0] } },
      { value: 76, itemStyle: { color: '#A855F7', borderRadius: [6,6,0,0] } },
      { value: 77, itemStyle: { color: '#FF4D4F', borderRadius: [6,6,0,0] } }
    ]
  }]
}

const ringOption = {
  tooltip: { trigger: 'item' },
  legend: { bottom: 0, textStyle: { color: '#9FB3C8' } },
  series: [{
    type: 'pie', radius: ['50%', '75%'], center: ['50%', '45%'],
    label: { show: false },
    data: [
      { value: 65, name: '优秀(<5%)', itemStyle: { color: '#27C46B' } },
      { value: 25, name: '良好(5-15%)', itemStyle: { color: '#3B82F6' } },
      { value: 10, name: '需改进(>15%)', itemStyle: { color: '#FFAA33' } }
    ]
  }]
}

function formatNum(v: number) { return v?.toLocaleString?.() || '-' }

onMounted(async () => {
  loading.value = true
  try {
    const res = await getPredictionLogs({ page: 1, size: 20 })
    tableData.value = res.data?.records || []
    logCount.value = res.data?.total || 500
  } catch (e) { /* handled */ }
  finally { loading.value = false }
})
</script>

<style scoped lang="scss">
.stat-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 16px; }
.chart-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 16px; height: 320px; }
.chart-row .glass-card { padding: 16px; overflow: auto; }
.chart-row h3 { font-size: 15px; color: #D7E3F4; margin-bottom: 8px; }
.glass-card { padding: 16px; }
</style>
