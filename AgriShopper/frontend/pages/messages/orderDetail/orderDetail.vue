<template>
  <view class="order-detail-container">
    <!-- 顶部导航栏 -->
    <view class="header">
      <text class="header-title">订单详情</text>
      <text class="header-status">{{ order.status }}</text>
    </view>

    <!-- 订单概览 -->
    <view class="card order-overview">
      <view class="status-main" :class="order.statusClass">{{ order.status }}</view>
      <view class="order-info-row">
        <text class="label">订单编号：</text>
        <text class="value">{{ order.orderId }}</text>
        <text class="copy-icon" @tap="copyToClipboard(order.orderId)">📋</text>
      </view>
      <view class="order-info-row">
        <text class="label">下单时间：</text>
        <text class="value">{{ order.orderTime }}</text>
      </view>
      <view class="order-info-row">
        <text class="label">供应商：</text>
        <text class="value supplier-name">{{ order.supplier.name }}</text>
        <text class="contact-icon" @tap="contactSupplier(order.supplier.contact)">💬</text>
      </view>
      <view class="order-info-row">
        <text class="label">产地：</text>
        <text class="value">{{ order.supplier.origin }}</text>
      </view>
      <view v-if="order.countdown" class="countdown-tip">
        <text class="countdown-icon">⏰</text>
        <text>{{ order.countdown }}</text>
      </view>
    </view>

    <!-- 商品详情 -->
    <view class="card product-details">
      <view class="card-title-section">
        <text class="card-title">商品详情</text>
      </view>
      <view class="product-item">
        <image class="product-image" :src="order.product.image" mode="aspectFill" @error="onImageError('product', order.product)"></image>
        <view class="product-info">
          <text class="product-name">{{ order.product.name }}</text>
          <text class="product-spec">规格：{{ order.product.spec }}</text>
          <text class="product-unit-price">单价：￥{{ order.product.unitPrice }}/{{ order.product.unit }}</text>
          <text class="product-quantity">数量：{{ order.product.quantity }} {{ order.product.unit }}</text>
          <text class="product-subtotal">小计：￥{{ order.product.subtotal }}</text>
        </view>
      </view>
      <view class="total-amount">
        <text>总金额：</text>
        <text class="amount-value">￥{{ order.totalAmount }}</text>
        <text class="tax-info">{{ order.taxIncluded ? '（含税）' : '（不含税）' }} {{ order.shippingIncluded ? '（含运费）' : '（不含运费）' }}</text>
      </view>
      <view v-if="order.traceabilityInfo" class="traceability-info" @tap="viewTraceability">
        <text>溯源信息：</text>
        <text class="traceability-link">查看产地批次详情 ></text>
      </view>
    </view>

    <!-- 物流信息 -->
    <view v-if="order.logistics && order.logistics.status" class="card logistics-info">
      <view class="card-title-section">
        <text class="card-title">物流信息</text>
      </view>
      <view class="logistics-status-line">
        <text class="logistics-status">{{ order.logistics.status }}</text>
        <text class="logistics-time">{{ order.logistics.latestTime }}</text>
      </view>
      <view class="logistics-detail-row">
        <text class="label">物流公司：</text>
        <text class="value">{{ order.logistics.company }}</text>
      </view>
      <view class="logistics-detail-row">
        <text class="label">运单号：</text>
        <text class="value">{{ order.logistics.trackingNumber }}</text>
        <text class="copy-icon" @tap="copyToClipboard(order.logistics.trackingNumber)">📋</text>
      </view>
      <view class="logistics-detail-row">
        <text class="label">发货时间：</text>
        <text class="value">{{ order.logistics.shippingTime }}</text>
      </view>
      <view class="logistics-detail-row">
        <text class="label">预计到达：</text>
        <text class="value">{{ order.logistics.eta }}</text>
      </view>
      <view class="logistics-detail-row">
        <text class="label">收货地址：</text>
        <text class="value">{{ order.logistics.deliveryAddress }}</text>
      </view>
      <!-- 冷链信息，根据后端传参判断显不显示 -->
      <view v-if="order.logistics.coldChain" class="cold-chain-info">
        <view class="cold-chain-title">
          <text class="cold-chain-icon">❄️</text>
          <text>冷链信息</text>
        </view>
        <view class="cold-chain-detail-row">
          <text class="label">当前温度：</text>
          <text class="value">{{ order.logistics.coldChain.currentTemp }}</text>
        </view>
        <view v-if="order.logistics.coldChain.tempAlert" class="cold-chain-detail-row alert">
          <text class="label">温度异常：</text>
          <text class="value">{{ order.logistics.coldChain.tempAlert }} ({{ order.logistics.coldChain.alertTime }})</text>
        </view>
        <view class="cold-chain-detail-row">
          <text class="label">冷链车号：</text>
          <text class="value">{{ order.logistics.coldChain.vehicleNumber }}</text>
        </view>
        <button class="action-button small" @tap="viewTempRecords">查看温度记录</button>
      </view>
      <button class="action-button" @tap="viewLogisticsDetail">查看物流详情</button>
    </view>

    <!-- 支付信息 -->
    <view class="card payment-info">
      <view class="card-title-section">
        <text class="card-title">支付信息</text>
      </view>
      <view class="payment-detail-row">
        <text class="label">支付状态：</text>
        <text class="value payment-status" :class="order.payment.statusClass">{{ order.payment.status }}</text>
      </view>
      <view class="payment-detail-row">
        <text class="label">支付方式：</text>
        <text class="value">{{ order.payment.method }}</text>
      </view>
      <view class="payment-detail-row">
        <text class="label">定金：</text>
        <text class="value">￥{{ order.payment.deposit }}</text>
      </view>
      <view class="payment-detail-row">
        <text class="label">尾款：</text>
        <text class="value">￥{{ order.payment.finalPayment }}</text>
      </view>
      <view class="payment-detail-row total">
        <text class="label">总计：</text>
        <text class="value">￥{{ order.payment.totalAmount }}</text>
      </view>
      <view v-if="order.payment.transactionTime" class="payment-detail-row">
        <text class="label">支付时间：</text>
        <text class="value">{{ order.payment.transactionTime }}</text>
      </view>
    </view>

    <!-- 质检与验收信息 -->
    <view v-if="order.qualityInspection" class="card quality-inspection-info">
      <view class="card-title-section">
        <text class="card-title">质检与验收</text>
      </view>
      <view v-if="order.qualityInspection.reportLink" class="inspection-item" @tap="viewInspectionReport">
        <text class="label">供应商质检报告：</text>
        <text class="value link">查看/下载 ></text>
      </view>
      <view class="inspection-item">
        <text class="label">验收状态：</text>
        <text class="value验收状态" :class="order.qualityInspection.acceptanceStatusClass">{{ order.qualityInspection.acceptanceStatus }}</text>
      </view>
      <view v-if="order.qualityInspection.acceptanceTime" class="inspection-item">
        <text class="label">验收时间：</text>
        <text class="value">{{ order.qualityInspection.acceptanceTime }}</text>
      </view>
      <view v-if="order.qualityInspection.issues && order.qualityInspection.issues.length > 0" class="inspection-issues">
        <text class="issue-title">异议详情：</text>
        <view v-for="(issue, index) in order.qualityInspection.issues" :key="index" class="issue-item">
          <text class="issue-description">{{ issue.description }}</text>
          <view class="issue-attachments" v-if="issue.images && issue.images.length > 0">
            <image v-for="(img, imgIndex) in issue.images" :key="imgIndex" :src="img" class="issue-image" mode="aspectFill" @tap.stop="previewImage(issue.images, imgIndex)"></image>
          </view>
          <text v-if="issue.video" class="issue-video-link" @tap.stop="playVideo(issue.video)">播放视频 ></text>
        </view>
      </view>
      <view v-if="order.qualityInspection.signer" class="inspection-item">
        <text class="label">签收人：</text>
        <text class="value">{{ order.qualityInspection.signer }} ({{ order.qualityInspection.signTime }})</text>
      </view>
    </view>

    <!-- 事件/状态变更记录 -->
    <view class="card event-history">
      <view class="card-title-section">
        <text class="card-title">订单日志</text>
      </view>
      <view class="event-list">
        <view v-for="(event, index) in order.eventHistory" :key="index" class="event-item">
          <text class="event-time">{{ event.time }}</text>
          <text class="event-description">{{ event.description }}</text>
        </view>
      </view>
    </view>

    <!-- 相关附件 -->
    <view v-if="order.attachments && order.attachments.length > 0" class="card attachments-section">
      <view class="card-title-section">
        <text class="card-title">相关附件</text>
      </view>
      <view v-for="(attachment, index) in order.attachments" :key="index" class="attachment-item" @tap="downloadAttachment(attachment)">
        <text class="attachment-name">{{ attachment.name }}</text>
        <text class="download-icon">⬇️</text>
      </view>
    </view>

    <!-- 核心操作区 -->
    <view class="action-bar-placeholder"></view>
    <view class="action-bar">
      <button v-if="order.actions.includes('pay')" class="action-button primary" @tap="goToPayment">去支付</button>
      <button v-if="order.actions.includes('contactSeller')" class="action-button" @tap="contactSeller">联系卖家</button>
      <button v-if="order.actions.includes('viewLogistics')" class="action-button" @tap="viewLogisticsDetail">查看物流</button>
      <button v-if="order.actions.includes('confirmReceipt')" class="action-button primary" @tap="confirmReceipt">确认收货</button>
      <button v-if="order.actions.includes('submitAcceptance')" class="action-button primary" @tap="submitAcceptance">申请验收/提交结果</button>
      <button v-if="order.actions.includes('applyAftersale')" class="action-button" @tap="applyAftersale">申请售后/退款</button>
      <button v-if="order.actions.includes('handleDispute')" class="action-button danger" @tap="handleDispute">处理异议/纠纷</button>
      <button v-if="order.actions.includes('cancelOrder')" class="action-button" @tap="cancelOrder">取消订单</button>
      <button v-if="order.actions.includes('evaluate')" class="action-button" @tap="evaluateOrder">评价</button>
      <button v-if="order.actions.includes('download')" class="action-button" @tap="downloadAll">下载订单/合同/发票</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';

