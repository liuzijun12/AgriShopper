<template>
  <view class="page">
    <view class="content">
      <!-- 分类标题 -->
      <view class="header">
        <text class="title">商品分类</text>
      </view>
      
      <!-- 分类网格 -->
      <view class="category-grid">
        <view 
          class="category-item" 
          v-for="(category, index) in categoryList" 
          :key="category.id"
          @click="handleCategoryClick(category)"
        >
          <image class="category-icon" :src="category.icon || '/static/images/category.png'"></image>
          <text class="category-name">{{ category.name }}</text>
        </view>
      </view>
      
      <!-- 空状态 -->
      <view v-if="categoryList.length === 0" class="empty-state">
        <text class="empty-text">暂无分类数据</text>
      </view>
    </view>
    
    <!-- 底部导航栏 -->
    <TabBar :currentPath="currentPath" />
  </view>
</template>

<script>
import TabBar from '@/components/TabBar.vue'
import { CategoryAPI } from '@/api'

export default {
  components: {
    TabBar
  },
  data() {
    return {
      currentPath: '/subPackages_user/pages/category/category',
      categoryList: []
    }
  },
  onLoad() {
    console.log('分类页面加载')
    this.loadCategoryList()
  },
  methods: {
    // 加载分类列表
    async loadCategoryList() {
      try {
        const categories = await CategoryAPI.getList()
        this.categoryList = categories || []
        console.log('获取分类列表成功:', categories)
      } catch (error) {
        console.error('获取分类列表失败:', error)
        // 使用模拟数据作为备用
        this.categoryList = [
          { id: 1, name: '蔬菜', icon: '/static/images/vegetables.png' },
          { id: 2, name: '水果', icon: '/static/images/fruits.png' },
          { id: 3, name: '粮油', icon: '/static/images/grains.png' },
          { id: 4, name: '肉禽蛋', icon: '/static/images/meat.png' },
          { id: 5, name: '水产', icon: '/static/images/seafood.png' },
          { id: 6, name: '蘑菇', icon: '/static/images/mushroom.png' },
          { id: 7, name: '豆制品', icon: '/static/images/tofu.png' },
          { id: 8, name: '调料', icon: '/static/images/category.png' }
        ]
      }
    },
    
    // 处理分类点击
    handleCategoryClick(category) {
      console.log('点击分类:', category.name)
      // 跳转到对应分类的商品列表页面
      uni.navigateTo({
        url: `/subPackages_user/pages/productList/productList?categoryId=${category.id}&categoryName=${category.name}`
      })
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

.category-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30rpx;
  padding: 20rpx;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  border-radius: 12rpx;
  padding: 40rpx 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.category-icon {
  width: 80rpx;
  height: 80rpx;
  margin-bottom: 20rpx;
}

.category-name {
  font-size: 28rpx;
  color: #333;
  text-align: center;
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