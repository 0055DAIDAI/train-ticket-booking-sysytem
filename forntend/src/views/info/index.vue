<template>
  <div class="info-page">
    <el-card shadow="hover">
      <div class="header">
        <h2>关于我们 · 订票系统</h2>
        <div class="sub">为您提供便捷的列车查询与在线购票服务</div>
      </div>

      <el-divider />

      <el-row :gutter="20" class="content">
        <el-col :span="16">
          <h3>产品简介</h3>
          <p>
            本系统为火车票预订演示系统，支持车次查询、分页展示、下单与订单管理以及用户充值等功能。
            旨在作为教学与快速联调的前端示例项目，界面基于 Element Plus，状态管理采用 Pinia，后端接口通过 /api 路径代理。
          </p>

          <h3>联系方式</h3>
          <el-list>
            <el-list-item>邮箱: 297332881@example.com</el-list-item>
            <el-list-item>电话: 400-800-1234</el-list-item>
          </el-list>

          <h3>开发团队</h3>
          <p>前端：廖家溢 · 后端：刘创立</p>
        </el-col>

        <el-col :span="8">
          <el-card class="meta" shadow="never">
            <div class="meta-row"><strong>版本</strong><span>{{ version }}</span></div>
            <div class="meta-row"><strong>运行环境</strong><span>{{ env }}</span></div>
            <el-divider />
            <div class="meta-row"><strong>文档</strong>
              <el-link type="primary" :underline="false" href="#" target="_blank">接口文档</el-link>
            </div>
            <div style="margin-top:12px; text-align:right">
              <el-button type="primary" plain size="small" @click="goBack">返回首页</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-divider />
      <div class="footer">© 2025 Train Ticket Booking System. 保留所有权利。</div>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref } from 'vue'

const router = useRouter()
const version = ref('v1.0.0')
const env = ref(import.meta.env.MODE || 'development')

function goBack() {
  router.push({ name: 'home' }).catch(() => {})
}
</script>

<style scoped lang="scss">
.info-page { padding: 16px; }
.header { display:flex; flex-direction:column; gap:6px; }
.header h2 { margin:0; }
.header .sub { color:#6b6b6b; font-size:13px; }
.content { margin-top:12px; }
.meta { background:#fafafa; }
.meta-row { display:flex; justify-content:space-between; padding:6px 0; }
.footer { text-align:center; color:#999; padding-top:12px; font-size:13px; }
</style>