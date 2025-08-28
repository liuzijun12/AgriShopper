<template>
  <view class="login-page">
    <!-- 简约背景 -->
    <view class="bg-simple">
      <!-- 简单的叶子装饰 -->
      <view class="decoration decoration-1">🌿</view>
      <view class="decoration decoration-2">🍃</view>
      <view class="decoration decoration-3">🌱</view>
      <view class="decoration decoration-4">🌿</view>
    </view>
    
    <!-- 主内容 -->
    <view class="content-wrapper">
      <!-- Logo -->
      <view class="logo-container">
        <view class="logo-card">
          <view class="logo-icon">🌿</view>
        </view>
      </view>
      
      <!-- 标题 -->
      <view class="title-container">
        <text class="app-name">田里有品</text>
        <text class="app-desc">优质农产品 直达您餐桌</text>
      </view>
      
      <!-- 登录按钮 -->
      <view class="button-container">
        <!-- #ifdef MP-WEIXIN -->
        <button 
          class="wechat-button" 
          @click="handleWechatLogin" 
          :disabled="wechatLoading"
        >
                      <view class="button-content">
              <text class="wechat-icon">💬</text>
              <text class="button-text">{{ wechatLoading ? '登录中...' : '微信一键登录' }}</text>
            </view>
        </button>
        <!-- #endif -->
      </view>
      
      <!-- 错误提示 -->
      <view class="error-container" v-if="errorMessage">
        <text class="error-message">{{ errorMessage }}</text>
      </view>
      
      <!-- 协议 -->
      <view class="agreement-container">
        <text class="agreement-text">登录即表示同意</text>
        <text class="agreement-link">《用户协议及隐私政策》</text>
      </view>
    </view>
  </view>
</template>

<script>
import AuthAPI from '@/api/auth';

export default {
  data() {
    return {
      errorMessage: '',
      wechatLoading: false
    };
  },
  methods: {
    // --- 微信一键登录逻辑 ---
    async handleWechatLogin() {
      this.wechatLoading = true;
      this.errorMessage = '';
      
      try {
        console.log('=== 开始微信登录流程 ===');
        
        // 1. 获取微信登录凭证 (code)
        const loginRes = await this.getWxLoginCode();
        console.log('1. 微信登录凭证:', loginRes);
        
        // 2. 先用code进行基础登录
        console.log('2. 开始基础登录...');
        const backendResult = await this.callBackendWxLogin(loginRes.code, null);
        console.log('2. 基础登录结果:', backendResult);
        
        if (backendResult && backendResult.accessToken) {
          // 3. 基础登录成功，询问是否授权获取用户信息
          console.log('3. 基础登录成功，询问用户授权');
          
          const authConfirm = await this.showAuthConfirm();
          if (authConfirm) {
            // 用户同意授权，获取详细信息
            console.log('4. 用户同意授权，获取详细信息');
            await this.getUserInfoAndUpdate(backendResult);
          } else {
            // 用户拒绝授权，使用基础信息登录
            console.log('4. 用户拒绝授权，使用基础信息');
            await this.loginWithBasicInfo(backendResult);
          }
        } else {
          throw new Error('登录失败，请重试');
        }
        
      } catch (error) {
        console.error('微信登录失败:', error);
        this.errorMessage = '登录失败，请重试';
        
        uni.showToast({
          title: '登录失败',
          icon: 'error',
          duration: 2000
        });
      } finally {
        this.wechatLoading = false;
      }
    },
    
    // 显示授权确认对话框
    showAuthConfirm() {
      return new Promise((resolve) => {
        uni.showModal({
          title: '授权提醒',
          content: '为了提供更好的服务，是否允许获取您的微信头像和昵称？',
          confirmText: '同意授权',
          cancelText: '暂不授权',
          success: (res) => {
            resolve(res.confirm);
          },
          fail: () => {
            resolve(false);
          }
        });
      });
    },
    
    // 获取用户信息并更新
    async getUserInfoAndUpdate(backendResult) {
      try {
        const userProfile = await this.getUserProfile();
        console.log('获取到用户详细信息:', userProfile);
        
        // 构建完整的用户信息
        const completeUserInfo = {
          username: userProfile.userInfo.nickName || '微信用户',
          loginType: 'wechat',
          userType: 'user',
          accessToken: backendResult.accessToken,
          refreshToken: backendResult.refreshToken,
          loginTime: Date.now(),
          // 微信用户信息
          openid: backendResult.openid,
          unionid: backendResult.unionid,
          nickname: userProfile.userInfo.nickName,
          avatar: userProfile.userInfo.avatarUrl,
          gender: userProfile.userInfo.gender,
          province: userProfile.userInfo.province,
          city: userProfile.userInfo.city,
          country: userProfile.userInfo.country
        };
        
        console.log('完整用户信息:', completeUserInfo);
        
        // 保存用户信息
        this.saveUserInfo(completeUserInfo);
        
        // 登录成功
        this.loginSuccess('授权成功，登录完成');
        
      } catch (error) {
        console.error('获取用户信息失败:', error);
        // 即使获取详细信息失败，也使用基础信息登录
        await this.loginWithBasicInfo(backendResult);
      }
    },
    
    // 使用基础信息登录
    async loginWithBasicInfo(backendResult) {
      const basicUserInfo = {
        username: '微信用户',
        loginType: 'wechat',
        userType: 'user',
        accessToken: backendResult.accessToken,
        refreshToken: backendResult.refreshToken,
        loginTime: Date.now(),
        // 基础微信信息
        openid: backendResult.openid,
        unionid: backendResult.unionid,
        nickname: '微信用户',
        avatar: '', // 无头像
        gender: 0
      };
      
      console.log('基础用户信息:', basicUserInfo);
      
      // 保存用户信息
      this.saveUserInfo(basicUserInfo);
      
      // 登录成功
      this.loginSuccess('登录成功');
    },
    
    // 保存用户信息到本地
    saveUserInfo(userInfo) {
      try {
        uni.setStorageSync('userInfo', userInfo);
        uni.setStorageSync('accessToken', userInfo.accessToken);
        if (userInfo.refreshToken) {
          uni.setStorageSync('refreshToken', userInfo.refreshToken);
        }
        console.log('用户信息保存成功');
      } catch (error) {
        console.error('保存用户信息失败:', error);
      }
    },
    
    // 登录成功处理
    loginSuccess(message) {
      console.log('登录成功，准备跳转');
      uni.showToast({
        title: message,
        icon: 'success',
        duration: 1500
      });
      
      // 延迟跳转，让用户看到成功提示
      setTimeout(() => {
        uni.reLaunch({
          url: '/pages/index/index'
        });
      }, 1500);
    },
    
    // 获取微信登录凭证
    getWxLoginCode() {
      return new Promise((resolve, reject) => {
        uni.login({
          provider: 'weixin',
          success: resolve,
          fail: reject
        });
      });
    },
    
    // 获取用户信息（新版本API）
    getUserProfile() {
      return new Promise((resolve, reject) => {
        uni.getUserProfile({
          desc: '用于完善会员资料',
          success: resolve,
          fail: reject
        });
      });
    },
    
    // 调用后端微信登录API
    async callBackendWxLogin(code, userProfile) {
      try {
        console.log('=== 开始调用后端API（兼容版本） ===');
        console.log('传入的code:', code);
        console.log('传入的userProfile:', userProfile);
        
        const result = await AuthAPI.loginByWxMiniAppCode(code, userProfile);
        
        console.log('=== 兼容版本API返回结果详情 ===');
        console.log('result类型:', typeof result);
        console.log('result内容:', result);
        console.log('result.openid:', result?.openid);
        console.log('result.accessToken:', result?.accessToken);
        
        return result;
      } catch (error) {
        console.error('后端API调用失败:', error);
        throw error;
      }
    },

    // 格式化价格，确保小数点后两位
    formatPrice(price) {
      if (price === null || price === undefined) {
        return '0.00';
      }
      const num = parseFloat(price);
      if (isNaN(num)) {
        return '0.00';
      }
      return num.toFixed(2);
    }
  }
}
</script>

