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
            v-hasPerm="['system:userFavorite:add']"
            type="success"
            @click="handleOpenDialog()"
        >
          <template #icon><Plus /></template>
          新增
        </el-button>
        <el-button
            v-hasPerm="['system:userFavorite:delete']"
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
                          v-if="scope.row.productImages"
                          :src="JSON.parse(scope.row.productImages)[0]"
                          style="width: 60px; height: 60px"
                          fit="cover"
                          :preview-src-list="JSON.parse(scope.row.productImages)"
                          preview-teleported
                        />
                        <span v-else>无图片</span>
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
        <el-table-column fixed="right" label="操作" width="220">
          <template #default="scope">
            <el-button
                v-hasPerm="['system:userFavorite:edit']"
                type="primary"
                size="small"
                link
                @click="handleOpenDialog(scope.row.id)"
            >
              <template #icon><Edit /></template>
              编辑
            </el-button>
            <el-button
                v-hasPerm="['system:userFavorite:delete']"
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

    <!-- 收藏表单弹窗 -->
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

                <el-form-item label="识别用户的唯一标识" prop="userId">
                      <el-input
                          v-model="formData.userId"
                          placeholder="识别用户的唯一标识"
                      />
                </el-form-item>

                <el-form-item label="商品的id" prop="productId">
                      <el-input
                          v-model="formData.productId"
                          placeholder="商品的id"
                      />
                </el-form-item>

                <el-form-item label="是否软删除" prop="isDeleted">
                      <el-input
                          v-model="formData.isDeleted"
                          placeholder="是否软删除"
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
    name: "UserFavorite",
    inheritAttrs: false,
  });

  import UserFavoriteAPI, { UserFavoritePageVO, UserFavoriteForm, UserFavoritePageQuery } from "@/api/system/userFavorite";
  import { useUserStoreHook } from "@/store/modules/user.store";

  const queryFormRef = ref();
  const dataFormRef = ref();
  const userStore = useUserStoreHook();

  const loading = ref(false);
  const removeIds = ref<number[]>([]);
  const total = ref(0);

  const queryParams = reactive<UserFavoritePageQuery>({
    pageNum: 1,
    pageSize: 10,
  });

  // 收藏表格数据
  const pageData = ref<UserFavoritePageVO[]>([]);

  // 弹窗
  const dialog = reactive({
    title: "",
    visible: false,
  });

  // 收藏表单数据
  const formData = reactive<UserFavoriteForm>({});

  // 收藏表单校验规则
  const rules = reactive({
                      id: [{ required: true, message: "请输入", trigger: "blur" }],
                      userId: [{ required: true, message: "请输入识别用户的唯一标识", trigger: "blur" }],
                      productId: [{ required: true, message: "请输入商品的id", trigger: "blur" }],
                      isDeleted: [{ required: true, message: "请输入是否软删除", trigger: "blur" }],
                      createTime: [{ required: true, message: "请输入创建时间", trigger: "blur" }],
                      updateTime: [{ required: true, message: "请输入更新时间", trigger: "blur" }],
  });

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

  /** 行复选框选中记录选中ID集合 */
  function handleSelectionChange(selection: any) {
    removeIds.value = selection.map((item: any) => item.id);
  }

  /** 打开收藏弹窗 */
  function handleOpenDialog(id?: number) {
    dialog.visible = true;
    if (id) {
      dialog.title = "修改收藏";
            UserFavoriteAPI.getFormData(id).then((data) => {
        Object.assign(formData, data);
      });
    } else {
      dialog.title = "新增收藏";
      // 清空表单数据，特别是userId
      Object.assign(formData, { userId: undefined });
    }
  }

  /** 提交收藏表单 */
  function handleSubmit() {
    dataFormRef.value.validate((valid: any) => {
      if (valid) {
        loading.value = true;
        const id = formData.id;
        if (id) {
                UserFavoriteAPI.update(id, formData)
              .then(() => {
                ElMessage.success("修改成功");
                handleCloseDialog();
                handleResetQuery();
              })
              .finally(() => (loading.value = false));
        } else {
          // 新增时设置当前用户ID
          const userId = userStore.userInfo.userId;
          if (!userId) {
            ElMessage.error("请先登录");
            loading.value = false;
            return;
          }
          formData.userId = parseInt(userId);
                UserFavoriteAPI.add(formData)
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

  /** 关闭收藏弹窗 */
  function handleCloseDialog() {
    dialog.visible = false;
    dataFormRef.value.resetFields();
    dataFormRef.value.clearValidate();
    formData.id = undefined;
  }

  /** 删除收藏 */
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
                UserFavoriteAPI.deleteByIds(ids)
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
