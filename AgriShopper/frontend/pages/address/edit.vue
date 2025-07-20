<template>
  <view class="edit-address-page">
    <view class="header">
      <text class="title">编辑地址</text>
    </view>
    
    <view class="form-container" v-if="addressForm">
      <view class="form-item">
        <text class="label">收货人姓名</text>
        <input 
          v-model="addressForm.receiverName" 
          placeholder="请输入收货人姓名"
          class="input"
        />
      </view>
      
      <view class="form-item">
        <text class="label">手机号码</text>
        <input 
          v-model="addressForm.receiverPhone" 
          placeholder="请输入手机号码"
          type="number"
          maxlength="11"
          class="input"
        />
      </view>
      
      <view class="form-item">
        <text class="label">省份</text>
        <input 
          v-model="addressForm.province" 
          placeholder="请输入省份"
          class="input"
        />
      </view>
      
      <view class="form-item">
        <text class="label">城市</text>
        <input 
          v-model="addressForm.city" 
          placeholder="请输入城市"
          class="input"
        />
      </view>
      
      <view class="form-item">
        <text class="label">区县</text>
        <input 
          v-model="addressForm.district" 
          placeholder="请输入区县"
          class="input"
        />
      </view>
      
      <view class="form-item">
        <text class="label">详细地址</text>
        <textarea 
          v-model="addressForm.detailAddress" 
          placeholder="请输入详细地址"
          class="textarea"
        />
      </view>
      
      <view class="form-item">
        <text class="label">邮政编码</text>
        <input 
          v-model="addressForm.postalCode" 
          placeholder="请输入邮政编码（可选）"
          type="number"
          maxlength="6"
          class="input"
        />
      </view>
      
      <view class="form-item checkbox-item">
        <text class="label">设为默认地址</text>
        <switch 
          :checked="addressForm.isDefault" 
          @change="onDefaultChange"
          color="#4CAF50"
        />
      </view>
    </view>
    
    <view class="button-container">
      <button @click="updateAddress" class="save-btn" :disabled="loading">
        {{ loading ? '保存中...' : '保存修改' }}
      </button>
      <button @click="deleteAddress" class="delete-btn" :disabled="loading">
        删除地址
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { store } from '../../store.js'
import addressApi from '../../api/address.js'

// 响应式数据
const loading = ref(false)
const addressForm = ref(null)

// 默认地址开关变化
const onDefaultChange = (e) => {
  addressForm.value.isDefault = e.detail.value
}

// 验证表单
const validateForm = () => {
  if (!addressForm.value.receiverName.trim()) {
    uni.showToast({
      title: '请输入收货人姓名',
      icon: 'none'
    })
    return false
  }
  
  if (!addressForm.value.receiverPhone.trim()) {
    uni.showToast({
      title: '请输入手机号码',
      icon: 'none'
    })
    return false
  }
  
  // 简单的手机号验证
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!phoneRegex.test(addressForm.value.receiverPhone)) {
    uni.showToast({
      title: '请输入正确的手机号码',
      icon: 'none'
    })
    return false
  }
  
  if (!addressForm.value.province.trim()) {
    uni.showToast({
      title: '请输入省份',
      icon: 'none'
    })
    return false
  }
  
  if (!addressForm.value.city.trim()) {
    uni.showToast({
      title: '请输入城市',
      icon: 'none'
    })
    return false
  }
  
  if (!addressForm.value.district.trim()) {
    uni.showToast({
      title: '请输入区县',
      icon: 'none'
    })
    return false
  }
  
  if (!addressForm.value.detailAddress.trim()) {
    uni.showToast({
      title: '请输入详细地址',
      icon: 'none'
    })
    return false
  }
  
  return true
}

// 加载地址详情
const loadAddressDetail = async () => {
  try {
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1]
    const addressId = currentPage.options.addressId
    
    if (!addressId) {
      uni.showToast({
        title: '地址ID不存在',
        icon: 'error'
      })
      return
    }
    
    const userInfo = store.getUserInfo()
    if (!userInfo || !userInfo.id) {
      uni.showToast({
        title: '请先登录',
        icon: 'none'
      })
      return
    }
    
    const response = await addressApi.getAddressById(addressId, userInfo.id)
    if (response.code === 200 && response.data) {
      addressForm.value = response.data
    } else {
      uni.showToast({
        title: response.message || '获取地址详情失败',
        icon: 'error'
      })
    }
  } catch (error) {
    console.error('获取地址详情失败:', error)
    uni.showToast({
      title: '获取地址详情失败',
      icon: 'error'
    })
  }
}

