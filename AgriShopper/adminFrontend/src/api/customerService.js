import request from '@/utils/request'

// 客服管理API
export const customerServiceApi = {
  // 获取未分配会话列表
  getUnassignedSessions() {
    return request({
      url: '/api/customer-service/sessions/unassigned',
      method: 'get'
    })
  },

  // 获取所有会话列表
  getAllSessions(params) {
    return request({
      url: '/api/customer-service/sessions',
      method: 'get',
      params
    })
  },

  // 获取当前客服的会话列表
  getMySessions(agentId) {
    return request({
      url: `/api/customer-service/sessions/agent/${agentId}`,
      method: 'get'
    })
  },

  // 获取会话消息
  getSessionMessages(sessionId) {
    return request({
      url: `/api/customer-service/messages/${sessionId}`,
      method: 'get'
    })
  },

  // 发送客服回复
  sendAgentReply(agentId, data) {
    return request({
      url: `/api/customer-service/message/reply?agentId=${agentId}`,
      method: 'post',
      data
    })
  },

  // 分配客服
  assignAgent(sessionId, agentId) {
    return request({
      url: `/api/customer-service/sessions/${sessionId}/assign/${agentId}`,
      method: 'post'
    })
  },

  // 结束会话
  endSession(sessionId) {
    return request({
      url: `/api/customer-service/sessions/${sessionId}/end`,
      method: 'post'
    })
  },

  // 标记消息已读
  markMessagesAsRead(sessionId) {
    return request({
      url: `/api/customer-service/messages/${sessionId}/read`,
      method: 'post'
    })
  },

  // 获取会话未读消息数
  countSessionUnreadMessages(sessionId) {
    return request({
      url: `/api/customer-service/stats/session/${sessionId}/unread`,
      method: 'get'
    })
  },

  // 获取客服会话列表
  getAgentSessions(agentId) {
    return request({
      url: `/api/customer-service/sessions/agent/${agentId}`,
      method: 'get'
    })
  },

  // 获取用户会话列表
  getUserSessions(userId) {
    return request({
      url: `/api/customer-service/sessions/user/${userId}`,
      method: 'get'
    })
  },

  // 统计用户未读消息数
  countUserUnreadMessages(userId) {
    return request({
      url: `/api/customer-service/stats/user/${userId}/unread`,
      method: 'get'
    })
  },

  // 统计客服当前会话数
  countAgentActiveSessions(agentId) {
    return request({
      url: `/api/customer-service/sessions/agent/${agentId}/active/count`,
      method: 'get'
    })
  }
} 