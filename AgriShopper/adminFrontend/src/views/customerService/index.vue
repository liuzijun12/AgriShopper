<template>
  <div class="customer-service-container">
    <!-- 固定头部区域 -->
    <div class="fixed-header">
      <div class="page-header">
        <h1>客服管理</h1>
        <div class="header-actions">
          <el-button type="info" @click="refreshSessions" size="small">
            <el-icon><Refresh /></el-icon>刷新
          </el-button>
          <el-button type="primary" @click="handleAssignAll" size="small">
            <el-icon><UserFilled /></el-icon>批量分配
          </el-button>
        </div>
      </div>

      <!-- 搜索栏 -->
      <el-card class="search-card" shadow="never">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="用户ID">
            <el-input v-model="searchForm.userId" placeholder="请输入用户ID" clearable />
          </el-form-item>
          <el-form-item label="商品ID">
            <el-input v-model="searchForm.productId" placeholder="请输入商品ID" clearable />
          </el-form-item>
          <el-form-item label="会话状态">
            <el-select v-model="searchForm.sessionStatus" placeholder="请选择状态" clearable>
              <el-option label="进行中" :value="1" />
              <el-option label="已结束" :value="2" />
              <el-option label="已关闭" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>搜索
            </el-button>
            <el-button @click="resetSearch">
              <el-icon><Refresh /></el-icon>重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧会话列表 -->
      <div class="session-list-panel">
        <el-card class="session-list-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>会话列表</span>
              <el-tag type="info" size="small">{{ sessionList.length }}个会话</el-tag>
            </div>
          </template>
          
          <div class="session-list" v-loading="loading">
            <div 
              v-for="session in sessionList" 
              :key="session.id"
              class="session-item"
              :class="{ active: selectedSession?.id === session.id }"
              @click="selectSession(session)"
            >
              <div class="session-avatar">
                <img 
                  v-if="session.productImage" 
                  :src="session.productImage" 
                  :alt="session.title"
                />
                <div v-else class="default-avatar">
                  <Service />
                </div>
              </div>
              <div class="session-info">
                <div class="session-title">
                  {{ session.title }}
                  <span v-if="session.agentName" class="agent-name">({{ session.agentName }})</span>
                </div>
                <div class="session-message">{{ session.lastMessage }}</div>
                <div class="session-time">{{ formatTime(session.lastMessageTime) }}</div>
              </div>
              <div class="session-status">
                <!-- 调试信息：显示未读消息数 -->
                <div class="debug-info" style="font-size: 10px; color: #999;">
                  未读: {{ session.unreadCount }}
                </div>
                <!-- 红点指示器 -->
                <div v-if="session.unreadCount > 0" class="unread-indicator"></div>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧聊天界面 -->
      <div class="chat-panel">
        <el-card class="chat-card" shadow="never">
          <template #header>
            <div class="chat-header">
              <div class="chat-info">
                <span class="chat-title">{{ selectedSession?.title || '请选择会话' }}</span>
                <span class="chat-status">{{ selectedSession ? '在线' : '' }}</span>
              </div>
              <div class="chat-actions">
                <el-button 
                  v-if="selectedSession" 
                  type="danger" 
                  size="small" 
                  @click="endSession"
                >
                  结束会话
                </el-button>
              </div>
            </div>
          </template>
          
          <div class="chat-container">
            <!-- 聊天消息区域 -->
            <div class="chat-messages" ref="messagesContainer">
              <div v-if="!selectedSession" class="no-session">
                <el-empty description="请选择一个会话开始聊天" />
              </div>
              
              <div v-else class="message-list">
                <div
                  v-for="message in messageList"
                  :key="message.id"
                  class="message-item"
                  :class="{ 'message-user': message.senderType === 1, 'message-agent': message.senderType !== 1 }"
                >
                  <!-- 用户消息显示在左侧 -->
                  <template v-if="message.senderType === 1">
                    <div class="message-avatar">
                      <el-avatar :size="40" style="background-color: #67c23a;">
                        用户
                      </el-avatar>
                    </div>
                    <div class="message-content">
                      <div class="message-text message-user-text">{{ message.messageContent }}</div>
                      <div class="message-time">{{ formatTime(message.createTime) }}</div>
                    </div>
                  </template>
                  
                  <!-- 客服/AI/系统消息显示在右侧 -->
                  <template v-else>
                    <div class="message-content">
                      <div class="message-text message-agent-text">{{ message.messageContent }}</div>
                      <div class="message-time">{{ formatTime(message.createTime) }}</div>
                    </div>
                    <div class="message-avatar">
                      <el-avatar :size="40" style="background-color: #409eff;">
                        {{ getSenderName(message.senderType) }}
                      </el-avatar>
                    </div>
                  </template>
                </div>
              </div>
            </div>
            
            <!-- 输入区域 - 固定在底部 -->
            <div v-if="selectedSession" class="chat-input">
              <div class="input-container">
                <div class="input-row">
                  <el-input
                    v-model="inputMessage"
                    type="textarea"
                    :rows="2"
                    placeholder="请输入回复内容..."
                    @keydown.ctrl.enter="sendMessage"
                    class="message-input"
                  />
                  <div class="input-actions">
                    <el-button type="primary" @click="sendMessage" :loading="sending" size="default">
                      发送
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Service, UserFilled } from '@element-plus/icons-vue'
import { customerServiceApi } from '@/api/customerService'

