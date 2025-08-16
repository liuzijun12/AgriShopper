<template>
  <view class="page">
    <view class="content">
      <view class="header">
        <text class="title">分类</text>
      </view>
      
      <view class="category-content">
        <view v-if="selectedCategory" class="selected-category">
          <text class="selected-text">当前选择：{{ selectedCategory.name }}</text>
          <text class="selected-id">分类ID：{{ selectedCategory.id }}</text>
        </view>
        <text class="placeholder">分类页面内容</text>
      </view>
    </view>
    
    <!-- 底部导航栏 -->
    <TabBar :currentPath="currentPath" />
  </view>
</template>

<script>
import TabBar from '@/components/TabBar.vue'

export default {
  components: {
    TabBar
  },
  data() {
    return {
      currentPath: '/ubPackages_user/pages/category/category',
      selectedCategory: null
    }
  },
  onLoad() {

  },
  onShow() {
    // 监听分类选择事件
    uni.$on('selectCategory', this.handleCategorySelect)
  },
  onHide() {
    // 移除事件监听
    uni.$off('selectCategory', this.handleCategorySelect)
  },
  methods: {
    // 处理分类选择
    handleCategorySelect(category) {
      this.selectedCategory = category
      console.log('接收到分类选择:', category)
      // 这里可以根据分类ID加载对应的商品数据
      this.loadCategoryProducts(category.id)
    },
    
    // 加载分类商品数据
    loadCategoryProducts(categoryId) {
      console.log('加载分类商品:', categoryId)
      // 这里调用API获取对应分类的商品数据
      // const products = await api.getProductsByCategory(categoryId)
    }
  }
}
</script>

<style>
.page {
  min-height: 100vh;
  padding-bottom: calc(100rpx + env(safe-area-inset-bottom));
}

.content {
  padding: 40rpx 20rpx;
}

.header {
  text-align: center;
  margin-bottom: 40rpx;
}

.title {
  font-size: 36rpx;
  color: #333;
  font-weight: bold;
}

.category-content {
  text-align: center;
  margin-top: 100rpx;
}

.selected-category {
  background-color: #f0f9ff;
  border: 2rpx solid #3acc6f;
  border-radius: 12rpx;
  padding: 30rpx;
  margin-bottom: 40rpx;
}

.selected-text {
  display: block;
  font-size: 32rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.selected-id {
  display: block;
  font-size: 24rpx;
  color: #666;
}

.placeholder {
  font-size: 28rpx;
  color: #666;
}
</style>