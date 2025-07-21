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
      <button @click="updateAddress" class="save-btn" :disabled="loading">
        {{ loading ? '保存中...' : '保存修改' }}
      </button>
      <button @click="deleteAddress" class="delete-btn" :disabled="loading">
        删除地址
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

// 响应式数据
const loading = ref(false)
const addressForm = ref(null)

// 自定义选择器相关
const showPickerModal = ref(false)
const pickerTitle = ref('')
const pickerOptions = ref([])
const selectedPickerIndex = ref(-1)
const currentPickerType = ref('') // 'province', 'city', 'district'

// 省市区数据
const provinceList = ref([
  '北京市', '天津市', '河北省', '山西省', '内蒙古自治区', '辽宁省', '吉林省', '黑龙江省',
  '上海市', '江苏省', '浙江省', '安徽省', '福建省', '江西省', '山东省', '河南省',
  '湖北省', '湖南省', '广东省', '广西壮族自治区', '海南省', '重庆市', '四川省', '贵州省',
  '云南省', '西藏自治区', '陕西省', '甘肃省', '青海省', '宁夏回族自治区', '新疆维吾尔自治区'
])

const cityList = ref({
  '北京市': ['北京市'],
  '天津市': ['天津市'],
  '河北省': ['石家庄市', '唐山市', '秦皇岛市', '邯郸市', '邢台市', '保定市', '张家口市', '承德市', '沧州市', '廊坊市', '衡水市'],
  '山西省': ['太原市', '大同市', '阳泉市', '长治市', '晋城市', '朔州市', '晋中市', '运城市', '忻州市', '临汾市', '吕梁市'],
  '内蒙古自治区': ['呼和浩特市', '包头市', '乌海市', '赤峰市', '通辽市', '鄂尔多斯市', '呼伦贝尔市', '巴彦淖尔市', '乌兰察布市', '兴安盟', '锡林郭勒盟', '阿拉善盟'],
  '辽宁省': ['沈阳市', '大连市', '鞍山市', '抚顺市', '本溪市', '丹东市', '锦州市', '营口市', '阜新市', '辽阳市', '盘锦市', '铁岭市', '朝阳市', '葫芦岛市'],
  '吉林省': ['长春市', '吉林市', '四平市', '辽源市', '通化市', '白山市', '松原市', '白城市', '延边朝鲜族自治州'],
  '黑龙江省': ['哈尔滨市', '齐齐哈尔市', '鸡西市', '鹤岗市', '双鸭山市', '大庆市', '伊春市', '佳木斯市', '七台河市', '牡丹江市', '黑河市', '绥化市', '大兴安岭地区'],
  '上海市': ['上海市'],
  '江苏省': ['南京市', '无锡市', '徐州市', '常州市', '苏州市', '南通市', '连云港市', '淮安市', '盐城市', '扬州市', '镇江市', '泰州市', '宿迁市'],
  '浙江省': ['杭州市', '宁波市', '温州市', '嘉兴市', '湖州市', '绍兴市', '金华市', '衢州市', '舟山市', '台州市', '丽水市'],
  '安徽省': ['合肥市', '芜湖市', '蚌埠市', '淮南市', '马鞍山市', '淮北市', '铜陵市', '安庆市', '黄山市', '滁州市', '阜阳市', '宿州市', '六安市', '亳州市', '池州市', '宣城市'],
  '福建省': ['福州市', '厦门市', '莆田市', '三明市', '泉州市', '漳州市', '南平市', '龙岩市', '宁德市'],
  '江西省': ['南昌市', '景德镇市', '萍乡市', '九江市', '新余市', '鹰潭市', '赣州市', '吉安市', '宜春市', '抚州市', '上饶市'],
  '山东省': ['济南市', '青岛市', '淄博市', '枣庄市', '东营市', '烟台市', '潍坊市', '济宁市', '泰安市', '威海市', '日照市', '莱芜市', '临沂市', '德州市', '聊城市', '滨州市', '菏泽市'],
  '河南省': ['郑州市', '开封市', '洛阳市', '平顶山市', '安阳市', '鹤壁市', '新乡市', '焦作市', '濮阳市', '许昌市', '漯河市', '三门峡市', '南阳市', '商丘市', '信阳市', '周口市', '驻马店市', '济源市'],
  '湖北省': ['武汉市', '黄石市', '十堰市', '宜昌市', '襄阳市', '鄂州市', '荆门市', '孝感市', '荆州市', '黄冈市', '咸宁市', '随州市', '恩施土家族苗族自治州'],
  '湖南省': ['长沙市', '株洲市', '湘潭市', '衡阳市', '邵阳市', '岳阳市', '常德市', '张家界市', '益阳市', '郴州市', '永州市', '怀化市', '娄底市', '湘西土家族苗族自治州'],
  '广东省': ['广州市', '韶关市', '深圳市', '珠海市', '汕头市', '佛山市', '江门市', '湛江市', '茂名市', '肇庆市', '惠州市', '梅州市', '汕尾市', '河源市', '阳江市', '清远市', '东莞市', '中山市', '潮州市', '揭阳市', '云浮市'],
  '广西壮族自治区': ['南宁市', '柳州市', '桂林市', '梧州市', '北海市', '防城港市', '钦州市', '贵港市', '玉林市', '百色市', '贺州市', '河池市', '来宾市', '崇左市'],
  '海南省': ['海口市', '三亚市', '三沙市', '儋州市'],
  '重庆市': ['重庆市'],
  '四川省': ['成都市', '自贡市', '攀枝花市', '泸州市', '德阳市', '绵阳市', '广元市', '遂宁市', '内江市', '乐山市', '南充市', '眉山市', '宜宾市', '广安市', '达州市', '雅安市', '巴中市', '资阳市', '阿坝藏族羌族自治州', '甘孜藏族自治州', '凉山彝族自治州'],
  '贵州省': ['贵阳市', '六盘水市', '遵义市', '安顺市', '毕节市', '铜仁市', '黔西南布依族苗族自治州', '黔东南苗族侗族自治州', '黔南布依族苗族自治州'],
  '云南省': ['昆明市', '曲靖市', '玉溪市', '保山市', '昭通市', '丽江市', '普洱市', '临沧市', '楚雄彝族自治州', '红河哈尼族彝族自治州', '文山壮族苗族自治州', '西双版纳傣族自治州', '大理白族自治州', '德宏傣族景颇族自治州', '怒江傈僳族自治州', '迪庆藏族自治州'],
  '西藏自治区': ['拉萨市', '日喀则市', '昌都市', '林芝市', '山南市', '那曲市', '阿里地区'],
  '陕西省': ['西安市', '铜川市', '宝鸡市', '咸阳市', '渭南市', '延安市', '汉中市', '榆林市', '安康市', '商洛市'],
  '甘肃省': ['兰州市', '嘉峪关市', '金昌市', '白银市', '天水市', '武威市', '张掖市', '平凉市', '酒泉市', '庆阳市', '定西市', '陇南市', '临夏回族自治州', '甘南藏族自治州'],
  '青海省': ['西宁市', '海东市', '海北藏族自治州', '黄南藏族自治州', '海南藏族自治州', '果洛藏族自治州', '玉树藏族自治州', '海西蒙古族藏族自治州'],
  '宁夏回族自治区': ['银川市', '石嘴山市', '吴忠市', '固原市', '中卫市'],
  '新疆维吾尔自治区': ['乌鲁木齐市', '克拉玛依市', '吐鲁番市', '哈密市', '昌吉回族自治州', '博尔塔拉蒙古自治州', '巴音郭楞蒙古自治州', '阿克苏地区', '克孜勒苏柯尔克孜自治州', '喀什地区', '和田地区', '伊犁哈萨克自治州', '塔城地区', '阿勒泰地区']
})

