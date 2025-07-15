<template>
  <view class="page-container">
    <view class="content-container">
      <!-- 搜索栏和用户入口 -->
      <view class="search-container">
        <view class="search-header">
                  <view class="search-box" @click="goToSearch">
          <image :src="getImageUrl('icon/4.png')" class="search-icon" mode="aspectFit"></image>
          <input type="text" placeholder="搜索您想要的产品名称" class="search-input" disabled />
          <view class="search-btn cursor-pointer">搜索</view>
        </view>
                      <view class="user-avatar" @click="handleUserClick">
              <image :src="userAvatar" mode="aspectFill" class="avatar-img" />
              <view v-if="!isLoggedIn" class="login-badge">登录</view>
            </view>
        </view>
      </view>
      
      <!-- 顶部轮播图 -->
      <view class="swiper-container">
        <swiper class="swiper" circular autoplay interval="3000" duration="500" @change="onSwiperChange">
          <swiper-item v-for="(item, index) in bannerList" :key="index">
            <image :src="item.imageUrl" mode="aspectFill" class="swiper-image" />
          </swiper-item>
        </swiper>
        <view class="swiper-dots">
          <view 
            v-for="(item, index) in bannerList" 
            :key="index" 
            :class="['dot', currentSwiper === index ? 'active' : '']"
          ></view>
        </view>
        <view class="swiper-arrow left cursor-pointer" @click="prevSwiper">
          <view class="arrow-circle">
            <image :src="getImageUrl('icon/1.png')" class="arrow-icon left-arrow" mode="aspectFit"></image>
          </view>
        </view>
        <view class="swiper-arrow right cursor-pointer" @click="nextSwiper">
          <view class="arrow-circle">
            <image :src="getImageUrl('icon/3.png')" class="arrow-icon right-arrow" mode="aspectFit"></image>
          </view>
        </view>
      </view>

      <!-- 推荐商品区域 -->
      <view class="recommend-title">
        <text>推荐商品</text>
        <text v-if="productsLoading" class="loading-text">加载中...</text>
      </view>
      
      <!-- 加载状态 -->
      <view v-if="productsLoading" class="loading-container">
        <view class="loading-spinner"></view>
        <text class="loading-text">正在加载商品...</text>
      </view>
      
      <!-- 商品网格 -->
      <view v-else class="product-grid">
        <view 
          v-for="(product, index) in products" 
          :key="product.id || index" 
          class="product-card cursor-pointer"
          @click="goToProductDetail(product)"
        >
          <image 
            :src="product.imageUrl" 
            mode="aspectFill" 
            class="product-image"
            @error="handleImageError"
          />
          <view class="product-info">
            <view class="product-header">
              <text class="product-name">{{ product.name }}</text>
              <view class="product-tags">
                <text v-if="product.isHotProduct" class="hot-tag">热销</text>
                <text v-if="product.isNewProduct" class="new-tag">新品</text>
              </view>
            </view>
            <text class="product-desc">{{ product.description }}</text>
            <view class="product-bottom">
              <view class="price-container">
                <text class="product-price">¥{{ product.price }}</text>
              </view>
              <view class="buy-btn cursor-pointer" @click.stop="addToCart(product)">购买</view>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 空状态 -->
      <view v-if="!productsLoading && products.length === 0" class="empty-container">
        <text class="empty-text">暂无推荐商品</text>
      </view>
    </view>
    
    <!-- 微信登录弹窗 -->
    <WxLoginModal 
      :visible="showLoginModal" 
      @close="handleCloseLogin"
      @login-success="handleLoginSuccess"
    />
  </view>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import { store } from '../../store.js';
import WxLoginModal from '../../components/WxLoginModal.vue';
import env from '../../config/env.js';

