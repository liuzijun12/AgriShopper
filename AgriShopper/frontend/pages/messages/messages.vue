<template>
  <view class="page-container">
    <!-- 顶部标题栏 -->
    <view class="header">
      <text class="header-title">消息</text>
      <view class="header-actions">
        <view class="mark-all-read" @tap="markAllAsRead">
          <text>全部已读</text>
        </view>
      </view>
    </view>

    <!-- 消息分类标签 -->
    <scroll-view class="message-tabs" scroll-x>
      <view class="tabs-container">
        <view 
          v-for="(tab, index) in messageTabs" 
          :key="index"
          :class="['tab-item', currentTab === index ? 'tab-active' : '']"
          @tap="switchTab(index)"
        >
          <text>{{ tab.name }}</text>
          <view v-if="tab.unread > 0" class="unread-badge">{{ tab.unread }}</view>
        </view>
      </view>
    </scroll-view>

    <!-- 消息列表 -->
    <scroll-view 
      class="message-list" 
      scroll-y 
      @scrolltolower="loadMore"
      :refresher-enabled="true"
      @refresherrefresh="onRefresh"
    >
      <!-- 系统通知 -->
      <view v-if="currentTab === 0">
        <view class="message-group" v-for="(group, date) in groupedSystemMessages" :key="date">
          <view class="date-divider">{{ date }}</view>
          <view 
            class="message-item" 
            v-for="(message, index) in group" 
            :key="index"
            :class="{'unread': !message.read}"
            @tap="openMessage(message)"
          >
            <view class="message-content">
              <view class="message-header">
                <text class="message-title">{{ message.title }}</text>
                <text class="message-time">{{ message.time }}</text>
              </view>
              <text class="message-summary">{{ message.content }}</text>
            </view>
            <view v-if="!message.read" class="unread-dot"></view>
          </view>
        </view>
      </view>

      <!-- 订单消息 -->
      <view v-if="currentTab === 1">
        <view class="message-group" v-for="(group, date) in groupedOrderMessages" :key="date">
          <view class="date-divider">{{ date }}</view>
          <view 
            class="message-item" 
            v-for="(message, index) in group" 
            :key="index"
            :class="{'unread': !message.read}"
            @tap="openMessage(message)"
          >
            <image class="order-image" :src="message.image" mode="aspectFill"></image>
            <view class="message-content">
              <view class="message-header">
                <text class="message-title">{{ message.title }}</text>
                <text class="message-time">{{ message.time }}</text>
              </view>
              <text class="message-summary">{{ message.content }}</text>
              <view class="order-info">
                <text class="order-status">{{ message.status }}</text>
                <text class="order-id">订单号：{{ message.orderId }}</text>
              </view>
            </view>
            <view v-if="!message.read" class="unread-dot"></view>
          </view>
        </view>
      </view>

      <!-- 活动消息 -->
      <view v-if="currentTab === 2">
        <view class="message-group" v-for="(group, date) in groupedPromotionMessages" :key="date">
          <view class="date-divider">{{ date }}</view>
          <view 
            class="message-item promotion-item" 
            v-for="(message, index) in group" 
            :key="index"
            :class="{'unread': !message.read}"
            @tap="openMessage(message)"
          >
            <image class="promotion-image" :src="message.image" mode="aspectFill"></image>
            <view class="message-content">
              <view class="message-header">
                <text class="message-title">{{ message.title }}</text>
                <text class="message-time">{{ message.time }}</text>
              </view>
              <text class="message-summary">{{ message.content }}</text>
              <view class="promotion-info">
                <text class="promotion-tag">{{ message.tag }}</text>
                <text class="promotion-date">{{ message.validDate }}</text>
              </view>
            </view>
            <view v-if="!message.read" class="unread-dot"></view>
          </view>
        </view>
      </view>

      <!-- 客服消息 -->
      <view v-if="currentTab === 3">
        <view 
          class="service-item" 
          v-for="(message, index) in serviceMessages" 
          :key="index"
          @tap="enterChat(message)"
        >
          <image class="service-avatar" :src="message.avatar" mode="aspectFill"></image>
          <view class="message-content">
            <view class="message-header">
              <text class="service-name">{{ message.name }}</text>
              <text class="message-time">{{ message.time }}</text>
            </view>
            <text class="message-summary">{{ message.lastMessage }}</text>
          </view>
          <view v-if="message.unread > 0" class="unread-count">{{ message.unread }}</view>
        </view>
      </view>

      <!-- 加载更多 -->
      <view v-if="hasMore" class="loading-more">
        <text>加载中...</text>
      </view>
    </scroll-view>

  </view>
