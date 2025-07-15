<template>
  <view class="page-container">
    <view class="header">
      <text class="header-title">消息</text>
      <view class="header-actions">
        <view class="mark-all-read" @tap="markAllAsRead">
          <image :src="getImageUrl('messages/clear_icon.png')" class="action-icon" mode="aspectFit"></image>
        </view>
      </view>
    </view>

    <scroll-view class="message-tabs-scroll" scroll-x>
      <view class="tabs-container">
        <view
          v-for="(tab, index) in messageTabs"
          :key="index"
          :class="['tab-item', currentTab === index ? 'tab-active' : '']"
          @tap="switchTab(index)"
        >
          <image
            :src="getImageUrl(tab.icon)"
            class="tab-icon"
            mode="aspectFit"
          ></image>
          <text>{{ tab.name }}</text>
          <view v-if="tab.unread > 0" class="unread-badge">{{ tab.unread }}</view>
        </view>
      </view>
    </scroll-view>

    <scroll-view
      class="message-list-scroll"
      scroll-y
      @scrolltolower="loadMore"
      :refresher-enabled="true"
      @refresherrefresh="onRefresh"
    >
      <view v-if="currentTab === 0" class="message-section">
        <view class="message-group" v-for="(group, date) in groupedSystemMessages" :key="date">
          <view class="date-divider">{{ date }}</view>
          <view
            class="message-card system-card"
            v-for="(message, index) in group"
            :key="index"
            @tap="openMessage(message)"
          >
            <view class="card-content">
              <view class="card-header">
                <text class="card-title">{{ message.title }}</text>
                <text class="card-time">{{ message.time }}</text>
              </view>
              <text class="card-summary">{{ message.content }}</text>
            </view>
            <view class="unread-dot"></view>
          </view>
        </view>
      </view>

      <view v-if="currentTab === 1" class="message-section">
        <view class="message-group" v-for="(group, date) in groupedOrderMessages" :key="date">
          <view class="date-divider">{{ date }}</view>
          <view
            class="message-card order-card"
            v-for="(message, index) in group"
            :key="index"
            @tap="openMessage(message)"
          >
            <view class="card-content">
              <view class="card-header">
                <text class="card-title">{{ message.title }}</text>
                <text class="card-time">{{ message.time }}</text>
              </view>
              <text class="card-summary">{{ message.content }}</text>
              <view class="card-info order-info">
                <text class="status-tag order-status-tag">{{ message.status }}</text>
                <text class="card-id">订单号：{{ message.orderId }}</text>
              </view>
            </view>
            <view class="unread-dot"></view>
          </view>
        </view>
      </view>

      <view v-if="currentTab === 2" class="message-section">
        <view class="message-group" v-for="(group, date) in groupedPromotionMessages" :key="date">
          <view class="date-divider">{{ date }}</view>
          <view
            class="message-card promotion-card"
            v-for="(message, index) in group"
            :key="index"
            @tap="openMessage(message)"
          >
            <image class="card-image promotion-image" :src="message.image" mode="aspectFill" @error="onImageError('promotion', message)"></image>
            <view class="card-content">
              <view class="card-header">
                <text class="card-title">{{ message.title }}</text>
                <text class="card-time">{{ message.time }}</text>
              </view>
              <text class="card-summary">{{ message.content }}</text>
              <view class="card-info promotion-info">
                <text class="status-tag promotion-tag">{{ message.tag }}</text>
                <text class="card-date">{{ message.validDate }}</text>
              </view>
            </view>
            <view class="unread-dot"></view>
          </view>
        </view>
      </view>

      <view v-if="currentTab === 3" class="message-section">
        <view
          class="message-card service-card"
          v-for="(message, index) in serviceMessages"
          :key="index"
          @tap="enterChat(message)"
        >
          <image class="service-avatar" :src="getImageUrl(message.avatar)" mode="aspectFill" @error="onImageError('service', message)"></image>
          <view class="card-content">
            <view class="card-header">
              <text class="service-name">{{ message.name }}</text>
              <text class="card-time">{{ message.time }}</text>
            </view>
            <text class="card-summary">{{ message.lastMessage }}</text>
          </view>
          <view class="unread-dot"></view>
        </view>
      </view>

      <view v-if="hasMore" class="loading-more">
        <text>加载中...</text>
      </view>
    </scroll-view>
    
    <!-- 微信登录弹窗 -->
    <!-- 已移除WxLoginModal相关内容 -->
  </view>
