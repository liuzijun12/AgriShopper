"use strict";
const common_vendor = require("./common/vendor.js");
const store = {
  state: common_vendor.reactive({
    userInfo: null,
    isAppReady: false,
    currentTab: 0,
    cartItems: [],
    favorites: [],
    refreshTrigger: 0
    // 添加刷新触发器
  }),
  setUserInfo(userInfo) {
    this.state.userInfo = userInfo;
    this.state.refreshTrigger++;
    try {
      common_vendor.index.setStorageSync("userInfo", userInfo);
    } catch (error) {
      common_vendor.index.__f__("error", "at store.js:21", "保存用户信息失败:", error);
    }
  },
  getUserInfo() {
    if (this.state.userInfo) {
      return this.state.userInfo;
    }
    try {
      const userInfo = common_vendor.index.getStorageSync("userInfo");
      if (userInfo) {
        this.state.userInfo = userInfo;
        return userInfo;
      }
    } catch (error) {
      common_vendor.index.__f__("error", "at store.js:38", "获取用户信息失败:", error);
    }
    return null;
  },
  clearUserInfo() {
    this.state.userInfo = null;
    this.state.refreshTrigger++;
    try {
      common_vendor.index.removeStorageSync("userInfo");
    } catch (error) {
      common_vendor.index.__f__("error", "at store.js:51", "清除用户信息失败:", error);
    }
  },
  clearAllData() {
    this.state.userInfo = null;
    this.state.cartItems = [];
    this.state.favorites = [];
    try {
      common_vendor.index.clearStorageSync();
    } catch (error) {
      common_vendor.index.__f__("error", "at store.js:65", "清除所有数据失败:", error);
    }
  },
  setAppReady(ready) {
    this.state.isAppReady = ready;
  },
  setCurrentTab(index) {
    this.state.currentTab = index;
  },
  addToCart(item) {
    const existingItem = this.state.cartItems.find((i) => i.id === item.id);
    if (existingItem) {
      existingItem.quantity += 1;
    } else {
      this.state.cartItems.push({ ...item, quantity: 1 });
    }
  },
  removeFromCart(itemId) {
    const index = this.state.cartItems.findIndex((i) => i.id === itemId);
    if (index > -1) {
      this.state.cartItems.splice(index, 1);
    }
  },
  toggleFavorite(item) {
    const index = this.state.favorites.findIndex((i) => i.id === item.id);
    if (index > -1) {
      this.state.favorites.splice(index, 1);
    } else {
      this.state.favorites.push(item);
    }
  }
};
exports.store = store;
//# sourceMappingURL=../.sourcemap/mp-weixin/store.js.map