// 定义响应式订单数据
const order = ref({});

// 模拟后端数据
const mockOrderData = {
  '20240715123456': { // 待验收订单，有冷链信息
    orderId: '20240715123456',
    status: '待验收',
    statusClass: 'status-pending-acceptance',
    orderTime: '2024-07-15 10:00:00',
    supplier: {
      name: 'XX生鲜供货商',
      contact: '13800138000',
      origin: '山东寿光'
    },
    countdown: '请在 24 小时内完成验收，否则将自动确认收货',
    product: {
      name: '山东红富士苹果',
      image: 'https://placehold.co/200x200/FF5733/FFFFFF?text=Apple',
      spec: '一级果 80mm+ 5kg/箱',
      unitPrice: '10.50',
      unit: '斤',
      quantity: '5000',
      subtotal: '52500.00'
    },
    totalAmount: '52500.00',
    taxIncluded: true,
    shippingIncluded: true,
    traceabilityInfo: true,

    logistics: {
      status: '已到达派送点',
      latestTime: '2024-07-15 16:30:00',
      company: '顺丰冷运',
      trackingNumber: 'SF1234567890',
      shippingTime: '2024-07-15 14:30:00',
      eta: '2024-07-15 18:00:00',
      deliveryAddress: '北京市朝阳区XX街道XX仓库',
      coldChain: { // 冷链信息存在
        currentTemp: '4.5°C',
        tempAlert: '温度异常：曾达到 10.2°C',
        alertTime: '2024-07-15 15:00:00',
        vehicleNumber: '京A88888'
      }
    },

    payment: {
      status: '已支付定金',
      statusClass: 'payment-status-deposit-paid',
      method: '银行转账',
      deposit: '10000.00',
      finalPayment: '42500.00',
      totalAmount: '52500.00',
      transactionTime: '2024-07-15 10:05:00'
    },

    qualityInspection: {
      reportLink: 'https://example.com/report_apple.pdf',
      acceptanceStatus: '待验收',
      acceptanceStatusClass: 'acceptance-status-pending',
      acceptanceTime: '',
      issues: [],
      signer: '',
      signTime: ''
    },

    eventHistory: [
      { time: '2024-07-15 16:30:00', description: '货物已到达派送点，等待派送' },
      { time: '2024-07-15 15:00:00', description: '冷链温度异常告警：达到 10.2°C' },
      { time: '2024-07-15 14:30:00', description: '供应商已发货，运单号：SF1234567890' },
      { time: '2024-07-15 10:05:00', description: '您支付了定金 ￥10000.00' },
      { time: '2024-07-15 10:00:00', description: '订单已创建' },
    ],
    attachments: [
      { name: '订单合同_20240715.pdf', url: 'https://example.com/contract.pdf' },
      { name: '供应商质检报告.pdf', url: 'https://example.com/supplier_report.pdf' }
    ],
    actions: ['contactSeller', 'viewLogistics', 'submitAcceptance', 'cancelOrder', 'download']
  },
  '20240714987654': { // 有异议订单，无冷链信息
    orderId: '20240714987654',
    status: '有异议：重量短缺',
    statusClass: 'status-disputed',
    orderTime: '2024-07-14 08:00:00',
    supplier: {
      name: '南方果园',
      contact: '13912345678',
      origin: '广东茂名'
    },
    countdown: '',
    product: {
      name: '广东荔枝',
      image: 'https://placehold.co/200x200/FF33CC/FFFFFF?text=Lychee',
      spec: '妃子笑 5kg/箱',
      unitPrice: '25.00',
      unit: '斤',
      quantity: '1000',
      subtotal: '25000.00'
    },
    totalAmount: '25000.00',
    taxIncluded: false,
    shippingIncluded: true,
    traceabilityInfo: true,

    logistics: {
      status: '已签收',
      latestTime: '2024-07-14 18:00:00',
      company: '德邦快递',
      trackingNumber: 'DB987654321',
      shippingTime: '2024-07-14 10:00:00',
      eta: '2024-07-14 18:00:00',
      deliveryAddress: '北京市海淀区XX街道XX门店',
      coldChain: null // 冷链信息不存在
    },

    payment: {
      status: '已全款支付',
      statusClass: 'payment-status-paid',
      method: '微信支付',
      deposit: '0.00',
      finalPayment: '25000.00',
      totalAmount: '25000.00',
      transactionTime: '2024-07-14 08:05:00'
    },

    qualityInspection: {
      reportLink: '',
      acceptanceStatus: '有异议',
      acceptanceStatusClass: 'acceptance-status-disputed',
      acceptanceTime: '2024-07-14 18:30:00',
      issues: [
        {
          description: '实际重量短缺：订单500kg，实收480kg',
          images: ['https://placehold.co/100x100/FF4D4F/FFFFFF?text=Issue1'],
          video: ''
        },
        {
          description: '部分腐烂：发现2箱荔枝腐烂率超标',
          images: ['https://placehold.co/100x100/FF4D4F/FFFFFF?text=Issue2', 'https://placehold.co/100x100/FF4D4F/FFFFFF?text=Issue3'],
          video: 'https://www.w3schools.com/html/mov_bbb.mp4'
        }
      ],
      signer: '张三',
      signTime: '2024-07-14 18:00:00'
    },

    eventHistory: [
      { time: '2024-07-14 18:30:00', description: '您提交了验收异议：重量短缺、部分腐烂' },
      { time: '2024-07-14 18:00:00', description: '物流已签收' },
      { time: '2024-07-14 10:00:00', description: '供应商已发货，运单号：DB987654321' },
      { time: '2024-07-14 08:05:00', description: '您全款支付了 ￥25000.00' },
      { time: '2024-07-14 08:00:00', description: '订单已创建' },
    ],
    attachments: [
      { name: '签收单_DB987654321.jpg', url: 'https://example.com/receipt.jpg' }
    ],
    actions: ['contactSeller', 'applyAftersale', 'handleDispute', 'evaluate', 'download']
  },
  '20240713000111': { // 已完成订单，无冷链信息
    orderId: '20240713000111',
    status: '已完成',
    statusClass: 'status-completed',
    orderTime: '2024-07-13 09:00:00',
    supplier: {
      name: '北方粮油',
      contact: '13711223344',
      origin: '黑龙江'
    },
    countdown: '',
    product: {
      name: '东北大米',
      image: 'https://placehold.co/200x200/99FF33/FFFFFF?text=Rice',
      spec: '长粒香 50kg/袋',
      unitPrice: '4.00',
      unit: '斤',
      quantity: '2000',
      subtotal: '8000.00'
    },
    totalAmount: '8000.00',
    taxIncluded: true,
    shippingIncluded: false,
    traceabilityInfo: false,

    logistics: {
      status: '已签收',
      latestTime: '2024-07-13 16:00:00',
      company: '中通快运',
      trackingNumber: 'ZT1122334455',
      shippingTime: '2024-07-13 10:00:00',
      eta: '2024-07-13 16:00:00',
      deliveryAddress: '北京市丰台区XX库房',
      coldChain: null // 冷链信息不存在
    },

    payment: {
      status: '已全款支付',
      statusClass: 'payment-status-paid',
      method: '支付宝',
      deposit: '0.00',
      finalPayment: '8000.00',
      totalAmount: '8000.00',
      transactionTime: '2024-07-13 09:05:00'
    },

    qualityInspection: {
      reportLink: '',
      acceptanceStatus: '已验收',
      acceptanceStatusClass: 'acceptance-status-accepted',
      acceptanceTime: '2024-07-13 16:15:00',
      issues: [],
      signer: '李四',
      signTime: '2024-07-13 16:00:00'
    },

    eventHistory: [
      { time: '2024-07-13 16:15:00', description: '您已确认收货并验收通过' },
      { time: '2024-07-13 16:00:00', description: '物流已签收' },
      { time: '2024-07-13 10:00:00', description: '供应商已发货，运单号：ZT1122334455' },
      { time: '2024-07-13 09:05:00', description: '您全款支付了 ￥8000.00' },
      { time: '2024-07-13 09:00:00', description: '订单已创建' },
    ],
    attachments: [
      { name: '发票_20240713.pdf', url: 'https://example.com/invoice.pdf' }
    ],
    actions: ['evaluate', 'download']
  }
};

