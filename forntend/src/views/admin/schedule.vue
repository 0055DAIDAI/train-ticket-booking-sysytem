// views/admin/schedule.vue
<template>
  <div class="schedule-page">
    <el-card>
      <div class="header">
        <h3>车次管理</h3>
        <el-button type="primary" @click="openCreateDialog">添加车次</el-button>
      </div>

      <el-table :data="schedules" style="margin-top: 20px; width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="trainNumber" label="车次" width="120" />
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="departureTime" label="出发时间" width="180">
          <template #default="{ row }">{{ formatTime(row.departureTime) }}</template>
        </el-table-column>
        <el-table-column prop="arrivalTime" label="到达时间" width="180">
          <template #default="{ row }">{{ formatTime(row.arrivalTime) }}</template>
        </el-table-column>
        <el-table-column prop="ticketTotal" label="总票数" width="100" />
        <el-table-column prop="ticketSold" label="已售" width="100" />
        <el-table-column prop="price" label="票价(¥)" width="100" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteTrain(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑车次对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="车次" prop="trainNumber">
              <el-input v-model="form.trainNumber" placeholder="请输入车次" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="日期" prop="date">
              <el-date-picker
                v-model="form.date"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="路线" prop="routeId">
              <el-select v-model="form.routeId" placeholder="请选择路线" style="width: 100%">
                <el-option
                  v-for="route in routes"
                  :key="route.id"
                  :label="route.name"
                  :value="route.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="票价(¥)" prop="price">
              <el-input-number v-model="form.price" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出发时间" prop="departureTime">
              <el-time-picker
                v-model="form.departureTime"
                value-format="HH:mm:ss"
                placeholder="请选择出发时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="到达时间" prop="arrivalTime">
              <el-time-picker
                v-model="form.arrivalTime"
                value-format="HH:mm:ss"
                placeholder="请选择到达时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="总票数" prop="ticketTotal">
              <el-input-number v-model="form.ticketTotal" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
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

const schedules = ref([])
const routes = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: '',
  trainNumber: '',
  date: '',
  routeId: '',
  departureTime: '',
  arrivalTime: '',
  ticketTotal: 0,
  price: 0
})

const dialogTitle = computed(() => {
  return isEdit.value ? '编辑车次' : '添加车次'
})

const rules = {
  trainNumber: [{ required: true, message: '请输入车次', trigger: 'blur' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }],
  routeId: [{ required: true, message: '请选择路线', trigger: 'change' }],
  departureTime: [{ required: true, message: '请选择出发时间', trigger: 'change' }],
  arrivalTime: [{ required: true, message: '请选择到达时间', trigger: 'change' }],
  ticketTotal: [{ required: true, message: '请输入总票数', trigger: 'blur' }],
  price: [{ required: true, message: '请输入票价', trigger: 'blur' }]
}

// 格式化时间显示
function formatTime(dateTime) {
  if (!dateTime) return ''
  // 如果是完整的时间字符串，只取时间部分
  if (dateTime.includes('T')) {
    return dateTime.split('T')[1].substring(0, 8)
  }
  return dateTime
}

// 获取车次列表
async function fetchTrains() {
  loading.value = true
  try {
    const res = await adminApi.getTrains()
    schedules.value = res.data || []
  } catch (err) {
    ElMessage.error(err.message || '获取车次列表失败')
  } finally {
    loading.value = false
  }
}

// 获取路线列表（用于下拉选择）
async function fetchRoutes() {
  try {
    const res = await adminApi.getRoutes()
    routes.value = res.data || []
  } catch (err) {
    ElMessage.error(err.message || '获取路线列表失败')
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
  // 处理时间格式
  const departureTime = row.departureTime ? formatTime(row.departureTime) : ''
  const arrivalTime = row.arrivalTime ? formatTime(row.arrivalTime) : ''
  
  Object.assign(form, {
    ...row,
    departureTime,
    arrivalTime
  })
  dialogVisible.value = true
}

// 重置表单
function resetForm() {
  Object.assign(form, {
    id: '',
    trainNumber: '',
    date: '',
    routeId: '',
    departureTime: '',
    arrivalTime: '',
    ticketTotal: 0,
    price: 0
  })
  formRef.value?.resetFields()
}

// 提交表单
async function submitForm() {
  try {
    await formRef.value.validate()
    
    submitLoading.value = true
    const payload = {
      trainNumber: form.trainNumber,
      date: form.date,
      routeId: form.routeId,
      departureTime: `${form.date}T${form.departureTime}`,
      arrivalTime: `${form.date}T${form.arrivalTime}`,
      ticketTotal: form.ticketTotal,
      price: form.price
    }
    
    if (isEdit.value) {
      payload.id = form.id
      await adminApi.updateTrain(payload)
      ElMessage.success('车次更新成功')
    } else {
      await adminApi.createTrain(payload)
      ElMessage.success('车次添加成功')
    }
    
    dialogVisible.value = false
    fetchTrains()
  } catch (err) {
    ElMessage.error(err.message || (isEdit.value ? '车次更新失败' : '车次添加失败'))
  } finally {
    submitLoading.value = false
  }
}

// 删除车次
function deleteTrain(id) {
  ElMessageBox.confirm('确定要删除该车次吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await adminApi.deleteTrain(id)
      ElMessage.success('删除成功')
      fetchTrains()
    } catch (err) {
      ElMessage.error(err.message || '删除失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

onMounted(() => {
  fetchTrains()
  fetchRoutes()
})
</script>

<style scoped>
.schedule-page {
  padding: 16px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>