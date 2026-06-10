<template>
  <el-popover
    :visible="visible"
    placement="bottom-start"
    :width="280"
    trigger="click"
    @show="$emit('show')"
    @hide="$emit('hide')"
  >
    <template #reference>
      <span
        class="col-filter-trigger"
        :class="{ 'is-active': hasActiveFilter }"
        @click="visible = !visible"
      >
        <el-icon :size="14"><Search /></el-icon>
      </span>
    </template>

    <div class="col-filter-panel">
      <!-- 已添加的筛选条件 -->
      <div class="filter-conditions">
        <div v-for="(cond, idx) in conditions" :key="idx" class="filter-row">
          <!-- 条件连接符 -->
          <span v-if="idx > 0" class="filter-and">且</span>

          <!-- 运算符选择 -->
          <el-select
            v-model="cond.operator"
            size="small"
            style="width: 110px"
            @change="onConditionChange"
          >
            <el-option
              v-for="op in availableOperators"
              :key="op.value"
              :label="op.label"
              :value="op.value"
            />
          </el-select>

          <!-- 值输入（为空/不为空时不显示） -->
          <el-input
            v-if="!isNullaryOp(cond.operator)"
            v-model="cond.value"
            size="small"
            :placeholder="inputPlaceholder"
            style="width: 100px"
            @input="onConditionChange"
          />

          <!-- 删除 -->
          <el-button link size="small" @click="removeCondition(idx)">
            <el-icon :size="14"><Close /></el-icon>
          </el-button>
        </div>
      </div>

      <!-- 底部操作 -->
      <div class="filter-footer">
        <el-button size="small" :disabled="conditions.length >= 3" @click="addCondition">
          <el-icon :size="12"><Plus /></el-icon> 添加条件
        </el-button>
        <div class="filter-footer-right">
          <el-button size="small" @click="clearAll">清空</el-button>
          <el-button size="small" type="primary" @click="applyFilter">确定</el-button>
        </div>
      </div>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { Search, Plus, Close } from '@element-plus/icons-vue'

export interface FilterCondition {
  operator: string
  value: string
}

const props = defineProps<{
  modelValue?: FilterCondition[]
  columnType?: 'string' | 'number' | 'date' | 'datetime' | 'boolean'
  columnLabel?: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', val: FilterCondition[]): void
  (e: 'filter-change', val: FilterCondition[]): void
  (e: 'show' | 'hide'): void
}>()

const visible = ref(false)

// Operator definitions by data type
const OPERATORS: Record<string, { value: string; label: string }[]> = {
  string: [
    { value: 'contains', label: '包含' },
    { value: 'notContains', label: '不包含' },
    { value: 'equals', label: '等于' },
    { value: 'notEquals', label: '不等于' },
    { value: 'startsWith', label: '以...开始' },
    { value: 'endsWith', label: '以...结尾' },
    { value: 'isEmpty', label: '为空' },
    { value: 'isNotEmpty', label: '不为空' }
  ],
  number: [
    { value: 'gt', label: '大于' },
    { value: 'gte', label: '大于等于' },
    { value: 'lt', label: '小于' },
    { value: 'lte', label: '小于等于' },
    { value: 'equals', label: '等于' },
    { value: 'notEquals', label: '不等于' },
    { value: 'isEmpty', label: '为空' },
    { value: 'isNotEmpty', label: '不为空' }
  ],
  date: [
    { value: 'gt', label: '大于' },
    { value: 'gte', label: '大于等于' },
    { value: 'lt', label: '小于' },
    { value: 'lte', label: '小于等于' },
    { value: 'equals', label: '等于' },
    { value: 'notEquals', label: '不等于' },
    { value: 'isEmpty', label: '为空' },
    { value: 'isNotEmpty', label: '不为空' }
  ],
  datetime: [
    { value: 'gt', label: '大于' },
    { value: 'gte', label: '大于等于' },
    { value: 'lt', label: '小于' },
    { value: 'lte', label: '小于等于' },
    { value: 'equals', label: '等于' },
    { value: 'notEquals', label: '不等于' },
    { value: 'isEmpty', label: '为空' },
    { value: 'isNotEmpty', label: '不为空' }
  ],
  boolean: [
    { value: 'isTrue', label: '是' },
    { value: 'isFalse', label: '否' },
    { value: 'isEmpty', label: '为空' },
    { value: 'isNotEmpty', label: '不为空' }
  ]
}

const NULLARY_OPS = ['isEmpty', 'isNotEmpty', 'isTrue', 'isFalse']

const availableOperators = computed(
  () => OPERATORS[props.columnType || 'string'] || OPERATORS.string
)
const inputPlaceholder = computed(() => {
  const t = props.columnType || 'string'
  if (t === 'number') return '输入数值'
  if (t === 'date' || t === 'datetime') return '输入日期'
  return '输入关键词'
})

const conditions = ref<FilterCondition[]>([{ operator: 'contains', value: '' }])

const hasActiveFilter = computed(() => {
  return conditions.value.some((c) => {
    if (NULLARY_OPS.includes(c.operator)) return true
    return c.value.trim() !== ''
  })
})

function isNullaryOp(op: string) {
  return NULLARY_OPS.includes(op)
}

function addCondition() {
  conditions.value.push({ operator: 'contains', value: '' })
}

function removeCondition(idx: number) {
  if (conditions.value.length <= 1) {
    conditions.value[0] = { operator: 'contains', value: '' }
    return
  }
  conditions.value.splice(idx, 1)
}

function onConditionChange() {
  // Live preview — emit changes but don't close
}

function clearAll() {
  conditions.value = [{ operator: 'contains', value: '' }]
  emit('update:modelValue', [])
  emit('filter-change', [])
}

function applyFilter() {
  const active = conditions.value.filter((c) => {
    if (NULLARY_OPS.includes(c.operator)) return true
    return c.value.trim() !== ''
  })
  emit('update:modelValue', active)
  emit('filter-change', active)
  visible.value = false
}

// Sync external changes
watch(
  () => props.modelValue,
  (val) => {
    if (val && val.length > 0) {
      conditions.value = val.map((c) => ({ ...c }))
    }
  },
  { deep: true }
)
</script>

<style lang="scss" scoped>
.col-filter-trigger {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  border-radius: 4px;
  cursor: pointer;
  color: var(--el-text-color-placeholder);
  opacity: 0;
  transition: all 0.2s;
  margin-left: 2px;
  vertical-align: middle;

  &:hover {
    color: var(--el-color-primary);
    background: var(--el-color-primary-light-9);
  }

  &.is-active {
    opacity: 1;
    color: var(--el-color-primary);
  }
}

.col-filter-panel {
  .filter-conditions {
    max-height: 200px;
    overflow-y: auto;
  }

  .filter-row {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-bottom: 8px;
  }

  .filter-and {
    font-size: 12px;
    color: var(--el-color-primary);
    font-weight: 600;
    padding: 0 4px;
    flex-shrink: 0;
  }

  .filter-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-top: 8px;
    border-top: 1px solid var(--el-border-color-lighter);
    margin-top: 4px;
  }

  .filter-footer-right {
    display: flex;
    gap: 6px;
  }
}
</style>

<!-- Global style to make filter icon appear on column header hover -->
<style lang="scss">
/* 表头悬浮时显示筛选图标 */
.el-table__header-wrapper {
  th {
    .cell {
      display: flex;
      align-items: center;
      gap: 2px;
    }

    &:hover .col-filter-trigger {
      opacity: 1;
    }
  }
}
</style>
