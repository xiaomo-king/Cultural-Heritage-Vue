<template>
  <div class="page-container">
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索用户昵称" clearable style="width: 250px" @keyup.enter="handleSearch" />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column type="index" label="序号" width="60" :index="calcIndex" />
      <el-table-column prop="nickName" label="昵称" />
      <el-table-column prop="account" label="账号" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="balance" label="余额（元）" width="120" />
      <el-table-column prop="checkinCount" label="打卡数" width="80" />
      <el-table-column prop="joinDate" label="注册时间" width="170" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '正常' : '已禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button text size="small" @click="toggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserList, searchUser, toggleUserStatus } from '../../api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const keyword = ref('')

onMounted(() => fetchData())

function calcIndex(index) {
  return (page.value - 1) * size.value + index + 1
}

async function fetchData() {
  loading.value = true
  try {
    if (keyword.value) {
      const res = await searchUser({ keyword: keyword.value })
      list.value = Array.isArray(res) ? res : []
      total.value = list.value.length
    } else {
      const res = await getUserList({ page: page.value - 1, size: size.value })
      list.value = res.list || []
      total.value = res.total || 0
    }
  } catch (e) { /* ignore */ }
  loading.value = false
}

function handleSearch() {
  page.value = 1
  fetchData()
}

async function toggleStatus(row) {
  const action = row.status === 1 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确认${action}用户「${row.nickName}」吗？`, '提示')
    await toggleUserStatus(row.id)
    ElMessage.success(`${action}成功`)
    fetchData()
  } catch (e) { /* cancel */ }
}
</script>

<style scoped>
.pagination-bar {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
