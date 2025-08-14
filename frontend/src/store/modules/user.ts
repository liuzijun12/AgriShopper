import { defineStore } from 'pinia'

interface UserInfo {
  username: string
  userType: 'user' | 'merchant'
  avatar?: string
  nickname?: string
  gender?: number
  city?: string
  province?: string
  country?: string
}

interface UserState {
  userInfo: UserInfo | null
  isLoggedIn: boolean
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    userInfo: null,
    isLoggedIn: false
  }),

  getters: {
    // 是否已登录
    getIsLoggedIn: (state) => state.isLoggedIn,

    // 获取用户信息
    getUserInfo: (state) => state.userInfo,

    // 获取用户类型
    getUserType: (state) => state.userInfo?.userType || null,

    // 是否是商户
    isMerchant: (state) => state.userInfo?.userType === 'merchant',

    // 是否是普通用户
    isUser: (state) => state.userInfo?.userType === 'user'
  },

  actions: {
    // 登录
    login(userInfo: UserInfo) {
      this.userInfo = userInfo
      this.isLoggedIn = true

      // 存储到本地
      uni.setStorageSync('userInfo', userInfo)
      uni.setStorageSync('isLoggedIn', true)
    },

    // 登出
    logout() {
      this.userInfo = null
      this.isLoggedIn = false

      // 清除本地存储
      uni.removeStorageSync('userInfo')
      uni.removeStorageSync('isLoggedIn')
    },

    // 初始化用户状态（从本地存储恢复）
    initUserState() {
      try {
        const userInfo = uni.getStorageSync('userInfo')
        const isLoggedIn = uni.getStorageSync('isLoggedIn')

        if (userInfo && isLoggedIn) {
          this.userInfo = userInfo
          this.isLoggedIn = true
        }
      } catch (error) {
        console.error('初始化用户状态失败:', error)
        this.logout() // 出错时清除状态
      }
    }
  }
})