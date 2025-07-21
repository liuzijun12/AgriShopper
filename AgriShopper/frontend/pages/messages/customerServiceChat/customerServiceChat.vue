<template>
  <!-- 顶部导航栏 -->
  <view class="chat-header">
    <view class="header-content">
      <!-- 商品图片或客服图标 -->
      <view class="header-avatar">
        <image 
          v-if="productImage" 
          class="product-image" 
          :src="productImage" 
          mode="aspectFill"
          @error="handleImageError"
        />
        <view v-else class="service-icon">
          <text class="service-text">客服</text>
        </view>
      </view>
      
      <view class="header-info">
        <text class="chat-title">{{ chatTitle }}</text>
        <text class="chat-status">{{ chatStatus }}</text>
      </view>
    </view>
  </view>

  <!-- 聊天消息列表 -->
  <scroll-view class="chat-messages" scroll-y :scroll-top="scrollTop" scroll-with-animation>
    <view class="message-list">
      <view 
        v-for="message in messages" 
        :key="message.id"
        class="message-item"
        :class="{ 'message-user': message.type === 'user', 'message-agent': message.type === 'agent' }"
      >
        <view class="message-avatar">
          <text class="avatar-text">{{ message.type === 'user' ? '我' : '客服' }}</text>
        </view>
        <view class="message-content">
          <text class="message-text">{{ message.content }}</text>
          <text class="message-time">{{ message.time }}</text>
        </view>
      </view>
      
      <!-- 空状态 -->
      <view v-if="messages.length === 0" class="empty-state">
        <text class="empty-text">暂无消息，开始对话吧</text>
      </view>
    </view>
  </scroll-view>

  <!-- 底部输入区域 -->
  <view class="chat-input">
    <view class="input-container">
      <input 
        class="message-input"
        v-model="inputMessage"
        placeholder="请输入消息..."
        @confirm="sendMessage"
        confirm-type="send"
      />
      <view class="send-btn" @click="sendMessage" :class="{ 'active': inputMessage.trim() }">
        <text class="send-text">发送</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { customerServiceApi } from '../../../api/customerService.js'
import { store } from '../../../store.js'
import env from '../../../config/env.js'

// 响应式数据
const chatTitle = ref('客服咨询')
const chatStatus = ref('在线')
const messages = ref([])
const inputMessage = ref('')
const scrollTop = ref(0)
const currentSessionId = ref(null)
const currentUserId = ref(null)
const isLoading = ref(false)
const productImage = ref(null) // 新增：用于存储商品图片

// 返回上一页
const goBack = () => {
  uni.navigateBack()
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || isLoading.value) return
  
  // 检查用户登录状态
  if (!currentUserId.value) {
    uni.showToast({
      title: '请先登录',
      icon: 'error'
    })
    return
  }
  
  const messageContent = inputMessage.value.trim()
  inputMessage.value = ''
  
  // 添加用户消息到本地
  const userMessage = {
    id: Date.now(),
    type: 'user',
    content: messageContent,
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  
  messages.value.push(userMessage)
  
  // 滚动到底部
  nextTick(() => {
    scrollToBottom()
  })
  
  // 发送消息到后端
  try {
    isLoading.value = true
    
    if (!currentSessionId.value) {
      // 如果没有会话ID，先创建会话
      await createOrGetSession()
    }
    
    // 发送消息到后端
    const response = await customerServiceApi.sendUserMessage(currentUserId.value, {
      sessionId: currentSessionId.value,
      content: messageContent,
      messageType: 1 // 文本消息
    })
    
    if (response.code === 200) {
      console.log('消息发送成功:', response.data)
      
      // 重新加载消息列表，确保显示最新的消息
      await loadSessionMessages()
      
      // 注意：只在发送用户消息后标记已读，客服消息不应该标记为已读
      // await markMessagesAsRead()
    } else {
      console.error('消息发送失败:', response.message)
      uni.showToast({
        title: '发送失败',
        icon: 'error'
      })
    }
  } catch (error) {
    console.error('发送消息错误:', error)
    uni.showToast({
      title: '网络错误',
      icon: 'error'
    })
  } finally {
    isLoading.value = false
  }
}

