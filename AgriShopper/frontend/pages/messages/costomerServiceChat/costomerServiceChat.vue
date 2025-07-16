<template>
  <view class="chat-detail-container">
    <!-- 会话头部信息 -->
    <view class="chat-header">
      <image class="cs-avatar" :src="currentChat.csInfo.avatar" mode="aspectFill" @error="onImageError('csAvatar', currentChat.csInfo)"></image>
      <view class="cs-info">
        <text class="cs-name">{{ currentChat.csInfo.name }}</text>
        <text :class="['cs-status', currentChat.csInfo.statusClass]">
          {{ currentChat.csInfo.status }}
          <text v-if="currentChat.csInfo.typing" class="typing-indicator">...</text>
        </text>
      </view>
      <view class="header-actions">
        <view v-if="currentChat.relatedOrder.orderId" class="related-info" @tap="goToOrderDetail(currentChat.relatedOrder.orderId)">
          <text class="related-text">关联订单: {{ currentChat.relatedOrder.orderId }}</text>
          <text class="arrow-icon">></text>
        </view>
        <view v-if="currentChat.issueDetails.ticketId" class="related-info" @tap="viewTicketDetails(currentChat.issueDetails.ticketId)">
          <text class="related-text">工单号: {{ currentChat.issueDetails.ticketId }}</text>
          <text class="arrow-icon">></text>
        </view>
        <button v-if="currentChat.showEvaluateButton" class="evaluate-button" @tap="evaluateService">服务评价</button>
      </view>
    </view>

    <!-- 消息对话区域 -->
    <scroll-view class="message-area" scroll-y :scroll-into-view="scrollViewTo" scroll-with-animation>
      <view v-for="(message, index) in currentChat.chatHistory" :key="message.id" :id="'msg-' + message.id" class="message-item-wrapper">
        <view class="message-time-divider" v-if="shouldShowTime(message.timestamp, index)">{{ formatMessageTime(message.timestamp) }}</view>
        <view :class="['message-item', message.senderType === 'user' ? 'user-message' : 'cs-message']">
          <image v-if="message.senderType !== 'user'" class="message-avatar" :src="message.senderAvatar" mode="aspectFill" @error="onImageError('messageAvatar', message)"></image>
          <view class="message-bubble">
            <!-- 文本消息 -->
            <text v-if="message.type === 'text'" class="message-content-text">{{ message.content }}</text>

            <!-- 图片消息 -->
            <view v-else-if="message.type === 'image'" class="message-content-image" @tap="previewImage([message.content])">
              <image :src="message.content" mode="widthFix" class="chat-image" @error="onImageError('chatImage', message)"></image>
            </view>

            <!-- 视频消息 -->
            <view v-else-if="message.type === 'video'" class="message-content-video" @tap="playVideo(message.content)">
              <image src="/static/messages/video_play_icon.png" class="video-play-icon"></image>
              <video :src="message.content" :poster="message.poster" controls class="chat-video"></video>
            </view>

            <!-- 文件消息 -->
            <view v-else-if="message.type === 'file'" class="message-content-file" @tap="downloadFile(message.content, message.fileName)">
              <text class="file-icon">📁</text>
              <view class="file-info">
                <text class="file-name">{{ message.fileName }}</text>
                <text class="file-size">{{ message.fileSize }}</text>
              </view>
              <text class="download-icon">⬇️</text>
            </view>

            <!-- 订单/商品卡片 -->
            <view v-else-if="message.type === 'card'" class="message-content-card" @tap="handleCardClick(message.cardType, message.cardData)">
              <view class="card-header-mini">
                <text class="card-type-label">{{ message.cardType === 'order' ? '订单信息' : '商品信息' }}</text>
              </view>
              <image class="card-thumbnail" :src="message.cardData.image" mode="aspectFill" @error="onImageError('cardThumbnail', message.cardData)"></image>
              <view class="card-details">
                <text class="card-title-mini">{{ message.cardData.title }}</text>
                <text class="card-price" v-if="message.cardData.price">￥{{ message.cardData.price }}</text>
                <text class="card-status" v-if="message.cardData.status">{{ message.cardData.status }}</text>
              </view>
              <button class="card-action-button" @tap.stop="handleCardClick(message.cardType, message.cardData)">{{ message.cardActionText }}</button>
            </view>

            <!-- 快捷操作按钮/卡片 -->
            <view v-if="message.actions && message.actions.length > 0" class="message-actions-container">
              <button v-for="(action, idx) in message.actions" :key="idx" class="message-action-button" @tap="handleEmbeddedAction(action.type, action.data)">
                {{ action.text }}
              </button>
            </view>
          </view>
          <image v-if="message.senderType === 'user'" class="message-avatar" :src="message.senderAvatar" mode="aspectFill" @error="onImageError('messageAvatar', message)"></image>
        </view>
        <view v-if="message.senderType === 'user'" class="message-status">
          <text v-if="message.status === 'sent'">已发送</text>
          <text v-else-if="message.status === 'delivered'">已送达</text>
          <text v-else-if="message.status === 'read'">已读</text>
        </view>
      </view>
      <view id="bottom-anchor"></view>
    </scroll-view>

    <!-- 问题处理进展/工单信息 -->
    <view v-if="currentChat.issueDetails.ticketId" class="issue-progress-card card">
      <view class="card-title-section">
        <text class="card-title">问题处理进展</text>
        <text class="ticket-id">工单号：{{ currentChat.issueDetails.ticketId }}</text>
      </view>
      <view class="progress-info">
        <text class="label">工单类型：</text>
        <text :class="['value', currentChat.issueDetails.issueTypeClass]">{{ currentChat.issueDetails.issueType }}</text>
      </view>
      <view class="progress-info">
        <text class="label">当前阶段：</text>
        <text :class="['value', currentChat.issueDetails.statusClass]">{{ currentChat.issueDetails.status }}</text>
      </view>
      <view v-if="currentChat.issueDetails.nextSteps" class="progress-info">
        <text class="label">下一步：</text>
        <text class="value next-steps">{{ currentChat.issueDetails.nextSteps }}</text>
      </view>
      <view v-if="currentChat.issueDetails.estimatedTime" class="progress-info">
        <text class="label">预计处理：</text>
        <text class="value">{{ currentChat.issueDetails.estimatedTime }}</text>
      </view>
      <view v-if="currentChat.issueDetails.lastUpdated" class="progress-info">
        <text class="label">最后更新：</text>
        <text class="value">{{ currentChat.issueDetails.lastUpdated }}</text>
      </view>
      <button v-if="currentChat.issueDetails.actionButtonText" :class="['action-button', currentChat.issueDetails.actionButtonClass]" @tap="handleIssueAction(currentChat.issueDetails.actionType)">
        {{ currentChat.issueDetails.actionButtonText }}
      </button>
    </view>

    <!-- 相关附件集中查看区 -->
    <view v-if="currentChat.attachments && currentChat.attachments.length > 0" class="attachments-section card">
      <view class="card-title-section">
        <text class="card-title">相关附件</text>
      </view>
      <view class="attachment-list">
        <view v-for="(attachment, index) in currentChat.attachments" :key="index" class="attachment-item" @tap="downloadFile(attachment.url, attachment.name)">
          <text class="attachment-icon">📎</text>
          <text class="attachment-name">{{ attachment.name }}</text>
          <text class="download-button">下载</text>
        </view>
      </view>
    </view>

    <!-- 核心操作区 -->
    <view class="chat-input-area-placeholder"></view>
    <view class="chat-input-area">
      <view class="input-row">
        <input
          class="message-input"
          v-model="newMessage"
          placeholder="请输入消息..."
          confirm-type="send"
          @confirm="sendMessage"
        />
        <button class="send-button" @tap="sendMessage">发送</button>
      </view>
      <view class="action-row">
        <view class="action-item" @tap="showAttachmentOptions">
          <image src="/static/messages/attachment_icon.png" class="action-icon"></image>
          <text>附件</text>
        </view>
        <view class="action-item" @tap="showQuickPhrases">
          <image src="/static/messages/quick_phrase_icon.png" class="action-icon"></image>
          <text>快捷短语</text>
        </view>
        <view class="action-item" @tap="showOrderAssociation">
          <image src="/static/messages/order_associate_icon.png" class="action-icon"></image>
          <text>关联订单</text>
        </view>
        <view v-if="currentChat.csInfo.type === 'ai'" class="action-item" @tap="transferToHuman">
          <image src="/static/messages/transfer_human_icon.png" class="action-icon"></image>
          <text>转人工</text>
        </view>
        <view v-else-if="currentChat.csInfo.type === 'human'" class="action-item" @tap="endChat">
          <image src="/static/messages/end_chat_icon.png" class="action-icon"></image>
          <text>结束会话</text>
        </view>
      </view>
    </view>

    <!-- 附件上传选项弹窗 -->
    <uni-popup ref="attachmentPopup" type="bottom">
      <view class="attachment-options-popup">
        <button @tap="chooseImage">上传图片</button>
        <button @tap="chooseVideo">上传视频</button>
        <button @tap="chooseFile">上传文件</button>
        <button @tap="closeAttachmentPopup">取消</button>
      </view>
    </uni-popup>

    <!-- 快捷短语弹窗 -->
    <uni-popup ref="quickPhrasePopup" type="bottom">
      <view class="quick-phrase-popup">
        <view class="popup-title">快捷短语</view>
        <scroll-view scroll-y class="phrase-list">
          <view v-for="(phrase, index) in quickPhrases" :key="index" class="phrase-item" @tap="selectQuickPhrase(phrase)">
            {{ phrase }}
          </view>
        </scroll-view>
        <button @tap="closeQuickPhrasePopup">取消</button>
      </view>
    </uni-popup>

    <!-- 订单关联弹窗 -->
    <uni-popup ref="orderAssociationPopup" type="bottom">
      <view class="order-association-popup">
        <view class="popup-title">选择关联订单</view>
        <scroll-view scroll-y class="order-list">
          <view v-for="order in userOrders" :key="order.id" class="order-item" @tap="selectAssociatedOrder(order)">
            <image class="order-thumbnail" :src="order.productImage" mode="aspectFill"></image>
            <view class="order-info">
              <text class="order-id">订单号: {{ order.orderId }}</text>
              <text class="order-product">{{ order.productName }}</text>
            </view>
            <text class="order-status">{{ order.status }}</text>
          </view>
        </scroll-view>
        <button @tap="closeOrderAssociationPopup">取消</button>
      </view>
    </uni-popup>
  </view>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
