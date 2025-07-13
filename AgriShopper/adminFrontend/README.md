# 农产品商城管理系统

## 项目简介
这是一个基于Vue 3 + Element Plus的农产品商城后台管理系统。

## 功能特性
- 商品管理（增删改查）
- 商品搜索
- 分页显示
- 响应式设计

## 技术栈
- Vue 3
- Element Plus
- Vue Router
- Axios

## 安装和运行

### 安装依赖
```bash
npm install
```

### 启动开发服务器
```bash
npm run serve
```

### 构建生产版本
```bash
npm run build
```

## 项目结构
```
src/
├── api/          # API接口
├── router/       # 路由配置
├── utils/        # 工具函数
├── views/        # 页面组件
├── App.vue       # 根组件
└── main.js       # 入口文件
```

## 接口说明
项目默认代理到 `http://localhost:8080`，确保后端服务已启动。

## 开发说明
- 使用Vue 3 Composition API
- 使用Element Plus组件库
- 支持热重载
- 配置了ESLint代码规范 