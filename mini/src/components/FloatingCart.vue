<template>
  <view 
    class="floating-cart"
    :style="{ left: cartPosition.x + 'px', top: cartPosition.y + 'px' }"
    @touchstart="handleTouchStart"
    @touchmove="handleTouchMove"
    @touchend="handleTouchEnd"
    @click="handleCartClick"
  >
    <view class="cart-icon">
      <image class="cart-image" src="/static/images/cart.png" mode="aspectFit"></image>
      <!-- 购物车数量角标 -->
      <view v-if="cartCount > 0" class="cart-badge">
        <text class="cart-count">{{ cartCount > 99 ? '99+' : cartCount }}</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'FloatingCart',
  props: {
    cartCount: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      cartPosition: {
        x: 0,
        y: 0
      },
      isDragging: false,
      startPosition: {
        x: 0,
        y: 0
      },
      screenWidth: 0,
      screenHeight: 0
    }
  },
  mounted() {
    this.initPosition()
  },
  methods: {
    // 初始化位置
    initPosition() {
      const systemInfo = uni.getWindowInfo()
      this.screenWidth = systemInfo.windowWidth
      this.screenHeight = systemInfo.windowHeight
      
      // 默认位置：右下角（紧贴角落）
      const cartSize = 50 // 购物车实际大小的一半（100rpx/2）
      this.cartPosition.x = this.screenWidth - cartSize - 15 // 距离右边缘15px
      this.cartPosition.y = this.screenHeight - cartSize - 130 // 距离底部130px（避开底部导航栏）
    },
    
    // 触摸开始
    handleTouchStart(e) {
      this.isDragging = true
      this.startPosition.x = e.touches[0].clientX - this.cartPosition.x
      this.startPosition.y = e.touches[0].clientY - this.cartPosition.y
    },
    
    // 触摸移动
    handleTouchMove(e) {
      if (!this.isDragging) return
      
      e.preventDefault()
      
      let newX = e.touches[0].clientX - this.startPosition.x
      let newY = e.touches[0].clientY - this.startPosition.y
      
      // 边界限制
      const cartSize = 100 // 购物车大小
      newX = Math.max(0, Math.min(newX, this.screenWidth - cartSize))
      newY = Math.max(0, Math.min(newY, this.screenHeight - cartSize - 100)) // 减去底部导航栏高度
      
      this.cartPosition.x = newX
      this.cartPosition.y = newY
    },
    
    // 触摸结束
    handleTouchEnd(e) {
      if (!this.isDragging) return
      
      this.isDragging = false
      
      // 自动吸附到屏幕边缘
      const centerX = this.screenWidth / 2
      const cartSize = 100
      
      if (this.cartPosition.x < centerX) {
        // 吸附到左边
        this.cartPosition.x = 20
      } else {
        // 吸附到右边
        this.cartPosition.x = this.screenWidth - cartSize - 20
      }
    },
    
    // 点击购物车
    handleCartClick() {
      if (this.isDragging) return
      
      console.log('点击购物车')
      this.$emit('cart-click')
      
      // 跳转到购物车页面
      // uni.navigateTo({
      //   url: '/pages/cart/cart'
      // })
    }
  }
}
</script>

<style scoped>
.floating-cart {
  position: fixed;
  width: 100rpx;
  height: 100rpx;
  z-index: 9999;
  transition: all 0.3s ease;
}

.cart-icon {
  position: relative;
  width: 100%;
  height: 100%;
  background-color: #4CAF50;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(76, 175, 80, 0.4);
}

.cart-image {
  width: 50rpx;
  height: 50rpx;
}

.cart-badge {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  background-color: #ff4757;
  border-radius: 50%;
  min-width: 32rpx;
  height: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2rpx solid #fff;
}

.cart-count {
  font-size: 20rpx;
  color: #fff;
  font-weight: bold;
  text-align: center;
  line-height: 1;
}
</style>