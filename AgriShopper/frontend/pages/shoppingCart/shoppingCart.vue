<template>
  <view class="cart-container">
    <!-- 搜索栏 -->
    <view class="search-header">
      <view class="search-bar" @click="goToSearch">
        <image class="search-icon" :src="getImageUrl('icon/搜索.png')"></image>
        <text class="search-text">搜索购物车商品</text>
      </view>
    </view>

    <!-- 地址栏 -->
    <view class="address-bar" @click="selectAddress">
      <image class="address-icon" :src="getImageUrl('icon/地址.png')"></image>
      <text class="address-text">河北张家口市</text>
      <text class="arrow">›</text>
    </view>

    <!-- 促销提示 -->
    <view class="promotion-bar">
      <text class="promotion-tag">满减</text>
      <text class="promotion-text">跨店每满300减30，7月31日24点结束</text>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <view class="loading-spinner"></view>
      <text class="loading-text">正在加载购物车...</text>
    </view>

    <!-- 空购物车状态 -->
    <view v-else-if="cartItems.length === 0" class="empty-cart">
      <image class="empty-icon" src="/static/shoppingCart/empty-cart.png" mode="aspectFit"></image>
      <text class="empty-text">您的购物车里面空空如也</text>
      <text class="empty-subtext">快去挑选心仪的产品吧</text>
      <button class="go-shopping-btn" @click="goShopping">去购物</button>
    </view>

    <!-- 商品分组 -->
    <scroll-view v-else scroll-y class="cart-list">
      <!-- 店铺分组1 -->
      <view class="shop-group">
        <view class="shop-header">
          <checkbox-group @change="toggleShopSelect(1)">
            <checkbox :checked="isShopAllSelected(1)" color="#e93b3d" />
          </checkbox-group>
          <text class="shop-name">有机农场直营店</text>
          <text class="coupon-tag">领券</text>
        </view>

        <!-- 商品项 -->
        <view class="cart-item-wrapper" v-for="(item, index) in cartItems" :key="item.id">
          <view class="cart-item">
            <checkbox-group @change="(e) => handleItemSelect(e, item)">
              <checkbox :checked="item.selected" color="#e93b3d" />
            </checkbox-group>

            <view class="item-icon">
              <image v-if="item.image" :src="item.image" mode="aspectFill" class="item-image" />
              <text v-else class="icon">{{ getProductIcon(item.name) }}</text>
            </view>
            
            <view class="item-info">
              <view class="item-header">
                <text class="item-name">{{ item.name }}</text>
                <text v-if="item.tag" class="item-tag" :style="{backgroundColor: tagColors[item.tag]}">{{ item.tag }}</text>
              </view>
              <text class="item-spec">{{ item.specification }}</text>
              
              <view class="price-row">
                <text class="item-price">¥{{ item.price.toFixed(2) }}</text>
                <text v-if="item.originalPrice && item.originalPrice > item.price" class="original-price">¥{{ item.originalPrice.toFixed(2) }}</text>
              </view>
              
              <view class="item-footer">
                <text class="stock" v-if="item.stock < 10">仅剩{{ item.stock }}件</text>
                <view class="quantity-control">
                  <text class="quantity-btn" @click="decreaseQuantity(item)">-</text>
                  <input 
                    type="number" 
                    v-model.number="item.quantity" 
                    class="quantity-input"
                    @blur="validateQuantity(item)"
                    min="1"
                    max="99"
                  />
                  <text class="quantity-btn" @click="increaseQuantity(item)">+</text>
                </view>
              </view>
            </view>
          </view>
          
          <!-- 右滑删除按钮 -->
          <view class="delete-btn" @click="deleteCartItem(item)">
            <text class="delete-text">删除</text>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 底部结算栏 -->
    <view v-if="!loading && cartItems.length > 0" class="cart-footer">
      <view class="select-all">
        <checkbox-group @change="handleSelectAll">
          <checkbox :checked="isAllSelected" color="#e93b3d" />
          <text>全选</text>
        </checkbox-group>
      </view>

      <view class="price-section">
        <view class="price-row">
          <text class="total-text">合计：</text>
          <text class="total-price">¥{{ totalPrice.toFixed(2) }}</text>
        </view>
        <view class="saved-row" v-if="totalSaved > 0">
          <text class="saved-text">已省</text>
          <text class="saved-price">¥{{ totalSaved.toFixed(2) }}</text>
        </view>
      </view>

      <button 
        class="checkout-btn" 
        :class="{disabled: selectedCount === 0}"
        @click="checkout"
      >
        去结算({{ selectedCount }})
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import cartApi from '../../api/cart.js'
import env from '../../config/env.js'