// 模拟从后端获取订单详情的函数
const fetchOrderDetail = (orderId) => {
  return new Promise(resolve => {
    uni.showLoading({ title: '加载订单详情...' });
    setTimeout(() => {
      const data = mockOrderData[orderId];
      if (data) {
        resolve(data);
      } else {
        // 如果没有找到订单，可以返回一个默认状态或错误信息
        resolve({
          orderId: orderId,
          status: '订单不存在',
          statusClass: 'status-cancelled',
          orderTime: 'N/A',
          supplier: { name: 'N/A', contact: 'N/A', origin: 'N/A' },
          product: { name: 'N/A', image: 'https://placehold.co/200x200/FF0000/FFFFFF?text=Error', spec: 'N/A', unitPrice: '0.00', unit: 'N/A', quantity: '0', subtotal: '0.00' },
          totalAmount: '0.00',
          taxIncluded: false,
          shippingIncluded: false,
          traceabilityInfo: false,
          logistics: null,
          payment: { status: 'N/A', statusClass: '', method: 'N/A', deposit: '0.00', finalPayment: '0.00', totalAmount: '0.00', transactionTime: 'N/A' },
          qualityInspection: null,
          eventHistory: [{ time: new Date().toLocaleString(), description: '订单信息加载失败或订单不存在。' }],
          attachments: [],
          actions: []
        });
      }
      uni.hideLoading();
    }, 1000); // 模拟网络延迟
  });
};

