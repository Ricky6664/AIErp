<template>
  <div class="page-p14-ai-dialog">
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
        <div class="area-placeholder">
          <el-icon :size="18"><Operation /></el-icon>
          <span>操作栏 — 可通过 action-bar 插槽自定义</span>
        </div>
      </slot>
    </div>

    <!-- 历史对话侧边栏关闭时的快捷入口 -->
    <div v-if="config.historyList && config.historyList.length > 0" class="history-toggle-bar">
      <el-button v-if="!historyVisible" text @click="historyVisible = true">
        <el-icon :size="16"><Histogram /></el-icon>
        <span>历史对话</span>
      </el-button>
      <span v-if="!historyVisible" class="history-hint"
        >共 {{ config.historyList.length }} 条历史对话</span
      >
    </div>

    <!-- AI对话主区域 -->
    <div class="ai-dialog-main">
      <!-- 历史对话侧边栏 -->
      <transition name="history-slide">
        <div
          v-if="config.historyList && config.historyList.length > 0 && historyVisible"
          class="history-panel"
        >
          <div class="history-panel-header">
            <span class="history-panel-title">历史对话</span>
            <el-button text :icon="Close" size="small" @click="historyVisible = false" />
          </div>
          <div class="history-panel-list">
            <div
              v-for="item in config.historyList"
              :key="item.id"
              class="history-item"
              :class="{ 'is-active': activeHistoryId === item.id }"
              @click="handleHistorySelect(item.id)"
            >
              <div class="history-item-title">{{ item.title }}</div>
              <div v-if="item.updatedAt" class="history-item-time">{{ item.updatedAt }}</div>
            </div>
          </div>
        </div>
      </transition>

      <!-- 对话交互区（左侧） -->
      <div class="dialog-panel" :style="dialogPanelStyle">
        <!-- 对话区头部 -->
        <div v-if="config.dialogTitle" class="dialog-panel-header">
          <el-icon :size="18"><ChatDotRound /></el-icon>
          <span>{{ config.dialogTitle }}</span>
        </div>

        <!-- 消息列表 -->
        <div ref="messageListRef" class="dialog-messages" @scroll="handleScroll">
          <!-- 欢迎语 -->
          <div
            v-if="config.welcomeMessage && messages.length === 0 && !config.isStreaming"
            class="welcome-card"
          >
            <div class="welcome-icon">
              <el-icon :size="36"><ChatLineRound /></el-icon>
            </div>
            <div class="welcome-text">{{ config.welcomeMessage }}</div>
            <!-- 预设提示词 -->
            <div v-if="config.prompts && config.prompts.length > 0" class="prompts-list">
              <div
                v-for="(prompt, idx) in config.prompts"
                :key="idx"
                class="prompt-item"
                @click="handlePromptClick(prompt)"
              >
                <el-icon :size="14"><Pointer /></el-icon>
                <span>{{ prompt }}</span>
              </div>
            </div>
          </div>

          <!-- 消息气泡 -->
          <div
            v-for="msg in messages"
            :key="msg.id"
            class="message-item"
            :class="`message--${msg.role}`"
          >
            <div v-if="msg.role === 'assistant'" class="message-avatar">
              <el-avatar :size="32" class="ai-avatar">
                <el-icon :size="18"><Cpu /></el-icon>
              </el-avatar>
            </div>
            <div class="message-bubble" :class="`bubble--${msg.role}`">
              <div class="bubble-content">{{ msg.content }}</div>
              <div v-if="msg.timestamp" class="bubble-time">{{ msg.timestamp }}</div>
            </div>
            <div v-if="msg.role === 'user'" class="message-avatar">
              <el-avatar :size="32" class="user-avatar">
                <el-icon :size="18"><UserFilled /></el-icon>
              </el-avatar>
            </div>
          </div>

          <!-- 流式输出指示器 -->
          <div v-if="config.isStreaming" class="message-item message--assistant">
            <div class="message-avatar">
              <el-avatar :size="32" class="ai-avatar streaming-avatar">
                <el-icon :size="18"><Cpu /></el-icon>
              </el-avatar>
            </div>
            <div class="message-bubble bubble--assistant bubble--streaming">
              <div class="bubble-content">
                {{ config.streamingContent || '' }}
                <span class="streaming-cursor" />
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div
            v-if="messages.length === 0 && !config.isStreaming && !config.welcomeMessage"
            class="empty-state"
          >
            <el-icon :size="48"><ChatLineRound /></el-icon>
            <span>开始新的对话</span>
          </div>
        </div>

        <!-- 输入区 -->
        <div class="dialog-input-area">
          <div class="input-wrapper">
            <el-input
              v-model="inputText"
              type="textarea"
              :rows="2"
              placeholder="输入您的问题..."
              resize="none"
              :disabled="config.isStreaming"
              @keydown.enter.exact="handleSend"
            />
            <div class="input-actions">
              <el-button
                v-if="config.isStreaming"
                type="danger"
                :icon="Close"
                size="small"
                @click="handleStop"
              >
                中断生成
              </el-button>
              <el-button
                v-else-if="messages.length > 0"
                type="primary"
                :icon="Promotion"
                :disabled="!inputText.trim()"
                size="small"
                @click="handleSend"
              >
                发送
              </el-button>
              <el-button
                v-else
                type="primary"
                :icon="Promotion"
                :disabled="!inputText.trim()"
                size="small"
                @click="handleSend"
              >
                发送
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 结果展示区（右侧） -->
      <div class="result-panel" :style="resultPanelStyle">
        <div class="result-panel-header">
          <el-icon :size="16"><DataBoard /></el-icon>
          <span>结果展示区</span>
          <div class="result-actions">
            <slot name="extra-area" />
            <el-button
              v-if="messages.length > 0 && !config.isStreaming"
              text
              :icon="Refresh"
              size="small"
              @click="handleRegenerate"
            >
              重新生成
            </el-button>
          </div>
        </div>
        <div class="result-panel-content">
          <slot name="main-content">
            <div
              v-if="!config.resultType || config.resultType === 'none'"
              class="result-placeholder"
            >
              <el-icon :size="60"><DataBoard /></el-icon>
              <span class="result-placeholder-title">结果展示区</span>
              <span class="result-placeholder-desc">
                AI对话结果将在此区域结构化展示<br />
                支持表单录入、数据表格、图表分析等展示形式
              </span>
            </div>

            <!-- 智能录入 → 表单 -->
            <div v-else-if="config.resultType === 'form'" class="result-form-area">
              <div class="result-type-label">
                <el-tag type="primary" effect="plain" size="small">智能录入</el-tag>
              </div>
              <slot name="result-form" />
            </div>

            <!-- 智能查询 → 表格/图表 -->
            <div v-else-if="config.resultType === 'table'" class="result-table-area">
              <div class="result-type-label">
                <el-tag type="success" effect="plain" size="small">智能查询</el-tag>
              </div>
              <slot name="result-table" />
            </div>

            <!-- 智能分析 → 分析图表 -->
            <div
              v-else-if="config.resultType === 'chart' || config.resultType === 'analysis'"
              class="result-analysis-area"
            >
              <div class="result-type-label">
                <el-tag type="warning" effect="plain" size="small">
                  {{ config.resultType === 'chart' ? '智能图表' : '智能分析' }}
                </el-tag>
              </div>
              <slot name="result-analysis" />
            </div>
          </slot>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import {
  Search,
  Operation,
  Promotion,
  Close,
  Refresh,
  Cpu,
  ChatDotRound,
  ChatLineRound,
  DataBoard,
  Histogram,
  Pointer,
  UserFilled
} from '@element-plus/icons-vue'
import type {
  PageBaseProps,
  PageBaseEmits,
  AIDialogPageConfig,
  AIDialogMessageConfig
} from '@/types/page-base.d.ts'

