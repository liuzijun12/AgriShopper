"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const __default__ = {
  name: "ShoppingCart",
  onShow() {
    common_vendor.index.$emit("tabPageShow");
  }
};
const _sfc_main = /* @__PURE__ */ Object.assign(__default__, {
  setup(__props) {
    const cartItems = common_vendor.ref([
      {
        id: 1,
        name: "新鲜西红柿",
        image: "/static/products/tomato.jpg",
        price: 5.99,
        quantity: 1,
        selected: false,
        specification: "500g/份"
      },
      {
        id: 2,
        name: "有机生菜",
        image: "/static/products/lettuce.jpg",
        price: 3.99,
        quantity: 2,
        selected: false,
        specification: "300g/份"
      }
    ]);
    const isEditMode = common_vendor.ref(false);
    const toggleEditMode = () => {
      isEditMode.value = !isEditMode.value;
    };
    const handleItemSelect = (e, item) => {
      item.selected = e.detail.value.length > 0;
    };
    const isAllSelected = common_vendor.computed(() => {
      return cartItems.value.length > 0 && cartItems.value.every((item) => item.selected);
    });
    const handleSelectAll = (e) => {
      const selected = e.detail.value.length > 0;
      cartItems.value.forEach((item) => {
        item.selected = selected;
      });
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
      const quantity = parseInt(item.quantity.toString());
      if (isNaN(quantity) || quantity < 1) {
        item.quantity = 1;
      } else if (quantity > 99) {
        item.quantity = 99;
      } else {
        item.quantity = quantity;
      }
    };
    const totalItems = common_vendor.computed(() => {
      return cartItems.value.reduce((sum, item) => sum + item.quantity, 0);
    });
    const selectedCount = common_vendor.computed(() => {
      return cartItems.value.filter((item) => item.selected).length;
    });
    const totalPrice = common_vendor.computed(() => {
      return cartItems.value.filter((item) => item.selected).reduce((sum, item) => sum + item.price * item.quantity, 0);
    });
    const goShopping = () => {
      common_vendor.index.switchTab({
        url: "/pages/index/index"
      });
    };
    const viewProduct = (item) => {
      common_vendor.index.navigateTo({
        url: `/pages/productDetail/productDetail?id=${item.id}`
      });
    };
    const deleteSelected = () => {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要删除选中的商品吗？",
        success: (res) => {
          if (res.confirm) {
            cartItems.value = cartItems.value.filter((item) => !item.selected);
            if (cartItems.value.length === 0) {
              isEditMode.value = false;
            }
          }
        }
      });
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
        a: cartItems.value.length === 0
      }, cartItems.value.length === 0 ? {
        b: common_assets._imports_0,
        c: common_vendor.o(goShopping)
      } : {
        d: common_vendor.t(totalItems.value),
        e: common_vendor.t(isEditMode.value ? "完成" : "编辑"),
        f: common_vendor.o(toggleEditMode),
        g: common_vendor.f(cartItems.value, (item, index, i0) => {
          return {
            a: item.selected,
            b: common_vendor.o((e) => handleItemSelect(e, item), index),
            c: item.image,
            d: common_vendor.o(($event) => viewProduct(item), index),
            e: common_vendor.t(item.name),
            f: common_vendor.t(item.specification),
            g: common_vendor.t(item.price.toFixed(2)),
            h: common_vendor.o(($event) => decreaseQuantity(item), index),
            i: common_vendor.o(($event) => validateQuantity(item), index),
            j: item.quantity,
            k: common_vendor.o(($event) => item.quantity = $event.detail.value, index),
            l: common_vendor.o(($event) => increaseQuantity(item), index),
            m: index
          };
        })
      }, {
        h: isAllSelected.value,
        i: common_vendor.o(handleSelectAll),
        j: !isEditMode.value
      }, !isEditMode.value ? {
        k: common_vendor.t(totalPrice.value.toFixed(2)),
        l: common_vendor.t(selectedCount.value),
        m: selectedCount.value === 0,
        n: common_vendor.o(checkout)
      } : {
        o: common_vendor.t(selectedCount.value),
        p: selectedCount.value === 0,
        q: common_vendor.o(deleteSelected)
      });
    };
  }
});
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/shoppingCart/shoppingCart.js.map
