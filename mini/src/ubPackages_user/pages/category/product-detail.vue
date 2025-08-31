<template>
  <view class="product-detail-page">
    <!-- 顶部导航栏 -->
    <view class="nav-bar">
      <view class="nav-back" @click="handleBack">
        <image class="back-icon" src="/static/images/back.png"></image>
      </view>
      <view class="nav-title">商品详情</view>
      <view class="nav-right"></view>
    </view>

    <!-- 商品图片轮播 -->
    <view class="product-images">
      <swiper 
        class="image-swiper" 
        indicator-dots 
        autoplay 
        interval="3000"
        duration="500"
      >
        <swiper-item v-for="(image, index) in productImages" :key="index">
          <image 
            class="product-image" 
            :src="image" 
            mode="aspectFill"
            @click="previewImage(image)"
          ></image>
        </swiper-item>
      </swiper>
    </view>

    <!-- 商品基本信息 -->
    <view class="product-info">
      <view class="product-title">{{ productDetail.name }}</view>
      <view class="product-subtitle">{{ productDetail.description }}</view>
      
      <view class="price-section">
        <view class="current-price">
          <text class="currency">¥</text>
          <text class="price-value">{{ formatPrice(productDetail.minPrice) }}</text>
          <text class="price-unit">/{{ productDetail.unit || '斤' }}</text>
        </view>
        <view class="sales-info">
          <text class="sales-text">销量 {{ productDetail.sales || 0 }}件</text>
        </view>
      </view>
    </view>

    <!-- 商品详情描述 -->
    <view class="product-description">
      <view class="section-title">商品详情</view>
      <view class="description-content">
        <text class="description-text">{{ productDetail.detailDescription || '暂无详细描述' }}</text>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-actions">
      <view class="action-left">
        <view class="cart-btn" @click="handleCartClick">
          <image class="cart-icon" src="/static/images/cart.png"></image>
          <text class="cart-text">购物车</text>
          <view v-if="cartCount > 0" class="cart-badge">{{ cartCount }}</view>
        </view>
      </view>
      <view class="action-right">
        <view class="add-cart-btn" @click="handleAddToCart">
          <text class="btn-text">加入购物车</text>
        </view>
        <view class="buy-now-btn" @click="handleBuyNow">
          <text class="btn-text">立即购买</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      productId: '',
      productDetail: {},
      productImages: [],
      cartCount: 0
    }
  },
  
  onLoad(options) {
    if (options.id) {
      this.productId = options.id
      this.loadProductDetail()
    }
  },
  
  methods: {
    // 返回上一页
    handleBack() {
      uni.navigateBack()
    },
    
    // 加载商品详情
    async loadProductDetail() {
      try {
        // 模拟商品详情数据
        this.productDetail = {
          id: this.productId,
          name: '新鲜有机苹果',
          description: '来自山东烟台的优质苹果，口感香甜',
          minPrice: 12.80,
          unit: '斤',
          sales: 156,
          detailDescription: '这是一款来自山东烟台的优质苹果，采用有机种植方式，无农药残留，口感香甜脆嫩，营养丰富。适合全家老少食用。'
        }
        
        this.productImages = [
          '/static/images/apple1.jpg',
          '/static/images/apple2.jpg',
          '/static/images/apple3.jpg'
        ]
        
        // TODO: 调用真实API获取商品详情
        // const response = await this.$api.getProductDetail(this.productId)
        // this.productDetail = response.data
      } catch (error) {
        console.error('加载商品详情失败:', error)
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        })
      }
    },
    
    // 预览图片
    previewImage(current) {
      uni.previewImage({
        current: current,
        urls: this.productImages
      })
    },
    
    // 购物车点击
    handleCartClick() {
      uni.navigateTo({
        url: '/pages/cart/cart'
      })
    },
    
    // 加入购物车
    handleAddToCart() {
      try {
        // TODO: 调用加入购物车API
        this.cartCount++
        uni.showToast({
          title: '已加入购物车',
          icon: 'success'
        })
      } catch (error) {
        console.error('加入购物车失败:', error)
        uni.showToast({
          title: '操作失败',
          icon: 'none'
        })
      }
    },
    
    // 立即购买
    handleBuyNow() {
      uni.navigateTo({
        url: `/pages/order/order?productId=${this.productId}&type=buy`
      })
    },
    
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
.product-detail-page {
  min-height: 100vh;
  background-color: #f8f9fa;
  padding-bottom: 100rpx;
}

/* 导航栏 */
.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 88rpx;
  background-color: #ffffff;
  padding: 0 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.nav-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  width: 32rpx;
  height: 32rpx;
}

.nav-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
}

.nav-right {
  width: 60rpx;
}

/* 商品图片 */
.product-images {
  margin-top: 88rpx;
  height: 500rpx;
  background-color: #ffffff;
}

.image-swiper {
  width: 100%;
  height: 100%;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 商品信息 */
.product-info {
  background-color: #ffffff;
  padding: 30rpx;
  margin-top: 20rpx;
}

.product-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #1c2534;
  line-height: 1.4;
  margin-bottom: 12rpx;
}

.product-subtitle {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.4;
  margin-bottom: 24rpx;
}

.price-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.current-price {
  display: flex;
  align-items: baseline;
  gap: 4rpx;
}

.currency {
  font-size: 24rpx;
  color: #ff4757;
  font-weight: bold;
}

.price-value {
  font-size: 48rpx;
  color: #ff4757;
  font-weight: bold;
}

.price-unit {
  font-size: 24rpx;
  color: #999999;
}

.sales-info {
  display: flex;
  align-items: center;
}

.sales-text {
  font-size: 24rpx;
  color: #f2645f;
  background-color: #fff5f5;
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
}

/* 商品详情 */
.product-description {
  background-color: #ffffff;
  padding: 30rpx;
  margin-top: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 20rpx;
}

.description-content {
  line-height: 1.6;
}

.description-text {
  font-size: 28rpx;
  color: #666666;
}

/* 底部操作栏 */
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100rpx;
  background-color: #ffffff;
  border-top: 1rpx solid #f0f0f0;
  display: flex;
  align-items: center;
  padding: 0 30rpx;
  z-index: 1000;
}

.action-left {
  flex: 0 0 120rpx;
}

.cart-btn {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.cart-icon {
  width: 40rpx;
  height: 40rpx;
  margin-bottom: 4rpx;
}

.cart-text {
  font-size: 20rpx;
  color: #666666;
}

.cart-badge {
  position: absolute;
  top: -8rpx;
  right: 20rpx;
  background-color: #ff4757;
  color: #ffffff;
  font-size: 18rpx;
  padding: 2rpx 8rpx;
  border-radius: 20rpx;
  min-width: 32rpx;
  text-align: center;
}

.action-right {
  flex: 1;
  display: flex;
  gap: 20rpx;
  margin-left: 30rpx;
}

.add-cart-btn {
  flex: 1;
  height: 60rpx;
  background-color: #ffa726;
  display: flex;
  align-items: center;
  justify-content: center;
}

.buy-now-btn {
  flex: 1;
  height: 60rpx;
  background-color: #f87171;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-text {
  font-size: 28rpx;
  color: #ffffff;
  font-weight: bold;
}
</style>