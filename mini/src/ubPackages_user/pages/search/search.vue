<template>
  <view class="page">
    <!-- 顶部安全区域 -->
    <view class="safe-area-top"></view>
    
    <!-- 搜索头部 -->
    <view class="search-header">
      <view class="search-input-wrapper">
        <image class="search-icon" src="/static/images/search.png"></image>
        <input 
          class="search-input" 
          v-model="searchKeyword"
          placeholder="搜索新鲜农产品"
          @confirm="handleSearch"
          @input="handleInput"
          focus
        />
        <view v-if="searchKeyword" class="clear-btn" @click="clearSearch">
          <text class="clear-text">×</text>
        </view>
      </view>
      <view class="cancel-btn" @click="goBack">
        <text class="cancel-text">取消</text>
      </view>
    </view>
    
    <view class="page-content">
      <!-- 搜索建议/自动填充 -->
      <view v-if="searchKeyword && !showSearchResults && suggestions.length > 0" class="suggestions-section">
      <view class="suggestion-list">
        <view 
          class="suggestion-item" 
          v-for="(suggestion, index) in suggestions" 
          :key="index"
          @click="handleSuggestionClick(suggestion)"
        >
          <image class="suggestion-icon" src="/static/images/search.png"></image>
          <text class="suggestion-text">{{ suggestion }}</text>
        </view>
      </view>
    </view>
    
    <!-- 热门搜索 -->
    <view v-if="!searchKeyword && !showSearchResults" class="hot-search-section">
      <view class="section-title">
        <text class="title-text">热门搜索</text>
      </view>
      <view class="hot-tags">
        <view 
          class="hot-tag" 
          v-for="(tag, index) in hotSearchList" 
          :key="index"
          @click="handleHotTagClick(tag)"
        >
          <text class="tag-text">{{ tag }}</text>
        </view>
      </view>
    </view>
    
    <!-- 搜索历史 -->
    <view v-if="!searchKeyword && !showSearchResults && searchHistory.length > 0" class="history-section">
      <view class="section-header">
        <text class="title-text">搜索历史</text>
        <view class="clear-history" @click="clearHistory">
          <text class="clear-text">清空</text>
        </view>
      </view>
      <view class="history-list">
        <view 
          class="history-item" 
          v-for="(item, index) in searchHistory" 
          :key="index"
          @click="handleHistoryClick(item)"
        >
          <image class="history-icon" src="/static/images/search.png"></image>
          <text class="history-text">{{ item }}</text>
        </view>
      </view>
    </view>
    
    <!-- 搜索结果 -->
    <view v-if="showSearchResults" class="search-results">
      <view class="result-header">
        <text class="result-count">找到 {{ searchResults.length }} 个相关商品</text>
      </view>
      
      <view class="product-list">
        <view 
          class="product-item" 
          v-for="(product, index) in searchResults" 
          :key="product.id"
          @click="handleProductClick(product)"
        >
          <!-- 商品图片 -->
          <view class="product-image-wrapper">
            <image class="product-image" :src="product.image" mode="aspectFill"></image>
            <view v-if="product.badge" class="product-badge">
              <text class="badge-text">{{ product.badge }}</text>
            </view>
          </view>
          
          <!-- 商品信息 -->
          <view class="product-info">
            <text class="product-name">{{ product.name }}</text>
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
      <view v-if="searchResults.length === 0" class="empty-state">
        <text class="empty-text">未找到相关商品</text>
      </view>
    </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      searchKeyword: '',
      hotSearchList: [
        '新鲜蔬菜', '有机水果', '优质大米', '新鲜鸡蛋', 
        '深海鱼类', '有机蘑菇', '豆制品', '调料'
      ],
      searchHistory: [],
      searchResults: [],
      suggestions: [], // 搜索建议列表
      showSearchResults: false, // 是否显示搜索结果
      suggestionTimer: null // 防抖定时器
    }
  },
  onLoad(options) {
    // 接收从首页传来的搜索词，只填充到输入框，不自动搜索
    if (options.keyword) {
      this.searchKeyword = options.keyword
    }
    
    // 加载搜索历史
    this.loadSearchHistory()
  },
  methods: {
    // 处理输入
    handleInput(e) {
      this.searchKeyword = e.detail.value
      this.showSearchResults = false
      
      // 防抖处理，避免频繁请求
      if (this.suggestionTimer) {
        clearTimeout(this.suggestionTimer)
      }
      
      if (this.searchKeyword.trim()) {
        this.suggestionTimer = setTimeout(() => {
          this.getSuggestions(this.searchKeyword)
        }, 300) // 300ms防抖
      } else {
        this.suggestions = []
      }
    },
    
    // 处理搜索
    handleSearch() {
      if (!this.searchKeyword.trim()) return
      
      console.log('搜索:', this.searchKeyword)
      
      // 隐藏建议列表，显示搜索结果
      this.suggestions = []
      this.showSearchResults = true
      
      // 添加到搜索历史
      this.addToHistory(this.searchKeyword)
      
      // 执行搜索
      this.performSearch(this.searchKeyword)
    },
    
    // 执行搜索
    async performSearch(keyword) {
      try {
        // 这里应该调用实际的搜索API
        // const res = await api.searchProducts(keyword)
        // this.searchResults = res.data
        
        // 模拟搜索结果
        this.searchResults = this.getMockSearchResults(keyword)
        
      } catch (error) {
        console.error('搜索失败:', error)
        this.searchResults = []
      }
    },
    
    // 获取模拟搜索结果
    getMockSearchResults(keyword) {
      const mockResults = [
        {
          id: 1,
          name: `${keyword}相关商品1`,
          price: '12.8',
          image: '/static/logo.png',
          badge: '热销'
        },
        {
          id: 2,
          name: `${keyword}相关商品2`,
          price: '15.6',
          image: '/static/logo.png',
          badge: '新品'
        },
        {
          id: 3,
          name: `${keyword}相关商品3`,
          price: '28.0',
          image: '/static/logo.png',
          badge: null
        }
      ]
      
      return mockResults
    },
    
    // 点击热门标签
    handleHotTagClick(tag) {
      this.searchKeyword = tag
      this.handleSearch()
    },
    
    // 点击搜索历史
    handleHistoryClick(item) {
      this.searchKeyword = item
      this.handleSearch()
    },
    
    // 获取搜索建议
    async getSuggestions(keyword) {
      try {
        // 这里应该调用实际的搜索建议API
        // const res = await api.getSearchSuggestions(keyword)
        // this.suggestions = res.data
        
        // 模拟搜索建议
        this.suggestions = this.getMockSuggestions(keyword)
        
      } catch (error) {
        console.error('获取搜索建议失败:', error)
        this.suggestions = []
      }
    },
    
    // 获取模拟搜索建议
    getMockSuggestions(keyword) {
      const allSuggestions = [
        '新鲜蔬菜', '新鲜水果', '新鲜鸡蛋', '新鲜牛奶',
        '有机蔬菜', '有机水果', '有机大米', '有机鸡蛋',
        '优质大米', '优质面粉', '优质食用油',
        '深海鱼类', '深海虾类', '深海蟹类',
        '农家蔬菜', '农家鸡蛋', '农家大米',
        '绿色食品', '绿色蔬菜', '绿色水果'
      ]
      
      // 根据输入关键词过滤建议
      return allSuggestions
        .filter(item => item.includes(keyword))
        .slice(0, 8) // 最多显示8个建议
    },
    
    // 点击搜索建议
    handleSuggestionClick(suggestion) {
      this.searchKeyword = suggestion
      this.handleSearch()
    },
    
    // 添加到搜索历史
    addToHistory(keyword) {
      // 移除重复项
      const index = this.searchHistory.indexOf(keyword)
      if (index > -1) {
        this.searchHistory.splice(index, 1)
      }
      
      // 添加到开头
      this.searchHistory.unshift(keyword)
      
      // 限制历史记录数量
      if (this.searchHistory.length > 10) {
        this.searchHistory = this.searchHistory.slice(0, 10)
      }
      
      // 保存到本地存储
      this.saveSearchHistory()
    },
    
    // 加载搜索历史
    loadSearchHistory() {
      try {
        const history = uni.getStorageSync('searchHistory')
        if (history) {
          this.searchHistory = JSON.parse(history)
        }
      } catch (error) {
        console.error('加载搜索历史失败:', error)
      }
    },
    
    // 保存搜索历史
    saveSearchHistory() {
      try {
        uni.setStorageSync('searchHistory', JSON.stringify(this.searchHistory))
      } catch (error) {
        console.error('保存搜索历史失败:', error)
      }
    },
    
    // 清空搜索
    clearSearch() {
      this.searchKeyword = ''
      this.searchResults = []
      this.suggestions = []
      this.showSearchResults = false
    },
    
    // 清空历史
    clearHistory() {
      this.searchHistory = []
      this.saveSearchHistory()
    },
    
    // 返回
    goBack() {
      uni.navigateBack()
    },
    
    // 处理商品点击
    handleProductClick(product) {
      console.log('点击商品:', product.name)
    },
    
    // 处理加入购物车点击
    handleAddToCart(product) {
      console.log('加入购物车:', product.name)
      uni.showToast({
        title: '已加入购物车',
        icon: 'success',
        duration: 1500
      })
    }
  }
}
</script>

