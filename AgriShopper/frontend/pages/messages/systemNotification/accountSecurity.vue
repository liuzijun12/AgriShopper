<template>
  <view class="container">
    <text>页面已删除</text>
  </view>
</template>

<script>
export default {
  name: 'AccountSecurity',
  data() {
    return {}
  },
  onLoad() {
    // 自动返回上一页
    uni.navigateBack()
  }
}
</script>

<style scoped>
.container {
  padding: 20px;
  text-align: center;
}
</style> 