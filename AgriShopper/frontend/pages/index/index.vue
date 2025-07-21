<template>
  <view class="page-container">
    <!-- 固定头部 -->
    <view class="fixed-header">
      <!-- 搜索栏和用户入口 -->
      <view class="search-container">
        <view class="search-header">
          <view class="search-box" @click="goToSearch">
            <image :src="getImageUrl('icon/4.png')" class="search-icon" mode="aspectFit"></image>
            <input type="text" placeholder="搜索您想要的产品名称" class="search-input" disabled />
            <view class="search-btn cursor-pointer">搜索</view>
          </view>
          <view class="user-avatar" @click="handleUserClick">
            <!-- 未登录状态 -->
            <view v-if="!isLoggedIn" class="avatar-container">
              <image :src="userAvatar" mode="aspectFill" class="avatar-img" @error="handleAvatarError" />
            </view>
            
            <!-- 已登录状态 -->
            <view v-else class="avatar-container logged-in">
              <image :src="userAvatar" mode="aspectFill" class="avatar-img" @error="handleAvatarError" />
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 可滚动内容区域 -->
    <scroll-view 
      class="scrollable-content" 
      scroll-y="true"
      refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <!-- 轮播图 -->
      <view class="swiper-container">
        <swiper class="swiper" circular :current="currentSwiper" autoplay interval="4000" duration="500" @change="onSwiperChange">
          <swiper-item v-for="(item, index) in bannerList" :key="index">
            <image :src="item.imageUrl" mode="aspectFill" class="swiper-image" />
          </swiper-item>
        </swiper>
        <view class="swiper-dots">
          <view 
            v-for="(item, index) in bannerList" 
            :key="index" 
            :class="['dot', currentSwiper === index ? 'active' : '']"
          ></view>
        </view>
        <view class="swiper-arrow left cursor-pointer" @click="prevSwiper">
          <view class="arrow-circle">
            <image :src="getImageUrl('icon/1.png')" class="arrow-icon left-arrow" mode="aspectFit"></image>
          </view>
        </view>
        <view class="swiper-arrow right cursor-pointer" @click="nextSwiper">
          <view class="arrow-circle">
            <image :src="getImageUrl('icon/3.png')" class="arrow-icon right-arrow" mode="aspectFit"></image>
          </view>
        </view>
      </view>

      <!-- 推荐商品标题 -->
      <view class="recommend-title">
        <text>推荐商品</text>
        <text v-if="recommendationType === 'personalized'" class="recommendation-type personalized">为您推荐</text>
        <text v-else-if="recommendationType === 'popular'" class="recommendation-type popular">热门推荐</text>
        <text v-else class="recommendation-type default">精选商品</text>
        <text v-if="productsLoading" class="loading-text">加载中...</text>
      </view>

      <!-- 加载状态 -->
      <view v-if="productsLoading" class="loading-container">
        <view class="loading-spinner"></view>
        <text class="loading-text">正在加载商品...</text>
      </view>
      
      <!-- 商品网格 -->
      <view v-else class="product-grid">
        <view 
          v-for="(product, index) in products" 
          :key="product.id || index" 
          class="product-card cursor-pointer"
          @click="goToProductDetail(product)"
        >
          <image 
            :src="product.imageUrl" 
            mode="aspectFill" 
            class="product-image"
            @error="handleImageError"
          />
          <view class="product-info">
            <view class="product-header">
              <text class="product-name">{{ product.name }}</text>
              <view class="product-tags">
                <text v-if="product.isHotProduct" class="hot-tag">热销</text>
                <text v-if="product.isNewProduct" class="new-tag">新品</text>
              </view>
            </view>
            <text class="product-desc">{{ product.description }}</text>
            <view class="product-bottom">
              <view class="price-container">
                <text class="product-price">¥{{ product.price }}</text>
              </view>
              <view class="buy-btn cursor-pointer" @click.stop="addToCart(product)">购买</view>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 空状态 -->
      <view v-if="!productsLoading && products.length === 0" class="empty-container">
        <text class="empty-text">暂无推荐商品</text>
      </view>
    </scroll-view>
    
    <!-- 微信登录弹窗 -->
    <WxLoginModal 
      :visible="showLoginModal" 
      @close="handleCloseLogin"
      @login-success="handleLoginSuccess"
    />
  </view>
