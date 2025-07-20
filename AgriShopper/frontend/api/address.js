import env from '../config/env.js'

const config = env.getConfig()

/**
 * 地址管理API
 */
const addressApi = {
  /**
   * 获取用户地址列表
   * @param {number} userId - 用户ID
   * @returns {Promise} 地址列表
   */
  getAddressList(userId) {
    return new Promise((resolve, reject) => {
      uni.request({
        url: `${config.baseUrl}/api/address/list?userId=${userId}`,
        method: 'GET',
        success: (res) => {
          resolve(res.data)
        },
        fail: (err) => {
          reject(err)
        }
      })
    })
  },

  /**
   * 获取默认地址
   * @param {number} userId - 用户ID
   * @returns {Promise} 默认地址信息
   */
  getDefaultAddress(userId) {
    return new Promise((resolve, reject) => {
      uni.request({
        url: `${config.baseUrl}/api/address/default?userId=${userId}`,
        method: 'GET',
        success: (res) => {
          resolve(res.data)
        },
        fail: (err) => {
          reject(err)
        }
      })
    })
  },

  /**
   * 根据ID获取地址详情
   * @param {number} id - 地址ID
   * @param {number} userId - 用户ID
   * @returns {Promise} 地址详情
   */
  getAddressById(id, userId) {
    return new Promise((resolve, reject) => {
      uni.request({
        url: `${config.baseUrl}/api/address/${id}?userId=${userId}`,
        method: 'GET',
        success: (res) => {
          resolve(res.data)
        },
        fail: (err) => {
          reject(err)
        }
      })
    })
  },

  /**
   * 新增地址
   * @param {Object} addressData - 地址信息
   * @returns {Promise} 新增的地址信息
   */
  addAddress(addressData) {
    return new Promise((resolve, reject) => {
      uni.request({
        url: `${config.baseUrl}/api/address/add`,
        method: 'POST',
        data: addressData,
        success: (res) => {
          resolve(res.data)
        },
        fail: (err) => {
          reject(err)
        }
      })
    })
  },

  /**
   * 修改地址
   * @param {Object} addressData - 地址信息
   * @returns {Promise} 修改后的地址信息
   */
  updateAddress(addressData) {
    return new Promise((resolve, reject) => {
      uni.request({
        url: `${config.baseUrl}/api/address/update`,
        method: 'PUT',
        data: addressData,
        success: (res) => {
          resolve(res.data)
        },
        fail: (err) => {
          reject(err)
        }
      })
    })
  },

  /**
   * 删除地址
   * @param {number} id - 地址ID
   * @param {number} userId - 用户ID
   * @returns {Promise} 删除结果
   */
  deleteAddress(id, userId) {
    return new Promise((resolve, reject) => {
      uni.request({
        url: `${config.baseUrl}/api/address/${id}?userId=${userId}`,
        method: 'DELETE',
        success: (res) => {
          resolve(res.data)
        },
        fail: (err) => {
          reject(err)
        }
      })
    })
  },

  /**
   * 设置默认地址
   * @param {number} id - 地址ID
   * @param {number} userId - 用户ID
   * @returns {Promise} 设置结果
   */
  setDefaultAddress(id, userId) {
    return new Promise((resolve, reject) => {
      uni.request({
        url: `${config.baseUrl}/api/address/set-default/${id}?userId=${userId}`,
        method: 'PUT',
        success: (res) => {
          resolve(res.data)
        },
        fail: (err) => {
          reject(err)
        }
      })
    })
  },

  /**
   * 获取地址数量
   * @param {number} userId - 用户ID
   * @returns {Promise} 地址数量
   */
  getAddressCount(userId) {
    return new Promise((resolve, reject) => {
      uni.request({
        url: `${config.baseUrl}/api/address/count?userId=${userId}`,
        method: 'GET',
        success: (res) => {
          resolve(res.data)
        },
        fail: (err) => {
          reject(err)
        }
      })
    })
  },

  /**
   * 验证地址信息
   * @param {Object} addressData - 地址信息
   * @returns {Promise} 验证结果
   */
  validateAddress(addressData) {
    return new Promise((resolve, reject) => {
      uni.request({
        url: `${config.baseUrl}/api/address/validate`,
        method: 'POST',
        data: addressData,
        success: (res) => {
          resolve(res.data)
        },
        fail: (err) => {
          reject(err)
        }
      })
    })
  }
}

export default addressApi 