// 引入 uni-popup 组件，确保您已安装 uni-ui
// import uniPopup from '@dcloudio/uni-ui/lib/uni-popup/uni-popup.vue';

// 模拟数据
const mockChatData = {
  'chat_order_123': {
    chatId: 'chat_order_123',
    csInfo: {
      name: '客服小A (在线)',
      avatar: '/static/messages/cs_online_en.png',
      status: '在线',
      statusClass: 'status-online',
      type: 'human', // human or ai
      typing: false
    },
    relatedOrder: {
      orderId: '20240715123456',
      productName: '山东红富士苹果',
      status: '待验收'
    },
    issueDetails: {
      ticketId: 'TK20240715001',
      issueType: '物流问题',
      issueTypeClass: 'issue-logistics',
      status: '处理中',
      statusClass: 'cs-status-processing',
      nextSteps: '已联系物流公司核实，请等待回复',
      estimatedTime: '2小时内',
      lastUpdated: '2025-07-15 16:45',
      actionButtonText: '查看物流详情',
      actionButtonClass: 'action-default',
      actionType: 'viewLogistics'
    },
    showEvaluateButton: false,
    chatHistory: [
      { id: 1, senderType: 'user', senderAvatar: '/static/messages/user_avatar.png', type: 'text', content: '您好，我的订单20240715123456的物流信息一直没更新，是不是出问题了？', timestamp: '2025-07-15 16:00', status: 'read' },
      { id: 2, senderType: 'cs', senderAvatar: '/static/messages/cs_online_en.png', type: 'text', content: '您好，请稍等，我帮您核实一下。', timestamp: '2025-07-15 16:01' },
      { id: 3, senderType: 'cs', senderAvatar: '/static/messages/cs_online_en.png', type: 'text', content: '经查询，您的订单目前正在派送途中，可能物流信息更新有延迟。我们已为您加急处理，请您留意最新物流动态。', timestamp: '2025-07-15 16:05' },
      { id: 4, senderType: 'cs', senderAvatar: '/static/messages/cs_online_en.png', type: 'card', cardType: 'order', cardData: { id: '20240715123456', title: '山东红富士苹果 5吨', image: 'https://placehold.co/100x100/FF5733/FFFFFF?text=Apple', price: '52500.00', status: '运输中' }, cardActionText: '查看订单详情', timestamp: '2025-07-15 16:06' },
      { id: 5, senderType: 'user', senderAvatar: '/static/messages/user_avatar.png', type: 'image', content: 'https://placehold.co/300x200/2196F3/FFFFFF?text=物流截图', timestamp: '2025-07-15 16:10', status: 'delivered' },
      { id: 6, senderType: 'user', senderAvatar: '/static/messages/user_avatar.png', type: 'text', content: '这是我物流截图，显示还在XX仓库，但是已经一天没动了。', timestamp: '2025-07-15 16:11', status: 'delivered' },
      { id: 7, senderType: 'cs', senderAvatar: '/static/messages/cs_online_en.png', type: 'text', content: '好的，我已收到您的截图，已为您创建工单TK20240715001，我们会立即联系物流公司进行核实，请您耐心等待。', timestamp: '2025-07-15 16:15' },
      { id: 8, senderType: 'cs', senderAvatar: '/static/messages/cs_online_en.png', type: 'actions', actions: [{ text: '查看工单详情', type: 'viewTicket', data: 'TK20240715001' }], timestamp: '2025-07-15 16:16' }
    ],
    attachments: [
      { name: '物流异常截图.png', url: 'https://placehold.co/300x200/2196F3/FFFFFF?text=物流截图', type: 'image' }
    ]
  },
  'chat_new_issue': {
    chatId: 'chat_new_issue',
    csInfo: {
      name: '智能客服',
      avatar: '/static/messages/cs_ai_en.png',
      status: '在线',
      statusClass: 'status-online',
      type: 'ai',
      typing: false
    },
    relatedOrder: {},
    issueDetails: {},
    showEvaluateButton: false,
    chatHistory: [
      { id: 1, senderType: 'cs', senderAvatar: '/static/messages/cs_ai_en.png', type: 'text', content: '您好，我是智能客服小智，请问有什么可以帮助您的？', timestamp: '2025-07-15 15:00' },
      { id: 2, senderType: 'cs', senderAvatar: '/static/messages/cs_ai_en.png', type: 'actions', actions: [{ text: '我的订单在哪里？', type: 'quickQuestion', data: 'order_status' }, { text: '如何申请退款？', type: 'quickQuestion', data: 'refund_process' }], timestamp: '2025-07-15 15:01' }
    ],
    attachments: []
  },
  'chat_resolved_issue': {
    chatId: 'chat_resolved_issue',
    csInfo: {
      name: '客服小B (已离线)',
      avatar: '/static/messages/cs_offline_en.png',
      status: '已解决',
      statusClass: 'status-resolved',
      type: 'human',
      typing: false
    },
    relatedOrder: {
      orderId: '20240714987654',
      productName: '广东荔枝',
      status: '已完成'
    },
    issueDetails: {
      ticketId: 'TK20240714002',
      issueType: '质量纠纷',
      issueTypeClass: 'issue-quality',
      status: '已解决',
      statusClass: 'cs-status-resolved',
      nextSteps: '您已接受解决方案，本次服务已结束。',
      estimatedTime: '',
      lastUpdated: '2025-07-14 18:00',
      actionButtonText: '查看解决方案',
      actionButtonClass: 'action-default',
      actionType: 'viewSolution'
    },
    showEvaluateButton: true,
    chatHistory: [
      { id: 1, senderType: 'user', senderAvatar: '/static/messages/user_avatar.png', type: 'text', content: '我的荔枝收到后发现有部分腐烂，请问怎么处理？', timestamp: '2025-07-14 10:00', status: 'read' },
      { id: 2, senderType: 'cs', senderAvatar: '/static/messages/cs_online_en.png', type: 'text', content: '请您提供腐烂的照片和视频，以及订单号，以便我们核实。', timestamp: '2025-07-14 10:05' },
      { id: 3, senderType: 'user', senderAvatar: '/static/messages/user_avatar.png', type: 'image', content: 'https://placehold.co/300x200/FF4D4F/FFFFFF?text=腐烂图片1', timestamp: '2025-07-14 10:10', status: 'read' },
      { id: 4, senderType: 'user', senderAvatar: '/static/messages/user_avatar.png', type: 'video', content: 'https://www.w3schools.com/html/mov_bbb.mp4', poster: 'https://placehold.co/300x200/FF4D4F/FFFFFF?text=视频封面', timestamp: '2025-07-14 10:11', status: 'read' },
      { id: 5, senderType: 'cs', senderAvatar: '/static/messages/cs_online_en.png', type: 'text', content: '好的，我们已收到您的凭证，已为您创建工单TK20240714002，并转交售后部门处理。', timestamp: '2025-07-14 10:15' },
      { id: 6, senderType: 'cs', senderAvatar: '/static/messages/cs_online_en.png', type: 'text', content: '经过核实，我们决定为您提供订单金额20%的退款作为补偿。请问您是否接受此方案？', timestamp: '2025-07-14 17:30' },
      { id: 7, senderType: 'cs', senderAvatar: '/static/messages/cs_online_en.png', type: 'actions', actions: [{ text: '接受方案', type: 'acceptSolution', data: 'TK20240714002' }, { text: '拒绝方案', type: 'rejectSolution', data: 'TK20240714002' }], timestamp: '2025-07-14 17:31' },
      { id: 8, senderType: 'user', senderAvatar: '/static/messages/user_avatar.png', type: 'text', content: '好的，我接受这个方案。谢谢！', timestamp: '2025-07-14 17:45', status: 'read' },
      { id: 9, senderType: 'cs', senderAvatar: '/static/messages/cs_online_en.png', type: 'text', content: '好的，退款将在1-3个工作日内原路返回您的账户，请注意查收。本次服务已结束，感谢您的支持！', timestamp: '2025-07-14 18:00' }
    ],
    attachments: [
      { name: '腐烂图片1.png', url: 'https://placehold.co/300x200/FF4D4F/FFFFFF?text=腐烂图片1', type: 'image' },
      { name: '腐烂视频.mp4', url: 'https://www.w3schools.com/html/mov_bbb.mp4', type: 'video' },
      { name: '平台补偿方案.pdf', url: 'https://example.com/solution.pdf', type: 'file' }
    ]
  }
};

