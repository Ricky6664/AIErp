<template>
  <div class="menu-list-page">
    <div class="page-header">
      <h2>菜单管理页面</h2>
      <p class="page-desc">管理系统菜单，支持目录/菜单/按钮三种类型，树形结构展示</p>
    </div>

    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="searchMenuName"
          placeholder="菜单名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
        >
          <template #prepend>菜单名称</template>
        </el-input>
        <el-select
          v-model="searchMenuType"
          placeholder="菜单类型"
          clearable
          style="width: 140px"
          @change="handleSearch"
        >
          <el-option label="目录" value="directory" />
          <el-option label="菜单" value="menu" />
          <el-option label="按钮" value="button" />
        </el-select>
        <el-select
          v-model="searchEnabled"
          placeholder="状态"
          clearable
          style="width: 120px"
          @change="handleSearch"
        >
          <el-option label="启用" :value="true" />
          <el-option label="禁用" :value="false" />
        </el-select>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-button v-permission="'system:menu:add'" type="success" @click="handleAddRoot">
          <el-icon><Plus /></el-icon>
          新增菜单
        </el-button>
        <el-button @click="toggleExpandAll">
          <el-icon><Sort /></el-icon>
          {{ expandAll ? '全部折叠' : '全部展开' }}
        </el-button>
      </div>
    </div>

    <el-table
      ref="tableRef"
      v-loading="loading"
      :data="menuTreeData"
      row-key="id"
      border
      stripe
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      :default-expand-all="expandAll"
      style="width: 100%"
    >
      <el-table-column prop="menuName" label="菜单名称" width="200" />
      <el-table-column label="菜单类型" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="menuTypeTagType(row.menuType)" size="small">
            {{ menuTypeLabel(row.menuType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="permissionCode" label="权限编码" width="180" show-overflow-tooltip />
      <el-table-column prop="routePath" label="路由路径" width="160" show-overflow-tooltip />
      <el-table-column prop="icon" label="图标" width="80" align="center">
        <template #default="{ row }">
          <el-icon v-if="row.icon" :size="18">
            <component :is="row.icon" />
          </el-icon>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="70" align="center" />
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.isEnabled ? 'success' : 'info'" size="small">
            {{ row.isEnabled ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="320" align="center" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.menuType !== 'button'"
            v-permission="'system:menu:add'"
            type="primary"
            link
            size="small"
            @click="handleAddChild(row)"
          >
            <el-icon><Plus /></el-icon>
            新增子级
          </el-button>
          <el-button
            v-permission="'system:menu:edit'"
            type="primary"
            link
            size="small"
            @click="handleEdit(row)"
          >
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-button
            v-permission="'system:menu:edit'"
            type="warning"
            link
            size="small"
            @click="handleToggleStatus(row)"
          >
            <el-icon><Switch /></el-icon>
            {{ row.isEnabled ? '禁用' : '启用' }}
          </el-button>
          <el-button
            v-permission="'system:menu:delete'"
            type="danger"
            link
            size="small"
            @click="handleDelete(row)"
          >
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="650px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        size="default"
      >
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="formData.parentId"
            :data="parentTreeOptions"
            :props="{ label: 'menuName', value: 'id', children: 'children' }"
            placeholder="无（根菜单）"
            clearable
            check-strictly
            :render-after-expand="false"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="formData.menuName" placeholder="请输入菜单名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="formData.menuType" :disabled="isEdit">
            <el-radio value="directory">目录</el-radio>
            <el-radio value="menu">菜单</el-radio>
            <el-radio value="button">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="formData.menuType !== 'button'" label="路由路径">
          <el-input v-model="formData.routePath" placeholder="如 /system/user" />
        </el-form-item>
        <el-form-item v-if="formData.menuType === 'menu'" label="组件路径">
          <el-input v-model="formData.componentPath" placeholder="如 system/user/index" />
        </el-form-item>
        <el-form-item label="权限编码">
          <el-input v-model="formData.permissionCode" placeholder="如 system:menu:add" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="formData.icon" placeholder="Element Plus 图标名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="9999" style="width: 160px" />
        </el-form-item>
        <el-form-item label="其他设置">
          <el-checkbox v-model="formData.isVisible">可见</el-checkbox>
          <el-checkbox v-model="formData.isEnabled" style="margin-left: 16px">启用</el-checkbox>
          <el-checkbox v-model="formData.isKeepAlive" style="margin-left: 16px">缓存</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Search, Refresh, Plus, Delete, Edit, Switch, Sort } from '@element-plus/icons-vue'
import type { SysMenuListItem } from '@/api/types/menu'
import {
  getSysMenuTree,
  getSysMenuDetail,
  createSysMenu,
  updateSysMenu,
  updateSysMenuStatus,
  deleteSysMenu
} from '@/api/modules/menu'

const loading = ref(false)
const expandAll = ref(true)

const searchMenuName = ref('')
const searchMenuType = ref('')
const searchEnabled = ref<boolean | undefined>(undefined)

const menuTreeData = ref<SysMenuListItem[]>([])
const allTreeData = ref<SysMenuListItem[]>([])

const menuTypeMap: Record<string, string> = {
  directory: '目录',
  menu: '菜单',
  button: '按钮'
}

const menuTypeTagMap: Record<string, 'primary' | 'warning' | 'danger'> = {
  directory: 'primary',
  menu: 'warning',
  button: 'danger'
}

function menuTypeLabel(type: string): string {
  return menuTypeMap[type] || type
}

function menuTypeTagType(type: string): '' | 'primary' | 'success' | 'warning' | 'info' | 'danger' {
  return menuTypeTagMap[type] || 'info'
}

// Dialog state
const dialogVisible = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()

const formData = reactive({
  parentId: undefined as number | undefined,
  menuName: '',
  menuType: 'menu',
  permissionCode: '',
  routePath: '',
  routeName: '',
  componentPath: '',
  icon: '',
  sortOrder: 0,
  isVisible: true,
  isEnabled: true,
  isKeepAlive: false,
  isExternalLink: false,
  externalUrl: ''
})

const formRules: FormRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
  sortOrder: [{ required: true, message: '请输入排序号', trigger: 'blur' }]
}

const dialogTitle = computed(() => (isEdit.value ? '编辑菜单' : '新增菜单'))

// Parent tree options exclude button types (buttons cannot have children)
const parentTreeOptions = computed(() => {
  function filterNonButton(items: SysMenuListItem[]): SysMenuListItem[] {
    return items
      .filter((item) => item.menuType !== 'button')
      .map((item) => ({
        ...item,
        children: item.children ? filterNonButton(item.children) : undefined
      }))
  }
  return filterNonButton(allTreeData.value)
})

// Data fetch
async function fetchMenuTree(): Promise<void> {
  loading.value = true
  try {
    const data = await getSysMenuTree()
    allTreeData.value = data || []
    applyFilter()
  } catch {
    allTreeData.value = []
    menuTreeData.value = []
  } finally {
    loading.value = false
  }
}

function applyFilter(): void {
  let result = allTreeData.value

  if (searchMenuName.value) {
    const keyword = searchMenuName.value.toLowerCase()
    result = filterTree(result, (item) => item.menuName.toLowerCase().includes(keyword))
  }
  if (searchMenuType.value) {
    result = filterTree(result, (item) => item.menuType === searchMenuType.value)
  }
  if (searchEnabled.value !== undefined) {
    result = filterTree(result, (item) => item.isEnabled === searchEnabled.value)
  }
  menuTreeData.value = result
}

function filterTree(
  items: SysMenuListItem[],
  predicate: (item: SysMenuListItem) => boolean
): SysMenuListItem[] {
  const result: SysMenuListItem[] = []
  for (const item of items) {
    const matchedChildren = item.children ? filterTree(item.children, predicate) : []
    if (predicate(item) || matchedChildren.length > 0) {
      result.push({
        ...item,
        children: matchedChildren.length > 0 ? matchedChildren : item.children
      })
    }
  }
  return result
}

function handleSearch(): void {
  applyFilter()
}

function handleReset(): void {
  searchMenuName.value = ''
  searchMenuType.value = ''
  searchEnabled.value = undefined
  applyFilter()
}

function toggleExpandAll(): void {
  expandAll.value = !expandAll.value
}

// Form operations
function resetForm(): void {
  formData.parentId = undefined
  formData.menuName = ''
  formData.menuType = 'menu'
  formData.permissionCode = ''
  formData.routePath = ''
  formData.routeName = ''
  formData.componentPath = ''
  formData.icon = ''
  formData.sortOrder = 0
  formData.isVisible = true
  formData.isEnabled = true
  formData.isKeepAlive = false
  formData.isExternalLink = false
  formData.externalUrl = ''
  isEdit.value = false
  editId.value = null
  formRef.value?.resetFields()
}

function handleAddRoot(): void {
  resetForm()
  dialogVisible.value = true
}

function handleAddChild(row: SysMenuListItem): void {
  resetForm()
  formData.parentId = row.id
  dialogVisible.value = true
}

function handleEdit(row: SysMenuListItem): void {
  resetForm()
  isEdit.value = true
  editId.value = row.id
  loadDetail(row.id)
}

async function loadDetail(id: number): Promise<void> {
  try {
    const detail = await getSysMenuDetail(id)
    formData.parentId = detail.parentId === 0 ? undefined : detail.parentId || undefined
    formData.menuName = detail.menuName
    formData.menuType = detail.menuType
    formData.permissionCode = detail.permissionCode || ''
    formData.routePath = detail.routePath || ''
    formData.routeName = detail.routeName || ''
    formData.componentPath = detail.componentPath || ''
    formData.icon = detail.icon || ''
    formData.sortOrder = detail.sortOrder
    formData.isVisible = detail.isVisible
    formData.isEnabled = detail.isEnabled
    formData.isKeepAlive = detail.isKeepAlive
    formData.isExternalLink = detail.isExternalLink
    formData.externalUrl = detail.externalUrl || ''
    dialogVisible.value = true
  } catch {
    ElMessage.error('加载菜单详情失败')
  }
}

async function handleSubmit(): Promise<void> {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value && editId.value !== null) {
      await updateSysMenu(editId.value, {
        id: editId.value,
        parentId: formData.parentId,
        menuName: formData.menuName,
        menuType: formData.menuType,
        permissionCode: formData.permissionCode || undefined,
        routePath: formData.routePath || undefined,
        routeName: formData.routeName || undefined,
        componentPath: formData.componentPath || undefined,
        icon: formData.icon || undefined,
        sortOrder: formData.sortOrder,
        isVisible: formData.isVisible,
        isEnabled: formData.isEnabled,
        isKeepAlive: formData.isKeepAlive,
        isExternalLink: formData.isExternalLink,
        externalUrl: formData.externalUrl || undefined
      })
      ElMessage.success('修改成功')
    } else {
      await createSysMenu({
        parentId: formData.parentId,
        menuName: formData.menuName,
        menuType: formData.menuType,
        permissionCode: formData.permissionCode || undefined,
        routePath: formData.routePath || undefined,
        routeName: formData.routeName || undefined,
        componentPath: formData.componentPath || undefined,
        icon: formData.icon || undefined,
        sortOrder: formData.sortOrder,
        isVisible: formData.isVisible,
        isEnabled: formData.isEnabled,
        isKeepAlive: formData.isKeepAlive,
        isExternalLink: formData.isExternalLink,
        externalUrl: formData.externalUrl || undefined
      })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    await fetchMenuTree()
  } catch {
    // error handled by interceptor
  } finally {
    submitLoading.value = false
  }
}

async function handleToggleStatus(row: SysMenuListItem): Promise<void> {
  const newStatus = !row.isEnabled
  const action = newStatus ? '启用' : '禁用'
  try {
    await updateSysMenuStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    await fetchMenuTree()
  } catch {
    // error handled by interceptor
  }
}

function handleDelete(row: SysMenuListItem): void {
  ElMessageBox.confirm(
    `确定删除菜单 "${row.menuName}"？删除该菜单将同时删除其所有子菜单，请谨慎操作。`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(async () => {
      await deleteSysMenu(row.id)
      ElMessage.success('删除成功')
      await fetchMenuTree()
    })
    .catch(() => {
      // user cancelled
    })
}

onMounted(() => {
  fetchMenuTree()
})
</script>

<style scoped lang="scss">
.menu-list-page {
  padding: 20px;

  .page-header {
    margin-bottom: 20px;

    h2 {
      margin: 0 0 8px;
      font-size: 20px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }

    .page-desc {
      margin: 0;
      font-size: 14px;
      color: var(--el-text-color-secondary);
    }
  }

  .toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    flex-wrap: wrap;
    gap: 12px;

    .toolbar-left {
      display: flex;
      align-items: center;
      gap: 8px;
      flex-wrap: wrap;
    }

    .toolbar-right {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }
}
</style>
