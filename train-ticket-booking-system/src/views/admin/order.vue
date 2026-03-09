// views/admin/order.vue
<template>
  <div class="order-page">
    <el-card class="order-card">
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
          <el-button type="primary" @click="refreshOrders" :icon="Refresh" circle />
        </div>
      </template>

      <!-- 搜索过滤区 -->
      <div class="filter-section">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-input 
              v-model="searchForm.passengerName" 
              placeholder="乘客姓名" 
              clearable 
              @clear="handleSearch"
            />
          </el-col>
          <el-col :span="6">
            <el-select 
              v-model="searchForm.status" 
              placeholder="订单状态" 
              clearable 
              @clear="handleSearch"
            >
              <el-option label="未支付" :value="0" />
              <el-option label="已支付" :value="1" />
              <el-option label="已取消" :value="2" />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
            />
          </el-col>
          <el-col :span="6">
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 订单表格 -->
      <el-table 
        :data="filteredOrders" 
        style="width: 100%; margin-top: 20px;" 
        v-loading="loading"
        stripe
        border
      >
        <el-table-column prop="id" label="订单ID" width="80" align="center" />
        <el-table-column prop="userId" label="用户ID" width="80" align="center" />
        <el-table-column prop="trainScheduleId" label="车次ID" width="80" align="center" />
        <el-table-column prop="passengerName" label="乘客姓名" width="120" />
        <el-table-column prop="ticketCount" label="票数" width="80" align="center" />
        <el-table-column prop="totalPrice" label="总价(¥)" width="100" align="right">
          <template #default="{ row }">¥{{ row.totalPrice.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewOrder(row)" :icon="View">查看</el-button>
            <el-button size="small" @click="editOrder(row)" :icon="Edit">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteOrder(row.id)" :icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="orders.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 查看/编辑订单对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogMode === 'view' ? '查看订单' : '编辑订单'" 
      width="600px"
      :before-close="handleDialogClose"
    >
      <el-form 
        :model="form" 
        :rules="rules" 
        ref="formRef" 
        label-width="120px"
        :disabled="dialogMode === 'view'"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="订单ID" prop="id">
              <el-input v-model="form.id" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户ID" prop="userId">
              <el-input-number 
                v-model="form.userId" 
                :min="1" 
                controls-position="right" 
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="车次ID" prop="trainScheduleId">
              <el-input-number 
                v-model="form.trainScheduleId" 
                :min="1" 
                controls-position="right" 
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="乘客姓名" prop="passengerName">
              <el-input v-model="form.passengerName" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="票数" prop="ticketCount">
              <el-input-number 
                v-model="form.ticketCount" 
                :min="1" 
                controls-position="right" 
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总价(¥)" prop="totalPrice">
              <el-input-number 
                v-model="form.totalPrice" 
                :precision="2" 
                :step="0.01" 
                :min="0" 
                controls-position="right" 
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="未支付" :value="0" />
                <el-option label="已支付" :value="1" />
                <el-option label="已取消" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建时间" prop="createTime">
              <el-date-picker
                v-model="form.createTime"
                type="datetime"
                placeholder="选择日期时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button v-if="dialogMode === 'view'" @click="switchToEdit">编辑</el-button>
          <el-button 
            v-if="dialogMode === 'edit'" 
            type="primary" 
            @click="submitForm" 
            :loading="submitLoading"
          >
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, View, Edit, Delete } from '@element-plus/icons-vue'
import adminApi from '@/api/adminApi'

// 响应式数据
const orders = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const dialogMode = ref('view') // 'view' 或 'edit'

// 搜索表单
const searchForm = reactive({
  passengerName: '',
  status: '',
  dateRange: ''
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10
})

// 表单数据
const form = reactive({
  id: '',
  userId: '',
  trainScheduleId: '',
  passengerName: '',
  ticketCount: 1,
  totalPrice: 0,
  status: 1,
  createTime: '',
  updateTime: ''
})