// 模拟用户订单数据（用于关联订单功能）
const userOrders = ref([
  { id: '20240715123456', orderId: '20240715123456', productName: '山东红富士苹果', productImage: 'https://placehold.co/100x100/FF5733/FFFFFF?text=Apple', status: '待验收' },
  { id: '20240714987654', orderId: '20240714987654', productName: '广东荔枝', productImage: 'https://placehold.co/100x100/FF33CC/FFFFFF?text=Lychee', status: '已完成' },
  { id: '20240713000111', orderId: '20240713000111', productName: '东北大米', productImage: 'https://placehold.co/100x100/99FF33/FFFFFF?text=Rice', status: '已完成' }
]);

const currentChat = ref({
  chatId: '',
  csInfo: {
    name: '加载中...',
    avatar: '/static/messages/cs_default_avatar.png',
    status: '',
    statusClass: '',
    type: 'ai',
    typing: false
  },
  relatedOrder: {},
  issueDetails: {},
  showEvaluateButton: false,
  chatHistory: [],
  attachments: []
});

const newMessage = ref('');
const scrollViewTo = ref(''); // 用于控制滚动到最新消息

// 弹窗引用
const attachmentPopup = ref(null);
const quickPhrasePopup = ref(null);
const orderAssociationPopup = ref(null);

