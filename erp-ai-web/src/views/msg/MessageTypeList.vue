<template>
  <div class="message-type-list">
    <!-- 左侧树形面板 -->
    <div class="left-tree">
      <div class="tree-header">
        <span>消息类型</span>
        <el-button type="primary" size="small" @click="openDialog()">新增根类型</el-button>
      </div>
      <el-tree
        :data="treeData"
        :props="{ label: 'typeName', children: 'children' }"
        node-key="id"
        highlight-current
        default-expand-all
        @node-click="onNodeClick"
      >
        <template #default="{ data }">
          <span class="tree-node">
            <el-icon v-if="data.icon"><component :is="data.icon" /></el-icon>
            <span>{{ data.typeName }}</span>
            <el-tag v-if="!data.enableFlag" size="small" type="danger">停用</el-tag>
          </span>
        </template>
      </el-tree>
    </div>
    <!-- 右侧子类型列表 -->
    <div class="right-list">
      <div class="list-header">
        <h3>{{ currentNode?.typeName || '全部类型' }} - 子类型列表</h3>
        <el-button type="success" @click="openDialog(currentNode?.id)">新增子类型</el-button>
      </div>
      <el-table :data="childList" border>
        <el-table-column prop="typeCode" label="类型编码" width="150" />
        <el-table-column prop="typeName" label="类型名称" min-width="160" />
        <el-table-column prop="sortNo" label="排序号" width="80" />
        <el-table-column prop="icon" label="图标" width="100">
          <template #default="{ row }">
            <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="enableFlag" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.enableFlag ? 'success' : 'danger'">
              {{ row.enableFlag ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link @click="openDialog(row.parentId, row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- CRUD弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑类型' : '新增类型'" width="550px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="父级类型">
          <el-tree-select
            v-model="formData.parentId"
            :data="treeData"
            :props="{ label: 'typeName', children: 'children' }"
            check-strictly
            clearable
            placeholder="不选则为根类型"
          />
        </el-form-item>
        <el-form-item label="类型编码" prop="typeCode">
          <el-input v-model="formData.typeCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="类型名称" prop="typeName">
          <el-input v-model="formData.typeName" />
        </el-form-item>
        <el-form-item label="排序号" prop="sortNo">
          <el-input-number v-model="formData.sortNo" :min="0" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="formData.icon" placeholder="图标组件名" />
        </el-form-item>
        <el-form-item label="启用状态">
          <el-switch v-model="formData.enableFlag" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getTypeList, createType, updateType, deleteType } from '@/api/msg/type'
import type { TypeListVO, TypeFormDTO } from '@/types/msg'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const treeData = ref<(TypeListVO & { children?: TypeListVO[] })[]>([])
const currentNode = ref<TypeListVO | null>(null)
const childList = ref<TypeListVO[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const defaultFormData: TypeFormDTO = {
  id: undefined,
  parentId: undefined,
  typeCode: '',
  typeName: '',
  sortNo: 0,
  icon: '',
  enableFlag: true
}

const formData = reactive<TypeFormDTO>({ ...defaultFormData })

const rules: FormRules = {
  typeCode: [{ required: true, message: '请输入类型编码', trigger: 'blur' }],
  typeName: [{ required: true, message: '请输入类型名称', trigger: 'blur' }],
  sortNo: [{ required: true, message: '请输入排序号', trigger: 'blur' }]
}

const buildTree = (list: TypeListVO[]): (TypeListVO & { children?: TypeListVO[] })[] => {
  const map = new Map<number, TypeListVO & { children?: TypeListVO[] }>()
  const roots: (TypeListVO & { children?: TypeListVO[] })[] = []
  list.forEach((item) => map.set(item.id, { ...item, children: [] }))
  list.forEach((item) => {
    const node = map.get(item.id)!
    if (item.parentId && map.has(item.parentId)) {
      map.get(item.parentId)!.children!.push(node)
    } else {
      roots.push(node)
    }
  })
  return roots
}

const loadTree = async () => {
  const res = await getTypeList()
  treeData.value = buildTree(res)
  if (currentNode.value) {
    childList.value = findNodeChildren(treeData.value, currentNode.value.id)
  }
}

const findNodeChildren = (
  nodes: (TypeListVO & { children?: TypeListVO[] })[],
  id: number
): TypeListVO[] => {
  for (const node of nodes) {
    if (node.id === id) return node.children || []
    if (node.children) {
      const found = findNodeChildren(node.children, id)
      if (found.length || node.children.some((c) => c.id === id)) return found
    }
  }
  return []
}

const onNodeClick = (data: TypeListVO & { children?: TypeListVO[] }) => {
  currentNode.value = data
  childList.value = data.children || []
}

const openDialog = (parentId?: number, row?: TypeListVO) => {
  if (row) {
    isEdit.value = true
    Object.assign(formData, {
      id: row.id,
      parentId: row.parentId,
      typeCode: row.typeCode,
      typeName: row.typeName,
      sortNo: row.sortNo,
      icon: row.icon || '',
      enableFlag: row.enableFlag
    })
  } else {
    isEdit.value = false
    Object.assign(formData, { ...defaultFormData, parentId: parentId ?? undefined })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  await formRef.value?.validate()
  if (isEdit.value) {
    await updateType(formData)
    ElMessage.success('修改成功')
  } else {
    await createType(formData)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadTree()
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确认删除该类型？', '提示', { type: 'warning' })
  await deleteType(id)
  ElMessage.success('删除成功')
  loadTree()
}

onMounted(() => {
  loadTree()
})
</script>

<style scoped lang="scss">
.message-type-list {
  display: flex;
  height: 100%;
  padding: 16px;
  gap: 16px;
}

.left-tree {
  width: 260px;
  flex-shrink: 0;
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  overflow: auto;

  .tree-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 8px 12px;
    border-bottom: 1px solid var(--el-border-color);
    font-weight: 600;
  }

  .el-tree {
    padding: 8px;
  }
}

.right-list {
  flex: 1;
  overflow: auto;

  .list-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;

    h3 {
      margin: 0;
      font-size: 15px;
    }
  }
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}
</style>
