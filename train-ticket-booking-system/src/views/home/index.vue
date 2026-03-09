<template>
  <div class="home-page">
    <!-- 搜索栏保持不变 -->
    <div class="search-bar">
      <el-row :gutter="12" align="middle">
        <el-col :span="6">
          <el-input v-model="filters.from" placeholder="出发站" clearable />
        </el-col>
        <el-col :span="6">
          <el-input v-model="filters.to" placeholder="到达站" clearable />
        </el-col>
        <el-col :span="6">
          <el-date-picker
            v-model="filters.date"
            type="date"
            placeholder="出发日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="onSearch" :loading="loading">查询</el-button>
          <el-button @click="resetFilters" style="margin-left:8px">重置</el-button>
        </el-col>
      </el-row>
    </div>

    <div class="table-wrap">
      <el-table
        :data="filteredList"
        stripe
        style="width: 100%"
        @row-click="onRowClick"
        :loading="tableLoading"
      >
        <el-table-column label="日期" width="120">
          <template #default="{ row }">{{ row.date || formatDate(row.startTime) }}</template>
        </el-table-column>

        <el-table-column label="起始时间" width="140">
          <template #default="{ row }">{{ formatTime(row.startTime) }}</template>
        </el-table-column>

        <el-table-column label="终止时间" width="140">
          <template #default="{ row }">{{ formatTime(row.endTime) }}</template>
        </el-table-column>

        <el-table-column prop="fromStation" label="起始站" width="140" />
        <el-table-column prop="toStation" label="终点站" width="140" />
        <el-table-column prop="trainNo" label="车次" width="100" />
        <el-table-column prop="duration" label="运行时长" width="120" />
        <el-table-column prop="price" label="票价(¥)" width="100" />

        <!-- 修改：仅对非管理员显示购票按钮 -->
        <el-table-column label="操作" width="110" align="center" v-if="!adminStore.isLoggedIn">
          <template #default="{ row }">
            <el-button type="success" size="mini" @click.stop="openPurchase(row)">购票</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页保持不变 -->
      <div class="pager" style="margin-top:12px; text-align: right;">
        <el-pagination
          v-if="total > 0"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next, jumper, ->, total"
          @current-change="onPageChange"
        />
      </div>
    </div>
<el-dialog v-model="purchaseDialog" title="购票" width="420px" v-if="!adminStore.isLoggedIn">
  <template #default>
    <div v-if="selectedTrain">
      <p><strong>{{ selectedTrain.fromStation }} → {{ selectedTrain.toStation }}</strong></p>
      <p>车次：{{ selectedTrain.trainNo || '—' }}</p>
      <p>出发：{{ formatTime(selectedTrain.departureTime || selectedTrain.startTime) || '—' }} 到达：{{ formatTime(selectedTrain.arrivalTime || selectedTrain.endTime) || '—' }}</p>

      <el-form label-width="90px" class="purchase-form" :model="purchaseForm">
        <el-form-item label="票数">
          <el-input-number v-model="purchaseForm.count" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="总价">
          <div>¥ {{ totalPrice.toFixed(2) }}</div>
        </el-form-item>
        <el-form-item label="乘客姓名">
          <el-input v-model="purchaseForm.passenger" placeholder="请输入乘客姓名" />
        </el-form-item>
      </el-form>
    </div>
  </template>
  <template #footer>
    <el-button @click="purchaseDialog = false">取消</el-button>
    <el-button type="primary" @click="confirmPurchase" :loading="isPurchasing">确认购票</el-button>
  </template>
</el-dialog>

    <!-- 车次详情对话框：对所有用户显示 -->
    <el-dialog v-model="detailDialog" title="车次详情" width="600px">
      <!-- ... 内容保持不变 ... -->
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import orderApi from '@/api/orderApi'
import { useUserStore } from '@/stores/index' 
import { useAdminStore } from '@/stores/admin' 

const userStore = useUserStore()
const adminStore = useAdminStore() // 新增管理员状态
const currentUserName = computed(() => userStore.name || '')


const filters = reactive({ from: '', to: '', date: '' })
const allTrains = ref([])
const filteredList = ref([])
const loading = ref(false)
const tableLoading = ref(false)

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const selectedTrain = ref(null)
const purchaseDialog = ref(false)
const detailDialog = ref(false)

const purchaseForm = reactive({ count: 1, passenger: '' })
const isPurchasing = ref(false) 

const totalPrice = computed(() => {
  if (!selectedTrain.value) return 0
  return (selectedTrain.value.price || 0) * (purchaseForm.count || 1)
})

function formatTime(iso) {
  if (!iso) return '—'
  // 如果是短时间字符串（如 "10:00"），直接返回
  if (typeof iso === 'string' && /^\d{1,2}:\d{2}$/.test(iso)) return iso
  try {
    const d = new Date(iso)
    const hh = String(d.getHours()).padStart(2, '0')
    const mm = String(d.getMinutes()).padStart(2, '0')
    return `${hh}:${mm}`
  } catch (e) {
    console.warn('formatTime error:', iso, e)
    return '—'
  }
}

function formatDate(iso) {
  if (!iso) return ''
  // 如果已经是 YYYY-MM-DD 直接返回
  if (typeof iso === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(iso)) return iso
  try {
    const d = new Date(iso)
    const yyyy = d.getFullYear()
    const mm = String(d.getMonth() + 1).padStart(2, '0')
    const dd = String(d.getDate()).padStart(2, '0')
    return `${yyyy}-${mm}-${dd}`
  } catch (e) {
    return ''
  }
}

