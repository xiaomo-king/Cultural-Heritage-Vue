<template>
  <el-dialog v-model="visible" :title="'评论管理 - ' + postTitle" width="700px" @open="handleOpen" @close="handleClose" class="comment-dialog">
    <div v-loading="loading" class="comment-body">
      <div v-if="commentList.length === 0 && !loading" class="empty-state">暂无评论</div>
      <div v-for="item in commentList" :key="item.id" class="comment-item">
        <div class="comment-header">
          <span class="comment-user">用户 {{ item.userId }}</span>
          <span class="comment-time">{{ item.createdAt }}</span>
        </div>
        <div class="comment-content">{{ item.content }}</div>
        <div class="comment-footer">
          <el-button text size="small" type="danger" @click="handleDelete(item)">删除</el-button>
        </div>
      </div>
      <!-- 加载更多 -->
      <div class="load-more-wrap" v-if="hasMore">
        <el-button :loading="loadingMore" size="small" @click="loadMore">加载更多评论 ({{ commentList.length }}/{{ total }})</el-button>
      </div>
      <div class="load-more-wrap" v-if="!hasMore && commentList.length > 0">
        <span class="no-more-text">已显示全部 {{ total }} 条评论</span>
      </div>
    </div>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { getCommentList, deleteComment } from '../api/comment'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps({
  visible: { type: Boolean, default: false },
  postId: { type: Number, default: null },
  postTitle: { type: String, default: '' }
})
const emit = defineEmits(['update:visible', 'deleted'])

const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const loading = ref(false)
const loadingMore = ref(false)
const commentList = ref([])
const page = ref(0)
const size = 10
const total = ref(0)
const hasMore = ref(false)

async function handleOpen() {
  if (!props.postId) return
  loading.value = true
  page.value = 0
  commentList.value = []
  try {
    const res = await getCommentList({ postId: props.postId, page: 0, size })
    // 后端返回 PageResponse 格式：{ list, page, size, total, totalPages }
    commentList.value = res.list || []
    total.value = res.total || 0
    hasMore.value = commentList.value.length < total.value
  } catch (e) { commentList.value = [] }
  loading.value = false
}

function handleClose() {
  commentList.value = []
}

async function loadMore() {
  loadingMore.value = true
  page.value++
  try {
    const res = await getCommentList({ postId: props.postId, page: page.value, size })
    const newList = res.list || []
    commentList.value = commentList.value.concat(newList)
    hasMore.value = commentList.value.length < total.value
  } catch (e) { /* ignore */ }
  loadingMore.value = false
}

async function handleDelete(item) {
  try {
    await ElMessageBox.confirm('确认删除该评论吗？', '提示')
    await deleteComment(item.id)
    ElMessage.success('删除成功')
    commentList.value = commentList.value.filter(c => c.id !== item.id)
    total.value--
    emit('deleted')
  } catch (e) { /* cancel */ }
}
</script>

<style scoped>
.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #999;
  font-size: 14px;
}
.comment-body {
  max-height: 500px;
  overflow-y: auto;
}
.comment-item {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  background: #fafafa;
}
.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}
.comment-user {
  font-weight: 600;
  color: #333;
  font-size: 13px;
}
.comment-time {
  color: #999;
  font-size: 12px;
}
.comment-content {
  color: #555;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 8px;
}
.comment-footer {
  text-align: right;
}
.load-more-wrap {
  text-align: center;
  padding: 12px 0;
}
.no-more-text {
  font-size: 12px;
  color: #bbb;
}
</style>