</template>

<script>
export default {
  name: 'Messages',
  onShow() {
    uni.$emit('tabPageShow')
  },
  onPullDownRefresh() {
    this.onRefresh();
  },
  onReachBottom() {
    this.loadMore();
  }
}
</script>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { store } from '../../store.js';
// 移除登录相关的ref、方法、import WxLoginModal

const messageTabs = ref([
  { name: '系统通知', unread: 2, icon: 'messages/system_notification.png' },
  { name: '订单消息', unread: 3, icon: 'messages/order_message.png' },
  { name: '活动消息', unread: 1, icon: 'messages/activity_message.png' },
  { name: '客服消息', unread: 5, icon: 'messages/customer_service_message.png' }
]);

const currentTab = ref(0);

// 登录状态
const isLoggedIn = ref(false);
const showLoginModal = ref(false);
const userAvatar = ref('/static/tabbar/user.png');

const systemMessages = ref([
  {
    id: 1,
    title: '账户安全提醒',
    content: '您的账户于2024年6月18日10:05在新设备上登录，如非本人操作，请及时修改密码。',
    time: '10:05',
    date: '2024-06-18',
    read: false
  },
  {
    id: 2,
    title: '系统升级通知',
    content: '系统将于今晚22:00-23:00进行例行维护升级，期间可能影响部分功能使用。',
    time: '09:30',
    date: '2024-06-18',
    read: true
  }
]);

const orderMessages = ref([
  {
    id: 1,
    title: '订单发货通知',
    content: '您购买的有机蔬菜已发货',
    time: '14:30',
    date: '2024-06-18',
    status: '已发货',
    orderId: '202406180001',
    image: 'https://placehold.co/100x100/cccccc/ffffff?text=Order1',
    read: false
  },
  {
    id: 2,
    title: '订单已送达',
    content: '您的订单已送达指定地点',
    time: '11:20',
    date: '2024-06-18',
    status: '已完成',
    orderId: '202406170002',
    image: 'https://placehold.co/100x100/cccccc/ffffff?text=Order2',
    read: true
  }
]);

const promotionMessages = ref([
  {
    id: 1,
    title: '限时特惠活动',
    content: '新鲜水果限时8折，快来抢购！',
    time: '15:00',
    date: '2024-06-18',
    tag: '限时特惠',
    validDate: '2024.6.18-6.20',
    image: 'https://placehold.co/200x100/cccccc/ffffff?text=Promo',
    read: false
  }
]);

const serviceMessages = ref([
  {
    id: 1,
    name: '在线客服',
    // 请将 'cs_online_en.png' 替换为您实际的文件名
    avatar: 'messages/cs_online_en.png',
    lastMessage: '您好，请问有什么可以帮您？',
    time: '16:30',
    unread: 1
  },
  {
    id: 2,
    name: '售后客服',
    // 请将 'cs_aftersales_en.png' 替换为您实际的文件名
    avatar: 'messages/cs_aftersales_en.png',
    lastMessage: '您的退款申请已处理完成',
    time: '15:45',
    unread: 0
  }
]);

const groupedSystemMessages = computed(() => {
  return groupMessagesByDate(systemMessages.value);
});

const groupedOrderMessages = computed(() => {
  return groupMessagesByDate(orderMessages.value);
});

const groupedPromotionMessages = computed(() => {
  return groupMessagesByDate(promotionMessages.value);
});

const groupMessagesByDate = (messages) => {
  const groups = {};
  messages.forEach(message => {
    if (!groups[message.date]) {
      groups[message.date] = [];
    }
    groups[message.date].push(message);
  });
  return groups;
};

const hasMore = ref(false);
const loading = ref(false);

const switchTab = (index) => {
  currentTab.value = index;
};

const markAllAsRead = () => {
  systemMessages.value.forEach(msg => msg.read = true);
  orderMessages.value.forEach(msg => msg.read = true);
  promotionMessages.value.forEach(msg => msg.read = true);
  serviceMessages.value.forEach(msg => msg.unread = 0);
  updateUnreadCount();
};

