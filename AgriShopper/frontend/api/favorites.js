import env from '../config/env.js';
import { store } from '../store.js';

const config = env.getConfig();

// 收藏相关API
const favoritesApi = {
  /**
   * 获取当前登录用户ID
   * @returns {number} 用户ID，测试阶段默认使用用户ID=1
   */
  getCurrentUserId() {
    return 1  // 测试阶段默认使用用户ID=1
  },

  /**
   * 检查用户是否已登录
   * @returns {boolean} 是否已登录
   */
  isLoggedIn() {
    const userInfo = store.getUserInfo()
    return userInfo !== null
  },
  // 添加收藏
  addFavorite: async (userId, productCode) => {
    try {
      const response = await uni.request({
        url: `${config.baseUrl}/api/favorites/add`,
        method: 'POST',
        header: {
          'Content-Type': 'application/json'
        },
        data: {
          userId: userId,
          productCode: productCode
        }
      });
      
      return response.data;
    } catch (error) {
      console.error('添加收藏失败:', error);
      throw error;
    }
  },

  // 取消收藏
  removeFavorite: async (userId, productCode) => {
    try {
      const response = await uni.request({
        url: `${config.baseUrl}/api/favorites/remove`,
        method: 'DELETE',
        header: {
          'Content-Type': 'application/json'
        },
        data: {
          userId: userId,
          productCode: productCode
        }
      });
      
      return response.data;
    } catch (error) {
      console.error('取消收藏失败:', error);
      throw error;
    }
  },

  // 批量取消收藏
  removeFavorites: async (userId, productCodes) => {
    try {
      const response = await uni.request({
        url: `${config.baseUrl}/api/favorites/batch-remove`,
        method: 'DELETE',
        header: {
          'Content-Type': 'application/json'
        },
        data: {
          userId: userId,
          productCodes: productCodes
        }
      });
      
      return response.data;
    } catch (error) {
      console.error('批量取消收藏失败:', error);
      throw error;
    }
  },

  // 清空用户收藏
  clearUserFavorites: async (userId) => {
    try {
      const response = await uni.request({
        url: `${config.baseUrl}/api/favorites/user/${userId}/clear`,
        method: 'DELETE'
      });
      
      return response.data;
    } catch (error) {
      console.error('清空收藏失败:', error);
      throw error;
    }
  },

  // 获取用户收藏列表（分页）
  getUserFavoritesPage: async (userId, page = 0, size = 10) => {
    try {
      const url = `${config.baseUrl}/api/favorites/user/${userId}/page?page=${page}&size=${size}`;
      console.log('请求收藏列表URL:', url);
      
      const response = await uni.request({
        url: url,
        method: 'GET'
      });
      
      console.log('收藏列表响应:', response);
      return response.data;
    } catch (error) {
      console.error('获取收藏列表失败:', error);
      throw error;
    }
  },

  // 获取用户收藏列表（不分页）
  getUserFavorites: async (userId) => {
    try {
      const response = await uni.request({
        url: `${config.baseUrl}/api/favorites/user/${userId}`,
        method: 'GET'
      });
      
      return response.data;
    } catch (error) {
      console.error('获取收藏列表失败:', error);
      throw error;
    }
  },

  // 检查商品是否已收藏
  checkFavoriteStatus: async (userId, productCode) => {
    try {
      const response = await uni.request({
        url: `${config.baseUrl}/api/favorites/check?userId=${userId}&productCode=${productCode}`,
        method: 'GET'
      });
      
      return response.data;
    } catch (error) {
      console.error('检查收藏状态失败:', error);
      throw error;
    }
  },

  // 检查商品是否已收藏（别名方法）
  checkFavorite: async (userId, productCode) => {
    return favoritesApi.checkFavoriteStatus(userId, productCode);
  },

  // 获取用户收藏数量
  getUserFavoriteCount: async (userId) => {
    try {
      const response = await uni.request({
        url: `${config.baseUrl}/api/favorites/user/${userId}/count`,
        method: 'GET'
      });
      
      return response.data;
    } catch (error) {
      console.error('获取收藏数量失败:', error);
      throw error;
    }
  }
};

export default favoritesApi; 