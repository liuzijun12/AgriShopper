<template>
  <view class="cart-container">
    <!-- 空购物车提示 -->
    <view v-if="cartItems.length === 0" class="empty-cart">
      <image src="/static/empty-cart.png" mode="aspectFit" class="empty-cart-image" />
      <text class="empty-cart-text">购物车是空的</text>
      <button class="go-shopping-btn" @click="goShopping">去逛逛</button>
    </view>

    <!-- 购物车列表 -->
    <scroll-view v-else scroll-y class="cart-list">
      <!-- 编辑按钮 -->
      <view class="edit-header">
        <text>共 {{ totalItems }} 件商品</text>
        <text class="edit-btn" @click="toggleEditMode">{{ isEditMode ? '完成' : '编辑' }}</text>
      </view>

      <!-- 商品列表 -->
      <view class="cart-items">
        <view v-for="(item, index) in cartItems" :key="index" class="cart-item">
          <!-- 选择框 -->
          <checkbox-group @change="(e) => handleItemSelect(e, item)">
            <checkbox :checked="item.selected" color="#4CAF50" />
          </checkbox-group>

          <!-- 商品信息 -->
          <image :src="item.image" mode="aspectFill" class="item-image" @click="viewProduct(item)" />
          <view class="item-info">
            <text class="item-name">{{ item.name }}</text>
            <text class="item-spec">{{ item.specification }}</text>
            <view class="item-price-row">
              <text class="item-price">¥{{ item.price.toFixed(2) }}</text>
              <!-- 数量控制 -->
              <view class="quantity-control">
                <text class="quantity-btn" @click="decreaseQuantity(item)">-</text>
                <input 
                  type="number" 
                  v-model="item.quantity" 
                  class="quantity-input"
                  @blur="validateQuantity(item)"
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
          <checkbox :checked="isAllSelected" color="#4CAF50" />
          <text>全选</text>
        </checkbox-group>
      </view>

      <view v-if="!isEditMode" class="total-section">
        <view class="total-price">
          <text>合计：</text>
          <text class="price">¥{{ totalPrice.toFixed(2) }}</text>
        </view>
        <button class="checkout-btn" :disabled="selectedCount === 0" @click="checkout">
          结算({{ selectedCount }})
        </button>
      </view>

      <view v-else class="delete-section">
        <button class="delete-btn" :disabled="selectedCount === 0" @click="deleteSelected">
          删除({{ selectedCount }})
        </button>
      </view>
    </view>

  </view>
</template>

<script lang="ts">
export default {
  name: 'ShoppingCart',
  onShow() {
    uni.$emit('tabPageShow')
  }
}
</script>

<script setup lang="ts">
import { ref, computed } from 'vue'

// 购物车数据
interface CartItem {
  id: number
  name: string
  image: string
  price: number
  quantity: number
  selected: boolean
  specification: string
}

const cartItems = ref<CartItem[]>([
  {
    id: 1,
    name: '新鲜西红柿',
    image: '/static/products/tomato.jpg',
    price: 5.99,
    quantity: 1,
    selected: false,
    specification: '500g/份'
  },
  {
    id: 2,
    name: '有机生菜',
    image: '/static/products/lettuce.jpg',
    price: 3.99,
    quantity: 2,
    selected: false,
    specification: '300g/份'
  }
])

// 编辑模式
const isEditMode = ref(false)
const toggleEditMode = () => {
  isEditMode.value = !isEditMode.value
}

// 商品选择
const handleItemSelect = (e: any, item: CartItem) => {
  item.selected = e.detail.value.length > 0
}

const isAllSelected = computed(() => {
  return cartItems.value.length > 0 && cartItems.value.every(item => item.selected)
})

const handleSelectAll = (e: any) => {
  const selected = e.detail.value.length > 0
  cartItems.value.forEach(item => {
    item.selected = selected
  })
}

// 数量控制
const decreaseQuantity = (item: CartItem) => {
  if (item.quantity > 1) {
    item.quantity--
  }
}

const increaseQuantity = (item: CartItem) => {
  if (item.quantity < 99) {
    item.quantity++
  }
}

