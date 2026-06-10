<template>
  <div class="designer-demo">
    <div class="demo-header">
      <h1>审批流程设计器（P15-审批）</h1>
      <p class="demo-desc">
        可视化编排审批流程：拖拽审批节点至画布、配置流转条件分支、设置审批人规则、预览流程执行路径。
      </p>
    </div>

    <div class="designer-layout">
      <div class="toolbar">
        <div class="toolbar__left">
          <el-button type="primary" :icon="Plus" size="small">新建流程</el-button>
          <el-button :icon="Check" size="small" type="success">保存</el-button>
          <el-button :icon="View" size="small" @click="previewVisible = true">预览</el-button>
          <el-divider direction="vertical" />
          <el-button :icon="RefreshLeft" size="small">撤销</el-button>
          <el-button :icon="RefreshRight" size="small">重做</el-button>
          <el-divider direction="vertical" />
          <el-button size="small" @click="autoLayout">自动布局</el-button>
          <el-button size="small" @click="validateFlow">校验流程</el-button>
        </div>
        <div class="toolbar__right">
          <el-tag v-if="validationOk" type="success" size="small">流程有效</el-tag>
          <el-tag v-else type="warning" size="small">请添加节点</el-tag>
        </div>
      </div>

      <div class="main-panels">
        <!-- 左侧：节点面板 -->
        <div class="panel panel--left">
          <div class="panel__title">流程节点</div>
          <div class="node-list">
            <div
              v-for="node in nodeTypes"
              :key="node.type"
              class="node-item"
              draggable="true"
              @dragstart="onNodeDrag($event, node)"
            >
              <div class="node-icon" :style="{ background: node.color }">
                <el-icon :size="18"><component :is="node.icon" /></el-icon>
              </div>
              <div class="node-info">
                <div class="node-name">{{ node.label }}</div>
                <div class="node-desc">{{ node.desc }}</div>
              </div>
            </div>
          </div>

          <el-divider style="margin: 8px 0" />

          <div class="panel__title">流程模板</div>
          <div class="template-list">
            <div
              v-for="tpl in templates"
              :key="tpl.name"
              class="tpl-item"
              @click="applyTemplate(tpl)"
            >
              <span>{{ tpl.label }}</span>
              <el-tag size="small" effect="plain">{{ tpl.nodes }}个节点</el-tag>
            </div>
          </div>
        </div>

        <!-- 中间：流程画布 -->
        <div class="panel panel--center">
          <div class="panel__title">流程画布</div>
          <div class="canvas-wrapper" @drop.prevent="onCanvasDrop" @dragover.prevent>
            <svg ref="svgRef" class="flow-canvas" :width="canvasW" :height="canvasH">
              <!-- 连线 -->
              <line
                v-for="(conn, ci) in connections"
                :key="'c' + ci"
                :x1="conn.x1"
                :y1="conn.y1"
                :x2="conn.x2"
                :y2="conn.y2"
                :stroke="conn.color || '#C0C4CC'"
                stroke-width="2"
                marker-end="url(#arrowhead)"
              />
              <defs>
                <marker
                  id="arrowhead"
                  markerWidth="10"
                  markerHeight="7"
                  refX="10"
                  refY="3.5"
                  orient="auto"
                >
                  <polygon points="0 0, 10 3.5, 0 7" fill="#C0C4CC" />
                </marker>
              </defs>
            </svg>
            <!-- 节点 -->
            <div
              v-for="(node, ni) in flowNodes"
              :key="node.id"
              class="flow-node"
              :class="{
                'is-active': activeFlowNodeId === node.id,
                'is-start': node.type === 'start',
                'is-end': node.type === 'end'
              }"
              :style="{ left: node.x + 'px', top: node.y + 'px', borderColor: node.color }"
              @click="selectFlowNode(node.id)"
              @mousedown.prevent="startDragNode($event, ni)"
            >
              <div class="flow-node__header" :style="{ background: node.color }">
                <el-icon :size="14"><component :is="node.icon" /></el-icon>
                <span>{{ node.label }}</span>
              </div>
              <div class="flow-node__body">{{ node.config?.approver || '未配置审批人' }}</div>
            </div>
            <!-- 条件标签 -->
            <div
              v-for="cond in conditionLabels"
              :key="cond.id"
              class="condition-label"
              :style="{ left: cond.x + 'px', top: cond.y + 'px' }"
            >
              <el-tag :type="cond.type" size="small" effect="dark">{{ cond.text }}</el-tag>
            </div>
          </div>
        </div>

        <!-- 右侧：属性面板 -->
        <div class="panel panel--right">
          <div class="panel__title">节点属性</div>
          <div v-if="!activeFlowNode" class="panel__empty">请选择画布中的节点</div>
          <el-form v-else label-width="90px" size="small">
            <el-form-item label="节点名称"
              ><el-input v-model="activeFlowNode.label"
            /></el-form-item>
            <el-form-item label="节点类型"
              ><el-tag size="small">{{ activeFlowNode.typeName }}</el-tag></el-form-item
            >

            <template v-if="activeFlowNode.type === 'approval'">
              <el-divider>审批配置</el-divider>
              <el-form-item label="审批人类型">
                <el-select v-model="activeFlowNode.config.approverType" style="width: 100%">
                  <el-option
                    v-for="at in approverTypes"
                    :key="at.value"
                    :label="at.label"
                    :value="at.value"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="审批人">
                <el-input
                  v-if="activeFlowNode.config.approverType === 'specific'"
                  v-model="activeFlowNode.config.approver"
                  placeholder="指定人员ID"
                />
                <el-select
                  v-else
                  v-model="activeFlowNode.config.approver"
                  style="width: 100%"
                  placeholder="选择"
                >
                  <el-option v-for="r in roleOptions" :key="r" :label="r" :value="r" />
                </el-select>
              </el-form-item>
              <el-form-item label="审批模式">
                <el-select v-model="activeFlowNode.config.mode" style="width: 100%">
                  <el-option label="会签（全部同意）" value="countersign" />
                  <el-option label="或签（任一同意）" value="or_sign" />
                  <el-option label="依次审批" value="sequential" />
                </el-select>
              </el-form-item>
              <el-form-item label="超时处理">
                <el-select v-model="activeFlowNode.config.timeoutAction" style="width: 100%">
                  <el-option label="无" value="none" /><el-option
                    label="自动通过"
                    value="auto_pass"
                  />
                  <el-option label="转交上级" value="escalate" /><el-option
                    label="通知催办"
                    value="notify"
                  />
                </el-select>
              </el-form-item>
              <el-form-item
                v-if="activeFlowNode.config.timeoutAction !== 'none'"
                label="超时时长(h)"
              >
                <el-input-number v-model="activeFlowNode.config.timeoutHours" :min="1" :max="720" />
              </el-form-item>
              <el-form-item label="允许加签"
                ><el-switch v-model="activeFlowNode.config.allowAddSign" size="small"
              /></el-form-item>
              <el-form-item label="允许转审"
                ><el-switch v-model="activeFlowNode.config.allowTransfer" size="small"
              /></el-form-item>
            </template>

            <template v-if="activeFlowNode.type === 'condition'">
              <el-divider>条件配置</el-divider>
              <el-form-item label="条件字段"
                ><el-input v-model="activeFlowNode.config.field" placeholder="如: amount"
              /></el-form-item>
              <el-form-item label="运算符"
                ><el-select v-model="activeFlowNode.config.operator" style="width: 100%"
                  ><el-option
                    v-for="o in ['>', '<', '>=', '<=', '==', '!=']"
                    :key="o"
                    :label="o"
                    :value="o" /></el-select
              ></el-form-item>
              <el-form-item label="比较值"
                ><el-input v-model="activeFlowNode.config.value" placeholder="如: 10000"
              /></el-form-item>
            </template>

            <template v-if="activeFlowNode.type === 'cc'">
              <el-divider>抄送配置</el-divider>
              <el-form-item label="抄送人"
                ><el-input v-model="activeFlowNode.config.ccTo" placeholder="指定人员ID"
              /></el-form-item>
              <el-form-item label="抄送时机"
                ><el-select v-model="activeFlowNode.config.ccTiming" style="width: 100%"
                  ><el-option label="审批通过后" value="after_approve" /><el-option
                    label="提交时"
                    value="on_submit" /></el-select
              ></el-form-item>
            </template>
          </el-form>

          <!-- 流程全局属性 -->
          <el-divider />
          <div class="panel__title" style="border: none; padding: 4px 0">全局属性</div>
          <el-form label-width="90px" size="small">
            <el-form-item label="流程名称"><el-input v-model="flowName" /></el-form-item>
            <el-form-item label="适用模块"
              ><el-select v-model="flowModule" style="width: 100%"
                ><el-option v-for="m in modules" :key="m" :label="m" :value="m" /></el-select
            ></el-form-item>
            <el-form-item label="允许撤回"
              ><el-switch v-model="allowRecall" size="small"
            /></el-form-item>
            <el-form-item label="允许催办"
              ><el-switch v-model="allowUrge" size="small"
            /></el-form-item>
            <el-form-item label="审批人去重"
              ><el-switch v-model="dedupApprover" size="small"
            /></el-form-item>
          </el-form>
        </div>
      </div>
    </div>

    <el-dialog v-model="previewVisible" title="审批流程预览" width="700px">
      <div class="preview-flow">
        <div class="pf-title">{{ flowName || '未命名流程' }}</div>
        <el-steps direction="vertical" :active="2">
          <el-step
            v-for="(s, i) in previewSteps"
            :key="i"
            :title="s.title"
            :description="s.desc"
            :status="s.status as any"
          />
        </el-steps>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Plus, Check, View, RefreshLeft, RefreshRight } from '@element-plus/icons-vue'

