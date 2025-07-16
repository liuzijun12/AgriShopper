<template>
  <view class="security-alert-container">
    <view class="header">
      <text class="icon">🛡️</text>
      <view class="header-text-group">
        <text class="main-title">账户异常登录</text>
        <text class="datetime">{{ messageDate }} {{ messageTime }}</text>
      </view>
    </view>

    <view class="body-content">
      <text class="main-text">
        <text class="bold">检测到您的账户于{{ loginTime }}通过新设备登录</text>
      </text>
      <view class="detail-list">
        <text>设备信息：{{ deviceInfo }}</text>
        <text>登录地点：{{ loginLocation }}（IP：{{ loginIp }}）</text>
        <text>风险判定：非常用设备，存在盗号风险</text>
      </view>

      <text class="action-prompt">
        <text class="bold">请立即：</text>
      </text>
      <view class="action-list">
        <text>1. 修改高强度密码（含大小写+数字+符号）</text>
        <text>2. 开启二次验证（推荐人脸识别）</text>
        <text>3. 核查最近3天登录记录</text>
      </view>
    </view>

    <view class="function-area">
      <button class="action-button primary" @tap="modifyPassword">立即修改密码</button>
      <button class="action-button" @tap="viewLoginHistory">查看登录历史</button>
      <button class="action-button" @tap="contactSecurity">联系安全专员</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { onLoad } from '@dcloudio/uni-app';

// 定义响应式数据
const loginTime = ref('');
const deviceInfo = ref('');
const loginLocation = ref('');
const loginIp = ref('');

// 新增用于头部显示的日期和时间
const messageDate = ref('');
const messageTime = ref('');

// 在页面加载时获取传递的参数
onLoad((options) => {
  if (options.messageData) {
    try {
      const message = JSON.parse(decodeURIComponent(options.messageData));
      console.log('Received message data:', message);

      // 根据接收到的数据更新页面内容
      loginTime.value = message.time || '未知时间';
      deviceInfo.value = message.deviceInfo || '未知设备';
      loginLocation.value = message.loginLocation || '未知地点';
      loginIp.value = message.loginIp || '未知IP';

      // 更新头部显示的日期和时间
      messageDate.value = message.date || '';
      messageTime.value = message.time || '';

    } catch (e) {
      console.error('Failed to parse messageData:', e);
      uni.showToast({
        title: '数据加载失败',
        icon: 'none'
      });
    }
  }
});

// 按钮点击事件处理函数
const modifyPassword = () => {
  uni.showToast({
    title: '跳转到修改密码页面',
    icon: 'none'
  });
  // 实际应用中会是 uni.navigateTo({ url: '/pages/security/changePassword' });
};

const viewLoginHistory = () => {
  uni.showToast({
    title: '跳转到登录历史页面',
    icon: 'none'
  });
  // 实际应用中会是 uni.navigateTo({ url: '/pages/security/loginHistory' });
};

const contactSecurity = () => {
  uni.showToast({
    title: '联系安全专员',
    icon: 'none'
  });
  // 实际应用中会是 uni.makePhoneCall 或 uni.navigateTo({ url: '/pages/customerService/chat' });
};
</script>

<style scoped>
/*
  使用 rpx 单位以适应小程序，并调整布局以获得更好的移动端体验。
  'scoped' 属性确保这些样式只应用于此组件。
*/
page {
  background-color: #f0f2f5;
  height: 100%;
}

.security-alert-container {
  background-color: #ffffff;
  border-radius: 16rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 700rpx; /* 适应手机屏幕宽度，例如 iPhone 12 Pro Max 物理像素宽度为 428px，700rpx 约为 350px */
  padding: 50rpx 40rpx;
  box-sizing: border-box;
  border-left: 10rpx solid #dc3545; /* 红色边框表示紧急 */
  margin: 40rpx auto; /* 居中并上下留白 */
  font-family: 'PingFang SC', 'Helvetica Neue', Helvetica, Arial, sans-serif; /* 更好的中文字体支持 */
}

.header {
  display: flex;
  align-items: center; /* 垂直居中对齐 */
  margin-bottom: 40rpx;
  padding-bottom: 30rpx;
  border-bottom: 1rpx solid #eeeeee;
}

.header .icon {
  font-size: 60rpx; /* 图标大小 */
  margin-right: 20rpx;
  color: #dc3545; /* 盾牌图标颜色 */
  line-height: 1; /* 确保图标垂直居中 */
  flex-shrink: 0; /* 防止图标被压缩 */
}

.header-text-group {
  display: flex;
  flex-direction: column; /* 标题和日期时间垂直排列 */
  flex-grow: 1; /* 允许文本组占据剩余空间 */
  min-width: 0; /* 防止内容溢出 */
}

.main-title {
  font-size: 36rpx; /* 标题字体大小 */
  color: #333;
  font-weight: bold;
  line-height: 1.3; /* 行高，避免过于紧凑 */
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap; /* 标题不换行 */
}

.datetime {
  font-size: 28rpx; /* 日期时间字体大小，比标题小但足够清晰 */
  color: #666;
  line-height: 1.3;
  margin-top: 8rpx; /* 与标题的间距 */
}


.body-content {
  margin-bottom: 40rpx;
}

/* 针对 uni-app 的 text 标签调整，使其能像 p 标签一样有上下间距 */
.main-text, .action-prompt {
  font-size: 32rpx;
  color: #555;
  line-height: 1.6;
  margin-bottom: 20rpx;
  display: block; /* 使 text 标签能够应用 margin-bottom */
}

.bold {
  font-weight: bold;
  color: #333;
}

/* 列表样式 */
.detail-list, .action-list {
  display: flex;
  flex-direction: column;
  margin-left: 30rpx; /* 列表缩进 */
  margin-bottom: 30rpx;
}

.detail-list text, .action-list text {
  font-size: 28rpx;
  color: #666;
  line-height: 1.8;
  margin-bottom: 10rpx; /* 列表项之间的间距 */
}

.function-area {
  display: flex;
  flex-direction: column; /* 默认垂直堆叠 */
  gap: 20rpx; /* 按钮间距 */
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
  width: 100%; /* 按钮全宽 */
}

.action-button:active {
  opacity: 0.8; /* 点击时降低透明度 */
}

.action-button.primary {
  background-color: #dc3545; /* 立即修改密码按钮为红色 */
}

/* 响应式调整：在宽屏上横向排列按钮 */
@media (min-width: 600px) {
  .function-area {
    flex-direction: row; /* 在宽屏上横向排列 */
    justify-content: space-between;
  }
  .action-button {
    flex: 1; /* 平均分配宽度 */
    max-width: 30%; /* 限制最大宽度，防止按钮过宽 */
  }
}
</style>
