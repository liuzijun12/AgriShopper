<template>
  <view class="page">
    <view class="content">
      <view class="logistics-content">
        <text class="placeholder">物流页面内容（用户分包）</text>
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
      currentPath: '/subPackages_user/pages/logistics/logistics'
    }
  },
  onLoad() {
    console.log('物流页面加载')
  }
}
</script>

<style>
.page {
  min-height: 100vh;
  padding-bottom: calc(100rpx + env(safe-area-inset-bottom));
}

.content {
  padding: 40rpx 20rpx;
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