defineOptions({ name: 'ApprovalFlowDesignerDemo' })

const previewVisible = ref(false)
const validationOk = ref(false)
const flowName = ref('销售订单审批流程')
const flowModule = ref('销售管理')
const allowRecall = ref(true)
const allowUrge = ref(true)
const dedupApprover = ref(true)

const modules = ['销售管理', '采购管理', '库存管理', '费用管理', 'HRM', 'OA办公']
const approverTypes = [
  { value: 'specific', label: '指定人员' },
  { value: 'role', label: '按角色' },
  { value: 'department', label: '部门负责人' },
  { value: 'superior', label: '直属上级' },
  { value: 'self', label: '发起人自选' }
]
const roleOptions = ['部门经理', '财务主管', '总经理', '分管副总']

const nodeTypes = [
  {
    type: 'approval',
    label: '审批节点',
    desc: '单人/多人审批',
    icon: 'UserFilled',
    color: '#409EFF'
  },
  { type: 'condition', label: '条件分支', desc: '按条件流转', icon: 'Operation', color: '#E6A23C' },
  { type: 'cc', label: '抄送节点', desc: '知会通知', icon: 'Share', color: '#67C23A' },
  { type: 'timer', label: '定时节点', desc: '延迟/定时', icon: 'Timer', color: '#909399' }
]