onLoad(async (options) => {
  if (options.orderId) {
    // 根据传递的 orderId 获取订单详情
    order.value = await fetchOrderDetail(options.orderId);
  } else {
    // 如果没有 orderId，可以加载一个默认订单或者显示错误
    order.value = await fetchOrderDetail('20240715123456'); // 默认加载第一个订单
  }
});

// Helper function to copy text to clipboard
const copyToClipboard = (text) => {
  uni.setClipboardData({
    data: text,
    success: () => {
      uni.showToast({
        title: '已复制',
        icon: 'success',
        duration: 1000
      });
    }
  });
};

// Action button handlers
const goToPayment = () => {
  uni.showToast({ title: '跳转到支付页面', icon: 'none' });
  // uni.navigateTo({ url: `/pages/payment/pay?orderId=${order.value.orderId}` });
};

const contactSupplier = (contactInfo) => {
  uni.showToast({ title: `联系供应商: ${contactInfo}`, icon: 'none' });
  // uni.makePhoneCall({ phoneNumber: contactInfo });
  // Or uni.navigateTo({ url: `/pages/chat/chatWithSupplier?supplierId=${order.value.supplier.id}` });
};

const contactSeller = () => {
  uni.showToast({ title: '联系卖家', icon: 'none' });
  // uni.navigateTo({ url: `/pages/chat/chatWithSeller?orderId=${order.value.orderId}` });
};

