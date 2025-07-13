"use strict";
const common_vendor = require("../../common/vendor.js");
if (!Array) {
  const _component_uni_icons = common_vendor.resolveComponent("uni-icons");
  _component_uni_icons();
}
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
    const products = common_vendor.ref([
      {
        name: "有机红薯",
        description: "农家自种，无公害种植",
        price: "12.8",
        originalPrice: "15.8",
        imageUrl: "https://readdy.ai/api/search-image?query=icon%2C%20Realistic%20food%2C%20photorealistic%20sweet%20potato%2C%20high-detail%203D%20rendering%2C%20prominent%20main%20subjects%2C%20clear%20and%20sharp%2C%20subject%20fills%2080%20percent%20of%20frame%2C%20isolated%20on%20white%20background%2C%20centered%20composition%2C%20soft%20lighting%2C%20subtle%20shadows%2C%20product%20photography%20style&width=300&height=300&seq=4&orientation=squarish"
      },
      {
        name: "优质玉米饲料",
        description: "高营养，适合家禽喂养",
        price: "45.9",
        originalPrice: "59.9",
        imageUrl: "https://readdy.ai/api/search-image?query=icon%2C%20Realistic%20food%2C%20photorealistic%20corn%20feed%20grains%2C%20high-detail%203D%20rendering%2C%20prominent%20main%20subjects%2C%20clear%20and%20sharp%2C%20subject%20fills%2080%20percent%20of%20frame%2C%20isolated%20on%20white%20background%2C%20centered%20composition%2C%20soft%20lighting%2C%20subtle%20shadows%2C%20product%20photography%20style&width=300&height=300&seq=5&orientation=squarish"
      },
      {
        name: "枸杞菊花茶",
        description: "养生保健，明目润肺",
        price: "38.5",
        originalPrice: "45.0",
        imageUrl: "https://readdy.ai/api/search-image?query=icon%2C%20Realistic%20food%2C%20photorealistic%20goji%20berries%20and%20chrysanthemum%20tea%2C%20high-detail%203D%20rendering%2C%20prominent%20main%20subjects%2C%20clear%20and%20sharp%2C%20subject%20fills%2080%20percent%20of%20frame%2C%20isolated%20on%20white%20background%2C%20centered%20composition%2C%20soft%20lighting%2C%20subtle%20shadows%2C%20product%20photography%20style&width=300&height=300&seq=6&orientation=squarish"
      },
      {
        name: "新鲜胡萝卜",
        description: "富含胡萝卜素，助力健康",
        price: "8.8",
        originalPrice: "10.8",
        imageUrl: "https://readdy.ai/api/search-image?query=icon%2C%20Realistic%20food%2C%20photorealistic%20fresh%20carrots%2C%20high-detail%203D%20rendering%2C%20prominent%20main%20subjects%2C%20clear%20and%20sharp%2C%20subject%20fills%2080%20percent%20of%20frame%2C%20isolated%20on%20white%20background%2C%20centered%20composition%2C%20soft%20lighting%2C%20subtle%20shadows%2C%20product%20photography%20style&width=300&height=300&seq=7&orientation=squarish"
      }
    ]);
    const currentSwiper = common_vendor.ref(0);
    common_vendor.ref(1);
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
    return (_ctx, _cache) => {
      return {
        a: common_vendor.p({
          type: "search",
          size: "24",
          color: "#999"
        }),
        b: common_vendor.o(goToSearch),
        c: common_vendor.f(bannerList.value, (item, index, i0) => {
          return {
            a: item.imageUrl,
            b: index
          };
        }),
        d: common_vendor.o(onSwiperChange),
        e: common_vendor.f(bannerList.value, (item, index, i0) => {
          return {
            a: index,
            b: common_vendor.n(currentSwiper.value === index ? "active" : "")
          };
        }),
        f: common_vendor.p({
          type: "left",
          size: "24",
          color: "#333"
        }),
        g: common_vendor.o(prevSwiper),
        h: common_vendor.p({
          type: "right",
          size: "24",
          color: "#333"
        }),
        i: common_vendor.o(nextSwiper),
        j: common_vendor.f(products.value, (product, index, i0) => {
          return common_vendor.e({
            a: product.imageUrl,
            b: common_vendor.t(product.name),
            c: common_vendor.t(product.description),
            d: common_vendor.t(product.price),
            e: product.originalPrice
          }, product.originalPrice ? {
            f: common_vendor.t(product.originalPrice)
          } : {}, {
            g: index
          });
        })
      };
    };
  }
};
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/index/index.js.map
