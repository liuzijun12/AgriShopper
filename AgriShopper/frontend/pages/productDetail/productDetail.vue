<template>
  <view class="page-container">


    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <view class="loading-spinner"></view>
      <text class="loading-text">正在加载商品详情...</text>
    </view>

    <!-- 错误状态 -->
    <view v-else-if="error" class="error-container">
      <text class="error-text">{{ error }}</text>
      <view class="retry-button cursor-pointer" @click="loadProductDetail">
        <text>重新加载</text>
      </view>
    </view>

    <!-- 商品详情内容 -->
    <view v-else class="product-detail-content">
      <!-- 商品标题区域 -->
      <view class="product-title-section">
        <view class="nav-left cursor-pointer" @click="goBack">
          <image :src="getImageUrl('icon/1.png')" class="back-icon" mode="aspectFit"></image>
          <text class="back-text">返回</text>
        </view>
        <view class="nav-title">商品详情</view>
        <view class="nav-right">
          <view class="share-btn cursor-pointer" @click="shareProduct">
            <image :src="getImageUrl('icon/5.png')" class="share-icon" mode="aspectFit"></image>
          </view>
        </view>
      </view>

      <!-- 商品图片轮播 -->
      <view class="product-images">
        <swiper class="image-swiper" circular autoplay interval="4000" duration="500" @change="onImageChange">
          <swiper-item v-for="(image, index) in productImages" :key="index">
            <image :src="image" mode="aspectFill" class="product-image" @error="handleImageError" />
          </swiper-item>
        </swiper>
        <view class="image-dots">
          <view 
            v-for="(image, index) in productImages" 
            :key="index" 
            :class="['dot', currentImage === index ? 'active' : '']"
          ></view>
        </view>
      </view>

      <!-- 商品基本信息 -->
      <view class="product-info-card">
        <view class="product-header">
          <view class="product-tags">
            <text v-if="product.isHotProduct" class="hot-tag">热销</text>
            <text v-if="product.isNewProduct" class="new-tag">新品</text>
          </view>
          <view class="product-code">商品编码：{{ product.productCode }}</view>
        </view>
        
        <text class="product-name">{{ product.productName }}</text>
        <text class="product-desc">{{ product.productDescription }}</text>
        
        <view class="price-section">
          <view class="price-row">
            <text class="current-price">¥{{ product.productPrice }}</text>
            <text v-if="product.costPrice" class="original-price">¥{{ product.costPrice }}</text>
          </view>
          <view class="price-tags">
            <text class="price-tag">正品保证</text>
            <text class="price-tag">7天退换</text>
          </view>
        </view>
      </view>

      <!-- 商品详情 -->
      <view class="detail-card">
        <view class="card-title-large">
          <text>商品详情</text>
        </view>
        <view class="detail-content">
          <text class="detail-text">{{ product.productDescription || '暂无详细描述' }}</text>
        </view>
      </view>

      <!-- 商品规格信息 -->
      <view class="spec-card">
        <view class="card-title">
          <text>商品规格</text>
        </view>
        <view class="spec-list">
          <view class="spec-item">
            <text class="spec-label">规格：</text>
            <text class="spec-value">{{ product.productSpec || '暂无规格信息' }}</text>
          </view>
          <view class="spec-item">
            <text class="spec-label">单位：</text>
            <text class="spec-value">{{ product.productUnit || '件' }}</text>
          </view>
          <view class="spec-item">
            <text class="spec-label">库存：</text>
            <text class="spec-value" :class="{ 'low-stock': product.stockQuantity <= 10 }">
              {{ product.stockQuantity }}件
            </text>
          </view>
          <view class="spec-item">
            <text class="spec-label">起订量：</text>
            <text class="spec-value">{{ product.minOrderQuantity }}件</text>
          </view>
        </view>
      </view>

      <!-- 购买数量选择 -->
      <view class="quantity-card">
        <view class="card-title">
          <text>购买数量</text>
        </view>
        <view class="quantity-selector">
          <view class="quantity-btn cursor-pointer" @click="decreaseQuantity" :class="{ disabled: quantity <= 1 }">
            <text>-</text>
          </view>
          <input 
            type="number" 
            v-model="quantity" 
            class="quantity-input"
            @input="onQuantityInput"
          />
          <view class="quantity-btn cursor-pointer" @click="increaseQuantity" :class="{ disabled: quantity >= product.stockQuantity }">
            <text>+</text>
          </view>
        </view>
        <text class="stock-tip">库存{{ product.stockQuantity }}件</text>
      </view>

      <!-- 推荐商品 -->
      <view class="recommend-card">
        <view class="card-title">
          <text>相关推荐</text>
        </view>
        <view class="recommend-list">
          <view 
            v-for="(item, index) in recommendProducts" 
            :key="item.id || index"
            class="recommend-item cursor-pointer"
            @click="goToProductDetail(item.id)"
          >
            <image :src="item.image" mode="aspectFill" class="recommend-image" />
            <view class="recommend-info">
              <text class="recommend-name">{{ item.name }}</text>
              <text class="recommend-price">¥{{ item.price }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部购买栏 -->
    <view class="bottom-bar">
      <view class="action-buttons">
        <view class="action-btn cursor-pointer" @click="addToFavorites">
          <image :src="getImageUrl('icon/7.png')" class="action-icon" mode="aspectFit"></image>
          <text class="action-text">收藏</text>
        </view>
        <view class="action-btn cursor-pointer" @click="addToCart">
          <image :src="getImageUrl('icon/8.png')" class="action-icon" mode="aspectFit"></image>
          <text class="action-text">购物车</text>
        </view>
      </view>
      <view class="buy-buttons">
        <view class="buy-now-btn cursor-pointer" @click="buyNow">
          <text>立即购买</text>
        </view>
        <view class="add-cart-btn cursor-pointer" @click="addToCartAndBuy">
          <text>加入购物车</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import productsApi from '../../api/products.js';
import cartApi from '../../api/cart.js';
import env from '../../config/env.js';

// 响应式数据
const product = ref({});
const loading = ref(true);
const error = ref('');
const currentImage = ref(0);
const quantity = ref(1);
const recommendProducts = ref([]);

// 计算属性
const productImages = computed(() => {
  if (!product.value.mainImageUrl) {
    return ['/static/default-product.png'];
  }
  return [product.value.mainImageUrl];
});

// 获取商品详情
const loadProductDetail = async () => {
  try {
    loading.value = true;
    error.value = '';
    
    const productId = getProductIdFromUrl();
    if (!productId) {
      throw new Error('商品ID不存在');
    }
    
    const response = await productsApi.getProductDetail(productId);
    
    if (response.code === 200 && response.data) {
      product.value = response.data;
      // 加载推荐商品
      loadRecommendProducts();
    } else {
      throw new Error(response.message || '获取商品详情失败');
    }
  } catch (err) {
    console.error('加载商品详情失败:', err);
    error.value = err.message || '加载商品详情失败，请重试';
  } finally {
    loading.value = false;
  }
};

// 获取推荐商品
const loadRecommendProducts = async () => {
  try {
    const response = await productsApi.getProductList({
      page: 0,
      size: 4
    });
    
    if (response.code === 200 && response.data) {
      const productList = response.data.content || [];
      // 过滤掉当前商品
      const filteredProducts = productList.filter(p => p.id !== product.value.id);
      recommendProducts.value = filteredProducts.slice(0, 3).map(item => ({
        id: item.id,
        name: item.productName,
        price: item.productPrice,
        image: getImageUrl(item.mainImageUrl)
      }));
    }
  } catch (error) {
    console.error('加载推荐商品失败:', error);
  }
};

// 从URL获取商品ID
const getProductIdFromUrl = () => {
  const pages = getCurrentPages();
  const currentPage = pages[pages.length - 1];
  return currentPage.options?.id;
};

// 图片轮播切换
const onImageChange = (e) => {
  currentImage.value = e.detail.current;
};

// 数量操作
const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--;
  }
};

