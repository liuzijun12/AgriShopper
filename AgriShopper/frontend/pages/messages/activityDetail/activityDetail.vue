<template>
  <view class="activity-detail-container">
    <!-- 活动头图/视频 -->
    <view class="activity-header-media">
      <image v-if="activity.banner" class="activity-banner" :src="activity.banner" mode="aspectFill" @error="onImageError('banner', activity)"></image>
      <video v-if="activity.video" class="activity-video" :src="activity.video" controls></video>
      <view class="activity-status-overlay" :class="activity.statusClass">{{ activity.status }}</view>
    </view>

    <!-- 活动标题与核心利益点 -->
    <view class="card activity-overview">
      <text class="activity-title">{{ activity.title }}</text>
      <text class="core-benefit">{{ activity.coreBenefit }}</text>
    </view>

    <!-- 活动时间 -->
    <view class="card activity-time-section">
      <view class="card-title-section">
        <text class="card-title">活动时间</text>
      </view>
      <view class="time-item">
        <text class="label">起止时间：</text>
        <text class="value">{{ activity.time.start }} 至 {{ activity.time.end }}</text>
      </view>
      <view v-if="activity.time.phases && activity.time.phases.length > 0">
        <view v-for="(phase, index) in activity.time.phases" :key="index" class="time-item phase-item">
          <text class="label">{{ phase.name }}：</text>
          <text class="value">{{ phase.start }} 至 {{ phase.end }}</text>
        </view>
      </view>
      <view v-if="activity.countdown" class="countdown-display">
        <text class="countdown-icon">⏰</text>
        <text class="countdown-text">距结束还剩：<text class="countdown-value">{{ activity.countdown }}</text></text>
      </view>
    </view>

    <!-- 活动对象 -->
    <view class="card activity-audience-section">
      <view class="card-title-section">
        <text class="card-title">活动对象</text>
      </view>
      <text class="audience-text">{{ activity.audience }}</text>
    </view>

    <!-- 活动规则与详情 -->
    <view class="card activity-rules-section">
      <view class="card-title-section">
        <text class="card-title">活动规则与详情</text>
      </view>
      <view class="rule-block">
        <text class="rule-subtitle">参与方式：</text>
        <text class="rule-content">{{ activity.rules.participation }}</text>
      </view>

      <view v-if="activity.rules.offers && activity.rules.offers.length > 0" class="rule-block">
        <text class="rule-subtitle">优惠内容：</text>
        <view v-for="(offer, index) in activity.rules.offers" :key="'offer-' + index" class="rule-content-item">
          <text class="dot">•</text>
          <text>{{ offer.type }}：{{ offer.description }}</text>
        </view>
      </view>

      <view v-if="activity.rules.coupons && activity.rules.coupons.length > 0" class="rule-block">
        <text class="rule-subtitle">优惠券：</text>
        <view v-for="(coupon, index) in activity.rules.coupons" :key="'coupon-' + index" class="coupon-item">
          <view class="coupon-left">
            <text class="coupon-value">￥{{ coupon.value }}</text>
            <text class="coupon-name">{{ coupon.name }}</text>
          </view>
          <view class="coupon-right">
            <text class="coupon-threshold">{{ coupon.threshold }}</text>
            <text class="coupon-scope">适用范围：{{ coupon.scope }}</text>
            <text class="coupon-limit">限制：{{ coupon.limit }}</text>
            <text class="coupon-expiry">有效期至：{{ coupon.expiry }}</text>
            <button v-if="!coupon.received" class="coupon-action-button" @tap="receiveCoupon(coupon.name)">立即领取</button>
            <text v-else class="coupon-received">已领取</text>
          </view>
        </view>
      </view>

      <view v-if="activity.rules.subsidy" class="rule-block">
        <text class="rule-subtitle">补贴类：</text>
        <text class="rule-content">{{ activity.rules.subsidy.description }}</text>
      </view>

      <view v-if="activity.rules.rewards" class="rule-block">
        <text class="rule-subtitle">奖励类：</text>
        <text class="rule-content">{{ activity.rules.rewards.description }}</text>
      </view>

      <view v-if="activity.rules.groupBuy" class="rule-block">
        <text class="rule-subtitle">拼团/集采：</text>
        <text class="rule-content">{{ activity.rules.groupBuy.description }}</text>
      </view>

      <view v-if="activity.rules.restrictions && activity.rules.restrictions.length > 0" class="rule-block">
        <text class="rule-subtitle">限制条件：</text>
        <view v-for="(restriction, index) in activity.rules.restrictions" :key="'res-' + index" class="rule-content-item">
          <text class="dot">•</text>
          <text>{{ restriction }}</text>
        </view>
      </view>

      <view v-if="activity.rules.flow && activity.rules.flow.length > 0" class="rule-block">
        <text class="rule-subtitle">活动流程/步骤：</text>
        <view v-for="(step, index) in activity.rules.flow" :key="'flow-' + index" class="flow-item">
          <text class="flow-step-number">{{ step.step }}</text>
          <text class="flow-step-description">{{ step.description }}</text>
        </view>
      </view>
    </view>

    <!-- 活动商品/供应商推荐 -->
    <view v-if="activity.recommendedProducts && activity.recommendedProducts.length > 0" class="card recommended-section">
      <view class="card-title-section">
        <text class="card-title">活动商品推荐</text>
      </view>
      <view class="product-list">
        <view v-for="product in activity.recommendedProducts" :key="product.id" class="product-card">
          <image class="product-img" :src="product.image" mode="aspectFill" @error="onImageError('product', product)"></image>
          <view class="product-info">
            <text class="product-name">{{ product.name }}</text>
            <text class="product-spec">{{ product.spec }}</text>
            <view class="price-row">
              <text class="activity-price">￥{{ product.activityPrice }}</text>
              <text class="original-price">￥{{ product.originalPrice }}</text>
              <text class="discount-tag">{{ product.discount }}</text>
            </view>
            <button class="product-action-button" @tap="goToProductDetail(product.id)">{{ product.action }}</button>
          </view>
        </view>
      </view>
    </view>

    <view v-if="activity.recommendedSuppliers && activity.recommendedSuppliers.length > 0" class="card recommended-section">
      <view class="card-title-section">
        <text class="card-title">合作供应商</text>
      </view>
      <view class="supplier-list">
        <view v-for="supplier in activity.recommendedSuppliers" :key="supplier.id" class="supplier-card">
          <image class="supplier-logo" :src="supplier.logo" mode="aspectFill"></image>
          <view class="supplier-info">
            <text class="supplier-name">{{ supplier.name }}</text>
            <text class="supplier-stats">{{ supplier.productsCount }}种商品 | 评分{{ supplier.rating }}</text>
          </view>
          <button class="supplier-action-button" @tap="goToSupplierStore(supplier.id)">{{ supplier.action }}</button>
        </view>
      </view>
    </view>

    <!-- 常见问题解答 -->
    <view v-if="activity.faq && activity.faq.length > 0" class="card faq-section">
      <view class="card-title-section">
        <text class="card-title">常见问题解答</text>
      </view>
      <view v-for="(item, index) in activity.faq" :key="'faq-' + index" class="faq-item">
        <text class="faq-question">Q: {{ item.question }}</text>
        <text class="faq-answer">A: {{ item.answer }}</text>
      </view>
    </view>

    <!-- 主办方信息 -->
    <view v-if="activity.organizer" class="card organizer-section">
      <view class="card-title-section">
        <text class="card-title">主办方</text>
      </view>
      <text class="organizer-name">{{ activity.organizer.name }}</text>
      <button v-if="activity.organizer.contact" class="action-button small" @tap="contactOrganizer(activity.organizer.link)">{{ activity.organizer.contact }}</button>
    </view>

    <!-- 核心操作按钮 (固定在页脚) -->
    <view class="action-bar-placeholder"></view>
    <view class="action-bar">
      <button v-if="activity.actions.includes('receiveCoupon')" class="action-button primary" @tap="receiveCoupon()">立即领取</button>
      <button v-if="activity.actions.includes('viewProducts')" class="action-button primary" @tap="viewActivityProducts">查看活动商品</button>
      <button v-if="activity.actions.includes('placeOrder')" class="action-button primary" @tap="placeOrder">去下单</button>
      <button v-if="activity.actions.includes('joinGroup')" class="action-button primary" @tap="joinGroup">一键参团</button>
      <button v-if="activity.actions.includes('initiateGroup')" class="action-button primary" @tap="initiateGroup">发起拼单</button>
      <button v-if="activity.actions.includes('register')" class="action-button primary" @tap="registerForActivity">报名参加</button>
      <button v-if="activity.actions.includes('shareActivity')" class="action-button" @tap="shareActivity">分享活动</button>
      <button v-if="activity.actions.includes('contactCS')" class="action-button" @tap="contactCustomerService">联系客服</button>
      <button v-if="activity.actions.includes('viewOtherActivities')" class="action-button" @tap="viewOtherActivities">查看其他活动</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';