function toDateString(val) {
  if (!val) return ''
  if (typeof val === 'string') {
    if (/^\d{4}-\d{2}-\d{2}$/.test(val)) return val
    try { return formatDate(new Date(val)) } catch (e) { return '' }
  }
  if (val instanceof Date) return formatDate(val)
  try { return formatDate(new Date(val)) } catch (e) { return '' }
}

// 规范化选中日期为 YYYY-MM-DD
watch(() => filters.date, (v) => {
  if (!v) return
  const s = toDateString(v)
  if (s && s !== v) filters.date = s
})

async function fetchList(page = currentPage.value) {
  tableLoading.value = true
  try {
    const payload = {
      from: filters.from || undefined,
      to: filters.to || undefined,
      date: toDateString(filters.date) || formatDate(new Date()),
      page: page,
      size: pageSize.value
    }
    console.log('request payload', payload)

    const res = await orderApi.getTrainSchedule(payload)
    console.log('raw response', res)

    // 兼容后端返回： { code:1, msg:'', data: { data:[...], total, page, size } }
    // 以及常见其它包装情况
    const root = res?.data ?? res
    let container = root?.data ?? root
    let list = []
    let totalCount = 0
    let returnedPage = page
    let returnedSize = pageSize.value

    if (container && Array.isArray(container.data)) {
      // container = { data: [...], total, page, size }
      list = container.data
      totalCount = Number(container.total ?? 0)
      returnedPage = Number(container.page ?? page)
      returnedSize = Number(container.size ?? pageSize.value)
    } else if (Array.isArray(container)) {
      // container is array
      list = container
      totalCount = list.length
    } else if (root && Array.isArray(root.data)) {
      // root.data is array
      list = root.data
      totalCount = Number(root.total ?? 0)
      returnedPage = Number(root.page ?? page)
      returnedSize = Number(root.size ?? pageSize.value)
    } else if (container && Array.isArray(container.list)) {
      list = container.list
      totalCount = Number(container.total ?? 0)
      returnedPage = Number(container.page ?? page)
      returnedSize = Number(container.size ?? pageSize.value)
    } else {
      list = []
      totalCount = 0
    }

    // 把后端的当前页记录直接展示（后端已经按 from/to/date 做过滤）
    allTrains.value = list
    filteredList.value = list
    total.value = Number(totalCount) || 0
    currentPage.value = Number(returnedPage) || page
    pageSize.value = Number(returnedSize) || pageSize.value
  } catch (err) {
    console.error('fetchList error', err)
    ElMessage.error(err?.message || '获取列表失败')
  } finally {
    tableLoading.value = false
  }
}

function onSearch() {
  currentPage.value = 1
  fetchList(1)
}

function onPageChange(page) {
  fetchList(page)
}

function resetFilters() {
  filters.from = ''
  filters.to = ''
  filters.date = ''
  currentPage.value = 1
  fetchList(1)
}

function openPurchase(row) {
  console.log('openPurchase called', row)
  selectedTrain.value = { ...row }
  
  // 添加字段映射逻辑
  if (row.startTime && !selectedTrain.value.departureTime) {
    selectedTrain.value.departureTime = row.startTime
  }
  if (row.endTime && !selectedTrain.value.arrivalTime) {
    selectedTrain.value.arrivalTime = row.endTime
  }
  
  purchaseForm.count = 1
  purchaseForm.passenger = userStore.name || ''
  purchaseDialog.value = true
}

function onRowClick(row) {
  selectedTrain.value = row
  detailDialog.value = true
}

async function confirmPurchase() {
  if (!selectedTrain.value) {
    ElMessage.warning('未选择车次')
    return
  }

  const passengerName = purchaseForm.passenger || userStore.name || ''
  const userId = Number(userStore.id || localStorage.getItem('userId') || localStorage.getItem('uid') || 0)
  if (!passengerName && !userId) {
    ElMessage.warning('请先登录或填写乘车人信息')
    return
  }


  const payload = {
    userId: userId,
    trainScheduleId: selectedTrain.value.id,
    passengerName: purchaseForm.passenger,
    ticketCount: purchaseForm.count,
    userId: userId,
    trainScheduleId: selectedTrain.value.id,
    passengerName: passengerName,
    ticketCount: purchaseForm.count
  }

  isPurchasing.value = true
  try {
    const res = await orderApi.createOrder(payload)
    console.log('createOrder res', res)
    ElMessage.success('下单成功')
    purchaseDialog.value = false
    // 可选：跳转到订单页或刷新列表
    // router.push('/orders')
  } catch (err) {
    console.error('createOrder error', err)
    ElMessage.error(err?.message || '下单失败')
  } finally {
    isPurchasing.value = false
  }
}

onMounted(() => {
  // 检查用户是否已登录
  userStore.loadFromStorage()
  // 检查管理员状态
  adminStore.loadFromStorage()
  
  // 如果没有用户信息，重定向到登录页
  if (!userStore.id && !adminStore.isLoggedIn) {
    router.push('/login')
    return
  }
  
  fetchList(1)
})
</script>

<style lang="scss" scoped>
.home-page {
  padding: 16px;
  min-height: calc(100vh - 80px);

  .search-bar {
    background: #fff;
    padding: 12px;
    border-radius: 6px;
    box-shadow: 0 6px 18px rgba(0,0,0,0.04);
    margin-bottom: 16px;
  }

  .table-wrap {
    background: #fff;
    padding: 12px;
    border-radius: 6px;
    box-shadow: 0 6px 18px rgba(0,0,0,0.04);
  }

  .purchase-form { margin-top: 12px; }
}
@media (max-width: 700px) {
  .home-page { padding: 8px; .search-bar .el-col { margin-bottom: 8px; } }
}
</style>