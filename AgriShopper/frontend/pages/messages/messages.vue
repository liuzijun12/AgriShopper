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
            <view class="message-icon-container">
              <image class="message-icon" :src="message.icon" mode="aspectFit" @error="handleImageError" @load="handleImageLoad"></image>
            </view>
            <view class="message-content">
              <view class="message-header">
                <text class="message-title">{{ message.title }}</text>
                <text class="message-time">{{ formatMessageTime(message.time) }}</text>
              </view>
              <text class="message-preview">{{ message.content }}</text>
            </view>
          </view>
          <!-- 未读消息统计 -->
          <view v-if="tabs[0].unread > 0" class="unread-summary">
            <view class="unread-summary-content">
              <text class="unread-summary-text">未读: {{ tabs[0].unread }}</text>
              <text class="mark-all-read-btn" @click="markAllRead">标记全部已读</text>
            </view>
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
            <view class="message-icon-container">
              <image class="message-icon" :src="message.icon" mode="aspectFit" @error="handleImageError" @load="handleImageLoad"></image>
            </view>
            <view class="message-content">
              <view class="message-header">
                <text class="message-title">{{ message.title }}</text>
                <text class="message-time">{{ formatMessageTime(message.time) }}</text>
              </view>
              <text class="message-preview">{{ message.content }}</text>
            </view>
          </view>
          <!-- 未读消息统计 -->
          <view v-if="tabs[1].unread > 0" class="unread-summary">
            <view class="unread-summary-content">
              <text class="unread-summary-text">未读: {{ tabs[1].unread }}</text>
              <text class="mark-all-read-btn" @click="markAllRead">标记全部已读</text>
            </view>
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
            <view class="message-icon-container">
              <image class="message-icon" :src="message.icon" mode="aspectFit" @error="handleImageError" @load="handleImageLoad"></image>
            </view>
            <view class="message-content">
              <view class="message-header">
                <text class="message-title">{{ message.title }}</text>
                <text class="message-time">{{ formatMessageTime(message.time) }}</text>
              </view>
              <text class="message-preview">{{ message.content }}</text>
            </view>
          </view>
          <!-- 未读消息统计 -->
          <view v-if="tabs[2].unread > 0" class="unread-summary">
            <view class="unread-summary-content">
              <text class="unread-summary-text">未读: {{ tabs[2].unread }}</text>
              <text class="mark-all-read-btn" @click="markAllRead">标记全部已读</text>
            </view>
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
            <!-- 红点指示器 - 放在时间下方 -->
            <view v-if="message.unreadCount > 0" class="unread-dot-qq">
              <text class="unread-number-qq">{{ message.unreadCount > 99 ? '99+' : message.unreadCount }}</text>
            </view>
            <view class="message-content">
              <view class="message-header">
                <text class="message-title">{{ message.title }}</text>
                <text class="message-time">{{ formatMessageTime(message.lastMessageTime || message.time) }}</text>
              </view>
              <text class="message-preview">{{ message.content }}</text>
            </view>
          </view>
          <!-- 未读消息统计 -->
          <view v-if="tabs[3].unread > 0" class="unread-summary">
            <view class="unread-summary-content">
              <text class="unread-summary-text">未读: {{ tabs[3].unread }}</text>
              <text class="mark-all-read-btn" @click="markAllRead">标记全部已读</text>
            </view>
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

// 图片URL处理函数
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

// 格式化时间显示
const formatMessageTime = (timeString) => {
  if (!timeString) return '';
  
  try {
    const messageTime = new Date(timeString);
    const now = new Date();
    
    // 检查是否是今天
    const isToday = messageTime.toDateString() === now.toDateString();
    
    if (isToday) {
      // 今天：显示小时和分钟
      const hours = messageTime.getHours().toString().padStart(2, '0');
      const minutes = messageTime.getMinutes().toString().padStart(2, '0');
      return `${hours}:${minutes}`;
    } else {
      // 不是今天：显示月份和日期
      const month = (messageTime.getMonth() + 1).toString();
      const day = messageTime.getDate().toString();
      return `${month}/${day}`;
    }
  } catch (error) {
    console.error('时间格式化错误:', error);
    return '';
  }
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
  
  // 添加测试数据（临时）
  if (customerServiceMessages.value.length === 0) {
    console.log('添加测试数据')
    customerServiceMessages.value = [
      {
        id: 'test1',
        type: 'customerService',
        title: '测试客服咨询',
        content: '您好，请问有什么可以帮助您的吗？',
        time: new Date().toISOString(), // 使用ISO格式，让格式化函数处理
        read: false,
        icon: getMessageImageUrl('server.png'),
        sessionId: 'test1',
        productId: null,
        lastMessageTime: new Date().toISOString(),
        unreadCount: 2, // 测试未读消息数
        hasRedDot: true, // 测试红点显示
        productImage: null
      }
    ]
  }
  
  updateUnreadCount()
}

