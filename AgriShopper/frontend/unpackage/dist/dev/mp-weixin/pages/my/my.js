"use strict";
const common_vendor = require("../../common/vendor.js");
if (!Math) {
  BottomTabBar();
}
const BottomTabBar = () => "../../components/BottomTabBar.js";
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "my",
  setup(__props) {
    const username = common_vendor.ref("张先生");
    const userAvatar = common_vendor.ref("https://readdy.ai/api/search-image?query=A%20realistic%20portrait%20photo%20of%20a%20middle-aged%20Chinese%20man%20with%20short%20black%20hair%20and%20a%20friendly%20smile%2C%20wearing%20casual%20clothing%2C%20warm%20expression%2C%20natural%20lighting%2C%20high-quality%20detailed%20photo%2C%20subject%20fills%2080%20percent%20of%20frame%2C%20isolated%20on%20white%20background%2C%20centered%20composition&width=120&height=120&seq=1&orientation=squarish");
    const orderItems = common_vendor.ref([
      { icon: "wallet", text: "待支付" },
      { icon: "paperplane", text: "待发货" },
      { icon: "car", text: "待收货" },
      { icon: "refresh", text: "售后" },
      { icon: "list", text: "全部订单" }
    ]);
    const functionItems = common_vendor.ref([
      { icon: "star", text: "我的收藏" },
      { icon: "eye", text: "浏览记录" }
    ]);
    const addresses = common_vendor.ref([
      {
        name: "张先生",
        phone: "138****1234",
        address: "浙江省杭州市西湖区文三路100号",
        isDefault: true
      },
      {
        name: "张先生",
        phone: "138****1234",
        address: "浙江省杭州市滨江区江南大道500号",
        isDefault: false
      }
    ]);
    const setDefaultAddress = (index) => {
      addresses.value.forEach((address, i) => {
        address.isDefault = i === index;
      });
    };
    return (_ctx, _cache) => {
      return {
        a: userAvatar.value,
        b: common_vendor.t(username.value),
        c: common_vendor.f(orderItems.value, (item, index, i0) => {
          return {
            a: common_vendor.t(item.text),
            b: index
          };
        }),
        d: common_vendor.f(functionItems.value, (item, index, i0) => {
          return {
            a: common_vendor.t(item.text),
            b: index
          };
        }),
        e: common_vendor.f(addresses.value, (address, index, i0) => {
          return common_vendor.e({
            a: common_vendor.t(address.name),
            b: common_vendor.t(address.phone),
            c: common_vendor.t(address.address),
            d: address.isDefault
          }, address.isDefault ? {} : {}, {
            e: address.isDefault ? 1 : "",
            f: common_vendor.o(($event) => setDefaultAddress(index), index),
            g: index
          });
        })
      };
    };
  }
});
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/my/my.js.map