const districtList = ref({
  '北京市': ['东城区', '西城区', '朝阳区', '丰台区', '石景山区', '海淀区', '门头沟区', '房山区', '通州区', '顺义区', '昌平区', '大兴区', '怀柔区', '平谷区', '密云区', '延庆区'],
  '上海市': ['黄浦区', '徐汇区', '长宁区', '静安区', '普陀区', '虹口区', '杨浦区', '闵行区', '宝山区', '嘉定区', '浦东新区', '金山区', '松江区', '青浦区', '奉贤区', '崇明区'],
  '广州市': ['荔湾区', '越秀区', '海珠区', '天河区', '白云区', '黄埔区', '番禺区', '花都区', '南沙区', '从化区', '增城区'],
  '深圳市': ['罗湖区', '福田区', '南山区', '宝安区', '龙岗区', '盐田区', '龙华区', '坪山区', '光明区'],
  '杭州市': ['上城区', '下城区', '江干区', '拱墅区', '西湖区', '滨江区', '萧山区', '余杭区', '富阳区', '临安区', '桐庐县', '淳安县', '建德市'],
  '南京市': ['玄武区', '秦淮区', '建邺区', '鼓楼区', '浦口区', '栖霞区', '雨花台区', '江宁区', '六合区', '溧水区', '高淳区'],
  '成都市': ['锦江区', '青羊区', '金牛区', '武侯区', '成华区', '龙泉驿区', '青白江区', '新都区', '温江区', '双流区', '郫都区', '新津区', '金堂县', '大邑县', '蒲江县', '都江堰市', '彭州市', '邛崃市', '崇州市', '简阳市'],
  '武汉市': ['江岸区', '江汉区', '硚口区', '汉阳区', '武昌区', '青山区', '洪山区', '东西湖区', '汉南区', '蔡甸区', '江夏区', '黄陂区', '新洲区'],
  '西安市': ['新城区', '碑林区', '莲湖区', '灞桥区', '未央区', '雁塔区', '阎良区', '临潼区', '长安区', '高陵区', '鄠邑区', '蓝田县', '周至县'],
  '重庆市': ['渝中区', '大渡口区', '江北区', '沙坪坝区', '九龙坡区', '南岸区', '渝北区', '巴南区', '黔江区', '长寿区', '江津区', '合川区', '永川区', '南川区', '綦江区', '大足区', '璧山区', '铜梁区', '潼南区', '荣昌区', '梁平区', '城口县', '丰都县', '垫江县', '武隆区', '忠县', '开州区', '云阳县', '奉节县', '巫山县', '巫溪县', '石柱土家族自治县', '秀山土家族苗族自治县', '酉阳土家族苗族自治县', '彭水苗族土家族自治县']
})

