<template>
  <view class="messages-page">
    <!-- 固定头部 -->
    <view class="fixed-header">
      <view class="header-title">消息中心</view>
      <view class="header-actions">
        <view class="action-btn" @click="markAllRead">
          <image class="action-icon" :src="getMessageImageUrl('clear.png')" mode="aspectFit" @error="handleImageError" @load="handleImageLoad"></image>
        </view>
      </view>
    </view>

    <!-- 固定标签页 -->
    <view class="fixed-tabs">
      <view 
        v-for="(tab, index) in tabs" 
        :key="index"
        class="tab-item"
        :class="{ active: currentTab === index }"
        @click="switchTab(index)"
      >
        <image class="tab-icon" :src="tab.icon" mode="aspectFit" @error="handleImageError" @load="handleImageLoad"></image>
        <text class="tab-label">{{ tab.label }}</text>
        <view v-if="tab.unread > 0" class="unread-badge">{{ tab.unread }}</view>
      </view>
    </view>

    <!-- 固定内容区域 -->
    <view class="fixed-content">
      <!-- 系统通知 -->
      <view v-if="currentTab === 0" class="tab-content">
        <view v-if="systemMessages.length === 0" class="empty-state">
          <image class="empty-icon" :src="getMessageImageUrl('system.png')" mode="aspectFit" @error="handleImageError" @load="handleImageLoad"></image>
          <text class="empty-text">暂无系统通知</text>
        </view>
        <view v-else class="message-list-content">
          <view 
            v-for="message in systemMessages" 
            :key="message.id"
            class="message-item"
            @click="handleMessageClick(message)"
          >
            <image class="message-icon" :src="message.icon" mode="aspectFit" @error="handleImageError" @load="handleImageLoad"></image>
            <view class="message-content">
              <view class="message-header">
                <text class="message-title">{{ message.title }}</text>
                <text class="message-time">{{ message.time }}</text>
              </view>
              <text class="message-preview">{{ message.content }}</text>
            </view>
            <view v-if="!message.read" class="unread-dot"></view>
          </view>
        </view>
      </view>

      <!-- 订单消息 -->
      <view v-if="currentTab === 1" class="tab-content">
        <view v-if="orderMessages.length === 0" class="empty-state">
          <image class="empty-icon" :src="getMessageImageUrl('express.png')" mode="aspectFit" @error="handleImageError" @load="handleImageLoad"></image>
          <text class="empty-text">暂无订单消息</text>
        </view>
        <view v-else class="message-list-content">
          <view 
            v-for="message in orderMessages" 
            :key="message.id"
            class="message-item"
            @click="handleMessageClick(message)"
          >
            <image class="message-icon" :src="message.icon" mode="aspectFit" @error="handleImageError" @load="handleImageLoad"></image>
            <view class="message-content">
              <view class="message-header">
                <text class="message-title">{{ message.title }}</text>
                <text class="message-time">{{ message.time }}</text>
              </view>
              <text class="message-preview">{{ message.content }}</text>
            </view>
            <view v-if="!message.read" class="unread-dot"></view>
          </view>
        </view>
      </view>

      <!-- 活动消息 -->
      <view v-if="currentTab === 2" class="tab-content">
        <view v-if="activityMessages.length === 0" class="empty-state">
          <image class="empty-icon" :src="getMessageImageUrl('activity.png')" mode="aspectFit" @error="handleImageError" @load="handleImageLoad"></image>
          <text class="empty-text">暂无活动消息</text>
        </view>
        <view v-else class="message-list-content">
          <view 
            v-for="message in activityMessages" 
            :key="message.id"
            class="message-item"
            @click="handleMessageClick(message)"
          >
            <image class="message-icon" :src="message.icon" mode="aspectFit" @error="handleImageError" @load="handleImageLoad"></image>
            <view class="message-content">
              <view class="message-header">
                <text class="message-title">{{ message.title }}</text>
                <text class="message-time">{{ message.time }}</text>
              </view>
              <text class="message-preview">{{ message.content }}</text>
            </view>
            <view v-if="!message.read" class="unread-dot"></view>
          </view>
        </view>
      </view>

      <!-- 客服消息 -->
      <view v-if="currentTab === 3" class="tab-content">
        <view v-if="customerServiceMessages.length === 0" class="empty-state">
          <image class="empty-icon" :src="getMessageImageUrl('server.png')" mode="aspectFit" @error="handleImageError" @load="handleImageLoad"></image>
          <text class="empty-text">暂无客服消息</text>
          <view class="start-chat-btn" @click="startCustomerService">
            <text class="btn-text">发起客服咨询</text>
          </view>
        </view>
        <view v-else class="message-list-content">
          <view 
            v-for="message in customerServiceMessages" 
            :key="message.id"
            class="message-item"
            @click="handleMessageClick(message)"
          >
            <!-- 商品图片或客服图标 -->
            <view class="message-avatar">
              <image 
                v-if="message.productImage" 
                class="product-avatar" 
                :src="message.productImage" 
                mode="aspectFill"
                @error="handleAvatarError(message)"
              />
              <view v-else class="service-avatar">
                <text class="service-avatar-text">客服</text>
              </view>
            </view>
            <view class="message-content">
              <view class="message-header">
                <text class="message-title">{{ message.title }}</text>
                <text class="message-time">{{ message.time }}</text>
              </view>
              <text class="message-preview">{{ message.content }}</text>
            </view>
            <view v-if="!message.read" class="unread-dot"></view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import env from '../../config/env.js'
