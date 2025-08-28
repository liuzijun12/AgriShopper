// pages.config.ts
import { defineUniPages } from "@uni-helper/vite-plugin-uni-pages";

export default defineUniPages({
  pages: [
    {
      path: "pages/index/index",
      type: "home"
    },
    {
      path: "pages/login/login",
      type: "page"
    },
    {
      path: "pages/mine/profile",
      type: "page"
    }
  ],
  globalStyle: {
    navigationBarBackgroundColor: "@navBgColor",
    navigationBarTextStyle: "@navTxtStyle",
    navigationBarTitleText: "Wot-Demo",
    backgroundColor: "@bgColor",
    backgroundTextStyle: "@bgTxtStyle",
    backgroundColorTop: "@bgColorTop",
    backgroundColorBottom: "@bgColorBottom",
    enablePullDownRefresh: false,
    onReachBottomDistance: 50,
    animationType: "pop-in",
    animationDuration: 300,
  },
  tabBar: {
    custom: true,
    customize: true,
    overlay: true,
    height: "0",
    color: "@tabColor",
    selectedColor: "@tabSelectedColor",
    backgroundColor: "@tabBgColor",
    borderStyle: "@tabBorderStyle",
    list: [
      {
        pagePath: "pages/index/index"
      },
      {
        pagePath: "pages/mine/profile"
      }
    ]
  },
  subPackages: [
    {
      root: "ubPackages_user",
      pages: [
        {
          path: "pages/category/category",
          style: { navigationBarTitleText: "分类" }
        },
        {
          path: "pages/logistics/logistics",
          style: { navigationBarTitleText: "物流" }
        },
        {
          path: "pages/productList/productList",
          style: { navigationBarTitleText: "商品列表" }
        },
        {
          path: "pages/search/search",
          style: { navigationBarTitleText: "搜索" }
        },
        {
          path: "pages/mine/mine",
          style: { navigationBarTitleText: "我的" }
        }
      ]
    },
    {
      root: "ubPackages_merchant",
      pages: [
        {
          path: "pages/index/index",
          style: { navigationBarTitleText: "商户首页" }
        },
        {
          path: "pages/mine/index",
          style: { navigationBarTitleText: "商户中心" }
        },
        {
          path: "pages/work/index",
          style: { navigationBarTitleText: "工作台" }
        }
      ]
    }
  ]
});
