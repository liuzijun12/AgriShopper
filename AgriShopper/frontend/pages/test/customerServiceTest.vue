<template>
  <view class="test-container">
    <view class="test-header">
      <text class="test-title">客服系统测试</text>
    </view>
    
    <view class="test-section">
      <text class="section-title">API测试</text>
      
      <view class="test-buttons">
        <button class="test-btn" @tap="testStartCustomerService">发起客服咨询</button>
        <button class="test-btn" @tap="testGetUserSessions">获取用户会话</button>
        <button class="test-btn" @tap="testGetSessionMessages">获取会话消息</button>
        <button class="test-btn" @tap="testSendMessage">发送消息</button>
        <button class="test-btn" @tap="testMarkAsRead">标记已读</button>
        <button class="test-btn" @tap="testEndSession">结束会话</button>
      </view>
    </view>
    
    <view class="test-section">
      <text class="section-title">测试结果</text>
      <view class="result-container">
        <text class="result-text">{{ testResult }}</text>
      </view>
    </view>
    
    <view class="test-section">
      <text class="section-title">当前会话信息</text>
      <view class="info-container">
        <text class="info-text">会话ID: {{ currentSessionId || '无' }}</text>
        <text class="info-text">用户ID: {{ userId }}</text>
        <text class="info-text">消息数量: {{ messageCount }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import { customerServiceApi } from '../../api/customerService.js'

export default {
  data() {
    return {
      userId: 1,
      currentSessionId: null,
      messageCount: 0,
      testResult: '点击上方按钮开始测试...'
    }
  },
  
  methods: {
    // 测试发起客服咨询
    async testStartCustomerService() {
      try {
        this.testResult = '正在发起客服咨询...'
        
        const data = {
          productId: 1,
          orderId: 'TEST001',
          sessionType: 1
        }
        
        const response = await customerServiceApi.startCustomerService(this.userId, data)
        
        if (response.code === 200) {
          this.currentSessionId = response.data.id
          this.testResult = `发起成功！会话ID: ${response.data.id}`
        } else {
          this.testResult = `发起失败: ${response.message}`
        }
      } catch (error) {
        this.testResult = `发起失败: ${error.message}`
        console.error('发起客服咨询失败:', error)
      }
    },
    
    // 测试获取用户会话
    async testGetUserSessions() {
      try {
        this.testResult = '正在获取用户会话...'
        
        const response = await customerServiceApi.getUserSessions(this.userId)
        
        if (response.code === 200) {
          const sessions = response.data || []
          this.testResult = `获取成功！共${sessions.length}个会话`
          if (sessions.length > 0) {
            this.currentSessionId = sessions[0].id
          }
        } else {
          this.testResult = `获取失败: ${response.message}`
        }
      } catch (error) {
        this.testResult = `获取失败: ${error.message}`
        console.error('获取用户会话失败:', error)
      }
    },
    
    // 测试获取会话消息
    async testGetSessionMessages() {
      if (!this.currentSessionId) {
        this.testResult = '请先创建会话或获取会话列表'
        return
      }
      
      try {
        this.testResult = '正在获取会话消息...'
        
        const response = await customerServiceApi.getSessionMessages(this.currentSessionId)
        
        if (response.code === 200) {
          const messages = response.data || []
          this.messageCount = messages.length
          this.testResult = `获取成功！共${messages.length}条消息`
        } else {
          this.testResult = `获取失败: ${response.message}`
        }
      } catch (error) {
        this.testResult = `获取失败: ${error.message}`
        console.error('获取会话消息失败:', error)
      }
    },
    
    // 测试发送消息
    async testSendMessage() {
      if (!this.currentSessionId) {
        this.testResult = '请先创建会话'
        return
      }
      
      try {
        this.testResult = '正在发送消息...'
        
        const data = {
          sessionId: this.currentSessionId,
          content: `测试消息 - ${new Date().toLocaleTimeString()}`,
          messageType: 1
        }
        
        const response = await customerServiceApi.sendUserMessage(this.userId, data)
        
        if (response.code === 200) {
          this.testResult = `发送成功！消息ID: ${response.data.id}`
          this.messageCount++
        } else {
          this.testResult = `发送失败: ${response.message}`
        }
      } catch (error) {
        this.testResult = `发送失败: ${error.message}`
        console.error('发送消息失败:', error)
      }
    },
    
    // 测试标记已读
    async testMarkAsRead() {
      if (!this.currentSessionId) {
        this.testResult = '请先创建会话'
        return
      }
      
      try {
        this.testResult = '正在标记已读...'
        
        const response = await customerServiceApi.markMessagesAsRead(this.currentSessionId)
        
        if (response.code === 200) {
          this.testResult = '标记已读成功！'
        } else {
          this.testResult = `标记失败: ${response.message}`
        }
      } catch (error) {
        this.testResult = `标记失败: ${error.message}`
        console.error('标记已读失败:', error)
      }
    },
    
    // 测试结束会话
    async testEndSession() {
      if (!this.currentSessionId) {
        this.testResult = '请先创建会话'
        return
      }
      
      try {
        this.testResult = '正在结束会话...'
        
        const response = await customerServiceApi.endSession(this.currentSessionId)
        
        if (response.code === 200) {
          this.testResult = '会话结束成功！'
          this.currentSessionId = null
        } else {
          this.testResult = `结束失败: ${response.message}`
        }
      } catch (error) {
        this.testResult = `结束失败: ${error.message}`
        console.error('结束会话失败:', error)
      }
    }
  }
}
</script>

<style scoped>
.test-container {
  padding: 20rpx;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.test-header {
  background-color: #fff;
  padding: 30rpx;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  text-align: center;
}

.test-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.test-section {
  background-color: #fff;
  padding: 30rpx;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  display: block;
}

.test-buttons {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.test-btn {
  background-color: #007aff;
  color: #fff;
  border: none;
  border-radius: 12rpx;
  padding: 20rpx;
  font-size: 28rpx;
}

.test-btn:active {
  background-color: #0056cc;
}

.result-container {
  background-color: #f8f9fa;
  padding: 20rpx;
  border-radius: 12rpx;
  border: 1rpx solid #e9ecef;
}

.result-text {
  font-size: 26rpx;
  color: #333;
  line-height: 1.5;
  word-break: break-all;
}

.info-container {
  background-color: #f8f9fa;
  padding: 20rpx;
  border-radius: 12rpx;
  border: 1rpx solid #e9ecef;
}

.info-text {
  font-size: 26rpx;
  color: #666;
  line-height: 1.8;
  display: block;
}
</style> 