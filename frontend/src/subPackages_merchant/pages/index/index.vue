<template>
  <view class="app-container">
    <!-- 轮播图 -->
    <view class="banner-container">
      <swiper class="banner-swiper" :indicator-dots="true" :autoplay="true" :interval="3000" :duration="500">
        <swiper-item v-for="(item, index) in swiperList" :key="index" @click="handleClick">
          <view class="banner-item">
            <image class="banner-image" :src="item" mode="aspectFill"></image>
          </view>
        </swiper-item>
      </swiper>
    </view>
    
    <!-- 快捷导航 -->
    <view class="nav-container">
      <view class="nav-grid">
        <view 
          class="nav-item" 
          v-for="(item, index) in navList" 
          :key="index"
          @click="handleNavClick(item)"
        >
          <image class="nav-icon" :src="item.icon"></image>
          <text class="nav-text">{{ item.title }}</text>
        </view>
      </view>
    </view>
    
    <!-- 通知公告 -->
    <view class="notice-container">
      <view class="notice-bar">
        <view class="notice-tag">通知公告</view>
        <text class="notice-text">vue-uniapp-template 是一个基于 Vue3 + UniApp 的前端模板项目，提供了一套完整的前端解决方案。</text>
      </view>
    </view>
    
    <!-- 数据统计 -->
    <view class="stats-container">
      <view class="stats-grid">
        <view class="stats-item">
          <view class="stats-content">
            <image class="stats-icon" src="/static/images/category.png" />
            <view class="stats-info">
              <text class="stats-label">访客数</text>
              <text class="stats-value">{{ visitStatsData.todayUvCount }}</text>
            </view>
          </view>
        </view>
        <view class="stats-item">
          <view class="stats-content">
            <image class="stats-icon" src="/static/images/category.png" />
            <view class="stats-info">
              <text class="stats-label">浏览量</text>
              <text class="stats-value">{{ visitStatsData.todayPvCount }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 访问趋势 -->
    <view class="trend-container">
      <view class="trend-header">
        <text class="trend-title">访问趋势</text>
        <view class="trend-tabs">
          <text 
            class="trend-tab" 
            :class="{ active: recentDaysRange === 7 }"
            @click="handleDataRangeChange(7)"
          >近7天</text>
          <text 
            class="trend-tab" 
            :class="{ active: recentDaysRange === 15 }"
            @click="handleDataRangeChange(15)"
          >近15天</text>
        </view>
      </view>
      <view class="trend-chart">
        <text class="chart-placeholder">图表区域</text>
      </view>
    </view>
    
    <!-- 商户端导航栏 -->
    <ManagerTabBar />
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { checkPageAccess } from '@/utils/routeGuard'
import ManagerTabBar from '@/components/ManagerTabBar.vue'

const userStore = useUserStore()

const current = ref(0)
const visitStatsData = ref({
  todayUvCount: 1234,
  uvGrowthRate: 15.6,
  totalUvCount: 45678,
  todayPvCount: 5678,
  pvGrowthRate: 23.4,
  totalPvCount: 123456,
})

const recentDaysRange = ref(7)

const swiperList = ref([
  "https://www.youlai.tech/storage/blog/banner9.png"
])

const navList = ref([
  {
    icon: "/static/images/category.png",
    title: "用户管理",
    url: "/subPackages_merchant/pages/work/index"
  },
  {
    icon: "/static/images/category.png", 
    title: "角色管理",
    url: "/subPackages_merchant/pages/work/index"
  },
  {
    icon: "/static/images/category.png",
    title: "通知公告", 
    url: "/subPackages_merchant/pages/work/index"
  },
  {
    icon: "/static/images/category.png",
    title: "系统配置",
    url: "/subPackages_merchant/pages/work/index"
  }
])

onLoad(() => {
  console.log('商户首页加载')
  
  // 检查页面访问权限
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const currentRoute = `/${currentPage.route}`
  checkPageAccess(currentRoute)
})

onShow(() => {
  // 确保 tabbar 状态正确
  const pages = getCurrentPages()
  if (pages.length > 0) {
    const currentPage = pages[pages.length - 1]
    if (currentPage.route.includes("subPackages_merchant/pages/index")) {
      // 通过事件通知 tabbar 布局更新状态
      uni.$emit("updateTabbar", "manager-index")
    }
  }
})

const handleClick = () => {
  console.log('轮播图点击')
}

const handleNavClick = (item: any) => {
  console.log('导航点击:', item.title)
  uni.navigateTo({
    url: item.url
  })
}

const handleDataRangeChange = (value: number) => {
  console.log("数据范围变化", value)
  recentDaysRange.value = value
}

// 退出登录
const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
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
</script>

<style>
.app-container {
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
  background-color: #f5f5f5;
}

.banner-container {
  margin: 20rpx;
}

.banner-swiper {
  height: 300rpx;
  border-radius: 12rpx;
  overflow: hidden;
}

.banner-item {
  width: 100%;
  height: 100%;
}

.banner-image {
  width: 100%;
  height: 100%;
}

.nav-container {
  margin: 20rpx;
  background-color: #fff;
  border-radius: 12rpx;
  padding: 40rpx 20rpx;
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30rpx;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx 10rpx;
}

.nav-icon {
  width: 60rpx;
  height: 60rpx;
  margin-bottom: 16rpx;
}

.nav-text {
  font-size: 24rpx;
  color: #333;
  text-align: center;
}

.notice-container {
  margin: 20rpx;
}

.notice-bar {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  display: flex;
  align-items: center;
}

.notice-tag {
  background-color: #FAA21E;
  color: #fff;
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  margin-right: 20rpx;
  white-space: nowrap;
}

.notice-text {
  font-size: 26rpx;
  color: #34D19D;
  line-height: 1.4;
  flex: 1;
}

.stats-container {
  margin: 20rpx;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}

.stats-item {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 40rpx 30rpx;
}

.stats-content {
  display: flex;
  align-items: center;
}

.stats-icon {
  width: 60rpx;
  height: 60rpx;
  margin-right: 30rpx;
}

.stats-info {
  flex: 1;
}

.stats-label {
  display: block;
  font-size: 28rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.stats-value {
  display: block;
  font-size: 36rpx;
  color: #333;
  font-weight: bold;
}

.trend-container {
  margin: 20rpx;
  background-color: #fff;
  border-radius: 12rpx;
  padding: 40rpx 30rpx;
}

.trend-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40rpx;
}

.trend-title {
  font-size: 32rpx;
  color: #333;
  font-weight: bold;
}

.trend-tabs {
  display: flex;
  gap: 20rpx;
}

.trend-tab {
  padding: 12rpx 24rpx;
  border-radius: 20rpx;
  font-size: 26rpx;
  color: #666;
  background-color: #f5f5f5;
}

.trend-tab.active {
  background-color: #007aff;
  color: #fff;
}

.trend-chart {
  height: 300rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f9f9f9;
  border-radius: 8rpx;
}

.chart-placeholder {
  font-size: 28rpx;
  color: #999;
}

.logout-container {
  margin: 40rpx 20rpx;
}

.logout-btn {
  background-color: #ff4757;
  color: white;
  border-radius: 12rpx;
  padding: 30rpx;
  font-size: 32rpx;
  border: none;
  width: 100%;
}
</style>