// 定义响应式活动数据
const activity = ref({});

// 模拟后端活动数据
const mockActivityData = {
  'ACT20240715001': { // 限时特惠活动，进行中
    activityId: 'ACT20240715001',
    status: '进行中',
    statusClass: 'status-active',
    banner: 'https://placehold.co/750x300/4CAF50/FFFFFF?text=限时特惠',
    video: '',
    title: '【限时特惠】山东寿光黄瓜直供，今日下单每斤立减0.5元！',
    coreBenefit: '全场黄瓜每斤立减0.5元，最高可省500元！',
    time: {
      start: '2024-07-15 09:00:00',
      end: '2024-07-17 23:59:59',
      phases: []
    },
    countdown: '2天10小时',
    audience: '所有注册采购商，等级不限',
    rules: {
      participation: '无需领券，活动期间下单指定商品自动生效',
      offers: [
        { type: '折扣', description: '指定寿光黄瓜品类，每斤立减0.5元' },
        { type: '满减', description: '订单满5000元，额外再减100元（可叠加）' }
      ],
      coupons: [
        { name: '黄瓜专属50元券', value: '50', threshold: '满1000元可用', scope: '寿光黄瓜品类', limit: '每人限领1张', expiry: '2024-07-17 23:59:59', received: false }
      ],
      subsidy: null,
      rewards: null,
      groupBuy: null,
      restrictions: [
        '仅限寿光产区黄瓜',
        '每笔订单最高优惠500元',
        '优惠券不可提现，退款时按实际支付金额退回',
        '活动最终解释权归平台所有'
      ],
      flow: [
        { step: '1', description: '浏览活动商品' },
        { step: '2', description: '选择指定黄瓜下单' },
        { step: '3', description: '系统自动计算优惠' },
        { step: '4', description: '完成支付' }
      ]
    },
    recommendedProducts: [
      {
        id: 'P001',
        name: '寿光精品黄瓜',
        image: 'https://placehold.co/150x150/4CAF50/FFFFFF?text=黄瓜1',
        spec: '5kg/箱，A级',
        originalPrice: '5.00',
        activityPrice: '4.50',
        discount: '立减0.5元/斤',
        action: '去下单'
      },
      {
        id: 'P002',
        name: '寿光有机黄瓜',
        image: 'https://placehold.co/150x150/4CAF50/FFFFFF?text=黄瓜2',
        spec: '10kg/箱，特级',
        originalPrice: '8.00',
        activityPrice: '7.50',
        discount: '立减0.5元/斤',
        action: '去下单'
      }
    ],
    recommendedSuppliers: [
      {
        id: 'S001',
        name: '寿光蔬菜直销基地',
        logo: 'https://placehold.co/80x80/FFC107/FFFFFF?text=基地1',
        productsCount: '120种',
        rating: '4.9',
        action: '进入店铺'
      }
    ],
    faq: [
      { question: '优惠券如何使用？', answer: '在结算页面选择对应优惠券即可自动抵扣。' },
      { question: '退款后优惠券会返还吗？', answer: '优惠券一经使用，若发生退款则不予退还。' }
    ],
    organizer: {
      name: '农康优选平台',
      contact: '联系客服',
      link: '/pages/customerService/chat'
    },
    actions: ['viewProducts', 'shareActivity', 'contactCS']
  },
  'ACT20240801002': { // 拼团活动，预热中，有视频
    activityId: 'ACT20240801002',
    status: '预热中',
    statusClass: 'status-upcoming',
    banner: 'https://placehold.co/750x300/FFC107/FFFFFF?text=拼团预热',
    video: 'https://www.w3schools.com/html/mov_bbb.mp4', // 示例视频
    title: '【8月拼团季】新疆阿克苏苹果限时拼团，低至6折！',
    coreBenefit: '3人成团，低至6折，数量有限，速来抢购！',
    time: {
      start: '2024-08-01 10:00:00',
      end: '2024-08-07 23:59:59',
      phases: [
        { name: '预热期', start: '2024-07-25 00:00:00', end: '2024-07-31 23:59:59' },
        { name: '拼团期', start: '2024-08-01 00:00:00', end: '2024-08-07 23:59:59' }
      ]
    },
    countdown: '距开始还剩：7天',
    audience: '所有注册采购商',
    rules: {
      participation: '点击“发起拼单”或“一键参团”参与',
      offers: [],
      coupons: [],
      subsidy: null,
      rewards: null,
      groupBuy: {
        description: '3人成团，成团后享受拼团价，24小时内未成团自动退款',
        minParticipants: 3,
        validityDuration: '24小时'
      },
      restrictions: [
        '每用户限发起3个拼单',
        '拼团商品不参与其他满减活动'
      ],
      flow: [
        { step: '1', description: '选择拼团商品' },
        { step: '2', description: '发起拼单或加入他人拼单' },
        { step: '3', description: '邀请好友参团，达到成团人数' },
        { step: '4', description: '成团成功，享受优惠价' }
      ]
    },
    recommendedProducts: [
      {
        id: 'P003',
        name: '新疆阿克苏冰糖心苹果',
        image: 'https://placehold.co/150x150/FF33CC/FFFFFF?text=苹果1',
        spec: '5kg/箱，一级果',
        originalPrice: '12.00',
        activityPrice: '7.20', // 6折
        discount: '拼团价',
        action: '一键参团'
      }
    ],
    recommendedSuppliers: [],
    faq: [
      { question: '拼团失败怎么办？', answer: '拼团失败后，系统将自动退款至您的原支付账户。' }
    ],
    organizer: {
      name: '平台运营部',
      contact: '联系客服',
      link: '/pages/customerService/chat'
    },
    actions: ['joinGroup', 'initiateGroup', 'shareActivity', 'contactCS']
  },
  'ACT20240601003': { // 已结束活动，优惠券领取
    activityId: 'ACT20240601003',
    status: '已结束',
    statusClass: 'status-ended',
    banner: 'https://placehold.co/750x300/6C757D/FFFFFF?text=活动已结束',
    video: '',
    title: '【首单补贴】新用户注册首单立享8折补贴！',
    coreBenefit: '新用户首单享8折，最高补贴100元！',
    time: {
      start: '2024-06-01 00:00:00',
      end: '2024-06-30 23:59:59',
      phases: []
    },
    countdown: '',
    audience: '新注册采购商',
    rules: {
      participation: '注册后自动发放优惠券至账户',
      offers: [],
      coupons: [
        { name: '新用户首单8折券', value: '8折', threshold: '无门槛', scope: '全品类', limit: '每人限领1张', expiry: '2024-07-30 23:59:59', received: true }
      ],
      subsidy: { description: '新用户首单享8折，最高补贴100元' },
      rewards: null,
      groupBuy: null,
      restrictions: [
        '仅限首次下单用户',
        '优惠券有效期30天'
      ],
      flow: []
    },
    recommendedProducts: [],
    recommendedSuppliers: [],
    faq: [],
    organizer: {
      name: '农康优选平台',
      contact: '联系客服',
      link: '/pages/customerService/chat'
    },
    actions: ['viewOtherActivities', 'contactCS']
  }
};

