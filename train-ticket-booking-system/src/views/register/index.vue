<template>
  <div class="register-page">
    <div class="card">
      <h2>注册账号</h2>

      <el-form :model="form" :rules="rules" ref="regForm" label-width="100px" class="form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>

        <el-form-item label="真实姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入真实姓名" />
        </el-form-item>

        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入身份证号" />
        </el-form-item>

        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="头像" prop="avator">
          <el-upload
            class="avatar-uploader"
            accept="image/*"
            :auto-upload="false"
            :on-change="handleAvatarChange"
            :on-remove="handleAvatarRemove"
            :file-list="fileList"
            list-type="picture-card"
          >
            <template #default>
              <i v-if="!preview && !isUploading" class="el-icon-plus" style="font-size:28px"></i>
              <i v-if="isUploading" class="el-icon-loading" style="font-size:28px"></i>
              <img v-if="preview" :src="preview" class="avatar" />
            </template>
          </el-upload>
          <div class="hint">请选择图片，图片仅在点击“注册”时上传；上传成功后会把 URL 存入 avatar 字段并随注册数据一起提交。</div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submit" :loading="isSubmitting">注册</el-button>
          <el-button @click="reset" :disabled="isSubmitting || isUploading">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import userApi from '@/api/userApi'

const router = useRouter()
const regForm = ref(null)

const isSubmitting = ref(false)
const isUploading = ref(false)

const form = reactive({
  id: '',
  username: '',
  password: '',
  name: '',
  idCard: '',
  phone: '',
  status: 1,
  avatar: '',      // 最终应为后端返回的 URL
  avatarFile: null,// 用户选中的 File，只有在 submit 时上传
})

const fileList = ref([])
const preview = ref('')

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^[0-9\-+() ]{6,20}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
}

/**
 * 处理文件选择：只做预览并保存原始 File，不上传
 */
function handleAvatarChange(file) {
  const raw = file.raw || file
  if (!raw) return
  // 可选：文件大小/类型校验
  const maxMB = 5
  if (raw.size / 1024 / 1024 > maxMB) {
    ElMessage.warning(`图片大小不能超过 ${maxMB}MB`)
    return
  }
  form.avatorFile = raw
  // 生成预览
  const reader = new FileReader()
  reader.onload = (e) => {
    preview.value = e.target.result
    fileList.value = [{ name: file.name || 'avatar', url: preview.value }]
  }
  reader.readAsDataURL(raw)
}

/**
 * 删除选择：清理预览与 file 引用
 */
function handleAvatarRemove() {
  form.avatorFile = null
  preview.value = ''
  fileList.value = []
}

/**
 * 点击注册时：先上传头像（如果有），再提交用户数据
 */
async function submit() {
  regForm.value.validate(async (valid) => {
    if (!valid) return
    if (isSubmitting.value) return
    isSubmitting.value = true
    try {
      let avatarUrl = form.avator || ''
      if (form.avatorFile) {
        isUploading.value = true
        try {
          const fd = new FormData()
          fd.append('file', form.avatorFile)
          const upRes = await userApi.uploadAvatar(fd)
          avatarUrl = upRes.data || ''
          if (!avatarUrl) {
            throw new Error('上传接口未返回图片 URL')
          }
          ElMessage.success('头像上传成功')
        } catch (err) {
          console.error('上传头像失败', err)
          ElMessage.error(err?.response?.data?.message || err.message || '头像上传失败')
          isUploading.value = false
          isSubmitting.value = false
          return
        } finally {
          isUploading.value = false
        }
      }

      const payload = {
        id: form.id || undefined,
        username: form.username,
        password: form.password,
        name: form.name,
        idCard: form.idCard,
        phone: form.phone,
        status: form.status,
        avatar: avatarUrl
      }

      const res = await userApi.registerUser(payload)
      if (res) {
        ElMessage.success('注册成功')
        router.push('/login').catch(() => {})
      } else {
        const msg = res.msg
        ElMessage.error(msg)
      }
    } catch (err) {
      console.error('注册出错', err)
      ElMessage.error(err?.response?.data?.message || err.message || '注册异常')
    } finally {
      isSubmitting.value = false
    }
  })
}

function reset() {
  regForm.value.resetFields()
  fileList.value = []
  preview.value = ''
  form.avatorFile = null
  form.avator = ''
  form.status = 1
}
</script>

<style scoped lang="scss">
.register-page {
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 40px;
  .card {
    width: 640px;
    background: #fff;
    padding: 24px;
    border-radius: 8px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
  }
  .hint {
    margin-top: 6px;
    color: #909399;
    font-size: 12px;
  }
}

.avatar-uploader {
  .avatar {
    width: 78px;
    height: 78px;
    display: block;
    object-fit: cover;
  }
  .el-icon-plus {
    font-size: 28px;
    color: #8c939a;
  }
}
</style>
