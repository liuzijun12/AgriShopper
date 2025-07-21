// 高德地图API服务
import { AMAP_CONFIG, amapCache, generateCacheKey, getApiKey } from '../config/amap.js'

/**
 * 获取省份列表
 */
export const getProvinces = async () => {
  try {
    const response = await uni.request({
      url: `${AMAP_CONFIG.BASE_URL}/config/district`,
      method: 'GET',
      data: {
        key: getApiKey(),
        keywords: '中国',
        subdistrict: 1,
        extensions: 'base'
      }
    })
    
    console.log('高德地图API响应:', response)
    
    if (response.data && response.data.status === '1') {
      return response.data.districts[0].districts || []
    } else {
      throw new Error(response.data?.info || '获取省份数据失败')
    }
  } catch (error) {
    console.error('获取省份数据失败:', error)
    throw error
  }
}

/**
 * 获取城市列表
 * @param {string} provinceCode - 省份编码
 */
export const getCities = async (provinceCode) => {
  try {
    const response = await uni.request({
      url: `${AMAP_CONFIG.BASE_URL}/config/district`,
      method: 'GET',
      data: {
        key: getApiKey(),
        keywords: provinceCode,
        subdistrict: 1,
        extensions: 'base'
      }
    })
    
    if (response.data && response.data.status === '1') {
      return response.data.districts[0].districts || []
    } else {
      throw new Error(response.data?.info || '获取城市数据失败')
    }
  } catch (error) {
    console.error('获取城市数据失败:', error)
    throw error
  }
}

/**
 * 获取区县列表
 * @param {string} cityCode - 城市编码
 */
export const getDistricts = async (cityCode) => {
  try {
    const response = await uni.request({
      url: `${AMAP_CONFIG.BASE_URL}/config/district`,
      method: 'GET',
      data: {
        key: getApiKey(),
        keywords: cityCode,
        subdistrict: 1,
        extensions: 'base'
      }
    })
    
    if (response.data && response.data.status === '1') {
      return response.data.districts[0].districts || []
    } else {
      throw new Error(response.data?.info || '获取区县数据失败')
    }
  } catch (error) {
    console.error('获取区县数据失败:', error)
    throw error
  }
}

/**
 * 根据关键词搜索地址
 * @param {string} keywords - 搜索关键词
 * @param {string} city - 城市名称（可选）
 */
export const searchAddress = async (keywords, city = '') => {
  try {
    const params = {
      key: getApiKey(),
      keywords: keywords,
      extensions: 'base'
    }
    
    if (city) {
      params.city = city
    }
    
    const response = await uni.request({
      url: `${AMAP_CONFIG.BASE_URL}/place/text`,
      method: 'GET',
      data: params,
      timeout: AMAP_CONFIG.TIMEOUT
    })
    
    if (response.data.status === '1') {
      return response.data.pois || []
    } else {
      throw new Error(response.data.info || '搜索地址失败')
    }
  } catch (error) {
    console.error('搜索地址失败:', error)
    throw error
  }
}

/**
 * 逆地理编码 - 根据经纬度获取地址信息
 * @param {number} longitude - 经度
 * @param {number} latitude - 纬度
 */
export const reverseGeocode = async (longitude, latitude) => {
  try {
    const response = await uni.request({
      url: `${AMAP_CONFIG.BASE_URL}/geocode/regeo`,
      method: 'GET',
      data: {
        key: getApiKey(),
        location: `${longitude},${latitude}`,
        extensions: 'base'
      },
      timeout: AMAP_CONFIG.TIMEOUT
    })
    
    if (response.data.status === '1') {
      return response.data.regeocode || {}
    } else {
      throw new Error(response.data.info || '逆地理编码失败')
    }
  } catch (error) {
    console.error('逆地理编码失败:', error)
    throw error
  }
}

/**
 * 地理编码 - 根据地址获取经纬度
 * @param {string} address - 地址
 * @param {string} city - 城市名称（可选）
 */
export const geocode = async (address, city = '') => {
  try {
    const params = {
      key: getApiKey(),
      address: address,
      extensions: 'base'
    }
    
    if (city) {
      params.city = city
    }
    
    const response = await uni.request({
      url: `${AMAP_CONFIG.BASE_URL}/geocode/geo`,
      method: 'GET',
      data: params,
      timeout: AMAP_CONFIG.TIMEOUT
    })
    
    if (response.data.status === '1') {
      return response.data.geocodes || []
    } else {
      throw new Error(response.data.info || '地理编码失败')
    }
  } catch (error) {
    console.error('地理编码失败:', error)
    throw error
  }
}

export default {
  getProvinces,
  getCities,
  getDistricts,
  searchAddress,
  reverseGeocode,
  geocode
} 