// 模拟从后端获取活动详情的函数
const fetchActivityDetail = (activityId) => {
  return new Promise(resolve => {
    uni.showLoading({ title: '加载活动详情...' });
    setTimeout(() => {
      const data = mockActivityData[activityId];
      if (data) {
        resolve(data);
      } else {
        // 如果没有找到活动，可以返回一个默认状态或错误信息
        resolve({
          activityId: activityId,
          status: '活动不存在',
          statusClass: 'status-ended',
          banner: 'https://placehold.co/750x300/CCCCCC/666666?text=活动不存在',
          video: '',
          title: '活动信息加载失败',
          coreBenefit: '请检查活动ID或稍后再试',
          time: { start: 'N/A', end: 'N/A', phases: [] },
          countdown: '',
          audience: 'N/A',
          rules: {
            participation: 'N/A',
            offers: [],
            coupons: [],
            subsidy: null,
            rewards: null,
            groupBuy: null,
            restrictions: [],
            flow: []
          },
          recommendedProducts: [],
          recommendedSuppliers: [],
          faq: [],
          organizer: null,
          actions: ['viewOtherActivities', 'contactCS']
        });
      }
      uni.hideLoading();
    }, 1000); // 模拟网络延迟
  });
};

onLoad(async (options) => {
  if (options.activityId) {
    activity.value = await fetchActivityDetail(options.activityId);
  } else {
    // 默认加载一个活动，例如第一个模拟数据
    activity.value = await fetchActivityDetail('ACT20240715001');
  }
});

