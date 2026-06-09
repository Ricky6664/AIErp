import request from '@/utils/request'
import type { PageResult } from '@/types/api'
import type {
  TodoListVO,
  TodoQueryDTO,
  TodoApproveDTO,
  TodoBatchApproveDTO,
  TodoCountVO
} from '@/types/msg'

/** 分页查询待办列表 */
export function getTodoPage(params: TodoQueryDTO): Promise<PageResult<TodoListVO>> {
  return request.get('/api/message/todo', { params })
}

/** 审批待办 */
export function approveTodo(data: TodoApproveDTO): Promise<void> {
  return request.post(`/api/message/todo/${data.todoId}/approve`, data)
}

/** 驳回答复 */
export function rejectTodo(data: TodoApproveDTO): Promise<void> {
  return request.post(`/api/message/todo/${data.todoId}/reject`, data)
}

/** 批量审批 */
export function batchApproveTodo(data: TodoBatchApproveDTO): Promise<void> {
  return request.post('/api/message/todo/batch-approve', data)
}

/** 获取待办数量统计 */
export function getTodoCount(): Promise<TodoCountVO> {
  return request.get('/api/message/todo/count')
}
