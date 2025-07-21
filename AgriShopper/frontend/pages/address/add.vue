<template>
  <view class="add-address-page">
    <view class="header">
      <text class="title">新增地址</text>
    </view>
    
    <view class="form-container">
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
        <view class="picker-container" @click="showProvincePicker">
          <view class="picker-content">
            <text class="picker-text" :class="{ 'placeholder': !addressForm.province }">
              {{ addressForm.province || '请选择省份' }}
            </text>
            <text class="picker-arrow">></text>
          </view>
        </view>
      </view>
      
      <view class="form-item">
        <text class="label">城市</text>
        <view class="picker-container" @click="showCityPicker" :class="{ 'disabled': !addressForm.province }">
          <view class="picker-content">
            <text class="picker-text" :class="{ 'placeholder': !addressForm.city }">
              {{ addressForm.city || '请选择城市' }}
            </text>
            <text class="picker-arrow">></text>
          </view>
        </view>
      </view>
      
      <view class="form-item">
        <text class="label">区县</text>
        <view class="picker-container" @click="showDistrictPicker" :class="{ 'disabled': !addressForm.city }">
          <view class="picker-content">
            <text class="picker-text" :class="{ 'placeholder': !addressForm.district }">
              {{ addressForm.district || '请选择区县' }}
            </text>
            <text class="picker-arrow">></text>
          </view>
        </view>
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
      <button @click="saveAddress" class="save-btn" :disabled="loading">
        {{ loading ? '保存中...' : '保存地址' }}
      </button>
    </view>
    
    <!-- 自定义选择器弹窗 -->
    <view v-if="showPickerModal" class="picker-modal-overlay" @click="closePickerModal">
      <view class="picker-modal" @click.stop>
        <view class="picker-modal-header">
          <text class="picker-modal-cancel" @click="closePickerModal">取消</text>
          <text class="picker-modal-title">{{ pickerTitle }}</text>
          <text class="picker-modal-confirm" @click="confirmPickerSelection">确定</text>
        </view>
        <scroll-view class="picker-modal-content" scroll-y>
          <view 
            v-for="(item, index) in pickerOptions" 
            :key="index"
            class="picker-option"
            :class="{ 'picker-option-selected': index === selectedPickerIndex }"
            @click="selectPickerOption(index)"
          >
            <text class="picker-option-text">{{ item }}</text>
          </view>
        </scroll-view>
      </view>
    </view>
      </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { store } from '../../store.js'
import addressApi from '../../api/address.js'
import { getProvinces, getCities, getDistricts } from '../../api/amap.js'

// 响应式数据
const loading = ref(false)

// 自定义选择器相关
const showPickerModal = ref(false)
const pickerTitle = ref('')
const pickerOptions = ref([])
const selectedPickerIndex = ref(-1)
const currentPickerType = ref('') // 'province', 'city', 'district'

// 省市区数据
const provinceList = ref([])
const cityList = ref([])
const districtList = ref([])

// 加载省份数据
const loadProvinces = async () => {
  try {
    uni.showLoading({ title: '加载中...' })
    
    // 尝试使用API获取数据
    try {
      const provinces = await getProvinces()
      provinceList.value = provinces.map(item => ({
        name: item.name,
        code: item.adcode
      }))
      console.log('API获取省份数据成功:', provinceList.value.length, '个省份')
    } catch (apiError) {
      console.warn('API获取失败，使用静态数据:', apiError)
      
      // 如果API失败，使用静态数据作为备选
      const staticProvinces = [
        { name: '北京市', code: '110000' },
        { name: '天津市', code: '120000' },
        { name: '河北省', code: '130000' },
        { name: '山西省', code: '140000' },
        { name: '内蒙古自治区', code: '150000' },
        { name: '辽宁省', code: '210000' },
        { name: '吉林省', code: '220000' },
        { name: '黑龙江省', code: '230000' },
        { name: '上海市', code: '310000' },
        { name: '江苏省', code: '320000' },
        { name: '浙江省', code: '330000' },
        { name: '安徽省', code: '340000' },
        { name: '福建省', code: '350000' },
        { name: '江西省', code: '360000' },
        { name: '山东省', code: '370000' },
        { name: '河南省', code: '410000' },
        { name: '湖北省', code: '420000' },
        { name: '湖南省', code: '430000' },
        { name: '广东省', code: '440000' },
        { name: '广西壮族自治区', code: '450000' },
        { name: '海南省', code: '460000' },
        { name: '重庆市', code: '500000' },
        { name: '四川省', code: '510000' },
        { name: '贵州省', code: '520000' },
        { name: '云南省', code: '530000' },
        { name: '西藏自治区', code: '540000' },
        { name: '江苏省', code: '610000' },
        { name: '甘肃省', code: '620000' },
        { name: '青海省', code: '630000' },
        { name: '宁夏回族自治区', code: '640000' },
        { name: '新疆维吾尔自治区', code: '650000' }
      ]
      
      provinceList.value = staticProvinces
    }
  } catch (error) {
    console.error('加载省份数据失败:', error)
    uni.showToast({
      title: '加载省份数据失败',
      icon: 'none'
    })
  } finally {
    uni.hideLoading()
  }
}

