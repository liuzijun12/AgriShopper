<template>
  <view class="page">
    <!-- 顶部导航栏 -->
    <view class="top-nav">
      <view class="search-bar" @click="handleSearchClick">
        <image class="search-icon" src="/static/images/search.png"></image>
        <text class="search-text">{{ currentHotWord }}</text>
      </view>
    </view>
    
    <!-- 轮播图 -->
    <view class="banner-container">
      <swiper class="banner-swiper" :indicator-dots="true" :autoplay="true" :interval="3000" :duration="500">
        <swiper-item v-for="(item, index) in bannerList" :key="item.id" @click="handleBannerClick(item)">
          <view class="banner-item">
            <image v-if="item.image" class="banner-image" :src="item.image" mode="aspectFill"></image>
            <view v-else class="banner-placeholder-wrapper">
              <text class="banner-placeholder">{{ item.title }}</text>
            </view>
          </view>
        </swiper-item>
      </swiper>
    </view>
    
    <!-- 分类栏 -->
    <view class="category-container">
      <view class="category-grid">
        <!-- 后台提供的分类数据 -->
        <view 
          class="category-item" 
          v-for="(item, index) in displayCategoryList" 
          :key="item.id"
          @click="handleCategoryClick(item)"
        >
          <image class="category-icon" :src="item.icon"></image>
          <text class="category-text">{{ item.name }}</text>
        </view>
        <!-- 更多按钮（只有超过8个分类时才显示） -->
        <view v-if="showMoreButton" class="category-item" @click="handleMoreClick">
          <image class="category-icon" src="/static/images/more.png"></image>
          <text class="category-text">更多</text>
        </view>
      </view>
    </view>
    
    <!-- 精选推荐标题栏 -->
    <view class="section-header">
      <text class="section-title">精选推荐</text>
      <view class="section-more" @click="handleRecommendMoreClick">
        <text class="more-text">更多</text>
        <text class="arrow-icon">></text>
      </view>
    </view>
    
    <!-- 商品展示区域 -->
    <view class="product-container">
      <view class="product-grid">
        <view 
          class="product-item" 
          v-for="(product, index) in productList" 
          :key="product.id"
          @click="handleProductClick(product)"
        >
          <!-- 商品图片 -->
          <view class="product-image-wrapper">
            <image class="product-image" :src="product.image" mode="aspectFill"></image>
            <!-- 角标 -->
            <view v-if="product.badge" class="product-badge">
              <text class="badge-text">{{ product.badge }}</text>
            </view>
          </view>
          
          <!-- 商品信息 -->
          <view class="product-info">
            <!-- 商品名称 -->
            <text class="product-name">{{ product.name }}</text>
            
            <!-- 价格和购买按钮 -->
            <view class="product-bottom">
              <text class="product-price">¥{{ product.price }}</text>
              <view class="buy-button" @click.stop="handleAddToCart(product)">
                <text class="buy-text">加入购物车</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 悬浮购物车 -->
    <FloatingCart :cartCount="cartCount" @cart-click="handleCartClick" />
    
    <!-- 底部导航栏 -->
    <TabBar :currentPath="currentPath" />
  </view>
</template>

<script>
import TabBar from '@/components/TabBar.vue'
import FloatingCart from '@/components/FloatingCart.vue'
import ProductAPI from '@/api/product'
import CategoryAPI from '@/api/category'

