<template>
  <view class="cart-container">
    <!-- 搜索栏 -->
    <view class="search-bar" @click="goToSearch">
      <text class="search-icon">🔍</text>
      <text class="search-text">搜索购物车商品</text>
    </view>

    <!-- 地址栏 -->
    <view class="address-bar" @click="selectAddress">
      <text class="address-icon">📍</text>
      <text class="address-text">河北张家口市</text>
      <text class="arrow">›</text>
    </view>

    <!-- 促销提示 -->
    <view class="promotion-bar">
      <text class="promotion-tag">满减</text>
      <text class="promotion-text">跨店每满300减30，7月31日24点结束</text>
    </view>

    <!-- 商品分组 -->
    <scroll-view scroll-y class="cart-list">
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
        <view class="cart-item" v-for="(item, index) in farmProducts" :key="index">
          <checkbox-group @change="(e) => handleItemSelect(e, item)">
            <checkbox :checked="item.selected" color="#e93b3d" />
          </checkbox-group>

          <view class="item-icon">
            <text class="icon">{{ getProductIcon(item.name) }}</text>
          </view>
          
          <view class="item-info">
            <view class="item-header">
              <text class="item-name">{{ item.name }}</text>
              <text v-if="item.tag" class="item-tag" :style="{backgroundColor: tagColors[item.tag]}">{{ item.tag }}</text>
            </view>
            <text class="item-spec">{{ item.specification }}</text>
            
            <view class="price-row">
              <text class="item-price">¥{{ item.price.toFixed(2) }}</text>
              <text v-if="item.originalPrice" class="original-price">¥{{ item.originalPrice.toFixed(2) }}</text>
            </view>
            
            <view class="item-footer">
              <text class="stock" v-if="item.stock < 10">仅剩{{ item.stock }}件</text>
              <view class="quantity-control">
                <text class="quantity-btn" @click="decreaseQuantity(item)">-</text>
                <input 
                  type="number" 
                  v-model="item.quantity" 
                  class="quantity-input"
                  disabled
                />
                <text class="quantity-btn" @click="increaseQuantity(item)">+</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 底部结算栏 -->
    <view class="cart-footer">
      <view class="select-all">
        <checkbox-group @change="handleSelectAll">
          <checkbox :checked="isAllSelected" color="#e93b3d" />
          <text>全选</text>
        </checkbox-group>
      </view>

      <view class="price-section">
        <text class="total-text">合计：</text>
        <text class="total-price">¥{{ totalPrice.toFixed(2) }}</text>
        <text class="saved-price" v-if="totalSaved > 0">已省¥{{ totalSaved.toFixed(2) }}</text>
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
import { ref, computed } from 'vue'

// 示例数据
const currentAddress = ref('河北张家口市')
const showPromotionTip = ref(true)

const tagColors = {
  '热卖': '#ffeeee',
  '特惠': '#fff8e6',
  '新品': '#e6f7ff',
  '促销': '#ffebee'
}

const farmProducts = ref([
  {
    id: 1,
    name: '有机西红柿',
    price: 5.99,
    originalPrice: 7.99,
    quantity: 1,
    selected: false,
    specification: '500g/份',
    stock: 8,
    tag: '热卖',
    shopId: 1
  },
  {
    id: 2,
    name: '农家土鸡蛋',
    price: 15.8,
    quantity: 2,
    selected: false,
    specification: '30枚/盒',
    stock: 15,
    tag: '特惠',
    shopId: 1
  },
  {
    id: 3,
    name: '新鲜菠菜',
    price: 3.5,
    quantity: 1,
    selected: false,
    specification: '250g/捆',
    stock: 20,
    tag: '新品',
    shopId: 1
  },
  {
    id: 4,
    name: '优质西红柿',
    price: 4.5,
    originalPrice: 6.0,
    quantity: 1,
    selected: false,
    specification: '400g/份',
    stock: 12,
    tag: '促销',
    shopId: 1
  },
  {
    id: 5,
    name: '新鲜土豆',
    price: 2.8,
    quantity: 3,
    selected: false,
    specification: '1kg/袋',
    stock: 25,
    tag: '热卖',
    shopId: 1
  },
  {
    id: 6,
    name: '有机白菜',
    price: 3.2,
    originalPrice: 4.0,
    quantity: 1,
    selected: false,
    specification: '800g/颗',
    stock: 18,
    tag: '特惠',
    shopId: 1
  },
  {
    id: 7,
    name: '红富士苹果',
    price: 8.8,
    originalPrice: 10.0,
    quantity: 1,
    selected: false,
    specification: '1kg/袋',
    stock: 15,
    tag: '促销',
    shopId: 1
  }
])

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

