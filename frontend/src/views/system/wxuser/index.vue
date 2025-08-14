<template>
  <div class="app-container">
      <div class="search-container">
        <el-form ref="queryFormRef" :model="queryParams" :inline="true">
          <el-form-item label="微信昵称" prop="nickname">
            <el-input
              v-model="queryParams.nickname"
              placeholder="请输入微信昵称"
              clearable
              style="width: 200px"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input
              v-model="queryParams.phone"
              placeholder="请输入手机号"
              clearable
              style="width: 200px"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item label="真实姓名" prop="realName">
            <el-input
              v-model="queryParams.realName"
              placeholder="请输入真实姓名"
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

      <div class="search-container">
        <el-button type="primary" @click="handleOpenDialog()">
          <template #icon><Plus /></template>
          新增
        </el-button>
        <el-button type="danger" :disabled="removeIds.length === 0" @click="handleDelete()">
          <template #icon><Delete /></template>
          删除
        </el-button>
      </div>

      <!-- 数据表格 -->
      <el-card shadow="never" class="table-container">
        <el-table
          ref="dataTableRef"
          v-loading="loading"
          :data="pageData"
          @selection-change="handleSelectionChange"
          @row-dblclick="handleOpenDialog"
          border
          stripe
        >
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="用户ID" prop="id" width="80" />
          <el-table-column label="OpenID" prop="openid" width="200" show-overflow-tooltip />
          <el-table-column label="UnionID" prop="unionid" width="200" show-overflow-tooltip />
          <el-table-column label="微信昵称" prop="nickname" width="150" show-overflow-tooltip />
          <el-table-column label="头像" prop="avatar" width="80">
             <template #default="{ row }">
               <el-avatar v-if="row.avatar" :src="row.avatar" :size="40" />
               <span v-else class="text-gray-400">无头像</span>
             </template>
           </el-table-column>
          <el-table-column label="真实姓名" prop="realName" width="120" show-overflow-tooltip />
          <el-table-column label="手机号" prop="phone" width="130" />
          <el-table-column label="性别" prop="gender" width="80">
            <template #default="{ row }">
              <el-tag v-if="row.gender === 1" type="primary">男</el-tag>
              <el-tag v-else-if="row.gender === 2" type="danger">女</el-tag>
              <span v-else class="text-gray-400">未知</span>
            </template>
          </el-table-column>
          <el-table-column label="省份" prop="province" width="100" show-overflow-tooltip />
          <el-table-column label="城市" prop="city" width="100" show-overflow-tooltip />
          <el-table-column label="地区" prop="district" width="100" show-overflow-tooltip />
          <el-table-column label="管理员" prop="isManager" width="80">
              <template #default="{ row }">
                <el-tag v-if="row.isManager === 1" type="success">是</el-tag>
                <el-tag v-else type="info">否</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="超级管理员" prop="isSupermanager" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.isSupermanager === 1" type="warning">是</el-tag>
                <el-tag v-else type="info">否</el-tag>
              </template>
            </el-table-column>
          <el-table-column label="账户余额" prop="balance" width="100">
            <template #default="{ row }">
              ¥{{ row.balance || 0 }}
            </template>
          </el-table-column>
          <el-table-column label="软删除" prop="isDeleted" width="80">
             <template #default="{ row }">
               <el-tag v-if="row.isDeleted === 1" type="danger">是</el-tag>
               <el-tag v-else type="success">否</el-tag>
             </template>
           </el-table-column>
          <el-table-column label="创建时间" prop="createTime" width="180" />
          <el-table-column label="更新时间" prop="updateTime" width="180" />
          <el-table-column label="删除时间" prop="deleteTime" width="180" show-overflow-tooltip />
          <el-table-column label="操作" width="150" align="center" fixed="right">
            <template #default="{ row }">
              <el-button
                type="primary"
                size="small"
                link
                @click="handleOpenDialog(row.id)"
              >
                <template #icon><Edit /></template>
                编辑
              </el-button>
              <el-button
                type="danger"
                size="small"
                link
                @click="handleDelete(row.id)"
              >
                <template #icon><Delete /></template>
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <pagination
          v-if="total > 0"
          v-model:total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="handleQuery"
        />
     </div>

     <!-- 用户弹窗 -->
      <el-dialog
        v-model="dialog.visible"
        :title="dialog.title"
        width="800px"
        @close="handleCloseDialog"
      >
        <el-form
          ref="dataFormRef"
          :model="formData"
          :rules="rules"
          label-width="100px"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="OpenID" prop="openid">
                <el-input v-model="formData.openid" placeholder="请输入微信用户唯一ID" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="UnionID" prop="unionid">
                <el-input v-model="formData.unionid" placeholder="请输入微信开放平台唯一ID" />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="微信昵称" prop="nickname">
                <el-input v-model="formData.nickname" placeholder="请输入微信昵称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="formData.phone" placeholder="请输入手机号" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="formData.gender">
                  <el-radio-button :value="1">男</el-radio-button>
                  <el-radio-button :value="2">女</el-radio-button>
                  <el-radio-button :value="0">未知</el-radio-button>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-form-item label="头像" prop="avatar">
                <SingleImageUpload v-model="formData.avatar" />
              </el-form-item>
          
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="省份" prop="province">
                <el-input v-model="formData.province" placeholder="请输入省份" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="城市" prop="city">
                <el-input v-model="formData.city" placeholder="请输入城市" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="地区" prop="district">
                <el-input v-model="formData.district" placeholder="请输入地区" />
              </el-form-item>
            </el-col>
          </el-row>
           
           <el-row :gutter="20">
             <el-col :span="12">
               <el-form-item label="管理员" prop="isManager">
                 <el-switch 
                   v-model="formData.isManager" 
                   active-text="是" 
                   inactive-text="否"
                   active-color="#13ce66"
                   inactive-color="#ff4949"
                   :active-value="1"
                   :inactive-value="0"
                 />
               </el-form-item>
             </el-col>
             <el-col :span="12">
               <el-form-item label="超级管理员" prop="isSupermanager">
                 <el-switch 
                   v-model="formData.isSupermanager" 
                   active-text="是" 
                   inactive-text="否"
                   active-color="#f7ba2a"
                   inactive-color="#ff4949"
                   :active-value="1"
                   :inactive-value="0"
                 />
               </el-form-item>
             </el-col>
           </el-row>
           
           <el-row :gutter="20">
             <el-col :span="12">
               <el-form-item label="账户余额" prop="balance">
                 <el-input-number 
                   v-model="formData.balance" 
                   :precision="2" 
                   :min="0" 
                   :step="0.01"
                   controls-position="right"
                   style="width: 200px"
                 >
                   <template #prepend>¥</template>
                 </el-input-number>
               </el-form-item>
             </el-col>
             <el-col :span="12">
               <el-form-item label="软删除" prop="isDeleted">
                 <el-switch 
                   v-model="formData.isDeleted" 
                   active-text="已删除" 
                   inactive-text="正常"
                   active-color="#ff4949"
                   inactive-color="#13ce66"
                   :active-value="1"
                   :inactive-value="0"
                 />
               </el-form-item>
             </el-col>
           </el-row>
           
           <el-row :gutter="20">
             <el-col :span="8">
               <el-form-item label="创建时间" prop="createTime">
                 <el-date-picker
                   v-model="formData.createTime"
                   type="datetime"
                   placeholder="选择创建时间"
                   format="YYYY-MM-DD HH:mm:ss"
                   value-format="YYYY-MM-DD HH:mm:ss"
                   style="width: 100%"
                 />
               </el-form-item>
             </el-col>
             <el-col :span="8">
               <el-form-item label="更新时间" prop="updateTime">
                 <el-date-picker
                   v-model="formData.updateTime"
                   type="datetime"
                   placeholder="选择更新时间"
                   format="YYYY-MM-DD HH:mm:ss"
                   value-format="YYYY-MM-DD HH:mm:ss"
                   style="width: 100%"
                 />
               </el-form-item>
             </el-col>
             <el-col :span="8">
               <el-form-item label="删除时间" prop="deleteTime">
                 <el-date-picker
                   v-model="formData.deleteTime"
                   type="datetime"
                   placeholder="请选择删除时间"
                   format="YYYY-MM-DD HH:mm:ss"
                   value-format="YYYY-MM-DD HH:mm:ss"
                   style="width: 100%"
                 />
               </el-form-item>
             </el-col>
           </el-row>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="handleCloseDialog">取消</el-button>
            <el-button type="primary" @click="handleSubmit">确定</el-button>
          </div>
        </template>
      </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Edit, Delete, Search, Refresh } from "@element-plus/icons-vue";