</template>

<script lang="ts">
export default {
  name: 'Messages',
  onShow() {
    uni.$emit('tabPageShow')
  },
  // 页面配置
  onPullDownRefresh() {
    this.onRefresh();
  },
  onReachBottom() {
    this.loadMore();
  }
}
</script>

<script setup lang="ts">
import { ref, computed } from 'vue';

// 头像图片
const aiAvatar = 'https://readdy.ai/api/search-image?query=3D%20cartoon%2C%20AI%20assistant%20robot%20with%20friendly%20face%2C%20subject%20fills%2080%20percent%20of%20frame%2C%20vibrant%20colors%20with%20soft%20gradients%2C%20minimalist%20design%2C%20smooth%20rounded%20shapes%2C%20subtle%20shading%2C%20no%20outlines%2C%20centered%20composition%2C%20isolated%20on%20white%20background%2C%20playful%20and%20friendly%20aesthetic%2C%20high%20detail%20quality%2C%20clean%20and%20modern%20look&width=100&height=100&seq=1001&orientation=squarish';
const userAvatar = 'https://readdy.ai/api/search-image?query=3D%20cartoon%2C%20simple%20user%20avatar%20icon%2C%20subject%20fills%2080%20percent%20of%20frame%2C%20vibrant%20colors%20with%20soft%20gradients%2C%20minimalist%20design%2C%20smooth%20rounded%20shapes%2C%20subtle%20shading%2C%20no%20outlines%2C%20centered%20composition%2C%20isolated%20on%20white%20background%2C%20playful%20and%20friendly%20aesthetic%2C%20high%20detail%20quality%2C%20clean%20and%20modern%20look&width=100&height=100&seq=1002&orientation=squarish';

// 消息分类标签
const messageTabs = ref([
  { name: '系统通知', unread: 2 },
  { name: '订单消息', unread: 3 },
  { name: '活动消息', unread: 1 },
  { name: '客服消息', unread: 5 }
]);

const currentTab = ref(0);

// 系统消息数据
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
    read: false
  }
]);

// 订单消息数据
const orderMessages = ref([
  {
    id: 1,
    title: '订单发货通知',
    content: '您购买的有机蔬菜已发货',
    time: '14:30',
    date: '2024-06-18',
    status: '已发货',
    orderId: '202406180001',
    image: 'https://readdy.ai/api/search-image?query=Fresh%20organic%20vegetables%20package%2C%20high-quality%20details%2C%20clear%20and%20sharp%2C%20isolated%20on%20white%20background%2C%20centered%20composition%2C%20soft%20lighting%2C%20no%20shadows%2C%20no%20text.&width=100&height=100',
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
    image: 'https://readdy.ai/api/search-image?query=Delivered%20package%20with%20check%20mark%2C%20high-quality%20details%2C%20clear%20and%20sharp%2C%20isolated%20on%20white%20background%2C%20centered%20composition%2C%20soft%20lighting%2C%20no%20shadows%2C%20no%20text.&width=100&height=100',
    read: true
  }
]);

