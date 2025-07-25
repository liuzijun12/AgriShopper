"use strict";
const common_vendor = require("../../common/vendor.js");
const store = require("../../store.js");
const api_address = require("../../api/address.js");
const config_env = require("../../config/env.js");
if (!Math) {
  WxLoginModal();
}
const WxLoginModal = () => "../../components/WxLoginModal.js";
const _sfc_main = {
  __name: "my",
  setup(__props) {
    const getImageUrl = (path) => {
      const config = config_env.env.getConfig();
      const fullUrl = `${config.baseUrl}/static/${path}`;
      common_vendor.index.__f__("log", "at pages/my/my.vue:108", "getImageUrl:", path, "->", fullUrl);
      return fullUrl;
    };
    const isLoggedIn = common_vendor.ref(false);
    const showLoginModal = common_vendor.ref(false);
    const username = common_vendor.ref("张先生");
    const userAvatar = common_vendor.ref(getImageUrl("icon/未登录.png"));
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
    const addresses = common_vendor.ref([]);
    const loading = common_vendor.ref(false);
    const loadAddresses = async () => {
      try {
        loading.value = true;
        const userInfo = store.store.getUserInfo();
        if (!userInfo || !userInfo.id) {
          common_vendor.index.__f__("log", "at pages/my/my.vue:145", "用户未登录，无法加载地址");
          return;
        }
        const response = await api_address.addressApi.getAddressList(userInfo.id);
        if (response.code === 200 && response.data) {
          addresses.value = response.data.map((address) => ({
            id: address.id,
            name: address.receiverName,
            phone: address.receiverPhone,
            address: `${address.province}${address.city}${address.district}${address.detailAddress}`,
            isDefault: address.isDefault,
            // 保存完整地址信息用于编辑
            fullAddress: address
          }));
        } else {
          common_vendor.index.__f__("error", "at pages/my/my.vue:162", "获取地址列表失败:", response.message);
          common_vendor.index.showToast({
            title: response.message || "获取地址列表失败",
            icon: "error"
          });
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/my/my.vue:169", "加载地址列表失败:", error);
        common_vendor.index.showToast({
          title: "加载地址列表失败",
          icon: "error"
        });
      } finally {
        loading.value = false;
      }
    };
    const setDefaultAddress = async (index) => {
      try {
        const address = addresses.value[index];
        const userInfo = store.store.getUserInfo();
        if (!userInfo || !userInfo.id) {
          common_vendor.index.showToast({
            title: "请先登录",
            icon: "none"
          });
          return;
        }
        const response = await api_address.addressApi.setDefaultAddress(address.id, userInfo.id);
        if (response.code === 200) {
          addresses.value.forEach((addr, i) => {
            addr.isDefault = i === index;
          });
          common_vendor.index.showToast({
            title: "设置默认地址成功",
            icon: "success"
          });
        } else {
          common_vendor.index.showToast({
            title: response.message || "设置默认地址失败",
            icon: "error"
          });
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/my/my.vue:211", "设置默认地址失败:", error);
        common_vendor.index.showToast({
          title: "设置默认地址失败",
          icon: "error"
        });
      }
    };
    const deleteAddress = async (index) => {
      try {
        const address = addresses.value[index];
        const userInfo = store.store.getUserInfo();
        if (!userInfo || !userInfo.id) {
          common_vendor.index.showToast({
            title: "请先登录",
            icon: "none"
          });
          return;
        }
        common_vendor.index.showModal({
          title: "确认删除",
          content: `确定要删除地址"${address.name}"吗？`,
          confirmText: "删除",
          confirmColor: "#ff6b6b",
          success: async (res) => {
            if (res.confirm) {
              const response = await api_address.addressApi.deleteAddress(address.id, userInfo.id);
              if (response.code === 200) {
                addresses.value.splice(index, 1);
                common_vendor.index.showToast({
                  title: "删除地址成功",
                  icon: "success"
                });
              } else {
                common_vendor.index.showToast({
                  title: response.message || "删除地址失败",
                  icon: "error"
                });
              }
            }
          }
        });
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/my/my.vue:259", "删除地址失败:", error);
        common_vendor.index.showToast({
          title: "删除地址失败",
          icon: "error"
        });
      }
    };
    const editAddress = (index) => {
      const address = addresses.value[index];
      common_vendor.index.navigateTo({
        url: `/pages/address/edit?addressId=${address.id}`
      });
    };
    const addAddress = () => {
      common_vendor.index.navigateTo({
        url: "/pages/address/add"
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
          } else {
            userAvatar.value = "/static/tabbar/user.png";
          }
        } else {
          isLoggedIn.value = false;
          userAvatar.value = getImageUrl("icon/未登录.png");
          username.value = "点击登录";
          common_vendor.index.__f__("log", "at pages/my/my.vue:299", "未登录状态，设置头像为:", userAvatar.value);
        }
      } catch (error) {
        common_vendor.index.__f__("log", "at pages/my/my.vue:302", "检查登录状态失败:", error);
        isLoggedIn.value = false;
        userAvatar.value = getImageUrl("icon/未登录.png");
        username.value = "点击登录";
        common_vendor.index.__f__("log", "at pages/my/my.vue:306", "出错状态，设置头像为:", userAvatar.value);
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
      common_vendor.index.__f__("log", "at pages/my/my.vue:331", "登录成功:", userInfo);
      checkLoginStatus();
      showLoginModal.value = false;
      loadAddresses();
    };
    const handleFunctionClick = (item) => {
      switch (item.text) {
        case "我的收藏":
          common_vendor.index.navigateTo({
            url: "/pages/favorites/favorites"
          });
          break;
        case "浏览记录":
          common_vendor.index.showToast({
            title: "功能开发中",
            icon: "none"
          });
          break;
      }
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
              userAvatar.value = getImageUrl("icon/未登录.png");
              common_vendor.index.showToast({
                title: "已退出登录",
                icon: "success",
                duration: 2e3
              });
              common_vendor.index.__f__("log", "at pages/my/my.vue:384", "用户已退出登录，所有数据已清除");
              setTimeout(() => {
                common_vendor.index.reLaunch({
                  url: "/pages/index/index"
                });
              }, 2e3);
            } catch (error) {
              common_vendor.index.__f__("error", "at pages/my/my.vue:394", "退出登录失败:", error);
              common_vendor.index.showToast({
                title: "退出失败，请重试",
                icon: "none"
              });
            }
          }
        }
      });
    };
    const handleAvatarError = (e) => {
      if (isLoggedIn.value) {
        e.target.src = "/static/tabbar/user.png";
      } else {
        e.target.src = getImageUrl("icon/未登录.png");
      }
    };
    common_vendor.onMounted(() => {
      common_vendor.index.__f__("log", "at pages/my/my.vue:418", "my.vue 页面加载，初始头像URL:", userAvatar.value);
      checkLoginStatus();
      if (isLoggedIn.value) {
        loadAddresses();
      }
    });
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: userAvatar.value,
        b: common_vendor.o(handleAvatarError),
        c: common_vendor.t(username.value),
        d: common_vendor.t(isLoggedIn.value ? "已登录" : "点击登录"),
        e: common_vendor.t(isLoggedIn.value ? "设置" : "登录"),
        f: common_vendor.o(handleUserClick),
        g: common_vendor.f(orderItems.value, (item, index, i0) => {
          return {
            a: common_vendor.t(item.text),
            b: index
          };
        }),
        h: common_vendor.f(functionItems.value, (item, index, i0) => {
          return {
            a: common_vendor.t(item.text),
            b: index,
            c: common_vendor.o(($event) => handleFunctionClick(item), index)
          };
        }),
        i: isLoggedIn.value
      }, isLoggedIn.value ? {
        j: common_vendor.o(handleLogout)
      } : {}, {
        k: common_vendor.o(addAddress),
        l: loading.value
      }, loading.value ? {} : addresses.value.length === 0 ? {} : {
        n: common_vendor.f(addresses.value, (address, index, i0) => {
          return common_vendor.e({
            a: common_vendor.t(address.name),
            b: common_vendor.t(address.phone),
            c: address.isDefault
          }, address.isDefault ? {} : {}, {
            d: common_vendor.t(address.address),
            e: common_vendor.o(($event) => editAddress(index), address.id),
            f: !address.isDefault
          }, !address.isDefault ? {
            g: common_vendor.o(($event) => setDefaultAddress(index), address.id)
          } : {}, {
            h: common_vendor.o(($event) => deleteAddress(index), address.id),
            i: address.id
          });
        })
      }, {
        m: addresses.value.length === 0,
        o: common_vendor.o(handleCloseLogin),
        p: common_vendor.o(handleLoginSuccess),
        q: common_vendor.p({
          visible: showLoginModal.value
        })
      });
    };
  }
};
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/my/my.js.map