const increaseQuantity = () => {
  if (quantity.value < product.value.stockQuantity) {
    quantity.value++;
  }
};

const onQuantityInput = (e) => {
  const value = parseInt(e.detail.value);
  if (isNaN(value) || value < 1) {
    quantity.value = 1;
  } else if (value > product.value.stockQuantity) {
    quantity.value = product.value.stockQuantity;
  } else {
    quantity.value = value;
  }
};

// 购买操作
const buyNow = () => {
  if (quantity.value > product.value.stockQuantity) {
    uni.showToast({
      title: '库存不足',
      icon: 'error'
    });
    return;
  }
  
  // TODO: 跳转到订单确认页面
  uni.showToast({
    title: '功能开发中',
    icon: 'none'
  });
};

const addToCart = async () => {
  if (quantity.value > product.value.stockQuantity) {
    uni.showToast({
      title: '库存不足',
      icon: 'error'
    });
    return;
  }
  
  try {
    // 调用购物车API添加商品
    await cartApi.addToCart(product.value.id, quantity.value);
    
    uni.showToast({
      title: '已添加到购物车',
      icon: 'success',
      duration: 1500
    });
  } catch (error) {
    console.error('添加到购物车失败:', error);
    
    uni.showToast({
      title: error.message || '添加失败，请重试',
      icon: 'error'
    });
  }
};

