<template>
  <view class="merchant-tabbar">
    <view class="tabbar-content">
      <view 
        class="tabbar-item" 
        v-for="(item, index) in tabbarList" 
        :key="index"
        @click="handleTabClick(item)"
        :class="{ active: isActive(item) }"
      >
        <view class="tabbar-icon">
          <wd-icon :name="item.icon" size="24px" />
        </view>
        <text class="tabbar-title">{{ item.title }}</text>
        <view v-if="item.value" class="tabbar-badge">
          <text class="badge-text">{{ item.value }}</text>
        </view>
      </view>
    </view>
    <!-- 底部安全区域 -->
    <view class="safe-area-bottom"></view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

// 定义TabBar项目接口
interface TabbarItem {
  name: string;
  value: number | null;
  active: boolean;
  title: string;
  icon: string;
  url: string;
}

// Props
interface Props {
  currentPath?: string;
}

const props = withDefaults(defineProps<Props>(), {
  currentPath: ''
});

// 商户端TabBar配置
const tabbarItems = ref<TabbarItem[]>([
  { 
    name: "home", 
    value: null, 
    active: true, 
    title: "首页", 
    icon: "home", 
    url: "/ubPackages_merchant/pages/index/index" 
  },
  { 
    name: "mine", 
    value: null, 
    active: false, 
    title: "我的", 
    icon: "user", 
    url: "/ubPackages_merchant/pages/mine/index" 
  }
]);

// 计算属性
const tabbarList = computed(() => tabbarItems.value);

const activeTabbar = computed(() => {
  const item = tabbarItems.value.find((item) => item.active);
  return item || tabbarItems.value[0];
});

// 判断是否为活跃状态
const isActive = (item: TabbarItem) => {
  return item.active === true;
};

// 方法
const getTabbarItemValue = (name: string) => {
  const item = tabbarItems.value.find((item) => item.name === name);
  return item && item.value ? item.value : null;
};

const setTabbarItem = (name: string, value: number) => {
  const tabbarItem = tabbarItems.value.find((item) => item.name === name);
  if (tabbarItem) {
    tabbarItem.value = value;
  }
};

const setTabbarItemActive = (name: string) => {
  tabbarItems.value.forEach((item) => {
    item.active = item.name === name;
  });
};

// 处理TabBar点击
const handleTabClick = (item: TabbarItem) => {
  if (item.name !== activeTabbar.value.name) {
    setTabbarItemActive(item.name);
    
    console.log('商户TabBar跳转到:', item.url);
    // 使用 reLaunch 避免增加页面栈
    uni.reLaunch({
      url: item.url,
      success: () => {
        console.log('商户TabBar跳转成功');
      },
      fail: (err) => {
        console.error('商户TabBar跳转失败:', err);
      }
    });
  }
};

// 根据当前路径更新活跃状态
const updateActiveByPath = () => {
  if (props.currentPath) {
    const matchedItem = tabbarItems.value.find(item => item.url === props.currentPath);
    if (matchedItem) {
      setTabbarItemActive(matchedItem.name);
    }
  }
};

// 监听路径变化
watch(() => props.currentPath, () => {
  updateActiveByPath();
}, { immediate: true });

// 暴露方法供外部使用
defineExpose({
  setTabbarItem,
  setTabbarItemActive,
  getTabbarItemValue
});
</script>

<style scoped>
.merchant-tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background-color: #fff;
  border-top: 1rpx solid #e5e5e5;
  box-shadow: 0 -2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.tabbar-content {
  display: flex;
  height: 120rpx;
}

.tabbar-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10rpx 0;
  position: relative;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tabbar-item:hover {
  background-color: #f5f5f5;
}

.tabbar-item.active .tabbar-icon {
  color: #07c160;
}

.tabbar-item.active .tabbar-title {
  color: #07c160;
}

.tabbar-icon {
  margin-bottom: 8rpx;
  color: #999;
  transition: color 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.tabbar-title {
  font-size: 24rpx;
  color: #999;
  line-height: 1;
  transition: color 0.3s ease;
}

.tabbar-badge {
  position: absolute;
  top: 8rpx;
  right: 20rpx;
  background-color: #ff4757;
  border-radius: 20rpx;
  min-width: 32rpx;
  height: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 8rpx;
}

.badge-text {
  font-size: 20rpx;
  color: #fff;
  font-weight: bold;
  line-height: 1;
}

.safe-area-bottom {
  height: env(safe-area-inset-bottom);
  background-color: #fff;
}

/* 深色主题支持 */
@media (prefers-color-scheme: dark) {
  .merchant-tabbar {
    background-color: #1a1a1a;
    border-top-color: #333;
  }
  
  .tabbar-item:hover {
    background-color: #2a2a2a;
  }
  
  .safe-area-bottom {
    background-color: #1a1a1a;
  }
}
</style>