<style scoped>
.login-page {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(180deg, #e8f5e8 0%, #f5f9f5 100%);
  position: relative;
  overflow: hidden;
}

/* 简约背景装饰 */
.bg-simple {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: 1;
}

.decoration {
  position: absolute;
  font-size: 30rpx;
  opacity: 0.2;
}

.decoration-1 { top: 15%; left: 20%; }
.decoration-2 { top: 25%; right: 25%; }
.decoration-3 { top: 70%; left: 15%; }
.decoration-4 { top: 80%; right: 20%; }

/* 主内容 */
.content-wrapper {
  position: relative;
  z-index: 2;
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 0 60rpx;
}

/* Logo */
.logo-container {
  margin-bottom: 60rpx;
}

.logo-card {
  width: 140rpx;
  height: 140rpx;
  background: #ffffff;
  border-radius: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.08);
}

.logo-icon {
  font-size: 70rpx;
}

/* 标题 */
.title-container {
  text-align: center;
  margin-bottom: 100rpx;
}

.app-name {
  display: block;
  font-size: 56rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 16rpx;
}

.app-desc {
  display: block;
  font-size: 28rpx;
  color: #666666;
}

/* 按钮 */
.button-container {
  width: 100%;
  margin-bottom: 40rpx;
}

.wechat-button {
  width: 100%;
  height: 96rpx;
  background: #07c160;
  border: none;
  border-radius: 48rpx;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6rpx 16rpx rgba(7, 193, 96, 0.2);
}

.wechat-button:disabled {
  background: #b0b0b0;
  box-shadow: none;
}

.button-content {
  display: flex;
  align-items: center;
  justify-content: center;
}

.wechat-icon {
  font-size: 40rpx;
  margin-right: 16rpx;
}

.button-text {
  font-size: 32rpx;
  color: #ffffff;
  font-weight: 500;
}

/* 错误信息 */
.error-container {
  margin-bottom: 30rpx;
}

.error-message {
  font-size: 26rpx;
  color: #ff4757;
  text-align: center;
}

/* 协议 */
.agreement-container {
  position: absolute;
  bottom: 80rpx;
  left: 0;
  right: 0;
  text-align: center;
}

.agreement-text {
  font-size: 22rpx;
  color: #999999;
}

.agreement-link {
  font-size: 22rpx;
  color: #07c160;
}
</style>