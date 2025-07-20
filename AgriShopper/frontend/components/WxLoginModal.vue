<template>
	<view v-if="visible" class="login-modal-overlay" @click="handleOverlayClick">
		<view class="login-modal" @click.stop>
			<!-- 关闭按钮 -->
			<view class="close-btn" @click="handleClose">
				<text class="close-icon">×</text>
			</view>
			
			<!-- 登录内容 -->
			<view class="login-content">
				<!-- Logo和标题 -->
				<view class="header-section">
					<image class="logo" src="/static/logo.png" mode="aspectFit"></image>
					<text class="title">农康优选</text>
					<text class="subtitle">欢迎使用微信登录</text>
				</view>
				
				<!-- 登录按钮 -->
				<view class="login-section">
					<button 
						class="wxLogin-btn" 
						:loading="isLoading"
						:disabled="isLoading"
						@click="handleWxLogin"
					>
						<image class="wechat-icon" src="/static/tabbar/user.png" mode="aspectFit"></image>
						<text class="btn-text">{{ isLoading ? '登录中...' : '微信登录' }}</text>
					</button>
					
					<text class="login-tip">登录后可享受完整的购物体验</text>
				</view>
				
				<!-- 隐私政策 -->
				<view class="privacy-section">
					<text class="privacy-text">
						登录即表示同意
						<text class="link" @click="showPrivacyPolicy">《用户协议》</text>
						和
						<text class="link" @click="showPrivacyPolicy">《隐私政策》</text>
					</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { store } from '../store.js'
import env from '../config/env.js'

