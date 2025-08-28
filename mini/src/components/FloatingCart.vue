<template>
  <view 
    class="floating-cart"
    :class="{ 'dragging': isDragging }"
    :style="{ left: cartPosition.x + 'px', top: cartPosition.y + 'px' }"
    @touchstart.stop="handleTouchStart"
    @touchmove.stop="handleTouchMove"
    @touchend.stop="handleTouchEnd"
    @click.stop="handleCartClick"
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
      touchOffset: {
        x: 0,
        y: 0
      },
      screenWidth: 0,
      screenHeight: 0,
      cartSize: 50 // 购物车实际大小（50px）
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
      
      // 默认位置：右下角
      this.cartPosition.x = this.screenWidth - this.cartSize - 20
      this.cartPosition.y = this.screenHeight - this.cartSize - 150 // 避开底部导航栏
    },
    
    // 触摸开始
    handleTouchStart(e) {
      const touch = e.touches[0]
      this.isDragging = true
      
      // 阻止事件冒泡，防止影响主界面
      e.stopPropagation()
      
      // 计算触摸点相对于购物车中心的偏移
      this.touchOffset.x = touch.clientX - this.cartPosition.x - this.cartSize / 2
      this.touchOffset.y = touch.clientY - this.cartPosition.y - this.cartSize / 2
    },
    
    // 触摸移动
    handleTouchMove(e) {
      if (!this.isDragging) return
      
      // 阻止默认行为和事件冒泡
      e.preventDefault()
      e.stopPropagation()
      
      const touch = e.touches[0]
      
      // 计算新位置（购物车中心跟随触摸点）
      let newX = touch.clientX - this.touchOffset.x - this.cartSize / 2
      let newY = touch.clientY - this.touchOffset.y - this.cartSize / 2
      
      // 边界限制
      newX = Math.max(0, Math.min(newX, this.screenWidth - this.cartSize))
      newY = Math.max(0, Math.min(newY, this.screenHeight - this.cartSize - 100)) // 减去底部导航栏高度
      
      this.cartPosition.x = newX
      this.cartPosition.y = newY
    },
    
    // 触摸结束
    handleTouchEnd(e) {
      if (!this.isDragging) return
      
      // 阻止事件冒泡
      e.stopPropagation()
      
      this.isDragging = false
      
      // 自动吸附到屏幕边缘
      const centerX = this.screenWidth / 2
      
      if (this.cartPosition.x < centerX) {
        // 吸附到左边
        this.cartPosition.x = 20
      } else {
        // 吸附到右边
        this.cartPosition.x = this.screenWidth - this.cartSize - 20
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
  width: 50px;
  height: 50px;
  z-index: 9999;
  transition: all 0.3s ease;
  user-select: none;
  -webkit-user-select: none;
}

.floating-cart.dragging {
  transition: none; /* 拖动时禁用过渡动画 */
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
  box-shadow: 0 2px 8px rgba(76, 175, 80, 0.4);
}

.cart-image {
  width: 25px;
  height: 25px;
}

.cart-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background-color: #ff4757;
  border-radius: 50%;
  min-width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #fff;
}

.cart-count {
  font-size: 10px;
  color: #fff;
  font-weight: bold;
  text-align: center;
  line-height: 1;
}
</style>