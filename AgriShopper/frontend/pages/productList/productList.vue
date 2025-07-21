<template>
  <view class="page-container">

    <!-- 顶部搜索栏 -->
    <view class="search-bar">
      <view class="search-input" @click="goToSearch">
        <image :src="getImageUrl('icon/搜索.png')" class="search-icon" mode="aspectFit"></image>
        <text class="search-placeholder">搜索商品名称</text>
      </view>
    </view>

    <!-- 主体内容区：左侧导航 + 右侧商品 -->
    <view class="content-container">

      <!-- 左侧主分类导航 -->
      <view class="category-sidebar">
        <!-- 分类加载状态 -->
        <view v-if="categoriesLoading" class="category-loading">
          <text class="loading-text">加载分类中...</text>
        </view>
        
        <view v-else class="main-categories">
          <view 
            v-for="(category, index) in categories" 
            :key="category.id || index"
            :class="['category-item', currentCategory === index ? 'category-active' : '']"
            @tap="changeCategory(index)"
            class="cursor-pointer"
          >
            <text>{{ category.name }}</text>
          </view>
        </view>
      </view>

      <!-- 右侧商品展示区 -->
      <scroll-view class="product-list" scroll-y="true" @scrolltolower="loadMore">
        
        <!-- 加载状态 -->
        <view v-if="loading && products.length === 0" class="loading-container">
          <uni-load-more status="loading" :content-text="loadingText"></uni-load-more>
        </view>

        <!-- 错误状态 -->
        <view v-if="error && products.length === 0" class="error-container">
          <text class="error-text">{{ error }}</text>
          <view class="retry-button" @tap="loadProducts">
            <text>重新加载</text>
          </view>
        </view>

        <!-- 商品卡片列表 -->
        <view 
          class="product-item cursor-pointer" 
          v-for="(product, index) in products" 
          :key="product.id"
          @tap="goToProductDetail(product.id)"
        >
          <image 
            class="product-image" 
            :src="product.mainImageUrl || '/static/default-product.png'" 
            mode="aspectFill"
            @error="handleImageError"
          ></image>
          <view class="product-info">
            <text class="product-name">{{ product.productName }}</text>
            <text class="product-desc">{{ product.productDescription }}</text>
            <view class="product-stats">
              <view class="stats-row">
                <text class="stock-info">库存: {{ product.stockQuantity }}</text>
                <view class="product-tags">
                  <text v-if="product.isHotProduct" class="hot-tag">热销</text>
                  <text v-if="product.isNewProduct" class="new-tag">新品</text>
                </view>
              </view>
              <view class="stats-row">
                <text class="unit-info">单位: {{ product.productUnit || '件' }}</text>
              </view>
            </view>
            <view class="product-bottom">
              <text class="product-price">¥{{ product.productPrice }}</text>
              <view class="buy-button cursor-pointer" @tap.stop="addToCart(product)">
                <text>购买</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 商品总数显示 -->
        <view v-if="products.length > 0" class="product-count">
          <text class="count-text">共找到 {{ products.length }} 个商品</text>
        </view>

      </scroll-view>
    </view>
    
  </view>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { store } from '../../store.js';
import productsApi from '../../api/products.js';
import categoriesApi from '../../api/categories.js';
import env from '../../config/env.js';

// 响应式数据
const currentCategory = ref(0);
const products = ref([]);
const loading = ref(false);
const error = ref('');
const hasMore = ref(true);

// 处理图片URL
const getImageUrl = (url) => {
  if (!url) return '/static/default-product.png';
  
  // 如果已经是完整URL，直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  
  // 使用环境配置中的baseUrl
  const config = env.getConfig();
  
  // 如果是icon图片，直接拼接后端地址
  if (url.startsWith('icon/')) {
    return config.baseUrl + '/static/' + url;
  }
  
  // 如果是tabbar图片，直接拼接后端地址
  if (url.startsWith('tabbar/')) {
    return config.baseUrl + '/static/' + url;
  }
  
  // 如果是Carousel轮播图，直接拼接后端地址
  if (url.startsWith('Carousel/')) {
    return config.baseUrl + '/static/' + url;
  }
  
  // 如果已经是 /static/uploads/ 开头的路径，直接拼接后端地址
  if (url.startsWith('/static/uploads/')) {
    return config.baseUrl + url;
  }
  
  // 如果是文件名，拼接完整的静态资源路径
  if (!url.startsWith('/')) {
    return config.baseUrl + '/static/uploads/' + url;
  }
  
  // 其他情况，拼接后端地址和路径
  return config.baseUrl + url;
};

