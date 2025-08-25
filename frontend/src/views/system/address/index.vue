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
            v-hasPerm="['system:address:delete']"
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
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="userId"
                        label="关联用户ID"
                        prop="userId"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="receiverName"
                        label="收货人姓名"
                        prop="receiverName"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="phone"
                        label="联系电话"
                        prop="phone"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="province"
                        label="省份"
                        prop="province"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="city"
                        label="城市"
                        prop="city"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="district"
                        label="区县"
                        prop="district"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="detailAddress"
                        label="详细地址"
                        prop="detailAddress"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="postalCode"
                        label="邮政编码"
                        prop="postalCode"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="isDefault"
                        label="是否默认地址"
                        prop="isDefault"
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
                v-hasPerm="['system:address:edit']"
                type="primary"
                size="small"
                link
                @click="handleOpenDialog(scope.row.id)"
            >
              <template #icon><Edit /></template>
              编辑
            </el-button>
            <el-button
                v-hasPerm="['system:address:delete']"
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

    <!-- 存储用户收货地址信息表单弹窗 -->
    <el-dialog
        v-model="dialog.visible"
        title="修改地址"
        width="500px"
        @close="handleCloseDialog"
    >
      <el-form ref="dataFormRef" :model="formData" :rules="rules" label-width="100px">
                <el-form-item label="地址ID" prop="id">
                      <el-input
                          v-model="formData.id"
                          placeholder="地址ID"
                          readonly
                      />
                </el-form-item>

                <el-form-item label="关联用户ID" prop="userId">
                      <el-input
                          v-model="formData.userId"
                          placeholder="关联用户ID"
                      />
                </el-form-item>

                <el-form-item label="收货人姓名" prop="receiverName">
                      <el-input
                          v-model="formData.receiverName"
                          placeholder="收货人姓名"
                      />
                </el-form-item>

                <el-form-item label="联系电话" prop="phone">
                      <el-input
                          v-model="formData.phone"
                          placeholder="联系电话"
                      />
                </el-form-item>

                <el-form-item label="省份" prop="province">
                      <el-input
                          v-model="formData.province"
                          placeholder="省份"
                      />
                </el-form-item>

                <el-form-item label="城市" prop="city">
                      <el-input
                          v-model="formData.city"
                          placeholder="城市"
                      />
                </el-form-item>

                <el-form-item label="区县" prop="district">
                      <el-input
                          v-model="formData.district"
                          placeholder="区县"
                      />
                </el-form-item>

                <el-form-item label="详细地址" prop="detailAddress">
                      <el-input
                          v-model="formData.detailAddress"
                          placeholder="详细地址"
                      />
                </el-form-item>

                <el-form-item label="邮政编码" prop="postalCode">
                      <el-input
                          v-model="formData.postalCode"
                          placeholder="邮政编码"
                      />
                </el-form-item>

                <el-form-item label="是否默认地址" prop="isDefault">
                      <el-select
                          v-model="formData.isDefault"
                          placeholder="请选择是否默认地址"
                      >
                        <el-option label="是" :value="1" />
                        <el-option label="否" :value="0" />
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
    name: "Address",
    inheritAttrs: false,
  });

  import AddressAPI, { AddressPageVO, AddressForm, AddressPageQuery } from "@/api/system/address";

  const queryFormRef = ref();
  const dataFormRef = ref();

  const loading = ref(false);
  const removeIds = ref<number[]>([]);
  const total = ref(0);

  const queryParams = reactive<AddressPageQuery>({
    pageNum: 1,
    pageSize: 10,
  });

  // 地址表格数据
  const pageData = ref<AddressPageVO[]>([]);

  // 弹窗
  const dialog = reactive({
    visible: false,
  });

  // 地址表单数据
  const formData = reactive<AddressForm>({});

  // 地址表单校验规则
  const rules = reactive({
                      receiverName: [{ required: true, message: "请输入收货人姓名", trigger: "blur" }],
                      phone: [{ required: true, message: "请输入联系电话", trigger: "blur" }],
                      province: [{ required: true, message: "请输入省份", trigger: "blur" }],
                      city: [{ required: true, message: "请输入城市", trigger: "blur" }],
                      district: [{ required: true, message: "请输入区县", trigger: "blur" }],
                      detailAddress: [{ required: true, message: "请输入详细地址", trigger: "blur" }],
  });

  /** 查询地址列表 */
  function handleQuery() {
    loading.value = true;
          AddressAPI.getPage(queryParams)
        .then((data) => {
          pageData.value = data.list;
          total.value = data.total;
        })
        .finally(() => {
          loading.value = false;
        });
  }

  /** 重置地址查询 */
  function handleResetQuery() {
    queryFormRef.value!.resetFields();
    queryParams.pageNum = 1;
    handleQuery();
  }

  /** 行复选框选中记录选中ID集合 */
  function handleSelectionChange(selection: any) {
    removeIds.value = selection.map((item: any) => item.id);
  }

  /** 打开地址编辑弹窗 */
  function handleOpenDialog(id: number) {
    dialog.visible = true;
    AddressAPI.getFormData(id).then((data) => {
      Object.assign(formData, data);
    });
  }

  /** 提交地址表单 */
  function handleSubmit() {
    dataFormRef.value.validate((valid: any) => {
      if (valid) {
        loading.value = true;
        const id = formData.id;
        AddressAPI.update(id, formData)
          .then(() => {
            ElMessage.success("修改成功");
            handleCloseDialog();
            handleResetQuery();
          })
          .finally(() => (loading.value = false));
      }
    });
  }

  /** 关闭地址弹窗 */
  function handleCloseDialog() {
    dialog.visible = false;
    dataFormRef.value.resetFields();
    dataFormRef.value.clearValidate();
    formData.id = undefined;
  }

  /** 删除地址 */
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
                AddressAPI.deleteByIds(ids)
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
