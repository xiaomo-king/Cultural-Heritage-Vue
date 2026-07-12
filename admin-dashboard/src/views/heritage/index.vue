<template>
  <div class="page-container">
    <div class="search-bar">
      <el-select v-model="category" placeholder="全部分类" clearable style="width: 150px" @change="fetchData">
        <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
      </el-select>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增非遗
      </el-button>
    </div>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column type="index" label="序号" width="60" :index="calcIndex" />
      <el-table-column label="封面" width="80">
        <template #default="{ row }">
          <el-image v-if="row.coverImage" :src="row.coverImage" style="width: 50px; height: 50px; border-radius: 4px;" fit="cover" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" min-width="180" />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="level" label="级别" width="80" />
      <el-table-column prop="city" label="城市" width="80" />
      <el-table-column prop="checkinCount" label="打卡" width="70" />
      <el-table-column prop="viewCount" label="浏览" width="70" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
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
      <el-pagination
        v-model:current-page="page"
        :page-size="size"
        :total="total"
        background
        layout="prev, pager, next, total"
        @current-change="fetchData"
      />
    </div>

    <!-- 非遗表单弹窗 -->
    <HeritageDialog
      v-model:visible="dialogVisible"
      :heritage-id="currentId"
      @saved="fetchData"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHeritageList, deleteHeritage } from '../../api/heritage'
import { ElMessage, ElMessageBox } from 'element-plus'
import HeritageDialog from '../../components/HeritageDialog.vue'

const categories = ['传统技艺', '传统戏剧', '传统舞蹈', '传统美术', '传统音乐', '民俗', '民间文学', '曲艺', '传统医药', '传统体育']

const list = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const category = ref('')

// 弹窗控制
const dialogVisible = ref(false)
const currentId = ref(null)

// 计算行号（跨页连续）
function calcIndex(index) {
  return (page.value - 1) * size.value + index + 1
}

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getHeritageList({ category: category.value || undefined, page: page.value - 1, size: size.value })
    list.value = res.list || []
    total.value = res.total || 0
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
    await deleteHeritage(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) { /* cancel */ }
}
</script>
<style scoped>
.pagination-bar { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