</template>
<script setup>
import { ref, onMounted, watch } from 'vue';
import { store } from '../../store.js';
import WxLoginModal from '../../components/WxLoginModal.vue';
import env from '../../config/env.js';

// 处理图片URL - 必须在其他函数之前定义
const getImageUrl = (url) => {
  if (!url) return '/static/default-product.png';
  
  // 如果已经是完整URL，直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  
  // 使用环境配置中的baseUrl
  const config = env.getConfig();
  
  // 如果是icon图片，直接拼接后端地址
  if (url.startsWith('icon/')) {
    return config.baseUrl + '/static/' + url;
  }
  
  // 如果是tabbar图片，直接拼接后端地址
  if (url.startsWith('tabbar/')) {
    return config.baseUrl + '/' + url;
  }
  
  // 如果是Carousel轮播图，直接拼接后端地址
  if (url.startsWith('Carousel/')) {
    return config.baseUrl + '/static/' + url;
  }
  
  // 如果已经是 /static/uploads/ 开头的路径，直接拼接后端地址
  if (url.startsWith('/static/uploads/')) {
    return config.baseUrl + url;
  }
  
  // 如果是文件名，拼接完整的静态资源路径
  if (!url.startsWith('/')) {
    return config.baseUrl + '/static/uploads/' + url;
  }
  
  // 其他情况，拼接后端地址和路径
  return config.baseUrl + url;
};

// 轮播图数据
const bannerList = ref([]);

// 分类数据
const categories = ref([
  { name: '饲料' },
  { name: '农产品' },
  { name: '养生保健品' }
]);

// 推荐商品数据
const products = ref([]);
const productsLoading = ref(false);
const recommendationType = ref(''); // 推荐类型：'personalized' | 'popular' | 'default'
const recommendationStats = ref(null); // 推荐统计信息

// 当前状态
const currentSwiper = ref(0);
const currentCategory = ref(1); // 默认选中农产品

// 登录状态
const isLoggedIn = ref(false);
const showLoginModal = ref(false);
const userAvatar = ref(getImageUrl('icon/登录.png')); // 使用后端的登录图片

// 下拉刷新状态
const refreshing = ref(false);

// 轮播图切换
const onSwiperChange = (e) => {
  currentSwiper.value = e.detail.current;
};

// 上一张轮播图
const prevSwiper = () => {
  const newIndex = currentSwiper.value === 0 ? bannerList.value.length - 1 : currentSwiper.value - 1;
  currentSwiper.value = newIndex;
};

// 下一张轮播图
const nextSwiper = () => {
  const newIndex = currentSwiper.value === bannerList.value.length - 1 ? 0 : currentSwiper.value + 1;
  currentSwiper.value = newIndex;
};

// 切换分类
const changeCategory = (index) => {
  currentCategory.value = index;
};

