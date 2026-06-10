<template>
  <div class="component-demo basic-inputs-demo">
    <div class="demo-header">
      <h1>基础通用录入组件（§8.1）</h1>
      <p class="demo-desc">
        20 个标准录入组件，覆盖文本/数值/日期/选择/上传/评分等全部基础交互类型。全表单 +
        表格单元格通用。
      </p>
    </div>

    <div class="demo-grid">
      <!-- 1. 单行文本 -->
      <el-card shadow="never">
        <template #header><span class="card-label">1. 单行文本输入框</span></template>
        <el-input
          v-model="form.text"
          placeholder="请输入文本"
          maxlength="50"
          show-word-limit
          clearable
        />
        <div class="card-value">值: {{ form.text || '(空)' }}</div>
      </el-card>

      <!-- 2. 多行文本 -->
      <el-card shadow="never">
        <template #header><span class="card-label">2. 多行文本输入框</span></template>
        <el-input
          v-model="form.textarea"
          type="textarea"
          :rows="3"
          placeholder="请输入多行文本"
          maxlength="200"
          show-word-limit
        />
        <div class="card-value">值: {{ form.textarea || '(空)' }}</div>
      </el-card>

      <!-- 3. 整数录入 -->
      <el-card shadow="never">
        <template #header><span class="card-label">3. 整数录入框</span></template>
        <el-input-number
          v-model="form.integer"
          :min="0"
          :max="99999"
          :step="1"
          placeholder="整数"
          style="width: 100%"
        />
        <div class="card-value">值: {{ form.integer ?? '(空)' }}</div>
      </el-card>

      <!-- 4. 小数/金额 -->
      <el-card shadow="never">
        <template #header><span class="card-label">4. 小数/金额录入框</span></template>
        <div class="amount-row">
          <span class="amount-prefix">¥</span>
          <el-input-number
            v-model="form.decimal"
            :min="0"
            :precision="2"
            :step="0.01"
            placeholder="金额"
            style="flex: 1"
          />
        </div>
        <div class="card-value">
          值: {{ form.decimal ?? '(空)' }} | 千分位: {{ form.decimal?.toLocaleString() ?? '-' }}
        </div>
      </el-card>

      <!-- 5. 百分比 -->
      <el-card shadow="never">
        <template #header><span class="card-label">5. 百分比录入框</span></template>
        <el-input-number
          v-model="form.percent"
          :min="0"
          :max="100"
          :precision="2"
          :step="1"
          style="width: 100%"
        >
          <template #suffix>%</template>
        </el-input-number>
        <div class="card-value">值: {{ form.percent ?? '(空)' }}%</div>
      </el-card>

      <!-- 6. 日期选择器 -->
      <el-card shadow="never">
        <template #header><span class="card-label">6. 日期选择器</span></template>
        <el-date-picker
          v-model="form.date"
          type="date"
          placeholder="选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 100%"
        />
        <div class="card-value">值: {{ form.date || '(空)' }}</div>
      </el-card>

      <!-- 7. 时间选择器 -->
      <el-card shadow="never">
        <template #header><span class="card-label">7. 时间选择器</span></template>
        <el-time-picker
          v-model="form.time"
          placeholder="选择时间"
          format="HH:mm:ss"
          value-format="HH:mm:ss"
          style="width: 100%"
        />
        <div class="card-value">值: {{ form.time || '(空)' }}</div>
      </el-card>

      <!-- 8. 日期时间选择器 -->
      <el-card shadow="never">
        <template #header><span class="card-label">8. 日期时间选择器</span></template>
        <el-date-picker
          v-model="form.datetime"
          type="datetime"
          placeholder="选择日期时间"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
          style="width: 100%"
        />
        <div class="card-value">值: {{ form.datetime || '(空)' }}</div>
      </el-card>

      <!-- 9. 年月选择器 -->
      <el-card shadow="never">
        <template #header><span class="card-label">9. 年月选择器</span></template>
        <el-date-picker
          v-model="form.month"
          type="month"
          placeholder="选择年月"
          format="YYYY-MM"
          value-format="YYYY-MM"
          style="width: 100%"
        />
        <div class="card-value">值: {{ form.month || '(空)' }}</div>
      </el-card>

      <!-- 10. 开关组件 -->
      <el-card shadow="never">
        <template #header><span class="card-label">10. 开关组件</span></template>
        <div class="switch-row">
          <el-switch v-model="form.switch1" active-text="启用" inactive-text="禁用" />
          <el-switch
            v-model="form.switch2"
            active-text="是"
            inactive-text="否"
            active-value="Y"
            inactive-value="N"
          />
        </div>
        <div class="card-value">启用/禁用: {{ form.switch1 }} | 是/否: {{ form.switch2 }}</div>
      </el-card>

      <!-- 11. 单选组件 -->
      <el-card shadow="never">
        <template #header><span class="card-label">11. 单选组件</span></template>
        <el-radio-group v-model="form.radio">
          <el-radio value="A">选项A</el-radio>
          <el-radio value="B">选项B</el-radio>
          <el-radio value="C">选项C</el-radio>
        </el-radio-group>
        <div class="card-value">值: {{ form.radio || '(空)' }}</div>
      </el-card>

      <!-- 12. 多选组件 -->
      <el-card shadow="never">
        <template #header><span class="card-label">12. 多选组件</span></template>
        <el-checkbox-group v-model="form.checkbox">
          <el-checkbox value="A">选项A</el-checkbox>
          <el-checkbox value="B">选项B</el-checkbox>
          <el-checkbox value="C">选项C</el-checkbox>
        </el-checkbox-group>
        <div class="card-value">值: {{ form.checkbox.join(',') || '(空)' }}</div>
      </el-card>

      <!-- 13. 树形选择组件 -->
      <el-card shadow="never">
        <template #header><span class="card-label">13. 树形选择组件</span></template>
        <el-tree-select
          v-model="form.treeSelect"
          :data="mockTreeData"
          placeholder="请选择分类"
          check-strictly
          filterable
          style="width: 100%"
        />
        <div class="card-value">值: {{ form.treeSelect || '(空)' }}</div>
      </el-card>

      <!-- 14. 附件上传 -->
      <el-card shadow="never">
        <template #header><span class="card-label">14. 附件上传组件</span></template>
        <el-upload v-model:file-list="form.files" action="#" :auto-upload="false" drag>
          <el-icon :size="28"><UploadFilled /></el-icon>
          <div class="upload-text">拖拽文件到此处或<em>点击上传</em></div>
        </el-upload>
        <div class="card-value">已选: {{ form.files.length }} 个文件</div>
      </el-card>

      <!-- 15. 图片上传 -->
      <el-card shadow="never">
        <template #header><span class="card-label">15. 图片上传组件</span></template>
        <el-upload
          v-model:file-list="form.images"
          action="#"
          :auto-upload="false"
          list-type="picture-card"
        >
          <el-icon :size="20"><Plus /></el-icon>
        </el-upload>
        <div class="card-value">已选: {{ form.images.length }} 张图片</div>
      </el-card>

      <!-- 16. 富文本编辑 -->
      <el-card shadow="never">
        <template #header><span class="card-label">16. 富文本编辑组件</span></template>
        <div class="richtext-placeholder">
          <el-input
            v-model="form.richtext"
            type="textarea"
            :rows="4"
            placeholder="富文本编辑（TinyMCE将在实际业务中集成）"
          />
        </div>
        <div class="card-value">内容长度: {{ form.richtext?.length || 0 }}</div>
      </el-card>

      <!-- 17. 颜色选择 -->
      <el-card shadow="never">
        <template #header><span class="card-label">17. 颜色选择组件</span></template>
        <el-color-picker v-model="form.color" show-alpha :predefine="presetColors" />
        <div class="card-value">值: {{ form.color || '(空)' }}</div>
      </el-card>

      <!-- 18. 星级评分 -->
      <el-card shadow="never">
        <template #header><span class="card-label">18. 星级评分组件</span></template>
        <el-rate
          v-model="form.rate"
          :max="5"
          show-score
          show-text
          :texts="['极差', '失望', '一般', '满意', '惊喜']"
        />
        <div class="card-value">值: {{ form.rate ?? '(空)' }}</div>
      </el-card>

      <!-- 19. 标签输入 -->
      <el-card shadow="never">
        <template #header><span class="card-label">19. 标签输入组件</span></template>
        <div class="tag-input-area">
          <el-tag
            v-for="(tag, i) in form.tags"
            :key="i"
            closable
            :disable-transitions="false"
            style="margin: 2px"
            @close="form.tags.splice(i, 1)"
          >
            {{ tag }}
          </el-tag>
          <el-input
            v-if="tagInputVisible"
            ref="tagInputRef"
            v-model="tagInputValue"
            size="small"
            style="width: 80px"
            @blur="confirmTagInput"
            @keyup.enter="confirmTagInput"
          />
          <el-button v-else size="small" @click="showTagInput">+ 新标签</el-button>
        </div>
        <div class="card-value">标签: {{ form.tags.join(', ') || '(空)' }}</div>
      </el-card>

      <!-- 20. 密码输入框 -->
      <el-card shadow="never">
        <template #header><span class="card-label">20. 密码输入框</span></template>
        <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        <div class="card-value">
          已输入: {{ form.password ? '***' + form.password.slice(-2) : '(空)' }}
        </div>
      </el-card>
    </div>

    <div class="demo-footer">
      <el-divider />
      <h3>全部 20 个基础录入组件</h3>
      <p>覆盖 §8.1 定义的全部基础通用录入组件类型，可作为业务表单与表格单元格的通用基座。</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue'
