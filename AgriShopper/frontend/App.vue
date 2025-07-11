<script>
import { store } from './store'

export default {
	globalData: {
		userInfo: null,
		cartItems: [],
		isAppReady: false
	},
	
	onLaunch: function() {
		console.log('App Launch')
		// 初始化全局状态
		this.globalData.isAppReady = true
		store.setAppReady(true)
	},
	
	onShow: function() {
		console.log('App Show')
	},
	
	onHide: function() {
		console.log('App Hide')
	},
	
	methods: {
		// 获取用户信息的方法，需要在页面中通过按钮点击调用
		getUserProfile() {
			return new Promise((resolve, reject) => {
				uni.getUserProfile({
					desc: '用于完善会员资料',
					success: (res) => {
						this.globalData.userInfo = res.userInfo
						store.setUserInfo(res.userInfo)
						resolve(res.userInfo)
					},
					fail: (err) => {
						console.error('Failed to get user info:', err)
						reject(err)
					}
				})
			})
		}
	}
}
</script>

<style>
	/*每个页面公共css */
	page {
		background-color: #f8f8f8;
		font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Helvetica,
			Segoe UI, Arial, Roboto, 'PingFang SC', 'miui', 'Hiragino Sans GB', 'Microsoft Yahei',
			sans-serif;
	}

	.container {
		width: 100%;
		min-height: 100vh;
	}

	/* 通用样式 */
	.cursor-pointer {
		cursor: pointer;
	}

	/* 文本溢出省略号 */
	.text-ellipsis {
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	/* 多行文本溢出省略号 */
	.text-ellipsis-2 {
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		overflow: hidden;
	}

	/* 底部安全区域 */
	.safe-area-bottom {
		padding-bottom: constant(safe-area-inset-bottom);
		padding-bottom: env(safe-area-inset-bottom);
	}

	/* flex布局工具类 */
	.flex {
		display: flex;
	}

	.flex-col {
		display: flex;
		flex-direction: column;
	}

	.items-center {
		align-items: center;
	}

	.justify-center {
		justify-content: center;
	}

	.justify-between {
		justify-content: space-between;
	}

	.flex-1 {
		flex: 1;
	}
</style>