// 优惠券领取操作
const receiveCoupon = (couponName) => {
  uni.showToast({
    title: `已领取 ${couponName || '优惠券'}`,
    icon: 'success'
  });
  // 实际应用中会调用后端接口更新优惠券状态
  if (activity.value.rules.coupons) {
    const coupon = activity.value.rules.coupons.find(c => c.name === couponName);
    if (coupon) {
      coupon.received = true;
    }
  }
};

// 跳转到活动商品列表
const viewActivityProducts = () => {
  uni.showToast({ title: '跳转到活动商品列表', icon: 'none' });
  // uni.navigateTo({ url: `/pages/productList/productList?activityId=${activity.value.activityId}` });
};

// 直接下单
const placeOrder = () => {
  uni.showToast({ title: '跳转到下单页面', icon: 'none' });
  // uni.navigateTo({ url: `/pages/order/create?activityId=${activity.value.activityId}` });
};

// 一键参团
const joinGroup = () => {
  uni.showToast({ title: '跳转到参团页面', icon: 'none' });
  // uni.navigateTo({ url: `/pages/groupBuy/join?activityId=${activity.value.activityId}` });
};

// 发起拼单
const initiateGroup = () => {
  uni.showToast({ title: '跳转到发起拼单页面', icon: 'none' });
  // uni.navigateTo({ url: `/pages/groupBuy/initiate?activityId=${activity.value.activityId}` });
};

