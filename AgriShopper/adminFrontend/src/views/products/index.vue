<template>
  <div class="products-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>商品管理</h1>
      <div style="display: flex; gap: 10px;">
        <el-button type="info" @click="debugCategories" size="small">
          调试分类
        </el-button>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>添加商品
        </el-button>
      </div>
    </div>

    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.productName" placeholder="请输入商品名称" clearable />
        </el-form-item>
        <el-form-item label="商品编码">
          <el-input v-model="searchForm.productCode" placeholder="请输入商品编码" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 商品列表 -->
    <el-card class="product-list-card" shadow="never">
      <el-table
        :data="productList"
        border
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column label="商品图片" width="100" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.mainImageUrl"
              :src="getImageUrl(row.mainImageUrl)"
              class="product-image"
              style="width: 60px; height: 60px; border-radius: 4px;"
              fit="cover"
              :preview-src-list="[getImageUrl(row.mainImageUrl)]"
              preview-teleported
            />
            <el-icon v-else style="font-size: 40px; color: #c0c4cc;">
              <PictureFilled />
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="productCode" label="商品编码" width="120" />
        <el-table-column prop="productName" label="商品名称" width="200" />
        <el-table-column label="分类" width="120">
          <template #default="{ row }">
            <el-tag v-if="getCategoryName(row.categoryId)" type="info" size="small">
              {{ getCategoryName(row.categoryId) }}
            </el-tag>
            <span v-else style="color: #999; font-size: 12px;">未分类 (ID: {{ row.categoryId }})</span>
          </template>
        </el-table-column>
        <el-table-column prop="productDescription" label="商品描述" show-overflow-tooltip />
        <el-table-column prop="productPrice" label="销售价" width="100">
          <template #default="{ row }">
            ¥{{ row.productPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="stockQuantity" label="库存" width="100" />
        <el-table-column prop="productStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.productStatus === 1 ? 'success' : 'info'">
              {{ row.productStatus === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button type="primary" size="small" @click="handleEdit(row)">
                编辑
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)">
                删除
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑商品对话框 -->
    <el-dialog
      :title="dialogType === 'add' ? '添加商品' : '编辑商品'"
      v-model="dialogVisible"
      width="60%"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="productName">
              <el-input v-model="formData.productName" placeholder="请输入商品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品编码" prop="productCode">
              <el-input 
                v-model="formData.productCode" 
                placeholder="请输入商品编码" 
                :disabled="dialogType === 'edit'"
                :readonly="dialogType === 'add'"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="商品描述" prop="productDescription">
          <el-input
            v-model="formData.productDescription"
            type="textarea"
            :rows="3"
            placeholder="请输入商品描述"
          />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="销售价" prop="productPrice">
              <el-input-number
                v-model="formData.productPrice"
                :precision="2"
                :step="0.1"
                :min="0"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存" prop="stockQuantity">
              <el-input-number
                v-model="formData.stockQuantity"
                :min="0"
                :precision="0"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品分类" prop="categoryId">
              <div style="display: flex; gap: 10px;">
                <el-select
                  v-model="formData.categoryId"
                  placeholder="请选择分类"
                  style="width: 200px"
                  :loading="categoriesLoading"
                  :disabled="categoriesLoading"
                  clearable
                >
                  <el-option
                    v-for="category in categories"
                    :key="category.id"
                    :label="category.categoryName || category.name"
                    :value="category.id"
                  />
                </el-select>
                <el-button type="primary" @click="showAddCategoryDialog" :disabled="categoriesLoading">
                  <el-icon><Plus /></el-icon>添加分类
                </el-button>
              </div>
              <!-- <div v-if="formData.categoryId" style="margin-top: 5px; font-size: 12px; color: #67c23a;">
                已选择: {{ getCategoryName(formData.categoryId) }}
              </div>
              <div v-else style="margin-top: 5px; font-size: 12px; color: #909399;">
                未选择分类
              </div> -->
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商ID" prop="supplierId">
              <el-input-number
                v-model="formData.supplierId"
                :min="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计量单位" prop="productUnit">
              <el-input v-model="formData.productUnit" placeholder="如：斤、袋、箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格" prop="productSpec">
              <el-input v-model="formData.productSpec" placeholder="如：500g/袋" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="成本价" prop="costPrice">
              <el-input-number
                v-model="formData.costPrice"
                :precision="2"
                :step="0.1"
                :min="0"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最小起订量" prop="minOrderQuantity">
              <el-input-number
                v-model="formData.minOrderQuantity"
                :min="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="商品状态" prop="productStatus">
              <el-select v-model="formData.productStatus" style="width: 100%">
                <el-option label="上架" :value="1" />
                <el-option label="下架" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否热销" prop="isHotProduct">
              <el-switch v-model="formData.isHotProduct" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否新品" prop="isNewProduct">
              <el-switch v-model="formData.isNewProduct" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="商品图片" prop="mainImageUrl">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :before-upload="beforeImageUpload"
            :headers="uploadHeaders"
          >
            <img v-if="formData.mainImageUrl" :src="formData.mainImageUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加分类对话框 -->
    <el-dialog
      title="添加分类"
      v-model="categoryDialogVisible"
      width="40%"
    >
      <el-form
        ref="categoryFormRef"
        :model="categoryFormData"
        :rules="categoryRules"
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="categoryFormData.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        
        <el-form-item label="分类描述" prop="categoryDescription">
          <el-input
            v-model="categoryFormData.categoryDescription"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
          />
        </el-form-item>
        
        <el-form-item label="排序权重" prop="sortOrder">
          <el-input-number
            v-model="categoryFormData.sortOrder"
            :min="1"
            :max="999"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="是否启用" prop="isEnabled">
          <el-switch v-model="categoryFormData.isEnabled" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="categoryDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAddCategory">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, PictureFilled } from '@element-plus/icons-vue'
import { getProducts, createProduct, updateProduct, deleteProduct } from '@/api/products'
import { getCategories, createCategory } from '@/api/categories'

export default {
  name: 'ProductsManagement',
  components: {
    Plus,
    Search,
    Refresh,
    PictureFilled
  },
  setup() {
    const loading = ref(false)
    const productList = ref([])
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const dialogVisible = ref(false)
    const dialogType = ref('add')
    const formRef = ref()
    
    // 分类相关
    const categories = ref([])
    const categoriesLoading = ref(false)
    const categoryDialogVisible = ref(false)
    const categoryFormRef = ref()

    const searchForm = reactive({
      productName: '',
      productCode: ''
    })

    const formData = reactive({
      productName: '',
      productCode: '',
      productDescription: '',
      categoryId: 1,
      subCategoryId: null,
      supplierId: 1,
      productUnit: '',
      productSpec: '',
      productPrice: 0,
      costPrice: 0,
      stockQuantity: 0,
      minOrderQuantity: 1,
      isHotProduct: false,
      isNewProduct: false,
      productStatus: 1,
      mainImageUrl: ''
    })

    const rules = {
      productName: [
        { required: true, message: '请输入商品名称', trigger: 'blur' }
      ],
      productCode: [
        { required: true, message: '请输入商品编码', trigger: 'blur' }
      ],
      productPrice: [
        { required: true, message: '请输入销售价', trigger: 'blur' }
      ],
      categoryId: [
        { required: true, message: '请选择商品分类', trigger: 'change' }
      ],
      supplierId: [
        { required: true, message: '请输入供应商ID', trigger: 'blur' }
      ]
    }

    const categoryFormData = reactive({
      categoryName: '',
      categoryDescription: '',
      sortOrder: 999,
      isEnabled: true
    })

    const categoryRules = {
      categoryName: [
        { required: true, message: '请输入分类名称', trigger: 'blur' }
      ],
      sortOrder: [
        { required: true, message: '请输入排序权重', trigger: 'blur' }
      ]
    }

    const uploadUrl = 'http://localhost:8080/api/upload'
    const uploadHeaders = {}

    const fetchProducts = async () => {
      loading.value = true
      try {
        const params = {
          page: currentPage.value - 1,
          size: pageSize.value,
          productName: searchForm.productName,
          productCode: searchForm.productCode
        }
        const response = await getProducts(params)
        productList.value = response.data.content
        total.value = response.data.totalElements
      } catch (error) {
        ElMessage.error('获取商品列表失败')
      } finally {
        loading.value = false
      }
    }

    const fetchCategories = async () => {
      categoriesLoading.value = true
      try {
        const response = await getCategories()
        console.log('分类API响应:', response)
        
        if (response && response.data && Array.isArray(response.data)) {
          categories.value = response.data
          console.log('分类数据加载成功:', categories.value)
        } else {
          console.warn('分类数据格式不正确:', response)
          categories.value = []
        }
      } catch (error) {
        console.error('获取分类列表失败:', error)
        ElMessage.error('获取分类列表失败')
        // 使用默认分类数据
        categories.value = [
          { id: 1, categoryName: '农产品' },
          { id: 2, categoryName: '养生保健' },
          { id: 3, categoryName: '饲料' }
        ]
      } finally {
        categoriesLoading.value = false
      }
    }

    const showAddCategoryDialog = () => {
      Object.assign(categoryFormData, {
        categoryName: '',
        categoryDescription: '',
        sortOrder: 999,
        isEnabled: true
      })
      categoryDialogVisible.value = true
    }

    const handleAddCategory = async () => {
      try {
        await categoryFormRef.value.validate()
        
        await createCategory(categoryFormData)
        ElMessage.success('分类添加成功')
        categoryDialogVisible.value = false
        
        // 重新获取分类列表
        await fetchCategories()
      } catch (error) {
        ElMessage.error('添加分类失败')
      }
    }

    const debugCategories = () => {
      console.log('当前分类数据:', categories.value)
      console.log('分类加载状态:', categoriesLoading.value)
      console.log('当前选中的分类ID:', formData.categoryId)
      ElMessage.info(`分类数量: ${categories.value.length}`)
    }

    const handleSearch = () => {
      currentPage.value = 1
      fetchProducts()
    }

    const resetSearch = () => {
      searchForm.productName = ''
      searchForm.productCode = ''
      currentPage.value = 1
      fetchProducts()
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
      fetchProducts()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      fetchProducts()
    }

    const handleAdd = async () => {
      dialogType.value = 'add'
      
      // 自动生成商品编码
      try {
        const response = await fetch('http://localhost:8080/api/products/generate-code')
        const data = await response.json()
        if (data.code === 200) {
          formData.productCode = data.data.productCode
        }
      } catch (error) {
        console.error('生成商品编码失败:', error)
        // 如果生成失败，使用默认值
        formData.productCode = ''
      }
      
      Object.assign(formData, {
        productName: '',
        productDescription: '',
        categoryId: 1,
        subCategoryId: null,
        supplierId: 1,
        productUnit: '',
        productSpec: '',
        productPrice: 0,
        costPrice: 0,
        stockQuantity: 0,
        minOrderQuantity: 1,
        isHotProduct: false,
        isNewProduct: false,
        productStatus: 1,
        mainImageUrl: ''
      })
      dialogVisible.value = true
    }

    const handleEdit = (row) => {
      dialogType.value = 'edit'
      Object.assign(formData, row)
      dialogVisible.value = true
    }

    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除这个商品吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await deleteProduct(row.id)
        ElMessage.success('删除成功')
        fetchProducts()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }

    const handleSubmit = async () => {
      try {
        await formRef.value.validate()
        
        if (dialogType.value === 'add') {
          await createProduct(formData)
          ElMessage.success('添加成功')
        } else {
          await updateProduct(formData.id, formData)
          ElMessage.success('更新成功')
        }
        
        dialogVisible.value = false
        fetchProducts()
      } catch (error) {
        ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
      }
    }

    const handleImageSuccess = (response) => {
      // 使用getImageUrl函数处理URL
      formData.mainImageUrl = getImageUrl(response.data)
      console.log('图片上传成功，URL:', formData.mainImageUrl)
      ElMessage.success('图片上传成功')
    }

    const beforeImageUpload = (file) => {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isImage) {
        ElMessage.error('只能上传图片文件!')
        return false
      }
      if (!isLt2M) {
        ElMessage.error('图片大小不能超过 2MB!')
        return false
      }
      return true
    }

    // 处理图片URL，确保是完整的URL
    const getImageUrl = (url) => {
      if (!url) return ''
      
      // 如果已经是完整URL，直接返回
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url
      }
      
      // 如果是相对路径，拼接后端地址
      if (url.startsWith('/')) {
        return 'http://localhost:8080' + url
      }
      
      // 其他情况，拼接后端地址和路径
      return 'http://localhost:8080/' + url
    }

    // 根据分类ID获取分类名称
    const getCategoryName = (categoryId) => {
      if (!categoryId || !categories.value || categories.value.length === 0) {
        return ''
      }
      const category = categories.value.find(cat => cat.id === categoryId)
      console.log('查找分类:', categoryId, '结果:', category)
      return category ? (category.categoryName || category.name) : ''
    }

    onMounted(async () => {
      try {
        // 先加载分类数据
        await fetchCategories()
        // 再加载商品数据
        await fetchProducts()
      } catch (error) {
        console.error('页面初始化失败:', error)
      }
    })

    // 全局错误处理
    const handleGlobalError = (event) => {
      if (event.message && event.message.includes('ResizeObserver')) {
        // 忽略ResizeObserver错误
        event.preventDefault()
        return false
      }
    }

    // 添加全局错误监听
    window.addEventListener('error', handleGlobalError)
    window.addEventListener('unhandledrejection', handleGlobalError)

    return {
      loading,
      productList,
      currentPage,
      pageSize,
      total,
      dialogVisible,
      dialogType,
      formRef,
      searchForm,
      formData,
      rules,
      uploadUrl,
      uploadHeaders,
      // 分类相关
      categories,
      categoriesLoading,
      categoryDialogVisible,
      categoryFormRef,
      categoryFormData,
      categoryRules,
      showAddCategoryDialog,
      handleAddCategory,
      debugCategories,
      getCategoryName,
      handleSearch,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      handleAdd,
      handleEdit,
      handleDelete,
      handleSubmit,
      handleImageSuccess,
      beforeImageUpload,
      getImageUrl
    }
  }
}
</script>

<style scoped>
.products-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  color: #303133;
  margin: 0;
}

.search-card {
  margin-bottom: 20px;
}

.product-list-card {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.avatar-uploader {
  text-align: center;
}

.avatar-uploader .avatar {
  width: 100px;
  height: 100px;
  border-radius: 4px;
  object-fit: cover;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

/* 商品列表图片样式 */
.product-image {
  border: 1px solid #ebeef5;
  transition: all 0.3s;
}

.product-image:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style> 