// 响应式数据
const loading = ref(false)
const sending = ref(false)
const sessionList = ref([])
const selectedSession = ref(null)
const messageList = ref([])
const inputMessage = ref('')
const messagesContainer = ref(null)

// 搜索表单
const searchForm = ref({
  userId: '',
  productId: '',
  sessionStatus: ''
})

// 获取会话列表
const loadSessions = async () => {
  try {
    loading.value = true
    // 获取所有会话，而不是只获取未分配的
    const response = await customerServiceApi.getAllSessions({
      userId: searchForm.value.userId || undefined,
      productId: searchForm.value.productId || undefined,
      sessionStatus: searchForm.value.sessionStatus || undefined
    })
    
    if (response.code === 200) {
      console.log('获取到的会话数量:', response.data.length)
      console.log('会话列表:', response.data)
      
      sessionList.value = response.data.map(session => ({
        ...session,
        title: session.productId ? `商品咨询 (ID: ${session.productId})` : '一般客服咨询',
        lastMessage: '暂无消息',
        unreadCount: 0,
        productImage: null,
        agentName: null
      }))
      
      console.log('处理后的会话数量:', sessionList.value.length)
      
      // 获取商品信息、未读消息数和客服信息
      await loadSessionDetails()
    }
  } catch (error) {
    console.error('加载会话列表失败:', error)
    ElMessage.error('加载会话列表失败')
  } finally {
    loading.value = false
  }
}

// 获取会话详细信息
const loadSessionDetails = async () => {
  console.log('=== 开始加载会话详细信息 ===')
  console.log('会话列表:', sessionList.value.map(s => ({ id: s.id, title: s.title })))
  
  for (const session of sessionList.value) {
    try {
      console.log(`\n--- 处理会话 ${session.id} ---`)
      
      // 获取非用户未读消息数（包括客服、AI和系统消息，不包括用户消息）
      console.log(`调用API: countSessionUnreadNonUserMessages(${session.id})`)
      const unreadResponse = await customerServiceApi.countSessionUnreadNonUserMessages(session.id)
      console.log(`API响应:`, unreadResponse)
      
      if (unreadResponse.code === 200) {
        session.unreadCount = unreadResponse.data || 0
        console.log(`✅ 会话 ${session.id} 的非用户未读消息数: ${session.unreadCount}`)
      } else {
        console.error(`❌ 获取会话 ${session.id} 未读消息数失败:`, unreadResponse)
        session.unreadCount = 0
      }
      
      // 获取最后一条消息
      console.log(`调用API: getSessionMessages(${session.id})`)
      const messagesResponse = await customerServiceApi.getSessionMessages(session.id)
      console.log(`消息API响应:`, messagesResponse)
      
      if (messagesResponse.code === 200 && messagesResponse.data.length > 0) {
        const lastMessage = messagesResponse.data[messagesResponse.data.length - 1]
        session.lastMessage = lastMessage.messageContent
        session.lastMessageTime = lastMessage.createTime
        console.log(`✅ 会话 ${session.id} 的最后消息:`, lastMessage.messageContent)
      } else {
        session.lastMessage = '暂无消息'
        session.lastMessageTime = null
      }
      
      // 获取商品信息
      if (session.productId) {
        console.log(`获取商品信息: productId = ${session.productId}`)
        try {
          const productResponse = await fetch(`/api/products/${session.productId}`)
          if (productResponse.ok) {
            const productData = await productResponse.json()
            if (productData.code === 200) {
              session.title = `商品咨询：${productData.data.productName}`
              session.productImage = productData.data.mainImageUrl
              console.log(`✅ 会话 ${session.id} 的商品信息:`, productData.data.productName)
            }
          }
        } catch (error) {
          console.error(`获取商品信息失败:`, error)
        }
      }
      
      // 获取客服信息
      if (session.assignedAgentId) {
        console.log(`获取客服信息: agentId = ${session.assignedAgentId}`)
        try {
          const agentResponse = await fetch(`/api/admins/${session.assignedAgentId}`)
          if (agentResponse.ok) {
            const agentData = await agentResponse.json()
            if (agentData.code === 200) {
              session.agentName = agentData.data.nickname || `客服${session.assignedAgentId}`
              console.log(`✅ 会话 ${session.id} 的客服信息:`, session.agentName)
            }
          }
        } catch (error) {
          console.error(`获取客服信息失败:`, error)
        }
      }
    } catch (error) {
      console.error(`❌ 获取会话 ${session.id} 详情失败:`, error)
      session.unreadCount = 0
      session.lastMessage = '获取消息失败'
    }
  }
  
  // 显示所有会话的未读消息数
  console.log('\n=== 最终结果 ===')
  console.log('所有会话的未读消息数:', sessionList.value.map(s => ({ 
    id: s.id, 
    title: s.title,
    unreadCount: s.unreadCount,
    hasRedDot: s.unreadCount > 0 
  })))
  
  // 检查哪些会话应该显示红点
  const sessionsWithRedDot = sessionList.value.filter(s => s.unreadCount > 0)
  console.log(`应该显示红点的会话数量: ${sessionsWithRedDot.length}`)
  console.log('应该显示红点的会话:', sessionsWithRedDot.map(s => ({ id: s.id, title: s.title, unreadCount: s.unreadCount })))
}

