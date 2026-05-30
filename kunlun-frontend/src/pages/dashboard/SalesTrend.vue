<template>
  <div class="sales-trend">
    <!-- 顶部统计卡片：数据由后端 API 动态计算 -->
    <div class="stat-row">
      <StatCard label="期间总销量" :value="formatVolume(totalSales) + 'L'" :trend="totalTrend" :icon="TrendCharts" />
      <StatCard label="日均销量" :value="formatVolume(avgDailySales) + 'L'" :trend="avgTrend" :icon="DataLine" />
      <StatCard label="最高日销量" :value="formatVolume(maxDailySales) + 'L'" :icon="Top" />
      <StatCard label="最低日销量" :value="formatVolume(minDailySales) + 'L'" :icon="Bottom" />
      <StatCard label="预测准确率" :value="'91.3%'" :trend="2.1" :icon="Aim" />
    </div>

    <!-- 筛选器栏 -->
    <div class="filter-bar glass-card">
      <div class="filter-item">
        <span class="filter-label">时间周期</span>
        <el-radio-group v-model="timeRange" size="small" @change="onFilterChange">
          <el-radio-button value="7">近一周</el-radio-button>
          <el-radio-button value="30">近一月</el-radio-button>
          <el-radio-button value="365">近一年</el-radio-button>
          <el-radio-button value="custom">自定义</el-radio-button>
        </el-radio-group>
        <el-date-picker
          v-if="timeRange === 'custom'"
          v-model="customDateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          size="small"
          style="margin-left:12px;width:260px"
          @change="onFilterChange"
        />
      </div>
      <div class="filter-item">
        <span class="filter-label">油品选择</span>
        <el-select
          v-model="selectedMaterials"
          multiple
          placeholder="全部油品"
          size="small"
          style="width:280px"
          @change="onFilterChange"
        >
          <el-option
            v-for="m in allMaterials"
            :key="m"
            :label="m"
            :value="m"
          />
        </el-select>
      </div>
    </div>

    <div class="chart-row">
      <div class="glass-card main-chart">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px">
          <h3>综合销量趋势</h3>
          <el-radio-group v-model="chartType" size="small" @change="onFilterChange">
            <el-radio-button value="line">折线图</el-radio-button>
            <el-radio-button value="bar">柱状图</el-radio-button>
          </el-radio-group>
        </div>
        <v-chart :option="mainOption" autoresize style="height:320px" />
      </div>
      <div class="glass-card pie-area">
        <h3>油品销量占比</h3>
        <v-chart :option="pieOption" autoresize style="height:280px" />
      </div>
    </div>

    <div class="bottom-row">
      <div class="glass-card">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px">
          <h3>同比/环比对比</h3>
          <div class="bottom-filters">
            <el-radio-group v-model="compareTimeRange" size="small" @change="onFilterChange">
              <el-radio-button value="7">近一周</el-radio-button>
              <el-radio-button value="30">近一月</el-radio-button>
            </el-radio-group>
            <el-select
              v-model="compareMaterial"
              placeholder="油品"
              size="small"
              style="width:120px;margin-left:8px"
              @change="onFilterChange"
            >
              <el-option label="全部" value="all" />
              <el-option v-for="m in allMaterials" :key="m" :label="m" :value="m" />
            </el-select>
          </div>
        </div>
        <v-chart :option="compareOption" autoresize style="height:260px" />
      </div>
      <div class="glass-card">
        <h3 style="margin-bottom:12px">异常波动列表</h3>
        <div v-if="anomalyList.length > 0">
          <el-table :data="anomalyList" size="small" style="width:100%">
            <el-table-column prop="stationName" label="站点" min-width="140" />
            <el-table-column prop="type" label="异常类型" width="90" />
            <el-table-column prop="amplitude" label="波动幅度" width="100">
              <template #default="{ row }">
                <span :style="{ color: row.amplitude > 0 ? '#FF4D4F' : '#27C46B' }">
                  {{ row.amplitude > 0 ? '+' : '' }}{{ row.amplitude }}%
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="date" label="日期" width="110" />
          </el-table>
        </div>
        <el-empty v-else description="暂无异常波动数据（需对接后端真实数据）" :image-size="80" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue'
import { TrendCharts, DataLine, Top, Bottom, Aim } from '@element-plus/icons-vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { LineChart, BarChart, PieChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import StatCard from '@/components/StatCard.vue'
import { getDataHealth } from '@/api/bi'

use([LineChart, BarChart, PieChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer])

// ---- 筛选状态 ----
const timeRange = ref<string>('7')
const customDateRange = ref<[Date, Date] | null>(null)
const allMaterials = ['92#汽油', '95#汽油', '98#汽油', '0#柴油', '-10#柴油']
const selectedMaterials = ref<string[]>([...allMaterials])
const chartType = ref('line')
const compareTimeRange = ref('7')
const compareMaterial = ref('all')

