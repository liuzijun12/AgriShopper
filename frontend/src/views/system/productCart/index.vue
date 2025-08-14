<template>
  <div class="app-container">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item label="商品ID" prop="productId">
          <el-input v-model="queryParams.productId" placeholder="请输入商品ID" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button @click="handleResetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table
          v-loading="loading"
          :data="pageData"
          border
          style="width: 100%"
      >
                    <el-table-column
                        key="id"
                        label="ID"
                        prop="id"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="userId"
                        label="用户唯一标识"
                        prop="userId"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="productId"
                        label="商品ID"
                        prop="productId"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="productType"
                        label="选择商品的规格"
                        min-width="150"
                        align="center"
                    >
                        <template #default="scope">
                            <div v-if="scope.row.productType">
                                <div v-for="(item, index) in parseProductType(scope.row.productType)" :key="index">
                                    {{ item.spec }}: {{ item.price }}元
                                </div>
                            </div>
                            <span v-else>暂无规格</span>
                        </template>
                    </el-table-column>
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

      </el-table>

      <div class="pagination-container">
        <pagination
            v-if="total > 0"
            v-model:total="total"
            v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize"
            @pagination="handleQuery()"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
  defineOptions({
    name: "ProductCart",
    inheritAttrs: false,
  });

  import ProductCartAPI, { ProductCartPageVO, ProductCartPageQuery, ProductCartForm } from "@/api/system/productCart";
  import { useUserStoreHook } from "@/store/modules/user.store";

  const queryFormRef = ref();
  const userStore = useUserStoreHook();

  const loading = ref(false);

  const total = ref(0);

  const queryParams = reactive<ProductCartPageQuery>({
    pageNum: 1,
    pageSize: 10,
    productId: undefined,
  });

  // 购物车表格数据
  const pageData = ref<ProductCartPageVO[]>([]);

  // 购物车表单数据
  const formData = reactive<ProductCartForm>({});

  // 商品选择相关数据
  const productOptions = ref<ProductPageVO[]>([]);
  const productLoading = ref(false);
  const selectedProduct = ref<ProductPageVO | null>(null);

  // 商品规格类型定义
  interface ProductSpec {
    spec: string;
    price: number;
  }

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

  /** 解析商品规格JSON数据 */
  function parseProductType(productType: any): ProductSpec[] {
    if (!productType) {
      return [];
    }

    // 如果已经是对象或数组，不需要解析
    if (typeof productType === 'object') {
      if (Array.isArray(productType)) {
        // 如果是数组，确保每个元素都有 spec 和 price
        return productType.filter(item => item && typeof item === 'object' && 'spec' in item && 'price' in item);
      } else if (productType && 'spec' in productType && 'price' in productType) {
        // 如果是单个规格对象
        return [productType];
      }
      return [];
    }

    // 如果是字符串，尝试解析
    if (typeof productType === 'string') {
      try {
        const parsed = JSON.parse(productType);
        if (Array.isArray(parsed)) {
          // 如果解析后是数组，确保每个元素都有 spec 和 price
          return parsed.filter(item => item && typeof item === 'object' && 'spec' in item && 'price' in item);
        } else if (parsed && typeof parsed === 'object' && 'spec' in parsed && 'price' in parsed) {
          // 如果解析后是单个规格对象
          return [parsed];
        }
      } catch (error) {
        // 如果解析失败，返回空数组
        return [];
      }
    }

    return [];
  }

  onMounted(() => {
    handleQuery();
  });
</script>

<style scoped>
.app-container {
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

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
