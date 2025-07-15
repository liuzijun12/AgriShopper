// 环境配置
const ENV = {
  // 开发环境
  development: {
    baseUrl: 'http://localhost:8080',
    apiPrefix: '/api'
  },
  // 生产环境
  production: {
    baseUrl: 'https://your-domain.com', // 需要替换为你的实际域名
    apiPrefix: '/api'
  }
}

// 获取当前环境
function getCurrentEnv() {
  // #ifdef MP-WEIXIN
  // 微信小程序环境
  if (process.env.NODE_ENV === 'development') {
    return 'development'
  } else {
    return 'production'
  }
  // #endif
  
  // #ifndef MP-WEIXIN
  // 其他环境
  return 'development'
  // #endif
}

// 获取当前环境配置
function getConfig() {
  const env = getCurrentEnv()
  return ENV[env] || ENV.development
}

// 获取完整的API URL
function getApiUrl(path) {
  const config = getConfig()
  return `${config.baseUrl}${config.apiPrefix}${path}`
}

export default {
  getCurrentEnv,
  getConfig,
  getApiUrl,
  ENV
} 