import { customerServiceApi } from '../../api/customerService.js'
import { store } from '../../store.js'

// 图片URL处理函数，参考productDetail页面的实现
const getImageUrl = (path) => {
  if (!path) return '/static/default-product.png';
  
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path;
  }
  
  // 使用环境配置中的baseUrl
  const config = env.getConfig();
  return `${config.baseUrl}/${path}`;
};

// 获取消息图片URL的专用函数
const getMessageImageUrl = (imageName) => {
  return getImageUrl(`static/messages/${imageName}`);
};

// 处理图片加载错误
const handleImageError = (e) => {
  console.error('Image failed to load:', e.detail.src);
  // 当图片加载失败时，隐藏图片元素
  const target = e.target;
  if (target) {
    target.style.display = 'none';
  }
};

// 处理图片加载成功
const handleImageLoad = (e) => {
  console.log('Image loaded successfully:', e.detail.src);
};

// 处理客服头像加载错误
const handleAvatarError = (message) => {
  console.error('Avatar failed to load:', message.productImage);
  // 当头像加载失败时，显示客服图标
  message.productImage = null; // 清空图片URL，显示客服图标
};

// 响应式数据
const currentTab = ref(0)
const systemMessages = ref([])
const orderMessages = ref([])
const activityMessages = ref([])
const customerServiceMessages = ref([])
const scrollTop = ref(0)

// 计算当前标签页的消息数量
const getCurrentMessageCount = () => {
  switch (currentTab.value) {
    case 0: return systemMessages.value.length
    case 1: return orderMessages.value.length
    case 2: return activityMessages.value.length
    case 3: return customerServiceMessages.value.length
    default: return 0
  }
}

// 标签页配置
const tabs = ref([
  { label: '系统', icon: getMessageImageUrl('system.png'), unread: 0 },
  { label: '订单', icon: getMessageImageUrl('express.png'), unread: 0 },
  { label: '活动', icon: getMessageImageUrl('activity.png'), unread: 0 },
  { label: '客服', icon: getMessageImageUrl('server.png'), unread: 0 }
])

// 切换标签页
const switchTab = (index) => {
  currentTab.value = index
  scrollTop.value = 0 // 切换标签时重置滚动位置
  loadMessages()
}

// 加载消息数据
const loadMessages = async () => {
  // 这里可以根据实际需求调用API
  // 目前使用空数据
  systemMessages.value = []
  orderMessages.value = []
  activityMessages.value = []
  
  // 加载客服消息
  await loadCustomerServiceMessages()
  
  updateUnreadCount()
}

