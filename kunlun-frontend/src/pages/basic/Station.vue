<template>
  <div class="station-page">
    <div class="stat-row">
      <StatCard label="站点总数" :value="total" :icon="Shop" icon-bg="rgba(59,130,246,0.15)" />
      <StatCard label="营业中" :value="18" :icon="CircleCheck" icon-bg="rgba(39,196,107,0.15)" />
      <StatCard label="油库" :value="2" :icon="HomeFilled" icon-bg="rgba(255,170,51,0.15)" />
    </div>

    <div class="glass-card">
      <div style="display:flex;gap:12px;margin-bottom:12px">
        <el-input v-model="searchName" placeholder="站点名称/编码" style="width:200px" clearable @change="loadData" />
        <el-select v-model="searchArea" placeholder="区域" style="width:120px" clearable @change="loadData">
          <el-option label="兰州" value="兰州" /><el-option label="白银" value="白银" />
        </el-select>
        <el-button type="primary" @click="openDrawer(null)">新增站点</el-button>
        <el-button type="danger" plain :disabled="!selected.length" @click="batchDelete">批量删除</el-button>
      </div>
      <el-table :data="tableData" stripe v-loading="loading" @selection-change="(val: any) => selected = val">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="code" label="编码" width="120" />
        <el-table-column prop="name" label="站点名称" min-width="180" />
        <el-table-column prop="area" label="区域" width="80" />
        <el-table-column prop="stationType" label="类型" width="100" />
        <el-table-column prop="grade" label="等级" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status==='营业中'?'success':'info'" size="small">{{ row.status }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link @click="openDrawer(row)">编辑</el-button>
            <el-button size="small" link type="danger" @click="del(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:12px;text-align:right">
        <el-pagination v-model:current-page="page" :total="total" :page-size="20" layout="prev, pager, next" @change="loadData" />
      </div>
    </div>

    <el-drawer v-model="drawerVisible" :title="editId ? '编辑站点' : '新增站点'" size="480px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="编码"><el-input v-model="form.code" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="区域"><el-select v-model="form.area" style="width:100%"><el-option label="兰州" value="兰州" /><el-option label="白银" value="白银" /></el-select></el-form-item>
        <el-form-item label="经度"><el-input-number v-model="form.longitude" :precision="7" style="width:100%" /></el-form-item>
        <el-form-item label="纬度"><el-input-number v-model="form.latitude" :precision="7" style="width:100%" /></el-form-item>
        <el-form-item label="类型"><el-select v-model="form.stationType" style="width:100%"><el-option label="加油站" value="加油站" /><el-option label="油库" value="油库" /></el-select></el-form-item>
        <el-form-item label="等级"><el-select v-model="form.grade" style="width:100%"><el-option label="一级" value="一级" /><el-option label="二级" value="二级" /><el-option label="三级" value="三级" /></el-select></el-form-item>
        <el-form-item label="服务水平"><el-input-number v-model="form.serviceLevel" :min="0.8" :max="0.999" :step="0.01" style="width:100%" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" active-value="营业中" inactive-value="停业" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="drawerVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Shop, CircleCheck, HomeFilled } from '@element-plus/icons-vue'
import StatCard from '@/components/StatCard.vue'
import { stationApi } from '@/api/base'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const page = ref(1)
const total = ref(0)
const tableData = ref<any[]>([])
const selected = ref<any[]>([])
const searchName = ref('')
const searchArea = ref('')
const drawerVisible = ref(false)
const editId = ref<number | null>(null)

const form = reactive({
  code: '', name: '', area: '兰州', longitude: 103.8, latitude: 36.0,
  stationType: '加油站', grade: '二级', serviceLevel: 0.95, status: '营业中'
})

async function loadData() {
  loading.value = true
  try {
    const res = await stationApi.page({ page: page.value, size: 20, name: searchName.value || undefined, area: searchArea.value || undefined })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) { /* handled */ } finally { loading.value = false }
}

function openDrawer(row: any) {
  if (row) {
    editId.value = row.id
    Object.assign(form, row)
  } else {
    editId.value = null
    Object.assign(form, { code: '', name: '', area: '兰州', longitude: 103.8, latitude: 36.0, stationType: '加油站', grade: '二级', serviceLevel: 0.95, status: '营业中' })
  }
  drawerVisible.value = true
}

async function save() {
  try {
    if (editId.value) {
      await stationApi.update(editId.value, form)
    } else {
      await stationApi.save(form)
    }
    ElMessage.success('保存成功')
    drawerVisible.value = false
    loadData()
  } catch (e) { /* handled */ }
}

async function del(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该站点？', '删除确认')
    await stationApi.delete(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { /* handled */ }
}

async function batchDelete() {
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selected.value.length} 个站点？`, '批量删除')
    for (const s of selected.value) await stationApi.delete(s.id)
    ElMessage.success('批量删除完成')
    loadData()
  } catch (e) { /* handled */ }
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.stat-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 16px; }
.glass-card { padding: 16px; }
</style>
