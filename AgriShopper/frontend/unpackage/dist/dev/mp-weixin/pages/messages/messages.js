"use strict";
const common_vendor = require("../../common/vendor.js");
const config_env = require("../../config/env.js");
const api_customerService = require("../../api/customerService.js");
const store = require("../../store.js");
const _sfc_main = {
  __name: "messages",
  setup(__props, { expose: __expose }) {
    const getImageUrl = (path) => {
      if (!path)
        return "/static/default-product.png";
      if (path.startsWith("http://") || path.startsWith("https://")) {
        return path;
      }
      const config = config_env.env.getConfig();
      return `${config.baseUrl}/${path}`;
    };
    const getMessageImageUrl = (imageName) => {
      return getImageUrl(`static/messages/${imageName}`);
    };
    const handleImageError = (e) => {
      common_vendor.index.__f__("error", "at pages/messages/messages.vue:178", "Image failed to load:", e.detail.src);
      const target = e.target;
      if (target) {
        target.style.display = "none";
      }
    };
    const handleImageLoad = (e) => {
      common_vendor.index.__f__("log", "at pages/messages/messages.vue:188", "Image loaded successfully:", e.detail.src);
    };
    const handleAvatarError = (message) => {
      common_vendor.index.__f__("error", "at pages/messages/messages.vue:193", "Avatar failed to load:", message.productImage);
      message.productImage = null;
    };
    const currentTab = common_vendor.ref(0);
    const systemMessages = common_vendor.ref([]);
    const orderMessages = common_vendor.ref([]);
    const activityMessages = common_vendor.ref([]);
    const customerServiceMessages = common_vendor.ref([]);
    const scrollTop = common_vendor.ref(0);
    const tabs = common_vendor.ref([
      { label: "系统", icon: getMessageImageUrl("system.png"), unread: 0 },
      { label: "订单", icon: getMessageImageUrl("express.png"), unread: 0 },
      { label: "活动", icon: getMessageImageUrl("activity.png"), unread: 0 },
      { label: "客服", icon: getMessageImageUrl("server.png"), unread: 0 }
    ]);
    const switchTab = (index) => {
      currentTab.value = index;
      scrollTop.value = 0;
      loadMessages();
    };
    const loadMessages = async () => {
      systemMessages.value = [];
      orderMessages.value = [];
      activityMessages.value = [];
      await loadCustomerServiceMessages();
      updateUnreadCount();
    };
    const loadCustomerServiceMessages = async () => {
      try {
        const userInfo = store.store.getUserInfo();
        if (!userInfo || !userInfo.id) {
          customerServiceMessages.value = [];
          return;
        }
        const userId = userInfo.id;
        const response = await api_customerService.customerServiceApi.getUserSessions(userId);
        if (response.code === 200 && response.data) {
          const sessions = response.data;
          const sessionMap = /* @__PURE__ */ new Map();
          sessions.forEach((session) => {
            const productId = session.productId;
            const key = productId ? `product_${productId}` : "general";
            if (!sessionMap.has(key)) {
              sessionMap.set(key, {
                id: session.id,
                title: session.productId ? `商品咨询` : "客服咨询",
                content: session.productId ? `关于商品 ${session.productId} 的咨询` : "一般客服咨询",
                time: new Date(session.createTime).toLocaleString("zh-CN"),
                read: false,
                // 这里可以根据实际逻辑判断是否已读
                icon: getMessageImageUrl("server.png"),
                sessionId: session.id,
                productId: session.productId,
                lastMessageTime: session.lastMessageTime,
                unreadCount: 0,
                // 这里可以获取未读消息数
                productImage: null
                // 新增商品图片URL
              });
            }
          });
          customerServiceMessages.value = Array.from(sessionMap.values()).sort((a, b) => {
            const timeA = a.lastMessageTime ? new Date(a.lastMessageTime).getTime() : 0;
            const timeB = b.lastMessageTime ? new Date(b.lastMessageTime).getTime() : 0;
            return timeB - timeA;
          });
          await loadProductInfo();
          common_vendor.index.__f__("log", "at pages/messages/messages.vue:297", "客服会话加载成功:", customerServiceMessages.value);
        } else {
          common_vendor.index.__f__("error", "at pages/messages/messages.vue:299", "加载客服会话失败:", response.message);
          customerServiceMessages.value = [];
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/messages/messages.vue:303", "加载客服消息错误:", error);
        customerServiceMessages.value = [];
      }
    };
    const loadProductInfo = async () => {
      try {
        const sessionsWithProduct = customerServiceMessages.value.filter((msg) => msg.productId);
        for (const message of sessionsWithProduct) {
          try {
            const productResponse = await common_vendor.index.request({
              url: `${config_env.env.getConfig().baseUrl}/api/products/${message.productId}`,
              method: "GET",
              header: {
                "Content-Type": "application/json"
              }
            });
            if (productResponse.statusCode === 200 && productResponse.data.code === 200) {
              const product = productResponse.data.data;
              message.title = `商品咨询：${product.productName}`;
              message.content = `关于商品 ${product.productName} 的咨询`;
              if (product.mainImageUrl) {
                if (product.mainImageUrl.startsWith("http://") || product.mainImageUrl.startsWith("https://")) {
                  message.productImage = product.mainImageUrl;
                } else {
                  message.productImage = `${config_env.env.getConfig().baseUrl}/${product.mainImageUrl}`;
                }
              } else {
                message.productImage = null;
              }
            }
          } catch (error) {
            common_vendor.index.__f__("error", "at pages/messages/messages.vue:344", `获取商品 ${message.productId} 信息失败:`, error);
            message.productImage = null;
          }
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/messages/messages.vue:350", "获取商品信息错误:", error);
      }
    };
    const updateUnreadCount = () => {
      tabs.value[0].unread = systemMessages.value.filter((m) => !m.read).length;
      tabs.value[1].unread = orderMessages.value.filter((m) => !m.read).length;
      tabs.value[2].unread = activityMessages.value.filter((m) => !m.read).length;
      tabs.value[3].unread = customerServiceMessages.value.filter((m) => !m.read).length;
    };
    const markAllRead = () => {
      systemMessages.value.forEach((m) => m.read = true);
      orderMessages.value.forEach((m) => m.read = true);
      activityMessages.value.forEach((m) => m.read = true);
      customerServiceMessages.value.forEach((m) => m.read = true);
      updateUnreadCount();
      common_vendor.index.showToast({
        title: "已标记全部已读",
        icon: "success"
      });
    };
    const handleMessageClick = (message) => {
      message.read = true;
      updateUnreadCount();
      if (currentTab.value === 3) {
        const params = {
          sessionId: message.sessionId
        };
        if (message.productId) {
          params.productId = message.productId;
        }
        const queryString = Object.keys(params).map((key) => `${key}=${encodeURIComponent(params[key])}`).join("&");
        common_vendor.index.navigateTo({
          url: `/pages/messages/customerServiceChat/customerServiceChat?${queryString}`
        });
      } else {
        common_vendor.index.showToast({
          title: "消息详情功能开发中",
          icon: "none"
        });
      }
    };
    const startCustomerService = async () => {
      try {
        const userInfo = store.store.getUserInfo();
        if (!userInfo || !userInfo.id) {
          common_vendor.index.showModal({
            title: "提示",
            content: "请先登录后再使用客服功能",
            showCancel: false
          });
          return;
        }
        const userId = userInfo.id;
        const response = await api_customerService.customerServiceApi.startCustomerService(userId, {
          productId: null,
          orderId: null,
          sessionType: 4
          // 其他类型
        });
        if (response.code === 200) {
          const sessionId = response.data.id;
          common_vendor.index.__f__("log", "at pages/messages/messages.vue:434", "客服会话创建成功:", response.data);
          common_vendor.index.navigateTo({
            url: `/pages/messages/customerServiceChat/customerServiceChat?sessionId=${sessionId}`
          });
        } else {
          common_vendor.index.__f__("error", "at pages/messages/messages.vue:441", "创建客服会话失败:", response.message);
          common_vendor.index.showToast({
            title: "创建会话失败",
            icon: "error"
          });
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/messages/messages.vue:448", "发起客服咨询错误:", error);
        common_vendor.index.showToast({
          title: "网络错误",
          icon: "error"
        });
      }
    };
    common_vendor.onMounted(async () => {
      await loadMessages();
      common_vendor.index.__f__("log", "at pages/messages/messages.vue:461", "Environment config:", config_env.env.getConfig());
      common_vendor.index.__f__("log", "at pages/messages/messages.vue:462", "System image URL:", getMessageImageUrl("system.png"));
      common_vendor.index.__f__("log", "at pages/messages/messages.vue:463", "Express image URL:", getMessageImageUrl("express.png"));
      common_vendor.index.__f__("log", "at pages/messages/messages.vue:464", "Activity image URL:", getMessageImageUrl("activity.png"));
      common_vendor.index.__f__("log", "at pages/messages/messages.vue:465", "Server image URL:", getMessageImageUrl("server.png"));
      common_vendor.index.__f__("log", "at pages/messages/messages.vue:466", "Clear image URL:", getMessageImageUrl("clear.png"));
    });
    const onShow = () => {
      loadMessages();
    };
    __expose({
      onShow
    });
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: getMessageImageUrl("clear.png"),
        b: common_vendor.o(handleImageError),
        c: common_vendor.o(handleImageLoad),
        d: common_vendor.o(markAllRead),
        e: common_vendor.f(tabs.value, (tab, index, i0) => {
          return common_vendor.e({
            a: tab.icon,
            b: common_vendor.o(handleImageError, index),
            c: common_vendor.o(handleImageLoad, index),
            d: common_vendor.t(tab.label),
            e: tab.unread > 0
          }, tab.unread > 0 ? {
            f: common_vendor.t(tab.unread)
          } : {}, {
            g: index,
            h: currentTab.value === index ? 1 : "",
            i: common_vendor.o(($event) => switchTab(index), index)
          });
        }),
        f: currentTab.value === 0
      }, currentTab.value === 0 ? common_vendor.e({
        g: systemMessages.value.length === 0
      }, systemMessages.value.length === 0 ? {
        h: getMessageImageUrl("system.png"),
        i: common_vendor.o(handleImageError),
        j: common_vendor.o(handleImageLoad)
      } : {
        k: common_vendor.f(systemMessages.value, (message, k0, i0) => {
          return common_vendor.e({
            a: message.icon,
            b: common_vendor.o(handleImageError, message.id),
            c: common_vendor.o(handleImageLoad, message.id),
            d: common_vendor.t(message.title),
            e: common_vendor.t(message.time),
            f: common_vendor.t(message.content),
            g: !message.read
          }, !message.read ? {} : {}, {
            h: message.id,
            i: common_vendor.o(($event) => handleMessageClick(message), message.id)
          });
        })
      }) : {}, {
        l: currentTab.value === 1
      }, currentTab.value === 1 ? common_vendor.e({
        m: orderMessages.value.length === 0
      }, orderMessages.value.length === 0 ? {
        n: getMessageImageUrl("express.png"),
        o: common_vendor.o(handleImageError),
        p: common_vendor.o(handleImageLoad)
      } : {
        q: common_vendor.f(orderMessages.value, (message, k0, i0) => {
          return common_vendor.e({
            a: message.icon,
            b: common_vendor.o(handleImageError, message.id),
            c: common_vendor.o(handleImageLoad, message.id),
            d: common_vendor.t(message.title),
            e: common_vendor.t(message.time),
            f: common_vendor.t(message.content),
            g: !message.read
          }, !message.read ? {} : {}, {
            h: message.id,
            i: common_vendor.o(($event) => handleMessageClick(message), message.id)
          });
        })
      }) : {}, {
        r: currentTab.value === 2
      }, currentTab.value === 2 ? common_vendor.e({
        s: activityMessages.value.length === 0
      }, activityMessages.value.length === 0 ? {
        t: getMessageImageUrl("activity.png"),
        v: common_vendor.o(handleImageError),
        w: common_vendor.o(handleImageLoad)
      } : {
        x: common_vendor.f(activityMessages.value, (message, k0, i0) => {
          return common_vendor.e({
            a: message.icon,
            b: common_vendor.o(handleImageError, message.id),
            c: common_vendor.o(handleImageLoad, message.id),
            d: common_vendor.t(message.title),
            e: common_vendor.t(message.time),
            f: common_vendor.t(message.content),
            g: !message.read
          }, !message.read ? {} : {}, {
            h: message.id,
            i: common_vendor.o(($event) => handleMessageClick(message), message.id)
          });
        })
      }) : {}, {
        y: currentTab.value === 3
      }, currentTab.value === 3 ? common_vendor.e({
        z: customerServiceMessages.value.length === 0
      }, customerServiceMessages.value.length === 0 ? {
        A: getMessageImageUrl("server.png"),
        B: common_vendor.o(handleImageError),
        C: common_vendor.o(handleImageLoad),
        D: common_vendor.o(startCustomerService)
      } : {
        E: common_vendor.f(customerServiceMessages.value, (message, k0, i0) => {
          return common_vendor.e({
            a: message.productImage
          }, message.productImage ? {
            b: message.productImage,
            c: common_vendor.o(($event) => handleAvatarError(message), message.id)
          } : {}, {
            d: common_vendor.t(message.title),
            e: common_vendor.t(message.time),
            f: common_vendor.t(message.content),
            g: !message.read
          }, !message.read ? {} : {}, {
            h: message.id,
            i: common_vendor.o(($event) => handleMessageClick(message), message.id)
          });
        })
      }) : {});
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-ecc172b4"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/messages/messages.js.map
