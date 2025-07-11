<template>
  <view class="page-container">

    <!-- 顶部搜索栏 -->
    <view class="search-bar">
      <view class="search-input">
        <uni-icons type="search" size="18" color="#999999"></uni-icons>
        <text class="search-placeholder">搜索商品名称</text>
      </view>
    </view>

    <!-- 主体内容区：左侧导航 + 右侧商品 -->
    <view class="content-container">

      <!-- 左侧主分类导航 -->
      <view class="category-sidebar">
        <view class="main-categories">
          <view 
            v-for="(category, index) in categories" 
            :key="index"
            :class="['category-item', currentCategory === index ? 'category-active' : '']"
            @tap="changeCategory(index)"
            class="cursor-pointer"
          >
            <text>{{ category.name }}</text>
          </view>
        </view>
      </view>

      <!-- 右侧商品展示区 -->
      <scroll-view class="product-list" scroll-y="true">


        <!-- 商品卡片列表 -->
        <view 
          class="product-item cursor-pointer" 
          v-for="(product, index) in products" 
          :key="index"
        >
          <image class="product-image" :src="product.image" mode="aspectFill"></image>
          <view class="product-info">
            <text class="product-name">{{ product.name }}</text>
            <text class="product-desc">{{ product.description }}</text>
            <view class="product-stats">
              <text>销量: {{ product.sales }}</text>
              <text>库存: {{ product.stock }}</text>
            </view>
            <view class="product-bottom">
              <text class="product-price">¥{{ product.price }}</text>
              <view class="buy-button cursor-pointer">
                <text>购买</text>
              </view>
            </view>
          </view>
        </view>

      </scroll-view>
    </view>

    <!-- 底部导航栏 -->
    <BottomTabBar />
  </view>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import BottomTabBar from '@/components/BottomTabBar.vue';

// 当前选中的分类
const currentCategory = ref(0);


// 主分类列表
const categories = ref([
  { name: '农产品', id: 1 },
  { name: '饲料', id: 2 },
  { name: '养生保健', id: 3 }
]);

// 商品列表
const products = ref([
  {
    id: 1,
    name: '有机西红柿',
    description: '新鲜采摘，无农药，口感酸甜多汁',
    price: '12.8',
    sales: 1280,
    stock: 500,
    image: 'https://readdy.ai/api/search-image?query=Realistic%20fresh%20organic%20tomatoes%2C%20vibrant%20red%20color%2C%20farm%20fresh%20produce%2C%20high-detail%20photography%2C%20clean%20background%2C%20natural%20lighting%2C%20showing%20texture%20and%20freshness%2C%20product%20photography%20style%2C%20no%20human%20elements%2C%20professional%20food%20photography.&width=160&height=160&seq=5&orientation=squarish'
  },
  {
    id: 2,
    name: '绿色菠菜',
    description: '富含铁质，叶片肥厚，口感清爽',
    price: '8.5',
    sales: 950,
    stock: 300,
    image: 'https://readdy.ai/api/search-image?query=Fresh%20green%20spinach%20leaves%2C%20organic%20farm%20produce%2C%20vibrant%20green%20color%2C%20crisp%20and%20healthy%20appearance%2C%20high-detail%20photography%2C%20clean%20background%2C%20natural%20lighting%2C%20showing%20texture%20and%20freshness%2C%20product%20photography%20style%2C%20no%20human%20elements.&width=160&height=160&seq=6&orientation=squarish'
  },
  {
    id: 3,
    name: '土鸡蛋',
    description: '散养土鸡所产，蛋黄饱满，营养丰富',
    price: '28.8',
    sales: 2100,
    stock: 200,
    image: 'https://readdy.ai/api/search-image?query=Organic%20farm%20fresh%20eggs%2C%20brown%20shells%2C%20arranged%20neatly%2C%20high-detail%20photography%2C%20clean%20background%2C%20natural%20lighting%2C%20showing%20texture%20and%20freshness%2C%20product%20photography%20style%2C%20no%20human%20elements%2C%20professional%20food%20photography.&width=160&height=160&seq=7&orientation=squarish'
  },
  {
    id: 4,
    name: '有机红薯',
    description: '农家种植，口感香甜，富含膳食纤维',
    price: '15.9',
    sales: 1560,
    stock: 400,
    image: 'https://readdy.ai/api/search-image?query=Organic%20sweet%20potatoes%2C%20freshly%20harvested%2C%20earthy%20brown%20skin%20with%20vibrant%20orange%20flesh%2C%20farm%20fresh%20produce%2C%20high-detail%20photography%2C%20clean%20background%2C%20natural%20lighting%2C%20showing%20texture%20and%20freshness%2C%20product%20photography%20style%2C%20no%20human%20elements.&width=160&height=160&seq=8&orientation=squarish'
  },
  {
    id: 5,
    name: '新鲜玉米',
    description: '当季采摘，颗粒饱满，甜度适中',
    price: '9.9',
    sales: 1890,
    stock: 350,
    image: 'https://readdy.ai/api/search-image?query=Fresh%20corn%20on%20the%20cob%2C%20bright%20yellow%20kernels%2C%20partially%20peeled%20husk%2C%20farm%20fresh%20produce%2C%20high-detail%20photography%2C%20clean%20background%2C%20natural%20lighting%2C%20showing%20texture%20and%20freshness%2C%20product%20photography%20style%2C%20no%20human%20elements.&width=160&height=160&seq=9&orientation=squarish'
  },
  {
    id: 6,
    name: '有机黄瓜',
    description: '绿色种植，脆嫩多汁，清香可口',
    price: '7.5',
    sales: 2300,
    stock: 600,
    image: 'https://readdy.ai/api/search-image?query=Organic%20cucumbers%2C%20bright%20green%20color%2C%20smooth%20skin%2C%20farm%20fresh%20produce%2C%20high-detail%20photography%2C%20clean%20background%2C%20natural%20lighting%2C%20showing%20texture%20and%20freshness%2C%20product%20photography%20style%2C%20no%20human%20elements%2C%20professional%20food%20photography.&width=160&height=160&seq=10&orientation=squarish'
  }
]);

// 切换分类
const changeCategory = (index: number) => {
  currentCategory.value = index;
};
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
  flex-shrink: 0;
  border-bottom: 1px solid #f0f0f0;
}

.search-input {
  height: 80rpx;
  background-color: #f5f5f5;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  padding: 0 30rpx;
  width: 100%;
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
  font-size: 12px;
  color: #999999;
  margin-bottom: 10rpx;
}

.product-stats text {
  margin-right: 20rpx;
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
  background-color: #4CAF50;
  color: white;
  font-size: 14px;
  padding: 10rpx 30rpx;
  border-radius: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}


</style>
