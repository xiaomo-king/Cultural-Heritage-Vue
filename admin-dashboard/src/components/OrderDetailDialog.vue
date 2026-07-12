<template>
  <el-dialog v-model="visible" title="订单详情" width="800px" @close="handleClose" @open="handleOpen">
    <div v-loading="loading">
      <template v-if="order">
        <el-card shadow="hover" class="detail-card">
          <template #header>
            <span>订单信息</span>
            <el-tag :type="statusType(order.status)" style="margin-left: 12px;">{{ statusLabel(order.status) }}</el-tag>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="总金额">¥{{ order.totalAmount }}</el-descriptions-item>
            <el-descriptions-item label="收货人">{{ order.consignee }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ order.phone }}</el-descriptions-item>
            <el-descriptions-item label="收货地址" :span="2">{{ order.addressRegion }} {{ order.addressDetail }}</el-descriptions-item>
            <el-descriptions-item label="物流公司">{{ order.expressCompany || '-' }}</el-descriptions-item>
            <el-descriptions-item label="物流单号">{{ order.expressNumber || '-' }}</el-descriptions-item>
            <el-descriptions-item label="下单时间">{{ order.createdAt }}</el-descriptions-item>
            <el-descriptions-item label="支付时间">{{ order.paidAt || '-' }}</el-descriptions-item>
            <el-descriptions-item label="发货时间">{{ order.shippedAt || '-' }}</el-descriptions-item>
            <el-descriptions-item label="收货时间">{{ order.receivedAt || '-' }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ order.remark || '无' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="hover" class="detail-card">
          <template #header><span>商品明细</span></template>
          <el-table :data="items" stripe>
            <el-table-column prop="productName" label="商品名称" />
            <el-table-column prop="spec" label="规格" width="120" />
            <el-table-column prop="price" label="单价" width="100">
              <template #default="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column label="小计" width="100">
              <template #default="{ row }">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </template>
    </div>

    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { getOrderDetail } from '../api/order'

const props = defineProps({
  visible: { type: Boolean, default: false },
  orderId: { type: Number, default: null }
})
const emit = defineEmits(['update:visible'])

const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const loading = ref(false)
const order = ref(null)
const items = ref([])

async function handleOpen() {
  if (!props.orderId) return
  loading.value = true
  try {
    const data = await getOrderDetail(props.orderId)
    order.value = data.order
    items.value = data.items || []
  } catch (e) { /* ignore */ }
  loading.value = false
}

function handleClose() {
  order.value = null
  items.value = []
}

function statusType(status) {
  const map = { pending: 'warning', paid: 'primary', shipped: '', received: 'success', completed: 'success', cancelled: 'info' }
  return map[status] || 'info'
}
function statusLabel(status) {
  const map = { pending: '待付款', paid: '待发货', shipped: '已发货', received: '已收货', completed: '已完成', cancelled: '已取消' }
  return map[status] || status
}
</script>

<style scoped>
.detail-card { margin-bottom: 16px; }
:deep(.el-card__body) { padding: 16px; }
</style>