// 更新地址
const updateAddress = async () => {
  if (!validateForm()) {
    return
  }
  
  try {
    loading.value = true
    
    const userInfo = store.getUserInfo()
    if (!userInfo || !userInfo.id) {
      uni.showToast({
        title: '请先登录',
        icon: 'none'
      })
      return
    }
    
    // 准备提交的数据
    const addressData = {
      ...addressForm.value,
      userId: userInfo.id
    }
    
    console.log('更新地址数据:', addressData)
    
    const response = await addressApi.updateAddress(addressData)
    console.log('更新地址响应:', response)
    
    if (response.code === 200) {
      uni.showToast({
        title: '地址更新成功',
        icon: 'success'
      })
      
      // 延迟返回上一页
      setTimeout(() => {
        uni.navigateBack()
      }, 1500)
    } else {
      uni.showToast({
        title: response.message || '更新地址失败',
        icon: 'error'
      })
    }
  } catch (error) {
    console.error('更新地址失败:', error)
    uni.showToast({
      title: '更新地址失败: ' + error.message,
      icon: 'error'
    })
  } finally {
    loading.value = false
  }
}

// 删除地址
const deleteAddress = () => {
  if (!addressForm.value) {
    return
  }
  
  uni.showModal({
    title: '确认删除',
    content: `确定要删除地址"${addressForm.value.receiverName}"吗？`,
    confirmText: '删除',
    confirmColor: '#ff6b6b',
    success: async (res) => {
      if (res.confirm) {
        try {
          loading.value = true
          
          const userInfo = store.getUserInfo()
          if (!userInfo || !userInfo.id) {
            uni.showToast({
              title: '请先登录',
              icon: 'none'
            })
            return
          }
          
          const response = await addressApi.deleteAddress(addressForm.value.id, userInfo.id)
          if (response.code === 200) {
            uni.showToast({
              title: '地址删除成功',
              icon: 'success'
            })
            
            // 延迟返回上一页
            setTimeout(() => {
              uni.navigateBack()
            }, 1500)
          } else {
            uni.showToast({
              title: response.message || '删除地址失败',
              icon: 'error'
            })
          }
        } catch (error) {
          console.error('删除地址失败:', error)
          uni.showToast({
            title: '删除地址失败: ' + error.message,
            icon: 'error'
          })
        } finally {
          loading.value = false
        }
      }
    }
  })
}

// 页面加载时获取地址详情
onMounted(() => {
  loadAddressDetail()
})
</script>

<style scoped>
.edit-address-page {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.header {
  background: #fff;
  padding: 30rpx;
  text-align: center;
  border-bottom: 1rpx solid #eee;
}

.title {
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
}

.form-container {
  background: #fff;
  margin-top: 20rpx;
}

.form-item {
  padding: 40rpx 30rpx;
  border-bottom: 1rpx solid #eee;
  display: flex;
  align-items: center;
}

.form-item:last-child {
  border-bottom: none;
}

.checkbox-item {
  justify-content: space-between;
}

.label {
  font-size: 32rpx;
  color: #333;
  min-width: 180rpx;
  margin-right: 20rpx;
  font-weight: 500;
}

.input {
  flex: 1;
  font-size: 32rpx;
  color: #333;
  border: none;
  outline: none;
}

.textarea {
  flex: 1;
  font-size: 32rpx;
  color: #333;
  border: none;
  outline: none;
  min-height: 100rpx;
  resize: none;
}

.button-container {
  padding: 40rpx 30rpx;
  margin-top: 40rpx;
}

.save-btn {
  background: #4CAF50;
  color: #fff;
  border: none;
  padding: 30rpx;
  border-radius: 15rpx;
  font-size: 36rpx;
  width: 100%;
  margin-bottom: 20rpx;
  font-weight: 600;
}

.save-btn:disabled {
  background: #ccc;
}

.delete-btn {
  background: #ff6b6b;
  color: #fff;
  border: none;
  padding: 30rpx;
  border-radius: 15rpx;
  font-size: 36rpx;
  width: 100%;
  font-weight: 600;
}

.delete-btn:disabled {
  background: #ccc;
}
</style> 