// 表单验证规则
const rules = {
  userId: [{ required: true, message: '请输入用户ID', trigger: 'blur' }],
  trainScheduleId: [{ required: true, message: '请输入车次ID', trigger: 'blur' }],
  passengerName: [{ required: true, message: '请输入乘客姓名', trigger: 'blur' }],
  ticketCount: [{ required: true, message: '请输入票数', trigger: 'blur' }],
  totalPrice: [{ required: true, message: '请输入总价', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 计算属性 - 过滤后的订单数据
const filteredOrders = computed(() => {
  let result = [...orders.value]
  
  // 根据乘客姓名过滤
  if (searchForm.passengerName) {
    result = result.filter(order => 
      order.passengerName.includes(searchForm.passengerName)
    )
  }
  
  // 根据状态过滤
  if (searchForm.status !== '' && searchForm.status !== null) {
    result = result.filter(order => order.status === searchForm.status)
  }
  
  // 根据日期范围过滤
  if (searchForm.dateRange && searchForm.dateRange.length === 2) {
    const [startDate, endDate] = searchForm.dateRange
    result = result.filter(order => {
      const orderDate = new Date(order.createTime)
      return orderDate >= new Date(startDate) && orderDate <= new Date(endDate)
    })
  }
  
  // 分页处理
  const start = (pagination.currentPage - 1) * pagination.pageSize
  const end = start + pagination.pageSize
  return result.slice(start, end)
})

// 获取订单列表
async function fetchOrders() {
  loading.value = true
  try {
    const res = await adminApi.getOrders()
    orders.value = res.data || []
  } catch (err) {
    ElMessage.error(err.message || '获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 刷新订单列表
function refreshOrders() {
  pagination.currentPage = 1
  fetchOrders()
}

// 搜索处理
function handleSearch() {
  pagination.currentPage = 1
}

// 重置搜索
function resetSearch() {
  searchForm.passengerName = ''
  searchForm.status = ''
  searchForm.dateRange = ''
  pagination.currentPage = 1
}

// 查看订单
function viewOrder(row) {
  Object.assign(form, row)
  dialogMode.value = 'view'
  dialogVisible.value = true
}

// 编辑订单
function editOrder(row) {
  Object.assign(form, row)
  dialogMode.value = 'edit'
  dialogVisible.value = true
}

// 切换到编辑模式
function switchToEdit() {
  dialogMode.value = 'edit'
}

// 处理对话框关闭
function handleDialogClose(done) {
  ElMessageBox.confirm('确认关闭吗？')
    .then(() => {
      done()
    })
    .catch(() => {
      // 取消关闭
    })
}

// 提交表单
async function submitForm() {
  try {
    await formRef.value.validate()
    
    submitLoading.value = true
    
    // 构造完整的 payload 数据格式
    const payload = {
      id: parseInt(form.id),
      userId: parseInt(form.userId),
      trainScheduleId: parseInt(form.trainScheduleId),
      passengerName: form.passengerName,
      ticketCount: parseInt(form.ticketCount),
      totalPrice: parseFloat(form.totalPrice),
      status: parseInt(form.status),
      createTime: form.createTime,
      updateTime: new Date().toISOString()
    }
    
    await adminApi.updateOrder(payload)
    ElMessage.success('订单更新成功')
    dialogVisible.value = false
    fetchOrders()
  } catch (err) {
    ElMessage.error(err.message || '订单更新失败')
  } finally {
    submitLoading.value = false
  }
}

// 删除订单
function deleteOrder(id) {
  ElMessageBox.confirm('确定要删除该订单吗？此操作不可恢复', '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      await adminApi.deleteOrder(id)
      ElMessage.success('删除成功')
      fetchOrders()
    } catch (err) {
      ElMessage.error(err.message || '删除失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 分页处理
function handleSizeChange(val) {
  pagination.pageSize = val
  pagination.currentPage = 1
}

function handleCurrentChange(val) {
  pagination.currentPage = val
}

// 获取状态文本
function getStatusText(status) {
  switch (status) {
    case 0: return '未支付'
    case 1: return '已支付'
    case 2: return '已取消'
    default: return '未知'
  }
}

// 获取状态标签类型
function getStatusType(status) {
  switch (status) {
    case 0: return 'warning'  // 未支付
    case 1: return 'success'  // 已支付
    case 2: return 'info'     // 已取消
    default: return 'info'
  }
}

// 格式化日期
function formatDate(dateString) {
  if (!dateString) return ''
  try {
    const date = new Date(dateString)
    return date.toLocaleString('zh-CN', { 
      year: 'numeric', 
      month: '2-digit', 
      day: '2-digit', 
      hour: '2-digit', 
      minute: '2-digit', 
      second: '2-digit' 
    }).replace(/\//g, '-')
  } catch (e) {
    return dateString
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-page {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 80px);
}

.order-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-section {
  padding: 20px 0;
  background-color: #fff;
  border-radius: 4px;
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>