"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const api_cart = require("../../api/cart.js");
const config_env = require("../../config/env.js");
const _sfc_main = {
  __name: "shoppingCart",
  setup(__props) {
    common_vendor.ref("河北张家口市");
    common_vendor.ref(true);
    const loading = common_vendor.ref(false);
    const cartItems = common_vendor.ref([]);
    const getImageUrl = (url) => {
      if (!url)
        return "/static/default-product.png";
      if (url.startsWith("http://") || url.startsWith("https://")) {
        return url;
      }
      const config = config_env.env.getConfig();
      if (url.startsWith("icon/")) {
        return config.baseUrl + "/static/" + url;
      }
      if (url.startsWith("tabbar/")) {
        return config.baseUrl + "/static/" + url;
      }
      if (url.startsWith("Carousel/")) {
        return config.baseUrl + "/static/" + url;
      }
      if (url.startsWith("/static/uploads/")) {
        return config.baseUrl + url;
      }
      if (!url.startsWith("/")) {
        return config.baseUrl + "/static/uploads/" + url;
      }
      return config.baseUrl + url;
    };
    const tagColors = {
      "热卖": "#ffeeee",
      "特惠": "#fff8e6",
      "新品": "#e6f7ff",
      "促销": "#ffebee"
    };
    const loadCartData = async () => {
      try {
        loading.value = true;
        const response = await api_cart.cartApi.getCartList();
        if (response.code === 200 && response.data) {
          cartItems.value = response.data.map((item) => {
            var _a, _b, _c, _d, _e;
            return {
              id: item.id,
              productId: item.productId,
              name: ((_a = item.product) == null ? void 0 : _a.productName) || "未知商品",
              price: parseFloat(item.unitPrice) || 0,
              originalPrice: parseFloat((_b = item.product) == null ? void 0 : _b.costPrice) || 0,
              quantity: item.quantity || 1,
              selected: item.isSelected || false,
              specification: ((_c = item.product) == null ? void 0 : _c.productSpec) || "默认规格",
              stock: ((_d = item.product) == null ? void 0 : _d.stockQuantity) || 0,
              tag: getProductTag(item.product),
              shopId: 1,
              // 默认店铺ID
              image: ((_e = item.product) == null ? void 0 : _e.mainImageUrl) || "/static/default-product.png"
            };
          });
        } else {
          common_vendor.index.__f__("error", "at pages/shoppingCart/shoppingCart.vue:213", "获取购物车数据失败:", response.message);
          common_vendor.index.showToast({
            title: response.message || "获取购物车数据失败",
            icon: "error"
          });
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/shoppingCart/shoppingCart.vue:220", "加载购物车数据失败:", error);
        common_vendor.index.showToast({
          title: "加载购物车数据失败",
          icon: "error"
        });
      } finally {
        loading.value = false;
      }
    };
    const getProductTag = (product) => {
      if (!product)
        return "";
      if (product.isHotProduct)
        return "热卖";
      if (product.isNewProduct)
        return "新品";
      if (product.costPrice && product.costPrice > product.productPrice)
        return "特惠";
      return "促销";
    };
    common_vendor.onMounted(() => {
      loadCartData();
    });
    const goToSearch = () => {
      common_vendor.index.navigateTo({
        url: "/pages/search/search"
      });
    };
    const getProductIcon = (name) => {
      const icons = {
        "西红柿": "🍅",
        "鸡蛋": "🥚",
        "菠菜": "🥬",
        "苹果": "🍎",
        "香蕉": "🍌",
        "土豆": "🥔",
        "白菜": "🥬"
      };
      return icons[name.replace(/有机|农家|新鲜|优质|红富士/g, "")] || "🛒";
    };
    const totalSaved = common_vendor.computed(() => {
      return cartItems.value.filter((item) => item.selected && item.originalPrice && item.originalPrice > item.price).reduce((sum, item) => sum + (item.originalPrice - item.price) * item.quantity, 0);
    });
    const isShopAllSelected = (shopId) => {
      const shopItems = cartItems.value.filter((item) => item.shopId === shopId);
      return shopItems.length > 0 && shopItems.every((item) => item.selected);
    };
    const toggleShopSelect = (shopId) => {
      const shouldSelect = !isShopAllSelected(shopId);
      cartItems.value = cartItems.value.map((item) => {
        if (item.shopId === shopId) {
          return { ...item, selected: shouldSelect };
        }
        return item;
      });
    };
    const handleItemSelect = (e, item) => {
      item.selected = e.detail.value.length > 0;
      updateItemSelectedStatus(item);
    };
    const isAllSelected = common_vendor.computed(() => {
      return cartItems.value.length > 0 && cartItems.value.every((item) => item.selected);
    });
    const handleSelectAll = (e) => {
      const selected = e.detail.value.length > 0;
      cartItems.value = cartItems.value.map((item) => ({
        ...item,
        selected
      }));
      updateAllSelectedStatus(selected);
    };
    const decreaseQuantity = async (item) => {
      if (item.quantity > 1) {
        item.quantity--;
        await updateItemQuantity(item);
      }
    };
    const increaseQuantity = async (item) => {
      if (item.quantity < 99) {
        item.quantity++;
        await updateItemQuantity(item);
      }
    };
    const validateQuantity = async (item) => {
      if (isNaN(item.quantity)) {
        item.quantity = 1;
      }
      item.quantity = Math.max(1, Math.min(99, Math.floor(item.quantity)));
      await updateItemQuantity(item);
    };
    const totalPrice = common_vendor.computed(() => {
      return cartItems.value.filter((item) => item.selected).reduce((sum, item) => sum + item.price * item.quantity, 0);
    });
    const selectedCount = common_vendor.computed(() => {
      return cartItems.value.filter((item) => item.selected).length;
    });
    const updateItemQuantity = async (item) => {
      try {
        await api_cart.cartApi.updateQuantity(item.productId, item.quantity);
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/shoppingCart/shoppingCart.vue:353", "更新商品数量失败:", error);
        common_vendor.index.showToast({
          title: "更新数量失败",
          icon: "error"
        });
      }
    };
    const updateItemSelectedStatus = async (item) => {
      try {
        await api_cart.cartApi.updateSelectedStatus(item.id, item.selected);
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/shoppingCart/shoppingCart.vue:366", "更新选中状态失败:", error);
        common_vendor.index.showToast({
          title: "更新选中状态失败",
          icon: "error"
        });
      }
    };
    const updateAllSelectedStatus = async (selected) => {
      try {
        await api_cart.cartApi.selectAll(selected);
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/shoppingCart/shoppingCart.vue:379", "更新全选状态失败:", error);
        common_vendor.index.showToast({
          title: "更新全选状态失败",
          icon: "error"
        });
      }
    };
    const goShopping = () => {
      common_vendor.index.switchTab({
        url: "/pages/index/index"
      });
    };
    const deleteCartItem = async (item) => {
      try {
        common_vendor.index.showModal({
          title: "确认删除",
          content: `确定要删除"${item.name}"吗？`,
          confirmText: "删除",
          confirmColor: "#e93b3d",
          success: async (res) => {
            if (res.confirm) {
              await api_cart.cartApi.softDeleteCartItem(item.id);
              const index = cartItems.value.findIndex((cartItem) => cartItem.id === item.id);
              if (index > -1) {
                cartItems.value.splice(index, 1);
              }
              common_vendor.index.showToast({
                title: "删除成功",
                icon: "success"
              });
            }
          }
        });
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/shoppingCart/shoppingCart.vue:420", "删除购物车项失败:", error);
        common_vendor.index.showToast({
          title: error.message || "删除失败",
          icon: "error"
        });
      }
    };
    const checkout = () => {
      const selectedItems = cartItems.value.filter((item) => item.selected);
      if (selectedItems.length === 0) {
        common_vendor.index.showToast({
          title: "请选择要结算的商品",
          icon: "none"
        });
        return;
      }
      common_vendor.index.navigateTo({
        url: "/pages/checkout/checkout"
      });
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: getImageUrl("icon/搜索.png"),
        b: common_vendor.o(goToSearch),
        c: getImageUrl("icon/地址.png"),
        d: common_vendor.o((...args) => _ctx.selectAddress && _ctx.selectAddress(...args)),
        e: loading.value
      }, loading.value ? {} : cartItems.value.length === 0 ? {
        g: common_assets._imports_0,
        h: common_vendor.o(goShopping)
      } : {
        i: isShopAllSelected(1),
        j: common_vendor.o(($event) => toggleShopSelect(1)),
        k: common_vendor.f(cartItems.value, (item, index, i0) => {
          return common_vendor.e({
            a: item.selected,
            b: common_vendor.o((e) => handleItemSelect(e, item), item.id),
            c: item.image
          }, item.image ? {
            d: item.image
          } : {
            e: common_vendor.t(getProductIcon(item.name))
          }, {
            f: common_vendor.t(item.name),
            g: item.tag
          }, item.tag ? {
            h: common_vendor.t(item.tag),
            i: tagColors[item.tag]
          } : {}, {
            j: common_vendor.t(item.specification),
            k: common_vendor.t(item.price.toFixed(2)),
            l: item.originalPrice && item.originalPrice > item.price
          }, item.originalPrice && item.originalPrice > item.price ? {
            m: common_vendor.t(item.originalPrice.toFixed(2))
          } : {}, {
            n: item.stock < 10
          }, item.stock < 10 ? {
            o: common_vendor.t(item.stock)
          } : {}, {
            p: common_vendor.o(($event) => decreaseQuantity(item), item.id),
            q: common_vendor.o(($event) => validateQuantity(item), item.id),
            r: item.quantity,
            s: common_vendor.o(common_vendor.m(($event) => item.quantity = $event.detail.value, {
              number: true
            }), item.id),
            t: common_vendor.o(($event) => increaseQuantity(item), item.id),
            v: common_vendor.o(($event) => deleteCartItem(item), item.id),
            w: item.id
          });
        })
      }, {
        f: cartItems.value.length === 0,
        l: !loading.value && cartItems.value.length > 0
      }, !loading.value && cartItems.value.length > 0 ? common_vendor.e({
        m: isAllSelected.value,
        n: common_vendor.o(handleSelectAll),
        o: common_vendor.t(totalPrice.value.toFixed(2)),
        p: totalSaved.value > 0
      }, totalSaved.value > 0 ? {
        q: common_vendor.t(totalSaved.value.toFixed(2))
      } : {}, {
        r: common_vendor.t(selectedCount.value),
        s: selectedCount.value === 0 ? 1 : "",
        t: common_vendor.o(checkout)
      }) : {});
    };
  }
};
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/shoppingCart/shoppingCart.js.map