// 加载客服消息
const loadCustomerServiceMessages = async () => {
  try {
    const userInfo = store.getUserInfo()
    if (!userInfo || !userInfo.id) {
      customerServiceMessages.value = []
      return
    }
    
    const userId = userInfo.id
    
    // 获取用户的客服会话列表
    const response = await customerServiceApi.getUserSessions(userId)
    
    if (response.code === 200 && response.data) {
      // 将会话转换为消息格式，按商品分组
      const sessions = response.data
      const sessionMap = new Map() // 用于按商品ID分组
      
      sessions.forEach(session => {
        const productId = session.productId
        const key = productId ? `product_${productId}` : 'general'
        
        if (!sessionMap.has(key)) {
          sessionMap.set(key, {
            id: session.id,
            title: session.productId ? `商品咨询` : '客服咨询',
            content: session.productId ? `关于商品 ${session.productId} 的咨询` : '一般客服咨询',
            time: new Date(session.createTime).toLocaleString('zh-CN'),
            read: false, // 这里可以根据实际逻辑判断是否已读
            icon: getMessageImageUrl('server.png'),
            sessionId: session.id,
            productId: session.productId,
            lastMessageTime: session.lastMessageTime,
            unreadCount: 0, // 这里可以获取未读消息数
            productImage: null // 新增商品图片URL
          })
        }
      })
      
      // 转换为数组并按最后消息时间排序
      customerServiceMessages.value = Array.from(sessionMap.values())
        .sort((a, b) => {
          const timeA = a.lastMessageTime ? new Date(a.lastMessageTime).getTime() : 0
          const timeB = b.lastMessageTime ? new Date(b.lastMessageTime).getTime() : 0
          return timeB - timeA
        })
      
      // 获取商品信息来显示商品名称
      await loadProductInfo()
      
      console.log('客服会话加载成功:', customerServiceMessages.value)
    } else {
      console.error('加载客服会话失败:', response.message)
      customerServiceMessages.value = []
    }
  } catch (error) {
    console.error('加载客服消息错误:', error)
    customerServiceMessages.value = []
  }
}

// 获取商品信息
const loadProductInfo = async () => {
  try {
    // 获取所有需要查询商品信息的会话
    const sessionsWithProduct = customerServiceMessages.value.filter(msg => msg.productId)
    
    for (const message of sessionsWithProduct) {
      try {
        // 调用商品API获取商品信息
        const productResponse = await uni.request({
          url: `${env.getConfig().baseUrl}/api/products/${message.productId}`,
          method: 'GET',
          header: {
            'Content-Type': 'application/json'
          }
        })
        
        if (productResponse.statusCode === 200 && productResponse.data.code === 200) {
          const product = productResponse.data.data
          message.title = `商品咨询：${product.productName}`
          message.content = `关于商品 ${product.productName} 的咨询`
          
          // 设置商品图片URL
          if (product.mainImageUrl) {
            // 如果是完整URL，直接使用
            if (product.mainImageUrl.startsWith('http://') || product.mainImageUrl.startsWith('https://')) {
              message.productImage = product.mainImageUrl
            } else {
              // 如果是相对路径，拼接baseUrl
              message.productImage = `${env.getConfig().baseUrl}/${product.mainImageUrl}`
            }
          } else {
            message.productImage = null
          }
        }
      } catch (error) {
        console.error(`获取商品 ${message.productId} 信息失败:`, error)
        // 保持默认显示
        message.productImage = null
      }
    }
  } catch (error) {
    console.error('获取商品信息错误:', error)
  }
}

// 更新未读消息数
const updateUnreadCount = () => {
  tabs.value[0].unread = systemMessages.value.filter(m => !m.read).length
  tabs.value[1].unread = orderMessages.value.filter(m => !m.read).length
  tabs.value[2].unread = activityMessages.value.filter(m => !m.read).length
  tabs.value[3].unread = customerServiceMessages.value.filter(m => !m.read).length
}

// 标记所有已读
const markAllRead = () => {
  systemMessages.value.forEach(m => m.read = true)
  orderMessages.value.forEach(m => m.read = true)
  activityMessages.value.forEach(m => m.read = true)
  customerServiceMessages.value.forEach(m => m.read = true)
  updateUnreadCount()
  
  uni.showToast({
    title: '已标记全部已读',
    icon: 'success'
  })
}

// 处理消息点击
const handleMessageClick = (message) => {
  message.read = true
  updateUnreadCount()
  
  // 根据消息类型跳转到相应页面
  if (currentTab.value === 3) {
    // 客服消息跳转到聊天页面
    const params = {
      sessionId: message.sessionId
    }
    
    // 如果有商品ID，也传递过去
    if (message.productId) {
      params.productId = message.productId
    }
    
    const queryString = Object.keys(params)
      .map(key => `${key}=${encodeURIComponent(params[key])}`)
      .join('&')
    
    uni.navigateTo({
      url: `/pages/messages/customerServiceChat/customerServiceChat?${queryString}`
    })
  } else {
    // 其他消息显示详情
    uni.showToast({
      title: '消息详情功能开发中',
      icon: 'none'
    })
  }
}

