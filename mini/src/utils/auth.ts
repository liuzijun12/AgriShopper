/**
 * 检查用户是否已登录（静默检查，不跳转）
 * @returns 返回用户是否已登录
 */
export function isLoggedIn(): boolean {
  const userInfo = uni.getStorageSync('userInfo');
  return !!(userInfo && (userInfo.username || userInfo.loginType));
}

/**
 * 获取当前用户信息
 * @returns 返回用户信息，如果不存在则返回null
 */
export function getUserInfo(): any {
  return uni.getStorageSync('userInfo') || null;
}

/**
 * 清除用户登录状态
 */
export function logout(): void {
  uni.removeStorageSync('userInfo');
  uni.removeStorageSync('accessToken');
  uni.removeStorageSync('refreshToken');
}