<style>
.page {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.safe-area-top {
  height: 130rpx;
  background-color: #f8f9fa;
}

.search-header {
  display: flex;
  align-items: center;
  padding: 16rpx 30rpx;
  margin-top: 24rpx;
  background-color: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.search-input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  background-color: #f3f4f6;
  padding: 16rpx 24rpx;
  margin-right: 20rpx;
  position: relative;
  border-radius: 8rpx;
}

.search-icon {
  width: 28rpx;
  height: 28rpx;
  margin-right: 16rpx;
}

.search-input {
  flex: 1;
  font-size: 26rpx;
  color: #333;
  height: 40rpx;
  line-height: 40rpx;
}

.clear-btn {
  padding: 10rpx;
}

.clear-text {
  font-size: 36rpx;
  color: #999;
}

.cancel-btn {
  padding: 10rpx;
}

.cancel-text {
  font-size: 28rpx;
  color: #666;
}

.suggestions-section, .hot-search-section, .history-section {
  background-color: #fff;
  margin-top: 20rpx;
  padding: 30rpx;
}

.suggestion-list {
  display: flex;
  flex-direction: column;
}

.suggestion-item {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.suggestion-item:last-child {
  border-bottom: none;
}

.suggestion-icon {
  width: 28rpx;
  height: 28rpx;
  margin-right: 20rpx;
}

.suggestion-text {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.section-title, .section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}

.title-text {
  font-size: 32rpx;
  color: #333;
  font-weight: bold;
}

.clear-history .clear-text {
  font-size: 26rpx;
  color: #999;
}

.hot-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.hot-tag {
  background-color: #f0f9ff;
  border-radius: 30rpx;
  padding: 16rpx 32rpx;
}

.tag-text {
  font-size: 26rpx;
  color: #3acc6f;
}

.history-list {
  display: flex;
  flex-direction: column;
}

.history-item {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.history-item:last-child {
  border-bottom: none;
}

.history-icon {
  width: 28rpx;
  height: 28rpx;
  margin-right: 20rpx;
}

.history-text {
  font-size: 28rpx;
  color: #666;
}

.search-results {
  padding: 30rpx;
}

.result-header {
  margin-bottom: 30rpx;
}

.result-count {
  font-size: 26rpx;
  color: #666;
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