export default {
	name: 'WxLoginModal',
	props: {
		visible: {
			type: Boolean,
			default: false
		}
	},
	data() {
		return {
			isLoading: false,
			wxLoginRetryCount: 0,
			maxRetryCount: 3
		}
	},
	methods: {
		// 图片URL处理函数
		getImageUrl(path) {
			// 如果是完整URL，直接返回
			if (path.startsWith('http://') || path.startsWith('https://')) {
				return path;
			}
			// 使用环境配置中的baseUrl
			const config = env.getConfig();
			return `${config.baseUrl}/${path}`;
		},
		
		// 处理遮罩点击
		handleOverlayClick() {
			if (!this.isLoading) {
				this.handleClose()
			}
		},
		
		// 关闭弹窗
		handleClose() {
			this.$emit('close')
		},
		
		// 处理微信登录
		async handleWxLogin() {
			if (this.isLoading) return
			this.isLoading = true
			
			try {
				// 直接在当前组件中处理登录
				const code = await this.getWxLoginCode()
				const result = await this.callBackendLogin(code)
				
				if (result && result.data) {
					// 登录成功，存储用户信息
					store.setUserInfo(result.data)
					
					uni.showToast({
						title: '登录成功',
						icon: 'success'
					})
					
					// 关闭登录弹窗
					this.$emit('close')
					
					// 刷新页面数据
					this.$emit('login-success')
				}
			} catch (error) {
				console.error('登录失败:', error)
				this.handleLoginError(error)
			} finally {
				this.isLoading = false
			}
		},
		
		// 测试后端连接
		testBackendConnection() {
			return new Promise((resolve, reject) => {
				console.log('测试后端连接...')
				
				uni.request({
					url: env.getApiUrl('/wxuser/simple-test'),
					method: 'GET',
					success: (res) => {
						console.log('简单测试接口返回:', res)
						if (res.statusCode === 200) {
							console.log('后端连接正常，WxUserController可用')
							resolve(res.data)
						} else {
							reject(new Error(`后端连接失败，状态码: ${res.statusCode}`))
						}
					},
					fail: (err) => {
						console.error('后端连接失败:', err)
						reject(new Error('无法连接到后端服务: ' + err.errMsg))
					}
				})
			})
		},
		
		// 检查用户授权设置
		checkUserAuth() {
			return new Promise((resolve, reject) => {
				uni.getSetting({
					success: (res) => {
						console.log('获取授权设置成功:', res.authSetting)
						resolve(res.authSetting)
					},
					fail: (err) => {
						console.error('获取授权设置失败:', err)
						reject(new Error('获取授权设置失败: ' + err.errMsg))
					}
				})
			})
		},
		
		// 获取微信登录code
		getWxLoginCode() {
			return new Promise((resolve, reject) => {
				uni.login({
					provider: 'weixin',
					success: (res) => {
						if (res.code) {
							resolve(res.code)
						} else {
							reject(new Error('获取微信登录凭证失败'))
						}
					},
					fail: (err) => {
						reject(new Error('微信登录失败: ' + err.errMsg))
					}
				})
			})
		},
		
		// 调用后端登录接口
		async callBackendLogin(code) {
			return new Promise((resolve, reject) => {
				const url = `${env.getApiUrl('/wxuser/login')}?code=${encodeURIComponent(code)}`
				
				console.log('调用后端登录接口:', url)
				
				uni.request({
					url: url,
					method: 'POST',
					data: {},
					header: {
						'Content-Type': 'application/json'
					},
					success: (res) => {
						console.log('后端返回:', res)
						if (res.statusCode === 200 && res.data.code === 200) {
							resolve(res.data)
						} else {
							reject(new Error(res.data.message || '登录失败'))
						}
					},
					fail: (err) => {
						console.error('请求失败:', err)
						reject(new Error('网络请求失败: ' + err.errMsg))
					}
				})
			})
		},
		
		// 处理登录错误
		handleLoginError(error) {
			this.wxLoginRetryCount++
			
			let errorMessage = '登录失败，请重试'
			
			if (error.message.includes('用户取消授权')) {
				errorMessage = '您取消了授权，请重新授权后登录'
			} else if (error.message.includes('获取授权设置失败')) {
				errorMessage = '获取授权设置失败，请检查小程序权限'
			} else if (error.message.includes('网络')) {
				errorMessage = '网络连接失败，请检查网络后重试'
			} else if (error.message.includes('ERR_CONNECTION_REFUSED')) {
				errorMessage = '后端服务连接失败，请检查服务器状态'
			}
			
			if (this.wxLoginRetryCount >= this.maxRetryCount) {
				uni.showModal({
					title: '登录失败',
					content: errorMessage + '\n\n如果问题持续存在，请检查：\n1. 网络连接是否正常\n2. 微信版本是否最新\n3. 小程序权限是否开启\n4. 后端服务是否正常运行',
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
.login-modal-overlay {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.6);
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 9999;
}

.login-modal {
	width: 600rpx;
	background: #fff;
	border-radius: 30rpx;
	position: relative;
	overflow: hidden;
	box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.3);
}

.close-btn {
	position: absolute;
	top: 20rpx;
	right: 20rpx;
	width: 60rpx;
	height: 60rpx;
	background: rgba(0, 0, 0, 0.1);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 10;
}

.close-icon {
	font-size: 40rpx;
	color: #666;
	font-weight: bold;
}

.login-content {
	padding: 60rpx 40rpx 40rpx;
}

.header-section {
	text-align: center;
	margin-bottom: 60rpx;
}

.logo {
	width: 120rpx;
	height: 120rpx;
	border-radius: 50%;
	margin-bottom: 30rpx;
	background: #f8f8f8;
}

.title {
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
	display: block;
	margin-bottom: 10rpx;
}

.subtitle {
	font-size: 28rpx;
	color: #666;
	display: block;
}

.login-section {
	margin-bottom: 40rpx;
}

.wxLogin-btn {
	width: 100%;
	height: 90rpx;
	border-radius: 45rpx;
	border: none;
	background: linear-gradient(135deg, #07c160 0%, #06ad56 100%);
	color: #fff;
	font-size: 32rpx;
	font-weight: bold;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 20rpx;
	box-shadow: 0 8rpx 20rpx rgba(7, 193, 96, 0.3);
}

.wxLogin-btn:active {
	transform: translateY(2rpx);
}

.wechat-icon {
	width: 40rpx;
	height: 40rpx;
	margin-right: 15rpx;
}

.btn-text {
	font-size: 32rpx;
	font-weight: bold;
}

.login-tip {
	font-size: 24rpx;
	color: #999;
	text-align: center;
	display: block;
}

.privacy-section {
	text-align: center;
}

.privacy-text {
	font-size: 22rpx;
	color: #999;
	line-height: 1.5;
}

.link {
	color: #07c160;
	text-decoration: underline;
}
</style> 