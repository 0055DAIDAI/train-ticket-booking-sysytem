// views/admin/route.vue
<template>
  <div class="route-page">
    <el-card>
      <div class="header">
        <h3>路线管理</h3>
        <el-button type="primary" @click="openCreateDialog">添加路线</el-button>
      </div>

      <el-table :data="routes" style="width: 100%; margin-top: 20px;" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="路线名称" width="200" />
        <el-table-column prop="fromStationName" label="起点站" width="150" />
        <el-table-column prop="toStationName" label="终点站" width="150" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteRoute(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑路线对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="路线名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入路线名称" />
        </el-form-item>
        <el-form-item label="起点站" prop="fromStationName">
          <el-input v-model="form.fromStationName" placeholder="请输入起点站" />
        </el-form-item>
        <el-form-item label="终点站" prop="toStationName">
          <el-input v-model="form.toStationName" placeholder="请输入终点站" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import adminApi from '@/api/adminApi'

const routes = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: '',
  name: '',
  fromStationName: '',
  toStationName: ''
})

const dialogTitle = computed(() => {
  return isEdit.value ? '编辑路线' : '添加路线'
})

const rules = {
  name: [{ required: true, message: '请输入路线名称', trigger: 'blur' }],
  fromStationName: [{ required: true, message: '请输入起点站', trigger: 'blur' }],
  toStationName: [{ required: true, message: '请输入终点站', trigger: 'blur' }]
}

// 获取路线列表
async function fetchRoutes() {
  loading.value = true
  try {
    const res = await adminApi.getRoutes()
    routes.value = res.data || []
  } catch (err) {
    ElMessage.error(err.message || '获取路线列表失败')
  } finally {
    loading.value = false
  }
}

// 打开添加对话框
function openCreateDialog() {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 打开编辑对话框
function openEditDialog(row) {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

// 重置表单
function resetForm() {
  Object.assign(form, {
    id: '',
    name: '',
    fromStationName: '',
    toStationName: ''
  })
  formRef.value?.resetFields()
}

// 提交表单
async function submitForm() {
  try {
    await formRef.value.validate()
    
    submitLoading.value = true
    const payload = {
      name: form.name,
      fromStationName: form.fromStationName,
      toStationName: form.toStationName
    }
    
    if (isEdit.value) {
      payload.id = form.id
      await adminApi.updateRoute(payload)
      ElMessage.success('路线更新成功')
    } else {
      await adminApi.createRoute(payload)
      ElMessage.success('路线添加成功')
    }
    
    dialogVisible.value = false
    fetchRoutes()
  } catch (err) {
    ElMessage.error(err.message || (isEdit.value ? '路线更新失败' : '路线添加失败'))
  } finally {
    submitLoading.value = false
  }
}

// 删除路线
function deleteRoute(id) {
  ElMessageBox.confirm('确定要删除该路线吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await adminApi.deleteRoute(id)
      ElMessage.success('删除成功')
      fetchRoutes()
    } catch (err) {
      ElMessage.error(err.message || '删除失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

onMounted(() => {
  fetchRoutes()
})
</script>

<style scoped>
.route-page {
  padding: 16px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>