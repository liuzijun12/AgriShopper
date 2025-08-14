<template>
  <wd-config-provider :theme-vars="themeVars" :custom-class="`page-wraper ${theme}`" :theme="theme">
    <slot />
    <wd-tabbar
      :model-value="activeTabbar.name"
      placeholder
      bordered
      safe-area-inset-bottom
      fixed
      @change="handleTabbarChange"
    >
      <wd-tabbar-item
        v-for="(item, index) in tabbarList"
        :key="index"
        :name="item.name"
        :value="getTabbarItemValue(item.name)"
        :title="item.title"
        :icon="item.icon"
      />
    </wd-tabbar>
    <wd-notify />
    <wd-toast />
    <wd-message-box />
  </wd-config-provider>
</template>

<script setup lang="ts">
// import { useRouter, useRoute } from "uni-mini-router"; // 暂时注释掉
import { useTheme } from "@/composables/useTheme";
import { useTabbar } from "@/composables/useTabbar";

// const router = useRouter(); // 暂时注释掉
// const route = useRoute(); // 暂时注释掉
const { themeVars, theme } = useTheme();
const { activeTabbar, getTabbarItemValue, setTabbarItemActive, tabbarList } = useTabbar();

function handleTabbarChange({ value }: { value: string }) {
  setTabbarItemActive(value);
  // 使用 uni 原生导航替代 router
  const tabItem = tabbarList.value.find(item => item.value === value);
  if (tabItem && tabItem.url) {
    uni.switchTab({
      url: tabItem.url
    });
  }
}

onMounted(() => {
  // #ifdef APP-PLUS
  uni.hideTabBar();
  // #endif
  nextTick(() => {
    // 暂时注释掉 route 相关逻辑
    // if (route.name && route.name !== activeTabbar.value.name) {
    //   setTabbarItemActive(route.name);
    // }
  });
});
</script>

<script lang="ts">
export default {
  options: {
    addGlobalClass: true,
    virtualHost: true,
    styleIsolation: "shared",
  },
};
</script>

<style lang="scss">
.page-wraper {
  box-sizing: border-box;
  min-height: calc(100vh - var(--window-top));
  background: #f9f9f9;
}

.wot-theme-dark.page-wraper {
  background: #222;
}
</style>
