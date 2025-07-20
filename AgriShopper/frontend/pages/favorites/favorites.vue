<template>
  <view class="page-container">
    <!-- 顶部导航栏 -->
    <view class="nav-header">
      <view class="nav-left cursor-pointer" @click="goBack">
        <image :src="getImageUrl('static/icon/1.png')" class="back-icon" mode="aspectFit"></image>
        <text class="back-text">返回</text>
      </view>
      <view class="nav-title">我的收藏</view>
      <view class="nav-right">
        <view v-if="favorites.length > 0" class="clear-btn cursor-pointer" @click="showClearConfirm">
          <text class="clear-text">清空</text>
        </view>
      </view>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading && favorites.length === 0" class="loading-container">
      <view class="loading-spinner"></view>
      <text class="loading-text">正在加载收藏记录...</text>
    </view>

    <!-- 错误状态 -->
    <view v-else-if="error && favorites.length === 0" class="error-container">
      <text class="error-text">{{ error }}</text>
      <view class="retry-button cursor-pointer" @click="loadFavorites">
        <text>重新加载</text>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else-if="favorites.length === 0" class="empty-container">
      <image :src="getImageUrl('static/icon/favorite.png')" class="empty-icon" mode="aspectFit"></image>
      <text class="empty-title">暂无收藏</text>
      <text class="empty-desc">您还没有收藏任何商品</text>
      <view class="go-shopping-btn cursor-pointer" @click="goToProductList">
        <text>去逛逛</text>
      </view>
    </view>

    <!-- 收藏列表 -->
    <view v-else class="favorites-content">
      <view class="favorites-header">
        <text class="favorites-count">共收藏 {{ favorites.length }} 件商品</text>
        <view class="sort-options">
          <text 
            :class="['sort-option', sortBy === 'time' ? 'sort-active' : '']" 
            @click="changeSort('time')"
          >
            收藏时间
          </text>
        </view>
      </view>

      <scroll-view class="favorites-list" scroll-y="true" @scrolltolower="loadMore">
        <view 
          class="favorite-item cursor-pointer" 
          v-for="(favorite, index) in favorites" 
          :key="favorite.id"
          @click="goToProductDetail(favorite)"
        >
          <!-- 商品图片 -->
          <image 
            class="product-image" 
            :src="getProductImage(favorite)" 
            mode="aspectFill"
            @error="handleImageError"
          ></image>
          
          <!-- 商品信息 -->
          <view class="product-info">
            <text class="product-name">{{ getProductName(favorite) }}</text>
            <text class="product-desc">{{ getProductDesc(favorite) }}</text>
            <view class="product-meta">
              <text class="product-price">¥{{ getProductPrice(favorite) }}</text>
              <text class="favorite-time">{{ formatTime(favorite.createTime) }}</text>
            </view>
          </view>
          
          <!-- 操作按钮 -->
          <view class="action-buttons">
            <view class="action-btn cursor-pointer" @click.stop="addToCart(favorite)">
              <image :src="getImageUrl('static/icon/cart.png')" class="action-icon" mode="aspectFit"></image>
              <text class="action-text">加入购物车</text>
            </view>
            <view class="action-btn cursor-pointer" @click.stop="removeFavorite(favorite)">
              <image :src="getImageUrl('static/icon/已收藏.png')" class="action-icon" mode="aspectFit"></image>
              <text class="action-text">取消收藏</text>
            </view>
          </view>
        </view>

        <!-- 加载更多 -->
        <view v-if="hasMore && !loading" class="load-more">
          <text class="load-more-text">上拉加载更多</text>
        </view>

        <!-- 没有更多数据 -->
        <view v-if="!hasMore && favorites.length > 0" class="no-more">
          <text class="no-more-text">没有更多收藏了</text>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import favoritesApi from '../../api/favorites.js';
import cartApi from '../../api/cart.js';
import productsApi from '../../api/products.js';
import env from '../../config/env.js';

// 响应式数据
const favorites = ref([]);
const loading = ref(true);
const error = ref('');
const hasMore = ref(true);
const sortBy = ref('time');
const productsMap = ref(new Map()); // 存储商品信息的映射