const templates = [
  {
    label: '通用二级审批',
    nodes: 3,
    flow: [
      {
        type: 'start',
        label: '开始',
        icon: 'Finished',
        color: '#67C23A',
        x: 250,
        y: 30,
        config: {}
      },
      {
        type: 'approval',
        label: '部门经理审批',
        icon: 'UserFilled',
        color: '#409EFF',
        x: 250,
        y: 140,
        config: {
          approverType: 'role',
          approver: '部门经理',
          mode: 'or_sign',
          timeoutAction: 'none'
        }
      },
      {
        type: 'approval',
        label: '总经理审批',
        icon: 'UserFilled',
        color: '#409EFF',
        x: 250,
        y: 260,
        config: {
          approverType: 'role',
          approver: '总经理',
          mode: 'countersign',
          timeoutAction: 'escalate',
          timeoutHours: 48
        }
      },
      { type: 'end', label: '结束', icon: 'Finished', color: '#F56C6C', x: 250, y: 380, config: {} }
    ]
  },
  {
    label: '条件分支审批',
    nodes: 4,
    flow: [
      {
        type: 'start',
        label: '开始',
        icon: 'Finished',
        color: '#67C23A',
        x: 250,
        y: 30,
        config: {}
      },
      {
        type: 'approval',
        label: '直属上级审批',
        icon: 'UserFilled',
        color: '#409EFF',
        x: 250,
        y: 130,
        config: {
          approverType: 'superior',
          approver: '直属上级',
          mode: 'or_sign',
          timeoutAction: 'notify',
          timeoutHours: 24
        }
      },
      {
        type: 'condition',
        label: '金额>10000?',
        icon: 'Operation',
        color: '#E6A23C',
        x: 250,
        y: 240,
        config: { field: 'amount', operator: '>', value: '10000' }
      },
      { type: 'end', label: '结束', icon: 'Finished', color: '#F56C6C', x: 250, y: 360, config: {} }
    ]
  },
  {
    label: '含抄送三级审批',
    nodes: 4,
    flow: [
      {
        type: 'start',
        label: '开始',
        icon: 'Finished',
        color: '#67C23A',
        x: 250,
        y: 20,
        config: {}
      },
      {
        type: 'approval',
        label: '组长审批',
        icon: 'UserFilled',
        color: '#409EFF',
        x: 250,
        y: 110,
        config: {
          approverType: 'specific',
          approver: '组长ID',
          mode: 'or_sign',
          timeoutAction: 'none'
        }
      },
      {
        type: 'approval',
        label: '经理审批',
        icon: 'UserFilled',
        color: '#409EFF',
        x: 250,
        y: 220,
        config: {
          approverType: 'role',
          approver: '部门经理',
          mode: 'countersign',
          timeoutAction: 'escalate',
          timeoutHours: 24
        }
      },
      {
        type: 'cc',
        label: '抄送财务',
        icon: 'Share',
        color: '#67C23A',
        x: 250,
        y: 340,
        config: { ccTo: '财务主管ID', ccTiming: 'after_approve' }
      },
      { type: 'end', label: '结束', icon: 'Finished', color: '#F56C6C', x: 250, y: 430, config: {} }
    ]
  }
]

