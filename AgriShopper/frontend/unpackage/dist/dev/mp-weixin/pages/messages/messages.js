"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const store = require("../../store.js");
const __default__ = {
  name: "Messages",
  onShow() {
    common_vendor.index.$emit("tabPageShow");
  },
  onPullDownRefresh() {
    this.onRefresh();
  },
  onReachBottom() {
    this.loadMore();
  }
};
const _sfc_main = /* @__PURE__ */ Object.assign(__default__, {
  setup(__props) {
    const messageTabs = common_vendor.ref([
      { name: "系统通知", unread: 2, icon: "/static/messages/system_notification.png" },
      { name: "订单消息", unread: 3, icon: "/static/messages/order_message.png" },
      { name: "活动消息", unread: 1, icon: "/static/messages/activity_message.png" },
      { name: "客服消息", unread: 5, icon: "/static/messages/customer_service_message.png" }
    ]);
    const currentTab = common_vendor.ref(0);
    const isLoggedIn = common_vendor.ref(false);
    common_vendor.ref(false);
    const userAvatar = common_vendor.ref("/static/tabbar/user.png");
    const systemMessages = common_vendor.ref([
      {
        id: 1,
        title: "账户安全提醒",
        content: "您的账户于2024年6月18日10:05在新设备上登录，如非本人操作，请及时修改密码。",
        time: "10:05",
        date: "2024-06-18",
        read: false
      },
      {
        id: 2,
        title: "系统升级通知",
        content: "系统将于今晚22:00-23:00进行例行维护升级，期间可能影响部分功能使用。",
        time: "09:30",
        date: "2024-06-18",
        read: true
      }
    ]);
    const orderMessages = common_vendor.ref([
      {
        id: 1,
        title: "订单发货通知",
        content: "您购买的有机蔬菜已发货",
        time: "14:30",
        date: "2024-06-18",
        status: "已发货",
        orderId: "202406180001",
        image: "https://placehold.co/100x100/cccccc/ffffff?text=Order1",
        read: false
      },
      {
        id: 2,
        title: "订单已送达",
        content: "您的订单已送达指定地点",
        time: "11:20",
        date: "2024-06-18",
        status: "已完成",
        orderId: "202406170002",
        image: "https://placehold.co/100x100/cccccc/ffffff?text=Order2",
        read: true
      }
    ]);
    const promotionMessages = common_vendor.ref([
      {
        id: 1,
        title: "限时特惠活动",
        content: "新鲜水果限时8折，快来抢购！",
        time: "15:00",
        date: "2024-06-18",
        tag: "限时特惠",
        validDate: "2024.6.18-6.20",
        image: "https://placehold.co/200x100/cccccc/ffffff?text=Promo",
        read: false
      }
    ]);
    const serviceMessages = common_vendor.ref([
      {
        id: 1,
        name: "在线客服",
        // 请将 'cs_online_en.png' 替换为您实际的文件名
        avatar: "/static/messages/cs_online_en.png",
        lastMessage: "您好，请问有什么可以帮您？",
        time: "16:30",
        unread: 1
      },
      {
        id: 2,
        name: "售后客服",
        // 请将 'cs_aftersales_en.png' 替换为您实际的文件名
        avatar: "/static/messages/cs_aftersales_en.png",
        lastMessage: "您的退款申请已处理完成",
        time: "15:45",
        unread: 0
      }
    ]);
    const groupedSystemMessages = common_vendor.computed(() => {
      return groupMessagesByDate(systemMessages.value);
    });
    const groupedOrderMessages = common_vendor.computed(() => {
      return groupMessagesByDate(orderMessages.value);
    });
    const groupedPromotionMessages = common_vendor.computed(() => {
      return groupMessagesByDate(promotionMessages.value);
    });
    const groupMessagesByDate = (messages) => {
      const groups = {};
      messages.forEach((message) => {
        if (!groups[message.date]) {
          groups[message.date] = [];
        }
        groups[message.date].push(message);
      });
      return groups;
    };
    const hasMore = common_vendor.ref(false);
    const loading = common_vendor.ref(false);
    const switchTab = (index) => {
      currentTab.value = index;
    };
    const markAllAsRead = () => {
      systemMessages.value.forEach((msg) => msg.read = true);
      orderMessages.value.forEach((msg) => msg.read = true);
      promotionMessages.value.forEach((msg) => msg.read = true);
      serviceMessages.value.forEach((msg) => msg.unread = 0);
      updateUnreadCount();
    };
    const openMessage = (message) => {
      if (message.read !== void 0) {
        message.read = true;
      }
      if (message.unread !== void 0) {
        message.unread = 0;
      }
      updateUnreadCount();
      common_vendor.index.showToast({
        title: "查看消息详情：" + message.title,
        icon: "none"
      });
    };
    const enterChat = (service) => {
      service.unread = 0;
      updateUnreadCount();
      common_vendor.index.navigateTo({
        url: `/pages/chat/chat?id=${service.id}`
      });
    };
    const updateUnreadCount = () => {
      messageTabs.value[0].unread = systemMessages.value.filter((msg) => !msg.read).length;
      messageTabs.value[1].unread = orderMessages.value.filter((msg) => !msg.read).length;
      messageTabs.value[2].unread = promotionMessages.value.filter((msg) => !msg.read).length;
      messageTabs.value[3].unread = serviceMessages.value.reduce((total, service) => total + service.unread, 0);
    };
    const onImageError = (type, item) => {
      common_vendor.index.__f__("error", "at pages/messages/messages.vue:320", `Failed to load ${type} image for item:`, item);
      if (type === "order" || type === "promotion") {
        item.image = "https://placehold.co/100x100/ff0000/ffffff?text=Error";
      } else if (type === "service") {
        item.avatar = "/static/messages/cs_error_fallback.png";
      }
    };
    const loadMore = () => {
      if (loading.value || !hasMore.value)
        return;
      loading.value = true;
      setTimeout(() => {
        loading.value = false;
      }, 1e3);
    };
    const onRefresh = () => {
      setTimeout(() => {
        common_vendor.index.stopPullDownRefresh();
      }, 1e3);
    };
    const checkLoginStatus = () => {
      try {
        const userInfo = store.store.getUserInfo();
        if (userInfo && userInfo.openid) {
          isLoggedIn.value = true;
          if (userInfo.avatar) {
            userAvatar.value = userInfo.avatar;
          }
        } else {
          isLoggedIn.value = false;
        }
      } catch (error) {
        common_vendor.index.__f__("log", "at pages/messages/messages.vue:356", "检查登录状态失败:", error);
        isLoggedIn.value = false;
      }
    };
    common_vendor.onMounted(() => {
      checkLoginStatus();
    });
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_assets._imports_0$1,
        b: common_vendor.o(markAllAsRead),
        c: common_vendor.f(messageTabs.value, (tab, index, i0) => {
          return common_vendor.e({
            a: tab.icon,
            b: common_vendor.t(tab.name),
            c: tab.unread > 0
          }, tab.unread > 0 ? {
            d: common_vendor.t(tab.unread)
          } : {}, {
            e: index,
            f: common_vendor.n(currentTab.value === index ? "tab-active" : ""),
            g: common_vendor.o(($event) => switchTab(index), index)
          });
        }),
        d: currentTab.value === 0
      }, currentTab.value === 0 ? {
        e: common_vendor.f(groupedSystemMessages.value, (group, date, i0) => {
          return {
            a: common_vendor.t(date),
            b: common_vendor.f(group, (message, index, i1) => {
              return {
                a: common_vendor.t(message.title),
                b: common_vendor.t(message.time),
                c: common_vendor.t(message.content),
                d: index,
                e: common_vendor.o(($event) => openMessage(message), index)
              };
            }),
            c: date
          };
        })
      } : {}, {
        f: currentTab.value === 1
      }, currentTab.value === 1 ? {
        g: common_vendor.f(groupedOrderMessages.value, (group, date, i0) => {
          return {
            a: common_vendor.t(date),
            b: common_vendor.f(group, (message, index, i1) => {
              return {
                a: common_vendor.t(message.title),
                b: common_vendor.t(message.time),
                c: common_vendor.t(message.content),
                d: common_vendor.t(message.status),
                e: common_vendor.t(message.orderId),
                f: index,
                g: common_vendor.o(($event) => openMessage(message), index)
              };
            }),
            c: date
          };
        })
      } : {}, {
        h: currentTab.value === 2
      }, currentTab.value === 2 ? {
        i: common_vendor.f(groupedPromotionMessages.value, (group, date, i0) => {
          return {
            a: common_vendor.t(date),
            b: common_vendor.f(group, (message, index, i1) => {
              return {
                a: message.image,
                b: common_vendor.o(($event) => onImageError("promotion", message), index),
                c: common_vendor.t(message.title),
                d: common_vendor.t(message.time),
                e: common_vendor.t(message.content),
                f: common_vendor.t(message.tag),
                g: common_vendor.t(message.validDate),
                h: index,
                i: common_vendor.o(($event) => openMessage(message), index)
              };
            }),
            c: date
          };
        })
      } : {}, {
        j: currentTab.value === 3
      }, currentTab.value === 3 ? {
        k: common_vendor.f(serviceMessages.value, (message, index, i0) => {
          return {
            a: message.avatar,
            b: common_vendor.o(($event) => onImageError("service", message), index),
            c: common_vendor.t(message.name),
            d: common_vendor.t(message.time),
            e: common_vendor.t(message.lastMessage),
            f: index,
            g: common_vendor.o(($event) => enterChat(message), index)
          };
        })
      } : {}, {
        l: hasMore.value
      }, hasMore.value ? {} : {}, {
        m: common_vendor.o(loadMore),
        n: common_vendor.o(onRefresh)
      });
    };
  }
});
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/messages/messages.js.map