const addToCartAndBuy = () => {
  if (quantity.value > product.value.stockQuantity) {
    uni.showToast({
      title: '库存不足',
      icon: 'error'
    });
    return;
  }
  
  // 添加到购物车，不跳转页面
  addToCart();
};

// 收藏商品
const addToFavorites = () => {
  // TODO: 实现收藏功能
  uni.showToast({
    title: '已收藏',
    icon: 'success'
  });
};

// 分享商品
const shareProduct = () => {
  // TODO: 实现分享功能
  uni.showToast({
    title: '分享功能开发中',
    icon: 'none'
  });
};

// 跳转到其他商品详情
const goToProductDetail = (productId) => {
  uni.redirectTo({
    url: `/pages/productDetail/productDetail?id=${productId}`
  });
};

// 返回上一页
const goBack = () => {
  uni.navigateBack();
};

// 图片URL处理
const getImageUrl = (path) => {
  if (!path) return '/static/default-product.png';
  
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path;
  }
  
  // 使用环境配置中的baseUrl
  const config = env.getConfig();
  return `${config.baseUrl}/${path}`;
};

// 处理图片加载错误
const handleImageError = (e) => {
  e.target.src = '/static/default-product.png';
};

// 页面加载
onMounted(() => {
  loadProductDetail();
});
</script>

<style>
page {
  background-color: #f5f5f5;
}

.cursor-pointer {
  cursor: pointer;
}

.page-container {
  min-height: 90vh;
  padding-bottom: 120rpx;
  padding-top: 20px;
}

/* 商品标题区域 */
.product-title-section {
  background-color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx 24rpx;
  border-bottom: 1px solid #f0f0f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  position: relative;
}

.nav-left {
  display: flex;
  align-items: center;
}

.back-icon {
  width: 32rpx;
  height: 32rpx;
  margin-right: 12rpx;
  transform: rotate(90deg);
}

.back-text {
  font-size: 32rpx;
  color: #333333;
  font-weight: 500;
}

.nav-title {
  font-size: 36rpx;
  color: #333333;
  font-weight: bold;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
}

.nav-right {
  display: flex;
  align-items: center;
}

.share-icon {
  width: 36rpx;
  height: 36rpx;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 40rpx;
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

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  font-size: 28rpx;
  color: #999999;
}

/* 错误状态 */
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 40rpx;
}

.error-text {
  color: #ff4444;
  font-size: 28rpx;
  margin-bottom: 40rpx;
  text-align: center;
}

.retry-button {
  background-color: #4CAF50;
  color: white;
  padding: 20rpx 40rpx;
  border-radius: 8rpx;
  font-size: 28rpx;
}

/* 商品详情内容 */
.product-detail-content {
  margin-top: 0;
}

/* 商品图片轮播 */
.product-images {
  position: relative;
  background-color: #ffffff;
  margin-bottom: 20rpx;
}

.image-swiper {
  height: 450rpx;
}

.product-image {
  width: 100%;
  height: 100%;
}

.image-dots {
  position: absolute;
  bottom: 24rpx;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12rpx;
}

.dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.7);
  transition: all 0.3s ease;
}

.dot.active {
  background-color: #4CAF50;
  transform: scale(1.2);
}

/* 商品信息卡片 */
.product-info-card {
  background-color: #ffffff;
  padding: 32rpx;
  margin-bottom: 20rpx;
  border-radius: 16rpx;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.product-tags {
  display: flex;
  gap: 12rpx;
}

.hot-tag, .new-tag {
  background-color: #ff4444;
  color: white;
  font-size: 20rpx;
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
}

.new-tag {
  background-color: #4CAF50;
}

.product-code {
  font-size: 24rpx;
  color: #999999;
}

.product-name {
  font-size: 40rpx;
  color: #333333;
  font-weight: bold;
  margin-bottom: 20rpx;
  line-height: 1.4;
}

.product-desc {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.6;
  margin-bottom: 32rpx;
}

.price-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 16rpx;
}

