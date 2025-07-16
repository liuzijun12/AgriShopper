<template>
  <view class="rule-update-container">
    <view class="header">
      <text class="icon">📄</text>
      <view class="header-text-group">
        <text class="main-title">生鲜商品售后规则更新</text>
        <text class="datetime">{{ noticeDate }} {{ noticeTime }}</text>
      </view>
    </view>

    <view class="body-content">
      <text class="main-text">
        <text class="bold">{{ updateTitle }}</text>
      </text>

      <view class="section-title">
        <text>- 核心变更：</text>
      </view>
      <view class="change-list">
        <text class="change-item">• 旧规：签收后24小时内可申请腐烂赔付</text>
        <text class="change-item">• 新规：需提供签收时完整开箱视频（≤30秒）</text>
      </view>

      <view class="section-title">
        <text>- 对您的影响：</text>
      </view>
      <view class="impact-list">
        <text>• 4月1日后订单需录制开箱视频作为理赔依据</text>
        <text>• 历史订单仍按原规则执行（凭证：物流签收单+照片）</text>
      </view>
    </view>

    <view class="function-area">
      <button class="action-button primary" @tap="viewNewRule">查看新规全文</button>
      <button class="action-button" @tap="viewVideoGuide">视频拍摄指南</button>
      <button class="action-button" @tap="consultRule">规则咨询</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';

// Define reactive data for the notice content
const noticeDate = ref('2025-03-17');
const noticeTime = ref('15:00');
const updateTitle = ref('《生鲜类商品争议处理规范》修订生效公告');

// In a real application, this data would likely come from API calls
// or be passed as parameters if navigating from a list.
onLoad((options) => {
  if (options.messageData) { // Assuming it's passed as messageData from messages.vue
    try {
      const data = JSON.parse(decodeURIComponent(options.messageData));
      console.log('Received rule update data:', data);
      noticeDate.value = data.date || noticeDate.value;
      noticeTime.value = data.time || noticeTime.value;
      updateTitle.value = data.title || updateTitle.value;
      // You can update other fields based on the passed data
    } catch (e) {
      console.error('Failed to parse messageData for rule update:', e);
      uni.showToast({
        title: '数据加载失败',
        icon: 'none'
      });
    }
  }
});

// Button click handlers
const viewNewRule = () => {
  uni.showToast({
    title: '跳转到新规全文页面',
    icon: 'none'
  });
  // In a real app: uni.navigateTo({ url: '/pages/rules/freshProduceNewRule' });
};

const viewVideoGuide = () => {
  uni.showToast({
    title: '跳转到视频拍摄指南页面',
    icon: 'none'
  });
  // In a real app: uni.navigateTo({ url: '/pages/guide/videoRecording' });
};

const consultRule = () => {
  uni.showToast({
    title: '跳转到规则咨询页面',
    icon: 'none'
  });
  // In a real app: uni.navigateTo({ url: '/pages/customerService/ruleConsult' });
};
</script>

<style scoped>
/* Base page styling */
page {
  background-color: #f0f2f5;
  height: 100%;
}

.rule-update-container {
  background-color: #ffffff;
  border-radius: 16rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 700rpx; /* Max width for better readability on larger screens */
  padding: 50rpx 40rpx;
  box-sizing: border-box;
  border-left: 10rpx solid #1890ff; /* Blue border for rule updates */
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
  color: #1890ff; /* Blue icon color */
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
  margin-bottom: 30rpx; /* Increased margin for main text */
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
  margin-top: 30rpx; /* Space above new sections */
}

.change-list, .impact-list {
  display: flex;
  flex-direction: column;
  margin-left: 20rpx; /* Indent lists */
  margin-bottom: 30rpx;
}

.change-item, .impact-list text {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 15rpx; /* Spacing between list items */
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
  background-color: #1890ff; /* Primary action button (e.g., 查看新规全文) in blue */
}
.action-button.primary:active {
  background-color: #096dd9; /* Darker blue on active */
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
