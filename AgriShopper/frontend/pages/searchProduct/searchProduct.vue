<template>
  <view class="search-product-page">
    <!-- 搜索栏 -->
    <view class="search-header">
      <view class="search-bar">
        <view class="search-input-wrapper">
          <text class="search-icon">🔍</text>
          <input 
            class="search-input" 
            type="text" 
            placeholder="请输入商品名称" 
            v-model="keyword" 
            @confirm="onSearch"
            focus
          />
        </view>
        <button class="search-btn" @click="onSearch">搜索</button>
      </view>
      <view class="cancel-btn" @click="goBack">取消</view>
    </view>

    <!-- 搜索历史 -->
    <view class="search-history" v-if="!keyword && searchHistory.length > 0">
      <view class="history-header">
        <text class="history-title">搜索历史</text>
        <text class="clear-history" @click="clearHistory">清空</text>
      </view>
      <view class="history-tags">
        <text 
          class="history-tag" 
          v-for="(item, index) in searchHistory" 
          :key="index"
          @click="selectHistory(item)"
        >
          {{ item }}
        </text>
      </view>
    </view>

    <!-- 热门搜索 -->
    <view class="hot-search" v-if="!keyword">
      <view class="hot-title">热门搜索</view>
      <view class="hot-tags">
        <text 
          class="hot-tag" 
          v-for="(item, index) in hotSearchList" 
          :key="index"
          @click="selectHotSearch(item)"
        >
          {{ item }}
        </text>
      </view>
    </view>

    <!-- 猜你喜欢 -->
    <view class="recommend-section" v-if="!keyword">
      <view class="recommend-header">
        <text class="recommend-title">猜你喜欢</text>
        <text class="refresh-btn" @click="refreshRecommend">换一换</text>
      </view>
      <view class="recommend-grid">
        <view 
          class="recommend-item" 
          v-for="(item, index) in recommendProducts" 
          :key="index"
          @click="goToProductDetail(item)"
        >
          <image class="recommend-image" :src="item.image" mode="aspectFill"></image>
          <view class="recommend-info">
            <text class="recommend-name">{{ item.name }}</text>
            <text class="recommend-desc">{{ item.description }}</text>
            <view class="recommend-price">
              <text class="price">¥{{ item.price }}</text>
              <text class="original-price" v-if="item.originalPrice">¥{{ item.originalPrice }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 搜索结果 -->
    <view class="search-result" v-if="keyword && searchResults.length > 0">
      <view class="result-header">
        <text class="result-count">找到 {{ searchResults.length }} 个商品</text>
      </view>
      <view class="product-list">
        <view 
          class="product-item" 
          v-for="(item, index) in searchResults" 
          :key="index"
          @click="goToProductDetail(item)"
        >
          <image class="product-image" :src="item.image" mode="aspectFill"></image>
          <view class="product-info">
            <text class="product-name">{{ item.name }}</text>
            <text class="product-desc">{{ item.description }}</text>
            <view class="product-price">
              <text class="price">¥{{ item.price }}</text>
              <text class="original-price" v-if="item.originalPrice">¥{{ item.originalPrice }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 无搜索结果 -->
    <view class="no-result" v-if="keyword && searchResults.length === 0 && hasSearched">
      <image class="no-result-image" src="/static/no-result.png" mode="aspectFit"></image>
      <text class="no-result-text">没有找到相关商品</text>
      <text class="no-result-tip">试试其他关键词吧</text>
    </view>
  </view>
</template>

<script>
export default {
  name: "searchProduct",
  data() {
    return {
      keyword: "",
      searchHistory: [],
      hotSearchList: ["苹果", "香蕉", "橙子", "葡萄", "草莓", "西瓜", "芒果", "梨子"],
      searchResults: [],
      hasSearched: false,
      recommendProducts: []
    }
  },
  onLoad() {
    // 加载搜索历史
    this.loadSearchHistory();
    // 加载推荐商品
    this.loadRecommendProducts();
  },
  methods: {
    // 执行搜索
    onSearch() {
      if (!this.keyword.trim()) {
        uni.showToast({
          title: '请输入搜索关键词',
          icon: 'none'
        });
        return;
      }
      
      // 添加到搜索历史
      this.addToHistory(this.keyword);
      
      // 模拟搜索
      this.searchProducts();
    },

    // 搜索商品
    searchProducts() {
      this.hasSearched = true;
      // 这里应该调用后端API
      // 暂时用模拟数据
      setTimeout(() => {
        this.searchResults = [
          {
            id: 1,
            name: this.keyword + " - 新鲜水果",
            description: "产地直供，新鲜美味",
            price: "9.9",
            originalPrice: "15.8",
            image: "/static/logo.png"
          },
          {
            id: 2,
            name: this.keyword + " - 有机认证",
            description: "无农药，健康安全",
            price: "12.8",
            originalPrice: "18.5",
            image: "/static/logo.png"
          }
        ];
      }, 500);
    },

    // 选择历史记录
    selectHistory(keyword) {
      this.keyword = keyword;
      this.onSearch();
    },

    // 选择热门搜索
    selectHotSearch(keyword) {
      this.keyword = keyword;
      this.onSearch();
    },

    // 添加到搜索历史
    addToHistory(keyword) {
      if (!this.searchHistory.includes(keyword)) {
        this.searchHistory.unshift(keyword);
        if (this.searchHistory.length > 10) {
          this.searchHistory = this.searchHistory.slice(0, 10);
        }
        this.saveSearchHistory();
      }
    },

    // 清空搜索历史
    clearHistory() {
      uni.showModal({
        title: '提示',
        content: '确定要清空搜索历史吗？',
        success: (res) => {
          if (res.confirm) {
            this.searchHistory = [];
            this.saveSearchHistory();
          }
        }
      });
    },

    // 保存搜索历史
    saveSearchHistory() {
      uni.setStorageSync('searchHistory', this.searchHistory);
    },

    // 加载搜索历史
    loadSearchHistory() {
      const history = uni.getStorageSync('searchHistory');
      if (history) {
        this.searchHistory = history;
      }
    },

    // 返回上一页
    goBack() {
      uni.navigateBack();
    },

    // 跳转到商品详情
    goToProductDetail(product) {
      uni.navigateTo({
        url: `/pages/productDetail/productDetail?id=${product.id}`
      });
    },

    // 加载推荐商品
    loadRecommendProducts() {
      // 模拟推荐商品数据
      this.recommendProducts = [
        {
          id: 101,
          name: "新鲜有机苹果",
          description: "红富士苹果，脆甜多汁",
          price: "15.8",
          originalPrice: "19.9",
          image: "/static/logo.png"
        },
        {
          id: 102,
          name: "优质香蕉",
          description: "海南香蕉，香甜软糯",
          price: "8.5",
          originalPrice: "12.0",
          image: "/static/logo.png"
        },
        {
          id: 103,
          name: "新鲜橙子",
          description: "赣南脐橙，酸甜可口",
          price: "12.9",
          originalPrice: "16.8",
          image: "/static/logo.png"
        },
        {
          id: 104,
          name: "有机葡萄",
          description: "巨峰葡萄，颗粒饱满",
          price: "18.8",
          originalPrice: "25.0",
          image: "/static/logo.png"
        },
        {
          id: 105,
          name: "新鲜草莓",
          description: "丹东草莓，香甜诱人",
          price: "22.8",
          originalPrice: "28.0",
          image: "/static/logo.png"
        },
        {
          id: 106,
          name: "有机西瓜",
          description: "麒麟瓜，清甜爽口",
          price: "16.8",
          originalPrice: "20.0",
          image: "/static/logo.png"
        }
      ];
    },

    // 刷新推荐商品
    refreshRecommend() {
      uni.showLoading({
        title: '刷新中...'
      });
      
      // 模拟刷新推荐商品
      setTimeout(() => {
        // 随机打乱推荐商品顺序
        this.recommendProducts = this.shuffleArray([...this.recommendProducts]);
        uni.hideLoading();
        uni.showToast({
          title: '已刷新',
          icon: 'success',
          duration: 1000
        });
      }, 800);
    },

    // 数组随机打乱
    shuffleArray(array) {
      for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
      }
      return array;
    }
  }
}
</script>

<style scoped>
.search-product-page {
  background-color: #f5f5f5;
  min-height: 100vh;
}

/* 搜索头部 */
.search-header {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background-color: #fff;
  border-bottom: 1rpx solid #eee;
}

.search-bar {
  display: flex;
  align-items: center;
  flex: 1;
  background-color: #f8f8f8;
  border-radius: 25rpx;
  padding: 10rpx 20rpx;
  margin-right: 20rpx;
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  flex: 1;
}

.search-icon {
  font-size: 28rpx;
  margin-right: 10rpx;
  color: #999;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.search-btn {
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 20rpx;
  padding: 10rpx 20rpx;
  font-size: 26rpx;
  margin-left: 10rpx;
}

.cancel-btn {
  color: #666;
  font-size: 28rpx;
  padding: 10rpx;
}

/* 搜索历史 */
.search-history {
  background-color: #fff;
  margin: 20rpx;
  border-radius: 12rpx;
  padding: 30rpx;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.history-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.clear-history {
  font-size: 26rpx;
  color: #999;
}

.history-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.history-tag {
  background-color: #f0f0f0;
  color: #666;
  padding: 10rpx 20rpx;
  border-radius: 20rpx;
  font-size: 26rpx;
}

/* 热门搜索 */
.hot-search {
  background-color: #fff;
  margin: 20rpx;
  border-radius: 12rpx;
  padding: 30rpx;
}

.hot-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.hot-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.hot-tag {
  background-color: #e8f5e8;
  color: #4CAF50;
  padding: 10rpx 20rpx;
  border-radius: 20rpx;
  font-size: 26rpx;
}

/* 猜你喜欢 */
.recommend-section {
  background-color: #fff;
  margin: 20rpx;
  border-radius: 12rpx;
  padding: 30rpx;
}

.recommend-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}

.recommend-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.refresh-btn {
  font-size: 26rpx;
  color: #4CAF50;
  padding: 8rpx 16rpx;
  border: 1rpx solid #4CAF50;
  border-radius: 20rpx;
}

.recommend-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}

