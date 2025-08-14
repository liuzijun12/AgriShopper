<template>
  <view class="manager-tabbar">
    <view class="tabbar-container">
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 'index' }"
        @click="switchTab('index')"
      >
        <view class="tab-icon">
          <image 
            class="icon-image" 
            :src="currentTab === 'index' ? '/static/tabbar/home-active.png' : '/static/tabbar/home.png'" 
            mode="aspectFit"
          />
        </view>
        <text class="tab-text">首页</text>
      </view>
      
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 'work' }"
        @click="switchTab('work')"
      >
        <view class="tab-icon">
          <image 
            class="icon-image" 
            :src="currentTab === 'work' ? '/static/tabbar/work-active.png' : '/static/tabbar/work.png'" 
            mode="aspectFit"
          />
        </view>
        <text class="tab-text">工作台</text>
      </view>
      
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 'mine' }"
        @click="switchTab('mine')"
      >
        <view class="tab-icon">
          <image 
            class="icon-image" 
            :src="currentTab === 'mine' ? '/static/tabbar/mine-active.png' : '/static/tabbar/mine.png'" 
            mode="aspectFit"
          />
        </view>
        <text class="tab-text">我的</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const currentTab = ref('index')

onMounted(() => {
  // 根据当前页面设置激活状态
  const pages = getCurrentPages()
  if (pages.length > 0) {
    const currentPage = pages[pages.length - 1]
    const route = currentPage.route
    
    if (route.includes('subPackages_merchant/pages/index')) {
      currentTab.value = 'index'
    } else if (route.includes('subPackages_merchant/pages/work')) {
      currentTab.value = 'work'
    } else if (route.includes('subPackages_merchant/pages/mine')) {
      currentTab.value = 'mine'
    }
  }
})

// 监听tabbar更新事件
uni.$on('updateTabbar', (tab: string) => {
  if (tab.includes('manager-')) {
    currentTab.value = tab.replace('manager-', '')
  }
})

const switchTab = (tab: string) => {
  if (tab === currentTab.value) return
  
  currentTab.value = tab
  
  let url = ''
  switch (tab) {
    case 'index':
      url = '/subPackages_merchant/pages/index/index'
      break
    case 'work':
      url = '/subPackages_merchant/pages/work/index'
      break
    case 'mine':
      url = '/subPackages_merchant/pages/mine/index'
      break
  }
  
  if (url) {
    // 使用 navigateTo 而不是 switchTab，因为这些页面在分包中
    uni.reLaunch({
      url,
      fail: (err) => {
        console.error('导航失败:', err)
        // 如果导航失败，尝试使用 reLaunch
        uni.reLaunch({
          url
        })
      }
    })
  }
}
</script>

<style lang="scss" scoped>
.manager-tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background-color: #fff;
  border-top: 1rpx solid #eee;
  padding-bottom: env(safe-area-inset-bottom);
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);
}

.tabbar-container {
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
  transition: all 0.2s ease;
  
  &:active {
    background-color: #f5f5f5;
  }
  
  &.active {
    .tab-text {
      color: #007aff;
      font-weight: 500;
    }
  }
}

.tab-icon {
  margin-bottom: 8rpx;
  
  .icon-image {
    width: 48rpx;
    height: 48rpx;
  }
}

.tab-text {
  font-size: 22rpx;
  color: #666;
  transition: color 0.2s ease;
}
</style>