const quickPhrases = ref([
  '我的订单状态是什么？',
  '如何申请退款？',
  '我需要上传凭证。',
  '请转接人工客服。',
  '谢谢！'
]);

onLoad((options) => {
  const chatId = options.chatId || 'chat_order_123'; // 默认加载一个订单咨询
  loadChatData(chatId);
});

// 加载聊天数据
const loadChatData = (chatId) => {
  uni.showLoading({ title: '加载会话...' });
  setTimeout(() => {
    const data = mockChatData[chatId];
    if (data) {
      currentChat.value = JSON.parse(JSON.stringify(data)); // 深拷贝，避免直接修改mock数据
      nextTick(() => {
        scrollToBottom();
      });
    } else {
      uni.showToast({ title: '会话不存在', icon: 'none' });
      // Fallback to a default empty chat or error state
      currentChat.value = {
        chatId: 'empty_chat',
        csInfo: { name: '系统', avatar: '/static/messages/cs_default_avatar.png', status: '离线', statusClass: 'status-offline', type: 'ai', typing: false },
        relatedOrder: {},
        issueDetails: {},
        showEvaluateButton: false,
        chatHistory: [{ id: 1, senderType: 'cs', senderAvatar: '/static/messages/cs_default_avatar.png', type: 'text', content: '当前会话加载失败或不存在。', timestamp: new Date().toLocaleString() }],
        attachments: []
      };
    }
    uni.hideLoading();
  }, 500);
};

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (currentChat.value.chatHistory.length > 0) {
      scrollViewTo.value = 'bottom-anchor'; // 滚动到最底部
    }
  });
};

// 发送消息
const sendMessage = () => {
  if (newMessage.value.trim() === '') {
    uni.showToast({ title: '消息不能为空', icon: 'none' });
    return;
  }

  const newMsg = {
    id: currentChat.value.chatHistory.length + 1,
    senderType: 'user',
    senderAvatar: '/static/messages/user_avatar.png', // 用户头像
    type: 'text',
    content: newMessage.value.trim(),
    timestamp: new Date().toLocaleString(),
    status: 'sent' // 模拟发送状态
  };

  currentChat.value.chatHistory.push(newMsg);
  newMessage.value = ''; // 清空输入框

  // 模拟客服回复
  setTimeout(() => {
    currentChat.value.csInfo.typing = true;
  }, 500);

  setTimeout(() => {
    currentChat.value.csInfo.typing = false;
    const csReply = {
      id: currentChat.value.chatHistory.length + 1,
      senderType: 'cs',
      senderAvatar: currentChat.value.csInfo.avatar,
      type: 'text',
      content: `您好，我已收到您的消息：“${newMsg.content}”。请问还有什么可以帮助您的吗？`,
      timestamp: new Date().toLocaleString()
    };
    currentChat.value.chatHistory.push(csReply);
    scrollToBottom();
  }, 1500); // 模拟回复延迟

  scrollToBottom();
};

// 格式化消息时间（根据时间间隔决定是否显示）
const shouldShowTime = (currentTimestamp, index) => {
  if (index === 0) return true;
  const currentTime = new Date(currentTimestamp).getTime();
  const prevTime = new Date(currentChat.value.chatHistory[index - 1].timestamp).getTime();
  // 如果与上一条消息的时间间隔超过5分钟，则显示时间
  return (currentTime - prevTime) > (5 * 60 * 1000);
};

const formatMessageTime = (timestamp) => {
  const date = new Date(timestamp);
  const now = new Date();
  const diff = now.getTime() - date.getTime(); // 毫秒差

  if (diff < 60 * 1000) { // 1分钟内
    return '刚刚';
  } else if (diff < 60 * 60 * 1000) { // 1小时内
    return `${Math.floor(diff / (60 * 1000))}分钟前`;
  } else if (date.toDateString() === now.toDateString()) { // 今天
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
  } else if (date.getFullYear() === now.getFullYear()) { // 今年
    return date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' });
  } else { // 往年
    return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' });
  }
};

