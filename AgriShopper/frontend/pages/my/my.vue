<template>
  <view class="page">
    <!-- 顶部欢迎区域 -->
    <view class="welcome-section">
      <text class="welcome-text">欢迎使用农康优选小程序！</text>
    </view>

    <!-- 用户信息模块 -->
    <view class="user-info-card" @click="handleUserClick">
      <view class="user-info">
        <image class="avatar" :src="userAvatar" mode="aspectFill"></image>
        <view class="user-details">
          <text class="username">{{ username }}</text>
          <text class="user-status">{{ isLoggedIn ? '已登录' : '点击登录' }}</text>
        </view>
      </view>
      <view class="settings-icon cursor-pointer">
        <text>{{ isLoggedIn ? '设置' : '登录' }}</text>
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
      <view class="function-item cursor-pointer" v-for="(item, index) in functionItems" :key="index" @click="handleFunctionClick(item)">
        <text class="function-text">{{ item.text }}</text>
      </view>
    </view>

    <!-- 退出登录区域（仅在已登录时显示） -->
    <view v-if="isLoggedIn" class="logout-section">
      <view class="logout-button cursor-pointer" @click="handleLogout">
        <text class="logout-icon">🚪</text>
        <text class="logout-text">退出登录</text>
      </view>
    </view>

    <!-- 地址管理区域 -->
    <view class="address-section">
      <view class="section-header">
        <text class="section-title">地址管理</text>
        <view class="add-address-btn cursor-pointer" @click="addAddress">
          <text class="add-icon">+</text>
          <text class="add-text">新增地址</text>
        </view>
      </view>
      
      <!-- 加载状态 -->
      <view v-if="loading" class="loading-container">
        <view class="loading-spinner"></view>
        <text class="loading-text">正在加载地址...</text>
      </view>
      
      <!-- 空状态 -->
      <view v-else-if="addresses.length === 0" class="empty-address">
        <text class="empty-text">暂无收货地址</text>
        <text class="empty-subtext">点击上方"新增地址"添加</text>
      </view>
      
      <!-- 地址列表 -->
      <view v-else class="address-list">
        <view class="address-item" v-for="(address, index) in addresses" :key="address.id">
          <view class="address-info" @click="editAddress(index)">
            <view class="address-detail">
              <text class="address-name">{{ address.name }}</text>
              <text class="address-phone">{{ address.phone }}</text>
              <text v-if="address.isDefault" class="default-tag">默认</text>
            </view>
            <text class="address-text">{{ address.address }}</text>
          </view>
          <view class="address-actions">
            <view class="action-btn cursor-pointer" @click="setDefaultAddress(index)" v-if="!address.isDefault">
              <text class="action-text">设为默认</text>
            </view>
            <view class="action-btn delete-btn cursor-pointer" @click="deleteAddress(index)">
              <text class="action-text">删除</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 微信登录弹窗 -->
    <WxLoginModal 
      :visible="showLoginModal" 
      @close="handleCloseLogin"
      @login-success="handleLoginSuccess"
    />
  </view>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import { store } from '../../store.js';
import WxLoginModal from '../../components/WxLoginModal.vue';
import addressApi from '../../api/address.js';

// 登录状态
const isLoggedIn = ref(false);
const showLoginModal = ref(false);

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
const addresses = ref([]);
const loading = ref(false);

// 加载地址列表
const loadAddresses = async () => {
  try {
    loading.value = true;
    const userInfo = store.getUserInfo();
    if (!userInfo || !userInfo.id) {
      console.log('用户未登录，无法加载地址');
      return;
    }

    const response = await addressApi.getAddressList(userInfo.id);
    if (response.code === 200 && response.data) {
      // 转换后端数据格式为前端格式
      addresses.value = response.data.map(address => ({
        id: address.id,
        name: address.receiverName,
        phone: address.receiverPhone,
        address: `${address.province}${address.city}${address.district}${address.detailAddress}`,
        isDefault: address.isDefault,
        // 保存完整地址信息用于编辑
        fullAddress: address
      }));
    } else {
      console.error('获取地址列表失败:', response.message);
      uni.showToast({
        title: response.message || '获取地址列表失败',
        icon: 'error'
      });
    }
  } catch (error) {
    console.error('加载地址列表失败:', error);
    uni.showToast({
      title: '加载地址列表失败',
      icon: 'error'
    });
  } finally {
    loading.value = false;
  }
};

