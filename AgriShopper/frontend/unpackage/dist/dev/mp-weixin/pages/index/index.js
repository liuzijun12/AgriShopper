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
  setup(__props, { expose: __expose }) {
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
    const userAvatar = common_vendor.ref(getImageUrl("icon/登录.png"));
    const refreshing = common_vendor.ref(false);
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
                url: config_env.env.getApiUrl(`/user-behavior/recommendations/${userInfo.id || 1}?limit=8`),
                method: "GET",
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
              common_vendor.index.__f__("log", "at pages/index/index.vue:248", "个性化推荐商品编码:", recommendedProductCodes);
              if (recommendedProductCodes.length > 0) {
                for (const productCode of recommendedProductCodes) {
                  try {
                    const productResponse = await new Promise((resolve, reject) => {
                      common_vendor.index.request({
                        url: config_env.env.getApiUrl(`/products?productCode=${productCode}&page=0&size=1`),
                        method: "GET",
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
                    common_vendor.index.__f__("error", "at pages/index/index.vue:274", "获取推荐商品详情失败:", error);
                  }
                }
              }
              if (productList.length > 0) {
                recommendationType.value = "personalized";
              }
            }
          } catch (error) {
            common_vendor.index.__f__("error", "at pages/index/index.vue:284", "获取个性化推荐失败:", error);
          }
        }
        if (productList.length === 0) {
          try {
            const hotResponse = await new Promise((resolve, reject) => {
              common_vendor.index.request({
                url: config_env.env.getApiUrl("/user-behavior/popular?limit=8&days=7"),
                method: "GET",
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
              common_vendor.index.__f__("log", "at pages/index/index.vue:306", "热门推荐商品编码:", hotProductCodes);
              for (const productCode of hotProductCodes) {
                try {
                  const productResponse = await new Promise((resolve, reject) => {
                    common_vendor.index.request({
                      url: config_env.env.getApiUrl(`/products?productCode=${productCode}&page=0&size=1`),
                      method: "GET",
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
                  common_vendor.index.__f__("error", "at pages/index/index.vue:331", "获取热门商品详情失败:", error);
                }
              }
              if (productList.length > 0) {
                recommendationType.value = "popular";
              }
            }
          } catch (error) {
            common_vendor.index.__f__("error", "at pages/index/index.vue:340", "获取热门推荐失败:", error);
          }
        }
        if (productList.length < 8) {
          try {
            const defaultResponse = await new Promise((resolve, reject) => {
              common_vendor.index.request({
                url: config_env.env.getApiUrl("/user-behavior/default-recommendations?limit=8"),
                method: "GET",
                success: (res) => {
                  resolve(res);
                },
                fail: (err) => {
                  reject(err);
                }
              });
            });
            if (defaultResponse.statusCode === 200 && defaultResponse.data.code === 200) {
              const defaultProductCodes = defaultResponse.data.data || [];
              common_vendor.index.__f__("log", "at pages/index/index.vue:362", "默认推荐商品编码:", defaultProductCodes);
              const existingProductCodes = new Set(productList.map((p) => p.productCode));
              for (const productCode of defaultProductCodes) {
                if (!existingProductCodes.has(productCode)) {
                  try {
                    const productResponse = await new Promise((resolve, reject) => {
                      common_vendor.index.request({
                        url: config_env.env.getApiUrl(`/products?productCode=${productCode}&page=0&size=1`),
                        method: "GET",
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
                    common_vendor.index.__f__("error", "at pages/index/index.vue:391", "获取默认推荐商品详情失败:", error);
                  }
                }
              }
              if (productList.length > 0 && recommendationType.value !== "personalized" && recommendationType.value !== "popular") {
                recommendationType.value = "sales-stock";
              }
            }
          } catch (error) {
            common_vendor.index.__f__("error", "at pages/index/index.vue:401", "获取默认推荐失败:", error);
          }
        }
        if (productList.length < 8) {
          try {
            const response = await new Promise((resolve, reject) => {
              common_vendor.index.request({
                url: config_env.env.getApiUrl("/products?page=0&size=8"),
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
              const allProducts = response.data.data.content || [];
              const existingProductCodes = new Set(productList.map((p) => p.productCode));
              for (const product of allProducts) {
                if (!existingProductCodes.has(product.productCode)) {
                  productList.push(product);
                }
              }
              if (productList.length > 0 && recommendationType.value !== "personalized" && recommendationType.value !== "popular" && recommendationType.value !== "sales-stock") {
                recommendationType.value = "default";
              }
            }
          } catch (error) {
            common_vendor.index.__f__("error", "at pages/index/index.vue:439", "获取普通商品列表失败:", error);
          }
        }
        if (productList.length === 0) {
          common_vendor.index.__f__("error", "at pages/index/index.vue:445", "所有推荐系统都失败，使用默认商品数据");
          loadDefaultProducts();
          recommendationType.value = "default";
          return;
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
        common_vendor.index.__f__("log", "at pages/index/index.vue:464", "推荐商品加载成功:", products.value);
        common_vendor.index.__f__("log", "at pages/index/index.vue:465", "推荐类型:", recommendationType.value);
        recordViewRecommendPage();
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:471", "获取推荐商品出错:", error);
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
          common_vendor.index.__f__("log", "at pages/index/index.vue:503", "推荐统计信息:", recommendationStats.value);
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:506", "获取推荐统计信息失败:", error);
      }
    };
    const recordViewRecommendPage = async () => {
      try {
        const userInfo = store.store.getUserInfo();
        if (!userInfo || !userInfo.openid) {
          common_vendor.index.__f__("log", "at pages/index/index.vue:515", "用户未登录，跳过行为记录");
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
        common_vendor.index.__f__("log", "at pages/index/index.vue:531", "记录查看推荐页面行为成功");
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:533", "记录查看推荐页面行为失败:", error);
      }
    };
    const recordClickProduct = async (product) => {
      try {
        const userInfo = store.store.getUserInfo();
        if (!userInfo || !userInfo.openid) {
          common_vendor.index.__f__("log", "at pages/index/index.vue:542", "用户未登录，跳过行为记录");
          return;
        }
        const pages = getCurrentPages();
        const currentPage = pages[pages.length - 1];
        const sourcePage = currentPage ? currentPage.route : "unknown";
        await common_vendor.index.request({
          url: config_env.env.getApiUrl("/user-behavior/click-product"),
          method: "POST",
          data: {
            userId: userInfo.id || 1,
            // 默认使用用户ID=1
            productCode: product.productCode,
            productName: product.name,
            sourcePage
          }
        });
        common_vendor.index.__f__("log", "at pages/index/index.vue:563", "记录点击商品行为成功:", product.name, "来源页面:", sourcePage);
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:565", "记录点击商品行为失败:", error);
      }
    };
    const recordViewProduct = async (product) => {
      try {
        const userInfo = store.store.getUserInfo();
        if (!userInfo || !userInfo.openid) {
          common_vendor.index.__f__("log", "at pages/index/index.vue:574", "用户未登录，跳过行为记录");
          return;
        }
        const pages = getCurrentPages();
        const currentPage = pages[pages.length - 1];
        const sourcePage = currentPage ? currentPage.route : "unknown";
        await common_vendor.index.request({
          url: config_env.env.getApiUrl("/user-behavior/view-product"),
          method: "POST",
          data: {
            userId: userInfo.id || 1,
            // 默认使用用户ID=1
            productCode: product.productCode,
            productName: product.name,
            sourcePage
          }
        });
        common_vendor.index.__f__("log", "at pages/index/index.vue:595", "记录查看商品行为成功:", product.name, "来源页面:", sourcePage);
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:597", "记录查看商品行为失败:", error);
      }
    };
    const recordSearchBehavior = async () => {
      try {
        const userInfo = store.store.getUserInfo();
        if (!userInfo || !userInfo.openid) {
          common_vendor.index.__f__("log", "at pages/index/index.vue:606", "用户未登录，跳过行为记录");
          return;
        }
        const pages = getCurrentPages();
        const currentPage = pages[pages.length - 1];
        const sourcePage = currentPage ? currentPage.route : "unknown";
        await common_vendor.index.request({
          url: config_env.env.getApiUrl("/user-behavior/search"),
          method: "POST",
          data: {
            userId: userInfo.id || 1,
            // 默认使用用户ID=1
            keyword: "搜索框点击",
            sourcePage
          }
        });
        common_vendor.index.__f__("log", "at pages/index/index.vue:626", "记录搜索行为成功，来源页面:", sourcePage);
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:628", "记录搜索行为失败:", error);
      }
    };
    const initBannerList = () => {
      return new Promise((resolve) => {
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
        resolve();
      });
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
          } else {
            userAvatar.value = "/static/tabbar/user.png";
          }
        } else {
          isLoggedIn.value = false;
          userAvatar.value = getImageUrl("icon/登录.png");
        }
      } catch (error) {
        common_vendor.index.__f__("log", "at pages/index/index.vue:741", "检查登录状态失败:", error);
        isLoggedIn.value = false;
        userAvatar.value = getImageUrl("icon/登录.png");
      }
    };
    const handleUserClick = () => {
      if (isLoggedIn.value) {
        common_vendor.index.showToast({
          title: "欢迎回来",
          icon: "none",
          duration: 1500
        });
        setTimeout(() => {
          common_vendor.index.switchTab({
            url: "/pages/my/my"
          });
        }, 500);
      } else {
        showLoginModal.value = true;
      }
    };
    const handleCloseLogin = () => {
      showLoginModal.value = false;
    };
    const handleLoginSuccess = (userInfo) => {
      common_vendor.index.__f__("log", "at pages/index/index.vue:775", "登录成功:", userInfo);
      checkLoginStatus();
      showLoginModal.value = false;
      fetchRecommendProducts();
      fetchRecommendationStats();
      common_vendor.index.showToast({
        title: "登录成功",
        icon: "success",
        duration: 2e3
      });
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
    const handleAvatarError = (e) => {
      if (isLoggedIn.value) {
        e.target.src = "/static/tabbar/user.png";
      } else {
        e.target.src = getImageUrl("icon/登录.png");
      }
    };
    common_vendor.onMounted(() => {
      initBannerList();
      checkLoginStatus();
      fetchRecommendProducts();
      fetchRecommendationStats();
    });
    const onShow = () => {
      common_vendor.index.__f__("log", "at pages/index/index.vue:842", "页面显示，刷新数据");
      checkLoginStatus();
      fetchRecommendProducts();
      fetchRecommendationStats();
    };
    const onRefresh = async () => {
      common_vendor.index.__f__("log", "at pages/index/index.vue:850", "触发下拉刷新");
      refreshing.value = true;
      try {
        await Promise.all([
          initBannerList(),
          fetchRecommendProducts(),
          fetchRecommendationStats()
        ]);
        checkLoginStatus();
        common_vendor.index.showToast({
          title: "刷新成功",
          icon: "success",
          duration: 1500
        });
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:869", "刷新失败:", error);
        common_vendor.index.showToast({
          title: "刷新失败",
          icon: "error",
          duration: 1500
        });
      } finally {
        refreshing.value = false;
      }
    };
    common_vendor.watch(() => store.store.state.refreshTrigger, (newVal, oldVal) => {
      if (newVal !== oldVal) {
        common_vendor.index.__f__("log", "at pages/index/index.vue:883", "检测到用户信息更新，刷新页面数据");
        checkLoginStatus();
        fetchRecommendProducts();
        fetchRecommendationStats();
      }
    });
    __expose({
      onShow
    });
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: getImageUrl("icon/4.png"),
        b: common_vendor.o(goToSearch),
        c: !isLoggedIn.value
      }, !isLoggedIn.value ? {
        d: userAvatar.value,
        e: common_vendor.o(handleAvatarError)
      } : {
        f: userAvatar.value,
        g: common_vendor.o(handleAvatarError)
      }, {
        h: common_vendor.o(handleUserClick),
        i: common_vendor.f(bannerList.value, (item, index, i0) => {
          return {
            a: item.imageUrl,
            b: index
          };
        }),
        j: currentSwiper.value,
        k: common_vendor.o(onSwiperChange),
        l: common_vendor.f(bannerList.value, (item, index, i0) => {
          return {
            a: index,
            b: common_vendor.n(currentSwiper.value === index ? "active" : "")
          };
        }),
        m: getImageUrl("icon/1.png"),
        n: common_vendor.o(prevSwiper),
        o: getImageUrl("icon/3.png"),
        p: common_vendor.o(nextSwiper),
        q: recommendationType.value === "personalized"
      }, recommendationType.value === "personalized" ? {} : recommendationType.value === "popular" ? {} : {}, {
        r: recommendationType.value === "popular",
        s: productsLoading.value
      }, productsLoading.value ? {} : {}, {
        t: productsLoading.value
      }, productsLoading.value ? {} : {
        v: common_vendor.f(products.value, (product, index, i0) => {
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
        w: !productsLoading.value && products.value.length === 0
      }, !productsLoading.value && products.value.length === 0 ? {} : {}, {
        x: refreshing.value,
        y: common_vendor.o(onRefresh),
        z: common_vendor.o(handleCloseLogin),
        A: common_vendor.o(handleLoginSuccess),
        B: common_vendor.p({
          visible: showLoginModal.value
        })
      });
    };
  }
};
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/index/index.js.map