// 附件功能
const showAttachmentOptions = () => {
  attachmentPopup.value.open('bottom');
};

const closeAttachmentPopup = () => {
  attachmentPopup.value.close();
};

const chooseImage = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      const tempFilePath = res.tempFilePaths[0];
      // 模拟上传并发送图片消息
      const newMsg = {
        id: currentChat.value.chatHistory.length + 1,
        senderType: 'user',
        senderAvatar: '/static/messages/user_avatar.png',
        type: 'image',
        content: tempFilePath, // 实际应为上传后的URL
        timestamp: new Date().toLocaleString(),
        status: 'sent'
      };
      currentChat.value.chatHistory.push(newMsg);
      scrollToBottom();
      closeAttachmentPopup();
      uni.showToast({ title: '图片已发送', icon: 'success' });
      // 模拟客服回复
      setTimeout(() => {
        currentChat.value.csInfo.typing = true;
      }, 500);
      setTimeout(() => {
        currentChat.value.csInfo.typing = false;
        const csReply = {
          id: currentChat.value.chatHistory.length + 1,
          senderType: 'cs',
          senderAvatar: currentChat.value.csInfo.avatar,
          type: 'text',
          content: '您发送的图片我已收到。',
          timestamp: new Date().toLocaleString()
        };
        currentChat.value.chatHistory.push(csReply);
        scrollToBottom();
      }, 1500);
    }
  });
};

const chooseVideo = () => {
  uni.chooseVideo({
    sourceType: ['album', 'camera'],
    compressed: true,
    maxDuration: 60,
    success: (res) => {
      const tempFilePath = res.tempFilePath;
      // 模拟上传并发送视频消息
      const newMsg = {
        id: currentChat.value.chatHistory.length + 1,
        senderType: 'user',
        senderAvatar: '/static/messages/user_avatar.png',
        type: 'video',
        content: tempFilePath, // 实际应为上传后的URL
        poster: 'https://placehold.co/300x200/CCCCCC/666666?text=视频封面', // 视频封面
        timestamp: new Date().toLocaleString(),
        status: 'sent'
      };
      currentChat.value.chatHistory.push(newMsg);
      scrollToBottom();
      closeAttachmentPopup();
      uni.showToast({ title: '视频已发送', icon: 'success' });
      // 模拟客服回复
      setTimeout(() => {
        currentChat.value.csInfo.typing = true;
      }, 500);
      setTimeout(() => {
        currentChat.value.csInfo.typing = false;
        const csReply = {
          id: currentChat.value.chatHistory.length + 1,
          senderType: 'cs',
          senderAvatar: currentChat.value.csInfo.avatar,
          type: 'text',
          content: '您发送的视频我已收到。',
          timestamp: new Date().toLocaleString()
        };
        currentChat.value.chatHistory.push(csReply);
        scrollToBottom();
      }, 1500);
    }
  });
};

const chooseFile = () => {
  uni.chooseMessageFile({
    count: 1,
    type: 'file',
    success: (res) => {
      const tempFile = res.tempFiles[0];
      // 模拟上传并发送文件消息
      const newMsg = {
        id: currentChat.value.chatHistory.length + 1,
        senderType: 'user',
        senderAvatar: '/static/messages/user_avatar.png',
        type: 'file',
        content: tempFile.path, // 实际应为上传后的URL
        fileName: tempFile.name,
        fileSize: (tempFile.size / 1024).toFixed(2) + 'KB',
        timestamp: new Date().toLocaleString(),
        status: 'sent'
      };
      currentChat.value.chatHistory.push(newMsg);
      scrollToBottom();
      closeAttachmentPopup();
      uni.showToast({ title: '文件已发送', icon: 'success' });
      // 模拟客服回复
      setTimeout(() => {
        currentChat.value.csInfo.typing = true;
      }, 500);
      setTimeout(() => {
        currentChat.value.csInfo.typing = false;
        const csReply = {
          id: currentChat.value.chatHistory.length + 1,
          senderType: 'cs',
          senderAvatar: currentChat.value.csInfo.avatar,
          type: 'text',
          content: '您发送的文件我已收到。',
          timestamp: new Date().toLocaleString()
        };
        currentChat.value.chatHistory.push(csReply);
        scrollToBottom();
      }, 1500);
    }
  });
};

// 预览图片
const previewImage = (urls) => {
  uni.previewImage({
    urls: urls,
    current: urls[0]
  });
};

// 播放视频
const playVideo = (videoUrl) => {
  uni.showToast({ title: `播放视频: ${videoUrl}`, icon: 'none' });
  // 实际应用中可能跳转到视频播放页面或使用 uni.createVideoContext
};

// 下载文件
const downloadFile = (url, name) => {
  uni.showToast({ title: `下载文件: ${name}`, icon: 'none' });
  // uni.downloadFile({
  //   url: url,
  //   success: (res) => {
  //     if (res.statusCode === 200) {
  //       uni.openDocument({
  //         filePath: res.tempFilePath,
  //         showMenu: true,
  //         success: (openRes) => {
  //           console.log('文件打开成功', openRes);
  //         },
  //         fail: (openErr) => {
  //           console.error('文件打开失败', openErr);
  //           uni.showToast({ title: '文件打开失败', icon: 'none' });
  //         }
  //       });
  //     }
  //   },
  //   fail: (err) => {
  //     console.error('文件下载失败', err);
  //     uni.showToast({ title: '文件下载失败', icon: 'none' });
  //   }
  // });
};

// 处理消息中内嵌的卡片点击
const handleCardClick = (cardType, cardData) => {
  if (cardType === 'order') {
    goToOrderDetail(cardData.id);
  } else if (cardType === 'product') {
    uni.showToast({ title: `查看商品详情: ${cardData.id}`, icon: 'none' });
    // uni.navigateTo({ url: `/pages/product/detail?productId=${cardData.id}` });
  }
};