const flowNodes = ref([
  {
    id: 1,
    type: 'start',
    label: '开始',
    typeName: '开始',
    icon: 'Finished',
    color: '#67C23A',
    x: 250,
    y: 20,
    config: {}
  },
  {
    id: 2,
    type: 'approval',
    label: '部门经理审批',
    typeName: '审批节点',
    icon: 'UserFilled',
    color: '#409EFF',
    x: 250,
    y: 130,
    config: {
      approverType: 'role',
      approver: '部门经理',
      mode: 'or_sign',
      timeoutAction: 'notify',
      timeoutHours: 24,
      allowAddSign: true,
      allowTransfer: true
    }
  },
  {
    id: 3,
    type: 'condition',
    label: '金额>10000',
    typeName: '条件分支',
    icon: 'Operation',
    color: '#E6A23C',
    x: 250,
    y: 250,
    config: { field: 'amount', operator: '>', value: '10000' }
  },
  {
    id: 4,
    type: 'approval',
    label: '总经理审批',
    typeName: '审批节点',
    icon: 'UserFilled',
    color: '#409EFF',
    x: 250,
    y: 360,
    config: {
      approverType: 'role',
      approver: '总经理',
      mode: 'countersign',
      timeoutAction: 'escalate',
      timeoutHours: 48,
      allowAddSign: false,
      allowTransfer: true
    }
  },
  {
    id: 5,
    type: 'end',
    label: '结束',
    typeName: '结束',
    icon: 'Finished',
    color: '#F56C6C',
    x: 250,
    y: 480,
    config: {}
  }
])

const conditionLabels = [
  { id: 'cond1', x: 160, y: 300, text: '是 → 总经理审批', type: 'success' },
  { id: 'cond2', x: 340, y: 300, text: '否 → 结束', type: 'info' }
]

const connections = computed(() => {
  const result = []
  for (let i = 0; i < flowNodes.value.length - 1; i++) {
    const a = flowNodes.value[i]
    const b = flowNodes.value[i + 1]
    result.push({
      x1: a.x + 50,
      y1: a.y + 50,
      x2: b.x + 50,
      y2: b.y + 10,
      color: a.type === 'condition' ? undefined : '#C0C4CC'
    })
  }
  return result
})

const canvasW = 600
const canvasH = 600
const activeFlowNodeId = ref<number | null>(null)
const activeFlowNode = computed(
  () => flowNodes.value.find((n) => n.id === activeFlowNodeId.value) || null
)

function selectFlowNode(id: number) {
  activeFlowNodeId.value = id
}
function onNodeDrag(e: DragEvent, node: any) {
  e.dataTransfer!.setData('node', JSON.stringify(node))
}
function onCanvasDrop(e: DragEvent) {
  const node = JSON.parse(e.dataTransfer!.getData('node'))
  const rect = (e.currentTarget as HTMLElement).getBoundingClientRect()
  const y = e.clientY - rect.top + (e.currentTarget as HTMLElement).scrollTop
  const id = Date.now()
  flowNodes.value.splice(flowNodes.value.length - 1, 0, {
    id,
    type: node.type,
    label: node.label,
    typeName: node.label,
    icon: node.icon,
    color: node.color,
    x: 250,
    y: Math.max(20, y - 25),
    config:
      node.type === 'approval'
        ? {
            approverType: 'role',
            approver: '',
            mode: 'or_sign',
            timeoutAction: 'none',
            allowAddSign: true,
            allowTransfer: true
          }
        : node.type === 'condition'
          ? { field: '', operator: '>', value: '' }
          : node.type === 'cc'
            ? { ccTo: '', ccTiming: 'after_approve' }
            : {}
  })
  validationOk.value = true
}

