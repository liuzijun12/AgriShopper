/**
 * API 统一导出
 */

// 认证相关
export { default as AuthAPI } from "./auth";
export type { LoginData, WxLoginData, LoginResult } from "./auth";

// 用户相关
export { default as UserAPI } from "./user";
export type { 
  UserInfo, 
  UserPageQuery, 
  UserPageVO, 
  UserProfileVO, 
  UserProfileForm,
  PasswordChangeForm,
  MobileBindingForm,
  EmailBindingForm,
  UserForm,
  WechatPhoneData,
  PhoneNumberResult
} from "./user";

// 文件相关
export { default as FileAPI } from "./file";
export type { FileInfo } from "./file";

// 商品相关
export { default as ProductAPI } from "./product";
export type { 
  Product, 
  ProductPageQuery, 
  ProductPageVO 
} from "./product";

// 分类相关
export { default as CategoryAPI } from "./category";
export type { 
  Category, 
  CategoryTree 
} from "./category";

// 购物车相关
export { default as CartAPI } from "./cart";
export type { 
  CartItem, 
  CartSummary 
} from "./cart";

// 收藏相关
export { default as FavoriteAPI } from "./favorite";
export type { 
  FavoriteItem, 
  FavoritePageQuery 
} from "./favorite";