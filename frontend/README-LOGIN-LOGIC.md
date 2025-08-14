# 登录逻辑和导航栏实现说明

## 功能概述

实现了基于用户类型的动态导航栏和登录逻辑：

### 1. 登录前状态
- **导航栏**：只显示"首页"和"我的"两个标签
- **页面访问**：
  - 首页：可正常访问
  - 我的：显示登录按钮，点击跳转到登录页

### 2. 普通用户登录后
- **导航栏**：显示"首页"、"分类"、"物流"、"我的"四个标签
- **页面访问**：
  - 首页：正常访问主包页面
  - 分类：跳转到用户分包页面 `/user/pages/category/category`
  - 物流：跳转到用户分包页面 `/user/pages/logistics/logistics`
  - 我的：跳转到用户分包页面 `/user/pages/mine/mine`

### 3. 商户登录后
- **自动跳转**：登录成功后直接跳转到商户管理系统
- **页面访问**：完全使用商户分包页面
  - 商户首页：`/merchant/pages/index/index`
  - 订单管理：`/merchant/pages/orders/orders`
  - 商品管理：`/merchant/pages/products/products`

## 测试账号

### 普通用户
- 用户名：`user`
- 密码：`123456`
- 登录后：显示完整导航栏，可访问用户分包页面

### 商户用户
- 用户名：`admin`
- 密码：`123456`
- 登录后：自动跳转到商户管理系统

## 技术实现

### 1. 状态管理
- 使用 Pinia 管理用户登录状态
- Store 位置：`src/store/modules/user.ts`
- 支持用户信息持久化存储

### 2. 导航栏组件
- 组件位置：`src/components/TabBar.vue`
- 根据登录状态动态显示不同的导航项
- 自动处理主包和分包页面的跳转逻辑

### 3. 分包配置
- 用户分包：`src/user/` 目录
- 商户分包：`src/merchant/` 目录
- 配置文件：`src/pages.json`

### 4. 路由逻辑
- 主包页面使用 `uni.switchTab()`
- 分包页面使用 `uni.navigateTo()`
- 商户登录后自动重定向到商户系统

## 文件结构

```
src/
├── components/
│   └── TabBar.vue              # 动态导航栏组件
├── pages/
│   ├── index/index.vue         # 主包首页
│   ├── login/login.vue         # 登录页面
│   └── mine/profile.vue        # 主包我的页面
├── user/                       # 用户分包
│   └── pages/
│       ├── category/category.vue
│       ├── logistics/logistics.vue
│       └── mine/mine.vue
├── merchant/                   # 商户分包
│   └── pages/
│       ├── index/index.vue
│       ├── orders/orders.vue
│       └── products/products.vue
└── store/
    └── modules/user.ts         # 用户状态管理
```

## 使用说明

1. 启动项目后，默认显示首页和我的两个导航标签
2. 点击"我的"进入登录页面
3. 使用测试账号登录：
   - 普通用户：导航栏变为四个标签，可访问分包页面
   - 商户用户：自动跳转到商户管理系统
4. 在各自的"我的"页面可以退出登录，返回初始状态