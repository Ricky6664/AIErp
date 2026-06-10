<template>
  <PageP14AIDialog view-id="demo-p14" page-type="P14" :config="pageConfig" :permissions="[]">
    <template #main-content>
      <div class="ai-dialog-container">
        <!-- 对话区（左侧） -->
        <div class="ai-chat-area">
          <div class="ai-chat-header">
            <h3 class="ai-chat-title">AI 智能助手</h3>
            <el-button text size="small">新对话</el-button>
          </div>
          <div class="ai-chat-messages">
            <div class="ai-message ai-message--assistant">
              <el-avatar :size="32" style="background: var(--el-color-primary)">AI</el-avatar>
              <div class="ai-message-bubble">
                <p>您好！我是ERP智能助手，我可以帮您：</p>
                <ul>
                  <li>快速录入业务单据</li>
                  <li>查询库存、订单等业务数据</li>
                  <li>生成销售分析报告</li>
                  <li>辅助审核单据</li>
                </ul>
                <p>请告诉我您需要什么帮助？</p>
              </div>
            </div>
            <div class="ai-message ai-message--user">
              <div class="ai-message-bubble">查看本月销售情况</div>
              <el-avatar :size="32">我</el-avatar>
            </div>
            <div class="ai-message ai-message--assistant">
              <el-avatar :size="32" style="background: var(--el-color-primary)">AI</el-avatar>
              <div class="ai-message-bubble">
                <p>好的，正在为您查询2026年6月销售数据：</p>
                <ul>
                  <li>本月订单数：<strong>1,286</strong> 笔</li>
                  <li>本月销售额：<strong>¥856万</strong></li>
                  <li>同比增长：<strong>12.5%</strong></li>
                </ul>
                <p>右侧已展示详细的销售分析，请查看。</p>
              </div>
            </div>
            <!-- 流式输出效果 -->
            <div class="ai-message ai-message--assistant">
              <el-avatar :size="32" style="background: var(--el-color-primary)">AI</el-avatar>
              <div class="ai-message-bubble streaming">
                <span>正在生成详细分析报告...</span>
                <span class="typing-cursor">|</span>
              </div>
            </div>
          </div>
          <div class="ai-chat-input">
            <el-input
              v-model="chatInput"
              placeholder="输入您的问题或指令..."
              type="textarea"
              :rows="2"
            />
            <div class="ai-chat-actions">
              <div class="ai-prompt-chips">
                <el-tag v-for="p in prompts" :key="p" class="prompt-chip" @click="chatInput = p">{{
                  p
                }}</el-tag>
              </div>
              <el-button type="primary" :disabled="!chatInput">发送</el-button>
            </div>
          </div>
        </div>

        <!-- 结果展示区（右侧） -->
        <div class="ai-result-area">
          <div class="ai-result-header">
            <h3 class="ai-result-title">分析结果</h3>
            <el-tag size="small" type="success">实时</el-tag>
          </div>
          <div class="ai-result-body">
            <!-- KPI 卡片 -->
            <div class="ai-result-kpis">
              <div class="ai-kpi-card">
                <span class="ai-kpi-label">本月销售额</span>
                <span class="ai-kpi-value" style="color: var(--el-color-primary)">¥856万</span>
              </div>
              <div class="ai-kpi-card">
                <span class="ai-kpi-label">订单数</span>
                <span class="ai-kpi-value" style="color: var(--el-color-success)">1,286</span>
              </div>
              <div class="ai-kpi-card">
                <span class="ai-kpi-label">同比增长</span>
                <span class="ai-kpi-value" style="color: var(--el-color-warning)">12.5%</span>
              </div>
            </div>
            <!-- 图表 -->
            <div class="ai-result-chart">
              <div class="chart-placeholder">
                <el-icon :size="48"><DataAnalysis /></el-icon>
                <span>销售趋势图 — ECharts</span>
              </div>
            </div>
            <!-- TOP 排行 -->
            <div class="ai-result-table">
              <h4>TOP 5 客户</h4>
              <el-table :data="topCustomers" size="small">
                <el-table-column type="index" width="50" />
                <el-table-column prop="name" label="客户" />
                <el-table-column prop="amount" label="销售额" width="120" />
                <el-table-column prop="percent" label="占比" width="80" />
              </el-table>
            </div>
          </div>
        </div>
      </div>
    </template>
  </PageP14AIDialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { DataAnalysis } from '@element-plus/icons-vue'
