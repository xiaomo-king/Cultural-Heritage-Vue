<template>
  <div class="login-page">
    <!-- 全屏背景图 -->
    <div class="bg-image"></div>
    <div class="bg-overlay"></div>

    <!-- 文化装饰元素 -->
    <div class="culture-element culture-1">🏯</div>
    <div class="culture-element culture-2">🏺</div>
    <div class="culture-element culture-3">🎭</div>
    <div class="culture-element culture-4">🌊</div>

    <!-- 左下角品牌文案 -->
    <div class="brand-section">
      <div class="brand-line"></div>
      <div class="brand-text">
        <div class="brand-subtitle">传承千年匠心</div>
        <div class="brand-subtitle">守护江西非遗文化</div>
      </div>
    </div>

    <!-- 右侧：登录卡片 -->
    <div class="login-card-wrapper">
      <div class="login-card">
        <!-- 印章 Logo -->
        <div class="seal-logo">
          <div class="seal-outer">
            <div class="seal-inner">
              <span class="seal-char">拾</span>
            </div>
          </div>
        </div>
        <div class="seal-text">江右拾遗 · 助力江西非遗传承</div>

        <div class="card-header">
          <h2 class="card-title">欢迎登录</h2>
          <p class="card-subtitle">探索江西传统文化之美</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <div class="input-wrapper">
              <span class="input-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#8B7D6B" stroke-width="1.5"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
              </span>
              <input
                v-model="form.username"
                placeholder="请输入账号"
                class="custom-input"
                @input="clearError('username')"
              />
            </div>
            <div class="input-error" v-if="errors.username">{{ errors.username }}</div>
          </el-form-item>

          <el-form-item prop="password">
            <div class="input-wrapper">
              <span class="input-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#8B7D6B" stroke-width="1.5"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
              </span>
              <input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入密码"
                class="custom-input"
                @input="clearError('password')"
              />
              <span class="toggle-pwd" @click="showPassword = !showPassword">
                <svg v-if="showPassword" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#8B7D6B" stroke-width="1.5"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
                <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#8B7D6B" stroke-width="1.5"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
              </span>
            </div>
            <div class="input-error" v-if="errors.password">{{ errors.password }}</div>
          </el-form-item>

          <el-form-item>
            <button
              class="submit-btn"
              :class="{ loading: loading }"
              :disabled="loading"
              @click.prevent="handleLogin"
            >
              <span v-if="!loading">进入非遗世界</span>
              <span v-else class="loading-text">登录中...</span>
            </button>
          </el-form-item>
        </el-form>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const showPassword = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const errors = reactive({
  username: '',
  password: ''
})

function clearError(field) {
  errors[field] = ''
}

async function handleLogin() {
  if (!form.username.trim()) {
    errors.username = '请输入用户名'
    return
  }
  if (!form.password) {
    errors.password = '请输入密码'
    return
  }
  loading.value = true
  try {
    await userStore.login(form.username, form.password)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ===== 全局 ===== */
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;600;700;900&family=Noto+Sans+SC:wght@300;400;500;600&display=swap');

.login-page {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  font-family: 'Noto Sans SC', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* ===== 全屏背景图 ===== */
.bg-image {
  position: absolute;
  inset: 0;
  background: url('/uploads/images/start/admin.jpg') center/cover no-repeat;
}

.bg-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    135deg,
    rgba(245, 240, 235, 0.15) 0%,
    rgba(245, 240, 235, 0.05) 50%,
    rgba(245, 240, 235, 0.2) 100%
  );
}

/* 文化装饰元素 */
.culture-element {
  position: absolute;
  opacity: 0.12;
  font-size: 64px;
  pointer-events: none;
  animation: float 6s ease-in-out infinite;
}
.culture-1 { top: 10%; left: 12%; animation-delay: 0s; }
.culture-2 { bottom: 30%; left: 8%; animation-delay: 1.5s; font-size: 52px; }
.culture-3 { top: 40%; left: 18%; animation-delay: 3s; font-size: 48px; }
.culture-4 { bottom: 15%; left: 20%; animation-delay: 4.5s; }

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-12px) rotate(3deg); }
}

/* ===== 左下角品牌文案 ===== */
.brand-section {
  position: absolute;
  bottom: 60px;
  left: 50px;
  display: flex;
  align-items: flex-start;
  gap: 20px;
  z-index: 2;
}

.brand-line {
  width: 3px;
  height: 80px;
  background: linear-gradient(to bottom, rgba(139, 26, 26, 0.7), rgba(139, 26, 26, 0.1));
  border-radius: 2px;
  flex-shrink: 0;
  margin-top: 4px;
}

