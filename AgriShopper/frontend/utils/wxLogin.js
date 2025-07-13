// 微信登录相关工具类
export class WxLogin {
  
  // 获取微信登录凭证
  static getLoginCode() {
    return new Promise((resolve, reject) => {
      uni.login({
        provider: 'weixin',
        success: (res) => {
          if (res.code) {
            resolve(res.code)
          } else {
            reject(new Error('获取微信登录凭证失败'))
          }
        },
        fail: (err) => {
          reject(new Error('微信登录失败: ' + err.errMsg))
        }
      })
    })
  }
  
  // 获取用户信息
  static getUserProfile() {
    return new Promise((resolve, reject) => {
      uni.getUserProfile({
        desc: '用于完善会员资料，提供更好的购物体验',
        success: (res) => {
          resolve(res.userInfo)
        },
        fail: (err) => {
          reject(new Error('获取用户信息失败: ' + err.errMsg))
        }
      })
    })
  }
  
  // 检查微信环境
  static checkWxEnvironment() {
    const systemInfo = uni.getSystemInfoSync()
    return systemInfo.platform.includes('mp-weixin')
  }
  
  // 获取用户授权设置
  static getSetting() {
    return new Promise((resolve, reject) => {
      uni.getSetting({
        success: (res) => {
          resolve(res.authSetting)
        },
        fail: (err) => {
          reject(new Error('获取授权设置失败: ' + err.errMsg))
        }
      })
    })
  }
  
  // 打开授权设置页面
  static openSetting() {
    return new Promise((resolve, reject) => {
      uni.openSetting({
        success: (res) => {
          resolve(res.authSetting)
        },
        fail: (err) => {
          reject(new Error('打开设置页面失败: ' + err.errMsg))
        }
      })
    })
  }
}

// API请求工具类
export class AuthAPI {
  
  // 基础URL
  static baseURL = 'http://localhost:8080/api'
  
  // 微信登录
  static async wxLogin(code) {
    try {
      const response = await uni.request({
        url: `${this.baseURL}/auth/wechat/login`,
        method: 'POST',
        data: { code },
        header: {
          'Content-Type': 'application/json'
        }
      })
      
      if (response.statusCode === 200) {
        return response.data
      } else {
        throw new Error('微信登录失败')
      }
    } catch (error) {
      throw new Error('网络请求失败: ' + error.message)
    }
  }
  
  // 保存用户信息
  static async saveUserInfo(userInfo) {
    try {
      const response = await uni.request({
        url: `${this.baseURL}/users`,
        method: 'POST',
        data: userInfo,
        header: {
          'Content-Type': 'application/json'
        }
      })
      
      if (response.statusCode === 200) {
        return response.data
      } else {
        throw new Error('保存用户信息失败')
      }
    } catch (error) {
      throw new Error('网络请求失败: ' + error.message)
    }
  }
  
  // 检查用户是否存在
  static async checkUserExists(openid) {
    try {
      const response = await uni.request({
        url: `${this.baseURL}/users/check/openid/${openid}`,
        method: 'GET'
      })
      
      if (response.statusCode === 200) {
        return response.data
      } else {
        throw new Error('检查用户失败')
      }
    } catch (error) {
      throw new Error('网络请求失败: ' + error.message)
    }
  }
  
  // 获取用户信息
  static async getUserInfo(openid) {
    try {
      const response = await uni.request({
        url: `${this.baseURL}/users/openid/${openid}`,
        method: 'GET'
      })
      
      if (response.statusCode === 200) {
        return response.data
      } else {
        throw new Error('获取用户信息失败')
      }
    } catch (error) {
      throw new Error('网络请求失败: ' + error.message)
    }
  }
}

// 登录状态管理
export class WxLoginManager {
  
  // 完整的微信登录流程
  static async completeWxLogin() {
    try {
      // 1. 检查微信环境
      if (!WxLogin.checkWxEnvironment()) {
        throw new Error('请在微信小程序中使用')
      }
      
      // 2. 获取微信登录凭证
      const code = await WxLogin.getLoginCode()
      
      // 3. 获取用户信息
      const userProfile = await WxLogin.getUserProfile()
      
      // 4. 调用后端登录接口（这里简化处理，直接使用code作为openid）
      // 实际项目中，后端需要通过code换取openid
      const loginResult = {
        openid: code, // 临时使用code作为openid
        unionid: null,
        session_key: null
      }
      
      // 5. 合并用户信息
      const fullUserInfo = {
        ...userProfile,
        openid: loginResult.openid,
        unionid: loginResult.unionid,
        sessionKey: loginResult.session_key
      }
      
      // 6. 保存到后端（可选）
      try {
        await AuthAPI.saveUserInfo(fullUserInfo)
      } catch (error) {
        console.warn('保存用户信息到后端失败:', error)
        // 这里可以选择是否抛出错误，取决于业务需求
      }
      
      return fullUserInfo
      
    } catch (error) {
      throw error
    }
  }
  
  // 检查登录状态
  static isLoggedIn() {
    try {
      const userInfo = uni.getStorageSync('userInfo')
      return userInfo && userInfo.openid
    } catch (error) {
      return false
    }
  }
  
  // 清除登录信息
  static clearLogin() {
    try {
      uni.removeStorageSync('userInfo')
      return true
    } catch (error) {
              console.error('清除登录信息失败:', error)
      return false
    }
  }
} 