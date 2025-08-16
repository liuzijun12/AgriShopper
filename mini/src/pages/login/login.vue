<template>
  <view class="container">
    <view class="login-form">
      <view class="title">
        <text class="title-text">用户登录</text>
      </view>
      
      <view class="form-item">
        <text class="label">用户名</text>
        <input 
          class="input" 
          v-model="username" 
          placeholder="请输入用户名"
          type="text"
        />
      </view>
      
      <view class="form-item">
        <text class="label">密码</text>
        <input 
          class="input" 
          v-model="password" 
          placeholder="请输入密码"
          type="password"
        />
      </view>
      
      <view class="error-msg" v-if="errorMessage">
        <text class="error-text">{{ errorMessage }}</text>
      </view>
      
      <button class="login-btn" @click="handleLogin" :disabled="loading">
        {{ loading ? '登录中...' : '登录' }}
      </button>
      
      <!-- 微信登录按钮 -->
      <!-- #ifdef MP-WEIXIN -->
      <button class="wechat-login-btn" @click="handleWechatLogin" :disabled="wechatLoading">
        {{ wechatLoading ? '微信登录中...' : '微信登录' }}
      </button>
      <!-- #endif -->
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      username: '',
      password: '',
      errorMessage: '',
      loading: false,
      wechatLoading: false
    };
  },
  methods: {
    handleLogin() {
      this.errorMessage = '';
      if (!this.username.trim() || !this.password.trim()) {
        this.errorMessage = '请输入用户名和密码';
        return;
      }
      this.loading = true;
      
      setTimeout(() => {
        if (this.username === 'admin' && this.password === '123456') {
          // 管理员登录
          uni.showToast({ title: '登录成功', icon: 'success' });
          uni.setStorageSync('userInfo', { username: 'admin', userType: 'merchant' });
          
          uni.reLaunch({
            url: '/ubPackages_merchant/pages/index/index',
          });
          
        } else if (this.username === 'user' && this.password === '123456') {
          // 普通用户登录
          uni.showToast({ title: '登录成功', icon: 'success' });
          uni.setStorageSync('userInfo', { username: 'user', userType: 'user' });
          uni.$emit('refreshTabBar');
          
          // 使用统一的跳转逻辑
          uni.reLaunch({
            url: '/pages/index/index'
          });
          
        } else {
          this.errorMessage = '用户名或密码错误';
          this.password = '';
        }
        this.loading = false;
      }, 1000);
    },
    
    async handleWechatLogin() {
      this.wechatLoading = true;
      this.errorMessage = '';
      
      try {
        // 1. 获取微信登录凭证
        const loginRes = await this.getWxLoginCode();
        console.log('微信登录凭证:', loginRes);
        
        // 2. 获取用户信息
        const userInfo = await this.getWxUserInfo();
        console.log('微信用户信息:', userInfo);
        
        // 3. 发送到后端验证（这里模拟后端验证）
        const authResult = await this.authenticateWithBackend(loginRes.code, userInfo);
        
        // 4. 登录成功，保存用户信息（确保格式符合验证要求）
        const completeUserInfo = {
          username: userInfo.userInfo.nickName || `微信用户_${authResult.openid.slice(-6)}`, // 确保username不为空
          loginType: 'wechat',
          userType: 'user',
          openid: authResult.openid,
          unionid: authResult.unionid,
          nickName: userInfo.userInfo.nickName || '微信用户',
          avatarUrl: userInfo.userInfo.avatarUrl || '',
          gender: userInfo.userInfo.gender || 0,
          province: userInfo.userInfo.province || '',
          city: userInfo.userInfo.city || '',
          country: userInfo.userInfo.country || '',
          // 添加时间戳，确保数据有效性
          loginTime: Date.now()
        };
        
        uni.setStorageSync('userInfo', completeUserInfo);
        console.log('微信登录保存的用户信息:', completeUserInfo);
        console.log('存储验证:', uni.getStorageSync('userInfo'));
        
        uni.showToast({ title: '微信登录成功', icon: 'success' });
        
        // 确保状态同步
        uni.$emit('refreshTabBar');
        uni.$emit('userLoginSuccess', completeUserInfo);
        
        // 延迟跳转，确保状态已更新
        setTimeout(() => {
          uni.reLaunch({
            url: '/pages/index/index'
          });
        }, 500);
        
      } catch (error) {
        console.error('微信登录失败:', error);
        this.errorMessage = error.message || '微信登录失败，请重试';
        uni.showToast({ title: '登录失败', icon: 'none' });
      } finally {
        this.wechatLoading = false;
      }
    },
    
    // 获取微信登录凭证
    getWxLoginCode() {
      return new Promise((resolve, reject) => {
        uni.login({
          provider: 'weixin',
          success: (res) => {
            if (res.code) {
              resolve(res);
            } else {
              reject(new Error('获取微信登录凭证失败'));
            }
          },
          fail: (err) => {
            console.error('微信登录失败:', err);
            reject(new Error('微信登录失败'));
          }
        });
      });
    },
    
    // 获取微信用户信息
    getWxUserInfo() {
      return new Promise((resolve, reject) => {
        // 检查是否已授权
        uni.getSetting({
          success: (res) => {
            if (res.authSetting['scope.userInfo']) {
              // 已授权，直接获取用户信息
              this.getUserInfoDirectly(resolve, reject);
            } else {
              // 未授权，请求用户授权
              uni.authorize({
                scope: 'scope.userInfo',
                success: () => {
                  this.getUserInfoDirectly(resolve, reject);
                },
                fail: () => {
                  // 用户拒绝授权，引导用户手动授权
                  uni.showModal({
                    title: '获取用户信息',
                    content: '需要获取您的微信用户信息，请授权后继续',
                    confirmText: '去授权',
                    success: (modalRes) => {
                      if (modalRes.confirm) {
                        uni.openSetting({
                          success: (settingRes) => {
                            if (settingRes.authSetting['scope.userInfo']) {
                              this.getUserInfoDirectly(resolve, reject);
                            } else {
                              reject(new Error('需要用户信息授权才能登录'));
                            }
                          }
                        });
                      } else {
                        reject(new Error('需要用户信息授权才能登录'));
                      }
                    }
                  });
                }
              });
            }
          },
          fail: (err) => {
            console.error('获取设置失败:', err);
            reject(new Error('获取用户设置失败'));
          }
        });
      });
    },
    
    // 直接获取用户信息
    getUserInfoDirectly(resolve, reject) {
      uni.getUserInfo({
        success: (res) => {
          console.log('获取用户信息成功:', res);
          resolve(res);
        },
        fail: (err) => {
          console.error('获取用户信息失败:', err);
          reject(new Error('获取用户信息失败'));
        }
      });
    },
    
    // 与后端验证登录信息（模拟）
    authenticateWithBackend(code, userInfo) {
      return new Promise((resolve, reject) => {
        // 这里应该调用你的后端API
        // 发送code和userInfo到后端，后端通过code获取openid和session_key
        
        // 模拟后端API调用
        setTimeout(() => {
          // 模拟成功响应
          resolve({
            success: true,
            openid: 'mock_openid_' + Date.now(),
            unionid: 'mock_unionid_' + Date.now(),
            session_key: 'mock_session_key',
            token: 'mock_jwt_token'
          });
          
          // 如果要模拟失败，可以这样：
          // reject(new Error('后端验证失败'));
        }, 1500);
      });
    },
    
    // 获取性别文本
    getGenderText(gender) {
      const genderMap = {
        0: '未知',
        1: '男',
        2: '女'
      };
      return genderMap[gender] || '未知';
    }
  }
};
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 40rpx;
}

