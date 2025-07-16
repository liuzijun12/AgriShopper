<template>
  <view class="page-container">
    <!-- 顶部导航栏 -->
    <view class="header">
      <text class="header-title">消息</text>
      <view class="header-actions">
        <view class="mark-all-read" @tap="markAllAsRead">
          <!-- clear_icon.png 的引用路径已根据后端静态资源配置修改 -->
          <image :src="backendStaticBaseUrl + '/messages/clear_icon.png'" class="action-icon" mode="aspectFit"></image>
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
          <!-- 使用一个包裹元素来控制图标颜色，因为uni-app的image标签无法直接用color属性改变颜色 -->
          <view :class="['tab-icon-wrapper', currentTab === index ? 'tab-icon-active' : '']">
            <image
              :src="tab.icon"
              class="tab-icon"
              mode="aspectFit"
            ></image>
          </view>
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
      <!-- System Notifications -->
      <view v-if="currentTab === 0" class="message-section">
        <view class="message-group" v-for="(group, date) in groupedSystemMessages" :key="date">
          <view class="date-divider">{{ date }}</view>
          <view
            :class="[
              'message-card',
              'system-card',
              // 根据消息类型添加特定颜色类和边框
              message.type === 'highRiskOp' ? 'high-risk-card' : '',
              message.type === 'riskWarning' ? 'high-risk-card' : '',
              message.type === 'platformOperation' ? 'maintenance-card' : '',
              message.type === 'ruleUpdate' ? 'rule-card' : '',
              message.type === 'globalAnnouncement' ? 'announcement-card' : ''
            ]"
            v-for="(message, index) in group"
            :key="index"
            @tap="handleMessageClick(message, 'system')"
          >
            <view class="card-content">
              <view class="card-header">
                <!-- 图标 -->
                <text class="card-icon">{{ message.icon }}</text>
                <text class="card-title">{{ message.title }}</text>
                <text class="card-time">{{ message.time }}</text>
              </view>
              <text class="card-summary">{{ message.content }}</text>

              <!-- 特定类型详情 (这里只展示部分，完整详情在各自的详情页中展示) -->
              <view v-if="message.type === 'highRiskOp'" class="card-info system-details">
                <text>异常设备/IP: {{ message.deviceIp }}</text>
                <text>操作类型: {{ message.operationType }}</text>
              </view>
              <view v-else-if="message.type === 'platformOperation'" class="card-info system-details">
                <text>维护时间: {{ message.maintenanceTime }}</text>
                <text>影响范围: {{ message.impactScope }}</text>
              </view>
              <view v-else-if="message.type === 'ruleUpdate'" class="card-info system-details">
                <text>生效时间: {{ message.effectiveTime }}</text>
                <text>核心变更: {{ message.newRule }}</text>
              </view>
              <view v-else-if="message.type === 'riskWarning'" class="card-info system-details">
                <text>风险对象: {{ message.riskObject }}</text>
                <text>潜在影响: {{ message.potentialImpact }}</text>
              </view>
              <view v-else-if="message.type === 'globalAnnouncement'" class="card-info system-details">
                <text>受影响时间: {{ message.affectedTimeRange }}</text>
                <text>暂停服务: {{ message.pausedServices }}</text>
              </view>
            </view>
            <view v-if="!message.read" class="unread-dot"></view>
          </view>
        </view>
      </view>

      <!-- Order Messages -->
      <view v-if="currentTab === 1" class="message-section">
        <!-- 筛选和搜索功能 -->
        <view class="order-filter-search-bar">
          <view class="search-input-container">
            <input
              class="search-input"
              v-model="searchQuery"
              placeholder="搜索订单编号或商品名称"
              @confirm="performSearch"
              confirm-type="search"
            />
            <text v-if="searchQuery" class="clear-search-btn" @tap="clearSearch">x</text>
          </view>
          <!-- Display current search query -->
          <view v-if="debouncedSearchQuery" class="current-search-query">
            正在搜索: <text class="highlight">{{ debouncedSearchQuery }}</text>
          </view>
          <scroll-view scroll-x class="filter-tabs-scroll">
            <view class="filter-tabs-container">
              <view
                v-for="(status, sIndex) in orderFilterOptions"
                :key="sIndex"
                :class="['filter-tab-item', filterStatus === status.value ? 'filter-tab-active' : '']"
                @tap="filterByStatus(status.value)"
              >
                {{ status.label }}
              </view>
            </view>
          </scroll-view>
        </view>

        <view class="message-group" v-for="(group, date) in groupedOrderMessages" :key="date">
          <view class="date-divider">{{ date }}</view>
          <view
            class="swipe-wrapper"
            v-for="(message) in group"
            :key="message.id"
            @touchstart="onTouchStart($event, message.id)"
            @touchmove="onTouchMove($event, message.id)"
            @touchend="onTouchEnd($event, message.id)"
            @tap="handleMessageClick(message, 'order')"
          >
            <view
              class="message-card order-card"
              :style="{ transform: `translateX(${swipeStates[message.id] || 0}rpx)` }"
            >
              <view class="card-content">
                <view class="card-header">
                  <text class="card-title">{{ message.title }}</text>
                  <text class="card-time">{{ message.time }}</text>
                  <text v-if="message.alertIcon" class="alert-icon">{{ message.alertIcon }}</text>
                  <!-- 垃圾桶图标已移除 -->
                </view>
                
                <view class="order-product-info">
                  <image v-if="message.productImage" class="product-thumbnail" :src="message.productImage" mode="aspectFill" @error="onImageError('product', message)"></image>
                  <view class="product-details">
                    <text class="product-name">{{ message.productName }}</text>
                    <text class="product-spec">{{ message.productSpec }}</text>
                  </view>
                </view>

                <view class="card-info order-info">
                  <text :class="['status-tag', 'order-status-tag', message.statusClass]">{{ message.status }}</text>
                  <text class="card-id">订单号：{{ message.orderId }}</text>
                </view>
              </view>
              <view v-if="!message.read" class="unread-dot"></view>
              <button v-if="message.actionButtonText" :class="['action-button', message.actionButtonClass]" @tap.stop="handleOrderAction(message)">
                {{ message.actionButtonText }}
              </button>
            </view>
            <!-- 右滑删除按钮 -->
            <view class="delete-button-container">
              <view class="delete-button" @tap.stop="deleteOrderMessage(message.id)">删除</view>
            </view>
          </view>
          <view v-if="group.length === 0 && searchQuery === '' && filterStatus === 'all'" class="no-messages">
            暂无订单消息
          </view>
          <view v-else-if="group.length === 0" class="no-messages">
            无匹配的订单消息
          </view>
        </view>
      </view>

      <!-- Activity Messages -->
      <view v-if="currentTab === 2" class="message-section">
        <!-- 筛选和搜索功能 -->
        <view class="order-filter-search-bar">
          <view class="search-input-container">
            <input
              class="search-input"
              v-model="activitySearchQuery"
              placeholder="搜索活动标题或关键词"
              @confirm="performActivitySearch"
              confirm-type="search"
            />
            <text v-if="activitySearchQuery" class="clear-search-btn" @tap="clearActivitySearch">x</text>
          </view>
          <view v-if="debouncedActivitySearchQuery" class="current-search-query">
            正在搜索: <text class="highlight">{{ debouncedActivitySearchQuery }}</text>
          </view>
          <scroll-view scroll-x class="filter-tabs-scroll">
            <view class="filter-tabs-container">
              <view
                v-for="(type, tIndex) in activityFilterOptions"
                :key="tIndex"
                :class="['filter-tab-item', activityFilterStatus === type.value ? 'filter-tab-active' : '']"
                @tap="filterByActivityStatus(type.value)"
              >
                {{ type.label }}
              </view>
            </view>
          </scroll-view>
        </view>

        <view class="message-group" v-for="(group, date) in groupedPromotionMessages" :key="date">
          <view class="date-divider">{{ date }}</view>
          <view
            class="swipe-wrapper"
            v-for="(message) in group"
            :key="message.id"
            @touchstart="onTouchStart($event, message.id)"
            @touchmove="onTouchMove($event, message.id)"
            @touchend="onTouchEnd($event, message.id)"
            @tap="handleMessageClick(message, 'activity')"
          >
            <view
              class="message-card promotion-card"
              :style="{ transform: `translateX(${swipeStates[message.id] || 0}rpx)` }"
            >
              <image class="card-image promotion-image" :src="message.image" mode="aspectFill" @error="onImageError('promotion', message)"></image>
              <view class="card-content">
                <view class="card-header">
                  <text class="card-title">{{ message.title }}</text>
                  <text class="card-time">{{ message.time }}</text>
                  <text v-if="message.isNew" class="new-badge">新</text>
                  <text v-else-if="!message.read" class="unread-dot-small"></text>
                </view>
                <view class="promotion-meta">
                  <text :class="['status-tag', 'promotion-type-tag', message.typeClass]">{{ message.type }}</text>
                  <text class="promotion-sender">{{ message.sender }}</text>
                </view>
                <text class="card-summary promotion-benefit">{{ message.benefitSummary }}</text>
                <text class="promotion-validity">{{ message.validity }}</text>
                <text class="promotion-target">{{ message.targetAudience }}</text>
              </view>
            </view>
            <!-- 右滑删除按钮 -->
            <view class="delete-button-container">
              <view class="delete-button" @tap.stop="deletePromotionMessage(message.id)">删除</view>
            </view>
          </view>
          <view v-if="group.length === 0 && activitySearchQuery === '' && activityFilterStatus === 'all'" class="no-messages">
            暂无活动消息
          </view>
          <view v-else-if="group.length === 0" class="no-messages">
            无匹配的活动消息
          </view>
        </view>
      </view>

      <!-- Customer Service Messages -->
      <view v-if="currentTab === 3" class="message-section">
        <!-- 发起新咨询按钮 -->
        <view class="start-new-chat-container">
          <button class="start-new-chat-button" @tap="startNewCustomerServiceChat">
            <text class="new-chat-icon">+</text> 发起新咨询
          </button>
        </view>

        <!-- 客服消息筛选和搜索功能 -->
        <view class="order-filter-search-bar">
          <view class="search-input-container">
            <input
              class="search-input"
              v-model="csSearchQuery"
              placeholder="搜索订单号或问题描述"
              @confirm="performCsSearch"
              confirm-type="search"
            />
            <text v-if="csSearchQuery" class="clear-search-btn" @tap="clearSearch">x</text>
          </view>
          <view v-if="debouncedCsSearchQuery" class="current-search-query">
            正在搜索: <text class="highlight">{{ debouncedCsSearchQuery }}</text>
          </view>
          <scroll-view scroll-x class="filter-tabs-scroll">
            <view class="filter-tabs-container">
              <view
                v-for="(status, sIndex) in csFilterStatusOptions"
                :key="sIndex"
                :class="['filter-tab-item', csFilterStatus === status.value ? 'filter-tab-active' : '']"
                @tap="filterByCsStatus(status.value)"
              >
                {{ status.label }}
              </view>
            </view>
          </scroll-view>
          <scroll-view scroll-x class="filter-tabs-scroll issue-type-filter">
            <view class="filter-tabs-container">
              <view
                v-for="(type, tIndex) in csIssueTypeOptions"
                :key="tIndex"
                :class="['filter-tab-item', csFilterIssueType === type.value ? 'filter-tab-active' : '']"
                @tap="filterByCsIssueType(type.value)"
              >
                {{ type.label }}
              </view>
            </view>
          </scroll-view>
        </view>

        <view class="message-group" v-for="(group, date) in groupedServiceMessages" :key="date">
          <view class="date-divider">{{ date }}</view>
          <view
            class="swipe-wrapper"
            v-for="(message) in group"
            :key="message.id"
            @touchstart="onTouchStart($event, message.id)"
            @touchmove="onTouchMove($event, message.id)"
            @touchend="onTouchEnd($event, message.id)"
            @tap="handleMessageClick(message, 'customerService')"
          >
            <view
              class="message-card service-card"
              :style="{ transform: `translateX(${swipeStates[message.id] || 0}rpx)` }"
            >
              <image class="service-avatar" :src="message.avatar" mode="aspectFill" @error="onImageError('service', message)"></image>
              <view class="card-content">
                <view class="card-header">
                  <text class="service-name">{{ message.title }}</text>
                  <text class="card-time">{{ message.time }}</text>
                  <text v-if="message.priorityIcon" class="priority-icon">{{ message.priorityIcon }}</text>
                </view>
                <view class="service-meta">
                  <text v-if="message.senderType === 'agent'" class="sender-info">{{ message.senderName }}</text>
                  <text v-else class="sender-info">{{ message.senderType }}</text>
                  <text v-if="message.orderId" class="associated-order">关联订单: {{ message.orderId }}</text>
                </view>
                <text class="card-summary">{{ message.lastMessage }}</text>
                <view class="service-tags">
                  <text :class="['status-tag', 'issue-type-tag', message.issueTypeClass]">{{ message.issueType }}</text>
                  <text :class="['status-tag', 'cs-status-tag', message.statusClass]">{{ message.status }}</text>
                </view>
              </view>
              <view v-if="message.unread > 0" class="unread-badge-small">{{ message.unread }}</view>
            </view>
            <!-- 右滑删除按钮 -->
            <view class="delete-button-container">
              <view class="delete-button" @tap.stop="deleteServiceMessage(message.id)">删除</view>
            </view>
          </view>
          <view v-if="group.length === 0 && csSearchQuery === '' && csFilterStatus === 'all' && csFilterIssueType === 'all'" class="no-messages">
            暂无客服消息
          </view>
          <view v-else-if="group.length === 0" class="no-messages">
            无匹配的客服消息
          </view>
        </view>
      </view>

      <view v-if="hasMore" class="loading-more">
        <text>加载中...</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { onShow, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'; 

// 后端静态资源基础 URL
// **请务必将此处的 URL 替换为您实际的后端服务地址和端口**
// 例如：'http://localhost:8080' 或 'https://your-backend-domain.com'
const backendStaticBaseUrl = ref('http://localhost:8080'); // <-- **请修改为您的实际后端地址**

const messageTabs = ref([
  // 修改 icon 路径，使用 backendStaticBaseUrl 拼接
  { name: '系统通知', unread: 0, icon: computed(() => backendStaticBaseUrl.value + '/messages/system_notification.png') }, 
  { name: '订单消息', unread: 0, icon: computed(() => backendStaticBaseUrl.value + '/messages/order_message.png') },
  { name: '活动消息', unread: 0, icon: computed(() => backendStaticBaseUrl.value + '/messages/activity_message.png') },
  { name: '客服消息', unread: 0, icon: computed(() => backendStaticBaseUrl.value + '/messages/customer_service_message.png') }
]);

const currentTab = ref(0);

const systemMessages = ref([]);
const orderMessages = ref([]);
const promotionMessages = ref([]);
const serviceMessages = ref([]);

// Order message filter and search states
const filterStatus = ref('all');
const searchQuery = ref('');
const debouncedSearchQuery = ref('');

// Activity message filter and search states
const activityFilterStatus = ref('all');
const activitySearchQuery = ref(''); 
const debouncedActivitySearchQuery = ref('');

// Customer Service message filter and search states
const csFilterStatus = ref('all');
const csFilterIssueType = ref('all');
const csSearchQuery = ref('');
const debouncedCsSearchQuery = ref('');


let searchTimeout = null;
watch(searchQuery, (newQuery) => {
  if (searchTimeout) {
    clearTimeout(searchTimeout);
  }
  searchTimeout = setTimeout(() => {
    debouncedSearchQuery.value = newQuery;
  }, 300); // Debounce delay: 300ms
});

let activitySearchTimeout = null;
watch(activitySearchQuery, (newQuery) => {
  if (activitySearchTimeout) {
    clearTimeout(activitySearchTimeout);
  }
  activitySearchTimeout = setTimeout(() => {
    debouncedActivitySearchQuery.value = newQuery;
  }, 300); // Debounce delay: 300ms
});

let csSearchTimeout = null;
watch(csSearchQuery, (newQuery) => {
  if (csSearchTimeout) {
    clearTimeout(csSearchTimeout);
  }
  csSearchTimeout = setTimeout(() => {
    debouncedCsSearchQuery.value = newQuery;
  }, 300); // Debounce delay: 300ms
});


const orderFilterOptions = ref([
  { label: '全部', value: 'all' },
  { label: '待处理', value: 'pending' },
  { label: '待付款', value: '待付款' },
  { label: '待发货', value: '待发货' },
  { label: '运输中', value: '运输中' },
  { label: '待验收', value: '待验收' },
  { label: '已完成', value: '已完成' },
  { label: '已取消', value: '已取消' },
  { label: '预警', value: '预警' }
]);

const activityFilterOptions = ref([
  { label: '全部', value: 'all' },
  { label: '促销', value: '促销' },
  { label: '新品', value: '新品' },
  { label: '补贴', value: '补贴' },
  { label: '预告', value: '预告' },
  { label: '行情', value: '行情' },
  { label: '公告', value: '公告' }
]);

const csFilterStatusOptions = ref([
  { label: '全部', value: 'all' },
  { label: '未读', value: 'unread' },
  { label: '待回复', value: '待回复' },
  { label: '处理中', value: '处理中' },
  { label: '已解决', value: '已解决' },
  { label: '已关闭', value: '已关闭' }
]);

const csIssueTypeOptions = ref([
  { label: '全部类型', value: 'all' },
  { label: '订单问题', value: '订单问题' },
  { label: '支付问题', value: '支付问题' },
  { label: '物流问题', value: '物流问题' },
  { label: '发票问题', value: '发票问题' },
  { label: '账户问题', value: '账户问题' },
  { label: '投诉建议', value: '投诉建议' },
  { label: '活动咨询', value: '活动咨询' },
  { label: '质检纠纷', value: '质检纠纷' },
  { label: '冷链异常', value: '冷链异常' },
  { label: '损耗理赔', value: '损耗理赔' }
]);


const fetchSystemMessages = () => {
  uni.showLoading({ title: '加载系统消息...' });
  setTimeout(() => {
    const mockData = [
      {
        id: 1,
        type: 'highRiskOp',
        icon: '🛡️',
        title: '账户安全提醒',
        content: '您在新设备（iPhone）登录，如非本人操作请修改密码',
        deviceInfo: 'iPhone 15 Pro（iOS 17.4）',
        operationType: '登录',
        time: '14:25',
        date: '2025-03-18',
        read: false,
        loginLocation: '北京市',
        loginIp: '123.123.123.123'
      },
      {
        id: 2,
        type: 'platformOperation',
        icon: '⚙️',
        title: '支付系统维护通知',
        content: '3月20日 00:00-06:00 银联通道升级维护',
        maintenanceTime: '3月20日 00:00-06:00',
        impactScope: '银行卡支付、提现到账',
        time: '09:30',
        date: '2025-03-18',
        read: false
      },
      {
        id: 3,
        type: 'ruleUpdate',
        icon: '📄',
        title: '生鲜商品售后规则更新',
        content: '《生鲜类商品争议处理规范》修订生效公告',
        effectiveTime: '2025年4月1日 00:00',
        oldRule: '签收后24小时内可申请腐烂赔付',
        newRule: '需提供签收时完整开箱视频（≤30秒）',
        impactAfterDate: '4月1日后订单需录制开箱视频作为理赔依据',
        impactHistory: '历史订单仍按原规则执行（凭证：物流签收单+照片）',
        time: '15:00',
        date: '2025-03-17',
        read: false
      },
      {
        id: 5,
        type: 'globalAnnouncement',
        icon: '📢',
        title: '台风“木兰”配送延迟公告',
        content: '受台风影响，7月15-18日 广东、福建产区发货及物流时效调整',
        affectedTimeRange: '7月15-18日',
        pausedServices: '汕头、汕尾、厦门产区直发',
        delayedRegions: '珠三角地区预计延后48-72小时',
        emergencyAction: '已发货订单可冻结冷链时效，解冻后继续配送',
        suggestion1: '暂勿下单受灾产区商品，可切换至华中/华北仓库',
        suggestion2: '已下单用户可免费取消订单（无违约金）',
        time: '08:00',
        date: '2025-07-14',
        read: false
      }
    ];
    systemMessages.value = mockData;
    updateUnreadCount();
    uni.hideLoading();
  }, 1000);
};

const fetchOrderMessages = () => {
  uni.showLoading({ title: '加载订单消息...' });
  setTimeout(() => {
    const mockData = [
      {
        id: 101,
        title: '您的订单 20240715123456 已发货！',
        content: '订单 20240715123456 已发货',
        orderId: '20240715123456',
        productName: '山东红富士苹果',
        productSpec: '5吨',
        productImage: 'https://placehold.co/100x100/FF5733/FFFFFF?text=Apple',
        status: '运输中',
        statusClass: 'status-shipping',
        alertIcon: '',
        time: '14:30',
        date: '2025-07-15',
        read: false,
        actionButtonText: '查看物流',
        actionButtonClass: 'action-primary'
      },
      {
        id: 102,
        title: '订单 20240715987654 待支付定金',
        content: '订单 20240715987654 待支付定金',
        orderId: '20240715987654',
        productName: '新疆哈密瓜',
        productSpec: '100箱',
        productImage: 'https://placehold.co/100x100/33FF57/FFFFFF?text=Melon',
        status: '待付款',
        statusClass: 'status-pending-payment',
        alertIcon: '⏰',
        time: '10:00',
        date: '2025-07-15',
        read: false,
        actionButtonText: '去支付',
        actionButtonClass: 'action-primary'
      },
      {
        id: 103,
        title: '订单 20240715543210 已到达，请尽快验收',
        content: '订单 20240715543210 已到达，请尽快验收',
        orderId: '20240715543210',
        productName: '智利车厘子',
        productSpec: '50kg',
        productImage: 'https://placehold.co/100x100/3366FF/FFFFFF?text=Cherry',
        status: '待验收',
        statusClass: 'status-pending-acceptance',
        alertIcon: '',
        time: '09:00',
        date: '2025-07-15',
        read: false,
        actionButtonText: '去验收',
        actionButtonClass: 'action-primary'
      },
      {
        id: 104,
        title: '供应商已拒绝您的订单 20240715000001',
        content: '供应商已拒绝您的订单 20240715000001',
        orderId: '20240715000001',
        productName: '泰国榴莲',
        productSpec: '20个',
        productImage: 'https://placehold.co/100x100/FF33CC/FFFFFF?text=Durian',
        status: '已取消',
        statusClass: 'status-cancelled',
        alertIcon: '',
        time: '昨天 18:00',
        date: '2025-07-14',
        read: true,
        actionButtonText: ''
      },
      {
        id: 105,
        title: '订单 20240715111111 温度异常告警！',
        content: '订单 20240715111111 运输中发生温度异常，请处理',
        orderId: '20240715111111',
        productName: '进口三文鱼',
        productSpec: '100kg',
        productImage: 'https://placehold.co/100x100/FF9933/FFFFFF?text=Salmon',
        status: '预警',
        statusClass: 'status-warning',
        alertIcon: '🌡️',
        time: '昨天 11:00',
        date: '2025-07-14',
        read: false,
        actionButtonText: '处理异议',
        actionButtonClass: 'action-danger'
      },
      {
        id: 106,
        title: '订单 20240715222222 已完成',
        content: '您的订单 20240715222222 已成功完成',
        orderId: '20240715222222',
        productName: '新鲜猪肉',
        productSpec: '500kg',
        productImage: 'https://placehold.co/100x100/99FF33/FFFFFF?text=Pork',
        status: '已完成',
        statusClass: 'status-completed',
        alertIcon: '',
        time: '2025-07-13 16:00',
        date: '2025-07-13',
        read: true,
        actionButtonText: ''
      }
    ].sort((a, b) => new Date(b.date + ' ' + b.time) - new Date(a.date + ' ' + a.time));

    orderMessages.value = mockData;
    updateUnreadCount();
    uni.hideLoading();
  }, 1000);
};

const fetchPromotionMessages = () => {
  uni.showLoading({ title: '加载活动消息...' });
  setTimeout(() => {
    const mockData = [
      {
        id: 'ACT20240715001', // Changed ID to match activityDetail mock data
        title: '【限时特惠】山东寿光黄瓜直供，今日下单每斤立减0.5元！',
        type: '限时折扣',
        typeClass: 'type-discount',
        benefitSummary: '每斤立减0.5元，最高可省500元',
        validity: '距结束 2天 10小时',
        targetAudience: '所有用户',
        image: 'https://placehold.co/400x200/4CAF50/FFFFFF?text=CucumberPromo',
        sender: '平台活动',
        time: '10:00',
        date: '2025-07-15',
        read: false,
        isNew: true
      },
      {
        id: 'ACT20240801002', // Changed ID to match activityDetail mock data
        title: '新供应商入驻！云南有机蓝莓基地直采，首单享95折！',
        type: '新品',
        typeClass: 'type-new',
        benefitSummary: '首单享95折，最高优惠200元',
        validity: '活动时间：2025.07.15 - 2025.07.31',
        targetAudience: '新注册用户',
        image: 'https://placehold.co/400x200/2196F3/FFFFFF?text=BlueberryNew',
        sender: '平台活动',
        time: '09:00',
        date: '2025-07-15',
        read: false,
        isNew: true
      },
      {
        id: 'ACT20240601003', // Changed ID to match activityDetail mock data
        title: '【冷链补贴】本周下单冻品，平台补贴50%运费！',
        type: '运费补贴',
        typeClass: 'type-subsidy',
        benefitSummary: '冷链运费补贴50%，单笔最高补贴100元',
        validity: '本周有效 (2025.07.14 - 2025.07.20)',
        targetAudience: '所有冻品采购商',
        image: 'https://placehold.co/400x200/FF9800/FFFFFF?text=ColdChain',
        sender: '平台活动',
        time: '昨天 14:00',
        date: '2025-07-14',
        read: false,
        isNew: false
      },
      {
        id: 'ACT20240713004', // New ID for consistency
        title: '【采购节预告】8.8生鲜狂欢周，百万优惠券即将开抢！',
        type: '节日大促',
        typeClass: 'type-festival',
        benefitSummary: '领200元、500元、1000元大额优惠券',
        validity: '抢券时间：2025.08.01 00:00',
        targetAudience: '所有用户',
        image: 'https://placehold.co/400x200/E91E63/FFFFFF?text=ShoppingFestival',
        sender: '平台活动',
        time: '2025-07-13 16:00',
        date: '2025-07-13',
        read: true,
        isNew: false
      },
      {
        id: 'ACT20240712005', // New ID for consistency
        title: '【行情快报】南方暴雨影响荔枝供应，价格预计上涨10%',
        type: '行情资讯',
        typeClass: 'type-market',
        benefitSummary: '建议近期谨慎采购，关注后续市场动态',
        validity: '发布时间：2025.07.12',
        targetAudience: '水果采购商',
        image: 'https://placehold.co/400x200/607D8B/FFFFFF?text=LycheeMarket',
        sender: '市场行情',
        time: '2025-07-12 08:00',
        date: '2025-07-12',
        read: true,
        isNew: false
      }
    ].sort((a, b) => new Date(b.date + ' ' + b.time) - new Date(a.date + ' ' + a.time));

    promotionMessages.value = mockData;
    updateUnreadCount();
    uni.hideLoading();
  }, 1000);
};

const fetchServiceMessages = () => {
  uni.showLoading({ title: '加载客服消息...' });
  setTimeout(() => {
    const mockData = [
      {
        id: 'chat_order_123', // Changed ID to match customerServiceChat mock data
        title: '关于订单20240715123456验收问题的咨询',
        lastMessage: '您好，您提交的验收问题已转交专员处理，请耐心等待。',
        time: '16:30',
        date: '2025-07-15',
        unread: 1,
        // 修改 avatar 路径，使用 backendStaticBaseUrl 拼接
        avatar: backendStaticBaseUrl.value + '/messages/cs_online_en.png',
        senderType: '平台客服',
        senderName: '客服001',
        orderId: '20240715123456',
        issueType: '订单问题',
        issueTypeClass: 'issue-order',
        status: '待回复',
        statusClass: 'cs-status-pending',
        priority: 'normal',
        priorityIcon: ''
      },
      {
        id: 'chat_new_issue', // Changed ID to match customerServiceChat mock data
        title: '运费补贴申请进度咨询',
        lastMessage: '您的运费补贴申请已通过审核，预计3个工作日到账。',
        time: '15:45',
        date: '2025-07-15',
        unread: 0,
        // 修改 avatar 路径，使用 backendStaticBaseUrl 拼接
        avatar: backendStaticBaseUrl.value + '/messages/cs_aftersales_en.png',
        senderType: '平台客服',
        senderName: '客服002',
        orderId: '',
        issueType: '支付问题',
        issueTypeClass: 'issue-payment',
        status: '已解决',
        statusClass: 'cs-status-resolved',
        priority: 'normal',
        priorityIcon: ''
      },
      {
        id: 'chat_resolved_issue', // Changed ID to match customerServiceChat mock data
        title: '【重要通知】关于XX产区物流延误的说明',
        lastMessage: '受恶劣天气影响，XX产区物流将有24-48小时延误。',
        time: '11:00',
        date: '2025-07-14',
        unread: 0,
        // 修改 avatar 路径，使用 backendStaticBaseUrl 拼接
        avatar: backendStaticBaseUrl.value + '/messages/system_notification.png', // System icon for system push
        senderType: '系统通知',
        senderName: '',
        orderId: '',
        issueType: '物流问题',
        issueTypeClass: 'issue-logistics',
        status: '已关闭',
        statusClass: 'cs-status-closed',
        priority: 'high',
        priorityIcon: '❗️' // High priority
      },
      {
        id: 304,
        title: '您的纠纷工单（编号：D20240715001）已受理',
        lastMessage: '您提交的质检纠纷工单已受理，专员将在24小时内联系您。',
        time: '昨天 09:00',
        date: '2025-07-14',
        unread: 1,
        // 修改 avatar 路径，使用 backendStaticBaseUrl 拼接
        avatar: backendStaticBaseUrl.value + '/messages/cs_online_en.png',
        senderType: '平台客服',
        senderName: '客服008',
        orderId: '20240715000001',
        issueType: '质检纠纷',
        issueTypeClass: 'issue-quality',
        status: '处理中',
        statusClass: 'cs-status-processing',
        priority: 'normal',
        priorityIcon: ''
      },
      {
        id: 305,
        title: '如何开电子发票？',
        lastMessage: '请在“我的订单”中选择对应订单，点击“申请发票”即可。',
        time: '2025-07-13 10:00',
        date: '2025-07-13',
        unread: 0,
        // 修改 avatar 路径，使用 backendStaticBaseUrl 拼接
        avatar: backendStaticBaseUrl.value + '/messages/cs_online_en.png',
        senderType: '平台客服',
        senderName: '客服005',
        orderId: '',
        issueType: '发票问题',
        issueTypeClass: 'issue-invoice',
        status: '已解决',
        statusClass: 'cs-status-resolved',
        priority: 'normal',
        priorityIcon: ''
      }
    ].sort((a, b) => new Date(b.date + ' ' + b.time) - new Date(a.date + ' ' + a.time));

    serviceMessages.value = mockData;
    updateUnreadCount();
    uni.hideLoading();
  }, 1000);
};

const groupedSystemMessages = computed(() => {
  return groupMessagesByDate(systemMessages.value);
});

const filteredOrderMessages = computed(() => {
  let filtered = orderMessages.value;

  if (filterStatus.value !== 'all') {
    if (filterStatus.value === 'pending') {
      filtered = filtered.filter(msg => 
        msg.status === '待付款' || 
        msg.status === '待验收' || 
        msg.status === '预警'
      );
    } else {
      filtered = filtered.filter(msg => msg.status === filterStatus.value);
    }
  }

  if (debouncedSearchQuery.value) {
    const query = debouncedSearchQuery.value.toLowerCase();
    filtered = filtered.filter(msg =>
      msg.orderId.toLowerCase().includes(query) ||
      msg.productName.toLowerCase().includes(query)
    );
  }

  return filtered;
});

const filteredPromotionMessages = computed(() => {
  let filtered = promotionMessages.value;

  if (activityFilterStatus.value !== 'all') {
    filtered = filtered.filter(msg => msg.type === activityFilterStatus.value);
  }

  if (debouncedActivitySearchQuery.value) {
    const query = debouncedActivitySearchQuery.value.toLowerCase();
    filtered = filtered.filter(msg =>
      msg.title.toLowerCase().includes(query) ||
      msg.benefitSummary.toLowerCase().includes(query) ||
      msg.sender.toLowerCase().includes(query) ||
      msg.type.toLowerCase().includes(query)
    );
  }

  return filtered;
});

const filteredServiceMessages = computed(() => {
  let filtered = serviceMessages.value;

  // 1. Filter by status
  if (csFilterStatus.value !== 'all') {
    if (csFilterStatus.value === 'unread') {
      filtered = filtered.filter(msg => msg.unread > 0);
    } else {
      filtered = filtered.filter(msg => msg.status === csFilterStatus.value);
    }
  }

  // 2. Filter by issue type
  if (csFilterIssueType.value !== 'all') {
    filtered = filtered.filter(msg => msg.issueType === csFilterIssueType.value);
  }

  // 3. Filter by search keyword
  if (debouncedCsSearchQuery.value) {
    const query = debouncedCsSearchQuery.value.toLowerCase();
    filtered = filtered.filter(msg =>
      msg.title.toLowerCase().includes(query) ||
      (msg.orderId && msg.orderId.toLowerCase().includes(query)) ||
      msg.lastMessage.toLowerCase().includes(query)
    );
  }

  return filtered;
});


const groupedOrderMessages = computed(() => {
  const sortedMessages = [...filteredOrderMessages.value].sort((a, b) => {
    const dateA = new Date(a.date + ' ' + a.time.replace('今天 ', '').replace('昨天 ', ''));
    const dateB = new Date(b.date + ' ' + b.time.replace('今天 ', '').replace('昨天 ', ''));
    return dateB - dateA;
  });
  return groupMessagesByDate(sortedMessages);
});

const groupedPromotionMessages = computed(() => {
  const sortedMessages = [...filteredPromotionMessages.value].sort((a, b) => {
    const dateA = new Date(a.date + ' ' + a.time.replace('今天 ', '').replace('昨天 ', ''));
    const dateB = new Date(b.date + ' ' + b.time.replace('今天 ', '').replace('昨天 ', ''));
    return dateB - dateA;
  });
  return groupMessagesByDate(sortedMessages);
});

const groupedServiceMessages = computed(() => {
  const sortedMessages = [...filteredServiceMessages.value].sort((a, b) => {
    const dateA = new Date(a.date + ' ' + a.time.replace('今天 ', '').replace('昨天 ', ''));
    const dateB = new Date(b.date + ' ' + b.time.replace('今天 ', '').replace('昨天 ', ''));
    return dateB - dateA;
  });
  return groupMessagesByDate(sortedMessages);
});


const groupMessagesByDate = (messages) => {
  const groups = {};
  messages.forEach(message => {
    const displayDate = formatDisplayDate(message.date);
    if (!groups[displayDate]) {
      groups[displayDate] = [];
    }
    groups[displayDate].push(message);
  });
  return groups;
};

const formatDisplayDate = (dateString) => {
  const today = new Date();
  const yesterday = new Date(today);
  yesterday.setDate(today.getDate() - 1);

  const messageDate = new Date(dateString);

  if (messageDate.toDateString() === today.toDateString()) {
    return '今天';
  } else if (messageDate.toDateString() === yesterday.toDateString()) {
    return '昨天';
  } else {
    return dateString;
  }
};

const hasMore = ref(false);
const loading = ref(false);

// Swipe-to-delete logic
const startX = ref(0);
const lastTransformX = ref(0); // Store the last transformX for continuity
const swipeStates = ref({}); // { messageId: transformX }
const DELETE_BUTTON_WIDTH = 160; // rpx, adjust based on your actual button width

const onTouchStart = (e, messageId) => {
  // Close any other open swipe cards
  for (const id in swipeStates.value) {
    if (id !== messageId && swipeStates.value[id] !== 0) {
      swipeStates.value[id] = 0;
    }
  }

  startX.value = e.touches[0].clientX;
  lastTransformX.value = swipeStates.value[messageId] || 0; // Get current position
};

const onTouchMove = (e, messageId) => {
  const currentX = e.touches[0].clientX;
  let deltaX = currentX - startX.value;

  // If the card was already open, allow closing it smoothly
  if (lastTransformX.value < 0) { // Card is open (transformed left)
    deltaX += lastTransformX.value; // Adjust delta to current open position
  }

  // Clamp deltaX to prevent swiping too far
  // Allow pulling slightly beyond the delete button width for a "pull" effect, then snap back
  const maxSwipe = -DELETE_BUTTON_WIDTH;
  const minSwipe = -DELETE_BUTTON_WIDTH - 20; // Allow slight pull beyond

  let newTransformX = Math.max(minSwipe, Math.min(0, deltaX)); // Clamp between max negative and 0

  swipeStates.value = {
    ...swipeStates.value,
    [messageId]: newTransformX
  };
};

const onTouchEnd = (e, messageId) => {
  const currentTransformX = swipeStates.value[messageId] || 0;
  const threshold = -DELETE_BUTTON_WIDTH / 2; // Half the button width as threshold

  if (currentTransformX < threshold) {
    // Open delete button
    swipeStates.value[messageId] = -DELETE_BUTTON_WIDTH;
  } else {
    // Close delete button
    swipeStates.value[messageId] = 0;
  }
};

const switchTab = (index) => {
  currentTab.value = index;
  // Reset swipe states when switching tabs
  swipeStates.value = {};
  // Reset search and filter states for all tabs
  searchQuery.value = '';
  debouncedSearchQuery.value = '';
  filterStatus.value = 'all';

  activitySearchQuery.value = '';
  debouncedActivitySearchQuery.value = '';
  activityFilterStatus.value = 'all';

  csSearchQuery.value = '';
  debouncedCsSearchQuery.value = '';
  csFilterStatus.value = 'all';
  csFilterIssueType.value = 'all';

  if (index === 0) {
    fetchSystemMessages();
  } else if (index === 1) {
    fetchOrderMessages();
  } else if (index === 2) {
    fetchPromotionMessages();
  } else if (index === 3) {
    fetchServiceMessages();
  }
};

const markAllAsRead = () => {
  if (currentTab.value === 0) {
    systemMessages.value.forEach(msg => msg.read = true);
  } else if (currentTab.value === 1) {
    filteredOrderMessages.value.forEach(msg => msg.read = true);
  } else if (currentTab.value === 2) {
    filteredPromotionMessages.value.forEach(msg => msg.read = true);
  } else if (currentTab.value === 3) {
    // For customer service, mark all currently filtered messages as read
    filteredServiceMessages.value.forEach(msg => msg.unread = 0);
  }
  updateUnreadCount();
  uni.showToast({
    title: '已标记当前分类所有消息为已读',
    icon: 'none'
  });
};

const handleMessageClick = (message, category) => {
  // If a card is swiped open, clicking it should close the swipe, not navigate
  if ((swipeStates.value[message.id] || 0) !== 0) {
    swipeStates.value[message.id] = 0; // Close swipe
    return;
  }

  let targetPage = '';
  let queryParams = {};
  let baseUrl = '';

  if (category === 'order') {
    // Navigate to the new OrderDetail page
    targetPage = 'orderDetail/orderDetail'; // Corrected path
    queryParams = { orderId: message.orderId }; // Pass orderId to the detail page
    baseUrl = '/pages/messages/'; 
    if (message.read !== undefined) { message.read = true; }
  } else if (category === 'activity') {
    // Navigate to the new ActivityDetail page
    targetPage = 'activityDetail/activityDetail'; // Corrected path based on new directory structure
    queryParams = { activityId: message.id }; // Pass activityId to the detail page
    baseUrl = '/pages/messages/'; 
    if (message.read !== undefined) { message.read = true; }
    // No toast here, as navigation will occur
  } else if (category === 'customerService') {
    // Navigate to the new CustomerServiceChat page
    targetPage = 'costomerServiceChat/costomerServiceChat'; // Updated path based on new directory structure
    queryParams = { chatId: message.id }; // Pass the chat ID
    baseUrl = '/pages/messages/'; 
    if (message.unread !== undefined) { message.unread = 0; } // Mark as read when entering chat
  } else if (category === 'system') {
    if (message.type === 'highRiskOp' || message.type === 'riskWarning') {
      targetPage = 'accountSecurity';
      queryParams = { messageData: encodeURIComponent(JSON.stringify(message)) };
      baseUrl = '/pages/messages/systemNotification/';
    } else if (message.type === 'platformOperation') {
      targetPage = 'platformOperation'; // Changed from maintenanceNotice to platformOperation
      queryParams = { noticeData: encodeURIComponent(JSON.stringify(message)) };
      baseUrl = '/pages/messages/systemNotification/';
    } else if (message.type === 'ruleUpdate') {
      targetPage = 'ruleUpdate';
      queryParams = { messageData: encodeURIComponent(JSON.stringify(message)) };
      baseUrl = '/pages/messages/systemNotification/';
    } else if (message.type === 'globalAnnouncement') {
      targetPage = 'globalAnnouncement';
      queryParams = { messageData: encodeURIComponent(JSON.stringify(message)) };
      baseUrl = '/pages/messages/systemNotification/';
    } else {
      console.warn('Non-specific system message, no corresponding detail page navigation logic.');
      uni.showToast({
        title: '非特定系统消息',
        icon: 'none'
      });
      return;
    }
    if (message.read !== undefined) { message.read = true; }
  }

  updateUnreadCount();

  if (targetPage && baseUrl) {
    const queryString = Object.keys(queryParams)
      .map(key => `${key}=${queryParams[key]}`)
      .join('&');
    
    uni.navigateTo({
      url: `${baseUrl}${targetPage}${queryString ? '?' + queryString : ''}`
    });
  } else {
    console.warn('Unknown message category or undefined navigation logic, cannot navigate:', category);
    uni.showToast({
      title: '未知消息类型',
      icon: 'none'
    });
  }
};

const handleOrderAction = (message) => {
  uni.showToast({
    title: `执行订单操作: ${message.actionButtonText} (订单号: ${message.orderId})`,
    icon: 'none'
  });
  if (message.status === '待付款') {
  } else if (message.status === '待验收') {
  } else if (message.status === '运输中') {
  } else if (message.status === '预警') {
  }
};

const filterByStatus = (status) => {
  filterStatus.value = status;
};

const performSearch = () => {
  if (searchQuery.value) {
    uni.showToast({
      title: `开始搜索: ${searchQuery.value}`,
      icon: 'none'
    });
  }
};

const clearSearch = () => {
  searchQuery.value = '';
};

const deleteOrderMessage = (id) => {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这条订单消息吗？',
    success: (res) => {
      if (res.confirm) {
        orderMessages.value = orderMessages.value.filter(msg => msg.id !== id);
        updateUnreadCount();
        uni.showToast({
          title: '删除成功',
          icon: 'success'
        });
        swipeStates.value[id] = 0;
      } else {
        swipeStates.value[id] = 0;
      }
    }
  });
};

