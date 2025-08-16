<template>
  <view class="tab-bar">
    <view class="tab-bar-content">
      <view 
        class="tab-item" 
        v-for="(item, index) in tabList" 
        :key="index"
        @click="switchTab(item.pagePath)"
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
      tabList: [],
      currentPath: '', // 在组件内部管理当前路径
    };
  },
  created() {
    // 组件创建时初始化标签页列表
    this.initTabList();
    
    // 监听登录状态变化事件
    uni.$on('refreshTabBar', this.initTabList);
    
    // 监听路由变化，更新当前路径
    if (typeof uni.onAppRoute === 'function') {
      uni.onAppRoute(e => {
        // uni.onAppRoute 返回的路径格式为 "pages/index/index"，需要加斜杠
        const routePath = '/' + e.route; 
        this.currentPath = routePath;
        // 每次路径变化时，也重新检查一下标签页列表（以防登录状态在页面间跳转时变化）
        this.initTabList();
      });
    }
    
    // 获取初始页面路径
    const pages = getCurrentPages();
    if (pages.length > 0) {
      const currentPage = pages[pages.length - 1];
      this.currentPath = '/' + currentPage.route;
    }
  },
  methods: {
    // 初始化标签页列表
    initTabList() {
      const currentLoginStatus = isLoggedIn();
      
      if (currentLoginStatus) {
        // 登录后显示：首页、分类、物流、我的（用户端）
        this.tabList = [
          {
            pagePath: '/pages/index/index',
            text: '首页',
            iconPath: '/static/images/home.png',
            selectedIconPath: '/static/images/home_active.png'
          },
          {
            pagePath: '/ubPackages_user/pages/category/category',
            text: '分类',
            iconPath: '/static/images/category.png',
            selectedIconPath: '/static/images/category_active.png'
          },
          {
            pagePath: '/ubPackages_user/pages/logistics/logistics',
            text: '物流',
            iconPath: '/static/images/logistics.png',
            selectedIconPath: '/static/images/logistics_active.png'
          },
          {
            pagePath: '/ubPackages_user/pages/mine/mine',
            text: '我的',
            iconPath: '/static/images/my.png',
            selectedIconPath: '/static/images/my_active.png'
          }
        ];
      } else {
        // 未登录显示：首页、我的（主包）
        this.tabList = [
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
        ];
      }
    },
    
    switchTab(pagePath) {
      if (this.currentPath !== pagePath) {
        // 使用 reLaunch 清空页面栈并跳转，确保导航栏正常显示
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
  }
};
</script>

<style scoped>
/* 样式部分保持不变，因为这部分是正确的 */
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