// 获取推荐商品
const fetchRecommendProducts = async () => {
  try {
    productsLoading.value = true;
    
    const userInfo = store.getUserInfo();
    let productList = [];
    
    // 如果用户已登录，尝试获取个性化推荐
    if (userInfo && userInfo.openid) {
      try {
        const personalizedResponse = await new Promise((resolve, reject) => {
          uni.request({
            url: env.getApiUrl(`/user-behavior/recommendations/${userInfo.id || 1}?limit=8`),
            method: 'GET',
            success: (res) => {
              resolve(res);
            },
            fail: (err) => {
              reject(err);
            }
          });
        });
        
        if (personalizedResponse.statusCode === 200 && personalizedResponse.data.code === 200) {
          // 获取个性化推荐的商品编码列表
          const recommendedProductCodes = personalizedResponse.data.data || [];
          console.log('个性化推荐商品编码:', recommendedProductCodes);
          
          // 根据推荐的商品编码获取商品详情
          if (recommendedProductCodes.length > 0) {
            for (const productCode of recommendedProductCodes) {
              try {
                const productResponse = await new Promise((resolve, reject) => {
                  uni.request({
                    url: env.getApiUrl(`/products?productCode=${productCode}&page=0&size=1`),
                    method: 'GET',
                    success: (res) => {
                      resolve(res);
                    },
                    fail: (err) => {
                      reject(err);
                    }
                  });
                });
                
                if (productResponse.statusCode === 200 && productResponse.data.code === 200) {
                  const products = productResponse.data.data.content || [];
                  if (products.length > 0) {
                    productList.push(products[0]);
                  }
    }
  } catch (error) {
                console.error('获取推荐商品详情失败:', error);
              }
            }
          }
          
          if (productList.length > 0) {
            recommendationType.value = 'personalized';
  }
        }
      } catch (error) {
        console.error('获取个性化推荐失败:', error);
      }
    }
    
    // 如果个性化推荐为空或失败，获取热门推荐
    if (productList.length === 0) {
      try {
                const hotResponse = await new Promise((resolve, reject) => {
          uni.request({
            url: env.getApiUrl('/user-behavior/popular?limit=8&days=7'),
            method: 'GET',
            success: (res) => {
              resolve(res);
            },
            fail: (err) => {
              reject(err);
            }
          });
        });
        
        if (hotResponse.statusCode === 200 && hotResponse.data.code === 200) {
          const hotProductCodes = hotResponse.data.data || [];
          console.log('热门推荐商品编码:', hotProductCodes);

          // 根据热门商品编码获取商品详情
          for (const productCode of hotProductCodes) {
            try {
              const productResponse = await new Promise((resolve, reject) => {
                uni.request({
                  url: env.getApiUrl(`/products?productCode=${productCode}&page=0&size=1`),
                  method: 'GET',
                  success: (res) => {
                    resolve(res);
                  },
                  fail: (err) => {
                    reject(err);
                  }
                });
              });
              
              if (productResponse.statusCode === 200 && productResponse.data.code === 200) {
                const products = productResponse.data.data.content || [];
                if (products.length > 0) {
                  productList.push(products[0]);
                }
              }
            } catch (error) {
              console.error('获取热门商品详情失败:', error);
            }
          }
          
                    if (productList.length > 0) {
            recommendationType.value = 'popular';
          }
        }
      } catch (error) {
        console.error('获取热门推荐失败:', error);
      }
    }

    // 如果推荐商品数量不足8个，使用基于销量和库存的默认推荐
    if (productList.length < 8) {
      try {
        const defaultResponse = await new Promise((resolve, reject) => {
          uni.request({
            url: env.getApiUrl('/user-behavior/default-recommendations?limit=8'),
            method: 'GET',
            success: (res) => {
              resolve(res);
            },
            fail: (err) => {
              reject(err);
            }
          });
        });
        
        if (defaultResponse.statusCode === 200 && defaultResponse.data.code === 200) {
          const defaultProductCodes = defaultResponse.data.data || [];
          console.log('默认推荐商品编码:', defaultProductCodes);

          // 获取已推荐的商品编码，避免重复
          const existingProductCodes = new Set(productList.map(p => p.productCode));

          // 根据默认推荐的商品编码获取商品详情
          for (const productCode of defaultProductCodes) {
            if (!existingProductCodes.has(productCode)) { // 避免重复
              try {
                const productResponse = await new Promise((resolve, reject) => {
                  uni.request({
                    url: env.getApiUrl(`/products?productCode=${productCode}&page=0&size=1`),
                    method: 'GET',
                    success: (res) => {
                      resolve(res);
                    },
                    fail: (err) => {
                      reject(err);
                    }
                  });
                });
                
                if (productResponse.statusCode === 200 && productResponse.data.code === 200) {
                  const products = productResponse.data.data.content || [];
                  if (products.length > 0) {
                    productList.push(products[0]);
                  }
                }
              } catch (error) {
                console.error('获取默认推荐商品详情失败:', error);
              }
            }
          }
          
          if (productList.length > 0 && recommendationType.value !== 'personalized' && recommendationType.value !== 'popular') {
            recommendationType.value = 'sales-stock';
          }
        }
      } catch (error) {
        console.error('获取默认推荐失败:', error);
      }
    }

    // 如果推荐商品数量仍然不足8个，使用普通商品列表补充
    if (productList.length < 8) {
      try {
        const response = await new Promise((resolve, reject) => {
          uni.request({
            url: env.getApiUrl('/products?page=0&size=8'),
            method: 'GET',
            success: (res) => {
              resolve(res);
            },
            fail: (err) => {
              reject(err);
            }
          });
        });
        
        if (response.statusCode === 200 && response.data.code === 200) {
          const allProducts = response.data.data.content || [];
          
          // 获取已推荐的商品编码，避免重复
          const existingProductCodes = new Set(productList.map(p => p.productCode));
          
          // 补充商品（不限制到8个，有多少补充多少）
          for (const product of allProducts) {
            if (!existingProductCodes.has(product.productCode)) { // 避免重复
              productList.push(product);
            }
          }
          
          if (productList.length > 0 && recommendationType.value !== 'personalized' && recommendationType.value !== 'popular' && recommendationType.value !== 'sales-stock') {
            recommendationType.value = 'default';
          }
        }
      } catch (error) {
        console.error('获取普通商品列表失败:', error);
      }
    }

    // 如果所有推荐都失败，使用默认商品数据
    if (productList.length === 0) {
      console.error('所有推荐系统都失败，使用默认商品数据');
      loadDefaultProducts();
      recommendationType.value = 'default';
      return;
    }
    
      // 转换数据格式以适配前端显示
      products.value = productList.map(product => ({
        id: product.id,
        name: product.productName,
        description: product.productDescription || '暂无描述',
        price: product.productPrice,
        imageUrl: getImageUrl(product.mainImageUrl),
        productCode: product.productCode,
        stockQuantity: product.stockQuantity,
        isHotProduct: product.isHotProduct,
        isNewProduct: product.isNewProduct
      }));
      
      console.log('推荐商品加载成功:', products.value);
    console.log('推荐类型:', recommendationType.value);
    
    // 记录用户查看首页推荐商品的行为
    recordViewRecommendPage();
    
  } catch (error) {
    console.error('获取推荐商品出错:', error);
    // 如果网络错误，使用默认数据
    loadDefaultProducts();
    recommendationType.value = 'default';
  } finally {
    productsLoading.value = false;
  }
};

