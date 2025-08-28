import request, { publicRequest } from "@/utils/request";

const AUTH_BASE_URL = "/api/v1/auth";

export interface LoginData {
  username: string;
  password: string;
}

export interface WxLoginData {
  code: string;
  encryptedData?: string;
  iv?: string;
  phoneCode?: string;
}

export interface LoginResult {
  accessToken: string;
  refreshToken?: string;
  tokenType: string;
  expiresIn: number;
  isNewUser?: boolean;
  isProfileComplete?: boolean;
  // 微信用户信息字段 - 与后端WxLoginResponseDTO匹配
  openid?: string;
  unionid?: string;
  nickname?: string;  // 注意：后端返回的是nickname，不是nickName
  avatar?: string;    // 注意：后端返回的是avatar，不是avatarUrl
  gender?: number;
  province?: string;
  city?: string;
  country?: string;
}

const AuthAPI = {
  /**
   * 账号密码登录
   * @param data 登录表单数据
   * @returns 登录结果
   */
  login(data: LoginData): Promise<LoginResult> {
    const formData = {
      username: data.username,
      password: data.password,
    };

    return publicRequest<LoginResult>({
      url: `${AUTH_BASE_URL}/login`,
      method: "POST",
      data: formData,
      header: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
    });
  },

  /**
   * 微信小程序授权登录 (使用code和用户信息)
   * @param code 微信登录凭证
   * @param userInfo 用户信息对象
   * @returns 登录结果
   */
  loginByWxMiniAppNew(code: string, userInfo?: any): Promise<LoginResult> {
    console.log('=== 调用loginByWxMiniAppNew ===');
    console.log('传入的code:', code);
    console.log('传入的userInfo:', userInfo);
    
    return publicRequest<LoginResult>({
      url: `${AUTH_BASE_URL}/wx/miniapp/code-login`,
      method: "POST",
      data: { 
        code,
        userInfo: userInfo || null
      },
    }).then(result => {
      console.log('=== loginByWxMiniAppNew API返回结果详情 ===');
      console.log('result类型:', typeof result);
      console.log('result是否为null:', result === null);
      console.log('result是否为undefined:', result === undefined);
      console.log('result的所有属性:', Object.keys(result || {}));
      console.log('result.openid:', result?.openid);
      console.log('result.unionid:', result?.unionid);
      console.log('result.accessToken:', result?.accessToken);
      console.log('result.nickname:', result?.nickname);
      console.log('result.avatar:', result?.avatar);
      console.log('result.gender:', result?.gender);
      return result;
    }).catch(error => {
      console.error('loginByWxMiniAppNew API调用失败:', error);
      throw error;
    });
  },

  // 为了兼容性，保留旧方法名但实际调用新方法
  loginByWxMiniAppCode(code: string, userInfo?: any): Promise<LoginResult> {
    console.log('=== 强制使用新方法 ===');
    console.log('=== 调用loginByWxMiniAppCode（实际调用新方法） ===');
    console.log('传入的code:', code);
    console.log('传入的userInfo:', userInfo);
    
    return publicRequest<LoginResult>({
      url: `${AUTH_BASE_URL}/wx/miniapp/code-login`,
      method: "POST",
      data: { 
        code,
        userInfo: userInfo || null
      },
    }).then(result => {
      console.log('=== 强制使用新方法 - API返回结果详情 ===');
      console.log('result类型:', typeof result);
      console.log('result是否为null:', result === null);
      console.log('result是否为undefined:', result === undefined);
      console.log('result的所有属性:', Object.keys(result || {}));
      console.log('result.openid:', result?.openid);
      console.log('result.unionid:', result?.unionid);
      console.log('result.accessToken:', result?.accessToken);
      console.log('result.nickname:', result?.nickname);
      console.log('result.avatar:', result?.avatar);
      console.log('result.gender:', result?.gender);
      return result;
    }).catch(error => {
      console.error('强制使用新方法 - API调用失败:', error);
      throw error;
    });
  },

  /**
   * 微信小程序获取用户信息
   * @param encryptedData 加密数据
   * @param iv 加密算法的初始向量
   * @param sessionKey 会话密钥
   * @returns 用户信息
   */
  getWxUserInfo(encryptedData: string, iv: string, sessionKey: string): Promise<any> {
    return publicRequest<any>({
      url: `${AUTH_BASE_URL}/wx/miniapp/user-info`,
      method: "POST",
      data: { 
        encryptedData,
        iv,
        sessionKey
      },
    });
  },

  /**
   * 微信小程序获取手机号
   * @param code 手机号获取凭证
   * @returns 手机号信息
   */
  getWxPhoneNumber(code: string): Promise<any> {
    return publicRequest<any>({
      url: `${AUTH_BASE_URL}/wx/miniapp/phone`,
      method: "POST",
      data: { code },
    });
  },

  /**
   * 微信小程序手机号授权登录
   * @param data 包含code、encryptedData、iv等手机号相关数据
   * @returns 登录结果
   */
  loginByWxMiniAppPhone(data: WxLoginData): Promise<LoginResult> {
    return publicRequest<LoginResult>({
      url: `${AUTH_BASE_URL}/wx/miniapp/phone-login`,
      method: "POST",
      data,
    });
  },

  /**
   * 检查会话有效性
   * @returns 会话是否有效
   */
  checkSession(): Promise<{ valid: boolean }> {
    return request<{ valid: boolean }>({
      url: `${AUTH_BASE_URL}/check-session`,
      method: "GET",
    });
  },

  /**
   * 登出
   * @returns 登出结果
   */
  logout(): Promise<any> {
    return request<any>({
      url: `${AUTH_BASE_URL}/logout`,
      method: "POST",
      skipAutoRedirect: true, // 跳过401错误时的自动跳转
    });
  },

  /**
   * 刷新令牌
   * @param refreshToken 刷新令牌
   * @returns 新的访问令牌
   */
  refreshToken(refreshToken: string): Promise<{ accessToken: string; expiresIn: number }> {
    return publicRequest<{ accessToken: string; expiresIn: number }>({
      url: `${AUTH_BASE_URL}/refresh-token`,
      method: "POST",
      data: { refreshToken },
    });
  },

  /**
   * 更新微信用户信息
   * @param openid 微信OpenID
   * @param userInfo 用户信息
   * @returns 更新结果
   */
  updateWxUserInfo(openid: string, userInfo: any): Promise<any> {
    return publicRequest<any>({
      url: `${AUTH_BASE_URL}/wx/update-user-info`,
      method: "POST",
      data: { openid, userInfo },
    });
  },
};

export default AuthAPI;
