<template>
  <view class="page">
    <!-- 顶部欢迎区域 -->
    <view class="welcome-section">
      <text class="welcome-text">欢迎使用农康优选小程序！</text>
    </view>

    <!-- 用户信息模块 -->
    <view class="user-info-card">
      <view class="user-info">
        <image class="avatar" :src="userAvatar" mode="aspectFill"></image>
        <view class="user-details">
          <text class="username">{{ username }}</text>
        </view>
      </view>
      <view class="settings-icon cursor-pointer">
        <text>设置</text>
      </view>
    </view>

    <!-- 订单快捷入口 -->
    <view class="order-section">
      <view class="order-item cursor-pointer" v-for="(item, index) in orderItems" :key="index">
        <text class="order-text">{{ item.text }}</text>
      </view>
    </view>

    <!-- 快捷功能区 -->
    <view class="quick-functions">
      <view class="function-item cursor-pointer" v-for="(item, index) in functionItems" :key="index">
        <text class="function-text">{{ item.text }}</text>
      </view>
    </view>

    <!-- 地址管理区域 -->
    <view class="address-section">
      <view class="section-title">
        <text>地址管理</text>
      </view>
      <view class="address-list">
        <view class="address-item" v-for="(address, index) in addresses" :key="index">
          <view class="address-info">
            <view class="address-detail">
              <text class="address-name">{{ address.name }}</text>
              <text class="address-phone">{{ address.phone }}</text>
            </view>
            <text class="address-text">{{ address.address }}</text>
          </view>
          <view class="address-default cursor-pointer" @click="setDefaultAddress(index)">
            <view class="radio-button" :class="{ 'radio-selected': address.isDefault }">
              <view v-if="address.isDefault" class="radio-inner"></view>
            </view>
            <text class="default-text">默认地址</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>
<script setup>
import { ref } from 'vue';

// 用户信息
const username = ref('张先生');
const userAvatar = ref('https://readdy.ai/api/search-image?query=A%20realistic%20portrait%20photo%20of%20a%20middle-aged%20Chinese%20man%20with%20short%20black%20hair%20and%20a%20friendly%20smile%2C%20wearing%20casual%20clothing%2C%20warm%20expression%2C%20natural%20lighting%2C%20high-quality%20detailed%20photo%2C%20subject%20fills%2080%20percent%20of%20frame%2C%20isolated%20on%20white%20background%2C%20centered%20composition&width=120&height=120&seq=1&orientation=squarish');

// 订单快捷入口
const orderItems = ref([
  { icon: 'wallet', text: '待支付' },
  { icon: 'paperplane', text: '待发货' },
  { icon: 'car', text: '待收货' },
  { icon: 'refresh', text: '售后' },
  { icon: 'list', text: '全部订单' }
]);

// 快捷功能区
const functionItems = ref([
  { icon: 'star', text: '我的收藏' },
  { icon: 'eye', text: '浏览记录' }
]);

// 地址信息
const addresses = ref([
  { 
    name: '张先生', 
    phone: '138****1234', 
    address: '浙江省杭州市西湖区文三路100号', 
    isDefault: true 
  },
  { 
    name: '张先生', 
    phone: '138****1234', 
    address: '浙江省杭州市滨江区江南大道500号', 
    isDefault: false 
  }
]);

// 设置默认地址
const setDefaultAddress = (index) => {
  addresses.value.forEach((address, i) => {
    address.isDefault = i === index;
  });
};
</script>
<style>
page {
  height: 100%;
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}

.cursor-pointer {
  cursor: pointer;
}

/* 顶部欢迎区域 */
.welcome-section {
  padding: 24rpx 40rpx;
}

.welcome-text {
  font-size: 18px;
  color: #333;
  font-weight: 500;
}

/* 用户信息卡片 */
.user-info-card {
  margin: 0 30rpx;
  padding: 32rpx 40rpx;
  background-color: #fff;
  border-radius: 24rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.user-info {
  display: flex;
  align-items: center;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  margin-right: 24rpx;
}

.username {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

/* 订单快捷入口 */
.order-section {
  margin: 24rpx 30rpx;
  padding: 30rpx 20rpx;
  background-color: #fff;
  border-radius: 24rpx;
  display: flex;
  justify-content: space-between;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.order-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.order-text {
  margin-top: 12rpx;
  font-size: 14px;
  color: #333;
}

/* 快捷功能区 */
.quick-functions {
  margin: 24rpx 30rpx;
  display: flex;
  justify-content: space-between;
}

.function-item {
  width: 48%;
  padding: 30rpx 0;
  background-color: #fff;
  border-radius: 24rpx;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.function-text {
  margin-left: 16rpx;
  font-size: 14px;
  color: #333;
}

/* 地址管理区域 */
.address-section {
  margin: 24rpx 30rpx;
  background-color: #fff;
  border-radius: 24rpx;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.section-title {
  padding: 30rpx;
  border-bottom: 1px solid #eee;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.address-list {
  padding: 0 30rpx;
}

.address-item {
  padding: 32rpx 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
}

.address-item:last-child {
  border-bottom: none;
}

.address-info {
  flex: 1;
}

.address-detail {
  display: flex;
  margin-bottom: 12rpx;
}

.address-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-right: 16rpx;
}

.address-phone {
  font-size: 14px;
  color: #666;
}

.address-text {
  font-size: 14px;
  color: #666;
  line-height: 1.4;
}

.address-default {
  display: flex;
  align-items: center;
  margin-left: 20rpx;
}

.radio-button {
  width: 36rpx;
  height: 36rpx;
  border-radius: 18rpx;
  border: 1px solid #ddd;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 12rpx;
}

.radio-selected {
  border-color: #4CAF50;
}

.radio-inner {
  width: 20rpx;
  height: 20rpx;
  border-radius: 10rpx;
  background-color: #4CAF50;
}

.default-text {
  font-size: 12px;
  color: #666;
}
</style>
