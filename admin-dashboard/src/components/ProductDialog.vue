<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '编辑商品' : '新增商品'"
    width="700px"
    :close-on-click-modal="false"
    @close="handleClose"
    @open="handleOpen"
  >
    <el-form ref="formRef" :model="form" label-width="100px" v-loading="loading">
      <el-form-item label="商品名称" required>
        <el-input v-model="form.name" placeholder="商品名称" />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="原价（选填）">
            <el-input v-model="form.originalPriceDisplay" placeholder="0" @input="onOriginalPriceInput" />
            <div class="field-note">不填或为0表示无原价</div>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="库存">
            <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="分类">
            <el-select v-model="form.category" style="width: 100%">
              <el-option label="陶瓷" value="陶瓷" />
              <el-option label="刺绣" value="刺绣" />
              <el-option label="茶叶" value="茶叶" />
              <el-option label="木雕" value="木雕" />
              <el-option label="造纸" value="造纸" />
              <el-option label="文创" value="文创" />
              <el-option label="文房" value="文房" />
              <el-option label="美术" value="美术" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="关联非遗">
            <el-select v-model="form.heritageId" placeholder="搜索并选择非遗项目" filterable clearable style="width: 100%">
              <el-option
                v-for="h in heritageList"
                :key="h.id"
                :label="h.name + '（' + (h.city || '未知') + '）'"
                :value="h.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="售卖人">
            <el-input v-model="form.seller" placeholder="传承人/商家" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 商品图片 -->
      <el-form-item label="商品图片">
        <el-upload
          ref="uploadRef"
          action="/api/upload/image?type=product"
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
        <div class="upload-tip">第一张图片作为商品主图</div>
      </el-form-item>

      <el-form-item label="商品描述">
        <el-input v-model="form.description" type="textarea" :rows="3" placeholder="商品描述" />
      </el-form-item>

      <el-form-item label="标签">
        <el-input v-model="form.tags" placeholder="逗号分隔" />
      </el-form-item>

      <!-- 规格管理 -->
      <el-form-item label="商品规格">
        <div class="spec-editor">
          <div class="spec-header">
            <span class="spec-title">规格列表（规格名 + 价格）</span>
            <el-button size="small" type="primary" @click="addSpecItem">+ 添加规格</el-button>
          </div>
          <div v-for="(item, index) in specItems" :key="index" class="spec-row">
            <el-input v-model="item.name" placeholder="规格名称（如：单杯）" class="spec-name" />
            <el-input-number v-model="item.price" :precision="2" :min="0" class="spec-price" />
            <el-button text type="danger" @click="removeSpecItem(index)" :disabled="specItems.length <= 1">删除</el-button>
          </div>
          <div class="spec-tip">至少一个规格，默认价格取第一个规格的价格</div>
        </div>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
    </template>

    <!-- 图片预览 -->
    <el-dialog v-model="previewVisible" width="600px" title="图片预览" destroy-on-close>
      <img :src="previewUrl" style="width: 100%; border-radius: 8px;" />
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { getProductDetail, createProduct, updateProduct } from '../api/product'
import { getHeritageList } from '../api/heritage'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const props = defineProps({
  visible: { type: Boolean, default: false },
  productId: { type: Number, default: null }
})

const emit = defineEmits(['update:visible', 'saved'])

const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})
const isEdit = computed(() => !!props.productId)
const loading = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const uploadRef = ref(null)

// 表单数据
const form = reactive({
  id: null, name: '', originalPrice: 0, originalPriceDisplay: '', stock: 0, category: '',
  heritageId: null, seller: '', description: '', tags: '',
  images: '', specs: '', price: 0
})

// 图片管理
const fileList = ref([])
const previewVisible = ref(false)
const previewUrl = ref('')

// 规格管理（动态列表，编辑器用）
const specItems = ref([{ name: '', price: 0 }])

// 非遗项目列表（用于下拉搜索）
const heritageList = ref([])

// 原价输入处理：只允许数字和小数点，最多两位小数
function onOriginalPriceInput(value) {
  const cleaned = value.replace(/[^\d.]/g, '')
  const parts = cleaned.split('.')
  if (parts.length > 2) {
    form.originalPriceDisplay = parts[0] + '.' + parts.slice(1).join('').substring(0, 2)
  } else if (parts.length === 2 && parts[1].length > 2) {
    form.originalPriceDisplay = parts[0] + '.' + parts[1].substring(0, 2)
  } else {
    form.originalPriceDisplay = cleaned
  }
  form.originalPrice = parseFloat(form.originalPriceDisplay) || 0
}

const imageBaseUrl = 'http://localhost:8080'

function toFullUrl(path) {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return imageBaseUrl + path
}

