<template>
  <el-container class="admin-container">
    <!-- 顶部导航栏 -->
    <el-header class="admin-header">
      <div class="header-left">
        <div class="logo">
          <img src="/logo.png" alt="Logo" class="logo-img" />
          <span class="logo-text">农康优选</span>
        </div>
      </div>
      <div class="header-right">
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
            <span class="username">{{ adminInfo.nickname || '管理员' }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="password">修改密码</el-dropdown-item>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="200px" class="admin-aside">
        <el-menu
          :default-active="$route.path"
          class="admin-menu"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/dashboard">
            <el-icon><DataBoard /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          <el-menu-item index="/products">
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </el-menu-item>
          <el-menu-item index="/orders">
            <el-icon><List /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/customer-service">
            <el-icon><Service /></el-icon>
            <span>客服管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区域 -->
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown, DataBoard, Goods, List, User, Service } from '@element-plus/icons-vue'

export default {
  name: 'AdminLayout',
  components: {
    ArrowDown,
    DataBoard,
    Goods,
    List,
    User,
    Service
  },
  setup() {
    const router = useRouter()
    const adminInfo = ref({})
    
    onMounted(() => {
      // 从localStorage获取管理员信息
      const adminInfoStr = localStorage.getItem('adminInfo')
      if (adminInfoStr) {
        try {
          adminInfo.value = JSON.parse(adminInfoStr)
        } catch (error) {
          console.error('解析管理员信息失败:', error)
        }
      }
    })
    
    const handleCommand = (command) => {
      switch (command) {
        case 'profile':
          ElMessage.info('个人信息功能开发中...')
          break
        case 'password':
          ElMessage.info('修改密码功能开发中...')
          break
        case 'logout':
          ElMessageBox.confirm(
            '确定要退出登录吗？',
            '提示',
            {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }
          ).then(() => {
            // 清除本地存储
            localStorage.removeItem('adminToken')
            localStorage.removeItem('adminInfo')
            ElMessage.success('已退出登录')
            // 跳转到登录页
            router.push('/login')
          }).catch(() => {
            // 用户取消
          })
          break
      }
    }
    
    return {
      adminInfo,
      handleCommand
    }
  }
}
</script>

<style scoped>
.admin-container {
  height: 100vh;
}

.admin-header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-img {
  width: 32px;
  height: 32px;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.username {
  font-size: 14px;
  color: #606266;
}

.admin-aside {
  background: #304156;
  height: calc(100vh - 60px);
}

.admin-menu {
  border: none;
  height: 100%;
}

.admin-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
  border-bottom: 1px solid #1f2d3d;
}

.admin-menu .el-menu-item:hover {
  background-color: #263445 !important;
}

.admin-menu .el-menu-item.is-active {
  background-color: #1890ff !important;
  color: #fff !important;
}

.admin-main {
  background: #f0f2f5;
  padding: 20px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px);
}
</style> 