const openMessage = (message) => {
  if (message.read !== undefined) {
    message.read = true;
  }
  if (message.unread !== undefined) {
    message.unread = 0;
  }
  updateUnreadCount();
  uni.showToast({
    title: '查看消息详情：' + message.title,
    icon: 'none'
  });
};

const enterChat = (service) => {
  service.unread = 0;
  updateUnreadCount();
  uni.navigateTo({
    url: `/pages/chat/chat?id=${service.id}`
  });
};

const updateUnreadCount = () => {
  messageTabs.value[0].unread = systemMessages.value.filter(msg => !msg.read).length;
  messageTabs.value[1].unread = orderMessages.value.filter(msg => !msg.read).length;
  messageTabs.value[2].unread = promotionMessages.value.filter(msg => !msg.read).length;
  messageTabs.value[3].unread = serviceMessages.value.reduce((total, service) => total + service.unread, 0);
};

const onImageError = (type, item) => {
  console.error(`Failed to load ${type} image for item:`, item);
  if (type === 'order' || type === 'promotion') {
    item.image = 'https://placehold.co/100x100/ff0000/ffffff?text=Error';
  } else if (type === 'service') {
    // 假设当原始头像加载失败时，也 fallback 到您提供的错误头像图片
    item.avatar = getImageUrl('messages/cs_error_fallback.png'); // 可以为错误头像准备一个通用的 fallback 图片
  }
};

const loadMore = () => {
  if (loading.value || !hasMore.value) return;
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
  }, 1000);
};

const onRefresh = () => {
  setTimeout(() => {
    uni.stopPullDownRefresh();
  }, 1000);
};

// 检查登录状态
const checkLoginStatus = () => {
  try {
    const userInfo = store.getUserInfo();
    if (userInfo && userInfo.openid) {
      isLoggedIn.value = true;
      if (userInfo.avatar) {
        userAvatar.value = userInfo.avatar;
      }
    } else {
      isLoggedIn.value = false;
    }
  } catch (error) {
    console.log('检查登录状态失败:', error);
    isLoggedIn.value = false;
  }
};

// 处理用户点击
const handleUserClick = () => {
  if (isLoggedIn.value) {
    // 已登录，跳转到个人中心
    uni.switchTab({
      url: '/pages/my/my'
    });
  } else {
    // 未登录，显示登录弹窗
    showLoginModal.value = true;
  }
};

// 关闭登录弹窗
const handleCloseLogin = () => {
  showLoginModal.value = false;
};

// 登录成功处理
const handleLoginSuccess = (userInfo) => {
  console.log('登录成功:', userInfo);
  checkLoginStatus();
  showLoginModal.value = false;
};

// 图片URL处理函数
const getImageUrl = (path) => {
  if (!path) return '';
  
  // 如果是完整URL，直接返回
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path;
  }
  
  // 如果是tabbar图片，直接拼接后端地址
  if (path.startsWith('tabbar/')) {
    return 'http://localhost:8080/' + path;
  }
  
  // 如果是相对路径，拼接后端地址
  return `http://localhost:8080/${path}`;
};

// 页面加载时检查登录状态
onMounted(() => {
  checkLoginStatus();
});
</script>

<style>
page {
  background-color: #f0f2f5;
  height: 100%;
}

.page-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  padding-bottom: env(safe-area-inset-bottom);
  box-sizing: border-box;
}

/* Header */
.header {
  padding: 24rpx 32rpx;
  background-color: #ffffff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1rpx solid #eeeeee;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.header-title {
  font-size: 38rpx;
  font-weight: 700;
  color: #222222;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.user-avatar {
  position: relative;
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  overflow: hidden;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.login-badge {
  position: absolute;
  bottom: -5rpx;
  right: -5rpx;
  background: #07c160;
  color: #fff;
  font-size: 18rpx;
  padding: 2rpx 6rpx;
  border-radius: 8rpx;
  white-space: nowrap;
}

.mark-all-read {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64rpx;
  height: 64rpx;
  background-color: #f0f2f5;
  border-radius: 50%;
  box-sizing: border-box;
  transition: background-color 0.2s ease;
}

.mark-all-read:active {
  background-color: #e0e2e5;
}

.action-icon {
  width: 44rpx;
  height: 44rpx;
  display: block;
}

/* Message category tabs */
.message-tabs-scroll {
  background-color: #ffffff;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #eeeeee;
  box-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.03);
  white-space: nowrap;
}

