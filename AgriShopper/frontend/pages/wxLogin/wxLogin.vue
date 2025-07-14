<template>
	<view class="wxLogin-container">
		<!-- 中间居中内容：logo和欢迎语 -->
		<view class="center-section">
			<image class="logo" src="/static/logo.png" mode="aspectFit"></image>
			<text class="welcome-title">农康优选欢迎你</text>
		</view>

		<!-- 底部登录按钮和协议 -->
		<view class="bottom-section">
			<!-- 原生微信授权按钮 -->
			<button 
				class="get-user-info-btn"
				open-type="getUserInfo"
				@getuserinfo="onGetUserInfo"
				:disabled="userProfileData"
			>
				{{ userProfileData ? '已获取用户信息' : '微信一键授权登录' }}
			</button>
			<!-- 微信登录按钮 -->
			<button 
				class="wx-login-btn" 
				:disabled="isLoading || !userProfileData"
				@click="handleWxLogin"
			>
				{{ isLoading ? '登录中...' : userProfileData ? '微信登录' : '请先授权' }}
			</button>
			<view class="wxLogin-tips">
				<text class="tip-text">登录后可享受完整的购物体验</text>
			</view>
			<view class="privacy-section">
				<text class="privacy-text">
					登录即表示同意
					<text class="link" @click="showPrivacyPolicy">《用户协议》</text>
					和
					<text class="link" @click="showPrivacyPolicy">《隐私政策》</text>
				</text>
			</view>
		</view>

		<!-- 调试信息区域 -->
		<view class="debug-section">
			<view class="debug-title">调试信息</view>
			<view class="debug-item">
				<text>用户信息状态: {{ userProfileData ? '已获取' : '未获取' }}</text>
			</view>
			<view v-if="userProfileData" class="debug-item">
				<text>昵称: {{ userProfileData.nickName }}</text>
			</view>
			<view v-if="userProfileData" class="debug-item">
				<text>性别: {{ userProfileData.gender === 1 ? '男' : userProfileData.gender === 2 ? '女' : '未知' }}</text>
			</view>
			<view v-if="userProfileData" class="debug-item">
				<text>地区: {{ userProfileData.country }} {{ userProfileData.province }} {{ userProfileData.city }}</text>
			</view>
			<view v-if="userProfileData" class="debug-item">
				<text>头像: {{ userProfileData.avatarUrl ? '已获取' : '未获取' }}</text>
			</view>
		</view>

		<!-- 加载遮罩 -->
		<view v-if="isLoading" class="loading-mask">
			<view class="loading-content">
				<view class="loading-spinner"></view>
				<text class="loading-text">正在登录...</text>
			</view>
		</view>

	</view>
</template>

<script>
import { store } from '../../store.js'
import env from '../../config/env.js'

export default {
	data() {
		return {
			isLoading: false,
			userProfileData: null
		}
	},
	
	onLoad() {
		// 页面加载时检查是否已经登录
		this.checkLoginStatus()
	},
	
	methods: {
		// 检查登录状态
		async checkLoginStatus() {
			try {
				const userInfo = store.getUserInfo()
				if (userInfo && userInfo.openid) {
					// 已经登录，直接跳转到首页
					this.navigateToHome()
				}
			} catch (error) {
				console.log('检查登录状态失败:', error)
			}
		},
		
		// 原生微信授权按钮回调
		onGetUserInfo(e) {
			console.log('【原生getUserInfo回调】:', JSON.stringify(e));
			if (e.detail && e.detail.userInfo) {
				this.userProfileData = e.detail.userInfo;
				uni.showToast({ title: '获取成功', icon: 'success' });
			} else {
				uni.showToast({ title: '授权失败', icon: 'none' });
			}
		},
		// 微信登录
		async handleWxLogin() {
			console.log('【点击微信登录按钮】');
			console.log('【userProfileData登录前】:', JSON.stringify(this.userProfileData));
			if (!this.userProfileData) {
				uni.showToast({ title: '请先授权', icon: 'none' });
				return;
			}
			this.isLoading = true;
			try {
				// 1. 获取code
				const loginRes = await new Promise((resolve, reject) => {
					uni.login({
						provider: 'weixin',
						success: resolve,
						fail: reject
					});
				});
				console.log('【uni.login返回】:', JSON.stringify(loginRes));
				// 2. 调用后端接口，传code和userProfileData
				console.log('【请求后端参数】:', JSON.stringify({ code: loginRes.code, userInfo: this.userProfileData }));
				uni.request({
					url: env.getApiUrl('/wxuser/login') + '?code=' + encodeURIComponent(loginRes.code),
					method: 'POST',
					data: {
						code: loginRes.code,
						userInfo: this.userProfileData
					},
					header: {
						'Content-Type': 'application/json'
					},
					success: (res) => {
						console.log('【后端返回】:', JSON.stringify(res));
						if (res.statusCode === 200 && res.data.code === 200) {
							store.setUserInfo(res.data.data);
							uni.showToast({ title: '登录成功', icon: 'success', duration: 1500 });
							setTimeout(() => {
								uni.switchTab({ url: '/pages/index/index' });
							}, 1500);
						} else {
							uni.showToast({ title: res.data.message || '登录失败', icon: 'none' });
						}
					},
					fail: (err) => {
						console.log('【uni.request失败】:', JSON.stringify(err));
						uni.showToast({ title: '网络请求失败', icon: 'none' });
					},
					complete: () => {
						this.isLoading = false;
					}
				});
			} catch (e) {
				this.isLoading = false;
				console.log('【微信登录catch异常】:', JSON.stringify(e));
				uni.showToast({ title: '微信登录失败', icon: 'none' });
			}
		},
		showPrivacyPolicy() {
			uni.showModal({
				title: '隐私政策',
				content: '我们承诺保护您的隐私信息，不会泄露给第三方。',
				showCancel: false,
				confirmText: '我知道了'
			});
		},
		// 跳转到首页
		navigateToHome() {
			uni.switchTab({
				url: '/pages/index/index'
			})
		}
	}
}
</script>

