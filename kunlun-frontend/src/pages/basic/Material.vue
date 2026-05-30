<template>
  <div class="material-page">
    <div class="glass-card">
      <div style="display:flex;gap:12px;margin-bottom:12px">
        <el-input v-model="searchName" placeholder="油品名称" style="width:200px" clearable @change="loadData" />
        <el-select v-model="searchType" placeholder="类型" style="width:120px" clearable @change="loadData">
          <el-option label="汽油" value="汽油" /><el-option label="柴油" value="柴油" />
        </el-select>
        <el-button type="primary" @click="openDrawer(null)">新增油品</el-button>
      </div>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="code" label="编码" width="100" />
        <el-table-column prop="name" label="名称" width="120" />
        <el-table-column prop="type" label="类型" width="80" />
        <el-table-column prop="density" label="密度(kg/L)" width="100" />
        <el-table-column prop="leadTime" label="提前期(天)" width="100" />
        <el-table-column prop="leadTimeStd" label="到货期标准差" width="120" />
        <el-table-column prop="pricePerLiter" label="单价(元/L)" width="100" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" link @click="openDrawer(row)">编辑</el-button>
            <el-button size="small" link type="danger" @click="del(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-drawer v-model="drawerVisible" :title="editId ? '编辑油品' : '新增油品'" size="480px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="编码"><el-input v-model="form.code" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="类型"><el-select v-model="form.type" style="width:100%"><el-option label="汽油" value="汽油" /><el-option label="柴油" value="柴油" /></el-select></el-form-item>
        <el-form-item label="密度(kg/L)"><el-input-number v-model="form.density" :precision="2" :step="0.01" style="width:100%" /></el-form-item>
        <el-form-item label="提前期(天)"><el-input-number v-model="form.leadTime" :precision="1" style="width:100%" /></el-form-item>
        <el-form-item label="到货期标准差"><el-input-number v-model="form.leadTimeStd" :precision="1" style="width:100%" /></el-form-item>
        <el-form-item label="单价(元/L)"><el-input-number v-model="form.pricePerLiter" :precision="2" style="width:100%" /></el-form-item>
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
import { materialApi } from '@/api/base'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref<any[]>([])
const searchName = ref('')
const searchType = ref('')
const drawerVisible = ref(false)
const editId = ref<number | null>(null)
const form = reactive({ code: '', name: '', type: '汽油', density: 0.73, unit: '升', leadTime: 2.0, leadTimeStd: 0.5, pricePerLiter: 6.75 })

async function loadData() {
  loading.value = true
  try {
    const res = await materialApi.page({ page: 1, size: 50, name: searchName.value || undefined, type: searchType.value || undefined })
    tableData.value = res.data?.records || []
  } catch (e) { /* handled */ } finally { loading.value = false }
}

function openDrawer(row: any) {
  if (row) { editId.value = row.id; Object.assign(form, row) }
  else { editId.value = null; Object.assign(form, { code: '', name: '', type: '汽油', density: 0.73, unit: '升', leadTime: 2.0, leadTimeStd: 0.5, pricePerLiter: 6.75 }) }
  drawerVisible.value = true
}

async function save() {
  try {
    if (editId.value) await materialApi.update(editId.value, form)
    else await materialApi.save(form)
    ElMessage.success('保存成功')
    drawerVisible.value = false
    loadData()
  } catch (e) { /* handled */ }
}

async function del(id: number) {
  try { await ElMessageBox.confirm('确定删除？', '删除确认'); await materialApi.delete(id); ElMessage.success('删除成功'); loadData() }
  catch (e) { /* handled */ }
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.glass-card { padding: 16px; }
</style>