// 活动消息数据
const promotionMessages = ref([
  {
    id: 1,
    title: '限时特惠活动',
    content: '新鲜水果限时8折，快来抢购！',
    time: '15:00',
    date: '2024-06-18',
    tag: '限时特惠',
    validDate: '2024.6.18-6.20',
    image: 'https://readdy.ai/api/search-image?query=Fresh%20fruits%20sale%20promotion%2C%20high-quality%20details%2C%20clear%20and%20sharp%2C%20isolated%20on%20white%20background%2C%20centered%20composition%2C%20soft%20lighting%2C%20no%20shadows%2C%20no%20text.&width=200&height=100',
    read: false
  }
]);

// 客服消息数据
const serviceMessages = ref([
  {
    id: 1,
    name: '在线客服',
    avatar: '/static/robot.png',
    lastMessage: '您好，请问有什么可以帮您？',
    time: '16:30',
    unread: 1
  },
  {
    id: 2,
    name: '售后客服',
    avatar: '/static/service.png',
    lastMessage: '您的退款申请已处理完成',
    time: '15:45',
    unread: 0
  }
]);

// 按日期分组的消息
const groupedSystemMessages = computed(() => {
  return groupMessagesByDate(systemMessages.value);
});

const groupedOrderMessages = computed(() => {
  return groupMessagesByDate(orderMessages.value);
});

const groupedPromotionMessages = computed(() => {
  return groupMessagesByDate(promotionMessages.value);
});

// 分组消息的辅助函数
const groupMessagesByDate = (messages: any[]) => {
  const groups: { [key: string]: any[] } = {};
  messages.forEach(message => {
    if (!groups[message.date]) {
      groups[message.date] = [];
    }
    groups[message.date].push(message);
  });
  return groups;
};

// 加载状态
const hasMore = ref(false);
const loading = ref(false);

// 切换标签
const switchTab = (index: number) => {
  currentTab.value = index;
};

// 标记所有消息为已读
const markAllAsRead = () => {
  systemMessages.value.forEach(msg => msg.read = true);
  orderMessages.value.forEach(msg => msg.read = true);
  promotionMessages.value.forEach(msg => msg.read = true);
  messageTabs.value.forEach(tab => tab.unread = 0);
};

// 打开消息详情
const openMessage = (message: any) => {
  message.read = true;
  // 更新未读数
  updateUnreadCount();
  // TODO: 跳转到消息详情页
  uni.showToast({
    title: '查看消息详情：' + message.title,
    icon: 'none'
  });
};

// 进入聊天界面
const enterChat = (service: any) => {
  // TODO: 跳转到聊天界面
  uni.navigateTo({
    url: `/pages/chat/chat?id=${service.id}`
  });
};

// 更新未读消息数
const updateUnreadCount = () => {
  messageTabs.value[0].unread = systemMessages.value.filter(msg => !msg.read).length;
  messageTabs.value[1].unread = orderMessages.value.filter(msg => !msg.read).length;
  messageTabs.value[2].unread = promotionMessages.value.filter(msg => !msg.read).length;
  messageTabs.value[3].unread = serviceMessages.value.reduce((total, service) => total + service.unread, 0);
};

// 加载更多消息
const loadMore = () => {
  if (loading.value || !hasMore.value) return;
  loading.value = true;
  // TODO: 实现加载更多逻辑
  setTimeout(() => {
    loading.value = false;
  }, 1000);
};

// 下拉刷新
const onRefresh = () => {
  // TODO: 实现刷新逻辑
  setTimeout(() => {
    uni.stopPullDownRefresh();
  }, 1000);
};
</script>

<style>
page {
  background-color: #f5f5f5;
  height: 100%;
}

.page-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  padding-bottom: 120rpx; /* 增加底部padding，为导航栏留出空间 */
  box-sizing: border-box;
}

/* 顶部标题栏 */
.header {
  padding: 20rpx 30rpx;
  background-color: #ffffff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1rpx solid #eee;
}

.header-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333;
}

.header-actions {
  display: flex;
  align-items: center;
}

.mark-all-read {
  display: flex;
  align-items: center;
  font-size: 28rpx;
  color: #666;
  padding: 10rpx 20rpx;
  background-color: #f5f5f5;
  border-radius: 30rpx;
}