// 加载会话消息
const loadSessionMessages = async () => {
  if (!currentSessionId.value) return
  
  try {
    const response = await customerServiceApi.getSessionMessages(currentSessionId.value)
    
    if (response.code === 200) {
      // 转换后端消息格式为前端格式
      const backendMessages = response.data
      messages.value = backendMessages.map(msg => ({
        id: msg.id,
        type: msg.senderType === 1 ? 'user' : (msg.senderType === 2 ? 'agent' : 'system'),
        content: msg.messageContent,
        time: new Date(msg.createTime).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
      }))
      
      nextTick(() => {
        scrollToBottom()
      })
      
      // 立即标记为已读，用户进入聊天页面就表示已查看
      await markMessagesAsRead()
    } else {
      console.error('加载消息失败:', response.message)
      messages.value = []
    }
  } catch (error) {
    console.error('加载消息错误:', error)
    messages.value = []
  }
}

// 创建或获取会话
const createOrGetSession = async (productId = null, productCode = null) => {
  if (!currentUserId.value) return
  
  try {
    const sessionData = {
      productId: productId ? parseInt(productId) : null,
      orderId: null,
      sessionType: productId ? 1 : 4 // 1-产品咨询，4-其他
    }
    
    const response = await customerServiceApi.startCustomerService(currentUserId.value, sessionData)
    
    if (response.code === 200) {
      currentSessionId.value = response.data.id
      console.log('会话创建/获取成功:', response.data)
      
      // 更新聊天标题
      if (productCode) {
        chatTitle.value = `咨询：${productCode}`
      }
    } else {
      console.error('会话创建/获取失败:', response.message)
      throw new Error(response.message)
    }
  } catch (error) {
    console.error('创建/获取会话错误:', error)
    uni.showToast({
      title: '创建会话失败，请重试',
      icon: 'error'
    })
    throw error
  }
}

// 标记消息已读
const markMessagesAsRead = async () => {
  if (!currentSessionId.value) return
  
  try {
    await customerServiceApi.markMessagesAsRead(currentSessionId.value)
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

// 滚动到底部
const scrollToBottom = () => {
  const query = uni.createSelectorQuery()
  query.select('.message-list').boundingClientRect()
  query.exec((res) => {
    if (res[0]) {
      scrollTop.value = res[0].height
    }
  })
}

// 获取当前用户ID
const getCurrentUserId = () => {
  const userInfo = store.getUserInfo()
  if (userInfo && userInfo.id) {
    return userInfo.id
  }
  
  // 如果没有用户信息，尝试从本地存储获取
  try {
    const storedUserInfo = uni.getStorageSync('userInfo')
    if (storedUserInfo && storedUserInfo.id) {
      return storedUserInfo.id
    }
  } catch (error) {
    console.error('获取本地用户信息失败:', error)
  }
  
  return null
}

// 检查用户登录状态
const checkUserLogin = () => {
  const userInfo = store.getUserInfo()
  if (!userInfo) {
    uni.showModal({
      title: '提示',
      content: '请先登录后再使用客服功能',
      showCancel: false,
      success: () => {
        uni.navigateBack()
      }
    })
    return false
  }
  return true
}

// 页面加载时初始化
onMounted(async () => {
  // 检查用户登录状态
  if (!checkUserLogin()) {
    return
  }
  
  currentUserId.value = getCurrentUserId()
  
  // 获取页面参数
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const options = currentPage.options || {}
  
  const sessionId = options.sessionId
  const productId = options.productId
  const productName = options.productName
  const productCode = options.productCode
  
  // 如果有产品信息，更新聊天标题
  if (productName) {
    chatTitle.value = `咨询：${decodeURIComponent(productName)}`
  } else if (productId) {
    // 如果没有产品名称但有产品ID，获取产品信息
    await loadProductInfo(productId)
  }
  
  if (sessionId) {
    // 如果有会话ID，加载现有会话的消息
    currentSessionId.value = parseInt(sessionId)
    await loadSessionMessages()
  } else {
    // 没有会话ID，创建或获取会话
    try {
      await createOrGetSession(productId, productCode)
      await loadSessionMessages()
    } catch (error) {
      console.error('创建/获取会话失败:', error)
    }
  }
  
  // 注意：移除这里的自动标记已读，改为在用户真正查看消息时标记
  // await markMessagesAsRead()
  
  nextTick(() => {
    scrollToBottom()
  })
})

// 获取商品信息
const loadProductInfo = async (productId) => {
  try {
    const response = await uni.request({
      url: `${env.getConfig().baseUrl}/api/products/${productId}`,
      method: 'GET',
      header: {
        'Content-Type': 'application/json'
      }
    })
    
    if (response.statusCode === 200 && response.data.code === 200) {
      const product = response.data.data
      chatTitle.value = `咨询：${product.productName}`
      
      // 设置商品图片
      if (product.mainImageUrl) {
        // 如果是完整URL，直接使用
        if (product.mainImageUrl.startsWith('http://') || product.mainImageUrl.startsWith('https://')) {
          productImage.value = product.mainImageUrl
        } else {
          // 如果是相对路径，拼接baseUrl
          productImage.value = `${env.getConfig().baseUrl}/${product.mainImageUrl}`
        }
      } else {
        productImage.value = null
      }
    }
  } catch (error) {
    console.error('获取商品信息失败:', error)
    chatTitle.value = `商品咨询`
    productImage.value = null
  }
}

// 处理图片加载错误
const handleImageError = () => {
  productImage.value = null // 加载失败时清除图片
}
</script>

<style scoped>
.chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
  box-sizing: border-box;
}

/* 顶部导航栏 */
.chat-header {
  background-color: #ffffff;
  border-bottom: 1rpx solid #e0e0e0;
  padding: 20rpx 30rpx;
  padding-top: calc(20rpx + env(safe-area-inset-top));
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  box-sizing: border-box;
}

.header-content {
  display: flex;
  align-items: center;
}

.header-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 20rpx;
  flex-shrink: 0;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.service-icon {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #4CAF50;
  border-radius: 50%;
}

.service-text {
  font-size: 28rpx;
  color: #ffffff;
  font-weight: 600;
}

.header-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
  line-height: 1.2;
}

