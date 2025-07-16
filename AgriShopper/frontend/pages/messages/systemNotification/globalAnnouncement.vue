<template>
  <view class="global-announcement-container">
    <view class="header">
      <text class="icon">📢</text>
      <view class="header-text-group">
        <text class="main-title">台风“木兰”配送延迟公告</text>
        <text class="datetime">{{ noticeDate }} {{ noticeTime }}</text>
      </view>
    </view>

    <view class="body-content">
      <text class="main-text">
        <text class="bold">{{ announcementSummary }}</text>
      </text>

      <view class="section-title">
        <text>- 影响详情：</text>
      </view>
      <view class="detail-list">
        <view class="detail-item">
          <text class="status-icon">⛔</text>
          <text>暂停服务：{{ pausedServices }}</text>
        </view>
        <view class="detail-item">
          <text class="status-icon">⚠️</text>
          <text>延迟配送：{{ delayedRegions }}</text>
        </view>
        <view class="detail-item">
          <text class="status-icon">✅</text>
          <text>应急措施：{{ emergencyAction }}</text>
        </view>
      </view>

      <view class="section-title">
        <text>- 建议：</text>
      </view>
      <view class="suggestion-list">
        <text>• {{ suggestion1 }}</text>
        <text>• {{ suggestion2 }}</text>
      </view>
    </view>

    <view class="function-area">
      <!-- 已删除：查看灾区地图按钮 -->
      <button class="action-button" @tap="cancelOrder">取消订单</button>
      <button class="action-button" @tap="callHotline">物流紧急热线</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';

// 定义响应式数据
const noticeDate = ref('');
const noticeTime = ref('');
const announcementSummary = ref('');
const pausedServices = ref('');
const delayedRegions = ref('');
const emergencyAction = ref('');
const suggestion1 = ref('');
const suggestion2 = ref('');

// 在页面加载时获取传递的参数
onLoad((options) => {
  if (options.messageData) { // 假设从 messages.vue 传递的是 messageData
    try {
      const data = JSON.parse(decodeURIComponent(options.messageData));
      console.log('Received global announcement data:', data);

      noticeDate.value = data.date || '未知日期';
      noticeTime.value = data.time || '未知时间';
      announcementSummary.value = data.content || '暂无摘要';
      pausedServices.value = data.pausedServices || '暂无';
      delayedRegions.value = data.delayedRegions || '暂无';
      emergencyAction.value = data.emergencyAction || '暂无';
      suggestion1.value = data.suggestion1 || '暂无';
      suggestion2.value = data.suggestion2 || '暂无';

    } catch (e) {
      console.error('Failed to parse messageData for global announcement:', e);
      uni.showToast({
        title: '数据加载失败',
        icon: 'none'
      });
    }
  }
});

// 按钮点击事件处理函数
const viewDisasterMap = () => {
  uni.showToast({
    title: '跳转到灾区地图页面',
    icon: 'none'
  });
  // 实际应用中会是 uni.navigateTo({ url: '/pages/map/disasterMap' });
};

const cancelOrder = () => {
  uni.showToast({
    title: '跳转到取消订单流程',
    icon: 'none'
  });
  // 实际应用中会是 uni.navigateTo({ url: '/pages/order/cancelOrder' });
};

const callHotline = () => {
  uni.showToast({
    title: '拨打物流紧急热线',
    icon: 'none'
  });
  // 实际应用中会是 uni.makePhoneCall({ phoneNumber: '12345678900' });
};
</script>

<style scoped>
/* Base page styling */
page {
  background-color: #f0f2f5;
  height: 100%;
}

.global-announcement-container {
  background-color: #ffffff;
  border-radius: 16rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 700rpx; /* Max width for better readability on larger screens */
  padding: 50rpx 40rpx;
  box-sizing: border-box;
  border-left: 10rpx solid #ffc107; /* Gold/Yellow border for announcements */
  margin: 40rpx auto;
  font-family: 'PingFang SC', 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

/* Header styling */
.header {
  display: flex;
  align-items: center;
  margin-bottom: 40rpx;
  padding-bottom: 30rpx;
  border-bottom: 1rpx solid #eeeeee;
}

.header .icon {
  font-size: 60rpx;
  margin-right: 20rpx;
  color: #ffc107; /* Gold/Yellow icon color */
  line-height: 1;
  flex-shrink: 0;
}

.header-text-group {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  min-width: 0;
}

.main-title {
  font-size: 36rpx;
  color: #333;
  font-weight: bold;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.datetime {
  font-size: 28rpx;
  color: #666;
  line-height: 1.3;
  margin-top: 8rpx;
}

/* Body content styling */
.body-content {
  margin-bottom: 40rpx;
}

.main-text {
  font-size: 32rpx;
  color: #555;
  line-height: 1.6;
  margin-bottom: 30rpx;
  display: block;
}

.bold {
  font-weight: bold;
  color: #333;
}

.section-title {
  font-size: 30rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 20rpx;
  margin-top: 30rpx;
}

.detail-list, .suggestion-list {
  display: flex;
  flex-direction: column;
  margin-left: 20rpx; /* Indent lists */
  margin-bottom: 30rpx;
}

.detail-item {
  display: flex;
  align-items: flex-start;
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 15rpx;
}

.status-icon {
  font-size: 36rpx;
  margin-right: 15rpx;
  flex-shrink: 0;
  line-height: 1.6;
}

.detail-item .status-icon {
  color: #333; /* Default icon color */
}
.detail-item:nth-child(1) .status-icon { /* For '暂停服务' */
  color: #dc3545; /* Red */
}
.detail-item:nth-child(2) .status-icon { /* For '延迟配送' */
  color: #ffc107; /* Orange/Yellow */
}
.detail-item:nth-child(3) .status-icon { /* For '应急措施' */
  color: #28a745; /* Green */
}


.suggestion-list text {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 15rpx;
}

/* Function area styling */
.function-area {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  margin-top: 40rpx;
}

.action-button {
  background-color: #007bff;
  color: white;
  padding: 24rpx 40rpx;
  border: none;
  border-radius: 10rpx;
  font-size: 32rpx;
  text-align: center;
  transition: background-color 0.3s ease;
  width: 100%;
}

.action-button:active {
  opacity: 0.8;
}

.action-button.primary {
  background-color: #ffc107; /* Primary action button in gold/yellow */
  color: #333; /* Darker text for contrast */
}
.action-button.primary:active {
  background-color: #e0a800; /* Darker gold/yellow on active */
}

/* Responsive adjustments for larger screens */
@media (min-width: 600px) {
  .function-area {
    flex-direction: row;
    justify-content: space-between;
  }
  .action-button {
    flex: 1;
    max-width: 30%;
  }
}
</style>
