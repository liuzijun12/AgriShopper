import env from '../config/env.js'
import { store } from '../store.js'

// 购物车相关API接口
const cartApi = {
  /**
   * 获取当前登录用户ID
   * @returns {number} 用户ID，如果未登录返回默认用户ID=1
   */
  getCurrentUserId() {
    const userInfo = store.getUserInfo()
    return userInfo ? userInfo.id : 1  // 默认使用用户ID=1
  },

  /**
   * 检查用户是否已登录
   * @returns {boolean} 是否已登录
   */
  isLoggedIn() {
    const userInfo = store.getUserInfo()
    return userInfo !== null
  },

  /**
   * 添加商品到购物车
   * @param {number} productId - 商品ID
   * @param {number} quantity - 商品数量，默认1
   * @returns {Promise} 返回添加结果
   */
  addToCart(productId, quantity = 1) {
    const userId = this.getCurrentUserId()

    return new Promise((resolve, reject) => {
      uni.request({
        url: env.getApiUrl('/cart/add'),
        method: 'POST',
        header: {
          'Content-Type': 'application/json'
        },
        data: {
          userId: userId,
          productId: productId,
          quantity: quantity
        },
        success: (res) => {
          if (res.statusCode === 200 && res.data.code === 200) {
            resolve(res.data)
          } else {
            reject(new Error(res.data.message || '添加失败'))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  /**
   * 更新购物车商品数量
   * @param {number} productId - 商品ID
   * @param {number} quantity - 新的数量
   * @returns {Promise} 返回更新结果
   */
  updateQuantity(productId, quantity) {
    const userId = this.getCurrentUserId()

    return new Promise((resolve, reject) => {
      uni.request({
        url: env.getApiUrl('/cart/update'),
        method: 'PUT',
        header: {
          'Content-Type': 'application/json'
        },
        data: {
          userId: userId,
          productId: productId,
          quantity: quantity
        },
        success: (res) => {
          if (res.statusCode === 200 && res.data.code === 200) {
            resolve(res.data)
          } else {
            reject(new Error(res.data.message || '更新失败'))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  /**
   * 从购物车移除商品
   * @param {number} productId - 商品ID
   * @returns {Promise} 返回移除结果
   */
  removeFromCart(productId) {
    const userId = this.getCurrentUserId()

    return new Promise((resolve, reject) => {
      uni.request({
        url: env.getApiUrl('/cart/remove'),
        method: 'DELETE',
        header: {
          'Content-Type': 'application/json'
        },
        data: {
          userId: userId,
          productId: productId
        },
        success: (res) => {
          if (res.statusCode === 200 && res.data.code === 200) {
            resolve(res.data)
          } else {
            reject(new Error(res.data.message || '移除失败'))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  /**
   * 获取用户购物车列表
   * @returns {Promise} 返回购物车列表
   */
  getCartList() {
    const userId = this.getCurrentUserId()

    return new Promise((resolve, reject) => {
      uni.request({
        url: env.getApiUrl('/cart/list'),
        method: 'GET',
        data: {
          userId: userId
        },
        success: (res) => {
          if (res.statusCode === 200 && res.data.code === 200) {
            resolve(res.data)
          } else {
            reject(new Error(res.data.message || '获取购物车失败'))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  /**
   * 获取用户选中的购物车项
   * @returns {Promise} 返回选中的购物车项
   */
  getSelectedItems() {
    const userId = this.getCurrentUserId()

    return new Promise((resolve, reject) => {
      uni.request({
        url: env.getApiUrl('/cart/selected'),
        method: 'GET',
        data: {
          userId: userId
        },
        success: (res) => {
          if (res.statusCode === 200 && res.data.code === 200) {
            resolve(res.data)
          } else {
            reject(new Error(res.data.message || '获取选中商品失败'))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  /**
   * 清空用户购物车
   * @returns {Promise} 返回清空结果
   */
  clearCart() {
    const userId = this.getCurrentUserId()

    return new Promise((resolve, reject) => {
      uni.request({
        url: env.getApiUrl('/cart/clear'),
        method: 'DELETE',
        data: {
          userId: userId
        },
        success: (res) => {
          if (res.statusCode === 200 && res.data.code === 200) {
            resolve(res.data)
          } else {
            reject(new Error(res.data.message || '清空购物车失败'))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  /**
   * 更新购物车项选中状态
   * @param {number} cartItemId - 购物车项ID
   * @param {boolean} isSelected - 是否选中
   * @returns {Promise} 返回更新结果
   */
  updateSelectedStatus(cartItemId, isSelected) {
    const userId = this.getCurrentUserId()

    return new Promise((resolve, reject) => {
      uni.request({
        url: env.getApiUrl('/cart/select'),
        method: 'PUT',
        data: {
          userId: userId,
          cartItemId: cartItemId,
          isSelected: isSelected
        },
        success: (res) => {
          if (res.statusCode === 200 && res.data.code === 200) {
            resolve(res.data)
          } else {
            reject(new Error(res.data.message || '更新选中状态失败'))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  /**
   * 全选/取消全选
   * @param {boolean} isSelected - 是否全选
   * @returns {Promise} 返回更新结果
   */
  selectAll(isSelected) {
    const userId = this.getCurrentUserId()

    return new Promise((resolve, reject) => {
      uni.request({
        url: env.getApiUrl('/cart/select-all'),
        method: 'PUT',
        data: {
          userId: userId,
          isSelected: isSelected
        },
        success: (res) => {
          if (res.statusCode === 200 && res.data.code === 200) {
            resolve(res.data)
          } else {
            reject(new Error(res.data.message || '全选操作失败'))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  /**
   * 获取购物车商品总数
   * @returns {Promise} 返回商品总数
   */
  getCartItemCount() {
    const userId = this.getCurrentUserId()

    return new Promise((resolve, reject) => {
      uni.request({
        url: env.getApiUrl('/cart/count'),
        method: 'GET',
        data: {
          userId: userId
        },
        success: (res) => {
          if (res.statusCode === 200 && res.data.code === 200) {
            resolve(res.data)
          } else {
            reject(new Error(res.data.message || '获取商品总数失败'))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  /**
   * 获取购物车总金额
   * @returns {Promise} 返回总金额
   */
  getCartTotalAmount() {
    const userId = this.getCurrentUserId()

    return new Promise((resolve, reject) => {
      uni.request({
        url: env.getApiUrl('/cart/total'),
        method: 'GET',
        data: {
          userId: userId
        },
        success: (res) => {
          if (res.statusCode === 200 && res.data.code === 200) {
            resolve(res.data)
          } else {
            reject(new Error(res.data.message || '获取总金额失败'))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  /**
   * 检查商品是否已在购物车中
   * @param {number} productId - 商品ID
   * @returns {Promise} 返回是否在购物车中
   */
  isProductInCart(productId) {
    const userId = this.getCurrentUserId()

    return new Promise((resolve, reject) => {
      uni.request({
        url: env.getApiUrl('/cart/check'),
        method: 'GET',
        data: {
          userId: userId,
          productId: productId
        },
        success: (res) => {
          if (res.statusCode === 200 && res.data.code === 200) {
            resolve(res.data)
          } else {
            reject(new Error(res.data.message || '检查失败'))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  },

  /**
   * 逻辑删除购物车项
   * @param {number} cartItemId - 购物车项ID
   * @returns {Promise} 返回删除结果
   */
  softDeleteCartItem(cartItemId) {
    const userId = this.getCurrentUserId()

    return new Promise((resolve, reject) => {
      uni.request({
        url: env.getApiUrl(`/cart/soft-delete/${cartItemId}?userId=${userId}`),
        method: 'DELETE',
        success: (res) => {
          if (res.statusCode === 200 && res.data.code === 200) {
            resolve(res.data)
          } else {
            reject(new Error(res.data.message || '删除失败'))
          }
        },
        fail: (err) => {
          reject(new Error(`网络请求失败: ${err.errMsg}`))
        }
      })
    })
  }
}

export default cartApi 