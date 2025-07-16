"use strict";
const common_vendor = require("../../common/vendor.js");
const DELETE_BUTTON_WIDTH = 160;
const _sfc_main = {
  __name: "messages",
  setup(__props) {
    const backendStaticBaseUrl = common_vendor.ref("http://localhost:8080");
    const messageTabs = common_vendor.ref([
      // 修改 icon 路径，使用 backendStaticBaseUrl 拼接
      { name: "系统通知", unread: 0, icon: common_vendor.computed(() => backendStaticBaseUrl.value + "/messages/system_notification.png") },
      { name: "订单消息", unread: 0, icon: common_vendor.computed(() => backendStaticBaseUrl.value + "/messages/order_message.png") },
      { name: "活动消息", unread: 0, icon: common_vendor.computed(() => backendStaticBaseUrl.value + "/messages/activity_message.png") },
      { name: "客服消息", unread: 0, icon: common_vendor.computed(() => backendStaticBaseUrl.value + "/messages/customer_service_message.png") }
    ]);
    const currentTab = common_vendor.ref(0);
    const systemMessages = common_vendor.ref([]);
    const orderMessages = common_vendor.ref([]);
    const promotionMessages = common_vendor.ref([]);
    const serviceMessages = common_vendor.ref([]);
    const filterStatus = common_vendor.ref("all");
    const searchQuery = common_vendor.ref("");
    const debouncedSearchQuery = common_vendor.ref("");
    const activityFilterStatus = common_vendor.ref("all");
    const activitySearchQuery = common_vendor.ref("");
    const debouncedActivitySearchQuery = common_vendor.ref("");
    const csFilterStatus = common_vendor.ref("all");
    const csFilterIssueType = common_vendor.ref("all");
    const csSearchQuery = common_vendor.ref("");
    const debouncedCsSearchQuery = common_vendor.ref("");
    let searchTimeout = null;
    common_vendor.watch(searchQuery, (newQuery) => {
      if (searchTimeout) {
        clearTimeout(searchTimeout);
      }
      searchTimeout = setTimeout(() => {
        debouncedSearchQuery.value = newQuery;
      }, 300);
    });
    let activitySearchTimeout = null;
    common_vendor.watch(activitySearchQuery, (newQuery) => {
      if (activitySearchTimeout) {
        clearTimeout(activitySearchTimeout);
      }
      activitySearchTimeout = setTimeout(() => {
        debouncedActivitySearchQuery.value = newQuery;
      }, 300);
    });
    let csSearchTimeout = null;
    common_vendor.watch(csSearchQuery, (newQuery) => {
      if (csSearchTimeout) {
        clearTimeout(csSearchTimeout);
      }
      csSearchTimeout = setTimeout(() => {
        debouncedCsSearchQuery.value = newQuery;
      }, 300);
    });
    const orderFilterOptions = common_vendor.ref([
      { label: "全部", value: "all" },
      { label: "待处理", value: "pending" },
      { label: "待付款", value: "待付款" },
      { label: "待发货", value: "待发货" },
      { label: "运输中", value: "运输中" },
      { label: "待验收", value: "待验收" },
      { label: "已完成", value: "已完成" },
      { label: "已取消", value: "已取消" },
      { label: "预警", value: "预警" }
    ]);
    const activityFilterOptions = common_vendor.ref([
      { label: "全部", value: "all" },
      { label: "促销", value: "促销" },
      { label: "新品", value: "新品" },
      { label: "补贴", value: "补贴" },
      { label: "预告", value: "预告" },
      { label: "行情", value: "行情" },
      { label: "公告", value: "公告" }
    ]);
    const csFilterStatusOptions = common_vendor.ref([
      { label: "全部", value: "all" },
      { label: "未读", value: "unread" },
      { label: "待回复", value: "待回复" },
      { label: "处理中", value: "处理中" },
      { label: "已解决", value: "已解决" },
      { label: "已关闭", value: "已关闭" }
    ]);
    const csIssueTypeOptions = common_vendor.ref([
      { label: "全部类型", value: "all" },
      { label: "订单问题", value: "订单问题" },
      { label: "支付问题", value: "支付问题" },
      { label: "物流问题", value: "物流问题" },
      { label: "发票问题", value: "发票问题" },
      { label: "账户问题", value: "账户问题" },
      { label: "投诉建议", value: "投诉建议" },
      { label: "活动咨询", value: "活动咨询" },
      { label: "质检纠纷", value: "质检纠纷" },
      { label: "冷链异常", value: "冷链异常" },
      { label: "损耗理赔", value: "损耗理赔" }
    ]);
    const fetchSystemMessages = () => {
      common_vendor.index.showLoading({ title: "加载系统消息..." });
      setTimeout(() => {
        const mockData = [
          {
            id: 1,
            type: "highRiskOp",
            icon: "🛡️",
            title: "账户安全提醒",
            content: "您在新设备（iPhone）登录，如非本人操作请修改密码",
            deviceInfo: "iPhone 15 Pro（iOS 17.4）",
            operationType: "登录",
            time: "14:25",
            date: "2025-03-18",
            read: false,
            loginLocation: "北京市",
            loginIp: "123.123.123.123"
          },
          {
            id: 2,
            type: "platformOperation",
            icon: "⚙️",
            title: "支付系统维护通知",
            content: "3月20日 00:00-06:00 银联通道升级维护",
            maintenanceTime: "3月20日 00:00-06:00",
            impactScope: "银行卡支付、提现到账",
            time: "09:30",
            date: "2025-03-18",
            read: false
          },
          {
            id: 3,
            type: "ruleUpdate",
            icon: "📄",
            title: "生鲜商品售后规则更新",
            content: "《生鲜类商品争议处理规范》修订生效公告",
            effectiveTime: "2025年4月1日 00:00",
            oldRule: "签收后24小时内可申请腐烂赔付",
            newRule: "需提供签收时完整开箱视频（≤30秒）",
            impactAfterDate: "4月1日后订单需录制开箱视频作为理赔依据",
            impactHistory: "历史订单仍按原规则执行（凭证：物流签收单+照片）",
            time: "15:00",
            date: "2025-03-17",
            read: false
          },
          {
            id: 5,
            type: "globalAnnouncement",
            icon: "📢",
            title: "台风“木兰”配送延迟公告",
            content: "受台风影响，7月15-18日 广东、福建产区发货及物流时效调整",
            affectedTimeRange: "7月15-18日",
            pausedServices: "汕头、汕尾、厦门产区直发",
            delayedRegions: "珠三角地区预计延后48-72小时",
            emergencyAction: "已发货订单可冻结冷链时效，解冻后继续配送",
            suggestion1: "暂勿下单受灾产区商品，可切换至华中/华北仓库",
            suggestion2: "已下单用户可免费取消订单（无违约金）",
            time: "08:00",
            date: "2025-07-14",
            read: false
          }
        ];
        systemMessages.value = mockData;
        updateUnreadCount();
        common_vendor.index.hideLoading();
      }, 1e3);
    };
    const fetchOrderMessages = () => {
      common_vendor.index.showLoading({ title: "加载订单消息..." });
      setTimeout(() => {
        const mockData = [
          {
            id: 101,
            title: "您的订单 20240715123456 已发货！",
            content: "订单 20240715123456 已发货",
            orderId: "20240715123456",
            productName: "山东红富士苹果",
            productSpec: "5吨",
            productImage: "https://placehold.co/100x100/FF5733/FFFFFF?text=Apple",
            status: "运输中",
            statusClass: "status-shipping",
            alertIcon: "",
            time: "14:30",
            date: "2025-07-15",
            read: false,
            actionButtonText: "查看物流",
            actionButtonClass: "action-primary"
          },
          {
            id: 102,
            title: "订单 20240715987654 待支付定金",
            content: "订单 20240715987654 待支付定金",
            orderId: "20240715987654",
            productName: "新疆哈密瓜",
            productSpec: "100箱",
            productImage: "https://placehold.co/100x100/33FF57/FFFFFF?text=Melon",
            status: "待付款",
            statusClass: "status-pending-payment",
            alertIcon: "⏰",
            time: "10:00",
            date: "2025-07-15",
            read: false,
            actionButtonText: "去支付",
            actionButtonClass: "action-primary"
          },
          {
            id: 103,
            title: "订单 20240715543210 已到达，请尽快验收",
            content: "订单 20240715543210 已到达，请尽快验收",
            orderId: "20240715543210",
            productName: "智利车厘子",
            productSpec: "50kg",
            productImage: "https://placehold.co/100x100/3366FF/FFFFFF?text=Cherry",
            status: "待验收",
            statusClass: "status-pending-acceptance",
            alertIcon: "",
            time: "09:00",
            date: "2025-07-15",
            read: false,
            actionButtonText: "去验收",
            actionButtonClass: "action-primary"
          },
          {
            id: 104,
            title: "供应商已拒绝您的订单 20240715000001",
            content: "供应商已拒绝您的订单 20240715000001",
            orderId: "20240715000001",
            productName: "泰国榴莲",
            productSpec: "20个",
            productImage: "https://placehold.co/100x100/FF33CC/FFFFFF?text=Durian",
            status: "已取消",
            statusClass: "status-cancelled",
            alertIcon: "",
            time: "昨天 18:00",
            date: "2025-07-14",
            read: true,
            actionButtonText: ""
          },
          {
            id: 105,
            title: "订单 20240715111111 温度异常告警！",
            content: "订单 20240715111111 运输中发生温度异常，请处理",
            orderId: "20240715111111",
            productName: "进口三文鱼",
            productSpec: "100kg",
            productImage: "https://placehold.co/100x100/FF9933/FFFFFF?text=Salmon",
            status: "预警",
            statusClass: "status-warning",
            alertIcon: "🌡️",
            time: "昨天 11:00",
            date: "2025-07-14",
            read: false,
            actionButtonText: "处理异议",
            actionButtonClass: "action-danger"
          },
          {
            id: 106,
            title: "订单 20240715222222 已完成",
            content: "您的订单 20240715222222 已成功完成",
            orderId: "20240715222222",
            productName: "新鲜猪肉",
            productSpec: "500kg",
            productImage: "https://placehold.co/100x100/99FF33/FFFFFF?text=Pork",
            status: "已完成",
            statusClass: "status-completed",
            alertIcon: "",
            time: "2025-07-13 16:00",
            date: "2025-07-13",
            read: true,
            actionButtonText: ""
          }
        ].sort((a, b) => /* @__PURE__ */ new Date(b.date + " " + b.time) - /* @__PURE__ */ new Date(a.date + " " + a.time));
        orderMessages.value = mockData;
        updateUnreadCount();
        common_vendor.index.hideLoading();
      }, 1e3);
    };
    const fetchPromotionMessages = () => {
      common_vendor.index.showLoading({ title: "加载活动消息..." });
      setTimeout(() => {
        const mockData = [
          {
            id: "ACT20240715001",
            // Changed ID to match activityDetail mock data
            title: "【限时特惠】山东寿光黄瓜直供，今日下单每斤立减0.5元！",
            type: "限时折扣",
            typeClass: "type-discount",
            benefitSummary: "每斤立减0.5元，最高可省500元",
            validity: "距结束 2天 10小时",
            targetAudience: "所有用户",
            image: "https://placehold.co/400x200/4CAF50/FFFFFF?text=CucumberPromo",
            sender: "平台活动",
            time: "10:00",
            date: "2025-07-15",
            read: false,
            isNew: true
          },
          {
            id: "ACT20240801002",
            // Changed ID to match activityDetail mock data
            title: "新供应商入驻！云南有机蓝莓基地直采，首单享95折！",
            type: "新品",
            typeClass: "type-new",
            benefitSummary: "首单享95折，最高优惠200元",
            validity: "活动时间：2025.07.15 - 2025.07.31",
            targetAudience: "新注册用户",
            image: "https://placehold.co/400x200/2196F3/FFFFFF?text=BlueberryNew",
            sender: "平台活动",
            time: "09:00",
            date: "2025-07-15",
            read: false,
            isNew: true
          },
          {
            id: "ACT20240601003",
            // Changed ID to match activityDetail mock data
            title: "【冷链补贴】本周下单冻品，平台补贴50%运费！",
            type: "运费补贴",
            typeClass: "type-subsidy",
            benefitSummary: "冷链运费补贴50%，单笔最高补贴100元",
            validity: "本周有效 (2025.07.14 - 2025.07.20)",
            targetAudience: "所有冻品采购商",
            image: "https://placehold.co/400x200/FF9800/FFFFFF?text=ColdChain",
            sender: "平台活动",
            time: "昨天 14:00",
            date: "2025-07-14",
            read: false,
            isNew: false
          },
          {
            id: "ACT20240713004",
            // New ID for consistency
            title: "【采购节预告】8.8生鲜狂欢周，百万优惠券即将开抢！",
            type: "节日大促",
            typeClass: "type-festival",
            benefitSummary: "领200元、500元、1000元大额优惠券",
            validity: "抢券时间：2025.08.01 00:00",
            targetAudience: "所有用户",
            image: "https://placehold.co/400x200/E91E63/FFFFFF?text=ShoppingFestival",
            sender: "平台活动",
            time: "2025-07-13 16:00",
            date: "2025-07-13",
            read: true,
            isNew: false
          },
          {
            id: "ACT20240712005",
            // New ID for consistency
            title: "【行情快报】南方暴雨影响荔枝供应，价格预计上涨10%",
            type: "行情资讯",
            typeClass: "type-market",
            benefitSummary: "建议近期谨慎采购，关注后续市场动态",
            validity: "发布时间：2025.07.12",
            targetAudience: "水果采购商",
            image: "https://placehold.co/400x200/607D8B/FFFFFF?text=LycheeMarket",
            sender: "市场行情",
            time: "2025-07-12 08:00",
            date: "2025-07-12",
            read: true,
            isNew: false
          }
        ].sort((a, b) => /* @__PURE__ */ new Date(b.date + " " + b.time) - /* @__PURE__ */ new Date(a.date + " " + a.time));
        promotionMessages.value = mockData;
        updateUnreadCount();
        common_vendor.index.hideLoading();
      }, 1e3);
    };
    const fetchServiceMessages = () => {
      common_vendor.index.showLoading({ title: "加载客服消息..." });
      setTimeout(() => {
        const mockData = [
          {
            id: "chat_order_123",
            // Changed ID to match customerServiceChat mock data
            title: "关于订单20240715123456验收问题的咨询",
            lastMessage: "您好，您提交的验收问题已转交专员处理，请耐心等待。",
            time: "16:30",
            date: "2025-07-15",
            unread: 1,
            // 修改 avatar 路径，使用 backendStaticBaseUrl 拼接
            avatar: backendStaticBaseUrl.value + "/messages/cs_online_en.png",
            senderType: "平台客服",
            senderName: "客服001",
            orderId: "20240715123456",
            issueType: "订单问题",
            issueTypeClass: "issue-order",
            status: "待回复",
            statusClass: "cs-status-pending",
            priority: "normal",
            priorityIcon: ""
          },
          {
            id: "chat_new_issue",
            // Changed ID to match customerServiceChat mock data
            title: "运费补贴申请进度咨询",
            lastMessage: "您的运费补贴申请已通过审核，预计3个工作日到账。",
            time: "15:45",
            date: "2025-07-15",
            unread: 0,
            // 修改 avatar 路径，使用 backendStaticBaseUrl 拼接
            avatar: backendStaticBaseUrl.value + "/messages/cs_aftersales_en.png",
            senderType: "平台客服",
            senderName: "客服002",
            orderId: "",
            issueType: "支付问题",
            issueTypeClass: "issue-payment",
            status: "已解决",
            statusClass: "cs-status-resolved",
            priority: "normal",
            priorityIcon: ""
          },
          {
            id: "chat_resolved_issue",
            // Changed ID to match customerServiceChat mock data
            title: "【重要通知】关于XX产区物流延误的说明",
            lastMessage: "受恶劣天气影响，XX产区物流将有24-48小时延误。",
            time: "11:00",
            date: "2025-07-14",
            unread: 0,
            // 修改 avatar 路径，使用 backendStaticBaseUrl 拼接
            avatar: backendStaticBaseUrl.value + "/messages/system_notification.png",
            // System icon for system push
            senderType: "系统通知",
            senderName: "",
            orderId: "",
            issueType: "物流问题",
            issueTypeClass: "issue-logistics",
            status: "已关闭",
            statusClass: "cs-status-closed",
            priority: "high",
            priorityIcon: "❗️"
            // High priority
          },
          {
            id: 304,
            title: "您的纠纷工单（编号：D20240715001）已受理",
            lastMessage: "您提交的质检纠纷工单已受理，专员将在24小时内联系您。",
            time: "昨天 09:00",
            date: "2025-07-14",
            unread: 1,
            // 修改 avatar 路径，使用 backendStaticBaseUrl 拼接
            avatar: backendStaticBaseUrl.value + "/messages/cs_online_en.png",
            senderType: "平台客服",
            senderName: "客服008",
            orderId: "20240715000001",
            issueType: "质检纠纷",
            issueTypeClass: "issue-quality",
            status: "处理中",
            statusClass: "cs-status-processing",
            priority: "normal",
            priorityIcon: ""
          },
          {
            id: 305,
            title: "如何开电子发票？",
            lastMessage: "请在“我的订单”中选择对应订单，点击“申请发票”即可。",
            time: "2025-07-13 10:00",
            date: "2025-07-13",
            unread: 0,
            // 修改 avatar 路径，使用 backendStaticBaseUrl 拼接
            avatar: backendStaticBaseUrl.value + "/messages/cs_online_en.png",
            senderType: "平台客服",
            senderName: "客服005",
            orderId: "",
            issueType: "发票问题",
            issueTypeClass: "issue-invoice",
            status: "已解决",
            statusClass: "cs-status-resolved",
            priority: "normal",
            priorityIcon: ""
          }
        ].sort((a, b) => /* @__PURE__ */ new Date(b.date + " " + b.time) - /* @__PURE__ */ new Date(a.date + " " + a.time));
        serviceMessages.value = mockData;
        updateUnreadCount();
        common_vendor.index.hideLoading();
      }, 1e3);
    };
    const groupedSystemMessages = common_vendor.computed(() => {
      return groupMessagesByDate(systemMessages.value);
    });
    const filteredOrderMessages = common_vendor.computed(() => {
      let filtered = orderMessages.value;
      if (filterStatus.value !== "all") {
        if (filterStatus.value === "pending") {
          filtered = filtered.filter(
            (msg) => msg.status === "待付款" || msg.status === "待验收" || msg.status === "预警"
          );
        } else {
          filtered = filtered.filter((msg) => msg.status === filterStatus.value);
        }
      }
      if (debouncedSearchQuery.value) {
        const query = debouncedSearchQuery.value.toLowerCase();
        filtered = filtered.filter(
          (msg) => msg.orderId.toLowerCase().includes(query) || msg.productName.toLowerCase().includes(query)
        );
      }
      return filtered;
    });
    const filteredPromotionMessages = common_vendor.computed(() => {
      let filtered = promotionMessages.value;
      if (activityFilterStatus.value !== "all") {
        filtered = filtered.filter((msg) => msg.type === activityFilterStatus.value);
      }
      if (debouncedActivitySearchQuery.value) {
        const query = debouncedActivitySearchQuery.value.toLowerCase();
        filtered = filtered.filter(
          (msg) => msg.title.toLowerCase().includes(query) || msg.benefitSummary.toLowerCase().includes(query) || msg.sender.toLowerCase().includes(query) || msg.type.toLowerCase().includes(query)
        );
      }
      return filtered;
    });
    const filteredServiceMessages = common_vendor.computed(() => {
      let filtered = serviceMessages.value;
      if (csFilterStatus.value !== "all") {
        if (csFilterStatus.value === "unread") {
          filtered = filtered.filter((msg) => msg.unread > 0);
        } else {
          filtered = filtered.filter((msg) => msg.status === csFilterStatus.value);
        }
      }
      if (csFilterIssueType.value !== "all") {
        filtered = filtered.filter((msg) => msg.issueType === csFilterIssueType.value);
      }
      if (debouncedCsSearchQuery.value) {
        const query = debouncedCsSearchQuery.value.toLowerCase();
        filtered = filtered.filter(
          (msg) => msg.title.toLowerCase().includes(query) || msg.orderId && msg.orderId.toLowerCase().includes(query) || msg.lastMessage.toLowerCase().includes(query)
        );
      }
      return filtered;
    });
    const groupedOrderMessages = common_vendor.computed(() => {
      const sortedMessages = [...filteredOrderMessages.value].sort((a, b) => {
        const dateA = /* @__PURE__ */ new Date(a.date + " " + a.time.replace("今天 ", "").replace("昨天 ", ""));
        const dateB = /* @__PURE__ */ new Date(b.date + " " + b.time.replace("今天 ", "").replace("昨天 ", ""));
        return dateB - dateA;
      });
      return groupMessagesByDate(sortedMessages);
    });
    const groupedPromotionMessages = common_vendor.computed(() => {
      const sortedMessages = [...filteredPromotionMessages.value].sort((a, b) => {
        const dateA = /* @__PURE__ */ new Date(a.date + " " + a.time.replace("今天 ", "").replace("昨天 ", ""));
        const dateB = /* @__PURE__ */ new Date(b.date + " " + b.time.replace("今天 ", "").replace("昨天 ", ""));
        return dateB - dateA;
      });
      return groupMessagesByDate(sortedMessages);
    });
    const groupedServiceMessages = common_vendor.computed(() => {
      const sortedMessages = [...filteredServiceMessages.value].sort((a, b) => {
        const dateA = /* @__PURE__ */ new Date(a.date + " " + a.time.replace("今天 ", "").replace("昨天 ", ""));
        const dateB = /* @__PURE__ */ new Date(b.date + " " + b.time.replace("今天 ", "").replace("昨天 ", ""));
        return dateB - dateA;
      });
      return groupMessagesByDate(sortedMessages);
    });
    const groupMessagesByDate = (messages) => {
      const groups = {};
      messages.forEach((message) => {
        const displayDate = formatDisplayDate(message.date);
        if (!groups[displayDate]) {
          groups[displayDate] = [];
        }
        groups[displayDate].push(message);
      });
      return groups;
    };
    const formatDisplayDate = (dateString) => {
      const today = /* @__PURE__ */ new Date();
      const yesterday = new Date(today);
      yesterday.setDate(today.getDate() - 1);
      const messageDate = new Date(dateString);
      if (messageDate.toDateString() === today.toDateString()) {
        return "今天";
      } else if (messageDate.toDateString() === yesterday.toDateString()) {
        return "昨天";
      } else {
        return dateString;
      }
    };
    const hasMore = common_vendor.ref(false);
    const loading = common_vendor.ref(false);
    const startX = common_vendor.ref(0);
    const lastTransformX = common_vendor.ref(0);
    const swipeStates = common_vendor.ref({});
    const onTouchStart = (e, messageId) => {
      for (const id in swipeStates.value) {
        if (id !== messageId && swipeStates.value[id] !== 0) {
          swipeStates.value[id] = 0;
        }
      }
      startX.value = e.touches[0].clientX;
      lastTransformX.value = swipeStates.value[messageId] || 0;
    };
    const onTouchMove = (e, messageId) => {
      const currentX = e.touches[0].clientX;
      let deltaX = currentX - startX.value;
      if (lastTransformX.value < 0) {
        deltaX += lastTransformX.value;
      }
      const minSwipe = -DELETE_BUTTON_WIDTH - 20;
      let newTransformX = Math.max(minSwipe, Math.min(0, deltaX));
      swipeStates.value = {
        ...swipeStates.value,
        [messageId]: newTransformX
      };
    };
    const onTouchEnd = (e, messageId) => {
      const currentTransformX = swipeStates.value[messageId] || 0;
      const threshold = -DELETE_BUTTON_WIDTH / 2;
      if (currentTransformX < threshold) {
        swipeStates.value[messageId] = -DELETE_BUTTON_WIDTH;
      } else {
        swipeStates.value[messageId] = 0;
      }
    };
    const switchTab = (index) => {
      currentTab.value = index;
      swipeStates.value = {};
      searchQuery.value = "";
      debouncedSearchQuery.value = "";
      filterStatus.value = "all";
      activitySearchQuery.value = "";
      debouncedActivitySearchQuery.value = "";
      activityFilterStatus.value = "all";
      csSearchQuery.value = "";
      debouncedCsSearchQuery.value = "";
      csFilterStatus.value = "all";
      csFilterIssueType.value = "all";
      if (index === 0) {
        fetchSystemMessages();
      } else if (index === 1) {
        fetchOrderMessages();
      } else if (index === 2) {
        fetchPromotionMessages();
      } else if (index === 3) {
        fetchServiceMessages();
      }
    };
    const markAllAsRead = () => {
      if (currentTab.value === 0) {
        systemMessages.value.forEach((msg) => msg.read = true);
      } else if (currentTab.value === 1) {
        filteredOrderMessages.value.forEach((msg) => msg.read = true);
      } else if (currentTab.value === 2) {
        filteredPromotionMessages.value.forEach((msg) => msg.read = true);
      } else if (currentTab.value === 3) {
        filteredServiceMessages.value.forEach((msg) => msg.unread = 0);
      }
      updateUnreadCount();
      common_vendor.index.showToast({
        title: "已标记当前分类所有消息为已读",
        icon: "none"
      });
    };
    const handleMessageClick = (message, category) => {
      if ((swipeStates.value[message.id] || 0) !== 0) {
        swipeStates.value[message.id] = 0;
        return;
      }
      let targetPage = "";
      let queryParams = {};
      let baseUrl = "";
      if (category === "order") {
        targetPage = "orderDetail/orderDetail";
        queryParams = { orderId: message.orderId };
        baseUrl = "/pages/messages/";
        if (message.read !== void 0) {
          message.read = true;
        }
      } else if (category === "activity") {
        targetPage = "activityDetail/activityDetail";
        queryParams = { activityId: message.id };
        baseUrl = "/pages/messages/";
        if (message.read !== void 0) {
          message.read = true;
        }
      } else if (category === "customerService") {
        targetPage = "costomerServiceChat/costomerServiceChat";
        queryParams = { chatId: message.id };
        baseUrl = "/pages/messages/";
        if (message.unread !== void 0) {
          message.unread = 0;
        }
      } else if (category === "system") {
        if (message.type === "highRiskOp" || message.type === "riskWarning") {
          targetPage = "accountSecurity";
          queryParams = { messageData: encodeURIComponent(JSON.stringify(message)) };
          baseUrl = "/pages/messages/systemNotification/";
        } else if (message.type === "platformOperation") {
          targetPage = "platformOperation";
          queryParams = { noticeData: encodeURIComponent(JSON.stringify(message)) };
          baseUrl = "/pages/messages/systemNotification/";
        } else if (message.type === "ruleUpdate") {
          targetPage = "ruleUpdate";
          queryParams = { messageData: encodeURIComponent(JSON.stringify(message)) };
          baseUrl = "/pages/messages/systemNotification/";
        } else if (message.type === "globalAnnouncement") {
          targetPage = "globalAnnouncement";
          queryParams = { messageData: encodeURIComponent(JSON.stringify(message)) };
          baseUrl = "/pages/messages/systemNotification/";
        } else {
          common_vendor.index.__f__("warn", "at pages/messages/messages.vue:1148", "Non-specific system message, no corresponding detail page navigation logic.");
          common_vendor.index.showToast({
            title: "非特定系统消息",
            icon: "none"
          });
          return;
        }
        if (message.read !== void 0) {
          message.read = true;
        }
      }
      updateUnreadCount();
      if (targetPage && baseUrl) {
        const queryString = Object.keys(queryParams).map((key) => `${key}=${queryParams[key]}`).join("&");
        common_vendor.index.navigateTo({
          url: `${baseUrl}${targetPage}${queryString ? "?" + queryString : ""}`
        });
      } else {
        common_vendor.index.__f__("warn", "at pages/messages/messages.vue:1169", "Unknown message category or undefined navigation logic, cannot navigate:", category);
        common_vendor.index.showToast({
          title: "未知消息类型",
          icon: "none"
        });
      }
    };
    const handleOrderAction = (message) => {
      common_vendor.index.showToast({
        title: `执行订单操作: ${message.actionButtonText} (订单号: ${message.orderId})`,
        icon: "none"
      });
      if (message.status === "待付款")
        ;
      else if (message.status === "待验收")
        ;
      else if (message.status === "运输中")
        ;
      else if (message.status === "预警")
        ;
    };
    const filterByStatus = (status) => {
      filterStatus.value = status;
    };
    const performSearch = () => {
      if (searchQuery.value) {
        common_vendor.index.showToast({
          title: `开始搜索: ${searchQuery.value}`,
          icon: "none"
        });
      }
    };
    const clearSearch = () => {
      searchQuery.value = "";
    };
    const deleteOrderMessage = (id) => {
      common_vendor.index.showModal({
        title: "确认删除",
        content: "确定要删除这条订单消息吗？",
        success: (res) => {
          if (res.confirm) {
            orderMessages.value = orderMessages.value.filter((msg) => msg.id !== id);
            updateUnreadCount();
            common_vendor.index.showToast({
              title: "删除成功",
              icon: "success"
            });
            swipeStates.value[id] = 0;
          } else {
            swipeStates.value[id] = 0;
          }
        }
      });
    };
    const filterByActivityStatus = (type) => {
      activityFilterStatus.value = type;
    };
    const performActivitySearch = () => {
      if (activitySearchQuery.value) {
        common_vendor.index.showToast({
          title: `开始搜索活动: ${activitySearchQuery.value}`,
          icon: "none"
        });
      }
    };
    const clearActivitySearch = () => {
      activitySearchQuery.value = "";
    };
    const deletePromotionMessage = (id) => {
      common_vendor.index.showModal({
        title: "确认删除",
        content: "确定要删除这条活动消息吗？",
        success: (res) => {
          if (res.confirm) {
            promotionMessages.value = promotionMessages.value.filter((msg) => msg.id !== id);
            updateUnreadCount();
            common_vendor.index.showToast({
              title: "删除成功",
              icon: "success"
            });
            swipeStates.value[id] = 0;
          } else {
            swipeStates.value[id] = 0;
          }
        }
      });
    };
    const startNewCustomerServiceChat = () => {
      common_vendor.index.showToast({
        title: "跳转到发起新客服咨询页面",
        icon: "none"
      });
    };
    const filterByCsStatus = (status) => {
      csFilterStatus.value = status;
    };
    const filterByCsIssueType = (type) => {
      csFilterIssueType.value = type;
    };
    const performCsSearch = () => {
      if (csSearchQuery.value) {
        common_vendor.index.showToast({
          title: `开始搜索客服消息: ${csSearchQuery.value}`,
          icon: "none"
        });
      }
    };
    const deleteServiceMessage = (id) => {
      common_vendor.index.showModal({
        title: "确认删除",
        content: "确定要删除这条客服消息吗？",
        success: (res) => {
          if (res.confirm) {
            serviceMessages.value = serviceMessages.value.filter((msg) => msg.id !== id);
            updateUnreadCount();
            common_vendor.index.showToast({
              title: "删除成功",
              icon: "success"
            });
            swipeStates.value[id] = 0;
          } else {
            swipeStates.value[id] = 0;
          }
        }
      });
    };
    const updateUnreadCount = () => {
      messageTabs.value[0].unread = systemMessages.value.filter((msg) => !msg.read).length;
      messageTabs.value[1].unread = orderMessages.value.filter((msg) => !msg.read).length;
      messageTabs.value[2].unread = promotionMessages.value.filter((msg) => !msg.read).length;
      messageTabs.value[3].unread = serviceMessages.value.reduce((total, service) => total + (service.unread || 0), 0);
    };
    const onImageError = (type, item) => {
      common_vendor.index.__f__("error", "at pages/messages/messages.vue:1324", `Failed to load ${type} type image, item:`, item);
      if (type === "product") {
        item.productImage = "https://placehold.co/100x100/FF0000/FFFFFF?text=Error";
      } else if (type === "promotion") {
        item.image = "https://placehold.co/400x200/FF0000/FFFFFF?text=ImageError";
      } else if (type === "service") {
        item.avatar = backendStaticBaseUrl.value + "/messages/cs_error_fallback.png";
      }
    };
    const loadMore = () => {
      if (loading.value || !hasMore.value)
        return;
      loading.value = true;
      common_vendor.index.showLoading({ title: "加载中..." });
      setTimeout(() => {
        loading.value = false;
        common_vendor.index.hideLoading();
      }, 1e3);
    };
    const onRefresh = () => {
      common_vendor.index.showLoading({ title: "刷新中..." });
      filterStatus.value = "all";
      searchQuery.value = "";
      activityFilterStatus.value = "all";
      activitySearchQuery.value = "";
      csFilterStatus.value = "all";
      csFilterIssueType.value = "all";
      csSearchQuery.value = "";
      swipeStates.value = {};
      fetchSystemMessages();
      fetchOrderMessages();
      fetchPromotionMessages();
      fetchServiceMessages();
      setTimeout(() => {
        common_vendor.index.hideLoading();
        common_vendor.index.stopPullDownRefresh();
      }, 1e3);
    };
    common_vendor.onShow(() => {
      common_vendor.index.$emit("tabPageShow");
      fetchSystemMessages();
      fetchOrderMessages();
      fetchPromotionMessages();
      fetchServiceMessages();
    });
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: backendStaticBaseUrl.value + "/messages/clear_icon.png",
        b: common_vendor.o(markAllAsRead),
        c: common_vendor.f(messageTabs.value, (tab, index, i0) => {
          return common_vendor.e({
            a: tab.icon,
            b: common_vendor.n(currentTab.value === index ? "tab-icon-active" : ""),
            c: common_vendor.t(tab.name),
            d: tab.unread > 0
          }, tab.unread > 0 ? {
            e: common_vendor.t(tab.unread)
          } : {}, {
            f: index,
            g: common_vendor.n(currentTab.value === index ? "tab-active" : ""),
            h: common_vendor.o(($event) => switchTab(index), index)
          });
        }),
        d: currentTab.value === 0
      }, currentTab.value === 0 ? {
        e: common_vendor.f(groupedSystemMessages.value, (group, date, i0) => {
          return {
            a: common_vendor.t(date),
            b: common_vendor.f(group, (message, index, i1) => {
              return common_vendor.e({
                a: common_vendor.t(message.icon),
                b: common_vendor.t(message.title),
                c: common_vendor.t(message.time),
                d: common_vendor.t(message.content),
                e: message.type === "highRiskOp"
              }, message.type === "highRiskOp" ? {
                f: common_vendor.t(message.deviceIp),
                g: common_vendor.t(message.operationType)
              } : message.type === "platformOperation" ? {
                i: common_vendor.t(message.maintenanceTime),
                j: common_vendor.t(message.impactScope)
              } : message.type === "ruleUpdate" ? {
                l: common_vendor.t(message.effectiveTime),
                m: common_vendor.t(message.newRule)
              } : message.type === "riskWarning" ? {
                o: common_vendor.t(message.riskObject),
                p: common_vendor.t(message.potentialImpact)
              } : message.type === "globalAnnouncement" ? {
                r: common_vendor.t(message.affectedTimeRange),
                s: common_vendor.t(message.pausedServices)
              } : {}, {
                h: message.type === "platformOperation",
                k: message.type === "ruleUpdate",
                n: message.type === "riskWarning",
                q: message.type === "globalAnnouncement",
                t: !message.read
              }, !message.read ? {} : {}, {
                v: common_vendor.n(
                  // 根据消息类型添加特定颜色类和边框
                  message.type === "highRiskOp" ? "high-risk-card" : ""
                ),
                w: common_vendor.n(message.type === "riskWarning" ? "high-risk-card" : ""),
                x: common_vendor.n(message.type === "platformOperation" ? "maintenance-card" : ""),
                y: common_vendor.n(message.type === "ruleUpdate" ? "rule-card" : ""),
                z: common_vendor.n(message.type === "globalAnnouncement" ? "announcement-card" : ""),
                A: index,
                B: common_vendor.o(($event) => handleMessageClick(message, "system"), index)
              });
            }),
            c: date
          };
        })
      } : {}, {
        f: currentTab.value === 1
      }, currentTab.value === 1 ? common_vendor.e({
        g: common_vendor.o(performSearch),
        h: searchQuery.value,
        i: common_vendor.o(($event) => searchQuery.value = $event.detail.value),
        j: searchQuery.value
      }, searchQuery.value ? {
        k: common_vendor.o(clearSearch)
      } : {}, {
        l: debouncedSearchQuery.value
      }, debouncedSearchQuery.value ? {
        m: common_vendor.t(debouncedSearchQuery.value)
      } : {}, {
        n: common_vendor.f(orderFilterOptions.value, (status, sIndex, i0) => {
          return {
            a: common_vendor.t(status.label),
            b: sIndex,
            c: common_vendor.n(filterStatus.value === status.value ? "filter-tab-active" : ""),
            d: common_vendor.o(($event) => filterByStatus(status.value), sIndex)
          };
        }),
        o: common_vendor.f(groupedOrderMessages.value, (group, date, i0) => {
          return common_vendor.e({
            a: common_vendor.t(date),
            b: common_vendor.f(group, (message, k1, i1) => {
              return common_vendor.e({
                a: common_vendor.t(message.title),
                b: common_vendor.t(message.time),
                c: message.alertIcon
              }, message.alertIcon ? {
                d: common_vendor.t(message.alertIcon)
              } : {}, {
                e: message.productImage
              }, message.productImage ? {
                f: message.productImage,
                g: common_vendor.o(($event) => onImageError("product", message), message.id)
              } : {}, {
                h: common_vendor.t(message.productName),
                i: common_vendor.t(message.productSpec),
                j: common_vendor.t(message.status),
                k: common_vendor.n(message.statusClass),
                l: common_vendor.t(message.orderId),
                m: !message.read
              }, !message.read ? {} : {}, {
                n: message.actionButtonText
              }, message.actionButtonText ? {
                o: common_vendor.t(message.actionButtonText),
                p: common_vendor.n(message.actionButtonClass),
                q: common_vendor.o(($event) => handleOrderAction(message), message.id)
              } : {}, {
                r: `translateX(${swipeStates.value[message.id] || 0}rpx)`,
                s: common_vendor.o(($event) => deleteOrderMessage(message.id), message.id),
                t: message.id,
                v: common_vendor.o(($event) => onTouchStart($event, message.id), message.id),
                w: common_vendor.o(($event) => onTouchMove($event, message.id), message.id),
                x: common_vendor.o(($event) => onTouchEnd($event, message.id), message.id),
                y: common_vendor.o(($event) => handleMessageClick(message, "order"), message.id)
              });
            }),
            c: group.length === 0 && searchQuery.value === "" && filterStatus.value === "all"
          }, group.length === 0 && searchQuery.value === "" && filterStatus.value === "all" ? {} : group.length === 0 ? {} : {}, {
            d: group.length === 0,
            e: date
          });
        })
      }) : {}, {
        p: currentTab.value === 2
      }, currentTab.value === 2 ? common_vendor.e({
        q: common_vendor.o(performActivitySearch),
        r: activitySearchQuery.value,
        s: common_vendor.o(($event) => activitySearchQuery.value = $event.detail.value),
        t: activitySearchQuery.value
      }, activitySearchQuery.value ? {
        v: common_vendor.o(clearActivitySearch)
      } : {}, {
        w: debouncedActivitySearchQuery.value
      }, debouncedActivitySearchQuery.value ? {
        x: common_vendor.t(debouncedActivitySearchQuery.value)
      } : {}, {
        y: common_vendor.f(activityFilterOptions.value, (type, tIndex, i0) => {
          return {
            a: common_vendor.t(type.label),
            b: tIndex,
            c: common_vendor.n(activityFilterStatus.value === type.value ? "filter-tab-active" : ""),
            d: common_vendor.o(($event) => filterByActivityStatus(type.value), tIndex)
          };
        }),
        z: common_vendor.f(groupedPromotionMessages.value, (group, date, i0) => {
          return common_vendor.e({
            a: common_vendor.t(date),
            b: common_vendor.f(group, (message, k1, i1) => {
              return common_vendor.e({
                a: message.image,
                b: common_vendor.o(($event) => onImageError("promotion", message), message.id),
                c: common_vendor.t(message.title),
                d: common_vendor.t(message.time),
                e: message.isNew
              }, message.isNew ? {} : !message.read ? {} : {}, {
                f: !message.read,
                g: common_vendor.t(message.type),
                h: common_vendor.n(message.typeClass),
                i: common_vendor.t(message.sender),
                j: common_vendor.t(message.benefitSummary),
                k: common_vendor.t(message.validity),
                l: common_vendor.t(message.targetAudience),
                m: `translateX(${swipeStates.value[message.id] || 0}rpx)`,
                n: common_vendor.o(($event) => deletePromotionMessage(message.id), message.id),
                o: message.id,
                p: common_vendor.o(($event) => onTouchStart($event, message.id), message.id),
                q: common_vendor.o(($event) => onTouchMove($event, message.id), message.id),
                r: common_vendor.o(($event) => onTouchEnd($event, message.id), message.id),
                s: common_vendor.o(($event) => handleMessageClick(message, "activity"), message.id)
              });
            }),
            c: group.length === 0 && activitySearchQuery.value === "" && activityFilterStatus.value === "all"
          }, group.length === 0 && activitySearchQuery.value === "" && activityFilterStatus.value === "all" ? {} : group.length === 0 ? {} : {}, {
            d: group.length === 0,
            e: date
          });
        })
      }) : {}, {
        A: currentTab.value === 3
      }, currentTab.value === 3 ? common_vendor.e({
        B: common_vendor.o(startNewCustomerServiceChat),
        C: common_vendor.o(performCsSearch),
        D: csSearchQuery.value,
        E: common_vendor.o(($event) => csSearchQuery.value = $event.detail.value),
        F: csSearchQuery.value
      }, csSearchQuery.value ? {
        G: common_vendor.o(clearSearch)
      } : {}, {
        H: debouncedCsSearchQuery.value
      }, debouncedCsSearchQuery.value ? {
        I: common_vendor.t(debouncedCsSearchQuery.value)
      } : {}, {
        J: common_vendor.f(csFilterStatusOptions.value, (status, sIndex, i0) => {
          return {
            a: common_vendor.t(status.label),
            b: sIndex,
            c: common_vendor.n(csFilterStatus.value === status.value ? "filter-tab-active" : ""),
            d: common_vendor.o(($event) => filterByCsStatus(status.value), sIndex)
          };
        }),
        K: common_vendor.f(csIssueTypeOptions.value, (type, tIndex, i0) => {
          return {
            a: common_vendor.t(type.label),
            b: tIndex,
            c: common_vendor.n(csFilterIssueType.value === type.value ? "filter-tab-active" : ""),
            d: common_vendor.o(($event) => filterByCsIssueType(type.value), tIndex)
          };
        }),
        L: common_vendor.f(groupedServiceMessages.value, (group, date, i0) => {
          return common_vendor.e({
            a: common_vendor.t(date),
            b: common_vendor.f(group, (message, k1, i1) => {
              return common_vendor.e({
                a: message.avatar,
                b: common_vendor.o(($event) => onImageError("service", message), message.id),
                c: common_vendor.t(message.title),
                d: common_vendor.t(message.time),
                e: message.priorityIcon
              }, message.priorityIcon ? {
                f: common_vendor.t(message.priorityIcon)
              } : {}, {
                g: message.senderType === "agent"
              }, message.senderType === "agent" ? {
                h: common_vendor.t(message.senderName)
              } : {
                i: common_vendor.t(message.senderType)
              }, {
                j: message.orderId
              }, message.orderId ? {
                k: common_vendor.t(message.orderId)
              } : {}, {
                l: common_vendor.t(message.lastMessage),
                m: common_vendor.t(message.issueType),
                n: common_vendor.n(message.issueTypeClass),
                o: common_vendor.t(message.status),
                p: common_vendor.n(message.statusClass),
                q: message.unread > 0
              }, message.unread > 0 ? {
                r: common_vendor.t(message.unread)
              } : {}, {
                s: `translateX(${swipeStates.value[message.id] || 0}rpx)`,
                t: common_vendor.o(($event) => deleteServiceMessage(message.id), message.id),
                v: message.id,
                w: common_vendor.o(($event) => onTouchStart($event, message.id), message.id),
                x: common_vendor.o(($event) => onTouchMove($event, message.id), message.id),
                y: common_vendor.o(($event) => onTouchEnd($event, message.id), message.id),
                z: common_vendor.o(($event) => handleMessageClick(message, "customerService"), message.id)
              });
            }),
            c: group.length === 0 && csSearchQuery.value === "" && csFilterStatus.value === "all" && csFilterIssueType.value === "all"
          }, group.length === 0 && csSearchQuery.value === "" && csFilterStatus.value === "all" && csFilterIssueType.value === "all" ? {} : group.length === 0 ? {} : {}, {
            d: group.length === 0,
            e: date
          });
        })
      }) : {}, {
        M: hasMore.value
      }, hasMore.value ? {} : {}, {
        N: common_vendor.o(loadMore),
        O: common_vendor.o(onRefresh)
      });
    };
  }
};
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/messages/messages.js.map