// 报名参加活动
const registerForActivity = () => {
  uni.showToast({ title: '报名成功', icon: 'success' });
  // 实际应用中会调用后端接口进行报名
};

// 分享活动
const shareActivity = () => {
  uni.showToast({ title: '分享活动', icon: 'none' });
  // uni.share(...)
};

// 联系客服
const contactCustomerService = () => {
  uni.showToast({ title: '联系客服', icon: 'none' });
  // uni.navigateTo({ url: '/pages/customerService/chat' });
};

// 查看其他活动
const viewOtherActivities = () => {
  uni.showToast({ title: '跳转到其他活动列表', icon: 'none' });
  // uni.navigateTo({ url: '/pages/activity/list' });
};

// 跳转到商品详情
const goToProductDetail = (productId) => {
  uni.showToast({ title: `查看商品 ${productId} 详情`, icon: 'none' });
  // uni.navigateTo({ url: `/pages/product/detail?productId=${productId}` });
};

// 跳转到供应商店铺
const goToSupplierStore = (supplierId) => {
  uni.showToast({ title: `进入供应商 ${supplierId} 店铺`, icon: 'none' });
  // uni.navigateTo({ url: `/pages/supplier/store?supplierId=${supplierId}` });
};

// 图片加载失败处理
const onImageError = (type, item) => {
  console.error(`Failed to load ${type} type image, item:`, item);
  if (type === 'banner') {
    item.banner = 'https://placehold.co/750x300/FF0000/FFFFFF?text=图片加载失败';
  } else if (type === 'product') {
    item.image = 'https://placehold.co/150x150/FF0000/FFFFFF?text=图片加载失败';
  }
  // For supplier logo, you might have a default fallback image
};
</script>

