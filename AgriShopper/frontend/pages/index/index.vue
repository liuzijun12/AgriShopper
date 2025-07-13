<template>
  <view class="page-container">
    <view class="content-container">
      <!-- 搜索栏 -->
      <view class="search-container">
        <view class="search-box" @click="goToSearch">
          <uni-icons type="search" size="24" color="#999"></uni-icons>
          <input type="text" placeholder="搜索您想要的产品名称" class="search-input" disabled />
          <view class="search-btn cursor-pointer">搜索</view>
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
          <uni-icons type="left" size="24" color="#333"></uni-icons>
        </view>
        <view class="swiper-arrow right cursor-pointer" @click="nextSwiper">
          <uni-icons type="right" size="24" color="#333"></uni-icons>
        </view>
      </view>

      <!-- 推荐商品区域 -->
      <view class="recommend-title">
        <text>推荐商品</text>
      </view>
      
      <view class="product-grid">
        <view 
          v-for="(product, index) in products" 
          :key="index" 
          class="product-card cursor-pointer"
        >
          <image :src="product.imageUrl" mode="aspectFill" class="product-image" />
          <view class="product-info">
            <text class="product-name">{{ product.name }}</text>
            <text class="product-desc">{{ product.description }}</text>
            <view class="product-bottom">
              <view class="price-container">
                <text class="product-price">¥{{ product.price }}</text>
                <text class="product-original" v-if="product.originalPrice">¥{{ product.originalPrice }}</text>
              </view>
              <view class="buy-btn cursor-pointer">购买</view>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>
<script setup>
import { ref } from 'vue';

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
const products = ref([
  {
    name: '有机红薯',
    description: '农家自种，无公害种植',
    price: '12.8',
    originalPrice: '15.8',
    imageUrl: 'https://readdy.ai/api/search-image?query=icon%2C%20Realistic%20food%2C%20photorealistic%20sweet%20potato%2C%20high-detail%203D%20rendering%2C%20prominent%20main%20subjects%2C%20clear%20and%20sharp%2C%20subject%20fills%2080%20percent%20of%20frame%2C%20isolated%20on%20white%20background%2C%20centered%20composition%2C%20soft%20lighting%2C%20subtle%20shadows%2C%20product%20photography%20style&width=300&height=300&seq=4&orientation=squarish'
  },
  {
    name: '优质玉米饲料',
    description: '高营养，适合家禽喂养',
    price: '45.9',
    originalPrice: '59.9',
    imageUrl: 'https://readdy.ai/api/search-image?query=icon%2C%20Realistic%20food%2C%20photorealistic%20corn%20feed%20grains%2C%20high-detail%203D%20rendering%2C%20prominent%20main%20subjects%2C%20clear%20and%20sharp%2C%20subject%20fills%2080%20percent%20of%20frame%2C%20isolated%20on%20white%20background%2C%20centered%20composition%2C%20soft%20lighting%2C%20subtle%20shadows%2C%20product%20photography%20style&width=300&height=300&seq=5&orientation=squarish'
  },
  {
    name: '枸杞菊花茶',
    description: '养生保健，明目润肺',
    price: '38.5',
    originalPrice: '45.0',
    imageUrl: 'https://readdy.ai/api/search-image?query=icon%2C%20Realistic%20food%2C%20photorealistic%20goji%20berries%20and%20chrysanthemum%20tea%2C%20high-detail%203D%20rendering%2C%20prominent%20main%20subjects%2C%20clear%20and%20sharp%2C%20subject%20fills%2080%20percent%20of%20frame%2C%20isolated%20on%20white%20background%2C%20centered%20composition%2C%20soft%20lighting%2C%20subtle%20shadows%2C%20product%20photography%20style&width=300&height=300&seq=6&orientation=squarish'
  },
  {
    name: '新鲜胡萝卜',
    description: '富含胡萝卜素，助力健康',
    price: '8.8',
    originalPrice: '10.8',
    imageUrl: 'https://readdy.ai/api/search-image?query=icon%2C%20Realistic%20food%2C%20photorealistic%20fresh%20carrots%2C%20high-detail%203D%20rendering%2C%20prominent%20main%20subjects%2C%20clear%20and%20sharp%2C%20subject%20fills%2080%20percent%20of%20frame%2C%20isolated%20on%20white%20background%2C%20centered%20composition%2C%20soft%20lighting%2C%20subtle%20shadows%2C%20product%20photography%20style&width=300&height=300&seq=7&orientation=squarish'
  }
]);

// 当前状态
const currentSwiper = ref(0);
const currentCategory = ref(1); // 默认选中农产品

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
.search-box {
  display: flex;
  align-items: center;
  height: 80rpx;
  background-color: #f3f5f7;
  border-radius: 40rpx;
  padding: 0 20rpx;
  box-shadow: none;
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
  width: 60rpx;
  height: 60rpx;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10;
  box-shadow: 0 2rpx 8rpx rgba(76,175,80,0.08);
}
.swiper-arrow.left {
  left: 20rpx;
}
.swiper-arrow.right {
  right: 20rpx;
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
  flex-direction: column;
}
.product-price {
  font-size: 36rpx;
  font-weight: bold;
  color: #4CAF50;
}
.product-original {
  font-size: 24rpx;
  color: #bdbdbd;
  text-decoration: line-through;
  margin-top: 4rpx;
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
.cursor-pointer {
  cursor: pointer;
}
</style>