// 获取推荐统计信息
const fetchRecommendationStats = async () => {
  try {
    const userInfo = store.getUserInfo();
    if (!userInfo || !userInfo.openid) {
      return;
    }
    
    const response = await new Promise((resolve, reject) => {
      uni.request({
        url: env.getApiUrl(`/user-behavior/statistics/${userInfo.id || 1}`),
        method: 'GET',
        success: (res) => {
          resolve(res);
        },
        fail: (err) => {
          reject(err);
        }
      });
    });
    
    if (response.statusCode === 200 && response.data.code === 200) {
      recommendationStats.value = response.data.data;
      console.log('推荐统计信息:', recommendationStats.value);
    }
  } catch (error) {
    console.error('获取推荐统计信息失败:', error);
  }
};

// 记录用户查看首页推荐商品的行为
const recordViewRecommendPage = async () => {
  try {
    const userInfo = store.getUserInfo();
    if (!userInfo || !userInfo.openid) {
      console.log('用户未登录，跳过行为记录');
      return;
    }
    
    // 记录查看推荐页面行为
    await uni.request({
      url: env.getApiUrl('/user-behavior/view-page'),
      method: 'POST',
      data: {
        userId: userInfo.id || 1, // 默认使用用户ID=1
        pagePath: '/pages/index/index',
        pageTitle: '首页推荐',
        sourcePage: 'home'
      }
    });
    
    console.log('记录查看推荐页面行为成功');
  } catch (error) {
    console.error('记录查看推荐页面行为失败:', error);
  }
};

// 记录用户点击商品的行为
const recordClickProduct = async (product) => {
  try {
    const userInfo = store.getUserInfo();
    if (!userInfo || !userInfo.openid) {
      console.log('用户未登录，跳过行为记录');
      return;
    }
    
    // 获取当前页面路径
    const pages = getCurrentPages();
    const currentPage = pages[pages.length - 1];
    const sourcePage = currentPage ? currentPage.route : 'unknown';
    
    // 记录点击商品行为
    await uni.request({
      url: env.getApiUrl('/user-behavior/click-product'),
      method: 'POST',
      data: {
        userId: userInfo.id || 1, // 默认使用用户ID=1
        productCode: product.productCode,
        productName: product.name,
        sourcePage: sourcePage
      }
    });
    
    console.log('记录点击商品行为成功:', product.name, '来源页面:', sourcePage);
  } catch (error) {
    console.error('记录点击商品行为失败:', error);
  }
};

