"use strict";
Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const common_vendor = require("./common/vendor.js");
const store = require("./store.js");
if (!Math) {
  "./pages/wxLogin/wxLogin.js";
  "./pages/index/index.js";
  "./pages/productList/productList.js";
  "./pages/messages/messages.js";
  "./pages/shoppingCart/shoppingCart.js";
  "./pages/my/my.js";
  "./pages/searchProduct/searchProduct.js";
}
const _sfc_main = {
  globalData: {
    userInfo: null,
    cartItems: [],
    isAppReady: false
  },
  onLaunch: function() {
    common_vendor.index.__f__("log", "at App.vue:12", "App Launch");
    this.globalData.isAppReady = true;
    store.store.setAppReady(true);
    this.checkUserLogin();
  },
  onShow: function() {
    common_vendor.index.__f__("log", "at App.vue:22", "App Show");
  },
  onHide: function() {
    common_vendor.index.__f__("log", "at App.vue:26", "App Hide");
  },
  methods: {
    // 检查用户登录状态
    checkUserLogin() {
      try {
        const userInfo = store.store.getUserInfo();
        if (userInfo && userInfo.openid) {
          setTimeout(() => {
            common_vendor.index.switchTab({
              url: "/pages/index/index"
            });
          }, 1e3);
        }
      } catch (error) {
        common_vendor.index.__f__("log", "at App.vue:43", "检查用户登录状态失败:", error);
      }
    },
    // 获取用户信息的方法，需要在页面中通过按钮点击调用
    getUserProfile() {
      return new Promise((resolve, reject) => {
        common_vendor.index.getUserProfile({
          desc: "用于完善会员资料",
          success: (res) => {
            this.globalData.userInfo = res.userInfo;
            store.store.setUserInfo(res.userInfo);
            resolve(res.userInfo);
          },
          fail: (err) => {
            common_vendor.index.__f__("error", "at App.vue:58", "Failed to get user info:", err);
            reject(err);
          }
        });
      });
    }
  }
};
function createApp() {
  const app = common_vendor.createSSRApp(_sfc_main);
  app.config.globalProperties.$store = store.store;
  app.config.globalProperties.$onLaunched = new Promise((resolve) => {
    app.config.globalProperties.$isResolveOnLaunched = resolve;
  });
  app.config.errorHandler = (err, instance, info) => {
    common_vendor.index.__f__("error", "at main.js:29", "Vue Error:", err, info);
  };
  app.config.globalProperties.$filters = {
    // 价格格式化
    formatPrice(price) {
      return "¥" + Number(price).toFixed(2);
    },
    // 日期格式化
    formatDate(date) {
      return new Date(date).toLocaleDateString();
    }
  };
  return {
    app
  };
}
createApp().app.mount("#app");
exports.createApp = createApp;
//# sourceMappingURL=../.sourcemap/mp-weixin/app.js.map