import { UploadFilled, Plus } from '@element-plus/icons-vue'

defineOptions({ name: 'BasicInputsDemo' })

const presetColors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#E6E6FA']

const mockTreeData = [
  {
    value: 'cat1',
    label: '原材料',
    children: [
      { value: 'cat1-1', label: '钢材' },
      { value: 'cat1-2', label: '塑料' }
    ]
  },
  {
    value: 'cat2',
    label: '成品',
    children: [
      { value: 'cat2-1', label: '电子产品' },
      { value: 'cat2-2', label: '机械产品' }
    ]
  }
]

const form = reactive({
  text: '',
  textarea: '',
  integer: null as number | null,
  decimal: null as number | null,
  percent: null as number | null,
  date: '',
  time: '',
  datetime: '',
  month: '',
  switch1: true,
  switch2: 'Y',
  radio: '',
  checkbox: [] as string[],
  treeSelect: '',
  files: [] as any[],
  images: [] as any[],
  richtext: '',
  color: '#409EFF',
  rate: null as number | null,
  tags: ['VIP客户', '重点跟进'],
  password: ''
})

const tagInputVisible = ref(false)
const tagInputValue = ref('')
const tagInputRef = ref<any>()

function showTagInput() {
  tagInputVisible.value = true
  nextTick(() => {
    tagInputRef.value?.focus()
  })
}

