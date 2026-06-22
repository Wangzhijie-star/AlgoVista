import { defineStore } from 'pinia'
import { authApi, type AuthPayload, type UserProfile } from '../api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null as UserProfile | null
  }),
  actions: {
    async fetchMe() {
      try {
        this.user = await authApi.me()
      } catch {
        this.user = null
      }
    },
    async login(payload: AuthPayload) {
      this.user = await authApi.login(payload)
    },
    async register(payload: AuthPayload) {
      this.user = await authApi.register(payload)
    },
    async logout() {
      await authApi.logout()
      this.user = null
    }
  }
})