// 加载城市数据
const loadCities = async (provinceCode) => {
  try {
    // 尝试使用API获取数据
    try {
      const cities = await getCities(provinceCode)
      cityList.value = cities.map(item => ({
        name: item.name,
        code: item.adcode
      }))
      console.log('API获取城市数据成功:', cityList.value.length, '个城市')
    } catch (apiError) {
      console.warn('API获取失败，使用静态数据:', apiError)
      
      // 如果API失败，使用静态数据作为备选
      const staticCityData = {
        '110000': [{ name: '北京市', code: '110100' }],
        '120000': [{ name: '天津市', code: '120100' }],
        '130000': [
          { name: '石家庄市', code: '130100' },
          { name: '唐山市', code: '130200' },
          { name: '秦皇岛市', code: '130300' },
          { name: '邯郸市', code: '130400' },
          { name: '邢台市', code: '130500' },
          { name: '保定市', code: '130600' },
          { name: '张家口市', code: '130700' },
          { name: '承德市', code: '130800' },
          { name: '沧州市', code: '130900' },
          { name: '廊坊市', code: '131000' },
          { name: '衡水市', code: '131100' }
        ],
        '310000': [{ name: '上海市', code: '310100' }],
        '320000': [
          { name: '南京市', code: '320100' },
          { name: '无锡市', code: '320200' },
          { name: '徐州市', code: '320300' },
          { name: '常州市', code: '320400' },
          { name: '苏州市', code: '320500' },
          { name: '南通市', code: '320600' },
          { name: '连云港市', code: '320700' },
          { name: '淮安市', code: '320800' },
          { name: '盐城市', code: '320900' },
          { name: '扬州市', code: '321000' },
          { name: '镇江市', code: '321100' },
          { name: '泰州市', code: '321200' },
          { name: '宿迁市', code: '321300' }
        ],
        '330000': [
          { name: '杭州市', code: '330100' },
          { name: '宁波市', code: '330200' },
          { name: '温州市', code: '330300' },
          { name: '嘉兴市', code: '330400' },
          { name: '湖州市', code: '330500' },
          { name: '绍兴市', code: '330600' },
          { name: '金华市', code: '330700' },
          { name: '衢州市', code: '330800' },
          { name: '舟山市', code: '330900' },
          { name: '台州市', code: '331000' },
          { name: '丽水市', code: '331100' }
        ],
        '440000': [
          { name: '广州市', code: '440100' },
          { name: '韶关市', code: '440200' },
          { name: '深圳市', code: '440300' },
          { name: '珠海市', code: '440400' },
          { name: '湖州市', code: '440500' },
          { name: '佛山市', code: '440600' },
          { name: '江门市', code: '440700' },
          { name: '湛江市', code: '440800' },
          { name: '茂名市', code: '440900' },
          { name: '肇庆市', code: '441200' },
          { name: '惠州市', code: '441300' },
          { name: '梅州市', code: '441400' },
          { name: '汕尾市', code: '441500' },
          { name: '河源市', code: '441600' },
          { name: '阳江市', code: '441700' },
          { name: '清远市', code: '441800' },
          { name: '东莞市', code: '441900' },
          { name: '中山市', code: '442000' },
          { name: '潮州市', code: '445100' },
          { name: '揭阳市', code: '445200' },
          { name: '云浮市', code: '445300' }
        ],
        '500000': [{ name: '重庆市', code: '500100' }]
      }
      
      cityList.value = staticCityData[provinceCode] || []
    }
  } catch (error) {
    console.error('加载城市数据失败:', error)
    uni.showToast({
      title: '加载城市数据失败',
      icon: 'none'
    })
  }
}

