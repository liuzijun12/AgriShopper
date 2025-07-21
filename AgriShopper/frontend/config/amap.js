// 高德地图配置文件

// 高德地图Web服务API密钥
// 请在高德开放平台申请：https://lbs.amap.com/
export const AMAP_CONFIG = {
  // 微信小程序API密钥
  WEB_SERVICE_KEY: 'caee53be8686ac43b28c077e193cb92e',
  
  // 微信小程序JavaScript API密钥
  JS_API_KEY: 'caee53be8686ac43b28c077e193cb92e',
  
  // 安全密钥（可选）
  SECURITY_KEY: 'your_amap_security_key_here',
  
  // API基础URL
  BASE_URL: 'https://restapi.amap.com/v3',
  
  // 请求超时时间（毫秒）
  TIMEOUT: 10000,
  
  // 是否启用缓存
  ENABLE_CACHE: true,
  
  // 缓存过期时间（毫秒）
  CACHE_EXPIRE: 24 * 60 * 60 * 1000 // 24小时
}

// 缓存管理
class AmapCache {
  constructor() {
    this.cache = new Map()
  }
  
  // 设置缓存
  set(key, value, expire = AMAP_CONFIG.CACHE_EXPIRE) {
    const item = {
      value,
      expire: Date.now() + expire
    }
    this.cache.set(key, item)
  }
  
  // 获取缓存
  get(key) {
    const item = this.cache.get(key)
    if (!item) return null
    
    if (Date.now() > item.expire) {
      this.cache.delete(key)
      return null
    }
    
    return item.value
  }
  
  // 清除缓存
  clear() {
    this.cache.clear()
  }
  
  // 删除指定缓存
  delete(key) {
    this.cache.delete(key)
  }
}

export const amapCache = new AmapCache()

// 生成缓存键
export const generateCacheKey = (type, params) => {
  return `amap_${type}_${JSON.stringify(params)}`
}

// 检查API密钥是否配置
export const checkApiKey = () => {
  if (!AMAP_CONFIG.WEB_SERVICE_KEY || AMAP_CONFIG.WEB_SERVICE_KEY === '') {
    console.warn('⚠️ 高德地图API密钥未配置')
    return false
  }
  return true
}

// 获取API密钥
export const getApiKey = () => {
  if (!checkApiKey()) {
    throw new Error('高德地图API密钥未配置')
  }
  return AMAP_CONFIG.WEB_SERVICE_KEY
} 