import { createSSRApp } from 'vue'
import App from './App.vue'
import { store } from './store'

// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
  ...App
})
app.$mount()
// #endif

export function createApp() {
  const app = createSSRApp(App)
  
  // 添加全局状态
  app.config.globalProperties.$store = store
  
  // 添加全局生命周期
  app.config.globalProperties.$onLaunched = new Promise(resolve => {
    app.config.globalProperties.$isResolveOnLaunched = resolve
  })
  
  // 添加全局错误处理
  app.config.errorHandler = (err, instance, info) => {
    console.error('Vue Error:', err, info)
    // 可以在这里添加错误上报逻辑
  }
  
  // 添加全局属性
  app.config.globalProperties.$filters = {
    // 价格格式化
    formatPrice(price) {
      return '¥' + Number(price).toFixed(2)
    },
    // 日期格式化
    formatDate(date) {
      return new Date(date).toLocaleDateString()
    }
  }
  
  return {
    app
  }
}