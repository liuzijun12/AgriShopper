import env from '../config/env.js'

// 分类相关API接口
const categoriesApi = {
  /**
   * 获取所有分类
   * @returns {Promise} 返回分类列表数据
   */
  getCategoryList() {
    const url = env.getApiUrl('/categories')
    
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

  /**
   * 获取分类详情
   * @param {number} id - 分类ID
   * @returns {Promise} 返回分类详情数据
   */
  getCategoryDetail(id) {
    const url = env.getApiUrl(`/categories/${id}`)
    
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
          } else if (res.statusCode === 404) {
            reject(new Error('分类不存在'))
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

  /**
   * 创建分类
   * @param {Object} categoryData - 分类数据
   * @returns {Promise} 返回创建的分类数据
   */
  createCategory(categoryData) {
    const url = env.getApiUrl('/categories')
    
    return new Promise((resolve, reject) => {
      uni.request({
        url: url,
        method: 'POST',
        header: {
          'Content-Type': 'application/json'
        },
        data: categoryData,
        success: (res) => {
          if (res.statusCode === 201) {
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

  /**
   * 更新分类
   * @param {number} id - 分类ID
   * @param {Object} categoryData - 分类数据
   * @returns {Promise} 返回更新后的分类数据
   */
  updateCategory(id, categoryData) {
    const url = env.getApiUrl(`/categories/${id}`)
    
    return new Promise((resolve, reject) => {
      uni.request({
        url: url,
        method: 'PUT',
        header: {
          'Content-Type': 'application/json'
        },
        data: categoryData,
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

  /**
   * 删除分类
   * @param {number} id - 分类ID
   * @returns {Promise} 返回删除结果
   */
  deleteCategory(id) {
    const url = env.getApiUrl(`/categories/${id}`)
    
    return new Promise((resolve, reject) => {
      uni.request({
        url: url,
        method: 'DELETE',
        header: {
          'Content-Type': 'application/json'
        },
        success: (res) => {
          if (res.statusCode === 204) {
            resolve({ success: true })
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

export default categoriesApi 