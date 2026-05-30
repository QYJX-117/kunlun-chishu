<template>
  <div class="user-page">
    <div class="glass-card">
      <div style="display:flex;gap:12px;margin-bottom:12px">
        <el-input v-model="searchUsername" placeholder="用户名" style="width:200px" clearable @change="loadData" />
        <el-select v-model="searchRole" placeholder="角色" style="width:140px" clearable @change="loadData">
          <el-option label="调度员" value="调度员" /><el-option label="运营经理" value="运营经理" />
          <el-option label="系统管理员" value="系统管理员" /><el-option label="站长" value="站长" />
        </el-select>
        <el-button type="primary" @click="openDrawer(null)">新增用户</el-button>
      </div>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="role" label="角色" width="100" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status==='启用'?'success':'danger'" size="small">{{ row.status }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" link @click="openDrawer(row)">编辑</el-button>
            <el-button size="small" link type="danger" @click="del(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-drawer v-model="drawerVisible" :title="editId ? '编辑用户' : '新增用户'" size="480px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" :placeholder="editId ? '不修改请留空' : '请输入密码'" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="角色"><el-select v-model="form.role" style="width:100%"><el-option label="调度员" value="调度员" /><el-option label="运营经理" value="运营经理" /><el-option label="系统管理员" value="系统管理员" /><el-option label="站长" value="站长" /></el-select></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" active-value="启用" inactive-value="禁用" /></el-form-item>
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
import { userApi } from '@/api/base'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref<any[]>([])
const searchUsername = ref('')
const searchRole = ref('')
const drawerVisible = ref(false)
const editId = ref<number | null>(null)
const form = reactive({ username: '', password: '', realName: '', role: '调度员', phone: '', status: '启用' })

async function loadData() {
  loading.value = true
  try {
    const res = await userApi.page({ page: 1, size: 50, username: searchUsername.value || undefined, role: searchRole.value || undefined })
    tableData.value = res.data?.records || []
  } catch (e) { /* handled */ } finally { loading.value = false }
}

function openDrawer(row: any) {
  if (row) { editId.value = row.id; Object.assign(form, { ...row, password: '' }) }
  else { editId.value = null; Object.assign(form, { username: '', password: '', realName: '', role: '调度员', phone: '', status: '启用' }) }
  drawerVisible.value = true
}

async function save() {
  try {
    if (editId.value) {
      const data: any = { ...form }
      if (!data.password) delete data.password
      await userApi.update(editId.value, data)
    } else {
      await userApi.save(form)
    }
    ElMessage.success('保存成功')
    drawerVisible.value = false
    loadData()
  } catch (e) { /* handled */ }
}

async function del(id: number) {
  try { await ElMessageBox.confirm('确定删除？', '删除确认'); await userApi.delete(id); ElMessage.success('删除成功'); loadData() }
  catch (e) { /* handled */ }
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.glass-card { padding: 16px; }
</style>
