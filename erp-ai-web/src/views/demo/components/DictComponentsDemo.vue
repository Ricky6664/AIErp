<template>
  <div class="component-demo dict-demo">
    <div class="demo-header">
      <h1>字典类下拉组件（§8.2）</h1>
      <p class="demo-desc">
        3
        个可配置字典源的标准下拉组件：单选下拉、多选下拉、级联下拉。支持严格字典匹配/非严格自由录入两种模式。
      </p>
    </div>

    <div class="demo-grid">
      <!-- 1. 通用字典下拉选择器 -->
      <el-card shadow="never">
        <template #header>
          <div class="card-header">
            <span class="card-label">1. 通用字典下拉选择器</span>
            <el-switch v-model="dictStrict" active-text="严格" inactive-text="自由" size="small" />
          </div>
        </template>
        <div class="demo-section">
          <p class="section-desc">来源: dict_code = "SETTLEMENT_METHOD"（结算方式）</p>
          <el-select
            v-model="form.settlement"
            placeholder="请选择结算方式"
            :allow-create="!dictStrict"
            :filterable="true"
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="item in settlementOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <div class="card-value">
            选中: {{ form.settlement || '(空)' }} | 模式: {{ dictStrict ? '严格字典' : '自由录入' }}
          </div>
        </div>
        <el-divider />
        <div class="demo-section">
          <p class="section-desc">来源: dict_code = "CURRENCY"（币种）</p>
          <el-select
            v-model="form.currency"
            placeholder="请选择币种"
            filterable
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="item in currencyOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
              <span>{{ item.label }}</span
              ><span style="float: right; color: var(--el-text-color-secondary); font-size: 13px">{{
                item.extra
              }}</span>
            </el-option>
          </el-select>
          <div class="card-value">选中: {{ form.currency || '(空)' }}</div>
        </div>
      </el-card>

      <!-- 2. 字典多选下拉组件 -->
      <el-card shadow="never">
        <template #header><span class="card-label">2. 字典多选下拉组件</span></template>
        <div class="demo-section">
          <p class="section-desc">来源: dict_code = "GRADE"（客户等级），maxCount=3</p>
          <el-select
            v-model="form.grades"
            placeholder="请选择客户等级（最多3个）"
            multiple
            filterable
            collapse-tags
            collapse-tags-tooltip
            :max-collapse-tags="2"
            style="width: 100%"
          >
            <el-option
              v-for="item in gradeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <div class="card-value">
            选中: {{ form.grades.join(', ') || '(空)' }} ({{ form.grades.length }}/3)
          </div>
        </div>
        <el-divider />
        <div class="demo-section">
          <p class="section-desc">来源: dict_code = "TAG_TYPE"（标签类型），无数量限制</p>
          <el-select
            v-model="form.tagTypes"
            placeholder="请选择标签类型"
            multiple
            filterable
            collapse-tags
            style="width: 100%"
          >
            <el-option
              v-for="item in tagTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <div class="card-value">选中: {{ form.tagTypes.join(', ') || '(空)' }}</div>
        </div>
      </el-card>

      <!-- 3. 字典级联下拉组件 -->
      <el-card shadow="never">
        <template #header><span class="card-label">3. 字典级联下拉组件</span></template>
        <div class="demo-section">
          <p class="section-desc">二级联动: 省份 → 城市（字典级联）</p>
          <div class="cascade-row">
            <el-select
              v-model="form.province"
              placeholder="选择省份"
              clearable
              style="flex: 1"
              @change="form.city = ''"
            >
              <el-option
                v-for="item in provinceOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
            <span class="cascade-arrow">→</span>
            <el-select
              v-model="form.city"
              placeholder="选择城市"
              clearable
              :disabled="!form.province"
              style="flex: 1"
            >
              <el-option
                v-for="item in cityMap[form.province] || []"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </div>
          <div class="card-value">
            省: {{ form.province || '(空)' }} → 市: {{ form.city || '(空)' }}
          </div>
        </div>
        <el-divider />
        <div class="demo-section">
          <p class="section-desc">三级联动: 商品分类 → 子分类 → 具体品类</p>
          <el-cascader
            v-model="form.productClass"
            :options="productClassOptions"
            placeholder="请选择商品分类"
            clearable
            filterable
            style="width: 100%"
          />
          <div class="card-value">路径: {{ form.productClass?.join(' / ') || '(空)' }}</div>
        </div>
      </el-card>
    </div>

    <div class="demo-footer">
      <el-divider />
      <h3>字典组件特性总览</h3>
      <el-row :gutter="16">
        <el-col :span="8">
          <el-statistic title="字典源类型" value="15+" suffix="种" />
          <p class="stat-desc">结算方式、币种、客户等级、标签类型、地区、商品分类等</p>
        </el-col>
        <el-col :span="8">
          <el-statistic title="录入模式" value="2" suffix="种" />
          <p class="stat-desc">严格字典匹配 / 非严格自由录入</p>
        </el-col>
        <el-col :span="8">
          <el-statistic title="级联深度" value="3" suffix="级" />
          <p class="stat-desc">支持二级、三级级联联动</p>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'