function confirmTagInput() {
  const val = tagInputValue.value.trim()
  if (val && !form.tags.includes(val)) form.tags.push(val)
  tagInputVisible.value = false
  tagInputValue.value = ''
}
</script>

<style lang="scss" scoped>
.basic-inputs-demo {
  padding: 24px;
  height: 100%;
  overflow-y: auto;
  background: #e6e6fa;

  .demo-header {
    margin-bottom: 24px;
    h1 {
      font-size: 22px;
      font-weight: 700;
      margin: 0 0 8px;
      color: var(--el-text-color-primary);
    }
    .demo-desc {
      font-size: 14px;
      color: var(--el-text-color-secondary);
      margin: 0;
    }
  }

  .demo-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 16px;
  }

  .el-card {
    border-radius: 12px;
    :deep(.el-card__header) {
      padding: 12px 16px;
    }
    :deep(.el-card__body) {
      padding: 16px;
    }
  }

  .card-label {
    font-weight: 600;
    font-size: 14px;
  }
  .card-value {
    margin-top: 12px;
    font-size: 12px;
    color: var(--el-color-primary);
    background: var(--el-fill-color-light);
    padding: 4px 8px;
    border-radius: 4px;
  }

  .amount-row {
    display: flex;
    align-items: center;
    gap: 4px;
    .amount-prefix {
      font-size: 14px;
      color: var(--el-text-color-secondary);
    }
  }
  .switch-row {
    display: flex;
    gap: 16px;
    flex-wrap: wrap;
  }
  .tag-input-area {
    min-height: 32px;
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 2px;
  }
  .upload-text {
    font-size: 12px;
    color: var(--el-text-color-secondary);
    em {
      color: var(--el-color-primary);
      font-style: normal;
    }
  }
  .richtext-placeholder {
    border: 1px dashed var(--el-border-color);
    border-radius: 8px;
    padding: 8px;
  }

  .demo-footer {
    margin-top: 32px;
    text-align: center;
    h3 {
      font-size: 16px;
      color: var(--el-text-color-primary);
    }
    p {
      font-size: 13px;
      color: var(--el-text-color-secondary);
    }
  }
}
</style>
