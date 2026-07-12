<template>
  <div class="page-container">
    <div class="search-bar">
      <el-select v-model="category" placeholder="全部分类" clearable style="width: 150px" @change="fetchData">
        <el-option label="陶瓷" value="陶瓷" />
        <el-option label="刺绣" value="刺绣" />
        <el-option label="茶叶" value="茶叶" />
        <el-option label="木雕" value="木雕" />
        <el-option label="造纸" value="造纸" />
        <el-option label="其他" value="其他" />
      </el-select>
      <el-input v-model="keyword" placeholder="搜索商品" clearable style="width: 250px" @keyup.enter="fetchData" />
      <el-button type="primary" @click="fetchData">搜索</el-button>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增商品
      </el-button>
    </div>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column type="index" label="序号" width="60" :index="calcIndex" />
      <el-table-column prop="name" label="商品名称" min-width="160" />
      <el-table-column prop="category" label="分类" width="80" />
      <el-table-column prop="price" label="价格" width="90">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="70" />
      <el-table-column prop="sales" label="销量" width="70" />
      <el-table-column prop="rating" label="评分" width="70" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button text size="small" @click="handleEdit(row.id)">编辑</el-button>
          <el-button text size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-bar">
      <el-pagination v-model:current-page="page" :page-size="size" :total="total" background layout="prev, pager, next, total" @current-change="fetchData" />
    </div>

    <!-- 商品表单弹窗 -->
    <ProductDialog
      v-model:visible="dialogVisible"
      :product-id="currentId"
      @saved="fetchData"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProductList, searchProduct, deleteProduct } from '../../api/product'
import { ElMessage, ElMessageBox } from 'element-plus'
import ProductDialog from '../../components/ProductDialog.vue'

const list = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const category = ref('')
const keyword = ref('')

// 弹窗控制
const dialogVisible = ref(false)
const currentId = ref(null)

onMounted(() => fetchData())

function calcIndex(index) {
  return (page.value - 1) * size.value + index + 1
}

async function fetchData() {
  loading.value = true
  try {
    if (keyword.value) {
      const res = await searchProduct({ keyword: keyword.value, page: page.value - 1, size: size.value })
      list.value = res.list || []
      total.value = res.total || 0
    } else {
      const res = await getProductList({ category: category.value || undefined, page: page.value - 1, size: size.value })
      list.value = res.list || []
      total.value = res.total || 0
    }
  } catch (e) { /* ignore */ }
  loading.value = false
}

function handleAdd() {
  currentId.value = null
  dialogVisible.value = true
}

function handleEdit(id) {
  currentId.value = id
  dialogVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确认删除「${row.name}」吗？`, '提示')
    await deleteProduct(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) { /* cancel */ }
}
</script>
<style scoped>
.pagination-bar { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
