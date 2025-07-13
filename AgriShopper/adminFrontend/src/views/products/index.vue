<template>
  <div class="products-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>商品管理</h1>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加商品
      </el-button>
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
        <el-table-column prop="productCode" label="商品编码" width="120" />
        <el-table-column prop="productName" label="商品名称" width="200" />
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
              <el-input v-model="formData.productCode" placeholder="请输入商品编码" :disabled="dialogType === 'edit'" />
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
            <el-form-item label="分类ID" prop="categoryId">
              <el-input-number
                v-model="formData.categoryId"
                :min="1"
                style="width: 100%"
              />
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
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { getProducts, createProduct, updateProduct, deleteProduct } from '@/api/products'

export default {
  name: 'ProductsManagement',
  components: {
    Plus,
    Search,
    Refresh
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
        { required: true, message: '请输入分类ID', trigger: 'blur' }
      ],
      supplierId: [
        { required: true, message: '请输入供应商ID', trigger: 'blur' }
      ]
    }

    const uploadUrl = 'http://localhost:8080/api/upload'
    const uploadHeaders = {
      'Content-Type': 'multipart/form-data'
    }

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

    const handleAdd = () => {
      dialogType.value = 'add'
      Object.assign(formData, {
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
      formData.mainImageUrl = response.data
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

    onMounted(() => {
      fetchProducts()
    })

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
      handleSearch,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      handleAdd,
      handleEdit,
      handleDelete,
      handleSubmit,
      handleImageSuccess,
      beforeImageUpload
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
</style> 