"use strict";
const common_vendor = require("../../common/vendor.js");
const store = require("../../store.js");
if (!Math) {
  WxLoginModal();
}
const WxLoginModal = () => "../../components/WxLoginModal.js";
const _sfc_main = {
  __name: "my",
  setup(__props) {
    const isLoggedIn = common_vendor.ref(false);
    const showLoginModal = common_vendor.ref(false);
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
    const checkLoginStatus = () => {
      try {
        const userInfo = store.store.getUserInfo();
        if (userInfo && userInfo.openid) {
          isLoggedIn.value = true;
          username.value = userInfo.nickname || "微信用户";
          if (userInfo.avatar) {
            userAvatar.value = userInfo.avatar;
          }
        } else {
          isLoggedIn.value = false;
        }
      } catch (error) {
        common_vendor.index.__f__("log", "at pages/my/my.vue:141", "检查登录状态失败:", error);
        isLoggedIn.value = false;
      }
    };
    const handleUserClick = () => {
      if (isLoggedIn.value) {
        common_vendor.index.showToast({
          title: "已登录",
          icon: "success"
        });
      } else {
        showLoginModal.value = true;
      }
    };
    const handleCloseLogin = () => {
      showLoginModal.value = false;
    };
    const handleLoginSuccess = (userInfo) => {
      common_vendor.index.__f__("log", "at pages/my/my.vue:167", "登录成功:", userInfo);
      checkLoginStatus();
      showLoginModal.value = false;
    };
    const handleLogout = () => {
      common_vendor.index.showModal({
        title: "确认退出",
        content: "确定要退出登录吗？退出后将清除所有本地数据。",
        confirmText: "退出",
        cancelText: "取消",
        confirmColor: "#ff6b6b",
        success: (res) => {
          if (res.confirm) {
            try {
              store.store.clearAllData();
              isLoggedIn.value = false;
              username.value = "点击登录";
              userAvatar.value = "https://readdy.ai/api/search-image?query=A%20realistic%20portrait%20photo%20of%20a%20middle-aged%20Chinese%20man%20with%20short%20black%20hair%20and%20a%20friendly%20smile%2C%20wearing%20casual%20clothing%2C%20warm%20expression%2C%20natural%20lighting%2C%20high-quality%20detailed%20photo%2C%20subject%20fills%2080%20percent%20of%20frame%2C%20isolated%20on%20white%20background%2C%20centered%20composition&width=120&height=120&seq=1&orientation=squarish";
              common_vendor.index.showToast({
                title: "已退出登录",
                icon: "success",
                duration: 2e3
              });
              common_vendor.index.__f__("log", "at pages/my/my.vue:199", "用户已退出登录，所有数据已清除");
              setTimeout(() => {
                common_vendor.index.reLaunch({
                  url: "/pages/index/index"
                });
              }, 2e3);
            } catch (error) {
              common_vendor.index.__f__("error", "at pages/my/my.vue:209", "退出登录失败:", error);
              common_vendor.index.showToast({
                title: "退出失败，请重试",
                icon: "none"
              });
            }
          }
        }
      });
    };
    common_vendor.onMounted(() => {
      checkLoginStatus();
    });
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: userAvatar.value,
        b: common_vendor.t(username.value),
        c: common_vendor.t(isLoggedIn.value ? "已登录" : "点击登录"),
        d: common_vendor.t(isLoggedIn.value ? "设置" : "登录"),
        e: common_vendor.o(handleUserClick),
        f: common_vendor.f(orderItems.value, (item, index, i0) => {
          return {
            a: common_vendor.t(item.text),
            b: index
          };
        }),
        g: common_vendor.f(functionItems.value, (item, index, i0) => {
          return {
            a: common_vendor.t(item.text),
            b: index
          };
        }),
        h: isLoggedIn.value
      }, isLoggedIn.value ? {
        i: common_vendor.o(handleLogout)
      } : {}, {
        j: common_vendor.f(addresses.value, (address, index, i0) => {
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
        }),
        k: common_vendor.o(handleCloseLogin),
        l: common_vendor.o(handleLoginSuccess),
        m: common_vendor.p({
          visible: showLoginModal.value
        })
      });
    };
  }
};
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/my/my.js.map
