<template>
  <div class="top-header">
    <div class="header-left">
      <div class="logo-mark">
        <span class="m1">&#9650;</span><span class="m2">&#9650;</span><span class="m3">&#9645;</span>
      </div>
      <div class="logo-words">
        <span class="logo-text gold-text">昆仑驰枢</span>
        <span class="subtitle">成品油智能补货与供应链协同平台</span>
      </div>
    </div>
    <div class="header-right">
      <el-dropdown @command="handleCommand">
        <span class="user-info">
          <el-avatar :size="32" style="background:#3B82F6">我</el-avatar>
          <span class="user-name">我的</span>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'

const router = useRouter()

function handleCommand(cmd: string) {
  if (cmd === 'logout') {
    ElMessageBox.confirm(
      '确定要退出登录吗？',
      '退出确认',
      {
        confirmButtonText: '确定退出',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      router.push('/login')
    }).catch(() => {})
  }
}
</script>

<style scoped lang="scss">
.top-header {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: rgba(7, 27, 52, 0.95);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  flex-shrink: 0;
}
.header-left { display: flex; align-items: center; gap: 12px; }
.logo-mark {
  display: flex; align-items: flex-end; gap: 2px; line-height: 1;
  .m1 { color: #F5C27A; font-size: 24px; text-shadow: 0 0 8px rgba(245,194,122,0.3); }
  .m2 { color: #F5C27A; font-size: 18px; opacity: 0.8; }
  .m3 { color: #F5C27A; font-size: 14px; opacity: 0.6; }
}
.logo-words { display: flex; flex-direction: column; }
.logo-text { font-size: 18px; font-weight: 700; letter-spacing: 3px; }
.subtitle { font-size: 11px; color: #60758A; }
.header-right { display: flex; align-items: center; gap: 20px; }
.user-info { display: flex; align-items: center; gap: 8px; cursor: pointer; }
.user-name { color: #D7E3F4; font-size: 14px; }
</style>
