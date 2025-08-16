<template>
  <view class="page">
    <view class="content">
      <view class="header">
        <text class="title">我的</text>
      </view>
      
      <!-- 用户信息区域 -->
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
      
      <!-- 功能菜单 -->
      <view class="menu-section">
        <view class="menu-item" @click="handleOrderClick">
          <image class="menu-icon" src="/static/images/category.png"></image>
          <text class="menu-text">我的订单</text>
          <text class="arrow">></text>
        </view>
        
        <view class="menu-item" @click="handleAddressClick">
          <image class="menu-icon" src="/static/images/logistics.png"></image>
          <text class="menu-text">收货地址</text>
          <text class="arrow">></text>
        </view>
        
        <view class="menu-item" @click="handleSettingsClick">
          <image class="menu-icon" src="/static/images/my.png"></image>
          <text class="menu-text">设置</text>
          <text class="arrow">></text>
        </view>
      </view>
      
      <!-- 退出登录按钮 -->
      <view class="logout-section">
        <button class="logout-btn" @click="handleLogout">退出登录</button>
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
      currentPath: '/ubPackages_user/pages/mine/mine',
      userInfo: {}
    }
  },
  onLoad() {
    this.loadUserInfo()
  },
  
  onShow() {
    this.loadUserInfo()
  },
  
  methods: {
    // 加载用户信息
    loadUserInfo() {
      this.userInfo = getUserInfo() || {}
      console.log('用户端我的页面 - 用户信息:', this.userInfo)
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
    
    // 我的订单
    handleOrderClick() {
      uni.showToast({
        title: '我的订单功能开发中',
        icon: 'none'
      })
    },
    
    // 收货地址
    handleAddressClick() {
      uni.showToast({
        title: '收货地址功能开发中',
        icon: 'none'
      })
    },
    
    // 设置
    handleSettingsClick() {
      uni.showToast({
        title: '设置功能开发中',
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
            
            uni.showToast({
              title: '已退出登录',
              icon: 'success'
            })
            
            // 立即跳转到主包首页，清空页面栈
            uni.reLaunch({
              url: '/pages/index/index'
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
  background-color: #f5f5f5;
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

/* 用户信息区域 */
.user-info-section {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 40rpx;
  margin-bottom: 40rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30rpx;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  margin-bottom: 20rpx;
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

.user-details {
  text-align: left;
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

/* 功能菜单 */
.menu-section {
  background-color: #fff;
  border-radius: 16rpx;
  margin-bottom: 40rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 30rpx 40rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon {
  width: 40rpx;
  height: 40rpx;
  margin-right: 20rpx;
}

.menu-text {
  font-size: 28rpx;
  color: #333;
  flex: 1;
}

.arrow {
  font-size: 28rpx;
  color: #999;
  font-weight: bold;
}

/* 退出登录按钮 */
.logout-section {
  text-align: center;
}

.logout-btn {
  background-color: #ff4757;
  color: white;
  border-radius: 16rpx;
  padding: 24rpx 60rpx;
  font-size: 32rpx;
  border: none;
  width: 100%;
  max-width: 400rpx;
}
</style>