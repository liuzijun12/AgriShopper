<template>
  <view class="app-container">
    <wd-status-tip type="search" tip="建设中..." />
    
    <!-- 商户端导航栏 -->
    <ManagerTabBar />
  </view>
</template>

<script setup lang="ts">
import ManagerTabBar from '@/components/ManagerTabBar.vue';
import { checkPageAccess } from '@/utils/routeGuard';
import { goToHome } from '@/utils/navigationHandler.js';

onLoad(() => {
  // 检查页面访问权限
  const pages = getCurrentPages();
  const currentPage = pages[pages.length - 1];
  const currentRoute = `/${currentPage.route}`;
  checkPageAccess(currentRoute);
  
  // 设置导航栏标题
  uni.setNavigationBarTitle({
    title: '工作台'
  });
});

// 处理导航栏按钮点击
const onNavigationBarButtonTap = (e) => {
  if (e.index === 0) {
    goToHome();
  }
};

// 重写返回按钮行为
const onBackPress = () => {
  console.log('商户工作台页面 - 拦截返回按钮')
  // 直接跳转到商户首页，不调用goToHome避免闪现
  uni.redirectTo({
    url: '/subPackages_merchant/pages/index/index'
  })
  return true; // 阻止默认返回行为
};

onShow(() => {
  // 确保 tabbar 状态正确
  const pages = getCurrentPages();
  if (pages.length > 0) {
    const currentPage = pages[pages.length - 1];
    if (currentPage.route.includes("subPackages_merchant/pages/work")) {
      // 通过事件通知 tabbar 布局更新状态
      uni.$emit("updateTabbar", "manager-work");
    }
  }
});
</script>

<route lang="json">
{
  "name": "work",
  "style": {
    "navigationBarTitleText": "工作台"
  },
  "meta": {
    "requireAuth": true
  }
}
</route>

<style lang="scss">
.app-container {
  min-height: 100vh;
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
}
</style>
