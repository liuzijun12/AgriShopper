"use strict";
const common_vendor = require("../common/vendor.js");
const __default__ = {
  options: {
    virtualHost: true
  }
};
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  ...__default__,
  __name: "BottomTabBar",
  setup(__props) {
    const currentTab = common_vendor.ref(0);
    const tabList = [
      {
        pagePath: "/pages/index/index",
        text: "首页",
        iconPath: "/static/tabbar/home.png",
        selectedIconPath: "/static/tabbar/home-active.png"
      },
      {
        pagePath: "/pages/productList/productList",
        text: "商品",
        iconPath: "/static/tabbar/list.png",
        selectedIconPath: "/static/tabbar/list-active.png"
      },
      {
        pagePath: "/pages/messages/messages",
        text: "消息",
        iconPath: "/static/tabbar/message.png",
        selectedIconPath: "/static/tabbar/message-active.png"
      },
      {
        pagePath: "/pages/shoppingCart/shoppingCart",
        text: "购物车",
        iconPath: "/static/tabbar/cart.png",
        selectedIconPath: "/static/tabbar/cart-active.png"
      },
      {
        pagePath: "/pages/my/my",
        text: "我的",
        iconPath: "/static/tabbar/user.png",
        selectedIconPath: "/static/tabbar/user-active.png"
      }
    ];
    const initCurrentTab = () => {
      const pages = getCurrentPages();
      if (pages.length > 0) {
        const currentPage = pages[pages.length - 1];
        const path = "/" + currentPage.route;
        const index = tabList.findIndex((tab) => tab.pagePath === path);
        if (index !== -1) {
          currentTab.value = index;
        }
      }
    };
    const switchTab = (path, index) => {
      if (currentTab.value === index)
        return;
      currentTab.value = index;
      common_vendor.index.reLaunch({
        url: path,
        success: () => {
          initCurrentTab();
        },
        fail: (err) => {
          common_vendor.index.__f__("error", "at components/BottomTabBar.vue:91", "Navigation failed:", err);
          common_vendor.index.showToast({
            title: "页面跳转失败",
            icon: "none"
          });
        }
      });
    };
    common_vendor.index.$on("tabPageShow", () => {
      initCurrentTab();
    });
    common_vendor.onUnmounted(() => {
      common_vendor.index.$off("tabPageShow");
    });
    common_vendor.onMounted(() => {
      initCurrentTab();
    });
    return (_ctx, _cache) => {
      return {
        a: common_vendor.f(tabList, (item, index, i0) => {
          return {
            a: currentTab.value === index ? item.selectedIconPath : item.iconPath,
            b: common_vendor.t(item.text),
            c: common_vendor.n(currentTab.value === index ? "active-text" : ""),
            d: index,
            e: common_vendor.n(currentTab.value === index ? "active" : ""),
            f: common_vendor.o(($event) => switchTab(item.pagePath, index), index)
          };
        })
      };
    };
  }
});
const Component = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-032475b5"]]);
wx.createComponent(Component);
//# sourceMappingURL=../../.sourcemap/mp-weixin/components/BottomTabBar.js.map
