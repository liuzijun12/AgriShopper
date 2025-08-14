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
      
      <!-- 微信登录测试 -->
      <view class="wechat-login-section">
        <view class="divider">
          <text class="divider-text">或</text>
        </view>
        
        <button class="wechat-login-btn" @click="handleWechatLogin" :disabled="wechatLoading">
          {{ wechatLoading ? '获取中...' : '微信登录测试' }}
        </button>
        
        <view class="test-info" v-if="wechatUserInfo">
          <text class="info-title">获取到的用户信息：</text>
          <view class="info-item">
            <text class="info-label">昵称：</text>
            <text class="info-value">{{ wechatUserInfo.nickName || '未获取' }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">头像：</text>
            <image v-if="wechatUserInfo.avatarUrl" class="avatar" :src="wechatUserInfo.avatarUrl" mode="aspectFill" />
            <text v-else class="info-value">未获取</text>
          </view>
          <view class="info-item">
            <text class="info-label">性别：</text>
            <text class="info-value">{{ getGenderText(wechatUserInfo.gender) }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">城市：</text>
            <text class="info-value">{{ wechatUserInfo.city || '未获取' }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">省份：</text>
            <text class="info-value">{{ wechatUserInfo.province || '未获取' }}</text>
          </view>
          
          <button class="use-wechat-info-btn" @click="loginWithWechatInfo">
            使用微信信息登录
          </button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { useUserStore } from '@/store/modules/user'

export default {
  data() {
    return {
      username: '',
      password: '',
      errorMessage: '',
      loading: false,
      wechatLoading: false,
      wechatUserInfo: null
    };
  },
  methods: {
    handleLogin() {
      // 清除之前的错误信息
      this.errorMessage = '';
      
      // 验证输入
      if (!this.username.trim()) {
        this.errorMessage = '请输入用户名';
        return;
      }
      
      if (!this.password.trim()) {
        this.errorMessage = '请输入密码';
        return;
      }
      
      this.loading = true;
      
      // 模拟登录验证
      setTimeout(() => {
        if (this.username === 'admin' && this.password === '123456') {
          // 商户登录
          const userInfo = {
            username: 'admin',
            userType: 'merchant'
          };
          
          // 使用store管理登录状态
          const userStore = useUserStore();
          userStore.login(userInfo);
          
          uni.showToast({
            title: '登录成功',
            icon: 'success'
          });
          
          // 商户登录后直接跳转到商户页面
          setTimeout(() => {
            uni.reLaunch({
              url: '/subPackages_merchant/pages/index/index'
            });
          }, 1500);
          
        } else if (this.username === 'user' && this.password === '123456') {
          // 普通用户登录
          const userInfo = {
            username: 'user',
            userType: 'user'
          };
          
          // 使用store管理登录状态
          const userStore = useUserStore();
          userStore.login(userInfo);
          
          uni.showToast({
            title: '登录成功',
            icon: 'success'
          });
          
          // 普通用户登录后跳转到首页
          setTimeout(() => {
            uni.switchTab({
              url: '/pages/index/index'
            });
          }, 1500);
          
        } else {
          // 登录失败
          this.errorMessage = '用户名或密码错误';
          this.password = ''; // 清空密码
        }
        
        this.loading = false;
      }, 1000);
    },
    
    // 微信登录测试
    handleWechatLogin() {
      this.wechatLoading = true;
      this.errorMessage = '';
      
      // 直接获取用户信息，必须在用户点击事件中调用
      this.getUserProfile();
    },
    
    // 微信登录（在获取用户信息成功后调用）
    wechatLogin() {
      uni.login({
        provider: 'weixin',
        success: (loginRes) => {
          console.log('微信登录成功', loginRes);
          
          if (loginRes.code) {
            // 这里通常需要将code发送到后端换取session_key
            // 为了测试，我们已经有了用户信息，直接完成登录流程
            console.log('获取到code:', loginRes.code);
            this.wechatLoading = false;
            
            uni.showToast({
              title: '微信登录成功',
              icon: 'success'
            });
          } else {
            this.errorMessage = '微信登录失败';
            this.wechatLoading = false;
          }
        },
        fail: (err) => {
          console.error('微信登录失败', err);
          this.errorMessage = '微信登录失败：' + (err.errMsg || '未知错误');
          this.wechatLoading = false;
        }
      });
    },
    
    // 获取用户信息
    getUserProfile() {
      // 新版本微信小程序需要用户主动触发才能获取头像昵称
      uni.getUserProfile({
        desc: '用于完善用户资料',
        success: (res) => {
          console.log('获取用户信息成功', res);
          this.wechatUserInfo = res.userInfo;
          
          // 获取用户信息成功后，再进行微信登录获取code
          this.wechatLogin();
        },
        fail: (err) => {
          console.error('获取用户信息失败', err);
          
          // 如果getUserProfile失败，尝试使用getUserInfo（旧版本兼容）
          uni.getUserInfo({
            success: (res) => {
              console.log('getUserInfo成功', res);
              this.wechatUserInfo = res.userInfo;
              this.wechatLogin();
            },
            fail: (infoErr) => {
              console.error('getUserInfo也失败', infoErr);
              this.errorMessage = '获取用户信息失败，请检查授权设置';
              this.wechatLoading = false;
              
              // 提供一个模拟的用户信息用于测试
              this.createMockUserInfo();
            }
          });
        }
      });
    },
    
    // 创建模拟用户信息（用于开发测试）
    createMockUserInfo() {
      this.wechatUserInfo = {
        nickName: '测试用户',
        avatarUrl: '/static/images/default-avatar.png',
        gender: 1,
        city: '深圳',
        province: '广东',
        country: '中国'
      };
      this.wechatLoading = false;
      
      uni.showToast({
        title: '使用模拟数据测试',
        icon: 'none'
      });
    },
    
    // 使用微信信息登录
    loginWithWechatInfo() {
      if (!this.wechatUserInfo) {
        this.errorMessage = '请先获取微信用户信息';
        return;
      }
      
      const userInfo = {
        username: this.wechatUserInfo.nickName || '微信用户',
        userType: 'user', // 默认为普通用户
        avatar: this.wechatUserInfo.avatarUrl,
        gender: this.wechatUserInfo.gender,
        city: this.wechatUserInfo.city,
        province: this.wechatUserInfo.province,
        nickname: this.wechatUserInfo.nickName
      };
      
      // 使用store管理登录状态
      const userStore = useUserStore();
      userStore.login(userInfo);
      
      uni.showToast({
        title: '微信登录成功',
        icon: 'success'
      });
      
      // 跳转到首页
      setTimeout(() => {
        uni.switchTab({
          url: '/pages/index/index'
        });
      }, 1500);
    },
    
    // 获取性别文本
    getGenderText(gender) {
      switch (gender) {
        case 1: return '男';
        case 2: return '女';
        default: return '未知';
      }
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

/* 微信登录样式 */
.wechat-login-section {
  margin-top: 40rpx;
}

.divider {
  display: flex;
  align-items: center;
  margin: 40rpx 0;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1rpx;
  background-color: #e0e0e0;
}

.divider-text {
  padding: 0 20rpx;
  color: #999;
  font-size: 24rpx;
}

.wechat-login-btn {
  width: 100%;
  height: 88rpx;
  background-color: #07c160;
  color: white;
  border: none;
  border-radius: 8rpx;
  font-size: 32rpx;
  font-weight: bold;
}

.wechat-login-btn:disabled {
  background-color: #ccc;
}

.test-info {
  margin-top: 40rpx;
  padding: 30rpx;
  background-color: #f8f9fa;
  border-radius: 12rpx;
  border: 1rpx solid #e9ecef;
}

.info-title {
  display: block;
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
}

.info-label {
  font-size: 26rpx;
  color: #666;
  width: 120rpx;
}

.info-value {
  font-size: 26rpx;
  color: #333;
  flex: 1;
}

.avatar {
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
}

.use-wechat-info-btn {
  width: 100%;
  height: 70rpx;
  background-color: #007aff;
  color: white;
  border: none;
  border-radius: 8rpx;
  font-size: 28rpx;
  margin-top: 20rpx;
}
</style>