// 加载客服消息
const loadCustomerServiceMessages = async () => {
  try {
    const userInfo = store.getUserInfo()
    console.log('当前用户信息:', userInfo)
    
    if (!userInfo || !userInfo.id) {
      console.log('用户未登录，清空客服消息列表')
      customerServiceMessages.value = []
      return
    }
    
    const userId = userInfo.id
    console.log('开始加载用户ID为', userId, '的客服消息')
    
    // 1. 获取用户会话列表
    const response = await customerServiceApi.getUserSessions(userId)
    
    if (response.code === 200) {
      const sessions = response.data || []
      // 2. 遍历每个会话，获取最后一条消息和未读消息数
      for (const session of sessions) {
        try {
          // 获取非用户未读消息数（客服、AI、系统）
          const unreadResponse = await customerServiceApi.countSessionUnreadNonUserMessages(session.id)
          
          if (unreadResponse.code === 200) {
            session.unreadCount = unreadResponse.data || 0
            session.hasRedDot = session.unreadCount > 0
          } else {
            console.error(`❌ 获取会话 ${session.id} 未读消息数失败:`, unreadResponse)
            session.unreadCount = 0
            session.hasRedDot = false
          }
          
          // 获取会话的最后一条消息作为预览内容
          await loadLastMessage(session)
          
          // 获取商品信息（如果有商品ID）
          if (session.productId) {
            await loadProductInfo(session)
          }
          
        } catch (error) {
          console.error(`❌ 处理会话 ${session.id} 失败:`, error)
          session.unreadCount = 0
          session.hasRedDot = false
        }
      }
      
      // 4. 将会话转换为消息格式，按商品分组
      const sessionMap = new Map() // 用于按商品ID分组
      
      sessions.forEach(session => {
        const productId = session.productId
        const key = productId ? `product_${productId}` : 'general'
        
        if (!sessionMap.has(key)) {
          const messageContent = session.lastMessageContent || (session.productId ? `关于商品 ${session.productId} 的咨询` : '一般客服咨询')
          
          sessionMap.set(key, {
            id: session.id,
            type: 'customerService', // 添加类型字段
            title: session.title || (session.productId ? `商品咨询` : '客服咨询'),
            content: messageContent,
            time: session.createTime, // 使用原始时间字符串，让格式化函数处理
            read: session.unreadCount === 0, // 根据未读消息数判断是否已读
            icon: getMessageImageUrl('server.png'),
            sessionId: session.id,
            productId: session.productId,
            lastMessageTime: session.lastMessageTime,
            unreadCount: session.unreadCount, // 使用实际的未读消息数
            hasRedDot: session.hasRedDot, // 使用实际的红点状态
            productImage: session.productImage || null
          })
        }
      })
      
      // 5. 转换为数组并按最后消息时间排序
      customerServiceMessages.value = Array.from(sessionMap.values())
        .sort((a, b) => {
          const timeA = a.lastMessageTime ? new Date(a.lastMessageTime).getTime() : 0
          const timeB = b.lastMessageTime ? new Date(b.lastMessageTime).getTime() : 0
          return timeB - timeA
        })
      
    } else {
      console.error('获取会话列表失败:', response.message)
      customerServiceMessages.value = []
    }
    
  } catch (error) {
    console.error('加载客服消息错误:', error)
    customerServiceMessages.value = []
  }
}

