"use strict";
const common_vendor = require("../../common/vendor.js");
require("../../store.js");
if (!Array) {
  const _component_uni_icons = common_vendor.resolveComponent("uni-icons");
  _component_uni_icons();
}
const _sfc_main = {
  __name: "productList",
  setup(__props) {
    const currentCategory = common_vendor.ref(0);
    const categories = common_vendor.ref([
      { name: "农产品", id: 1 },
      { name: "饲料", id: 2 },
      { name: "养生保健", id: 3 }
    ]);
    const products = common_vendor.ref([
      {
        id: 1,
        name: "有机西红柿",
        description: "新鲜采摘，无农药，口感酸甜多汁",
        price: "12.8",
        sales: 1280,
        stock: 500,
        image: "https://readdy.ai/api/search-image?query=Realistic%20fresh%20organic%20tomatoes%2C%20vibrant%20red%20color%2C%20farm%20fresh%20produce%2C%20high-detail%20photography%2C%20clean%20background%2C%20natural%20lighting%2C%20showing%20texture%20and%20freshness%2C%20product%20photography%20style%2C%20no%20human%20elements%2C%20professional%20food%20photography.&width=160&height=160&seq=5&orientation=squarish"
      },
      {
        id: 2,
        name: "绿色菠菜",
        description: "富含铁质，叶片肥厚，口感清爽",
        price: "8.5",
        sales: 950,
        stock: 300,
        image: "https://readdy.ai/api/search-image?query=Fresh%20green%20spinach%20leaves%2C%20organic%20farm%20produce%2C%20vibrant%20green%20color%2C%20crisp%20and%20healthy%20appearance%2C%20high-detail%20photography%2C%20clean%20background%2C%20natural%20lighting%2C%20showing%20texture%20and%20freshness%2C%20product%20photography%20style%2C%20no%20human%20elements.&width=160&height=160&seq=6&orientation=squarish"
      },
      {
        id: 3,
        name: "土鸡蛋",
        description: "散养土鸡所产，蛋黄饱满，营养丰富",
        price: "28.8",
        sales: 2100,
        stock: 200,
        image: "https://readdy.ai/api/search-image?query=Organic%20farm%20fresh%20eggs%2C%20brown%20shells%2C%20arranged%20neatly%2C%20high-detail%20photography%2C%20clean%20background%2C%20natural%20lighting%2C%20showing%20texture%20and%20freshness%2C%20product%20photography%20style%2C%20no%20human%20elements%2C%20professional%20food%20photography.&width=160&height=160&seq=7&orientation=squarish"
      },
      {
        id: 4,
        name: "有机红薯",
        description: "农家种植，口感香甜，富含膳食纤维",
        price: "15.9",
        sales: 1560,
        stock: 400,
        image: "https://readdy.ai/api/search-image?query=Organic%20sweet%20potatoes%2C%20freshly%20harvested%2C%20earthy%20brown%20skin%20with%20vibrant%20orange%20flesh%2C%20farm%20fresh%20produce%2C%20high-detail%20photography%2C%20clean%20background%2C%20natural%20lighting%2C%20showing%20texture%20and%20freshness%2C%20product%20photography%20style%2C%20no%20human%20elements.&width=160&height=160&seq=8&orientation=squarish"
      },
      {
        id: 5,
        name: "新鲜玉米",
        description: "当季采摘，颗粒饱满，甜度适中",
        price: "9.9",
        sales: 1890,
        stock: 350,
        image: "https://readdy.ai/api/search-image?query=Fresh%20corn%20on%20the%20cob%2C%20bright%20yellow%20kernels%2C%20partially%20peeled%20husk%2C%20farm%20fresh%20produce%2C%20high-detail%20photography%2C%20clean%20background%2C%20natural%20lighting%2C%20showing%20texture%20and%20freshness%2C%20product%20photography%20style%2C%20no%20human%20elements.&width=160&height=160&seq=9&orientation=squarish"
      },
      {
        id: 6,
        name: "有机黄瓜",
        description: "绿色种植，脆嫩多汁，清香可口",
        price: "7.5",
        sales: 2300,
        stock: 600,
        image: "https://readdy.ai/api/search-image?query=Organic%20cucumbers%2C%20bright%20green%20color%2C%20smooth%20skin%2C%20farm%20fresh%20produce%2C%20high-detail%20photography%2C%20clean%20background%2C%20natural%20lighting%2C%20showing%20texture%20and%20freshness%2C%20product%20photography%20style%2C%20no%20human%20elements%2C%20professional%20food%20photography.&width=160&height=160&seq=10&orientation=squarish"
      }
    ]);
    const changeCategory = (index) => {
      currentCategory.value = index;
    };
    const goToSearch = () => {
      common_vendor.index.navigateTo({
        url: "/pages/searchProduct/searchProduct"
      });
    };
    common_vendor.onMounted(() => {
    });
    return (_ctx, _cache) => {
      return {
        a: common_vendor.p({
          type: "search",
          size: "18",
          color: "#999999"
        }),
        b: common_vendor.o(goToSearch),
        c: common_vendor.f(categories.value, (category, index, i0) => {
          return {
            a: common_vendor.t(category.name),
            b: index,
            c: common_vendor.n(currentCategory.value === index ? "category-active" : ""),
            d: common_vendor.o(($event) => changeCategory(index), index)
          };
        }),
        d: common_vendor.f(products.value, (product, index, i0) => {
          return {
            a: product.image,
            b: common_vendor.t(product.name),
            c: common_vendor.t(product.description),
            d: common_vendor.t(product.sales),
            e: common_vendor.t(product.stock),
            f: common_vendor.t(product.price),
            g: index
          };
        })
      };
    };
  }
};
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/productList/productList.js.map
