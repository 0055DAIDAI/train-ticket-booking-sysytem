<template>
  <div class="order-page">
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
      <h3>我的订单</h3>
      <div>
        <el-button @click="fetchList" :loading="loading" type="primary" size="small">刷新</el-button>
      </div>
    </div>

    <el-table :data="orders" stripe style="width:100%" v-loading="loading" @row-click="onRowClick">
      <el-table-column prop="orderId" label="订单ID" width="100" />
      <el-table-column prop="trainNo" label="车次" width="120" />
      <el-table-column label="区间" width="200">
        <template #default="{ row }">{{ row.fromStation }} → {{ row.toStation }}</template>
      </el-table-column>
      <el-table-column prop="travelDate" label="出行日期" width="120" />
      <el-table-column label="出发/到达" width="220">
        <template #default="{ row }">
          {{ formatTime(row.departureTime) }} / {{ formatTime(row.arrivalTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="passengerName" label="乘客" width="120" />
      <el-table-column prop="ticketCount" label="票数" width="80" />
      <el-table-column prop="totalPrice" label="总价(¥)" width="100">
        <template #default="{ row }">{{ Number(row.totalPrice ?? 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="{ row }">{{ statusText(row.status) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="140" align="center">
        <template #default="{ row }">
          <el-button type="primary" size="mini" @click.stop="viewDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="detailDialog" title="订单详情" width="540px">
      <div v-if="currentOrder">
        <el-descriptions column="1" border>
          <el-descriptions-item label="订单ID">{{ currentOrder.orderId }}</el-descriptions-item>
          <el-descriptions-item label="车次">{{ currentOrder.trainNo }}</el-descriptions-item>
          <el-descriptions-item label="区间">{{ currentOrder.fromStation }} → {{ currentOrder.toStation }}</el-descriptions-item>
          <el-descriptions-item label="出行日期/时间">{{ currentOrder.travelDate }} / {{ formatTime(currentOrder.departureTime) }}</el-descriptions-item>
          <el-descriptions-item label="乘客">{{ currentOrder.passengerName }}</el-descriptions-item>
          <el-descriptions-item label="票数">{{ currentOrder.ticketCount }}</el-descriptions-item>
          <el-descriptions-item label="总价">¥ {{ Number(currentOrder.totalPrice ?? 0).toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ statusText(currentOrder.status) }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import orderApi from '@/api/orderApi'
// 导入你的 user store（项目中若路径为 '@/stores/user' 请改为相应路径）
import { useUserStore } from '@/stores/index'

const userStore = useUserStore()
if (typeof userStore.loadFromStorage === 'function') userStore.loadFromStorage()
const userId = computed(() => userStore.id ?? (userStore.user?.id) ?? Number(localStorage.getItem('userId') || 0))

const orders = ref([])
const loading = ref(false)
const detailDialog = ref(false)
const currentOrder = ref(null)

function formatTime(iso) {
  if (!iso) return ''
  try {
    const d = new Date(iso)
    const hh = String(d.getHours()).padStart(2, '0')
    const mm = String(d.getMinutes()).padStart(2, '0')
    return `${d.getFullYear() ? '' : ''}${hh}:${mm}`.trim() === 'NaN:NaN' ? iso : `${hh}:${mm}`
  } catch (e) {
    return iso
  }
}

function statusText(code) {
  switch (code) {
    case 1: return '已支付'
    case 0: return '未支付'
    case 2: return '已取消'
    default: return '未知'
  }
}

async function fetchList() {
  if (!userId.value) {
    orders.value = []
    return
  }
  loading.value = true
  try {
    const res = await orderApi.getOrderList(userId.value)
    const body = res?.data ?? res
    // 后端返回示例： { code:1, msg:'', data: [ ... ] }
    const list = Array.isArray(body?.data) ? body.data : Array.isArray(body) ? body : body?.data ?? []
    orders.value = list
  } catch (err) {
    console.error('getOrderList error', err)
    ElMessage.error(err?.message || '获取订单失败')
  } finally {
    loading.value = false
  }
}

function onRowClick(row) {
  viewDetail(row)
}

function viewDetail(row) {
  currentOrder.value = row
  detailDialog.value = true
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.order-page { padding: 16px; }
</style>