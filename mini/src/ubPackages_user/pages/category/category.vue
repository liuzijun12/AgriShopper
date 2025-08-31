<template>
  <view class="page">
    <!-- 顶部搜索组件 -->
    <view class="search-header">
      <view class="search-bar" @click="handleSearchClick">
        <image class="search-icon" src="/static/images/search.png"></image>
        <input 
          class="search-input" 
          placeholder="搜索商品" 
          v-model="searchKeyword"
          @input="handleSearchInput"
          @confirm="handleSearchConfirm"
        />
      </view>
    </view>
    
    <!-- 主体内容区域 -->
    <view class="content-container">
      <!-- 左侧分类导航栏 -->
      <view class="category-sidebar">
        <scroll-view 
          class="category-list" 
          scroll-y="true"
          :scroll-top="categoryScrollTop"
        >
          <view 
            v-for="(category, index) in categoryList" 
            :key="category.id"
            class="category-item"
            :class="{ 'active': selectedCategoryId === category.id }"
            @click="handleCategorySelect(category, index)"
          >
            <image class="category-icon" :src="category.icon"></image>
            <text class="category-name">{{ category.name }}</text>
          </view>
        </scroll-view>
      </view>
      
      <!-- 右侧商品列表区域 -->
      <view class="product-container">
        <scroll-view 
          class="product-scroll" 
          scroll-y="true"
          :scroll-top="productScrollTop"
          @scrolltolower="handleLoadMore"
        >
          <!-- 商品流式布局 -->
          <view class="product-grid">
              <view 
                v-for="product in productList" 
                :key="product.id"
                class="product-card"
                @click="handleProductClick(product)"
              >
                <!-- 商品卡片容器 -->
                <view class="card-container">
                  <!-- 上方图片区域 -->
                  <view class="image-section">
                    <image 
                      class="responsive-image" 
                      :src="product.image" 
                      mode="aspectFill"
                      :lazy-load="true"
                      alt="{{ product.name }}"
                    ></image>
                    <!-- 角标标签 -->
                    <view v-if="product.badge" class="product-badge">
                      <text class="badge-text">{{ product.badge }}</text>
                    </view>
                  </view>
                  
                  <!-- 下方文本与交互区域 -->
                  <view class="content-section">
                    <!-- 商品名称和销量 - 水平布局 -->
                    <view class="product-title-block">
                      <text class="product-name">{{ product.name }}</text>
                      <!-- 销量信息 - 内联元素组合 -->
                      <view class="sales-info-block">
                        <text class="sales-label">销量</text>
                        <text class="sales-value">{{ product.sales || 0 }}件</text>
                      </view>
                    </view>
                    
                    <!-- 价格与交互区域（水平布局） -->
                    <view class="price-action-section">
                      <!-- 价格信息（最低价格） - 内联元素组合 -->
                      <view class="price-inline-group">
                        <text class="currency-symbol">¥</text>
                        <text class="price-value">{{ formatPrice(getMinPrice(product)) }}</text>
                        <text class="price-unit">/{{ product.unit || '斤' }}</text>
                      </view>
                      
                      <!-- 交互按钮 -->
                      <view 
                        class="action-button" 
                        @click.stop="handleAddToCart(product)"
                      >
                        <text class="button-text">立即加购</text>
                      </view>
                    </view>
                  </view>
                </view>
              </view>
          </view>
          
          <!-- 加载更多状态 -->
          <view v-if="loading" class="loading-more">
            <text class="loading-text">加载中...</text>
          </view>
          
          <!-- 没有更多数据 -->
          <view v-if="!hasMore && productList.length > 0" class="no-more">
            <text class="no-more-text">没有更多商品了</text>
          </view>
          
          <!-- 空状态 -->
          <view v-if="productList.length === 0 && !loading" class="empty-state">
            <image class="empty-icon" src="/static/images/empty.png"></image>
            <text class="empty-text">暂无商品</text>
          </view>
        </scroll-view>
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