.chat-status {
  font-size: 24rpx;
  color: #4CAF50;
  margin-top: 4rpx;
}

/* 聊天消息列表 */
.chat-messages {
  flex: 1;
  padding: 20rpx;
  margin-top: calc(100rpx + env(safe-area-inset-top));
  margin-bottom: calc(120rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
  height: calc(100vh - 220rpx - env(safe-area-inset-top) - env(safe-area-inset-bottom));
}

.message-list {
  display: flex;
  flex-direction: column;
  min-height: 100%;
}

.message-item {
  display: flex;
  margin-bottom: 30rpx;
  align-items: flex-start;
}

.message-user {
  flex-direction: row-reverse;
}

.message-agent {
  flex-direction: row;
}

.message-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 20rpx;
  flex-shrink: 0;
}

.message-user .message-avatar {
  background-color: #007aff;
}

.message-agent .message-avatar {
  background-color: #4CAF50;
}

.message-system .message-avatar {
  background-color: #FF9500;
}

.avatar-text {
  font-size: 28rpx;
  color: #ffffff;
  font-weight: 600;
}

.message-content {
  max-width: 60%;
  display: flex;
  flex-direction: column;
}

.message-user .message-content {
  align-items: flex-end;
}

.message-agent .message-content {
  align-items: flex-start;
}

.message-text {
  background-color: #ffffff;
  padding: 20rpx 24rpx;
  border-radius: 20rpx;
  font-size: 28rpx;
  color: #333333;
  line-height: 1.4;
  word-wrap: break-word;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.message-user .message-text {
  background-color: #007aff;
  color: #ffffff;
}

.message-time {
  font-size: 24rpx;
  color: #999999;
  margin-top: 8rpx;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.empty-text {
  font-size: 28rpx;
  color: #999999;
  text-align: center;
}

/* 底部输入区域 */
.chat-input {
  background-color: #ffffff;
  border-top: 1rpx solid #e0e0e0;
  padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 999;
  box-sizing: border-box;
}

.input-container {
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  border-radius: 40rpx;
  padding: 10rpx;
}

.message-input {
  flex: 1;
  height: 60rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  color: #333333;
  background-color: transparent;
}

.send-btn {
  width: 100rpx;
  height: 60rpx;
  background-color: #cccccc;
  border-radius: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s ease;
}

.send-btn.active {
  background-color: #007aff;
}

.send-text {
  font-size: 26rpx;
  color: #ffffff;
  font-weight: 600;
}
</style> 