<script setup lang="ts">
import { onLaunch, onShow, onHide } from "@dcloudio/uni-app";
import { useTheme } from "@/composables/useTheme";
import { useUserStore } from "@/store/modules/user";
import { interceptNavigation } from "@/utils/navigationHandler.js";

const { initTheme } = useTheme();
const userStore = useUserStore();

onLaunch(() => {
  console.log("App Launch");
  // 初始化主题
  initTheme();
  // 初始化用户状态
  userStore.initUserState();
  // 初始化导航拦截
  interceptNavigation();
});

onShow(() => {
  console.log("App Show");
});

onHide(() => {
  console.log("App Hide");
});
</script>

<style lang="scss"></style>