// 选择会话
const selectSession = async (session) => {
  selectedSession.value = session
  await loadMessages(session.id)
  scrollToBottom()
}

// 加载消息
const loadMessages = async (sessionId) => {
  try {
    const response = await customerServiceApi.getSessionMessages(sessionId)
    
    if (response.code === 200) {
      messageList.value = response.data
      // 注意：移除自动标记已读，让用户端来决定何时标记已读
      // await customerServiceApi.markMessagesAsRead(sessionId)
      // 更新会话列表中的未读数
      const session = sessionList.value.find(s => s.id === sessionId)
      if (session) {
        // 不强制设置为0，保持原有的未读状态
        // session.unreadCount = 0
      }
    }
  } catch (error) {
    console.error('加载消息失败:', error)
    ElMessage.error('加载消息失败')
  }
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || !selectedSession.value) return
  
  try {
    sending.value = true
    const response = await customerServiceApi.sendAgentReply(1, { // 假设当前管理员ID为1
      sessionId: selectedSession.value.id,
      content: inputMessage.value.trim(),
      messageType: 1
    })
    
    if (response.code === 200) {
      inputMessage.value = ''
      // 注意：不要重新加载消息，避免触发标记已读
      // await loadMessages(selectedSession.value.id)
      
      // 直接将新消息添加到当前消息列表
      if (response.data) {
        messageList.value.push(response.data)
      }
      
      scrollToBottom()
      ElMessage.success('消息发送成功')
    } else {
      ElMessage.error('消息发送失败')
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送消息失败')
  } finally {
    sending.value = false
  }
}

