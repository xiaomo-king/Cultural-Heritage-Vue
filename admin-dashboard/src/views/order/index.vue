<template>
  <div class="page-container">
    <el-tabs v-model="statusTab" @tab-change="fetchData">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="待付款" name="pending" />
      <el-tab-pane label="待发货" name="paid" />
      <el-tab-pane label="已发货" name="shipped" />
      <el-tab-pane label="已收货" name="received" />
      <el-tab-pane label="已完成" name="completed" />
      <el-tab-pane label="已取消" name="cancelled" />
    </el-tabs>
    <el-table :data="list" v-loading="loading" stripe style="margin-top: 16px;">
      <el-table-column prop="order.orderNo" label="订单号" width="180" />
      <el-table-column label="商品" min-width="200">
        <template #default="{ row }">
          <div v-for="item in (row.items || [])" :key="item.id" style="margin-bottom: 4px;">
            {{ item.productName }} × {{ item.quantity }}
            <span v-if="item.spec" style="color: #999; font-size: 12px;">（{{ item.spec }}）</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="金额" width="100">
        <template #default="{ row }">¥{{ row.order.totalAmount }}</template>
      </el-table-column>
      <el-table-column label="收货人" width="100">
        <template #default="{ row }">{{ row.order.consignee }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.order.status)" size="small">{{ statusLabel(row.order.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="时间" width="170">
        <template #default="{ row }">{{ row.order.createdAt }}</template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button text size="small" @click="handleDetail(row.order.id)">详情</el-button>
          <el-button v-if="row.order.status === 'paid'" text size="small" type="primary" @click="handleShip(row)">发货</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-bar">
      <el-pagination v-model:current-page="page" :page-size="size" :total="total" background layout="prev, pager, next, total" @current-change="fetchData" />
    </div>

    <!-- 订单详情弹窗 -->
    <OrderDetailDialog
      v-model:visible="detailVisible"
      :order-id="currentOrderId"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrderList, shipOrder } from '../../api/order'
import { ElMessage, ElMessageBox } from 'element-plus'
import OrderDetailDialog from '../../components/OrderDetailDialog.vue'

const list = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const statusTab = ref('all')

// 详情弹窗控制
const detailVisible = ref(false)
const currentOrderId = ref(null)

onMounted(() => fetchData())

function handleDetail(id) {
  currentOrderId.value = id
  detailVisible.value = true
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getOrderList({ status: statusTab.value, page: page.value - 1, size: size.value })
    list.value = res.list || []
    total.value = res.total || 0
  } catch (e) { /* ignore */ }
  loading.value = false
}

function statusType(status) {
  const map = { pending: 'warning', paid: 'primary', shipped: '', received: 'success', completed: 'success', cancelled: 'info' }
  return map[status] || 'info'
}
function statusLabel(status) {
  const map = { pending: '待付款', paid: '待发货', shipped: '已发货', received: '已收货', completed: '已完成', cancelled: '已取消' }
  return map[status] || status
}

async function handleShip(row) {
  const { value: form } = await ElMessageBox.prompt('请输入物流单号', '发货确认', { inputPattern: /\S+/, inputErrorMessage: '物流单号不能为空' })
  try {
    await shipOrder({ id: row.order.id, expressCompany: '默认快递', expressNumber: form })
    ElMessage.success('发货成功')
    fetchData()
  } catch (e) { /* cancel or error */ }
}
</script>
<style scoped>
.pagination-bar { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
