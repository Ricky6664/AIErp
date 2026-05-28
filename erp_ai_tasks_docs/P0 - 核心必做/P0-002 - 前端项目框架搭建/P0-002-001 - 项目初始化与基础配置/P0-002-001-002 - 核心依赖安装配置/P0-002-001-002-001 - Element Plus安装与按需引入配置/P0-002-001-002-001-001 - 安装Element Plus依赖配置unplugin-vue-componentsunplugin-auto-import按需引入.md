# P0-002-001-002-001-001 安装Element Plus依赖+配置unplugin-vue-components+unplugin-auto-import按需引入

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P0-002-001-002-001-001 |
| 任务名称 | 安装Element Plus依赖+配置unplugin-vue-components+unplugin-auto-import按需引入 |
| 所属模块 | P0-002 |
| 优先级 | P0 |
| 任务类型 | 配置/基础设施 |

## 二、任务目标

安装Element Plus(pnpm add element-plus)与按需引入插件(pnpm add -D unplugin-vue-components unplugin-auto-import)、vite.config.ts配置AutoImport(resolvers: ElementPlusResolver, imports: vue/vue-router/pinia)与Components(resolvers: ElementPlusResolver)、配置主题色--el-color-primary=#409EFF、无需全量引入

## 三、前置依赖

### 3.1 前置任务

- P0-002-001-002-001 Element Plus安装与按需引入配置（父任务）

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
> - 任务类型：前端基础设施配置
> - 技术栈：Vue 3.5.x + Vite 6.x + TypeScript 5.x + Element Plus 2.8.x
> - 项目目录：erp-web/（前端项目根目录）

### 5.1 安装依赖
```bash
pnpm add element-plus
pnpm add -D unplugin-vue-components unplugin-auto-import
```

### 5.2 vite.config.ts 配置
```ts
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

export default defineConfig({
  plugins: [
    // ... 其他插件
    AutoImport({
      imports: ['vue', 'vue-router', 'pinia'],
      resolvers: [ElementPlusResolver()],
      dts: 'src/auto-imports.d.ts'
    }),
    Components({
      resolvers: [ElementPlusResolver()],
      dts: 'src/components.d.ts'
    })
  ]
})
```

### 5.3 关键配置项
| 配置项 | 作用 | 注意事项 |
|--------|------|----------|
| `AutoImport.imports` | 自动导入 `ref`/`reactive`/`computed`/`watch` 等 Vue API | 无需手动 `import { ref } from 'vue'` |
| `AutoImport.resolvers` | 自动导入 Element Plus 的方法（ElMessage/ElMessageBox 等） | 必须配置，否则方法引入不生效 |
| `Components.resolvers` | 自动导入 Element Plus 组件（ElButton/ElTable 等） | 模板中直接使用 `<el-button>` 无需 import |
| `dts: true` | 生成 TypeScript 声明文件 | 为 `auto-imports.d.ts` 和 `components.d.ts` |
| `ElementPlusResolver` | 按需引入 Element Plus 组件和样式 | 不配置则全量引入，产物超大 |

### 5.4 自动生成的声明文件
- `src/auto-imports.d.ts`：Vue/Vue Router/Pinia/Element Plus 自动导入的类型声明
- `src/components.d.ts`：自动注册组件的类型声明
- 这两个文件应加入 `.gitignore`（自动生成，不纳入版本控制）

### 5.5 环境适配
- **不要** 在 `main.ts` 中全量引入 Element Plus（`import ElementPlus from 'element-plus'`），与按需引入冲突
- **不要** 在 `main.ts` 中引入 `element-plus/dist/index.css`，按需引入自动处理样式
- 自动导入插件仅在 Vite 开发/构建时生效，不影响运行时
- 按需引入后构建产物应 < 500KB（全量引入 > 2MB）

## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | erp-web/vite.config.ts | AutoImport + Components 插件配置 |
| 2 | erp-web/package.json | element-plus 和 unplugin 依赖 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | AutoImport 配置 `imports: ['vue', 'vue-router', 'pinia']` 生效 | 组件中直接使用 `ref`/`computed` 无需 import |
| 2 | Components 配置 `ElementPlusResolver` 生效 | 模板中直接使用 `<el-button>` 无需 import |
| 3 | 自动生成 `auto-imports.d.ts` 和 `components.d.ts` | `pnpm dev` 后检查文件是否存在 |
| 4 | 构建产物按需引入 < 500KB | 执行 `pnpm build` 后检查 dist/ 总大小 |

## 八、易错警示

> ⚠️ `dts: true` 必须配置，否则 TypeScript 无法识别自动导入的 API 和组件

> ⚠️ `ElementPlusResolver` 必须在 AutoImport 和 Components 两处都配置，否则方法/组件样式引入不完整

> ⚠️ 不要在 `main.ts` 中 `import ElementPlus from 'element-plus'`，与按需引入冲突

> ⚠️ 不要在 `main.ts` 中 `import 'element-plus/dist/index.css'`，否则全量引入样式