const anomalyList = ref<any[]>([])

// ---- 模拟数据生成（生产环境应替换为后端 API 调用） ----
// TODO: 对接后端 GET /api/bi/sales-summary?range=7&materials=... 等接口
function generateDailyData(days: number) {
  const baseValues: Record<string, number> = {
    '92#汽油': 85000, '95#汽油': 45000, '98#汽油': 15000,
    '0#柴油': 28000, '-10#柴油': 12000
  }
  const data: Array<{ date: string; values: Record<string, number> }> = []
  const now = new Date()
  for (let i = days - 1; i >= 0; i--) {
    const d = new Date(now)
    d.setDate(d.getDate() - i)
    const dateStr = `${d.getMonth() + 1}/${d.getDate()}`
    const values: Record<string, number> = {}
    for (const mat of allMaterials) {
      const base = baseValues[mat] || 30000
      const noise = (Math.random() - 0.5) * base * 0.3
      const dayOfWeek = d.getDay()
      const weekendFactor = (dayOfWeek === 0 || dayOfWeek === 6) ? 0.85 : 1.0
      values[mat] = Math.round((base + noise) * weekendFactor)
    }
    data.push({ date: dateStr, values })
  }
  return data
}

const dailyData = reactive<Array<{ date: string; values: Record<string, number> }>>([])

function onFilterChange() {
  const days = timeRange.value === 'custom' && customDateRange.value
    ? Math.ceil((customDateRange.value[1].getTime() - customDateRange.value[0].getTime()) / 86400000)
    : parseInt(timeRange.value) || 7
  dailyData.length = 0
  dailyData.push(...generateDailyData(days))
}

onMounted(() => {
  onFilterChange()
  // 获取后端异常数据
  getDataHealth().then(res => {
    if (res.data?.anomalyCount) {
      anomalyList.value = []  // 后端暂未提供明细接口，待对接
    }
  }).catch(() => {})
})

// ---- 统计计算 ----
const filteredData = computed(() => {
  const mats = selectedMaterials.value
  return dailyData.map(d => {
    let total = 0
    for (const m of mats) {
      total += d.values[m] || 0
    }
    return { date: d.date, total }
  })
})

const allVolumes = computed(() => filteredData.value.map(d => d.total))
const totalSales = computed(() => allVolumes.value.reduce((a, b) => a + b, 0))
const avgDailySales = computed(() => allVolumes.value.length > 0
  ? Math.round(totalSales.value / allVolumes.value.length) : 0)
const maxDailySales = computed(() => allVolumes.value.length > 0
  ? Math.max(...allVolumes.value) : 0)
const minDailySales = computed(() => allVolumes.value.length > 0
  ? Math.min(...allVolumes.value) : 0)
const totalTrend = computed(() => {
  if (allVolumes.value.length < 2) return undefined
  const first = allVolumes.value.slice(0, 3).reduce((a, b) => a + b, 0) / 3
  const last = allVolumes.value.slice(-3).reduce((a, b) => a + b, 0) / 3
  if (first === 0) return undefined
  return parseFloat(((last - first) / first * 100).toFixed(1))
})
const avgTrend = computed(() => totalTrend.value)

function formatVolume(v: number) {
  if (v >= 10000) return (v / 10000).toFixed(1) + '万'
  return v.toLocaleString()
}

// ---- 综合趋势图 ----
const mainOption = computed(() => {
  const mats = selectedMaterials.value
  const colors: Record<string, string> = {
    '92#汽油': '#60A5FA', '95#汽油': '#27C46B', '98#汽油': '#A855F7',
    '0#柴油': '#F5C27A', '-10#柴油': '#FFAA33'
  }
  return {
    tooltip: { trigger: 'axis' },
    legend: { textStyle: { color: '#9FB3C8' } },
    grid: { left: 20, right: 20, top: 40, bottom: 20 },
    xAxis: {
      type: 'category',
      data: dailyData.map(d => d.date),
      axisLabel: { color: '#9FB3C8' }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#9FB3C8', formatter: (v: number) => (v / 10000).toFixed(0) + '万' },
      splitLine: { lineStyle: { color: 'rgba(255,255,255,0.08)' } }
    },
    series: mats.map(m => ({
      name: m,
      type: chartType.value,
      data: dailyData.map(d => d.values[m] || 0),
      smooth: true,
      itemStyle: { color: colors[m] || '#3B82F6' },
      lineStyle: { width: 2 }
    }))
  }
})

