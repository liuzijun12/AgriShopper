"use strict";
const common_vendor = require("../../common/vendor.js");
const store = require("../../store.js");
const config_env = require("../../config/env.js");
if (!Array) {
  const _component_uni_icons = common_vendor.resolveComponent("uni-icons");
  _component_uni_icons();
}
if (!Math) {
  WxLoginModal();
}
const WxLoginModal = () => "../../components/WxLoginModal.js";
const _sfc_main = {
  __name: "index",
  setup(__props) {
    const bannerList = common_vendor.ref([
      {
        imageUrl: "https://readdy.ai/api/search-image?query=Beautiful%20organic%20farm%20with%20green%20vegetables%20and%20fruits%2C%20fresh%20produce%20harvest%20scene%2C%20morning%20sunlight%2C%20vibrant%20colors%2C%20natural%20farming%20landscape%2C%20healthy%20food%20ingredients%20displayed%2C%20sustainable%20agriculture%2C%20farm%20to%20table%20concept%2C%20high%20quality%20professional%20photography&width=750&height=400&seq=1&orientation=landscape"
      },
      {
        imageUrl: "https://readdy.ai/api/search-image?query=Farmers%20market%20with%20colorful%20organic%20vegetables%20and%20fruits%2C%20fresh%20local%20produce%2C%20wooden%20crates%2C%20natural%20lighting%2C%20vibrant%20colors%2C%20rustic%20farm%20stand%2C%20healthy%20food%20display%2C%20sustainable%20agriculture%2C%20countryside%20scenery%2C%20professional%20food%20photography&width=750&height=400&seq=2&orientation=landscape"
      },
      {
        imageUrl: "https://readdy.ai/api/search-image?query=Organic%20health%20supplements%20and%20natural%20remedies%2C%20herbal%20medicine%20bottles%20and%20capsules%2C%20green%20leaves%20background%2C%20wellness%20products%2C%20clean%20modern%20display%2C%20healthy%20lifestyle%20concept%2C%20soft%20natural%20lighting%2C%20professional%20product%20photography&width=750&height=400&seq=3&orientation=landscape"
      }
    ]);
    common_vendor.ref([
      { name: "饲料" },
      { name: "农产品" },
      { name: "养生保健品" }
    ]);
    const products = common_vendor.ref([]);
    const productsLoading = common_vendor.ref(false);
    const currentSwiper = common_vendor.ref(0);
    common_vendor.ref(1);
    const isLoggedIn = common_vendor.ref(false);
    const showLoginModal = common_vendor.ref(false);
    const userAvatar = common_vendor.ref("/static/tabbar/user.png");
    const onSwiperChange = (e) => {
      currentSwiper.value = e.detail.current;
    };
    const prevSwiper = () => {
      if (currentSwiper.value === 0) {
        currentSwiper.value = bannerList.value.length - 1;
      } else {
        currentSwiper.value--;
      }
    };
    const nextSwiper = () => {
      if (currentSwiper.value === bannerList.value.length - 1) {
        currentSwiper.value = 0;
      } else {
        currentSwiper.value++;
      }
    };
    const goToSearch = () => {
      common_vendor.index.navigateTo({
        url: "/pages/searchProduct/searchProduct"
      });
    };
    const checkLoginStatus = () => {
      try {
        const userInfo = store.store.getUserInfo();
        if (userInfo && userInfo.openid) {
          isLoggedIn.value = true;
          if (userInfo.avatar) {
            userAvatar.value = userInfo.avatar;
          }
        } else {
          isLoggedIn.value = false;
        }
      } catch (error) {
        common_vendor.index.__f__("log", "at pages/index/index.vue:187", "检查登录状态失败:", error);
        isLoggedIn.value = false;
      }
    };
    const handleUserClick = () => {
      if (isLoggedIn.value) {
        common_vendor.index.switchTab({
          url: "/pages/my/my"
        });
      } else {
        showLoginModal.value = true;
      }
    };
    const handleCloseLogin = () => {
      showLoginModal.value = false;
    };
    const handleLoginSuccess = (userInfo) => {
      common_vendor.index.__f__("log", "at pages/index/index.vue:212", "登录成功:", userInfo);
      checkLoginStatus();
      showLoginModal.value = false;
    };
    const fetchRecommendProducts = async () => {
      try {
        productsLoading.value = true;
        const response = await new Promise((resolve, reject) => {
          common_vendor.index.request({
            url: config_env.env.getApiUrl("/products"),
            method: "GET",
            data: {
              page: 0,
              size: 4
            },
            success: (res) => {
              resolve(res);
            },
            fail: (err) => {
              reject(err);
            }
          });
        });
        if (response.statusCode === 200 && response.data.code === 200) {
          const productList = response.data.data.content || [];
          products.value = productList.map((product) => ({
            id: product.id,
            name: product.productName,
            description: product.productDescription || "暂无描述",
            price: product.productPrice,
            imageUrl: getImageUrl(product.mainImageUrl),
            productCode: product.productCode,
            stockQuantity: product.stockQuantity,
            isHotProduct: product.isHotProduct,
            isNewProduct: product.isNewProduct
          }));
          common_vendor.index.__f__("log", "at pages/index/index.vue:255", "推荐商品加载成功:", products.value);
        } else {
          common_vendor.index.__f__("error", "at pages/index/index.vue:257", "获取推荐商品失败:", response.data);
          loadDefaultProducts();
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:262", "获取推荐商品出错:", error);
        loadDefaultProducts();
      } finally {
        productsLoading.value = false;
      }
    };
    const getImageUrl = (url) => {
      if (!url)
        return "/static/default-product.png";
      if (url.startsWith("http://") || url.startsWith("https://")) {
        return url;
      }
      if (url.startsWith("/static/uploads/")) {
        return "http://localhost:8080" + url;
      }
      if (!url.startsWith("/")) {
        return "http://localhost:8080/static/uploads/" + url;
      }
      return "http://localhost:8080" + url;
    };
    const loadDefaultProducts = () => {
      products.value = [
        {
          id: 1,
          name: "有机红薯",
          description: "农家自种，无公害种植",
          price: 12.8,
          imageUrl: "/static/default-product.png"
        },
        {
          id: 2,
          name: "优质玉米饲料",
          description: "高营养，适合家禽喂养",
          price: 45.9,
          imageUrl: "/static/default-product.png"
        },
        {
          id: 3,
          name: "枸杞菊花茶",
          description: "养生保健，明目润肺",
          price: 38.5,
          imageUrl: "/static/default-product.png"
        },
        {
          id: 4,
          name: "新鲜胡萝卜",
          description: "富含胡萝卜素，助力健康",
          price: 8.8,
          imageUrl: "/static/default-product.png"
        }
      ];
    };
    const goToProductDetail = (product) => {
      common_vendor.index.navigateTo({
        url: `/pages/productDetail/productDetail?id=${product.id}`
      });
    };
    const addToCart = (product) => {
      common_vendor.index.showToast({
        title: "已添加到购物车",
        icon: "success"
      });
    };
    const handleImageError = (e) => {
      e.target.src = "/static/default-product.png";
    };
    common_vendor.onMounted(() => {
      checkLoginStatus();
      fetchRecommendProducts();
    });
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.p({
          type: "search",
          size: "24",
          color: "#999"
        }),
        b: common_vendor.o(goToSearch),
        c: userAvatar.value,
        d: !isLoggedIn.value
      }, !isLoggedIn.value ? {} : {}, {
        e: common_vendor.o(handleUserClick),
        f: common_vendor.f(bannerList.value, (item, index, i0) => {
          return {
            a: item.imageUrl,
            b: index
          };
        }),
        g: common_vendor.o(onSwiperChange),
        h: common_vendor.f(bannerList.value, (item, index, i0) => {
          return {
            a: index,
            b: common_vendor.n(currentSwiper.value === index ? "active" : "")
          };
        }),
        i: common_vendor.p({
          type: "left",
          size: "24",
          color: "#333"
        }),
        j: common_vendor.o(prevSwiper),
        k: common_vendor.p({
          type: "right",
          size: "24",
          color: "#333"
        }),
        l: common_vendor.o(nextSwiper),
        m: productsLoading.value
      }, productsLoading.value ? {} : {}, {
        n: productsLoading.value
      }, productsLoading.value ? {} : {
        o: common_vendor.f(products.value, (product, index, i0) => {
          return common_vendor.e({
            a: product.imageUrl,
            b: common_vendor.o(handleImageError, product.id || index),
            c: common_vendor.t(product.name),
            d: product.isHotProduct
          }, product.isHotProduct ? {} : {}, {
            e: product.isNewProduct
          }, product.isNewProduct ? {} : {}, {
            f: common_vendor.t(product.description),
            g: common_vendor.t(product.price),
            h: common_vendor.o(($event) => addToCart(), product.id || index),
            i: product.id || index,
            j: common_vendor.o(($event) => goToProductDetail(product), product.id || index)
          });
        })
      }, {
        p: !productsLoading.value && products.value.length === 0
      }, !productsLoading.value && products.value.length === 0 ? {} : {}, {
        q: common_vendor.o(handleCloseLogin),
        r: common_vendor.o(handleLoginSuccess),
        s: common_vendor.p({
          visible: showLoginModal.value
        })
      });
    };
  }
};
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/index/index.js.map
