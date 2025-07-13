<template>
	<view class="wxLogin-container">
		<!-- 中间居中内容：logo和欢迎语 -->
		<view class="center-section">
			<image class="logo" src="/static/logo.png" mode="aspectFit"></image>
			<text class="welcome-title">农康优选欢迎你</text>
		</view>

		<!-- 底部登录按钮和协议 -->
		<view class="bottom-section">
			<button 
				class="wxLogin-btn primary" 
				:loading="isLoading"
				:disabled="isLoading"
				@click="handleWxLogin"
			>
				<text class="btn-text">{{ isLoading ? '登录中...' : '微信登录' }}</text>
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
import { WxLoginManager } from '../../utils/wxLogin.js'

export default {
	data() {
		return {
			isLoading: false,
			wxLoginRetryCount: 0,
			maxRetryCount: 3
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
		
		// 处理微信登录
		async handleWxLogin() {
			this.navigateToHome();
		},
		

		
		// 处理登录错误
		handleLoginError(error) {
			this.wxLoginRetryCount++
			
			let errorMessage = '登录失败，请重试'
			
			// 根据错误类型显示不同的提示
			if (error.message.includes('请在微信小程序中使用')) {
				errorMessage = '请在微信小程序中使用'
			} else if (error.message.includes('用户取消')) {
				errorMessage = '您取消了登录，请重新登录'
			} else if (error.message.includes('网络')) {
				errorMessage = '网络连接失败，请检查网络后重试'
			}
			
			if (this.wxLoginRetryCount >= this.maxRetryCount) {
				uni.showModal({
					title: '登录失败',
					content: errorMessage + '\n\n如果问题持续存在，请检查：\n1. 网络连接是否正常\n2. 微信版本是否最新\n3. 小程序权限是否开启',
					showCancel: false,
					confirmText: '我知道了'
				})
				this.wxLoginRetryCount = 0
			} else {
				uni.showToast({
					title: errorMessage,
					icon: 'none',
					duration: 2000
				})
			}
		},
		
		// 跳转到首页
		navigateToHome() {
			uni.switchTab({
				url: '/pages/index/index'
			})
		},
		
		// 显示隐私政策
		showPrivacyPolicy() {
			uni.showModal({
				title: '隐私政策',
				content: '我们承诺保护您的隐私信息，不会泄露给第三方。',
				showCancel: false,
				confirmText: '我知道了'
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
.wxLogin-btn {
	width: 100%;
	height: 100rpx;
	border-radius: 50rpx;
	border: none;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 32rpx;
	font-weight: bold;
	margin-bottom: 30rpx;
	transition: all 0.3s ease;
	background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
	color: #fff;
	box-shadow: 0 8rpx 20rpx rgba(76, 175, 80, 0.15);
}
.wxLogin-btn:active {
	transform: translateY(2rpx);
	box-shadow: 0 4rpx 10rpx rgba(76, 175, 80, 0.15);
}
.wxLogin-btn:disabled {
	opacity: 0.7;
	transform: none;
}
.btn-text {
	margin-left: 0;
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
</style> 