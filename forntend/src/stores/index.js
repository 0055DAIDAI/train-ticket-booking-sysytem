import { defineStore } from 'pinia'

const STORAGE_KEY = 'app_user_info'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null,    // 整个用户对象，普通用户或管理员都存这里
  }),
  getters: {
    id: (state) => state.user?.id ?? null,
    name: (state) => state.user?.name ?? state.user?.username ?? '',
    // 判断是否管理员（优先用 user.role/isAdmin）
    isAdmin: (state) => {
      if (!state.user && state.token) {
        // 可选：解析 JWT token 判断 role（若后端只返回 token）
        try {
          const payload = JSON.parse(atob(state.token.split('.')[1].replace(/-/g,'+').replace(/_/g,'/')))
          return payload?.role === 'admin' || payload?.isAdmin === true
        } catch (e) { /* ignore */ }
      }
      return state.user?.role === 'admin' || state.user?.isAdmin === true
    }
  },
  actions: {
    setUser(user, token) {
      this.user = user || null
      if (token) this.token = token
      try {
        if (user) localStorage.setItem(STORAGE_KEY, JSON.stringify(user))
        else localStorage.removeItem(STORAGE_KEY)
        if (token) localStorage.setItem('token', token)
      } catch (e) { console.error(e) }
    },
    setUserAndLogin(user, token) {
      this.setUser(user, token)
    },
    loadFromStorage() {
      try {
        const raw = localStorage.getItem(STORAGE_KEY)
        if (raw) this.user = JSON.parse(raw)
        const t = localStorage.getItem('token')
        if (t) this.token = t
      } catch (e) { this.user = null; this.token = '' }
    },
    clear() {
      this.user = null
      this.token = ''
      try {
        localStorage.removeItem(STORAGE_KEY)
        localStorage.removeItem('token')
      } catch (e) {}
    }
  }
})