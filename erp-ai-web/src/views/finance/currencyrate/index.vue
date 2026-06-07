<template>
  <div class="currencyrate-list-page">
    <!-- 快捷统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总记录数</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card stat-card--today">
          <div class="stat-value">{{ stats.todayCount }}</div>
          <div class="stat-label">今日新增</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card stat-card--type">
          <div class="stat-value">{{ stats.typeCount }}</div>
          <div class="stat-label">汇率类型数</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索表单 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" :inline="true" @submit.prevent>
        <el-form-item label="币种名称">
          <el-input
            v-model="searchForm.currencyName"
            placeholder="请输入币种名称"
            clearable
            style="width: 200px"
            @input="handleSearchDebounced"
          />
        </el-form-item>
        <el-form-item label="汇率类型">
          <el-select
            v-model="searchForm.rateType"
            placeholder="请选择汇率类型"
            clearable
            style="width: 160px"
            @change="handleSearch"
          >
            <el-option label="固定汇率" :value="1" />
            <el-option label="浮动汇率" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="RefreshRight" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="table-header">
          <span>{{ stats.total }} 条记录</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增币种汇率</el-button>
        </div>
      </template>

      <vxe-table
        :loading="tableLoading"
        :data="tableData"
        :scroll-y="{ enabled: true, gt: 100 }"
        max-height="600"
        border
        style="width: 100%"
      >
        <vxe-column type="seq" title="序号" width="60" align="center" />
        <vxe-column field="currencyCode" title="币种编码" min-width="120" />
        <vxe-column field="currencyName" title="币种名称" min-width="140" />
        <vxe-column field="currencySymbol" title="基准币种" width="100" align="center" />
        <vxe-column field="exchangeRate" title="汇率" width="140" align="right">
          <template #default="{ row }">
            <span>{{ formatRate(row.exchangeRate) }}</span>
          </template>
        </vxe-column>
        <vxe-column field="effectiveDate" title="汇率日期" width="130" align="center" />
        <vxe-column field="rateType" title="汇率类型" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="rateTypeTag(row.rateType)" size="small">
              {{ rateTypeLabel(row.rateType) }}
            </el-tag>
          </template>
        </vxe-column>
        <vxe-column field="createTime" title="创建时间" width="180" sortable />
        <vxe-column title="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm
              title="确认删除该币种汇率？"
              confirm-button-text="确认"
              cancel-button-text="取消"
              @confirm="handleDelete(row)"
            >
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </vxe-column>
      </vxe-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, RefreshRight, Plus } from '@element-plus/icons-vue'
import {
  getCurrencyRatePageApi,
  deleteCurrencyRateApi,
  type CurrencyRateVO
} from '@/api/modules/finance-currencyrate'

const tableLoading = ref(false)
const tableData = ref<CurrencyRateVO[]>([])

const searchForm = reactive({
  currencyName: '',
  rateType: undefined as number | undefined
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0
})

const stats = reactive({
  total: 0,
  todayCount: 0,
  typeCount: 0
})

const rateTypeMap: Record<number, string> = {
  1: '固定汇率',
  2: '浮动汇率'
}

function rateTypeLabel(type: number): string {
  return rateTypeMap[type] || '未知'
}

function rateTypeTag(type: number): 'success' | 'warning' | 'info' {
  if (type === 1) return 'success'
  if (type === 2) return 'warning'
  return 'info'
}

function formatRate(rate: number): string {
  if (rate == null) return '-'
  return Number(rate).toFixed(6)
}

let debounceTimer: ReturnType<typeof setTimeout> | null = null

function handleSearchDebounced(): void {
  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    handleSearch()
  }, 300)
}

async function handleSearch(): void {
  pagination.pageNum = 1
  await loadData()
}

function handleReset(): void {
  searchForm.currencyName = ''
  searchForm.rateType = undefined
  pagination.pageNum = 1
  loadData()
}

async function loadData(): Promise<void> {
  tableLoading.value = true
  try {
    const res = await getCurrencyRatePageApi({
      currencyName: searchForm.currencyName || undefined,
      rateType: searchForm.rateType,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    tableData.value = res.list || []
    pagination.total = res.total || 0
    updateStats(res.list || [], res.total || 0)
  } catch {
    ElMessage.error('加载币种汇率列表失败')
  } finally {
    tableLoading.value = false
  }
}

function updateStats(list: CurrencyRateVO[], total: number): void {
  stats.total = total
  const today = new Date().toISOString().split('T')[0]
  stats.todayCount = list.filter(
    (item) => item.createTime && item.createTime.startsWith(today)
  ).length
  const types = new Set(list.map((item) => item.rateType))
  stats.typeCount = types.size
}

async function handleDelete(row: CurrencyRateVO): Promise<void> {
  try {
    await deleteCurrencyRateApi(row.id)
    ElMessage.success('删除成功')
    await loadData()
  } catch {
    ElMessage.error('删除失败')
  }
}

function handleEdit(row: CurrencyRateVO): void {
  ElMessage.info(`编辑功能将在表单页实现，ID: ${row.id}`)
}

function handleAdd(): void {
  ElMessage.info('新增功能将在表单页实现')
}

function handleSizeChange(): void {
  pagination.pageNum = 1
  loadData()
}

function handlePageChange(): void {
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.currencyrate-list-page {
  padding: 20px;

  .stats-row {
    margin-bottom: 16px;

    .stat-card {
      text-align: center;

      .stat-value {
        font-size: 28px;
        font-weight: 700;
        color: var(--el-color-primary);
        line-height: 1.2;
      }

      .stat-label {
        margin-top: 8px;
        font-size: 14px;
        color: var(--el-text-color-secondary);
      }

      &--today {
        .stat-value {
          color: var(--el-color-success);
        }
      }

      &--type {
        .stat-value {
          color: var(--el-color-warning);
        }
      }
    }
  }

  .search-card {
    margin-bottom: 16px;
  }

  .table-card {
    .table-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .pagination-wrapper {
      display: flex;
      justify-content: flex-end;
      margin-top: 16px;
    }
  }
}
</style>
