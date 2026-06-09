# 验证报告 — P1-003-002-004-001-002

> **任务**：P1-003-002-004-001-002 验证功能（单据待办列表页）
> **验证日期**：2026-06-09
> **验证人**：W10
> **关联任务**：P1-003-002-004-001-001 编写核心代码

---

## 一、正常流程验证（对照父任务验收标准）

| 序号 | 检查项 | 验证方法 | 结果 |
|:---:|--------|---------|:---:|
| 1 | Tab切换正确过滤待办类型，每个Tab显示对应待办数量 | 代码审查：activeTab绑定todoType查询参数，onTabChange重置页码并调用loadData；countMap通过badge显示各Tab数量 | ✅ PASS |
| 2 | 表格列展示正确：来源模块标签、单据编号链接、标题、类型、时间 | 代码审查：7列完整（selection/businessType/businessNo/title/todoType/createTime/dueTime）+操作列 | ✅ PASS |
| 3 | 单据编号点击可跳转到对应单据详情页 | 代码审查：goToDoc使用router.push(`/${businessType}/${businessId}`)动态构建路由 | ✅ PASS |
| 4 | 审批按钮点击弹出意见输入框，提交后调用审批接口 | 代码审查：el-popover+textarea+确认审批按钮，doApprove调用approveTodo API | ✅ PASS |
| 5 | 驳回按钮点击弹出原因输入框，驳回原因为必填 | 代码审查：ElMessageBox.prompt含inputValidator非空校验，调用rejectTodo API | ✅ PASS |
| 6 | 多选复选框+批量审批按钮功能正常，未选中时按钮禁用 | 代码审查：selectedIds.length控制disabled，ElMessageBox.confirm二次确认 | ✅ PASS |
| 7 | 页面编译通过无TypeScript错误 | pnpm build：TodoList.vue/todo.ts/msg.ts/static.ts 零TS错误（构建失败为其他文件预存问题） | ✅ PASS |

---

## 二、边界条件与异常场景验证

| 序号 | 检查项 | 验证方法 | 结果 |
|:---:|--------|---------|:---:|
| 1 | 驳回原因空值校验 | 代码审查：inputValidator检查 `!val \|\| !val.trim()`，返回"驳回原因不能为空" | ✅ PASS |
| 2 | 批量审批未选中时按钮禁用 | 代码审查：`:disabled="selectedIds.length === 0"` | ✅ PASS |
| 3 | Tab切换时页码重置 | 代码审查：onTabChange中 `pageNum.value = 1` | ✅ PASS |
| 4 | 业务类型未知时fallback | 代码审查：`businessTypeMap[row.businessType] \|\| row.businessType` 兜底显示原始值 | ✅ PASS |
| 5 | 审批后关闭Popover | 代码审查：`popoverRefMap[todo.id]?.hide()` 审批完成后关闭弹窗 | ✅ PASS |

---

## 三、易错警示规避确认

| 序号 | 警示项 | 确认结果 |
|:---:|--------|:---:|
| 1 | 批量审批前需二次确认 | ✅ ElMessageBox.confirm已实现 |
| 2 | 单据编号跳转根据businessType动态构建路由 | ✅ goToDoc使用模板字面量动态构建 |
| 3 | 审批意见Popover在表格中正确管理 | ✅ popoverRefMap + setPopoverRef管理实例 |
| 4 | 驳回必须填写原因 | ✅ inputValidator非空校验 |

---

## 四、交付物清单核对

| 序号 | 文件路径 | 状态 |
|:---:|---------|:---:|
| 1 | erp-ai-web/src/views/msg/TodoList.vue | ✅ 存在（206行） |
| 2 | erp-ai-web/src/api/msg/todo.ts | ✅ 存在（35行，5个API函数） |
| 3 | erp-ai-web/src/types/msg.ts | ✅ 已追加6个类型（TodoListVO/TodoQueryDTO/TodoApproveDTO/TodoBatchApproveDTO/TodoCountVO） |
| 4 | erp-ai-web/src/router/modules/static.ts | ✅ 已注册MSG_TODO路由（/msg/todo） |

---

## 五、编译验证

| 项目 | 结果 |
|------|:---:|
| TodoList.vue TypeScript错误 | 0 |
| todo.ts TypeScript错误 | 0 |
| msg.ts TypeScript错误 | 0 |
| static.ts TypeScript错误 | 0 |
| 构建整体结果 | ⚠️ FAIL（12个预存错误，均在system/menu/params/user/warehouse模块，与本次变更无关） |

---

## 六、问题清单

| 序号 | 问题描述 | 严重程度 | 状态 |
|:---:|--------|:---:|:---:|
| 1 | pnpm build存在12个预存TS错误（system模块4文件+warehouse 1文件），非本次变更引入 | 低（已知遗留问题） | ⚠️ 已知问题，不在本次修复范围 |

---

## 七、总结

**验证结论：PASS**

单据待办列表页（TodoList.vue）实现完整且正确：
- 全部7项验收标准通过
- 5项边界条件处理正确
- 4项易错警示全部规避
- 交付物完整（4个文件全部就位）
- TypeScript编译零错误（本任务文件）
- 1个已知遗留问题（其他文件的预存TS错误，不影响本功能）