// 分页参数
const pageParams = {
  page: 0,
  size: 10
};

// 加载收藏列表
const loadFavorites = async (reset = false) => {
  try {
    console.log('开始加载收藏数据，reset:', reset);
    loading.value = true;
    error.value = '';
    
    if (reset) {
      pageParams.page = 0;
      favorites.value = [];
      hasMore.value = true;
    }
    
    const userId = favoritesApi.getCurrentUserId();
    console.log('用户ID:', userId);
    
    const response = await favoritesApi.getUserFavoritesPage(
      userId, 
      pageParams.page, 
      pageParams.size
    );
    
    console.log('API响应:', response);
    
    if (response.code === 200 && response.data) {
      const newFavorites = response.data.content || [];
      console.log('收藏数据:', newFavorites);
      
      if (reset) {
        favorites.value = newFavorites;
      } else {
        favorites.value.push(...newFavorites);
      }
      
      hasMore.value = response.data.currentPage < response.data.totalPages - 1;
      pageParams.page++;
      
      console.log('收藏数据加载成功:', newFavorites);
      console.log('收藏列表长度:', favorites.value.length);
      console.log('loading状态:', loading.value);
      
      // 暂时注释掉商品详情加载，避免卡住
      await loadProductsInfo(newFavorites);
    } else {
      console.log('API响应错误:', response);
      throw new Error(response.message || '获取收藏列表失败');
    }
    
  } catch (err) {
    console.error('加载收藏失败:', err);
    console.log('错误详情:', err);
    error.value = err.message || '加载收藏失败，请重试';
    if (reset) {
      favorites.value = [];
    }
  } finally {
    console.log('设置loading为false');
    loading.value = false;
    console.log('最终loading状态:', loading.value);
    console.log('最终favorites长度:', favorites.value.length);
  }
};

// 加载商品详细信息
const loadProductsInfo = async (favoritesList) => {
  try {
    console.log('开始加载商品详情，收藏列表:', favoritesList);
    const productCodes = favoritesList.map(f => f.productCode).filter(code => !productsMap.value.has(code));
    console.log('需要加载的商品编号:', productCodes);
    
    if (productCodes.length === 0) {
      console.log('没有需要加载的商品信息');
      return;
    }
    
    // 批量获取商品信息 - 使用精确匹配
    for (const productCode of productCodes) {
      try {
        console.log(`正在加载商品${productCode}的详情...`);
        // 使用商品列表API来查找对应商品，使用精确匹配
        const productResponse = await productsApi.getProductList({ 
          productCode: productCode,
          page: 0,
          size: 100 // 获取更多数据以确保能找到匹配的商品
        });
        console.log(`商品${productCode}的API响应:`, productResponse);
        
        if (productResponse.code === 200 && productResponse.data && productResponse.data.content) {
          // 精确匹配商品编码
          const product = productResponse.data.content.find(p => p.productCode === productCode);
          if (product) {
            productsMap.value.set(productCode, product);
            console.log(`商品${productCode}信息已缓存:`, product);
          } else {
            console.error(`未找到商品编码为${productCode}的商品`);
          }
        } else {
          console.error(`商品${productCode}的API响应异常:`, productResponse);
        }
      } catch (error) {
        console.error(`获取商品${productCode}信息失败:`, error);
      }
    }
    
    console.log('商品信息加载完成，当前缓存:', productsMap.value);
  } catch (error) {
    console.error('加载商品信息失败:', error);
  }
};

// 加载更多
const loadMore = () => {
  if (hasMore.value && !loading.value) {
    loadFavorites(false);
  }
};

// 取消收藏
const removeFavorite = async (favorite) => {
  try {
    await favoritesApi.removeFavorite(favoritesApi.getCurrentUserId(), favorite.productCode);
    
    // 从列表中移除
    const index = favorites.value.findIndex(f => f.id === favorite.id);
    if (index > -1) {
      favorites.value.splice(index, 1);
    }
    
    uni.showToast({
      title: '已取消收藏',
      icon: 'success'
    });
  } catch (error) {
    console.error('取消收藏失败:', error);
    uni.showToast({
      title: error.message || '取消收藏失败',
      icon: 'error'
    });
  }
};