<style scoped>
page {
  background-color: #f0f2f5;
  min-height: 100vh;
  padding-bottom: env(safe-area-inset-bottom);
}

.activity-detail-container {
  display: flex;
  flex-direction: column;
  padding-bottom: 160rpx; /* Leave space for fixed action bar */
}

/* Activity Header Media (Banner/Video) */
.activity-header-media {
  position: relative;
  width: 100%;
  height: 380rpx; /* Adjust height as needed */
  overflow: hidden;
  background-color: #e0e0e0; /* Placeholder background */
  display: flex;
  align-items: center;
  justify-content: center;
}

.activity-banner, .activity-video {
  width: 100%;
  height: 100%;
  display: block;
}

.activity-status-overlay {
  position: absolute;
  top: 30rpx;
  right: 30rpx;
  background-color: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 10rpx 25rpx;
  border-radius: 30rpx;
  font-size: 28rpx;
  font-weight: bold;
  z-index: 10;
}

.status-active {
  background-color: #4CAF50; /* Green */
}
.status-upcoming {
  background-color: #FFC107; /* Amber */
}
.status-ended {
  background-color: #6C757D; /* Grey */
}

/* Card Styles */
.card {
  background-color: #ffffff;
  border-radius: 16rpx;
  margin: 20rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
}

.card-title-section {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
  padding-bottom: 15rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.card-title {
  font-size: 34rpx;
  font-weight: bold;
  color: #333333;
}

/* Activity Overview */
.activity-overview {
  margin-top: -80rpx; /* Overlap with banner */
  position: relative;
  z-index: 5;
  padding-top: 40rpx; /* Adjust padding to make space for overlap */
  border-top-left-radius: 16rpx;
  border-top-right-radius: 16rpx;
}

.activity-title {
  font-size: 40rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 15rpx;
  line-height: 1.4;
}

.core-benefit {
  font-size: 32rpx;
  color: #dc3545; /* Red for core benefit */
  font-weight: 600;
  line-height: 1.5;
}

/* Activity Time Section */
.activity-time-section {
  border-left: 10rpx solid #2196F3; /* Blue border */
}

.time-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 15rpx;
  font-size: 28rpx;
  color: #555555;
  line-height: 1.5;
}

.time-item .label {
  color: #888888;
  flex-shrink: 0;
  margin-right: 10rpx;
}

.time-item .value {
  flex-grow: 1;
  word-break: break-all;
}

.phase-item {
  margin-left: 30rpx; /* Indent phases */
  font-size: 26rpx;
  color: #666666;
}

.countdown-display {
  display: flex;
  align-items: center;
  background-color: #fffbe6;
  border: 1rpx solid #ffe58f;
  border-radius: 12rpx;
  padding: 15rpx 25rpx;
  margin-top: 20rpx;
  font-size: 28rpx;
  color: #d46b08;
  font-weight: 500;
}

