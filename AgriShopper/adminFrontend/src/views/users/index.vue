<template>
  <div class="users-container">
    <div class="page-header">
      <h1>用户管理</h1>
    </div>
    
    <el-card class="placeholder-card" shadow="never">
      <div class="placeholder-content">
        <el-icon><User /></el-icon>
        <h3>用户管理功能</h3>
        <p>用户管理功能正在开发中，敬请期待...</p>
      </div>
    </el-card>
  </div>
</template>

<script>
import { User } from '@element-plus/icons-vue'

export default {
  name: 'UsersManagement',
  components: {
    User
  }
}
</script>

<style scoped>
.users-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  color: #303133;
  margin: 0;
}

.placeholder-card {
  height: 400px;
}

.placeholder-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
}

.placeholder-content .el-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.placeholder-content h3 {
  font-size: 20px;
  margin-bottom: 10px;
}

.placeholder-content p {
  font-size: 14px;
}
</style> 