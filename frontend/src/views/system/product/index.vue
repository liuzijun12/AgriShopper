<template>
  <div class="app-container">
    <div class="search-container">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item label="商品名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入商品名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="产地" prop="origin">
          <el-input
            v-model="queryParams.origin"
            placeholder="请输入产地"
            clearable
            style="width: 150px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="是否热门" prop="isHot">
          <el-select
            v-model="queryParams.isHot"
            placeholder="请选择"
            clearable
            style="width: 120px"
          >
            <el-option label="是" :value="1" />
            <el-option label="否" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <template #icon><Search /></template>
            搜索
          </el-button>
          <el-button @click="handleResetQuery">
            <template #icon><Refresh /></template>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card shadow="never" class="table-container">
      <div class="mb-10px">
        <el-button
          v-hasPerm="['system:product:add']"
          type="success"
          @click="handleOpenDialog()"
        >
          <template #icon><Plus /></template>
          新增
        </el-button>
        <el-button
          v-hasPerm="['system:product:delete']"
          type="danger"
          :disabled="removeIds.length === 0"
          @click="handleDelete()"
        >
          <template #icon><Delete /></template>
          删除
        </el-button>
      </div>

      <div class="table-wrapper">
        <el-table
          ref="dataTableRef"
          v-loading="loading"
          :data="pageData"
          highlight-current-row
          border
          @selection-change="handleSelectionChange"
        >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column
          key="id"
          label="商品ID"
          prop="id"
          min-width="150"
          align="center"
        />
        <el-table-column
          key="name"
          label="商品名称"
          prop="name"
          min-width="150"
          align="center"
        />
        <el-table-column
          key="images"
          label="商品图片"
          prop="images"
          width="120"
          align="center"
        >
          <template #default="scope">
            <div v-if="scope.row.images" class="image-preview">
              <el-image
                style="width: 60px; height: 60px"
                :src="getFirstImage(scope.row.images)"
                :preview-src-list="getImageList(scope.row.images)"
                fit="cover"
                preview-teleported
              />
            </div>
            <span v-else class="text-gray-400">暂无图片</span>
          </template>
        </el-table-column>
        <el-table-column
          key="description"
          label="商品简介"
          prop="description"
          min-width="200"
          align="left"
          show-overflow-tooltip
        />
        <el-table-column
          key="price"
          label="价格"
          prop="price"
          width="120"
          align="center"
        >
          <template #default="scope">
            <div class="price-display">
              <span class="original-price">¥{{ scope.row.price }}</span>
              <span v-if="scope.row.hasDiscount && scope.row.discountPrice" class="discount-price">
                ¥{{ scope.row.discountPrice }}
              </span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
          key="sales"
          label="销量"
          prop="sales"
          width="100"
          align="center"
        >
          <template #default="scope">
            <span>{{ scope.row.sales || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column
          key="tags"
          label="标签"
          prop="tagNames"
          min-width="200"
          align="center"
        >
          <template #default="scope">
            <div v-if="scope.row.tagNames" class="tags-container">
              <el-tag
                v-for="tag in scope.row.tagNames.split(',')"
                :key="tag"
                size="small"
                type="primary"
                class="tag-item"
              >
                {{ tag.trim() }}
              </el-tag>
            </div>
            <span v-else class="text-gray-400">暂无标签</span>
          </template>
        </el-table-column>
        <el-table-column
          key="categories"
          label="分类"
          prop="categoryNames"
          min-width="200"
          align="center"
        >
          <template #default="scope">
            <div v-if="scope.row.categoryNames" class="categories-container">
              <el-tag
                v-for="category in scope.row.categoryNames.split(',')"
                :key="category"
                size="small"
                type="success"
                class="category-item"
              >
                {{ category.trim() }}
              </el-tag>
            </div>
            <span v-else class="text-gray-400">暂无分类</span>
          </template>
        </el-table-column>
        <el-table-column
          key="hasDiscount"
          label="有优惠"
          prop="hasDiscount"
          width="80"
          align="center"
        >
          <template #default="scope">
            <span>{{ scope.row.hasDiscount ? '是' : '否' }}</span>
          </template>
        </el-table-column>
        <el-table-column
          key="discountPrice"
          label="优惠价"
          prop="discountPrice"
          width="100"
          align="center"
        >
          <template #default="scope">
            <span v-if="scope.row.hasDiscount && scope.row.discountPrice" class="discount-price-text">
              ¥{{ scope.row.discountPrice }}
            </span>
            <span v-else class="text-gray-400">-</span>
          </template>
        </el-table-column>
        <el-table-column
          key="origin"
          label="产地"
          prop="origin"
          min-width="120"
          align="center"
        />
        <el-table-column
          key="stock"
          label="库存"
          prop="stock"
          width="80"
          align="center"
        />
        <el-table-column
          key="virtualSales"
          label="虚拟销量"
          prop="virtualSales"
          width="100"
          align="center"
        />
        <el-table-column
          key="isHot"
          label="热门推荐"
          prop="isHot"
          width="100"
          align="center"
        >
          <template #default="scope">
            <span>{{ scope.row.isHot ? '热门' : '普通' }}</span>
          </template>
        </el-table-column>
        <el-table-column
          key="isOnline"
          label="是否推荐"
          prop="isOnline"
          width="100"
          align="center"
        >
          <template #default="scope">
            <span>{{ scope.row.isOnline ? '推荐' : '不推荐' }}</span>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="220">
          <template #default="scope">
            <el-button
              v-hasPerm="['system:product:edit']"
              type="primary"
              size="small"
              link
              @click="handleOpenDialog(scope.row.id)"
            >
              <template #icon><Edit /></template>
              编辑
            </el-button>
            <el-button
              v-hasPerm="['system:product:delete']"
              type="danger"
              size="small"
              link
              @click="handleDelete(scope.row.id)"
            >
              <template #icon><Delete /></template>
              删除
            </el-button>
          </template>
        </el-table-column>
        </el-table>
      </div>

      <div class="pagination-wrapper">
        <pagination
          v-if="total > 0"
          v-model:total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="handleQuery()"
        />
      </div>
    </el-card>

    <!-- 商品表表单弹窗 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="60%"
      :style="{ maxHeight: '80vh' }"
      @close="handleCloseDialog"
      class="product-dialog"
    >
      <div class="product-form-container" style="max-height: 60vh; overflow-y: auto;">
        <el-form ref="dataFormRef" :model="formData" :rules="rules" label-width="100px" class="product-form">
          
          <!-- 基本信息 -->
          <el-card class="form-section" shadow="never">
            <template #header>
              <div class="section-header">
                <el-icon class="section-icon"><InfoFilled /></el-icon>
                <span class="section-title">基本信息</span>
              </div>
            </template>
            
            <el-row :gutter="24">
              <el-col :span="24">
                <el-form-item label="商品名称" prop="name">
                  <el-input
                    v-model="formData.name"
                    placeholder="请输入商品名称"
                    size="large"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :span="24">
                <el-form-item label="商品简介" prop="description">
                  <el-input
                    v-model="formData.description"
                    type="textarea"
                    :rows="4"
                    placeholder="请输入商品简介，详细描述商品特点和优势"
                    show-word-limit
                    maxlength="500"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :span="24">
                <el-form-item label="产地" prop="origin">
                  <el-input
                    v-model="formData.origin"
                    placeholder="请输入产地"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="商品标签" prop="tagIds">
                  <div class="selection-container">
                    <div class="tree-wrapper tag-tree-wrapper">
                      <div class="tree-header">
                        <span class="tree-title">选择标签</span>
                        <span class="selected-count">已选择: {{ selectedTagNames.length }}</span>
                      </div>
                      <el-tree
                        ref="tagTreeRef"
                        :data="tagOptions"
                        show-checkbox
                        node-key="id"
                        :props="{ children: 'children', label: 'name', disabled: 'disabled' }"
                        :default-checked-keys="formData.tagIds || []"
                        @check="handleTagCheck"
                        @check-change="updateTagDisabledState"
                        class="tag-tree"
                      />
                    </div>
                    <div v-if="selectedTagNames.length > 0" class="selected-preview">
                      <div class="preview-header">已选择的标签:</div>
                      <div class="selected-tags">
                        <el-tag
                          v-for="tagName in selectedTagNames"
                          :key="tagName"
                          size="small"
                          type="primary"
                          closable
                          @close="removeSelectedTag(tagName)"
                          class="selected-tag"
                        >
                          {{ tagName }}
                        </el-tag>
                      </div>
                    </div>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="商品分类" prop="categoryIds">
                  <div class="selection-container">
                    <div class="tree-wrapper category-tree-wrapper">
                      <div class="tree-header">
                        <span class="tree-title">选择分类</span>
                        <span class="selected-count">已选择: {{ selectedCategoryNames.length }}</span>
                      </div>
                      <el-tree
                        ref="categoryTreeRef"
                        :data="categoryOptions"
                        show-checkbox
                        node-key="id"
                        :props="{ children: 'children', label: 'name', disabled: 'disabled' }"
                        :default-checked-keys="formData.categoryIds || []"
                        @check="handleCategoryCheck"
                        @check-change="updateCategoryDisabledState"
                        class="category-tree"
                      />
                    </div>
                    <div v-if="selectedCategoryNames.length > 0" class="selected-preview">
                      <div class="preview-header">已选择的分类:</div>
                      <div class="selected-categories">
                        <el-tag
                          v-for="categoryName in selectedCategoryNames"
                          :key="categoryName"
                          size="small"
                          type="success"
                          closable
                          @close="removeSelectedCategory(categoryName)"
                          class="selected-category"
                        >
                          {{ categoryName }}
                        </el-tag>
                      </div>
                    </div>
                  </div>
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>

          <!-- 价格库存信息 -->
          <el-card class="form-section" shadow="never">
            <template #header>
              <div class="section-header">
                <el-icon class="section-icon"><Money /></el-icon>
                <span class="section-title">价格库存</span>
              </div>
            </template>
            
            <el-row :gutter="24">
              <el-col :span="8">
                <el-form-item label="原价" prop="price">
                  <el-input-number
                    v-model="formData.price"
                    :min="0"
                    :precision="2"
                    placeholder="请输入原价"
                    style="width: 100%"
                    size="large"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="优惠价" prop="discountPrice">
                  <el-input-number
                    v-model="formData.discountPrice"
                    :min="0"
                    :precision="2"
                    :disabled="!formData.hasDiscount"
                    placeholder="请输入优惠价"
                    style="width: 100%"
                    size="large"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="启用优惠" prop="hasDiscount">
                  <el-switch
                    v-model="formData.hasDiscount"
                    :active-value="1"
                    :inactive-value="0"
                    active-text="是"
                    inactive-text="否"
                    size="large"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :span="8">
                <el-form-item label="库存" prop="stock">
                  <el-input-number
                    v-model="formData.stock"
                    :min="0"
                    placeholder="库存数量"
                    style="width: 100%"
                    size="large"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="销量" prop="sales">
                  <el-input-number
                    v-model="formData.sales"
                    :min="0"
                    placeholder="实际销量"
                    style="width: 100%"
                    size="large"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="虚拟销量" prop="virtualSales">
                  <el-input-number
                    v-model="formData.virtualSales"
                    :min="0"
                    placeholder="虚拟销量"
                    style="width: 100%"
                    size="large"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>

          <!-- 规格价格 -->
          <el-card class="form-section" shadow="never">
            <template #header>
              <div class="section-header">
                <el-icon class="section-icon"><List /></el-icon>
                <span class="section-title">规格价格</span>
              </div>
            </template>
            
            <el-form-item label="规格配置" prop="type">
              <div class="spec-price-container">
                <div v-for="(item, index) in specPriceList" :key="index" class="spec-price-item">
                  <el-input
                    v-model="item.spec"
                    placeholder="请输入规格名称"
                    class="spec-input"
                  />
                  <span class="spec-separator">:</span>
                  <el-input-number
                    v-model="item.price"
                    :min="0"
                    :precision="2"
                    placeholder="价格"
                    class="price-input"
                  />
                  <el-button
                    type="danger"
                    size="small"
                    @click="removeSpecPrice(index)"
                    :disabled="specPriceList.length === 1"
                    class="remove-btn"
                    icon="Delete"
                  >
                    删除
                  </el-button>
                </div>
                <el-button type="primary" size="small" @click="addSpecPrice" icon="Plus" class="add-spec-btn" style="margin-top: 10px">
                  添加规格
                </el-button>
              </div>
            </el-form-item>
          </el-card>

          <!-- 媒体文件 -->
          <el-card class="form-section" shadow="never">
            <template #header>
              <div class="section-header">
                <el-icon class="section-icon"><Picture /></el-icon>
                <span class="section-title">媒体文件</span>
              </div>
            </template>
            
            <el-row :gutter="24">
              <el-col :span="24">
                <el-form-item label="商品图片" prop="images">
                  <div class="upload-wrapper">
                    <el-upload
                      class="image-upload"
                      :auto-upload="false"
                      :on-change="handleImageChange"
                      :on-preview="handlePreview"
                      :on-remove="handleRemove"
                      :before-remove="beforeRemove"
                      multiple
                      :limit="5"
                      :on-exceed="handleExceed"
                      :file-list="fileList"
                      list-type="picture-card"
                      accept="image/*"
                    >
                      <el-icon class="upload-icon"><Plus /></el-icon>
                      <template #tip>
                        <div class="upload-tip">
                          支持 JPG/PNG 格式，单张不超过 500KB，最多上传 5 张
                        </div>
                      </template>
                    </el-upload>
                  </div>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :span="24">
                <el-form-item label="商品视频" prop="vedio">
                  <div class="upload-wrapper">
                    <el-upload
                      class="video-upload"
                      :auto-upload="false"
                      :on-change="handleVideoChange"
                      :on-preview="handleVideoPreview"
                      :on-remove="handleVideoRemove"
                      :before-remove="beforeVideoRemove"
                      :limit="1"
                      :on-exceed="handleVideoExceed"
                      :file-list="videoFileList"
                      list-type="picture-card"
                      accept="video/*"
                    >
                      <el-icon class="upload-icon"><VideoCamera /></el-icon>
                      <template #tip>
                        <div class="upload-tip">
                          支持 MP4 格式，不超过 50MB，最多上传 1 个
                        </div>
                      </template>
                    </el-upload>
                  </div>
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>

          <!-- 状态设置 -->
          <el-card class="form-section" shadow="never">
            <template #header>
              <div class="section-header">
                <el-icon class="section-icon"><Setting /></el-icon>
                <span class="section-title">状态设置</span>
              </div>
            </template>
            
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="热门推荐" prop="isHot">
                  <el-switch
                    v-model="formData.isHot"
                    :active-value="1"
                    :inactive-value="0"
                    active-text="推荐"
                    inactive-text="普通"
                    size="large"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="商品状态" prop="isOnline">
                  <el-switch
                    v-model="formData.isOnline"
                    :active-value="1"
                    :inactive-value="0"
                    active-text="上架"
                    inactive-text="下架"
                    size="large"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>
         </el-form>
       </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="handleCloseDialog()" icon="Close">
            取消
          </el-button>
          <el-button type="primary" size="large" @click="handleSubmit()" icon="Check">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import ProductAPI from '@/api/system/product'
import ProductTagsAPI from '@/api/system/product-tags'
import ProductCategoryAPI from '@/api/system/product-category'
import type { ProductPageVO, ProductForm, ProductPageQuery } from '@/api/system/product'
import type { ProductTagsVO } from '@/api/system/product-tags'
import type { ProductCategoryVO } from '@/api/system/product-category'
import { 
  InfoFilled, 
  Money, 
  List, 
  Picture, 
  VideoCamera, 
  Setting, 
  Plus, 
  Delete, 
  Close, 
  Check 
} from '@element-plus/icons-vue'

defineOptions({
  name: "Product",
  inheritAttrs: false,
});

const queryFormRef = ref();
const dataFormRef = ref();
const tagTreeRef = ref();
const categoryTreeRef = ref();

const loading = ref(false);
const removeIds = ref<number[]>([]);
const total = ref(0);

const queryParams = reactive<ProductPageQuery>({
  pageNum: 1,
  pageSize: 10,
});

// 商品表表格数据
const pageData = ref<ProductPageVO[]>([]);

// 弹窗
const dialog = reactive({
  title: "",
  visible: false,
});

// 商品表表单数据
const formData = reactive<ProductForm>({});

// 文件上传列表
const fileList = ref([]);

// 视频上传列表
const videoFileList = ref([]);

// 规格价格列表
const specPriceList = ref([{ spec: '', price: 0 }]);

// 标签选项列表
const tagOptions = ref<ProductTagsVO[]>([]);

// 分类选项列表
const categoryOptions = ref<ProductCategoryVO[]>([]);

// 已选择的标签名称
const selectedTagNames = ref<string[]>([]);

// 已选择的分类名称
const selectedCategoryNames = ref<string[]>([]);

// 商品表表单校验规则
const rules = reactive({
  name: [{ required: true, message: "请输入商品名称", trigger: "blur" }],
  images: [{ required: true, message: "请上传商品图片", trigger: "change" }],
  description: [{ required: true, message: "请输入商品简介", trigger: "blur" }],
  price: [{ required: true, message: "请输入原价", trigger: "blur" }],
  stock: [{ required: true, message: "请输入库存数量", trigger: "blur" }],
});

/** 查询商品表 */
function handleQuery() {
  loading.value = true;
  ProductAPI.getPage(queryParams)
    .then((data) => {
      pageData.value = data.list;
      total.value = data.total;
    })
    .finally(() => {
      loading.value = false;
    });
}

/** 获取标签选项列表 */
function getTagOptions() {
  ProductTagsAPI.getTagTree()
    .then((data) => {
      tagOptions.value = data;
    })
    .catch(() => {
      ElMessage.error("获取标签列表失败");
    });
}

/** 获取分类选项列表 */
function getCategoryOptions() {
  ProductCategoryAPI.getCategoryTree()
    .then((data) => {
      categoryOptions.value = data;
    })
    .catch(() => {
      ElMessage.error("获取分类列表失败");
    });
}

/** 处理标签树复选框选中事件 */
function handleTagCheck() {
  if (tagTreeRef.value) {
    const checkedKeys = tagTreeRef.value.getCheckedKeys();
    const checkedNodes = tagTreeRef.value.getCheckedNodes();
    
    // 处理父子级互斥逻辑
    const filteredKeys = filterParentChildConflicts(checkedKeys, tagOptions.value);
    
    // 如果过滤后的keys与原来不同，重新设置选中状态
    if (filteredKeys.length !== checkedKeys.length || !filteredKeys.every(key => checkedKeys.includes(key))) {
      tagTreeRef.value.setCheckedKeys(filteredKeys);
      const filteredNodes = tagTreeRef.value.getCheckedNodes();
      formData.tagIds = filteredKeys;
      selectedTagNames.value = filteredNodes.map((node: any) => node.name);
    } else {
      formData.tagIds = checkedKeys;
      selectedTagNames.value = checkedNodes.map((node: any) => node.name);
    }
  }
}

/** 处理分类树复选框选中事件 */
function handleCategoryCheck() {
  if (categoryTreeRef.value) {
    const checkedKeys = categoryTreeRef.value.getCheckedKeys();
    const checkedNodes = categoryTreeRef.value.getCheckedNodes();
    
    // 处理父子级互斥逻辑
    const filteredKeys = filterParentChildConflicts(checkedKeys, categoryOptions.value);
    
    // 如果过滤后的keys与原来不同，重新设置选中状态
    if (filteredKeys.length !== checkedKeys.length || !filteredKeys.every(key => checkedKeys.includes(key))) {
      categoryTreeRef.value.setCheckedKeys(filteredKeys);
      const filteredNodes = categoryTreeRef.value.getCheckedNodes();
      formData.categoryIds = filteredKeys;
      selectedCategoryNames.value = filteredNodes.map((node: any) => node.name);
    } else {
      formData.categoryIds = checkedKeys;
      selectedCategoryNames.value = checkedNodes.map((node: any) => node.name);
    }
  }
}

/** 过滤父子级冲突的选择项 */
function filterParentChildConflicts(checkedKeys: any[], treeData: any[]): any[] {
  const keySet = new Set(checkedKeys);
  const filteredKeys = new Set(checkedKeys);
  
  // 递归遍历树结构
  function traverseTree(nodes: any[]) {
    for (const node of nodes) {
      const nodeId = node.id;
      
      // 如果当前节点被选中
      if (keySet.has(nodeId)) {
        // 检查是否有子节点被选中
        const hasSelectedChildren = hasAnyChildSelected(node, keySet);
        
        if (hasSelectedChildren) {
          // 如果有子节点被选中，取消父节点的选中状态
          filteredKeys.delete(nodeId);
        } else {
          // 如果父节点被选中，取消所有子节点的选中状态
          removeAllChildrenFromSelection(node, filteredKeys);
        }
      }
      
      // 递归处理子节点
      if (node.children && node.children.length > 0) {
        traverseTree(node.children);
      }
    }
  }
  
  // 检查节点是否有任何子节点被选中
  function hasAnyChildSelected(node: any, selectedKeys: Set<any>): boolean {
    if (!node.children || node.children.length === 0) {
      return false;
    }
    
    for (const child of node.children) {
      if (selectedKeys.has(child.id) || hasAnyChildSelected(child, selectedKeys)) {
        return true;
      }
    }
    return false;
  }
  
  // 从选择集合中移除所有子节点
  function removeAllChildrenFromSelection(node: any, selectedKeys: Set<any>) {
    if (!node.children || node.children.length === 0) {
      return;
    }
    
    for (const child of node.children) {
      selectedKeys.delete(child.id);
      removeAllChildrenFromSelection(child, selectedKeys);
    }
  }
  
  traverseTree(treeData);
  return Array.from(filteredKeys);
}

/** 更新标签树的禁用状态 */
function updateTagDisabledState() {
  if (tagTreeRef.value) {
    const checkedKeys = tagTreeRef.value.getCheckedKeys();
    updateTreeDisabledState(tagOptions.value, checkedKeys);
  }
}

/** 更新分类树的禁用状态 */
function updateCategoryDisabledState() {
  if (categoryTreeRef.value) {
    const checkedKeys = categoryTreeRef.value.getCheckedKeys();
    updateTreeDisabledState(categoryOptions.value, checkedKeys);
  }
}

/** 更新树节点的禁用状态 */
function updateTreeDisabledState(treeData: any[], checkedKeys: any[]) {
  const checkedSet = new Set(checkedKeys);
  
  function updateNodeState(nodes: any[]) {
    for (const node of nodes) {
      // 重置禁用状态
      node.disabled = false;
      
      // 如果当前节点被选中
      if (checkedSet.has(node.id)) {
        // 禁用所有子节点
        disableAllChildren(node);
      }
      
      // 如果有子节点被选中，禁用父节点
      if (hasAnyChildSelected(node, checkedSet)) {
        node.disabled = true;
      }
      
      // 递归处理子节点
      if (node.children && node.children.length > 0) {
        updateNodeState(node.children);
      }
    }
  }
  
  function disableAllChildren(node: any) {
    if (!node.children || node.children.length === 0) {
      return;
    }
    
    for (const child of node.children) {
      child.disabled = true;
      disableAllChildren(child);
    }
  }
  
  function hasAnyChildSelected(node: any, selectedKeys: Set<any>): boolean {
    if (!node.children || node.children.length === 0) {
      return false;
    }
    
    for (const child of node.children) {
      if (selectedKeys.has(child.id) || hasAnyChildSelected(child, selectedKeys)) {
        return true;
      }
    }
    return false;
  }
  
  updateNodeState(treeData);
}

/** 移除已选择的标签 */
function removeSelectedTag(tagName: string) {
  if (tagTreeRef.value) {
    const allNodes = tagTreeRef.value.store.nodesMap;
    for (const nodeId in allNodes) {
      const node = allNodes[nodeId];
      if (node.data.name === tagName) {
        tagTreeRef.value.setChecked(node.data.id, false);
        break;
      }
    }
    handleTagCheck();
  }
}

/** 移除已选择的分类 */
function removeSelectedCategory(categoryName: string) {
  if (categoryTreeRef.value) {
    const allNodes = categoryTreeRef.value.store.nodesMap;
    for (const nodeId in allNodes) {
      const node = allNodes[nodeId];
      if (node.data.name === categoryName) {
        categoryTreeRef.value.setChecked(node.data.id, false);
        break;
      }
    }
    handleCategoryCheck();
  }
}

/** 重置商品表查询 */
function handleResetQuery() {
  queryFormRef.value!.resetFields();
  queryParams.pageNum = 1;
  handleQuery();
}

/** 行复选框选中记录选中ID集合 */
function handleSelectionChange(selection: any) {
  removeIds.value = selection.map((item: any) => item.id);
}

/** 打开商品表弹窗 */
function handleOpenDialog(id?: number) {
  dialog.visible = true;
  // 获取标签列表
  getTagOptions();
  // 获取分类列表
  getCategoryOptions();
  
  if (id) {
    dialog.title = "修改商品表";
    ProductAPI.getFormData(id).then((data) => {
      Object.assign(formData, data);
      // 设置树形复选框的选中状态
      nextTick(() => {
        if (tagTreeRef.value && formData.tagIds) {
          tagTreeRef.value.setCheckedKeys(formData.tagIds);
          handleTagCheck();
        }
        if (categoryTreeRef.value && formData.categoryIds) {
          categoryTreeRef.value.setCheckedKeys(formData.categoryIds);
          handleCategoryCheck();
        }
        // 延迟初始化禁用状态，确保树组件已渲染
        setTimeout(() => {
          updateTagDisabledState();
          updateCategoryDisabledState();
        }, 100);
      });
      // 如果有type数据，解析为规格价格列表
      if (data.type) {
        try {
          const typeData = JSON.parse(data.type);
          if (Array.isArray(typeData) && typeData.length > 0) {
            specPriceList.value = typeData;
          } else {
            specPriceList.value = [{ spec: '', price: 0 }];
          }
        } catch (e) {
          specPriceList.value = [{ spec: '', price: 0 }];
        }
      } else {
        specPriceList.value = [{ spec: '', price: 0 }];
      }
    });
  } else {
    dialog.title = "新增商品表";
    // 重置规格价格列表
    specPriceList.value = [{ spec: '', price: 0 }];
    // 清空树形复选框选中状态
    nextTick(() => {
      if (tagTreeRef.value) {
        tagTreeRef.value.setCheckedKeys([]);
      }
      if (categoryTreeRef.value) {
        categoryTreeRef.value.setCheckedKeys([]);
      }
      selectedTagNames.value = [];
      selectedCategoryNames.value = [];
      // 初始化树的禁用状态
      updateTagDisabledState();
      updateCategoryDisabledState();
    });
  }
}

/** 提交商品表表单 */
function handleSubmit() {
  dataFormRef.value.validate((valid: any) => {
    if (valid) {
      loading.value = true;
      // 将规格价格列表转换为JSON格式
      formData.type = convertSpecPriceToJson();
      
      const id = formData.id;
      if (id) {
        ProductAPI.update(id, formData)
          .then(() => {
            ElMessage.success("修改成功");
            handleCloseDialog();
            handleResetQuery();
          })
          .finally(() => (loading.value = false));
      } else {
        // 新增时排除ID字段
        const { id: _, ...submitData } = formData;
        ProductAPI.add(submitData)
          .then(() => {
            ElMessage.success("新增成功");
            handleCloseDialog();
            handleResetQuery();
          })
          .finally(() => (loading.value = false));
      }
    }
  });
}

/** 关闭商品表弹窗 */
function handleCloseDialog() {
  dialog.visible = false;
  dataFormRef.value.resetFields();
  dataFormRef.value.clearValidate();
  // 重置表单数据为空对象
  Object.keys(formData).forEach(key => {
    delete formData[key];
  });
  // 重置文件列表
  fileList.value = [];
  videoFileList.value = [];
  // 重置规格价格列表
  specPriceList.value = [{ spec: '', price: 0 }];
  // 重置树形复选框
  if (tagTreeRef.value) {
    tagTreeRef.value.setCheckedKeys([]);
  }
  if (categoryTreeRef.value) {
    categoryTreeRef.value.setCheckedKeys([]);
  }
  selectedTagNames.value = [];
  selectedCategoryNames.value = [];
}

/** 删除商品表 */
function handleDelete(id?: number) {
  const ids = [id || removeIds.value].join(",");
  if (!ids) {
    ElMessage.warning("请勾选删除项");
    return;
  }

  ElMessageBox.confirm("确认删除已选中的数据项?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(
    () => {
      loading.value = true;
      ProductAPI.deleteByIds(ids)
        .then(() => {
          ElMessage.success("删除成功");
          handleResetQuery();
        })
        .finally(() => (loading.value = false));
    },
    () => {
      ElMessage.info("已取消删除");
    }
  );
}

// 上传组件相关方法
function handleImageChange(file: any, fileList: any) {
  console.log('图片文件变化:', file, fileList);
  // 这里可以添加图片上传到服务器的逻辑
  // 暂时将文件信息存储到formData中
  const imageUrls = fileList.map((item: any) => {
    return item.url || URL.createObjectURL(item.raw);
  });
  formData.images = JSON.stringify(imageUrls);
}

function handlePreview(file: any) {
  console.log('预览文件:', file);
  // 可以在这里添加图片预览逻辑
}

function handleRemove(file: any, fileList: any) {
  console.log('移除文件:', file, fileList);
  // 更新formData中的图片信息
  const imageUrls = fileList.map((item: any) => {
    return item.url || URL.createObjectURL(item.raw);
  });
  formData.images = JSON.stringify(imageUrls);
}

function beforeRemove(file: any) {
  return ElMessageBox.confirm(
    `确定移除 ${file.name}？`
  ).then(
    () => true,
    () => false
  );
}

function handleExceed(files: any, fileList: any) {
  ElMessage.warning(
    `当前限制选择 5 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`
  );
}

// 视频上传相关方法
function handleVideoChange(file: any, fileList: any) {
  console.log('视频文件变化:', file, fileList);
  // 这里可以添加视频上传到服务器的逻辑
  // 暂时将文件信息存储到formData中
  if (fileList.length > 0) {
    const videoFile = fileList[0];
    formData.vedio = videoFile.url || URL.createObjectURL(videoFile.raw);
  } else {
    formData.vedio = '';
  }
}

function handleVideoPreview(file: any) {
  console.log('预览视频:', file);
  // 可以在这里添加视频预览逻辑
}

function handleVideoRemove(file: any, fileList: any) {
  console.log('移除视频:', file, fileList);
  // 更新formData中的视频信息
  if (fileList.length > 0) {
    const videoFile = fileList[0];
    formData.vedio = videoFile.url || URL.createObjectURL(videoFile.raw);
  } else {
    formData.vedio = '';
  }
}

function beforeVideoRemove(file: any) {
  return ElMessageBox.confirm(
    `确定移除 ${file.name}？`
  ).then(
    () => true,
    () => false
  );
}

function handleVideoExceed(files: any, fileList: any) {
  ElMessage.warning(
    `当前限制选择 1 个视频文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`
  );
}

// 规格价格相关方法
function addSpecPrice() {
  specPriceList.value.push({ spec: '', price: 0 });
}

function removeSpecPrice(index: number) {
  if (specPriceList.value.length > 1) {
    specPriceList.value.splice(index, 1);
  }
}

// 将规格价格列表转换为JSON格式
function convertSpecPriceToJson() {
  const validSpecs = specPriceList.value.filter(item => item.spec.trim() !== '' && item.price > 0);
  return JSON.stringify(validSpecs);
}

// 获取第一张图片用于表格显示
function getFirstImage(images: string) {
  if (!images) return '';
  try {
    const imageList = JSON.parse(images);
    return Array.isArray(imageList) && imageList.length > 0 ? imageList[0] : '';
  } catch {
    return images; // 如果不是JSON格式，直接返回
  }
}

// 获取图片列表用于预览
function getImageList(images: string) {
  if (!images) return [];
  try {
    const imageList = JSON.parse(images);
    return Array.isArray(imageList) ? imageList : [images];
  } catch {
    return [images]; // 如果不是JSON格式，作为单个图片处理
  }
}

// 获取标签列表
function getTagList(tags: string) {
  if (!tags) return [];
  return tags.split(',').map(tag => tag.trim()).filter(tag => tag);
}

onMounted(() => {
  handleQuery();
  getTagOptions();
});
</script>

<style scoped>
.app-container {
  padding: 20px;
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
}

.search-container {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.table-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.table-container :deep(.el-card__body) {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.table-wrapper {
  flex: 1;
  overflow: auto;
  min-height: 0;
}

.table-container .el-table {
  width: 100%;
}

.pagination-wrapper {
  flex-shrink: 0;
  padding: 20px 0;
  border-top: 1px solid #ebeef5;
  margin-top: 16px;
  display: flex;
  justify-content: center;
}

.spec-price-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 15px;
  background-color: #fafafa;
}

.spec-price-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.spec-price-item:last-child {
  margin-bottom: 0;
}

.image-preview {
  display: flex;
  justify-content: center;
  align-items: center;
}

.price-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.original-price {
  font-weight: 600;
  color: #303133;
}

.discount-price {
  font-size: 12px;
  color: #f56c6c;
  text-decoration: line-through;
}

.discount-price-text {
  font-weight: 600;
  color: #f56c6c;
}

.tags-display {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  justify-content: center;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  justify-content: center;
  align-items: center;
  min-height: 24px;
}

.categories-container {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  justify-content: center;
  align-items: center;
  min-height: 24px;
}

.tag-item {
  margin: 2px;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.category-item {
  margin: 2px;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.text-gray-400 {
  color: #909399;
  font-size: 12px;
}

:deep(.el-table) {
  border-radius: 4px;
  overflow: hidden;
}

:deep(.el-table th) {
  background-color: #fafafa;
  color: #606266;
  font-weight: 500;
}

:deep(.el-table td) {
  padding: 12px 0;
}

:deep(.el-card) {
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

:deep(.el-dialog) {
  border-radius: 12px;
}

:deep(.el-dialog__header) {
  background: #f5f7fa;
  color: #303133;
  border-radius: 12px 12px 0 0;
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-dialog__title) {
  color: #303133;
  font-weight: 600;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-button--primary) {
  background: #409eff;
  border: 1px solid #409eff;
}

:deep(.el-button--primary:hover) {
  background: #66b1ff;
  border-color: #66b1ff;
}

.product-dialog {
  .product-form-container {
    max-height: 70vh;
    overflow-y: auto;
    padding-right: 10px;
  }

  .form-section {
    margin-bottom: 20px;
    
    .section-header {
      display: flex;
      align-items: center;
      gap: 8px;
      
      .section-icon {
        color: #409eff;
        font-size: 18px;
      }
      
      .section-title {
        font-weight: 600;
        color: #303133;
        font-size: 16px;
      }
    }
  }

  .spec-input {
    width: 200px;
    margin-right: 10px;
  }

  .spec-separator {
    margin: 0 10px;
    color: #909399;
    font-weight: 500;
  }

  .price-input {
    width: 150px;
    margin-right: 10px;
  }

  .remove-btn {
    margin-left: 10px;
  }

  .add-spec-btn {
    width: 120px;
  }

  /* 标签和分类选择容器样式 */
  .selection-container {
    border: 1px solid #e4e7ed;
    border-radius: 6px;
    background-color: #fff;
    overflow: hidden;
  }

  .tree-wrapper {
    border-bottom: 1px solid #f0f0f0;
  }

  .tree-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    background-color: #f8f9fa;
    border-bottom: 1px solid #e9ecef;
  }

  .tree-title {
    font-weight: 600;
    color: #303133;
    font-size: 14px;
  }

  .selected-count {
    font-size: 12px;
    color: #909399;
    background-color: #e9ecef;
    padding: 2px 8px;
    border-radius: 12px;
  }

  .tag-tree-wrapper,
  .category-tree-wrapper {
    padding: 12px 16px;
    max-height: 200px;
    overflow-y: auto;
    background-color: #fff;
  }

  .tag-tree,
  .category-tree {
    background-color: transparent;
  }

  /* 已选择项预览样式 */
  .selected-preview {
    padding: 12px 16px;
    background-color: #f8f9fa;
    border-top: 1px solid #e9ecef;
  }

  .preview-header {
    font-size: 12px;
    color: #606266;
    margin-bottom: 8px;
    font-weight: 500;
  }

  .selected-tags,
  .selected-categories {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
  }

  .selected-tag,
  .selected-category {
    margin: 0;
    font-size: 12px;
    border-radius: 4px;
    transition: all 0.2s ease;
  }

  .selected-tag:hover,
  .selected-category:hover {
    transform: translateY(-1px);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  .upload-wrapper {
    width: 100%;
    
    .upload-tip {
      color: #909399;
      font-size: 12px;
      margin-top: 8px;
      text-align: center;
    }
    
    .upload-icon {
      font-size: 28px;
      color: #8c939d;
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: center;
    gap: 16px;
    padding: 20px 0;
  }
}
</style>