// 添加到购物车
const addToCart = async (favorite) => {
  try {
    // 需要根据商品编号获取商品ID，然后添加到购物车
    const product = productsMap.value.get(favorite.productCode);
    if (product && product.id) {
      await cartApi.addToCart(product.id, 1);
      
      uni.showToast({
        title: '已添加到购物车',
        icon: 'success'
      });
    } else {
      throw new Error('商品信息不存在');
    }
  } catch (error) {
    console.error('添加到购物车失败:', error);
    uni.showToast({
      title: error.message || '添加失败',
      icon: 'error'
    });
  }
};

// 清空收藏确认
const showClearConfirm = () => {
  uni.showModal({
    title: '确认清空',
    content: '确定要清空所有收藏吗？此操作不可恢复。',
    confirmText: '清空',
    cancelText: '取消',
    confirmColor: '#ff6b6b',
    success: async (res) => {
      if (res.confirm) {
        try {
          await favoritesApi.clearUserFavorites(favoritesApi.getCurrentUserId());
          favorites.value = [];
          
          uni.showToast({
            title: '已清空收藏',
            icon: 'success'
          });
        } catch (error) {
          console.error('清空收藏失败:', error);
          uni.showToast({
            title: error.message || '清空失败',
            icon: 'error'
          });
        }
      }
    }
  });
};

// 排序方式切换
const changeSort = (sort) => {
  sortBy.value = sort;
  loadFavorites(true);
};

// 获取商品图片
const getProductImage = (favorite) => {
  const product = productsMap.value.get(favorite.productCode);
  console.log(`获取商品${favorite.productCode}的图片，缓存信息:`, product);
  if (product && product.mainImageUrl) {
    return getImageUrl(product.mainImageUrl);
  }
  // 使用后端的默认图片
  return getImageUrl('static/icon/1.png');
};

// 获取商品名称
const getProductName = (favorite) => {
  const product = productsMap.value.get(favorite.productCode);
  console.log(`获取商品${favorite.productCode}的名称，缓存信息:`, product);
  if (product && product.productName) {
    return product.productName;
  }
  return `商品${favorite.productCode}`;
};

// 获取商品描述
const getProductDesc = (favorite) => {
  const product = productsMap.value.get(favorite.productCode);
  if (product && product.productDescription) {
    return product.productDescription;
  }
  return '商品描述信息';
};

// 获取商品价格
const getProductPrice = (favorite) => {
  const product = productsMap.value.get(favorite.productCode);
  if (product && product.productPrice !== undefined) {
    return product.productPrice.toFixed(2);
  }
  return '0.00';
};

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '';
  
  const date = new Date(timeStr);
  const now = new Date();
  const diff = now - date;
  
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  const hours = Math.floor(diff / (1000 * 60 * 60));
  const minutes = Math.floor(diff / (1000 * 60));
  
  if (days > 0) {
    return `${days}天前`;
  } else if (hours > 0) {
    return `${hours}小时前`;
  } else if (minutes > 0) {
    return `${minutes}分钟前`;
  } else {
    return '刚刚';
  }
};

// 图片URL处理
const getImageUrl = (path) => {
  if (!path) return getImageUrl('static/icon/1.png');
  
  // 如果已经是完整URL，直接返回
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path;
  }
  
  const config = env.getConfig();
  
  // 如果是静态资源（icon、messages等），直接拼接后端地址
  if (path.startsWith('static/')) {
    return `${config.baseUrl}/${path}`;
  }
  
  // 如果是动态上传的图片（uploads目录），拼接完整的静态资源路径
  if (path.startsWith('uploads/') || path.includes('/uploads/')) {
    return `${config.baseUrl}/static/uploads/${path.replace('uploads/', '')}`;
  }
  
  // 如果已经是 /static/uploads/ 开头的路径，直接拼接后端地址
  if (path.startsWith('/static/uploads/')) {
    return `${config.baseUrl}${path}`;
  }
  
  // 其他情况，假设是动态上传的图片文件名
  return `${config.baseUrl}/static/uploads/${path}`;
};

