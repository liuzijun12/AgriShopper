<template>
  <view class="page">
    <view class="content">
      <view class="header">
        <text class="title">我的</text>
      </view>
      
      <!-- 未登录状态 -->
      <view v-if="!isLoggedIn" class="profile-content">
        <view class="avatar-section">
          <image class="avatar" src="/static/images/default-avatar.png" mode="aspectFill"></image>
          <text class="welcome-text">欢迎使用</text>
        </view>
        
        <!-- 登录按钮 -->
        <view class="login-section">
          <button class="login-btn" @click="goToLogin">登录</button>
        </view>
      </view>
      
      <!-- 已登录状态 -->
      <view v-else class="profile-content">
        <view class="user-info-section">
          <view class="avatar-section">
            <image class="avatar" :src="userInfo.avatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
            <text class="username">{{ getUserDisplayName() }}</text>
            <text class="user-type">{{ getUserTypeText() }}</text>
          </view>
          
          <view class="user-details">
            <view class="detail-item" v-if="userInfo.loginType === 'wechat'">
              <text class="label">登录方式：</text>
              <text class="value">微信登录</text>
            </view>
            <view class="detail-item" v-if="userInfo.userType">
              <text class="label">用户类型：</text>
              <text class="value">{{ getUserTypeText() }}</text>
            </view>
          </view>
        </view>
        
        <!-- 退出登录按钮 -->
        <view class="logout-section">
          <button class="logout-btn" @click="handleLogout">退出登录</button>
        </view>
      </view>
    </view>
    
    <!-- 底部导航栏 -->
    <TabBar :currentPath="currentPath" />
  </view>
</template>

<script>
import TabBar from '@/components/TabBar.vue'
import { isLoggedIn, getUserInfo, logout } from '@/utils/auth'

export default {
  components: {
    TabBar
  },
  data() {
    return {
      currentPath: '/pages/mine/profile',
      isLoggedIn: false,
      userInfo: {}
    }
  },
  onLoad() {
    this.checkLoginStatus()
  },
  
  onShow() {
    // 页面显示时检查登录状态
    this.checkLoginStatus()
  },
  
  methods: {
    // 检查登录状态
    checkLoginStatus() {
      this.isLoggedIn = isLoggedIn()
      this.userInfo = getUserInfo() || {}
      console.log('我的页面 - 登录状态:', this.isLoggedIn)
      console.log('我的页面 - 用户信息:', this.userInfo)
    },
    
    // 获取用户显示名称
    getUserDisplayName() {
      if (this.userInfo.username) {
        return this.userInfo.username
      } else if (this.userInfo.loginType === 'wechat') {
        return '微信用户'
      }
      return '用户'
    },
    
    // 获取用户类型文本
    getUserTypeText() {
      switch (this.userInfo.userType) {
        case 'merchant':
          return '商户'
        case 'user':
          return '普通用户'
        default:
          return '用户'
      }
    },
    
    // 跳转到登录页面
    goToLogin() {
      uni.navigateTo({
        url: '/pages/login/login'
      })
    },
    
    // 退出登录
    handleLogout() {
      uni.showModal({
        title: '确认退出',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            // 清除登录状态
            logout()
            
            // 更新页面状态
            this.checkLoginStatus()
            
            uni.showToast({
              title: '已退出登录',
              icon: 'success'
            })
            
            // 重置导航栈，跳转到首页
            setTimeout(() => {
              uni.reLaunch({
                url: '/pages/index/index'
              })
            }, 1500)
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
  margin-top: 60rpx;
}

/* 头像区域 */
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40rpx;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  margin-bottom: 20rpx;
}

.welcome-text {
  font-size: 28rpx;
  color: #666;
}

.username {
  font-size: 32rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.user-type {
  font-size: 24rpx;
  color: #999;
  background-color: #f0f0f0;
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
}

/* 用户详情 */
.user-info-section {
  margin-bottom: 60rpx;
}

.user-details {
  margin-top: 40rpx;
  text-align: left;
  max-width: 500rpx;
  margin-left: auto;
  margin-right: auto;
}

.detail-item {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.detail-item:last-child {
  border-bottom: none;
}

.label {
  font-size: 28rpx;
  color: #666;
  width: 160rpx;
}

.value {
  font-size: 28rpx;
  color: #333;
  flex: 1;
}

/* 登录按钮 */
.login-section {
  margin-top: 60rpx;
}

.login-btn {
  background-color: #007aff;
  color: white;
  border-radius: 8rpx;
  padding: 24rpx 60rpx;
  font-size: 32rpx;
  border: none;
  margin: 0 auto;
  display: block;
  width: 300rpx;
}

/* 退出登录按钮 */
.logout-section {
  margin-top: 40rpx;
}

.logout-btn {
  background-color: #ff4757;
  color: white;
  border-radius: 8rpx;
  padding: 24rpx 60rpx;
  font-size: 32rpx;
  border: none;
  margin: 0 auto;
  display: block;
  width: 300rpx;
}
</style>