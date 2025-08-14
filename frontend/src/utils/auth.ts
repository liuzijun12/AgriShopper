import { useUserStore } from '@/store/modules/user'

/**
 * 检查用户是否已登录（静默检查，不跳转）
 * @returns 返回用户是否已登录
 */
export function isLoggedIn(): boolean {
  try {
    const userStore = useUserStore()
    return userStore.isLoggedIn
  } catch (error) {
    // 如果store未初始化，回退到本地存储检查
    const userInfo = uni.getStorageSync('userInfo');
    const isLoggedIn = uni.getStorageSync('isLoggedIn');
    return !!(userInfo && userInfo.username && isLoggedIn);
  }
}

/**
 * 获取当前用户信息
 * @returns 返回用户信息，如果不存在则返回null
 */
export function getUserInfo(): any {
  try {
    const userStore = useUserStore()
    return userStore.userInfo
  } catch (error) {
    // 如果store未初始化，回退到本地存储
    return uni.getStorageSync('userInfo') || null;
  }
}

/**
 * 清除用户登录状态
 */
export function logout(): void {
  try {
    const userStore = useUserStore()
    userStore.logout()
  } catch (error) {
    // 如果store未初始化，直接清除本地存储
    uni.removeStorageSync('userInfo');
    uni.removeStorageSync('isLoggedIn');
  }
}
