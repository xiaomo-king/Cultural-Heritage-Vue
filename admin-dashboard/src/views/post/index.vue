<template>
  <div class="page-container">
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column type="index" label="序号" width="60" :index="calcIndex" />
      <el-table-column prop="nickName" label="发布者" width="120" />
      <el-table-column prop="post.title" label="标题" min-width="180">
        <template #default="{ row }">{{ row.post.title || '(无标题)' }}</template>
      </el-table-column>
      <el-table-column prop="post.heritageName" label="关联非遗" width="160" />
      <el-table-column prop="post.likeCount" label="点赞" width="70" />
      <el-table-column prop="post.commentCount" label="评论" width="70" />
      <el-table-column label="可见性" width="80">
        <template #default="{ row }">
          <el-tag :type="row.post.visibility === 'public' ? 'success' : 'info'" size="small">
            {{ row.post.visibility === 'public' ? '公开' : '私密' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="post.createdAt" label="发布时间" width="170" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button text size="small" @click="handleShowComments(row)">评论</el-button>
          <el-button text size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-bar">
      <el-pagination v-model:current-page="page" :page-size="size" :total="total" background layout="prev, pager, next, total" @current-change="fetchData" />
    </div>

    <!-- 评论管理弹窗 -->
    <PostCommentDialog
      v-model:visible="commentVisible"
      :post-id="currentPostId"
      :post-title="currentPostTitle"
      @deleted="fetchData"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPostList, deletePost } from '../../api/post'
import { ElMessage, ElMessageBox } from 'element-plus'
import PostCommentDialog from '../../components/PostCommentDialog.vue'

const list = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)

// 评论弹窗
const commentVisible = ref(false)
const currentPostId = ref(null)
const currentPostTitle = ref('')

onMounted(() => fetchData())

function calcIndex(index) {
  return (page.value - 1) * size.value + index + 1
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getPostList({ page: page.value - 1, size: size.value })
    list.value = res.list || []
    total.value = res.total || 0
  } catch (e) { /* ignore */ }
  loading.value = false
}

function handleShowComments(row) {
  currentPostId.value = row.post.id
  currentPostTitle.value = row.post.title || '(无标题)'
  commentVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确认删除该打卡吗？`, '提示')
    await deletePost(row.post.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) { /* cancel */ }
}
</script>
<style scoped>
.pagination-bar { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
