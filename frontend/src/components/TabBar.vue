<template>
  <view class="tab-bar">
    <view class="tab-bar-content">
      <view 
        class="tab-item" 
        v-for="(item, index) in filteredTabList" 
        :key="index"
        @click="switchTab(item.pagePath)"
        :class="{ active: isCurrentPath(item.pagePath) }"
      >
        <image class="tab-icon" :src="isCurrentPath(item.pagePath) ? item.selectedIconPath : item.iconPath"></image>
        <text class="tab-text">{{ item.text }}</text>
      </view>
    </view>
    <!-- 底部安全区域 -->
    <view class="safe-area-bottom"></view>
  </view>
</template>

<script>
import { useUserStore } from '@/store/modules/user'

export default {
  name: 'TabBar',
  props: {
    currentPath: {
      type: String,
      default: '/pages/index/index'
    }
  },
  data() {
    return {
      // 未登录时的导航栏配置
      unloggedTabList: [
        {
          pagePath: '/pages/index/index',
          text: '首页',
          iconPath: '/static/images/home.png',
          selectedIconPath: '/static/images/home_active.png'
        },
        {
          pagePath: '/pages/mine/profile',
          text: '我的',
          iconPath: '/static/images/my.png',
          selectedIconPath: '/static/images/my_active.png'
        }
      ],
      // 登录后的导航栏配置
      loggedTabList: [
        {
          pagePath: '/pages/index/index',
          text: '首页',
          iconPath: '/static/images/home.png',
          selectedIconPath: '/static/images/home_active.png'
        },
        {
          pagePath: '/subPackages_user/pages/category/category',
          text: '分类',
          iconPath: '/static/images/category.png',
          selectedIconPath: '/static/images/category_active.png'
        },
        {
          pagePath: '/subPackages_user/pages/logistics/logistics',
          text: '物流',
          iconPath: '/static/images/logistics.png',
          selectedIconPath: '/static/images/logistics_active.png'
        },
        {
          pagePath: '/subPackages_user/pages/mine/mine',
          text: '我的',
          iconPath: '/static/images/my.png',
          selectedIconPath: '/static/images/my_active.png'
        }
      ]
    }
  },
  computed: {
    userStore() {
      return useUserStore()
    },
    // 根据登录状态返回对应的导航栏配置
    filteredTabList() {
      return this.userStore.isLoggedIn ? this.loggedTabList : this.unloggedTabList
    }
  },
  methods: {
    // 判断是否为当前路径
    isCurrentPath(pagePath) {
      // 处理我的页面的路径匹配
      if (pagePath === '/subPackages_user/pages/mine/mine' && this.currentPath === '/pages/mine/profile') {
        return true
      }
      if (pagePath === '/pages/mine/profile' && this.currentPath === '/subPackages_user/pages/mine/mine') {
        return true
      }
      return this.currentPath === pagePath
    },
    
    switchTab(pagePath) {
      if (!this.isCurrentPath(pagePath)) {
        // 如果是子包页面，使用 navigateTo
        if (pagePath.startsWith('/subPackages_user/') || pagePath.startsWith('/subPackages_merchant/')) {
          uni.reLaunch({
            url: pagePath
          })
        } else {
          // 主包页面使用 switchTab
          uni.switchTab({
            url: pagePath
          })
        }
      }
    }
  }
}
</script>

<style scoped>
.tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  border-top: 1rpx solid #e5e5e5;
  z-index: 1000;
}

.tab-bar-content {
  display: flex;
  height: 120rpx;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10rpx 0;
}

.tab-icon {
  width: 50rpx;
  height: 50rpx;
  margin-bottom: 4rpx;
}

.tab-text {
  font-size: 25rpx;
  color: #999;
  line-height: 1;
}

.tab-item.active .tab-text {
  color: #3acc6f;
}

.safe-area-bottom {
  height: env(safe-area-inset-bottom);
  background-color: #fff;
}
</style>