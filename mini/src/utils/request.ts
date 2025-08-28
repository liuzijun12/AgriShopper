import { getUserInfo } from "./auth";

// 请求配置
interface RequestOptions<T = any> {
  url: string;
  method: "GET" | "POST" | "PUT" | "DELETE";
  data?: T;
  header?: Record<string, string>;
  timeout?: number;
  responseType?: "text" | "arraybuffer";
  skipAuth?: boolean; // 标记是否跳过认证
  skipAutoRedirect?: boolean; // 标记401错误时是否跳过自动跳转
}

// 请求函数
function request<T = any>(options: RequestOptions): Promise<T> {
  return new Promise<T>((resolve, reject) => {
    // 构建请求头
    const header = Object.assign({}, options.header || {});

    // 检查是否需要添加认证令牌
    if (!options.skipAuth) {
      // 🔥 修复：直接使用 accessToken 而不是 userInfo.username
      const accessToken = uni.getStorageSync('accessToken');
      if (accessToken) {
        header["Authorization"] = `Bearer ${accessToken}`;
      } else {
        // 需要认证但没有令牌，跳转到登录页
        uni.navigateTo({
          url: "/pages/login/login",
        });
        return reject(new Error("请先登录"));
      }
    }

    // 根据平台决定URL前缀
    let requestUrl = options.url;
    
    // 强制使用本地开发地址
    const apiUrl = 'http://localhost:8989';
    
    // #ifdef MP-WEIXIN
    // 微信小程序环境，使用完整URL
    requestUrl = `${apiUrl}${options.url}`;
    // #endif

    // #ifndef MP-WEIXIN
    // 非微信小程序环境，使用代理前缀
    requestUrl = `${apiUrl}${options.url}`;
    // #endif

    // 统一处理请求
    uni.request({
      url: requestUrl,
      method: options.method,
      data: options.data,
      header,
      timeout: options.timeout || 30000,
      responseType: options.responseType,
      success: (res: any) => {
        // 🔥 强制调试：检查响应数据结构
        console.log('🔥 强制调试 - 原始响应数据:', JSON.stringify(res.data, null, 2));
        console.log('🔥 强制调试 - res.data.data是否存在:', res.data.data !== undefined);
        console.log('🔥 强制调试 - res.data.data内容:', res.data.data);
        
        // 请求成功
        if (res.statusCode >= 200 && res.statusCode < 300) {
          // 检查后端返回的数据结构
          // 如果res.data.data存在，说明数据被包装在data字段中
          // 否则直接使用res.data
          const responseData = res.data.data !== undefined ? res.data.data : res.data;
          
          // 🔥 强制调试：检查最终返回的数据
          console.log('🔥 强制调试 - 最终返回的数据:', JSON.stringify(responseData, null, 2));
          console.log('🔥 强制调试 - responseData.openid:', responseData?.openid);
          console.log('🔥 强制调试 - responseData.unionid:', responseData?.unionid);
          
          resolve(responseData);
        }
        // 未授权错误
        else if (res.statusCode === 401) {
          // 如果需要认证且未授权，且没有跳过自动跳转，则跳转到登录页
          if (!options.skipAuth && !options.skipAutoRedirect) {
            uni.navigateTo({
              url: "/pages/login/login",
            });
          }
          reject(new Error(res.data.message || "未授权，请重新登录"));
        }
        // 其他错误
        else {
          const errorMsg = res.data.message || `请求失败: ${res.statusCode}`;
          reject(new Error(errorMsg));
        }
      },
      fail: (err) => {
        reject(new Error(err.errMsg || "网络请求失败"));
      },
    });
  });
}

/**
 * 无需认证的请求
 * @param options 请求配置
 */
export function publicRequest<T = any>(options: RequestOptions): Promise<T> {
  return request<T>({
    ...options,
    skipAuth: true,
  });
}

export default request;