.recommend-item {
  background-color: #fff;
  border: 1rpx solid #f0f0f0;
  border-radius: 12rpx;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
}

.recommend-item:active {
  transform: scale(0.98);
  box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.1);
}

.recommend-image {
  width: 100%;
  height: 200rpx;
  background-color: #f8f8f8;
}

.recommend-info {
  padding: 20rpx;
}

.recommend-name {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-bottom: 8rpx;
}

.recommend-desc {
  font-size: 24rpx;
  color: #999;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 12rpx;
}

.recommend-price {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.recommend-price .price {
  font-size: 30rpx;
  font-weight: bold;
  color: #ff6b6b;
}

.recommend-price .original-price {
  font-size: 22rpx;
  color: #999;
  text-decoration: line-through;
}

/* 搜索结果 */
.search-result {
  background-color: #fff;
  margin: 20rpx;
  border-radius: 12rpx;
  padding: 30rpx;
}

.result-header {
  margin-bottom: 20rpx;
}

.result-count {
  font-size: 28rpx;
  color: #666;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.product-item {
  display: flex;
  padding: 20rpx;
  border: 1rpx solid #eee;
  border-radius: 12rpx;
  background-color: #fff;
}

.product-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: 8rpx;
  margin-right: 20rpx;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-name {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}

.product-desc {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 10rpx;
}

.product-price {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.price {
  font-size: 30rpx;
  font-weight: bold;
  color: #ff6b6b;
}

.original-price {
  font-size: 24rpx;
  color: #999;
  text-decoration: line-through;
}

/* 无搜索结果 */
.no-result {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 20rpx;
}

.no-result-image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 30rpx;
}

.no-result-text {
  font-size: 30rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.no-result-tip {
  font-size: 26rpx;
  color: #999;
}
</style> 