import PageP14AIDialog from '@/components/page-base/PageP14AIDialog.vue'

const chatInput = ref('')
const prompts = ['查询本月库存', '分析销售趋势', '审核待办单据', '生成财务报表']

const pageConfig = {
  title: 'AI智能助手（P14）',
  dialogTitle: 'AI 智能助手',
  dialogWidthPercent: 40,
  welcomeMessage: '您好！我是ERP智能助手，请告诉我您需要什么帮助？',
  prompts,
  resultType: 'analysis' as const
}

const topCustomers = [
  { name: 'XX科技有限公司', amount: '¥182万', percent: '21.3%' },
  { name: 'YY制造集团', amount: '¥156万', percent: '18.2%' },
  { name: 'ZZ商贸公司', amount: '¥98万', percent: '11.4%' },
  { name: 'AA电子商务', amount: '¥85万', percent: '9.9%' },
  { name: 'BB物流公司', amount: '¥62万', percent: '7.2%' }
]
</script>

<style scoped lang="scss">
.ai-dialog-container {
  display: flex;
  height: 100%;
  gap: 16px;
  min-height: 0;
}
.ai-chat-area {
  flex: 4;
  display: flex;
  flex-direction: column;
  background: var(--el-bg-color);
  border-radius: 12px;
  overflow: hidden;
  min-width: 0;
  .ai-chat-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 20px;
    border-bottom: 1px solid var(--el-border-color-lighter);
    .ai-chat-title {
      margin: 0;
      font-size: 16px;
    }
  }
  .ai-chat-messages {
    flex: 1;
    overflow-y: auto;
    padding: 16px;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  .ai-chat-input {
    padding: 12px 16px;
    border-top: 1px solid var(--el-border-color-lighter);
  }
  .ai-chat-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 8px;
  }
  .ai-prompt-chips {
    display: flex;
    gap: 6px;
    flex-wrap: wrap;
    .prompt-chip {
      cursor: pointer;
    }
  }
}
.ai-message {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  &--user {
    justify-content: flex-end;
    .ai-message-bubble {
      background: var(--el-color-primary);
      color: #fff;
    }
  }
  &--assistant {
    .ai-message-bubble {
      background: var(--el-fill-color-light);
    }
  }
}
.ai-message-bubble {
  max-width: 75%;
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  p {
    margin: 0 0 4px;
  }
  ul {
    margin: 4px 0;
    padding-left: 18px;
  }
  &.streaming {
    font-style: italic;
    color: var(--el-text-color-secondary);
  }
}
.typing-cursor {
  animation: blink 0.8s infinite;
}
@keyframes blink {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0;
  }
}

.ai-result-area {
  flex: 6;
  display: flex;
  flex-direction: column;
  background: var(--el-bg-color);
  border-radius: 12px;
  overflow: hidden;
  min-width: 0;
  .ai-result-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 20px;
    border-bottom: 1px solid var(--el-border-color-lighter);
    .ai-result-title {
      margin: 0;
      font-size: 16px;
    }
  }
  .ai-result-body {
    flex: 1;
    overflow-y: auto;
    padding: 16px;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
}
.ai-result-kpis {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
.ai-kpi-card {
  background: var(--el-fill-color-lighter);
  border-radius: 8px;
  padding: 16px;
  text-align: center;
  .ai-kpi-label {
    display: block;
    font-size: 13px;
    color: var(--el-text-color-secondary);
    margin-bottom: 6px;
  }
  .ai-kpi-value {
    font-size: 24px;
    font-weight: 700;
  }
}
.ai-result-chart {
  background: var(--el-fill-color-lighter);
  border-radius: 8px;
  padding: 16px;
  min-height: 200px;
}
.chart-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 160px;
  gap: 8px;
  color: var(--el-text-color-placeholder);
  font-size: 14px;
}
.ai-result-table {
  h4 {
    margin: 0 0 8px;
    font-size: 15px;
  }
}
</style>
