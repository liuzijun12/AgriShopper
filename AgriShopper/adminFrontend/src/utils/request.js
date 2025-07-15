import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const service = axios.create({
  baseURL: '', // 使用相对路径，通过Vue代理访问后端
  timeout: 15000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    console.log('=== 发送请求 ===')
    console.log('请求URL:', config.url)
    console.log('请求方法:', config.method)
    console.log('请求数据:', config.data)
    console.log('请求头:', config.headers)
    
    // 从localStorage获取token
    const token = localStorage.getItem('adminToken')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误：', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    console.log('=== 收到响应 ===')
    console.log('响应状态:', response.status)
    console.log('响应数据:', response.data)
    
    const res = response.data

    // 如果后端返回的是标准的API响应格式
    if (res.code !== undefined) {
      console.log('标准API响应格式，code:', res.code)
      if (res.code !== 200) {
        console.log('❌ 业务错误:', res.message)
        ElMessage({
          message: res.message || '请求失败',
          type: 'error',
          duration: 5 * 1000
        })
        return Promise.reject(new Error(res.message || '请求失败'))
      } else {
        console.log('✅ 业务成功')
        return res
      }
    } else {
      console.log('非标准格式，包装成标准格式')
      // 如果后端直接返回数据，包装成标准格式
      return {
        code: 200,
        message: 'success',
        data: res
      }
    }
  },
  error => {
    console.error('=== 响应错误 ===')
    console.error('错误对象:', error)
    console.error('错误响应:', error.response)
    console.error('错误请求:', error.request)
    console.error('错误消息:', error.message)
    
    // 如果是401错误，清除token并跳转到登录页
    if (error.response && error.response.status === 401) {
      console.log('401错误，清除token并跳转登录页')
      localStorage.removeItem('adminToken')
      localStorage.removeItem('adminInfo')
      window.location.href = '/login'
      return Promise.reject(error)
    }
    
    ElMessage({
      message: error.message || '请求失败',
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service 