// 设置默认地址
const setDefaultAddress = async (index) => {
  try {
    const address = addresses.value[index];
    const userInfo = store.getUserInfo();
    
    if (!userInfo || !userInfo.id) {
      uni.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }

    const response = await addressApi.setDefaultAddress(address.id, userInfo.id);
    if (response.code === 200) {
      // 更新本地状态
      addresses.value.forEach((addr, i) => {
        addr.isDefault = i === index;
      });
      
      uni.showToast({
        title: '设置默认地址成功',
        icon: 'success'
      });
    } else {
      uni.showToast({
        title: response.message || '设置默认地址失败',
        icon: 'error'
      });
    }
  } catch (error) {
    console.error('设置默认地址失败:', error);
    uni.showToast({
      title: '设置默认地址失败',
      icon: 'error'
    });
  }
};

// 删除地址
const deleteAddress = async (index) => {
  try {
    const address = addresses.value[index];
    const userInfo = store.getUserInfo();
    
    if (!userInfo || !userInfo.id) {
      uni.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }

    uni.showModal({
      title: '确认删除',
      content: `确定要删除地址"${address.name}"吗？`,
      confirmText: '删除',
      confirmColor: '#ff6b6b',
      success: async (res) => {
        if (res.confirm) {
          const response = await addressApi.deleteAddress(address.id, userInfo.id);
          if (response.code === 200) {
            // 从本地列表中移除
            addresses.value.splice(index, 1);
            
            uni.showToast({
              title: '删除地址成功',
              icon: 'success'
            });
          } else {
            uni.showToast({
              title: response.message || '删除地址失败',
              icon: 'error'
            });
          }
        }
      }
    });
  } catch (error) {
    console.error('删除地址失败:', error);
    uni.showToast({
      title: '删除地址失败',
      icon: 'error'
    });
  }
};

// 编辑地址
const editAddress = (index) => {
  const address = addresses.value[index];
  // 跳转到地址编辑页面，传递地址信息
  uni.navigateTo({
    url: `/pages/address/edit?addressId=${address.id}`
  });
};

// 新增地址
const addAddress = () => {
  uni.navigateTo({
    url: '/pages/address/add'
  });
};

// 检查登录状态
const checkLoginStatus = () => {
  try {
    const userInfo = store.getUserInfo();
    if (userInfo && userInfo.openid) {
      isLoggedIn.value = true;
      username.value = userInfo.nickname || '微信用户';
      if (userInfo.avatar) {
        userAvatar.value = userInfo.avatar;
      }
    } else {
      isLoggedIn.value = false;
    }
  } catch (error) {
    console.log('检查登录状态失败:', error);
    isLoggedIn.value = false;
  }
};

// 处理用户点击
const handleUserClick = () => {
  if (isLoggedIn.value) {
    // 已登录，可以跳转到设置页面或用户详情页
    uni.showToast({
      title: '已登录',
      icon: 'success'
    });
  } else {
    // 未登录，显示登录弹窗
    showLoginModal.value = true;
  }
};

// 关闭登录弹窗
const handleCloseLogin = () => {
  showLoginModal.value = false;
};

// 登录成功处理
const handleLoginSuccess = (userInfo) => {
  console.log('登录成功:', userInfo);
  checkLoginStatus();
  showLoginModal.value = false;
  // 登录成功后加载地址列表
  loadAddresses();
};

// 处理功能项点击
const handleFunctionClick = (item) => {
  switch (item.text) {
    case '我的收藏':
      uni.navigateTo({
        url: '/pages/favorites/favorites'
      });
      break;
    case '浏览记录':
      uni.showToast({
        title: '功能开发中',
        icon: 'none'
      });
      break;
    default:
      break;
  }
};