function startDragNode(_e: MouseEvent, _idx: number) {
  /* drag placeholder */
}
function autoLayout() {
  flowNodes.value.forEach((n, i) => {
    n.y = 20 + i * 110
  })
}
function validateFlow() {
  validationOk.value = flowNodes.value.length >= 3
}
function applyTemplate(tpl: any) {
  flowNodes.value = tpl.flow.map((n: any) => ({ ...n, id: Date.now() + Math.random() }))
  validationOk.value = true
}

const previewSteps = [
  { title: '提交申请', desc: '申请人提交销售订单', status: 'success' },
  { title: '部门经理审批', desc: '部门经理: 张三 — 待审批', status: 'process' },
  { title: '总经理审批', desc: '总经理: 李四 — 待审批（条件触发）', status: 'wait' },
  { title: '审批完成', desc: '流程结束，订单生效', status: 'wait' }
]
</script>

<style lang="scss" scoped>
.designer-demo {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #e6e6fa;

  .demo-header {
    padding: 16px 24px 0;
    h1 {
      font-size: 20px;
      font-weight: 700;
      margin: 0 0 4px;
    }
    .demo-desc {
      font-size: 13px;
      color: var(--el-text-color-secondary);
    }
  }

  .designer-layout {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    padding: 16px;
    gap: 8px;
  }

  .toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: #fff;
    border-radius: 12px;
    padding: 8px 16px;
    flex-shrink: 0;
    &__left,
    &__right {
      display: flex;
      align-items: center;
      gap: 6px;
    }
  }

  .main-panels {
    flex: 1;
    display: flex;
    gap: 8px;
    overflow: hidden;
  }

  .panel {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    &__title {
      padding: 10px 12px;
      font-weight: 600;
      font-size: 13px;
      border-bottom: 1px solid var(--el-border-color-lighter);
    }
    &__empty {
      padding: 24px;
      text-align: center;
      color: var(--el-text-color-secondary);
      font-size: 13px;
    }
    &--left {
      width: 220px;
      flex-shrink: 0;
      overflow-y: auto;
      padding: 8px;
    }
    &--center {
      flex: 1;
      overflow: auto;
      padding: 12px;
    }
    &--right {
      width: 260px;
      flex-shrink: 0;
      overflow-y: auto;
      padding: 8px 12px;
    }
  }

  .node-list {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }
  .node-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px;
    border-radius: 8px;
    cursor: grab;
    transition: background 0.15s;
    &:hover {
      background: var(--el-fill-color-light);
    }
    .node-icon {
      width: 36px;
      height: 36px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      flex-shrink: 0;
    }
    .node-info {
      flex: 1;
      .node-name {
        font-size: 13px;
        font-weight: 500;
      }
      .node-desc {
        font-size: 11px;
        color: var(--el-text-color-secondary);
      }
    }
  }

  .template-list {
    .tpl-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 6px 8px;
      border-radius: 6px;
      cursor: pointer;
      font-size: 12px;
      &:hover {
        background: var(--el-fill-color-light);
      }
    }
  }

  .canvas-wrapper {
    position: relative;
    height: calc(100% - 40px);
    overflow: auto;
  }
  .flow-canvas {
    position: absolute;
    top: 0;
    left: 0;
  }

  .flow-node {
    position: absolute;
    width: 120px;
    background: #fff;
    border: 2px solid #409eff;
    border-radius: 8px;
    cursor: pointer;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    transition: box-shadow 0.2s;
    &.is-active {
      box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.3);
    }
    &.is-start,
    &.is-end {
      border-radius: 30px;
      width: 80px;
      margin-left: 20px;
    }
    &__header {
      display: flex;
      align-items: center;
      gap: 4px;
      padding: 6px 8px;
      color: #fff;
      border-radius: 6px 6px 0 0;
      font-size: 12px;
      font-weight: 600;
    }
    &__body {
      padding: 6px 8px;
      font-size: 11px;
      color: var(--el-text-color-secondary);
    }
  }

  .condition-label {
    position: absolute;
    z-index: 1;
  }

  .preview-flow {
    .pf-title {
      text-align: center;
      font-size: 18px;
      font-weight: 700;
      margin-bottom: 20px;
    }
  }
}
</style>
