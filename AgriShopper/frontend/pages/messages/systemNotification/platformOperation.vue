<template>
  <view class="platform-operation-container">
    <view class="header">
      <text class="icon">⚙️</text>
      <view class="header-text-group">
        <text class="main-title">支付系统维护通知</text>
        <text class="datetime">{{ noticeDate }} {{ noticeTime }}</text>
      </view>
    </view>

    <view class="body-content">
      <text class="main-text">
        <text class="bold">{{ maintenanceTitle }}</text>
      </text>

      <view class="section-title">
        <text>- 受影响功能：</text>
      </view>
      <view class="feature-list">
        <view class="feature-item success">
          <text class="status-icon">✅</text>
          <text>可正常使用：余额充值、订单查询</text>
        </view>
        <view class="feature-item error">
          <text class="status-icon">⛔</text>
          <text>暂停服务：银行卡支付、提现到账</text>
        </view>
      </view>

      <view class="section-title">
        <text>- 用户须知：</text>
      </view>
      <view class="user-notice-list">
        <text>• 维护前请完成提现申请（3月19日23:00截止）</text>
        <text>• 紧急支付需求请使用平台余额</text>
        <text>• 预计延长时间不超过1小时</text>
      </view>
    </view>

    <view class="function-area">
      <button class="action-button primary" @tap="withdrawNow">立即提现</button>
      <button class="action-button" @tap="rechargeBalance">充值平台余额</button>
      <button class="action-button" @tap="checkProgress">维护进度查询</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';

// 定义响应式数据
const noticeDate = ref('2025-03-18');
const noticeTime = ref('09:30');
const maintenanceTitle = ref('3月20日 00:00-06:00 银联通道升级维护');

// 在页面加载时获取传递的参数
onLoad((options) => {
  if (options.noticeData) {
    try {
      const data = JSON.parse(decodeURIComponent(options.noticeData));
      console.log('Received notice data:', data);
      noticeDate.value = data.date || noticeDate.value;
      noticeTime.value = data.time || noticeTime.value;
      maintenanceTitle.value = data.title || maintenanceTitle.value;
      // 您可以根据需要更新其他字段
    } catch (e) {
      console.error('Failed to parse noticeData:', e);
      uni.showToast({
        title: '数据加载失败',
        icon: 'none'
      });
    }
  }
});

// 按钮点击事件处理函数
const withdrawNow = () => {
  uni.showToast({
    title: '跳转到提现页面',
    icon: 'none'
  });
  // 实际应用中会是 uni.navigateTo({ url: '/pages/wallet/withdraw' });
};

const rechargeBalance = () => {
  uni.showToast({
    title: '跳转到充值页面',
    icon: 'none'
  });
  // 实际应用中会是 uni.navigateTo({ url: '/pages/wallet/recharge' });
};

const checkProgress = () => {
  uni.showToast({
    title: '查询维护进度',
    icon: 'none'
  });
  // 实际应用中会是 uni.navigateTo({ url: '/pages/system/maintenanceStatus' });
};
</script>

<style scoped>
/* Base page styling */
page {
  background-color: #f0f2f5;
  height: 100%;
}

.platform-operation-container { /* Changed class name for consistency */
  background-color: #ffffff;
  border-radius: 16rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 700rpx; /* Max width for better readability on larger screens */
  padding: 50rpx 40rpx;
  box-sizing: border-box;
  border-left: 10rpx solid #ffa500; /* Orange border for notice/warning */
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
  color: #ffa500; /* Orange icon color */
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

.feature-list, .user-notice-list {
  display: flex;
  flex-direction: column;
  margin-left: 20rpx; /* Indent lists */
  margin-bottom: 30rpx;
}

.feature-item {
  display: flex;
  align-items: flex-start; /* Align icon and text at the top */
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 15rpx; /* Spacing between list items */
}

.status-icon {
  font-size: 36rpx; /* Make icons larger */
  margin-right: 15rpx;
  flex-shrink: 0; /* Prevent icon from shrinking */
  line-height: 1.6; /* Align with text line-height */
}

.feature-item.success .status-icon {
  color: #28a745; /* Green for success */
}

.feature-item.error .status-icon {
  color: #dc3545; /* Red for error/pause */
}

.user-notice-list text {
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
  background-color: #ffa500; /* Primary action button (e.g., 立即提现) in orange */
}
.action-button.primary:active {
  background-color: #e69500; /* Darker orange on active */
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