.login-form {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 60rpx 40rpx;
  width: 100%;
  max-width: 600rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  margin-bottom: 60rpx;
}

.title-text {
  font-size: 48rpx;
  font-weight: bold;
  color: #333;
}

.form-item {
  margin-bottom: 40rpx;
}

.label {
  display: block;
  font-size: 28rpx;
  color: #666;
  margin-bottom: 16rpx;
}

.input {
  width: 100%;
  height: 88rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 8rpx;
  padding: 0 24rpx;
  font-size: 32rpx;
  background-color: #fafafa;
}

.input:focus {
  border-color: #007aff;
  background-color: #fff;
}

.error-msg {
  margin-bottom: 20rpx;
}

.error-text {
  color: #ff4757;
  font-size: 26rpx;
}

.login-btn {
  width: 100%;
  height: 88rpx;
  background-color: #007aff;
  color: white;
  border: none;
  border-radius: 8rpx;
  font-size: 32rpx;
  font-weight: bold;
  margin-top: 40rpx;
}

.login-btn:disabled {
  background-color: #ccc;
}

/* 微信登录按钮 */
.wechat-login-btn {
  width: 100%;
  height: 88rpx;
  background-color: #07c160;
  color: white;
  border: none;
  border-radius: 8rpx;
  font-size: 32rpx;
  font-weight: bold;
  margin-top: 20rpx;
}

.wechat-login-btn:disabled {
  background-color: #a8d8b8;
}
</style>