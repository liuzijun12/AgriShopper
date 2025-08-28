<template>
  <view class="tab-bar">
    <view class="tab-bar-content">
      <view
        class="tab-item"
        v-for="(item, index) in tabList"
        :key="index"
        @click="handleTabClick(item)"
      >
        <image class="tab-icon" :src="currentPath === item.pagePath ? item.selectedIconPath : item.iconPath"></image>
        <text class="tab-text" :class="{ active: currentPath === item.pagePath }">{{ item.text }}</text>
      </view>
    </view>
    <view class="safe-area-bottom"></view>
  </view>
</template>

<script>
import { isLoggedIn } from '@/utils/auth';

export default {
  name: 'TabBar',
  data() {
    return {
      // 始终显示完整的标签列表，并标记哪些需要登录权限
      tabList: [
        {
          pagePath: '/pages/index/index',
          text: '首页',
          iconPath: '/static/images/home.png',
          selectedIconPath: '/static/images/home_active.png',
          requireAuth: false // 不需要登录
        },
        {
          pagePath: '/ubPackages_user/pages/category/category',
          text: '分类',
          iconPath: '/static/images/category.png',
          selectedIconPath: '/static/images/category_active.png',
          requireAuth: false // 不需要登录
        },
        {
          pagePath: '/ubPackages_user/pages/logistics/logistics',
          text: '订单',
          iconPath: '/static/images/order.png',
          selectedIconPath: '/static/images/order_active.png',
          requireAuth: true // 需要登录
        },
        {
          pagePath: '/pages/mine/profile',
          text: '我的',
          iconPath: '/static/images/my.png',
          selectedIconPath: '/static/images/my_active.png',
          requireAuth: false // 不需要登录
        }
      ],
      currentPath: '',
    };
  },
  created() {
    // 监听路由变化，更新当前路径
    if (typeof uni.onAppRoute === 'function') {
      uni.onAppRoute(e => {
        const routePath = '/' + e.route;
        this.currentPath = routePath;
      });
    }

    // 获取初始页面路径
    const pages = getCurrentPages();
    if (pages.length > 0) {
      const currentPage = pages[pages.length - 1];
      this.currentPath = '/' + currentPage.route;
    }

    // 监听登录成功事件，登录后重新尝试跳转
    uni.$on('loginSuccess', (targetPath) => {
      if (targetPath) {
        this.switchTab(targetPath);
      }
    });
  },
  methods: {
    // 处理标签点击事件
    handleTabClick(item) {
      // 如果点击的是当前页面，不做处理
      if (this.currentPath === item.pagePath) {
        return;
      }

      // 检查是否需要登录
      if (item.requireAuth) {
        // 已登录则直接跳转
        if (isLoggedIn()) {
          this.switchTab(item.pagePath);
        } else {
          // 未登录则先触发登录，记录目标路径
          this.triggerLogin(item.pagePath);
        }
      } else {
        // 不需要登录的页面直接跳转
        this.switchTab(item.pagePath);
      }
    },

    // 触发登录流程
    triggerLogin(targetPath) {
      // 这里可以根据实际项目的登录方式进行调整
      // 示例：跳转到登录页，并传递回调路径
      uni.navigateTo({
        url: `/pages/login/login?callbackPath=${encodeURIComponent(targetPath)}`,
        fail: (err) => {
          console.error('打开登录页失败：', err);
        }
      });

      // 或者如果是弹出登录模态框
      // this.$emit('showLoginModal', targetPath);
    },

    // 页面跳转
    switchTab(pagePath) {
      uni.reLaunch({
        url: pagePath,
        success: () => {
          console.log('TabBar跳转成功:', pagePath);
        },
        fail: (err) => {
          console.error('TabBar跳转失败：', err);
        }
      });
    }
  }
};
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

.tab-item .tab-text.active {
  color: #3acc6f;
}

.safe-area-bottom {
  height: env(safe-area-inset-bottom);
  background-color: #fff;
}
</style>
