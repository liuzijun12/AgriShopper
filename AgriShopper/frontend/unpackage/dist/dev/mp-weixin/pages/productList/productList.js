"use strict";
const common_vendor = require("../../common/vendor.js");
require("../../store.js");
const api_products = require("../../api/products.js");
const api_categories = require("../../api/categories.js");
const config_env = require("../../config/env.js");
if (!Array) {
  const _component_uni_load_more = common_vendor.resolveComponent("uni-load-more");
  _component_uni_load_more();
}
const _sfc_main = {
  __name: "productList",
  setup(__props) {
    const currentCategory = common_vendor.ref(0);
    const products = common_vendor.ref([]);
    const loading = common_vendor.ref(false);
    const error = common_vendor.ref("");
    const hasMore = common_vendor.ref(true);
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
    const pageParams = common_vendor.reactive({
      page: 0,
      size: 50
      // 增加页面大小，一次加载更多商品
    });
    const categories = common_vendor.ref([
      { name: "全部", id: 0 }
    ]);
    const categoriesLoading = common_vendor.ref(false);
    const loadingText = {
      contentdown: "正在加载商品...",
      contentrefresh: "正在加载...",
      contentnomore: "加载完成"
    };
    const loadProducts = async (reset = false) => {
      if (loading.value)
        return;
      try {
        loading.value = true;
        error.value = "";
        if (reset) {
          pageParams.page = 0;
          products.value = [];
          hasMore.value = true;
        }
        const params = {
          page: 0,
          size: 1e3
          // 设置一个很大的数字，确保获取所有商品
        };
        const response = await api_products.productsApi.getProductList(params);
        if (response.code === 200 && response.data) {
          let allProducts = response.data.content || [];
          if (currentCategory.value > 0) {
            const categoryId = categories.value[currentCategory.value].id;
            allProducts = allProducts.filter((product) => product.categoryId === categoryId);
          }
          products.value = allProducts;
          hasMore.value = false;
          common_vendor.index.__f__("log", "at pages/productList/productList.vue:208", `加载了 ${allProducts.length} 个商品`);
        } else {
          throw new Error(response.message || "获取商品数据失败");
        }
      } catch (err) {
        common_vendor.index.__f__("error", "at pages/productList/productList.vue:215", "加载商品失败:", err);
        error.value = err.message || "加载商品失败，请重试";
        if (reset) {
          products.value = [];
        }
      } finally {
        loading.value = false;
      }
    };
    const changeCategory = (index) => {
      if (currentCategory.value === index)
        return;
      currentCategory.value = index;
      loadProducts(true);
    };
    const loadMore = () => {
      common_vendor.index.__f__("log", "at pages/productList/productList.vue:238", "所有商品已加载完成");
    };
    const goToProductDetail = (productId) => {
      common_vendor.index.navigateTo({
        url: `/pages/productDetail/productDetail?id=${productId}`
      });
    };
    const addToCart = (product) => {
      common_vendor.index.navigateTo({
        url: `/pages/productDetail/productDetail?id=${product.id}`
      });
    };
    const goToSearch = () => {
      common_vendor.index.navigateTo({
        url: "/pages/searchProduct/searchProduct"
      });
    };
    const handleImageError = (e) => {
      e.target.src = "/static/default-product.png";
    };
    const loadCategories = async () => {
      try {
        categoriesLoading.value = true;
        const response = await api_categories.categoriesApi.getCategoryList();
        if (response.code === 200 && response.data) {
          const dbCategories = response.data.map((category) => ({
            id: category.id,
            name: category.categoryName,
            description: category.categoryDescription,
            icon: category.categoryIcon,
            sortOrder: category.sortOrder
          }));
          dbCategories.sort((a, b) => a.sortOrder - b.sortOrder);
          categories.value = [
            { name: "全部", id: 0 },
            ...dbCategories
          ];
          common_vendor.index.__f__("log", "at pages/productList/productList.vue:294", "分类加载成功:", categories.value);
        } else {
          common_vendor.index.__f__("error", "at pages/productList/productList.vue:296", "获取分类失败:", response.message);
          loadDefaultCategories();
        }
      } catch (error2) {
        common_vendor.index.__f__("error", "at pages/productList/productList.vue:301", "获取分类出错:", error2);
        loadDefaultCategories();
      } finally {
        categoriesLoading.value = false;
      }
    };
    const loadDefaultCategories = () => {
      categories.value = [
        { name: "全部", id: 0 },
        { name: "农产品", id: 1 },
        { name: "养生保健", id: 2 },
        { name: "饲料", id: 3 }
      ];
    };
    common_vendor.onMounted(() => {
      loadCategories();
      loadProducts(true);
    });
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: getImageUrl("icon/搜索.png"),
        b: common_vendor.o(goToSearch),
        c: categoriesLoading.value
      }, categoriesLoading.value ? {} : {
        d: common_vendor.f(categories.value, (category, index, i0) => {
          return {
            a: common_vendor.t(category.name),
            b: category.id || index,
            c: common_vendor.n(currentCategory.value === index ? "category-active" : ""),
            d: common_vendor.o(($event) => changeCategory(index), category.id || index)
          };
        })
      }, {
        e: loading.value && products.value.length === 0
      }, loading.value && products.value.length === 0 ? {
        f: common_vendor.p({
          status: "loading",
          ["content-text"]: loadingText
        })
      } : {}, {
        g: error.value && products.value.length === 0
      }, error.value && products.value.length === 0 ? {
        h: common_vendor.t(error.value),
        i: common_vendor.o(loadProducts)
      } : {}, {
        j: common_vendor.f(products.value, (product, index, i0) => {
          return common_vendor.e({
            a: product.mainImageUrl || "/static/default-product.png",
            b: common_vendor.o(handleImageError, product.id),
            c: common_vendor.t(product.productName),
            d: common_vendor.t(product.productDescription),
            e: common_vendor.t(product.stockQuantity),
            f: product.isHotProduct
          }, product.isHotProduct ? {} : {}, {
            g: product.isNewProduct
          }, product.isNewProduct ? {} : {}, {
            h: common_vendor.t(product.productUnit || "件"),
            i: common_vendor.t(product.productPrice),
            j: common_vendor.o(($event) => addToCart(product), product.id),
            k: product.id,
            l: common_vendor.o(($event) => goToProductDetail(product.id), product.id)
          });
        }),
        k: products.value.length > 0
      }, products.value.length > 0 ? {
        l: common_vendor.t(products.value.length)
      } : {}, {
        m: common_vendor.o(loadMore)
      });
    };
  }
};
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/productList/productList.js.map