// 加载区县数据
const loadDistricts = async (cityCode) => {
  try {
    // 尝试使用API获取数据
    try {
      const districts = await getDistricts(cityCode)
      districtList.value = districts.map(item => ({
        name: item.name,
        code: item.adcode
      }))
      console.log('API获取区县数据成功:', districtList.value.length, '个区县')
    } catch (apiError) {
      console.warn('API获取失败，使用静态数据:', apiError)
      
      // 如果API失败，使用静态数据作为备选
      const staticDistrictData = {
        '110100': [
          { name: '东城区', code: '110101' },
          { name: '西城区', code: '110102' },
          { name: '朝阳区', code: '110105' },
          { name: '丰台区', code: '110106' },
          { name: '石景山区', code: '110107' },
          { name: '海淀区', code: '110108' },
          { name: '门头沟区', code: '110109' },
          { name: '朝阳区', code: '110111' },
          { name: '通州区', code: '110112' },
          { name: '顺义区', code: '110113' },
          { name: '昌平区', code: '110114' },
          { name: '大兴区', code: '110115' },
          { name: '怀柔区', code: '110116' },
          { name: '平谷区', code: '110117' },
          { name: '密云区', code: '110118' },
          { name: '延庆区', code: '110119' }
        ],
        '310100': [
          { name: '黄浦区', code: '310101' },
          { name: '徐汇区', code: '310104' },
          { name: '长宁区', code: '310105' },
          { name: '静安区', code: '310106' },
          { name: '普陀区', code: '310107' },
          { name: '虹口区', code: '310109' },
          { name: '杨浦区', code: '310110' },
          { name: '闵行区', code: '310112' },
          { name: '宝山区', code: '310113' },
          { name: '嘉定区', code: '310114' },
          { name: '浦东新区', code: '310115' },
          { name: '金山区', code: '310116' },
          { name: '松江区', code: '310117' },
          { name: '青浦区', code: '310118' },
          { name: '奉贤区', code: '310120' },
          { name: '崇明区', code: '310151' }
        ],
        '440100': [
          { name: '荔湾区', code: '440103' },
          { name: '越秀区', code: '440104' },
          { name: '海珠区', code: '440105' },
          { name: '天河区', code: '440106' },
          { name: '白云区', code: '440111' },
          { name: '黄埔区', code: '440112' },
          { name: '番禺区', code: '440113' },
          { name: '花都区', code: '440114' },
          { name: '南沙区', code: '440115' },
          { name: '从化区', code: '440117' },
          { name: '增城区', code: '440118' }
        ],
        '440300': [
          { name: '罗湖区', code: '440303' },
          { name: '福田区', code: '440304' },
          { name: '南山区', code: '440305' },
          { name: '宝安区', code: '440306' },
          { name: '龙岗区', code: '440307' },
          { name: '盐田区', code: '440308' },
          { name: '龙华区', code: '440309' },
          { name: '坪山区', code: '440310' },
          { name: '光明区', code: '440311' }
        ]
      }
      
      districtList.value = staticDistrictData[cityCode] || []
    }
  } catch (error) {
    console.error('加载区县数据失败:', error)
    uni.showToast({
      title: '加载区县数据失败',
      icon: 'none'
    })
  }
}

// 页面加载时获取省份数据
onMounted(() => {
  loadProvinces()
})

// 地址表单
const addressForm = reactive({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  postalCode: '',
  isDefault: false
})

// 默认地址开关变化
const onDefaultChange = (e) => {
  addressForm.isDefault = e.detail.value
}

// 显示省份选择器
const showProvincePicker = () => {
  currentPickerType.value = 'province'
  pickerTitle.value = '选择省份'
  pickerOptions.value = provinceList.value.map(item => item.name)
  selectedPickerIndex.value = -1
  showPickerModal.value = true
}

// 显示城市选择器
const showCityPicker = async () => {
  if (!addressForm.province) {
    uni.showToast({
      title: '请先选择省份',
      icon: 'none'
    })
    return
  }
  
  // 找到选中的省份
  const selectedProvince = provinceList.value.find(item => item.name === addressForm.province)
  if (!selectedProvince) {
    uni.showToast({
      title: '省份数据异常',
      icon: 'none'
    })
    return
  }
  
  // 加载城市数据
  await loadCities(selectedProvince.code)
  
  if (cityList.value.length === 0) {
    uni.showToast({
      title: '该省份暂无城市数据',
      icon: 'none'
    })
    return
  }
  
  currentPickerType.value = 'city'
  pickerTitle.value = '选择城市'
  pickerOptions.value = cityList.value.map(item => item.name)
  selectedPickerIndex.value = -1
  showPickerModal.value = true
}

