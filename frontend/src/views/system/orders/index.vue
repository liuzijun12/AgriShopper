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
            v-hasPerm="['system:orders:delete']"
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
                        label="订单ID"
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
                        key="addressSnapshot"
                        label="完整地址快照"
                        prop="addressSnapshot"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="originalAddressId"
                        label="关联原始地址"
                        prop="originalAddressId"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="status"
                        label="订单的状态"
                        prop="status"
                        min-width="150"
                        align="center"
                    />
                    <el-table-column
                        key="totalAmount"
                        label="订单总金额"
                        prop="totalAmount"
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
                v-hasPerm="['system:orders:edit']"
                type="primary"
                size="small"
                link
                @click="handleOpenDialog(scope.row.id)"
            >
              <template #icon><Edit /></template>
              编辑
            </el-button>
            <el-button
                v-hasPerm="['system:orders:delete']"
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

    <!-- 订单表单弹窗 -->
    <el-dialog
        v-model="dialog.visible"
        title="修改订单"
        width="500px"
        @close="handleCloseDialog"
    >
      <el-form ref="dataFormRef" :model="formData" :rules="rules" label-width="100px">
                <el-form-item label="订单ID" prop="id">
                      <el-input
                          v-model="formData.id"
                          placeholder="订单ID"
                          readonly
                      />
                </el-form-item>

                <el-form-item label="关联用户ID" prop="userId">
                      <el-input
                          v-model="formData.userId"
                          placeholder="关联用户ID"
                      />
                </el-form-item>

                <el-form-item label="关联原始地址" prop="originalAddressId">
                      <el-select
                          v-model="formData.originalAddressId"
                          placeholder="请选择地址"
                          style="width: 100%"
                          @change="handleAddressChange"
                      >
                        <el-option
                          v-for="address in addressOptions"
                          :key="address.id"
                          :label="address.receiverName + ' - ' + address.detailAddress"
                          :value="address.id"
                        />
                      </el-select>
                </el-form-item>

                <el-form-item label="完整地址快照" prop="addressSnapshot">
                      <el-input
                          v-model="formData.addressSnapshot"
                          placeholder="选择地址后自动生成"
                          readonly
                          type="textarea"
                          :rows="3"
                      />
                </el-form-item>

                <el-form-item label="订单的状态" prop="status">
                      <el-input
                          v-model="formData.status"
                          placeholder="订单的状态"
                      />
                </el-form-item>

                <el-form-item label="订单总金额" prop="totalAmount">
                      <el-input
                          v-model="formData.totalAmount"
                          placeholder="订单总金额"
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
    name: "Orders",
    inheritAttrs: false,
  });

  import OrdersAPI, { OrdersPageVO, OrdersForm, OrdersPageQuery } from "@/api/system/orders";
  import AddressAPI, { AddressPageVO } from "@/api/system/address";

  const queryFormRef = ref();
  const dataFormRef = ref();

  const loading = ref(false);
  const removeIds = ref<number[]>([]);
  const total = ref(0);

  const queryParams = reactive<OrdersPageQuery>({
    pageNum: 1,
    pageSize: 10,
  });

  // 订单表格数据
  const pageData = ref<OrdersPageVO[]>([]);

  // 地址选项数据
  const addressOptions = ref<AddressPageVO[]>([]);

  // 弹窗
  const dialog = reactive({
    visible: false,
  });

  // 订单表单数据
  const formData = reactive<OrdersForm>({});

  // 订单表单校验规则
  const rules = reactive({
                      userId: [{ required: true, message: "请输入关联用户ID", trigger: "blur" }],
                      originalAddressId: [{ required: true, message: "请选择关联地址", trigger: "blur" }],
                      status: [{ required: true, message: "请输入订单的状态", trigger: "blur" }],
                      totalAmount: [{ required: true, message: "请输入订单总金额", trigger: "blur" }],
  });

  /** 查询订单 */
  function handleQuery() {
    loading.value = true;
          OrdersAPI.getPage(queryParams)
        .then((data) => {
          pageData.value = data.list;
          total.value = data.total;
        })
        .finally(() => {
          loading.value = false;
        });
  }

  /** 重置订单查询 */
  function handleResetQuery() {
    queryFormRef.value!.resetFields();
    queryParams.pageNum = 1;
    handleQuery();
  }

  /** 行复选框选中记录选中ID集合 */
  function handleSelectionChange(selection: any) {
    removeIds.value = selection.map((item: any) => item.id);
  }

  /** 获取地址列表 */
  function loadAddressOptions() {
    AddressAPI.getPage({ pageNum: 1, pageSize: 100 }).then((data) => {
      addressOptions.value = data.list;
    });
  }

  /** 打开订单弹窗 */
  function handleOpenDialog(id: number) {
    dialog.visible = true;
    loadAddressOptions(); // 加载地址选项
    OrdersAPI.getFormData(id).then((data) => {
      Object.assign(formData, data);
    });
  }

  /** 提交订单表单 */
  function handleSubmit() {
    dataFormRef.value.validate((valid: any) => {
      if (valid) {
        loading.value = true;
        const id = formData.id;
        OrdersAPI.update(id, formData)
          .then(() => {
            ElMessage.success("修改成功");
            handleCloseDialog();
            handleResetQuery();
          })
          .finally(() => (loading.value = false));
      }
    });
  }

  /** 处理地址选择变化 */
  function handleAddressChange(addressId: number) {
    const selectedAddress = addressOptions.value.find(addr => addr.id === addressId);
    if (selectedAddress) {
      // 生成 JSON 格式的地址快照
      const addressSnapshot = {
        receiverName: selectedAddress.receiverName,
        phone: selectedAddress.phone,
        province: selectedAddress.province,
        city: selectedAddress.city,
        district: selectedAddress.district,
        detailAddress: selectedAddress.detailAddress,
        postalCode: selectedAddress.postalCode
      };
      formData.addressSnapshot = JSON.stringify(addressSnapshot, null, 2);
    }
  }

  /** 关闭订单弹窗 */
  function handleCloseDialog() {
    dialog.visible = false;
    dataFormRef.value.resetFields();
    dataFormRef.value.clearValidate();
    formData.id = undefined;
  }

  /** 删除订单 */
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
                OrdersAPI.deleteByIds(ids)
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
