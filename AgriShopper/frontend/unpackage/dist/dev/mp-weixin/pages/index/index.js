"use strict";
const common_vendor = require("../../common/vendor.js");
const store = require("../../store.js");
const config_env = require("../../config/env.js");
if (!Math) {
  WxLoginModal();
}
const WxLoginModal = () => "../../components/WxLoginModal.js";
const _sfc_main = {
  __name: "index",
  setup(__props) {
    const bannerList = common_vendor.ref([]);
    common_vendor.ref([
      { name: "饲料" },
      { name: "农产品" },
      { name: "养生保健品" }
    ]);
    const products = common_vendor.ref([]);
    const productsLoading = common_vendor.ref(false);
    const recommendationType = common_vendor.ref("");
    const recommendationStats = common_vendor.ref(null);
    const currentSwiper = common_vendor.ref(0);
    common_vendor.ref(1);
    const isLoggedIn = common_vendor.ref(false);
    const showLoginModal = common_vendor.ref(false);
    const userAvatar = common_vendor.ref("/static/tabbar/user.png");
    const onSwiperChange = (e) => {
      currentSwiper.value = e.detail.current;
    };
    const prevSwiper = () => {
      const newIndex = currentSwiper.value === 0 ? bannerList.value.length - 1 : currentSwiper.value - 1;
      currentSwiper.value = newIndex;
    };
    const nextSwiper = () => {
      const newIndex = currentSwiper.value === bannerList.value.length - 1 ? 0 : currentSwiper.value + 1;
      currentSwiper.value = newIndex;
    };
    const fetchRecommendProducts = async () => {
      try {
        productsLoading.value = true;
        const userInfo = store.store.getUserInfo();
        let productList = [];
        if (userInfo && userInfo.openid) {
          try {
            const personalizedResponse = await new Promise((resolve, reject) => {
              common_vendor.index.request({
                url: config_env.env.getApiUrl(`/user-behavior/recommendations/${userInfo.id || 1}`),
                method: "GET",
                data: {
                  limit: 8
                },
                success: (res) => {
                  resolve(res);
                },
                fail: (err) => {
                  reject(err);
                }
              });
            });
            if (personalizedResponse.statusCode === 200 && personalizedResponse.data.code === 200) {
              const recommendedProductCodes = personalizedResponse.data.data || [];
              common_vendor.index.__f__("log", "at pages/index/index.vue:192", "个性化推荐商品编码:", recommendedProductCodes);
              if (recommendedProductCodes.length > 0) {
                for (const productCode of recommendedProductCodes) {
                  try {
                    const productResponse = await new Promise((resolve, reject) => {
                      common_vendor.index.request({
                        url: config_env.env.getApiUrl(`/products`),
                        method: "GET",
                        data: {
                          productCode,
                          page: 0,
                          size: 1
                        },
                        success: (res) => {
                          resolve(res);
                        },
                        fail: (err) => {
                          reject(err);
                        }
                      });
                    });
                    if (productResponse.statusCode === 200 && productResponse.data.code === 200) {
                      const products2 = productResponse.data.data.content || [];
                      if (products2.length > 0) {
                        productList.push(products2[0]);
                      }
                    }
                  } catch (error) {
                    common_vendor.index.__f__("error", "at pages/index/index.vue:223", "获取推荐商品详情失败:", error);
                  }
                }
              }
              if (productList.length > 0) {
                recommendationType.value = "personalized";
              }
            }
          } catch (error) {
            common_vendor.index.__f__("error", "at pages/index/index.vue:233", "获取个性化推荐失败:", error);
          }
        }
        if (productList.length === 0) {
          try {
            const hotResponse = await new Promise((resolve, reject) => {
              common_vendor.index.request({
                url: config_env.env.getApiUrl("/user-behavior/popular"),
                method: "GET",
                data: {
                  limit: 8,
                  days: 7
                },
                success: (res) => {
                  resolve(res);
                },
                fail: (err) => {
                  reject(err);
                }
              });
            });
            if (hotResponse.statusCode === 200 && hotResponse.data.code === 200) {
              const hotProductCodes = hotResponse.data.data || [];
              common_vendor.index.__f__("log", "at pages/index/index.vue:259", "热门推荐商品编码:", hotProductCodes);
              for (const productCode of hotProductCodes) {
                try {
                  const productResponse = await new Promise((resolve, reject) => {
                    common_vendor.index.request({
                      url: config_env.env.getApiUrl(`/products`),
                      method: "GET",
                      data: {
                        productCode,
                        page: 0,
                        size: 1
                      },
                      success: (res) => {
                        resolve(res);
                      },
                      fail: (err) => {
                        reject(err);
                      }
                    });
                  });
                  if (productResponse.statusCode === 200 && productResponse.data.code === 200) {
                    const products2 = productResponse.data.data.content || [];
                    if (products2.length > 0) {
                      productList.push(products2[0]);
                    }
                  }
                } catch (error) {
                  common_vendor.index.__f__("error", "at pages/index/index.vue:289", "获取热门商品详情失败:", error);
                }
              }
              if (productList.length > 0) {
                recommendationType.value = "popular";
              }
            }
          } catch (error) {
            common_vendor.index.__f__("error", "at pages/index/index.vue:298", "获取热门推荐失败:", error);
          }
        }
        if (productList.length === 0) {
          const response = await new Promise((resolve, reject) => {
            common_vendor.index.request({
              url: config_env.env.getApiUrl("/products"),
              method: "GET",
              data: {
                page: 0,
                size: 8
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
            productList = response.data.data.content || [];
            recommendationType.value = "default";
          } else {
            common_vendor.index.__f__("error", "at pages/index/index.vue:325", "获取商品列表失败:", response.data);
            loadDefaultProducts();
            recommendationType.value = "default";
            return;
          }
        }
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
        common_vendor.index.__f__("log", "at pages/index/index.vue:345", "推荐商品加载成功:", products.value);
        common_vendor.index.__f__("log", "at pages/index/index.vue:346", "推荐类型:", recommendationType.value);
        recordViewRecommendPage();
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:352", "获取推荐商品出错:", error);
        loadDefaultProducts();
        recommendationType.value = "default";
      } finally {
        productsLoading.value = false;
      }
    };
    const fetchRecommendationStats = async () => {
      try {
        const userInfo = store.store.getUserInfo();
        if (!userInfo || !userInfo.openid) {
          return;
        }
        const response = await new Promise((resolve, reject) => {
          common_vendor.index.request({
            url: config_env.env.getApiUrl(`/user-behavior/statistics/${userInfo.id || 1}`),
            method: "GET",
            success: (res) => {
              resolve(res);
            },
            fail: (err) => {
              reject(err);
            }
          });
        });
        if (response.statusCode === 200 && response.data.code === 200) {
          recommendationStats.value = response.data.data;
          common_vendor.index.__f__("log", "at pages/index/index.vue:384", "推荐统计信息:", recommendationStats.value);
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:387", "获取推荐统计信息失败:", error);
      }
    };
    const recordViewRecommendPage = async () => {
      try {
        const userInfo = store.store.getUserInfo();
        if (!userInfo || !userInfo.openid) {
          common_vendor.index.__f__("log", "at pages/index/index.vue:396", "用户未登录，跳过行为记录");
          return;
        }
        await common_vendor.index.request({
          url: config_env.env.getApiUrl("/user-behavior/view-page"),
          method: "POST",
          data: {
            userId: userInfo.id || 1,
            // 默认使用用户ID=1
            pagePath: "/pages/index/index",
            pageTitle: "首页推荐",
            sourcePage: "home"
          }
        });
        common_vendor.index.__f__("log", "at pages/index/index.vue:412", "记录查看推荐页面行为成功");
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:414", "记录查看推荐页面行为失败:", error);
      }
    };
    const recordClickProduct = async (product) => {
      try {
        const userInfo = store.store.getUserInfo();
        if (!userInfo || !userInfo.openid) {
          common_vendor.index.__f__("log", "at pages/index/index.vue:423", "用户未登录，跳过行为记录");
          return;
        }
        await common_vendor.index.request({
          url: config_env.env.getApiUrl("/user-behavior/click-product"),
          method: "POST",
          data: {
            userId: userInfo.id || 1,
            // 默认使用用户ID=1
            productCode: product.productCode,
            productName: product.name,
            sourcePage: "home"
          }
        });
        common_vendor.index.__f__("log", "at pages/index/index.vue:439", "记录点击商品行为成功:", product.name);
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:441", "记录点击商品行为失败:", error);
      }
    };
    const recordViewProduct = async (product) => {
      try {
        const userInfo = store.store.getUserInfo();
        if (!userInfo || !userInfo.openid) {
          common_vendor.index.__f__("log", "at pages/index/index.vue:450", "用户未登录，跳过行为记录");
          return;
        }
        await common_vendor.index.request({
          url: config_env.env.getApiUrl("/user-behavior/view-product"),
          method: "POST",
          data: {
            userId: userInfo.id || 1,
            // 默认使用用户ID=1
            productCode: product.productCode,
            productName: product.name,
            sourcePage: "home"
          }
        });
        common_vendor.index.__f__("log", "at pages/index/index.vue:466", "记录查看商品行为成功:", product.name);
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:468", "记录查看商品行为失败:", error);
      }
    };
    const recordSearchBehavior = async () => {
      try {
        const userInfo = store.store.getUserInfo();
        if (!userInfo || !userInfo.openid) {
          common_vendor.index.__f__("log", "at pages/index/index.vue:477", "用户未登录，跳过行为记录");
          return;
        }
        await common_vendor.index.request({
          url: config_env.env.getApiUrl("/user-behavior/search"),
          method: "POST",
          data: {
            userId: userInfo.id || 1,
            // 默认使用用户ID=1
            keyword: "搜索框点击",
            sourcePage: "home"
          }
        });
        common_vendor.index.__f__("log", "at pages/index/index.vue:492", "记录搜索行为成功");
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:494", "记录搜索行为失败:", error);
      }
    };
    const getImageUrl = (url) => {
      if (!url)
        return "/static/default-product.png";
      if (url.startsWith("http://") || url.startsWith("https://")) {
        return url;
      }
      const config = config_env.env.getConfig();
      if (url.startsWith("icon/")) {
        return config.baseUrl + "/" + url;
      }
      if (url.startsWith("tabbar/")) {
        return config.baseUrl + "/" + url;
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
    const initBannerList = () => {
      bannerList.value = [
        {
          imageUrl: getImageUrl("Carousel/siliao.jpg")
        },
        {
          imageUrl: getImageUrl("Carousel/shucai.jpg")
        },
        {
          imageUrl: getImageUrl("Carousel/baojian.jpg")
        }
      ];
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
        },
        {
          id: 5,
          name: "有机白菜",
          description: "新鲜采摘，绿色健康",
          price: 6.5,
          imageUrl: "/static/default-product.png"
        },
        {
          id: 6,
          name: "土鸡蛋",
          description: "散养土鸡，营养丰富",
          price: 15.9,
          imageUrl: "/static/default-product.png"
        },
        {
          id: 7,
          name: "蜂蜜",
          description: "纯天然野生蜂蜜",
          price: 68,
          imageUrl: "/static/default-product.png"
        },
        {
          id: 8,
          name: "有机大米",
          description: "东北黑土地种植",
          price: 25.8,
          imageUrl: "/static/default-product.png"
        }
      ];
    };
    const goToSearch = () => {
      recordSearchBehavior();
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
        common_vendor.index.__f__("log", "at pages/index/index.vue:639", "检查登录状态失败:", error);
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
      common_vendor.index.__f__("log", "at pages/index/index.vue:664", "登录成功:", userInfo);
      checkLoginStatus();
      showLoginModal.value = false;
    };
    const goToProductDetail = (product) => {
      recordClickProduct(product);
      recordViewProduct(product);
      common_vendor.index.navigateTo({
        url: `/pages/productDetail/productDetail?id=${product.id}`
      });
    };
    const addToCart = (product) => {
      recordClickProduct(product);
      common_vendor.index.navigateTo({
        url: `/pages/productDetail/productDetail?id=${product.id}`
      });
    };
    const handleImageError = (e) => {
      e.target.src = "/static/default-product.png";
    };
    common_vendor.onMounted(() => {
      initBannerList();
      checkLoginStatus();
      fetchRecommendProducts();
      fetchRecommendationStats();
    });
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: getImageUrl("icon/4.png"),
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
        g: currentSwiper.value,
        h: common_vendor.o(onSwiperChange),
        i: common_vendor.f(bannerList.value, (item, index, i0) => {
          return {
            a: index,
            b: common_vendor.n(currentSwiper.value === index ? "active" : "")
          };
        }),
        j: getImageUrl("icon/1.png"),
        k: common_vendor.o(prevSwiper),
        l: getImageUrl("icon/3.png"),
        m: common_vendor.o(nextSwiper),
        n: recommendationType.value === "personalized"
      }, recommendationType.value === "personalized" ? {} : recommendationType.value === "popular" ? {} : {}, {
        o: recommendationType.value === "popular",
        p: productsLoading.value
      }, productsLoading.value ? {} : {}, {
        q: productsLoading.value
      }, productsLoading.value ? {} : {
        r: common_vendor.f(products.value, (product, index, i0) => {
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
            h: common_vendor.o(($event) => addToCart(product), product.id || index),
            i: product.id || index,
            j: common_vendor.o(($event) => goToProductDetail(product), product.id || index)
          });
        })
      }, {
        s: !productsLoading.value && products.value.length === 0
      }, !productsLoading.value && products.value.length === 0 ? {} : {}, {
        t: common_vendor.o(handleCloseLogin),
        v: common_vendor.o(handleLoginSuccess),
        w: common_vendor.p({
          visible: showLoginModal.value
        })
      });
    };
  }
};
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/index/index.js.map
