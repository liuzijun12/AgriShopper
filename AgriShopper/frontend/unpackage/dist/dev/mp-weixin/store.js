"use strict";
const common_vendor = require("./common/vendor.js");
const store = {
  state: common_vendor.reactive({
    userInfo: null,
    isAppReady: false,
    currentTab: 0,
    cartItems: [],
    favorites: []
  }),
  setUserInfo(userInfo) {
    this.state.userInfo = userInfo;
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