const props = defineProps<PageBaseProps>()

const emit = defineEmits<PageBaseEmits>()

const config = computed<AIDialogPageConfig>(() => {
  return (props.config || {}) as AIDialogPageConfig
})

const inputText = ref('')
const historyVisible = ref(false)
const activeHistoryId = ref<string>('')
const messageListRef = ref<HTMLElement | null>(null)

const messages = computed<AIDialogMessageConfig[]>(() => {
  return config.value.messages ?? []
})

const dialogPanelStyle = computed<Record<string, string>>(() => {
  const percent = config.value.dialogWidthPercent ?? 40
  return { width: `${percent}%` }
})

const resultPanelStyle = computed<Record<string, string>>(() => {
  const percent = 100 - (config.value.dialogWidthPercent ?? 40)
  return { width: `${percent}%` }
})

function scrollToBottom() {
  void nextTick(() => {
    const el = messageListRef.value
    if (el) {
      el.scrollTop = el.scrollHeight
    }
  })
}

function handleSend() {
  const text = inputText.value.trim()
  if (!text) return
  emit('data-change', { source: 'ai-send', data: { content: text } })
  inputText.value = ''
  void nextTick(scrollToBottom)
}

function handleStop() {
  emit('data-change', { source: 'ai-stop', data: {} })
}

function handleRegenerate() {
  emit('data-change', { source: 'ai-regenerate', data: {} })
}

function handlePromptClick(prompt: string) {
  inputText.value = prompt
  void nextTick(handleSend)
}

function handleHistorySelect(historyId: string) {
  activeHistoryId.value = historyId
  emit('data-change', { source: 'ai-history-select', data: { historyId } })
}

function handleScroll() {
  // hook for potential auto-scroll management
}

watch(
  () => config.value.isStreaming,
  (val) => {
    if (val) {
      scrollToBottom()
    }
  }
)

watch(
  () => messages.value.length,
  () => {
    scrollToBottom()
  }
)

onMounted(() => {
  emit('page-ready', { viewId: props.viewId, pageType: props.pageType })
})
</script>

<style scoped lang="scss">
.page-p14-ai-dialog {
  padding: 12px 16px;
  height: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.area-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--el-text-color-placeholder);
  font-size: 14px;
  min-height: 44px;
}