// 处理消息中内嵌的动作按钮点击
const handleEmbeddedAction = (actionType, data) => {
  if (actionType === 'viewTicket') {
    viewTicketDetails(data);
  } else if (actionType === 'quickQuestion') {
    newMessage.value = data; // 将快捷问题填充到输入框
    sendMessage(); // 并自动发送
  } else if (actionType === 'applyRefund') {
    uni.showToast({ title: `跳转到退款申请页面，订单ID: ${data}`, icon: 'none' });
    // uni.navigateTo({ url: `/pages/aftersale/apply?orderId=${data}` });
  } else if (actionType === 'uploadProof') {
    showAttachmentOptions(); // 打开附件上传选项
  } else if (actionType === 'confirmSolution') {
    uni.showToast({ title: `确认解决方案，工单ID: ${data}`, icon: 'success' });
    // 模拟更新工单状态
    currentChat.value.issueDetails.status = '已解决';
    currentChat.value.issueDetails.statusClass = 'cs-status-resolved';
    currentChat.value.issueDetails.nextSteps = '您已确认解决方案。';
    currentChat.value.issueDetails.actionButtonText = '';
    currentChat.value.showEvaluateButton = true; // 显示评价按钮
  }
};

// 快捷短语功能
const showQuickPhrases = () => {
  quickPhrasePopup.value.open('bottom');
};

const closeQuickPhrasePopup = () => {
  quickPhrasePopup.value.close();
};

const selectQuickPhrase = (phrase) => {
  newMessage.value = phrase;
  sendMessage();
  closeQuickPhrasePopup();
};

// 订单关联功能
const showOrderAssociation = () => {
  orderAssociationPopup.value.open('bottom');
};

const closeOrderAssociationPopup = () => {
  orderAssociationPopup.value.close();
};

const selectAssociatedOrder = (order) => {
  uni.showToast({ title: `已关联订单: ${order.orderId}`, icon: 'none' });
  currentChat.value.relatedOrder = { ...order }; // 更新关联订单信息
  closeOrderAssociationPopup();
  // 可以在此发送一条系统消息到聊天记录中，表明已关联订单
  const systemMsg = {
    id: currentChat.value.chatHistory.length + 1,
    senderType: 'system',
    senderAvatar: '/static/messages/system_notification.png',
    type: 'text',
    content: `您已将当前会话关联到订单：${order.orderId} - ${order.productName}。`,
    timestamp: new Date().toLocaleString()
  };
  currentChat.value.chatHistory.push(systemMsg);
  scrollToBottom();
};

// 导航到订单详情
const goToOrderDetail = (orderId) => {
  uni.showToast({ title: `跳转到订单详情: ${orderId}`, icon: 'none' });
  uni.navigateTo({ url: `/pages/messages/orderDetail/orderDetail?orderId=${orderId}` });
};

// 查看工单详情
const viewTicketDetails = (ticketId) => {
  uni.showToast({ title: `查看工单详情: ${ticketId}`, icon: 'none' });
  // uni.navigateTo({ url: `/pages/ticket/detail?ticketId=${ticketId}` });
};

// 处理工单操作按钮
const handleIssueAction = (actionType) => {
  if (actionType === 'viewLogistics') {
    uni.showToast({ title: '查看物流详情', icon: 'none' });
    // uni.navigateTo({ url: `/pages/logistics/detail?orderId=${currentChat.value.relatedOrder.orderId}` });
  } else if (actionType === 'viewSolution') {
    uni.showToast({ title: '查看解决方案', icon: 'none' });
    // uni.navigateTo({ url: `/pages/ticket/solution?ticketId=${currentChat.value.issueDetails.ticketId}` });
  }
};

// 服务评价
const evaluateService = () => {
  uni.showToast({ title: '跳转到服务评价页面', icon: 'none' });
  // uni.navigateTo({ url: `/pages/customerService/evaluate?chatId=${currentChat.value.chatId}` });
};

// 转人工客服
const transferToHuman = () => {
  uni.showModal({
    title: '转接人工客服',
    content: '确定要转接人工客服吗？',
    success: (res) => {
      if (res.confirm) {
        uni.showToast({ title: '正在转接人工客服...', icon: 'none' });
        // 模拟转接成功
        setTimeout(() => {
          currentChat.value.csInfo.name = '客服小C (人工)';
          currentChat.value.csInfo.avatar = '/static/messages/cs_online_en.png';
          currentChat.value.csInfo.status = '在线';
          currentChat.value.csInfo.statusClass = 'status-online';
          currentChat.value.csInfo.type = 'human';
          currentChat.value.csInfo.typing = false;
          const systemMsg = {
            id: currentChat.value.chatHistory.length + 1,
            senderType: 'system',
            senderAvatar: '/static/messages/system_notification.png',
            type: 'text',
            content: '已为您转接人工客服，客服小C将为您服务。',
            timestamp: new Date().toLocaleString()
          };
          currentChat.value.chatHistory.push(systemMsg);
          scrollToBottom();
          uni.showToast({ title: '已转接人工客服', icon: 'success' });
        }, 1500);
      }
    }
  });
};

// 结束会话
const endChat = () => {
  uni.showModal({
    title: '结束会话',
    content: '确定要结束本次会话吗？',
    success: (res) => {
      if (res.confirm) {
        uni.showToast({ title: '会话已结束', icon: 'success' });
        currentChat.value.csInfo.status = '已结束';
        currentChat.value.csInfo.statusClass = 'status-closed';
        currentChat.value.showEvaluateButton = true; // 会话结束后显示评价按钮
        // 可以禁用输入框和操作按钮
      }
    }
  });
};

// 图片加载失败处理
const onImageError = (type, item) => {
  console.error(`Failed to load ${type} type image, item:`, item);
  if (type === 'csAvatar' || type === 'messageAvatar') {
    item.avatar = '/static/messages/cs_default_avatar.png';
  } else if (type === 'chatImage' || type === 'cardThumbnail') {
    item.content = 'https://placehold.co/300x200/FF0000/FFFFFF?text=图片加载失败';
  }
};
</script>

<style scoped>
page {
  background-color: #f0f2f5;
  height: 100%;
}

.chat-detail-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  box-sizing: border-box;
}

