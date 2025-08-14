<template>
  <view class="page">
    <view class="content">
      <view class="header">
        <text class="title">我的</text>
      </view>
      
      <view class="profile-content">
        <!-- 未登录状态 -->
        <view v-if="!userStore.isLoggedIn">
          <text class="placeholder">请先登录</text>
          
          <!-- 登录按钮 -->
          <view class="login-section">
            <button class="login-btn" @click="goToLogin">登录</button>
          </view>
        </view>
        
        <!-- 已登录状态 -->
        <view v-else>
          <view class="user-info">
            <text class="username">欢迎，{{ userStore.userInfo.username }}</text>
            <text class="user-type">用户类型：{{ userStore.userInfo.userType === 'user' ? '普通用户' : '商户' }}</text>
          </view>
          
          <view class="actions">
            <button class="logout-btn" @click="handleLogout">退出登录</button>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 底部导航栏 -->
    <TabBar :currentPath="currentPath" />
  </view>
</template>

<script>
import TabBar from '@/components/TabBar.vue'
import { useUserStore } from '@/store/modules/user'

export default {
  components: {
    TabBar
  },
  data() {
    return {
      currentPath: '/pages/mine/profile'
    }
  },
  computed: {
    userStore() {
      return useUserStore()
    }
  },
  onLoad() {
    console.log('我的页面加载（主包）')
  },
  onShow() {
    // 如果用户已登录，自动跳转到用户分包的我的页面
    if (this.userStore.isLoggedIn && this.userStore.isUser) {
      console.log('用户已登录，跳转到用户分包我的页面')
      uni.navigateTo({
        url: '/subPackages_user/pages/mine/mine'
      })
    }
  },
  methods: {
    // 跳转到登录页面
    goToLogin() {
      uni.navigateTo({
        url: '/pages/login/login'
      })
    },
    
    // 退出登录
    handleLogout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            this.userStore.logout()
            uni.showToast({
              title: '已退出登录',
              icon: 'success'
            })
          }
        }
      })
    }
  }
}
</script>

<style>
.page {
  min-height: 100vh;
  padding-bottom: calc(100rpx + env(safe-area-inset-bottom));
}

.content {
  padding: 40rpx 20rpx;
}

.header {
  text-align: center;
  margin-bottom: 40rpx;
}

.title {
  font-size: 36rpx;
  color: #333;
  font-weight: bold;
}

.profile-content {
  text-align: center;
  margin-top: 100rpx;
}

.placeholder {
  font-size: 28rpx;
  color: #666;
}

.login-section {
  margin-top: 60rpx;
}

.login-btn, .logout-btn {
  border-radius: 8rpx;
  padding: 24rpx 60rpx;
  font-size: 32rpx;
  border: none;
  margin: 0 auto;
  display: block;
  width: 300rpx;
}

.login-btn {
  background-color: #007aff;
  color: white;
}

.logout-btn {
  background-color: #ff4757;
  color: white;
}

.user-info {
  margin-bottom: 60rpx;
}

.username {
  display: block;
  font-size: 32rpx;
  color: #333;
  margin-bottom: 20rpx;
}

.user-type {
  display: block;
  font-size: 28rpx;
  color: #666;
}

.actions {
  margin-top: 60rpx;
}
</style>