/* 消息分类标签 */
.message-tabs {
  background-color: #ffffff;
  padding: 20rpx 0;
  white-space: nowrap;
  border-bottom: 1rpx solid #eee;
}

.tabs-container {
  display: inline-flex;
  height: 88rpx;
  padding: 0 20rpx;
}

.tab-item {
  position: relative;
  padding: 0 30rpx;
  margin-right: 20rpx;
  font-size: 28rpx;
  color: #666;
  display: flex;
  align-items: center;
}

.tab-active {
  color: #4CAF50;
  font-weight: 500;
}

.tab-active::after {
  content: '';
  position: absolute;
  bottom: -10rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 40rpx;
  height: 4rpx;
  background-color: #4CAF50;
  border-radius: 2rpx;
}

.unread-badge {
  position: absolute;
  top: 7rpx;
  right: 10rpx;
  min-width: 32rpx;
  height: 32rpx;
  padding: 0 6rpx;
  background-color: #ff4d4f;
  border-radius: 16rpx;
  color: #fff;
  font-size: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 消息列表 */
.message-list {
  flex: 1;
  background-color: #f5f5f5;
  padding-bottom: 20rpx;
}

.message-group {
  margin-bottom: 20rpx;
}

.date-divider {
  padding: 20rpx 30rpx;
  font-size: 24rpx;
  color: #999;
}

.message-item {
  background-color: #ffffff;
  padding: 30rpx;
  display: flex;
  align-items: flex-start;
  position: relative;
  border-bottom: 1rpx solid #eee;
}

.message-icon {
  margin-right: 20rpx;
}

.message-content {
  flex: 1;
  margin-right: 20rpx;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10rpx;
}

.message-title {
  font-size: 30rpx;
  color: #333;
  font-weight: 500;
}

.message-time {
  font-size: 24rpx;
  color: #999;
}

.message-summary {
  font-size: 28rpx;
  color: #666;
  line-height: 1.5;
}

.unread-dot {
  width: 16rpx;
  height: 16rpx;
  background-color: #ff4d4f;
  border-radius: 50%;
  position: absolute;
  right: 30rpx;
  top: 30rpx;
}

/* 订单消息样式 */
.order-image {
  width: 100rpx;
  height: 100rpx;
  border-radius: 10rpx;
  margin-right: 20rpx;
}

.order-info {
  margin-top: 16rpx;
  display: flex;
  align-items: center;
}

.order-status {
  font-size: 24rpx;
  color: #4CAF50;
  background-color: rgba(76, 175, 80, 0.1);
  padding: 4rpx 12rpx;
  border-radius: 4rpx;
  margin-right: 20rpx;
}

.order-id {
  font-size: 24rpx;
  color: #999;
}

/* 活动消息样式 */
.promotion-image {
  width: 200rpx;
  height: 100rpx;
  border-radius: 10rpx;
  margin-right: 20rpx;
}

.promotion-info {
  margin-top: 16rpx;
  display: flex;
  align-items: center;
}

.promotion-tag {
  font-size: 24rpx;
  color: #ff4d4f;
  background-color: rgba(255, 77, 79, 0.1);
  padding: 4rpx 12rpx;
  border-radius: 4rpx;
  margin-right: 20rpx;
}

.promotion-date {
  font-size: 24rpx;
  color: #999;
}

/* 客服消息样式 */
.service-item {
  background-color: #ffffff;
  padding: 30rpx;
  display: flex;
  align-items: center;
  border-bottom: 1rpx solid #eee;
}

.service-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 40rpx;
  margin-right: 20rpx;
}

.service-name {
  font-size: 30rpx;
  color: #333;
  font-weight: 500;
}

.unread-count {
  min-width: 40rpx;
  height: 40rpx;
  padding: 0 10rpx;
  background-color: #ff4d4f;
  border-radius: 20rpx;
  color: #fff;
  font-size: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 加载更多 */
.loading-more {
  padding: 30rpx 0;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