// 显示区县选择器
const showDistrictPicker = async () => {
  if (!addressForm.city) {
    uni.showToast({
      title: '请先选择城市',
      icon: 'none'
    })
    return
  }
  
  // 找到选中的城市
  const selectedCity = cityList.value.find(item => item.name === addressForm.city)
  if (!selectedCity) {
    uni.showToast({
      title: '城市数据异常',
      icon: 'none'
    })
    return
  }
  
  // 加载区县数据
  await loadDistricts(selectedCity.code)
  
  if (districtList.value.length === 0) {
    uni.showModal({
      title: '提示',
      content: '该城市暂无区县数据，请在详细地址中填写完整地址信息',
      showCancel: false,
      confirmText: '知道了'
    })
    return
  }
  
  currentPickerType.value = 'district'
  pickerTitle.value = '选择区县'
  pickerOptions.value = districtList.value.map(item => item.name)
  selectedPickerIndex.value = -1
  showPickerModal.value = true
}

// 选择选项
const selectPickerOption = (index) => {
  selectedPickerIndex.value = index
}

// 确认选择
const confirmPickerSelection = () => {
  if (selectedPickerIndex.value === -1) {
    uni.showToast({
      title: '请选择一个选项',
      icon: 'none'
    })
    return
  }
  
  const selectedValue = pickerOptions.value[selectedPickerIndex.value]
  
  if (currentPickerType.value === 'province') {
    addressForm.province = selectedValue
    addressForm.city = ''
    addressForm.district = ''
  } else if (currentPickerType.value === 'city') {
    addressForm.city = selectedValue
    addressForm.district = ''
  } else if (currentPickerType.value === 'district') {
    addressForm.district = selectedValue
  }
  
  closePickerModal()
}

// 关闭选择器弹窗
const closePickerModal = () => {
  showPickerModal.value = false
  selectedPickerIndex.value = -1
  currentPickerType.value = ''
}



// 验证表单
const validateForm = () => {
  if (!addressForm.receiverName.trim()) {
    uni.showToast({
      title: '请输入收货人姓名',
      icon: 'none'
    })
    return false
  }
  
  if (!addressForm.receiverPhone.trim()) {
    uni.showToast({
      title: '请输入手机号码',
      icon: 'none'
    })
    return false
  }
  
  // 简单的手机号验证
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!phoneRegex.test(addressForm.receiverPhone)) {
    uni.showToast({
      title: '请输入正确的手机号码',
      icon: 'none'
    })
    return false
  }
  
  if (!addressForm.province.trim()) {
    uni.showToast({
      title: '请输入省份',
      icon: 'none'
    })
    return false
  }
  
  if (!addressForm.city.trim()) {
    uni.showToast({
      title: '请输入城市',
      icon: 'none'
    })
    return false
  }
  
  if (!addressForm.district.trim()) {
    uni.showToast({
      title: '请输入区县',
      icon: 'none'
    })
    return false
  }
  
  if (!addressForm.detailAddress.trim()) {
    uni.showToast({
      title: '请输入详细地址',
      icon: 'none'
    })
    return false
  }
  
  return true
}