import Pagination from "@/components/Pagination/index.vue";
import SingleImageUpload from "@/components/Upload/SingleImageUpload.vue";

  defineOptions({
    name: "wxUser",
    inheritAttrs: false,
  });

  import wxUserAPI, { wxUserPageVO, wxUserForm, wxUserPageQuery } from "@/api/system/wxuser";

  const queryFormRef = ref();
  const dataFormRef = ref();

  const loading = ref(false);
  const removeIds = ref<number[]>([]);
  const total = ref(0);

  const queryParams = reactive<wxUserPageQuery>({
    pageNum: 1,
    pageSize: 10,
  });

  // 用户表格数据
  const pageData = ref<wxUserPageVO[]>([]);

  // 弹窗
  const dialog = reactive({
    title: "",
    visible: false,
  });

  // 用户表单数据
  const formData = reactive<wxUserForm>({});

  // 用户表单校验规则
  const rules = reactive({
    openid: [{ required: true, message: "请输入微信用户唯一ID", trigger: "blur" }],
    nickname: [{ required: true, message: "请输入微信昵称", trigger: "blur" }],
  });

  /** 查询用户 */
  function handleQuery() {
    loading.value = true;
          wxUserAPI.getPage(queryParams)
        .then((data) => {
          pageData.value = data.list;
          total.value = data.total;
        })
        .finally(() => {
          loading.value = false;
        });
  }

  /** 重置用户查询 */
  function handleResetQuery() {
    queryFormRef.value!.resetFields();
    queryParams.pageNum = 1;
    handleQuery();
  }

  /** 行复选框选中记录选中ID集合 */
  function handleSelectionChange(selection: any) {
    removeIds.value = selection.map((item: any) => item.id);
  }

  /** 打开用户弹窗 */
  function handleOpenDialog(id?: number) {
    dialog.visible = true;
    if (id) {
      dialog.title = "修改用户";
            wxUserAPI.getFormData(id).then((data) => {
        Object.assign(formData, data);
      });
    } else {
      dialog.title = "新增用户";
    }
  }

  /** 提交用户表单 */
  function handleSubmit() {
    dataFormRef.value.validate((valid: any) => {
      if (valid) {
        loading.value = true;
        const id = formData.id;
        if (id) {
                wxUserAPI.update(id, formData)
              .then(() => {
                ElMessage.success("修改成功");
                handleCloseDialog();
                handleResetQuery();
              })
              .finally(() => (loading.value = false));
        } else {
                wxUserAPI.add(formData)
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

  /** 关闭用户弹窗 */
  function handleCloseDialog() {
    dialog.visible = false;
    dataFormRef.value.resetFields();
    dataFormRef.value.clearValidate();
    
    // 重置表单数据
    Object.assign(formData, {
      id: undefined,
      openid: '',
      unionid: '',
      nickname: '',
      avatar: '',
      realName: '',
      phone: '',
      gender: 0,
      province: '',
      city: '',
      district: '',
      isManager: 0,
      isSupermanager: 0,
      balance: 0,
      isDeleted: 0,
      createTime: undefined,
      updateTime: undefined,
      deleteTime: undefined
    });
  }

  /** 删除用户 */
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
                wxUserAPI.deleteByIds(ids)
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

.table-container {
  margin-top: 16px;
}

.app-container {
  padding: 20px;
}

:deep(.el-card__body) {
  padding: 20px;
}

.pagination-wrapper {
  flex-shrink: 0;
  padding: 20px 0;
  border-top: 1px solid #ebeef5;
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
</style>