// 结束会话
const endSession = async () => {
  if (!selectedSession.value) return
  
  try {
    await ElMessageBox.confirm('确定要结束这个会话吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await customerServiceApi.endSession(selectedSession.value.id)
    
    if (response.code === 200) {
      ElMessage.success('会话已结束')
      selectedSession.value = null
      messageList.value = []
      await loadSessions()
    } else {
      ElMessage.error('结束会话失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('结束会话失败:', error)
      ElMessage.error('结束会话失败')
    }
  }
}

// 批量分配
const handleAssignAll = async () => {
  try {
    const unassignedSessions = sessionList.value.filter(s => !s.assignedAgentId)
    
    if (unassignedSessions.length === 0) {
      ElMessage.info('没有需要分配的会话')
      return
    }
    
    await ElMessageBox.confirm(
      `确定要将 ${unassignedSessions.length} 个未分配会话分配给当前客服吗？`,
      '批量分配',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    for (const session of unassignedSessions) {
      await customerServiceApi.assignAgent(session.id, 1) // 假设当前管理员ID为1
    }
    
    ElMessage.success('批量分配完成')
    await loadSessions() // 重新加载所有会话
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量分配失败:', error)
      ElMessage.error('批量分配失败')
    }
  }
}

// 搜索
const handleSearch = () => {
  loadSessions()
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    userId: '',
    productId: '',
    sessionStatus: ''
  }
  loadSessions()
}

// 刷新会话
const refreshSessions = () => {
  loadSessions()
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 获取发送者名称
const getSenderName = (senderType) => {
  switch (senderType) {
    case 1: return '用户'
    case 2: return '客服'
    case 3: return 'AI'
    case 4: return '系统'
    default: return '未知'
  }
}

// 监听选中会话变化
watch(selectedSession, () => {
  if (selectedSession.value) {
    scrollToBottom()
  }
})

// 页面加载时初始化
onMounted(() => {
  loadSessions()
})
</script>

<style scoped>
.customer-service-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.fixed-header {
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 20px;
  z-index: 1000;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-card {
  margin-bottom: 0;
}

.main-content {
  flex: 1;
  display: flex;
  gap: 20px;
  padding: 20px;
  overflow: hidden;
}

.session-list-panel {
  width: 400px;
  flex-shrink: 0;
}

.chat-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.session-list-card,
.chat-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chat-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.session-list {
  max-height: calc(100vh - 300px); /* 调整高度与聊天区域一致 */
  overflow-y: auto;
  padding: 10px;
  min-height: 500px; /* 最小高度 */
}

.session-list::-webkit-scrollbar {
  width: 6px;
}

.session-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.session-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.session-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.session-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e4e7ed;
  background-color: #fff;
}

.session-item:hover {
  background-color: #f5f7fa;
  border-color: #409eff;
}

.session-item.active {
  background-color: #ecf5ff;
  border-color: #409eff;
}

.session-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 15px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.session-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.default-avatar {
  width: 100%;
  height: 100%;
  background-color: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  border-radius: 50%;
}

.session-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.session-title {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.agent-name {
  color: #409eff;
  font-size: 12px;
  font-weight: normal;
}

.session-message {
  color: #606266;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.session-time {
  color: #909399;
  font-size: 12px;
}

.session-preview {
  color: #606266;
  font-size: 14px;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.session-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 5px;
  color: #909399;
  font-size: 12px;
}

.session-status {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 5px;
}

.debug-info {
  font-size: 10px;
  color: #999;
}

.unread-indicator {
  width: 8px;
  height: 8px;
  background-color: #f56c6c;
  border-radius: 50%;
  margin-left: 5px;
}

.session-actions {
  margin-left: 10px;
  opacity: 0;
  transition: opacity 0.3s;
}

.session-item:hover .session-actions {
  opacity: 1;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-info {
  display: flex;
  flex-direction: column;
}

.chat-title {
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
}

.chat-status {
  color: #67c23a;
  font-size: 14px;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 320px); /* 稍微调整高度 */
  min-height: 600px; /* 增加最小高度 */
  max-height: calc(100vh - 200px); /* 添加最大高度限制 */
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 0;
  max-height: calc(100vh - 450px); /* 增加为输入框留出的空间 */
}

.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.no-session {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
}

.message-item.message-user {
  justify-content: flex-start;
}

.message-item.message-agent {
  justify-content: flex-end;
}

.message-avatar {
  flex-shrink: 0;
}

.message-content {
  max-width: 70%;
}

.message-text {
  padding: 12px 16px;
  border-radius: 8px;
  word-wrap: break-word;
}

.message-user-text {
  background-color: #fff;
  border: 1px solid #e4e7ed;
  color: #303133;
}

.message-agent-text {
  background-color: #409eff;
  color: #fff;
  border: 1px solid #409eff;
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
  text-align: center;
}

.chat-input {
  flex-shrink: 0;
  padding: 15px;
  border-top: 1px solid #e4e7ed;
  background-color: #fff;
  min-height: 100px; /* 减少最小高度 */
}

.input-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.input-row {
  display: flex;
  align-items: flex-end; /* 底部对齐 */
  gap: 10px;
}

.message-input {
  flex: 1;
  min-width: 0; /* 防止输入框溢出 */
}

.message-input .el-textarea__inner {
  resize: vertical; /* 只允许垂直调整大小 */
  min-height: 60px; /* 设置最小高度 */
  max-height: 120px; /* 设置最大高度 */
}

.input-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 0;
  min-height: 32px; /* 确保按钮有足够高度 */
  flex-shrink: 0; /* 防止按钮被压缩 */
}

/* 响应式设计 */
@media (max-height: 800px) {
  .chat-container {
    height: calc(100vh - 280px);
    min-height: 500px;
  }
  
  .chat-messages {
    max-height: calc(100vh - 400px);
  }
  
  .chat-input {
    min-height: 80px;
    padding: 10px;
  }
  
  .input-row {
    gap: 8px;
  }
}

@media (max-height: 600px) {
  .chat-container {
    height: calc(100vh - 250px);
    min-height: 400px;
  }
  
  .chat-messages {
    max-height: calc(100vh - 350px);
  }
  
  .chat-input {
    min-height: 70px;
    padding: 8px;
  }
  
  .input-container {
    gap: 8px;
  }
  
  .input-row {
    gap: 6px;
  }
  
  .message-input .el-textarea__inner {
    min-height: 50px;
    max-height: 100px;
  }
}

/* 超小屏幕适配 */
@media (max-width: 768px) {
  .input-row {
    flex-direction: column;
    align-items: stretch;
  }
  
  .input-actions {
    justify-content: center;
  }
}
</style> 