// 记录用户查看商品详情的行为
const recordViewProduct = async (product) => {
  try {
    const userInfo = store.getUserInfo();
    if (!userInfo || !userInfo.openid) {
      console.log('用户未登录，跳过行为记录');
      return;
    }
    
    // 获取当前页面路径
    const pages = getCurrentPages();
    const currentPage = pages[pages.length - 1];
    const sourcePage = currentPage ? currentPage.route : 'unknown';
    
    // 记录查看商品行为
    await uni.request({
      url: env.getApiUrl('/user-behavior/view-product'),
      method: 'POST',
      data: {
        userId: userInfo.id || 1, // 默认使用用户ID=1
        productCode: product.productCode,
        productName: product.name,
        sourcePage: sourcePage
      }
    });
    
    console.log('记录查看商品行为成功:', product.name, '来源页面:', sourcePage);
  } catch (error) {
    console.error('记录查看商品行为失败:', error);
  }
};

// 记录用户搜索行为
const recordSearchBehavior = async () => {
  try {
    const userInfo = store.getUserInfo();
    if (!userInfo || !userInfo.openid) {
      console.log('用户未登录，跳过行为记录');
      return;
    }
    
    // 获取当前页面路径
    const pages = getCurrentPages();
    const currentPage = pages[pages.length - 1];
    const sourcePage = currentPage ? currentPage.route : 'unknown';
    
    // 记录搜索行为（搜索框点击）
    await uni.request({
      url: env.getApiUrl('/user-behavior/search'),
      method: 'POST',
      data: {
        userId: userInfo.id || 1, // 默认使用用户ID=1
        keyword: '搜索框点击',
        sourcePage: sourcePage
      }
    });
    
    console.log('记录搜索行为成功，来源页面:', sourcePage);
  } catch (error) {
    console.error('记录搜索行为失败:', error);
  }
};



// 初始化轮播图数据
const initBannerList = () => {
  return new Promise((resolve) => {
    bannerList.value = [
      {
        imageUrl: getImageUrl('Carousel/siliao.jpg')
      },
      {
        imageUrl: getImageUrl('Carousel/shucai.jpg')
      },
      {
        imageUrl: getImageUrl('Carousel/baojian.jpg')
      }
    ];
    resolve();
  });
};

// 加载默认商品数据（当API调用失败时使用）
const loadDefaultProducts = () => {
  products.value = [
    {
      id: 1,
      name: '有机红薯',
      description: '农家自种，无公害种植',
      price: 12.8,
      imageUrl: '/static/default-product.png'
    },
    {
      id: 2,
      name: '优质玉米饲料',
      description: '高营养，适合家禽喂养',
      price: 45.9,
      imageUrl: '/static/default-product.png'
    },
    {
      id: 3,
      name: '枸杞菊花茶',
      description: '养生保健，明目润肺',
      price: 38.5,
      imageUrl: '/static/default-product.png'
    },
    {
      id: 4,
      name: '新鲜胡萝卜',
      description: '富含胡萝卜素，助力健康',
      price: 8.8,
      imageUrl: '/static/default-product.png'
    },
    {
      id: 5,
      name: '有机白菜',
      description: '新鲜采摘，绿色健康',
      price: 6.5,
      imageUrl: '/static/default-product.png'
    },
    {
      id: 6,
      name: '土鸡蛋',
      description: '散养土鸡，营养丰富',
      price: 15.9,
      imageUrl: '/static/default-product.png'
    },
    {
      id: 7,
      name: '蜂蜜',
      description: '纯天然野生蜂蜜',
      price: 68.0,
      imageUrl: '/static/default-product.png'
    },
    {
      id: 8,
      name: '有机大米',
      description: '东北黑土地种植',
      price: 25.8,
      imageUrl: '/static/default-product.png'
    }
  ];
};

// 跳转到搜索页面
const goToSearch = () => {
  // 记录搜索行为
  recordSearchBehavior();
  
  uni.navigateTo({
    url: '/pages/searchProduct/searchProduct'
  });
};