// 发起客服咨询
const startCustomerService = async () => {
  try {
    // 检查用户登录状态
    const userInfo = store.getUserInfo()
    if (!userInfo || !userInfo.id) {
      uni.showModal({
        title: '提示',
        content: '请先登录后再使用客服功能',
        showCancel: false
      })
      return
    }
    
    const userId = userInfo.id
    
    // 调用后端API创建会话
    const response = await customerServiceApi.startCustomerService(userId, {
      productId: null,
      orderId: null,
      sessionType: 4 // 其他类型
    })
    
    if (response.code === 200) {
      const sessionId = response.data.id
      console.log('客服会话创建成功:', response.data)
      
      // 跳转到聊天页面
      uni.navigateTo({
        url: `/pages/messages/customerServiceChat/customerServiceChat?sessionId=${sessionId}`
      })
    } else {
      console.error('创建客服会话失败:', response.message)
      uni.showToast({
        title: '创建会话失败',
        icon: 'error'
      })
    }
  } catch (error) {
    console.error('发起客服咨询错误:', error)
    uni.showToast({
      title: '网络错误',
      icon: 'error'
    })
  }
}

// 页面加载时初始化
onMounted(async () => {
  await loadMessages()
  
  // 调试：检查图片URL
  console.log('Environment config:', env.getConfig())
  console.log('System image URL:', getMessageImageUrl('system.png'))
  console.log('Express image URL:', getMessageImageUrl('express.png'))
  console.log('Activity image URL:', getMessageImageUrl('activity.png'))
  console.log('Server image URL:', getMessageImageUrl('server.png'))
  console.log('Clear image URL:', getMessageImageUrl('clear.png'))
})

// 页面显示时刷新消息
const onShow = () => {
  loadMessages()
}

// 暴露onShow方法给页面
defineExpose({
  onShow
})
</script>

<style scoped>
/* 隐藏滚动条 */
::-webkit-scrollbar {
  display: none;
}

.messages-page {
  height: 100vh;
  width: 100vw;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 防止整体滚动 */
  position: relative;
  box-sizing: border-box;
}

/* 固定头部 */
.fixed-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 30rpx;
  background-color: #ffffff;
  border-bottom: 1rpx solid #e0e0e0;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  width: 100%;
  z-index: 1000;
  height: 120rpx; /* 增加高度 */
  box-sizing: border-box;
}

.header-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333333;
  box-sizing: border-box;
}

.header-actions {
  display: flex;
  align-items: center;
  box-sizing: border-box;
}

.action-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8f8f8;
  border-radius: 50%;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.action-btn:active {
  background-color: #e0e0e0;
  transform: scale(0.95);
}

.action-icon {
  width: 32rpx;
  height: 32rpx;
  min-height: 32rpx;
  object-fit: contain;
  display: block;
  box-sizing: border-box;
}

/* 固定标签页 */
.fixed-tabs {
  display: flex;
  background-color: #ffffff;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #e0e0e0;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
  position: fixed;
  top: 120rpx; /* 调整位置 */
  left: 0;
  right: 0;
  width: 100%;
  z-index: 999;
  height: 120rpx; /* 增加高度 */
  box-sizing: border-box;
  justify-content: center;
  align-items: center;
}

.tab-item {
  flex: 0 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  padding: 20rpx 30rpx;
  transition: all 0.3s ease;
  box-sizing: border-box;
  margin: 0 30rpx;
}

.tab-item:active {
  transform: scale(0.95);
}

.tab-icon {
  width: 56rpx;
  height: 56rpx;
  min-height: 56rpx;
  margin-bottom: 10rpx;
  opacity: 0.6;
  transition: all 0.3s ease;
  object-fit: contain;
  display: block;
  box-sizing: border-box;
}

