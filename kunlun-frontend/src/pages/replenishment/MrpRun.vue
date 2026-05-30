<template>
  <div class="mrp-run">
    <!-- 步骤条 -->
    <div class="steps-bar glass-card">
      <el-steps :active="activeStep" align-center>
        <el-step title="数据清洗" description="异常值检测与缺失补全" />
        <el-step title="历史数据校验" description="3σ原则验证" />
        <el-step title="算法选择与预测" description="指数平滑自动选优" />
        <el-step title="安全库存计算" description="工业级公式" />
        <el-step title="生成补货建议" description="净需求计算" />
        <el-step title="结果确认" description="生成订单" />
      </el-steps>
    </div>

    <div class="content-row">
      <!-- 左侧：参数选择 -->
      <div class="left-panel">
        <div class="glass-card">
          <h3>运行参数</h3>
          <el-form label-width="100px" label-position="top" style="margin-top:12px">
            <el-form-item label="选择站点">
              <el-select v-model="form.stationId" placeholder="请选择站点" style="width:100%">
                <el-option v-for="s in stations" :key="s.id" :label="s.name" :value="s.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="选择油品">
              <el-select v-model="form.materialId" placeholder="请选择油品" style="width:100%">
                <el-option v-for="m in materials" :key="m.id" :label="m.name" :value="m.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="预测天数">
              <el-input-number v-model="form.forecastDays" :min="1" :max="30" style="width:100%" />
            </el-form-item>
            <el-button type="primary" size="large" style="width:100%" :loading="running" @click="runMrp">
              启动MRP运算
            </el-button>
          </el-form>
        </div>

        <!-- 算法选择卡片 -->
        <div class="glass-card" style="margin-top:12px">
          <h3>预测算法</h3>
          <div class="algo-cards">
            <div v-for="a in algorithms" :key="a.name" class="algo-card" :class="{ active: selectedAlgo === a.name }" @click="selectedAlgo = a.name">
              <div class="algo-name">{{ a.name }}</div>
              <div class="algo-desc">{{ a.desc }}</div>
              <el-tag v-if="a.recommended" type="warning" size="small">推荐</el-tag>
            </div>
          </div>
        </div>

        <!-- 安全库存公式 -->
        <div class="glass-card" style="margin-top:12px">
          <h3>安全库存公式</h3>
          <div class="formula-display">
            Is = h × √(T × σo² + Oa² × σT²)
          </div>
          <div class="formula-params">
            <div>h = 安全系数 (95% → 1.645)</div>
            <div>T = 平均到货期(天)</div>
            <div>σo = 出库量标准差</div>
            <div>σT = 到货期标准差</div>
          </div>
        </div>
      </div>

      <!-- 右侧：结果 -->
      <div class="right-panel">
        <div class="glass-card result-dashboard">
          <h3>建议补货量</h3>
          <div class="gauge-area" v-if="result">
            <div class="big-number gold-text">{{ result.suggestedQuantity ? result.suggestedQuantity.toLocaleString() : '0' }} L</div>
            <div class="sub-info">
              <span>预测需求：{{ result.forecastValue?.toLocaleString() || '-' }} L</span>
              <span>安全库存：{{ result.safetyStock?.toLocaleString() || '-' }} L</span>
              <span>净需求：{{ result.netRequirement?.toLocaleString() || '-' }} L</span>
            </div>
            <div v-if="result.orderGenerated" style="margin-top:12px">
              <el-tag type="success" size="large">已生成补货订单 #{{ result.orderId }}</el-tag>
            </div>
            <div v-else-if="result" style="margin-top:12px">
              <el-tag type="info" size="large">库存充足，无需补货</el-tag>
            </div>
          </div>
          <el-empty v-else description="尚未运行MRP" :image-size="80" />
        </div>

        <div class="glass-card" style="margin-top:12px">
          <h3>影响因素</h3>
          <el-descriptions :column="1" size="small" border>
            <el-descriptions-item label="预测方法">{{ result?.predictionMethod || '-' }}</el-descriptions-item>
            <el-descriptions-item label="最优α值">{{ result?.predictionParams || '-' }}</el-descriptions-item>
            <el-descriptions-item label="MSE">{{ result?.mse || '-' }}</el-descriptions-item>
            <el-descriptions-item label="当前库存">{{ result?.currentStock?.toLocaleString() || '-' }} L</el-descriptions-item>
            <el-descriptions-item label="到货日期">{{ result?.requiredDate || '-' }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { runMrp as runMrpApi } from '@/api/mrp'
import { stationApi, materialApi } from '@/api/base'

const activeStep = ref(0)
const running = ref(false)
const selectedAlgo = ref('SES')
const result = ref<any>(null)
const stations = ref<any[]>([])
const materials = ref<any[]>([])

const form = reactive({ stationId: 1, materialId: 1, forecastDays: 3 })

const algorithms = [
  { name: 'SES', desc: '一次指数平滑', recommended: true },
  { name: 'DES', desc: '二次指数平滑', recommended: false },
  { name: 'ARIMA', desc: '自回归积分移动平均', recommended: false },
  { name: 'LSTM', desc: '长短期记忆网络', recommended: false },
  { name: 'Prophet', desc: 'Meta时序预测', recommended: false }
]

async function runMrp() {
  running.value = true
  activeStep.value = 0
  try {
    for (let i = 1; i <= 5; i++) {
      await new Promise(r => setTimeout(r, 400))
      activeStep.value = i
    }
    const res = await runMrpApi({
      stationId: form.stationId,
      materialId: form.materialId,
      forecastDays: form.forecastDays
    })
    result.value = res.data
    activeStep.value = 6
  } catch (e) { /* handled */ }
  finally { running.value = false }
}

onMounted(async () => {
  try {
    const [sRes, mRes] = await Promise.all([
      stationApi.page({ page: 1, size: 50 }),
      materialApi.page({ page: 1, size: 20 })
    ])
    stations.value = sRes.data?.records || []
    materials.value = mRes.data?.records || []
  } catch (e) { /* handled */ }
})
</script>

<style scoped lang="scss">
.steps-bar { padding: 24px 40px; margin-bottom: 16px; }
.content-row { display: grid; grid-template-columns: 360px 1fr; gap: 16px; }
.left-panel { display: flex; flex-direction: column; }
.left-panel .glass-card { padding: 16px; }
.left-panel h3 { font-size: 15px; color: #D7E3F4; }
.right-panel .glass-card { padding: 16px; }
.right-panel h3 { font-size: 15px; color: #D7E3F4; margin-bottom: 12px; }
.algo-cards { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 8px; }
.algo-card {
  padding: 10px 12px; border-radius: 8px; cursor: pointer;
  border: 1px solid rgba(255,255,255,0.08); transition: all 0.2s;
  background: rgba(255,255,255,0.03); font-size: 13px;
  &:hover, &.active { border-color: #F5C27A; background: rgba(245,194,122,0.1); }
}
.algo-name { color: #F5F7FA; font-weight: 600; }
.algo-desc { color: #9FB3C8; font-size: 11px; margin-top: 2px; }
.formula-display {
  background: rgba(255,255,255,0.04); padding: 14px; border-radius: 8px;
  font-family: monospace; font-size: 15px; color: #F5C27A; text-align: center; margin: 12px 0;
}
.formula-params { font-size: 12px; color: #9FB3C8; line-height: 1.8; }
.big-number { font-size: 42px; font-weight: 700; text-align: center; margin: 16px 0; }
.sub-info { display: flex; justify-content: space-around; font-size: 13px; color: #D7E3F4; margin-bottom: 12px; }
</style>
