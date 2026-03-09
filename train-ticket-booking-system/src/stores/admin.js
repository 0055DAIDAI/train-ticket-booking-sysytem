import { defineStore } from 'pinia'

const STORAGE_KEY = 'app_admin_info'
export const useAdminStore = defineStore('admin', {
  state: () => ({
    admin: null,
    token: ''
  }),
  getters: {
    id: (s) => s.admin?.id ?? null,
    name: (s) => s.admin?.name ?? s.admin?.username ?? '',
    isLoggedIn: (s) => !!s.token || !!s.admin
  },
  actions: {
    setAdmin(admin, token = '') {
      this.admin = admin ?? null
      this.token = token ?? ''
      try {
        if (admin) localStorage.setItem(STORAGE_KEY, JSON.stringify(admin))
        else localStorage.removeItem(STORAGE_KEY)
        if (token) localStorage.setItem('admin_token', token)
        else localStorage.removeItem('admin_token')
      } catch (e) {}
    },
    loadFromStorage() {
      try {
        const raw = localStorage.getItem(STORAGE_KEY)
        if (raw) this.admin = JSON.parse(raw)
        const t = localStorage.getItem('admin_token')
        if (t) this.token = t
      } catch (e) {}
    },
    clear() {
      this.admin = null
      this.token = ''
      try { localStorage.removeItem(STORAGE_KEY); localStorage.removeItem('admin_token') } catch (e) {}
    }
  }
})