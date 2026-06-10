<template>
  <div class="page-p13-config">
    <!-- 查询区 -->
    <div v-if="config.showQueryPanel !== false" class="query-area">
      <slot name="query-panel">
        <div class="area-placeholder">
          <el-icon :size="18"><Search /></el-icon>
          <span>查询区 — 可通过 query-panel 插槽自定义</span>
        </div>
      </slot>
    </div>

    <!-- 操作栏 -->
    <div v-if="config.showActionBar !== false" class="action-area">
      <slot name="action-bar">
        <div class="action-bar-default">
          <div class="action-left">
            <el-button v-if="config.showSaveBtn !== false" type="primary" @click="handleSave">
              <el-icon><Check /></el-icon>
              保存
            </el-button>
            <el-button v-if="config.showResetBtn !== false" @click="handleReset">
              <el-icon><RefreshRight /></el-icon>
              重置
            </el-button>
          </div>
        </div>
      </slot>
    </div>

    <!-- 简单配置页（表单式） -->
    <div v-if="(config.configLayout || 'simple') === 'simple'" class="simple-config">
      <el-form
        ref="formRef"
        :model="formModel"
        label-width="140px"
        :style="{ maxWidth: formMaxWidthPx }"
      >
        <div v-for="group in config.groups" :key="group.id" class="config-group-card">
          <div class="config-group-header">
            <el-icon v-if="group.icon" :size="18">
              <component :is="group.icon" />
            </el-icon>
            <span class="config-group-title">{{ group.title }}</span>
          </div>
          <el-row :gutter="16">
            <el-col v-for="field in group.fields" :key="field.id" :span="field.span ?? 24">
              <el-form-item :label="field.label" :prop="field.field" :required="field.required">
                <el-input
                  v-if="field.type === 'input'"
                  v-model="formModel[field.field]"
                  :placeholder="field.placeholder"
                />
                <el-input-number
                  v-else-if="field.type === 'number'"
                  v-model="formModel[field.field]"
                  :placeholder="field.placeholder"
                  controls-position="right"
                  style="width: 100%"
                />
                <el-select
                  v-else-if="field.type === 'select'"
                  v-model="formModel[field.field]"
                  :placeholder="field.placeholder"
                  style="width: 100%"
                >
                  <el-option
                    v-for="opt in field.options"
                    :key="String(opt.value)"
                    :label="opt.label"
                    :value="opt.value"
                  />
                </el-select>
                <el-switch v-else-if="field.type === 'switch'" v-model="formModel[field.field]" />
                <el-date-picker
                  v-else-if="field.type === 'date'"
                  v-model="formModel[field.field]"
                  type="date"
                  :placeholder="field.placeholder"
                  style="width: 100%"
                />
                <el-input
                  v-else-if="field.type === 'textarea'"
                  v-model="formModel[field.field]"
                  type="textarea"
                  :rows="4"
                  :placeholder="field.placeholder"
                />
                <el-color-picker
                  v-else-if="field.type === 'color'"
                  v-model="formModel[field.field]"
                />
                <slot
                  :name="`field-${field.field}`"
                  :field="field"
                  :model-value="formModel[field.field]"
                  @update:model-value="(v: unknown) => (formModel[field.field] = v)"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </el-form>
    </div>

    <!-- 复杂配置页（左右分栏式） -->
    <div v-else class="split-config">
      <div class="split-left">
        <div class="nav-tree-header">
          <el-input v-model="navSearchText" placeholder="搜索配置项" clearable size="small" />
        </div>
        <div class="nav-tree-body">
          <el-tree
            ref="navTreeRef"
            :data="filteredNavItems"
            :props="{ label: 'label', children: 'children' }"
            node-key="id"
            highlight-current
            default-expand-all
            @node-click="handleNavClick"
          />
        </div>
      </div>
      <div class="split-right">
        <div class="split-right-header">
          <span class="split-right-title">{{ currentNavLabel || '请选择配置项' }}</span>
        </div>
        <div class="split-right-body">
          <slot name="config-content" :nav-id="currentNavId" :nav-label="currentNavLabel">
            <div class="area-placeholder">
              <el-icon :size="48"><Setting /></el-icon>
              <span>配置内容区 — 请选择左侧导航项</span>
            </div>
          </slot>
        </div>
      </div>
    </div>

    <!-- 额外区域 -->
    <div v-if="$slots['extra-area']" class="extra-area">
      <slot name="extra-area" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, onMounted, watch } from 'vue'
