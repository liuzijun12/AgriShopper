<template>
  <view class="tab-bar">
    <view
      v-for="(item, index) in tabList"
      :key="index"
      :class="['tab-item', currentTab === index ? 'active' : '']"
      @click="switchTab(item.pagePath, index)"
    >
      <image 
        :src="currentTab === index ? item.selectedIconPath : item.iconPath" 
        class="tab-icon"
        mode="aspectFit"
      />
      <text :class="['tab-text', currentTab === index ? 'active-text' : '']">
        {{ item.text }}
      </text>
    </view>
  </view>
</template>

<script lang="ts">
export default {
  options: {
    virtualHost: true
  }
}
</script>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const currentTab = ref(0)

const tabList = [
  {
    pagePath: '/pages/index/index',
    text: '首页',
    iconPath: '/static/tabbar/home.png',
    selectedIconPath: '/static/tabbar/home-active.png'
  },
  {
    pagePath: '/pages/productList/productList',
    text: '商品',
    iconPath: '/static/tabbar/list.png',
    selectedIconPath: '/static/tabbar/list-active.png'
  },
  {
    pagePath: '/pages/messages/messages',
    text: '消息',
    iconPath: '/static/tabbar/message.png',
    selectedIconPath: '/static/tabbar/message-active.png'
  },
  {
    pagePath: '/pages/shoppingCart/shoppingCart',
    text: '购物车',
    iconPath: '/static/tabbar/cart.png',
    selectedIconPath: '/static/tabbar/cart-active.png'
  },
  {
    pagePath: '/pages/my/my',
    text: '我的',
    iconPath: '/static/tabbar/user.png',
    selectedIconPath: '/static/tabbar/user-active.png'
  }
]

// 获取当前页面路径并设置对应的tab
const initCurrentTab = () => {
  const pages = getCurrentPages()
  if (pages.length > 0) {
    const currentPage = pages[pages.length - 1]
    const path = '/' + currentPage.route
    const index = tabList.findIndex(tab => tab.pagePath === path)
    if (index !== -1) {
      currentTab.value = index
    }
  }
}

// 切换页面
const switchTab = (path: string, index: number) => {
  if (currentTab.value === index) return
  currentTab.value = index
  
  uni.reLaunch({
    url: path,
    success: () => {
      initCurrentTab()
    },
    fail: (err) => {
      console.error('Navigation failed:', err)
      uni.showToast({
        title: '页面跳转失败',
        icon: 'none'
      })
    }
  })
}

// 监听页面显示事件
uni.$on('tabPageShow', () => {
  initCurrentTab()
})

// 组件卸载时清理事件监听
onUnmounted(() => {
  uni.$off('tabPageShow')
})

// 组件挂载时初始化当前tab
onMounted(() => {
  initCurrentTab()
})
</script>

<style scoped>
.tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100rpx;
  background-color: #ffffff;
  display: flex;
  justify-content: space-around;
  align-items: center;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  z-index: 9999;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10rpx 0;
  position: relative;
}

.tab-item::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%) scaleX(0);
  width: 40rpx;
  height: 4rpx;
  background-color: #4CAF50;
  transition: transform 0.3s ease;
}

.tab-item.active::after {
  transform: translateX(-50%) scaleX(1);
}

.tab-item:active {
  opacity: 0.8;
}

.tab-icon {
  width: 48rpx;
  height: 48rpx;
  margin-bottom: 4rpx;
  transition: all 0.3s ease;
}

.tab-text {
  font-size: 24rpx;
  color: #999999;
  line-height: 1;
  transition: all 0.3s ease;
}

.active-text {
  color: #4CAF50;
  font-weight: 500;
}
</style> 