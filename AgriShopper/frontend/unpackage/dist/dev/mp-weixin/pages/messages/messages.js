"use strict";
const common_vendor = require("../../common/vendor.js");
const __default__ = {
  name: "Messages",
  onShow() {
    common_vendor.index.$emit("tabPageShow");
  },
  // 页面配置
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
      { name: "系统通知", unread: 2 },
      { name: "订单消息", unread: 3 },
      { name: "活动消息", unread: 1 },
      { name: "客服消息", unread: 5 }
    ]);
    const currentTab = common_vendor.ref(0);
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
        read: false
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
        image: "https://readdy.ai/api/search-image?query=Fresh%20organic%20vegetables%20package%2C%20high-quality%20details%2C%20clear%20and%20sharp%2C%20isolated%20on%20white%20background%2C%20centered%20composition%2C%20soft%20lighting%2C%20no%20shadows%2C%20no%20text.&width=100&height=100",
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
        image: "https://readdy.ai/api/search-image?query=Delivered%20package%20with%20check%20mark%2C%20high-quality%20details%2C%20clear%20and%20sharp%2C%20isolated%20on%20white%20background%2C%20centered%20composition%2C%20soft%20lighting%2C%20no%20shadows%2C%20no%20text.&width=100&height=100",
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
        image: "https://readdy.ai/api/search-image?query=Fresh%20fruits%20sale%20promotion%2C%20high-quality%20details%2C%20clear%20and%20sharp%2C%20isolated%20on%20white%20background%2C%20centered%20composition%2C%20soft%20lighting%2C%20no%20shadows%2C%20no%20text.&width=200&height=100",
        read: false
      }
    ]);
    const serviceMessages = common_vendor.ref([
      {
        id: 1,
        name: "在线客服",
        avatar: "/static/robot.png",
        lastMessage: "您好，请问有什么可以帮您？",
        time: "16:30",
        unread: 1
      },
      {
        id: 2,
        name: "售后客服",
        avatar: "/static/service.png",
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
      messageTabs.value.forEach((tab) => tab.unread = 0);
    };
    const openMessage = (message) => {
      message.read = true;
      updateUnreadCount();
      common_vendor.index.showToast({
        title: "查看消息详情：" + message.title,
        icon: "none"
      });
    };
    const enterChat = (service) => {
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
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.o(markAllAsRead),
        b: common_vendor.f(messageTabs.value, (tab, index, i0) => {
          return common_vendor.e({
            a: common_vendor.t(tab.name),
            b: tab.unread > 0
          }, tab.unread > 0 ? {
            c: common_vendor.t(tab.unread)
          } : {}, {
            d: index,
            e: common_vendor.n(currentTab.value === index ? "tab-active" : ""),
            f: common_vendor.o(($event) => switchTab(index), index)
          });
        }),
        c: currentTab.value === 0
      }, currentTab.value === 0 ? {
        d: common_vendor.f(groupedSystemMessages.value, (group, date, i0) => {
          return {
            a: common_vendor.t(date),
            b: common_vendor.f(group, (message, index, i1) => {
              return common_vendor.e({
                a: common_vendor.t(message.title),
                b: common_vendor.t(message.time),
                c: common_vendor.t(message.content),
                d: !message.read
              }, !message.read ? {} : {}, {
                e: index,
                f: !message.read ? 1 : "",
                g: common_vendor.o(($event) => openMessage(message), index)
              });
            }),
            c: date
          };
        })
      } : {}, {
        e: currentTab.value === 1
      }, currentTab.value === 1 ? {
        f: common_vendor.f(groupedOrderMessages.value, (group, date, i0) => {
          return {
            a: common_vendor.t(date),
            b: common_vendor.f(group, (message, index, i1) => {
              return common_vendor.e({
                a: message.image,
                b: common_vendor.t(message.title),
                c: common_vendor.t(message.time),
                d: common_vendor.t(message.content),
                e: common_vendor.t(message.status),
                f: common_vendor.t(message.orderId),
                g: !message.read
              }, !message.read ? {} : {}, {
                h: index,
                i: !message.read ? 1 : "",
                j: common_vendor.o(($event) => openMessage(message), index)
              });
            }),
            c: date
          };
        })
      } : {}, {
        g: currentTab.value === 2
      }, currentTab.value === 2 ? {
        h: common_vendor.f(groupedPromotionMessages.value, (group, date, i0) => {
          return {
            a: common_vendor.t(date),
            b: common_vendor.f(group, (message, index, i1) => {
              return common_vendor.e({
                a: message.image,
                b: common_vendor.t(message.title),
                c: common_vendor.t(message.time),
                d: common_vendor.t(message.content),
                e: common_vendor.t(message.tag),
                f: common_vendor.t(message.validDate),
                g: !message.read
              }, !message.read ? {} : {}, {
                h: index,
                i: !message.read ? 1 : "",
                j: common_vendor.o(($event) => openMessage(message), index)
              });
            }),
            c: date
          };
        })
      } : {}, {
        i: currentTab.value === 3
      }, currentTab.value === 3 ? {
        j: common_vendor.f(serviceMessages.value, (message, index, i0) => {
          return common_vendor.e({
            a: message.avatar,
            b: common_vendor.t(message.name),
            c: common_vendor.t(message.time),
            d: common_vendor.t(message.lastMessage),
            e: message.unread > 0
          }, message.unread > 0 ? {
            f: common_vendor.t(message.unread)
          } : {}, {
            g: index,
            h: common_vendor.o(($event) => enterChat(message), index)
          });
        })
      } : {}, {
        k: hasMore.value
      }, hasMore.value ? {} : {}, {
        l: common_vendor.o(loadMore),
        m: common_vendor.o(onRefresh)
      });
    };
  }
});
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/messages/messages.js.map