const viewLogisticsDetail = () => {
  uni.showToast({ title: '查看物流详情', icon: 'none' });
  // uni.navigateTo({ url: `/pages/logistics/detail?trackingNumber=${order.value.logistics.trackingNumber}` });
};

const confirmReceipt = () => {
  uni.showModal({
    title: '确认收货',
    content: '请确认您已收到货物并验收无误。确认后订单将完成。',
    success: (res) => {
      if (res.confirm) {
        uni.showToast({ title: '确认收货成功', icon: 'success' });
        // Update order status and actions
        order.value.status = '已完成';
        order.value.statusClass = 'status-completed';
        order.value.actions = ['evaluate', 'download'];
        order.value.eventHistory.unshift({ time: new Date().toLocaleString(), description: '您已确认收货并验收通过' });
      }
    }
  });
};

const submitAcceptance = () => {
  uni.showToast({ title: '跳转到验收提交页面', icon: 'none' });
  // uni.navigateTo({ url: `/pages/qualityInspection/submitAcceptance?orderId=${order.value.orderId}` });
};

const applyAftersale = () => {
  uni.showToast({ title: '跳转到申请售后页面', icon: 'none' });
  // uni.navigateTo({ url: `/pages/aftersale/apply?orderId=${order.value.orderId}` });
};