// 计算还差多少金额满足满减
const neededAmount = computed(() => {
  const currentTotal = totalPrice.value
  return currentTotal < 300 ? (300 - currentTotal).toFixed(2) : 0
})

// 计算节省金额
const totalSaved = computed(() => {
  return farmProducts.value
    .filter(item => item.selected && item.originalPrice)
    .reduce((sum, item) => sum + (item.originalPrice - item.price) * item.quantity, 0)
})

// 店铺全选逻辑
const isShopAllSelected = (shopId) => {
  const shopItems = farmProducts.value.filter(item => item.shopId === shopId)
  return shopItems.length > 0 && shopItems.every(item => item.selected)
}

const toggleShopSelect = (shopId) => {
  const shouldSelect = !isShopAllSelected(shopId)
  farmProducts.value.forEach(item => {
    if (item.shopId === shopId) {
      item.selected = shouldSelect
    }
  })
}

// 商品选择
const handleItemSelect = (e, item) => {
  item.selected = e.detail.value.length > 0
}

// 是否全选
const isAllSelected = computed(() => {
  return farmProducts.value.length > 0 && farmProducts.value.every(item => item.selected)
})

// 全选/取消全选
const handleSelectAll = (e) => {
  const selected = e.detail.value.length > 0
  farmProducts.value.forEach(item => {
    item.selected = selected
  })
}

// 减少数量
const decreaseQuantity = (item) => {
  if (item.quantity > 1) {
    item.quantity--
  }
}

// 增加数量
const increaseQuantity = (item) => {
  if (item.quantity < 99) {
    item.quantity++
  }
}

// 计算总价
const totalPrice = computed(() => {
  return farmProducts.value
    .filter(item => item.selected)
    .reduce((sum, item) => sum + item.price * item.quantity, 0)
})

// 计算选中商品数量
const selectedCount = computed(() => {
  return farmProducts.value.filter(item => item.selected).length
})

// 结算操作
const checkout = () => {
  const selectedItems = farmProducts.value.filter(item => item.selected)
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

/* 搜索栏样式 */
.search-bar {
  display: flex;
  align-items: center;
  padding: 20rpx 25rpx;
  background-color: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.search-icon {
  margin-right: 15rpx;
  font-size: 36rpx;
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
  margin-right: 15rpx;
  font-size: 40rpx;
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

/* 店铺分组 */
.shop-group {
  background-color: #fff;
  margin-bottom: 20rpx;
  border-radius: 15rpx;
  overflow: hidden;
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

/* 商品项 */
.cart-item {
  display: flex;
  padding: 25rpx;
  align-items: center;
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
}
.quantity-btn {
  width: 70rpx;
  height: 70rpx;
  line-height: 70rpx;
  text-align: center;
  font-size: 36rpx;
  color: #666;
  background-color: #f7f7f7;
}
.quantity-input {
  width: 90rpx;
  height: 70rpx;
  text-align: center;
  font-size: 32rpx;
  border-left: 1rpx solid #ddd;
  border-right: 1rpx solid #ddd;
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
}
.total-text {
  font-size: 32rpx;
}
.total-price {
  color: #e93b3d;
  font-size: 38rpx;
  font-weight: bold;
}
.saved-price {
  color: #999;
  font-size: 26rpx;
  margin-left: 15rpx;
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