// Activity Message specific functions
const filterByActivityStatus = (type) => {
  activityFilterStatus.value = type;
};

const performActivitySearch = () => {
  if (activitySearchQuery.value) {
    uni.showToast({
      title: `开始搜索活动: ${activitySearchQuery.value}`,
      icon: 'none'
    });
  }
};

const clearActivitySearch = () => {
  activitySearchQuery.value = '';
};

const deletePromotionMessage = (id) => {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这条活动消息吗？',
    success: (res) => {
      if (res.confirm) {
        promotionMessages.value = promotionMessages.value.filter(msg => msg.id !== id);
        updateUnreadCount();
        uni.showToast({
          title: '删除成功',
          icon: 'success'
        });
        swipeStates.value[id] = 0;
      } else {
        swipeStates.value[id] = 0;
      }
    }
  });
};

// Customer Service Message specific functions
const startNewCustomerServiceChat = () => {
  uni.showToast({
    title: '跳转到发起新客服咨询页面',
    icon: 'none'
  });
  // In a real application: uni.navigateTo({ url: '/pages/customerService/newChat' });
};

const filterByCsStatus = (status) => {
  csFilterStatus.value = status;
};

const filterByCsIssueType = (type) => {
  csFilterIssueType.value = type;
};

const performCsSearch = () => {
  if (csSearchQuery.value) {
    uni.showToast({
      title: `开始搜索客服消息: ${csSearchQuery.value}`,
      icon: 'none'
    });
  }
};