// 轮播图数据
const bannerList = ref([
  {
    imageUrl: 'https://readdy.ai/api/search-image?query=Beautiful%20organic%20farm%20with%20green%20vegetables%20and%20fruits%2C%20fresh%20produce%20harvest%20scene%2C%20morning%20sunlight%2C%20vibrant%20colors%2C%20natural%20farming%20landscape%2C%20healthy%20food%20ingredients%20displayed%2C%20sustainable%20agriculture%2C%20farm%20to%20table%20concept%2C%20high%20quality%20professional%20photography&width=750&height=400&seq=1&orientation=landscape'
  },
  {
    imageUrl: 'https://readdy.ai/api/search-image?query=Farmers%20market%20with%20colorful%20organic%20vegetables%20and%20fruits%2C%20fresh%20local%20produce%2C%20wooden%20crates%2C%20natural%20lighting%2C%20vibrant%20colors%2C%20rustic%20farm%20stand%2C%20healthy%20food%20display%2C%20sustainable%20agriculture%2C%20countryside%20scenery%2C%20professional%20food%20photography&width=750&height=400&seq=2&orientation=landscape'
  },
  {
    imageUrl: 'https://readdy.ai/api/search-image?query=Organic%20health%20supplements%20and%20natural%20remedies%2C%20herbal%20medicine%20bottles%20and%20capsules%2C%20green%20leaves%20background%2C%20wellness%20products%2C%20clean%20modern%20display%2C%20healthy%20lifestyle%20concept%2C%20soft%20natural%20lighting%2C%20professional%20product%20photography&width=750&height=400&seq=3&orientation=landscape'
  }
]);

// 分类数据
const categories = ref([
  { name: '饲料' },
  { name: '农产品' },
  { name: '养生保健品' }
]);

// 推荐商品数据
const products = ref([]);
const productsLoading = ref(false);

// 当前状态
const currentSwiper = ref(0);
const currentCategory = ref(1); // 默认选中农产品

// 登录状态
const isLoggedIn = ref(false);
const showLoginModal = ref(false);
const userAvatar = ref('/static/tabbar/user.png');

// 轮播图切换
const onSwiperChange = (e) => {
  currentSwiper.value = e.detail.current;
};

// 上一张轮播图
const prevSwiper = () => {
  if (currentSwiper.value === 0) {
    currentSwiper.value = bannerList.value.length - 1;
  } else {
    currentSwiper.value--;
  }
};

// 下一张轮播图
const nextSwiper = () => {
  if (currentSwiper.value === bannerList.value.length - 1) {
    currentSwiper.value = 0;
  } else {
    currentSwiper.value++;
  }
};

// 切换分类
const changeCategory = (index) => {
  currentCategory.value = index;
};

// 跳转到搜索页面
const goToSearch = () => {
  uni.navigateTo({
    url: '/pages/searchProduct/searchProduct'
  });
};

// 检查登录状态
const checkLoginStatus = () => {
  try {
    const userInfo = store.getUserInfo();
    if (userInfo && userInfo.openid) {
      isLoggedIn.value = true;
      if (userInfo.avatar) {
        userAvatar.value = userInfo.avatar;
      }
    } else {
      isLoggedIn.value = false;
    }
  } catch (error) {
    console.log('检查登录状态失败:', error);
    isLoggedIn.value = false;
  }
};

// 处理用户点击
const handleUserClick = () => {
  if (isLoggedIn.value) {
    // 已登录，跳转到个人中心
    uni.switchTab({
      url: '/pages/my/my'
    });
  } else {
    // 未登录，显示登录弹窗
    showLoginModal.value = true;
  }
};

// 关闭登录弹窗
const handleCloseLogin = () => {
  showLoginModal.value = false;
};

// 登录成功处理
const handleLoginSuccess = (userInfo) => {
  console.log('登录成功:', userInfo);
  checkLoginStatus();
  showLoginModal.value = false;
};

// 获取推荐商品
const fetchRecommendProducts = async () => {
  try {
    productsLoading.value = true;
    
    // 调用后端API获取商品列表，限制4个推荐商品
    const response = await new Promise((resolve, reject) => {
      uni.request({
        url: env.getApiUrl('/products'),
        method: 'GET',
        data: {
          page: 0,
          size: 4
        },
        success: (res) => {
          resolve(res);
        },
        fail: (err) => {
          reject(err);
        }
      });
    });
    
    if (response.statusCode === 200 && response.data.code === 200) {
      // 转换数据格式以适配前端显示
      const productList = response.data.data.content || [];
      products.value = productList.map(product => ({
        id: product.id,
        name: product.productName,
        description: product.productDescription || '暂无描述',
        price: product.productPrice,
        imageUrl: getImageUrl(product.mainImageUrl),
        productCode: product.productCode,
        stockQuantity: product.stockQuantity,
        isHotProduct: product.isHotProduct,
        isNewProduct: product.isNewProduct
      }));
      
      console.log('推荐商品加载成功:', products.value);
    } else {
      console.error('获取推荐商品失败:', response.data);
      // 如果API调用失败，使用默认数据
      loadDefaultProducts();
    }
  } catch (error) {
    console.error('获取推荐商品出错:', error);
    // 如果网络错误，使用默认数据
    loadDefaultProducts();
  } finally {
    productsLoading.value = false;
  }
};