// 检查登录状态
const checkLoginStatus = () => {
  try {
    const userInfo = store.getUserInfo();
    if (userInfo && userInfo.openid) {
      isLoggedIn.value = true;
      // 设置用户头像
      if (userInfo.avatar) {
        userAvatar.value = userInfo.avatar;
      } else {
        userAvatar.value = '/static/tabbar/user.png'; // 默认头像
      }
    } else {
      isLoggedIn.value = false;
      userAvatar.value = getImageUrl('icon/登录.png'); // 未登录时显示登录图片
    }
  } catch (error) {
    console.log('检查登录状态失败:', error);
    isLoggedIn.value = false;
    userAvatar.value = getImageUrl('icon/登录.png'); // 出错时也显示登录图片
  }
};

// 处理用户点击
const handleUserClick = () => {
  if (isLoggedIn.value) {
    // 已登录，跳转到个人中心
    uni.showToast({
      title: '欢迎回来',
      icon: 'none',
      duration: 1500
    });
    
    setTimeout(() => {
      uni.switchTab({
        url: '/pages/my/my'
      });
    }, 500);
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
  
  // 登录成功后刷新推荐商品
  fetchRecommendProducts();
  fetchRecommendationStats();
  
  // 显示登录成功提示
  uni.showToast({
    title: '登录成功',
    icon: 'success',
    duration: 2000
  });
};

// 跳转到商品详情
const goToProductDetail = (product) => {
  // 记录点击商品行为
  recordClickProduct(product);
  
  // 记录查看商品行为
  recordViewProduct(product);
  
  uni.navigateTo({
    url: `/pages/productDetail/productDetail?id=${product.id}`
  });
};

// 购买按钮点击处理
const addToCart = (product) => {
  // 记录点击商品行为
  recordClickProduct(product);
  
  // 跳转到商品详情页面
  uni.navigateTo({
    url: `/pages/productDetail/productDetail?id=${product.id}`
  });
};

// 处理图片加载错误
const handleImageError = (e) => {
  // 设置默认图片
  e.target.src = '/static/default-product.png';
};

// 处理头像图片加载错误
const handleAvatarError = (e) => {
  if (isLoggedIn.value) {
    // 已登录用户头像加载失败，使用默认用户头像
    e.target.src = '/static/tabbar/user.png';
  } else {
    // 未登录时登录图片加载失败，使用默认登录图标
    e.target.src = getImageUrl('icon/登录.png');
  }
};

// 页面加载时检查登录状态
onMounted(() => {
  initBannerList(); // 初始化轮播图数据
  checkLoginStatus();
  fetchRecommendProducts();
  fetchRecommendationStats();
});

// 页面显示时刷新数据
const onShow = () => {
  console.log('页面显示，刷新数据');
  checkLoginStatus();
  fetchRecommendProducts();
  fetchRecommendationStats();
};

// 下拉刷新处理
const onRefresh = async () => {
  console.log('触发下拉刷新');
  refreshing.value = true;
  
  try {
    // 刷新所有数据
    await Promise.all([
      initBannerList(),
      fetchRecommendProducts(),
      fetchRecommendationStats()
    ]);
    
    checkLoginStatus();
    
    uni.showToast({
      title: '刷新成功',
      icon: 'success',
      duration: 1500
    });
  } catch (error) {
    console.error('刷新失败:', error);
    uni.showToast({
      title: '刷新失败',
      icon: 'error',
      duration: 1500
    });
  } finally {
    refreshing.value = false;
  }
};

// 监听store中的刷新触发器
watch(() => store.state.refreshTrigger, (newVal, oldVal) => {
  if (newVal !== oldVal) {
    console.log('检测到用户信息更新，刷新页面数据');
    checkLoginStatus();
    fetchRecommendProducts();
    fetchRecommendationStats();
  }
});

// 导出页面生命周期函数
defineExpose({
  onShow
});
</script>
<style>
.page-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f6f8fa;
  font-family: "PingFang SC", "Helvetica Neue", Helvetica, Arial, sans-serif;
  overflow: hidden;
}

.fixed-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: #ffffff;
  padding-bottom: 10rpx; /* 减少底部padding */
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}