const clearCsSearch = () => {
  csSearchQuery.value = '';
};

const deleteServiceMessage = (id) => {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这条客服消息吗？',
    success: (res) => {
      if (res.confirm) {
        serviceMessages.value = serviceMessages.value.filter(msg => msg.id !== id);
        updateUnreadCount();
        uni.showToast({
          title: '删除成功',
          icon: 'success'
        });
        swipeStates.value[id] = 0;
      } else {
        swipeStates.value[id] = 0;
      }
    }
  });
};


const updateUnreadCount = () => {
  messageTabs.value[0].unread = systemMessages.value.filter(msg => !msg.read).length;
  messageTabs.value[1].unread = orderMessages.value.filter(msg => !msg.read).length; 
  messageTabs.value[2].unread = promotionMessages.value.filter(msg => !msg.read).length;
  // Sum up unread counts for customer service messages
  messageTabs.value[3].unread = serviceMessages.value.reduce((total, service) => total + (service.unread || 0), 0);
};

const onImageError = (type, item) => {
  console.error(`Failed to load ${type} type image, item:`, item);
  if (type === 'product') {
    item.productImage = 'https://placehold.co/100x100/FF0000/FFFFFF?text=Error';
  } else if (type === 'promotion') {
    item.image = 'https://placehold.co/400x200/FF0000/FFFFFF?text=ImageError';
  } else if (type === 'service') {
    // 这里的 fallback 也应该使用 backendStaticBaseUrl 或一个完整的外部 URL
    item.avatar = backendStaticBaseUrl.value + '/messages/cs_error_fallback.png'; 
  }
};

