/**
 * 全局导航栏处理工具
 */
import { useUserStore } from '@/store/modules/user'

// 处理返回首页逻辑
export function goToHome() {
  const userStore = useUserStore()

  console.log('goToHome - 用户登录状态:', userStore.isLoggedIn)
  console.log('goToHome - 是否商户:', userStore.isMerchant)
  console.log('goToHome - 用户信息:', userStore.userInfo)

  // 获取当前页面路由，根据当前页面类型决定跳转目标
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const currentRoute = currentPage.route

  console.log('goToHome - 当前路由:', currentRoute)

  // 如果当前在商户分包页面，直接替换到商户首页
  if (currentRoute.includes('subPackages_merchant/')) {
    console.log('当前在商户分包页面，直接替换到商户首页')
    uni.redirectTo({
      url: '/subPackages_merchant/pages/index/index'
    })
  }
  // 如果用户是商户类型，也直接替换到商户首页
  else if (userStore.isLoggedIn && userStore.isMerchant) {
    console.log('商户用户，直接替换到商户首页')
    uni.redirectTo({
      url: '/subPackages_merchant/pages/index/index'
    })
  }
  // 其他情况跳转到普通用户首页
  else {
    console.log('跳转到普通用户首页')
    uni.switchTab({
      url: '/pages/index/index'
    })
  }
}

// 全局拦截导航返回
export function interceptNavigation() {
  // 重写 uni.navigateBack 方法
  const originalNavigateBack = uni.navigateBack

  uni.navigateBack = function (options = {}) {
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1]
    const currentRoute = currentPage.route

    console.log('导航返回拦截 - 当前路由:', currentRoute)
    console.log('导航返回拦截 - 页面栈长度:', pages.length)

    // 需要特殊处理的底部导航栏页面（商户页面已在页面内处理，这里只处理用户页面）
    const tabBarPages = [
      'subPackages_user/pages/category/category',
      'subPackages_user/pages/logistics/logistics',
      'subPackages_user/pages/mine/mine'
    ]

    // 检查是否是底部导航栏页面
    const isTabBarPage = tabBarPages.some(route => currentRoute.includes(route))

    if (isTabBarPage) {
      // 如果页面栈只有1个页面，说明是直接访问的，应该跳转到首页
      if (pages.length <= 1) {
        console.log('底部导航栏页面且页面栈为空，跳转到对应首页')
        goToHome()
        return
      }

      // 如果有多个页面，检查上一个页面是否也是同类型的页面
      const previousPage = pages[pages.length - 2]
      const previousRoute = previousPage.route

      // 如果上一个页面是用户分包页面，而当前是商户分包页面（或反之），跳转到对应首页
      const isCurrentMerchant = currentRoute.includes('subPackages_merchant/')
      const isPreviousMerchant = previousRoute.includes('subPackages_merchant/')

      if (isCurrentMerchant !== isPreviousMerchant) {
        console.log('跨用户类型页面返回，跳转到对应首页')
        goToHome()
        return
      }

      // 如果上一个页面是主包首页，跳转到对应首页
      if (previousRoute.includes('pages/index/index')) {
        console.log('上一个页面是主包首页，跳转到对应首页')
        goToHome()
        return
      }
    }

    console.log('执行原始返回逻辑')
    // 否则执行原始的返回逻辑
    originalNavigateBack.call(this, options)
  }
}

// 设置页面导航栏右侧按钮
export function setNavigationBarButton(title = '首页') {
  uni.setNavigationBarTitle({
    title: title
  })

  // 设置右侧按钮
  uni.setNavigationBarColor({
    frontColor: '#000000',
    backgroundColor: '#ffffff'
  })
}