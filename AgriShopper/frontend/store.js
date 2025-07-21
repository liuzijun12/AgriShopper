import { reactive } from 'vue'

export const store = {
  state: reactive({
    userInfo: null,
    isAppReady: false,
    currentTab: 0,
    cartItems: [],
    favorites: [],
    refreshTrigger: 0 // 添加刷新触发器
  }),
  
  setUserInfo(userInfo) {
    this.state.userInfo = userInfo
    // 触发刷新
    this.state.refreshTrigger++
    // 持久化存储用户信息
    try {
      uni.setStorageSync('userInfo', userInfo)
    } catch (error) {
      console.error('保存用户信息失败:', error)
    }
  },
  
  getUserInfo() {
    // 优先从内存获取，如果没有则从本地存储获取
    if (this.state.userInfo) {
      return this.state.userInfo
    }
    
    try {
      const userInfo = uni.getStorageSync('userInfo')
      if (userInfo) {
        this.state.userInfo = userInfo
        return userInfo
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
    
    return null
  },
  
  clearUserInfo() {
    this.state.userInfo = null
    // 触发刷新
    this.state.refreshTrigger++
    try {
      uni.removeStorageSync('userInfo')
    } catch (error) {
      console.error('清除用户信息失败:', error)
    }
  },
  
  clearAllData() {
    // 清除所有用户相关数据
    this.state.userInfo = null
    this.state.cartItems = []
    this.state.favorites = []
    
    try {
      // 清除所有本地存储
      uni.clearStorageSync()
    } catch (error) {
      console.error('清除所有数据失败:', error)
    }
  },
  
  setAppReady(ready) {
    this.state.isAppReady = ready
  },
  
  setCurrentTab(index) {
    this.state.currentTab = index
  },
  
  addToCart(item) {
    const existingItem = this.state.cartItems.find(i => i.id === item.id)
    if (existingItem) {
      existingItem.quantity += 1
    } else {
      this.state.cartItems.push({ ...item, quantity: 1 })
    }
  },
  
  removeFromCart(itemId) {
    const index = this.state.cartItems.findIndex(i => i.id === itemId)
    if (index > -1) {
      this.state.cartItems.splice(index, 1)
    }
  },
  
  toggleFavorite(item) {
    const index = this.state.favorites.findIndex(i => i.id === item.id)
    if (index > -1) {
      this.state.favorites.splice(index, 1)
    } else {
      this.state.favorites.push(item)
    }
  }
} 