// 处理图片加载错误
const handleImageError = (e) => {
  e.target.src = getImageUrl('static/icon/1.png');
};

// 页面跳转
const goBack = () => {
  uni.navigateBack();
};

const goToProductDetail = (favorite) => {
  // 需要根据商品编号获取商品ID，然后跳转到商品详情
  const product = productsMap.value.get(favorite.productCode);
  if (product && product.id) {
    uni.navigateTo({
      url: `/pages/productDetail/productDetail?id=${product.id}`
    });
  } else {
    uni.showToast({
      title: '商品信息不存在',
      icon: 'error'
    });
  }
};

const goToProductList = () => {
  uni.switchTab({
    url: '/pages/productList/productList'
  });
};

// 页面加载
onMounted(() => {
  console.log('页面加载，开始获取收藏数据');
  loadFavorites(true);
});
</script>

<style>
page {
  background-color: #f5f5f5;
  /* 添加安全区域支持 */
  padding-top: env(safe-area-inset-top);
  padding-bottom: env(safe-area-inset-bottom);
}

.cursor-pointer {
  cursor: pointer;
}

.page-container {
  min-height: 100vh;
  padding-bottom: 20rpx;
}

/* 顶部导航栏 */
.nav-header {
  background-color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx 24rpx;
  padding-top: calc(32rpx + 40rpx);
  border-bottom: 1px solid #f0f0f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
  margin-top: 0;
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

.clear-btn {
  padding: 12rpx 24rpx;
  background-color: #ff6b6b;
  border-radius: 20rpx;
}

.clear-text {
  font-size: 24rpx;
  color: #ffffff;
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

/* 空状态 */
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 40rpx;
}

.empty-icon {
  width: 120rpx;
  height: 120rpx;
  margin-bottom: 40rpx;
  opacity: 0.5;
}

.empty-title {
  font-size: 32rpx;
  color: #666666;
  margin-bottom: 20rpx;
}

.empty-desc {
  font-size: 28rpx;
  color: #999999;
  margin-bottom: 60rpx;
}

.go-shopping-btn {
  background-color: #4CAF50;
  color: white;
  padding: 24rpx 48rpx;
  border-radius: 24rpx;
  font-size: 28rpx;
}

/* 收藏列表 */
.favorites-content {
  padding: 20rpx;
}

.favorites-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  padding: 0 10rpx;
}

.favorites-count {
  font-size: 28rpx;
  color: #666666;
}

.sort-options {
  display: flex;
  gap: 20rpx;
}

.sort-option {
  font-size: 24rpx;
  color: #999999;
  padding: 8rpx 16rpx;
  border-radius: 12rpx;
  transition: all 0.3s ease;
}

.sort-active {
  color: #4CAF50;
  background-color: rgba(76, 175, 80, 0.1);
}

.favorites-list {
  height: calc(100vh - 200rpx);
}

.favorite-item {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  display: flex;
  gap: 24rpx;
}

.product-image {
  width: 160rpx;
  height: 160rpx;
  border-radius: 12rpx;
  flex-shrink: 0;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-name {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
  margin-bottom: 12rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-desc {
  font-size: 24rpx;
  color: #999999;
  margin-bottom: 16rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  font-size: 32rpx;
  color: #FF4433;
  font-weight: bold;
}

.favorite-time {
  font-size: 22rpx;
  color: #999999;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  justify-content: center;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx;
  border-radius: 12rpx;
  background-color: #f8f8f8;
  transition: all 0.3s ease;
}

.action-btn:active {
  background-color: #f0f0f0;
  transform: scale(0.95);
}

.action-icon {
  width: 32rpx;
  height: 32rpx;
}

.action-text {
  font-size: 20rpx;
  color: #666666;
}

/* 加载更多 */
.load-more, .no-more {
  text-align: center;
  padding: 40rpx;
}

.load-more-text, .no-more-text {
  font-size: 24rpx;
  color: #999999;
}
</style> 