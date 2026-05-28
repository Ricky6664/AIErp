# P0-002-004-001-004-001 实现debounce/throttle

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P0-002-004-001-004-001 |
| 任务名称 | 实现debounce/throttle |
| 所属模块 | P0-002 |
| 优先级 | P0 |
| 任务类型 | 综合开发任务 |

## 二、任务目标

leading/trailing配置+取消方法+this绑定

## 三、前置依赖

### 3.1 前置任务

- P0-002-004-001-004 防抖节流工具（父任务）

### 3.2 前置资源

- 项目代码仓库已就绪
- 开发环境已搭建（Node.js 18+ + pnpm 9.x + Vite 6.x）

## 四、关联规范引用

| 规范文档名 | 引用原因 |
|-----------|---------|
| 全局规范-项目架构与开发约束 | 项目架构、技术约束与任务依赖关系 |
| 全局规范-前端代码规范 | 前端代码开发规范约束 |
| 全局规范-末端任务文档编写规范 | 末端任务文档结构与编写规范约束 |
| 全局规范-AI开发执行手册 | AI开发执行流程与规范约束 |

## 五、详细开发规格

> **📦 模块上下文**
> - 任务类型：前端工具函数开发
> - 技术栈：Vue 3.5.x + TypeScript 5.x
> - 工具函数规范：纯函数设计，无副作用，完整TypeScript类型标注
> - 脚本规范：禁止any类型，所有参数和返回值必须有类型注解

### 5.1 函数签名与类型定义

```typescript
// erp-web/src/utils/debounce.ts

/** 防抖/节流配置选项 */
interface DebounceOptions {
  /** 是否在延迟开始时立即执行一次，默认false */
  leading?: boolean;
  /** 是否在延迟结束后执行，默认true */
  trailing?: boolean;
}

/** 防抖/节流返回的增强函数 */
interface DebouncedFunction<T extends (...args: any[]) => any> {
  (...args: Parameters<T>): void;
  /** 取消pending的调用 */
  cancel: () => void;
  /** 立即执行pending的调用 */
  flush: () => void;
}

/**
 * 防抖函数：连续调用只在最后一次触发后延迟执行
 * @param fn - 目标函数
 * @param delay - 延迟毫秒数，默认300
 * @param options - leading/trailing配置
 * @returns 增强后的防抖函数（含cancel/flush方法）
 */
export function debounce<T extends (...args: any[]) => any>(
  fn: T,
  delay?: number,
  options?: DebounceOptions
): DebouncedFunction<T>;

/**
 * 节流函数：固定间隔内只执行一次
 * @param fn - 目标函数
 * @param interval - 间隔毫秒数，默认300
 * @param options - leading/trailing配置
 * @returns 增强后的节流函数（含cancel/flush方法）
 */
export function throttle<T extends (...args: any[]) => any>(
  fn: T,
  interval?: number,
  options?: DebounceOptions
): DebouncedFunction<T>;
```

### 5.2 实现逻辑

1. **debounce**：使用setTimeout延迟执行，每次调用时clearTimeout重置计时；leading=true时首次调用立即执行，trailing=true时最后一次调用后延迟执行
2. **throttle**：使用时间戳+定时器双重控制，记录lastExecTime，当前时间-lastExecTime >= interval时执行；leading控制首次是否立即执行
3. **cancel方法**：清除pending的setTimeout，重置内部状态
4. **flush方法**：立即执行pending的调用（不等待延迟结束）
5. **this绑定**：返回的函数使用`fn.apply(this, args)`确保this和arguments正确透传

**边界处理**：
- delay/interval为0时等同于直接调用
- fn不是函数时throw TypeError
- 组件卸载后调用cancel不会报错

### 5.3 使用场景

1. **搜索输入框**：`const debouncedSearch = debounce(search, 300)` 输入停止300ms后发起搜索
2. **窗口resize**：`const throttledResize = throttle(handleResize, 200)` 限制resize处理频率
3. **按钮防抖**：`const debouncedSubmit = debounce(submitForm, 500, {leading:true, trailing:false})` 首次点击立即提交，后续点击忽略

## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | `erp-web/src/utils/debounce.ts` | debounce/throttle基础实现（含cancel/flush方法） |
| 2 | 验证通过截图 | 浏览器DevTools截图或接口测试记录 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | debounce连续调用5次只执行1次 | console中快速调用验证执行次数 |
| 2 | throttle每200ms最多执行1次 | console中持续调用验证执行间隔 |
| 3 | cancel方法取消pending调用 | 调用cancel后验证不再执行 |
| 4 | leading:true首次调用立即执行 | 验证首次调用无延迟 |

## 八、易错警示

> ⚠️ 自行实现debounce/throttle减少依赖，不要用lodash

> ⚠️ 返回的函数必须支持cancel方法取消pending调用，组件卸载时必须调用

> ⚠️ this和args必须正确透传给原函数，使用apply而非直接调用

> ⚠️ 工具函数必须是纯函数，不要在其中读取localStorage或发起API调用
