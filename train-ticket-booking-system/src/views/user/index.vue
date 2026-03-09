<template>
    <div class="user-page">
        <el-card>
            <div style="display:flex; align-items:center; justify-content:space-between;">
                <h3>个人信息</h3>
                <el-button type="primary" @click="openRecharge">充值</el-button>
            </div>

            <el-table :data="[user]" stripe style="width:100%; margin-top:12px" v-if="user">
                <el-table-column label="头像" width="90">
                    <template #default="{ row }">
                        <el-avatar :src="row.avatar" size="40">{{ !row.avatar ? (row.name ? row.name[0] :
                            row.username?.[0]) : '' }}</el-avatar>
                    </template>
                </el-table-column>
                <el-table-column prop="id" label="用户ID" width="100" />
                <el-table-column prop="username" label="账号" width="140" />
                <el-table-column prop="name" label="姓名" width="140" />
                <el-table-column prop="phone" label="手机号" width="140" />
                <el-table-column prop="idCard" label="身份证" width="180" />
                <el-table-column label="性别" width="80">
                    <template #default="{ row }">{{ row.gender === 1 ? '男' : row.gender === 0 ? '女' : '未知' }}</template>
                </el-table-column>
                <el-table-column label="余额(¥)" width="120">
                    <template #default="{ row }">{{ Number(row.balance ?? 0).toFixed(2) }}</template>
                </el-table-column>
            </el-table>

            <div v-else>未登录或无用户信息</div>
        </el-card>

        <!-- 统一使用 v-model -->
        <el-dialog v-model="rechargeDialog" title="充值" width="420px">
            <el-form :model="rechargeForm" label-width="90px">
                <el-form-item label="充值金额" :required="true">
                    <el-input-number v-model="rechargeForm.amount" :min="1" :step="1" style="width:100%" />
                </el-form-item>
                <el-form-item label="支付方式">
                    <el-select v-model="rechargeForm.method" placeholder="请选择" style="width:100%">
                        <el-option label="支付宝" value="alipay" />
                        <el-option label="微信" value="wechat" />
                        <el-option label="余额支付" value="balance" />
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="rechargeDialog = false">取消</el-button>
                <el-button type="primary" :loading="isRecharging" @click="confirmRecharge">确认充值</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/index'
import userApi from '@/api/userApi' // 需在 api 中实现 recharge 接口，可参考：post /user/recharge

const userStore = useUserStore()
userStore.loadFromStorage()
const user = computed(() => userStore.user)

const rechargeDialog = ref(false)
const isRecharging = ref(false)
const rechargeForm = reactive({
    amount: 100,
    method: 'alipay'
})

function openRecharge() {
    if (!user.value) {
        ElMessage.warning('请先登录')
        return
    }
    rechargeForm.amount = 100
    rechargeForm.method = 'alipay'
    rechargeDialog.value = true
}

async function confirmRecharge() {
  if (!user.value) {
    ElMessage.warning('请先登录')
    rechargeDialog.value = false
    return
  }
  if (!rechargeForm.amount || Number(rechargeForm.amount) <= 0) {
    ElMessage.warning('请输入正确的充值金额')
    return
  }

  isRecharging.value = true
  try {
    const payload = {
      userId: user.value.id,
      amount: Number(rechargeForm.amount)
    }
    await userApi.recharge(payload)

    // 充值成功后，拉取最新用户信息并更新 store
    try {
      const infoRes = await userApi.getUserInfo(user.value.id)
      const infoRoot = infoRes?.data ?? infoRes
      const latest = infoRoot?.data ?? infoRoot
      if (latest) {
        if (typeof userStore.setUser === 'function') {
          userStore.setUser(latest)
        } else if (typeof userStore.$patch === 'function') {
          userStore.$patch({ user: latest })
        } else {
          userStore.user = JSON.parse(JSON.stringify(latest))
          try { localStorage.setItem('app_user_info', JSON.stringify(latest)) } catch (e) {}
        }
      }
    } catch (e) {
      console.warn('拉取最新用户信息失败', e)
    }

    ElMessage.success('充值成功，用户信息已更新')
    // 成功后关闭弹窗
    rechargeDialog.value = false
  } catch (err) {
    console.error('recharge error', err)
    ElMessage.error(err?.response?.data?.message || err?.message || '充值失败')
    // 失败也关闭弹窗或按需保留：这里关闭弹窗
    rechargeDialog.value = false
  } finally {
    isRecharging.value = false
  }
}

onMounted(() => {
    userStore.loadFromStorage()
})
</script>

<style lang="scss" scoped>
.user-page {
    padding: 16px;

    .el-card {
        padding: 16px;
    }
}
</style>