.scrollable-content {
  flex: 1;
  overflow-y: auto;
  padding-top: 160rpx; /* 为固定头部留出足够空间，避免遮挡 */
  padding-bottom: 40rpx; /* 适当增加底部间距，让内容更舒适 */
}

/* 搜索栏样式优化 */
.search-container {
  padding: 40rpx 30rpx 20rpx 30rpx;
  background: #fff;
  border-bottom-left-radius: 32rpx;
  border-bottom-right-radius: 32rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.03);
}

.search-header {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.user-avatar {
  position: relative;
  cursor: pointer;
}

.avatar-container {
  display: flex;
  align-items: center;
  transition: all 0.3s ease;
}

.avatar-container:hover {
  transform: translateY(-2rpx);
}

.avatar-container.logged-in {
  /* 已登录状态保持简洁 */
}

.avatar-container.logged-in:hover {
  transform: translateY(-2rpx);
}

.avatar-img {
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  flex-shrink: 0;
}



.login-badge {
  font-size: 24rpx;
  font-weight: 600;
  color: #4CAF50;
  background: rgba(76,175,80,0.1);
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
  border: 1rpx solid rgba(76,175,80,0.2);
}
.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  height: 80rpx;
  background-color: #f3f5f7;
  border-radius: 40rpx;
  padding: 0 20rpx;
  box-shadow: none;
}

.search-icon {
  width: 40rpx;
  height: 40rpx;
  flex-shrink: 0;
}
.search-input {
  flex: 1;
  height: 80rpx;
  font-size: 32rpx;
  margin-left: 20rpx;
  background: transparent;
  border: none;
  outline: none;
}
.search-btn {
  padding: 0 36rpx;
  height: 60rpx;
  line-height: 60rpx;
  background: linear-gradient(90deg, #4CAF50 60%, #43a047 100%);
  color: #fff;
  border-radius: 30rpx;
  font-size: 28rpx;
  font-weight: 600;
  margin-left: 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(76,175,80,0.08);
}

/* 轮播图样式优化 */
.swiper-container {
  position: relative;
  width: calc(100% - 60rpx); /* 左右各留30rpx间距 */
  height: 420rpx;
  border-radius: 32rpx;
  overflow: hidden;
  margin: 30rpx 30rpx 28rpx 30rpx; /* 增加顶部间距，确保不被头部遮挡 */
  box-shadow: 0 4rpx 24rpx rgba(76,175,80,0.06);
  background: #fff;
}
.swiper {
  width: 100%;
  height: 100%;
}
.swiper-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}


.swiper-dots {
  position: absolute;
  bottom: 24rpx;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  z-index: 10;
}
.dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background-color: rgba(76,175,80,0.18);
  margin: 0 8rpx;
  transition: all 0.2s;
}
.dot.active {
  width: 32rpx;
  background: linear-gradient(90deg, #4CAF50 60%, #43a047 100%);
}
.swiper-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 10;
}

.arrow-circle {
  width: 80rpx;
  height: 80rpx;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.15);
  transition: all 0.3s ease;
  backdrop-filter: blur(10rpx);
}

.arrow-circle:active {
  background-color: rgba(255, 255, 255, 1);
  box-shadow: 0 6rpx 20rpx rgba(0,0,0,0.2);
  transform: scale(0.95);
}
.swiper-arrow.left {
  left: 30rpx;
}
.swiper-arrow.right {
  right: 30rpx;
}

.arrow-icon {
  width: 40rpx;
  height: 40rpx;
}

.left-arrow {
  transform: rotate(-270deg);
}

.right-arrow {
  transform: rotate(90deg);
}

