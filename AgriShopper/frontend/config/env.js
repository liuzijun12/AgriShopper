// 环境配置
const ENV = {
  // 开发环境 - 本地调试
  development: {
    baseUrl: 'http://localhost:8080',
    apiPrefix: '/api'
  },
  // 真机调试环境 - 使用电脑IP地址
  mobile: {
    baseUrl: 'http://192.168.31.248:8080', // 你的电脑IP地址
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
  // 其他环境 - 可以根据需要手动切换
  // return 'development'  // 本地调试
  // return 'mobile'  // 真机调试
  return 'mobile'
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