.tabs-container {
  display: flex;
  width: 100%;
  height: 96rpx;
  padding: 0 20rpx;
  box-sizing: border-box;
  justify-content: space-around;
  align-items: center;
}

.tab-item {
  position: relative;
  font-size: 28rpx;
  color: #666666;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  min-width: 0;
  text-align: center;
  padding: 0 10rpx;
}

.tab-icon {
  width: 56rpx;
  height: 56rpx;
  margin-bottom: 6rpx;
}

.tab-active {
  color: #007bff;
  font-weight: 600;
}

.unread-badge {
  position: absolute;
  top: 4rpx;
  right: 12rpx;
  min-width: 36rpx;
  height: 36rpx;
  padding: 0 8rpx;
  background-color: #ff4d4f;
  border-radius: 18rpx;
  color: #fff;
  font-size: 22rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
}

/* Message List - scroll area */
.message-list-scroll {
  flex: 1;
  background-color: #f0f2f5;
  padding: 20rpx 0;
  box-sizing: border-box;
}

.message-section {
  padding: 0 20rpx;
}

.message-group {
  margin-bottom: 30rpx;
}

.date-divider {
  padding: 20rpx 0;
  font-size: 26rpx;
  color: #999999;
  font-weight: 500;
  text-align: left;
  margin-left: 10rpx;
}

/* Unified Card Style - 蓝色边框现在是默认样式 */
.message-card {
  background-color: #ffffff;
  border-radius: 16rpx;
  box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.08);
  margin-bottom: 24rpx;
  padding: 30rpx;
  display: flex;
  align-items: flex-start;
  position: relative;
  overflow: hidden;
  border-left: 10rpx solid #007bff; /* 蓝色左边框现在无条件显示 */
  padding-left: 20rpx; /* 调整内边距以适应边框 */
}

/* Common card content styling */
.card-content {
  flex: 1;
  min-width: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.card-title {
  font-size: 32rpx;
  color: #333333;
  font-weight: 600;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  max-width: 80%;
}

.card-time {
  font-size: 26rpx;
  color: #999999;
  flex-shrink: 0;
  margin-left: 10rpx;
}

.card-summary {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 10rpx;
}

/* Unread dot style (现在无条件显示) */
.unread-dot {
  width: 18rpx;
  height: 18rpx;
  background-color: #ff4d4f;
  border-radius: 50%;
  position: absolute;
  right: 30rpx;
  top: 30rpx;
  z-index: 2;
}

/* Image styles for Order and Promotion Cards */
.card-image {
  border-radius: 8rpx;
  flex-shrink: 0;
  background-color: #f5f5f5;
}

.order-image {
  width: 140rpx;
  height: 140rpx;
  margin-right: 24rpx;
}

.promotion-image {
  width: 240rpx;
  height: 140rpx;
  margin-right: 24rpx;
}

/* Information sections (order, promotion) */
.card-info {
  margin-top: 16rpx;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

/* Unified Status Tag Style */
.status-tag {
  font-size: 24rpx;
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
  font-weight: 500;
  margin-right: 16rpx;
  white-space: nowrap;
}

.order-status-tag {
  color: #28a745;
  background-color: rgba(40, 167, 69, 0.1);
}

.promotion-tag {
  color: #dc3545;
  background-color: rgba(220, 53, 69, 0.1);
}

.card-id, .card-date {
  font-size: 24rpx;
  color: #999999;
}

/* Customer Service Card specific styles */
.service-card {
  align-items: center;
}

.service-avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  margin-right: 24rpx;
  flex-shrink: 0;
  box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.05);
}

.service-name {
  font-size: 32rpx;
  color: #333333;
  font-weight: 600;
}

/* Load More */
.loading-more {
  padding: 30rpx 0;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 28rpx;
  color: #999999;
}
</style>