/* 推荐商品标题优化 */
.recommend-title {
  padding: 20rpx 30rpx 15rpx 30rpx; /* 减少顶部padding，因为轮播图已经有间距 */
  font-size: 38rpx;
  font-weight: bold;
  color: #222;
  background: transparent;
  letter-spacing: 2rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.recommendation-type {
  font-size: 24rpx;
  padding: 6rpx 12rpx;
  border-radius: 12rpx;
  font-weight: normal;
}

.recommendation-type.personalized {
  background: linear-gradient(90deg, #4CAF50 60%, #43a047 100%);
  color: #fff;
}

.recommendation-type.popular {
  background: linear-gradient(90deg, #ff9800 60%, #f57c00 100%);
  color: #fff;
}

.recommendation-type.default {
  background: #f0f0f0;
  color: #666;
}

/* 推荐统计信息样式 */
.recommendation-stats {
  display: flex;
  justify-content: space-around;
  padding: 20rpx 30rpx;
  background: #fff;
  margin: 0 30rpx 20rpx 30rpx;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05);
}

.stats-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.stats-label {
  font-size: 24rpx;
  color: #666;
}

.stats-value {
  font-size: 32rpx;
  font-weight: bold;
  color: #4CAF50;
}

/* 商品网格样式优化 */
.product-grid {
  display: flex;
  flex-wrap: wrap;
  padding: 0 15rpx 20rpx 15rpx; /* 增加左右和底部padding，让布局更舒适 */
  background: transparent;
  justify-content: space-between;
  gap: 24rpx; /* 增加商品卡片之间的间距 */
}
.product-card {
  width: calc(50% - 12rpx); /* 调整宽度以适应新的间距 */
  margin: 0;
  background: #fff;
  border-radius: 24rpx; /* 增加圆角，让卡片更美观 */
  overflow: hidden;
  box-shadow: 0 4rpx 20rpx rgba(76,175,80,0.08); /* 增强阴影效果 */
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease; /* 增强过渡效果 */
}
.product-card:hover {
  box-shadow: 0 8rpx 32rpx rgba(76,175,80,0.15);
  transform: translateY(-4rpx); /* 添加悬停时的上移效果 */
}

.product-card:hover .product-image {
  transform: scale(1.05); /* 悬停时图片轻微放大 */
}
.product-image {
  width: 100%;
  height: 380rpx; /* 增加图片高度，让商品图片更突出 */
  background: #f8f8f8;
  object-fit: cover;
  transition: transform 0.3s ease; /* 添加图片缩放过渡效果 */
}
.product-info {
  padding: 28rpx 20rpx 20rpx 20rpx; /* 增加内边距，让内容更舒适 */
  display: flex;
  flex-direction: column;
  flex: 1;
}
.product-name {
  font-size: 34rpx; /* 增加字体大小 */
  font-weight: 600;
  color: #222;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-bottom: 8rpx; /* 增加底部间距 */
}
.product-desc {
  font-size: 28rpx; /* 增加字体大小 */
  color: #8e8e8e;
  margin-top: 8rpx;
  line-height: 1.5; /* 增加行高 */
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 16rpx; /* 增加底部间距 */
}
.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto; /* 让价格和按钮自动推到底部 */
  padding-top: 16rpx; /* 增加顶部间距 */
}
.price-container {
  display: flex;
  align-items: center;
}
.product-price {
  font-size: 38rpx; /* 增加价格字体大小 */
  font-weight: bold;
  color: #4CAF50;
}
.buy-btn {
  padding: 12rpx 32rpx; /* 增加按钮内边距 */
  background: linear-gradient(90deg, #4CAF50 60%, #43a047 100%);
  color: #fff;
  border-radius: 32rpx; /* 增加圆角 */
  font-size: 28rpx; /* 增加字体大小 */
  font-weight: 500;
  box-shadow: 0 4rpx 12rpx rgba(76,175,80,0.12); /* 增强阴影 */
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.buy-btn:active {
  background: #388e3c;
  transform: scale(0.95);
  box-shadow: 0 1rpx 4rpx rgba(76,175,80,0.12);
}

.buy-btn::before {
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

.buy-btn:active::before {
  width: 100rpx;
  height: 100rpx;
}

/* 加载状态样式 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60rpx 0;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid #f3f3f3;
  border-top: 4rpx solid #4CAF50;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20rpx;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  font-size: 28rpx;
  color: #999;
}

/* 空状态样式 */
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 0;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

/* 商品标签样式 */
.product-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8rpx;
}

.product-tags {
  display: flex;
  gap: 8rpx;
}

.hot-tag, .new-tag {
  font-size: 20rpx;
  padding: 4rpx 8rpx;
  border-radius: 8rpx;
  color: #fff;
}

.hot-tag {
  background-color: #ff4444;
}

.new-tag {
  background-color: #4CAF50;
}

.cursor-pointer {
  cursor: pointer;
}
</style>
