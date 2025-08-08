<template>
  <div class="app-container">
    <div class="search-container">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
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
          label="商品图片，JSON数组格式存储多张图片URL"
          prop="images"
          min-width="150"
          align="center"
        />
        <el-table-column
          key="description"
          label="商品简介"
          prop="description"
          min-width="150"
          align="center"
        />
        <el-table-column
          key="price"
          label="原价"
          prop="price"
          min-width="150"
          align="center"
        />
        <el-table-column
          key="hasDiscount"
          label="是否有优惠"
          prop="hasDiscount"
          min-width="150"
          align="center"
        />
        <el-table-column
          key="discountPrice"
          label="优惠价"
          prop="discountPrice"
          min-width="150"
          align="center"
        />
        <el-table-column
          key="sales"
          label="销量"
          prop="sales"
          min-width="150"
          align="center"
        />
        <el-table-column
          key="tags"
          label="标签，多个标签用逗号分隔"
          prop="tags"
          min-width="150"
          align="center"
        />
        <el-table-column
          key="origin"
          label="产地"
          prop="origin"
          min-width="150"
          align="center"
        />
        <el-table-column
          key="isHot"
          label="是否为热门推荐商品"
          prop="isHot"
          min-width="150"
          align="center"
        />
        <el-table-column
          key="createdAt"
          label="创建时间"
          prop="createdAt"
          min-width="150"
          align="center"
        />
        <el-table-column
          key="updatedAt"
          label="更新时间"
          prop="updatedAt"
          min-width="150"
          align="center"
        />
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

      <pagination
        v-if="total > 0"
        v-model:total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="handleQuery()"
      />
    </el-card>

    <!-- 商品表表单弹窗 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="500px"
      @close="handleCloseDialog"
    >
      <el-form ref="dataFormRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="商品ID" prop="id">
          <el-input
            v-model="formData.id"
            placeholder="商品ID"
          />
        </el-form-item>

        <el-form-item label="商品名称" prop="name">
          <el-input
            v-model="formData.name"
            placeholder="商品名称"
          />
        </el-form-item>

        <el-form-item label="商品图片，JSON数组格式存储多张图片URL" prop="images">
          <el-input
            v-model="formData.images"
            placeholder="商品图片，JSON数组格式存储多张图片URL"
          />
        </el-form-item>

        <el-form-item label="商品简介" prop="description">
          <el-input
            v-model="formData.description"
            placeholder="商品简介"
          />
        </el-form-item>

        <el-form-item label="原价" prop="price">
          <el-input
            v-model="formData.price"
            placeholder="原价"
          />
        </el-form-item>

        <el-form-item label="是否有优惠" prop="hasDiscount">
          <el-switch
            v-model="formData.hasDiscount"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>

        <el-form-item label="优惠价" prop="discountPrice">
          <el-input
            v-model="formData.discountPrice"
            placeholder="优惠价"
          />
        </el-form-item>

        <el-form-item label="销量" prop="sales">
          <el-input
            v-model="formData.sales"
            placeholder="销量"
          />
        </el-form-item>

        <el-form-item label="标签，多个标签用逗号分隔" prop="tags">
          <el-input
            v-model="formData.tags"
            placeholder="标签，多个标签用逗号分隔"
          />
        </el-form-item>

        <el-form-item label="产地" prop="origin">
          <el-input
            v-model="formData.origin"
            placeholder="产地"
          />
        </el-form-item>

        <el-form-item label="是否为热门推荐商品" prop="isHot">
          <el-switch
            v-model="formData.isHot"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>

        <el-form-item label="创建时间" prop="createdAt">
          <el-input
            v-model="formData.createdAt"
            placeholder="创建时间"
          />
        </el-form-item>

        <el-form-item label="更新时间" prop="updatedAt">
          <el-input
            v-model="formData.updatedAt"
            placeholder="更新时间"
          />
        </el-form-item>

        <el-form-item label="虚拟销量
" prop="virtualSales">
          <el-input
            v-model="formData.virtualSales"
            placeholder="虚拟销量
"
          />
        </el-form-item>

        <el-form-item label="库存" prop="stock">
          <el-input
            v-model="formData.stock"
            placeholder="库存"
          />
        </el-form-item>

        <el-form-item label="是否上架
" prop="isOnline">
          <el-switch
            v-model="formData.isOnline"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>

        <el-form-item label="规格" prop="type">
          <el-select v-model="formData.type" placeholder="请选择规格">
            <el-option :value="0" label="选项一"/>
            <el-option :value="1" label="选项二"/>
          </el-select>
        </el-form-item>

        <el-form-item label="视频" prop="vedio">
          <el-input
            v-model="formData.vedio"
            placeholder="视频"
          />
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
  name: "Product",
  inheritAttrs: false,
});

import ProductAPI, { ProductPageVO, ProductForm, ProductPageQuery } from "@/api/system/product";

const queryFormRef = ref();
const dataFormRef = ref();

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

// 商品表表单校验规则
const rules = reactive({
  id: [{ required: true, message: "请输入商品ID", trigger: "blur" }],
  name: [{ required: true, message: "请输入商品名称", trigger: "blur" }],
  images: [{ required: true, message: "请输入商品图片，JSON数组格式存储多张图片URL", trigger: "blur" }],
  description: [{ required: true, message: "请输入商品简介", trigger: "blur" }],
  price: [{ required: true, message: "请输入原价", trigger: "blur" }],
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
  if (id) {
    dialog.title = "修改商品表";
    ProductAPI.getFormData(id).then((data) => {
      Object.assign(formData, data);
    });
  } else {
    dialog.title = "新增商品表";
  }
}

/** 提交商品表表单 */
function handleSubmit() {
  dataFormRef.value.validate((valid: any) => {
    if (valid) {
      loading.value = true;
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
        ProductAPI.add(formData)
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
  formData.id = undefined;
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

onMounted(() => {
  handleQuery();
});
</script>