import { Search, Check, RefreshRight, Setting } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import type {
  PageBaseProps,
  PageBaseEmits,
  ConfigPageConfig,
  ConfigNavItemConfig
} from '@/types/page-base.d.ts'

const props = defineProps<PageBaseProps>()
const emit = defineEmits<PageBaseEmits>()

const config = computed<ConfigPageConfig>(() => {
  return (props.config || {}) as ConfigPageConfig
})

const formMaxWidthPx = computed(() => {
  const w = config.value.formMaxWidth ?? 960
  return typeof w === 'number' ? `${w}px` : w
})

const formRef = ref<FormInstance>()
const navTreeRef = ref()

const navSearchText = ref('')
const currentNavId = ref('')
const currentNavLabel = ref('')

const filteredNavItems = computed<ConfigNavItemConfig[]>(() => {
  if (!navSearchText.value) return config.value.navItems || []
  const keyword = navSearchText.value.toLowerCase()
  const filterTree = (items: ConfigNavItemConfig[]): ConfigNavItemConfig[] => {
    return items
      .filter((item) => {
        const match = item.label.toLowerCase().includes(keyword)
        const childMatch = item.children ? filterTree(item.children).length > 0 : false
        return match || childMatch
      })
      .map((item) => ({
        ...item,
        children: item.children ? filterTree(item.children) : undefined
      }))
  }
  return filterTree(config.value.navItems || [])
})

const formModel = reactive<Record<string, unknown>>({})

function initFormModel(): void {
  for (const group of config.value.groups || []) {
    for (const field of group.fields) {
      if (!(field.field in formModel)) {
        formModel[field.field] = field.defaultValue ?? undefined
      }
    }
  }
}

function handleNavClick(node: ConfigNavItemConfig): void {
  if (!node.children || node.children.length === 0) {
    currentNavId.value = node.id
    currentNavLabel.value = node.label
  }
}

function handleSave(): void {
  emit('data-change', { source: 'config-save', data: { ...formModel } })
}

function handleReset(): void {
  for (const group of config.value.groups || []) {
    for (const field of group.fields) {
      formModel[field.field] = field.defaultValue ?? undefined
    }
  }
}

watch(
  () => config.value.groups,
  () => {
    initFormModel()
  },
  { immediate: true }
)

onMounted(() => {
  emit('page-ready', { viewId: props.viewId, pageType: props.pageType })
})
</script>

<style scoped lang="scss">
.page-p13-config {
  padding: 16px;
  height: 100%;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.area-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--el-text-color-placeholder);
  font-size: 14px;
  min-height: 80px;
}

.query-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
  min-height: 56px;
  display: flex;
  align-items: center;
}

.action-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 12px 20px;
  min-height: 48px;
  display: flex;
  align-items: center;

  .action-bar-default {
    display: flex;
    align-items: center;
    width: 100%;

    .action-left {
      display: flex;
      gap: 8px;
    }
  }
}

// 简单配置页
.simple-config {
  flex: 1;
  min-height: 0;
  overflow: auto;

  .config-group-card {
    background: var(--el-bg-color);
    border-radius: 12px;
    padding: 20px 24px;
    margin-bottom: 16px;

    &:last-child {
      margin-bottom: 0;
    }

    .config-group-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 16px;
      padding-bottom: 12px;
      border-bottom: 1px solid var(--el-border-color-lighter);

      .config-group-title {
        font-size: 16px;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }
    }
  }
}

// 复杂配置页（左右分栏）
.split-config {
  flex: 1;
  display: flex;
  gap: 12px;
  min-height: 0;
}

.split-left {
  width: 260px;
  flex-shrink: 0;
  background: var(--el-bg-color);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  .nav-tree-header {
    padding: 12px;
    border-bottom: 1px solid var(--el-border-color-lighter);
  }

  .nav-tree-body {
    flex: 1;
    overflow: auto;
    padding: 8px;

    :deep(.el-tree-node__content) {
      border-radius: 6px;
    }
  }
}

.split-right {
  flex: 1;
  background: var(--el-bg-color);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;

  .split-right-header {
    padding: 12px 20px;
    border-bottom: 1px solid var(--el-border-color-lighter);

    .split-right-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
  }

  .split-right-body {
    flex: 1;
    overflow: auto;
    padding: 20px;
  }
}

.extra-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
}
</style>
