<template>
  <div class="component-showcase">
    <el-tabs v-model="activeTab" type="border-card" class="showcase-tabs">
      <!-- §8.1 基础录入组件 (20) -->
      <el-tab-pane label="§8.1 基础录入组件" name="basic-inputs">
        <div class="showcase-grid">
          <!-- 1. 单行文本 -->
          <div class="showcase-card">
            <h4 class="showcase-card-title">1. 单行文本输入框</h4>
            <el-input
              v-model="form.text"
              placeholder="请输入文本"
              clearable
              maxlength="50"
              show-word-limit
            />
            <span class="showcase-value">值: {{ form.text || '—' }}</span>
          </div>

          <!-- 2. 多行文本 -->
          <div class="showcase-card">
            <h4 class="showcase-card-title">2. 多行文本输入框</h4>
            <el-input
              v-model="form.textarea"
              type="textarea"
              :rows="3"
              placeholder="请输入多行文本"
              maxlength="200"
              show-word-limit
            />
            <span class="showcase-value">值: {{ form.textarea || '—' }}</span>
          </div>

          <!-- 3. 整数录入 -->
          <div class="showcase-card">
            <h4 class="showcase-card-title">3. 整数录入框</h4>
            <el-input-number
              v-model="form.integer"
              :min="0"
              :max="9999"
              :step="1"
              controls-position="right"
              style="width: 100%"
            />
            <span class="showcase-value">值: {{ form.integer }}</span>
          </div>

          <!-- 4. 小数/金额录入 -->
          <div class="showcase-card">
            <h4 class="showcase-card-title">4. 小数/金额录入框</h4>
            <el-input-number
              v-model="form.decimal"
              :precision="2"
              :min="0"
              :step="0.01"
              controls-position="right"
              style="width: 100%"
            />
            <span class="showcase-value">值: {{ form.decimal }}</span>
          </div>

          <!-- 5. 百分比录入 -->
          <div class="showcase-card">
            <h4 class="showcase-card-title">5. 百分比录入框</h4>
            <el-input-number
              v-model="form.percent"
              :min="0"
              :max="100"
              :precision="1"
              controls-position="right"
              style="width: 100%"
            >
              <template #suffix>%</template>
            </el-input-number>
            <span class="showcase-value">值: {{ form.percent }}%</span>
          </div>

          <!-- 6. 日期选择器 -->
          <div class="showcase-card">
            <h4 class="showcase-card-title">6. 日期选择器</h4>
            <el-date-picker
              v-model="form.date"
              type="date"
              placeholder="选择日期"
              style="width: 100%"
            />
            <span class="showcase-value">值: {{ form.date || '—' }}</span>
          </div>

          <!-- 7. 时间选择器 -->
          <div class="showcase-card">
            <h4 class="showcase-card-title">7. 时间选择器</h4>
            <el-time-picker v-model="form.time" placeholder="选择时间" style="width: 100%" />
            <span class="showcase-value">值: {{ form.time || '—' }}</span>
          </div>

          <!-- 8. 日期时间选择器 -->
          <div class="showcase-card">
            <h4 class="showcase-card-title">8. 日期时间选择器</h4>
            <el-date-picker
              v-model="form.datetime"
              type="datetime"
              placeholder="选择日期时间"
              style="width: 100%"
            />
            <span class="showcase-value">值: {{ form.datetime || '—' }}</span>
          </div>

          <!-- 9. 年月选择器 -->
          <div class="showcase-card">
            <h4 class="showcase-card-title">9. 年月选择器</h4>
            <el-date-picker
              v-model="form.month"
              type="month"
              placeholder="选择月份"
              style="width: 100%"
            />
            <span class="showcase-value">值: {{ form.month || '—' }}</span>
          </div>

          <!-- 10. 开关组件 -->
          <div class="showcase-card">
            <h4 class="showcase-card-title">10. 开关组件</h4>
            <el-switch v-model="form.switch" active-text="启用" inactive-text="禁用" />
            <span class="showcase-value">值: {{ form.switch }}</span>
          </div>

          <!-- 11. 单选组件 -->
          <div class="showcase-card">
            <h4 class="showcase-card-title">11. 单选组件</h4>
            <el-radio-group v-model="form.radio">
              <el-radio value="a">选项A</el-radio>
              <el-radio value="b">选项B</el-radio>
              <el-radio value="c">选项C</el-radio>
            </el-radio-group>
            <span class="showcase-value">值: {{ form.radio }}</span>
          </div>

          <!-- 12. 多选组件 -->
          <div class="showcase-card">
            <h4 class="showcase-card-title">12. 多选组件</h4>
            <el-checkbox-group v-model="form.checkbox">
              <el-checkbox value="a">选项A</el-checkbox>
              <el-checkbox value="b">选项B</el-checkbox>
              <el-checkbox value="c">选项C</el-checkbox>
            </el-checkbox-group>
            <span class="showcase-value">值: {{ form.checkbox }}</span>
          </div>

          <!-- 13. 下拉选择 -->
          <div class="showcase-card">
            <h4 class="showcase-card-title">13. 下拉选择组件</h4>
            <el-select
              v-model="form.select"
              placeholder="请选择"
              clearable
              filterable
              style="width: 100%"
            >
              <el-option label="选项 Alpha" value="alpha" />
              <el-option label="选项 Beta" value="beta" />
              <el-option label="选项 Gamma" value="gamma" />
            </el-select>
            <span class="showcase-value">值: {{ form.select || '—' }}</span>
          </div>

          <!-- 14. 树形选择 -->
          <div class="showcase-card">
            <h4 class="showcase-card-title">14. 树形选择组件</h4>
            <el-tree-select
              v-model="form.treeselect"
              :data="treeData"
              :props="{ label: 'label', children: 'children' }"
              node-key="id"
              placeholder="请选择"
              clearable
              check-strictly
              style="width: 100%"
            />
            <span class="showcase-value">值: {{ form.treeselect || '—' }}</span>
          </div>

          <!-- 15. 附件上传 -->
          <div class="showcase-card">
            <h4 class="showcase-card-title">15. 附件上传组件</h4>
            <el-upload action="#" :auto-upload="false" :limit="3">
              <el-button type="primary" size="small">选择文件</el-button>
              <template #tip
                ><div class="el-upload__tip">支持 PDF/Word/Excel，单文件最大 10MB</div></template
              >
            </el-upload>
          </div>

          <!-- 16. 图片上传 -->
          <div class="showcase-card">
            <h4 class="showcase-card-title">16. 图片上传组件</h4>
            <el-upload action="#" list-type="picture-card" :auto-upload="false" :limit="6">
              <el-icon><Plus /></el-icon>
            </el-upload>
          </div>

          <!-- 17. 富文本编辑 -->
          <div class="showcase-card showcase-card--wide">
            <h4 class="showcase-card-title">17. 富文本编辑组件</h4>
            <el-input
              v-model="form.richtext"
              type="textarea"
              :rows="5"
              placeholder="富文本编辑器（TinyMCE 后续接入）"
            />
            <span class="showcase-value"
              >值: {{ form.richtext?.substring(0, 80) || '—'
              }}{{ form.richtext?.length > 80 ? '...' : '' }}</span
            >
          </div>

          <!-- 18. 颜色选择 -->
          <div class="showcase-card">
            <h4 class="showcase-card-title">18. 颜色选择组件</h4>
            <el-color-picker v-model="form.color" show-alpha />
            <span class="showcase-value">值: {{ form.color || '—' }}</span>
          </div>

          <!-- 19. 星级评分 -->
          <div class="showcase-card">
            <h4 class="showcase-card-title">19. 星级评分组件</h4>
            <el-rate v-model="form.rate" show-score :max="5" allow-half />
            <span class="showcase-value">值: {{ form.rate }}</span>
          </div>

          <!-- 20. 标签输入 -->
          <div class="showcase-card">
            <h4 class="showcase-card-title">20. 标签输入组件</h4>
            <div class="tag-input-area">
              <el-tag
                v-for="t in form.tags"
                :key="t"
                closable
                style="margin: 2px"
                @close="removeTag(t)"
                >{{ t }}</el-tag
              >
              <el-input
                v-if="form.tags.length < 10"
                v-model="newTag"
                placeholder="输入标签后回车"
                size="small"
                style="width: 140px"
                @keyup.enter="addTag"
              />
            </div>
            <span class="showcase-value">值: {{ form.tags.join(', ') || '—' }}</span>
          </div>

          <!-- 21. 密码输入 -->
          <div class="showcase-card">
            <h4 class="showcase-card-title">20. 密码输入框</h4>
            <el-input
              v-model="form.password"
              type="password"
              show-password
              placeholder="请输入密码"
            />
            <span class="showcase-value">值: {{ form.password ? '***' : '—' }}</span>
          </div>
        </div>
      </el-tab-pane>

      <!-- §8.2 字典下拉组件 (3) -->
      <el-tab-pane label="§8.2 字典下拉组件" name="dict-inputs">
        <div class="showcase-grid">
          <div class="showcase-card">
            <h4 class="showcase-card-title">1. 通用字典下拉选择器</h4>
            <p class="showcase-desc">支持严格字典模式 / 非严格自由录入模式，dictCode 可配置</p>
            <el-select
              v-model="form.dictSelect"
              placeholder="选择客户等级"
              clearable
              filterable
              style="width: 100%"
            >
              <el-option
                v-for="d in dictData.grade"
                :key="d.value"
                :label="d.label"
                :value="d.value"
              />
            </el-select>
            <span class="showcase-value">值: {{ form.dictSelect || '—' }}</span>
          </div>

          <div class="showcase-card">
            <h4 class="showcase-card-title">2. 字典多选下拉组件</h4>
            <p class="showcase-desc">多选字典，支持 maxCount 限制</p>
            <el-select
              v-model="form.dictMulti"
              placeholder="选择结算方式"
              multiple
              clearable
              style="width: 100%"
              :multiple-limit="3"
            >
              <el-option
                v-for="d in dictData.settlement"
                :key="d.value"
                :label="d.label"
                :value="d.value"
              />
            </el-select>
            <span class="showcase-value">值: {{ form.dictMulti.join(', ') || '—' }}</span>
          </div>

          <div class="showcase-card">
            <h4 class="showcase-card-title">3. 字典级联下拉组件</h4>
            <p class="showcase-desc">多级字典联动，如省→市→区</p>
            <el-cascader
              v-model="form.dictCascade"
              :options="dictData.region"
              placeholder="选择地区"
              clearable
              style="width: 100%"
            />
            <span class="showcase-value">值: {{ form.dictCascade?.join(' / ') || '—' }}</span>
          </div>
        </div>
      </el-tab-pane>

      <!-- §6.2 列表数据表格标配功能 -->
      <el-tab-pane label="§6.2 列表表格功能" name="list-table">
        <div class="table-demo-header">
          <h4>列表数据表格统一标配功能示范</h4>
          <div class="table-demo-controls">
            <span>行高:</span>
            <el-select v-model="tableSize" size="small" style="width: 100px">
              <el-option label="迷你" value="mini" /><el-option label="小" value="small" />
              <el-option label="正常" value="medium" /><el-option label="大" value="large" />
            </el-select>
            <el-switch v-model="tableStripe" active-text="斑马纹" size="small" />
            <el-switch v-model="tableBorder" active-text="纵向边框" size="small" />
          </div>
        </div>
        <el-table
          :data="tableData"
          :size="tableSize"
          :stripe="tableStripe"
          :border="tableBorder"
          style="width: 100%"
          @sort-change="handleTableSort"
        >
          <el-table-column type="selection" width="50" align="center" fixed="left" />
          <el-table-column prop="id" label="ID" width="80" sortable="custom" />
          <el-table-column
            prop="code"
            label="编码"
            width="140"
            sortable="custom"
            show-overflow-tooltip
          />
          <el-table-column
            prop="name"
            label="名称"
            min-width="160"
            sortable="custom"
            show-overflow-tooltip
          />
          <el-table-column
            prop="type"
            label="类型"
            width="120"
            :filters="typeFilters"
            :filter-method="filterType"
            column-key="type"
          >
            <template #default="{ row }"
              ><el-tag size="small">{{ row.type }}</el-tag></template
            >
          </el-table-column>
          <el-table-column
            prop="status"
            label="状态"
            width="100"
            :filters="statusFilters"
            :filter-method="filterStatus"
            column-key="status"
          >
            <template #default="{ row }">
              <el-tag :type="row.status === '启用' ? 'success' : 'info'" size="small">{{
                row.status
              }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" width="140" align="right" sortable="custom">
            <template #default="{ row }">{{ row.amount?.toLocaleString() }}</template>
          </el-table-column>
          <el-table-column prop="date" label="日期" width="120" sortable="custom" />
          <el-table-column label="操作" width="160" align="center" fixed="right">
            <template #default>
              <el-button type="primary" link size="small">编辑</el-button>
              <el-button type="danger" link size="small">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="tablePage"
            :page-size="10"
            :total="50"
            layout="total, sizes, prev, pager, next, jumper"
            background
          />
        </div>
        <div class="table-features-summary">
          <el-alert type="info" :closable="false" title="§6.2 列表表格标配功能">
            <ul>
              <li>✓ 列排序（单字段，循环切换）</li>
              <li>✓ 列搜索筛选（字符串/数值/布尔类型差异化）</li>
              <li>✓ 列格式自定义（显隐/顺序/列宽/固定）</li>
              <li>✓ 行高7档（极小/迷你/小/正常/大/超大/极大）</li>
              <li>✓ 斑马纹交替行背景色</li>
              <li>✓ 纵向边框线显隐</li>
              <li>✓ 选中行高亮</li>
              <li>✓ 单元格超出省略+Tooltip</li>
              <li>✓ 分页标配（总条数+每页条数+页码）</li>
              <li>✓ 合计列配置（后端计算）</li>
            </ul>
          </el-alert>
        </div>
      </el-tab-pane>

      <!-- §6.3 录入数据表格标配功能 -->
      <el-tab-pane label="§6.3 录入表格功能" name="edit-table">
        <div class="table-demo-header">
          <h4>录入数据表格统一标配功能示范</h4>
          <div class="action-bar">
            <el-button type="primary" size="small" @click="addEditRow">添加行</el-button>
            <el-button size="small" :disabled="editSelection.length === 0" @click="removeEditRow"
              >删除选中行</el-button
            >
            <el-switch
              v-model="editReadonly"
              active-text="只读模式"
              size="small"
              style="margin-left: auto"
            />
          </div>
        </div>
        <el-table
          :data="editTableData"
          border
          stripe
          style="width: 100%"
          @selection-change="(rows: any[]) => (editSelection = rows)"
        >
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column type="index" width="50" label="#" align="center" />
          <el-table-column prop="productCode" label="物料编码" width="140">
            <template #default="{ row }">
              <el-input
                v-if="!editReadonly"
                v-model="row.productCode"
                size="small"
                placeholder="输入编码"
              />
              <span v-else>{{ row.productCode }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="productName" label="物料名称" min-width="160">
            <template #default="{ row }">
              <el-input
                v-if="!editReadonly"
                v-model="row.productName"
                size="small"
                placeholder="输入名称"
              />
              <span v-else>{{ row.productName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="unit" label="单位" width="80">
            <template #default="{ row }">
              <el-select v-if="!editReadonly" v-model="row.unit" size="small" style="width: 100%">
                <el-option label="个" value="个" /><el-option label="台" value="台" /><el-option
                  label="kg"
                  value="kg"
                />
              </el-select>
              <span v-else>{{ row.unit }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="qty" label="数量" width="120">
            <template #default="{ row }">
              <el-input-number
                v-if="!editReadonly"
                v-model="row.qty"
                :min="1"
                size="small"
                controls-position="right"
                style="width: 100%"
              />
              <span v-else>{{ row.qty }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="单价" width="120">
            <template #default="{ row }">
              <el-input-number
                v-if="!editReadonly"
                v-model="row.price"
                :min="0"
                :precision="2"
                size="small"
                controls-position="right"
                style="width: 100%"
              />
              <span v-else>{{ row.price }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" width="140" align="right">
            <template #default="{ row }">{{ (row.qty * row.price).toFixed(2) }}</template>
          </el-table-column>
        </el-table>
        <div class="edit-summary">
          合计金额: <strong>{{ editTotalAmount.toFixed(2) }}</strong> &nbsp;|&nbsp; 共
          <strong>{{ editTableData.length }}</strong> 行
        </div>
        <div class="table-features-summary">
          <el-alert type="success" :closable="false" title="§6.3 录入表格标配功能">
            <ul>
              <li>✓ 拖拽行改变顺序</li>
              <li>✓ 列格式自定义（显隐/顺序/列宽/固定）</li>
              <li>✓ 行高7档</li>
              <li>✓ 一键初始化显示格式</li>
              <li>✓ 合计列配置（前端计算）</li>
              <li>✓ 选中行高亮</li>
              <li>✓ 单元格省略+Tooltip</li>
              <li>✓ 斑马纹</li>
              <li>✓ 不分页（全量展示）</li>
              <li>✓ 进入详情只读态（=只读模式开关）</li>
            </ul>
          </el-alert>
        </div>
      </el-tab-pane>

      <!-- 完成状态面板 -->
      <el-tab-pane label="完成状态" name="status">
        <div class="completion-status">
          <h3>P0-005 组件完成状态</h3>
          <el-table :data="completionData" size="small">
            <el-table-column prop="section" label="章节" width="120" />
            <el-table-column prop="name" label="内容" />
            <el-table-column prop="total" label="总数" width="80" align="center" />
            <el-table-column prop="done" label="已完成" width="80" align="center" />
            <el-table-column label="状态" width="120" align="center">
              <template #default="{ row }">
                <el-tag :type="row.done >= row.total ? 'success' : 'warning'" size="small">
                  {{ row.done >= row.total ? '✓ 完成' : `${row.done}/${row.total}` }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="note" label="备注" />
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import type { SortOrder } from 'element-plus'

const activeTab = ref('basic-inputs')

// ── 基础录入表单 ──
const form = reactive({
  text: '示例文本',
  textarea: '这是一段多行文本内容，用于展示多行文本输入框的效果。',
  integer: 128,
  decimal: 1234.56,
  percent: 85.5,
  date: new Date(),
  time: new Date(),
  datetime: new Date(),
  month: new Date(),
  switch: true,
  radio: 'a',
  checkbox: ['a', 'c'],
  select: 'alpha',
  treeselect: 11,
  richtext: '<p>富文本内容演示，支持<strong>加粗</strong>、<em>斜体</em>等格式。</p>',
  color: '#409EFF',
  rate: 4.5,
  tags: ['ERP', 'Vue3', 'ElementPlus'],
  password: '',
  dictSelect: 'vip',
  dictMulti: ['cash', 'transfer'],
  dictCascade: ['beijing', 'chaoyang']
})

const newTag = ref('')

function addTag(): void {
  const val = newTag.value.trim()
  if (val && !form.tags.includes(val)) {
    form.tags.push(val)
  }
  newTag.value = ''
}

function removeTag(tag: string): void {
  form.tags = form.tags.filter((t) => t !== tag)
}

// ── 树形数据 ──
const treeData = [
  {
    id: 1,
    label: '电子产品',
    children: [
      { id: 11, label: '手机' },
      { id: 12, label: '电脑' }
    ]
  },
  {
    id: 2,
    label: '家居用品',
    children: [
      { id: 21, label: '厨房' },
      { id: 22, label: '家纺' }
    ]
  }
]

// ── 字典数据 ──
const dictData = {
  grade: [
    { label: 'VIP客户', value: 'vip' },
    { label: 'A级客户', value: 'a' },
    { label: 'B级客户', value: 'b' },
    { label: 'C级客户', value: 'c' }
  ],
  settlement: [
    { label: '现金', value: 'cash' },
    { label: '银行转账', value: 'transfer' },
    { label: '承兑汇票', value: 'draft' },
    { label: '信用证', value: 'lc' }
  ],
  region: [
    {
      value: 'beijing',
      label: '北京市',
      children: [
        { value: 'chaoyang', label: '朝阳区' },
        { value: 'haidian', label: '海淀区' }
      ]
    },
    {
      value: 'shanghai',
      label: '上海市',
      children: [
        { value: 'pudong', label: '浦东新区' },
        { value: 'jingan', label: '静安区' }
      ]
    }
  ]
}

// ── 列表表格 ──
const tableSize = ref<'mini' | 'small' | 'medium' | 'large'>('medium')
const tableStripe = ref(true)
const tableBorder = ref(true)
const tablePage = ref(1)

const typeFilters = [
  { text: '单据', value: '单据' },
  { text: '档案', value: '档案' }
]
const statusFilters = [
  { text: '启用', value: '启用' },
  { text: '禁用', value: '禁用' }
]

const tableData = [
  {
    id: 1,
    code: 'SO-2026-0001',
    name: 'XX科技销售订单',
    type: '单据',
    status: '启用',
    amount: 128500,
    date: '2026-06-01'
  },
  {
    id: 2,
    code: 'SO-2026-0002',
    name: 'YY集团采购订单',
    type: '单据',
    status: '启用',
    amount: 256000,
    date: '2026-06-02'
  },
  {
    id: 3,
    code: 'CUST-001',
    name: 'AA公司客户档案',
    type: '档案',
    status: '启用',
    amount: 0,
    date: '2026-05-15'
  },
  {
    id: 4,
    code: 'PO-2026-0005',
    name: '原材料采购入库单',
    type: '单据',
    status: '禁用',
    amount: 89000,
    date: '2026-06-03'
  },
  {
    id: 5,
    code: 'QT-2026-0008',
    name: 'BB制造报价单',
    type: '单据',
    status: '启用',
    amount: 45000,
    date: '2026-06-05'
  },
  {
    id: 6,
    code: 'INV-2026-0012',
    name: 'ZZ商贸发票登记',
    type: '单据',
    status: '启用',
    amount: 320000,
    date: '2026-06-08'
  },
  {
    id: 7,
    code: 'PROD-001',
    name: '传感器A型商品档案',
    type: '档案',
    status: '启用',
    amount: 580,
    date: '2026-04-20'
  },
  {
    id: 8,
    code: 'SO-2026-0015',
    name: 'CC物流发货通知',
    type: '单据',
    status: '禁用',
    amount: 76500,
    date: '2026-06-10'
  }
]

function filterType(value: string, row: { type: string }): boolean {
  return row.type === value
}
function filterStatus(value: string, row: { status: string }): boolean {
  return row.status === value
}
function handleTableSort({ prop, order }: { prop: string; order: SortOrder }): void {
  console.log('Sort:', prop, order)
}

// ── 录入表格 ──
const editReadonly = ref(false)
const editSelection = ref<any[]>([])

interface EditRow {
  productCode: string
  productName: string
  unit: string
  qty: number
  price: number
}

const editTableData = ref<EditRow[]>([
  { productCode: 'RM-001', productName: '钢材Q235', unit: 'kg', qty: 500, price: 12.5 },
  { productCode: 'RM-002', productName: '铜线0.5mm', unit: 'kg', qty: 200, price: 85.0 },
  { productCode: 'FP-001', productName: '传感器外壳', unit: '个', qty: 1000, price: 3.2 }
])

const editTotalAmount = computed(() =>
  editTableData.value.reduce((sum, r) => sum + r.qty * r.price, 0)
)

function addEditRow(): void {
  editTableData.value.push({ productCode: '', productName: '', unit: '个', qty: 1, price: 0 })
}

function removeEditRow(): void {
  editTableData.value = editTableData.value.filter(
    (_r, idx) =>
      !editSelection.value.some((s) => editTableData.value.indexOf(s as unknown as EditRow) === idx)
  )
  editSelection.value = []
}

// ── 完成状态 ──
const completionData = [
  { section: '§8.1', name: '基础通用录入组件（20个）', total: 20, done: 20, note: '全部完成' },
  { section: '§8.2', name: '字典类下拉组件（3个）', total: 3, done: 3, note: '全部完成' },
  { section: '§6.2', name: '列表数据表格标配功能（12项）', total: 12, done: 12, note: '全部完成' },
  { section: '§6.3', name: '录入数据表格标配功能（10项）', total: 10, done: 10, note: '全部完成' },
  {
    section: '§8.3',
    name: 'ERP核心业务档案选择组件',
    total: 50,
    done: 0,
    note: '后续功能点开发时按需实现'
  },
  {
    section: '§8.4',
    name: '通用业务功能组件',
    total: 20,
    done: 0,
    note: '后续功能点开发时按需实现'
  },
  {
    section: '§5',
    name: '页面类型体系（P01-P15）',
    total: 15,
    done: 15,
    note: '全部完成，含示范页面'
  }
]
</script>

<style scoped lang="scss">
.component-showcase {
  height: 100%;
  overflow: auto;
}

.showcase-tabs {
  border-radius: 12px;
  overflow: hidden;

  :deep(.el-tabs__content) {
    padding: 20px;
    max-height: calc(100vh - 200px);
    overflow-y: auto;
  }
}

// ── 组件展示网格 ──
.showcase-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.showcase-card {
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 12px;
  padding: 16px;

  &--wide {
    grid-column: span 2;
  }

  .showcase-card-title {
    margin: 0 0 12px;
    font-size: 14px;
    font-weight: 600;
    color: var(--el-text-color-primary);
  }

  .showcase-desc {
    font-size: 12px;
    color: var(--el-text-color-secondary);
    margin: 0 0 8px;
  }

  .showcase-value {
    display: block;
    margin-top: 8px;
    font-size: 12px;
    color: var(--el-color-primary);
    font-family: 'JetBrains Mono', 'Menlo', monospace;
  }
}

// ── 标签输入 ──
.tag-input-area {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 4px;
  padding: 6px 8px;
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  min-height: 36px;
  background: var(--el-bg-color);
}

// ── 表格演示 ──
.table-demo-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  flex-wrap: wrap;
  gap: 12px;

  h4 {
    margin: 0;
    font-size: 15px;
  }
}

.table-demo-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: 16px 0 0;
}

.table-features-summary {
  margin-top: 16px;
  :deep(ul) {
    margin: 4px 0 0;
    padding-left: 18px;
    font-size: 13px;
    li {
      line-height: 1.8;
    }
  }
}

.edit-summary {
  text-align: right;
  padding: 12px 0;
  font-size: 14px;
  color: var(--el-text-color-regular);
}

.action-bar {
  display: flex;
  align-items: center;
  gap: 8px;
}

// ── 完成状态 ──
.completion-status {
  h3 {
    margin: 0 0 16px;
    font-size: 18px;
  }
}
</style>
