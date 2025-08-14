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


      <el-table
          ref="dataTableRef"
          v-loading="loading"
          :data="pageData"
          highlight-current-row
          border

      >

                    <el-table-column
                        key="id"
                        label="ID"
                        prop="id"
                        width="80"
                        align="center"
                    />
                    <el-table-column
                        key="userName"
                        label="用户名称"
                        prop="userName"
                        min-width="120"
                        align="center"
                    />
                    <el-table-column
                        key="productImages"
                        label="商品图片"
                        prop="productImages"
                        width="100"
                        align="center"
                    >
                      <template #default="scope">
                        <el-image
                          v-if="getProductImages(scope.row.productImages).length > 0"
                          :src="getProductImages(scope.row.productImages)[0]"
                          style="width: 60px; height: 60px"
                          fit="cover"
                          :preview-src-list="getProductImages(scope.row.productImages)"
                          preview-teleported
                        />
                        <span v-else>暂无图片</span>
                      </template>
                    </el-table-column>
                    <el-table-column
                        key="productName"
                        label="商品名称"
                        prop="productName"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="productType"
                        label="选择商品的规格"
                        prop="productType"
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
                        key="productPrice"
                        label="商品价格"
                        prop="productPrice"
                        width="100"
                        align="center"
                    >
                      <template #default="scope">
                        ¥{{ scope.row.productPrice }}
                      </template>
                    </el-table-column>
                    <el-table-column
                        key="createTime"
                        label="收藏时间"
                        prop="createTime"
                        width="180"
                        align="center"
                    />

      </el-table>
    </el-card>

    <div class="pagination-wrapper">
      <pagination
        v-if="total > 0"
        v-model:total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="handleQuery()"
      />
    </div>


  </div>
</template>

<script setup lang="ts">
  defineOptions({
    name: "UserFavorite",
    inheritAttrs: false,
  });

  import UserFavoriteAPI, { UserFavoritePageVO, UserFavoritePageQuery } from "@/api/system/userFavorite";
  import { useUserStoreHook } from "@/store/modules/user.store";

  const queryFormRef = ref();
  const userStore = useUserStoreHook();

  const loading = ref(false);

  const total = ref(0);

  const queryParams = reactive<UserFavoritePageQuery>({
    pageNum: 1,
    pageSize: 10,
  });

  // 收藏表格数据
  const pageData = ref<UserFavoritePageVO[]>([]);



  /** 查询收藏 */
  function handleQuery() {
    loading.value = true;
          UserFavoriteAPI.getPage(queryParams)
        .then((data) => {
          pageData.value = data.list;
          total.value = data.total;
        })
        .finally(() => {
          loading.value = false;
        });
  }

  /** 重置收藏查询 */
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
  function parseProductType(productType: string): Array<{spec: string, price: number}> {
    if (!productType) {
      return [];
    }
    try {
      const types = JSON.parse(productType);
      if (Array.isArray(types)) {
        // 如果是数组，直接返回
        return types;
      } else if (typeof types === 'object') {
        // 如果是单个对象，转换为数组返回
        return [types];
      }
      // 如果是其他格式，返回空数组
      return [];
    } catch (error) {
      // 如果解析失败，返回空数组
      return [];
    }
  }

  onMounted(() => {
    handleQuery();
  });
</script>

<style scoped>
.pagination-wrapper {
  flex-shrink: 0;
  padding: 20px 0;
  border-top: 1px solid #ebeef5;
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
</style>