// 处理图片URL
const getImageUrl = (url) => {
  if (!url) return '/static/default-product.png';
  
  // 如果已经是完整URL，直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  
  // 如果是icon图片，直接拼接后端地址
  if (url.startsWith('icon/')) {
    return 'http://localhost:8080/' + url;
  }
  
  // 如果是tabbar图片，直接拼接后端地址
  if (url.startsWith('tabbar/')) {
    return 'http://localhost:8080/' + url;
  }
  
  // 如果已经是 /static/uploads/ 开头的路径，直接拼接后端地址
  if (url.startsWith('/static/uploads/')) {
    return 'http://localhost:8080' + url;
  }
  
  // 如果是文件名，拼接完整的静态资源路径
  if (!url.startsWith('/')) {
    return 'http://localhost:8080/static/uploads/' + url;
  }
  
  // 其他情况，拼接后端地址和路径
  return 'http://localhost:8080' + url;
};

// 加载默认商品数据（当API调用失败时使用）
const loadDefaultProducts = () => {
  products.value = [
    {
      id: 1,
      name: '有机红薯',
      description: '农家自种，无公害种植',
      price: 12.8,
      imageUrl: '/static/default-product.png'
    },
    {
      id: 2,
      name: '优质玉米饲料',
      description: '高营养，适合家禽喂养',
      price: 45.9,
      imageUrl: '/static/default-product.png'
    },
    {
      id: 3,
      name: '枸杞菊花茶',
      description: '养生保健，明目润肺',
      price: 38.5,
      imageUrl: '/static/default-product.png'
    },
    {
      id: 4,
      name: '新鲜胡萝卜',
      description: '富含胡萝卜素，助力健康',
      price: 8.8,
      imageUrl: '/static/default-product.png'
    }
  ];
};

// 跳转到商品详情
const goToProductDetail = (product) => {
  uni.navigateTo({
    url: `/pages/productDetail/productDetail?id=${product.id}`
  });
};

// 添加到购物车
const addToCart = (product) => {
  // TODO: 实现添加到购物车的逻辑
  uni.showToast({
    title: '已添加到购物车',
    icon: 'success'
  });
};

// 处理图片加载错误
const handleImageError = (e) => {
  // 设置默认图片
  e.target.src = '/static/default-product.png';
};

// 页面加载时检查登录状态
onMounted(() => {
  checkLoginStatus();
  fetchRecommendProducts();
});
</script>
<style>
.page-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f6f8fa;
  font-family: "PingFang SC", "Helvetica Neue", Helvetica, Arial, sans-serif;
  padding-bottom: 100rpx;
}

.content-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0 0 20rpx 0;
}

/* 搜索栏样式优化 */
.search-container {
  padding: 40rpx 30rpx 20rpx 30rpx;
  background: #fff;
  border-bottom-left-radius: 32rpx;
  border-bottom-right-radius: 32rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.03);
}

.search-header {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.user-avatar {
  position: relative;
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  overflow: hidden;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
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
  font-size: 20rpx;
  padding: 4rpx 8rpx;
  border-radius: 10rpx;
  white-space: nowrap;
}
.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  height: 80rpx;
  background-color: #f3f5f7;
  border-radius: 40rpx;
  padding: 0 20rpx;
  box-shadow: none;
}

