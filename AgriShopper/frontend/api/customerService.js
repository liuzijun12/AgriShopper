import env from '../config/env.js'

// 客服系统API
export const customerServiceApi = {
  // 发起客服咨询
  startCustomerService(userId, data) {
    const url = env.getApiUrl(`/customer-service/start?userId=${userId}`)
    
    return new Promise((resolve, reject) => {
      uni.request({
        url: url,
        method: 'POST',
        data: data,
        header: {
          'Content-Type': 'application/json'
        },
        success: (res) => {
          if (res.statusCode === 200) {
            resolve(res.data)
          } else {
            reject(new Error(`请求失败: ${res.statusCode}`))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  // 发送用户消息
  sendUserMessage(userId, data) {
    const url = env.getApiUrl(`/customer-service/message/send?userId=${userId}`)
    
    return new Promise((resolve, reject) => {
      uni.request({
        url: url,
        method: 'POST',
        data: data,
        header: {
          'Content-Type': 'application/json'
        },
        success: (res) => {
          if (res.statusCode === 200) {
            resolve(res.data)
          } else {
            reject(new Error(`请求失败: ${res.statusCode}`))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  // 客服回复消息
  sendAgentReply(agentId, data) {
    const url = env.getApiUrl(`/customer-service/message/reply?agentId=${agentId}`)
    
    return new Promise((resolve, reject) => {
      uni.request({
        url: url,
        method: 'POST',
        data: data,
        header: {
          'Content-Type': 'application/json'
        },
        success: (res) => {
          if (res.statusCode === 200) {
            resolve(res.data)
          } else {
            reject(new Error(`请求失败: ${res.statusCode}`))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  // 获取用户会话列表
  getUserSessions(userId) {
    const url = env.getApiUrl(`/customer-service/sessions/user/${userId}`)
    
    return new Promise((resolve, reject) => {
      uni.request({
        url: url,
        method: 'GET',
        header: {
          'Content-Type': 'application/json'
        },
        success: (res) => {
          if (res.statusCode === 200) {
            resolve(res.data)
          } else {
            reject(new Error(`请求失败: ${res.statusCode}`))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  // 获取客服会话列表
  getAgentSessions(agentId) {
    const url = env.getApiUrl(`/customer-service/sessions/agent/${agentId}`)
    
    return new Promise((resolve, reject) => {
      uni.request({
        url: url,
        method: 'GET',
        header: {
          'Content-Type': 'application/json'
        },
        success: (res) => {
          if (res.statusCode === 200) {
            resolve(res.data)
          } else {
            reject(new Error(`请求失败: ${res.statusCode}`))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  // 获取未分配会话
  getUnassignedSessions() {
    const url = env.getApiUrl('/customer-service/sessions/unassigned')
    
    return new Promise((resolve, reject) => {
      uni.request({
        url: url,
        method: 'GET',
        header: {
          'Content-Type': 'application/json'
        },
        success: (res) => {
          if (res.statusCode === 200) {
            resolve(res.data)
          } else {
            reject(new Error(`请求失败: ${res.statusCode}`))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  // 获取会话消息
  getSessionMessages(sessionId) {
    const url = env.getApiUrl(`/customer-service/messages/${sessionId}`)
    
    return new Promise((resolve, reject) => {
      uni.request({
        url: url,
        method: 'GET',
        header: {
          'Content-Type': 'application/json'
        },
        success: (res) => {
          if (res.statusCode === 200) {
            resolve(res.data)
          } else {
            reject(new Error(`请求失败: ${res.statusCode}`))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  // 标记消息已读
  markMessagesAsRead(sessionId) {
    const url = env.getApiUrl(`/customer-service/messages/${sessionId}/read`)
    
    return new Promise((resolve, reject) => {
      uni.request({
        url: url,
        method: 'POST',
        header: {
          'Content-Type': 'application/json'
        },
        success: (res) => {
          if (res.statusCode === 200) {
            resolve(res.data)
          } else {
            reject(new Error(`请求失败: ${res.statusCode}`))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  // 分配客服
  assignAgent(sessionId, agentId) {
    const url = env.getApiUrl(`/customer-service/sessions/${sessionId}/assign/${agentId}`)
    
    return new Promise((resolve, reject) => {
      uni.request({
        url: url,
        method: 'POST',
        header: {
          'Content-Type': 'application/json'
        },
        success: (res) => {
          if (res.statusCode === 200) {
            resolve(res.data)
          } else {
            reject(new Error(`请求失败: ${res.statusCode}`))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  // 结束会话
  endSession(sessionId) {
    const url = env.getApiUrl(`/customer-service/sessions/${sessionId}/end`)
    
    return new Promise((resolve, reject) => {
      uni.request({
        url: url,
        method: 'POST',
        header: {
          'Content-Type': 'application/json'
        },
        success: (res) => {
          if (res.statusCode === 200) {
            resolve(res.data)
          } else {
            reject(new Error(`请求失败: ${res.statusCode}`))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  // 获取用户未读消息数
  countUserUnreadMessages(userId) {
    const url = env.getApiUrl(`/customer-service/stats/user/${userId}/unread`)
    
    return new Promise((resolve, reject) => {
      uni.request({
        url: url,
        method: 'GET',
        header: {
          'Content-Type': 'application/json'
        },
        success: (res) => {
          if (res.statusCode === 200) {
            resolve(res.data)
          } else {
            reject(new Error(`请求失败: ${res.statusCode}`))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  // 获取会话未读消息数
  countSessionUnreadMessages(sessionId) {
    const url = env.getApiUrl(`/customer-service/stats/session/${sessionId}/unread`)
    
    return new Promise((resolve, reject) => {
      uni.request({
        url: url,
        method: 'GET',
        header: {
          'Content-Type': 'application/json'
        },
        success: (res) => {
          if (res.statusCode === 200) {
            resolve(res.data)
          } else {
            reject(new Error(`请求失败: ${res.statusCode}`))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  }
} 