/**
 * API 扩展类型定义
 * 全局类型已在 global.d.ts 中定义
 */

declare global {
  /** 选项数据 */
  interface Option<T = any> {
    /** 选项值 */
    value: T;
    /** 选项标签 */
    label: string;
    /** 是否禁用 */
    disabled?: boolean;
    /** 子选项 */
    children?: Option<T>[];
  }
}

export {};