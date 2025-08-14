<template>
  <view class="custom-navbar">
    <!-- 状态栏占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <!-- 导航栏内容 -->
    <view class="navbar-content">
      <view class="navbar-left" @click="handleBack">
        <text class="back-icon">‹</text>
        <text class="back-text">返回</text>
      </view>
      
      <view class="navbar-center">
        <text class="navbar-title">{{ title }}</text>
      </view>
      
      <view class="navbar-right">
        <view class="home-btn" @click="goToHome">
          <image class="home-icon" src="/static/images/home.png" mode="aspectFit" />
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { useUserStore } from '@/store/modules/user'

export default {
  name: 'CustomNavBar',
  props: {
    title: {
      type: String,
      default: ''
    },
    showHome: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      statusBarHeight: 0
    }
  },
  computed: {
    userStore() {
      return useUserStore()
    }
  },
  mounted() {
    // 获取状态栏高度
    const systemInfo = uni.getSystemInfoSync()
    this.statusBarHeight = systemInfo.statusBarHeight || 0
  },
  methods: {
    // 处理返回按钮点击
    handleBack() {
      const pages = getCurrentPages()
      if (pages.length > 1) {
        uni.navigateBack()
      } else {
        // 如果是第一个页面，跳转到对应的首页
        this.goToHome()
      }
    },
    
    // 跳转到首页
    goToHome() {
      if (this.userStore.isLoggedIn && this.userStore.isMerchant) {
        // 商户用户跳转到商户首页
        uni.reLaunch({
          url: '/subPackages_merchant/pages/index/index'
        })
      } else {
        // 普通用户或未登录用户跳转到普通用户首页
        uni.switchTab({
          url: '/pages/index/index'
        })
      }
    }
  }
}
</script>

<style scoped>
.custom-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background-color: #fff;
  border-bottom: 1rpx solid #e5e5e5;
}

.status-bar {
  background-color: #fff;
}

.navbar-content {
  display: flex;
  align-items: center;
  height: 88rpx;
  padding: 0 20rpx;
  position: relative;
}

.navbar-left {
  display: flex;
  align-items: center;
  padding: 10rpx;
  min-width: 120rpx;
}

.back-icon {
  font-size: 40rpx;
  color: #333;
  margin-right: 8rpx;
  font-weight: bold;
}

.back-text {
  font-size: 28rpx;
  color: #333;
}

.navbar-center {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  flex: 1;
  text-align: center;
}

.navbar-title {
  font-size: 32rpx;
  color: #333;
  font-weight: 500;
}

.navbar-right {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  min-width: 120rpx;
}

.home-btn {
  padding: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.home-icon {
  width: 36rpx;
  height: 36rpx;
}
</style>