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
        
        <view class="product-main-info">
          <text class="product-name">{{ product.productName }}</text>
                  <view class="price-section">
          <view class="price-row">
            <text class="current-price">¥{{ product.productPrice }}</text>
          </view>
          <view class="price-tags">
            <text class="price-tag">产地直供</text>
            <text class="price-tag">新鲜采摘</text>
          </view>
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
        <view class="spec-grid">
          <view class="spec-item-card">
            <view class="spec-icon">📦</view>
            <view class="spec-content">
              <text class="spec-label">规格</text>
              <text class="spec-value">{{ product.productSpec || '暂无规格信息' }}</text>
            </view>
          </view>
          <view class="spec-item-card">
            <view class="spec-icon">⚖️</view>
            <view class="spec-content">
              <text class="spec-label">单位</text>
              <text class="spec-value">{{ product.productUnit || '件' }}</text>
            </view>
          </view>
          <view class="spec-item-card">
            <view class="spec-icon">📊</view>
            <view class="spec-content">
              <text class="spec-label">库存</text>
              <text class="spec-value" :class="{ 'low-stock': product.stockQuantity <= 10 }">
                {{ product.stockQuantity }}件
              </text>
            </view>
          </view>
          <view class="spec-item-card">
            <view class="spec-icon">🛒</view>
            <view class="spec-content">
              <text class="spec-label">起订量</text>
              <text class="spec-value">{{ product.minOrderQuantity }}件</text>
            </view>
          </view>
        </view>
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
        <view class="action-btn cursor-pointer" @click="toggleFavorite">
          <image :src="getImageUrl(isFavorited ? 'icon/已收藏.png' : 'icon/favorite.png')" class="action-icon" mode="aspectFit"></image>
          <text class="action-text" :class="{ 'favorite-active-text': isFavorited }">{{ isFavorited ? '已收藏' : '收藏' }}</text>
        </view>
        <view class="action-btn cursor-pointer" @click="addToCart">
          <image :src="getImageUrl('icon/cart.png')" class="action-icon" mode="aspectFit"></image>
          <text class="action-text">购物车</text>
        </view>
      </view>
      <view class="buy-buttons">
        <view class="add-cart-btn cursor-pointer" @click="showPurchaseModal('cart')">
          <text>加入购物车</text>
        </view>
        <view class="buy-now-btn cursor-pointer" @click="showPurchaseModal('buy')">
          <text>立即购买</text>
        </view>

      </view>
    </view>

    <!-- 购买弹窗 -->
    <view v-if="showModal" class="modal-overlay" @click="closeModal">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">{{ modalType === 'buy' ? '立即购买' : '加入购物车' }}</text>
          <view class="close-btn cursor-pointer" @click="closeModal">
            <text>×</text>
          </view>
        </view>
        
        <view class="modal-body">
          <!-- 商品信息 -->
          <view class="product-summary">
            <image :src="product.mainImageUrl ? getImageUrl(product.mainImageUrl) : '/static/default-product.png'" 
                   mode="aspectFill" class="product-thumbnail" />
            <view class="product-info">
              <text class="product-title">{{ product.productName }}</text>
              <text class="product-price">¥{{ product.productPrice }}</text>
            </view>
          </view>
          
          <!-- 数量选择 -->
          <view class="quantity-section">
            <text class="quantity-label">购买数量</text>
            <view class="quantity-selector">
              <view class="quantity-btn cursor-pointer" @click="decreaseQuantity" :class="{ disabled: modalQuantity <= 1 }">
                <text>-</text>
              </view>
              <input 
                type="number" 
                v-model="modalQuantity" 
                class="quantity-input"
                @input="onModalQuantityInput"
              />
              <view class="quantity-btn cursor-pointer" @click="increaseQuantity" :class="{ disabled: modalQuantity >= product.stockQuantity }">
                <text>+</text>
              </view>
            </view>
            <text class="stock-tip">库存{{ product.stockQuantity }}件</text>
          </view>
          
          <!-- 价格计算 -->
          <view class="price-calculation">
            <view class="price-row">
              <text class="price-label">单价</text>
              <text class="price-value">¥{{ product.productPrice }}</text>
            </view>
            <view class="price-row">
              <text class="price-label">数量</text>
              <text class="price-value">{{ modalQuantity }}件</text>
            </view>
            <view class="price-row total">
              <text class="price-label">总计</text>
              <text class="total-price">¥{{ totalPrice }}</text>
            </view>
          </view>
        </view>
        
        <view class="modal-footer">
          <view class="modal-btn cancel-btn cursor-pointer" @click="closeModal">
            <text>取消</text>
          </view>
          <view class="modal-btn confirm-btn cursor-pointer" @click="confirmPurchase">
            <text>{{ modalType === 'buy' ? '立即购买' : '加入购物车' }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import productsApi from '../../api/products.js';
import cartApi from '../../api/cart.js';
import favoritesApi from '../../api/favorites.js';
import env from '../../config/env.js';

// 响应式数据
const product = ref({});
const loading = ref(true);
const error = ref('');
const currentImage = ref(0);
const recommendProducts = ref([]);
const isFavorited = ref(false);

// 弹窗相关
const showModal = ref(false);
const modalType = ref('buy'); // 'buy' 或 'cart'
const modalQuantity = ref(1);

// 计算属性
const productImages = computed(() => {
  if (!product.value.mainImageUrl) {
    return ['/static/default-product.png'];
  }
  return [product.value.mainImageUrl];
});

const totalPrice = computed(() => {
  return (product.value.productPrice * modalQuantity.value).toFixed(2);
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
      // 检查收藏状态
      checkFavoriteStatus();
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

// 弹窗相关方法
const showPurchaseModal = (type) => {
  modalType.value = type;
  modalQuantity.value = 1;
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
};

// 数量操作
const decreaseQuantity = () => {
  if (modalQuantity.value > 1) {
    modalQuantity.value--;
  }
};

const increaseQuantity = () => {
  if (modalQuantity.value < product.value.stockQuantity) {
    modalQuantity.value++;
  }
};

const onModalQuantityInput = (e) => {
  const value = parseInt(e.detail.value);
  if (isNaN(value) || value < 1) {
    modalQuantity.value = 1;
  } else if (value > product.value.stockQuantity) {
    modalQuantity.value = product.value.stockQuantity;
  } else {
    modalQuantity.value = value;
  }
};

// 确认购买
const confirmPurchase = async () => {
  if (modalQuantity.value > product.value.stockQuantity) {
    uni.showToast({
      title: '库存不足',
      icon: 'error'
    });
    return;
  }
  
  try {
    if (modalType.value === 'cart') {
      // 添加到购物车
      await cartApi.addToCart(product.value.id, modalQuantity.value);
      
      uni.showToast({
        title: '已添加到购物车',
        icon: 'success',
        duration: 1500
      });
    } else {
      // 立即购买
      uni.showToast({
        title: '功能开发中',
        icon: 'none'
      });
    }
    
    closeModal();
  } catch (error) {
    console.error('操作失败:', error);
    
    uni.showToast({
      title: error.message || '操作失败，请重试',
      icon: 'error'
    });
  }
};

// 直接添加到购物车（保留原有功能）
const addToCart = async () => {
  try {
    // 调用购物车API添加商品，默认数量为1
    await cartApi.addToCart(product.value.id, 1);
    
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

// 收藏商品
// 检查收藏状态
const checkFavoriteStatus = async () => {
  try {
    if (!product.value.productCode) return;
    
    const response = await favoritesApi.checkFavorite(favoritesApi.getCurrentUserId(), product.value.productCode);
    if (response.code === 200 && response.data) {
      isFavorited.value = response.data.isFavorited;
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error);
  }
};

// 切换收藏状态
const toggleFavorite = async () => {
  try {
    if (!product.value.productCode) {
      uni.showToast({
        title: '商品信息不完整',
        icon: 'error'
      });
      return;
    }
    
    if (isFavorited.value) {
      // 取消收藏
      await favoritesApi.removeFavorite(favoritesApi.getCurrentUserId(), product.value.productCode);
      isFavorited.value = false;
      uni.showToast({
        title: '已取消收藏',
        icon: 'success'
      });
    } else {
      // 添加收藏
      await favoritesApi.addFavorite(favoritesApi.getCurrentUserId(), product.value.productCode);
      isFavorited.value = true;
      uni.showToast({
        title: '已收藏',
        icon: 'success'
      });
    }
  } catch (error) {
    console.error('收藏操作失败:', error);
    uni.showToast({
      title: error.message || '操作失败，请重试',
      icon: 'error'
    });
  }
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
  background: linear-gradient(135deg, #ff4444 0%, #ff6666 100%);
  color: white;
  font-size: 22rpx;
  padding: 10rpx 20rpx;
  border-radius: 24rpx;
  font-weight: 600;
  box-shadow: 0 4rpx 12rpx rgba(255, 68, 68, 0.3);
}

.new-tag {
  background: linear-gradient(135deg, #4CAF50 0%, #66BB6A 100%);
  box-shadow: 0 4rpx 12rpx rgba(76, 175, 80, 0.3);
}

.product-code {
  font-size: 24rpx;
  color: #999999;
}

.product-main-info {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.product-name {
  font-size: 44rpx;
  color: #333333;
  font-weight: bold;
  line-height: 1.3;
  margin-bottom: 8rpx;
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
  align-items: center;
  background: linear-gradient(135deg, #f0f9ff 0%, #fff 100%);
  padding: 24rpx;
  border-radius: 16rpx;
  border: 1px solid #e0f2fe;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 16rpx;
}

.current-price {
  font-size: 56rpx;
  color: #4CAF50;
  font-weight: bold;
  text-shadow: 0 2rpx 4rpx rgba(76, 175, 80, 0.1);
}

.original-price {
  font-size: 28rpx;
  color: #999999;
  text-decoration: line-through;
}

.price-tags {
  display: flex;
  gap: 8rpx;
  flex-direction: column;
}

.price-tag {
  background-color: #fff;
  color: #4CAF50;
  font-size: 22rpx;
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
  border: 1px solid #4CAF50;
  text-align: center;
  font-weight: 500;
  box-shadow: 0 2rpx 8rpx rgba(76, 175, 80, 0.1);
}

/* 规格卡片 */
.spec-card, .detail-card, .recommend-card {
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

.spec-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}

.spec-item-card {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 24rpx;
  border-radius: 16rpx;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border: 1px solid #e9ecef;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.spec-item-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4rpx;
  background: linear-gradient(90deg, #4CAF50, #66BB6A);
}

.spec-icon {
  font-size: 44rpx;
  color: #4CAF50;
  background: rgba(76, 175, 80, 0.1);
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.spec-content {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 0;
}

.spec-label {
  font-size: 24rpx;
  color: #666666;
  margin-bottom: 8rpx;
  font-weight: 500;
}

.spec-value {
  font-size: 30rpx;
  color: #333333;
  font-weight: 600;
  line-height: 1.2;
}

.low-stock {
  color: #FF4433;
  font-weight: 700;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
  100% {
    opacity: 1;
  }
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
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
  padding: 8rpx 0;
}

.recommend-item {
  background-color: #f8f8f8;
  border-radius: 16rpx;
  padding: 16rpx;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

.recommend-item:active {
  transform: scale(0.98);
  background-color: #f0f0f0;
}

.recommend-image {
  width: 100%;
  height: 200rpx;
  border-radius: 12rpx;
  margin-bottom: 16rpx;
  object-fit: cover;
}

.recommend-info {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  flex: 1;
}

.recommend-name {
  font-size: 26rpx;
  color: #333333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.3;
  font-weight: 500;
  min-height: 68rpx;
}

.recommend-price {
  font-size: 28rpx;
  color: #FF4433;
  font-weight: bold;
  margin-top: auto;
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

.favorite-active-text {
  color: #FF4433;
  font-weight: bold;
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

/* 购买弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 2000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #ffffff;
  border-radius: 24rpx 24rpx 0 0;
  max-height: 70vh;
  overflow: hidden;
  animation: slideUp 0.3s ease;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.15);
}

@keyframes slideUp {
  from { 
    transform: translateY(100%);
  }
  to { 
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: 1px solid #f0f0f0;
  background-color: #ffffff;
  position: relative;
}

.modal-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60rpx;
  height: 6rpx;
  background-color: #e0e0e0;
  border-radius: 3rpx;
}

.modal-title {
  font-size: 36rpx;
  color: #333333;
  font-weight: bold;
}

.close-btn {
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666666;
  font-size: 32rpx;
  font-weight: bold;
}

.modal-body {
  padding: 32rpx;
  max-height: 50vh;
  overflow-y: auto;
}

.product-summary {
  display: flex;
  gap: 24rpx;
  margin-bottom: 32rpx;
  padding: 24rpx;
  background-color: #f8f9fa;
  border-radius: 16rpx;
}

.product-thumbnail {
  width: 120rpx;
  height: 120rpx;
  border-radius: 12rpx;
  flex-shrink: 0;
}

.product-info {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  flex: 1;
}

.product-title {
  font-size: 28rpx;
  color: #333333;
  font-weight: 600;
  line-height: 1.3;
  margin-bottom: 12rpx;
}

.product-price {
  font-size: 32rpx;
  color: #4CAF50;
  font-weight: bold;
}

.quantity-section {
  margin-bottom: 32rpx;
}

.quantity-label {
  font-size: 28rpx;
  color: #333333;
  font-weight: 600;
  margin-bottom: 20rpx;
  display: block;
}

.quantity-selector {
  display: flex;
  align-items: center;
  gap: 24rpx;
  margin-bottom: 16rpx;
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

.price-calculation {
  background-color: #f8f9fa;
  border-radius: 16rpx;
  padding: 24rpx;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.price-row:last-child {
  margin-bottom: 0;
}

.price-label {
  font-size: 28rpx;
  color: #666666;
}

.price-value {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
}

.price-row.total {
  border-top: 1px solid #e0e0e0;
  padding-top: 16rpx;
  margin-top: 16rpx;
}

.total-price {
  font-size: 36rpx;
  color: #4CAF50;
  font-weight: bold;
}

.modal-footer {
  display: flex;
  gap: 20rpx;
  padding: 32rpx;
  border-top: 1px solid #f0f0f0;
  background-color: #ffffff;
  padding-bottom: calc(32rpx + env(safe-area-inset-bottom));
}

.modal-btn {
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

.cancel-btn {
  background-color: #f0f0f0;
  color: #666666;
}

.cancel-btn:active {
  background-color: #e0e0e0;
  transform: scale(0.98);
}

.confirm-btn {
  background-color: #4CAF50;
  color: white;
}

.confirm-btn:active {
  background-color: #45a049;
  transform: scale(0.98);
}
</style> 