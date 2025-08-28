<template>
  <view class="page">
    <view class="content">
      <view class="header">
        <text class="title">{{ categoryName || '商品列表' }}</text>
      </view>
      
      <view class="product-list">
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
      
      <!-- 空状态 -->
      <view v-if="productList.length === 0" class="empty-state">
        <text class="empty-text">该分类暂无商品</text>
      </view>
    </view>
    
    <!-- 底部导航栏 -->
    <TabBar :currentPath="currentPath" />
  </view>
</template>

<script>
import TabBar from '@/components/TabBar.vue'

export default {
  components: {
    TabBar
  },
  data() {
    return {
      currentPath: '/ubPackages_user/pages/productList/productList',
      categoryId: '',
      categoryName: '',
      productList: []
    }
  },
  onLoad(options) {
    // 接收传递的分类参数
    this.categoryId = options.categoryId || ''
    this.categoryName = options.categoryName || '商品列表'
    
    // 加载该分类的商品数据
    this.loadProductList()
  },
  methods: {
    // 加载商品列表
    async loadProductList() {
      try {
        // 这里应该调用实际的API接口
        // const res = await api.getProductsByCategory(this.categoryId)
        // this.productList = res.data
        
        // 模拟数据
        this.productList = this.getMockProductList()
        
        console.log('加载分类商品:', this.categoryName, this.categoryId)
      } catch (error) {
        console.error('加载商品列表失败:', error)
      }
    },
    
    // 获取模拟商品数据
    getMockProductList() {
      const mockProducts = [
        {
          id: 1,
          name: `精选${this.categoryName}商品1`,
          price: '12.80',
          image: '/static/logo.png',
          badge: '热销'
        },
        {
          id: 2,
          name: `新鲜${this.categoryName}商品2`,
          price: '15.60',
          image: '/static/logo.png',
          badge: '新品'
        },
        {
          id: 3,
          name: `优质${this.categoryName}商品3`,
          price: '28.00',
          image: '/static/logo.png',
          badge: null
        },
        {
          id: 4,
          name: `特价${this.categoryName}商品4`,
          price: '18.80',
          image: '/static/logo.png',
          badge: '特价'
        }
      ]
      
      return mockProducts
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
      // 这里可以添加购买逻辑
      uni.showToast({
        title: '已加入购物车',
        icon: 'success',
        duration: 1500
      })
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

<style>
.page {
  min-height: 100vh;
  padding-bottom: calc(100rpx + env(safe-area-inset-bottom));
}

.content {
  padding: 40rpx 20rpx;
}

.header {
  text-align: center;
  margin-bottom: 40rpx;
}

.title {
  font-size: 36rpx;
  color: #333;
  font-weight: bold;
}

.product-list {
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

.empty-state {
  text-align: center;
  margin-top: 200rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}
</style>