.tab-label {
  font-size: 24rpx;
  color: #666666;
  font-weight: 500;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.tab-item.active .tab-icon {
  opacity: 1;
  transform: scale(1.1);
}

.tab-item.active .tab-label {
  color: #4CAF50;
  font-weight: 600;
}

.unread-badge {
  position: absolute;
  top: 25rpx;
  right: 50%;
  transform: translateX(50%);
  background-color: #ff4444;
  color: #ffffff;
  font-size: 20rpx;
  padding: 4rpx 8rpx;
  border-radius: 20rpx;
  min-width: 32rpx;
  text-align: center;
  font-weight: 600;
  box-sizing: border-box;
  z-index: 10;
}

/* 固定内容区域 */
.fixed-content {
  flex: 1;
  margin-top: 240rpx; /* 调整间距 */
  height: calc(100vh - 240rpx); /* 调整高度 */
  width: 100%;
  overflow: hidden; /* 防止内容区域滚动 */
  position: relative;
  box-sizing: border-box;
}

.tab-content {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 20rpx;
  box-sizing: border-box;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: transparent;
  width: 100%;
  max-width: 100%;
  text-align: center;
  box-sizing: border-box;
}

.empty-icon {
  width: 160rpx;
  height: 160rpx;
  min-height: 160rpx;
  margin-bottom: 30rpx;
  opacity: 0.6;
  object-fit: contain;
  display: block;
  box-sizing: border-box;
}

.empty-text {
  font-size: 28rpx;
  color: #999999;
  margin-bottom: 40rpx;
  font-weight: 500;
  text-align: center;
  box-sizing: border-box;
}

.start-chat-btn {
  background: linear-gradient(90deg, #4CAF50 60%, #43a047 100%);
  color: #ffffff;
  padding: 24rpx 48rpx;
  border-radius: 32rpx;
  box-shadow: 0 4rpx 12rpx rgba(76, 175, 80, 0.12);
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.start-chat-btn:active {
  background: #388e3c;
  transform: scale(0.95);
  box-shadow: 0 1rpx 4rpx rgba(76, 175, 80, 0.12);
}

.btn-text {
  font-size: 28rpx;
  font-weight: 600;
  box-sizing: border-box;
}

/* 消息列表内容 */
.message-list-content {
  width: 100%;
  max-width: 100%;
  height: 100%;
  overflow-y: auto; /* 只有消息列表可以滚动 */
  padding: 20rpx;
  padding-bottom: 40rpx;
  box-sizing: border-box;
}

.message-item {
  display: flex;
  align-items: center;
  padding: 30rpx;
  background-color: #ffffff;
  margin-bottom: 20rpx;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border: 1rpx solid #f0f0f0;
  box-sizing: border-box;
}

.message-item:active {
  transform: scale(0.98);
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.12);
}

.message-icon {
  width: 100rpx;
  height: 100rpx;
  min-height: 100rpx;
  border-radius: 16rpx;
  margin-right: 20rpx;
  background-color: #f8f8f8;
  padding: 10rpx;
  object-fit: contain;
  display: block;
  box-sizing: border-box;
}

.message-content {
  flex: 1;
  min-width: 0;
  box-sizing: border-box;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10rpx;
  box-sizing: border-box;
}

.message-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #333333;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  box-sizing: border-box;
}

.message-time {
  font-size: 24rpx;
  color: #999999;
  margin-left: 20rpx;
  box-sizing: border-box;
}

.message-preview {
  font-size: 26rpx;
  color: #666666;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  box-sizing: border-box;
}

.unread-dot {
  width: 16rpx;
  height: 16rpx;
  background-color: #ff4444;
  border-radius: 50%;
  margin-left: 20rpx;
  box-sizing: border-box;
}

/* 客服消息头像样式 */
.message-avatar {
  width: 80rpx;
  height: 80rpx;
  min-height: 80rpx;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 20rpx;
  background-color: #f8f8f8;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
}

.product-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  box-sizing: border-box;
}

.service-avatar {
  width: 100%;
  height: 100%;
  background-color: #4CAF50;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
}

.service-avatar-text {
  color: #ffffff;
  font-size: 36rpx;
  font-weight: 600;
  box-sizing: border-box;
}

/* 响应式设计 */
@media (max-width: 750rpx) {
  .fixed-header {
    padding: 20rpx 20rpx;
    height: 100rpx;
  }
  
  .header-title {
    font-size: 32rpx;
  }
  
  .fixed-tabs {
    padding: 15rpx 0;
    height: 100rpx;
    top: 100rpx;
  }
  
  .tab-item {
    padding: 15rpx 0;
  }
  
  .tab-icon {
    width: 52rpx;
    height: 52rpx;
    min-height: 52rpx;
    object-fit: contain;
    display: block;
  }
  
  .tab-label {
    font-size: 22rpx;
  }
  
  .fixed-content {
    margin-top: 200rpx;
    height: calc(100vh - 200rpx);
  }
  
  .message-list-content {
    padding: 15rpx;
  }
  
  .message-item {
    padding: 25rpx;
    margin-bottom: 15rpx;
  }
  
  .message-icon {
    width: 80rpx;
    height: 80rpx;
    min-height: 80rpx;
    margin-right: 15rpx;
    object-fit: contain;
    display: block;
  }
  
  .message-title {
    font-size: 26rpx;
  }
  
  .message-preview {
    font-size: 24rpx;
  }
}
</style>