/* Header */
.chat-header {
  background-color: #ffffff;
  padding: 24rpx 32rpx;
  display: flex;
  align-items: center;
  border-bottom: 1rpx solid #eeeeee;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
  flex-shrink: 0; /* 防止被压缩 */
}

.cs-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  margin-right: 20rpx;
  flex-shrink: 0;
  background-color: #e0e0e0;
}

.cs-info {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  min-width: 0;
}

.cs-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cs-status {
  font-size: 26rpx;
  color: #666666;
  margin-top: 4rpx;
  line-height: 1.4;
}

.cs-status.status-online {
  color: #4CAF50; /* Green */
}
.cs-status.status-offline, .cs-status.status-closed {
  color: #6c757d; /* Grey */
}
.cs-status.status-processing {
  color: #2196F3; /* Blue */
}
.cs-status.status-resolved {
  color: #4CAF50; /* Green */
}
.typing-indicator {
  margin-left: 8rpx;
  animation: blink 1s infinite steps(1, start);
}
@keyframes blink {
  50% { opacity: 0; }
}

.header-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10rpx;
  flex-shrink: 0;
}

.related-info {
  display: flex;
  align-items: center;
  background-color: #f0f2f5;
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  color: #666666;
  white-space: nowrap;
  transition: background-color 0.2s ease;
}
.related-info:active {
  background-color: #e0e2e5;
}
.related-text {
  margin-right: 8rpx;
}
.arrow-icon {
  font-size: 24rpx;
  color: #999999;
}

.evaluate-button {
  background-color: #FFC107; /* Amber */
  color: #333;
  font-size: 24rpx;
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
  line-height: 1;
  height: auto; /* Ensure button height adjusts to padding */
  white-space: nowrap;
  transition: background-color 0.2s ease;
}
.evaluate-button:active {
  background-color: #e0ac00;
}

/* Message Area */
.message-area {
  flex: 1;
  padding: 20rpx;
  background-color: #f0f2f5;
  box-sizing: border-box;
}

.message-time-divider {
  text-align: center;
  font-size: 24rpx;
  color: #999999;
  margin: 20rpx 0;
}

.message-item-wrapper {
  display: flex;
  flex-direction: column;
}

.message-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20rpx;
}

.message-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  flex-shrink: 0;
  background-color: #e0e0e0;
}