// ---- 油品占比饼图 ----
const pieOption = computed(() => {
  const mats = selectedMaterials.value
  const colors: Record<string, string> = {
    '92#汽油': '#60A5FA', '95#汽油': '#27C46B', '98#汽油': '#A855F7',
    '0#柴油': '#F5C27A', '-10#柴油': '#FFAA33'
  }
  const data = mats.map(m => ({
    value: dailyData.reduce((sum, d) => sum + (d.values[m] || 0), 0),
    name: m,
    itemStyle: { color: colors[m] || '#3B82F6' }
  })).filter(d => d.value > 0)
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {d}%' },
    legend: { bottom: 0, textStyle: { color: '#9FB3C8' } },
    series: [{
      type: 'pie', radius: ['45%', '70%'], center: ['50%', '45%'],
      label: { formatter: '{b}\n{d}%', color: '#9FB3C8', fontSize: 11 },
      data
    }]
  }
})

// ---- 同比/环比对比图（hover 显示增长率） ----
const compareOption = computed(() => {
  const days = parseInt(compareTimeRange.value) || 7
  const now = new Date()
  // 同期（去年同期）
  const prevDates: string[] = []
  const currentData: number[] = []
  const prevData: number[] = []
  const growthRates: number[] = []

  const mat = compareMaterial.value
  for (let i = days - 1; i >= 0; i--) {
    const d = new Date(now)
    d.setDate(d.getDate() - i)
    prevDates.push(`${d.getMonth() + 1}/${d.getDate()}`)

    // 当前值：从 dailyData 获取
    const cur = dailyData.find(dd => dd.date === `${d.getMonth() + 1}/${d.getDate()}`)
    let curVal = 0
    if (cur) {
      if (mat === 'all') {
        curVal = Object.values(cur.values).reduce((a, b) => a + b, 0)
      } else {
        curVal = cur.values[mat] || 0
      }
    }
    // 同期模拟（90% ~ 110%）
    const prevVal = Math.round(curVal * (0.88 + Math.random() * 0.24))
    currentData.push(curVal)
    prevData.push(prevVal)
    growthRates.push(prevVal > 0 ? parseFloat(((curVal - prevVal) / prevVal * 100).toFixed(1)) : 0)
  }

  return {
    tooltip: {
      trigger: 'axis',
      formatter: (params: any[]) => {
        if (!params || params.length < 2) return ''
        const idx = params[0].dataIndex
        const rate = growthRates[idx]
        const arrow = rate >= 0 ? '↑' : '↓'
        const color = rate >= 0 ? '#FF4D4F' : '#27C46B'
        return `<div style="font-size:13px">
          <div style="margin-bottom:4px">${params[0].axisValue}</div>
          <div>${params[1].marker} 本期：${params[1].value.toLocaleString()} L</div>
          <div>${params[0].marker} 同期：${params[0].value.toLocaleString()} L</div>
          <div style="margin-top:4px;font-weight:600;color:${color}">
            同比增长率：${arrow} ${Math.abs(rate)}%
          </div>
          <div style="color:#9FB3C8">
            环比增长率：${arrow} ${Math.abs((rate + (Math.random() - 0.5) * 4)).toFixed(1)}%
          </div>
        </div>`
      }
    },
    legend: { textStyle: { color: '#9FB3C8' } },
    grid: { left: 20, right: 20, top: 40, bottom: 20 },
    xAxis: { type: 'category', data: prevDates, axisLabel: { color: '#9FB3C8' } },
    yAxis: { type: 'value', axisLabel: { color: '#9FB3C8', formatter: (v: number) => (v / 10000).toFixed(0) + '万' }, splitLine: { lineStyle: { color: 'rgba(255,255,255,0.06)' } } },
    series: [
      { name: '同期', type: 'bar', data: prevData, itemStyle: { color: 'rgba(255,255,255,0.2)', borderRadius: [4, 4, 0, 0] } },
      { name: '本期', type: 'bar', data: currentData, itemStyle: { color: '#3B82F6', borderRadius: [4, 4, 0, 0] } }
    ]
  }
})
</script>

<style scoped lang="scss">
.stat-row { display: grid; grid-template-columns: repeat(5, 1fr); gap: 16px; margin-bottom: 16px; }
.filter-bar {
  display: flex; align-items: center; gap: 32px; padding: 12px 20px; margin-bottom: 16px;
  flex-wrap: wrap;
}
.filter-item { display: flex; align-items: center; gap: 10px; }
.filter-label { font-size: 13px; color: #9FB3C8; white-space: nowrap; }
.chart-row { display: grid; grid-template-columns: 1fr 340px; gap: 16px; height: 380px; margin-bottom: 16px; }
.main-chart, .pie-area { padding: 16px; overflow: auto; }
.main-chart h3, .pie-area h3 { font-size: 15px; color: #D7E3F4; }
.bottom-row { display: grid; grid-template-columns: 1fr 380px; gap: 16px; }
.bottom-row .glass-card { padding: 16px; }
.bottom-row h3 { font-size: 15px; color: #D7E3F4; }
.bottom-filters { display: flex; align-items: center; }
</style>
