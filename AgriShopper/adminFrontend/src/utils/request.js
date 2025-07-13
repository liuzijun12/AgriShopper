import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 15000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
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
    const res = response.data

    // 如果后端返回的是标准的API响应格式
    if (res.code !== undefined) {
      if (res.code !== 200) {
        ElMessage({
          message: res.message || '请求失败',
          type: 'error',
          duration: 5 * 1000
        })
        return Promise.reject(new Error(res.message || '请求失败'))
      } else {
        return res
      }
    } else {
      // 如果后端直接返回数据，包装成标准格式
      return {
        code: 200,
        message: 'success',
        data: res
      }
    }
  },
  error => {
    console.error('响应错误：', error)
    ElMessage({
      message: error.message || '请求失败',
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service 