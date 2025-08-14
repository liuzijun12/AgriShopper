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
            v-hasPerm="['system:productCart:add']"
            type="success"
            @click="handleOpenDialog()"
        >
          <template #icon><Plus /></template>
          新增
        </el-button>
        <el-button
            v-hasPerm="['system:productCart:delete']"
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
                v-hasPerm="['system:productCart:edit']"
                type="primary"
                size="small"
                link
                @click="handleOpenDialog(scope.row.id)"
            >
              <template #icon><Edit /></template>
              编辑
            </el-button>
            <el-button
                v-hasPerm="['system:productCart:delete']"
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
      <el-form ref="dataFormRef" :model="formData" :rules="rules" label-width="120px">
                <!-- 商品选择区域（仅新增时显示） -->
                <div v-if="dialog.title === '新增购物车'">
                  <el-divider content-position="left">选择商品</el-divider>
                  <el-form-item label="选择商品" prop="productId">
                    <el-select
                      v-model="formData.productId"
                      placeholder="请选择商品"
                      filterable
                      remote
                      :remote-method="searchProducts"
                      :loading="productLoading"
                      @change="handleProductChange"
                      style="width: 100%"
                    >
                      <el-option
                        v-for="product in productOptions"
                        :key="product.id"
                        :label="product.name"
                        :value="product.id"
                      >
                        <div style="display: flex; align-items: center;">
                          <el-image
                            v-if="getProductImages(product.images).length > 0"
                            :src="getProductImages(product.images)[0]"
                            style="width: 30px; height: 30px; margin-right: 10px;"
                            fit="cover"
                          />
                          <div>
                            <div>{{ product.name }}</div>
                            <div style="font-size: 12px; color: #999;">¥{{ product.price }}</div>
                          </div>
                        </div>
                      </el-option>
                    </el-select>
                  </el-form-item>
                </div>

                <!-- 商品信息展示区域 -->
                <div class="product-info-section" v-if="formData.productName || selectedProduct">
                  <el-divider content-position="left">商品信息</el-divider>
                  <el-row :gutter="20">
                    <el-col :span="8" v-if="getProductImages(formData.productImages || selectedProduct?.images).length > 0">
                      <div class="product-image">
                        <el-image
                          :src="getProductImages(formData.productImages || selectedProduct?.images)[0]"
                          :preview-src-list="getProductImages(formData.productImages || selectedProduct?.images)"
                          style="width: 120px; height: 120px"
                          fit="cover"
                          preview-teleported
                        />
                      </div>
                    </el-col>
                    <el-col :span="16">
                      <div class="product-details">
                        <h4>{{ formData.productName || selectedProduct?.name }}</h4>
                        <p class="product-price">¥{{ formData.productPrice || selectedProduct?.price }}</p>
                        <p class="user-info" v-if="formData.userName">购买用户：{{ formData.userName }}</p>
                      </div>
                    </el-col>
                  </el-row>
                </div>

                <el-divider content-position="left">购物车详情</el-divider>
                
                <el-form-item label="商品规格" prop="productType">
                  <el-input
                    v-model="formData.productType"
                    placeholder="请输入商品规格"
                    :readonly="dialog.title.includes('查看')"
                  />
                </el-form-item>

                <el-form-item label="购买数量" prop="productCount">
                  <el-input-number
                    v-model="formData.productCount"
                    :min="1"
                    :max="999"
                    placeholder="请输入购买数量"
                    :disabled="dialog.title.includes('查看')"
                    style="width: 100%"
                  />
                </el-form-item>

                <el-form-item label="商品单价" prop="productPrice">
                  <el-input
                    v-model="formData.productPrice"
                    placeholder="请输入商品单价"
                    :readonly="dialog.title.includes('查看')"
                  >
                    <template #prepend>¥</template>
                  </el-input>
                </el-form-item>

                <el-form-item label="小计金额">
                  <el-input
                    :value="((formData.productPrice || selectedProduct?.price || 0) * (formData.productCount || 0)).toFixed(2)"
                    readonly
                  >
                    <template #prepend>¥</template>
                  </el-input>
                </el-form-item>

                <el-form-item label="添加时间" v-if="formData.createTime">
                  <el-input
                    :value="formData.createTime"
                    readonly
                          placeholder="删除时间"
                   />
                 </el-form-item>

      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button v-if="!dialog.title.includes('查看')" type="primary" @click="handleSubmit()">确定</el-button>
          <el-button @click="handleCloseDialog()">{{ dialog.title.includes('查看') ? '关闭' : '取消' }}</el-button>
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

  import ProductCartAPI, { ProductCartPageVO, ProductCartForm, ProductCartPageQuery } from "@/api/system/productCart";
  import ProductAPI, { ProductPageVO } from "@/api/system/product";

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

  // 商品选择相关数据
  const productOptions = ref<ProductPageVO[]>([]);
  const productLoading = ref(false);
  const selectedProduct = ref<ProductPageVO | null>(null);

  // 购物车表单校验规则
  const rules = reactive({
    productId: [{ required: true, message: "请选择商品", trigger: "change" }],
    productType: [{ required: true, message: "请输入商品规格", trigger: "blur" }],
    productCount: [
      { required: true, message: "请输入购买数量", trigger: "blur" },
      { type: "number", min: 1, message: "购买数量必须大于0", trigger: "blur" }
    ],
    productPrice: [
      { required: true, message: "请输入商品单价", trigger: "blur" },
      { type: "number", min: 0, message: "商品单价不能为负数", trigger: "blur" }
    ],
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

  /** 搜索商品 */
  function searchProducts(query: string) {
    productLoading.value = true;
    ProductAPI.getPage({ pageNum: 1, pageSize: 50 })
      .then((data) => {
        if (query) {
          productOptions.value = data.list.filter(product => 
            product.name?.toLowerCase().includes(query.toLowerCase())
          );
        } else {
          productOptions.value = data.list;
        }
      })
      .finally(() => {
        productLoading.value = false;
      });
  }

  /** 处理商品选择 */
  function handleProductChange(productId: number) {
    const product = productOptions.value.find(p => p.id === productId);
    if (product) {
      selectedProduct.value = product;
      formData.productId = product.id;
      formData.productPrice = product.price;
      // 重置其他字段
      formData.productCount = 1;
      formData.productType = '';
    }
  }

  /** 关闭弹窗时重置数据 */
  function handleCloseDialog() {
    dialog.visible = false;
    dataFormRef.value?.resetFields();
    Object.assign(formData, {});
    selectedProduct.value = null;
    productOptions.value = [];
  }

  onMounted(() => {
    handleQuery();
    // 初始加载一些商品选项
    searchProducts('');
  });
</script>

<style scoped>
.product-info-section {
  margin-bottom: 20px;
}

.product-image {
  text-align: center;
}

.product-details {
  padding-left: 10px;
}

.product-details h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.product-price {
  margin: 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #e6a23c;
}

.user-info {
  margin: 8px 0;
  font-size: 14px;
  color: #909399;
}

.dialog-footer {
  text-align: right;
}

.el-divider {
  margin: 15px 0;
}

.el-form-item {
  margin-bottom: 18px;
}
</style>