.countdown-icon {
  font-size: 36rpx;
  margin-right: 15rpx;
  flex-shrink: 0;
}

.countdown-value {
  font-size: 32rpx;
  font-weight: bold;
  color: #dc3545; /* Red for countdown value */
  margin-left: 10rpx;
}

/* Activity Audience Section */
.activity-audience-section {
  border-left: 10rpx solid #FF9800; /* Orange border */
}

.audience-text {
  font-size: 28rpx;
  color: #555555;
  line-height: 1.6;
}

/* Activity Rules Section */
.activity-rules-section {
  border-left: 10rpx solid #9C27B0; /* Purple border */
}

.rule-block {
  margin-bottom: 30rpx;
}

.rule-subtitle {
  font-size: 30rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 15rpx;
  display: block;
}

.rule-content {
  font-size: 28rpx;
  color: #555555;
  line-height: 1.6;
}

.rule-content-item {
  display: flex;
  align-items: flex-start;
  font-size: 28rpx;
  color: #555555;
  line-height: 1.6;
  margin-bottom: 8rpx;
  margin-left: 20rpx; /* Indent list items */
}

.rule-content-item .dot {
  margin-right: 10rpx;
  font-size: 36rpx; /* Larger dot */
  line-height: 1;
  color: #9C27B0; /* Purple dot */
}

.coupon-item {
  display: flex;
  background-color: #f9f9f9;
  border: 1rpx solid #eeeeee;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
}

.coupon-left {
  background-color: #4CAF50; /* Green background for value */
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20rpx 30rpx;
  flex-shrink: 0;
  position: relative;
}

.coupon-left::after {
  content: '';
  position: absolute;
  right: -15rpx; /* Half of the circle diameter */
  top: 50%;
  transform: translateY(-50%);
  width: 30rpx;
  height: 30rpx;
  background-color: #f0f2f5; /* Match page background */
  border-radius: 50%;
  z-index: 1;
}

.coupon-value {
  font-size: 48rpx;
  font-weight: bold;
  line-height: 1;
  margin-bottom: 10rpx;
}

.coupon-name {
  font-size: 26rpx;
  white-space: nowrap;
}

