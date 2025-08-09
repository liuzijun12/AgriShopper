<template>
  <div class="app-container">
    <div class="search-container">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item label="分类名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入分类名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
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

    <el-card shadow="never">
      <div class="mb-10px">
        <el-button
          v-hasPerm="['system:product-category:add']"
          type="success"
          @click="handleOpenDialog()"
        >
          <template #icon><Plus /></template>
          新增
        </el-button>
        <el-button
          v-hasPerm="['system:product-category:delete']"
          type="danger"
          :disabled="removeIds.length === 0"
          @click="handleDelete()"
        >
          <template #icon><Delete /></template>
          删除
        </el-button>
      </div>

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
          label="分类ID"
          prop="id"
          width="80"
          align="center"
        />
        <el-table-column
          key="name"
          label="分类名称"
          prop="name"
          min-width="150"
          align="center"
        />
        <el-table-column
          key="icon"
          label="分类图标"
          prop="icon"
          width="120"
          align="center"
        >
          <template #default="scope">
            <el-icon v-if="scope.row.icon" size="20">
              <component :is="scope.row.icon" />
            </el-icon>
            <span v-else class="text-gray-400">无图标</span>
          </template>
        </el-table-column>
        <el-table-column
          key="parentId"
          label="父级分类"
          prop="parentId"
          width="120"
          align="center"
        >
          <template #default="scope">
            <span v-if="scope.row.parentId === 0 || !scope.row.parentId">根分类</span>
            <span v-else>{{ scope.row.parentId }}</span>
          </template>
        </el-table-column>
        <el-table-column
          key="createTime"
          label="创建时间"
          prop="createTime"
          width="180"
          align="center"
        />
        <el-table-column fixed="right" label="操作" width="220">
          <template #default="scope">
            <el-button
              v-hasPerm="['system:product-category:edit']"
              type="primary"
              size="small"
              link
              @click="handleOpenDialog(scope.row.id)"
            >
              <template #icon><Edit /></template>
              编辑
            </el-button>
            <el-button
              v-hasPerm="['system:product-category:delete']"
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

      <pagination
        v-if="total > 0"
        v-model:total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="handleQuery()"
      />
    </el-card>

    <!-- 分类表单弹窗 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="500px"
      @close="handleCloseDialog"
    >
      <el-form ref="dataFormRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input
            v-model="formData.name"
            placeholder="请输入分类名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="分类图标" prop="icon">
          <el-input
            v-model="formData.icon"
            placeholder="请输入图标名称（如：Folder、Document等）"
            maxlength="50"
          >
            <template #prepend>
              <el-icon v-if="formData.icon" size="16">
                <component :is="formData.icon" />
              </el-icon>
              <span v-else>图标</span>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="父级分类" prop="parentId">
          <el-select
            v-model="formData.parentId"
            placeholder="请选择父级分类"
            clearable
            style="width: 100%"
          >
            <el-option label="根分类" :value="0" />
            <el-option
              v-for="category in parentCategoryOptions"
              :key="category.id"
              :label="category.name"
              :value="category.id"
              :disabled="category.id === formData.id"
            />
          </el-select>
        </el-form-item>

      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmit()">确定</el-button>
          <el-button @click="handleCloseDialog()">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: "ProductCategory",
  inheritAttrs: false,
});

import ProductCategoryAPI, { ProductCategoryPageVO, ProductCategoryForm, ProductCategoryPageQuery } from "@/api/system/product-category";

const queryFormRef = ref();
const dataFormRef = ref();

const loading = ref(false);
const removeIds = ref<number[]>([]);
const total = ref(0);

const queryParams = reactive<ProductCategoryPageQuery>({
  pageNum: 1,
  pageSize: 10,
});

// 分类表格数据
const pageData = ref<ProductCategoryPageVO[]>([]);

// 弹窗
const dialog = reactive({
  title: "",
  visible: false,
});

// 分类表单数据
const formData = reactive<ProductCategoryForm>({});

// 父级分类选项
const parentCategoryOptions = ref<ProductCategoryPageVO[]>([]);

// 分类表单校验规则
const rules = reactive({
  name: [{ required: true, message: "请输入分类名称", trigger: "blur" }],
});

/** 查询分类 */
function handleQuery() {
  loading.value = true;
  ProductCategoryAPI.getPage(queryParams)
    .then((data) => {
      pageData.value = data.list;
      total.value = data.total;
    })
    .finally(() => {
      loading.value = false;
    });
}

/** 重置分类查询 */
function handleResetQuery() {
  queryFormRef.value!.resetFields();
  queryParams.pageNum = 1;
  handleQuery();
}

/** 行复选框选中记录选中ID集合 */
function handleSelectionChange(selection: any) {
  removeIds.value = selection.map((item: any) => item.id);
}

/** 获取父级分类选项 */
function getParentCategoryOptions() {
  ProductCategoryAPI.getPage({ pageNum: 1, pageSize: 1000 })
    .then((data) => {
      parentCategoryOptions.value = data.list;
    })
    .catch(() => {
      ElMessage.error("获取父级分类列表失败");
    });
}

/** 打开分类弹窗 */
function handleOpenDialog(id?: number) {
  dialog.visible = true;
  getParentCategoryOptions();
  if (id) {
    dialog.title = "修改分类";
    ProductCategoryAPI.getFormData(id).then((data) => {
      Object.assign(formData, data);
    });
  } else {
    dialog.title = "新增分类";
  }
}

/** 提交分类表单 */
function handleSubmit() {
  dataFormRef.value.validate((valid: any) => {
    if (valid) {
      loading.value = true;
      const id = formData.id;
      if (id) {
        ProductCategoryAPI.update(id, formData)
          .then(() => {
            ElMessage.success("修改成功");
            handleCloseDialog();
            handleResetQuery();
          })
          .finally(() => (loading.value = false));
      } else {
        ProductCategoryAPI.add(formData)
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

/** 关闭分类弹窗 */
function handleCloseDialog() {
  dialog.visible = false;
  dataFormRef.value.resetFields();
  dataFormRef.value.clearValidate();
  Object.assign(formData, {});
}

/** 删除分类 */
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
      ProductCategoryAPI.deleteByIds(ids)
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

onMounted(() => {
  handleQuery();
});
</script>

<style lang="scss" scoped>
.search-container {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 16px;
}

.category-tree-wrapper {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
  max-height: 300px;
  overflow-y: auto;
}

:deep(.el-table) {
  .el-table__header {
    th {
      background-color: #fafafa;
      color: #606266;
      font-weight: 600;
    }
  }
}

:deep(.el-dialog__title) {
  color: #303133;
  font-weight: 600;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

.text-gray-400 {
  color: #9ca3af;
}
</style>
