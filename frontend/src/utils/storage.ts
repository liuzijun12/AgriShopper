/**
 * 本地存储工具类
 */

const ACCESS_TOKEN_KEY = "access_token";
const REFRESH_TOKEN_KEY = "refresh_token";
const USER_INFO_KEY = "user_info";

class Storage {
  /**
   * 设置存储项
   * @param key 键
   * @param value 值
   */
  static set<T>(key: string, value: T): void {
    try {
      const serializedValue = JSON.stringify(value);
      uni.setStorageSync(key, serializedValue);
    } catch (error) {
      console.error(`Storage set error for key "${key}":`, error);
    }
  }

  /**
   * 获取存储项
   * @param key 键
   * @returns 值
   */
  static get<T>(key: string): T | null {
    try {
      const serializedValue = uni.getStorageSync(key);
      if (serializedValue) {
        return JSON.parse(serializedValue) as T;
      }
      return null;
    } catch (error) {
      console.error(`Storage get error for key "${key}":`, error);
      return null;
    }
  }

  /**
   * 移除存储项
   * @param key 键
   */
  static remove(key: string): void {
    try {
      uni.removeStorageSync(key);
    } catch (error) {
      console.error(`Storage remove error for key "${key}":`, error);
    }
  }

  /**
   * 清空所有存储
   */
  static clear(): void {
    try {
      uni.clearStorageSync();
    } catch (error) {
      console.error("Storage clear error:", error);
    }
  }
}

/**
 * 获取访问令牌
 */
export function getToken(): string | null {
  return Storage.get<string>(ACCESS_TOKEN_KEY) || null;
}

/**
 * 设置访问令牌
 * @param token 访问令牌
 */
export function setToken(token: string): void {
  Storage.set(ACCESS_TOKEN_KEY, token);
}

/**
 * 移除访问令牌
 */
export function removeToken(): void {
  Storage.remove(ACCESS_TOKEN_KEY);
}

/**
 * 获取刷新令牌
 */
export function getRefreshToken(): string | null {
  return Storage.get<string>(REFRESH_TOKEN_KEY) || null;
}

/**
 * 设置刷新令牌
 * @param token 刷新令牌
 */
export function setRefreshToken(token: string): void {
  Storage.set(REFRESH_TOKEN_KEY, token);
}

/**
 * 移除刷新令牌
 */
export function removeRefreshToken(): void {
  Storage.remove(REFRESH_TOKEN_KEY);
}

/**
 * 获取用户信息
 */
export function getUserInfo(): any {
  return Storage.get(USER_INFO_KEY);
}

/**
 * 设置用户信息
 * @param userInfo 用户信息
 */
export function setUserInfo(userInfo: any): void {
  Storage.set(USER_INFO_KEY, userInfo);
}

/**
 * 移除用户信息
 */
export function removeUserInfo(): void {
  Storage.remove(USER_INFO_KEY);
}

/**
 * 清空所有认证信息
 */
export function clearAuthInfo(): void {
  removeToken();
  removeRefreshToken();
  removeUserInfo();
}

export default Storage;
