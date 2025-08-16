<template>
  <view class="page">
    <view class="content">
      <view class="header">
        <text class="title">物流信息</text>
      </view>
      
      <view class="logistics-content">
        <text class="placeholder">物流页面内容</text>
      </view>
    </view>
    
    <!-- 底部导航栏 -->
    <TabBar :currentPath="currentPath" />
  </view>
</template>

<script>
import TabBar from '@/components/TabBar.vue'

export default {
  components: {
    TabBar
  },
  data() {
    return {
      currentPath: '/ubPackages_user/pages/logistics/logistics'
    }
  },
  onLoad() {

  },
  methods: {

  }
}
</script>

<style>
.page {
  min-height: 100vh;
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
  background-color: #f5f5f5;
}

.content {
  padding: 40rpx 20rpx;
}

.header {
  text-align: center;
  margin-bottom: 40rpx;
}

.title {
  font-size: 36rpx;
  color: #333;
  font-weight: bold;
}

.logistics-content {
  text-align: center;
  margin-top: 100rpx;
}

.placeholder {
  font-size: 28rpx;
  color: #666;
}
</style>