.current-price {
  font-size: 52rpx;
  color: #FF4433;
  font-weight: bold;
}

.original-price {
  font-size: 28rpx;
  color: #999999;
  text-decoration: line-through;
}

.price-tags {
  display: flex;
  gap: 12rpx;
}

.price-tag {
  background-color: #fff2f0;
  color: #FF4433;
  font-size: 20rpx;
  padding: 6rpx 12rpx;
  border-radius: 12rpx;
  border: 1px solid #FF4433;
}

/* 规格卡片 */
.spec-card, .quantity-card, .detail-card, .recommend-card {
  background-color: #ffffff;
  padding: 32rpx;
  margin-bottom: 20rpx;
  border-radius: 16rpx;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-title {
  font-size: 32rpx;
  color: #333333;
  font-weight: bold;
  margin-bottom: 24rpx;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 16rpx;
}

.card-title-large {
  font-size: 44rpx;
  color: #333333;
  font-weight: bold;
  margin-bottom: 32rpx;
  border-bottom: 3px solid #4CAF50;
  padding-bottom: 24rpx;
  position: relative;
}

.card-title-large::after {
  content: '';
  position: absolute;
  bottom: -3px;
  left: 0;
  width: 60rpx;
  height: 3px;
  background-color: #4CAF50;
  border-radius: 2rpx;
}

.spec-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.spec-item {
  display: flex;
  align-items: center;
}

.spec-label {
  font-size: 28rpx;
  color: #666666;
  width: 120rpx;
  flex-shrink: 0;
}

.spec-value {
  font-size: 28rpx;
  color: #333333;
  flex: 1;
}

.low-stock {
  color: #FF4433;
}

/* 数量选择器 */
.quantity-selector {
  display: flex;
  align-items: center;
  gap: 24rpx;
  margin-bottom: 20rpx;
}

.quantity-btn {
  width: 64rpx;
  height: 64rpx;
  background-color: #f8f8f8;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  color: #333333;
  transition: all 0.2s ease;
}

.quantity-btn:active {
  background-color: #e8e8e8;
  transform: scale(0.95);
}

.quantity-btn.disabled {
  color: #cccccc;
  background-color: #f0f0f0;
}

.quantity-input {
  width: 140rpx;
  height: 64rpx;
  text-align: center;
  border: 2px solid #e0e0e0;
  border-radius: 12rpx;
  font-size: 32rpx;
  background-color: #ffffff;
}

.stock-tip {
  font-size: 24rpx;
  color: #999999;
}

/* 商品详情 */
.detail-content {
  line-height: 1.8;
}

.detail-text {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.8;
}

/* 推荐商品 */
.recommend-list {
  display: flex;
  gap: 24rpx;
  overflow-x: auto;
  padding: 8rpx 0;
}

.recommend-item {
  flex-shrink: 0;
  width: 220rpx;
  background-color: #f8f8f8;
  border-radius: 16rpx;
  padding: 16rpx;
  transition: all 0.3s ease;
}

.recommend-item:active {
  transform: scale(0.98);
  background-color: #f0f0f0;
}

.recommend-image {
  width: 188rpx;
  height: 188rpx;
  border-radius: 12rpx;
  margin-bottom: 16rpx;
}

.recommend-info {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.recommend-name {
  font-size: 26rpx;
  color: #333333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 500;
}

.recommend-price {
  font-size: 26rpx;
  color: #FF4433;
  font-weight: bold;
}

/* 底部购买栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 120rpx;
  background-color: #ffffff;
  border-top: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  padding: 0 30rpx;
  z-index: 1000;
  box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.1);
}

.action-buttons {
  display: flex;
  gap: 40rpx;
  margin-right: 30rpx;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.action-icon {
  width: 40rpx;
  height: 40rpx;
}

.action-text {
  font-size: 20rpx;
  color: #666666;
}

.buy-buttons {
  flex: 1;
  display: flex;
  gap: 20rpx;
}

.buy-now-btn, .add-cart-btn {
  flex: 1;
  height: 80rpx;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  font-weight: bold;
  transition: all 0.3s ease;
}

.buy-now-btn {
  background-color: #FF4433;
  color: white;
}

.buy-now-btn:active {
  background-color: #e63946;
  transform: scale(0.98);
}

.add-cart-btn {
  background-color: #4CAF50;
  color: white;
}

.add-cart-btn:active {
  background-color: #45a049;
  transform: scale(0.98);
}
</style> 