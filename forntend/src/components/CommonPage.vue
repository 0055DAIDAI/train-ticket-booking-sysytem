<template>
  <el-container class="commonPage">
    <el-header class="header">
      <div class="left-content">火车订票系统</div>
      <div class="right-content">
        <el-button size="small" type="primary" @click="editAccount" v-if="!adminStore.isLoggedIn">账户</el-button>
        <el-button size="small" type="danger" @click="logout">退出登录</el-button>
      </div>
    </el-header>

    <el-container class="content-area">
      <el-aside class="aside">
        <el-menu router>
          <el-menu-item index="/home">
            <el-icon><House /></el-icon> 首页
          </el-menu-item>

          <!-- 仅普通用户可见 -->
          <el-menu-item v-if="!adminStore.isLoggedIn" index="/order">我的订单</el-menu-item>
          <el-menu-item v-if="!adminStore.isLoggedIn" index="/user">个人信息</el-menu-item>

          <!-- 管理端入口 -->
          <el-menu-item v-if="adminStore.isLoggedIn" index="/admin/schedule">管理 - 车次</el-menu-item>
          <el-menu-item v-if="adminStore.isLoggedIn" index="/admin/route">管理 - 路线</el-menu-item>
          <el-menu-item v-if="adminStore.isLoggedIn" index="/admin/order">管理 - 订单</el-menu-item>

          <el-menu-item index="/info">关于我们</el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/index'
import { useAdminStore } from '@/stores/admin'
import { House } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const adminStore = useAdminStore()

function editAccount() {
  if (adminStore.isLoggedIn) {
    router.push('/admin/trains').catch(()=>{})
  } else {
    router.push('/user').catch(()=>{})
  }
}

function logout() {
  try {
    try { userStore.clear?.() } catch(e){}
    try { adminStore.clear?.() } catch(e){}
    localStorage.removeItem('token')
    localStorage.removeItem('admin_token')
    localStorage.removeItem('app_user_info')
    ElMessage.success('已退出登录')
    router.replace('/login').catch(()=>{})
  } catch (e) {
    console.error('logout error', e)
    ElMessage.error('退出登录失败')
  }
}
</script>

<style lang="scss" scoped>
.commonPage {
  height: 100vh;
  width: 100vw;
  display: flex;
  flex-direction: column;
}

/* 顶部 header 固定高度 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  min-height: 60px;
  flex: 0 0 60px;
  border-bottom: 1px solid #dcdfe6;
  width: 100%;
  box-sizing: border-box;
}

/* 主体区域：侧栏 + 主内容 */
.el-container > .el-container {
  /* 确保内部容器撑满剩余高度 */
  flex: 1 1 auto;
  display: flex;
  min-height: 0; /* 允许子元素滚动 */
}

/* 侧栏：使用固定宽度并设置最小宽度（避免过窄） */
.aside {
  width: 220px; /* 建议固定值，调整为合适宽度 */
  min-width: 180px;
  flex: 0 0 220px;
  box-sizing: border-box;
  border-right: 1px solid #e6e6e6;
  overflow: auto;
  background: #fff;
}

/* 主内容区：占满剩余空间并可滚动 */
.main-content {
  flex: 1 1 auto;
  padding: 16px;
  box-sizing: border-box;
  min-height: 0; /* 关键：允许内部滚动 */
  overflow: auto;
}

/* 头部左右区域布局小调整 */
.left-content { text-align: left; }
.right-content {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 12px;
}

/* 小屏适配 */
@media (max-width: 960px) {
  .aside { width: 160px; min-width: 120px; }
  .main-content { padding: 12px; }
}
</style>