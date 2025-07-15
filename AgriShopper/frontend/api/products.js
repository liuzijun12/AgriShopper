import env from '../config/env.js'

// 商品相关API接口
const productsApi = {
  /**
   * 获取商品列表
   * @param {Object} params - 查询参数
   * @param {number} params.page - 页码，默认0
   * @param {number} params.size - 每页大小，默认10
   * @param {string} params.productName - 商品名称搜索
   * @param {string} params.productCode - 商品编码搜索
   * @returns {Promise} 返回商品列表数据
   */
  getProductList(params = {}) {
    const { page = 0, size = 10, productName, productCode } = params
    
    let url = env.getApiUrl('/products') + `?page=${page}&size=${size}`
    
    if (productName) {
      url += `&productName=${encodeURIComponent(productName)}`
    }
    
    if (productCode) {
      url += `&productCode=${encodeURIComponent(productCode)}`
    }
    
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
   * 获取商品详情
   * @param {number} id - 商品ID
   * @returns {Promise} 返回商品详情数据
   */
  getProductDetail(id) {
    const url = env.getApiUrl(`/products/${id}`)
    
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
            reject(new Error('商品不存在'))
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
   * 搜索商品
   * @param {string} keyword - 搜索关键词
   * @param {Object} params - 其他参数
   * @returns {Promise} 返回搜索结果
   */
  searchProducts(keyword, params = {}) {
    return this.getProductList({
      ...params,
      productName: keyword
    })
  },

  /**
   * 根据分类获取商品
   * @param {number} categoryId - 分类ID
   * @param {Object} params - 其他参数
   * @returns {Promise} 返回分类商品列表
   */
  getProductsByCategory(categoryId, params = {}) {
    // 注意：当前后端接口没有直接支持按分类查询
    // 这里先获取所有商品，然后在前端过滤
    // 后续可以在后端添加分类查询接口
    return this.getProductList(params).then(response => {
      if (response.code === 200 && response.data.content) {
        const filteredProducts = response.data.content.filter(
          product => product.categoryId === categoryId
        )
        return {
          ...response,
          data: {
            ...response.data,
            content: filteredProducts,
            totalElements: filteredProducts.length
          }
        }
      }
      return response
    })
  }
}

export default productsApi 