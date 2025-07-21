# 高德地图API集成说明

## 概述

本项目已集成高德地图Web服务API，用于获取最新的中国行政区划数据（省市区），替代了原有的静态数据。

## 功能特性

- ✅ 获取全国省份列表
- ✅ 根据省份获取城市列表
- ✅ 根据城市获取区县列表
- ✅ 地址搜索功能
- ✅ 地理编码（地址转经纬度）
- ✅ 逆地理编码（经纬度转地址）
- ✅ 数据缓存机制（24小时）
- ✅ 错误处理和用户提示

## 申请API密钥

### 1. 注册高德开放平台账号
访问 [高德开放平台](https://lbs.amap.com/) 注册账号

### 2. 创建应用
1. 登录后进入控制台
2. 点击"应用管理" → "创建新应用"
3. 填写应用名称和简介

### 3. 添加Web服务API密钥
1. 在应用详情页面，点击"添加key"
2. 选择"Web服务"平台
3. 填写相关信息：
   - 名称：如"AgriShopper Web服务"
   - 安全域名：添加您的域名（开发时可添加localhost）
4. 提交后获得API密钥

## 配置API密钥

### 1. 修改配置文件
编辑 `frontend/config/amap.js` 文件：

```javascript
export const AMAP_CONFIG = {
  // 将这里替换为您的实际API密钥
  WEB_SERVICE_KEY: 'your_actual_amap_key_here',
  
  // 其他配置保持不变
  JS_API_KEY: 'your_amap_js_api_key_here',
  SECURITY_KEY: 'your_amap_security_key_here',
  // ...
}
```

### 2. 环境变量配置（推荐）
为了安全起见，建议使用环境变量：

```javascript
// 在 config/amap.js 中
export const AMAP_CONFIG = {
  WEB_SERVICE_KEY: process.env.VUE_APP_AMAP_KEY || 'your_amap_key_here',
  // ...
}
```

然后在项目根目录创建 `.env` 文件：
```
VUE_APP_AMAP_KEY=your_actual_amap_key_here
```

## API使用示例

### 1. 获取省份列表
```javascript
import { getProvinces } from '@/api/amap.js'

const provinces = await getProvinces()
console.log(provinces)
// 返回格式：[{name: '北京市', adcode: '110000'}, ...]
```

### 2. 获取城市列表
```javascript
import { getCities } from '@/api/amap.js'

const cities = await getCities('110000') // 北京市的adcode
console.log(cities)
// 返回格式：[{name: '北京市', adcode: '110100'}, ...]
```

### 3. 获取区县列表
```javascript
import { getDistricts } from '@/api/amap.js'

const districts = await getDistricts('110100') // 北京市的adcode
console.log(districts)
// 返回格式：[{name: '东城区', adcode: '110101'}, ...]
```

### 4. 地址搜索
```javascript
import { searchAddress } from '@/api/amap.js'

const results = await searchAddress('天安门', '北京市')
console.log(results)
```

### 5. 地理编码
```javascript
import { geocode } from '@/api/amap.js'

const results = await geocode('北京市朝阳区阜通东大街6号')
console.log(results)
```

## 缓存机制

系统内置了缓存机制，避免重复请求：

- **缓存时间**：24小时
- **缓存内容**：省份、城市、区县数据
- **缓存位置**：内存缓存（页面刷新后清除）

## 错误处理

API调用失败时会自动显示用户友好的错误提示：

- 网络错误：显示"网络连接失败"
- API错误：显示具体的错误信息
- 数据异常：显示"数据加载失败"

## 注意事项

### 1. API限制
- 免费版每日调用次数限制
- 建议在生产环境使用付费版
- 注意请求频率限制

### 2. 数据更新
- 高德地图数据会定期更新
- 建议定期检查数据准确性
- 缓存机制确保数据一致性

### 3. 安全考虑
- 不要在前端代码中暴露API密钥
- 建议使用环境变量或后端代理
- 可以配置IP白名单限制

## 故障排除

### 1. API密钥无效
```
错误：高德地图API密钥未配置
解决：检查配置文件中的API密钥是否正确
```

### 2. 网络请求失败
```
错误：获取省份数据失败
解决：检查网络连接和API密钥权限
```

### 3. 数据格式异常
```
错误：省份数据异常
解决：检查API返回的数据格式是否符合预期
```

## 相关文件

- `frontend/api/amap.js` - API服务文件
- `frontend/config/amap.js` - 配置文件
- `frontend/pages/address/add.vue` - 地址添加页面
- `frontend/pages/address/edit.vue` - 地址编辑页面

## 更新日志

- 2024-01-XX：初始版本，集成高德地图API
- 添加省市区三级联动功能
- 实现数据缓存机制
- 完善错误处理 