// 分页参数 - 设置更大的页面大小以显示更多商品
const pageParams = reactive({
  page: 0,
  size: 50 // 增加页面大小，一次加载更多商品
});

// 主分类列表 - 从数据库获取
const categories = ref([
  { name: '全部', id: 0 }
]);
const categoriesLoading = ref(false);

// 加载文本配置 - 简化版本
const loadingText = {
  contentdown: '正在加载商品...',
  contentrefresh: '正在加载...',
  contentnomore: '加载完成'
};

// 加载商品列表
const loadProducts = async (reset = false) => {
  if (loading.value) return;
  
  try {
    loading.value = true;
    error.value = '';
    
    if (reset) {
      pageParams.page = 0;
      products.value = [];
      hasMore.value = true;
    }
    
    // 获取所有商品，不分页
    const params = {
      page: 0,
      size: 1000 // 设置一个很大的数字，确保获取所有商品
    };
    
    const response = await productsApi.getProductList(params);
    
    if (response.code === 200 && response.data) {
      let allProducts = response.data.content || [];
      
      // 如果选择了特定分类，按分类ID过滤商品
      if (currentCategory.value > 0) {
        const categoryId = categories.value[currentCategory.value].id;
        allProducts = allProducts.filter(product => product.categoryId === categoryId);
      }
      
      // 直接设置所有商品，不分页
      products.value = allProducts;
      hasMore.value = false; // 一次性加载所有商品，不需要分页
      
      console.log(`加载了 ${allProducts.length} 个商品`);
      
    } else {
      throw new Error(response.message || '获取商品数据失败');
    }
    
  } catch (err) {
    console.error('加载商品失败:', err);
    error.value = err.message || '加载商品失败，请重试';
    if (reset) {
      products.value = [];
    }
  } finally {
    loading.value = false;
  }
};

// 处理商品响应数据 - 已简化，不再需要分页处理

// 切换分类
const changeCategory = (index) => {
  if (currentCategory.value === index) return;
  
  currentCategory.value = index;
  loadProducts(true); // 重置并重新加载
};

// 加载更多 - 现在一次性加载所有商品，不需要分页加载
const loadMore = () => {
  // 一次性加载所有商品，不需要分页
  console.log('所有商品已加载完成');
};

// 跳转到商品详情
const goToProductDetail = (productId) => {
  uni.navigateTo({
    url: `/pages/productDetail/productDetail?id=${productId}`
  });
};

// 购买按钮点击处理
const addToCart = (product) => {
  // 跳转到商品详情页面
  uni.navigateTo({
    url: `/pages/productDetail/productDetail?id=${product.id}`
  });
};

// 跳转到搜索页面
const goToSearch = () => {
  uni.navigateTo({
    url: '/pages/searchProduct/searchProduct'
  });
};

// 处理图片加载错误
const handleImageError = (e) => {
  // 设置默认图片
  e.target.src = '/static/default-product.png';
};

// 获取分类列表
const loadCategories = async () => {
  try {
    categoriesLoading.value = true;
    const response = await categoriesApi.getCategoryList();
    
    if (response.code === 200 && response.data) {
      // 将数据库分类数据转换为前端格式
      const dbCategories = response.data.map(category => ({
        id: category.id,
        name: category.categoryName,
        description: category.categoryDescription,
        icon: category.categoryIcon,
        sortOrder: category.sortOrder
      }));
      
      // 按排序权重排序
      dbCategories.sort((a, b) => a.sortOrder - b.sortOrder);
      
      // 更新分类列表，保留"全部"选项
      categories.value = [
        { name: '全部', id: 0 },
        ...dbCategories
      ];
      
      console.log('分类加载成功:', categories.value);
    } else {
      console.error('获取分类失败:', response.message);
      // 如果API调用失败，使用默认分类
      loadDefaultCategories();
    }
  } catch (error) {
    console.error('获取分类出错:', error);
    // 如果网络错误，使用默认分类
    loadDefaultCategories();
  } finally {
    categoriesLoading.value = false;
  }
};

// 加载默认分类数据（当API调用失败时使用）
const loadDefaultCategories = () => {
  categories.value = [
    { name: '全部', id: 0 },
    { name: '农产品', id: 1 },
    { name: '养生保健', id: 2 },
    { name: '饲料', id: 3 }
  ];
};

// 页面加载时检查登录状态
onMounted(() => {
  loadCategories();
  loadProducts(true);
});
</script>
<style>
page {
  height: 100%;
}

.cursor-pointer {
  cursor: pointer;
}

.page-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #f5f5f5;
}

