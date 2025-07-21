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
    const formatMessageTime = (timeString) => {
      if (!timeString)
        return "";
      try {
        const messageTime = new Date(timeString);
        const now = /* @__PURE__ */ new Date();
        const isToday = messageTime.toDateString() === now.toDateString();
        if (isToday) {
          const hours = messageTime.getHours().toString().padStart(2, "0");
          const minutes = messageTime.getMinutes().toString().padStart(2, "0");
          return `${hours}:${minutes}`;
        } else {
          const month = (messageTime.getMonth() + 1).toString();
          const day = messageTime.getDate().toString();
          return `${month}/${day}`;
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/messages/messages.vue:233", "时间格式化错误:", error);
        return "";
      }
    };
    const handleImageError = (e) => {
      common_vendor.index.__f__("error", "at pages/messages/messages.vue:240", "Image failed to load:", e.detail.src);
      const target = e.target;
      if (target) {
        target.style.display = "none";
      }
    };
    const handleImageLoad = (e) => {
      common_vendor.index.__f__("log", "at pages/messages/messages.vue:250", "Image loaded successfully:", e.detail.src);
    };
    const handleAvatarError = (message) => {
      common_vendor.index.__f__("error", "at pages/messages/messages.vue:255", "Avatar failed to load:", message.productImage);
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
      if (customerServiceMessages.value.length === 0) {
        common_vendor.index.__f__("log", "at pages/messages/messages.vue:307", "添加测试数据");
        customerServiceMessages.value = [
          {
            id: "test1",
            type: "customerService",
            title: "测试客服咨询",
            content: "您好，请问有什么可以帮助您的吗？",
            time: (/* @__PURE__ */ new Date()).toISOString(),
            // 使用ISO格式，让格式化函数处理
            read: false,
            icon: getMessageImageUrl("server.png"),
            sessionId: "test1",
            productId: null,
            lastMessageTime: (/* @__PURE__ */ new Date()).toISOString(),
            unreadCount: 2,
            // 测试未读消息数
            hasRedDot: true,
            // 测试红点显示
            productImage: null
          }
        ];
      }
      updateUnreadCount();
    };
    const loadCustomerServiceMessages = async () => {
      try {
        const userInfo = store.store.getUserInfo();
        common_vendor.index.__f__("log", "at pages/messages/messages.vue:334", "当前用户信息:", userInfo);
        if (!userInfo || !userInfo.id) {
          common_vendor.index.__f__("log", "at pages/messages/messages.vue:337", "用户未登录，清空客服消息列表");
          customerServiceMessages.value = [];
          return;
        }
        const userId = userInfo.id;
        common_vendor.index.__f__("log", "at pages/messages/messages.vue:343", "开始加载用户ID为", userId, "的客服消息");
        const response = await api_customerService.customerServiceApi.getUserSessions(userId);
        if (response.code === 200) {
          const sessions = response.data || [];
          for (const session of sessions) {
            try {
              const unreadResponse = await api_customerService.customerServiceApi.countSessionUnreadNonUserMessages(session.id);
              if (unreadResponse.code === 200) {
                session.unreadCount = unreadResponse.data || 0;
                session.hasRedDot = session.unreadCount > 0;
              } else {
                common_vendor.index.__f__("error", "at pages/messages/messages.vue:360", `❌ 获取会话 ${session.id} 未读消息数失败:`, unreadResponse);
                session.unreadCount = 0;
                session.hasRedDot = false;
              }
              await loadLastMessage(session);
              if (session.productId) {
                await loadProductInfo(session);
              }
            } catch (error) {
              common_vendor.index.__f__("error", "at pages/messages/messages.vue:374", `❌ 处理会话 ${session.id} 失败:`, error);
              session.unreadCount = 0;
              session.hasRedDot = false;
            }
          }
          const sessionMap = /* @__PURE__ */ new Map();
          sessions.forEach((session) => {
            const productId = session.productId;
            const key = productId ? `product_${productId}` : "general";
            if (!sessionMap.has(key)) {
              const messageContent = session.lastMessageContent || (session.productId ? `关于商品 ${session.productId} 的咨询` : "一般客服咨询");
              sessionMap.set(key, {
                id: session.id,
                type: "customerService",
                // 添加类型字段
                title: session.title || (session.productId ? `商品咨询` : "客服咨询"),
                content: messageContent,
                time: session.createTime,
                // 使用原始时间字符串，让格式化函数处理
                read: session.unreadCount === 0,
                // 根据未读消息数判断是否已读
                icon: getMessageImageUrl("server.png"),
                sessionId: session.id,
                productId: session.productId,
                lastMessageTime: session.lastMessageTime,
                unreadCount: session.unreadCount,
                // 使用实际的未读消息数
                hasRedDot: session.hasRedDot,
                // 使用实际的红点状态
                productImage: session.productImage || null
              });
            }
          });
          customerServiceMessages.value = Array.from(sessionMap.values()).sort((a, b) => {
            const timeA = a.lastMessageTime ? new Date(a.lastMessageTime).getTime() : 0;
            const timeB = b.lastMessageTime ? new Date(b.lastMessageTime).getTime() : 0;
            return timeB - timeA;
          });
        } else {
          common_vendor.index.__f__("error", "at pages/messages/messages.vue:417", "获取会话列表失败:", response.message);
          customerServiceMessages.value = [];
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/messages/messages.vue:422", "加载客服消息错误:", error);
        customerServiceMessages.value = [];
      }
    };
    const loadLastMessage = async (session) => {
      try {
        const response = await api_customerService.customerServiceApi.getSessionMessages(session.id);
        if (response.code === 200 && response.data && response.data.length > 0) {
          const messages = response.data.sort((a, b) => {
            return new Date(a.createTime) - new Date(b.createTime);
          });
          const lastMessage = messages[messages.length - 1];
          const messageContent = lastMessage.messageContent || lastMessage.content || lastMessage.message || lastMessage.text || lastMessage.body || lastMessage.detail || "暂无消息";
          session.lastMessageContent = messageContent;
          session.lastMessageTime = lastMessage.createTime || session.createTime;
        } else {
          session.lastMessageContent = session.productId ? `关于商品 ${session.productId} 的咨询` : "一般客服咨询";
          session.lastMessageTime = session.createTime;
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/messages/messages.vue:451", `获取会话 ${session.id} 最后消息失败:`, error);
        session.lastMessageContent = session.productId ? `关于商品 ${session.productId} 的咨询` : "一般客服咨询";
        session.lastMessageTime = session.createTime;
      }
    };
    const loadProductInfo = async (session) => {
      try {
        const productResponse = await common_vendor.index.request({
          url: `${config_env.env.getConfig().baseUrl}/api/products/${session.productId}`,
          method: "GET",
          header: {
            "Content-Type": "application/json"
          }
        });
        if (productResponse.statusCode === 200 && productResponse.data.code === 200) {
          const product = productResponse.data.data;
          session.title = `${product.productName}-商品咨询`;
          if (product.mainImageUrl) {
            if (product.mainImageUrl.startsWith("http://") || product.mainImageUrl.startsWith("https://")) {
              session.productImage = product.mainImageUrl;
            } else {
              session.productImage = `${config_env.env.getConfig().baseUrl}/${product.mainImageUrl}`;
            }
          } else {
            session.productImage = null;
          }
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/messages/messages.vue:490", `获取商品 ${session.productId} 信息失败:`, error);
        session.productImage = null;
        session.title = `商品${session.productId}-商品咨询`;
      }
    };
    const updateUnreadCount = () => {
      tabs.value[0].unread = systemMessages.value.filter((m) => !m.read).length;
      tabs.value[1].unread = orderMessages.value.filter((m) => !m.read).length;
      tabs.value[2].unread = activityMessages.value.filter((m) => !m.read).length;
      tabs.value[3].unread = customerServiceMessages.value.reduce((total, message) => {
        return total + (message.unreadCount || 0);
      }, 0);
    };
    const markAllRead = async () => {
      try {
        systemMessages.value.forEach((m) => m.read = true);
        orderMessages.value.forEach((m) => m.read = true);
        activityMessages.value.forEach((m) => m.read = true);
        customerServiceMessages.value.forEach((message) => {
          message.unreadCount = 0;
          message.hasRedDot = false;
        });
        updateUnreadCount();
        common_vendor.index.showToast({
          title: "已标记全部已读",
          icon: "success"
        });
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/messages/messages.vue:532", "标记全部已读失败:", error);
        common_vendor.index.showToast({
          title: "操作失败",
          icon: "error"
        });
      }
    };
    const handleMessageClick = (message) => {
      if (message.type === "customerService") {
        const params = {
          sessionId: message.sessionId
        };
        if (message.productId) {
          params.productId = message.productId;
        }
        const session = customerServiceMessages.value.find((s) => s.sessionId === message.sessionId);
        if (session) {
          session.unreadCount = 0;
          session.hasRedDot = false;
        }
        updateUnreadCount();
        const queryString = Object.keys(params).map((key) => `${key}=${encodeURIComponent(params[key])}`).join("&");
        common_vendor.index.navigateTo({
          url: `/pages/messages/customerServiceChat/customerServiceChat?${queryString}`,
          success: () => {
            common_vendor.index.__f__("log", "at pages/messages/messages.vue:571", "跳转成功");
          },
          fail: (error) => {
            common_vendor.index.__f__("error", "at pages/messages/messages.vue:574", "跳转失败:", error);
            common_vendor.index.showToast({
              title: "跳转失败",
              icon: "error"
            });
          }
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
          common_vendor.index.__f__("log", "at pages/messages/messages.vue:615", "客服会话创建成功:", response.data);
          common_vendor.index.navigateTo({
            url: `/pages/messages/customerServiceChat/customerServiceChat?sessionId=${sessionId}`
          });
        } else {
          common_vendor.index.__f__("error", "at pages/messages/messages.vue:622", "创建客服会话失败:", response.message);
          common_vendor.index.showToast({
            title: "创建会话失败",
            icon: "error"
          });
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/messages/messages.vue:629", "发起客服咨询错误:", error);
        common_vendor.index.showToast({
          title: "网络错误",
          icon: "error"
        });
      }
    };
    common_vendor.onMounted(async () => {
      await loadMessages();
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
      } : common_vendor.e({
        k: common_vendor.f(systemMessages.value, (message, k0, i0) => {
          return {
            a: message.icon,
            b: common_vendor.o(handleImageError, message.id),
            c: common_vendor.o(handleImageLoad, message.id),
            d: common_vendor.t(message.title),
            e: common_vendor.t(formatMessageTime(message.time)),
            f: common_vendor.t(message.content),
            g: message.id,
            h: common_vendor.o(($event) => handleMessageClick(message), message.id)
          };
        }),
        l: tabs.value[0].unread > 0
      }, tabs.value[0].unread > 0 ? {
        m: common_vendor.t(tabs.value[0].unread),
        n: common_vendor.o(markAllRead)
      } : {})) : {}, {
        o: currentTab.value === 1
      }, currentTab.value === 1 ? common_vendor.e({
        p: orderMessages.value.length === 0
      }, orderMessages.value.length === 0 ? {
        q: getMessageImageUrl("express.png"),
        r: common_vendor.o(handleImageError),
        s: common_vendor.o(handleImageLoad)
      } : common_vendor.e({
        t: common_vendor.f(orderMessages.value, (message, k0, i0) => {
          return {
            a: message.icon,
            b: common_vendor.o(handleImageError, message.id),
            c: common_vendor.o(handleImageLoad, message.id),
            d: common_vendor.t(message.title),
            e: common_vendor.t(formatMessageTime(message.time)),
            f: common_vendor.t(message.content),
            g: message.id,
            h: common_vendor.o(($event) => handleMessageClick(message), message.id)
          };
        }),
        v: tabs.value[1].unread > 0
      }, tabs.value[1].unread > 0 ? {
        w: common_vendor.t(tabs.value[1].unread),
        x: common_vendor.o(markAllRead)
      } : {})) : {}, {
        y: currentTab.value === 2
      }, currentTab.value === 2 ? common_vendor.e({
        z: activityMessages.value.length === 0
      }, activityMessages.value.length === 0 ? {
        A: getMessageImageUrl("activity.png"),
        B: common_vendor.o(handleImageError),
        C: common_vendor.o(handleImageLoad)
      } : common_vendor.e({
        D: common_vendor.f(activityMessages.value, (message, k0, i0) => {
          return {
            a: message.icon,
            b: common_vendor.o(handleImageError, message.id),
            c: common_vendor.o(handleImageLoad, message.id),
            d: common_vendor.t(message.title),
            e: common_vendor.t(formatMessageTime(message.time)),
            f: common_vendor.t(message.content),
            g: message.id,
            h: common_vendor.o(($event) => handleMessageClick(message), message.id)
          };
        }),
        E: tabs.value[2].unread > 0
      }, tabs.value[2].unread > 0 ? {
        F: common_vendor.t(tabs.value[2].unread),
        G: common_vendor.o(markAllRead)
      } : {})) : {}, {
        H: currentTab.value === 3
      }, currentTab.value === 3 ? common_vendor.e({
        I: customerServiceMessages.value.length === 0
      }, customerServiceMessages.value.length === 0 ? {
        J: getMessageImageUrl("server.png"),
        K: common_vendor.o(handleImageError),
        L: common_vendor.o(handleImageLoad),
        M: common_vendor.o(startCustomerService)
      } : common_vendor.e({
        N: common_vendor.f(customerServiceMessages.value, (message, k0, i0) => {
          return common_vendor.e({
            a: message.productImage
          }, message.productImage ? {
            b: message.productImage,
            c: common_vendor.o(($event) => handleAvatarError(message), message.id)
          } : {}, {
            d: message.unreadCount > 0
          }, message.unreadCount > 0 ? {
            e: common_vendor.t(message.unreadCount > 99 ? "99+" : message.unreadCount)
          } : {}, {
            f: common_vendor.t(message.title),
            g: common_vendor.t(formatMessageTime(message.lastMessageTime || message.time)),
            h: common_vendor.t(message.content),
            i: message.id,
            j: common_vendor.o(($event) => handleMessageClick(message), message.id)
          });
        }),
        O: tabs.value[3].unread > 0
      }, tabs.value[3].unread > 0 ? {
        P: common_vendor.t(tabs.value[3].unread),
        Q: common_vendor.o(markAllRead)
      } : {})) : {});
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-ecc172b4"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/messages/messages.js.map