// 响应式数据
const currentAddress = ref('河北张家口市')
const showPromotionTip = ref(true)
const loading = ref(false)
const cartItems = ref([])

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

const tagColors = {
  '热卖': '#ffeeee',
  '特惠': '#fff8e6',
  '新品': '#e6f7ff',
  '促销': '#ffebee'
}

// 加载购物车数据
const loadCartData = async () => {
  try {
    loading.value = true
    const response = await cartApi.getCartList()
    
    if (response.code === 200 && response.data) {
      // 转换后端数据为前端格式
      cartItems.value = response.data.map(item => ({
        id: item.id,
        productId: item.productId,
        name: item.product?.productName || '未知商品',
        price: parseFloat(item.unitPrice) || 0,
        originalPrice: parseFloat(item.product?.costPrice) || 0,
        quantity: item.quantity || 1,
        selected: item.isSelected || false,
        specification: item.product?.productSpec || '默认规格',
        stock: item.product?.stockQuantity || 0,
        tag: getProductTag(item.product),
        shopId: 1, // 默认店铺ID
        image: item.product?.mainImageUrl || '/static/default-product.png'
      }))
    } else {
      console.error('获取购物车数据失败:', response.message)
      uni.showToast({
        title: response.message || '获取购物车数据失败',
        icon: 'error'
      })
    }
  } catch (error) {
    console.error('加载购物车数据失败:', error)
    uni.showToast({
      title: '加载购物车数据失败',
      icon: 'error'
    })
  } finally {
    loading.value = false
  }
}

// 获取商品标签
const getProductTag = (product) => {
  if (!product) return ''
  if (product.isHotProduct) return '热卖'
  if (product.isNewProduct) return '新品'
  if (product.costPrice && product.costPrice > product.productPrice) return '特惠'
  return '促销'
}

// 页面加载时获取数据
onMounted(() => {
  loadCartData()
})

// 搜索方法
const goToSearch = () => {
  uni.navigateTo({
    url: '/pages/search/search'
  })
}

// 获取商品图标
const getProductIcon = (name) => {
  const icons = {
    '西红柿': '🍅',
    '鸡蛋': '🥚',
    '菠菜': '🥬',
    '苹果': '🍎',
    '香蕉': '🍌',
    '土豆': '🥔',
    '白菜': '🥬'
  }
  return icons[name.replace(/有机|农家|新鲜|优质|红富士/g, '')] || '🛒'
}

// 计算节省金额
const totalSaved = computed(() => {
  return cartItems.value
    .filter(item => item.selected && item.originalPrice && item.originalPrice > item.price)
    .reduce((sum, item) => sum + (item.originalPrice - item.price) * item.quantity, 0)
})

// 店铺全选逻辑
const isShopAllSelected = (shopId) => {
  const shopItems = cartItems.value.filter(item => item.shopId === shopId)
  return shopItems.length > 0 && shopItems.every(item => item.selected)
}

const toggleShopSelect = (shopId) => {
  const shouldSelect = !isShopAllSelected(shopId)
  cartItems.value = cartItems.value.map(item => {
    if (item.shopId === shopId) {
      return {...item, selected: shouldSelect}
    }
    return item
  })
}

// 商品选择
const handleItemSelect = (e, item) => {
  item.selected = e.detail.value.length > 0
  // 更新后端选中状态
  updateItemSelectedStatus(item)
}

// 是否全选
const isAllSelected = computed(() => {
  return cartItems.value.length > 0 && cartItems.value.every(item => item.selected)
})

// 全选/取消全选
const handleSelectAll = (e) => {
  const selected = e.detail.value.length > 0
  cartItems.value = cartItems.value.map(item => ({
    ...item,
    selected
  }))
  // 更新后端全选状态
  updateAllSelectedStatus(selected)
}

// 减少数量
const decreaseQuantity = async (item) => {
  if (item.quantity > 1) {
    item.quantity--
    await updateItemQuantity(item)
  }
}

// 增加数量
const increaseQuantity = async (item) => {
  if (item.quantity < 99) {
    item.quantity++
    await updateItemQuantity(item)
  }
}

// 验证数量输入
const validateQuantity = async (item) => {
  if (isNaN(item.quantity)) {
    item.quantity = 1
  }
  item.quantity = Math.max(1, Math.min(99, Math.floor(item.quantity)))
  await updateItemQuantity(item)
}

// 计算总价
const totalPrice = computed(() => {
  return cartItems.value
    .filter(item => item.selected)
    .reduce((sum, item) => sum + item.price * item.quantity, 0)
})