const handleDispute = () => {
  uni.showToast({ title: '跳转到处理异议页面', icon: 'none' });
  // uni.navigateTo({ url: `/pages/dispute/handle?orderId=${order.value.orderId}` });
};

const cancelOrder = () => {
  uni.showModal({
    title: '取消订单',
    content: '确定要取消此订单吗？',
    success: (res) => {
      if (res.confirm) {
        uni.showToast({ title: '订单已取消', icon: 'success' });
        order.value.status = '已取消';
        order.value.statusClass = 'status-cancelled';
        order.value.actions = []; // No actions after cancellation
        order.value.eventHistory.unshift({ time: new Date().toLocaleString(), description: '您已取消订单' });
      }
    }
  });
};

const evaluateOrder = () => {
  uni.showToast({ title: '跳转到评价页面', icon: 'none' });
  // uni.navigateTo({ url: `/pages/evaluation/create?orderId=${order.value.orderId}` });
};

const downloadAll = () => {
  uni.showToast({ title: '开始下载相关附件', icon: 'none' });
  order.value.attachments.forEach(attachment => {
    // In a real app, you'd handle actual download logic
    console.log(`Downloading: ${attachment.name} from ${attachment.url}`);
  });
};

const viewTraceability = () => {
  uni.showToast({ title: '查看溯源信息', icon: 'none' });
  // uni.navigateTo({ url: `/pages/product/traceability?productId=${order.value.product.id}` });
};

const viewTempRecords = () => {
  uni.showToast({ title: '查看温度记录', icon: 'none' });
  // uni.navigateTo({ url: `/pages/logistics/tempRecords?trackingNumber=${order.value.logistics.trackingNumber}` });
};

const viewInspectionReport = () => {
  uni.showToast({ title: '查看质检报告', icon: 'none' });
  // uni.navigateTo({ url: `/pages/qualityInspection/reportViewer?url=${encodeURIComponent(order.value.qualityInspection.reportLink)}` });
};

const previewImage = (images, index) => {
  uni.previewImage({
    urls: images,
    current: index
  });
};

const playVideo = (videoUrl) => {
  uni.showToast({ title: '播放视频 (实际会跳转到视频播放器)', icon: 'none' });
  // In a real app, you might use uni.navigateTo with a custom video player page
  // or a system video player if supported.
  // Example: uni.navigateTo({ url: `/pages/videoPlayer?url=${encodeURIComponent(videoUrl)}` });
};

const downloadAttachment = (attachment) => {
  uni.showToast({ title: `下载附件：${attachment.name}`, icon: 'none' });
  // uni.downloadFile({
  //   url: attachment.url,
  //   success: (res) => {
  //     if (res.statusCode === 200) {
  //       uni.openDocument({
  //         filePath: res.tempFilePath,
  //         showMenu: true,
  //         success: (openRes) => {
  //           console.log('文件打开成功', openRes);
  //         },
  //         fail: (openErr) => {
  //           console.error('文件打开失败', openErr);
  //           uni.showToast({ title: '文件打开失败', icon: 'none' });
  //         }
  //       });
  //     }
  //   },
  //   fail: (err) => {
  //     console.error('文件下载失败', err);
  //     uni.showToast({ title: '文件下载失败', icon: 'none' });
  //   }
  // });
};

const onImageError = (type, item) => {
  console.error(`Failed to load ${type} type image, item:`, item);
  if (type === 'product') {
    item.image = 'https://placehold.co/200x200/FF0000/FFFFFF?text=Error';
  }
};

</script>

<style scoped>
page {
  background-color: #f0f2f5;
  min-height: 100vh;
  padding-bottom: env(safe-area-inset-bottom); /* 适配底部安全区 */
}

.order-detail-container {
  display: flex;
  flex-direction: column;
  padding-bottom: 160rpx; /* Leave space for fixed action bar */
}

/* Header */
.header {
  background-color: #4CAF50; /* Green header */
  color: white;
  padding: 30rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 4rpx 10rpx rgba(0, 0, 0, 0.1);
}