defineOptions({ name: 'DictComponentsDemo' })

const dictStrict = ref(true)

const settlementOptions = [
  { value: 'CASH', label: '现金' },
  { value: 'BANK', label: '银行转账' },
  { value: 'CHECK', label: '支票' },
  { value: 'WECHAT', label: '微信支付' },
  { value: 'ALIPAY', label: '支付宝' },
  { value: 'CREDIT', label: '账期结算' }
]

const currencyOptions = [
  { value: 'CNY', label: '人民币', extra: '¥' },
  { value: 'USD', label: '美元', extra: '$' },
  { value: 'EUR', label: '欧元', extra: '€' },
  { value: 'JPY', label: '日元', extra: '¥' },
  { value: 'GBP', label: '英镑', extra: '£' },
  { value: 'HKD', label: '港币', extra: 'HK$' }
]

const gradeOptions = [
  { value: 'A', label: 'A级-战略客户' },
  { value: 'B', label: 'B级-重点客户' },
  { value: 'C', label: 'C级-普通客户' },
  { value: 'D', label: 'D级-潜在客户' }
]

const tagTypeOptions = [
  { value: 'VIP', label: 'VIP' },
  { value: 'NEW', label: '新客户' },
  { value: 'HOT', label: '热成交' },
  { value: 'COLD', label: '休眠' },
  { value: 'RISK', label: '风险' },
  { value: 'KEY', label: '重点' }
]

const provinceOptions = [
  { value: 'GD', label: '广东省' },
  { value: 'BJ', label: '北京市' },
  { value: 'SH', label: '上海市' }
]

const cityMap: Record<string, { value: string; label: string }[]> = {
  GD: [
    { value: 'GZ', label: '广州市' },
    { value: 'SZ', label: '深圳市' },
    { value: 'DG', label: '东莞市' }
  ],
  BJ: [
    { value: 'CY', label: '朝阳区' },
    { value: 'HD', label: '海淀区' },
    { value: 'FT', label: '丰台区' }
  ],
  SH: [
    { value: 'PD', label: '浦东新区' },
    { value: 'HP', label: '黄浦区' },
    { value: 'JA', label: '静安区' }
  ]
}

const productClassOptions = [
  {
    value: 'raw',
    label: '原材料',
    children: [
      {
        value: 'metal',
        label: '金属材料',
        children: [
          { value: 'steel', label: '钢材' },
          { value: 'alum', label: '铝材' }
        ]
      },
      {
        value: 'plastic',
        label: '塑料',
        children: [
          { value: 'abs', label: 'ABS' },
          { value: 'pp', label: 'PP' }
        ]
      }
    ]
  },
  {
    value: 'finished',
    label: '产成品',
    children: [
      {
        value: 'elec',
        label: '电子产品',
        children: [
          { value: 'phone', label: '手机' },
          { value: 'pc', label: '电脑' }
        ]
      }
    ]
  }
]

const form = reactive({
  settlement: '',
  currency: '',
  grades: [] as string[],
  tagTypes: [] as string[],
  province: '',
  city: '',
  productClass: [] as string[]
})
</script>

<style lang="scss" scoped>
.dict-demo {
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
    grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
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

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
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
  .section-desc {
    font-size: 12px;
    color: var(--el-text-color-secondary);
    margin: 0 0 8px;
  }
  .demo-section {
    margin-bottom: 4px;
  }

  .cascade-row {
    display: flex;
    align-items: center;
    gap: 8px;
    .cascade-arrow {
      color: var(--el-text-color-placeholder);
      font-weight: 600;
    }
  }

  .demo-footer {
    margin-top: 32px;
    text-align: center;
    h3 {
      font-size: 16px;
      color: var(--el-text-color-primary);
      margin-bottom: 16px;
    }
    .stat-desc {
      font-size: 12px;
      color: var(--el-text-color-secondary);
      text-align: center;
      margin-top: 4px;
    }
  }
}
</style>
