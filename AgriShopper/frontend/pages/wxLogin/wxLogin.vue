<template>
	<view class="wxLogin-container">
		<!-- 中间居中内容：logo和欢迎语 -->
		<view class="center-section">
			<image class="logo" src="/static/logo.png" mode="aspectFit"></image>
			<text class="welcome-title">农康优选欢迎你</text>
		</view>

		<!-- 底部登录按钮和协议 -->
		<view class="bottom-section">
			<!-- 获取code按钮 -->
			<button 
				class="get-code-btn"
				@click="getWxCode"
				:disabled="wxCode"
			>
				{{ wxCode ? '已获取Code' : '获取微信Code' }}
			</button>
			
			<!-- 获取用户信息按钮 -->
			<button 
				class="get-user-info-btn"
				@click="getUserProfile"
				:disabled="!sessionInfo || userProfileData"
			>
				{{ userProfileData ? (userProfileData.is_demote ? '已获取基础信息' : '已获取完整信息') : '获取用户信息' }}
			</button>
			
			<!-- 测试解密按钮 -->
			<button 
				class="test-decrypt-btn"
				@click="testDecrypt"
				:disabled="!encryptedData || !iv || !sessionInfo"
			>
				测试解密
			</button>
			
			<!-- 微信登录按钮 -->
			<button 
				class="wx-login-btn" 
				:disabled="isLoading || !wxCode || !userProfileData"
				@click="handleWxLogin"
			>
				{{ isLoading ? '登录中...' : (wxCode && userProfileData) ? '完成登录' : '请先获取Code和用户信息' }}
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
				<text>Code状态: {{ wxCode ? '已获取' : '未获取' }}</text>
			</view>
			<view v-if="wxCode" class="debug-item">
				<text>Code: {{ wxCode.substring(0, 10) }}...</text>
			</view>
			<view class="debug-item">
				<text>Session状态: {{ sessionInfo ? '已获取' : '未获取' }}</text>
			</view>
			<view v-if="sessionInfo" class="debug-item">
				<text>OpenID: {{ sessionInfo.openid ? sessionInfo.openid.substring(0, 10) + '...' : '未获取' }}</text>
			</view>
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
			<view v-if="userProfileData" class="debug-item">
				<text>是否降级: {{ userProfileData.is_demote ? '是' : '否' }}</text>
			</view>
			<view v-if="encryptedData" class="debug-item">
				<text>加密数据: 已获取</text>
			</view>
			<view v-if="iv" class="debug-item">
				<text>IV: 已获取</text>
			</view>
			<view v-if="signature" class="debug-item">
				<text>签名: 已获取</text>
			</view>
			<view class="debug-item">
				<text>登录状态: {{ isLoading ? '登录中...' : '待登录' }}</text>
			</view>
		</view>

		<!-- 加载遮罩 -->
		<view v-if="isLoading" class="loading-mask">
			<view class="loading-content">
				<view class="loading-spinner"></view>
				<text class="loading-text">正在登录...</text>
			</view>
		</view>

		<!-- 补充信息弹窗 -->
		<view v-if="showSupplement" class="supplement-mask">
			<view class="supplement-content">
				<view class="supplement-header">
					<text class="supplement-title">补充个人信息</text>
					<text class="supplement-close" @click="hideSupplementModal">×</text>
				</view>
				<view class="supplement-body">
					<view class="form-item">
						<text class="form-label">昵称</text>
						<input class="form-input" v-model="supplementData.nickName" placeholder="请输入昵称" />
					</view>
					<view class="form-item">
						<text class="form-label">性别</text>
						<picker class="form-picker" :value="supplementData.gender" :range="genderOptions" @change="onGenderChange">
							<view class="picker-text">{{ genderOptions[supplementData.gender] }}</view>
						</picker>
					</view>
					<view class="form-item">
						<text class="form-label">地区</text>
						<picker class="form-picker" mode="region" @change="onRegionChange">
							<view class="picker-text">{{ supplementData.region.join(' ') || '请选择地区' }}</view>
						</picker>
					</view>
				</view>
				<view class="supplement-footer">
					<button class="supplement-btn" @click="saveSupplementInfo">保存信息</button>
				</view>
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
			wxCode: null,
			sessionInfo: null,
			userProfileData: null,
			encryptedData: null,
			iv: null,
			signature: null,
			showSupplement: false,
			supplementData: {
				nickName: '',
				gender: 0,
				region: []
			},
			genderOptions: ['未知', '男', '女']
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
		
		// 获取微信登录code
		async getWxCode() {
			console.log('【点击获取微信Code按钮】');
			
			try {
				const loginRes = await new Promise((resolve, reject) => {
					uni.login({
						provider: 'weixin',
						success: (res) => {
							console.log('【uni.login成功】:', JSON.stringify(res));
							resolve(res);
						},
						fail: (err) => {
							console.log('【uni.login失败】:', JSON.stringify(err));
							reject(err);
						}
					});
				});
				
				if (loginRes.code) {
					this.wxCode = loginRes.code;
					console.log('【获取到完整Code】:', this.wxCode);
					console.log('【Code长度】:', this.wxCode.length);
					
					// 获取到code后，立即调用后端获取session
					this.getSessionFromBackend();
				} else {
					throw new Error('获取code失败');
				}
			} catch (e) {
				console.log('【获取Code异常】:', JSON.stringify(e));
				uni.showToast({ 
					title: '获取Code失败', 
					icon: 'none' 
				});
			}
		},
		
		// 从后端获取session信息
		async getSessionFromBackend() {
			console.log('【开始从后端获取session】');
			
			try {
				const apiRes = await new Promise((resolve, reject) => {
					uni.request({
						url: env.getApiUrl('/wxuser/code2session') + '?code=' + encodeURIComponent(this.wxCode),
						method: 'POST',
						header: {
							'Content-Type': 'application/json'
						},
						success: (res) => {
							console.log('【后端session返回】:', JSON.stringify(res));
							resolve(res);
						},
						fail: (err) => {
							console.log('【获取session失败】:', JSON.stringify(err));
							reject(err);
						}
					});
				});
				
				if (apiRes.statusCode === 200 && apiRes.data.code === 200) {
					this.sessionInfo = apiRes.data.data;
					console.log('【获取到session信息】:', JSON.stringify(this.sessionInfo));
					uni.showToast({ 
						title: 'Session获取成功', 
						icon: 'success',
						duration: 2000
					});
				} else {
					throw new Error(apiRes.data?.message || '获取session失败');
				}
			} catch (e) {
				console.log('【获取session异常】:', JSON.stringify(e));
				uni.showToast({ 
					title: '获取Session失败', 
					icon: 'none' 
				});
			}
		},
		
		// 微信官方推荐授权API
		getUserProfile() {
			console.log('【点击获取用户信息按钮】');
			
			if (!this.sessionInfo) {
				uni.showToast({ title: '请先获取Session', icon: 'none' });
				return;
			}
			
			// 直接调用微信原生授权，尝试获取完整信息
			this.requestFullProfile();
		},
		
		// 请求完整用户信息
		requestFullProfile() {
			uni.getUserProfile({
				desc: '用于完善会员资料',
				lang: 'zh_CN',
				success: (res) => {
					console.log('【getUserProfile返回】:', JSON.stringify(res));
					this.userProfileData = res.userInfo;
					this.encryptedData = res.encryptedData;
					this.iv = res.iv;
					this.signature = res.signature;
					
					console.log('【加密数据】:', {
						encryptedData: this.encryptedData,
						iv: this.iv,
						signature: this.signature
					});
					
					console.log('【用户信息详情】:', {
						nickName: res.userInfo.nickName,
						gender: res.userInfo.gender,
						country: res.userInfo.country,
						province: res.userInfo.province,
						city: res.userInfo.city,
						is_demote: res.userInfo.is_demote
					});
					
					if (res.userInfo.is_demote || res.userInfo.nickName === '微信用户') {
						// 检测到匿名信息，提示用户但允许继续
						uni.showToast({ 
							title: '已获取基础信息，请点击登录测试解密', 
							icon: 'success',
							duration: 3000
						});
					} else {
						uni.showToast({ 
							title: '授权成功', 
							icon: 'success',
							duration: 2000
						});
					}
				},
				fail: (err) => {
					console.log('【getUserProfile失败】:', JSON.stringify(err));
					uni.showToast({ title: '授权失败', icon: 'none' });
				}
			});
		},
		

		// 微信登录
		async handleWxLogin() {
			console.log('【点击微信登录按钮】');
			console.log('【userProfileData登录前】:', JSON.stringify(this.userProfileData));
			console.log('【wxCode登录前】:', this.wxCode);
			
			if (!this.wxCode) {
				uni.showToast({ title: '请先获取Code', icon: 'none' });
				return;
			}
			
			if (!this.userProfileData) {
				uni.showToast({ title: '请先获取用户信息', icon: 'none' });
				return;
			}
			
			this.isLoading = true;
			try {
				console.log('【使用已获取的完整code】:', this.wxCode);
				
				// 2. 调用后端接口，传session信息、userProfileData和加密数据
				const requestData = {
					code: this.wxCode,
					sessionInfo: this.sessionInfo,
					userInfo: this.userProfileData,
					encryptedData: this.encryptedData,
					iv: this.iv,
					signature: this.signature
				};
				console.log('【请求后端参数】:', JSON.stringify(requestData));
				
				const apiRes = await new Promise((resolve, reject) => {
					uni.request({
						url: env.getApiUrl('/wxuser/login'),
						method: 'POST',
						data: requestData,
						header: {
							'Content-Type': 'application/json'
						},
						success: (res) => {
							console.log('【后端返回完整响应】:', JSON.stringify(res));
							resolve(res);
						},
						fail: (err) => {
							console.log('【uni.request失败】:', JSON.stringify(err));
							reject(err);
						}
					});
				});
				
				console.log('【后端返回状态码】:', apiRes.statusCode);
				console.log('【后端返回数据】:', JSON.stringify(apiRes.data));
				
				if (apiRes.statusCode === 200 && apiRes.data.code === 200) {
					// 登录成功
					const userData = apiRes.data.data;
					console.log('【登录成功，用户数据】:', JSON.stringify(userData));
					
					// 保存用户信息到本地存储
					store.setUserInfo(userData);
					
					uni.showToast({ 
						title: '登录成功', 
						icon: 'success', 
						duration: 1500 
					});
					
					// 延迟跳转，让用户看到成功提示
					setTimeout(() => {
						this.navigateToHome();
					}, 1500);
				} else {
					// 登录失败
					const errorMsg = apiRes.data?.message || '登录失败';
					console.log('【登录失败】:', errorMsg);
					uni.showToast({ 
						title: errorMsg, 
						icon: 'none' 
					});
				}
			} catch (e) {
				console.log('【微信登录catch异常】:', JSON.stringify(e));
				uni.showToast({ 
					title: e.message || '微信登录失败', 
					icon: 'none' 
				});
			} finally {
				this.isLoading = false;
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
		
		// 重新请求完整用户信息
		reRequestFullProfile() {
			// 直接重新调用微信授权
			this.requestFullProfile();
		},
		
		// 显示补充信息弹窗
		showSupplementModal() {
			this.supplementData.nickName = this.userProfileData.nickName || '';
			this.supplementData.gender = this.userProfileData.gender || 0;
			this.supplementData.region = [
				this.userProfileData.country || '',
				this.userProfileData.province || '',
				this.userProfileData.city || ''
			].filter(item => item);
			this.showSupplement = true;
		},
		
		// 隐藏补充信息弹窗
		hideSupplementModal() {
			this.showSupplement = false;
		},
		
		// 性别选择
		onGenderChange(e) {
			this.supplementData.gender = parseInt(e.detail.value);
		},
		
		// 地区选择
		onRegionChange(e) {
			this.supplementData.region = e.detail.value;
		},
		
		// 保存补充信息
		saveSupplementInfo() {
			if (!this.supplementData.nickName.trim()) {
				uni.showToast({ title: '请输入昵称', icon: 'none' });
				return;
			}
			
			// 更新用户信息
			this.userProfileData.nickName = this.supplementData.nickName;
			this.userProfileData.gender = this.supplementData.gender;
			this.userProfileData.country = this.supplementData.region[0] || '';
			this.userProfileData.province = this.supplementData.region[1] || '';
			this.userProfileData.city = this.supplementData.region[2] || '';
			
			// 标记为非降级信息
			this.userProfileData.is_demote = false;
			
			uni.showToast({ title: '信息已更新', icon: 'success' });
			this.hideSupplementModal();
		},
		
		// 测试解密功能
		async testDecrypt() {
			console.log('【点击测试解密按钮】');
			
			if (!this.encryptedData || !this.iv || !this.sessionInfo) {
				uni.showToast({ title: '缺少解密参数', icon: 'none' });
				return;
			}
			
			try {
				console.log('【开始测试解密】');
				console.log('encryptedData: ' + this.encryptedData.substring(0, 50) + '...');
				console.log('iv: ' + this.iv);
				console.log('sessionKey长度: ' + this.sessionInfo.session_key.length);
				console.log('sessionKey: ' + this.sessionInfo.session_key.substring(0, 20) + '...');
				console.log('完整sessionKey: ' + this.sessionInfo.session_key);
				
				const apiRes = await new Promise((resolve, reject) => {
					uni.request({
						url: env.getApiUrl('/wxuser/test-decrypt'),
						method: 'POST',
						data: {
							encryptedData: this.encryptedData,
							iv: this.iv,
							sessionKey: this.sessionInfo.session_key
						},
						header: {
							'Content-Type': 'application/json'
						},
						success: (res) => {
							console.log('【测试解密返回】:', JSON.stringify(res));
							resolve(res);
						},
						fail: (err) => {
							console.log('【测试解密失败】:', JSON.stringify(err));
							reject(err);
						}
					});
				});
				
				if (apiRes.statusCode === 200 && apiRes.data.code === 200) {
					const decryptedData = apiRes.data.data;
					console.log('【解密成功】:', JSON.stringify(decryptedData));
					
					uni.showModal({
						title: '解密成功',
						content: `昵称: ${decryptedData.nickName}\n性别: ${decryptedData.gender}\n地区: ${decryptedData.country} ${decryptedData.province} ${decryptedData.city}`,
						showCancel: false,
						confirmText: '确定'
					});
				} else {
					const errorMsg = apiRes.data?.message || '解密失败';
					console.log('【解密失败】:', errorMsg);
					uni.showToast({ title: errorMsg, icon: 'none' });
				}
			} catch (e) {
				console.log('【测试解密异常】:', JSON.stringify(e));
				uni.showToast({ title: '解密测试失败', icon: 'none' });
			}
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
.get-code-btn {
	width: 100%;
	height: 100rpx;
	border-radius: 50rpx;
	border: none;
	background: linear-gradient(135deg, #FF9800, #F57C00);
	color: #fff;
	font-size: 32rpx;
	font-weight: bold;
	display: flex;
	align-items: center;
	justify-content: center;
	transition: all 0.3s ease;
	box-shadow: 0 8rpx 20rpx rgba(255, 152, 0, 0.15);
	margin-bottom: 20rpx;
}

.get-code-btn:active {
	transform: scale(0.98);
	background: linear-gradient(135deg, #F57C00, #E65100);
}

.get-code-btn:disabled {
	background: linear-gradient(135deg, #4CAF50, #45a049);
	color: #fff;
	box-shadow: 0 8rpx 20rpx rgba(76, 175, 80, 0.15);
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

.test-decrypt-btn {
	width: 100%;
	height: 80rpx;
	border-radius: 40rpx;
	border: none;
	background: linear-gradient(135deg, #9C27B0, #7B1FA2);
	color: #fff;
	font-size: 28rpx;
	font-weight: bold;
	display: flex;
	align-items: center;
	justify-content: center;
	transition: all 0.3s ease;
	box-shadow: 0 6rpx 16rpx rgba(156, 39, 176, 0.15);
	margin-bottom: 20rpx;
}

.test-decrypt-btn:active {
	transform: scale(0.98);
	background: linear-gradient(135deg, #7B1FA2, #6A1B9A);
}

.test-decrypt-btn:disabled {
	background: #ccc;
	color: #999;
	box-shadow: none;
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

/* 补充信息弹窗样式 */
.supplement-mask {
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

.supplement-content {
	background: #ffffff;
	border-radius: 20rpx;
	width: 80%;
	max-width: 600rpx;
	max-height: 80%;
	overflow: hidden;
}

.supplement-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1px solid #f0f0f0;
}

.supplement-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.supplement-close {
	font-size: 40rpx;
	color: #999;
	padding: 10rpx;
}

.supplement-body {
	padding: 30rpx;
}

.form-item {
	margin-bottom: 30rpx;
}

.form-label {
	display: block;
	font-size: 28rpx;
	color: #333;
	margin-bottom: 15rpx;
}

.form-input {
	width: 100%;
	height: 80rpx;
	border: 1px solid #ddd;
	border-radius: 10rpx;
	padding: 0 20rpx;
	font-size: 28rpx;
	box-sizing: border-box;
}

.form-picker {
	width: 100%;
	height: 80rpx;
	border: 1px solid #ddd;
	border-radius: 10rpx;
	display: flex;
	align-items: center;
	padding: 0 20rpx;
	box-sizing: border-box;
}

.picker-text {
	font-size: 28rpx;
	color: #333;
}

.supplement-footer {
	padding: 30rpx;
	border-top: 1px solid #f0f0f0;
}

.supplement-btn {
	width: 100%;
	height: 80rpx;
	background: linear-gradient(135deg, #4CAF50, #45a049);
	color: #fff;
	border: none;
	border-radius: 10rpx;
	font-size: 28rpx;
	font-weight: bold;
}

.supplement-info-btn {
	width: 100%;
	height: 80rpx;
	border-radius: 50rpx;
	border: 1px solid #4CAF50;
	background: #fff;
	color: #4CAF50;
	font-size: 28rpx;
	font-weight: bold;
	display: flex;
	align-items: center;
	justify-content: center;
	transition: all 0.3s ease;
	margin-bottom: 20rpx;
}

.supplement-info-btn:active {
	background: #4CAF50;
	color: #fff;
}

.re-auth-btn {
	width: 100%;
	height: 80rpx;
	border-radius: 50rpx;
	border: 1px solid #2196F3;
	background: #fff;
	color: #2196F3;
	font-size: 28rpx;
	font-weight: bold;
	display: flex;
	align-items: center;
	justify-content: center;
	transition: all 0.3s ease;
	margin-bottom: 20rpx;
}

.re-auth-btn:active {
	background: #2196F3;
	color: #fff;
}
</style> 