<style scoped>
.wxLogin-container {
	position: relative;
	min-height: 100vh;
	background: linear-gradient(135deg, #f8f8f8 0%, #e8f5e9 100%);
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	align-items: stretch;
}
.center-section {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	margin-top: 0;
	margin-bottom: 0;
}
.logo {
	width: 180rpx;
	height: 180rpx;
	margin-bottom: 30rpx;
	border-radius: 50%;
	background: #fff;
	box-shadow: 0 8rpx 32rpx rgba(76, 175, 80, 0.08);
}
.welcome-title {
	font-size: 44rpx;
	font-weight: bold;
	color: #2e7d32;
	margin-top: 10rpx;
	letter-spacing: 2rpx;
}
.bottom-section {
	width: 100%;
	padding: 0 60rpx 60rpx 60rpx;
	box-sizing: border-box;
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-bottom: 0;
}
.get-user-info-btn {
	width: 100%;
	height: 100rpx;
	border-radius: 50rpx;
	border: none;
	background: linear-gradient(135deg, #2196F3, #1976D2);
	color: #fff;
	font-size: 32rpx;
	font-weight: bold;
	display: flex;
	align-items: center;
	justify-content: center;
	transition: all 0.3s ease;
	box-shadow: 0 8rpx 20rpx rgba(33, 150, 243, 0.15);
	margin-bottom: 20rpx;
}
.get-user-info-btn:active {
	transform: scale(0.98);
	background: linear-gradient(135deg, #1976D2, #1565C0);
}
.get-user-info-btn:disabled {
	background: linear-gradient(135deg, #4CAF50, #45a049);
	color: #fff;
	box-shadow: 0 8rpx 20rpx rgba(76, 175, 80, 0.15);
}
.wx-login-btn {
	width: 100%;
	height: 100rpx;
	border-radius: 50rpx;
	border: none;
	background: linear-gradient(135deg, #4CAF50, #45a049);
	color: #fff;
	font-size: 32rpx;
	font-weight: bold;
	display: flex;
	align-items: center;
	justify-content: center;
	transition: all 0.3s ease;
	box-shadow: 0 8rpx 20rpx rgba(76, 175, 80, 0.15);
	margin-bottom: 20rpx;
}
.wx-login-btn:active {
	transform: scale(0.98);
	background: linear-gradient(135deg, #45a049, #3d8b40);
}
.wx-login-btn:disabled {
	background: #ccc;
	color: #999;
	box-shadow: none;
}
.wxLogin-tips {
	text-align: center;
	margin-bottom: 18rpx;
}
.tip-text {
	font-size: 26rpx;
	color: #388e3c;
}
.privacy-section {
	text-align: center;
	margin-bottom: 0;
}
.privacy-text {
	font-size: 24rpx;
	color: #888;
	line-height: 1.5;
}
.link {
	color: #388e3c;
	text-decoration: underline;
}
.loading-mask {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(0, 0, 0, 0.5);
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 9999;
}
.loading-content {
	background: #ffffff;
	border-radius: 20rpx;
	padding: 60rpx 40rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
}
.loading-spinner {
	width: 60rpx;
	height: 60rpx;
	border: 4rpx solid #f3f3f3;
	border-top: 4rpx solid #4CAF50;
	border-radius: 50%;
	animation: spin 1s linear infinite;
	margin-bottom: 20rpx;
}
.loading-text {
	font-size: 28rpx;
	color: #333333;
}
@keyframes spin {
	0% { transform: rotate(0deg); }
	100% { transform: rotate(360deg); }
}
.debug-section {
	margin: 20rpx;
	padding: 20rpx;
	background-color: #f8f9fa;
	border-radius: 12rpx;
	border: 1px solid #e9ecef;
}
.debug-title {
	font-size: 16px;
	font-weight: bold;
	color: #495057;
	margin-bottom: 16rpx;
}
.debug-item {
	margin-bottom: 12rpx;
	padding: 8rpx 0;
	border-bottom: 1px solid #dee2e6;
}
.debug-item:last-child {
	border-bottom: none;
}
.debug-item text {
	font-size: 12px;
	color: #6c757d;
	word-break: break-all;
}
</style> 