.query-area,
.action-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 12px 20px;
  flex-shrink: 0;
}

.history-toggle-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.history-hint {
  font-size: 12px;
  color: var(--el-text-color-placeholder);
}

// 主区域
.ai-dialog-main {
  flex: 1;
  display: flex;
  gap: 12px;
  min-height: 0;
}

// 历史侧边栏
.history-panel {
  width: 240px;
  flex-shrink: 0;
  background: var(--el-bg-color);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.history-panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  flex-shrink: 0;
}

.history-panel-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.history-panel-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.history-item {
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.15s;

  &:hover {
    background: var(--el-fill-color-light);
  }

  &.is-active {
    background: var(--el-color-primary-light-9);

    .history-item-title {
      color: var(--el-color-primary);
    }
  }
}

.history-item-title {
  font-size: 13px;
  color: var(--el-text-color-primary);
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.history-item-time {
  font-size: 11px;
  color: var(--el-text-color-placeholder);
  margin-top: 4px;
}

// 历史面板过渡动画
.history-slide-enter-active,
.history-slide-leave-active {
  transition:
    width 0.25s ease,
    opacity 0.2s ease;
}

.history-slide-enter-from,
.history-slide-leave-to {
  width: 0;
  opacity: 0;
}

// 对话面板
.dialog-panel {
  display: flex;
  flex-direction: column;
  background: var(--el-bg-color);
  border-radius: 12px;
  overflow: hidden;
  min-width: 300px;
}

.dialog-panel-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 20px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  font-size: 15px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  flex-shrink: 0;
}

// 消息列表
.dialog-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px 20px 8px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

// 欢迎卡片
.welcome-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 24px 16px;
  gap: 16px;
}

.welcome-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.welcome-text {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  max-width: 320px;
  line-height: 1.6;
}

.prompts-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
  max-width: 360px;
}

.prompt-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  border-radius: 8px;
  border: 1px solid var(--el-border-color);
  cursor: pointer;
  transition: all 0.15s;
  font-size: 13px;
  color: var(--el-text-color-regular);

  &:hover {
    border-color: var(--el-color-primary);
    color: var(--el-color-primary);
    background: var(--el-color-primary-light-9);
  }
}

// 消息项
.message-item {
  display: flex;
  gap: 8px;
  align-items: flex-start;

  &.message--user {
    flex-direction: row-reverse;
  }
}

.message-avatar {
  flex-shrink: 0;
  padding-top: 4px;
}

.ai-avatar {
  background: linear-gradient(135deg, #409eff, #337ecc);
  color: #fff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.25);
}

.user-avatar {
  background: var(--el-fill-color-dark);
  color: var(--el-text-color-secondary);
}

.message-bubble {
  max-width: 75%;
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.55;
  word-break: break-word;

  &.bubble--assistant {
    background: var(--el-fill-color-light);
    color: var(--el-text-color-primary);
    border-top-left-radius: 4px;
  }

  &.bubble--user {
    background: var(--el-color-primary);
    color: #fff;
    border-top-right-radius: 4px;
  }

  &.bubble--streaming {
    background: linear-gradient(
      135deg,
      var(--el-fill-color-light),
      var(--el-color-primary-light-9)
    );
    border-left: 3px solid var(--el-color-primary);
  }
}

.bubble-content {
  white-space: pre-wrap;
}

.bubble-time {
  font-size: 11px;
  margin-top: 4px;
  opacity: 0.6;
}

// 流式输出光标
.streaming-cursor {
  display: inline-block;
  width: 2px;
  height: 16px;
  background: var(--el-color-primary);
  vertical-align: text-bottom;
  animation: blink 0.8s infinite;
}

@keyframes blink {
  0%,
  50% {
    opacity: 1;
  }
  51%,
  100% {
    opacity: 0;
  }
}

// 空状态
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 48px 16px;
  color: var(--el-text-color-placeholder);
  font-size: 14px;
}

// 输入区
.dialog-input-area {
  flex-shrink: 0;
  padding: 12px 16px;
  border-top: 1px solid var(--el-border-color-lighter);
  background: var(--el-fill-color-lighter);
}

.input-wrapper {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

// 结果展示区
.result-panel {
  display: flex;
  flex-direction: column;
  background: var(--el-bg-color);
  border-radius: 12px;
  overflow: hidden;
  min-width: 300px;
}

.result-panel-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 20px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  flex-shrink: 0;
}

.result-actions {
  margin-left: auto;
  display: flex;
  gap: 4px;
}

.result-panel-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.result-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  height: 100%;
  color: var(--el-text-color-placeholder);
  text-align: center;
}

.result-placeholder-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--el-text-color-secondary);
}

.result-placeholder-desc {
  font-size: 13px;
  line-height: 1.7;
  color: var(--el-text-color-placeholder);
}

.result-type-label {
  margin-bottom: 12px;
}

.result-form-area,
.result-table-area,
.result-analysis-area {
  min-height: 200px;
}

// 额外区域
.extra-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
}
</style>
