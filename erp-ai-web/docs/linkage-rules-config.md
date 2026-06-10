# 联动规则配置说明

## 概述

联动规则（Linkage Rules）定义表单中字段之间的联动关系。当触发字段的值满足条件时，自动对目标字段执行指定动作（显示/隐藏/启用/禁用/设值/设选项）。

联动规则支持两种形式：

- **JSON可序列化格式**（`LinkageRuleConfig[]`）：用于后端API传输和存储，条件使用运算符表达式
- **运行时格式**（`FieldLinkageRule[]`）：用于前端组件执行，条件为可执行函数

使用 `parseLinkageJson()` 函数将JSON格式转换为运行时格式。

## 配置结构

### LinkageRuleConfig（JSON格式）

```json
{
  "triggerField": "string (必填) - 触发字段名",
  "targetField": "string (必填) - 目标字段名",
  "action": "string (必填) - 联动动作",
  "condition": {
    "operator": "string - 条件运算符",
    "value": "any - 条件参考值"
  },
  "params": {
    "key": "value - 联动参数，含义取决于action"
  }
}
```

### 联动动作 (action)

| 动作         | 说明                   | params                            |
| ------------ | ---------------------- | --------------------------------- |
| `show`       | 显示目标字段           | 无                                |
| `hide`       | 隐藏目标字段           | 无                                |
| `enable`     | 启用目标字段           | 无                                |
| `disable`    | 禁用目标字段           | 无                                |
| `setValue`   | 设置目标字段的值       | `{ value: 目标值 }`               |
| `setOptions` | 设置目标字段的选项列表 | `{ options: [{ label, value }] }` |

### 条件运算符 (condition.operator)

| 运算符       | 说明                      | value类型 |
| ------------ | ------------------------- | --------- |
| `eq`         | 等于                      | 任意      |
| `neq`        | 不等于                    | 任意      |
| `gt`         | 大于                      | Number    |
| `gte`        | 大于等于                  | Number    |
| `lt`         | 小于                      | Number    |
| `lte`        | 小于等于                  | Number    |
| `in`         | 在列表中                  | Array     |
| `notIn`      | 不在列表中                | Array     |
| `isEmpty`    | 为空（null/undefined/''） | 无需value |
| `isNotEmpty` | 不为空                    | 无需value |
| `startsWith` | 以...开头                 | String    |
| `endsWith`   | 以...结尾                 | String    |
| `contains`   | 包含                      | String    |

## 使用方式

```typescript
import { parseLinkageJson } from '@/composables/useFormLinkage'
import type { LinkageRuleConfig } from '@/types/list-table'

// 从后端API获取的JSON联动规则
const jsonRules: LinkageRuleConfig[] = [
  {
    triggerField: 'category',
    targetField: 'subCategory',
    action: 'show',
    condition: { operator: 'eq', value: 'A' }
  }
]

// 解析为可执行规则
const executableRules = parseLinkageJson(jsonRules)

// 可嵌入FormFieldConfig中使用
const fieldConfig: FormFieldConfig = {
  field: 'category',
  fieldType: 'select',
  linkages: executableRules
}
```

## 默认配置

默认联动规则配置示例文件：`src/config/linkage-rules-default.json`，包含所有运算符和动作类型的演示示例。
