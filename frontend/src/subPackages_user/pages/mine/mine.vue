<template>
  <view class="page">
    <view class="content">
      <!-- 用户信息卡片 -->
      <view class="user-profile">
        <view class="avatar-container">
          <image 
            class="avatar" 
            :src="userStore.isLoggedIn ? (userStore.userInfo.avatar || defaultAvatar) : defaultAvatar" 
            mode="aspectFill"
          />
        </view>
        
        <view class="user-info" v-if="userStore.isLoggedIn">
          <text class="username">{{ userStore.userInfo.username || '用户' }}</text>
          <text class="user-type">{{ userStore.userInfo.userType === 'user' ? '普通用户' : '商户用户' }}</text>
        </view>
      </view>
      
      <!-- 功能菜单 -->
      <view class="menu-container">
        <view class="menu-item" @click="navigateToProfile" v-if="userStore.isLoggedIn">
          <view class="menu-icon">👤</view>
          <text class="menu-text">个人资料</text>
          <text class="menu-arrow">></text>
        </view>
        
        <view class="menu-item" @click="navigateToOrders" v-if="userStore.isLoggedIn">
          <view class="menu-icon">📦</view>
          <text class="menu-text">我的订单</text>
          <text class="menu-arrow">></text>
        </view>
        
        <view class="menu-item" @click="navigateToSettings">
          <view class="menu-icon">⚙️</view>
          <text class="menu-text">设置</text>
          <text class="menu-arrow">></text>
        </view>
        
        <view class="menu-item" @click="navigateToAbout">
          <view class="menu-icon">ℹ️</view>
          <text class="menu-text">关于我们</text>
          <text class="menu-arrow">></text>
        </view>
      </view>
      
      <!-- 操作按钮 -->
      <view class="actions">
        <button class="logout-btn" @click="handleLogout" v-if="userStore.isLoggedIn">退出登录</button>
      </view>
    </view>
    
    <!-- 底部导航栏 -->
    <TabBar :currentPath="currentPath" />
  </view>
</template>

<script>
import TabBar from '@/components/TabBar.vue'
import { useUserStore } from '@/store/modules/user'
import { goToHome } from '@/utils/navigationHandler.js'

export default {
  components: {
    TabBar
  },
  data() {
    return {
      currentPath: '/subPackages_user/pages/mine/mine',
      defaultAvatar: '/static/images/default-avatar.png'
    }
  },
  computed: {
    userStore() {
      return useUserStore()
    }
  },
  onLoad() {
    console.log('我的页面加载（用户分包）')
  },
  onNavigationBarButtonTap(e) {
    if (e.index === 0) {
      goToHome()
    }
  },
  // 重写返回按钮行为
  onBackPress() {
    goToHome()
    return true // 阻止默认返回行为
  },
  methods: {
    // 跳转到登录页面
    goToLogin() {
      uni.navigateTo({
        url: '/pages/login/login'
      })
    },
    
    // 跳转到个人资料
    navigateToProfile() {
      uni.navigateTo({
        url: '/pages/mine/profile'
      })
    },
    
    // 跳转到我的订单
    navigateToOrders() {
      uni.showToast({
        title: '功能开发中',
        icon: 'none'
      })
    },
    
    // 跳转到设置
    navigateToSettings() {
      uni.showToast({
        title: '功能开发中',
        icon: 'none'
      })
    },
    
    // 跳转到关于我们
    navigateToAbout() {
      uni.showToast({
        title: '功能开发中',
        icon: 'none'
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
            
            // 跳转到首页
            setTimeout(() => {
              uni.switchTab({
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
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
  background-color: #f5f5f5;
}

.content {
  padding: 0;
}

/* 用户信息卡片 */
.user-profile {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60rpx 40rpx 40rpx;
  text-align: center;
  color: white;
}

.avatar-container {
  margin-bottom: 30rpx;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  border: 4rpx solid rgba(255, 255, 255, 0.8);
}

.user-info {
  margin-bottom: 20rpx;
}

.username {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  margin-bottom: 12rpx;
}

.user-type {
  display: block;
  font-size: 26rpx;
  opacity: 0.9;
}

/* 功能菜单 */
.menu-container {
  background-color: #fff;
  margin: 20rpx;
  border-radius: 12rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 30rpx 24rpx;
  border-bottom: 1rpx solid #f0f0f0;
  transition: background-color 0.2s;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:active {
  background-color: #f8f8f8;
}

.menu-icon {
  font-size: 36rpx;
  margin-right: 24rpx;
  width: 40rpx;
  text-align: center;
}

.menu-text {
  flex: 1;
  font-size: 30rpx;
  color: #333;
}

.menu-arrow {
  font-size: 24rpx;
  color: #999;
}

/* 操作按钮 */
.actions {
  padding: 40rpx;
}

.login-btn, .logout-btn {
  width: 100%;
  border-radius: 12rpx;
  padding: 30rpx;
  font-size: 32rpx;
  font-weight: 500;
  border: none;
  transition: opacity 0.2s;
}

.login-btn {
  background-color: #007aff;
  color: white;
}

.logout-btn {
  background-color: #ff4757;
  color: white;
}

.login-btn:active, .logout-btn:active {
  opacity: 0.8;
}
</style>