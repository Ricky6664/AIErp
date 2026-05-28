# P0-002-001-001-003-001 安装Husky+lint-staged依赖+编写.husky/pre-commit脚本+配置lint-staged文件匹配规则

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P0-002-001-001-003-001 |
| 任务名称 | 安装Husky+lint-staged依赖+编写.husky/pre-commit脚本+配置lint-staged文件匹配规则 |
| 所属模块 | P0-002 |
| 优先级 | P0 |
| 任务类型 | 配置/基础设施 |

## 二、任务目标

安装husky+lint-staged(pnpm add -D husky lint-staged)、pnpm exec husky init初始化.husky/、配置.husky/pre-commit(pnpm exec lint-staged)、package.json添加lint-staged配置(*.{vue,ts,tsx}: eslint --fix + prettier --write, *.{css,scss}: prettier --write)、提交时自动校验

## 三、前置依赖

### 3.1 前置任务

- P0-002-001-001-003 Husky + lint-staged配置（父任务）

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
> - 技术栈：Husky 9.x + lint-staged 15.x + ESLint 8.x + Prettier 3.x
> - 项目目录：erp-web/（前端项目根目录）

### 5.1 安装依赖
```bash
pnpm add -D husky lint-staged
```

### 5.2 初始化 Husky
```bash
pnpm exec husky init
```
- 生成 `.husky/` 目录和 `.husky/pre-commit` 文件
- 自动在 `package.json` 中添加 `"prepare": "husky"` 脚本

### 5.3 配置 .husky/pre-commit
```bash
#!/usr/bin/env sh
. "$(dirname -- "$0")/_/husky.sh"

pnpm exec lint-staged
```
- 仅对 git 暂存区（staged）文件执行检查，不扫描全量代码
- 如果 lint-staged 退出码非 0，git commit 将被阻止

### 5.4 配置 lint-staged（package.json）
```json
{
  "lint-staged": {
    "*.{vue,ts,tsx}": [
      "eslint --fix",
      "prettier --write"
    ],
    "*.{css,scss}": [
      "prettier --write"
    ],
    "*.{json,md}": [
      "prettier --write"
    ]
  }
}
```
- `*.{vue,ts,tsx}`：先执行 ESLint 自动修复，再执行 Prettier 格式化
- `*.{css,scss}`：仅执行 Prettier 格式化
- 顺序很重要：`eslint --fix` 必须在 `prettier --write` 之前

### 5.5 关键配置项
| 配置项 | 作用 | 注意事项 |
|--------|------|----------|
| `husky init` | 初始化 Git hooks 目录和 prepare 脚本 | 必须在 git 仓库根目录执行 |
| `lint-staged` | 仅对暂存文件运行 linter | 不扫描全量，速度快 |
| `.husky/pre-commit` | Git pre-commit 钩子脚本 | 不要使用 Windows 路径分隔符 `\` |
| `prepare` script | 克隆仓库后自动安装 husky 钩子 | `pnpm install` 时自动触发 |

### 5.6 环境适配
- Husky 9.x 使用 `.husky/` 目录方式，不再使用 `husky install` 命令
- 首次配置后需执行一次 `git add . && git commit` 验证钩子是否生效
- Windows 环境下 `.husky/pre-commit` 脚本中的 `#!/usr/bin/env sh` 确保 Git Bash 兼容
- 如果团队使用 `--no-verify` 跳过钩子，应在 code review 中严格要求禁止使用

## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | erp-web/.husky/pre-commit | Git pre-commit 钩子脚本 |
| 2 | erp-web/package.json | 新增 prepare 脚本和 lint-staged 配置 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | `.husky/pre-commit` 文件存在且包含 `pnpm exec lint-staged` | `cat .husky/pre-commit` 确认内容 |
| 2 | package.json 中有 `"prepare": "husky"` 脚本 | `cat package.json` 检查 scripts 字段 |
| 3 | git commit 时自动触发 lint-staged 对暂存文件校验 | 故意暂存不规范代码后执行 `git commit` 确认被拦截 |

## 八、易错警示

> ⚠️ `pnpm exec husky init` 必须在 git 仓库根目录执行，否则 `.husky/` 生成位置错误

> ⚠️ lint-staged 只处理暂存区文件，不要配置为全量 lint（速度太慢）

> ⚠️ `eslint --fix` 必须在 `prettier --write` 之前，否则 ESLint 修复可能破坏 Prettier 格式