export default {
  components: {
    TabBar,
    FloatingCart
  },
  data() {
    return {
      currentPath: '/ubPackages_user/pages/category/category',
      cartCount: 0, // 购物车商品数量
      
      // 搜索相关
      searchKeyword: '',
      
      // 分类相关
      selectedCategoryId: null,
      categoryScrollTop: 0,
      categoryList: [
        { id: 'all', name: '全部', icon: '/static/images/category.png' },
        { id: 1, name: '蜀菜', icon: '/static/images/vegetables.png' },
        { id: 2, name: '水果', icon: '/static/images/fruits.png' },
        { id: 3, name: '粮油', icon: '/static/images/grains.png' },
        { id: 4, name: '肉禽蛋', icon: '/static/images/meat.png' },
        { id: 5, name: '水产', icon: '/static/images/seafood.png' },
        { id: 6, name: '蘑菇', icon: '/static/images/mushroom.png' },
        { id: 7, name: '豆制品', icon: '/static/images/tofu.png' },
        { id: 8, name: '调料', icon: '/static/images/category.png' }
      ],
      
      // 商品相关
      productList: [],
      productScrollTop: 0,
      loading: false,
      hasMore: true,
      pageNum: 1,
      pageSize: 20
    }
  },
  onLoad(options) {
    // 接收从首页传来的分类ID
    if (options.categoryId) {
      this.selectedCategoryId = options.categoryId
    } else {
      this.selectedCategoryId = 'all'
    }
    
    // 初始化数据
    this.initData()
  },
  onShow() {
    // 监听分类选择事件
    uni.$on('selectCategory', this.handleCategorySelect)
    
    // 刷新购物车数量
    this.updateCartCount()
  },
  onHide() {
    // 移除事件监听
    uni.$off('selectCategory', this.handleCategorySelect)
  },
  methods: {
    // 初始化数据
    async initData() {
      this.loadProductList(true)
    },
    
    // =================================
    // 搜索相关方法
    // =================================
    
    // 处理搜索点击
    handleSearchClick() {
      uni.navigateTo({
        url: '/ubPackages_user/pages/search/search?keyword=' + encodeURIComponent(this.searchKeyword)
      })
    },
    
    // 处理搜索输入
    handleSearchInput(e) {
      this.searchKeyword = e.detail.value
    },
    
    // 处理搜索确认
    handleSearchConfirm() {
      if (this.searchKeyword.trim()) {
        this.handleSearchClick()
      }
    },
    
    // =================================
    // 分类相关方法
    // =================================
    
    // 处理分类选择
    handleCategorySelect(category, index) {
      if (this.selectedCategoryId === category.id) return
      
      this.selectedCategoryId = category.id
      console.log('选择分类:', category)
      
      // 重新加载商品列表
      this.loadProductList(true)
    },
    
    // =================================
    // 商品相关方法
    // =================================
    
    // 加载商品列表
    async loadProductList(isRefresh = false) {
      if (this.loading) return
      
      this.loading = true
      
      try {
        if (isRefresh) {
          this.pageNum = 1
          this.productList = []
          this.hasMore = true
        }
        
        // 模拟商品数据（实际开发中调用API）
        const mockProducts = this.getMockProducts()
        
        if (this.pageNum === 1) {
          this.productList = mockProducts
        } else {
          this.productList = [...this.productList, ...mockProducts]
        }
        
        // 模拟分页逻辑
        if (mockProducts.length < this.pageSize) {
          this.hasMore = false
        }
        
        this.pageNum++
        
      } catch (error) {
        console.error('加载商品列表失败:', error)
        uni.showToast({
          title: '加载失败，请重试',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },
    
    // 获取模拟商品数据
    getMockProducts() {
      const products = [
        {
          id: 1,
          name: '新鲜胡萝卜',
          image: 'https://via.placeholder.com/200x200/4CAF50/FFFFFF?text=胡萝卜',
          price: 7.8,
          unit: '斤',
          sales: 230,
          badge: '热销'
        },
        {
          id: 2,
          name: '新鲜土豆',
          image: 'https://via.placeholder.com/200x200/FF9800/FFFFFF?text=土豆',
          price: 6.8,
          unit: '斤',
          sales: 156,
          badge: null
        },
        {
          id: 3,
          name: '有机西红柿',
          image: 'https://via.placeholder.com/200x200/F44336/FFFFFF?text=西红柿',
          price: 12.5,
          unit: '斤',
          sales: 89,
          badge: '有机'
        },
        {
          id: 4,
          name: '新鲜黑玉米',
          image: 'https://via.placeholder.com/200x200/795548/FFFFFF?text=黑玉米',
          price: 8.9,
          unit: '斤',
          sales: 67,
          badge: null
        },
        {
          id: 5,
          name: '优质大米',
          image: 'https://via.placeholder.com/200x200/FFC107/FFFFFF?text=大米',
          price: 5.6,
          unit: '斤',
          sales: 345,
          badge: '精选'
        },
        {
          id: 6,
          name: '野生蘑菇',
          image: 'https://via.placeholder.com/200x200/8BC34A/FFFFFF?text=蘑菇',
          price: 15.8,
          unit: '斤',
          sales: 23,
          badge: '野生'
        }
      ]
      
      // 根据选中的分类过滤商品（模拟）
      if (this.selectedCategoryId === 'all') {
        return products
      }
      
      // 返回过滤后的商品（实际应该按分类ID过滤）
      return products.slice(0, 4)
    },
    
    // 处理商品点击
    handleProductClick(product) {
      console.log('点击商品:', product.name)
      // 跳转到商品详情页
      uni.navigateTo({
        url: `/ubPackages_user/pages/category/product-detail?id=${product.id}`
      })
    },
    
    // 获取商品最低价格
    getMinPrice(product) {
      // 如果商品有多个规格，返回最低价格
      if (product.specs && product.specs.length > 0) {
        const prices = product.specs.map(spec => parseFloat(spec.price)).filter(price => !isNaN(price))
        return prices.length > 0 ? Math.min(...prices) : product.price
      }
      // 否则返回默认价格
      return product.price
    },
    
    // 处理加载更多
    handleLoadMore() {
      if (!this.hasMore || this.loading) return
      this.loadProductList(false)
    },
    
    // =================================
    // 购物车相关方法
    // =================================
    
    // 处理购物车点击
    handleCartClick() {
      console.log('点击悬浮购物车')
      // 跳转到购物车页面
      // uni.navigateTo({
      //   url: '/pages/cart/cart'
      // })
    },
    
    // 处理加入购物车
    handleAddToCart(product) {
      console.log('加入购物车:', product.name)
      this.cartCount++
      
      uni.showToast({
        title: '已加入购物车',
        icon: 'success',
        duration: 1500
      })
    },
    
    // 更新购物车数量
    updateCartCount() {
      // 这里可以从本地存储或API获取购物车数量
      // const cartData = uni.getStorageSync('cartData')
      // this.cartCount = cartData ? cartData.totalCount : 0
    },
    
    // =================================
    // 工具方法
    // =================================
    
    // 格式化价格
    formatPrice(price) {
      if (price === null || price === undefined) {
        return '0.00'
      }
      const num = parseFloat(price)
      if (isNaN(num)) {
        return '0.00'
      }
      return num.toFixed(2)
    }
  }
}
</script>

<style>
/* 页面整体布局 */
.page {
  min-height: 100vh;
  background-color: #f8f9fa;
  padding-bottom: calc(100rpx + env(safe-area-inset-bottom));
}

/* =================================
   顶部搜索组件样式
   ================================= */
.search-header {
  background-color: #ffffff;
  padding: 20rpx 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.search-bar {
  display: flex;
  align-items: center;
  background-color: #f3f4f6;
  border-radius: 50rpx;
  padding: 16rpx 24rpx;
  position: relative;
}

.search-icon {
  width: 32rpx;
  height: 32rpx;
  margin-right: 16rpx;
  opacity: 0.6;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: #333333;
  background-color: transparent;
  border: none;
  outline: none;
}

/* =================================
   主体内容区域样式
   ================================= */
.content-container {
  display: flex;
  height: calc(100vh - 140rpx - 100rpx); /* 减去顶部搜索和底部导航 */
}

/* =================================
   左侧分类导航样式
   ================================= */
.category-sidebar {
  width: 180rpx;
  background-color: #f8f9fa;
  border-right: 1rpx solid #e9ecef;
}

.category-list {
  height: 100%;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30rpx 10rpx;
  background-color: #f8f9fa;
  border-bottom: 1rpx solid #e9ecef;
  transition: all 0.3s ease;
}

.category-item.active {
  background-color: #ffffff;
  border-right: 6rpx solid #4CAF50;
}

.category-icon {
  width: 40rpx;
  height: 40rpx;
  margin-bottom: 8rpx;
}

.category-name {
  font-size: 22rpx;
  color: #666666;
  text-align: center;
  line-height: 1.2;
}

.category-item.active .category-name {
  color: #4CAF50;
  font-weight: bold;
}

/* =================================
   右侧商品列表区域
   ================================= */
.product-container {
  flex: 1;
  background-color: #ffffff;
}

.product-scroll {
  height: 100%;
}

/* =================================
   商品卡片容器样式
   ================================= */
.product-grid {
  display: flex;
  flex-direction: column;
  padding: 20rpx;
  gap: 16rpx;
}

/* 商品卡片主容器 - 单列布局 */
.product-card {
  width: 100%;
  transition: transform 0.2s ease, box-shadow 0.3s ease;
}

.product-card:active {
  transform: scale(0.98);
}

/* 卡片容器 - 垂直布局 */
.card-container {
  background-color: #ffffff;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid #f0f0f0;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  min-height: 400rpx;
}

.card-container:hover {
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.12);
  transform: translateY(-2rpx);
}

/* =================================
   上方图片区域（全宽度）
   ================================= */
.image-section {
  position: relative;
  width: 100%;
  height: 280rpx;
  flex-shrink: 0;
  overflow: hidden;
  background-color: #f8f9fa;
}

/* 响应式图片技术 */
.responsive-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  transition: transform 0.3s ease;
}

.responsive-image:hover {
  transform: scale(1.05);
}

/* 角标标签 */
.product-badge {
  position: absolute;
  top: 16rpx;
  right: 16rpx;
  background: linear-gradient(135deg, #ff6b6b, #ff4757);
  color: #ffffff;
  padding: 6rpx 12rpx;
  border-radius: 16rpx;
  font-size: 18rpx;
  font-weight: bold;
  z-index: 2;
  box-shadow: 0 2rpx 6rpx rgba(255, 71, 87, 0.3);
}

.badge-text {
  font-size: 18rpx;
  color: #ffffff;
  line-height: 1;
}

/* =================================
   下方文本与交互区域
   ================================= */
.content-section {
  flex: 1;
  padding: 20rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 8rpx;
}

/* 商品名称和销量 - 水平布局 */
.product-title-block {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8rpx;
}

.product-name {
  font-size: 30rpx;
  color: #1c2534;
  font-weight: 500;
  line-height: 1.3;
  flex: 1;
  margin-right: 16rpx;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  word-break: break-word;
}

/* 销量信息 - 内联元素组合 */
.sales-info-block {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.sales-label {
  font-size: 20rpx;
  color: #f2645f;
  margin-right: 4rpx;
}

.sales-value {
  font-size: 20rpx;
  color: #f2645f;
  font-weight: 500;
}

/* =================================
   价格与交互区域（水平布局）
   ================================= */
.price-action-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8rpx;
}

/* 价格信息 - 内联元素组合 */
.price-inline-group {
  display: flex;
  align-items: baseline;
  gap: 2rpx;
}

.currency-symbol {
  font-size: 20rpx;
  color: #ff4757;
  font-weight: bold;
  line-height: 1;
}

.price-value {
  font-size: 32rpx;
  color: #ff4757;
  font-weight: bold;
  line-height: 1;
}

.price-unit {
  font-size: 18rpx;
  color: #999999;
  margin-left: 4rpx;
  line-height: 1;
}

/* 交互按钮 */
.action-button {
  background: #f87171;
  color: #ffffff;
  padding: 16rpx 24rpx;
  border-radius: 0;
  font-size: 22rpx;
  font-weight: bold;
  text-align: center;
  transition: all 0.2s ease;
  box-shadow: 0 4rpx 8rpx rgba(248, 113, 113, 0.3);
  min-width: 120rpx;
}

.action-button:active {
  transform: scale(0.95);
  box-shadow: 0 2rpx 4rpx rgba(248, 113, 113, 0.3);
}

.button-text {
  font-size: 22rpx;
  color: #ffffff;
  line-height: 1;
}

/* =================================
   响应式设计适配 - 单列布局
   ================================= */

/* 中等屏幕设备 */
@media screen and (max-width: 750rpx) {
  .card-container {
    min-height: 360rpx;
  }
  
  .image-section {
    height: 240rpx;
  }
  
  .content-section {
    padding: 16rpx;
    gap: 8rpx;
  }
  
  .product-name {
    font-size: 26rpx;
  }
  
  .price-value {
    font-size: 30rpx;
  }
}

/* 小屏幕设备 */
@media screen and (max-width: 600rpx) {
  .card-container {
    min-height: 320rpx;
  }
  
  .image-section {
    height: 200rpx;
  }
  
  .content-section {
    padding: 12rpx;
    gap: 6rpx;
  }
  
  .product-name {
    font-size: 24rpx;
    -webkit-line-clamp: 1;
  }
  
  .price-value {
    font-size: 28rpx;
  }
  
  .action-button {
    padding: 12rpx 16rpx;
    min-width: 100rpx;
  }
  
  .button-text {
    font-size: 18rpx;
  }
}

/* =================================
   状态样式
   ================================= */
.loading-more {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx 0;
}

.loading-text {
  font-size: 24rpx;
  color: #999999;
}

.no-more {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx 0;
}

.no-more-text {
  font-size: 24rpx;
  color: #cccccc;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 40rpx;
}

.empty-icon {
  width: 120rpx;
  height: 120rpx;
  margin-bottom: 24rpx;
  opacity: 0.5;
}

.empty-text {
  font-size: 28rpx;
  color: #999999;
}


</style>