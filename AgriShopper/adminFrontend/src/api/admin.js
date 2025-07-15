import request from '@/utils/request'

// 管理员登录
export function login(data) {
  console.log('API login被调用，数据:', data)
  return request({
    url: '/api/admin/login',
    method: 'post',
    data
  })
}

// 获取管理员信息
export function getProfile() {
  return request({
    url: '/api/admin/profile',
    method: 'get'
  })
}

// 获取所有管理员
export function getAdmins() {
  return request({
    url: '/api/admin/list',
    method: 'get'
  })
}

// 创建管理员
export function createAdmin(data) {
  return request({
    url: '/api/admin/create',
    method: 'post',
    data
  })
}

// 更新管理员
export function updateAdmin(id, data) {
  return request({
    url: `/api/admin/${id}`,
    method: 'put',
    data
  })
}

// 删除管理员
export function deleteAdmin(id) {
  return request({
    url: `/api/admin/${id}`,
    method: 'delete'
  })
} 