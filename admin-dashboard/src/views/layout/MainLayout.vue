<template>
  <div class="main-layout">
    <!-- 侧边栏 -->
    <div class="sidebar" :class="{ collapsed }">
      <div class="sidebar-header">
        <span v-if="!collapsed" class="sidebar-title">江右拾遗</span>
        <span v-else class="sidebar-title-short">拾</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="collapsed"
        :router="true"
        class="sidebar-menu"
        background-color="#302A25"
        text-color="#C5B8A8"
        active-text-color="#FDFCF9"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/user">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/heritage">
          <el-icon><Collection /></el-icon>
          <span>非遗管理</span>
        </el-menu-item>
        <el-menu-item index="/product">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/order">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/post">
          <el-icon><Document /></el-icon>
          <span>打卡管理</span>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 主区域 -->
    <div class="main-area" :class="{ collapsed }">
      <!-- 顶栏 -->
      <div class="navbar">
        <div class="navbar-left">
          <el-button text @click="collapsed = !collapsed" style="border: none;">
            <el-icon :size="20"><Fold v-if="!collapsed" /><Expand v-else /></el-icon>
          </el-button>
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ routeTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="navbar-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" icon="User" />
              <span class="user-name">{{ userStore.nickname || userStore.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 内容区 -->
      <div class="content">
        <router-view :key="routeKey" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '../../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const collapsed = ref(false)

// 强制内容区根据路由变化重新渲染
const routeKey = computed(() => route.fullPath)

const activeMenu = computed(() => {
  const path = route.path
  // 子路由高亮父菜单
  if (path.startsWith('/heritage')) return '/heritage'
  if (path.startsWith('/product')) return '/product'
  if (path.startsWith('/order')) return '/order'
  return path
})

const routeTitle = computed(() => route.meta?.title || '')

function handleCommand(command) {
  if (command === 'logout') {
    ElMessageBox.confirm('确认退出登录吗？', '提示').then(() => {
      userStore.logout()
      router.push('/login')
    }).catch(() => {})
  }
}
</script>

<style scoped>
.main-layout {
  height: 100vh;
  display: flex;
  overflow: hidden;
  --sidebar-width: 220px;
  --sidebar-collapsed-width: 64px;
}
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: var(--sidebar-width);
  background: #302A25;
  transition: width 0.25s ease;
  z-index: 100;
  overflow: hidden;
}
.sidebar.collapsed {
  width: var(--sidebar-collapsed-width);
}
.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.04);
  border-bottom: 1px solid rgba(197, 184, 168, 0.08);
}
.sidebar-title {
  font-size: 20px;
  font-weight: bold;
  color: #D4AF72;
  letter-spacing: 3px;
  font-family: 'Noto Serif SC', 'STSong', serif;
}
.sidebar-title-short {
  font-size: 22px;
  font-weight: bold;
  color: #D4AF72;
  font-family: 'Noto Serif SC', 'STSong', serif;
}
.sidebar-menu {
  border-right: none !important;
}
.main-area {
  flex: 1;
  margin-left: var(--sidebar-width);
  transition: margin-left 0.25s ease;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.main-area.collapsed {
  margin-left: var(--sidebar-collapsed-width);
}
.navbar {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: #FDFCF9;
  box-shadow: 0 1px 3px rgba(48, 42, 37, 0.06);
  z-index: 10;
  border-bottom: 1px solid rgba(232, 221, 208, 0.3);
}
.navbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.breadcrumb {
  font-size: 14px;
}
.navbar-right {
  display: flex;
  align-items: center;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 8px;
  transition: background 0.2s;
}
.user-info:hover {
  background: #F7F3EC;
}
.user-name {
  font-size: 14px;
  color: #5A4A3A;
}
.content {
  flex: 1;
  padding: 20px;
  overflow-x: hidden;
  overflow-y: auto;
  background: #F7F3EC;
}
</style>