// 计算选中商品数量
const selectedCount = computed(() => {
  return cartItems.value.filter(item => item.selected).length
})

// 更新商品数量
const updateItemQuantity = async (item) => {
  try {
    await cartApi.updateQuantity(item.productId, item.quantity)
  } catch (error) {
    console.error('更新商品数量失败:', error)
    uni.showToast({
      title: '更新数量失败',
      icon: 'error'
    })
  }
}

// 更新商品选中状态
const updateItemSelectedStatus = async (item) => {
  try {
    await cartApi.updateSelectedStatus(item.id, item.selected)
  } catch (error) {
    console.error('更新选中状态失败:', error)
    uni.showToast({
      title: '更新选中状态失败',
      icon: 'error'
    })
  }
}

// 更新全选状态
const updateAllSelectedStatus = async (selected) => {
  try {
    await cartApi.selectAll(selected)
  } catch (error) {
    console.error('更新全选状态失败:', error)
    uni.showToast({
      title: '更新全选状态失败',
      icon: 'error'
    })
  }
}

// 去购物
const goShopping = () => {
  uni.switchTab({
    url: '/pages/index/index'
  })
}

// 删除购物车项
const deleteCartItem = async (item) => {
  try {
    uni.showModal({
      title: '确认删除',
      content: `确定要删除"${item.name}"吗？`,
      confirmText: '删除',
      confirmColor: '#e93b3d',
      success: async (res) => {
        if (res.confirm) {
          await cartApi.softDeleteCartItem(item.id)
          
          // 从本地列表中移除
          const index = cartItems.value.findIndex(cartItem => cartItem.id === item.id)
          if (index > -1) {
            cartItems.value.splice(index, 1)
          }
          
          uni.showToast({
            title: '删除成功',
            icon: 'success'
          })
        }
      }
    })
  } catch (error) {
    console.error('删除购物车项失败:', error)
    uni.showToast({
      title: error.message || '删除失败',
      icon: 'error'
    })
  }
}

// 结算操作
const checkout = () => {
  const selectedItems = cartItems.value.filter(item => item.selected)
  if (selectedItems.length === 0) {
    uni.showToast({
      title: '请选择要结算的商品',
      icon: 'none'
    })
    return
  }
  
  uni.navigateTo({
    url: '/pages/checkout/checkout'
  })
}
</script>

<style>
/* 基础样式调整 */
.cart-container {
  font-size: 32rpx;
  background-color: #f7f7f7;
  padding-bottom: 120rpx;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid #f3f3f3;
  border-top: 4rpx solid #e93b3d;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20rpx;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  color: #999;
  font-size: 28rpx;
}

/* 空购物车状态 */
.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.empty-icon {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 36rpx;
  color: #333;
  margin-bottom: 10rpx;
}

.empty-subtext {
  font-size: 28rpx;
  color: #999;
  margin-bottom: 40rpx;
}

.go-shopping-btn {
  width: 300rpx;
  height: 80rpx;
  line-height: 80rpx;
  background-color: #e93b3d;
  color: white;
  border-radius: 40rpx;
  font-size: 32rpx;
  font-weight: bold;
}

