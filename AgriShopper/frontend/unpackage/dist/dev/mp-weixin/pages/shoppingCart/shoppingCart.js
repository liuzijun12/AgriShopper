"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
  __name: "shoppingCart",
  setup(__props) {
    common_vendor.ref("河北张家口市");
    common_vendor.ref(true);
    const tagColors = {
      "热卖": "#ffeeee",
      "特惠": "#fff8e6",
      "新品": "#e6f7ff",
      "促销": "#ffebee"
    };
    const farmProducts = common_vendor.ref([
      {
        id: 1,
        name: "有机西红柿",
        price: 5.99,
        originalPrice: 7.99,
        quantity: 1,
        selected: false,
        specification: "500g/份",
        stock: 8,
        tag: "热卖",
        shopId: 1
      },
      {
        id: 2,
        name: "农家土鸡蛋",
        price: 15.8,
        quantity: 2,
        selected: false,
        specification: "30枚/盒",
        stock: 15,
        tag: "特惠",
        shopId: 1
      },
      {
        id: 3,
        name: "新鲜菠菜",
        price: 3.5,
        quantity: 1,
        selected: false,
        specification: "250g/捆",
        stock: 20,
        tag: "新品",
        shopId: 1
      },
      {
        id: 4,
        name: "优质西红柿",
        price: 4.5,
        originalPrice: 6,
        quantity: 1,
        selected: false,
        specification: "400g/份",
        stock: 12,
        tag: "促销",
        shopId: 1
      },
      {
        id: 5,
        name: "新鲜土豆",
        price: 2.8,
        quantity: 3,
        selected: false,
        specification: "1kg/袋",
        stock: 25,
        tag: "热卖",
        shopId: 1
      },
      {
        id: 6,
        name: "有机白菜",
        price: 3.2,
        originalPrice: 4,
        quantity: 1,
        selected: false,
        specification: "800g/颗",
        stock: 18,
        tag: "特惠",
        shopId: 1
      },
      {
        id: 7,
        name: "红富士苹果",
        price: 8.8,
        originalPrice: 10,
        quantity: 1,
        selected: false,
        specification: "1kg/袋",
        stock: 15,
        tag: "促销",
        shopId: 1
      }
    ]);
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
      return farmProducts.value.filter((item) => item.selected && item.originalPrice).reduce((sum, item) => sum + (item.originalPrice - item.price) * item.quantity, 0);
    });
    const isShopAllSelected = (shopId) => {
      const shopItems = farmProducts.value.filter((item) => item.shopId === shopId);
      return shopItems.length > 0 && shopItems.every((item) => item.selected);
    };
    const toggleShopSelect = (shopId) => {
      const shouldSelect = !isShopAllSelected(shopId);
      farmProducts.value = farmProducts.value.map((item) => {
        if (item.shopId === shopId) {
          return { ...item, selected: shouldSelect };
        }
        return item;
      });
    };
    const handleItemSelect = (e, item) => {
      item.selected = e.detail.value.length > 0;
    };
    const isAllSelected = common_vendor.computed(() => {
      return farmProducts.value.length > 0 && farmProducts.value.every((item) => item.selected);
    });
    const handleSelectAll = (e) => {
      const selected = e.detail.value.length > 0;
      farmProducts.value = farmProducts.value.map((item) => ({
        ...item,
        selected
      }));
    };
    const decreaseQuantity = (item) => {
      if (item.quantity > 1) {
        item.quantity--;
      }
    };
    const increaseQuantity = (item) => {
      if (item.quantity < 99) {
        item.quantity++;
      }
    };
    const validateQuantity = (item) => {
      if (isNaN(item.quantity)) {
        item.quantity = 1;
      }
      item.quantity = Math.max(1, Math.min(99, Math.floor(item.quantity)));
    };
    const totalPrice = common_vendor.computed(() => {
      return farmProducts.value.filter((item) => item.selected).reduce((sum, item) => sum + item.price * item.quantity, 0);
    });
    const selectedCount = common_vendor.computed(() => {
      return farmProducts.value.filter((item) => item.selected).length;
    });
    const checkout = () => {
      const selectedItems = farmProducts.value.filter((item) => item.selected);
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
        a: common_assets._imports_0$2,
        b: common_vendor.o(goToSearch),
        c: common_assets._imports_1$1,
        d: common_vendor.o((...args) => _ctx.selectAddress && _ctx.selectAddress(...args)),
        e: isShopAllSelected(1),
        f: common_vendor.o(($event) => toggleShopSelect(1)),
        g: common_vendor.f(farmProducts.value, (item, index, i0) => {
          return common_vendor.e({
            a: item.selected,
            b: common_vendor.o((e) => handleItemSelect(e, item), index),
            c: common_vendor.t(getProductIcon(item.name)),
            d: common_vendor.t(item.name),
            e: item.tag
          }, item.tag ? {
            f: common_vendor.t(item.tag),
            g: tagColors[item.tag]
          } : {}, {
            h: common_vendor.t(item.specification),
            i: common_vendor.t(item.price.toFixed(2)),
            j: item.originalPrice
          }, item.originalPrice ? {
            k: common_vendor.t(item.originalPrice.toFixed(2))
          } : {}, {
            l: item.stock < 10
          }, item.stock < 10 ? {
            m: common_vendor.t(item.stock)
          } : {}, {
            n: common_vendor.o(($event) => decreaseQuantity(item), index),
            o: common_vendor.o(($event) => validateQuantity(item), index),
            p: item.quantity,
            q: common_vendor.o(common_vendor.m(($event) => item.quantity = $event.detail.value, {
              number: true
            }), index),
            r: common_vendor.o(($event) => increaseQuantity(item), index),
            s: index
          });
        }),
        h: isAllSelected.value,
        i: common_vendor.o(handleSelectAll),
        j: common_vendor.t(totalPrice.value.toFixed(2)),
        k: totalSaved.value > 0
      }, totalSaved.value > 0 ? {
        l: common_vendor.t(totalSaved.value.toFixed(2))
      } : {}, {
        m: common_vendor.t(selectedCount.value),
        n: selectedCount.value === 0 ? 1 : "",
        o: common_vendor.o(checkout)
      });
    };
  }
};
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/shoppingCart/shoppingCart.js.map
