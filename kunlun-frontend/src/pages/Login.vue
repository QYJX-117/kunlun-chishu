<template>
  <div class="login-page">
    <!-- 动态粒子背景 -->
    <canvas ref="canvasRef" class="particles" />

    <!-- 登录卡片 -->
    <div class="login-card">
      <div class="card-glow" />
      <div class="card-inner">
        <!-- Logo 区 -->
        <div class="logo-area">
          <div class="logo-icon">
            <span class="logo-mountain">&#9650;</span>
            <span class="logo-peak">&#9650;</span>
            <span class="logo-base">&#9645;</span>
          </div>
          <h1 class="sys-title gold-text">昆仑驰枢</h1>
          <p class="sys-subtitle">成品油智能补货与供应链协同平台</p>
        </div>

        <!-- 表单 -->
        <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="用户名"
              size="large"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            进入系统
          </el-button>
        </el-form>

        <p class="login-hint">演示账号：dispatcher / 123456</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const canvasRef = ref<HTMLCanvasElement>()
const loading = ref(false)
const formRef = ref()
let animationId = 0

const form = reactive({ username: 'dispatcher', password: '123456' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// ===== 粒子动画系统 =====
interface Particle {
  x: number; y: number; vx: number; vy: number; size: number; opacity: number
}
const particles: Particle[] = []
const PARTICLE_COUNT = 80

function initCanvas() {
  const canvas = canvasRef.value
  if (!canvas) return
  canvas.width = window.innerWidth
  canvas.height = window.innerHeight
  const ctx = canvas.getContext('2d')
  if (!ctx) return

  for (let i = 0; i < PARTICLE_COUNT; i++) {
    particles.push({
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      vx: (Math.random() - 0.5) * 0.5,
      vy: (Math.random() - 0.5) * 0.5,
      size: Math.random() * 2 + 0.5,
      opacity: Math.random() * 0.5 + 0.1
    })
  }

  function draw() {
    ctx!.clearRect(0, 0, canvas!.width, canvas!.height)

    // 画连线
    for (let i = 0; i < particles.length; i++) {
      for (let j = i + 1; j < particles.length; j++) {
        const dx = particles[i].x - particles[j].x
        const dy = particles[i].y - particles[j].y
        const dist = Math.sqrt(dx * dx + dy * dy)
        if (dist < 120) {
          ctx!.beginPath()
          ctx!.strokeStyle = `rgba(245,194,122,${0.12 * (1 - dist / 120)})`
          ctx!.lineWidth = 0.5
          ctx!.moveTo(particles[i].x, particles[i].y)
          ctx!.lineTo(particles[j].x, particles[j].y)
          ctx!.stroke()
        }
      }
    }

    // 画粒子
    for (const p of particles) {
      p.x += p.vx
      p.y += p.vy
      if (p.x < 0 || p.x > canvas!.width) p.vx *= -1
      if (p.y < 0 || p.y > canvas!.height) p.vy *= -1

      ctx!.beginPath()
      ctx!.arc(p.x, p.y, p.size, 0, Math.PI * 2)
      ctx!.fillStyle = `rgba(96,165,250,${p.opacity})`
      ctx!.fill()

      // 发光效果
      ctx!.beginPath()
      ctx!.arc(p.x, p.y, p.size * 3, 0, Math.PI * 2)
      ctx!.fillStyle = `rgba(245,194,122,${p.opacity * 0.2})`
      ctx!.fill()
    }

    animationId = requestAnimationFrame(draw)
  }
  draw()
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  // 模拟登录，直接放行
  setTimeout(() => {
    ElMessage.success('欢迎使用昆仑驰枢')
    router.push('/dashboard/map')
    loading.value = false
  }, 800)
}

onMounted(initCanvas)
onBeforeUnmount(() => cancelAnimationFrame(animationId))
</script>

<style scoped lang="scss">
.login-page {
  width: 100vw; height: 100vh;
  display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #050F1E 0%, #071B34 30%, #0A2342 60%, #071B34 100%);
  overflow: hidden; position: relative;

  // 背景光晕
  &::before {
    content: '';
    position: absolute; width: 600px; height: 600px;
    background: radial-gradient(circle, rgba(245,194,122,0.06) 0%, transparent 70%);
    top: -200px; left: -100px; border-radius: 50%;
  }
  &::after {
    content: '';
    position: absolute; width: 500px; height: 500px;
    background: radial-gradient(circle, rgba(59,130,246,0.06) 0%, transparent 70%);
    bottom: -150px; right: -100px; border-radius: 50%;
  }
}
.particles { position: absolute; top: 0; left: 0; z-index: 0; }

.login-card {
  position: relative; z-index: 1; width: 420px;
  .card-glow {
    position: absolute; inset: -2px; border-radius: 20px;
    background: linear-gradient(135deg, rgba(245,194,122,0.15), rgba(59,130,246,0.1), rgba(245,194,122,0.05));
    filter: blur(2px);
  }
  .card-inner {
    position: relative;
    background: rgba(11,33,58,0.85);
    backdrop-filter: blur(20px);
    border-radius: 20px; padding: 44px 40px 32px;
    border: 1px solid rgba(255,255,255,0.08);
  }
}

.logo-area { text-align: center; margin-bottom: 32px; }
.logo-icon {
  display: flex; align-items: flex-end; justify-content: center; gap: 6px; margin-bottom: 16px;
  .logo-mountain, .logo-peak, .logo-base {
    display: block; color: #F5C27A;
    text-shadow: 0 0 20px rgba(245,194,122,0.4);
  }
  .logo-mountain { font-size: 48px; line-height: 1; }
  .logo-peak { font-size: 36px; line-height: 1; opacity: 0.8; }
  .logo-base { font-size: 28px; line-height: 1; opacity: 0.6; }
}
.sys-title { font-size: 32px; font-weight: 700; letter-spacing: 6px; }
.sys-subtitle { font-size: 13px; color: #9FB3C8; margin-top: 6px; letter-spacing: 2px; }

.login-form {
  .el-input :deep(.el-input__wrapper) {
    background: rgba(255,255,255,0.04); border-radius: 10px;
    box-shadow: 0 0 0 1px rgba(255,255,255,0.08) inset !important;
    padding: 4px 12px;
  }
  .el-input :deep(.el-input__wrapper:hover) {
    box-shadow: 0 0 0 1px rgba(245,194,122,0.3) inset !important;
  }
  .el-form-item { margin-bottom: 18px; }
}

.login-btn {
  width: 100%; height: 44px; border-radius: 10px; font-size: 16px; letter-spacing: 4px;
  margin-top: 8px;
  background: linear-gradient(90deg, #F5C27A, #FFB357) !important;
  border: none !important; color: #071B34 !important; font-weight: 700;
  transition: all 0.3s;
  &:hover { box-shadow: 0 0 24px rgba(245,194,122,0.4); transform: translateY(-1px); }
}

.login-hint { text-align: center; color: #60758A; font-size: 12px; margin-top: 20px; }
</style>