.header-title {
  font-size: 38rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.header-status {
  font-size: 30rpx;
  font-weight: 500;
  opacity: 0.9;
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

/* Order Overview */
.order-overview .status-main {
  font-size: 40rpx;
  font-weight: bold;
  margin-bottom: 30rpx;
  text-align: center;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #eee;
}

.order-info-row {
  display: flex;
  align-items: center;
  margin-bottom: 15rpx;
  font-size: 28rpx;
  color: #555555;
  line-height: 1.5;
}

.order-info-row .label {
  color: #888888;
  flex-shrink: 0;
  margin-right: 10rpx;
}

.order-info-row .value {
  flex-grow: 1;
  word-break: break-all;
}

.supplier-name {
  color: #4CAF50; /* Green for supplier name */
  font-weight: 500;
}

.copy-icon, .contact-icon {
  font-size: 36rpx;
  margin-left: 15rpx;
  color: #007bff;
  flex-shrink: 0;
  padding: 5rpx;
  border-radius: 8rpx;
  transition: background-color 0.2s ease;
}
.copy-icon:active, .contact-icon:active {
  background-color: #e0e0e0;
}

.countdown-tip {
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

/* Product Details */
.product-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx dashed #f0f0f0;
}

.product-image {
  width: 180rpx;
  height: 180rpx;
  border-radius: 12rpx;
  margin-right: 25rpx;
  flex-shrink: 0;
  background-color: #e0e0e0;
}

.product-info {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  min-width: 0;
}

.product-name {
  font-size: 32rpx;
  color: #333333;
  font-weight: 600;
  margin-bottom: 8rpx;
  line-height: 1.4;
}

.product-spec, .product-unit-price, .product-quantity, .product-subtotal {
  font-size: 28rpx;
  color: #666666;
  margin-bottom: 6rpx;
  line-height: 1.4;
}

.product-subtotal {
  font-weight: bold;
  color: #333333;
  margin-top: 10rpx;
}

.total-amount {
  display: flex;
  justify-content: flex-end;
  align-items: baseline;
  font-size: 32rpx;
  color: #333333;
  font-weight: bold;
  margin-top: 20rpx;
}

.amount-value {
  font-size: 40rpx;
  color: #dc3545; /* Red for total amount */
  margin-left: 10rpx;
}

.tax-info {
  font-size: 26rpx;
  color: #999999;
  margin-left: 15rpx;
  font-weight: normal;
}

.traceability-info {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-top: 20rpx;
  font-size: 28rpx;
  color: #007bff;
  font-weight: 500;
  padding-top: 15rpx;
  border-top: 1rpx dashed #f0f0f0;
}

.traceability-link {
  margin-left: 10rpx;
}

/* Logistics Info */
.logistics-info {
  border-left: 10rpx solid #28a745; /* Green border for logistics */
}

.logistics-status-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.logistics-status {
  font-size: 32rpx;
  font-weight: bold;
  color: #28a745; /* Green for logistics status */
}

.logistics-time {
  font-size: 26rpx;
  color: #999999;
}

.logistics-detail-row {
  display: flex;
  align-items: center;
  margin-bottom: 15rpx;
  font-size: 28rpx;
  color: #555555;
  line-height: 1.5;
}

.logistics-detail-row .label {
  color: #888888;
  flex-shrink: 0;
  margin-right: 10rpx;
}

.logistics-detail-row .value {
  flex-grow: 1;
  word-break: break-all;
}

.cold-chain-info {
  background-color: #e6f7ff; /* Light blue background for cold chain */
  border: 1rpx solid #91d5ff;
  border-radius: 12rpx;
  padding: 20rpx 25rpx;
  margin-top: 20rpx;
}

.cold-chain-title {
  display: flex;
  align-items: center;
  font-size: 30rpx;
  font-weight: bold;
  color: #1890ff; /* Blue for cold chain title */
  margin-bottom: 15rpx;
}

.cold-chain-icon {
  font-size: 36rpx;
  margin-right: 15rpx;
}

.cold-chain-detail-row {
  display: flex;
  align-items: center;
  margin-bottom: 10rpx;
  font-size: 28rpx;
  color: #555555;
  line-height: 1.5;
}

.cold-chain-detail-row.alert {
  color: #dc3545; /* Red for temperature alerts */
  font-weight: bold;
}

/* Payment Info */
.payment-info {
  border-left: 10rpx solid #ffc107; /* Amber border for payment */
}

.payment-status {
  font-weight: bold;
}

.payment-status-deposit-paid {
  color: #ffc107; /* Amber for deposit paid */
}
.payment-status-paid {
  color: #28a745; /* Green for fully paid */
}
.payment-status-refunded {
  color: #6c757d; /* Grey for refunded */
}

.payment-detail-row {
  display: flex;
  align-items: center;
  margin-bottom: 15rpx;
  font-size: 28rpx;
  color: #555555;
  line-height: 1.5;
}

.payment-detail-row .label {
  color: #888888;
  flex-shrink: 0;
  margin-right: 10rpx;
}

.payment-detail-row .value {
  flex-grow: 1;
  word-break: break-all;
}

.payment-detail-row.total .value {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
}

/* Quality Inspection Info */
.quality-inspection-info {
  border-left: 10rpx solid #1890ff; /* Blue border for quality inspection */
}

.inspection-item {
  display: flex;
  align-items: center;
  margin-bottom: 15rpx;
  font-size: 28rpx;
  color: #555555;
  line-height: 1.5;
}

.inspection-item .label {
  color: #888888;
  flex-shrink: 0;
  margin-right: 10rpx;
}

.inspection-item .value {
  flex-grow: 1;
  word-break: break-all;
}

.inspection-item .value.link {
  color: #007bff;
  font-weight: 500;
}

.acceptance-status-pending {
  color: #ffc107; /* Amber */
  font-weight: bold;
}
.acceptance-status-accepted {
  color: #28a745; /* Green */
  font-weight: bold;
}
.acceptance-status-disputed {
  color: #dc3545; /* Red */
  font-weight: bold;
}

.inspection-issues {
  background-color: #fff1f0; /* Light red background for issues */
  border: 1rpx solid #ffa39e;
  border-radius: 12rpx;
  padding: 20rpx 25rpx;
  margin-top: 20rpx;
}

.issue-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #dc3545; /* Red for issue title */
  margin-bottom: 15rpx;
  display: block;
}

.issue-item {
  margin-bottom: 20rpx;
}

.issue-description {
  font-size: 28rpx;
  color: #333333;
  line-height: 1.5;
  margin-bottom: 10rpx;
  display: block;
}

.issue-attachments {
  display: flex;
  flex-wrap: wrap;
  gap: 15rpx;
  margin-top: 10rpx;
}

.issue-image {
  width: 160rpx;
  height: 160rpx;
  border-radius: 8rpx;
  background-color: #e0e0e0;
}

.issue-video-link {
  font-size: 28rpx;
  color: #007bff;
  margin-top: 10rpx;
  display: block;
}

/* Event History */
.event-history {
  border-left: 10rpx solid #6c757d; /* Grey border for history */
}

.event-list {
  display: flex;
  flex-direction: column;
}

.event-item {
  display: flex;
  flex-direction: column;
  padding: 15rpx 0;
  border-bottom: 1rpx dashed #f0f0f0;
}

.event-item:last-child {
  border-bottom: none;
}

.event-time {
  font-size: 26rpx;
  color: #999999;
  margin-bottom: 5rpx;
}

.event-description {
  font-size: 28rpx;
  color: #555555;
  line-height: 1.5;
}

/* Attachments Section */
.attachments-section {
  border-left: 10rpx solid #007bff; /* Blue border for attachments */
}

.attachment-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15rpx 0;
  border-bottom: 1rpx dashed #f0f0f0;
}

