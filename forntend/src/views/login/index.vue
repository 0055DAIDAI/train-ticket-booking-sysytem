<template>
  <div class="login-page">
    <div class="left-panel">
      <h1 class="heading">火车订票系统</h1>
    </div>

    <div class="divider" />

    <div class="right-panel">
      <div class="login-box">
        <h2 class="title">账号登录</h2>
        <el-form :model="form" :rules="rules" ref="loginForm" class="login-form" label-position="top">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="请输入账号" clearable />
          </el-form-item>

          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password @keyup.enter="login" />
          </el-form-item>

          <!-- 新增：角色选择（用户 / 管理员） -->
          <el-form-item>
            <el-radio-group v-model="form.role">
              <el-radio label="user">用户登录</el-radio>
              <el-radio label="admin">管理员登录</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" class="full-btn" :loading="loading" @click="login">登录</el-button>
          </el-form-item>

          <el-form-item class="actions" v-if="form.role === 'user'">
            <el-button type="text" @click="goRegister">注册账号</el-button>
            <el-button type="text" @click="forgot">忘记密码？</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import userApi from '@/api/userApi'
import adminApi from '@/api/adminApi'
import { useUserStore } from '@/stores/index'
import { useAdminStore } from '@/stores/admin'

const router = useRouter()
const loginForm = ref(null)
const userStore = useUserStore()
const adminStore = useAdminStore()

const loading = ref(false)

// 表单数据
const form = reactive({
  username: '',
  password: '',
  role: 'user' // 默认角色为用户
})


// 校验规则
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 登录方法
async function login() {
  try {
    await loginForm.value.validate()
  } catch {
    ElMessage.error('请填写完整的登录信息')
    return
  }

  loading.value = true
  try {
    if (form.role === 'admin') {
      const resp = await adminApi.loginAdmin({ adminName: form.username, password: form.password })
      const body = resp?.data ?? resp
      const info = body?.data ?? body
      if (info && (info.token || info.id)) {
        if (info.token) localStorage.setItem('admin_token', info.token)
        adminStore.setAdmin?.(info, info.token ?? '')
        console.log('[debug] admin token saved', localStorage.getItem('admin_token'))
        // 使用显式路径并等待跳转结果
        try {
          await router.replace('/admin/schedule')
          console.log('[debug] navigate to /admin/schedule success')
        } catch (e) {
          console.error('[debug] navigate failed', e)
          // 兜底强制跳转
          window.location.href = '/admin/schedule'
        }
        return
      }
    }


    // 普通用户登录
    const response = await userApi.login({ username: form.username, password: form.password })
    const resp = response?.data ?? response
    const loginInfo = resp?.data ?? resp

    if (loginInfo && loginInfo.token) {
      localStorage.setItem('token', loginInfo.token)
      userStore.setUserAndLogin?.(loginInfo, loginInfo.token)
      ElMessage.success('登录成功')
      router.push({ name: 'home' }).catch(() => { })
    } else {
      ElMessage.error('登录失败：未获取到用户信息或 token')
    }
  } catch (error) {
    const errMsg = error?.response?.data?.message || error?.response?.data?.msg || error.message || '登录失败'
    ElMessage.error(errMsg)
    console.error('登录出错', error)
  } finally {
    loading.value = false
  }
}

// 跳转注册页
function goRegister() {
  router.push('/register').catch(() => { })
}

// 忘记密码
function forgot() {
  ElMessage.info('忘记密码功能暂未实现')
}
</script>

<style lang="scss" scoped>
.login-page {
  height: 100vh;
  width: 100vw;
  display: flex;
  position: relative;
  background: #f5f7fa;

  .left-panel {
    flex: 1;
    position: relative;
    /* 新增：确保 .heading 相对于左侧面板定位 */
    background-image: url('@/assets/images/login-bg.jpg');
    background-size: cover;
    background-position: center;
    min-width: 320px;

    .heading {
      position: absolute;
      top: 14%;
      /* 在左侧面板的中上方，按需调整 10%~20% */
      left: 50%;
      transform: translateX(-50%);
      color: #fff;
      font-size: 28px;
      font-weight: 600;
      text-shadow: 0 2px 8px rgba(0, 0, 0, 0.55);
      padding: 8px 16px;
      border-radius: 6px;
      white-space: nowrap;
    }

  }

  .divider {
    width: 1px;
    background: rgba(0, 0, 0, 0.08);
    position: absolute;
    left: 30%;
    top: 10%;
    bottom: 10%;
    transform: translateX(-50%);
    z-index: 1;
  }

  .right-panel {
    width: 70%;
    min-width: 360px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 40px;
    box-sizing: border-box;

    .login-box {
      width: 420px;
      max-width: calc(100% - 40px);
      background: #fff;
      padding: 36px;
      border-radius: 8px;
      box-shadow: 0 8px 30px rgba(44, 62, 80, 0.08);
      z-index: 2;

      .title {
        margin: 0 0 18px;
        font-size: 20px;
        color: #2d3a4b;
        text-align: center;
      }

      .login-form {
        .full-btn {
          width: 100%;
        }

        .actions {
          display: flex;
          justify-content: space-between;
          padding-top: 6px;
        }
      }
    }
  }
}
</style>