.search-icon {
  width: 40rpx;
  height: 40rpx;
  flex-shrink: 0;
}
.search-input {
  flex: 1;
  height: 80rpx;
  font-size: 32rpx;
  margin-left: 20rpx;
  background: transparent;
  border: none;
  outline: none;
}
.search-btn {
  padding: 0 36rpx;
  height: 60rpx;
  line-height: 60rpx;
  background: linear-gradient(90deg, #4CAF50 60%, #43a047 100%);
  color: #fff;
  border-radius: 30rpx;
  font-size: 28rpx;
  font-weight: 600;
  margin-left: 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(76,175,80,0.08);
}

/* 轮播图样式优化 */
.swiper-container {
  position: relative;
  width: 100%;
  height: 420rpx;
  border-radius: 32rpx;
  overflow: hidden;
  margin: 32rpx 0 28rpx 0;
  box-shadow: 0 4rpx 24rpx rgba(76,175,80,0.06);
  background: #fff;
}
.swiper {
  width: 100%;
  height: 100%;
}
.swiper-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.swiper-dots {
  position: absolute;
  bottom: 24rpx;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  z-index: 10;
}
.dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background-color: rgba(76,175,80,0.18);
  margin: 0 8rpx;
  transition: all 0.2s;
}
.dot.active {
  width: 32rpx;
  background: linear-gradient(90deg, #4CAF50 60%, #43a047 100%);
}
.swiper-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 10;
}

.arrow-circle {
  width: 60rpx;
  height: 60rpx;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.1);
  transition: all 0.2s ease;
}

.arrow-circle:hover {
  background-color: rgba(255, 255, 255, 1);
  box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.15);
}
.swiper-arrow.left {
  left: 20rpx;
}
.swiper-arrow.right {
  right: 20rpx;
}

.arrow-icon {
  width: 32rpx;
  height: 32rpx;
}

.left-arrow {
  transform: rotate(-270deg);
}

.right-arrow {
  transform: rotate(90deg);
}

/* 推荐商品标题优化 */
.recommend-title {
  padding: 30rpx 30rpx 10rpx 30rpx;
  font-size: 38rpx;
  font-weight: bold;
  color: #222;
  background: transparent;
  letter-spacing: 2rpx;
}

/* 商品网格样式优化 */
.product-grid {
  display: flex;
  flex-wrap: wrap;
  padding: 0 10rpx 120rpx 10rpx;
  background: transparent;
  justify-content: space-between;
}
.product-card {
  width: calc(50% - 16rpx);
  margin: 10rpx 0;
  background: #fff;
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(76,175,80,0.06);
  display: flex;
  flex-direction: column;
  transition: box-shadow 0.2s;
}
.product-card:hover {
  box-shadow: 0 8rpx 32rpx rgba(76,175,80,0.12);
}
.product-image {
  width: 100%;
  height: 300rpx;
  background: #f8f8f8;
  object-fit: cover;
}
.product-info {
  padding: 24rpx 18rpx 18rpx 18rpx;
  display: flex;
  flex-direction: column;
  flex: 1;
}
.product-name {
  font-size: 32rpx;
  font-weight: 600;
  color: #222;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.product-desc {
  font-size: 26rpx;
  color: #8e8e8e;
  margin-top: 8rpx;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 18rpx;
}
.price-container {
  display: flex;
  align-items: center;
}
.product-price {
  font-size: 36rpx;
  font-weight: bold;
  color: #4CAF50;
}
.buy-btn {
  padding: 10rpx 28rpx;
  background: linear-gradient(90deg, #4CAF50 60%, #43a047 100%);
  color: #fff;
  border-radius: 30rpx;
  font-size: 26rpx;
  font-weight: 500;
  box-shadow: 0 2rpx 8rpx rgba(76,175,80,0.08);
  transition: background 0.2s;
}
.buy-btn:active {
  background: #388e3c;
}

/* 加载状态样式 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60rpx 0;
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
  color: #999;
}

/* 空状态样式 */
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 0;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

/* 商品标签样式 */
.product-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8rpx;
}

.product-tags {
  display: flex;
  gap: 8rpx;
}

.hot-tag, .new-tag {
  font-size: 20rpx;
  padding: 4rpx 8rpx;
  border-radius: 8rpx;
  color: #fff;
}

.hot-tag {
  background-color: #ff4444;
}

.new-tag {
  background-color: #4CAF50;
}

.cursor-pointer {
  cursor: pointer;
}
</style>
