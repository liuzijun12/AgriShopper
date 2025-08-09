<template>
  <div class="app-container">
    <div class="search-container">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item label="标签名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入标签名称"
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
          v-hasPerm="['system:product-tags:add']"
          type="success"
          @click="handleOpenDialog()"
        >
          <template #icon><Plus /></template>
          新增
        </el-button>
        <el-button
          v-hasPerm="['system:product-tags:delete']"
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
          label="标签ID"
          prop="id"
          width="80"
          align="center"
        />
        <el-table-column
          key="name"
          label="标签名称"
          prop="name"
          min-width="150"
          align="center"
        />
        <el-table-column
          key="parentId"
          label="父级标签"
          prop="parentId"
          width="120"
          align="center"
        >
          <template #default="scope">
            <span v-if="scope.row.parentId === 0 || !scope.row.parentId">根标签</span>
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
              v-hasPerm="['system:product-tags:edit']"
              type="primary"
              size="small"
              link
              @click="handleOpenDialog(scope.row.id)"
            >
              <template #icon><Edit /></template>
              编辑
            </el-button>
            <el-button
              v-hasPerm="['system:product-tags:delete']"
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

    <!-- 标签表单弹窗 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="500px"
      @close="handleCloseDialog"
    >
      <el-form ref="dataFormRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="标签名称" prop="name">
          <el-input
            v-model="formData.name"
            placeholder="请输入标签名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="父级标签" prop="parentId">
          <el-select
            v-model="formData.parentId"
            placeholder="请选择父级标签"
            clearable
            style="width: 100%"
          >
            <el-option label="根标签" :value="0" />
            <el-option
              v-for="tag in parentTagOptions"
              :key="tag.id"
              :label="tag.name"
              :value="tag.id"
              :disabled="tag.id === formData.id"
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
  name: "ProductTags",
  inheritAttrs: false,
});

import ProductTagsAPI, { ProductTagsPageVO, ProductTagsForm, ProductTagsPageQuery } from "@/api/system/product-tags";

const queryFormRef = ref();
const dataFormRef = ref();

const loading = ref(false);
const removeIds = ref<number[]>([]);
const total = ref(0);

const queryParams = reactive<ProductTagsPageQuery>({
  pageNum: 1,
  pageSize: 10,
});

// 标签表格数据
const pageData = ref<ProductTagsPageVO[]>([]);

// 弹窗
const dialog = reactive({
  title: "",
  visible: false,
});

// 标签表单数据
const formData = reactive<ProductTagsForm>({});

// 父级标签选项
const parentTagOptions = ref<ProductTagsPageVO[]>([]);

// 标签表单校验规则
const rules = reactive({
  name: [{ required: true, message: "请输入标签名称", trigger: "blur" }],
});

/** 查询标签 */
function handleQuery() {
  loading.value = true;
  ProductTagsAPI.getPage(queryParams)
    .then((data) => {
      pageData.value = data.list;
      total.value = data.total;
    })
    .finally(() => {
      loading.value = false;
    });
}

/** 重置标签查询 */
function handleResetQuery() {
  queryFormRef.value!.resetFields();
  queryParams.pageNum = 1;
  handleQuery();
}

/** 行复选框选中记录选中ID集合 */
function handleSelectionChange(selection: any) {
  removeIds.value = selection.map((item: any) => item.id);
}

/** 获取父级标签选项 */
function getParentTagOptions() {
  ProductTagsAPI.getPage({ pageNum: 1, pageSize: 1000 })
    .then((data) => {
      parentTagOptions.value = data.list;
    })
    .catch(() => {
      ElMessage.error("获取父级标签列表失败");
    });
}

/** 打开标签弹窗 */
function handleOpenDialog(id?: number) {
  dialog.visible = true;
  getParentTagOptions();
  if (id) {
    dialog.title = "修改标签";
    ProductTagsAPI.getFormData(id).then((data) => {
      Object.assign(formData, data);
    });
  } else {
    dialog.title = "新增标签";
  }
}

/** 提交标签表单 */
function handleSubmit() {
  dataFormRef.value.validate((valid: any) => {
    if (valid) {
      loading.value = true;
      const id = formData.id;
      if (id) {
        ProductTagsAPI.update(id, formData)
          .then(() => {
            ElMessage.success("修改成功");
            handleCloseDialog();
            handleResetQuery();
          })
          .finally(() => (loading.value = false));
      } else {
        ProductTagsAPI.add(formData)
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

/** 关闭标签弹窗 */
function handleCloseDialog() {
  dialog.visible = false;
  dataFormRef.value.resetFields();
  dataFormRef.value.clearValidate();
  Object.assign(formData, {});
}

/** 删除标签 */
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
      ProductTagsAPI.deleteByIds(ids)
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

.tag-tree-wrapper {
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
</style>