.coupon-right {
  flex-grow: 1;
  padding: 20rpx 30rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.coupon-threshold, .coupon-scope, .coupon-limit, .coupon-expiry {
  font-size: 26rpx;
  color: #666666;
  margin-bottom: 6rpx;
  line-height: 1.4;
}

.coupon-action-button {
  background-color: #FFC107; /* Amber button */
  color: #333;
  padding: 10rpx 20rpx;
  border-radius: 8rpx;
  font-size: 26rpx;
  font-weight: 500;
  align-self: flex-end; /* Align to bottom right */
  margin-top: 10rpx;
  transition: background-color 0.2s ease;
}

.coupon-action-button:active {
  background-color: #e0ac00;
}

.coupon-received {
  font-size: 28rpx;
  color: #4CAF50;
  font-weight: bold;
  align-self: flex-end;
  margin-top: 10rpx;
}

.flow-item {
  display: flex;
  align-items: center;
  margin-bottom: 15rpx;
  margin-left: 20rpx;
}

.flow-step-number {
  background-color: #4CAF50; /* Green circle */
  color: white;
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  font-weight: bold;
  margin-right: 15rpx;
  flex-shrink: 0;
}

.flow-step-description {
  font-size: 28rpx;
  color: #555555;
  line-height: 1.6;
  flex-grow: 1;
}

/* Recommended Section (Products/Suppliers) */
.recommended-section {
  border-left: 10rpx solid #E91E63; /* Pink border */
}

.product-list, .supplier-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.product-card {
  display: flex;
  background-color: #f9f9f9;
  border-radius: 12rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.product-img {
  width: 200rpx;
  height: 200rpx;
  flex-shrink: 0;
  background-color: #e0e0e0;
}

.product-info {
  flex-grow: 1;
  padding: 20rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-name {
  font-size: 30rpx;
  font-weight: bold;
  color: #333333;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-spec {
  font-size: 26rpx;
  color: #666666;
  margin-top: 8rpx;
}

.price-row {
  display: flex;
  align-items: baseline;
  margin-top: 10rpx;
}

.activity-price {
  font-size: 36rpx;
  font-weight: bold;
  color: #dc3545; /* Red for activity price */
  margin-right: 15rpx;
}

.original-price {
  font-size: 26rpx;
  color: #999999;
  text-decoration: line-through;
  margin-right: 15rpx;
}

.discount-tag {
  background-color: #FFC107; /* Amber tag */
  color: #333;
  font-size: 24rpx;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
  white-space: nowrap;
}

.product-action-button {
  background-color: #4CAF50; /* Green button */
  color: white;
  padding: 12rpx 25rpx;
  border-radius: 8rpx;
  font-size: 28rpx;
  align-self: flex-end;
  margin-top: 10rpx;
  transition: background-color 0.2s ease;
}

.product-action-button:active {
  background-color: #388E3C;
}

.supplier-card {
  display: flex;
  align-items: center;
  background-color: #f9f9f9;
  border-radius: 12rpx;
  padding: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.supplier-logo {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  margin-right: 20rpx;
  flex-shrink: 0;
  background-color: #e0e0e0;
}

.supplier-info {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.supplier-name {
  font-size: 30rpx;
  font-weight: bold;
  color: #333333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.supplier-stats {
  font-size: 26rpx;
  color: #666666;
  margin-top: 8rpx;
}

.supplier-action-button {
  background-color: #2196F3; /* Blue button */
  color: white;
  padding: 12rpx 25rpx;
  border-radius: 8rpx;
  font-size: 28rpx;
  flex-shrink: 0;
  margin-left: 20rpx;
  transition: background-color 0.2s ease;
}

.supplier-action-button:active {
  background-color: #1976D2;
}

/* FAQ Section */
.faq-section {
  border-left: 10rpx solid #FF5722; /* Deep Orange border */
}

.faq-item {
  margin-bottom: 25rpx;
}

.faq-question {
  font-size: 28rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 10rpx;
  display: block;
  line-height: 1.5;
}

.faq-answer {
  font-size: 28rpx;
  color: #555555;
  line-height: 1.6;
  margin-left: 20rpx; /* Indent answer */
  display: block;
}

/* Organizer Section */
.organizer-section {
  border-left: 10rpx solid #4CAF50; /* Green border */
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.organizer-name {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
  flex-grow: 1;
}

/* Action Bar (Fixed at bottom) */
.action-bar-placeholder {
  height: 160rpx; /* Height to reserve space for the fixed action bar */
  width: 100%;
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background-color: #ffffff;
  padding: 20rpx 30rpx env(safe-area-inset-bottom); /* 适配底部安全区 */
  box-shadow: 0 -4rpx 12rpx rgba(0, 0, 0, 0.1);
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 20rpx;
  box-sizing: border-box;
  z-index: 100;
}

.action-button {
  flex-grow: 1;
  min-width: 200rpx;
  background-color: #f0f2f5;
  color: #333333;
  padding: 24rpx 30rpx;
  border-radius: 12rpx;
  font-size: 30rpx;
  font-weight: 500;
  text-align: center;
  transition: background-color 0.2s ease, color 0.2s ease;
  box-sizing: border-box;
  border: 1rpx solid #dddddd;
}

.action-button:active {
  opacity: 0.8;
}

.action-button.primary {
  background-color: #4CAF50; /* Primary actions in green */
  color: white;
  border-color: #4CAF50;
}

.action-button.primary:active {
  background-color: #388E3C;
}

.action-button.small {
  padding: 12rpx 20rpx;
  font-size: 26rpx;
  min-width: unset;
  flex-grow: 0;
  align-self: center; /* Vertically align with organizer name */
}
</style>