/* 顶部搜索栏 */
.search-bar {
  height: 100rpx;
  padding: 0 30rpx;
  background-color: #ffffff;
  display: flex;
  align-items: center;
  gap: 20rpx;
  flex-shrink: 0;
  border-bottom: 1px solid #f0f0f0;
}

.user-avatar {
  position: relative;
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  overflow: hidden;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.login-badge {
  position: absolute;
  bottom: -5rpx;
  right: -5rpx;
  background: #07c160;
  color: #fff;
  font-size: 18rpx;
  padding: 2rpx 6rpx;
  border-radius: 8rpx;
  white-space: nowrap;
}

.search-input {
  flex: 1;
  height: 80rpx;
  background-color: #f5f5f5;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  padding: 0 30rpx;
}

.search-icon {
  width: 36rpx;
  height: 36rpx;
  flex-shrink: 0;
}

.search-placeholder {
  margin-left: 20rpx;
  font-size: 14px;
  color: #999999;
}

/* 主体内容区 */
.content-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 左侧分类导航 */
.category-sidebar {
  width: 25%;
  background-color: #ffffff;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #f0f0f0;
}

/* 快捷分类入口 */
.quick-entry {
  display: flex;
  flex-wrap: wrap;
  padding: 20rpx 10rpx;
  border-bottom: 1px solid #f0f0f0;
}

.quick-entry-item {
  width: 25%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10rpx 0;
}

.quick-entry-image {
  width: 80rpx;
  height: 80rpx;
  border-radius: 10rpx;
}

.quick-entry-text {
  font-size: 12px;
  color: #666666;
  margin-top: 10rpx;
}

/* 分类加载状态 */
.category-loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200rpx;
}

.category-loading .loading-text {
  font-size: 28rpx;
  color: #999;
}

/* 主分类列表 */
.main-categories {
  flex: 1;
}

.category-item {
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: #333333;
  border-bottom: 1px solid #f0f0f0;
}

.category-active {
  color: #4CAF50;
  background-color: #f5f5f5;
  border-left: 4px solid #4CAF50;
  font-weight: bold;
}

/* 右侧商品列表 */
.product-list {
  width: 75%;
  overflow: auto;
  padding: 20rpx;
}

/* 加载状态 */
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx;
}

/* 错误状态 */
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx;
}

.error-text {
  color: #ff4444;
  font-size: 14px;
  margin-bottom: 20rpx;
  text-align: center;
}

.retry-button {
  background-color: #4CAF50;
  color: white;
  padding: 20rpx 40rpx;
  border-radius: 8rpx;
  font-size: 14px;
}

/* 商品总数显示 */
.product-count {
  display: flex;
  justify-content: center;
  padding: 20rpx;
  background-color: #f8f8f8;
  border-radius: 8rpx;
  margin: 20rpx 0;
}

.count-text {
  font-size: 28rpx;
  color: #666;
  font-weight: 500;
}

.product-item {
  display: flex;
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.product-image {
  width: 160rpx;
  height: 160rpx;
  border-radius: 8rpx;
  flex-shrink: 0;
}

.product-info {
  flex: 1;
  margin-left: 24rpx;
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: 14px;
  color: #333333;
  font-weight: bold;
  margin-bottom: 10rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 12px;
  color: #666666;
  line-height: 1.4;
  margin-bottom: 10rpx;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.product-stats {
  display: flex;
  flex-direction: column;
  font-size: 12px;
  color: #999999;
  margin-bottom: 10rpx;
}

.stats-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4rpx;
}

.stock-info, .unit-info {
  flex: 1;
}

.product-tags {
  display: flex;
  gap: 8rpx;
}

.hot-tag, .new-tag {
  background-color: #ff4444;
  color: white;
  font-size: 10px;
  padding: 4rpx 8rpx;
  border-radius: 4rpx;
}

.new-tag {
  background-color: #4CAF50;
}

.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10rpx;
}

.product-price {
  font-size: 16px;
  color: #FF4433;
  font-weight: bold;
}

.buy-button {
  background: linear-gradient(90deg, #4CAF50 60%, #43a047 100%);
  color: white;
  font-size: 14px;
  padding: 10rpx 30rpx;
  border-radius: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2rpx 8rpx rgba(76,175,80,0.08);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.buy-button:active {
  background: #388e3c;
  transform: scale(0.95);
  box-shadow: 0 1rpx 4rpx rgba(76,175,80,0.12);
}

.buy-button::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: width 0.3s, height 0.3s;
}

.buy-button:active::before {
  width: 100rpx;
  height: 100rpx;
}


</style>
