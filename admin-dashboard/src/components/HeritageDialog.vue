<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '编辑非遗' : '新增非遗'"
    width="750px"
    :close-on-click-modal="false"
    @close="handleClose"
    @open="handleOpen"
  >
    <el-form
      ref="formRef"
      :model="form"
      label-width="100px"
      v-loading="loading"
    >
      <!-- 第一行：名称 + 别名 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="名称" required>
            <el-input v-model="form.name" placeholder="非遗项目名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="别名">
            <el-input v-model="form.alias" placeholder="别名" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 第二行：分类 + 级别 + 城市 -->
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="分类" required>
            <el-select v-model="form.category" placeholder="选择分类" style="width: 100%">
              <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="级别">
            <el-select v-model="form.level" placeholder="选择级别" style="width: 100%">
              <el-option label="国家级" value="国家级" />
              <el-option label="省级" value="省级" />
              <el-option label="市级" value="市级" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="城市">
            <el-select v-model="form.city" placeholder="选择城市" style="width: 100%">
              <el-option v-for="c in cities" :key="c" :label="c" :value="c" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 非遗图片（多张上传，第一张为封面） -->
      <el-form-item label="非遗图片">
        <el-upload
          ref="uploadRef"
          action="/api/upload/image?type=heritage"
          list-type="picture-card"
          :file-list="fileList"
          :on-success="handleUploadSuccess"
          :on-remove="handleUploadRemove"
          :on-preview="handlePreview"
          multiple
          accept="image/*"
        >
          <el-icon><Plus /></el-icon>
        </el-upload>
        <div class="upload-tip">第一张图片作为封面图，支持多张上传</div>
      </el-form-item>

      <!-- 简介 -->
      <el-form-item label="简介">
        <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="非遗项目简介" />
      </el-form-item>

      <!-- 详细介绍 -->
      <el-form-item label="详细介绍">
        <el-input v-model="form.description" type="textarea" :rows="4" placeholder="详细介绍" />
      </el-form-item>

      <!-- 历史渊源 + 特色 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="历史渊源">
            <el-input v-model="form.history" type="textarea" :rows="4" placeholder="历史渊源" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="特色">
            <el-input v-model="form.features" type="textarea" :rows="4" placeholder="特色" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 探访攻略 + 开放时间 + 门票 -->
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="探访攻略">
            <el-input v-model="form.travelTips" type="textarea" :rows="2" placeholder="探访攻略" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="开放时间">
            <el-input v-model="form.visitHours" placeholder="如：08:00-17:00" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="门票信息">
            <el-input v-model="form.ticketInfo" placeholder="如：免费/50元" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
    </template>

    <!-- 图片预览弹窗 -->
    <el-dialog v-model="previewVisible" width="600px" title="图片预览" destroy-on-close>
      <img :src="previewUrl" style="width: 100%; border-radius: 8px;" />
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch, nextTick } from 'vue'
import { getHeritageDetail, createHeritage, updateHeritage } from '../api/heritage'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const props = defineProps({
  visible: { type: Boolean, default: false },
  heritageId: { type: Number, default: null }
})

const emit = defineEmits(['update:visible', 'saved'])

// 常量
const categories = ['传统技艺', '传统戏剧', '传统舞蹈', '传统美术', '传统音乐', '民俗', '民间文学', '曲艺', '传统医药', '传统体育']
const cities = ['南昌市', '景德镇市', '萍乡市', '九江市', '新余市', '鹰潭市', '赣州市', '吉安市', '宜春市', '抚州市', '上饶市']

// 弹窗状态
const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})
const isEdit = computed(() => !!props.heritageId)
const loading = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const uploadRef = ref(null)

// 表单数据
const form = reactive({
  id: null, name: '', alias: '', category: '', level: '', city: '',
  summary: '', description: '', history: '', features: '',
  coverImage: '', images: '', tags: '',
  travelTips: '', visitHours: '', ticketInfo: ''
})

// 图片管理
const fileList = ref([])  // el-upload 的 file-list
const previewVisible = ref(false)
const previewUrl = ref('')

const imageBaseUrl = 'http://localhost:8080'

// 图片URL转完整路径
function toFullUrl(path) {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return imageBaseUrl + path
}

// 完整路径转相对路径
function toRelativeUrl(fullPath) {
  if (!fullPath) return ''
  if (fullPath.startsWith(imageBaseUrl)) return fullPath.replace(imageBaseUrl, '')
  return fullPath
}

// 弹窗打开时：加载数据（编辑模式）或清空（新增模式）
async function handleOpen() {
  fileList.value = []
  if (isEdit.value) {
    loading.value = true
    try {
      const data = await getHeritageDetail(props.heritageId)
      Object.assign(form, {
        id: data.id, name: data.name || '', alias: data.alias || '',
        category: data.category || '', level: data.level || '', city: data.city || '',
        summary: data.summary || '', description: data.description || '',
        history: data.history || '', features: data.features || '',
        coverImage: data.coverImage || '', images: data.images || '', tags: data.tags || '',
        travelTips: data.travelTips || '', visitHours: data.visitHours || '', ticketInfo: data.ticketInfo || ''
      })
      // 初始化已上传图片列表
      if (data.images) {
        const urls = data.images.split(',').filter(Boolean)
        fileList.value = urls.map((url, i) => ({
          name: `heritage_img_${i}`,
          url: toFullUrl(url)
        }))
      }
    } catch (e) {
      ElMessage.error('加载非遗数据失败')
    }
    loading.value = false
  } else {
    resetForm()
  }
}

// 弹窗关闭时
function handleClose() {
  resetForm()
}

function resetForm() {
  form.id = null; form.name = ''; form.alias = ''
  form.category = ''; form.level = ''; form.city = ''
  form.summary = ''; form.description = ''; form.history = ''; form.features = ''
  form.coverImage = ''; form.images = ''; form.tags = ''
  form.travelTips = ''; form.visitHours = ''; form.ticketInfo = ''
  fileList.value = []
}

// 图片上传成功
function handleUploadSuccess(response, file, fileListRaw) {
  if (response.code === 200 && response.data?.url) {
    const relativeUrl = response.data.url
    fileList.value = fileListRaw.map(f => {
      // 新上传的文件：从 response 取
      if (f.response?.data?.url) {
        return { name: f.name, url: toFullUrl(f.response.data.url) }
      }
      // 已有的文件：保持原样
      return { name: f.name, url: f.url }
    })
  } else {
    ElMessage.error('图片上传失败')
  }
}

// 删除图片
function handleUploadRemove(file, fileListRaw) {
  fileList.value = fileListRaw.map(f => ({
    name: f.name,
    url: f.url
  }))
}

// 预览图片
function handlePreview(file) {
  previewUrl.value = file.url
  previewVisible.value = true
}

// 提交
async function handleSubmit() {
  if (!form.name || !form.category) {
    ElMessage.warning('请填写名称和分类')
    return
  }
  submitting.value = true
  try {
    // 从 fileList 提取图片URL（相对路径）
    const urls = fileList.value.map(f => toRelativeUrl(f.url)).filter(Boolean)
    form.images = urls.join(',')
    form.coverImage = urls.length > 0 ? urls[0] : ''

    const submitData = { ...form }

    if (isEdit.value) {
      await updateHeritage(submitData)
      ElMessage.success('保存成功')
    } else {
      await createHeritage(submitData)
      ElMessage.success('创建成功')
    }
    visible.value = false
    emit('saved')
  } catch (e) {
    // 错误已在拦截器中处理
  }
  submitting.value = false
}
</script>

<style scoped>
.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
  line-height: 1.4;
}
</style>