// 退出登录处理
const handleLogout = () => {
  uni.showModal({
    title: '确认退出',
    content: '确定要退出登录吗？退出后将清除所有本地数据。',
    confirmText: '退出',
    cancelText: '取消',
    confirmColor: '#ff6b6b',
    success: (res) => {
      if (res.confirm) {
        // 用户确认退出
        try {
          // 清除所有用户相关数据
          store.clearAllData();
          
          // 重置页面状态
          isLoggedIn.value = false;
          username.value = '点击登录';
          userAvatar.value = 'https://readdy.ai/api/search-image?query=A%20realistic%20portrait%20photo%20of%20a%20middle-aged%20Chinese%20man%20with%20short%20black%20hair%20and%20a%20friendly%20smile%2C%20wearing%20casual%20clothing%2C%20warm%20expression%2C%20natural%20lighting%2C%20high-quality%20detailed%20photo%2C%20subject%20fills%2080%20percent%20of%20frame%2C%20isolated%20on%20white%20background%2C%20centered%20composition&width=120&height=120&seq=1&orientation=squarish';
          
          // 显示退出成功提示
          uni.showToast({
            title: '已退出登录',
            icon: 'success',
            duration: 2000
          });
          
          console.log('用户已退出登录，所有数据已清除');
          
          // 跳转到首页
          setTimeout(() => {
            uni.reLaunch({
              url: '/pages/index/index'
            });
          }, 2000);
          
        } catch (error) {
          console.error('退出登录失败:', error);
          uni.showToast({
            title: '退出失败，请重试',
            icon: 'none'
          });
        }
      }
    }
  });
};

// 页面加载时检查登录状态
onMounted(() => {
  checkLoginStatus();
  // 如果已登录，加载地址列表
  if (isLoggedIn.value) {
    loadAddresses();
  }
});
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
  display: block;
  margin-bottom: 8rpx;
}

.user-status {
  font-size: 12px;
  color: #999;
  display: block;
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

/* 退出登录区域 */
.logout-section {
  margin: 24rpx 30rpx;
  padding: 20rpx;
  background-color: #fff;
  border-radius: 24rpx;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.logout-button {
  padding: 24rpx 40rpx;
  background: linear-gradient(135deg, #ff6b6b, #ee5a52);
  border-radius: 16rpx;
  border: none;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.3s ease;
}

.logout-button:active {
  transform: scale(0.98);
  background: linear-gradient(135deg, #ee5a52, #d63031);
}

.logout-text {
  font-size: 16px;
  color: #fff;
  font-weight: 500;
}

.logout-icon {
  font-size: 24px;
  margin-right: 10rpx;
}

/* 地址管理区域 */
.address-section {
  margin: 24rpx 30rpx;
  background-color: #fff;
  border-radius: 24rpx;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.section-header {
  padding: 40rpx 30rpx;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333;
}

.add-address-btn {
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  background-color: #4CAF50;
  border-radius: 25rpx;
  color: white;
}

.add-icon {
  font-size: 32rpx;
  margin-right: 12rpx;
  font-weight: bold;
}

.add-text {
  font-size: 28rpx;
  font-weight: 500;
}

/* 加载状态 */
.loading-container {
  padding: 80rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.loading-spinner {
  width: 80rpx;
  height: 80rpx;
  border: 6rpx solid #f3f3f3;
  border-top: 6rpx solid #4CAF50;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 30rpx;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  color: #999;
  font-size: 32rpx;
  font-weight: 500;
}

/* 空状态 */
.empty-address {
  padding: 80rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.empty-text {
  font-size: 36rpx;
  color: #999;
  margin-bottom: 16rpx;
  font-weight: 500;
}

.empty-subtext {
  font-size: 30rpx;
  color: #ccc;
}

/* 地址列表 */
.address-list {
  padding: 0 30rpx;
}

.address-item {
  padding: 40rpx 0;
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
  cursor: pointer;
}

.address-detail {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
}

.address-name {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  margin-right: 20rpx;
}

.address-phone {
  font-size: 28rpx;
  color: #666;
  margin-right: 20rpx;
}

.default-tag {
  font-size: 24rpx;
  color: #4CAF50;
  background-color: #e8f5e8;
  padding: 8rpx 16rpx;
  border-radius: 16rpx;
  font-weight: 500;
}

.address-text {
  font-size: 28rpx;
  color: #666;
  line-height: 1.5;
}

.address-actions {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.action-btn {
  padding: 16rpx 24rpx;
  border-radius: 20rpx;
  text-align: center;
  font-size: 26rpx;
  background-color: #f5f5f5;
  color: #666;
  font-weight: 500;
}

.action-btn.delete-btn {
  background-color: #ffebee;
  color: #f44336;
}
</style>
