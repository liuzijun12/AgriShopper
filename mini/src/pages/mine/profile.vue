<template>
  <view class="page">
    <!-- 已登录状态 -->
    <view v-if="isLoggedIn" class="profile-page">
      <!-- 页面标题 -->
      <view class="page-header">
        <text class="page-title">我的</text>
      </view>
      
      <!-- 用户信息卡片 -->
      <view class="user-card">
        <view class="user-info">
          <!-- 头像 -->
          <view class="avatar-container">
            <image class="avatar" :src="userInfo.avatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
            <view class="online-dot"></view>
          </view>
          
          <!-- 用户基本信息 -->
          <view class="user-details">
            <text class="username">{{ getUserDisplayName() }}</text>
            <text class="phone-number">138****8888</text>
            <view class="user-badge">
              <text class="badge-text">编辑资料</text>
            </view>
          </view>
          
          <!-- 右侧功能 -->
          <view class="user-actions">
            <view class="action-item">
              <text class="action-label">地址</text>
              <text class="action-label">管理</text>
              <view class="red-dot"></view>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 功能按钮区域 -->
      <view class="function-buttons">
        <view class="function-item" @click="handleBalance">
          <text class="amount">¥ 0.00</text>
          <text class="function-label">余额</text>
        </view>
        <view class="function-item" @click="handleFavorites">
          <text class="icon">♡</text>
          <text class="function-label">收藏</text>
        </view>
        <view class="function-item" @click="handleHistory">
          <text class="icon">↻</text>
          <text class="function-label">浏览记录</text>
        </view>
      </view>
      
      <!-- 服务项目 -->
      <view class="service-section">
        <!-- 优惠券 -->
        <view class="service-item" @click="handleCoupons">
          <view class="service-icon coupon-icon">
            <text class="icon-text">券</text>
          </view>
          <text class="service-label">优惠券</text>
          <text class="arrow">></text>
        </view>
        
        <!-- 客服 -->
        <view class="service-item" @click="handleCustomerService">
          <view class="service-icon cs-icon">
            <text class="icon-text">👩</text>
          </view>
          <text class="service-label">客服</text>
          <text class="arrow">></text>
        </view>
        
        <!-- 退出登录 -->
        <view class="service-item logout-item" @click="handleLogout">
          <view class="service-icon logout-icon">
            <text class="icon-text">⚠</text>
          </view>
          <text class="service-label">退出登录</text>
          <text class="arrow">></text>
        </view>
      </view>
    </view>
    
    <!-- 未登录状态 -->
    <view v-else class="unlogged-container">
      <!-- 背景装饰 -->
      <view class="bg-decoration">
        <view class="decoration decoration-1">🌿</view>
        <view class="decoration decoration-2">🍃</view>
        <view class="decoration decoration-3">🌱</view>
      </view>
      
      <!-- 主内容 -->
      <view class="main-content">
        <!-- 头像区域 -->
        <view class="avatar-container">
          <view class="avatar-wrapper">
            <view class="default-avatar">👤</view>
          </view>
        </view>
        
        <!-- 欢迎文字 -->
        <view class="welcome-container">
          <text class="welcome-title">我的</text>
          <text class="welcome-desc">欢迎使用</text>
        </view>
        
        <!-- 登录按钮 -->
        <view class="action-container">
          <button class="login-button" @click="goToLogin">
            <text class="login-text">登录</text>
          </button>
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
    
    // 余额管理
    handleBalance() {
      uni.showToast({
        title: '余额功能开发中',
        icon: 'none'
      })
    },
    
    // 收藏管理
    handleFavorites() {
      uni.showToast({
        title: '收藏功能开发中',
        icon: 'none'
      })
    },
    
    // 浏览记录
    handleHistory() {
      uni.showToast({
        title: '浏览记录功能开发中',
        icon: 'none'
      })
    },
    
    // 优惠券
    handleCoupons() {
      uni.showToast({
        title: '优惠券功能开发中',
        icon: 'none'
      })
    },
    
    // 客服
    handleCustomerService() {
      uni.showToast({
        title: '客服功能开发中',
        icon: 'none'
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

/* 未登录状态样式 */
.unlogged-container {
  width: 100vw;
  height: calc(100vh - 100rpx);
  background: linear-gradient(180deg, #f8f9fa 0%, #e9ecef 100%);
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: 1;
}

.decoration {
  position: absolute;
  font-size: 30rpx;
  opacity: 0.15;
}

.decoration-1 { top: 20%; left: 15%; }
.decoration-2 { top: 30%; right: 20%; }
.decoration-3 { top: 75%; left: 25%; }

/* 主内容 */
.main-content {
  position: relative;
  z-index: 2;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 0 60rpx;
}

/* 头像容器 */
.avatar-container {
  margin-bottom: 50rpx;
}

.avatar-wrapper {
  width: 160rpx;
  height: 160rpx;
  background: #ffffff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
}

.default-avatar {
  font-size: 80rpx;
  color: #cccccc;
}

/* 欢迎文字 */
.welcome-container {
  text-align: center;
  margin-bottom: 80rpx;
}

.welcome-title {
  display: block;
  font-size: 48rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 16rpx;
}

.welcome-desc {
  display: block;
  font-size: 28rpx;
  color: #666666;
}

/* 登录按钮 */
.action-container {
  width: 100%;
}

.login-button {
  width: 100%;
  height: 88rpx;
  background: #007aff;
  border: none;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6rpx 16rpx rgba(0, 122, 255, 0.2);
}

.login-text {
  font-size: 32rpx;
  color: #ffffff;
  font-weight: 500;
}

/* 已登录状态样式 */
.profile-page {
  padding: 40rpx 20rpx;
  background-color: #f5f5f5;
}

.page-header {
  text-align: center;
  margin-bottom: 30rpx;
}

.page-title {
  font-size: 36rpx;
  color: #333;
  font-weight: bold;
}

.user-card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
}

.user-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.avatar-container {
  position: relative;
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 20rpx;
}

.avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.online-dot {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 20rpx;
  height: 20rpx;
  background-color: #4caf50;
  border-radius: 50%;
  border: 2rpx solid #fff;
}

.user-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.username {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 8rpx;
}

.phone-number {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 10rpx;
}

.user-badge {
  background-color: #ffece6;
  border: 1rpx solid #ffd7c1;
  border-radius: 8rpx;
  padding: 4rpx 12rpx;
  display: inline-block;
}

.badge-text {
  font-size: 22rpx;
  color: #ff6b00;
  font-weight: bold;
}

.user-actions {
  display: flex;
  align-items: center;
  margin-top: 20rpx;
}

.action-item {
  display: flex;
  align-items: center;
  margin-right: 30rpx;
}

.action-label {
  font-size: 28rpx;
  color: #666;
  margin-left: 10rpx;
}

.red-dot {
  width: 12rpx;
  height: 12rpx;
  background-color: #ff4757;
  border-radius: 50%;
  margin-left: 10rpx;
}

.function-buttons {
  display: flex;
  justify-content: space-around;
  margin-bottom: 30rpx;
}

.function-item {
  text-align: center;
  padding: 20rpx 0;
}

.amount {
  font-size: 36rpx;
  font-weight: bold;
  color: #ff6b00;
  margin-bottom: 8rpx;
}

.function-label {
  font-size: 24rpx;
  color: #666;
}

.icon {
  font-size: 40rpx;
  color: #ff6b00;
  margin-bottom: 8rpx;
}

.service-section {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
}

.service-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.service-item:last-child {
  border-bottom: none;
}

.service-icon {
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  background-color: #ffece6;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15rpx;
}

.coupon-icon .icon-text {
  font-size: 36rpx;
  color: #ff6b00;
}

.cs-icon .icon-text {
  font-size: 36rpx;
  color: #007aff;
}

.logout-icon .icon-text {
  font-size: 36rpx;
  color: #ff4757;
}

.service-label {
  font-size: 32rpx;
  color: #333;
  font-weight: 500;
  flex: 1;
  text-align: left;
}

.arrow {
  font-size: 36rpx;
  color: #ccc;
  margin-left: 15rpx;
}
</style>