// 保存地址
const saveAddress = async () => {
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
      ...addressForm,
      userId: userInfo.id
    }
    
    console.log('提交地址数据:', addressData)
    
    const response = await addressApi.addAddress(addressData)
    console.log('新增地址响应:', response)
    
    if (response.code === 200) {
      uni.showToast({
        title: '地址添加成功',
        icon: 'success'
      })
      
      // 延迟返回上一页
      setTimeout(() => {
        uni.navigateBack()
      }, 1500)
    } else {
      uni.showToast({
        title: response.message || '添加地址失败',
        icon: 'error'
      })
    }
  } catch (error) {
    console.error('添加地址失败:', error)
    uni.showToast({
      title: '添加地址失败: ' + error.message,
      icon: 'error'
    })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.add-address-page {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.header {
  background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
  padding: 40rpx 30rpx;
  text-align: center;
  box-shadow: 0 4rpx 12rpx rgba(76, 175, 80, 0.3);
}

.title {
  font-size: 40rpx;
  font-weight: bold;
  color: #ffffff;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
}

.form-container {
  background: #fff;
  margin-top: 20rpx;
  border-radius: 20rpx 20rpx 0 0;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.form-item {
  padding: 40rpx 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
  display: flex;
  align-items: center;
  transition: background-color 0.3s ease;
}

.form-item:hover {
  background-color: #f8f9fa;
}

.form-item:last-child {
  border-bottom: none;
}

.checkbox-item {
  justify-content: space-between;
}

.label {
  font-size: 32rpx;
  color: #2c3e50;
  min-width: 180rpx;
  margin-right: 20rpx;
  font-weight: 600;
  position: relative;
}

.label::before {
  content: '';
  position: absolute;
  left: -10rpx;
  top: 50%;
  transform: translateY(-50%);
  width: 4rpx;
  height: 24rpx;
  background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
  border-radius: 2rpx;
}

.input {
  flex: 1;
  font-size: 32rpx;
  color: #2c3e50;
  border: none;
  outline: none;
  padding: 20rpx 24rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
  border: 2rpx solid #e9ecef;
  transition: all 0.3s ease;
}

.input:focus {
  border-color: #4CAF50;
  background: #ffffff;
  box-shadow: 0 0 0 4rpx rgba(76, 175, 80, 0.1);
}

.picker-container {
  flex: 1;
}

.picker-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 24rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
  border: 2rpx solid #e9ecef;
  transition: all 0.3s ease;
}

.picker-content:active {
  background: #e9ecef;
  border-color: #4CAF50;
  transform: scale(0.98);
}

.picker-container.disabled .picker-content {
  opacity: 0.5;
  background: #f5f5f5;
}

.picker-text {
  font-size: 32rpx;
  color: #2c3e50;
  flex: 1;
  font-weight: 500;
}

.picker-text.placeholder {
  color: #6c757d;
}

.picker-arrow {
  font-size: 24rpx;
  color: #4CAF50;
  margin-left: 16rpx;
  font-weight: bold;
}

.textarea {
  flex: 1;
  font-size: 32rpx;
  color: #2c3e50;
  border: none;
  outline: none;
  min-height: 120rpx;
  resize: none;
  padding: 20rpx 24rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
  border: 2rpx solid #e9ecef;
  transition: all 0.3s ease;
}

.textarea:focus {
  border-color: #4CAF50;
  background: #ffffff;
  box-shadow: 0 0 0 4rpx rgba(76, 175, 80, 0.1);
}

.button-container {
  padding: 40rpx 30rpx;
  margin-top: 40rpx;
}

.save-btn {
  background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
  color: #fff;
  border: none;
  padding: 32rpx;
  border-radius: 20rpx;
  font-size: 36rpx;
  width: 100%;
  font-weight: 600;
  box-shadow: 0 8rpx 20rpx rgba(76, 175, 80, 0.3);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.save-btn:active {
  transform: translateY(2rpx);
  box-shadow: 0 4rpx 12rpx rgba(76, 175, 80, 0.4);
}

.save-btn:disabled {
  background: linear-gradient(135deg, #cccccc 0%, #bbbbbb 100%);
  box-shadow: none;
  transform: none;
}

.save-btn::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: width 0.3s, height 0.3s;
}

.save-btn:active::before {
  width: 200rpx;
  height: 200rpx;
}

/* 自定义选择器弹窗样式 */
.picker-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 9999;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.picker-modal {
  width: 100%;
  background-color: #ffffff;
  border-radius: 24rpx 24rpx 0 0;
  max-height: 70vh;
  overflow: hidden;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}

.picker-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: 1rpx solid #f0f0f0;
  background-color: #ffffff;
}

.picker-modal-cancel {
  font-size: 36rpx;
  color: #666666;
  font-weight: 500;
}

.picker-modal-title {
  font-size: 40rpx;
  color: #333333;
  font-weight: bold;
}

.picker-modal-confirm {
  font-size: 36rpx;
  color: #4CAF50;
  font-weight: 600;
}

.picker-modal-content {
  max-height: 50vh;
  padding: 0;
}

.picker-option {
  padding: 32rpx;
  border-bottom: 1rpx solid #f0f0f0;
  transition: background-color 0.3s ease;
}

.picker-option:active {
  background-color: #f8f9fa;
}

.picker-option-selected {
  background-color: #e8f5e8;
  border-left: 6rpx solid #4CAF50;
}

.picker-option-text {
  font-size: 36rpx;
  color: #333333;
  font-weight: 500;
  text-align: center;
  display: block;
}

.picker-option-selected .picker-option-text {
  color: #4CAF50;
  font-weight: 600;
}
</style> 