const validateQuantity = (item: CartItem) => {
  const quantity = parseInt(item.quantity.toString())
  if (isNaN(quantity) || quantity < 1) {
    item.quantity = 1
  } else if (quantity > 99) {
    item.quantity = 99
  } else {
    item.quantity = quantity
  }
}

// 计算属性
const totalItems = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

const selectedCount = computed(() => {
  return cartItems.value.filter(item => item.selected).length
})

const totalPrice = computed(() => {
  return cartItems.value
    .filter(item => item.selected)
    .reduce((sum, item) => sum + item.price * item.quantity, 0)
})

// 操作方法
const goShopping = () => {
  uni.switchTab({
    url: '/pages/index/index'
  })
}

const viewProduct = (item: CartItem) => {
  uni.navigateTo({
    url: `/pages/productDetail/productDetail?id=${item.id}`
  })
}

const deleteSelected = () => {
  uni.showModal({
    title: '提示',
    content: '确定要删除选中的商品吗？',
    success: (res) => {
      if (res.confirm) {
        cartItems.value = cartItems.value.filter(item => !item.selected)
        if (cartItems.value.length === 0) {
          isEditMode.value = false
        }
      }
    }
  })
}

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
.cart-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  padding-bottom: 220rpx;
  box-sizing: border-box;
  background-color: #f5f5f5;
}

.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.empty-cart-image {
  width: 240rpx;
  height: 240rpx;
  margin-bottom: 30rpx;
}

.empty-cart-text {
  color: #999;
  font-size: 28rpx;
  margin-bottom: 40rpx;
}

.go-shopping-btn {
  width: 240rpx;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  background-color: #4CAF50;
  color: #fff;
  border-radius: 40rpx;
  font-size: 28rpx;
}

.cart-list {
  flex: 1;
}

.edit-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 30rpx;
  background-color: #fff;
  font-size: 28rpx;
  color: #333;
}

.edit-btn {
  color: #4CAF50;
}

.cart-items {
  padding: 20rpx;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background-color: #fff;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
}

.item-image {
  width: 160rpx;
  height: 160rpx;
  border-radius: 8rpx;
  margin: 0 20rpx;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.item-name {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 10rpx;
}

.item-spec {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 20rpx;
}

.item-price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-price {
  color: #ff6b6b;
  font-size: 32rpx;
  font-weight: bold;
}

.quantity-control {
  display: flex;
  align-items: center;
  border: 1rpx solid #eee;
  border-radius: 8rpx;
}

.quantity-btn {
  width: 60rpx;
  height: 60rpx;
  line-height: 60rpx;
  text-align: center;
  font-size: 28rpx;
  color: #333;
  background-color: #f5f5f5;
}

.quantity-input {
  width: 80rpx;
  height: 60rpx;
  line-height: 60rpx;
  text-align: center;
  font-size: 28rpx;
  border-left: 1rpx solid #eee;
  border-right: 1rpx solid #eee;
}

.cart-footer {
  position: fixed;
  bottom: 100rpx;
  left: 0;
  right: 0;
  height: 100rpx;
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30rpx;
  border-top: 1rpx solid #eee;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  z-index: 98;
}

.select-all {
  display: flex;
  align-items: center;
}

.select-all text {
  font-size: 28rpx;
  margin-left: 10rpx;
}

.total-section {
  display: flex;
  align-items: center;
}

.total-price {
  margin-right: 20rpx;
  font-size: 28rpx;
}

.price {
  color: #ff6b6b;
  font-size: 36rpx;
  font-weight: bold;
}

.checkout-btn {
  width: 200rpx;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  background-color: #4CAF50;
  color: #fff;
  border-radius: 40rpx;
  font-size: 28rpx;
}

.checkout-btn[disabled] {
  background-color: #ccc;
}

.delete-section {
  display: flex;
  align-items: center;
}

.delete-btn {
  width: 200rpx;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  background-color: #ff6b6b;
  color: #fff;
  border-radius: 40rpx;
  font-size: 28rpx;
}

.delete-btn[disabled] {
  background-color: #ccc;
}
</style>