export default {
  components: {
    TabBar,
    FloatingCart
  },
  data() {
    return {
      title: 'Hello',
      currentPath: '/pages/index/index',
      cartCount: 3, // 购物车商品数量
      // 热门搜索词
      hotSearchWords: [
        '搜索新鲜农产品',
        '新鲜蔬菜',
        '有机水果', 
        '优质大米',
        '新鲜鸡蛋',
        '深海鱼类',
        '有机蘑菇'
      ],
      currentHotWordIndex: 0,
      hotWordTimer: null,
      // 轮播图数据（后台提供）
      bannerList: [
        { 
          id: 1, 
          title: '春季优惠活动', 
          image: '/static/images/banner1.png',
          type: 'activity',
          targetId: 'activity_001',
          url: ''
        },
        { 
          id: 2, 
          title: '新鲜水果特价', 
          image: '/static/images/banner2.png',
          type: 'product',
          targetId: 'product_123',
          url: ''
        },
        { 
          id: 3, 
          title: '限时秒杀活动', 
          image: '/static/images/banner3.png',
          type: 'activity',
          targetId: 'activity_002',
          url: ''
        }
      ],
      // 后台提供的分类数据（超过8个的测试数据）
      categoryList: [
        { id: 1, name: '蔬菜', icon: '/static/images/vegetables.png' },
        { id: 2, name: '水果', icon: '/static/images/fruits.png' },
        { id: 3, name: '粮油', icon: '/static/images/grains.png' },
        { id: 4, name: '肉禽蛋', icon: '/static/images/meat.png' },
        { id: 5, name: '水产', icon: '/static/images/seafood.png' },
        { id: 6, name: '蘑菇', icon: '/static/images/mushroom.png' },
        { id: 7, name: '豆制品', icon: '/static/images/tofu.png' },
        { id: 8, name: '调料', icon: '/static/images/category.png' },
        { id: 9, name: '零食', icon: '/static/images/category.png' },
        { id: 10, name: '饮品', icon: '/static/images/category.png' },
        { id: 11, name: '茶叶', icon: '/static/images/category.png' },
        { id: 12, name: '保健品', icon: '/static/images/category.png' }
      ],
      // 后台提供的商品数据（精选推荐4个）
      productList: []
    }
  },
  computed: {
    // 计算要显示的分类列表
    displayCategoryList() {
      if (this.categoryList.length > 8) {
        // 如果超过8个，只显示前7个
        return this.categoryList.slice(0, 7)
      } else {
        // 如果不超过8个，全部显示
        return this.categoryList
      }
    },
    // 计算是否显示更多按钮
    showMoreButton() {
      return this.categoryList.length > 8
    },
    // 当前显示的热门词
    currentHotWord() {
      return this.hotSearchWords[this.currentHotWordIndex] || '搜索新鲜农产品'
    }
  },
  onLoad() {
    // 页面加载时获取数据
    this.getProductList()
    this.getCategoryList()
    this.getBannerList()
  },
  
  onShow() {
    // 检查并清理无效的用户数据
    this.cleanInvalidUserData()
    // 检查用户登录状态和类型
    this.checkUserTypeAndRedirect()
    
    // 获取页面数据
    this.getProductList()
    this.getCategoryList()
    this.getBannerList()
  },
  methods: {
    // 处理分类点击
    handleCategoryClick(item) {
      console.log('点击分类:', item.name)
      // 跳转到对应分类的商品列表页面
      uni.navigateTo({
        url: `/ubPackages_user/pages/productList/productList?categoryId=${item.id}&categoryName=${item.name}`
      })
    },
    
    // 处理更多按钮点击
    handleMoreClick() {
      uni.navigateTo({
        url: '/ubPackages_user/pages/category/category'
      })
    },
    
    // 处理精选推荐更多点击
    handleRecommendMoreClick() {
      console.log('点击精选推荐更多')
      // 跳转到分类页面
      uni.navigateTo({
        url: '/ubPackages_user/pages/category/category'
      })
    },
    
    // 处理商品点击
    handleProductClick(product) {
      console.log('点击商品:', product.name)
      // 跳转到商品详情页
      // uni.navigateTo({
      //   url: `/pages/productDetail/productDetail?id=${product.id}`
      // })
    },
    
    // 处理加入购物车点击
    handleAddToCart(product) {
      console.log('加入购物车:', product.name)
      // 添加到购物车逻辑
      this.cartCount++
      
      // 可以添加成功提示
      uni.showToast({
        title: '已加入购物车',
        icon: 'success',
        duration: 1500
      })
    },
    
    // 处理购物车点击
    handleCartClick() {
      console.log('点击悬浮购物车')
      // 跳转到购物车页面
      // uni.navigateTo({
      //   url: '/pages/cart/cart'
      // })
    },
    
    // 获取分类数据（调用真实API接口）
    async getCategoryList() {
      try {
        console.log('开始获取分类数据...')
        
        // 调用真实API获取启用的分类
        const result = await CategoryAPI.getEnabledCategories()
        
        if (result && result.length > 0) {
          // 转换数据格式以适配前端显示
          this.categoryList = result.map(item => {
            return {
              id: item.id,
              name: item.name,
              icon: item.icon || '/static/images/category.png', // 默认图标
              description: item.description
            }
          })
          
          console.log('获取分类数据成功:', this.categoryList)
        } else {
          console.warn('分类数据为空，使用默认数据')
          // 如果API返回空数据，使用默认数据
          this.categoryList = [
            { id: 1, name: '蔬菜', icon: '/static/images/vegetables.png' },
            { id: 2, name: '水果', icon: '/static/images/fruits.png' },
            { id: 3, name: '粮油', icon: '/static/images/grains.png' },
            { id: 4, name: '肉禽蛋', icon: '/static/images/meat.png' },
            { id: 5, name: '水产', icon: '/static/images/seafood.png' },
            { id: 6, name: '蘑菇', icon: '/static/images/mushroom.png' },
            { id: 7, name: '豆制品', icon: '/static/images/tofu.png' },
            { id: 8, name: '调料', icon: '/static/images/category.png' }
          ]
        }
      } catch (error) {
        console.error('获取分类数据失败:', error)
        // 出错时使用默认数据
        this.categoryList = [
          { id: 1, name: '蔬菜', icon: '/static/images/vegetables.png' },
          { id: 2, name: '水果', icon: '/static/images/fruits.png' },
          { id: 3, name: '粮油', icon: '/static/images/grains.png' },
          { id: 4, name: '肉禽蛋', icon: '/static/images/meat.png' },
          { id: 5, name: '水产', icon: '/static/images/seafood.png' },
          { id: 6, name: '蘑菇', icon: '/static/images/mushroom.png' },
          { id: 7, name: '豆制品', icon: '/static/images/tofu.png' },
          { id: 8, name: '调料', icon: '/static/images/category.png' }
        ]
      }
    },
    
    // 处理轮播图点击
    handleBannerClick(banner) {
      console.log('点击轮播图:', banner.title)
      
      if (banner.type === 'activity') {
        // 跳转到活动页面
        this.goToActivity(banner.targetId)
      } else if (banner.type === 'product') {
        // 跳转到商品详情页面
        this.goToProduct(banner.targetId)
      } else if (banner.url) {
        // 直接跳转到指定URL
        this.goToUrl(banner.url)
      }
    },
    
    // 跳转到活动页面
    goToActivity(activityId) {
      console.log('跳转到活动页面:', activityId)
      // uni.navigateTo({
      //   url: `/pages/activity/activity?id=${activityId}`
      // })
      
      // 暂时跳转到分类页面作为示例
      uni.navigateTo({
        url: '/ubPackages_user/pages/category/category'
      })
    },
    
    // 跳转到商品详情页面
    goToProduct(productId) {
      console.log('跳转到商品详情页面:', productId)
      // uni.navigateTo({
      //   url: `/pages/productDetail/productDetail?id=${productId}`
      // })
      
      // 暂时跳转到商品列表页面作为示例
      uni.navigateTo({
        url: '/ubPackages_user/pages/productList/productList?categoryId=1&categoryName=推荐商品'
      })
    },
    
    // 跳转到指定URL
    goToUrl(url) {
      console.log('跳转到URL:', url)
      // 可以是H5页面或其他页面
      // uni.navigateTo({
      //   url: `/pages/webview/webview?url=${encodeURIComponent(url)}`
      // })
    },
    
    // 获取轮播图数据
    async getBannerList() {
      try {
        // 这里应该调用实际的API接口
        // const res = await api.getBannerList()
        // this.bannerList = res.data
        
        console.log('获取轮播图数据')
      } catch (error) {
        console.error('获取轮播图数据失败:', error)
      }
    },
    
    // 获取商品数据（调用真实API接口）
    async getProductList() {
      try {
        console.log('开始获取商品数据...')
        
        // 调用真实API获取热门商品
        const result = await ProductAPI.getHotProducts(4)
        
        if (result && result.length > 0) {
          // 转换数据格式以适配前端显示
          this.productList = result.map(item => {
            // 解析图片JSON数组，取第一张图片
            let imageUrl = '/static/images/product1.png' // 默认图片
            try {
              if (item.images) {
                const images = JSON.parse(item.images)
                if (images && images.length > 0) {
                  imageUrl = images[0]
                }
              }
            } catch (e) {
              console.warn('解析商品图片失败:', e)
            }
            
            // 确定角标
            let badge = null
            if (item.hasDiscount === 1) {
              badge = '特价'
            } else if (item.isHot === 1) {
              badge = '热销'
            }
            
            return {
              id: item.id,
              name: item.name,
              price: this.formatPrice(item.price),
              image: imageUrl,
              badge: badge,
              description: item.description,
              sales: item.sales,
              stock: item.stock,
              origin: item.origin
            }
          })
          
          console.log('获取商品数据成功:', this.productList)
        } else {
          console.warn('商品数据为空，使用默认数据')
          // 如果API返回空数据，使用默认数据
          this.productList = [
            {
              id: 1,
              name: '有机新鲜番茄',
              price: '12.80',
              image: '/static/images/product1.png',
              badge: '惊爆价'
            },
            {
              id: 2,
              name: '精选苹果',
              price: '15.60',
              image: '/static/images/product2.png',
              badge: '新品'
            },
            {
              id: 3,
              name: '优质大米',
              price: '28.00',
              image: '/static/images/product3.png',
              badge: null
            },
            {
              id: 4,
              name: '新鲜鸡蛋',
              price: '18.80',
              image: '/static/images/product4.png',
              badge: '热销'
            }
          ]
        }
      } catch (error) {
        console.error('获取商品数据失败:', error)
        // 出错时使用默认数据
        this.productList = [
          {
            id: 1,
            name: '有机新鲜番茄',
            price: '12.80',
            image: '/static/images/product1.png',
            badge: '惊爆价'
          },
          {
            id: 2,
            name: '精选苹果',
            price: '15.60',
            image: '/static/images/product2.png',
            badge: '新品'
          },
          {
            id: 3,
            name: '优质大米',
            price: '28.00',
            image: '/static/images/product3.png',
            badge: null
          },
          {
            id: 4,
            name: '新鲜鸡蛋',
            price: '18.80',
            image: '/static/images/product4.png',
            badge: '热销'
          }
        ]
      }
    },
    
    // 处理搜索框点击
    handleSearchClick() {
      console.log('点击搜索框')
      // 跳转到搜索页面，不传递任何参数
      uni.navigateTo({
        url: '/ubPackages_user/pages/search/search'
      })
    },
    
    // 启动热门词循环
    startHotWordRotation() {
      this.hotWordTimer = setInterval(() => {
        this.currentHotWordIndex = (this.currentHotWordIndex + 1) % this.hotSearchWords.length
      }, 3000) // 每3秒切换一次
    },
    
    // 获取轮播图数据
    async getBannerList() {
      try {
        // 这里应该调用实际的API接口
        // const res = await api.getBannerList()
        // this.bannerList = res.data
        
        console.log('获取轮播图数据')
      } catch (error) {
        console.error('获取轮播图数据失败:', error)
      }
    },
    
    // 检查用户类型并重定向
    checkUserTypeAndRedirect() {
      try {
        const userInfo = uni.getStorageSync('userInfo')
        console.log('当前用户信息:', userInfo)
        
        // 只有在用户确实登录且是商户类型时才跳转
        if (userInfo && userInfo.username && userInfo.userType === 'merchant') {
          console.log('检测到已登录的商户用户，自动跳转到商户页面')
          setTimeout(() => {
            uni.navigateTo({
              url: '/ubPackages_merchant/pages/index/index'
            })
          }, 500)
        } else if (userInfo && !userInfo.username) {
          // 如果有用户信息但没有用户名，说明是无效数据，清除它
          console.log('发现无效用户信息，清除本地存储')
          uni.removeStorageSync('userInfo')
        }
      } catch (error) {
        console.error('检查用户类型失败:', error)
      }
    },
    
    // 清理无效的用户数据
    cleanInvalidUserData() {
      try {
        const userInfo = uni.getStorageSync('userInfo')
        if (userInfo && (!userInfo.username || !userInfo.userType)) {
          console.log('发现无效用户数据，清除本地存储')
          uni.removeStorageSync('userInfo')
        }
      } catch (error) {
        console.error('清理用户数据失败:', error)
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
  },
  
  mounted() {
    // 页面加载时获取数据
    this.getBannerList()
    this.getCategoryList()
    this.getProductList()
    // 启动热门词循环
    this.startHotWordRotation()
    
    // 开发调试：打印当前存储的用户信息
    const userInfo = uni.getStorageSync('userInfo')
    console.log('页面加载时的用户信息:', userInfo)
  },
  
  beforeDestroy() {
    // 清除定时器
    if (this.hotWordTimer) {
      clearInterval(this.hotWordTimer)
    }
  }
}
</script>

<style>
.page {
  min-height: 100vh;
  padding-bottom: calc(100rpx + env(safe-area-inset-bottom));
}

.top-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 999;
  padding: calc(env(safe-area-inset-top) + 20rpx) 30rpx 20rpx 30rpx;
  background-color: #fff;
}

.search-bar {
  display: flex;
  align-items: center;
  background-color: #f3f4f6;
  padding: 20rpx 30rpx;
}

.search-icon {
  width: 32rpx;
  height: 32rpx;
  margin-right: 20rpx;
}

.search-text {
  font-size: 28rpx;
  color: #777d8b;
}

.banner-container {
  margin-top: calc(env(safe-area-inset-top) + 120rpx + 24rpx);
  padding: 0 30rpx;
}

.banner-swiper {
  height: 300rpx;
  border-radius: 12rpx;
  overflow: hidden;
}

.banner-item {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
}

.banner-image {
  width: 100%;
  height: 100%;
}

.banner-placeholder-wrapper {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-placeholder {
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
  text-align: center;
}

.category-container {
  margin-top: 24rpx;
  padding: 0 30rpx;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 30rpx;
  background-color: #fff;
  padding: 40rpx 20rpx;
  border-radius: 12rpx;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20rpx 10rpx;
}

.category-icon {
  width: 60rpx;
  height: 60rpx;
  margin-bottom: 16rpx;
}

.category-text {
  font-size: 24rpx;
  color: #333;
  text-align: center;
  line-height: 1.2;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24rpx;
  padding: 0 30rpx;
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.section-more {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.more-text {
  font-size: 26rpx;
  color: #999;
  margin-right: 8rpx;
}

.arrow-icon {
  font-size: 26rpx;
  color: #999;
  font-weight: bold;
}

.product-container {
  padding: 0 30rpx;
  margin-bottom: 40rpx;
}

.product-grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.product-item {
  width: calc(50% - 12rpx);
  background-color: #fff;
  border-radius: 12rpx;
  overflow: hidden;
  margin-bottom: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.product-image-wrapper {
  position: relative;
  width: 100%;
  height: 300rpx;
}

.product-image {
  width: 100%;
  height: 100%;
}

.product-badge {
  position: absolute;
  top: 16rpx;
  left: 16rpx;
  background-color: #ef4444;
  border-radius: 30rpx;
  padding: 10rpx 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.badge-text {
  font-size: 22rpx;
  color: #fff;
  font-weight: bold;
  white-space: nowrap;
  text-align: center;
}

.product-info {
  padding: 20rpx;
}

.product-name {
  font-size: 28rpx;
  color: #333;
  line-height: 1.4;
  margin-bottom: 16rpx;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  font-size: 32rpx;
  color: #ff4757;
  font-weight: bold;
}

.buy-button {
  background-color: #f87272;
  border-radius: 30rpx;
  padding: 12rpx 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.buy-text {
  font-size: 24rpx;
  color: #fff;
  font-weight: bold;
  text-align: center;
  white-space: nowrap;
}
</style>