// 默认地址开关变化
const onDefaultChange = (e) => {
  addressForm.value.isDefault = e.detail.value
}

// 显示省份选择器
const showProvincePicker = () => {
  currentPickerType.value = 'province'
  pickerTitle.value = '选择省份'
  pickerOptions.value = provinceList.value
  selectedPickerIndex.value = -1
  showPickerModal.value = true
}

// 显示城市选择器
const showCityPicker = () => {
  if (!addressForm.value.province) {
    uni.showToast({
      title: '请先选择省份',
      icon: 'none'
    })
    return
  }
  
  const cities = cityList.value[addressForm.value.province] || []
  if (cities.length === 0) {
    uni.showToast({
      title: '该省份暂无城市数据',
      icon: 'none'
    })
    return
  }
  
  currentPickerType.value = 'city'
  pickerTitle.value = '选择城市'
  pickerOptions.value = cities
  selectedPickerIndex.value = -1
  showPickerModal.value = true
}

// 显示区县选择器
const showDistrictPicker = () => {
  if (!addressForm.value.city) {
    uni.showToast({
      title: '请先选择城市',
      icon: 'none'
    })
    return
  }
  
  const districts = districtList.value[addressForm.value.city] || []
  if (districts.length === 0) {
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
  pickerOptions.value = districts
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
    addressForm.value.province = selectedValue
    addressForm.value.city = ''
    addressForm.value.district = ''
  } else if (currentPickerType.value === 'city') {
    addressForm.value.city = selectedValue
    addressForm.value.district = ''
  } else if (currentPickerType.value === 'district') {
    addressForm.value.district = selectedValue
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
  margin-bottom: 20rpx;
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

.delete-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: #fff;
  border: none;
  padding: 32rpx;
  border-radius: 20rpx;
  font-size: 36rpx;
  width: 100%;
  font-weight: 600;
  box-shadow: 0 8rpx 20rpx rgba(255, 107, 107, 0.3);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.delete-btn:active {
  transform: translateY(2rpx);
  box-shadow: 0 4rpx 12rpx rgba(255, 107, 107, 0.4);
}

.delete-btn:disabled {
  background: linear-gradient(135deg, #cccccc 0%, #bbbbbb 100%);
  box-shadow: none;
  transform: none;
}

.delete-btn::before {
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

.delete-btn:active::before {
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