.attachment-item:last-child {
  border-bottom: none;
}

.attachment-name {
  font-size: 28rpx;
  color: #333333;
  flex-grow: 1;
  word-break: break-all;
}

.download-icon {
  font-size: 36rpx;
  color: #007bff;
  margin-left: 20rpx;
  flex-shrink: 0;
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
  flex-wrap: wrap; /* 允许按钮换行 */
  justify-content: flex-end; /* 靠右对齐 */
  gap: 20rpx; /* 按钮间距 */
  box-sizing: border-box;
  z-index: 100;
}

.action-button {
  flex-grow: 1; /* 允许按钮增长 */
  min-width: 200rpx; /* 最小宽度，防止过小 */
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
  background-color: #388E3C; /* Darker green on active */
}

.action-button.danger {
  background-color: #dc3545; /* Danger actions in red */
  color: white;
  border-color: #dc3545;
}

.action-button.danger:active {
  background-color: #c82333; /* Darker red on active */
}

.action-button.small {
  padding: 12rpx 20rpx;
  font-size: 26rpx;
  min-width: unset;
  flex-grow: 0;
  align-self: flex-start;
}


/* Status Color Classes (for order status, payment status, acceptance status) */
.status-pending-payment, .payment-status-pending {
  color: #dc3545; /* Red */
}
.status-shipping {
  color: #2196F3; /* Blue */
}
.status-pending-acceptance, .payment-status-deposit-paid, .acceptance-status-pending {
  color: #ffc107; /* Amber */
}
.status-completed, .payment-status-paid, .acceptance-status-accepted {
  color: #4CAF50; /* Green */
}
.status-cancelled, .payment-status-refunded {
  color: #6c757d; /* Grey */
}
.status-warning, .status-disputed, .acceptance-status-disputed {
  color: #dc3545; /* Red */
  font-weight: bold;
}
</style>
