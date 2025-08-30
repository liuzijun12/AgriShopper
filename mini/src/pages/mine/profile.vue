<template>
<view class="profile-page min-h-screen py-5 px-4">
    <!-- 第一个卡片容器：用户信息（未登录状态） -->
    <view class="card-1 rounded-xl mb-5 overflow-hidden">
      <view class="p-6 flex items-center">
        <!-- 左侧：未登录头像占位 -->
        <view class="w-100 h-100 rounded-full bg-gray-200 flex items-center justify-center mr-6">
          <image src="/static/images/default-avatar.png" class="w-full h-full rounded-full" mode="aspectFill" />
        </view>

        <!-- 右侧：文本信息和按钮 -->
        <view class="flex-1 flex flex-col justify-center items-start">
          <!-- 文本区域（自然左对齐） -->
          <view class="mb-4">
            <text class="text-[#31444e] text-lg font-medium block mb-1">请登录账号</text>
            <text class="text-[#31444e]/70 text-sm">登录后可享受更多服务</text>
          </view>

          <!-- 按钮区域（无需额外设置，继承父容器左对齐） -->
          <view>
            <button class="bg-[#31444e] text-white rounded-full px-6 py-2 text-sm" @click="handleLogin">
              立即登录
            </button>
          </view>
        </view>
      </view>
    </view>
    <!-- 第二个卡片容器：余额、收藏、历史记录 -->
    <view class="card-2 bg-white rounded-xl mb-5 p-5" >
      <view class="grid grid-cols-3 gap-2">
        <!-- 余额 -->
        <view class="flex flex-col items-center p-3" @click="handleBalanceClick">
          <text class="text-[#4b5563] text-lg mb-1">--</text>
          <text class="text-[#4b5563] text-sm">余额</text>
        </view>

        <!-- 收藏 -->
        <view class="flex flex-col items-center p-3" @click="handleFavoriteClick">
          <text class="text-[#4b5563] text-lg mb-1">--</text>
          <text class="text-[#4b5563] text-sm">收藏</text>
        </view>

        <!-- 历史记录 -->
        <view class="flex flex-col items-center p-3" @click="handleHistoryClick">
          <text class="text-[#4b5563] text-lg mb-1">--</text>
          <text class="text-[#4b5563] text-sm">历史记录</text>
        </view>
      </view>
    </view>

    <!-- 第三个卡片容器：优惠券、客服 -->
    <view class="card-3 bg-[#e8f2e8] rounded-xl p-4">
      <!-- 优惠券 -->
      <view class="bg-white rounded-2xl p-5 mb-4 shadow-none" @click="handleCouponClick">
        <view class="flex items-center justify-center">
          <!-- 优惠券图标 -->
          <image src="/static/images/Coupons.png" class="w-80 h-80 mr-3" mode="aspectFit" />
          <text class="text-[#333333] text-base font-medium">优惠券</text>
        </view>
      </view>

      <!-- 客服 -->
      <view class="bg-white rounded-2xl p-5 shadow-none" @click="handleCustomerServiceClick">
        <view class="flex items-center justify-center">
          <!-- 客服图标 -->
          <image src="/static/images/Customer.png" class="w-80 h-80 mr-3" mode="aspectFit" />
          <text class="text-[#333333] text-base font-medium">客服</text>
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

    // 跳转到登录页面
    handleLogin() {
      uni.navigateTo({
        url: '/pages/login/login'
      })
    },

    // 检查登录状态并跳转
    checkLoginAndNavigate(targetPage, targetName) {
      if (isLoggedIn()) {
        // 已登录，跳转到目标页面（后续添加）
        uni.showToast({
          title: `${targetName}功能开发中`,
          icon: 'none'
        })
      } else {
        // 未登录，跳转到登录页面
        uni.navigateTo({
          url: '/pages/login/login'
        })
      }
    },

    // 余额点击
    handleBalanceClick() {
      this.checkLoginAndNavigate('/pages/balance/balance', '余额')
    },

    // 收藏点击
    handleFavoriteClick() {
      this.checkLoginAndNavigate('/pages/favorite/favorite', '收藏')
    },

    // 历史记录点击
    handleHistoryClick() {
      this.checkLoginAndNavigate('/pages/history/history', '历史记录')
    },

    // 优惠券点击
    handleCouponClick() {
      this.checkLoginAndNavigate('/pages/coupon/coupon', '优惠券')
    },

    // 客服点击
    handleCustomerServiceClick() {
      this.checkLoginAndNavigate('/pages/service/service', '客服')
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
/* 主背景设置为从上方#eefdf3到下方#dffce9的渐变 */
.profile-page {
  background: linear-gradient(to bottom, #eefdf3, #dffce9);
}

.card-1 {
  background: linear-gradient(to right, #ddfce7, #effdf3);
}

/* 确保按钮点击效果 */
button {
  line-height: normal;
}

/* 网格布局支持 */
.grid {
  display: grid;
}

.grid-cols-3 {
  grid-template-columns: repeat(3, 1fr);
}

.gap-2 {
  gap: 8rpx;
}
</style>