.brand-subtitle {
  font-family: 'Noto Serif SC', 'STSong', serif;
  font-size: 22px;
  font-weight: 600;
  color: #2c1810;
  letter-spacing: 6px;
  line-height: 1.6;
  text-shadow: 0 1px 3px rgba(255, 255, 255, 0.5);
}

.brand-subtitle:last-child {
  font-weight: 400;
  font-size: 16px;
  color: #6b5a4a;
  letter-spacing: 4px;
}

/* ===== 右侧登录卡片 ===== */
.login-card-wrapper {
  position: absolute;
  top: 50%;
  right: 8%;
  transform: translateY(-50%);
  z-index: 2;
}

/* ===== 登录卡片 ===== */
.login-card {
  width: 420px;
  padding: 48px 44px;
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow:
    0 8px 32px rgba(139, 26, 26, 0.08),
    0 2px 8px rgba(0, 0, 0, 0.04),
    inset 0 1px 0 rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.5);
  position: relative;
}

/* 印章 Logo */
.seal-logo {
  display: flex;
  justify-content: center;
  margin-bottom: 12px;
}

.seal-outer {
  width: 72px;
  height: 72px;
  border: 3px solid #B83B2D;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.seal-outer::before {
  content: '';
  position: absolute;
  inset: -2px;
  border: 1px solid rgba(184, 59, 45, 0.3);
  border-radius: 50%;
}

.seal-inner {
  width: 56px;
  height: 56px;
  background: #B83B2D;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.seal-char {
  font-family: 'Noto Serif SC', serif;
  font-size: 26px;
  font-weight: 900;
  color: #fff;
  letter-spacing: 0;
}

.seal-text {
  text-align: center;
  font-size: 13px;
  color: #8B7D6B;
  letter-spacing: 2px;
  margin-bottom: 28px;
  font-weight: 400;
}

/* 卡片头部 */
.card-header {
  text-align: center;
  margin-bottom: 32px;
}

.card-title {
  font-family: 'Noto Serif SC', 'STSong', serif;
  font-size: 28px;
  font-weight: 700;
  color: #2c1810;
  margin: 0 0 8px 0;
  letter-spacing: 3px;
}

.card-subtitle {
  font-size: 14px;
  color: #8B7D6B;
  margin: 0;
  letter-spacing: 2px;
  font-weight: 300;
}

/* ===== 表单样式 ===== */
.login-form {
  margin-top: 8px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.login-form :deep(.el-form-item__content) {
  flex-wrap: wrap;
}

/* 自定义输入框 */
.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  height: 52px;
  background: #f8f5f0;
  border: 1.5px solid #e0d5c8;
  border-radius: 12px;
  transition: all 0.3s ease;
  padding: 0 14px;
}

.input-wrapper:focus-within {
  border-color: #B83B2D;
  box-shadow: 0 0 0 3px rgba(184, 59, 45, 0.1);
  background: #fff;
}

.input-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  margin-right: 10px;
  flex-shrink: 0;
}

.custom-input {
  flex: 1;
  height: 100%;
  border: none;
  outline: none;
  background: transparent;
  font-size: 15px;
  color: #2c1810;
  font-family: 'Noto Sans SC', sans-serif;
}

.custom-input::placeholder {
  color: #b5a89a;
  font-weight: 300;
}

.toggle-pwd {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px;
  flex-shrink: 0;
  opacity: 0.5;
  transition: opacity 0.2s;
}

.toggle-pwd:hover {
  opacity: 1;
}

/* 错误提示 */
.input-error {
  font-size: 12px;
  color: #B83B2D;
  margin-top: 6px;
  padding-left: 4px;
}

/* 提交按钮 */
.submit-btn {
  width: 100%;
  height: 52px;
  background: linear-gradient(135deg, #B83B2D 0%, #C41E3A 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 17px;
  font-weight: 500;
  letter-spacing: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  font-family: 'Noto Sans SC', sans-serif;
}

.submit-btn::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #C41E3A 0%, #B83B2D 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.submit-btn:hover::before {
  opacity: 1;
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(184, 59, 45, 0.3);
}

.submit-btn:active {
  transform: translateY(0);
}

.submit-btn span {
  position: relative;
  z-index: 1;
}

.submit-btn.loading {
  opacity: 0.85;
  cursor: not-allowed;
}

.loading-text {
  letter-spacing: 2px;
}

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .login-card-wrapper { right: 5%; }
  .login-card { width: 380px; padding: 40px 36px; }
}

@media (max-width: 768px) {
  .login-card-wrapper {
    right: auto;
    left: 50%;
    transform: translate(-50%, -50%);
  }
  .login-card { width: 90%; max-width: 420px; padding: 36px 28px; }
}
</style>
