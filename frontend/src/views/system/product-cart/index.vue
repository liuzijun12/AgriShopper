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
            v-hasPerm="['system:product-cart:add']"
            type="success"
            @click="handleOpenDialog()"
        >
          <template #icon><Plus /></template>
          新增
        </el-button>
        <el-button
            v-hasPerm="['system:product-cart:delete']"
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
                        label=""
                        prop="id"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="userName"
                        label="用户昵称"
                        prop="userName"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="productName"
                        label="商品名称"
                        prop="productName"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="productImages"
                        label="商品图片"
                        prop="productImages"
                        min-width="200"
                        align="center"
                    >
                        <template #default="scope">
                            <el-image
                                v-if="getProductImages(scope.row.productImages).length > 0"
                                :src="getProductImages(scope.row.productImages)[0]"
                                :preview-src-list="getProductImages(scope.row.productImages)"
                                style="width: 60px; height: 60px"
                                fit="cover"
                                preview-teleported
                            />
                            <span v-else>暂无图片</span>
                        </template>
                    </el-table-column>
                    <el-table-column
                        key="productType"
                        label="选择商品的规格"
                        prop="productType"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="productCount"
                        label="商品的数量"
                        prop="productCount"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="isDeleted"
                        label="是否软删除"
                        prop="isDeleted"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="productPrice"
                        label="所选规格的价格"
                        prop="productPrice"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="createTime"
                        label="创建时间"
                        prop="createTime"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="updateTime"
                        label="更新时间"
                        prop="updateTime"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="deleteTime"
                        label="删除时间"
                        prop="deleteTime"
                        min-width="150"
                        align="center"
                    />
        <el-table-column fixed="right" label="操作" width="220">
          <template #default="scope">
            <el-button
                v-hasPerm="['system:product-cart:edit']"
                type="primary"
                size="small"
                link
                @click="handleOpenDialog(scope.row.id)"
            >
              <template #icon><Edit /></template>
              编辑
            </el-button>
            <el-button
                v-hasPerm="['system:product-cart:delete']"
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

    <!-- 购物车表单弹窗 -->
    <el-dialog
        v-model="dialog.visible"
        :title="dialog.title"
        width="500px"
        @close="handleCloseDialog"
    >
      <el-form ref="dataFormRef" :model="formData" :rules="rules" label-width="100px">
                <el-form-item label="" prop="id">
                      <el-input
                          v-model="formData.id"
                          placeholder=""
                      />
                </el-form-item>

                <el-form-item label="用户唯一标识" prop="userId">
                      <el-input
                          v-model="formData.userId"
                          placeholder="用户唯一标识"
                      />
                </el-form-item>

                <el-form-item label="商品ID" prop="productId">
                      <el-input
                          v-model="formData.productId"
                          placeholder="商品ID"
                      />
                </el-form-item>

                <el-form-item label="选择商品的规格" prop="productType">
                      <el-input
                          v-model="formData.productType"
                          placeholder="选择商品的规格"
                      />
                </el-form-item>

                <el-form-item label="商品的数量" prop="productCount">
                      <el-input
                          v-model="formData.productCount"
                          placeholder="商品的数量"
                      />
                </el-form-item>

                <el-form-item label="是否软删除" prop="isDeleted">
                      <el-input
                          v-model="formData.isDeleted"
                          placeholder="是否软删除"
                      />
                </el-form-item>

                <el-form-item label="所选规格的价格" prop="productPrice">
                      <el-input
                          v-model="formData.productPrice"
                          placeholder="所选规格的价格"
                      />
                </el-form-item>

                <el-form-item label="创建时间" prop="createTime">
                      <el-date-picker
                          v-model="formData.createTime"
                          type="datetime"
                          placeholder="创建时间"
                          value-format="YYYY-MM-DD HH:mm:ss"
                      />
                </el-form-item>

                <el-form-item label="更新时间" prop="updateTime">
                      <el-date-picker
                          v-model="formData.updateTime"
                          type="datetime"
                          placeholder="更新时间"
                          value-format="YYYY-MM-DD HH:mm:ss"
                      />
                </el-form-item>

                <el-form-item label="删除时间" prop="deleteTime">
                      <el-date-picker
                          v-model="formData.deleteTime"
                          type="datetime"
                          placeholder="删除时间"
                          value-format="YYYY-MM-DD HH:mm:ss"
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
    name: "ProductCart",
    inheritAttrs: false,
  });

  import ProductCartAPI, { ProductCartPageVO, ProductCartForm, ProductCartPageQuery } from "@/api/system/product-cart";

  const queryFormRef = ref();
  const dataFormRef = ref();

  const loading = ref(false);
  const removeIds = ref<number[]>([]);
  const total = ref(0);

  const queryParams = reactive<ProductCartPageQuery>({
    pageNum: 1,
    pageSize: 10,
  });

  // 购物车表格数据
  const pageData = ref<ProductCartPageVO[]>([]);

  // 弹窗
  const dialog = reactive({
    title: "",
    visible: false,
  });

  // 购物车表单数据
  const formData = reactive<ProductCartForm>({});

  // 购物车表单校验规则
  const rules = reactive({
                      id: [{ required: true, message: "请输入", trigger: "blur" }],
                      userId: [{ required: true, message: "请输入用户唯一标识", trigger: "blur" }],
                      productId: [{ required: true, message: "请输入商品ID", trigger: "blur" }],
                      productType: [{ required: true, message: "请输入选择商品的规格", trigger: "blur" }],
                      productCount: [{ required: true, message: "请输入商品的数量", trigger: "blur" }],
                      isDeleted: [{ required: true, message: "请输入是否软删除", trigger: "blur" }],
                      productPrice: [{ required: true, message: "请输入所选规格的价格", trigger: "blur" }],
                      createTime: [{ required: true, message: "请输入创建时间", trigger: "blur" }],
                      updateTime: [{ required: true, message: "请输入更新时间", trigger: "blur" }],
  });

  /** 查询购物车 */
  function handleQuery() {
    loading.value = true;
          ProductCartAPI.getPage(queryParams)
        .then((data) => {
          pageData.value = data.list;
          total.value = data.total;
        })
        .finally(() => {
          loading.value = false;
        });
  }

  /** 重置购物车查询 */
  function handleResetQuery() {
    queryFormRef.value!.resetFields();
    queryParams.pageNum = 1;
    handleQuery();
  }

  /** 行复选框选中记录选中ID集合 */
  function handleSelectionChange(selection: any) {
    removeIds.value = selection.map((item: any) => item.id);
  }

  /** 打开购物车弹窗 */
  function handleOpenDialog(id?: number) {
    dialog.visible = true;
    if (id) {
      dialog.title = "修改购物车";
            ProductCartAPI.getFormData(id).then((data) => {
        Object.assign(formData, data);
      });
    } else {
      dialog.title = "新增购物车";
    }
  }

  /** 提交购物车表单 */
  function handleSubmit() {
    dataFormRef.value.validate((valid: any) => {
      if (valid) {
        loading.value = true;
        const id = formData.id;
        if (id) {
                ProductCartAPI.update(id, formData)
              .then(() => {
                ElMessage.success("修改成功");
                handleCloseDialog();
                handleResetQuery();
              })
              .finally(() => (loading.value = false));
        } else {
                ProductCartAPI.add(formData)
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

  /** 关闭购物车弹窗 */
  function handleCloseDialog() {
    dialog.visible = false;
    dataFormRef.value.resetFields();
    dataFormRef.value.clearValidate();
    formData.id = undefined;
  }

  /** 删除购物车 */
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
                ProductCartAPI.deleteByIds(ids)
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

  /** 解析商品图片JSON数据 */
  function getProductImages(productImages: string): string[] {
    if (!productImages) {
      return [];
    }
    try {
      // 如果是JSON数组格式
      const parsed = JSON.parse(productImages);
      if (Array.isArray(parsed)) {
        return parsed;
      }
      // 如果是单个字符串
      return [parsed];
    } catch (error) {
      // 如果解析失败，尝试按逗号分割
      return productImages.split(',').filter(img => img.trim());
    }
  }

  onMounted(() => {
    handleQuery();
  });
</script>