function toRelativeUrl(fullPath) {
  if (!fullPath) return ''
  if (fullPath.startsWith(imageBaseUrl)) return fullPath.replace(imageBaseUrl, '')
  return fullPath
}

// 弹窗打开
async function handleOpen() {
  fileList.value = []
  specItems.value = [{ name: '', price: 0 }]
  // 加载非遗列表供下拉选择
  try {
    const res = await getHeritageList({ page: 0, size: 100 })
    heritageList.value = res.list || []
    if (heritageList.value.length === 0) {
      console.warn('非遗列表为空，API返回:', res)
    }
  } catch (e) {
    console.error('加载非遗列表失败:', e)
    heritageList.value = []
  }
  if (isEdit.value) {
    loading.value = true
    try {
      const data = await getProductDetail(props.productId)
      Object.assign(form, {
        id: data.id, name: data.name || '', originalPrice: data.originalPrice || 0,
        originalPriceDisplay: data.originalPrice > 0 ? String(data.originalPrice) : '',
        stock: data.stock || 0, category: data.category || '',
        heritageId: data.heritageId || null, seller: data.seller || '',
        description: data.description || '', tags: data.tags || '',
        images: data.images || '', specs: data.specs || '', price: data.price || 0
      })
      // 初始化图片列表
      if (data.images) {
        const urls = data.images.split(',').filter(Boolean)
        fileList.value = urls.map((url, i) => ({
          name: `product_img_${i}`,
          url: toFullUrl(url)
        }))
      }
      // 解析规格
      if (data.specs) {
        try {
          const specs = JSON.parse(data.specs)
          if (specs && specs.规格 && specs.价格) {
            specItems.value = specs.规格.map(name => ({
              name: name,
              price: specs.价格[name] || 0
            }))
          }
        } catch (e) { /* invalid json */ }
      }
    } catch (e) {
      ElMessage.error('加载商品数据失败')
    }
    loading.value = false
  } else {
    resetForm()
  }
}

function handleClose() {
  resetForm()
}

function resetForm() {
  form.id = null; form.name = ''; form.originalPrice = 0; form.originalPriceDisplay = ''; form.stock = 0
  form.category = ''; form.heritageId = null; form.seller = ''
  form.description = ''; form.tags = ''; form.images = ''; form.specs = ''; form.price = 0
  fileList.value = []
  specItems.value = [{ name: '', price: 0 }]
}

// 规格管理
function addSpecItem() {
  specItems.value.push({ name: '', price: 0 })
}

function removeSpecItem(index) {
  if (specItems.value.length <= 1) return
  specItems.value.splice(index, 1)
}

// 图片上传成功
function handleUploadSuccess(response, file, fileListRaw) {
  if (response.code === 200 && response.data?.url) {
    fileList.value = fileListRaw.map(f => {
      if (f.response?.data?.url) {
        return { name: f.name, url: toFullUrl(f.response.data.url) }
      }
      return { name: f.name, url: f.url }
    })
  } else {
    ElMessage.error('图片上传失败')
  }
}

function handleUploadRemove(file, fileListRaw) {
  fileList.value = fileListRaw.map(f => ({ name: f.name, url: f.url }))
}

function handlePreview(file) {
  previewUrl.value = file.url
  previewVisible.value = true
}

// 提交
async function handleSubmit() {
  if (!form.name) {
    ElMessage.warning('请填写商品名称')
    return
  }
  // 验证规格
  const validSpecs = specItems.value.filter(s => s.name.trim())
  if (validSpecs.length === 0) {
    ElMessage.warning('请至少添加一个规格')
    return
  }

  submitting.value = true
  try {
    // 构建规格JSON
    const specNames = validSpecs.map(s => s.name.trim())
    const specPriceMap = {}
    validSpecs.forEach(s => {
      specPriceMap[s.name.trim()] = s.price
    })
    form.specs = JSON.stringify({ 规格: specNames, 价格: specPriceMap })
    // 取第一个规格的价格作为默认价格
    form.price = validSpecs[0].price

    // 处理图片
    const urls = fileList.value.map(f => toRelativeUrl(f.url)).filter(Boolean)
    form.images = urls.join(',')

    const submitData = { ...form }

    if (isEdit.value) {
      await updateProduct(submitData)
      ElMessage.success('保存成功')
    } else {
      await createProduct(submitData)
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
.spec-editor {
  width: 100%;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  padding: 12px;
}
.spec-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.spec-title {
  font-size: 13px;
  color: #666;
}
.spec-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}
.spec-name {
  flex: 1;
}
.spec-price {
  width: 160px;
}
.spec-tip {
  font-size: 12px;
  color: #bbb;
  margin-top: 4px;
}
.field-note {
  font-size: 11px;
  color: #bbb;
  margin-top: 2px;
  line-height: 1.2;
}
</style>