// 获取会话的最后一条消息
const loadLastMessage = async (session) => {
  try {
    // 调用API获取会话的所有消息
    const response = await customerServiceApi.getSessionMessages(session.id)
    
    if (response.code === 200 && response.data && response.data.length > 0) {
      // 按时间排序，获取最后一条消息
      const messages = response.data.sort((a, b) => {
        return new Date(a.createTime) - new Date(b.createTime)
      })
      
      const lastMessage = messages[messages.length - 1]
      
      // 使用正确的字段名获取消息内容
      const messageContent = lastMessage.messageContent || lastMessage.content || lastMessage.message || lastMessage.text || lastMessage.body || lastMessage.detail || '暂无消息'
      session.lastMessageContent = messageContent
      session.lastMessageTime = lastMessage.createTime || session.createTime
    } else {
      // 如果没有消息，使用默认内容
      session.lastMessageContent = session.productId ? `关于商品 ${session.productId} 的咨询` : '一般客服咨询'
      session.lastMessageTime = session.createTime
    }
  } catch (error) {
    console.error(`获取会话 ${session.id} 最后消息失败:`, error)
    // 使用默认内容
    session.lastMessageContent = session.productId ? `关于商品 ${session.productId} 的咨询` : '一般客服咨询'
    session.lastMessageTime = session.createTime
  }
}

// 获取商品信息
const loadProductInfo = async (session) => {
  try {
    // 调用商品API获取商品信息
    const productResponse = await uni.request({
      url: `${env.getConfig().baseUrl}/api/products/${session.productId}`,
      method: 'GET',
      header: {
        'Content-Type': 'application/json'
      }
    })
    
    if (productResponse.statusCode === 200 && productResponse.data.code === 200) {
      const product = productResponse.data.data
      session.title = `${product.productName}-商品咨询`
      // 不要覆盖最后一条消息内容，只更新标题
      // session.content = `关于商品 ${product.productName} 的咨询`
      
      // 设置商品图片URL
      if (product.mainImageUrl) {
        // 如果是完整URL，直接使用
        if (product.mainImageUrl.startsWith('http://') || product.mainImageUrl.startsWith('https://')) {
          session.productImage = product.mainImageUrl
        } else {
          // 如果是相对路径，拼接baseUrl
          session.productImage = `${env.getConfig().baseUrl}/${product.mainImageUrl}`
        }
      } else {
        session.productImage = null
      }
    }
  } catch (error) {
    console.error(`获取商品 ${session.productId} 信息失败:`, error)
    // 保持默认显示
    session.productImage = null
    // 设置默认title
    session.title = `商品${session.productId}-商品咨询`
  }
}

// 更新未读消息数
const updateUnreadCount = () => {
  tabs.value[0].unread = systemMessages.value.filter(m => !m.read).length
  tabs.value[1].unread = orderMessages.value.filter(m => !m.read).length
  tabs.value[2].unread = activityMessages.value.filter(m => !m.read).length
  // 客服消息使用实际的未读消息数
  tabs.value[3].unread = customerServiceMessages.value.reduce((total, message) => {
    return total + (message.unreadCount || 0)
  }, 0)
  
}

// 标记所有已读
const markAllRead = async () => {
  try {
    // 标记系统、订单、活动消息为已读
    systemMessages.value.forEach(m => m.read = true)
    orderMessages.value.forEach(m => m.read = true)
    activityMessages.value.forEach(m => m.read = true)
    
    // 标记客服消息为已读
    customerServiceMessages.value.forEach(message => {
      message.unreadCount = 0
      message.hasRedDot = false
    })
    
    // 更新未读消息总数
    updateUnreadCount()
    
    uni.showToast({
      title: '已标记全部已读',
      icon: 'success'
    })
  } catch (error) {
    console.error('标记全部已读失败:', error)
    uni.showToast({
      title: '操作失败',
      icon: 'error'
    })
  }
}