const loadMore = () => {
  if (loading.value || !hasMore.value) return;
  loading.value = true;
  uni.showLoading({ title: '加载中...' });
  setTimeout(() => {
    loading.value = false;
    uni.hideLoading();
  }, 1000);
};

const onRefresh = () => {
  uni.showLoading({ title: '刷新中...' });
  filterStatus.value = 'all';
  searchQuery.value = '';
  activityFilterStatus.value = 'all';
  activitySearchQuery.value = '';
  csFilterStatus.value = 'all';
  csFilterIssueType.value = 'all';
  csSearchQuery.value = '';
  swipeStates.value = {};
  fetchSystemMessages();
  fetchOrderMessages();
  fetchPromotionMessages();
  fetchServiceMessages();
  setTimeout(() => {
    uni.hideLoading();
    uni.stopPullDownRefresh();
  }, 1000);
};

onShow(() => {
  uni.$emit('tabPageShow'); 
  fetchSystemMessages();
  fetchOrderMessages();
  fetchPromotionMessages();
  fetchServiceMessages();
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

/* Message Category Tabs */
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
  flex-direction: column; /* 图标在上文字在下 */
  align-items: center;
  justify-content: center;
  flex: 1;
  min-width: 0;
  text-align: center;
  padding: 0 10rpx;
  transition: background-color 0.3s ease, color 0.3s ease, border-left 0.3s ease;
  border-left: 10rpx solid transparent; /* 默认透明边框 */
  padding-left: 0; /* 垂直布局不需要左填充 */
}

/* Wrapper for tab icon to allow color change */
.tab-icon-wrapper {
  width: 56rpx;
  height: 56rpx;
  margin-bottom: 6rpx; /* 图标和文字之间的间距 */
  margin-right: 0; /* 垂直布局不需要右边距 */
  display: flex;
  align-items: center;
  justify-content: center;
  /* 默认灰色滤镜 */
  filter: grayscale(100%); 
  transition: filter 0.3s ease;
}

.tab-icon {
  width: 100%;
  height: 100%;
  display: block;
}

/* Active Tab Styles */
.tab-active {
  background-color: transparent; /* 移除背景色 */
  border-left: 10rpx solid transparent; /* 移除左侧边框 */
  color: #4CAF50; /* 绿色文字 */
  font-weight: 600;
  padding-left: 0; /* 垂直布局不需要左填充 */
}

.tab-active .tab-icon-wrapper {
  filter: grayscale(0%); /* 移除灰色滤镜，显示原色图标 */
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

/* Message List - Scroll Area */
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

/* Swipe Wrapper for Order Messages */
.swipe-wrapper {
  position: relative;
  width: 100%;
  margin-bottom: 24rpx; /* Same margin as message-card */
  border-radius: 16rpx;
  overflow: hidden; /* 确保溢出内容被隐藏 */
  transform: translateZ(0); /* 强制硬件加速和新的层叠上下文，解决 clipping bug */
}

/* Unified Card Style */
.message-card {
  background-color: #ffffff;
  /* border-radius: 16rpx; */ /* 圆角由父级 .swipe-wrapper 控制 */
  box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.08);
  padding: 30rpx;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  position: relative;
  /* overflow: hidden; */ /* 移除这里的 overflow: hidden，由父级控制 */
  border-left: none; 
  padding-left: 30rpx; /* Adjust padding as border is removed */
  transition: transform 0.3s ease; 
  z-index: 1; /* Ensure card is above delete button */
  /* margin-bottom: 24rpx; */ /* 移除这里的 margin-bottom，由 .swipe-wrapper 控制 */
}

/* System Notification Card Specific Background and Border */
.system-card {
  background-color: #ffffff; /* Changed to white background */
  border-left: 10rpx solid; /* Only specify border width and style */
  padding-left: 20rpx; /* Re-add padding for system cards that have border */
}
.high-risk-card {
  border-color: #ff4d4f; /* Red border */
}
.maintenance-card {
  border-color: #ffa500; /* Orange border */
}
.rule-card {
  border-color: #1890ff; /* Blue border */
}
.risk-card { /* Risk warning card, same style as high risk */
  border-color: #ff4d4f; /* Red border */
}
.announcement-card {
  border-color: #ffc107; /* Amber border */
}

/* Order Card Specific Styles */
.order-card {
  border-left: 10rpx solid #4CAF50; /* Added green border */
  padding-left: 20rpx; /* Adjusted padding for border */
}

/* General Card Content Style */
.card-content {
  flex: 1;
  min-width: 0;
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
  width: 100%;
}

/* System Notification Card Icon */
.card-icon {
  font-size: 36rpx;
  margin-right: 12rpx;
  line-height: 1;
  flex-shrink: 0;
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
  flex-grow: 1;
  min-width: 0;
}

.card-time {
  font-size: 26rpx;
  color: #999999;
  flex-shrink: 0;
  margin-left: 10rpx;
}

/* Order Message Unique Alert Icon */
.alert-icon {
  font-size: 36rpx;
  margin-left: 10rpx;
  color: #ff4d4f;
  flex-shrink: 0;
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

/* System Notification Card Detail Area */
.system-details {
  font-size: 24rpx;
  color: #555555;
  background-color: #f9f9f9;
  border-radius: 8rpx;
  padding: 12rpx 16rpx;
  display: flex;
  flex-direction: column;
  border-top: 1rpx dashed #eeeeee;
  margin-top: 10rpx;
  width: 100%;
  box-sizing: border-box;
}

/* Unread Dot Style (for general unread) */
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

/* Order Messages - Product Thumbnail Info */
.order-product-info {
  display: flex;
  align-items: center;
  margin-top: 10rpx;
  margin-bottom: 20rpx;
  background-color: #f8f8f8;
  padding: 20rpx;
  border-radius: 12rpx;
  width: 100%;
  box-sizing: border-box;
}

.product-thumbnail {
  width: 120rpx;
  height: 120rpx;
  border-radius: 8rpx;
  margin-right: 20rpx;
  flex-shrink: 0;
  background-color: #e0e0e0;
}

.product-details {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  min-width: 0;
}

.product-name {
  font-size: 30rpx;
  color: #333;
  font-weight: 500;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-spec {
  font-size: 26rpx;
  color: #666;
  margin-top: 8rpx;
  line-height: 1.4;
}


/* Info Area (Order, Activity) */
.card-info {
  margin-top: 16rpx;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  width: 100%;
}

/* Status Tag Style */
.status-tag {
  font-size: 24rpx;
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
  font-weight: 500;
  margin-right: 16rpx;
  white-space: nowrap;
}

/* Order Status Colors */
.status-pending-payment {
  color: #dc3545;
  background-color: rgba(220, 53, 69, 0.1);
}
.status-shipping {
  color: #4CAF50; /* Changed to green */
  background-color: rgba(76, 175, 80, 0.1); /* Changed to green */
}
.status-pending-acceptance {
  color: #ffc107;
  background-color: rgba(255, 193, 7, 0.1);
}
.status-completed {
  color: #4CAF50; /* Changed to green */
  background-color: rgba(76, 175, 80, 0.1); /* Changed to green */
}
.status-cancelled {
  color: #6c757d;
  background-color: rgba(108, 117, 125, 0.1);
}
.status-warning {
  color: #dc3545;
  background-color: rgba(220, 53, 69, 0.1);
  font-weight: bold;
}

/* Action Button Styles */
.action-button {
  background-color: #4CAF50; /* Green */
  color: white;
  padding: 16rpx 32rpx;
  border-radius: 12rpx;
  font-size: 28rpx;
  font-weight: 500;
  margin-top: 20rpx;
  align-self: flex-end; /* Align to the right within the card */
  transition: background-color 0.2s ease;
  box-sizing: border-box;
  box-shadow: 0 4rpx 8rpx rgba(0, 0, 0, 0.1); /* Added shadow */
}

.action-button:active {
  background-color: #388E3C; /* Darker green on active */
}

/* Promotion Card Specific Styles */
.promotion-card {
  border-left: 10rpx solid #4CAF50; /* Added green border */
  padding-left: 20rpx; /* Adjusted padding for border */
}
.promotion-image {
  width: 100%;
  height: 240rpx; /* Larger image for banner effect */
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  background-color: #e0e0e0;
}
.promotion-meta {
  display: flex;
  align-items: center;
  margin-bottom: 10rpx;
}
.promotion-type-tag {
  font-size: 24rpx;
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
  font-weight: 500;
  margin-right: 16rpx;
  white-space: nowrap;
}
/* Activity Type Colors - Adjusted to harmonize */
.type-discount {
  color: #E91E63;
  background-color: rgba(233, 30, 99, 0.1);
}
.type-new {
  color: #00BCD4;
  background-color: rgba(0, 188, 212, 0.1);
}
.type-subsidy {
  color: #FF9800; /* Orange, complements green */
  background-color: rgba(255, 152, 0, 0.1);
}
.type-festival {
  color: #FF5722;
  background-color: rgba(255, 87, 34, 0.1);
}
.type-market {
  color: #607D8B;
  background-color: rgba(96, 125, 139, 0.1);
}
.type-announcement {
  color: #4CAF50;
  background-color: rgba(76, 175, 80, 0.1);
}

.promotion-sender {
  font-size: 26rpx;
  color: #999;
}
.promotion-benefit {
  font-size: 30rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 10rpx;
}
.promotion-validity {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 6rpx;
}
.promotion-target {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 6rpx; /* Added margin-bottom for consistency */
}
.new-badge {
  background-color: #FF4D4F;
  color: white;
  font-size: 22rpx;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
  margin-left: 16rpx;
  flex-shrink: 0;
}
.unread-dot-small {
  width: 14rpx;
  height: 14rpx;
  background-color: #ff4d4f;
  border-radius: 50%;
  margin-left: 16rpx;
  flex-shrink: 0;
}


/* Customer Service Message Card Specific Style */
.service-card {
  border-left: 10rpx solid #4CAF50; /* Added green border */
  padding-left: 20rpx; /* Adjusted padding for border */
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
  flex-grow: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.priority-icon {
  font-size: 36rpx;
  margin-left: 10rpx;
  color: #FF4D4F;
  flex-shrink: 0;
}

.service-meta {
  display: flex;
  align-items: center;
  margin-bottom: 10rpx;
  width: 100%;
  flex-wrap: wrap;
}

.sender-info {
  font-size: 26rpx;
  color: #666;
  margin-right: 16rpx;
  white-space: nowrap;
}

.associated-order {
  font-size: 26rpx;
  color: #999;
  white-space: nowrap;
}

.service-tags {
  display: flex;
  flex-wrap: wrap;
  margin-top: 10rpx;
}

/* Issue Type Colors - Adjusted to harmonize */
.issue-order {
  color: #4CAF50; /* Green */
  background-color: rgba(76, 175, 80, 0.1);
}
.issue-payment {
  color: #FF9800; /* Orange */
  background-color: rgba(255, 152, 0, 0.1);
}
.issue-logistics {
  color: #2196F3; /* Blue */
  background-color: rgba(33, 150, 243, 0.1);
}
.issue-invoice {
  color: #9C27B0; /* Purple */
  background-color: rgba(156, 39, 176, 0.1);
}
.issue-account {
  color: #F44336; /* Red */
  background-color: rgba(244, 67, 54, 0.1);
}
.issue-complaint {
  color: #E91E63; /* Pink */
  background-color: rgba(233, 30, 99, 0.1);
}
.issue-activity {
  color: #00BCD4; /* Cyan */
  background-color: rgba(0, 188, 212, 0.1);
}
.issue-quality {
  color: #FFC107; /* Amber */
  background-color: rgba(255, 193, 7, 0.1);
}
.issue-coldchain {
  color: #607D8B; /* Blue Grey */
  background-color: rgba(96, 125, 139, 0.1);
}
.issue-loss {
  color: #795548; /* Brown */
  background-color: rgba(121, 85, 72, 0.1);
}


.cs-status-tag {
  font-size: 24rpx;
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
  font-weight: 500;
  white-space: nowrap;
}

/* CS Status Colors - Adjusted to harmonize */
.cs-status-pending {
  color: #FFC107; /* Amber */
  background-color: rgba(255, 193, 7, 0.1);
}
.cs-status-processing {
  color: #2196F3; /* Blue */
  background-color: rgba(33, 150, 243, 0.1);
}
.cs-status-resolved {
  color: #4CAF50; /* Green */
  background-color: rgba(76, 175, 80, 0.1);
}
.cs-status-closed {
  color: #6c757d;
  background-color: rgba(108, 117, 125, 0.1);
}

.unread-badge-small {
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
  position: absolute;
  right: 30rpx;
  top: 30rpx;
  z-index: 2;
}

/* Start New Chat Button */
.start-new-chat-container {
  padding: 20rpx;
  background-color: #f0f2f5;
  text-align: center;
}
.start-new-chat-button {
  width: 90%;
  background-color: #4CAF50; /* Changed to green */
  color: white;
  padding: 24rpx 0;
  border-radius: 16rpx;
  font-size: 32rpx;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  transition: background-color 0.2s ease;
  box-shadow: 0 4rpx 8rpx rgba(0, 0, 0, 0.1); /* Added shadow */
}
.start-new-chat-button:active {
  background-color: #388E3C; /* Darker green on active */
}
.new-chat-icon {
  font-size: 40rpx;
  margin-right: 10rpx;
  line-height: 1;
}

/* Delete Button Container (for swipe) */
.delete-button-container {
  position: absolute;
  top: 0;
  /* 初始位置完全隐藏在右侧 */
  right: -160rpx; /* 80px 转换为 rpx */
  height: 100%;
  width: 160rpx; /* 80px 转换为 rpx */
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #ff4d4f; /* Red background for delete */
  border-radius: 0 16rpx 16rpx 0; /* 匹配卡片右侧圆角 */
  z-index: 0; /* 确保在卡片下方 */
  transition: right 0.3s ease; /* 添加过渡效果 */
}

.delete-button {
  color: white;
  font-size: 28rpx;
  font-weight: bold;
  text-align: center;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}


/* Filter and Search Bar Style */
.order-filter-search-bar {
  background-color: #ffffff;
  padding: 20rpx;
  margin-bottom: 20rpx;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.search-input-container {
  position: relative;
  width: 100%;
}

.search-input {
  width: 100%;
  height: 70rpx;
  background-color: #f0f2f5;
  border-radius: 35rpx;
  padding: 0 30rpx;
  font-size: 28rpx;
  color: #333;
  box-sizing: border-box;
}

.clear-search-btn {
  position: absolute;
  right: 20rpx;
  top: 50%;
  transform: translateY(-50%);
  font-size: 32rpx;
  color: #999;
  padding: 10rpx;
  line-height: 1;
}

.current-search-query {
  font-size: 26rpx;
  color: #666;
  padding-left: 10rpx;
}

.current-search-query .highlight {
  color: #4CAF50; /* Changed to green */
  font-weight: bold;
}

.filter-tabs-scroll {
  width: 100%;
  white-space: nowrap;
}

.filter-tabs-container {
  display: flex;
  padding-bottom: 10rpx;
}

.filter-tab-item {
  display: inline-flex;
  padding: 12rpx 28rpx;
  margin-right: 16rpx;
  background-color: #f0f2f5;
  border-radius: 30rpx;
  font-size: 26rpx;
  color: #666;
  flex-shrink: 0;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.filter-tab-item:last-child {
  margin-right: 0;
}

/* 统一筛选标签活跃颜色为图片中的蓝色/紫色 */
.filter-tab-active {
  background-color: #4CAF50; /* Green accent color */
  color: #ffffff;
  font-weight: 500;
}

.filter-tab-active:active {
  background-color: #388E3C; /* Darker green on active */
}

.no-messages {
  text-align: center;
  padding: 40rpx;
  color: #999;
  font-size: 28rpx;
}

/* Loading More */
.loading-more {
  padding: 30rpx 0;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 28rpx;
  color: #999999;
}
</style>