.message-bubble {
  max-width: 70%; /* 限制消息气泡宽度 */
  padding: 20rpx 28rpx;
  border-radius: 20rpx;
  font-size: 30rpx;
  line-height: 1.6;
  word-break: break-all; /* 自动换行 */
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.user-message {
  justify-content: flex-end;
  margin-left: auto;
}
.user-message .message-bubble {
  background-color: #4CAF50; /* Green for user messages */
  color: white;
  margin-left: 20rpx;
}
.user-message .message-avatar {
  margin-left: 20rpx;
}

.cs-message {
  justify-content: flex-start;
  margin-right: auto;
}
.cs-message .message-bubble {
  background-color: #ffffff;
  color: #333333;
  margin-right: 20rpx;
}
.cs-message .message-avatar {
  margin-right: 20rpx;
}

.message-content-text {
  /* No specific styles needed, inherited from message-bubble */
}

/* Media Messages */
.message-content-image {
  max-width: 100%;
  border-radius: 12rpx;
  overflow: hidden;
}
.chat-image {
  width: 100%;
  height: auto;
  display: block;
}

.message-content-video {
  position: relative;
  width: 100%;
  height: 300rpx; /* Fixed height for video preview */
  background-color: black;
  border-radius: 12rpx;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}
.chat-video {
  width: 100%;
  height: 100%;
}
.video-play-icon {
  position: absolute;
  width: 100rpx;
  height: 100rpx;
  z-index: 1;
  opacity: 0.8;
}

/* File Message */
.message-content-file {
  display: flex;
  align-items: center;
  background-color: #f0f2f5;
  padding: 20rpx;
  border-radius: 12rpx;
  width: 400rpx; /* Fixed width for file bubble */
  box-sizing: border-box;
}
.file-icon {
  font-size: 48rpx;
  margin-right: 20rpx;
  color: #2196F3;
}
.file-info {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.file-name {
  font-size: 28rpx;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.file-size {
  font-size: 24rpx;
  color: #999;
  margin-top: 4rpx;
}
.download-icon {
  font-size: 36rpx;
  margin-left: 20rpx;
  color: #2196F3;
}

/* Card Message */
.message-content-card {
  display: flex;
  flex-direction: column;
  width: 450rpx; /* Fixed width for card bubble */
  background-color: #ffffff;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
}
.card-header-mini {
  background-color: #f8f8f8;
  padding: 15rpx 20rpx;
  font-size: 26rpx;
  color: #666;
  font-weight: bold;
  border-bottom: 1rpx solid #eee;
}
.card-thumbnail {
  width: 100%;
  height: 200rpx;
  background-color: #e0e0e0;
}
.card-details {
  padding: 20rpx;
  display: flex;
  flex-direction: column;
}
.card-title-mini {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.card-price, .card-status {
  font-size: 26rpx;
  color: #666;
  margin-top: 8rpx;
}
.card-action-button {
  background-color: #4CAF50;
  color: white;
  font-size: 28rpx;
  padding: 16rpx 0;
  border-radius: 0; /* No border-radius for full-width button */
  width: 100%;
  margin-top: 10rpx;
}

/* Embedded Action Buttons */
.message-actions-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-top: 15rpx;
  width: 100%;
}
.message-action-button {
  background-color: #f0f2f5;
  color: #333;
  font-size: 26rpx;
  padding: 12rpx 20rpx;
  border-radius: 16rpx;
  border: 1rpx solid #dddddd;
  white-space: nowrap;
  flex-shrink: 0;
  transition: background-color 0.2s ease;
}
.message-action-button:active {
  background-color: #e0e2e5;
}

.message-status {
  font-size: 24rpx;
  color: #999999;
  margin-top: 8rpx;
  align-self: flex-end; /* Align status with user message bubble */
  margin-right: 10rpx; /* Align with avatar */
}

/* Issue Progress Card & Attachments Section (reusing card styles) */
.card {
  background-color: #ffffff;
  border-radius: 16rpx;
  margin: 20rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
}

.card-title-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
  padding-bottom: 15rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.card-title {
  font-size: 34rpx;
  font-weight: bold;
  color: #333333;
}

.issue-progress-card {
  border-left: 10rpx solid #2196F3; /* Blue border for progress */
}
.ticket-id {
  font-size: 26rpx;
  color: #999;
}
.progress-info {
  display: flex;
  align-items: flex-start;
  margin-bottom: 15rpx;
  font-size: 28rpx;
  color: #555555;
  line-height: 1.5;
}
.progress-info .label {
  color: #888888;
  flex-shrink: 0;
  margin-right: 10rpx;
}
.progress-info .value {
  flex-grow: 1;
  word-break: break-all;
}
.next-steps {
  color: #dc3545; /* Red for urgent next steps */
  font-weight: bold;
}

/* Issue Type Colors (same as messages.vue) */
.issue-order { color: #4CAF50; }
.issue-payment { color: #FF9800; }
.issue-logistics { color: #2196F3; }
.issue-invoice { color: #9C27B0; }
.issue-account { color: #F44336; }
.issue-complaint { color: #E91E63; }
.issue-activity { color: #00BCD4; }
.issue-quality { color: #FFC107; }
.issue-coldchain { color: #607D8B; }
.issue-loss { color: #795548; }

/* CS Status Colors (same as messages.vue) */
.cs-status-pending { color: #FFC107; }
.cs-status-processing { color: #2196F3; }
.cs-status-resolved { color: #4CAF50; }
.cs-status-closed { color: #6c757d; }

.attachments-section {
  border-left: 10rpx solid #007bff; /* Blue border for attachments */
}
.attachment-list {
  display: flex;
  flex-direction: column;
}
.attachment-item {
  display: flex;
  align-items: center;
  padding: 15rpx 0;
  border-bottom: 1rpx dashed #f0f0f0;
}
.attachment-item:last-child {
  border-bottom: none;
}
.attachment-icon {
  font-size: 36rpx;
  margin-right: 20rpx;
  color: #007bff;
}
.attachment-name {
  font-size: 28rpx;
  color: #333;
  flex-grow: 1;
  word-break: break-all;
}
.download-button {
  background-color: #f0f2f5;
  color: #333;
  font-size: 24rpx;
  padding: 8rpx 16rpx;
  border-radius: 16rpx;
  margin-left: 20rpx;
  flex-shrink: 0;
}

/* Chat Input Area */
.chat-input-area-placeholder {
  height: 200rpx; /* Reserve space for fixed input area */
  padding-bottom: env(safe-area-inset-bottom);
}

.chat-input-area {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background-color: #ffffff;
  box-shadow: 0 -4rpx 12rpx rgba(0, 0, 0, 0.1);
  padding: 20rpx 30rpx env(safe-area-inset-bottom); /* 适配底部安全区 */
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  z-index: 100;
}

.input-row {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.message-input {
  flex-grow: 1;
  height: 80rpx;
  background-color: #f0f2f5;
  border-radius: 40rpx;
  padding: 0 30rpx;
  font-size: 30rpx;
  color: #333;
  margin-right: 20rpx;
}

.send-button {
  background-color: #4CAF50; /* Green */
  color: white;
  padding: 16rpx 32rpx;
  border-radius: 40rpx;
  font-size: 30rpx;
  font-weight: 500;
  line-height: 1;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s ease;
}
.send-button:active {
  background-color: #388E3C;
}

.action-row {
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 24rpx;
  color: #666;
  padding: 10rpx;
}
.action-item:active {
  opacity: 0.7;
}

.action-icon {
  width: 56rpx;
  height: 56rpx;
  margin-bottom: 8rpx;
}

/* Popups */
.attachment-options-popup, .quick-phrase-popup, .order-association-popup {
  background-color: #ffffff;
  border-top-left-radius: 20rpx;
  border-top-right-radius: 20rpx;
  padding: 30rpx;
  padding-bottom: env(safe-area-inset-bottom);
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.attachment-options-popup button, .quick-phrase-popup button, .order-association-popup button {
  background-color: #f0f2f5;
  color: #333;
  font-size: 32rpx;
  padding: 24rpx 0;
  border-radius: 16rpx;
  width: 100%;
  transition: background-color 0.2s ease;
}
.attachment-options-popup button:last-child, .quick-phrase-popup button:last-child, .order-association-popup button:last-child {
  background-color: #ffffff;
  border: 1rpx solid #eeeeee;
  color: #666;
}
.attachment-options-popup button:active, .quick-phrase-popup button:active, .order-association-popup button:active {
  background-color: #e0e2e5;
}

.popup-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  text-align: center;
  margin-bottom: 30rpx;
}

.phrase-list, .order-list {
  max-height: 500rpx; /* Limit scroll height */
  margin-bottom: 30rpx;
}

.phrase-item {
  padding: 20rpx;
  font-size: 30rpx;
  color: #333;
  border-bottom: 1rpx solid #eee;
  transition: background-color 0.2s ease;
}
.phrase-item:active {
  background-color: #f0f2f5;
}
.phrase-item:last-child {
  border-bottom: none;
}

.order-item {
  display: flex;
  align-items: center;
  padding: 20rpx;
  border-bottom: 1rpx solid #eee;
  transition: background-color 0.2s ease;
}
.order-item:active {
  background-color: #f0f2f5;
}
.order-item:last-child {
  border-bottom: none;
}
.order-thumbnail {
  width: 100rpx;
  height: 100rpx;
  border-radius: 8rpx;
  margin-right: 20rpx;
  flex-shrink: 0;
  background-color: #e0e0e0;
}
.order-info {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.order-id {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
}
.order-product {
  font-size: 26rpx;
  color: #666;
  margin-top: 4rpx;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.order-status {
  font-size: 26rpx;
  color: #4CAF50;
  margin-left: 20rpx;
  flex-shrink: 0;
}
</style>