/* 搜索栏样式 */
.search-header {
  display: flex;
  align-items: center;
  padding: 20rpx 25rpx;
  background-color: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.search-bar {
  flex: 1;
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  border-radius: 50rpx;
  padding: 15rpx 25rpx;
}

.search-icon {
  width: 36rpx;
  height: 36rpx;
  margin-right: 15rpx;
}

.search-text {
  color: #999;
  font-size: 30rpx;
}

/* 地址栏 */
.address-bar {
  display: flex;
  align-items: center;
  padding: 25rpx;
  background-color: #fff;
  margin-bottom: 20rpx;
  font-size: 34rpx;
}
.address-icon {
  width: 40rpx;
  height: 40rpx;
  margin-right: 15rpx;
}
.address-text {
  flex: 1;
}
.arrow {
  font-size: 50rpx;
  color: #999;
}

/* 促销栏 */
.promotion-bar {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background-color: #fff8e6;
  margin-bottom: 20rpx;
}
.promotion-tag {
  background-color: #e93b3d;
  color: white;
  padding: 5rpx 15rpx;
  border-radius: 10rpx;
  font-size: 26rpx;
  margin-right: 15rpx;
}
.promotion-text {
  font-size: 28rpx;
  color: #666;
}

/* 店铺分组 */
.shop-group {
  background-color: #fff;
  margin-bottom: 20rpx;
  border-radius: 15rpx;
  overflow: hidden;
}

.shop-group .cart-item-wrapper:last-child {
  margin-bottom: 0;
}
.shop-header {
  display: flex;
  align-items: center;
  padding: 25rpx;
  border-bottom: 1rpx solid #f0f0f0;
}
.shop-name {
  flex: 1;
  font-size: 34rpx;
  font-weight: bold;
  margin-left: 15rpx;
}
.coupon-tag {
  color: #e93b3d;
  border: 1rpx solid #e93b3d;
  padding: 5rpx 15rpx;
  border-radius: 30rpx;
  font-size: 26rpx;
}

/* 商品项包装器 */
.cart-item-wrapper {
  position: relative;
  background-color: #fff;
  margin-bottom: 2rpx;
  overflow: hidden;
}

/* 商品项 */
.cart-item {
  display: flex;
  padding: 25rpx;
  padding-right: 145rpx;
  align-items: flex-start;
  background-color: #fff;
  transition: transform 0.3s ease;
  position: relative;
  overflow: hidden;
}

/* 删除按钮 */
.delete-btn {
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  width: 120rpx;
  background-color: #e93b3d;
  display: flex;
  align-items: center;
  justify-content: center;
  transform: translateX(100%);
  transition: transform 0.3s ease;
  z-index: 10;
}

.cart-item-wrapper:hover .delete-btn {
  transform: translateX(0);
}

.delete-text {
  color: white;
  font-size: 28rpx;
  font-weight: bold;
}
.item-icon {
  width: 180rpx;
  height: 180rpx;
  border-radius: 10rpx;
  margin: 0 25rpx;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 80rpx;
  overflow: hidden;
}

.item-image {
  width: 100%;
  height: 100%;
  border-radius: 10rpx;
}
.item-info {
  flex: 1;
}
.item-header {
  display: flex;
  align-items: center;
  margin-bottom: 15rpx;
}
.item-name {
  font-size: 32rpx;
  color: #333;
  margin-right: 15rpx;
}
.item-tag {
  font-size: 24rpx;
  padding: 3rpx 10rpx;
  border-radius: 6rpx;
}
.item-spec {
  color: #999;
  font-size: 28rpx;
  margin-bottom: 20rpx;
}
.price-row {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}
.item-price {
  color: #e93b3d;
  font-size: 36rpx;
  font-weight: bold;
  margin-right: 15rpx;
}
.original-price {
  color: #999;
  font-size: 28rpx;
  text-decoration: line-through;
}
.item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
  margin-top: 20rpx;
}
.stock {
  color: #e93b3d;
  font-size: 26rpx;
}

/* 数量选择器 */
.quantity-control {
  display: flex;
  align-items: center;
  border: 1rpx solid #ddd;
  border-radius: 10rpx;
  background-color: #fff;
  position: relative;
  z-index: 1;
  margin-right: 10rpx;
}
.quantity-btn {
  width: 70rpx;
  height: 70rpx;
  line-height: 70rpx;
  text-align: center;
  font-size: 36rpx;
  color: #666;
  background-color: #f7f7f7;
  border: none;
  outline: none;
}
.quantity-input {
  width: 90rpx;
  height: 70rpx;
  text-align: center;
  font-size: 32rpx;
  border-left: 1rpx solid #ddd;
  border-right: 1rpx solid #ddd;
  border-top: none;
  border-bottom: none;
  background-color: #fff;
  outline: none;
}

/* 底部结算栏 */
.cart-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 120rpx;
  background-color: #fff;
  display: flex;
  align-items: center;
  padding: 0 25rpx;
  border-top: 1rpx solid #f0f0f0;
  z-index: 1000;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);
}
.select-all {
  display: flex;
  align-items: center;
  margin-right: 30rpx;
}
.select-all text {
  margin-left: 15rpx;
  font-size: 32rpx;
}
.price-section {
  flex: 1;
  margin-left: 20rpx;
  line-height: 1;
}
.price-row {
  display: flex;
  align-items: center;
}
.saved-row {
  display: flex;
  align-items: center;
  margin-top: 0;
}
.total-text {
  font-size: 28rpx;
  color: #666;
}
.total-price {
  color: #e93b3d;
  font-size: 32rpx;
  font-weight: bold;
  margin-left: 10rpx;
}
.saved-text {
  font-size: 24rpx;
  color: #999;
}
.saved-price {
  color: #999;
  font-size: 24rpx;
  margin-left: 10rpx;
}
.checkout-btn {
  width: 250rpx;
  height: 90rpx;
  line-height: 90rpx;
  background-color: #e93b3d;
  color: white;
  border-radius: 45rpx;
  font-size: 32rpx;
  font-weight: bold;
}
.checkout-btn.disabled {
  background-color: #ccc;
}
</style>