// 点击消息
const handleMessageClick = (message) => {
  
  if (message.type === 'customerService') {
    // 客服消息，跳转到聊天页面
    const params = {
      sessionId: message.sessionId
    }
    
    // 如果有商品ID，也传递过去
    if (message.productId) {
      params.productId = message.productId
    }
    
    // 立即更新当前会话的红点状态
    const session = customerServiceMessages.value.find(s => s.sessionId === message.sessionId)
    if (session) {
      session.unreadCount = 0
      session.hasRedDot = false
    }
    
    // 更新未读消息总数
    updateUnreadCount()
    
    const queryString = Object.keys(params)
      .map(key => `${key}=${encodeURIComponent(params[key])}`)
      .join('&')
    
    uni.navigateTo({
      url: `/pages/messages/customerServiceChat/customerServiceChat?${queryString}`,
      success: () => {
        console.log('跳转成功')
      },
      fail: (error) => {
        console.error('跳转失败:', error)
        uni.showToast({
          title: '跳转失败',
          icon: 'error'
        })
      }
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
  padding: 40rpx 30rpx; /* 增加上下内边距，确保徽章不被截断 */
  border-bottom: 1rpx solid #e0e0e0;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
  position: fixed;
  top: 120rpx; /* 调整位置 */
  left: 0;
  right: 0;
  width: 100%;
  z-index: 999;
  height: 180rpx; /* 增加高度，确保有足够空间显示徽章 */
  box-sizing: border-box;
  justify-content: space-between; /* 改为space-between，让标签项均匀分布 */
  align-items: center;
}

.tab-item {
  flex: 1; /* 让每个标签项平均分配空间 */
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 20rpx 10rpx; /* 调整内边距 */
  transition: all 0.3s ease;
  box-sizing: border-box;
  margin: 0 15rpx; /* 减少外边距，让space-between控制间距 */
  min-height: 100rpx; /* 确保最小高度 */
  max-width: 160rpx; /* 限制最大宽度，避免过宽 */
}

.tab-item:active {
  transform: scale(0.95);
}

.tab-icon {
  width: 64rpx;
  height: 64rpx;
  min-height: 64rpx;
  margin-bottom: 15rpx;
  opacity: 0.6;
  transition: all 0.3s ease;
  object-fit: contain;
  display: block;
  box-sizing: border-box;
}

.tab-label {
  font-size: 26rpx;
  color: #666666;
  font-weight: 500;
  transition: all 0.3s ease;
  box-sizing: border-box;
  line-height: 1.2;
}

.tab-item.active .tab-icon {
  opacity: 1;
  transform: scale(1.1);
}

.tab-item.active .tab-label {
  color: #4CAF50;
  font-weight: 600;
}

/* 未读消息数字样式 */
.unread-badge {
  position: absolute;
  top: 5rpx; /* 调整位置，确保不被截断 */
  right: 50%; /* 相对于标签项居中 */
  transform: translateX(50%); /* 水平居中 */
  min-width: 40rpx;
  height: 40rpx;
  background: linear-gradient(135deg, #ff4444 0%, #ff6666 100%);
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  padding: 0 8rpx;
  box-sizing: border-box;
  z-index: 10;
  box-shadow: 0 4rpx 12rpx rgba(255, 68, 68, 0.4);
  border: 2rpx solid #ffffff;
  animation: pulse 2s infinite;
}

.unread-number {
  color: #ffffff;
  font-size: 24rpx;
  font-weight: 700;
  line-height: 1;
  text-align: center;
  box-sizing: border-box;
  text-shadow: 0 1rpx 2rpx rgba(0, 0, 0, 0.3);
}

/* 徽章脉冲动画 */
@keyframes pulse {
  0% {
    transform: scale(1);
    box-shadow: 0 4rpx 12rpx rgba(255, 68, 68, 0.4);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 6rpx 16rpx rgba(255, 68, 68, 0.6);
  }
  100% {
    transform: scale(1);
    box-shadow: 0 4rpx 12rpx rgba(255, 68, 68, 0.4);
  }
}

/* 固定内容区域 */
.fixed-content {
  flex: 1;
  margin-top: 300rpx; /* 调整间距，适应新的标签页高度 */
  height: calc(100vh - 300rpx); /* 调整高度，适应新的标签页高度 */
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
  position: relative; /* 添加相对定位 */
}

.message-item:active {
  transform: scale(0.98);
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.12);
}

.message-icon-container {
  position: relative;
  width: 100rpx;
  height: 100rpx;
  min-height: 100rpx;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 20rpx;
  background-color: #f8f8f8;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
}

.message-icon {
  width: 100%;
  height: 100%;
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



/* 客服消息头像样式 */
.message-avatar {
  width: 80rpx;
  height: 80rpx;
  min-height: 80rpx;
  border-radius: 50%;
  margin-right: 20rpx;
  background-color: #f8f8f8;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  position: relative; /* 添加相对定位 */
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
  position: relative; /* 添加相对定位 */
}

.service-avatar-text {
  color: #ffffff;
  font-size: 36rpx;
  font-weight: 600;
  box-sizing: border-box;
}

/* 未读消息统计样式 */
.unread-summary {
  width: 100%;
  padding: 20rpx;
  background-color: #f8f8f8;
  border-top: 1rpx solid #e0e0e0;
  box-sizing: border-box;
}

.unread-summary-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-sizing: border-box;
}

.unread-summary-text {
  font-size: 26rpx;
  color: #ff4444;
  font-weight: 600;
  box-sizing: border-box;
}

.mark-all-read-btn {
  font-size: 24rpx;
  color: #4CAF50;
  text-decoration: underline;
  padding: 10rpx 15rpx;
  border-radius: 8rpx;
  background-color: rgba(76, 175, 80, 0.1);
  box-sizing: border-box;
}

/* 红点指示器样式 */
.unread-dot {
  position: absolute;
  top: 50rpx;
  right: 30rpx;
  width: 16rpx;
  height: 16rpx;
  background-color: #ff4444;
  border-radius: 50%;
  z-index: 10;
  box-sizing: border-box;
}

/* QQ风格红点指示器样式 - 放在时间下方 */
.unread-dot-qq {
  position: absolute;
  top: 80rpx; /* 放在时间下方 */
  right: 30rpx;
  min-width: 32rpx;
  height: 32rpx;
  background-color: #ff4444;
  border-radius: 16rpx;
  z-index: 10;
  box-sizing: border-box;
  box-shadow: 0 2rpx 4rpx rgba(255, 68, 68, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 8rpx;
  border: 2rpx solid #ffffff;
}

.unread-number-qq {
  color: #ffffff;
  font-size: 20rpx;
  font-weight: 600;
  line-height: 1;
  text-align: center;
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
    padding: 25rpx 20rpx; /* 调整小屏幕下的内边距 */
    height: 160rpx; /* 增加小屏幕下的高度 */
    top: 100rpx;
  }
  
  .tab-item {
    padding: 15rpx 8rpx; /* 调整小屏幕下的内边距 */
    min-height: 90rpx; /* 确保小屏幕下的最小高度 */
    margin: 0 10rpx; /* 调整小屏幕下的外边距 */
    max-width: 140rpx; /* 限制小屏幕下的最大宽度 */
  }
  
  .tab-icon {
    width: 56rpx;
    height: 56rpx;
    min-height: 56rpx;
    margin-bottom: 12rpx;
    object-fit: contain;
    display: block;
  }
  
  .tab-label {
    font-size: 24rpx;
  }
  
  .unread-badge {
    top: 3rpx; /* 调整小屏幕下的位置 */
    right: 50%; /* 相对于标签项居中 */
    transform: translateX(50%); /* 水平居中 */
    min-width: 36rpx;
    height: 36rpx;
    border-radius: 18rpx;
    border: 2rpx solid #ffffff;
  }
  
  .unread-number {
    font-size: 22rpx;
    font-weight: 700;
  }
  
  .fixed-content {
    margin-top: 260rpx; /* 调整小屏幕下的间距 */
    height: calc(100vh - 260rpx);
  }
  
  .message-list-content {
    padding: 15rpx;
  }
  
  .message-item {
    padding: 25rpx;
    margin-bottom: 15rpx;
  }
  
  .message-icon-container {
    width: 80rpx;
    height: 80rpx;
    min-height: 80rpx;
    margin-right: 15rpx;
  }
  
  .message-icon {
    width: 100%;
    height: 100%;
    object-fit: contain;
    display: block;
  }
  
  .message-title {
    font-size: 26rpx;
  }
  
  .message-preview {
    font-size: 24rpx;
  }
  
  .unread-dot-qq {
    top: 70rpx; /* 小屏幕下调整位置 */
    right: 25rpx;
    min-width: 28rpx;
    height: 28rpx;
    border-radius: 14rpx;
    padding: 0 6rpx;
  }
  
  .unread-number-qq {
    font-size: 18rpx;
  }
}
</style>
