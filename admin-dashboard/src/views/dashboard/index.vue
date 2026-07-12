<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="4" v-for="card in statCards" :key="card.label">
        <el-card shadow="hover" class="stat-card" @click="card.path && $router.push(card.path)">
          <div class="stat-value">{{ card.value }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区 - 第一行 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span>用户注册趋势（近7天）</span>
          </template>
          <div ref="userChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span>非遗分类分布</span>
          </template>
          <div ref="categoryChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区 - 第二行 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span>每日订单金额趋势（近30天）</span>
          </template>
          <div ref="orderChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span>热门非遗打卡排行</span>
          </template>
          <div ref="heritageChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, shallowRef } from 'vue'
import { getDashboardStats } from '../../api/dashboard'
import * as echarts from 'echarts'

// 统计卡片
const statCards = ref([
  { label: '用户总数', value: 0, path: '/user' },
  { label: '非遗项目', value: 0, path: '/heritage' },
  { label: '商品总数', value: 0, path: '/product' },
  { label: '打卡笔记', value: 0, path: '/post' },
  { label: '订单总数', value: 0, path: '/order' },
  { label: '交易总额', value: '¥0', path: '/order' }
])

// DOM refs
const userChartRef = ref(null)
const categoryChartRef = ref(null)
const orderChartRef = ref(null)
const heritageChartRef = ref(null)

// ECharts 实例（用 shallowRef 避免响应式追踪）
let userChart = null
let categoryChart = null
let orderChart = null
let heritageChart = null

// 颜色主题（东方文化配色）
const brandColors = ['#B63A2B', '#55735A', '#D4AF72', '#315B7D', '#8B6F4E', '#5A7A8C', '#9B6B5A', '#6B8B6B']

function initCharts() {
  if (!userChartRef.value) return

  // 用户注册趋势 - 折线图
  userChart = echarts.init(userChartRef.value)
  userChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: [], axisLabel: { color: '#666' } },
    yAxis: { type: 'value', minInterval: 1, axisLabel: { color: '#666' } },
    series: [{ data: [], type: 'line', smooth: true, lineStyle: { color: '#B63A2B', width: 3 }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(182, 58, 43, 0.25)' }, { offset: 1, color: 'rgba(182, 58, 43, 0.02)' }]) }, itemStyle: { color: '#B63A2B' }, symbol: 'circle', symbolSize: 8 }]
  })

  // 非遗分类分布 - 饼图
  categoryChart = echarts.init(categoryChartRef.value)
  categoryChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}项 ({d}%)' },
    series: [{ type: 'pie', radius: ['30%', '65%'], center: ['50%', '50%'], roseType: 'area', itemStyle: { borderRadius: 6 }, data: [], label: { color: '#333', fontSize: 11 } }]
  })

  // 订单金额趋势 - 面积图
  orderChart = echarts.init(orderChartRef.value)
  orderChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: [], axisLabel: { color: '#666', rotate: 45, fontSize: 10 } },
    yAxis: { type: 'value', axisLabel: { color: '#666', formatter: '¥{value}' } },
    series: [{ name: '金额', type: 'line', smooth: true, data: [], lineStyle: { color: '#315B7D', width: 2 }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(49, 91, 125, 0.2)' }, { offset: 1, color: 'rgba(49, 91, 125, 0.02)' }]) }, itemStyle: { color: '#315B7D' }, symbol: 'none' }]
  })

  // 热门非遗排行 - 柱状图
  heritageChart = echarts.init(heritageChartRef.value)
  heritageChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '5%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value', axisLabel: { color: '#666' } },
    yAxis: { type: 'category', data: [], axisLabel: { color: '#333', fontSize: 11 } },
    series: [{ type: 'bar', data: [], barWidth: 20, label: { show: true, position: 'right', color: '#333', fontSize: 11 } }]
  })
}

function updateCharts(data) {
  if (!data) return

  // 1. 更新统计卡片
  if (data.overview) {
    statCards.value[0].value = data.overview.userCount || 0
    statCards.value[1].value = data.overview.heritageCount || 0
    statCards.value[2].value = data.overview.productCount || 0
    statCards.value[3].value = data.overview.postCount || 0
    statCards.value[4].value = data.overview.orderCount || 0
    statCards.value[5].value = '¥' + (data.overview.totalRevenue || 0).toLocaleString()
  }

  // 2. 用户注册趋势
  if (data.userTrend && userChart) {
    userChart.setOption({
      xAxis: { data: data.userTrend.map(d => d.date?.substring(5) || '') },
      series: [{ data: data.userTrend.map(d => d.count) }]
    })
  }

  // 3. 非遗分类分布
  if (data.categoryDistribution && categoryChart) {
    categoryChart.setOption({
      series: [{
        data: data.categoryDistribution.map((d, i) => ({
          name: d.category, value: d.count,
          itemStyle: { color: brandColors[i % brandColors.length] }
        }))
      }]
    })
  }

  // 4. 订单趋势
  if (data.orderTrend && orderChart) {
    orderChart.setOption({
      xAxis: { data: data.orderTrend.map(d => d.date?.substring(5) || '') },
      series: [{ data: data.orderTrend.map(d => d.amount) }]
    })
  }

  // 5. 热门非遗排行
  if (data.topHeritage && heritageChart) {
    const names = data.topHeritage.map(d => {
      const name = d.name || ''
      return name.length > 8 ? name.substring(0, 8) + '...' : name
    })
    heritageChart.setOption({
      yAxis: { data: names.reverse() },
      series: [{
        data: data.topHeritage.map(d => d.checkinCount).reverse(),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#D4AF72' },
            { offset: 1, color: '#B63A2B' }
          ]),
          borderRadius: [0, 6, 6, 0]
        }
      }]
    })
  }
}

function handleResize() {
  if (userChart) userChart.resize()
  if (categoryChart) categoryChart.resize()
  if (orderChart) orderChart.resize()
  if (heritageChart) heritageChart.resize()
}

function disposeCharts() {
  if (userChart) { userChart.dispose(); userChart = null }
  if (categoryChart) { categoryChart.dispose(); categoryChart = null }
  if (orderChart) { orderChart.dispose(); orderChart = null }
  if (heritageChart) { heritageChart.dispose(); heritageChart = null }
}

onMounted(() => {
  // 先初始化空白图表（占位）
  initCharts()

  // 再异步加载数据
  getDashboardStats().then(data => {
    if (data) {
      updateCharts(data)
    }
  }).catch(e => {
    console.error('仪表盘数据加载失败', e)
  })

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  disposeCharts()
})
</script>

<style scoped>
.dashboard {
  max-width: 1400px;
}
.stat-cards {
  margin-bottom: 20px;
}
.stat-card {
  cursor: pointer;
  text-align: center;
  transition: transform 0.2s;
  --card-accent: #B63A2B;
}
.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 20px rgba(48, 42, 37, 0.12) !important;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #B63A2B;
  line-height: 1.4;
}
.stat-label {
  font-size: 13px;
  color: #7A6B5A;
  margin-top: 4px;
}
.chart-row {
  margin-bottom: 20px;
}
.chart-card {
  min-height: 360px;
  border: 1px solid rgba(232, 221, 208, 0.3) !important;
}
.chart-card :deep(.el-card__header) {
  border-bottom: 1px solid #EDE6DC;
  color: #5A4A3A;
  font-weight: 500;
  font-family: 'Noto Serif SC', serif;
  font-size: 15px;
}
.chart-box {
  width: 100%;
  height: 300px;
}
</style>
