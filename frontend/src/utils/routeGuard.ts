/**
 * 路由守卫工具函数
 */

// 检查页面访问权限
export function checkPageAccess(route: string): boolean {
  console.log('检查页面访问权限:', route)

  // 这里可以添加具体的权限检查逻辑
  // 目前先返回true，允许所有访问
  return true
}

// 获取当前用户信息
export function getCurrentUser() {
  try {
    const userInfo = uni.getStorageSync('userInfo')
    return userInfo ? JSON.parse(userInfo) : null
  } catch (error) {
    console.error('获取用户信息失败:', error)
    return null
  }
}

// 退出登录
export function routeLogout() {
  try {
    // 清除本地存储的用户信息
    uni.removeStorageSync('userInfo')
    uni.removeStorageSync('token')

    console.log('用户已退出登录')
    return true
  } catch (error) {
    console.error('退出登录失败:', error)
    return false
  }
}

// 检查是否已登录
export function checkLoginStatus(): boolean {
  const userInfo = getCurrentUser()
  return !!userInfo
}

// 跳转到登录页面
export function navigateToLogin(redirect?: string) {
  const url = redirect
    ? `/pages/login/login?redirect=${encodeURIComponent(redirect)}`
    : '/pages/login/login'

  uni.navigateTo({
    url
  })
}